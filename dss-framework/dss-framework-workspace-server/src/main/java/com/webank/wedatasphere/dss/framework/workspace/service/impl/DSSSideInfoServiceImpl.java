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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSDictionary;
import com.webank.wedatasphere.dss.framework.workspace.bean.Sidebar;
import com.webank.wedatasphere.dss.framework.workspace.bean.SidebarContent;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.SidebarContentVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.SidebarVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.SidebarContentMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.SidebarMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSSideInfoService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DSSSideInfoServiceImpl implements DSSSideInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSSideInfoServiceImpl.class);

    @Autowired
    private SidebarMapper sidebarMapper;
    @Autowired
    private SidebarContentMapper sidebarContentMapper;
    @Autowired
    private DSSWorkspaceService workspaceService;

    @Override
    public List<SidebarVO> getSidebarVOList(String username, Long workspaceId,boolean isEnglish) {
        //查询所在工作空间以及默认空间的侧边栏
        List<SidebarVO> retList = new ArrayList<>();
        QueryWrapper<Sidebar> sidebarQueryWrapper = new QueryWrapper<>();
        sidebarQueryWrapper.in("workspace_id", Arrays.asList(workspaceId,0));
        sidebarQueryWrapper.orderByAsc("order_num");
        List<Sidebar> sidebarList = sidebarMapper.selectList(sidebarQueryWrapper);
        if(CollectionUtils.isEmpty(sidebarList)){
            return retList;
        }
        //查询所在工作空间以及默认空间的侧边栏对应的侧边栏-内容
        List<Integer> sideBarIds = sidebarList.stream().map(Sidebar::getId).collect(Collectors.toList());
        QueryWrapper<SidebarContent> sidebarQueryContentWrapper = new QueryWrapper<>();
        sidebarQueryContentWrapper.in("workspace_id", Arrays.asList(workspaceId.intValue(),0));
        sidebarQueryContentWrapper.in("sidebar_id", sideBarIds);
        sidebarQueryContentWrapper.orderByAsc("order_num");
        List<SidebarContent> sidebarContentList = sidebarContentMapper.selectList(sidebarQueryContentWrapper);

        //封装返回数据
        return getSidebarVOS(username, workspaceId,retList, sidebarList, sidebarContentList,isEnglish);
    }

    //封装返回数据
    private List<SidebarVO> getSidebarVOS(String username, Long workspaceId,List<SidebarVO> retList, List<Sidebar> sidebarList, List<SidebarContent> sidebarContentList,boolean isEnglish) {
        Map<Integer,List<SidebarContent>> contentMap = new HashMap<>();
        for(SidebarContent sidebarContent : sidebarContentList){
            Integer sidebarId = sidebarContent.getSidebarId();
            if(contentMap.get(sidebarId)==null){
                contentMap.put(sidebarId,new ArrayList<>());
            }
            contentMap.get(sidebarId).add(sidebarContent);
        }
        for (Sidebar  sidebar : sidebarList){
            SidebarVO sidebarVO = new SidebarVO();
            BeanUtils.copyProperties(sidebar,sidebarVO);
            international(isEnglish,sidebar, sidebarVO);
            List<SidebarContentVO> sidebarContentVOList = new ArrayList<>();
            List<SidebarContent> sidebarContents = contentMap.get(sidebar.getId());
            if(!CollectionUtils.isEmpty(sidebarContents)){
                for (SidebarContent  sidebarContent : sidebarContents){
                    if(DSSWorkspaceConstant.WORKSPACE_MANAGEMENT_NAME.equals(sidebarContent.getTitle())){
                        if(!workspaceService.isAdminUser(workspaceId,username)){
                            continue;
                        }
                    }
                    SidebarContentVO sidebarContentVO = new SidebarContentVO();
                    BeanUtils.copyProperties(sidebarContent,sidebarContentVO);
                    international(isEnglish,sidebarContent,sidebarContentVO);
                    sidebarContentVOList.add(sidebarContentVO);
                }
            }
            sidebarVO.setContents(sidebarContentVOList);
            retList.add(sidebarVO);
        }
        return retList;
    }

    //国际化
    public void international(boolean isEnglish,Sidebar sidebar, SidebarVO sidebarVO){
        if(sidebar==null||!isEnglish){
            return;
        }
        if(StringUtils.isNotBlank(sidebar.getNameEn())){
            sidebarVO.setName(sidebar.getNameEn());
        }
        if(StringUtils.isNotBlank(sidebar.getTitleEn())){
            sidebarVO.setTitle(sidebar.getTitleEn());
        }
    }

    //国际化
    public void international(boolean isEnglish,SidebarContent sidebarContent, SidebarContentVO sidebarContentVO){
        if(sidebarContent==null||!isEnglish){
            return;
        }
        if(StringUtils.isNotBlank(sidebarContent.getNameEn())){
            sidebarContentVO.setName(sidebarContent.getNameEn());
        }
        if(StringUtils.isNotBlank(sidebarContent.getTitleEn())){
            sidebarContentVO.setTitle(sidebarContent.getTitleEn());
        }
    }
}
