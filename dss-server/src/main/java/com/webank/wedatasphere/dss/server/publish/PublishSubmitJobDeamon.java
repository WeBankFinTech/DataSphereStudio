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

package com.webank.wedatasphere.dss.server.publish;


import java.util.concurrent.Future;


public class PublishSubmitJobDeamon extends PublishJob {

    private Future future;

    private int time;

    public PublishSubmitJobDeamon(Future future, int time) {
        this.future = future;
        this.time = time;
    }

    @Override
    public void run() {
        logger.info("projectVersionID:{}的发布守护线程启动", projectVersionID);
        try {
            Thread.sleep(1000L * time);
        } catch (InterruptedException e) {
            logger.error("deamon被打断了",e);
            Thread.currentThread().interrupt();
        }
        logger.info("开始取消projectVersionID:{}的发布线程", projectVersionID);
        future.cancel(true);
        logger.info("成功取消projectVersionID:{}的发布线程", projectVersionID);
    }
}
