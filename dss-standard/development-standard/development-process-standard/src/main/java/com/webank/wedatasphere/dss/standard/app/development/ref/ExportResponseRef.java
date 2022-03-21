package com.webank.wedatasphere.dss.standard.app.development.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface ExportResponseRef extends ResponseRef {

    /**
     * Now, DSS only supports to import/export BML resources, so the resourceMap is consisted of `resourceId`
     * and `version`.
     * <br>
     * So, if third-part AppConn want to achieve the RefExportOperation, it is neccessary that the third-part AppConn
     * must upload the third-part AppConn jobs to Linkis BML at first, and then return the resourceMap to DSS.
     * @return a refJobContent related a only third appConn refJob
     */
    Map<String, Object> getResourceMap();

    default boolean isLinkisBMLResources() {
        return getResourceMap().containsKey(ImportRequestRef.RESOURCE_ID_KEY)
                && getResourceMap().containsKey(ImportRequestRef.RESOURCE_VERSION_KEY);
    }

    static ExportResponseRefBuilder newBuilder() {
        return new ExportResponseRefBuilder();
    }

    class ExportResponseRefBuilder
            extends ResponseRefBuilder.ExternalResponseRefBuilder<ExportResponseRefBuilder, ExportResponseRef> {

        private Map<String, Object> resourceMap;

        public ExportResponseRefBuilder setResourceMap(Map<String, Object> resourceMap) {
            this.resourceMap = resourceMap;
            return this;
        }

        class ExportResponseRefImpl extends ResponseRefImpl implements ExportResponseRef {
            public ExportResponseRefImpl() {
                super(ExportResponseRefBuilder.this.responseBody, ExportResponseRefBuilder.this.status,
                        ExportResponseRefBuilder.this.errorMsg, ExportResponseRefBuilder.this.responseMap);
            }
            @Override
            public Map<String, Object> getResourceMap() {
                return resourceMap;
            }
        }

        @Override
        protected ExportResponseRefImpl createResponseRef() {
            return new ExportResponseRefImpl();
        }
    }
}
