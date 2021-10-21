package com.webank.wedatasphere.dss.datamodel.center.common.ujes;


public interface DataModelUJESJobLauncher<E> {


    /**
     *
     * @param task
     * @return
     */
     E launch(DataModelUJESJobTask task);

}
