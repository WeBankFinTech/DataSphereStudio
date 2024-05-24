package com.webank.wedatasphere.dss.git.utils;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitBaseRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitRevertRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitDiffResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.git.constant.DSSGitConstant;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DSSGitUtils {
    private static final Logger logger = LoggerFactory.getLogger(DSSGitUtils.class);

    public static void init(String projectName, GitUserEntity gitUserDO) throws Exception, GitErrorException{
        if (checkProjectName(projectName, gitUserDO)) {
            try {
                URL url = new URL(UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()) + "/" +GitServerConfig.GIT_RESTFUL_API_CREATE_PROJECTS.getValue());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("PRIVATE-TOKEN", gitUserDO.getGitToken());
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonInputString = String.format("{\"name\": \"%s\", \"description\": \"%s\"}", projectName, projectName);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                logger.info("Response Code : " + responseCode);

                connection.disconnect();
                logger.info("init success");
            } catch (Exception e) {
               throw new GitErrorException(80001, "git init failed, the reason is: ", e);
            }
        } else {
            throw new GitErrorException(80001, "git init failed, the reason is: projectName " + projectName +" already exists");
        }
    }

    public static void remote(Repository repository, String projectName, GitUserEntity gitUser)throws GitErrorException {
        // 拼接git remote Url
        String remoteUrl = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()) + "/" +gitUser.getGitUser() + File.separator + projectName + ".git";
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


    public static void create(String projectName, GitUserEntity gitUserDO) throws GitErrorException{
        logger.info("start success");
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName); // 指定仓库的目录
        File respo = new File(generateGitPath(projectName));
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
            throw new GitErrorException(80001, "git pull failed, the reason is: ", e);
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

    public static GitDiffResponse diff(String projectName, List<String> fileList)throws GitErrorException{

        Set<String> status = status(projectName, fileList);
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



    public static void push(Repository repository, String projectName, GitUserEntity gitUser, String comment) throws GitErrorException{

        try {
            Git git = new Git(repository);
            // 添加新增、更改到暂存区
            git.add().addFilepattern(".").call(); // 添加所有更改
            // 添加删除到暂存区
            git.add().setUpdate(true).addFilepattern(".").call();

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
            throw new GitErrorException(80001, "git push failed, the reason is: ", e);
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
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName()+ File.separator + ".git");
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
            throw new GitErrorException(80001, "git check out failed, the reason is: ", e);
        }
    }

    public static boolean checkProjectName(String name, GitUserEntity gitUser) throws DSSErrorException {
        int retry = 0;
        List<String> allProjectName = new ArrayList<>();
        while (true) {
            retry += 1;
            try {
                allProjectName = getAllProjectName(gitUser);
                return allProjectName.contains(name);
            } catch (DSSErrorException e) {
                logger.info("getAllProjectName failed, try again");
                if (retry >= 3) {
                    throw new GitErrorException(80001, "getAllProjectName failed, the reason is: ", e);
                }
            }
        }

    }

    public static List<String> getAllProjectName(GitUserEntity gitUserDO) throws DSSErrorException {
        int page = 1;
        List<String> allProjectNames = new ArrayList<>();

        List<String> projectNames = new ArrayList<>();
        do {
        String gitLabUrl = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()) + "/api/v4/projects?per_page=100&page=" + page; // 修改为你的GitLab实例的URL
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
            throw new GitErrorException(80001, "getProjectsName failed, the reason is ", e);
        } catch (Exception e) {
            throw new GitErrorException(80001, "getProjectsName failed, the reason is JSON: ", e);
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

    public static Set<String> status(String projectName, List<String> fileList)throws GitErrorException {
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName + File.separator +".git"); // 修改为你的仓库路径

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
            URL url = new URL(GitServerConfig.GIT_URL_PRE + "/api/v4/projects/" + projectUrlEncoded + "/archive");
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
            throw new GitErrorException(80001, "git archive failed, the reason is : ", e);
        }
    }

    public static void archiveLocal(String projectName) throws GitErrorException{
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName + File.separator + ".git");
        if (!repoDir.exists()) {
            logger.info("file {} not exists", repoDir.getAbsolutePath());
            return ;
        }
        try (Repository repository = new FileRepositoryBuilder().setGitDir(repoDir).build()) {
            Git git = new Git(repository);
            // 删除名为"origin"的远程仓库配置
            git.remoteRemove().setRemoteName("origin").call();
            // 删除本地文件
            FileUtils.removeDirectory(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName);
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

    public static void getCommitId(Repository repository, String projectName, int num)throws GitErrorException {
        // 获取当前CommitId，
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName + File.separator +".git");

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
                commitResponse.setComment(commit.getShortMessage());
                commitResponse.setCommitUser(commit.getAuthorIdent().getName());
                return commitResponse; // 返回commitId字符串
            }
        } catch (IOException e) {
            throw new GitErrorException(80001, "get current commit failed, the reason is : ", e);
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
                commitResponse.setComment(commit.getShortMessage());
                commitResponse.setCommitUser(commit.getAuthorIdent().getName());
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

            Iterable<RevCommit> commits = git.log()
                    .addRange(repository.resolve(oldCommitId), repository.resolve(newCommitId))
                    .addPath(path)
                    .call();

            for (RevCommit commit : walk) {
                PersonIdent authorIdent = commit.getAuthorIdent(); // 获取提交人信息
                GitCommitResponse commitResponse = new GitCommitResponse();
                commitResponse.setCommitId(commit.getId().getName());
                commitResponse.setCommitTime(sdf.format(commit.getAuthorIdent().getWhen()));
                commitResponse.setComment(commit.getShortMessage());
                commitResponse.setCommitUser(commit.getAuthorIdent().getName());
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

    public static String generateGitPath(String projectName) {
        // eg ： /data/GitInstall/testGit/.git
        return DSSGitConstant.GIT_PATH_PRE + projectName + DSSGitConstant.GIT_PATH_SUFFIX;
    }








}
