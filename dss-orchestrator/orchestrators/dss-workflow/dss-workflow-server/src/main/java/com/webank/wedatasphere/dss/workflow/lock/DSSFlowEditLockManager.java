/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.workflow.lock;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowEditLock;
import org.apache.linkis.DataWorkCloudApplication;
import org.apache.linkis.common.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


/**
 * 工作流编辑分布式锁
 */
public class DSSFlowEditLockManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFlowEditLockManager.class);

    private volatile static boolean isInit;

    private static LockMapper lockMapper;


    private static final DelayQueue<UnLockEvent> unLockEvents = new DelayQueue<>();

    protected DSSFlowEditLockManager() {
    }

    static {
        LOGGER.info("unLockEvents移除定时线程开启...");
        LOGGER.info("编辑锁超时时间为：{} ms", DSSWorkFlowConstant.DSS_FLOW_EDIT_LOCK_TIMEOUT.getValue());
        init();
        //程序重启时，删除所有编辑锁
        lockMapper.deleteALL();
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            UnLockEvent pop = unLockEvents.poll();
            if (pop != null) {
                LOGGER.info(pop.toString());
                DSSFlowEditLock flowEditLock = pop.getFlowEditLock();
                DSSFlowEditLock updateFlowEditLock = lockMapper.getFlowEditLockByID(flowEditLock.getFlowID());
                //队列对象如果过期，先去数据库查询锁，并判断锁是否过期
                if (updateFlowEditLock != null && isLockExpire(updateFlowEditLock)) {
                    //锁过期，移除记录
                    flowEditLock.setExpire(true);
                    lockMapper.clearExpire(flowEditLock.getFlowID());
                    LOGGER.info(String.format("clear lock success,lock:%s", pop));
                } else if (updateFlowEditLock == null) {
                    LOGGER.info("lock already clear");
                } else {
                    //锁没有过期，延长队列时间
                    LOGGER.info(String.format("delay pop:%s", pop));
                    pop.setExpireTime(updateFlowEditLock.getUpdateTime().getTime() + DSSWorkFlowConstant.DSS_FLOW_EDIT_LOCK_TIMEOUT.getValue());
                    unLockEvents.offer(pop);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 获取工作流编辑锁
     *
     * @param dssFlow  工作流对象
     * @param username 登陆用户
     * @param owner    锁的拥有者(ticketId)
     * @return 锁id
     * @throws DSSErrorException ex
     */
    public static String tryAcquireLock(DSSFlow dssFlow, String username, String owner) throws DSSErrorException {
        if (StringUtils.isBlank(username)) {
            throw new DSSErrorException(60061, "tryAcquireLock failed , because username is null");
        }
        if (StringUtils.isBlank(owner)) {
            throw new DSSErrorException(60062, "tryAcquireLock failed , because owner is null");
        }
        Long flowID = dssFlow.getId();
        if (flowID == null) {
            throw new DSSErrorException(60063, "tryAcquireLock failed , because flowId is null");
        }
        String lock;
        init();
        DSSFlowEditLock flowEditLock = lockMapper.getPersonalFlowEditLock(flowID, owner);

        if (flowEditLock != null && isLockExpire(flowEditLock)) {
            // 1.另外的dss-server服务挂掉了（main）;2.记录已经过期，但是UnLockEvent 尚未(即将)去更新数据库;3.记录已经过期，但是updateTime 尚未（即将）更新数据库
            flowEditLock.setExpire(true);
            int i = lockMapper.compareAndSwap(flowEditLock);
            LOGGER.info("try to set lock to an expire state,row:{},lock:{}", i, flowEditLock);
            if (i == 1) {
                //更新成功，尝试获取新锁
                LOGGER.info("unlock success,lock:{}", flowEditLock);
                lockMapper.clearExpire(flowEditLock.getFlowID());
                lock = generateLock(flowID, username, owner);
            } else {
                // 失败的缘由: 1.另外的dss-server也走到了这步，优先更新了;2.UnLockEvent;3.用户刚好点了saveFlow延续
                throw new DSSErrorException(60055, "acquire lock failed");
            }
        } else if (flowEditLock != null) {
            lock = flowEditLock.getLockContent();
        } else {
            // 插入锁,获取到数据库自增id
            lock = generateLock(flowID, username, owner);
        }
        // 插入成功，返回lock，push一条记录到queue中，插入失败，返回null
        return lock;
    }

    private static String generateLock(Long flowID,  String username, String owner) throws DSSErrorException {
        try {
            String lockContent = UUID.randomUUID().toString();
            Date date = new Date();
            DSSFlowEditLock newLock = new DSSFlowEditLock();
            newLock.setExpire(false);
            newLock.setCreateTime(date);
            newLock.setUpdateTime(date);
            newLock.setFlowID(flowID);
            newLock.setLockContent(lockContent);
            newLock.setOwner(owner);
            newLock.setUsername(username);
            lockMapper.insertLock(newLock);
            //推到queue中
            UnLockEvent unLockEvent = new UnLockEvent();
            unLockEvent.setCreateTime(date.getTime());
            unLockEvent.setExpireTime(date.getTime() + DSSWorkFlowConstant.DSS_FLOW_EDIT_LOCK_TIMEOUT.getValue());
            unLockEvent.setFlowEditLock(newLock);
            unLockEvents.offer(unLockEvent);
            return lockContent + DSSWorkFlowConstant.SPLIT + 0;
        } catch (DuplicateKeyException e) {
            LOGGER.warn("acquire lock failed", e);
            DSSFlowEditLock personalFlowEditLock = lockMapper.getPersonalFlowEditLock(flowID,  null);
            String userName = Optional.ofNullable(personalFlowEditLock).map(DSSFlowEditLock::getUsername).orElse(null);
            throw new DSSErrorException(DSSWorkFlowConstant.EDIT_LOCK_ERROR_CODE, "用户" + userName + "已锁定编辑");
        }
    }

    public static String updateLock(String lock) throws DSSErrorException {
        if (StringUtils.isBlank(lock)) {
            throw new DSSErrorException(60066, "update workflow failed because you do not have flowEditLock!");
        }
        //保存并刷新数据库更新时间
        String[] array = lock.split(DSSWorkFlowConstant.SPLIT);
        String lockContent = array[0];
        DSSFlowEditLock dssFlowEditLock = new DSSFlowEditLock();
        dssFlowEditLock.setUpdateTime(new Date());
        dssFlowEditLock.setLockContent(lockContent);
        int i = lockMapper.compareAndSwap(dssFlowEditLock);
        if (i == 0) {
            //update failed
            //情况1.点太快了，分发到另外一台dss-server先更新了
            //情况2.已经让unlockEvent设置为过期了
            //情况3，已经让tryAccquire 方法设置为过期了
            DSSFlowEditLock updateFlowEditLock = lockMapper.getFlowEditLockByLockContent(array[0]);
            if (updateFlowEditLock == null || updateFlowEditLock.getExpire()) {
                throw new DSSErrorException(60057, "编辑锁已过期，请刷新页面");
            } else {
                return lock;
            }
        } else if (i == 1) {
            //update success 防止开2窗口的情况
            LOGGER.info("update lock expire time success,{}", dssFlowEditLock);
            return lock;
        } else {
            LOGGER.info("unexpected update error,{}", dssFlowEditLock);
            throw new DSSErrorException(60059, String.format("unexpected update error,%s", dssFlowEditLock));
        }
    }

    public static boolean isLockExpire(DSSFlowEditLock flowEditLock) {
        return System.currentTimeMillis() - flowEditLock.getUpdateTime().getTime() >= DSSWorkFlowConstant.DSS_FLOW_EDIT_LOCK_TIMEOUT.getValue();
    }


    public static void init() {
        if (!isInit) {
            synchronized (DSSFlowEditLockManager.class) {
                if (!isInit) {
                    lockMapper = DataWorkCloudApplication.getApplicationContext().getBean(LockMapper.class);
                    isInit = true;
                }
            }
        }
    }

    public static class UnLockEvent implements Delayed {
        private long createTime;
        private long expireTime;
        private DSSFlowEditLock flowEditLock;

        DSSFlowEditLock getFlowEditLock() {
            return flowEditLock;
        }

        void setFlowEditLock(DSSFlowEditLock flowEditLock) {
            this.flowEditLock = flowEditLock;
        }

        public boolean isExpire() {
            return System.currentTimeMillis() - expireTime > 0;
        }

        public long getCreateTime() {
            return createTime;
        }

        void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getExpireTime() {
            return this.expireTime;
        }

        void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expireTime - System.currentTimeMillis(), unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }

        @Override
        public String toString() {
            return "UnLockEvent{" +
                    "createTime=" + createTime +
                    ", expireTime=" + expireTime +
                    ", flowEditLock=" + flowEditLock +
                    '}';
        }
    }

}
