<template>
  <!--新增弹窗-->
  <Modal
    v-model="mergeModalShow"
    @on-cancel="ProjectMergeCancel"
    :footer-hide="true"
    :title="$t('message.workflow.Addworkflow')"
  >
    <WorkflowFormNew
      v-if="mergeModalShow"
      :workflow-data="currentOrchetratorData"
      :orchestratorModeList="orchestratorModeList"
      :selectOrchestratorList="selectOrchestratorList"
      :projectNameList="projectNameList"
      @cancel="ProjectMergeCancel"
      @confirm="ProjectMergeConfirm">
    </WorkflowFormNew>
  </Modal>
</template>
<script>
import WorkflowFormNew from './workflowFormNew.vue';
import api from '@dataspherestudio/shared/common/service/api';
export default {
  props: {
    currentTreeProject: {
      type: Object,
      default: null
    },
    treeModalShow: {
      type: Boolean,
      default: false,
    },
    orchestratorModeList: {
      type: Object,
      default: () => {}
    },
    currentMode: {
      type: String,
      default: null
    },
    selectOrchestratorList: {
      type: Array,
      default: () => []
    },
    projectNameList: {
      type: Array,
      default: () => []
    }
  },
  components: {
    WorkflowFormNew,
  },
  data() {
    return {
      mergeModalShow: this.treeModalShow, // 弹窗展示
      currentOrchetratorData: null
    };
  },
  watch: {
    currentTreeProject() {
      this.init();
      this.initUpload();
    },
    treeModalShow(val) {
      this.mergeModalShow = val;
    }
  },
  mounted() {
    this.init();
    this.initUpload();
  },
  methods: {
    ProjectMergeCancel() {
      this.mergeModalShow = false;
      this.$emit('on-tree-modal-cancel')
    },
    ProjectMergeConfirm({data, cb}) {
      data.dssLabels = [this.getCurrentDsslabels()];
      data.labels = { route: this.getCurrentDsslabels() };
      const curTemplateIds = data.templateIds;
      delete data.templateIds
      api.fetch(`${this.$API_PATH.ORCHESTRATOR_PATH}createOrchestrator`, data, 'post').then((res) => {
        this.$Message.success(this.$t('message.workflow.createdSuccess'));
        if (curTemplateIds) {
          const templateData = {
            projectId: data.projectId,
            orchestratorId: res.orchestratorId,
            templateIds: curTemplateIds,
          }
          api.fetch(
            `${this.$API_PATH.ORCHESTRATOR_PATH}saveTemplateRef`,
            templateData,
            "put"
          )
        }
        this.$emit('on-tree-modal-confirm', {
          id: data.projectId
        })
        if (cb) {
          cb()
        }
        this.init()
      })
        .catch(() => {
          if (cb) {
            cb()
          }
        });
    },
    init() {
      this.currentOrchetratorData = {
        orchestratorName: '',
        description: '',
        uses: '',
        orchestratorMode: '',
        orchestratorWays: null,
        projectId: String(this.currentTreeProject ? this.currentTreeProject.id : this.$route.query.projectID),
        workspaceId: this.$route.query.workspaceId,
        isDefaultReference: "0"
      };
    },
    initUpload() {
      // this.uploadUrl = `/api/rest_j/v1/dss/framework/orchestrator/importOrchestratorFile?labels=dev`
      // this.uploadData = {
      //   projectName: this.$route.query.projectName,
      //   projectId: this.currentTreeProject ? this.currentTreeProject.id : this.$route.query.projectID,
      //   dssLabels: 'dev'
      // }
      // this.$refs.uploadJson.clearFiles();
    },
    // 手动上传
    handleUpload(file) {
      if (file.name.indexOf('.zip') === -1) {
        this.$Message.warning(this.$t('message.orchestratorModes.selectZip'));
        return false;
      }
    },
    handleSuccess(response, file) {
      if (response.status === 0) {
        this.$Message.success(`${file.name} ${this.$t('message.orchestratorModes.importSuccess')}`);
        this.$emit('on-tree-modal-confirm', this.currentTreeProject)
      }
      this.ProjectMergeCancel();
    },
    handleError() {
      this.$Message.error(this.$t('message.orchestratorModes.importFail'));
      this.ProjectMergeCancel();
    }
  },
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.upload-box {
  padding: 120px 0;
  @include font-color(#515a6e, $dark-text-color);
}
</style>
