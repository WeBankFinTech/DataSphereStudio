package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


import com.webank.wedatasphere.linkis.computation.client.ResultSetIterator;
import com.webank.wedatasphere.linkis.computation.client.interactive.SubmittableInteractiveJob;

public class CommonExchangisJobLauncher  implements ExchangisJobLauncher<SubmittableInteractiveJob>{

    @Override
    public SubmittableInteractiveJob callBack(SubmittableInteractiveJob job) {
        return job;
    }
}
