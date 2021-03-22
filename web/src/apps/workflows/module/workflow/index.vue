<template>
  <div class="page-bgc">
    <div v-if="!dataMapModule">
      <template v-if="dataList.length > 0">
        <project-content-item
          v-for="item in dataList"
          :key="item.id"
          :hide-publish-andcopy="false"
          :current-data="item"
          :data-list="item.dwsFlowList"
          :applicationAreaMap="applicationAreaMap"
          :publishingList="publishingList"
          tag-prop="uses"
          @goto="gotoWorkflow"
          @detail="versionDetail"
          @modify="projectModify"
          @delete="deleteWorkflow"
          @publish="orchestratorPublish"
          @Export="orchestraorExport">
          <div class="workflow-header-search">
            <div>
              <Button class="button-text" type="primary" @click="addProject" v-if="checkEditable">
                <Icon :size="20" type="ios-add"></Icon>
                {{$t('message.orchestratorModes.createOrchestrator')}}
              </Button>
              <Button class="button-text" style="margin-left: 20px" type="primary" @click="orchestratorImport">
                {{$t('message.orchestratorModes.importOrchestrator')}}
              </Button>
            </div>
            <Input
              search
              class="search-input"
              :placeholder="$t('message.workflow.searchWorkflow')"
              @on-change="searchWorkflow($event, item.id)"/>
            <!-- 地图模式先去掉 -->
            <!-- <Button type="primary" @click="dataMapModule = !dataMapModule" v-if="checkEditable">
              <Icon type="md-repeat"></Icon>
              {{dataMapModule ? $t('message.workflow.narmalModel') : $t('message.workflow.mapModel')}}
            </Button>
            <Button type="primary" @click="gotoBack">
              <Icon type="md-arrow-round-back"></Icon>
              {{$t('message.workflow.backItem')}}
            </Button> -->
          </div>
          <slot name="tagList"></slot>
        </project-content-item>
      </template>
      <template v-else>
        <div class="no-data">
          <img
            class="no-data-img"
            src="../../../../dss/assets/images/no-data.svg"
            :alt="$t('message.workflow.searchWorkflow')" />
          <div>{{$t('message.workflow.searchWorkflow')}}</div>
        </div>
      </template>
    </div>
    <div class="process-bar" v-else>
      <dataMap
        ref="dataMap"
        :dataMapModuleValue="dataMapModule"
        @openWorkflow="openWorkflow"
        @addFlow="addProject"
        @changeMode="changeMode"
      ></dataMap>
    </div>
    <Modal
      v-model="deleteProjectShow"
      :title="$t('message.orchestratorModes.deleteOrchestrator')"
      @on-ok="deleteProjectConfirm">{{`${$t('message.orchestratorModes.confirmDeleteOrchestrator')}${deleteProjectItem.orchestratorName} ？`}}</Modal>
    <WorkflowForm
      ref="workflowForm"
      :workflow-data="currentOrchetratorData"
      :classify-list="cacheData"
      :add-project-show="ProjectShow"
      :action-type="actionType"
      :projectData="projectData"
      :orchestratorModeList="orchestratorModeList"
      :selectOrchestratorList="selectOrchestratorList"
      @show="ProjectShowAction"
      @confirm="ProjectConfirm"></WorkflowForm>
    <VersionDetail
      :version-detail-show="versionDetailShow"
      :version-data="versionData"
      @versionDetailShow="versionDetailAction"
      @goto="versionGotoWorkflow"></VersionDetail>
    <Modal
      v-model="publishShow"
      :title="$t('message.orchestratorModes.orchestratorPublish')"
      @on-ok="commonComfirm"
      @on-cancel="commonCancel"
    >
      <publishComponent
        ref="publishForm"
        :currentData="currentOrchetratorData"
        @removeRuning="removeRuning"
        @publishSuccess="$emit('publishSuccess', currentOrchetratorData)"></publishComponent>

    </Modal>
    <!-- 导入弹窗 -->
    <Modal
      v-model="importModal"
      :footer-hide="true">
      <div
        class="process-module-title"
        slot="header">
        {{$t('message.orchestratorModes.importOrchestrator')}}
      </div>
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
        <div style="padding: 20px 0">
          <Icon
            type="ios-cloud-upload"
            size="52"
            style="color: #3399ff"></Icon>
          <p>{{ $t('message.orchestratorModes.clickOrDragFile') }}</p>
        </div>
      </Upload>

    </Modal>
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import commonModule from '@/apps/workflows/module/common';
import WorkflowForm from './module/workflowForm.vue';
import VersionDetail from './module/versionDetail.vue';
import storage from '@/common/helper/storage';
import api from '@/common/service/api';
import pinyin from 'pinyin';
import dataMap from './module/dataMap.vue';
import publishComponent from './module/publishForm.vue';
import { GetAreaMap } from '@/common/service/apiCommonMethod.js';
import axios from 'axios';
export default {
  props: {
    projectData: {
      type: Object,
      default: () => {}
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
    }
  },
  components: {
    projectContentItem: commonModule.component.workflowContentItem,
    WorkflowForm,
    VersionDetail,
    dataMap,
    publishComponent
  },
  data() {
    return {
      ProjectShow: false, // 添加工作流展示
      versionDetailShow: false,
      deleteProjectShow: false, // 删除工作流弹窗展示
      deleteClassifyItem: '',
      deleteProjectItem: '', // 删除的工作流项
      isRootFlow: false,
      actionType: '', // add || modify
      loading: false,
      projectClassify: {
        name: '',
        descipte: '',
      },
      dataList: [
        {
          id: 1,
          name: 'My workflow',
          dwsFlowList: []
        }
      ], // 右侧数据过滤后的
      cacheData: [], // 工作流初始树结构，用于搜索过滤
      params: '',
      versionData: [], // 工作流版本信息
      flowTaxonomyID: 0, // 工作流分类的id
      currentOrchetratorData: null,
      dataMapModule: false, // 是否地图模式
      applicationAreaMap: [],
      publishShow: false, // 发布弹窗
      publishingList: [], // 发布中的编排，只记录ID
      importModal: false,
      uploadUrl: ``,
      uploadData: null
    };
  },
  computed: {
    checkEditable() {
      if (this.projectData.editUsers && this.projectData.editUsers.length > 0) {
        return this.projectData.editUsers.some(e => e === this.getUserName());
      } else {
        return false;
      }
    },
  },
  watch: {
    isRootFlow(val) {
      this.getParams(val);
      this.getFlowData(this.params);
    },
    '$route'() {
      this.fetchFlowData();
    },
    currentMode(val) {
      if (val) {
        this.fetchFlowData();
      }
    },
    publishingList(val) {//监听发布中的编排
      storage.set('runningPublish', val);
    }
  },
  created() {
    this.fetchFlowData();
    GetAreaMap().then(res => {
      this.applicationAreaMap = res.applicationAreas;
    });
  },
  mounted() {
    this.$nextTick(() => {
      this.publishingList = storage.get('runningPublish') || [];
    });
  },
  methods: {
    changeMode(val) {
      this.dataMapModule = val;
    },
    fetchFlowData() {
      this.getParams();
      this.getFlowData(this.params);
    },
    // 获取工作流的参数
    getParams() {
      this.params = {
        workspaceId: this.$route.query.workspaceId,
        projectId: this.$route.query.projectID,
        orchestratorMode: this.currentMode
      }
    },
    // 获取所有分类和工作流
    getFlowData(params = {}) {
      this.loading = true;
      api.fetch(`${this.$API_PATH.PROJECT_PATH}getAllOrchestrator`, params, 'post').then((res) => {
        this.dataList[0].dwsFlowList = res.page;
        this.cacheData = this.dataList;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 新增工作流
    addProject() {
      this.ProjectShow = true;
      this.actionType = 'add';
      this.init();
    },
    // 确认新增工作流
    ProjectConfirm(orchestratorData) {
      orchestratorData.dssLabels = [this.getCurrentDsslabels()];
      if (this.checkName(this.dataList[0].dwsFlowList, orchestratorData.orchestratorName, orchestratorData.id)) return this.$Message.warning(this.$t('message.workflow.nameUnrepeatable'));
      this.loading = true;
      if (this.actionType === 'add') {
        api.fetch(`${this.$API_PATH.PROJECT_PATH}createOrchestrator`, orchestratorData, 'post').then(() => {
          this.$Message.success(this.$t('message.workflow.createdSuccess'));
          this.getParams();
          this.getFlowData(this.params);
        }).catch(() => {
          this.loading = false;
        });
      } else {
        const {workspaceId, projectId, orchestratorName, orchestratorMode, orchestratorWays, id, description, uses} = orchestratorData
        api.fetch(`${this.$API_PATH.PROJECT_PATH}modifyOrchestrator`, {workspaceId, projectId, orchestratorName, orchestratorMode, orchestratorWays, id, description, uses, dssLabels: [this.getCurrentDsslabels()]}, 'post').then(() => {
          this.loading = false;
          this.$Message.success(this.$t('message.workflow.eitorSuccess', { name: orchestratorData.orchestratorName }));
          this.getParams();
          this.getFlowData(this.params);
        }).catch(() => {
          this.loading = false;
          this.currentOrchetratorData.uses = this.$refs.workflowForm.originBusiness;
        });
      }
    },
    // 删除单项工作流
    deleteWorkflow(params) {
      this.deleteProjectShow = true;
      this.deleteProjectItem = params;
    },
    // 确认删除单项工作流
    deleteProjectConfirm() {
      // if (!this.deleteProjectItem.rootFlow) return this.$Message.error(this.$t('message.workflow.notDelete'));
      // 调用删除接口
      this.loading = true;
      const params = {
        id: this.deleteProjectItem.id,
        projectId: this.deleteProjectItem.projectId,
        workspaceId: this.deleteProjectItem.workspaceId,
        dssLabels: [this.getCurrentDsslabels()]
      };
      api.fetch(`${this.$API_PATH.PROJECT_PATH}deleteOrchestrator`, params, 'post').then((res) => {
        this.loading = false;
        if (res.warmMsg) {
          this.$Modal.confirm({
            title: this.$t('message.workflow.mandatoryDelete'),
            content: res.warmMsg,
            onOk: () => {
              params.sure = true;
              this.loading = true;
              api.fetch('/dss/deleteProject', params, 'post').then(() => {
                this.$Message.success(this.$t('message.workflow.deleteSuccessName', { name: this.deleteProjectItem.name }));
                this.getParams();
                this.getFlowData(this.params);
              }).catch(() => {
                this.loading = false;
              });
            },
          });
        } else {
          this.$Message.success(this.$t('message.workflow.deleteSuccessName', { name: this.deleteProjectItem.name }));
          this.getParams();
          this.getFlowData(this.params);
        }
      }).catch(() => {
        this.loading = false;
      });
    },
    init() {
      this.currentOrchetratorData = {
        orchestratorName: '',
        description: '',
        uses: '',
        orchestratorMode: '',
        orchestratorWays: null,
        projectId: this.$route.query.projectID,
        workspaceId: this.$route.query.workspaceId
      };
    },
    // 修改编排
    projectModify(classifyId, project) {
      this.init();
      this.ProjectShow = true;
      this.actionType = 'modify';
      this.currentOrchetratorData = project;
      this.currentOrchetratorData.taxonomyID = classifyId;
    },
    ProjectShowAction(val) {
      this.ProjectShow = val;
    },
    // 数据地图双击进入
    openWorkflow(node) {
      this.$emit('open-workflow', {
        ...this.$route.query,
        id: node.id,
        name: node.title,
        version: '',
        priv: node.priv
      })
    },
    // 点击工程跳转到工作流编辑
    gotoWorkflow(subItem) {
      // 这里的一定是最新版本
      this.$emit('open-workflow', {
        ...this.$route.query,
        id: subItem.orchestratorId,
        name: subItem.orchestratorName,
        version: String(subItem.orchestratorVersionId),
        orchestratorMode: subItem.orchestratorMode,
        priv: subItem.priv // 权限字段
      })
    },
    // 查看版本详情
    versionDetail(classifyId, project) {
      this.versionDetailShow = true;
      this.currentOrchetratorData = project;
      const prams = {
        orchestratorId: project.orchestratorId,
      };
      api.fetch(`${this.$API_PATH.ORCHESTRATOR_PATH}getVersionByOrchestratorId`, prams, 'post').then((res) => {
        // 新加tab功能需要工作流名称
        this.versionData = res.list;
      });
    },
    versionDetailAction(val) {
      this.versionDetailShow = val;
    },
    searchWorkflow(event, id) {
      let tepArray = JSON.parse(JSON.stringify(this.cacheData));
      if (event.target.value) {
        this.dataList = tepArray.map((item) => {
          if (id === item.id) {
            item.dwsFlowList = item.dwsFlowList.filter((subItem) => {
              return subItem.orchestratorName.indexOf(event.target.value) != -1;
            });
          }
          return item;
        })
      } else {
        this.dataList = tepArray;
      }
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
    // 点击版本的打开跳转工作流编辑
    versionGotoWorkflow(row) {
      console.log(row, 'row')
      this.$emit('open-workflow',
        {
          ...this.$route.query,
          id: row.orchestratorId,
          version: String(row.id), // 在编排列表返回的是版本id
          name: row._index === 0 ? `${this.currentOrchetratorData.orchestratorName}` : `${this.currentOrchetratorData.orchestratorName}(${this.$t('message.workflow.historicalVersion')})`,
          priv: this.currentOrchetratorData.priv,
          orchestratorMode: this.currentOrchetratorData.orchestratorMode,
          readonly: row._index === 0 ? 'false' : 'true',
          appId: row.appId,
          orchestratorVersionId: row.id
        });
    },
    goBack() {
      this.$router.go(-1);
    },
    charCompare(charA, charB) {
      // 谁为非法值谁在前面
      if (charA === undefined || charA === null || charA === '' || charA === ' ' || charA === '　') {
        return -1;
      }
      if (charB === undefined || charB === null || charB === '' || charB === ' ' || charB === '　') {
        return 1;
      }
      if (!this.notChinese(charA)) {
        charA = pinyin(charA)[0][0];
      }
      if (!this.notChinese(charB)) {
        charB = pinyin(charB)[0][0];
      }
      return charA.localeCompare(charB);
    },
    notChinese(char) {
      const charCode = char.charCodeAt(0);
      return charCode >= 0 && charCode <= 128;
    },
    // 退出到合作项目页
    gotoBack() {
      let workspaceId = this.$route.query.workspaceId;
      this.$router.push({
        path: `/workspaceHome?workspaceId=${workspaceId}`,
      })
    },
    // 编排的发布
    orchestratorPublish(currentData) {
      // 一个工程只能同时一个发布编排
      if(this.publishingList.length > 0) return this.$Message.warning(this.$t('message.orchestratorModes.publishLoading'))
      this.publishShow = true;
      this.currentOrchetratorData = currentData;
    },
    // 编排的导出
    orchestraorExport(orchestrator){
      const params = {
        workspaceId: +this.$route.query.workspaceId,
        projectId: +this.$route.query.projectID,
        projectName: this.$route.query.projectName,
        orchestratorId: orchestrator.orchestratorId,
        orcVersionId: orchestrator.orchestratorVersionId,
        addOrcVersion: false,
        dssLabels: this.getCurrentDsslabels()
      };
      // 返回结构不一样
      axios.get(`http://${window.location.host}/api/rest_j/v1/dss/framework/orchestrator/exportOrchestrator`,{
        params,
        responseType: 'arraybuffer'
      }).then((res) => {
        const blob = new Blob([res.data], { type: 'application/zip' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.style.display = 'none'
        a.href = url
        a.setAttribute('download', `${this.$route.query.projectName}_${orchestrator.orchestratorName}`)
        const evObj = document.createEvent('MouseEvents');
        evObj.initMouseEvent('click', true, true, window, 0, 0, 0, 0, 0, false, false, true, false, 0, null);
        const flag = a.dispatchEvent(evObj);
        this.$nextTick(() => {
          if (flag) {
            this.$Message.success(this.$t('message.orchestratorModes.exportSuccess'));
          }
        });
      }).catch(() => {
        this.$Message.error(this.$t('message.orchestratorModes.exportFail'))
      })
    },
    // 发布确认
    commonComfirm() {
      // 记录要发布的编排的id
      if (!this.publishingList.includes(this.currentOrchetratorData.orchestratorId)) {
        this.publishingList.push(this.currentOrchetratorData.orchestratorId)
      }
      this.$refs.publishForm.publish();
    },
    // 发布取消
    commonCancel() {

    },
    // 清除发布结束的编排
    removeRuning(id) {
      this.publishingList = this.publishingList.filter((i) => i !== id);
    },
    orchestratorImport() {
      this.uploadUrl = `/api/rest_j/v1/dss/framework/orchestrator/importOrchestratorFile?labels=dev`
      this.uploadData = {
        projectName: this.$route.query.projectName,
        projectID: +this.$route.query.projectID,
        dssLabels: 'dev'
      }
      this.importModal = true;
      this.$refs.uploadJson.clearFiles();
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

        this.getFlowData(this.params);
      }
      this.importModal = false;
    },
    handleError() {
      this.$Message.error(this.$t('message.orchestratorModes.importFail'));
      this.importModal = false;
    }
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
