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

package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.util.Arrays;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSPublicTableVO extends AbstractDSSVO{
    private int id;
    private String name;
    private String status;
    private String type;
    private Boolean isPartitionTable;
    private String creator;
    private String authority;
    private String lastOperator;
    private String lastOperateTime;

    public DSSPublicTableVO() {
    }

    public DSSPublicTableVO(int id, String name, String status, String type, Boolean isPartitionTable, String creator, String authority, String lastOperator, String lastOperateTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.type = type;
        this.isPartitionTable = isPartitionTable;
        this.creator = creator;
        this.authority = authority;
        this.lastOperator = lastOperator;
        this.lastOperateTime = lastOperateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPartitionTable() {
        return isPartitionTable;
    }

    public void setPartitionTable(Boolean partitionTable) {
        isPartitionTable = partitionTable;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public void setLastOperator(String lastOperator) {
        this.lastOperator = lastOperator;
    }

    public String getLastOperateTime() {
        return lastOperateTime;
    }

    public void setLastOperateTime(String lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
    }


    public static void main(String[] args){
        DSSPublicTableVO vo1 =
                new DSSPublicTableVO(1, "default.aaa", "正常", "视图表", true, "enjoyyin", "700", "enjoyyin", "2020-03-08 20:14:01");
        DSSPublicTableVO vo2 =
                new DSSPublicTableVO(2, "default.bbb", "正常", "普通表", false, "enjoyyin", "700", "cooperyang", "2020-03-08 20:14:01");

        System.out.println(VOUtils.gson.toJson(Arrays.asList(vo1, vo2)));

    }

}
