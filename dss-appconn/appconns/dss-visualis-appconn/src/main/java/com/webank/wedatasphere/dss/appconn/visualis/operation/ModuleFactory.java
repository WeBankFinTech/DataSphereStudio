package com.webank.wedatasphere.dss.appconn.visualis.operation;


import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.DashboardOptStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.DisplayOptStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.ViewOptStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.WidgetOptStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.enums.ModuleEnum;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ModuleFactory {
    private volatile static ModuleFactory factory;
    private static Map<String, OperationStrategy> map = new HashMap<>();

    static {
        map.put(ModuleEnum.DASHBOARD.getName(), new DashboardOptStrategy());
        map.put(ModuleEnum.DISPLAY.getName(), new DisplayOptStrategy());
        map.put(ModuleEnum.VIEW.getName(), new ViewOptStrategy());
        map.put(ModuleEnum.WIDGET.getName(), new WidgetOptStrategy());
    }

    private ModuleFactory() {
    }


    public OperationStrategy crateModule(String name) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(name) || !map.containsKey(name)) {
            throw new ExternalOperationFailedException(90177, "create module failed,Unknown task type: " + name, null);
        }
        return map.get(name);
    }


     public static ModuleFactory getInstance() {
        if (factory == null) {
            synchronized (ModuleFactory.class) {
                if (factory == null) {
                    factory = new ModuleFactory();
                }
            }
        }

        return factory;
    }


}
