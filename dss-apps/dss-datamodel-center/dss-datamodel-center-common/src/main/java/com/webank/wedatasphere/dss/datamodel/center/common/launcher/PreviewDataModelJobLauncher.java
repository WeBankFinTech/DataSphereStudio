package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


import org.apache.linkis.computation.client.ResultSetIterator;
import org.apache.linkis.computation.client.interactive.SubmittableInteractiveJob;
import scala.collection.Map;

import java.util.List;

public class PreviewDataModelJobLauncher implements DataModelJobLauncher<List<Map<String,Object>>>{

    @Override
    public List<Map<String, Object>> callBack(SubmittableInteractiveJob job) {
        ResultSetIterator<?, ?> iterator = job.getResultSetIterables()[0].iterator();

        return null;
    }
}
