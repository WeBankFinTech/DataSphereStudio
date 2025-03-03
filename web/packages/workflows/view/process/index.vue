<template>
  <process v-if="query.appId" :query="query"></process>
  <Spin v-else fix></Spin>
</template>
<script>
import process from '@/workflows/module/process';
import api from '@dataspherestudio/shared/common/service/api';

// 工作流编辑区单独页面

export default {
  components: {
    process: process.component,
  },
  data() {
    return {
      query: {}
    };
  },
  methods: {
    openOrchestrator(params) {
      api
        .fetch(
          `${this.$API_PATH.ORCHESTRATOR_PATH}openOrchestrator`,
          {
            orchestratorId: params.orchestratorId,
            labels: { route: params.label },
            workspaceName: params.workspaceName,
          },
          "post"
        )
        .then((openOrchestrator) => {
          let paramsquery = {};
          if (openOrchestrator) {
            paramsquery = {
              appId: openOrchestrator.OrchestratorVo.dssOrchestratorVersion.appId,
              version: openOrchestrator.OrchestratorVo.dssOrchestratorVersion.id,
              orchestratorVersionId: openOrchestrator.OrchestratorVo.dssOrchestratorVersion.id
            }
            this.query = {
              ...paramsquery,
              ...params
            }
          } else {
            return this.$Message.warning(
              this.$t("message.orchestratorModes.FailedOpenOrchestrator")
            );
          }
        });
    },
    getAllOrchestrator() {
      return api
        .fetch(
          `${this.$API_PATH.ORCHESTRATOR_PATH}getAllOrchestrator`,
          {
            workspaceId: this.$route.query.workspaceId,
            orchestratorMode: this.$route.query.orchestratorMode || 'pom_work_flow',
            projectId: this.$route.query.projectID,
          },
          "post"
        )
        .then((res) => {
          const flow = res.page.find((f) => {
            return f.orchestratorId == this.$route.query.orchestratorId;
          });
          return flow
        });
    }
  },
  async mounted() {
    // query参数
    // noHeader=1
    // label=dev
    // workspaceId=271
    // projectName=copy_128
    // projectID=3742
    // orchestratorId=10684

    // http://sit.dss.bdp.weoa.com/#/process?workspaceId=271&projectID=3742&projectName=copy_128&orchestratorId=10684&noHeader=1&label=dev
    let flow = {}
    try {
      flow = await this.getAllOrchestrator() || {};
    } catch (error) {
      //
    }
    this.openOrchestrator({
      ...this.$route.query,
      editable: flow.editable,
      releaseable: flow.releaseable
    });
  }
};
</script>
