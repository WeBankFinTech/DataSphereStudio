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

package com.webank.wedatasphere.dss.framework.workspace.service;


import com.webank.wedatasphere.dss.framework.workspace.bean.DSSDictionary;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSDictionaryRequestVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;

import java.util.List;
import java.util.Map;


public interface DSSDictionaryService {

    public List<DSSDictionary> getListByParam(DSSDictionaryRequestVO dictionaryRequestVO) ;

    /**
     * 获取编排模式
     * @param dictionaryRequestVO
     * @return
     */
    public Map<String,Object> getDicSecondList(DSSDictionaryRequestVO dictionaryRequestVO) ;

    /**
     * 获取空间默认部门
     * @return
     */
    public List<DepartmentVO> getDefaultDepartmentVOList();

}
