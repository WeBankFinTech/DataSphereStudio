package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.webank.wedatasphere.dss.standard.app.development.ref.DSSCommonResponseRef;

import java.util.Map;

public class VisualisCopyResponseRef extends DSSCommonResponseRef {


    private Map<String, Object> newJobContent;

    public VisualisCopyResponseRef(Map<String, Object> jobContent, String responseBody) throws Exception {
        super(responseBody);
        this.newJobContent = jobContent;
    }

    @Override
    public Map<String, Object> toMap() {
        return newJobContent;
    }

}
