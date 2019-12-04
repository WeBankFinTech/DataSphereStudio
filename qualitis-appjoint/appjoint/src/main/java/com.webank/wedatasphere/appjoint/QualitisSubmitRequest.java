package com.webank.wedatasphere.appjoint;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * @author howeye
 */
public class QualitisSubmitRequest {

    @SerializedName("group_id")
    private Long groupId;

    private String partition;
    @SerializedName("execution_user")
    private String executionUser;

    @SerializedName("create_user")
    private String createUser;

    public QualitisSubmitRequest() {
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
