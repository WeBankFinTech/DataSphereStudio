package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @ClassName: DssOnestopMenuApplication
 * @Description: dss_onestop_menu_application 实体类
 * @author: lijw
 * @date: 2022/1/6 10:44
 */
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DssOnestopMenuJoinApplication {
    private Integer id;
    private  Integer applicationId;
    private Integer onestopMenuId;
    private  String titleEn;
    private  String titleCn;
    private String descEn;
    private String descCn;
    private String labelsEn;
    private  String labelsCn;
    private  Integer isActive;
    private  String accessButtonEn;
    private String accessButtonCn;
    private  String manualButtonEn;
    private  String manualButtonCn;
    private  String  manualButtonUrl;
    private  String icon;
    private Integer order;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date lastUpdateTime;
    private String lastUpdateUser;
    private  String image;
    private String name;
    private String url;
    private Integer isUserNeedInit;
    private  Integer level;
    private String userInitUrl;
    private int existProjectService;
    private String projectUrl;
    private  String enhanceJson;
    private Integer ifIframe;
    private String homepageUrl;
    private String redirectUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getOnestopMenuId() {
        return onestopMenuId;
    }

    public void setOnestopMenuId(Integer onestopMenuId) {
        this.onestopMenuId = onestopMenuId;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleCn() {
        return titleCn;
    }

    public void setTitleCn(String titleCn) {
        this.titleCn = titleCn;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    public String getDescCn() {
        return descCn;
    }

    public void setDescCn(String descCn) {
        this.descCn = descCn;
    }

    public String getLabelsEn() {
        return labelsEn;
    }

    public void setLabelsEn(String labelsEn) {
        this.labelsEn = labelsEn;
    }

    public String getLabelsCn() {
        return labelsCn;
    }

    public void setLabelsCn(String labelsCn) {
        this.labelsCn = labelsCn;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getAccessButtonEn() {
        return accessButtonEn;
    }

    public void setAccessButtonEn(String accessButtonEn) {
        this.accessButtonEn = accessButtonEn;
    }

    public String getAccessButtonCn() {
        return accessButtonCn;
    }

    public void setAccessButtonCn(String accessButtonCn) {
        this.accessButtonCn = accessButtonCn;
    }

    public String getManualButtonEn() {
        return manualButtonEn;
    }

    public void setManualButtonEn(String manualButtonEn) {
        this.manualButtonEn = manualButtonEn;
    }

    public String getManualButtonCn() {
        return manualButtonCn;
    }

    public void setManualButtonCn(String manualButtonCn) {
        this.manualButtonCn = manualButtonCn;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getIsUserNeedInit() {
        return isUserNeedInit;
    }

    public void setIsUserNeedInit(Integer isUserNeedInit) {
        this.isUserNeedInit = isUserNeedInit;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getUserInitUrl() {
        return userInitUrl;
    }

    public void setUserInitUrl(String userInitUrl) {
        this.userInitUrl = userInitUrl;
    }

    public int getExistProjectService() {
        return existProjectService;
    }

    public void setExistProjectService(int existProjectService) {
        this.existProjectService = existProjectService;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getEnhanceJson() {
        return enhanceJson;
    }

    public void setEnhanceJson(String enhanceJson) {
        this.enhanceJson = enhanceJson;
    }

    public Integer getIfIframe() {
        return ifIframe;
    }

    public void setIfIframe(Integer ifIframe) {
        this.ifIframe = ifIframe;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString() {
        return "DssOnestopMenuJoinApplication{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", onestopMenuId=" + onestopMenuId +
                ", titleEn='" + titleEn + '\'' +
                ", titleCn='" + titleCn + '\'' +
                ", descEn='" + descEn + '\'' +
                ", descCn='" + descCn + '\'' +
                ", labelsEn='" + labelsEn + '\'' +
                ", labelsCn='" + labelsCn + '\'' +
                ", isActive=" + isActive +
                ", accessButtionEn='" + accessButtonEn + '\'' +
                ", accessButtonCn='" + accessButtonCn + '\'' +
                ", manualButtonEn='" + manualButtonEn + '\'' +
                ", manualButtonCn='" + manualButtonCn + '\'' +
                ", manualButtonUrl='" + manualButtonUrl + '\'' +
                ", icon='" + icon + '\'' +
                ", order=" + order +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", lastUpdateUser='" + lastUpdateUser + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", isUserNeedInit=" + isUserNeedInit +
                ", level=" + level +
                ", userInitUrl='" + userInitUrl + '\'' +
                ", existProjectService=" + existProjectService +
                ", projectUrl='" + projectUrl + '\'' +
                ", enhanceJson='" + enhanceJson + '\'' +
                ", ifIframe=" + ifIframe +
                ", homepageUrl='" + homepageUrl + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}
