package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


import com.webank.wedatasphere.linkis.computation.client.ResultSetIterator;
import com.webank.wedatasphere.linkis.computation.client.interactive.SubmittableInteractiveJob;

public class DataExistsDataModelJobLauncher implements DataModelJobLauncher<Integer> {
    @Override
    public Integer callBack(SubmittableInteractiveJob job) {
        ResultSetIterator<?, ?> iterator = job.getResultSetIterables()[0].iterator();

        System.out.println(iterator.getMetadata());

        // 如果已数据返回1
        if (iterator.hasNext()) {
            return 1;
        }
        return 0;
    }
}
