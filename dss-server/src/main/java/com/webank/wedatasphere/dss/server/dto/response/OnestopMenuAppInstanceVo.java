package com.webank.wedatasphere.dss.server.dto.response;

/**
 * Created by schumiyi on 2020/6/24
 */
public class OnestopMenuAppInstanceVo {
    private Long id;
    private String title;
    private String description;
    private String labels;
    private String accessButton;
    private String accessButtonUrl;
    private String manualButton;
    private String manualButtonUrl;
    private String projectUrl;
    private String name;
    private Boolean isActive;
    private String icon;
    private Integer order;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getAccessButton() {
        return accessButton;
    }

    public void setAccessButton(String accessButton) {
        this.accessButton = accessButton;
    }

    public String getAccessButtonUrl() {
        return accessButtonUrl;
    }

    public void setAccessButtonUrl(String accessButtonUrl) {
        this.accessButtonUrl = accessButtonUrl;
    }

    public String getManualButton() {
        return manualButton;
    }

    public void setManualButton(String manualButton) {
        this.manualButton = manualButton;
    }

    public String getManualButtonUrl() {
        return manualButtonUrl;
    }

    public void setManualButtonUrl(String manualButtonUrl) {
        this.manualButtonUrl = manualButtonUrl;
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

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
