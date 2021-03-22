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

package com.webank.wedatasphere.dss.common.entity.label;

import java.util.Arrays;

/**
 * created by cooperyang on 2021/1/12
 * Description: 环境label的枚举
 * 标签的流转都是从小的开始，然后逐步添加，如果现在是dev标签，后一个标签就是TEST,拿到了TEST之后，
 * 如果没有test的instance，则继续往上,直到最后
 */
public enum EnvEnum {

    /**
     *
     */
    DEV(1, "DEV", "开发中心"),
    TEST(2, "TEST", "生产中心"),
    PROD(3, "PROD", "生产中心"),
    VIRTUAL(-99, "VIRTUAL", "默认的返回,不做操作");

    int index;
    String name;
    String description;

    EnvEnum(int index, String name, String description){
        this.index = index;
        this.name = name;
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EnvEnum getNext(EnvEnum envEnum){
        return getNext(envEnum.index);
    }

    public static EnvEnum getNext(int index){
        return Arrays.stream(values()).filter(v -> v.index == index + 1).findFirst().orElse(VIRTUAL);
    }

    public static EnvEnum getPrevious(EnvEnum envEnum){
        return getNext(envEnum.index);
    }

    public static EnvEnum getPrevious(int index){
        return Arrays.stream(values()).filter(v -> v.index == index - 1).findFirst().orElse(VIRTUAL);
    }




}
