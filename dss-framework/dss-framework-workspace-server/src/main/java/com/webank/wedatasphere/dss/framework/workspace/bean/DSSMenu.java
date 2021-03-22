/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * created by cooperyang on 2020/3/5
 * Description:
 */
@TableName(value = "dss_menu")
public class DSSMenu implements Serializable {
    private static final long serialVersionUID=1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private int level;
    private int upperMenuId;
    private String frontName;
    private String comment;
    private String description;
    private boolean isActive;
    private boolean isComponent;
    @TableField(exist = false)
    private String url;
    private String icon;
    private int applicationId;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getUpperMenuId() {
        return upperMenuId;
    }

    public void setUpperMenuId(int upperMenuId) {
        this.upperMenuId = upperMenuId;
    }

    public String getFrontName() {
        return frontName;
    }

    public void setFrontName(String frontName) {
        this.frontName = frontName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isComponent() {
        return isComponent;
    }

    public void setComponent(boolean component) {
        isComponent = component;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DSSMenu dssMenu = (DSSMenu) o;
        return id == dssMenu.id &&
                level == dssMenu.level &&
                upperMenuId == dssMenu.upperMenuId &&
                isActive == dssMenu.isActive &&
                isComponent == dssMenu.isComponent &&
                applicationId == dssMenu.applicationId &&
                Objects.equal(name, dssMenu.name) &&
                Objects.equal(frontName, dssMenu.frontName) &&
                Objects.equal(description, dssMenu.description) &&
                Objects.equal(url, dssMenu.url) &&
                Objects.equal(icon, dssMenu.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, level, upperMenuId, frontName, description, isActive, isComponent, url, icon, applicationId);
    }
}
