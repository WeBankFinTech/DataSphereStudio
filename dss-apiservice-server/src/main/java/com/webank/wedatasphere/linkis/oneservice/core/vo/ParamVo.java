package com.webank.wedatasphere.linkis.oneservice.core.vo;

/**
 * oneservice param
 *
 * @author zhulixin
 */
public class ParamVo {

    /**
     * id
     */
    private Long id;

    /**
     * configId
     */
    private Long configId;

    /**
     * version
     */
    private String version;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型：1-String, 2-number, 3-Date
     */
    private String type;

    /**
     * 是否必须
     */
    private Integer required;

    /**
     * 参数描述
     */
    private String description;

    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
