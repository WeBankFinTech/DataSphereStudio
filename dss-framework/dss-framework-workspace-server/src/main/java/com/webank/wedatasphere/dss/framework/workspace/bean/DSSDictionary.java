/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;


@TableName(value = "dss_workspace_dictionary")
public class DSSDictionary implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键ID，默认为0，所有空间都有
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 空间ID，默认为0，所有空间都有
     */
    private Integer workspaceId;

    /**
     * 父key
     */
    private String parentKey;

    /**
     * 名称
     */
    private String dicName;
    /**
     * 名称(英文)
     */
    private String dicNameEn;

    /**
     * key 相当于编码，空间是w_开头，工程是p_
     */
    private String dicKey;

    /**
     * key对应的值
     */
    private String dicValue;

    /**
     * key对应的值(英文)
     */
    private String dicValueEn;

    /**
     * 标题
     */
    private String title;

    /**
     * 标题(英文)
     */
    private String titleEn;

    /**
     * url
     */
    private String url;

    /**
     * url类型: 0-内部系统，1-外部系统；默认是内部
     */
    private Integer urlType;

    /**
     * 图标
     */
    private String icon;

    /**
     * 序号
     */
    private Integer orderNum;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 默认勾选工作流开发流程
     */
    private Integer checked;

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }

    public String getDicKey() {
        return dicKey;
    }

    public void setDicKey(String dicKey) {
        this.dicKey = dicKey;
    }

    public String getDicValue() {
        return dicValue;
    }

    public void setDicValue(String dicValue) {
        this.dicValue = dicValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getUrlType() {
        return urlType;
    }

    public void setUrlType(Integer urlType) {
        this.urlType = urlType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDicNameEn() {
        return dicNameEn;
    }

    public void setDicNameEn(String dicNameEn) {
        this.dicNameEn = dicNameEn;
    }

    public String getDicValueEn() {
        return dicValueEn;
    }

    public void setDicValueEn(String dicValueEn) {
        this.dicValueEn = dicValueEn;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    @Override
    public String toString() {
        return "DSSDictionary{" +
                "id=" + id +
                ", workspaceId=" + workspaceId +
                ", parentKey='" + parentKey + '\'' +
                ", dicName='" + dicName + '\'' +
                ", dicNameEn='" + dicNameEn + '\'' +
                ", dicKey='" + dicKey + '\'' +
                ", dicValue='" + dicValue + '\'' +
                ", dicValueEn='" + dicValueEn + '\'' +
                ", title='" + title + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", url='" + url + '\'' +
                ", urlType=" + urlType +
                ", icon='" + icon + '\'' +
                ", orderNum=" + orderNum +
                ", remark='" + remark + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", updateUser='" + updateUser + '\'' +
                ", updateTime=" + updateTime + '\'' +
                ", checked=" + checked +
                '}';
    }
}
