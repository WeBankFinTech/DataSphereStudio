/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.server.entity;


public enum ApplicationArea {
    //  Operational optimization, risk management, marketing recommendation, trend prediction, customer management
    //  运营优化,风险管理,营销推荐,趋势预测,客户管理;
    OO("运营优化","Operational Optimization",0),
    RM("风险管理","Risk Management",1),
    MR("营销推荐","Marketing Recommendation",2),
    TP("趋势预测","Trend Prediction",3),
    CM("客户管理","Customer Management",4);


    private int id;
    private String name;
    private String enName;

    ApplicationArea(String name, String enName, int id) {
        this.name = name;
        this.enName = enName;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId(){
        return this.id;
    }

    public String getEnName(){
        return this.enName;
    }

}
