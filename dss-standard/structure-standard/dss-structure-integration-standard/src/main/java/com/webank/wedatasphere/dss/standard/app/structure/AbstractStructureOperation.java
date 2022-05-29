package com.webank.wedatasphere.dss.standard.app.structure;

import com.webank.wedatasphere.dss.standard.app.sso.operation.AbstractOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractStructureOperation<K extends RequestRef, V extends ResponseRef>
        extends AbstractOperation<K, V> implements StructureOperation<K, V> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final void setStructureService(StructureService service) {
        this.service = service;
    }

    protected StructureService getStructureService() {
        return (StructureService) this.service;
    }

}
