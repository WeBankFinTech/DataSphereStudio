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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSDictionary;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSDictionaryRequestVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSDictionaryMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSDictionaryService;
import com.webank.wedatasphere.dss.framework.workspace.util.DSSDictionaryConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * created by v_wbzwchen on 2020-12-15
 * Description:
 */
@Service
public class DSSDictionaryServiceImpl implements DSSDictionaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSDictionaryServiceImpl.class);

    @Autowired
    private DSSDictionaryMapper dssDictionaryMapper;

    @Override
    public List<DSSDictionary> getListByParam(DSSDictionaryRequestVO dictionaryRequestVO) {
        //查询所在工作空间以及默认空间的开发流程或编码模式配置
        QueryWrapper<DSSDictionary> dictionaryQueryWrapper = new QueryWrapper<>();
        dictionaryQueryWrapper.in("workspace_id", Arrays.asList(dictionaryRequestVO.getWorkspaceId(),0));
        if(StringUtils.isNotBlank(dictionaryRequestVO.getDicKey())){
            dictionaryQueryWrapper.eq("dic_key",dictionaryRequestVO.getDicKey());
        }
        if(StringUtils.isNotBlank(dictionaryRequestVO.getParentKey())){
            dictionaryQueryWrapper.eq("parent_key",dictionaryRequestVO.getParentKey());
        }
        dictionaryQueryWrapper.orderByAsc("order_num");
        List<DSSDictionary> dictionaries = dssDictionaryMapper.selectList(dictionaryQueryWrapper);
        return dictionaries;
    }

    @Override
    public Map<String,Object> getDicSecondList(DSSDictionaryRequestVO dictionaryRequestVO) {
        //一级字典
        List<DSSDictionary> dictionaries = getListByParam(dictionaryRequestVO);

        //二级字典
        List<String> keyList = dictionaries.stream().map(DSSDictionary::getDicKey).collect(Collectors.toList());
        QueryWrapper<DSSDictionary> dictionaryQueryWrapper = new QueryWrapper<>();
        dictionaryQueryWrapper.in("workspace_id", Arrays.asList(dictionaryRequestVO.getWorkspaceId(),0));
        dictionaryQueryWrapper.in("parent_key",keyList);
        dictionaryQueryWrapper.orderByAsc("order_num");
        List<DSSDictionary> dictionarieSelectList = dssDictionaryMapper.selectList(dictionaryQueryWrapper);

        //封装返回
        Map<String,List<DSSDictionary>> mapList = new HashMap<>();
        for(DSSDictionary dssDictionary : dictionarieSelectList){
            String parentKey = dssDictionary.getParentKey();
            if(mapList.get(parentKey)==null){
                mapList.put(parentKey,new ArrayList<>());
            }
            mapList.get(parentKey).add(dssDictionary);
        }
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("list",dictionaries);
        retMap.put("mapList",mapList);
        return retMap;
    }


    /**
     * 获取空间默认部门
     * @return
     */
    @Override
    public List<DepartmentVO> getDefaultDepartmentVOList(){
        QueryWrapper<DSSDictionary> dictionaryQueryWrapper = new QueryWrapper<>();
        dictionaryQueryWrapper.eq("workspace_id", 0);
        dictionaryQueryWrapper.eq("dic_key", DSSDictionaryConstant.W_WORKSPACE_DEPARTMENT);
        List<DSSDictionary> dictionarieList = dssDictionaryMapper.selectList(dictionaryQueryWrapper);
        List<DepartmentVO> retList = new ArrayList<>();
        if(CollectionUtils.isEmpty(dictionarieList)){
            return retList;
        }
        String dicValue = dictionarieList.get(0).getDicValue();
        if(StringUtils.isBlank(dicValue)){
            return retList;
        }
        String[] tempStrArr = dicValue.split(";");
        DepartmentVO departmentVO = null;
        for(int i=0;i<tempStrArr.length;i++){
            departmentVO = new DepartmentVO();
            String[] tempArr = tempStrArr[i].split("-");
            departmentVO.setId(Integer.valueOf(tempArr[0]));
            departmentVO.setName(tempArr[1]);
            departmentVO.setFrontName(tempArr[1]);
            retList.add(departmentVO);
        }
        return retList;
    }
}
