<template>
  <!--新增和导入 二合一弹窗-->
  <Modal
    v-model="mergeModalShow"
    @on-cancel="ProjectMergeCancel"
    :footer-hide="true"
    title="添加工作流"
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
    <!-- <Tab-pane label="导入工作流" name="upload">
        <Upload
          ref="uploadJson"
          type="drag"
          :data="uploadData"
          :before-upload="handleUpload"
          :on-success="handleSuccess"
          :on-error="handleError"
          :format="['zip']"
          :max-size="2001000"
          :action="uploadUrl">
          <div class="upload-box">
            <Icon
              type="ios-cloud-upload"
              size="52"
              style="color: #3399ff"></Icon>
            <p>{{ $t('message.orchestratorModes.clickOrDragFile') }}</p>
          </div>
        </Upload>
      </Tab-pane> -->
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
      mergeModalShow: this.treeModalShow, // 二合一弹窗展示
      currentOrchetratorData: null,
      uploadUrl: `/api/rest_j/v1/dss/framework/orchestrator/importOrchestratorFile?labels=dev`,
      uploadData: null
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
      api.fetch(`${this.$API_PATH.ORCHESTRATOR_PATH}createOrchestrator`, data, 'post').then(() => {
        this.$Message.success(this.$t('message.workflow.createdSuccess'));
        this.$emit('on-tree-modal-confirm', {
          id: data.projectId
        })
        if (cb) {
          cb()
        }
        this.init()
      })
    },
    init() {
      this.currentOrchetratorData = {
        orchestratorName: '',
        description: '',
        uses: '',
        orchestratorMode: '',
        orchestratorWays: null,
        projectId: String(this.currentTreeProject ? this.currentTreeProject.id : this.$route.query.projectID),
        workspaceId: this.$route.query.workspaceId
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
