package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.google.common.base.Objects;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceBuilder {

    static class SimplePair<K, V> {

        private K key;
        private V value;

        public SimplePair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SimplePair<?, ?> simplePair = (SimplePair<?, ?>)o;
            return Objects.equal(key, simplePair.key) && Objects.equal(value, simplePair.value);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ServiceBuilder.class);

    private volatile static ServiceBuilder serviceBuilder;

    private ServiceBuilder() {

    }

    public static ServiceBuilder getInstance() {
        if (serviceBuilder == null) {
            synchronized (ServiceBuilder.class) {
                if (serviceBuilder == null) {
                    serviceBuilder = new ServiceBuilder();
                }
            }
        }
        return serviceBuilder;
    }

    private final Map<SimplePair<AppInstance, Class<? extends StructureService>>, StructureService> serviceMaps
        = new ConcurrentHashMap<>();

    public StructureService getDolphinSchedulerStructureService(AppInstance appInstance, AppDesc appDesc,
        StructureIntegrationStandard structureIntegrationStandard, Class<? extends StructureService> clazz) {
        SimplePair<AppInstance, Class<? extends StructureService>> simplePair = new SimplePair<>(appInstance, clazz);
        if (serviceMaps.get(simplePair) != null) {
            return serviceMaps.get(simplePair);
        }
        try {
            Constructor<?> constructor = clazz.getConstructor(AppDesc.class, AppInstance.class,
                StructureIntegrationStandard.class);
            StructureService service = (StructureService)constructor.newInstance(appDesc, appInstance,
                structureIntegrationStandard);
            //            service.setAppInstance(appInstance);
            //            service.setAppDesc(appDesc);
            //            service.setAppStandard(structureIntegrationStandard);
            serviceMaps.put(simplePair, service);
        } catch (Exception e) {
            logger.error("Failed to instance {}", clazz.getName(), e);
        }
        return serviceMaps.get(simplePair);
    }

}
