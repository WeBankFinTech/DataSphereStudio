package com.webank.wedatasphere.dss.data.api.server.entity.response;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
@Data
public class ApiExecuteInfo {
    private String log;
    private List<HashMap<String,Object>> resList;
    private Integer totalPage;
}
