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

package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSDictionary;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSDictionaryRequestVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSDictionaryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
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
    @RequestMapping(path ="getDicList", method = RequestMethod.POST)
    public Message getDevFlowList(HttpServletRequest request, @RequestBody DSSDictionaryRequestVO dictionaryRequestVO){
        LOGGER.info("begin to getDicList for dickey:{}, parantKey:{} ", dictionaryRequestVO.getDicKey(), dictionaryRequestVO.getParentKey());
        try{
            if(dictionaryRequestVO.getWorkspaceId()==null){
                return Message.error("workspaceId(空间id)不能为空");
            }
            if(StringUtils.isEmpty(dictionaryRequestVO.getParentKey())&&StringUtils.isEmpty(dictionaryRequestVO.getDicKey())){
                return Message.error("获取的parentKey和dicKey不能同时为空");
            }
            List<DSSDictionary> dictionaries = dictionaryService.getListByParam(dictionaryRequestVO);
            boolean isEnglish = "en".equals(request.getHeader("Content-language"));
            if(CollectionUtils.isNotEmpty(dictionaries) && isEnglish){
                dictionaries.stream().forEach(e->international(e));
            }
            return Message.ok("获取数据字典成功").data("list", dictionaries);
        }catch(Exception e){
            LOGGER.error("Fail to get getDevFlowListError for user {} in workspace {}",dictionaryRequestVO.getWorkspaceId(), e);
            return Message.error("获取数据字典失败:"+e.getMessage());
        }
    }

    @RequestMapping(path ="getDicSecondList", method = RequestMethod.POST)
    public Message getDicSecondList(HttpServletRequest request, @RequestBody DSSDictionaryRequestVO dictionaryRequestVO){
        LOGGER.info("begin to getDicSecondList for dickey:{}, parantKey:{} ", dictionaryRequestVO.getDicKey(), dictionaryRequestVO.getParentKey());
        try{
            if(dictionaryRequestVO.getWorkspaceId()==null){
                return Message.error("workspaceId(空间id)不能为空");
            }
            if(StringUtils.isEmpty(dictionaryRequestVO.getParentKey())&&StringUtils.isEmpty(dictionaryRequestVO.getDicKey())){
                return Message.error("获取的parentKey和dicKey不能同时为空");
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
            return Message.ok("获取数据字典成功").data("list", map);
        }catch(Exception e){
            LOGGER.error("Fail to get getDicSecondListError for user {} in workspace {}",dictionaryRequestVO.getWorkspaceId(), e);
            return Message.ok("获取数据字典失败:"+e.getMessage());
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