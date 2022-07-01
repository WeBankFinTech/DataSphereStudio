package com.webank.wedatasphere.dss.framework.admin.pojo.entity;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/18-01-18-15:58
 */
public class DssExchangeTask {
    private int id;
    private String jobName;
    private String jobCorn;
    private String jobDesc;
    private String createTime;
    private String jobStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobCorn() {
        return jobCorn;
    }

    public void setJobCorn(String jobCorn) {
        this.jobCorn = jobCorn;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }


}
