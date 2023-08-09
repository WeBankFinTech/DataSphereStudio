/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.webank.wedatasphere.dss.apiservice.core.service.impl;

import com.google.common.base.Splitter;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceToken;
import com.webank.wedatasphere.dss.apiservice.core.constant.ApiCommonConstant;
import com.webank.wedatasphere.dss.apiservice.core.constant.SQLMetadataInfoCheckStatus;
import com.webank.wedatasphere.dss.apiservice.core.constant.SaveTokenEnum;
import com.webank.wedatasphere.dss.apiservice.core.dao.*;
import com.webank.wedatasphere.dss.apiservice.core.util.UUIDGenerator;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceQueryException;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceTokenException;
import com.webank.wedatasphere.dss.apiservice.core.execute.LinkisJobSubmit;
import com.webank.wedatasphere.dss.apiservice.core.token.JwtManager;
import com.webank.wedatasphere.dss.apiservice.core.token.TokenAuth;
import com.webank.wedatasphere.dss.apiservice.core.vo.*;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiService;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.client.BmlClientFactory;
import org.apache.linkis.bml.protocol.BmlUpdateResponse;
import org.apache.linkis.bml.protocol.BmlUploadResponse;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.io.FsPath;
import org.apache.linkis.storage.script.ScriptFsWriter;
import org.apache.linkis.storage.script.ScriptMetaData;
import org.apache.linkis.storage.script.ScriptRecord;
import org.apache.linkis.storage.script.Variable;
import org.apache.linkis.storage.script.VariableParser;
import org.apache.linkis.storage.script.writer.StorageScriptFsWriter;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.ibatis.annotations.Param;
//import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceImpl.class);


    @Autowired
    private ApiServiceDao apiServiceDao;

    @Autowired
    private ApiServiceParamDao apiServiceParamDao;

    @Autowired
    private ApiServiceVersionDao apiServiceVersionDao;

    @Autowired
    private ApiServiceTokenManagerDao apiServiceTokenManagerDao;

    @Autowired
    TokenAuth tokenAuth;




    /**
     * Bml client
     */
    private BmlClient client;

    private UJESClient ujesClient;

    @PostConstruct
    public void buildClient() {
        LOG.info("build client start ======");
        client = BmlClientFactory.createBmlClient();
        ujesClient = LinkisJobSubmit.getClient();
        LOG.info("build client end =======");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ApiServiceVo apiService) throws Exception {
        String user = apiService.getCreator();
        String resourceId = null;
        try {
            // check script path if already created
            String scriptPath = apiService.getScriptPath();

            // upload to bml
            Map<String, String> uploadResult = uploadBml(user, scriptPath,
                    apiService.getMetadata(), apiService.getContent());

            // insert linkis_oneservice_config
            String version = uploadResult.get("version");
            resourceId = uploadResult.get("resourceId");
            apiServiceDao.insert(apiService);

            //insert into version
            ApiVersionVo apiVersionVo = new ApiVersionVo();
            apiVersionVo.setApiId(apiService.getId());
            apiVersionVo.setBmlResourceId(resourceId);
            apiVersionVo.setBmlVersion(version);
            apiVersionVo.setVersion(version);
            apiVersionVo.setCreator(user);
            apiVersionVo.setCreateTime(Calendar.getInstance().getTime());
            apiVersionVo.setSource(apiService.getScriptPath());
            //1为正常 0为禁用
            apiVersionVo.setStatus(1);

            String approvalNo = UUIDGenerator.genUUID();
            apiVersionVo.setAuthId(approvalNo);

            //顺序不能改变，版本信息依赖审批单信息
            //todo update by query
            apiVersionVo.setMetadataInfo("default");
            apiServiceVersionDao.insert(apiVersionVo);



            // insert linkis_oneservice_params
            List<ParamVo> params = apiService.getParams();
            if (params != null && !params.isEmpty()) {
                for (ParamVo param : params) {
                    param.setApiVersionId(apiVersionVo.getId());
                    apiServiceParamDao.insert(param);
                }
            }

            //insert a token record for self
            genTokenForPublisher(apiService,apiVersionVo.getId());
        } catch (Exception e) {
            LOG.error("one service insert error", e);
            if (StringUtils.isNotBlank(resourceId)) {
//                removeBml(user, resourceId);
            }
            if (e.getCause() instanceof ErrorException) {
                throw (ErrorException) e.getCause();
            }
            throw e;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiServiceVo saveByApp(ApiServiceVo apiService) throws Exception {
        String user = apiService.getCreator();
        String resourceId = null;
        try {
            // check script path if already created
            String scriptPath = apiService.getScriptPath();

            // upload to bml
            Map<String, String> uploadResult = uploadBml(user, scriptPath,
                    apiService.getMetadata(), apiService.getContent());

            // insert linkis_oneservice_config
            String version = uploadResult.get("version");
            resourceId = uploadResult.get("resourceId");
            apiServiceDao.insert(apiService);

            //insert into version
            ApiVersionVo apiVersionVo = new ApiVersionVo();
            apiVersionVo.setApiId(apiService.getId());
            apiVersionVo.setBmlResourceId(resourceId);
            apiVersionVo.setBmlVersion(version);
            apiVersionVo.setVersion(version);
            apiVersionVo.setCreator(user);
            apiVersionVo.setCreateTime(Calendar.getInstance().getTime());
            apiVersionVo.setSource(apiService.getScriptPath());
            //1为正常 0为禁用
            apiVersionVo.setStatus(1);


            //生成审批记录，必须使用发布用户执行sql
//            checkApprovalFromDM(user,apiService,apiVersionVo);
            //顺序不能改变，版本信息依赖审批单信息
            apiServiceVersionDao.insert(apiVersionVo);

//            addApprovalToDB(apiService,apiVersionVo.getId(),apiVersionVo.getAuthId());

            // insert linkis_oneservice_params
            List<ParamVo> params = apiService.getParams();
            if (params != null && !params.isEmpty()) {
                for (ParamVo param : params) {
                    param.setApiVersionId(apiVersionVo.getId());
                    apiServiceParamDao.insert(param);
                }
            }

            //insert a token record for self
            genTokenForPublisher(apiService,apiVersionVo.getId());
            return apiService;
        } catch (Exception e) {
            LOG.error("one service insert error", e);
            if (StringUtils.isNotBlank(resourceId)) {
//                removeBml(user, resourceId);
            }
            if (e.getCause() instanceof ErrorException) {
                throw (ErrorException) e.getCause();
            }
            throw e;
        }
    }





    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiServiceVo  update(ApiServiceVo apiService) throws Exception {
        try {
            if(null!=apiService.getTargetServiceId()) {
                ApiVersionVo maxTargetApiVersionVo = getMaxVersion(apiService.getTargetServiceId());
                if(!checkUserWorkspace(apiService.getModifier(),apiService.getWorkspaceId().intValue())){
                    throw new ApiServiceQueryException(800035,"Only can update the api service by owner workspace! ");
                }
                if(maxTargetApiVersionVo.getCreator().equals(apiService.getModifier())) {
                    Map<String, String> updateResult = updateBml(apiService.getModifier(), maxTargetApiVersionVo.getBmlResourceId(),
                            apiService.getScriptPath(), apiService.getMetadata(), apiService.getContent());
                    apiService.setCreator(maxTargetApiVersionVo.getCreator());
                    apiService.setId(maxTargetApiVersionVo.getApiId());
                    apiServiceDao.updateToTarget(apiService);
//                    Log.info("Update to other Api Service, ID: " + apiService.getTargetServiceId() + ",resourceId:" + maxTargetApiVersionVo.getBmlResourceId());


                    String version = updateResult.get("version");
                    String resourceId = updateResult.get("resourceId");


                    //update api version
                    //insert into version
                    ApiVersionVo apiServiceVersionVo = new ApiVersionVo();
                    apiServiceVersionVo.setApiId(apiService.getId());
                    apiServiceVersionVo.setBmlResourceId(resourceId);
                    apiServiceVersionVo.setBmlVersion(version);
                    apiServiceVersionVo.setVersion(version);
                    apiServiceVersionVo.setCreator(apiService.getModifier());
                    apiServiceVersionVo.setCreateTime(Calendar.getInstance().getTime());
                    apiServiceVersionVo.setSource(apiService.getScriptPath());
                    //0默认已禁用 1为正常
                    apiServiceVersionVo.setStatus(1);
                    String approvalNo = UUIDGenerator.genUUID();
                    apiServiceVersionVo.setAuthId(approvalNo);

                    //todo update by query
                    apiServiceVersionVo.setMetadataInfo("null");
                    //顺序不能改变，版本信息依赖审批单信息
                    apiServiceVersionDao.insert(apiServiceVersionVo);

                    //改变历史版本状态
                    apiServiceVersionDao.updateApiVersionStatusById(maxTargetApiVersionVo.getId(), 0);

                    //改变历史Token状态
                    apiServiceTokenManagerDao.disableTokenStatusByVersionId(maxTargetApiVersionVo.getId());
                    // insert params
                    List<ParamVo> params = apiService.getParams();
                    if (params != null && !params.isEmpty()) {
                        for (ParamVo param : params) {
                            param.setApiVersionId(apiServiceVersionVo.getId());
                            apiServiceParamDao.insert(param);
                        }
                    }

//                    addApprovalToDB(apiService, apiServiceVersionVo.getId(), apiServiceVersionVo.getAuthId());

                    //insert a token record for self
                    genTokenForPublisher(apiService, apiServiceVersionVo.getId());
                    return apiService;
                }else {
                    throw new ApiServiceQueryException(800036,"Only can update the api service by owner! ");
                }
            }else {
                throw new ApiServiceQueryException(800037,"Target service id  can not be  null for update");
            }

        } catch (Exception e) {
            LOG.error("api service update error", e);
            if (e.getCause() instanceof ErrorException) {
                throw (ErrorException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public List<ApiServiceVo> query(ApiServiceQuery apiServiceQuery) throws ApiServiceQueryException {
        //todo 查询需要优化，量小时不影响效率
        List<ApiServiceVo> queryList = apiServiceDao.query(apiServiceQuery);

        //add auth check
        List<TokenManagerVo> userTokenManagerVos = apiServiceTokenManagerDao.queryByApplyUser(apiServiceQuery.getUserName());

        List<ApiServiceVo> authQueryList = queryList.stream().filter(apiServiceVo -> {
                    TokenManagerVo findUserTokenManagerVo = userTokenManagerVos.stream().filter(userTokenManagerVo ->
                    userTokenManagerVo.getApiId().equals(apiServiceVo.getId())
                            && userTokenManagerVo.getUser().equals(apiServiceQuery.getUserName())
                    ).findAny().orElse(null);
                    if (null != findUserTokenManagerVo) {
                        //检查token状态为有效的
                        if(findUserTokenManagerVo.getStatus().equals(1) || findUserTokenManagerVo.getPublisher().equals(apiServiceVo.getCreator())) {
                            return true;
                        }else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());

        // query param
        if (authQueryList != null && !authQueryList.isEmpty()) {
            for (ApiServiceVo apiServiceVo : authQueryList) {
                ApiVersionVo maxApiVersionVo = getMaxVersion(apiServiceVo.getId());
                if(null == maxApiVersionVo){
                    throw new ApiServiceQueryException(800032,"数据服务API版本记录为空"+apiServiceVo.getName());
                }
                apiServiceVo.setParams(apiServiceParamDao.queryByVersionId(maxApiVersionVo.getId()));
                apiServiceVo.setLatestVersionId(maxApiVersionVo.getId());
            }
        }

        return authQueryList;
    }

    @Override
    public List<ApiServiceVo> queryByWorkspaceId(Integer workspaceId, String userName){
        List<ApiServiceVo> result = apiServiceDao.queryByWorkspaceId(workspaceId,userName);
        return result;
    }


    @Override
    public ApiServiceVo queryById(Long id,String userName) {
        ApiServiceVo apiServiceVo = apiServiceDao.queryById(id);
        ApiVersionVo maxApiVersionVo =  getMaxVersion(apiServiceVo.getId());
        List<TokenManagerVo> userTokenManagerVos = apiServiceTokenManagerDao.queryByApplyUserAndVersionId(userName,maxApiVersionVo.getId());
        if(userTokenManagerVos.size()>0) {
            // query param
            if (apiServiceVo != null) {
                apiServiceVo.setParams(apiServiceParamDao.queryByVersionId(maxApiVersionVo.getId()));
                apiServiceVo.setUserToken(userTokenManagerVos.get(0).getToken());
                apiServiceVo.setLatestVersionId(maxApiVersionVo.getId());
            }
        }else {
            return null;
        }
        return apiServiceVo;
    }


    @Override
    public List <String> queryAllTags(String userName,Integer workspaceId) {
        //todo 会有历史版本的tag
        List <String> tags = apiServiceDao.queryAllTags(userName,workspaceId);
        List<String> tagList=
                tags.stream().filter(tag->!StringUtils.isEmpty(tag)).map(tag -> Splitter.on(",").splitToList(tag)).flatMap(List::stream).distinct()
                        .collect(Collectors.toList());
        return tagList;
    }


    @Override
    public ApiServiceVo queryByScriptPath(@Param("scriptPath") String scriptPath) {
        List<ApiServiceVo> apiServiceList = apiServiceDao.queryByScriptPath(scriptPath);
        ApiServiceVo latestApiService = apiServiceList.stream().max(Comparator.comparing(ApiServiceVo::getModifyTime)).orElse(null);
        if(null == latestApiService){
            return null;
        }
        List<ApiVersionVo> apiVersionVos =apiServiceVersionDao.queryApiVersionByApiServiceId(latestApiService.getId());
        ApiVersionVo maxVersion =apiVersionVos.stream().max(Comparator.comparing(ApiVersionVo::getVersion)).orElse(null);
        // query param
        if (latestApiService != null && null != maxVersion ) {
            latestApiService.setParams(apiServiceParamDao.queryByVersionId(maxVersion.getId()));
            latestApiService.setLatestVersionId(maxVersion.getId());
        }
        return latestApiService;
    }






    @Override
    public Integer queryCountByPath(String scriptPath, String path) {
        return apiServiceDao.queryCountByPath(scriptPath, path);
    }

    @Override
    public Integer queryCountByName(String name) {
        return apiServiceDao.queryCountByName(name);
    }

    @Override
    public Boolean enableApi(String userName,ApiServiceVo apiServiceVo) {

        if(!checkUserWorkspace(userName,apiServiceVo.getWorkspaceId().intValue())){
            LOG.error("api service check workspace error");
            return false;
        }
        if(apiServiceVo.getCreator().equals(userName)) {
            long id=apiServiceVo.getId();
            Integer updateCount = apiServiceDao.enableApi(id);
            List<ApiVersionVo> targetApiVersionList = apiServiceVersionDao.queryApiVersionByApiServiceId(id);
            ApiVersionVo maxTargetApiVersionVo = targetApiVersionList.stream().max(Comparator.comparing(ApiVersionVo::getVersion)).orElse(null);

            apiServiceTokenManagerDao.enableTokenStatusByVersionId(maxTargetApiVersionVo.getId());
            apiServiceVersionDao.updateApiVersionStatusById(maxTargetApiVersionVo.getId(), 1);
            return updateCount > 0;
        }else {
            return false;
        }
    }

    @Override
    public Boolean disableApi(String userName,ApiServiceVo apiServiceVo) {
        if(!checkUserWorkspace(userName,apiServiceVo.getWorkspaceId().intValue())){
            LOG.error("api service check workspace error");
            return false;
        }
        if(apiServiceVo.getCreator().equals(userName)) {
            Long id=apiServiceVo.getId();
            Integer updateCount = apiServiceDao.disableApi(id);
            apiServiceTokenManagerDao.disableTokenStatusByApiId(id);
            apiServiceVersionDao.updateAllApiVersionStatusByApiServiceId(id, 0);
            return updateCount > 0;
        }else {
            return false;
        }
    }


    @Override
    public Boolean deleteApi(String userName,ApiServiceVo apiServiceVo) {
        if(!checkUserWorkspace(userName,apiServiceVo.getWorkspaceId().intValue())){
            LOG.error("api service check workspace error");
            return false;
        }
        if(apiServiceVo.getCreator().equals(userName)) {
            Long id = apiServiceVo.getId();
            Integer updateCount = apiServiceDao.deleteApi(id);
            apiServiceTokenManagerDao.disableTokenStatusByApiId(id);
            apiServiceVersionDao.updateAllApiVersionStatusByApiServiceId(id, 0);
            return updateCount > 0;
        }else {
            return false;
        }
    }

    @Override
    public Boolean updateComment(String comment, String userName,ApiServiceVo apiServiceVo) {
        if(!checkUserWorkspace(userName,apiServiceVo.getWorkspaceId().intValue())){
            LOG.error("api service check workspace error");
            return false;
        }
        if(apiServiceVo.getCreator().equals(userName)) {
            Integer updateCount = apiServiceDao.updateApiServiceComment(apiServiceVo.getId(),comment);
            return updateCount > 0;
        }else {
            return false;
        }
    }

    private Map<String, String> uploadBml(String userName, String scriptPath, Map<String, Object> metadata, String scriptContent) {
        try {
            ScriptFsWriter writer = StorageScriptFsWriter.getScriptFsWriter(new FsPath(scriptPath), Consts.UTF_8.toString(), null);
            List<Variable> variableList=null;
           if(metadata.entrySet().size() >0) {
               Variable[] v = VariableParser.getVariables(metadata);
               variableList = Arrays.stream(v).filter(var -> !StringUtils.isEmpty(var.getValue())).collect(Collectors.toList());

           }
           if(variableList!=null) {
               writer.addMetaData(new ScriptMetaData(variableList.toArray(new Variable[0])));
           }else {
               writer.addMetaData(null);
           }
            writer.addRecord(new ScriptRecord(scriptContent));
            InputStream inputStream = writer.getInputStream();
            //  新增文件
            BmlUploadResponse resource = client.uploadResource(userName, scriptPath, inputStream);
            if (!resource.isSuccess()) {
                throw new IOException("upload bml error");
            }
            Map<String, String> result = new HashMap<>();
            result.put("resourceId", resource.resourceId());
            result.put("version", resource.version());
            return result;
        } catch (IOException e) {
            LOG.error("upload bml error", e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> updateBml(String userName, String resourceId, String scriptPath, Map<String, Object> metadata, String scriptContent) {
        try {
            ScriptFsWriter writer = StorageScriptFsWriter.getScriptFsWriter(new FsPath(scriptPath), Consts.UTF_8.toString(), null);
            Variable[] v = VariableParser.getVariables(metadata);
            List<Variable> variableList = Arrays.stream(v).filter(var -> !StringUtils.isEmpty(var.getValue())).collect(Collectors.toList());
            writer.addMetaData(new ScriptMetaData(variableList.toArray(new Variable[0])));
            writer.addRecord(new ScriptRecord(scriptContent));
            InputStream inputStream = writer.getInputStream();

            //  更新文件
            BmlUpdateResponse resource = client.updateResource(userName, resourceId, "", inputStream);
            if (!resource.isSuccess()) {
                throw new IOException("update bml error");
            }
            Map<String, String> result = new HashMap<>();
            result.put("resourceId", resource.resourceId());
            result.put("version", resource.version());
            return result;
        } catch (IOException e) {
            LOG.error("update bml error", e);
            throw new RuntimeException(e);
        }
    }

    private void removeBml(String userName, String resourceId) {
        try {
            LOG.info("delete bml resource: userName: " + userName + ", resourceId: " + resourceId);
            client.deleteResource(userName, resourceId);
        } catch (Exception e) {
            LOG.error("remove bml error", e);
        }
    }


    public SQLMetadataInfoCheckStatus sendApprovalToDM(String user, ApiServiceVo apiService, ApiVersionVo apiVersionVo) throws Exception {

        String approvalNo = UUIDGenerator.genUUID();
        apiVersionVo.setAuthId(approvalNo);
        return SQLMetadataInfoCheckStatus.LEGAL;
    }




    public void  genTokenForPublisher(ApiServiceVo apiService,Long apiVersionId) throws ApiServiceTokenException {

        List<TokenManagerVo>   tokenManagerVoList = genTokenForAccessApi(apiService,apiVersionId);
        SaveTokenEnum res = tokenAuth.saveTokensToDb(tokenManagerVoList, ApiCommonConstant.DEFAULT_APPROVAL_NO);
    }

    /**
     * 为申请用户和自己生成访问token
     * @param apiService
     * @param apiVersionId
     * @return
     */
    private List<TokenManagerVo> genTokenForAccessApi(ApiServiceVo apiService,Long apiVersionId){
        List<TokenManagerVo> tokenManagerVoList = new ArrayList<>();
        List<String> userNamesTmp = Arrays.asList(apiService.getApprovalVo().getApplyUser().split(","));
        List<String> userNames = new ArrayList<>(userNamesTmp);
        userNames.add(apiService.getCreator());
        userNames.stream().forEach(userName ->{
            TokenManagerVo tokenManagerVo = new TokenManagerVo();
            tokenManagerVo.setApiId(apiService.getId());
            tokenManagerVo.setApiVersionId(apiVersionId);
            tokenManagerVo.setApplyTime(Calendar.getInstance().getTime());
            tokenManagerVo.setDuration(365L);
            tokenManagerVo.setReason("create api service");
            tokenManagerVo.setStatus(1);
            tokenManagerVo.setIpWhitelist("");
            tokenManagerVo.setCaller("scriptis");
            tokenManagerVo.setUser(userName);
            tokenManagerVo.setPublisher(apiService.getCreator());

            ApiServiceToken apiServiceToken = new ApiServiceToken();
            apiServiceToken.setApplyUser(userName);
            apiServiceToken.setPublisher(apiService.getCreator());
            apiServiceToken.setApplyTime(tokenManagerVo.getApplyTime());
            apiServiceToken.setApiServiceId(tokenManagerVo.getApiId());

            tokenManagerVo.setToken(JwtManager.createToken(userName,apiServiceToken,tokenManagerVo.getDuration()));

            tokenManagerVo.setApplySource(ApiCommonConstant.DEFAULT_APPROVAL_NO);
            tokenManagerVoList.add(tokenManagerVo);
        });

        return tokenManagerVoList;
    }


    @Override
    public ApiVersionVo  getMaxVersion(long serviceId){
        List<ApiVersionVo> apiVersionVoList = apiServiceVersionDao.queryApiVersionByApiServiceId(serviceId);
        ApiVersionVo maxApiVersionVo = apiVersionVoList.stream().max(Comparator.comparing(ApiVersionVo::getVersion)).orElse(null);
        return maxApiVersionVo;
    }

    @Override
    public boolean checkUserWorkspace(String userName,Integer workspaceId){
        //todo cache user workspaceIds
        return true;
//        String workspaceIds = ExecuteCodeHelper.getUserWorkspaceIds(userName,ujesClient);
//        if(Arrays.stream(workspaceIds.split(",")).map(Integer::valueOf).collect(Collectors.toList()).contains(workspaceId)){
//            return true;
//        }else {
//            return false;
//        }

    }

}
