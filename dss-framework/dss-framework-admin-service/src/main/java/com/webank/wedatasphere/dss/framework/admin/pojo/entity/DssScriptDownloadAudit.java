package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/10-01-10-15:48
 */



@TableName(value = "dss_workspace_download_audit")
public class DssScriptDownloadAudit {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer  id;
    private String  creator;
    private String  tenant;
    private String  path;
     @TableField("`sql`")
    @NotEmpty(message = "查询语句不能为空")
    private String  sql;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DssScriptDownloadAudit{" +
                "id=" + id +
                ", creator='" + creator + '\'' +
                ", tenant='" + tenant + '\'' +
                ", path='" + path + '\'' +
                ", sql='" + sql + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
