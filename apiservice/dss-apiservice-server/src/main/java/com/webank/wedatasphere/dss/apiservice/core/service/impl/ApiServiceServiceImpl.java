/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.wedatasphere.dss.apiservice.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceConfigDao;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceParamDao;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceService;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceQuery;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ParamVo;
import com.webank.wedatasphere.linkis.bml.client.BmlClient;
import com.webank.wedatasphere.linkis.bml.client.BmlClientFactory;
import com.webank.wedatasphere.linkis.bml.protocol.BmlUpdateResponse;
import com.webank.wedatasphere.linkis.bml.protocol.BmlUploadResponse;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.common.io.FsPath;
import com.webank.wedatasphere.linkis.storage.script.ScriptFsWriter;
import com.webank.wedatasphere.linkis.storage.script.ScriptMetaData;
import com.webank.wedatasphere.linkis.storage.script.ScriptRecord;
import com.webank.wedatasphere.linkis.storage.script.Variable;
import com.webank.wedatasphere.linkis.storage.script.VariableParser;
import com.webank.wedatasphere.linkis.storage.script.writer.StorageScriptFsWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * service impl
 *
 * @author zhulixin
 */
@Service
public class ApiServiceServiceImpl implements ApiServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceServiceImpl.class);

    @Autowired
    private ApiServiceConfigDao apiServiceConfigDao;

    @Autowired
    private ApiServiceParamDao apiServiceParamDao;

    /**
     * Bml client
     */
    private BmlClient client;

    @PostConstruct
    public void buildClient() {
        LOG.info("build client start ======");
        client = BmlClientFactory.createBmlClient();
        LOG.info("build client end =======");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ApiServiceVo apiService) throws ErrorException {
        String user = apiService.getCreator();
        String resourceId = null;
        try {
            // check script path if already created
            String scriptPath = apiService.getScriptPath();
            ApiServiceVo vo = apiServiceConfigDao.queryByScriptPath(scriptPath);
            if (vo != null) {
                throw new RuntimeException("the script path: " + scriptPath + " is already created!");
            }
            // upload to bml
            Map<String, String> uploadResult = uploadBml(user, scriptPath,
                    apiService.getMetadata(), apiService.getContent());

            // insert dss_oneservice_config
            String version = uploadResult.get("version");
            resourceId = uploadResult.get("resourceId");
            apiService.setResourceId(resourceId);
            apiService.setVersion(version);
            apiServiceConfigDao.insert(apiService);

            // insert dss_oneservice_params
            List<ParamVo> params = apiService.getParams();
            if (params != null && !params.isEmpty()) {
                for (ParamVo param : params) {
                    param.setConfigId(apiService.getId());
                    param.setVersion(version);
                    apiServiceParamDao.insert(param);
                }
            }
        } catch (Exception e) {
            LOG.error("api service insert error", e);
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
    public void update(ApiServiceVo apiService) throws ErrorException {
        try {
            // update to bml
            Map<String, String> updateResult = updateBml(apiService.getModifier(), apiService.getResourceId(),
                    apiService.getScriptPath(), apiService.getMetadata(), apiService.getContent());

            String version = updateResult.get("version");
            apiService.setVersion(version);
            apiServiceConfigDao.update(apiService);

            // insert params
            List<ParamVo> params = apiService.getParams();
            if (params != null && !params.isEmpty()) {
                for (ParamVo param : params) {
                    param.setConfigId(apiService.getId());
                    param.setVersion(version);
                    apiServiceParamDao.insert(param);
                }
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
    public PageInfo<ApiServiceVo> query(ApiServiceQuery apiServiceQuery) {
        PageHelper.startPage(apiServiceQuery.getCurrentPage(), apiServiceQuery.getPageSize());
        List<ApiServiceVo> queryList = apiServiceConfigDao.query(apiServiceQuery);
        // query param
//        if (queryList != null && !queryList.isEmpty()) {
//            for (OneServiceVo oneServiceVo : queryList) {
//                oneServiceVo.setParams(oneServiceParamDao.query(oneServiceVo.getId()));
//            }
//        }
        PageInfo<ApiServiceVo> pageInfo = new PageInfo<>(queryList);
        return pageInfo;
    }

    @Override
    public ApiServiceVo queryByScriptPath(@Param("scriptPath") String scriptPath) {
        ApiServiceVo result = apiServiceConfigDao.queryByScriptPath(scriptPath);
        // query param
        if (result != null) {
            result.setParams(apiServiceParamDao.queryByConfigIdAndVersion(result.getId(), result.getVersion()));
        }
        return result;
    }

    @Override
    public Integer queryCountByPath(String scriptPath, String path) {
        return apiServiceConfigDao.queryCountByPath(scriptPath, path);
    }

    @Override
    public Integer queryCountByName(String name) {
        return apiServiceConfigDao.queryCountByName(name);
    }

    @Override
    public Boolean enableApi(Integer id) {
        Integer updateCount = apiServiceConfigDao.enableApi(id);
        return updateCount > 0;
    }

    @Override
    public Boolean disableApi(Integer id) {
        Integer updateCount = apiServiceConfigDao.disableApi(id);
        return updateCount > 0;
    }

    private Map<String, String> uploadBml(String userName, String scriptPath, Map<String, Object> metadata, String scriptContent) {
        try {
            ScriptFsWriter writer = StorageScriptFsWriter.getScriptFsWriter(new FsPath(scriptPath), Consts.UTF_8.toString(), null);
            Variable[] v = VariableParser.getVariables(metadata);
            List<Variable> variableList = Arrays.stream(v).filter(var -> !StringUtils.isEmpty(var.value())).collect(Collectors.toList());
            writer.addMetaData(new ScriptMetaData(variableList.toArray(new Variable[0])));
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
            List<Variable> variableList = Arrays.stream(v).filter(var -> !StringUtils.isEmpty(var.value())).collect(Collectors.toList());
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
}
