/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSDictionary;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSDictionaryRequestVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSDictionaryService;
import com.webank.wedatasphere.dss.framework.workspace.util.RestfulUtils;
import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/10/26
 * Description:侧边栏的内容展示
 */
@Component
@Path("/dss/framework/workspace/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSDictionaryRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSDictionaryRestful.class);

    @Autowired
    private DSSDictionaryService dictionaryService;

    /**
     * 数据字典 - 根据key获取
     * @param request
     * @param dictionaryRequestVO
     * @return
     */
    @POST
    @Path("getDicList")
    public Response getDevFlowList(@Context HttpServletRequest request, @RequestBody DSSDictionaryRequestVO dictionaryRequestVO){
        try{
            if(dictionaryRequestVO.getWorkspaceId()==null){
                return Message.messageToResponse(Message.error("workspaceId(空间id)不能为空"));
            }
            if(StringUtils.isEmpty(dictionaryRequestVO.getParentKey())&&StringUtils.isEmpty(dictionaryRequestVO.getDicKey())){
                return Message.messageToResponse(Message.error("获取的parentKey和dicKey不能同时为空"));
            }
            List<DSSDictionary> dictionaries = dictionaryService.getListByParam(dictionaryRequestVO);
            boolean isEnglish = "en".equals(request.getHeader("Content-language"));
            if(CollectionUtils.isNotEmpty(dictionaries) && isEnglish){
                dictionaries.stream().forEach(e->international(e));
            }
            return RestfulUtils.dealOk("获取数据字典成功", new Pair<>("list", dictionaries));
        }catch(Exception e){
            LOGGER.error("Fail to get getDevFlowListError for user {} in workspace {}",dictionaryRequestVO.getWorkspaceId(), e);
            return RestfulUtils.dealError("获取数据字典失败:"+e.getMessage());
        }
    }

    @POST
    @Path("getDicSecondList")
    public Response getDicSecondList(@Context HttpServletRequest request, @RequestBody DSSDictionaryRequestVO dictionaryRequestVO){
        try{
            if(dictionaryRequestVO.getWorkspaceId()==null){
                return Message.messageToResponse(Message.error("workspaceId(空间id)不能为空"));
            }
            if(StringUtils.isEmpty(dictionaryRequestVO.getParentKey())&&StringUtils.isEmpty(dictionaryRequestVO.getDicKey())){
                return Message.messageToResponse(Message.error("获取的parentKey和dicKey不能同时为空"));
            }
            Map<String,Object> map = dictionaryService.getDicSecondList(dictionaryRequestVO);
            boolean isEnglish = "en".equals(request.getHeader("Content-language"));
            if(map!=null && map.size() > 0 && isEnglish){
                List<DSSDictionary> dictionaries = (List<DSSDictionary>) map.get("list");
                if(CollectionUtils.isNotEmpty(dictionaries)){
                    dictionaries.stream().forEach(e->international(e));
                }
                Map<String,List<DSSDictionary>>  mapList = ( Map<String,List<DSSDictionary>>) map.get("mapList");
                if(mapList!=null && mapList.size() > 0){
                    for(String key : mapList.keySet()){
                        mapList.get(key).stream().forEach(e->international(e));
                    }
                }
            }
            return RestfulUtils.dealOk("获取数据字典成功", new Pair<>("list", map));
        }catch(Exception e){
            LOGGER.error("Fail to get getDicSecondListError for user {} in workspace {}",dictionaryRequestVO.getWorkspaceId(), e);
            return RestfulUtils.dealError("获取数据字典失败:"+e.getMessage());
        }
    }


    //国际化，由于前端只是使用dicName，所以在英文的时候直接将dicNameEn赋值给dicName即可
    public void international(DSSDictionary dssDictionary){
        if(dssDictionary==null){
            return;
        }
        if(StringUtils.isNotBlank(dssDictionary.getDicNameEn())){
            dssDictionary.setDicName(dssDictionary.getDicNameEn());
            dssDictionary.setDicNameEn(null);
        }
        if(StringUtils.isNotBlank(dssDictionary.getDicValueEn())){
            dssDictionary.setDicValue(dssDictionary.getDicValueEn());
            dssDictionary.setDicValueEn(null);
        }
        if(StringUtils.isNotBlank(dssDictionary.getTitleEn())){
            dssDictionary.setTitle(dssDictionary.getTitleEn());
            dssDictionary.setTitleEn(null);
        }
    }
}