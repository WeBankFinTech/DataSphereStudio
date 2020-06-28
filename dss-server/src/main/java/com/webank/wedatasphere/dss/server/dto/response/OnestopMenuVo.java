package com.webank.wedatasphere.dss.server.dto.response;

import java.util.List;

/**
 * Created by schumiyi on 2020/6/24
 */
public class OnestopMenuVo {
    private Long id;
    private String title;
    private Integer order;
    private List<OnestopMenuAppInstanceVo> appInstances;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<OnestopMenuAppInstanceVo> getAppInstances() {
        return appInstances;
    }

    public void setAppInstances(List<OnestopMenuAppInstanceVo> appInstances) {
        this.appInstances = appInstances;
    }
}
