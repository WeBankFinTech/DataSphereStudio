package com.webank.wedatasphere.dss.server.dto.response;

import java.util.List;

/**
 * Created by schumiyi on 2020/6/23
 */
public class HomepageDemoMenuVo {

    private Long id;
    private String name;
    private String title;
    private String description;
    private String icon;
    private Integer order;
    private List<HomepageDemoInstanceVo> demoInstances;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<HomepageDemoInstanceVo> getDemoInstances() {
        return demoInstances;
    }

    public void setDemoInstances(List<HomepageDemoInstanceVo> demoInstances) {
        this.demoInstances = demoInstances;
    }
}
