package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

/**
 * @ClassName: DssCreateApplicationData
 * @Description: create Application data
 * @author: lijw
 * @date: 2022/1/6 17:36
 */
public class DssCreateApplicationData {
    private  String id;
    private  String titleEn;
    private String  titleCn;
    private  String url;
    private  String onestopMenuId;
    private String projectUrl;
    private String  homepageUrl;
    private Integer ifIframe;
    private String redirectUrl;
    private String descCn;
    private  String descEn;
    private String labelsEn;
    private String labelsCn;
    private String isActive;
    private String accessButtonEn;
    private String accessButtonCn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOnestopMenuId() {
        return onestopMenuId;
    }

    public void setOnestopMenuId(String onestopMenuId) {
        this.onestopMenuId = onestopMenuId;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public Integer getIfIframe() {
        return ifIframe;
    }

    public void setIfIframe(Integer ifIframe) {
        this.ifIframe = ifIframe;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getDescCn() {
        return descCn;
    }

    public void setDescCn(String descCn) {
        this.descCn = descCn;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
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

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
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

    @Override
    public String toString() {
        return "DssCreateApplicationData{" +
                "id='" + id + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", titleCn='" + titleCn + '\'' +
                ", url='" + url + '\'' +
                ", onestopMenuId='" + onestopMenuId + '\'' +
                ", projectUrl='" + projectUrl + '\'' +
                ", homepageUrl='" + homepageUrl + '\'' +
                ", ifIframe=" + ifIframe +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", descCn='" + descCn + '\'' +
                ", descEn='" + descEn + '\'' +
                ", labelsEn='" + labelsEn + '\'' +
                ", labelsCn='" + labelsCn + '\'' +
                ", isActive='" + isActive + '\'' +
                ", accessButtonEn='" + accessButtonEn + '\'' +
                ", accessButtonCn='" + accessButtonCn + '\'' +
                '}';
    }
}
