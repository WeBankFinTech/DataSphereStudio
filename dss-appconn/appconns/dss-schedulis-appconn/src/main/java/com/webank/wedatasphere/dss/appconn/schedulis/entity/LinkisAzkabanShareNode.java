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

package com.webank.wedatasphere.dss.appconn.schedulis.entity;


import com.webank.wedatasphere.dss.appconn.schedulis.linkisjob.LinkisJobConverter;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.ShareNode;

public class LinkisAzkabanShareNode extends LinkisAzkabanSchedulerNode implements ShareNode {
    private SchedulerNode schedulerNode;
    private int shareTimes;

    @Override
    public int getShareTimes() {
        return shareTimes;
    }

    @Override
    public void setShareTimes(int num) {
        this.shareTimes = num;
    }

    @Override
    public SchedulerNode getSchedulerNode() {
        return schedulerNode;
    }

    @Override
    public void setSchedulerNode(SchedulerNode node) {
        this.schedulerNode = node;
    }

    @Override
    public String toJobString(LinkisJobConverter converter) {
        return converter.conversion(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if(obj == null){
            return false;
        }

        if(obj instanceof LinkisAzkabanShareNode){
            LinkisAzkabanShareNode other = (LinkisAzkabanShareNode) obj;
            //需要比较的字段相等，则这两个对象相等
            if(this.getId().equals(other.getId())
                    && (this.getName().equals(other.getName()))){
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.getId() == null ? 0 : this.getId().hashCode());
        result = 31 * result + (this.getName() == null ? 0 : this.getName().hashCode());
        return result;
    }

}
