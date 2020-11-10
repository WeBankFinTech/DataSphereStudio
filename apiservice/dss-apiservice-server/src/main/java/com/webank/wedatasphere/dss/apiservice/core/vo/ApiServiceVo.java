/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.wedatasphere.dss.apiservice.core.vo;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * vo
 *
 * @author zhulixin
 */
public class ApiServiceVo {

    /**
     * id
     */
    private Long id;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务地址
     */
    private String path;

    /**
     * 服务协议： 1-http, 2-https
     */
    private int protocol;

    /**
     * 请求方法：GET POST PUT DELETE
     */
    private String method;

    /**
     * 标签
     */
    private String tag;

    /**
     * 可见范围
     */
    private String scope;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status = 1;

    /**
     * 类型： jdbc脚本
     */
    private String type;

    /**
     * create time
     */
    private Date createTime;

    /**
     * Modify time
     */
    private Date modifyTime;

    /**
     * api创建者
     */
    private String creator;

    /**
     * Modify user
     */
    private String modifier;

    /**
     * 参数列表
     */
    private List<ParamVo> params;

    /**
     * 脚本内容
     */
    private String content;

    /**
     * 脚本路径
     */
    private String scriptPath;

    /**
     * bml的文件id
     */
    private String resourceId;

    /**
     * bml的文件版本
     */
    private String version;

    private Map<String, Object> metadata;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public List<ParamVo> getParams() {
        return params;
    }

    public void setParams(List<ParamVo> params) {
        this.params = params;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
