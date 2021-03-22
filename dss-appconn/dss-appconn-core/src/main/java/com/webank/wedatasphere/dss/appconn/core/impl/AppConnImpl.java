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

package com.webank.wedatasphere.dss.appconn.core.impl;


import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by enjoyyin on 2020/9/10.
 */
public class AppConnImpl implements AppConn {

    private AppDesc appDesc;
    private List<AppStandard> appStandards;

    @Override
    public List<AppStandard> getAppStandards() {
        if(appStandards == null) {
            initAppStandards();
        }
        return appStandards;
    }


    /**
     * 规范：每个AppConn需要定义一个以create开头返回为Standard类型的方法来初始化AppConn拥有的规范。
     * */
    protected void initAppStandards() {
        appStandards = Arrays.stream(getClass().getDeclaredMethods()).filter(method -> method.getName().startsWith("create") &&
                method.getReturnType().isAssignableFrom(AppStandard.class))
                .map(method -> {
                    try {
                        return (AppStandard)method.invoke(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
        appStandards.forEach(appStandard -> appStandard.setAppDesc(appDesc));
    }

    @Override
    public AppDesc getAppDesc() {
        return appDesc;
    }


    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    public static AppConnBuilder newBuilder() {
        return new AppConnBuilderImpl();
    }

    static class AppConnBuilderImpl implements AppConnBuilder {

        private AppConnImpl appConn = new AppConnImpl();

        @Override
        public AppConnBuilder setAppDesc(AppDesc appDesc) {
            appConn.appDesc = appDesc;
            return this;
        }

        @Override
        public AppConnBuilder setAppStandards(List<AppStandard> appStandards) {
            appConn.appStandards = appStandards;
            return this;
        }

        @Override
        public AppConnBuilder addAppStandard(AppStandard appStandard) {
            if(appConn.appStandards == null) {
                appConn.appStandards = new ArrayList<>();
            }
            appConn.appStandards.add(appStandard);
            return this;
        }

        @Override
        public AppConn build() {
            return appConn;
        }
    }
}
