package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Data
public class DSSWorkspaceUsersDepartmentVo implements Serializable {

    private static final long serialVersionUID=1L;
    private List<DepartmentUserTreeVo> editUsers;

    private List<DepartmentUserTreeVo> accessUsers;

    private List<DepartmentUserTreeVo> releaseUsers;


}
