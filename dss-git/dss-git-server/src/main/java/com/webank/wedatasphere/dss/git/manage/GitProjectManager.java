package com.webank.wedatasphere.dss.git.manage;


import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.git.dao.DSSWorkspaceGitMapper;
import com.webank.wedatasphere.dss.git.dto.GitProjectGitInfo;
import com.webank.wedatasphere.dss.git.utils.DSSGitUtils;
import com.webank.wedatasphere.dss.git.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.linkis.DataWorkCloudApplication;
import org.apache.linkis.common.utils.Utils;
import org.eclipse.jgit.api.Git;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GitProjectManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitProjectManager.class);

    private volatile static boolean isInit;

    private static DSSWorkspaceGitMapper workspaceGitMapper;


    protected GitProjectManager() {
    }

    static {
        LOGGER.info("已归档项目移除定时线程开启...");
        init();
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            try {
                List<Long> allWorkspaceId = workspaceGitMapper.getAllWorkspaceId();
                for (Long workspaceId : allWorkspaceId) {
                    List<GitProjectGitInfo> projectInfoByWorkspaceId = getProjectInfo(workspaceId);
                    Map<String, Map<String, String>> map = getMap(projectInfoByWorkspaceId);
                    for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                        String gitUser = entry.getKey();
                        Map<String, String> value = entry.getValue();
                        for (Map.Entry<String, String> entry1 : value.entrySet()) {
                            String gitUrl = entry1.getKey();
                            String gitToken = generateKeys(entry1.getValue(), Cipher.DECRYPT_MODE);
                            List<String> allGitProjectName = DSSGitUtils.getAllProjectName(gitToken, gitUrl);
                            List<String> localProjectName = FileUtils.getLocalProjectName(workspaceId, gitUser);
                            LOGGER.info("localProjectName is : {}", localProjectName);
                            localProjectName.removeAll(allGitProjectName);
                            if (!localProjectName.isEmpty()) {
                                LOGGER.info("Project to delete : {}", localProjectName.toString());
                                for (String projectName : localProjectName) {
                                    // 删除本地项目
                                    DSSGitUtils.archiveLocal(projectName, gitUser, workspaceId);
                                }
                            } else {
                                LOGGER.info("Nothing need to delete");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("定时清理归档项目错误", e);
            }
        }, 0,1, TimeUnit.DAYS);
    }

    private static Map<String, Map<String, String>> getMap(List<GitProjectGitInfo> projectInfoByWorkspaceId) {
        Map<String, Map<String, String>> map = new HashMap<>();
        for (GitProjectGitInfo projectGitInfo : projectInfoByWorkspaceId) {
            String gitUserName = projectGitInfo.getGitUser();
            String gitToken = projectGitInfo.getGitToken();
            String gitUrl = projectGitInfo.getGitUrl();
            Map<String, String> tempMap = new HashMap<>();
            if (map.containsKey(gitUserName)) {
                tempMap = map.get(gitUserName);
            }
            tempMap.put(gitUrl, gitToken);
            map.put(gitUserName, tempMap);
        }
        return map;
    }

    public static void init() {
        if (!isInit) {
            synchronized (GitProjectManager.class) {
                if (!isInit) {
                    workspaceGitMapper = DataWorkCloudApplication.getApplicationContext().getBean(DSSWorkspaceGitMapper.class);
                    isInit = true;
                }
            }
        }
    }


    public static GitUserUpdateResponse associateGit(GitUserUpdateRequest gitUserCreateRequest) throws DSSErrorException, IOException {
        return new GitUserUpdateResponse(0, "", 0L);
    }

//    private static GitUserUpdateResponse getUserIdFromGit(String type, Boolean update, Long workspaceId, String gitUser, String gitToken) throws IOException, com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException {
//        if (GitConstant.GIT_ACCESS_READ_TYPE.equals(type)) {
//            GitUserEntity writeGitUser = selectGit(workspaceId, GitConstant.GIT_ACCESS_WRITE_TYPE, true);
//            if (writeGitUser == null) {
//                return new GitUserUpdateResponse(80001, "配置只读用户前需首先完成该工作空间编辑用户的配置", workspaceId);
//            }
//            String userGitId = DSSGitUtils.getUserIdByUsername(writeGitUser, gitUser);
//            gitUser.setGitUserId(userGitId);
//            if (update) {
//                List<GitProjectGitInfo> projectGitInfos = workspaceGitMapper.getProjectInfoByWorkspaceId(workspaceId);
//                for (GitProjectGitInfo projectGitInfo : projectGitInfos) {
//                    // 删除权限
//                    LOGGER.info("删除用户" + oldGitUser.getGitUser() + "在" + projectGitInfo.getProjectName() + "项目的只读权限");
//                    DSSGitUtils.removeProjectMember(writeGitUser, oldGitUser.getGitUserId(), projectGitInfo.getGitProjectId());
//                    LOGGER.info("删除成功");
//                    // 增加权限
//                    LOGGER.info("增加用户" + gitUser.getGitUser() + "在" + projectGitInfo.getProjectName() + "项目的只读权限");
//                    DSSGitUtils.addProjectMember(writeGitUser, userGitId, projectGitInfo.getGitProjectId(), 20);
//                    LOGGER.info("增加成功");
//                }
//            }
//        }
//        return null;
//    }

    public static void insert(GitProjectGitInfo projectGitInfo, Boolean isExist) throws DSSErrorException {
        String projectName = projectGitInfo.getProjectName();
        // 加密处理密码
        String gitToken = projectGitInfo.getGitToken();
        String encryptToken = generateKeys(gitToken, Cipher.ENCRYPT_MODE);
        projectGitInfo.setGitToken(encryptToken);

        if (isExist) {
            workspaceGitMapper.updateProjectToken(projectName, encryptToken);
        } else {
            workspaceGitMapper.insertProjectInfo(projectGitInfo);
        }
    }

    public static GitProjectGitInfo getProjectInfoByProjectName(String projectName) throws DSSErrorException {
        GitProjectGitInfo projectInfoByProjectName = workspaceGitMapper.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName != null) {
            String gitToken = projectInfoByProjectName.getGitToken();
            String token = generateKeys(gitToken, Cipher.DECRYPT_MODE);
            projectInfoByProjectName.setGitToken(token);
        }
        return projectInfoByProjectName;
    }

    public static List<GitProjectGitInfo> getProjectInfo(Long workspaceId) {
        return workspaceGitMapper.getProjectInfoByWorkspaceId(workspaceId);
    }

//    public static GitUserInfoResponse selectGitUserInfo(GitUserInfoRequest gitUserInfoRequest) throws DSSErrorException {
//        if (gitUserInfoRequest == null) {
//            throw  new DSSErrorException(010101, "gitUserCreateRequest is null");
//        }
//        Long workspaceId = gitUserInfoRequest.getWorkspaceId();
//        String type = gitUserInfoRequest.getType();
//        GitUserEntity gitUserEntity = selectGit(workspaceId, type, gitUserInfoRequest.getDecrypt());
//
//
//        GitUserInfoResponse gitUserInfoResponse = new GitUserInfoResponse();
//        gitUserInfoResponse.setGitUser(gitUserEntity);
//        return gitUserInfoResponse;
//    }

//    public static GitUserEntity selectGit(Long workspaceId, String type, Boolean decrypt) {
//        try {
//            GitUserEntity gitUserEntity = workspaceGitMapper.selectByWorkspaceId(workspaceId, type);
//            if (gitUserEntity == null) {
//                return null;
//            }
//            // 密码 token 解密处理
//            if (!StringUtils.isEmpty(gitUserEntity.getGitPassword())) {
//                String encryptPassword = generateKeys(gitUserEntity.getGitPassword(), Cipher.DECRYPT_MODE);
//                gitUserEntity.setGitPassword(encryptPassword);
//            }
//            if (!StringUtils.isEmpty(gitUserEntity.getGitToken())) {
//                String encryptToken = generateKeys(gitUserEntity.getGitToken(), Cipher.DECRYPT_MODE);
//                gitUserEntity.setGitToken(encryptToken);
//            }
//            return gitUserEntity;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public static String generateKeys(String password, int mode) throws DSSErrorException{
        // 定义一个字符串作为密钥源
        String keyString = GitServerConfig.LINKIS_MYSQL_PRI_KEY.getValue();
        if (keyString.length() < 16) {
            throw new DSSErrorException(800001, "密钥长度必须大于16位");
        }
        if (StringUtils.isEmpty(password)) {
            throw new DSSErrorException(800001, "密码或token为空");
        }
        try {
            // 确保密钥长度合适，AES 密钥长度为 128 位 (16 字节)
            byte[] keyBytes = keyString.substring(0, 16).getBytes();
            Key key = new SecretKeySpec(keyBytes, "AES");
            // 加密Cipher对象
            Cipher encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(mode, key);
            if (mode == Cipher.ENCRYPT_MODE) {
                byte[] encrypted = encryptCipher.doFinal(password.getBytes());
                return Base64.getEncoder().encodeToString(encrypted);
            } else {
                byte[] decode = Base64.getDecoder().decode(password);
                byte[] encrypted = encryptCipher.doFinal(decode);
                return new String(encrypted);
            }
        } catch (Exception e) {
            throw new DSSErrorException(800001, "加密失败,原因为" + e);
        }
    }

    public static Boolean gitTokenTest(String token, String expectedUsername)throws DSSErrorException {
        // GitLab API 用户信息接口
        String apiUrl = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()) + "/api/v4/user";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder(apiUrl);
            HttpGet request = new HttpGet(builder.build());
            request.setHeader("PRIVATE-TOKEN", token);

            HttpResponse response = client.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity());


            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject userData = new JSONObject(jsonResponse);

                String actualUsername = userData.getString("username");
                if (actualUsername.equals(expectedUsername)) {
                    LOGGER.info("Token is valid and matches the username: " + actualUsername);
                    return true;
                }else {
                    LOGGER.info("当前token与用户名" + actualUsername + "匹配，与当前用户名" + expectedUsername + "不匹配");
                    throw new DSSErrorException(800001, "当前用户名 token 不匹配，请检查");
                }
            } else if (response.getStatusLine().getStatusCode() == 401){
                throw new DSSErrorException(800001, "请检查token是否正确");
            }
        } catch (DSSErrorException e) {
            LOGGER.info("Error verifying token: " + e.getMessage());
            throw new DSSErrorException(800001, "校验失败" + e.getMessage());
        }catch (Exception e) {
            throw new DSSErrorException(800001, "校验token失败，请确认当前环境git是否可以正常访问" + e.getMessage());
        }
        return false;
    }

    public static GitUserInfoListResponse getGitUserByType(GitUserInfoByRequest infoByRequest) {
        List<GitUserEntity> gitUserEntities = workspaceGitMapper.selectGitUser(infoByRequest.getWorkspaceId(), infoByRequest.getType(), infoByRequest.getGitUserName());
        GitUserInfoListResponse gitUserInfoListResponse = new GitUserInfoListResponse();
        gitUserInfoListResponse.setGitUserEntities(gitUserEntities);
        return gitUserInfoListResponse;
    }

    public static void updateGitProjectId(String projectName, String gitProjectId) {
        workspaceGitMapper.updateProjectId(projectName, gitProjectId);
    }


}
