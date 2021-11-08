package com.webank.wedatasphere.dss.datamodel;


import com.webank.wedatasphere.linkis.computation.client.LinkisJobClient;
import com.webank.wedatasphere.linkis.computation.client.ResultSetIterator;
import com.webank.wedatasphere.linkis.computation.client.interactive.SubmittableInteractiveJob;

public class TestJobTask {
    public static void main(String[] args) {
        // TODO First, set the right gateway url.
        LinkisJobClient.config().setDefaultServerUrl("http://121.36.12.247:8088");
        //TODO Secondly, please modify the executeUser
        SubmittableInteractiveJob job = LinkisJobClient.interactive().builder()
                .setEngineType("hive-2.3.3").setRunTypeStr("hql")
                .setCode("select * from linkis_db.linkis_partitions limit 1")
                .addExecuteUser("hdfs")
                .setCreator("hdfs")
                .build();
        // 3. Submit Job to Linkis
        job.submit();
//        System.out.println("execId: " + job.getJobSubmitResult().getExecID() + ", taskId: " + job.getJobSubmitResult().taskID());
//        System.out.println("ResponseBody:"+job.getJobSubmitResult().getResponseBody());

//        // 4. Wait for Job completed
        job.waitForCompleted();

//        // 5. Get results from iterators.
        ResultSetIterator iterator = job.getResultSetIterables()[0].iterator();
        System.out.println(iterator.getMetadata());
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }
}
