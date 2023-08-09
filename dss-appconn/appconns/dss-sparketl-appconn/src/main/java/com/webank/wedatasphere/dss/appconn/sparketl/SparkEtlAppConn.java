package com.webank.wedatasphere.dss.appconn.sparketl;

import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractAppConn;
import com.webank.wedatasphere.dss.appconn.sparketl.standard.SparkEtlDevelopmentStandard;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import org.apache.linkis.common.conf.CommonVars;


public class SparkEtlAppConn extends AbstractAppConn implements OnlyDevelopmentAppConn {

    public static final String SPARKETL_APPCONN_NAME = CommonVars.apply("wds.dss.appconn.sparketl.name", "sparketl").getValue();

    private SparkEtlDevelopmentStandard developmentStandard;

    @Override
    protected void initialize() {
        developmentStandard = new SparkEtlDevelopmentStandard();
    }

    @Override
    public DevelopmentIntegrationStandard getOrCreateDevelopmentStandard() {
        return developmentStandard;
    }




}
