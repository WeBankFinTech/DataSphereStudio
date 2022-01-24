package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

/**
 * @ClassName: DssApplication
 * @Description: dss_aapplication 实体类
 * @author: lijw
 * @date: 2022/1/7 9:18
 */
public class DssApplicationInfo {
    private  Integer id;
    private String name;
    private  String url;
    private  Integer isUserNeedInit;
    private Integer level;
    private String userInitUrl;
    private Integer existsProjectService;
    private  String projectUrl;
    private String enhanceJson;
    private Integer ifIframe;
    private  String homepageUrl;
    private String redirectUrl;

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

    public Integer getExistsProjectService() {
        return existsProjectService;
    }

    public void setExistsProjectService(Integer existsProjectService) {
        this.existsProjectService = existsProjectService;
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
        return "DssApplicationInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", isUserNeedInit=" + isUserNeedInit +
                ", level=" + level +
                ", userInitUrl='" + userInitUrl + '\'' +
                ", existsProjectService=" + existsProjectService +
                ", projectUrl='" + projectUrl + '\'' +
                ", enhanceJson='" + enhanceJson + '\'' +
                ", ifIframe=" + ifIframe +
                ", homepageUrl='" + homepageUrl + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}
