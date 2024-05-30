package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitConnectRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoByRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserUpdateRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitConnectResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoListResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserUpdateResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoResponse;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.git.dao.DSSWorkspaceGitMapper;
import com.webank.wedatasphere.dss.git.service.DSSWorkspaceGitService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.List;

@Service
public class DSSWorkspaceGitServiceImpl implements DSSWorkspaceGitService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DSSWorkspaceGitMapper workspaceGitMapper;


    @Override
    public GitUserUpdateResponse associateGit(GitUserUpdateRequest gitUserCreateRequest) throws DSSErrorException {
        if (gitUserCreateRequest == null) {
            throw new DSSErrorException(010101, "gitUserCreateRequest is null");
        }
        GitUserEntity gitUser = gitUserCreateRequest.getGitUser();
        String userName = gitUserCreateRequest.getUsername();


        // 不存在则更新，存在则新增
        GitUserEntity oldGitUserDo = selectGit(gitUser.getWorkspaceId(), gitUser.getType(), true);
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
        if (oldGitUserDo == null) {
            // 工作空间--git用户 为一一对应关系
            if (gitUserEntity != null) {
                throw new DSSErrorException(010101, "该用户已配置为" + gitUserEntity.getWorkspaceId() + "工作空间的读写或只读用户，请更换用户");
            }
            gitUser.setCreateBy(userName);
            gitUser.setGitUrl(UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue()));
            workspaceGitMapper.insert(gitUser);
        }else {
            if (GitConstant.GIT_ACCESS_WRITE_TYPE.equals(gitUser.getType()) && !oldGitUserDo.getGitUser().equals(gitUser.getGitUser())) {
                throw new DSSErrorException(800001, "用户名不得修改");
            }
            if (gitUserEntity != null && (!gitUserEntity.getWorkspaceId().equals(gitUser.getWorkspaceId()) || !gitUserEntity.getType().equals(gitUser.getType()))) {
                throw new DSSErrorException(010101, "该用户已配置为" + gitUserEntity.getWorkspaceId() + "工作空间的读写或只读用户，请更换用户");
            }
            workspaceGitMapper.update(gitUser);
        }

        return new GitUserUpdateResponse();
    }


    @Override
    public GitUserInfoResponse selectGitUserInfo(GitUserInfoRequest gitUserInfoRequest) throws DSSErrorException {
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

    @Override
    public GitUserEntity selectGit(Long workspaceId, String type, Boolean decrypt) {
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

    private String generateKeys(String password, int mode) throws DSSErrorException{
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

    @Override
    public GitConnectResponse gitTokenTest(GitConnectRequest connectTestRequest)throws DSSErrorException {
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
            JSONObject userData = new JSONObject(jsonResponse);

            String actualUsername = userData.getString("username");

            if (response.getStatusLine().getStatusCode() == 200 && actualUsername.equals(expectedUsername)) {
                logger.info("Token is valid and matches the username: " + actualUsername);
                return new GitConnectResponse(true);
            } else {
                logger.info("Token is invalid or does not match the expected username.");
                return new GitConnectResponse(false);
            }
        } catch (Exception e) {
            logger.info("Error verifying token: " + e.getMessage());
            throw new DSSErrorException(800001, "verifying token failed, the reason is:" + e);
        }
    }

    @Override
    public GitUserInfoListResponse getGitUserByType(GitUserInfoByRequest infoByRequest) {
        List<GitUserEntity> gitUserEntities = workspaceGitMapper.selectGitUser(infoByRequest.getWorkspaceId(), infoByRequest.getType(), infoByRequest.getGitUserName());
        GitUserInfoListResponse gitUserInfoListResponse = new GitUserInfoListResponse();
        gitUserInfoListResponse.setGitUserEntities(gitUserEntities);
        return gitUserInfoListResponse;
    }

}
