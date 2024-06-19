package com.webank.wedatasphere.dss.git.manage;


import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitConnectRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoByRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserUpdateRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitConnectResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoListResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserUpdateResponse;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                    GitUserEntity gitUser = selectGit(workspaceId, GitConstant.GIT_ACCESS_WRITE_TYPE, true);
                    if (gitUser == null) {
                        break;
                    }
                    List<String> allGitProjectName = DSSGitUtils.getAllProjectName(gitUser);
                    List<String> localProjectName = FileUtils.getLocalProjectName(workspaceId);
                    LOGGER.info("localProjectName is : {}", localProjectName.toString());
                    localProjectName.removeAll(allGitProjectName);
                    if (localProjectName.size() > 0) {
                        LOGGER.info("Project to delete : {}", localProjectName.toString());
                        for (String projectName : localProjectName) {
                            // 删除本地项目
                            DSSGitUtils.archiveLocal(projectName, workspaceId);
                        }
                    } else {
                        LOGGER.info("Nothing need to delete");
                    }

                }
            } catch (Exception e) {
                LOGGER.error("定时清理归档项目错误", e);
            }
        }, 0,1, TimeUnit.DAYS);
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
        if (gitUserCreateRequest == null) {
            throw new DSSErrorException(010101, "gitUserCreateRequest is null");
        }
        GitUserEntity gitUser = gitUserCreateRequest.getGitUser();
        String userName = gitUserCreateRequest.getUsername();
        if (gitUser == null) {
            throw new DSSErrorException(010101, "gitUser is null");
        }
        String type = gitUser.getType();


        // 不存在则更新，存在则新增
        GitUserEntity oldGitUserDo = selectGit(gitUser.getWorkspaceId(), type, true);
        gitUser.setUpdateBy(userName);
        // 密码 token 加密处理
        if (!StringUtils.isEmpty(gitUser.getGitPassword())) {
            String encryptPassword = generateKeys(gitUser.getGitPassword(), Cipher.ENCRYPT_MODE);
            gitUser.setGitPassword(encryptPassword);
        }
        if (!StringUtils.isEmpty(gitUser.getGitToken())) {
            String encryptToken = generateKeys(gitUser.getGitToken(), Cipher.ENCRYPT_MODE);
            gitUser.setGitToken(encryptToken);
        }
        GitUserEntity gitUserEntity = workspaceGitMapper.selectByUser(gitUser.getGitUser());
        gitUser.setGitUrl(UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()));
        if (oldGitUserDo == null) {
            // 工作空间--git用户 为一一对应关系
            if (gitUserEntity != null) {
                return new GitUserUpdateResponse(80001, "该用户已配置为" + gitUserEntity.getWorkspaceId() + "工作空间的读写或只读用户，请更换用户", gitUserEntity.getWorkspaceId());
            }
            gitUser.setCreateBy(userName);
            GitUserUpdateResponse userIdFromGit = getUserIdFromGit(gitUser, type, false, oldGitUserDo);
            if (userIdFromGit != null) {
                return userIdFromGit;
            }
            workspaceGitMapper.insert(gitUser);
        }else {
            if (GitConstant.GIT_ACCESS_WRITE_TYPE.equals(type) && !oldGitUserDo.getGitUser().equals(gitUser.getGitUser())) {
                throw new DSSErrorException(800001, "用户名不得修改");
            }
            if (gitUserEntity != null && (!gitUserEntity.getWorkspaceId().equals(gitUser.getWorkspaceId()) || !gitUserEntity.getType().equals(type))) {
                return new GitUserUpdateResponse(010101, "该用户已配置为" + gitUserEntity.getWorkspaceId() + "工作空间的读写或只读用户，请更换用户", gitUserEntity.getWorkspaceId());
            }
            GitUserUpdateResponse userIdFromGit = getUserIdFromGit(gitUser, type, true, oldGitUserDo);
            if (userIdFromGit != null) {
                return userIdFromGit;
            }
            workspaceGitMapper.update(gitUser);
        }

        return new GitUserUpdateResponse(0,"", null);
    }

    private static GitUserUpdateResponse getUserIdFromGit(GitUserEntity gitUser, String type, Boolean update, GitUserEntity oldGitUser) throws IOException, com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException {
        if (GitConstant.GIT_ACCESS_READ_TYPE.equals(type)) {
            GitUserEntity writeGitUser = selectGit(gitUser.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE, true);
            if (writeGitUser == null) {
                return new GitUserUpdateResponse(80001, "配置只读用户前需首先完成该工作空间编辑用户的配置", gitUser.getWorkspaceId());
            }
            String userGitId = DSSGitUtils.getUserIdByUsername(writeGitUser, gitUser.getGitUser());
            gitUser.setGitUserId(userGitId);
            if (update) {
                List<GitProjectGitInfo> projectGitInfos = workspaceGitMapper.getProjectIdListByWorkspaceId(gitUser.getWorkspaceId());
                for (GitProjectGitInfo projectGitInfo : projectGitInfos) {
                    // 删除权限
                    LOGGER.info("删除用户" + oldGitUser.getGitUser() + "在" + projectGitInfo.getProjectName() + "项目的只读权限");
                    DSSGitUtils.removeProjectMember(writeGitUser, oldGitUser.getGitUserId(), projectGitInfo.getGitProjectId());
                    LOGGER.info("删除成功");
                    // 增加权限
                    LOGGER.info("增加用户" + gitUser.getGitUser() + "在" + projectGitInfo.getProjectName() + "项目的只读权限");
                    DSSGitUtils.addProjectMember(writeGitUser, userGitId, projectGitInfo.getGitProjectId(), 20);
                    LOGGER.info("增加成功");
                }
            }
        }
        return null;
    }

    public static void insert (GitProjectGitInfo projectGitInfo) {
        workspaceGitMapper.insertProjectInfo(projectGitInfo);
    }

    public static GitUserInfoResponse selectGitUserInfo(GitUserInfoRequest gitUserInfoRequest) throws DSSErrorException {
        if (gitUserInfoRequest == null) {
            throw  new DSSErrorException(010101, "gitUserCreateRequest is null");
        }
        Long workspaceId = gitUserInfoRequest.getWorkspaceId();
        String type = gitUserInfoRequest.getType();
        GitUserEntity gitUserEntity = selectGit(workspaceId, type, gitUserInfoRequest.getDecrypt());


        GitUserInfoResponse gitUserInfoResponse = new GitUserInfoResponse();
        gitUserInfoResponse.setGitUser(gitUserEntity);
        return gitUserInfoResponse;
    }

    public static GitUserEntity selectGit(Long workspaceId, String type, Boolean decrypt) {
        try {
            GitUserEntity gitUserEntity = workspaceGitMapper.selectByWorkspaceId(workspaceId, type);
            if (gitUserEntity == null) {
                return null;
            }
            // 密码 token 解密处理
            if (!StringUtils.isEmpty(gitUserEntity.getGitPassword())) {
                String encryptPassword = generateKeys(gitUserEntity.getGitPassword(), Cipher.DECRYPT_MODE);
                gitUserEntity.setGitPassword(encryptPassword);
            }
            if (!StringUtils.isEmpty(gitUserEntity.getGitToken())) {
                String encryptToken = generateKeys(gitUserEntity.getGitToken(), Cipher.DECRYPT_MODE);
                gitUserEntity.setGitToken(encryptToken);
            }
            return gitUserEntity;
        } catch (Exception e) {
            return null;
        }
    }

    private static String generateKeys(String password, int mode) throws DSSErrorException{
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
            throw new DSSErrorException(800001, "加密失败");
        }
    }

    public static GitConnectResponse gitTokenTest(GitConnectRequest connectTestRequest)throws DSSErrorException {
        // GitLab 令牌
        String token = connectTestRequest.getToken();
        // 期望匹配的用户名
        String expectedUsername = connectTestRequest.getUsername();
        GitUserEntity gitUserEntity = workspaceGitMapper.selectByUser(expectedUsername);
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
                    return new GitConnectResponse(true);
                }else {
                    LOGGER.info("Token is invalid or does not match the expected username.");
                    return new GitConnectResponse(false);
                }
            } else if (response.getStatusLine().getStatusCode() == 401){
                throw new DSSErrorException(800001, "请检查token是否正确");
            }
            return new GitConnectResponse(false);
        } catch (DSSErrorException e) {
            LOGGER.info("Error verifying token: " + e.getMessage());
            throw new DSSErrorException(800001, "请检查token是否正确");
        }catch (Exception e) {
            throw new DSSErrorException(800001, "校验token失败，请确认当前环境git是否可以正常访问" + e);
        }
    }

    public static GitUserInfoListResponse getGitUserByType(GitUserInfoByRequest infoByRequest) {
        List<GitUserEntity> gitUserEntities = workspaceGitMapper.selectGitUser(infoByRequest.getWorkspaceId(), infoByRequest.getType(), infoByRequest.getGitUserName());
        GitUserInfoListResponse gitUserInfoListResponse = new GitUserInfoListResponse();
        gitUserInfoListResponse.setGitUserEntities(gitUserEntities);
        return gitUserInfoListResponse;
    }
}
