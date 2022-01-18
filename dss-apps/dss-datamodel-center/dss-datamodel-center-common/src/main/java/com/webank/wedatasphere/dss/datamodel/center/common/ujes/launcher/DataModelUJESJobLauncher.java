package com.webank.wedatasphere.dss.datamodel.center.common.ujes.launcher;


import com.webank.wedatasphere.dss.datamodel.center.common.ujes.task.DataModelUJESJobTask;

public interface DataModelUJESJobLauncher<E> {


    /**
     *
     * @param task
     * @return
     */
     E launch(DataModelUJESJobTask task);

}
