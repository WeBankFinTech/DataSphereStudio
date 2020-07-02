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

package com.webank.wedatasphere.dss.server.lock;

import com.webank.wedatasphere.dss.server.constant.DSSServerConstant;
import com.webank.wedatasphere.dss.server.dao.ProjectMapper;
import com.webank.wedatasphere.dss.common.entity.project.DSSProjectVersion;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;


@Aspect
@Component
public class LockAspect implements Ordered {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProjectMapper projectMapper;

    @Pointcut(value = "@annotation(com.webank.wedatasphere.dss.server.lock.Lock)")
    private void cut() {
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        Lock  lockAnnotation = currentMethod.getAnnotation(Lock.class);
        logger.info("调用方法：" + currentMethod.getName());
        Object[] args = point.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        int index = ArrayUtils.indexOf(parameterNames, DSSServerConstant.PROJECT_VERSION_ID);
        Long projectVersionID = (Long) args[index];
        if (projectVersionID == null) {
            logger.info("执行删除工程，直接通过");
            return point.proceed();
        }
        logger.info("projectVersionID为：" + projectVersionID);
        DSSProjectVersion dssProjectVersion = projectMapper.selectProjectVersionByID(projectVersionID);
        Integer lock = dssProjectVersion.getLock();
        try {
            Object proceed = point.proceed();
            judge(lockAnnotation, dssProjectVersion, lock, projectVersionID);
            return proceed;
        } catch (Exception e) {
            logger.info("执行过程出现异常", e);
            throw e;
        }
    }

    private void judge(Lock  lockAnnotation, DSSProjectVersion dssProjectVersion, Integer lock, Long projectVersionID) throws DSSErrorException {
        if (lockAnnotation.type().equals(LockEnum.ADD)) {
            logger.info("projectVersion会增加");
            List<DSSProjectVersion> dssProjectVersions = projectMapper.listProjectVersionsByProjectID(dssProjectVersion.getProjectID());
            if (dssProjectVersions.size() < 2 || !dssProjectVersions.get(1).getId().equals(projectVersionID)) {
                throw new DSSErrorException(67457, "已经有别的用户对此project进行了版本更新操作，不能进行此操作！");
            }
        } else {
            logger.info("projectVersion不会增加");
            DSSProjectVersion latest = projectMapper.selectLatestVersionByProjectID(dssProjectVersion.getProjectID());
            if (!latest.getId().equals(projectVersionID)) {
                throw new DSSErrorException(67455, "目前project版本已经不是最新，不能进行此操作！");
            }
        }
        Integer row = projectMapper.updateLock(lock, projectVersionID);
        if (row != 1) {
            throw new DSSErrorException(67456, "已经有别的用户对此project进行了更新，不能进行此操作！");
        }
    }


    @Override
    public int getOrder() {
        return 3;
    }
}
