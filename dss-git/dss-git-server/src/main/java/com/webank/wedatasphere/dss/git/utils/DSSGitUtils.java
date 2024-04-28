package com.webank.wedatasphere.dss.git.utils;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitBaseRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitRevertRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitDiffResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.git.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.constant.DSSGitConstant;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jgit.api.*;
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

    public static void init(String projectName, GitUserEntity gitUserDO) throws DSSErrorException {
        if (checkProjectName(projectName, gitUserDO)) {
            try {
                URL url = new URL(GitServerConfig.GIT_URL_PRE.getValue() + GitServerConfig.GIT_RESTFUL_API_CREATE_PROJECTS.getValue());
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
               logger.error("init failed by ", e);
            }
        } else {
            logger.info("projectName {} already exists", projectName);
        }
    }

    public static void remote(Repository repository, String projectName, GitUserEntity gitUser) {
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName + File.separator + ".git");

        try {
            Git git = new Git(repository);

            // 添加远程仓库引用
            git.remoteAdd()
                    .setName("origin")
                    .setUri(new URIish(GitServerConfig.GIT_URL_PRE.getValue() + gitUser.getGitUser() + File.separator + projectName +".git"))
                    .call();

            logger.info("remote success");
        } catch (GitAPIException | URISyntaxException e) {
            logger.error("remote failed by : ", e);
        }

    }


    public static void create(String projectName, GitUserEntity gitUserDO) {
        logger.info("start success");
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName); // 指定仓库的目录
        File respo = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator +  projectName + File.separator +".git");
        if (!respo.exists()) {
            try {
                // 初始化仓库
                Git git = Git.init()
                        .setDirectory(repoDir)
                        .call();

                logger.info("Initialized empty Git repository in " + git.getRepository().getDirectory());

                git.close(); // 不再需要时关闭Git对象
            } catch (GitAPIException e) {
                logger.error("gitCreate failed by : ", e);
            }
        }
    }

    public static void pull(Repository repository, String projectName, GitUserEntity gitUser) {
        try {
            // Opening the repository
            Git git = new Git(repository);

            // Pulling changes from the remote repository
            PullCommand pullCmd = git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser.getGitUser(), gitUser.getGitToken()));
            PullResult result = pullCmd.call();

            // Checking the pull result
            if(result.isSuccessful()) {
                logger.info("Pull successful!");
            } else {
                logger.info("Pull failed: " + result.toString());
            }

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }
    }

    public static GitDiffResponse diff(String projectName, List<String> fileList) {

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



    public static void push(Repository repository, String projectName, GitUserEntity gitUser, String comment) {

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
            logger.error("push failed, the reeason is ", e);
        }
    }


    public static void reset(String projectName) {
        String repoPath = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName + File.separator + ".git"; // 仓库路径
        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoPath))
                .build();
             Git git = new Git(repository)) {

            git.reset().setMode(ResetCommand.ResetType.HARD).call();

            git.clean().setForce(true).setCleanDirectories(true).call();

            logger.info("File has been unstaged: " + projectName);
        } catch (GitAPIException | IOException e) {
            logger.error("reset failed, the reason is ", e);
        }
    }

    public static void checkoutTargetCommit(GitRevertRequest request) {
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName()+ File.separator + ".git");
        String commitId = request.getCommitId(); // 替换为目标commit的完整哈希值

        try (Repository repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
             Git git = new Git(repository)) {

            // 检出（回滚）指定commit的文件版本
            git.checkout()
                    .setStartPoint(commitId)
                    .addPaths(request.getPath())
                    .call();

            logger.info("File " + repoDir.getAbsolutePath() + " has been rolled back to the version at commit: " + commitId);

        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkProjectName(String name, GitUserEntity gitUser) throws DSSErrorException {
        int retry = 0;
        List<String> allProjectName = new ArrayList<>();
        while (retry < 3) {
            retry += 1;
            try {
                allProjectName = getAllProjectName(gitUser);
                return allProjectName.contains(name);
            } catch (DSSErrorException e) {
                logger.info("getAllProjectName failed, try again");
                if (retry >= 3) {
                    logger.error("getAllProjectName failed, the reason is: ", e);
                    throw new DSSErrorException(01001, "getAllProjectName failed");
                }
            }
        }
        return allProjectName.contains(name);

    }

    public static List<String> getAllProjectName(GitUserEntity gitUserDO) throws DSSErrorException {
        int page = 1;
        List<String> allProjectNames = new ArrayList<>();

        List<String> projectNames = new ArrayList<>();
        do {
        String gitLabUrl = "http://git.bdp.weoa.com/api/v4/projects?per_page=100&page=" + page; // 修改为你的GitLab实例的URL
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
            } catch (Exception e) {
                logger.error("getProjectsName failed, the reason is JSON: ", e);
                throw new DSSErrorException(010001, "json 解析失败");
            }
        } catch (IOException e) {
            logger.info("getProjectsName failed, the reason is: ", e);
            throw new DSSErrorException(010001, "Internal error，网络请求失败");
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

    public static Set<String> status(String projectName, List<String> fileList) {
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
            logger.error("get git status Failed, the reason is : ", e);
            return new HashSet<>();
        }
    }

    public static void archive(String projectName, GitUserEntity gitUserDO) {
        try {
            String projectUrlEncoded = java.net.URLEncoder.encode(gitUserDO.getGitUser() + "/" + projectName, "UTF-8");
            URL url = new URL("http://git.bdp.weoa.com/api/v4/projects/" + projectUrlEncoded + "/archive");
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
            logger.error("archive failed by : ", e);
        }
    }

    public static void archiveLocal(String projectName) {
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
        } catch (GitAPIException | IOException e) {
            logger.error("revert remote failed, the reason is: ",e);
        }
    }

    public static void updateLocal(GitBaseRequest request, String filePath) {
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName() + File.separator + ".git");
        Repository repository = null;
        try {
            repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
        } catch (IOException e) {
            logger.error("get remote repos Failed, the reason is : ", e);
        }
        try (Git git = new Git(repository)) {
//            git.reset().addPath(filePath).call();
            git.reset().addPath(filePath).setMode(ResetCommand.ResetType.HARD).setRef("origin/master").call();
            logger.info("File has been unstaged: " + filePath);
        } catch (GitAPIException e) {
            logger.error("updateLocal repos Failed, the reason is : ", e);
        }
    }

    public static String getTargetCommitFileContent(String projectName, String commitId, String filePath) {
        String repoPath = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName + File.separator +".git";
        String content = "";
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            try (Repository repository = repositoryBuilder.setGitDir(new File(repoPath))
                    .readEnvironment()
                    .findGitDir()
                    .build()) {

                ObjectId lastCommitId = repository.resolve(commitId);

                // Instantiate a RevWalk to walk over commits based on some criteria
                try (RevWalk revWalk = new RevWalk(repository)) {
                    RevCommit commit = revWalk.parseCommit(lastCommitId);
                    RevTree tree = commit.getTree();
                    logger.info("Having tree: " + tree);

                    // Now use a TreeWalk to iterate over all files in the Tree recursively
                    // You can set filters to narrow down the results if needed
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
            }
        } catch (IOException e) {
            logger.error("getFileContent Failed, the reason is: ", e);
        }
        return content;
    }

    public static void getCommitId(String projectName, int num) {
        // 获取当前CommitId，
        File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName + File.separator +".git");

        try (Repository repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
             Git git = new Git(repository)) {

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
                logger.info("-----------------------------------");
            }
        } catch (IOException | GitAPIException e) {
            logger.error("get git log failed, the reason is: ", e);
        }
    }

    public static GitCommitResponse getCurrentCommit(Repository repository) {
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
            logger.error("get current commit failed, the reason is : ", e);
            return null;
        }
    }

    public static GitHistoryResponse listCommitsBetween(Repository repository, String startCommitId, String endCommitId) throws Exception {
        List<GitCommitResponse> gitCommitResponseList = new ArrayList<>();

        ObjectId start = repository.resolve(startCommitId);
        ObjectId end = repository.resolve(endCommitId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit startCommit = walk.parseCommit(start);
            RevCommit endCommit = walk.parseCommit(end);

            walk.markStart(startCommit);
            if (endCommit.getParentCount() > 0) {  // Check if endCommit has any parents
                walk.markUninteresting(walk.parseCommit(endCommit.getParent(0))); // Mark parent of end commit as uninteresting
            }

            for (RevCommit commit : walk) {
                PersonIdent authorIdent = commit.getAuthorIdent(); // 获取提交人信息
                GitCommitResponse commitResponse = new GitCommitResponse();
                commitResponse.setCommitId(commit.getId().getName());
                commitResponse.setCommitTime(sdf.format(commit.getAuthorIdent().getWhen()));
                commitResponse.setComment(commit.getShortMessage());
                commitResponse.setCommitUser(commit.getAuthorIdent().getName());
                gitCommitResponseList.add(commitResponse);
                System.out.println("Commit Hash: " + commit.getName()); // 提交的Hash值
                System.out.println("Commit Time: " + authorIdent.getWhen()); // 提交时间
                System.out.println("Commit Message: " + commit.getFullMessage()); // 提交信息
                System.out.println("Author: " + authorIdent.getName() + " <" + authorIdent.getEmailAddress() + ">"); // 提交人
                System.out.println("-----------------------------------------------------");
            }
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
