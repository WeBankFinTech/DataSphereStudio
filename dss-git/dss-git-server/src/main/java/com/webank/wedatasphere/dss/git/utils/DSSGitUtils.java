package com.webank.wedatasphere.dss.git.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitRevertRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitDiffResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.git.constant.DSSGitConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class DSSGitUtils {
    private static final Logger logger = LoggerFactory.getLogger(DSSGitUtils.class);

    public static void init(String projectName, GitUserEntity gitUserDO) throws Exception, GitErrorException{
        String projectPath = gitUserDO.getGitUser() + "/" + projectName;
        if (!checkIfProjectExists(gitUserDO, projectPath)) {
            String url = UrlUtils.normalizeIp(gitUserDO.getGitUrl()) + "/" + GitServerConfig.GIT_RESTFUL_API_CREATE_PROJECTS.getValue();
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(url);
                post.addHeader("PRIVATE-TOKEN", gitUserDO.getGitToken());
                post.addHeader("Content-Type", "application/json");
                String jsonInputString = String.format("{\"name\": \"%s\", \"description\": \"%s\"}", projectName, projectName);
                post.setEntity(new StringEntity(jsonInputString));

                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 201) {
                        logger.info("init success");
                    } else {
                        throw new GitErrorException(80001, "创建Git项目失败，请检查工作空间token是否过期");
                    }
                }
            }
        } else {
            throw new GitErrorException(80001, "git init failed, the reason is: projectName " + projectName +" already exists");
        }
    }

    public static void remote(Repository repository, String projectName, GitUserEntity gitUser)throws GitErrorException {
        // 拼接git remote Url
        String remoteUrl = UrlUtils.normalizeIp(gitUser.getGitUrl()) + "/" +gitUser.getGitUser() + File.separator + projectName + ".git";
        try {
            Git git = new Git(repository);

            // 添加远程仓库引用
            git.remoteAdd()
                    .setName("origin")
                    .setUri(new URIish(remoteUrl))
                    .call();

            logger.info("remote success");
        } catch (URISyntaxException e) {
            throw new GitErrorException(80001, "connect Uri " + remoteUrl +" failed, the reason is: ", e);
        } catch (GitAPIException  e) {
            throw new GitErrorException(80001, "remote git failed, the reason is: ", e);
        }

    }


    public static void create(String projectName, GitUserEntity gitUserDO, Long workspaceId) throws GitErrorException{
        logger.info("start success");
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + workspaceId + File.separator + projectName); // 指定仓库的目录
        File respo = new File(generateGitPath(projectName, workspaceId));
        if (!respo.exists()) {
            try {
                // 初始化仓库
                Git git = Git.init()
                        .setDirectory(repoDir)
                        .call();

                logger.info("Initialized empty Git repository in " + git.getRepository().getDirectory());

                git.close(); // 不再需要时关闭Git对象
            } catch (GitAPIException e) {
                throw new GitErrorException(80001, "git Create failed, the reason is: ", e);
            }
        } else {
            logger.info(respo + " already exists");
        }
    }

    public static void pull(Repository repository, String projectName, GitUserEntity gitUser)throws GitErrorException {
        try {
            Git git = new Git(repository);

            int i = 1;
            while (true) {
                i += 1;
                // 拉取远程仓库更新至本地
                PullCommand pullCmd = git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser.getGitUser(), gitUser.getGitToken()));
                PullResult result = pullCmd.call();

                // 成功直接返回，失败清空本地修改重试最多3次
                if (result.isSuccessful()) {
                    logger.info("Pull successful!");
                    break;
                } else if (i <= 3){
                    logger.info("Pull failed : " + result.toString());
                    // 冲突时以远程仓库为准
                    if (result.getMergeResult().getConflicts() != null) {
                        logger.info("Conflicts occurred. Resolving with remote as priority...");
                        // 丢失本地修改，处理冲突
                        reset(repository, projectName);
                    }
                }else {
                    throw new GitErrorException(80001, "git pull failed");
                }
            }
        } catch (Exception e) {
            // 丢失本地修改，处理冲突
            reset(repository, projectName);
        }
    }

    public static void pullTargetFile(Repository repository, String projectName, GitUserEntity gitUser, List<String> paths) throws Exception {
        try {
            if (CollectionUtils.isEmpty(paths)) {
                return ;
            }

            // Opening the repository
            Git git = new Git(repository);
            // 拉取最新提交记录，但不进行合并
            git.fetch().call();
            // 设置需要更新的文件或文件夹路径
            try {
                git.checkout()
                        .setStartPoint("origin/master")
                        .addPaths(paths)
                        .call();
            } catch (CheckoutConflictException e) {
                logger.info("Conflicts occurred, resetting local changes to match remote...");
                git.reset()
                        .setMode(ResetCommand.ResetType.HARD)
                        .setRef("refs/remotes/origin/master")
                        .call();
                logger.info("reset success");
            }

        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public static GitDiffResponse diff(String projectName, List<String> fileList, Long workspaceId)throws GitErrorException{

        Set<String> status = status(projectName, fileList, workspaceId);
        GitTree root = new GitTree("");
        for (String statu : status) {
            root.addChild(statu);
        }
        // 打印树形结构
        printTree("", root);
        return new GitDiffResponse(root);
    }

    // 打印树结构
    static void printTree(String prefix, GitTree tree) {
        logger.info(prefix + tree.getName());
        for (GitTree child : tree.getChildren().values()) {
            printTree(prefix + "  ", child);
        }
    }



    public static void push(Repository repository, String projectName, GitUserEntity gitUser, String comment, List<String> paths) throws GitErrorException{

        try {
            Git git = new Git(repository);
            // 添加新增、更改到暂存区
            for (String path : paths) {
                // 添加所有更改
                git.add().addFilepattern(path).call();
                // 添加删除到暂存区
                git.add().setUpdate(true).addFilepattern(path).call();
            }

            // 创建新的提交
            git.commit()
                    .setMessage(comment)
                    .call();

            logger.info("Changes committed to local repository.");

            // 推送到远程仓库
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser.getGitUser(), gitUser.getGitToken()))
                    .call();

            logger.info("Changes pushed to remote repository.");
        } catch (GitAPIException e) {
            reset(repository, projectName);
            throw new GitErrorException(80001, "提交失败，请重试或检查token是否过期", e);
        }
    }


    public static void reset(Repository repository, String projectName)throws GitErrorException {
        try {
            Git git = new Git(repository);

            git.reset().setMode(ResetCommand.ResetType.HARD).call();

            git.clean().setForce(true).setCleanDirectories(true).call();

            logger.info("git reset success : " + projectName);
        } catch (GitAPIException e) {
            throw new GitErrorException(80001, "git reset failed, the reason is: ", e);
        }
    }

    public static void checkoutTargetCommit(Repository repository, GitRevertRequest request) throws GitAPIException, IOException, GitErrorException {
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getWorkspaceId() + File.separator + request.getProjectName()+ File.separator + ".git");
        String commitId = request.getCommitId(); // 替换为目标commit的完整哈希值

        try {
            Git git = new Git(repository);
            // 检出（回滚）指定commit的文件版本
            git.checkout()
                    .setStartPoint(commitId)
                    .addPath(request.getPath())
                    .call();

            logger.info("git check out success");
            logger.info("File " + repoDir.getAbsolutePath() + " has been rolled back to the version at commit: " + commitId);

        } catch (GitAPIException e) {
            reset(repository, request.getProjectName());
            throw new GitErrorException(80001, "git check out failed, the reason is: ", e);
        }
    }

    public static boolean checkIfProjectExists(GitUserEntity gitUser, String projectPath) throws GitErrorException {
        String url = UrlUtils.normalizeIp(gitUser.getGitUrl()) + "/api/v4/projects/" + projectPath.replace("/", "%2F");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("PRIVATE-TOKEN", gitUser.getGitToken());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    return true;
                } else if (statusCode == 404) {
                    return false;
                } else {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    logger.info("Unexpected response status: " + statusCode);
                    logger.info("Response body: " + responseBody);
                    return false;
                }
            }
        } catch (Exception e) {
            throw new GitErrorException(80001, "检查项目名称失败，请检查工作空间token是否过期", e);
        }
    }

    public static String getUserIdByUsername(GitUserEntity gitUser, String username) throws GitErrorException, IOException {
        String url = UrlUtils.normalizeIp(gitUser.getGitUrl()) + "/api/v4/users?username=" + username;
        BufferedReader in = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("PRIVATE-TOKEN", gitUser.getGitToken());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    String responseBody = content.toString();
                    logger.info("Response Body: " + responseBody);

                    JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();

                    if (jsonArray.size() > 0) {
                        JsonObject userObject = jsonArray.get(0).getAsJsonObject();
                        return userObject.get("id").toString();
                    } else {
                        throw new GitErrorException(80001, "获取userId失败，请检查该用户是否为git用户并激活");
                    }
                } else {
                    throw new GitErrorException(80001, "获取userId失败，请检查编辑用户token是否过期或git服务是否正常");
                }
            }
        } catch (Exception e) {
            throw new GitErrorException(80001, "获取该git用户Id失败，原因为", e);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String getProjectIdByName(GitUserEntity gitUser, String projectName) throws GitErrorException{
        String urlString = UrlUtils.normalizeIp(gitUser.getGitUrl()) + "/api/v4/projects?search=" + projectName;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(urlString);
            request.setHeader("PRIVATE-TOKEN", gitUser.getGitToken());

            CloseableHttpResponse response = httpClient.execute(request);
            System.out.println("Response Status Line: " + response.getStatusLine());

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();

                String responseBody = content.toString();
                logger.info("Response Body: " + responseBody);

                JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();

                if (jsonArray.size() > 0) {
                    for (JsonElement element : jsonArray) {
                        JsonObject projectObject = element.getAsJsonObject();
                        if (projectObject.get("name").getAsString().equals(projectName)) {
                            return projectObject.get("id").toString();
                        }
                    }
                } else {
                    throw new GitErrorException(80001, "项目创建失败，请稍后重试");
                }
            } else {
                throw new GitErrorException(80001, "请检查编辑用户token是否过期或git服务是否正常");
            }
        } catch (Exception e) {
            throw new GitErrorException(80001, "获取该项目git Id 失败，原因为", e);
        }
        return null;
    }

    public static boolean addProjectMember(GitUserEntity gitUser, String userId, String projectId, int accessLevel) throws GitErrorException, IOException {
        String url = UrlUtils.normalizeIp(gitUser.getGitUrl()) + "/api/v4/projects/" + projectId + "/members";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.addHeader("PRIVATE-TOKEN", gitUser.getGitToken());
            request.addHeader("Content-Type", "application/json");

            String json = String.format("{\"user_id\": \"%s\", \"access_level\": \"%d\"}", userId, accessLevel);
            request.setEntity(new StringEntity(json));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode == 201) {
                    return true;
                } else {
                    throw new GitErrorException(80001, "添加用户失败，请检查只读用户是否存在或编辑用户token是否过期");
                }
            }
        } catch (Exception e) {
            throw new GitErrorException(80001, "添加用户失败，请检查编辑用户token是否过期或git服务是否正常");
        }
    }

    public static List<String> getAllProjectName(GitUserEntity gitUserDO) throws DSSErrorException {
        int page = 1;
        List<String> allProjectNames = new ArrayList<>();

        List<String> projectNames = new ArrayList<>();
        do {
            // 修改为GitLab实例的URL
            String gitLabUrl = UrlUtils.normalizeIp(gitUserDO.getGitUrl()) + "/api/v4/projects?per_page=100&page=" + page;
            // 创建HttpClient实例
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // 创建HttpGet请求
                HttpGet request = new HttpGet(gitLabUrl);
                // 添加认证头部
                request.addHeader("PRIVATE-TOKEN", gitUserDO.getGitToken());
                // 执行请求
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();
                    // 将响应实体转换为字符串
                    String result = EntityUtils.toString(entity);
                    // 解析项目名称
                    projectNames = parseProjectNames(result);
                    // 打印项目名称
                    logger.info("projectNames is: {}", projectNames.toString());
                    // 添加到总项目列表中
                    allProjectNames.addAll(projectNames);
                }
            } catch (IOException e) {
                throw new GitErrorException(80001, "检查项目名称失败，请检查工作空间token是否过期", e);
            } catch (Exception e) {
                throw new GitErrorException(80001, "检查项目名称时解析JSON失败，请确认git当前是否可访问 ", e);
            }
            page++;
        } while (projectNames.size() > 0);

        return allProjectNames;
    }

    public static List<String> parseProjectNames(String json) throws org.json.JSONException {
        JSONArray projects = new JSONArray(json);
        List<String> projectNames = new ArrayList<>();

        for (int i = 0; i < projects.length(); i++) {
            JSONObject project = projects.getJSONObject(i);
            projectNames.add(project.getString("name"));
        }

        return projectNames;
    }

    public static Set<String> status(String projectName, List<String> fileList, Long workspaceId)throws GitErrorException {
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + workspaceId + File.separator + projectName + File.separator +".git"); // 修改为你的仓库路径

        try (Repository repository = new FileRepositoryBuilder().setGitDir(repoDir).build()) {
            Git git = new Git(repository);
            StatusCommand statusCommand = git.status();
            // 仅关注当前diff改动涉及的文件夹
            for (String file : fileList) {
                statusCommand.addPath(file);
            }

            Status status = statusCommand.call();

            logger.info("Modified files:");
            logger.info("Modified files: {} , \nUntracked files: {}, \nAdded to index: {}, \nChanged files: {}, \nRemoved files: {}, \nMissing files: {}, \nConflicting files: {} ",
                    status.getModified().toString(),
                    status.getUntracked().toString(),
                    status.getAdded().toString(),
                    status.getChanged().toString(),
                    status.getRemoved().toString(),
                    status.getMissing().toString(),
                    status.getConflicting().toString()
                    );

            Set<String> tree = new HashSet<>();
            tree.addAll(status.getModified());
            tree.addAll(status.getUntracked());
            tree.addAll(status.getAdded());
            tree.addAll(status.getChanged());
            tree.addAll(status.getRemoved());
            tree.addAll(status.getMissing());
            tree.addAll(status.getConflicting());

            return tree;
        } catch (IOException | GitAPIException e) {
            throw new GitErrorException(80001, "git status failed, the reason is : ", e);
        }
    }

    public static void archive(String projectName, GitUserEntity gitUserDO) throws GitErrorException {
        try {
            String projectUrlEncoded = java.net.URLEncoder.encode(gitUserDO.getGitUser() + "/" + projectName, "UTF-8");
            URL url = new URL(gitUserDO.getGitUrl() + "/api/v4/projects/" + projectUrlEncoded + "/archive");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("PRIVATE-TOKEN", gitUserDO.getGitToken());

            int responseCode = conn.getResponseCode();
            logger.info("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 打印结果
            logger.info(response.toString());

        } catch (Exception e) {
            throw new GitErrorException(80001, "归档失败，请检查当前token是否过期 ", e);
        }
    }

    public static void archiveLocal(String projectName, Long workspaceId) throws GitErrorException{
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + workspaceId + File.separator + projectName + File.separator + ".git");
        if (!repoDir.exists()) {
            logger.info("file {} not exists", repoDir.getAbsolutePath());
            return ;
        }
        try (Repository repository = new FileRepositoryBuilder().setGitDir(repoDir).build()) {
            Git git = new Git(repository);
            // 删除名为"origin"的远程仓库配置
            git.remoteRemove().setRemoteName("origin").call();
            // 删除本地文件
            FileUtils.removeDirectory(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + workspaceId + File.separator +  projectName);
            logger.info("Remote 'origin' removed successfully.");
        } catch (GitAPIException e) {
            throw new GitErrorException(80001, "git archive failed, the reason is : ", e);
        } catch (IOException e) {
            throw new GitErrorException(80001, "archive failed, the reason is : ", e);
        }
    }

    public static String getTargetCommitFileContent(Repository repository, String projectName, String commitId, String filePath) throws GitErrorException {
        String content = "";
        try {
            // 获取最新的commitId
            ObjectId lastCommitId = repository.resolve(commitId);
            // 获取提交记录
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(lastCommitId);
                RevTree tree = commit.getTree();
                logger.info("Having tree: " + tree);
                // 遍历获取最近提交记录
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(filePath));
                    if (!treeWalk.next()) {
                        throw new IllegalStateException("Did not find expected file '" + filePath + "'");
                    }

                    ObjectId objectId = treeWalk.getObjectId(0);
                    try {
                        ObjectLoader loader = repository.open(objectId);
                        byte[] bytes = loader.getBytes();
                        content = new String(bytes);
                        logger.info("File content: " + content);
                    } catch (Exception e) {
                        logger.error("getFileContent Failed, the reason is: ", e);
                    }
                }
                revWalk.dispose();
            }
        } catch (IOException e) {
            throw new GitErrorException(80001, "getFileContent failed, the reason is : ", e);
        }
        return content;
    }

    public static void getCommitId(Repository repository, String projectName, int num, Long workspaceId)throws GitErrorException {
        // 获取当前CommitId，
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + workspaceId + File.separator +  projectName + File.separator +".git");

        try {
            Git git = new Git(repository);
            Iterable<RevCommit> commits = git.log().setMaxCount(num).call();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (RevCommit commit : commits) {
                PersonIdent authorIdent = commit.getAuthorIdent();
                String commitHash = commit.getName(); // Commit hash
                String commitTime = sdf.format(authorIdent.getWhen()); // Commit time
                String commitMessage = commit.getShortMessage(); // Commit message
                String commitAuthor = authorIdent.getName(); // Commit author

                logger.info("Commit Hash: " + commitHash);
                logger.info("Commit Time: " + commitTime);
                logger.info("Commit Message: " + commitMessage);
                logger.info("Commit Author: " + commitAuthor);
            }
        } catch (Exception e) {
            throw new GitErrorException(80001, "git log failed, the reason is : ", e);
        }
    }

    public static GitCommitResponse getCurrentCommit(Repository repository) throws GitErrorException{
        GitCommitResponse commitResponse = new GitCommitResponse();
        try {
            // 获取HEAD引用
            Ref head = repository.exactRef("HEAD");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 使用RevWalk解析当前的提交
            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit commit = walk.parseCommit(head.getObjectId());
                walk.dispose();
                commitResponse.setCommitId(commit.getId().getName());
                commitResponse.setCommitTime(sdf.format(commit.getAuthorIdent().getWhen()));
                String shortMessage = commit.getShortMessage();

                getUserName(shortMessage, commitResponse, commit);
                // 返回commitId字符串
                return commitResponse;
            }
        } catch (IOException e) {
            throw new GitErrorException(80001, "get current commit failed, the reason is : ", e);
        }
    }

    public static void getUserName(String shortMessage, GitCommitResponse commitResponse, RevCommit commit) {
        if (StringUtils.isEmpty(shortMessage)) {
            return ;
        }

        int lastIndexOf = shortMessage.lastIndexOf(DSSGitConstant.GIT_USERNAME_FLAG);

        if (lastIndexOf != -1) {
            String username = shortMessage.substring(lastIndexOf + DSSGitConstant.GIT_USERNAME_FLAG.length());
            String comment = shortMessage.substring(0, lastIndexOf);
            commitResponse.setCommitUser(username);
            commitResponse.setComment(comment);
        } else {
            commitResponse.setCommitUser(commit.getCommitterIdent().getName());
            commitResponse.setComment(shortMessage);
        }
    }

    public static List<GitCommitResponse> getLatestCommit(Repository repository, String filePath, Integer num) throws GitErrorException{
        List<GitCommitResponse> commitResponseList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().addPath(filePath).setMaxCount(num).call();
            for (RevCommit commit : commits) {
                GitCommitResponse commitResponse = new GitCommitResponse();
                commitResponse.setCommitId(commit.getId().getName());
                commitResponse.setCommitTime(sdf.format(commit.getAuthorIdent().getWhen()));
                String shortMessage = commit.getShortMessage();
                getUserName(shortMessage, commitResponse, commit);
                logger.info("提交ID: " + commit.getId().getName());
                commitResponseList.add(commitResponse);
            }

            return commitResponseList;
        } catch (GitAPIException e) {
            throw new GitErrorException(80001, "get latestCommitId failed, the reason is : ", e);
        }
    }

    public static GitHistoryResponse listCommitsBetween(Repository repository, String oldCommitId, String newCommitId, String path) throws Exception {
        List<GitCommitResponse> gitCommitResponseList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (RevWalk walk = new RevWalk(repository)) {
            Git git = new Git(repository);
            ObjectId commitIdNow = null;
            if (newCommitId == null) {
                ObjectId head = repository.resolve("HEAD");
                commitIdNow = walk.parseCommit(head);
            } else {
                commitIdNow = repository.resolve(newCommitId);
            }


            Iterable<RevCommit> commits = git.log()
                    .addRange(repository.resolve(oldCommitId), commitIdNow)
                    .addPath(path)
                    .call();

            for (RevCommit commit : commits) {
                PersonIdent authorIdent = commit.getAuthorIdent(); // 获取提交人信息
                GitCommitResponse commitResponse = new GitCommitResponse();
                commitResponse.setCommitId(commit.getId().getName());
                commitResponse.setCommitTime(sdf.format(commit.getAuthorIdent().getWhen()));
                String shortMessage = commit.getShortMessage();
                getUserName(shortMessage, commitResponse, commit);
                gitCommitResponseList.add(commitResponse);
                logger.info("Commit Hash: " + commit.getName()); // 提交的Hash值
                logger.info("Commit Time: " + authorIdent.getWhen()); // 提交时间
                logger.info("Commit Message: " + commit.getFullMessage()); // 提交信息
                logger.info("Author: " + authorIdent.getName() + " <" + authorIdent.getEmailAddress() + ">"); // 提交人
            }
        } catch (Exception e) {
            throw new GitErrorException(80001, "get log between " + oldCommitId + " and " + newCommitId + "failed, the reason is : ", e);
        }
        GitHistoryResponse historyResponse = new GitHistoryResponse();
        historyResponse.setResponses(gitCommitResponseList);
        return historyResponse;
    }

    public static String generateGitPath(String projectName, Long workspaceId) {
        // eg ： /data/GitInstall/224/testGit/.git
        return DSSGitConstant.GIT_PATH_PRE + workspaceId + File.separator + projectName + File.separator + DSSGitConstant.GIT_PATH_SUFFIX;
    }








}
