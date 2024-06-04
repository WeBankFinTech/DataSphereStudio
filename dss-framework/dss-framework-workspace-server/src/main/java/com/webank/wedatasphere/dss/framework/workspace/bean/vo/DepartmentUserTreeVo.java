package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DepartmentUserTreeVo implements Serializable {
    private static final long serialVersionUID=1L;
    private Integer id;
    private String name;
    private String type;
    private List<DepartmentUserTreeVo> child;

    public DepartmentUserTreeVo(Integer id, String name, String type, List<DepartmentUserTreeVo> child){
        this.id = id;
        this.name = name;
        this.type = type;
        this.child = child;
    }
}
