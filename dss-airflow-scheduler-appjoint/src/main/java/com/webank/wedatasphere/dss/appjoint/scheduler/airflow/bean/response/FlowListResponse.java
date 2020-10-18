package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FlowListResponse {
    private Long totalElements;
    private Integer totalPages;
    private List<String> flows;
}
