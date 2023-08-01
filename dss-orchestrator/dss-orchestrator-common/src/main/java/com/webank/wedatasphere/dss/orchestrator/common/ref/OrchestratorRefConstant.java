package com.webank.wedatasphere.dss.orchestrator.common.ref;

/**
 * @author enjoyyin
 * @date 2022-03-10
 * @since 0.5.0
 */
public interface OrchestratorRefConstant {

    String DSS_ORCHESTRATOR_INFO_KEY = "dssOrchestratorInfo";

    /*************************************************************/
    /** These keys below are used by Orchestrator Framework,      **/
    /** so it is represented the orchestrator framework abstract defination. **/
    /*************************************************************/
    String ORCHESTRATOR_ID_KEY = "orchestratorId";
    String ORCHESTRATOR_VERSION_KEY = "orchestratorVersion";


    /*************************************************************/
    /** These keys below are used by the real implementations of Orchestrator, such as workflow,      **/
    /** which is a kind of realized orchestrator of DSS.
     * So it is represented the detail orchestrator. we use orchestration in head to distinct the difference of
     * the keys of orchestrator **/
    /*************************************************************/
    String ORCHESTRATION_ID_KEY = "orchestrationId";
    String ORCHESTRATION_CONTENT_KEY = "orchestrationContent";
    String ORCHESTRATION_PARAMCONF_TEMPLATEIDS_KEY = "orchestrationParamConfTemplateIds";
    String ORCHESTRATION_NAME = "orchestrationName";
    String ORCHESTRATION_DESCRIPTION = "orchestrationDescription";
    String ORCHESTRATION_USES = "orchestrationUses";
    String ORCHESTRATION_SCHEDULER_APP_CONN = "schedulerAppConnName";

    //workflow node suffix input by the user during workflow replication.
    String ORCHESTRATION_NODE_SUFFIX = "nodeSuffix";

}
