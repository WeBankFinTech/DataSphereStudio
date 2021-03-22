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

import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/3/20
 * Description:
 */
public class DSSWorkspaceOverviewVO {
    private String title;


    private String description;

    private String dssDescription;




    private List<OverviewInfo> videos;


    Map<String, List<OverviewInfo>> demos;

    public static class OverviewInfo{
        private int id;
        private String title;
        private String url;

        public OverviewInfo(){

        }

        public OverviewInfo(String title, String url){
            this.title = title;
            this.url = url;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDssDescription() {
        return dssDescription;
    }

    public void setDssDescription(String dssDescription) {
        this.dssDescription = dssDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Map<String, List<OverviewInfo>> getDemos() {
        return demos;
    }

    public void setDemos(Map<String, List<OverviewInfo>> demos) {
        this.demos = demos;
    }

    public List<OverviewInfo> getVideos() {
        return videos;
    }

    public void setVideos(List<OverviewInfo> videos) {
        this.videos = videos;
    }
}
