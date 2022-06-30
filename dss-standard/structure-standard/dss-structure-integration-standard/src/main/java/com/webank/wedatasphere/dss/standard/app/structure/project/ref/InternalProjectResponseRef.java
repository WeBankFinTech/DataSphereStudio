package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-08
 * @since 0.5.0
 */
public class InternalProjectResponseRef extends InternalResponseRef implements ProjectResponseRef {

    private Long projectRefId;

    public InternalProjectResponseRef(String responseBody, int status,
                                  String errorMsg, Map<String, Object> responseMap,
                                  Long projectRefId) {
        super(responseBody, status, errorMsg, responseMap);
        this.projectRefId = projectRefId;
    }

    @Override
    public Long getRefProjectId() {
        return projectRefId;
    }
}
