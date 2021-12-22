<template>
  <!--新增和导入 二合一弹窗-->
  <Modal
    v-model="mergeModalShow"
    @on-cancel="ProjectMergeCancel"
    :footer-hide="true">
    <Tabs value="form">
      <Tab-pane label="新建编排" name="form">
        <WorkflowFormNew
          v-if="mergeModalShow"
          :workflow-data="currentOrchetratorData"
          :orchestratorModeList="orchestratorModeList"
          :selectOrchestratorList="selectOrchestratorList"
          :projectNameList="projectNameList"
          @cancel="ProjectMergeCancel"
          @confirm="ProjectMergeConfirm"></WorkflowFormNew>
      </Tab-pane>
      <!-- <Tab-pane label="导入编排" name="upload">
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
    </Tabs>
  </Modal>
</template>
<script>
import WorkflowFormNew from './workflowFormNew.vue';
import api from '@/common/service/api';
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
      flowList: [], // project下的所有工作流，用于checkName
      params: '',
      currentOrchetratorData: null,
      uploadUrl: `/api/rest_j/v1/dss/framework/orchestrator/importOrchestratorFile?labels=dev`,
      uploadData: null
    };
  },
  watch: {
    currentTreeProject(val) {
      this.init();
      this.initUpload();
      this.fetchFlowData();
    },
    treeModalShow(val) {
      this.mergeModalShow = val;
    },
    currentMode(val) {
      if (val) {
        this.fetchFlowData();
      }
    },
  },
  mounted() {
    this.init();
    this.initUpload();
    this.fetchFlowData();
  },
  methods: {
    fetchFlowData() {
      this.getParams();
      this.getFlowData(this.params);
    },
    // 获取工作流的参数
    getParams() {
      this.params = {
        workspaceId: this.$route.query.workspaceId,
        projectId: this.currentTreeProject ? this.currentTreeProject.id : this.$route.query.projectID,
        orchestratorMode: this.currentMode
      }
    },
    // 获取所有分类和工作流
    getFlowData(params = {}) {
      if (!params.projectId) return
      api.fetch(`${this.$API_PATH.PROJECT_PATH}getAllOrchestrator`, params, 'post').then((res) => {
        this.flowList = res.page;
      })
    },
    ProjectMergeCancel() {
      this.mergeModalShow = false;
      this.$emit('on-tree-modal-cancel')
    },
    ProjectMergeConfirm(orchestratorData) {
      orchestratorData.dssLabels = [this.getCurrentDsslabels()];
      orchestratorData.labels = { route: this.getCurrentDsslabels() };
      if (this.checkName(this.flowList, orchestratorData.orchestratorName, orchestratorData.id)) return this.$Message.warning(this.$t('message.workflow.nameUnrepeatable'));
      api.fetch(`${this.$API_PATH.PROJECT_PATH}createOrchestrator`, orchestratorData, 'post').then(() => {
        this.$Message.success(this.$t('message.workflow.createdSuccess'));
        this.$emit('on-tree-modal-confirm', this.currentTreeProject)
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
    // 分类重名检查
    checkName(data, name, id) {
      let flag = false;
      data.map((item) => {
        if (item.orchestratorName === name && item.id !== id) {
          flag = true;
        }
      });
      return flag;
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
@import "@/common/style/variables.scss";
.upload-box {
  padding: 120px 0;
  @include font-color(#515a6e, $dark-text-color);
}
</style>
