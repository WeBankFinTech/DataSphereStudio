package com.webank.wedatasphere.dss.server.entity;

/**
 * Created by Adamyuanyuan on 2020/6/25
 */
public class DWSFavorite extends BaseEntity{

    private Long id;

    private String username;

    private Long workspaceId;

    private Long menuApplicationId;

    private Integer order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getMenuApplicationId() {
        return menuApplicationId;
    }

    public void setMenuApplicationId(Long menuApplicationId) {
        this.menuApplicationId = menuApplicationId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
