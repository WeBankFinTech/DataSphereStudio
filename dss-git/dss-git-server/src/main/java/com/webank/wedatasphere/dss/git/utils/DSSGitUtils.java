package com.webank.wedatasphere.dss.git.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitFileContentResponse;
import org.apache.http.client.methods.HttpDelete;
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
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.OrTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class DSSGitUtils {
    private static final Logger logger = LoggerFactory.getLogger(DSSGitUtils.class);

    public static void init(String projectName, String gitUser, String gitToken, String gitUrl) throws Exception, GitErrorException{
        String projectPath = gitUser + "/" + projectName;

        String url = gitUrl + "/" + GitServerConfig.GIT_RESTFUL_API_CREATE_PROJECTS.getValue();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.addHeader("PRIVATE-TOKEN", gitToken);
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
    }

    public static void remote(Repository repository, String projectName, String gitUser, String gitUrl)throws GitErrorException {
        // 拼接git remote Url
        String remoteUrl = gitUrl + "/" + gitUser + File.separator + projectName + ".git";
        try {
            Git git = new Git(repository);

            // 添加远程仓库引用
            git.remoteAdd()
                    .setName("origin")
                    .setUri(new URIish(remoteUrl))
                    .call();

            logger.info("remote success");
        } catch (URISyntaxException e) {
            throw new GitErrorException(80102, "connect Uri " + remoteUrl +" failed, the reason is: ", e);
        } catch (GitAPIException  e) {
            throw new GitErrorException(80102, "remote git failed, the reason is: ", e);
        }

    }


    public static void create(String projectName, Long workspaceId, String gitUser) throws GitErrorException{
        logger.info("start success");
        File repoDir = new File(generateGitPrePath(projectName, workspaceId, gitUser)); // 指定仓库的目录
        File respo = new File(generateGitPath(projectName, workspaceId, gitUser));
        if (!respo.exists()) {
            try {
                // 初始化仓库
                Git git = Git.init()
                        .setDirectory(repoDir)
                        .call();

                logger.info("Initialized empty Git repository in " + git.getRepository().getDirectory());

                git.close(); // 不再需要时关闭Git对象
            } catch (GitAPIException e) {
                throw new GitErrorException(80103, "git Create failed, the reason is: ", e);
            }
        } else {
            logger.info(respo + " already exists");
        }
    }

    public static void pull(Repository repository, String projectName, String gitUser, String gitToken)throws GitErrorException {
        try {
            Git git = new Git(repository);

            int i = 1;
            while (true) {
                i += 1;
                // 拉取远程仓库更新至本地
                PullCommand pullCmd = git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser, gitToken));
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
                    throw new GitErrorException(80104, "git pull failed");
                }
            }
        } catch (Exception e) {
            // 丢失本地修改，处理冲突
            reset(repository, projectName);
            throw new GitErrorException(80001, "拉取git最新代码失败，原因为:" + e.getMessage());
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

    public static GitDiffResponse diff(Repository repository, String projectName, List<String> fileList, String gitUser, Long workspaceId)throws GitErrorException{

        Set<String> status = status(projectName, fileList, gitUser, workspaceId);
        List<GitTree> codeTree = new ArrayList<>();
        List<GitTree> metaTree = new ArrayList<>();
        GitCommitResponse currentCommit = getCurrentCommit(repository);
        String commitId = currentCommit.getCommitId();
        if (status.isEmpty()) {
            return new GitDiffResponse(codeTree, metaTree, commitId);
        }
        GitTree root = new GitTree("", false);
        GitTree rootMeta = new GitTree("", true);
        for (String statu : status) {
            if (statu.startsWith(GitConstant.GIT_SERVER_META_PATH)) {
                rootMeta.setAbsolutePath(statu);
                rootMeta.addChild(statu);
            } else {
                root.setAbsolutePath(statu);
                root.addChild(statu);
            }
        }
        Map<String, GitTree> children = root.getChildren();
        for (Map.Entry<String, GitTree> entry: children.entrySet()) {
            codeTree.add(entry.getValue());
            printTree("", entry.getValue());
        }

        for (Map.Entry<String, GitTree> entry : rootMeta.getChildren().entrySet()) {
            metaTree.add(entry.getValue());
            DSSGitUtils.printTree("", entry.getValue());
        }

        return new GitDiffResponse(codeTree, metaTree, commitId);
    }

    public static GitDiffResponse diffGit(Repository repository, String projectName, String commitId, String filePath) {
        List<GitTree> codeTree = new ArrayList<>();
        List<GitTree> metaTree = new ArrayList<>();
        try (Git git = new Git(repository)) {
            ObjectId commitObjectId = ObjectId.fromString(commitId);
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(commitObjectId);

                // Get the tree of the specified commit
                ObjectId commitTree = commit.getTree().getId();

                // Get the tree of the current working directory
                ObjectId workTree = repository.resolve("HEAD^{tree}");

                // Prepare the tree iterators for the diff
                CanonicalTreeParser commitTreeParser = new CanonicalTreeParser();
                CanonicalTreeParser workTreeParser = new CanonicalTreeParser();

                try (ObjectReader reader = repository.newObjectReader()) {
                    commitTreeParser.reset(reader, commitTree);
                    workTreeParser.reset(reader, workTree);
                }

                List<String> paths = new ArrayList<>();
                paths.add(filePath);
                paths.add(GitConstant.GIT_SERVER_META_PATH + File.separator + filePath);

                String[] array = paths.toArray(new String[paths.size()]);
                TreeFilter pathFilter = createPathFilter(array);

                // Perform the diff
                List<DiffEntry> diffs = git.diff()
                        .setOldTree(commitTreeParser)
                        .setNewTree(workTreeParser)
                        .setPathFilter(pathFilter)
                        .call();

                GitTree root = new GitTree("", false);
                GitTree rootMeta = new GitTree("", true);
                // Filter the diffs by the specified file path
                for (DiffEntry entry : diffs) {
                    // 获取变更类型和文件路径
                    DiffEntry.ChangeType changeType = entry.getChangeType();
                    String oldPath = entry.getOldPath();
                    String newPath = entry.getNewPath();

                    if (changeType.equals(DiffEntry.ChangeType.RENAME)) {
                        addChild(root, rootMeta, newPath, changeType, oldPath);
                    } else {
                        if (oldPath != null) {
                            addChild(root, rootMeta, oldPath, changeType, null);
                        }
                        if (newPath != null) {
                            addChild(root, rootMeta, newPath, changeType, null);
                        }
                    }
                }
                Map<String, GitTree> children = root.getChildren();
                for (Map.Entry<String, GitTree> entry: children.entrySet()) {
                    codeTree.add(entry.getValue());
                    printTree("", entry.getValue());
                }
                for (Map.Entry<String, GitTree> entry: rootMeta.getChildren().entrySet()) {
                    metaTree.add(entry.getValue());
                    printTree("", entry.getValue());
                }

            } catch (GitAPIException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new GitDiffResponse(codeTree, metaTree, null);
    }

    private static TreeFilter createPathFilter(String[] paths) {
        TreeFilter[] filters = new TreeFilter[paths.length];
        for (int i = 0; i < paths.length; i++) {
            filters[i] = PathFilter.create(paths[i]);
        }
        return OrTreeFilter.create(filters);
    }

    private static void addChild(GitTree root, GitTree rootMeta, String path, DiffEntry.ChangeType status, String oldFilePath) {
        if (path == null) {
            return;
        }
        String pathName = path;
        if (pathName.contains("/dev/null")) {
            return ;
        }
        // 处理重命名文件
        if (status.equals(DiffEntry.ChangeType.RENAME)) {
            String oldFileName = oldFilePath.substring(oldFilePath.lastIndexOf("/") + 1);
            pathName = path + "--(" + oldFileName + ")--";
        }
        if (path.startsWith(GitConstant.GIT_SERVER_META_PATH)) {
            rootMeta.setAbsolutePath(path);
            rootMeta.addChild(pathName);
        } else {
            root.setAbsolutePath(path);
            root.addChild(pathName);
        }
    }

    // 打印树结构
    public static void printTree(String prefix, GitTree tree) {
        logger.info(prefix + tree.getName());
        for (GitTree child : tree.getChildren().values()) {
            printTree(prefix + "  ", child);
        }
    }



    public static void push(Repository repository, String projectName, String gitUser, String gitToken, String comment, List<String> paths) throws GitErrorException{

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
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser, gitToken))
                    .call();

            logger.info("Changes pushed to remote repository.");
        } catch (GitAPIException e) {
            reset(repository, projectName);
            throw new GitErrorException(80105, "提交失败，请重试或检查token是否过期", e);
        }
    }


    public static void reset(Repository repository, String projectName)throws GitErrorException {
        try {
            Git git = new Git(repository);

            git.reset().setMode(ResetCommand.ResetType.HARD).call();

            git.clean().setForce(true).setCleanDirectories(true).call();

            logger.info("git reset success : " + projectName);
        } catch (GitAPIException e) {
            throw new GitErrorException(80106, "git reset failed, the reason is: ", e);
        }
    }

    public static void checkoutTargetCommit(Repository repository, GitRevertRequest request, String gitUser) throws GitAPIException, IOException, GitErrorException {
        File repoDir = new File(generateGitPath(request.getProjectName(), request.getWorkspaceId(), gitUser));
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
            throw new GitErrorException(80107, "git check out failed, the reason is: ", e);
        }
    }

    public static boolean checkIfProjectExists(String gitToken, String projectPath) throws GitErrorException {
        String url = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()) + "/api/v4/projects/" + projectPath.replace("/", "%2F");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("PRIVATE-TOKEN", gitToken);

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
            throw new GitErrorException(80108, "检查项目名称失败，请检查工作空间token是否过期", e);
        }
    }

    public static String getUserIdByUsername(String gitUrl, String gitToken, String username) throws GitErrorException, IOException {
        String url = gitUrl + "/api/v4/users?username=" + username;
        BufferedReader in = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("PRIVATE-TOKEN", gitToken);

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
                        throw new GitErrorException(80109, "获取userId失败，请检查该用户是否为git用户并激活");
                    }
                } else {
                    throw new GitErrorException(80109, "获取userId失败，请检查编辑用户token是否过期或git服务是否正常");
                }
            }
        } catch (Exception e) {
            throw new GitErrorException(80109, "获取该git用户Id失败，原因为", e);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String getProjectIdByName(String projectName, String gitUser, String gitToken, String gitUrl) throws GitErrorException{
        String urlString = gitUrl + "/api/v4/projects?search=" + projectName;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(urlString);
            request.setHeader("PRIVATE-TOKEN", gitToken);

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
                    throw new GitErrorException(80110, "项目创建失败，请稍后重试");
                }
            } else {
                throw new GitErrorException(80110, "请检查编辑用户token是否过期或git服务是否正常");
            }
        } catch (Exception e) {
            throw new GitErrorException(80110, "获取该项目git Id 失败，原因为", e);
        }
        return null;
    }

    public static boolean addProjectMember(String gitUrl, String gitToken, String userId, String projectId, int accessLevel) throws GitErrorException, IOException {
        String url = gitUrl + "/api/v4/projects/" + projectId + "/members";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.addHeader("PRIVATE-TOKEN", gitToken);
            request.addHeader("Content-Type", "application/json");

            String json = String.format("{\"user_id\": \"%s\", \"access_level\": \"%d\"}", userId, accessLevel);
            request.setEntity(new StringEntity(json));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode == 201 || (statusCode == 409 && responseBody.contains("Member already exists"))) {
                    return true;
                } else {
                    throw new GitErrorException(80111, "添加用户失败，请检查只读用户是否存在或编辑用户token是否过期");
                }
            }
        } catch (Exception e) {
            throw new GitErrorException(80111, "添加用户失败，请检查编辑用户token是否过期或git服务是否正常");
        }
    }

//    public static boolean removeProjectMember(GitUserEntity gitUser, String userId, String projectId) throws GitErrorException {
//        String urlString = UrlUtils.normalizeIp(gitUser.getGitUrl()) + "/api/v4/projects/" + projectId + "/members/" + userId;
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpDelete request = new HttpDelete(urlString);
//            request.addHeader("PRIVATE-TOKEN", gitUser.getGitToken());
//
//            try (CloseableHttpResponse response = httpClient.execute(request)) {
//                int responseCode = response.getStatusLine().getStatusCode();
//                if (responseCode == 204) {
//                    return true;
//                } else {
//                    throw new GitErrorException(80112, "请检查工作空间Git只读用户是否存在");
//                }
//            }
//        } catch (IOException e) {
//            throw new GitErrorException(80112, "更新用户权限失败", e);
//        }
//    }

    public static List<String> getAllProjectName(String gitToken, String gitUrl) throws DSSErrorException {
        int page = 1;
        List<String> allProjectNames = new ArrayList<>();

        List<String> projectNames = new ArrayList<>();
        do {
            // 修改为GitLab实例的URL
            String gitLabUrl = gitUrl + "/api/v4/projects?per_page=100&page=" + page;
            // 创建HttpClient实例
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // 创建HttpGet请求
                HttpGet request = new HttpGet(gitLabUrl);
                // 添加认证头部
                request.addHeader("PRIVATE-TOKEN", gitToken);
                // 执行请求
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();
                    // 将响应实体转换为字符串
                    String result = EntityUtils.toString(entity);
                    // 解析项目名称
                    projectNames = parseProjectNames(result);
                    // 打印项目名称
                    logger.info("projectNames is: {}", projectNames);
                    // 添加到总项目列表中
                    allProjectNames.addAll(projectNames);
                }
            } catch (IOException e) {
                throw new GitErrorException(80113, "检查项目名称失败，请检查工作空间token是否过期", e);
            } catch (Exception e) {
                throw new GitErrorException(80113, "检查项目名称时解析JSON失败，请确认git当前是否可访问 ", e);
            }
            page++;
        } while (!projectNames.isEmpty());

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

    public static Set<String> status(String projectName, List<String> fileList, String gitUser, Long workspaceId)throws GitErrorException {
        File repoDir = new File(generateGitPath(projectName, workspaceId, gitUser)); // 修改为你的仓库路径

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
            // 仅关注 修改、删除、新增并且未暂存的文件
            Set<String> fileSet = new HashSet<>();
            if (!CollectionUtils.isEmpty(status.getModified())) {
                fileSet.addAll(status.getModified());
            }
            if (!CollectionUtils.isEmpty(status.getMissing())) {
                fileSet.addAll(status.getMissing());
            }
            if (!CollectionUtils.isEmpty(status.getUntracked())) {
                fileSet.addAll(status.getUntracked());
            }

            return fileSet;
        } catch (IOException | GitAPIException e) {
            throw new GitErrorException(80114, "git status failed, the reason is : ", e);
        }
    }

    public static void archive(String projectName, String gitUser, String gitToken, String gitUrl) throws GitErrorException {
        try {
            String projectUrlEncoded = java.net.URLEncoder.encode(gitUser + "/" + projectName, "UTF-8");
            URL url = new URL(gitUrl + "/api/v4/projects/" + projectUrlEncoded + "/archive");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("PRIVATE-TOKEN", gitToken);

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
            throw new GitErrorException(80115, "归档失败，请检查当前token是否过期 ", e);
        }
    }

    public static void archiveLocal(String projectName, String gitUser, Long workspaceId) throws GitErrorException{
        File repoDir = new File(generateGitPath(projectName, workspaceId, gitUser));
        if (!repoDir.exists()) {
            logger.info("file {} not exists", repoDir.getAbsolutePath());
            return ;
        }
        try (Repository repository = new FileRepositoryBuilder().setGitDir(repoDir).build()) {
            Git git = new Git(repository);
            // 删除名为"origin"的远程仓库配置
            git.remoteRemove().setRemoteName("origin").call();
            // 删除本地文件
            FileUtils.removeDirectory(generateGitPrePath(projectName, workspaceId, gitUser));
            logger.info("Remote 'origin' removed successfully.");
        } catch (GitAPIException e) {
            throw new GitErrorException(80116, "git archive failed, the reason is : ", e);
        } catch (IOException e) {
            throw new GitErrorException(80116, "archive failed, the reason is : ", e);
        }
    }

    public static String getFileContent(String path, String projectName, String gitUser, Long workspaceId) throws IOException {
        String filePath = generateGitPrePath(projectName, workspaceId, gitUser) + File.separator + path;
        File file = new File(filePath);
        if (file.exists()) {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }
        return null;
    }

    public static void getTargetCommitFileContent(Repository repository, String commitId, String filePath, GitFileContentResponse contentResponse) throws GitErrorException {
        String content = "";
        try {
            // 获取最新的commitId
            ObjectId lastCommitId = repository.resolve(commitId);
            // 获取提交记录
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(lastCommitId);
                if (commit != null) {
                    contentResponse.setBeforeAnnotate(commit.getShortMessage());
                    contentResponse.setBeforeCommitId(commit.getId().getName());
                }
                RevTree tree = commit.getTree();
                logger.info("Having tree: {}", tree);
                // 遍历获取最近提交记录
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(filePath));
                    if (!treeWalk.next()) {
                        logger.warn("Did not find expected file '{}'，忽略", filePath);
                        contentResponse.setBefore(null);
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
            throw new GitErrorException(80117, "getFileContent failed, the reason is : ", e);
        }
        contentResponse.setBefore(content);
    }

    public static RevCommit getLatestCommitInfo(Repository repository, String filePath, String projectName, Long workspaceId, String gitUser) throws GitErrorException {
        try {
            Git git = new Git(repository);
            Iterable<RevCommit> commits = git.log().addPath(filePath).call();
            String path = DSSGitUtils.generateGitPrePath(projectName, workspaceId, gitUser) + filePath;
            File file = new File(path);
            if (file.exists()) {
                for (RevCommit commit : commits) {
                    return commit;
                }
            } else {
                RevCommit previousCommit = null;
                for (RevCommit commit : commits) {
                    if (commit != null) {
                        try (TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), path, commit.getTree())) {
                            if (treeWalk == null) {
                                // The file does not exist in this commit, so this is the deletion commit
                                return commit;
                            }
                        }
                    }
                    previousCommit = commit;
                }
            }
            return null;
        } catch (Exception e) {
            throw new GitErrorException(80118, "getLatestCommitInfo failed, the reason is : ", e);
        }
    }

    public static RevCommit getTargetCommitInfo(Repository repository, String commitId) throws GitErrorException {
        try {
            Git git = new Git(repository);
            ObjectId commitIdInfo = ObjectId.fromString("commitId");
            RevWalk walk = new RevWalk(repository);
            RevCommit commit = walk.parseCommit(commitIdInfo);
            return commit;
        } catch (Exception e) {
            throw new GitErrorException(80118, "getTargetCommitInfo failed, the reason is : ", e);
        }
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
            throw new GitErrorException(80118, "git log failed, the reason is : ", e);
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
            throw new GitErrorException(80119, "get current commit failed, the reason is : ", e);
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
            Iterable<RevCommit> commits = null;
            if (num == null) {
                commits = git.log().addPath(filePath).call();
            } else {
                commits = git.log().addPath(filePath).setMaxCount(num).call();
            }
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
            throw new GitErrorException(80120, "get latestCommitId failed, the reason is : ", e);
        }
    }

    public static GitHistoryResponse listCommitsBetween(Repository repository, String oldCommitId, String newCommitId, String path) throws Exception {
        List<GitCommitResponse> gitCommitResponseList = new ArrayList<>();
        Set<String> commitIdSet = new HashSet<>();


        try (RevWalk walk = new RevWalk(repository)) {
            Git git = new Git(repository);
            ObjectId commitIdNow = null;
            if (newCommitId == null) {
                ObjectId head = repository.resolve("HEAD");
                commitIdNow = walk.parseCommit(head);
            } else {
                commitIdNow = repository.resolve(newCommitId);
            }
            // 代码改动
            gitLogHistory(git, repository, oldCommitId, commitIdNow, path, gitCommitResponseList, commitIdSet);
            // 元数据改动
            gitLogHistory(git, repository, oldCommitId, commitIdNow, GitConstant.GIT_SERVER_META_PATH + File.separator + path, gitCommitResponseList, commitIdSet);
        } catch (Exception e) {
            throw new GitErrorException(80121, "get log between " + oldCommitId + " and " + newCommitId + "failed, the reason is : ", e);
        }
        GitHistoryResponse historyResponse = new GitHistoryResponse();
        historyResponse.setResponses(gitCommitResponseList);
        return historyResponse;
    }

    private static void gitLogHistory(Git git, Repository repository, String oldCommitId, ObjectId commitIdNow, String path, List<GitCommitResponse> gitCommitResponseList, Set<String> commitIdSet) throws IOException, GitAPIException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Iterable<RevCommit> commits = git.log()
                .addRange(repository.resolve(oldCommitId), commitIdNow)
                .addPath(path)
                .call();

        for (RevCommit commit : commits) {
            PersonIdent authorIdent = commit.getAuthorIdent(); // 获取提交人信息
            GitCommitResponse commitResponse = new GitCommitResponse();
            String commitId = commit.getId().getName();
            if (!commitIdSet.contains(commitId)) {
                commitIdSet.add(commitId);
                commitResponse.setCommitId(commitId);
                commitResponse.setCommitTime(sdf.format(commit.getAuthorIdent().getWhen()));
                String shortMessage = commit.getShortMessage();
                getUserName(shortMessage, commitResponse, commit);
                gitCommitResponseList.add(commitResponse);
                logger.info("Commit Hash: " + commit.getName()); // 提交的Hash值
                logger.info("Commit Time: " + authorIdent.getWhen()); // 提交时间
                logger.info("Commit Message: " + commit.getFullMessage()); // 提交信息
                logger.info("Author: " + authorIdent.getName() + " <" + authorIdent.getEmailAddress() + ">"); // 提交人
            }
        }
    }



    public static String generateGitPath(String projectName, Long workspaceId, String gitUser) {
        // eg ： /data/GitInstall/224/testGit/.git
        return generateGitPrePath(projectName, workspaceId, gitUser) + File.separator + DSSGitConstant.GIT_PATH_SUFFIX;
    }

    public static String generateGitPrePath(String projectName, Long workspaceId, String gitUser) {
        // eg ： /data/GitInstall/224/testGit
        return DSSGitConstant.GIT_PATH_PRE + workspaceId + File.separator + gitUser + File.separator + projectName ;
    }








}
