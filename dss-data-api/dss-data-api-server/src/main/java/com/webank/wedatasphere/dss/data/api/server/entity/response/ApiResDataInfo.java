package com.webank.wedatasphere.dss.data.api.server.entity.response;

import java.util.HashMap;
import java.util.List;


import lombok.Data;

@Data
public class ApiResDataInfo {
    private List<HashMap<String, Object>> data;
    private Integer total;
    private Integer pageSize;
    private Integer currentPageNum;
    private Integer totalPage;
}
