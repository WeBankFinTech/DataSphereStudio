package com.webank.wedatasphere.dss.appconn.visualis.operation;


import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.AbstractOperationStrategy;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;

import java.util.*;
import java.util.function.Supplier;

public class OperationStrategyFactory {

    private static OperationStrategyFactory factory;
    private static SSORequestOperation ssoRequestOperation;
    private static final Map<AppInstance, List<OperationStrategy>> operationStrategies = new HashMap<>();
    private static final Map<String, Class<? extends OperationStrategy>> operationStrategyClasses = new HashMap<>();

    private OperationStrategyFactory() {
        ClassUtils.getClasses(OperationStrategy.class).forEach(clazz -> {
            try {
                operationStrategyClasses.put(clazz.newInstance().getStrategyName(), clazz);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExternalOperationWarnException(50700, "Instance " + clazz + " of visualis OperationStrategy failed.", e);
            }
        });
    }


    public OperationStrategy getOperationStrategy(AppInstance appInstance, String strategyName) throws ExternalOperationFailedException {
        if(!operationStrategies.containsKey(appInstance)) {
            synchronized (operationStrategies) {
                if(!operationStrategies.containsKey(appInstance)) {
                    operationStrategies.put(appInstance, new ArrayList<>());
                }
            }
        }
        List<OperationStrategy> strategies = operationStrategies.get(appInstance);
        Supplier<Optional<OperationStrategy>> function = () -> strategies.stream()
                .filter(strategy -> strategy.getStrategyName().equals(strategyName)).findAny();
        return function.get().orElseGet(() -> {
            synchronized (strategies) {
                return function.get().orElseGet(() -> {
                    OperationStrategy operationStrategy;
                    try {
                        operationStrategy = operationStrategyClasses.get(strategyName).newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new ExternalOperationWarnException(50700, "Instance " + strategyName + " of visualis OperationStrategy failed.", e);
                    }
                    if(operationStrategy instanceof AbstractOperationStrategy) {
                        ((AbstractOperationStrategy) operationStrategy).setBaseUrl(appInstance.getBaseUrl());
                        ((AbstractOperationStrategy) operationStrategy).setSsoRequestOperation(ssoRequestOperation);
                    }
                    strategies.add(operationStrategy);
                    return operationStrategy;
                });
            }
        });
    }


     public static OperationStrategyFactory getInstance() {
        if (factory == null) {
            synchronized (OperationStrategyFactory.class) {
                if (factory == null) {
                    factory = new OperationStrategyFactory();
                }
            }
        }
        return factory;
    }

    public static void setSsoRequestOperation(SSORequestOperation ssoRequestOperation) {
        OperationStrategyFactory.ssoRequestOperation = ssoRequestOperation;
    }
}
