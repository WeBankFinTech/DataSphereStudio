<template>
  <div class="workflow-wrap">
    <div class="workflow-nav-tree" :class="{ 'tree-fold': treeFold }">
      <div class="workflow-nav-tree-switch" @click="handleTreeToggle">
        <span class="project-nav-tree-top-t-icon">
          <SvgIcon
            icon-class="dev_center_flod"
            style="opacity: 0.65"
          />
        </span>
      </div>
      <div class="project-nav-tree">
        <div class="project-nav-tree-top">
          <div class="project-nav-tree-top-t">
            <span class="project-nav-tree-top-t-txt">{{ $t('message.workflow.Project') }}</span>
            <div class="project-nav-tree-top-t-icon">
              <SvgIcon class="icon sort-icon" icon-class="xinzeng" style="display: inline-flex;margin-top: 4px;" @click="createProject" />
              <Dropdown class="sort-icon" @on-click="filerSort($event,'sort')">
                <SvgIcon class="icon" :icon-class="filterBar.sort ==='name' ? 'text-sort' : 'down'" style="display: inline-flex;font-size:14px"/>
                <DropdownMenu slot="list">
                  <DropdownItem
                    v-for="(item) in sortTypeList"
                    :name="item.value"
                    :key="item.value"
                  >{{ item.lable}}</DropdownItem>
                </DropdownMenu>
              </Dropdown>
              <Dropdown class="projcat-icon" @on-click="filerSort($event,'filter')">
                <Icon :type="{all: 'ios-funnel-outline',owner:'ios-person-add',share:'md-share'}[filterBar.cat] " class="icon" size="16"></Icon>
                <DropdownMenu slot="list">
                  <DropdownItem
                    name="all"
                    key="all"
                  >{{ $t('message.workflow.AllProj') }}</DropdownItem>
                  <DropdownItem
                    name="owner"
                    key="owner"
                  >{{ $t('message.workflow.Individual') }}</DropdownItem>
                  <DropdownItem
                    name="share"
                    key="share"
                  >{{ $t('message.workflow.ShareProj') }}</DropdownItem>
                </DropdownMenu>
              </Dropdown>
              <SvgIcon
                icon-class="dev_center_flod"
                class="icon"
                style="opacity: 0.65;margin-top: 2px;"
                @click="handleTreeToggle"
              />
            </div>
          </div>
        </div>
        <virtual-tree
          class="tree-container"
          keyText="id"
          :size="32"
          :list="projectsTree"
          :render="renderNode"
          :open="openNode"
          :height="height - 30"
          @we-open-node="openNodeChange"
          @we-click="handleTreeClick" />
        <Spin v-show="loadingTree" size="large" fix />
      </div>
      <WorkflowModal
        :treeModalShow="treeModalShow"
        :currentTreeProject="currentTreeProject"
        :orchestratorModeList="orchestratorModeList"
        :currentMode="currentMode"
        :selectOrchestratorList="selectOrchestratorList"
        :projectNameList="formatProjectNameList"
        @on-tree-modal-cancel="handleTreeModalCancel"
        @on-tree-modal-confirm="handleTreeModalConfirm"
      >
      </WorkflowModal>
    </div>

    <WorkflowTabList
      :class="{ 'tree-fold': treeFold }"
      :treeFold="treeFold"
      :loading="loading"
      :modeOfKey="modeOfKey"
      :buttonText="selectDevprocess"
      :bottomTapList="tabList"
      :modeName="modeName"
      :currentTab="current"
      @bandleTapTab="onTabClick"
      @handleTabRemove="onTabRemove"
      @handleChangeButton="handleChangeButton"
    >
      <template v-if="modeOfKey === DEVPROCESS.DEVELOPMENTCENTER">
        <div
          class="workflowListContainer"
          v-show="(tabList.length < 1 && projectsTree.length > 0)"
        >
          <Workflow
            class="workflowListLeft"
            ref="workflow"
            :refreshFlow="refreshFlow"
            :projectData="currentProjectData"
            :orchestratorModeList="orchestratorModeList"
            :currentMode="currentMode"
            :selectOrchestratorList="selectOrchestratorList"
            :projectsTree="projectsTree"
            :top-tab-list="tabList"
            :create-project-handler="createProject"
            @open-workflow="openWorkflow"
            @delete-workflow="deleteWorkflow"
            @rollback="onRollBack"
            @on-tree-modal-confirm="handleTreeModalConfirm"
          >
          </Workflow>
        </div>

        <template
          v-for="(item, index) in tabList.filter(
            (i) => i.type === DEVPROCESS.DEVELOPMENTCENTER
          )"
        >
          <commonIframe
            v-if="item.url && item.isIframe"
            v-show="item.tabId === current.tabId"
            :key="item.name"
            :url="current.url"
          ></commonIframe>
          <process
            v-else-if="item.orchestratorMode.startsWith(ORCHESTRATORMODES.WORKFLOW) && item.tabId === current.tabId"
            :key="item.tabId"
            :query="item.query"
            @updateWorkflowList="updateWorkflowList"
            @isChange="isChange(index, arguments)"
            @close="onTabRemove(item.tabId, false)"
            @open="reopen(item)"
            @release="(p)=>{openItemAction({...p, name: `${item.name}(${$t('message.workflow.historicalVersion')})`})}"
          ></process>
          <template v-else>
            <!-- 其他模式 -->
          </template>
        </template>
      </template>
      <template v-if="modeOfKey === 'streamis_prod'">
        <Streamis class="streamisContainer" :project-name="$route.query.projectName"/>
      </template>
      <template v-else>
        <!-- 其他应用流程 -->
      </template>
    </WorkflowTabList>
    <ProjectForm
      ref="projectForm"
      :action-type="actionType"
      :project-data="currentProjectData"
      :applicationAreaMap="applicationAreaMap"
      :classify-list="cacheData"
      :framework="true"
      :workspace-users="workspaceUsers"
      :orchestratorModeList="orchestratorModeList"
      @getDevProcessData="getDevProcessData"
      @confirm="ProjectConfirm"
    ></ProjectForm>

    <!-- 删除项目弹窗 -->
    <Modal
      v-model="deleteProjectShow"
      :title="$t('message.common.projectDetail.deleteProject')"
      @on-ok="deleteProjectConfirm"
    >
      {{ $t("message.common.projectDetail.confirmDeleteProject")
      }}{{ deleteProjectItem.name }}?
      <br />
      <br />
      <Checkbox v-model="ifDelOtherSys">{{ $t('message.workflow.Simultaneously') }}</Checkbox>
    </Modal>
    <!-- flow 基础属性 -->
    <Modal v-model="baseprop.show" :footer-hide="true" class="prop-modal" :title="$t('message.workflow.Essential')">
      <div class="prop-item">
        <span class="label-prop">  {{$t('message.workflow.CreateUser') }}</span> {{ baseprop.createUser }}
      </div>
      <div class="prop-item">
        <span class="label-prop"> {{$t('message.workflow.CreateTime') }}</span> {{ baseprop.createTime | formatDate }}
      </div>
      <div class="prop-item">
        <span class="label-prop"> {{$t('message.workflow.LastModifyUser') }}</span> {{ baseprop.updateUser }}
      </div>
      <div class="prop-item">
        <span class="label-prop"> {{$t('message.workflow.LastModifyTime') }}</span> {{ baseprop.updateTime | formatDate }}
      </div>
    </Modal>
    <!-- 复制工作流 -->
    <CopyModal v-model="showCopyForm" ref="copyForm" @finish="copySended" />
    <!-- 导入工作流 -->
    <ImportFlow v-model="importModal" ref="import" @finish="importSended" />
  </div>
</template>
<script>
import qs from 'qs';
import { debounce } from 'lodash';
import Workflow from '@/workflows/module/workflow';
import WorkflowModal from '@/workflows/module/workflow/module/workflowModal.vue';
import Process from '@/workflows/module/process';
import storage from '@dataspherestudio/shared/common/helper/storage';
import VirtualTree from '@dataspherestudio/shared/components/virtualTree';
import WorkflowTabList from '@/workflows/module/common/tabList/index.vue';
import ProjectForm from '@dataspherestudio/shared/components/projectForm/index.js';
import eventbus from "@dataspherestudio/shared/common/helper/eventbus";
import api from '@dataspherestudio/shared/common/service/api';
import { DEVPROCESS, ORCHESTRATORMODES } from '@dataspherestudio/shared/common/config/const.js';
import {
  GetWorkspaceUserList,
  GetDicSecondList,
  GetAreaMap,
} from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
import { setVirtualRoles } from '@dataspherestudio/shared/common/config/permissions.js';
import Streamis from '@/workflows/module/innerIframe';
import filters from '@dataspherestudio/shared/common/util/filters';
import CopyModal from './copyModal.vue'
import ImportFlow from './importModal.vue'

export default {
  components: {
    VirtualTree,
    WorkflowModal,
    Workflow: Workflow.component,
    process: Process.component,
    WorkflowTabList,
    ProjectForm,
    Streamis: Streamis.component,
    CopyModal,
    ImportFlow
  },
  data() {
    return {
      tabList: [],
      current: {},
      modeOfKey: "dev",
      modeName: this.$t('message.workflow.Development'),
      currentMode: null,
      applicationAreaMap: [],
      orchestratorModeList: {},
      currentProjectData: {
        name: "",
        description: "",
        business: "",
        applicationArea: "",
        product: "",
        editUsers: [],
        accessUsers: [],
        devProcessList: [],
        orchestratorModeList: [],
        releaseUsers: [],
      },
      selectOrchestratorList: [],
      devProcessBase: [],
      selectDevprocess: [],
      DEVPROCESS,
      ORCHESTRATORMODES,
      loading: false,
      loadingTree: false,
      projectsTree: [],
      treeFold: false,
      treeModalShow: false,
      currentTreeId: +this.$route.query.projectID, // tree中active节点
      currentTreeProject: null, // 点击哪个project的添加
      refreshFlow: false, // 左侧树添加工作流后通知右侧刷新
      deleteProjectShow: false, // 删除工程弹窗展示
      ifDelOtherSys: false,
      deleteProjectItem: "", // 删除的工程项
      actionType: "", // add || modify
      dataList: [
        {
          id: 1,
          name: this.$t("message.common.projectDetail.WCYDXM"),
          dwsProjectList: [],
        },
      ],
      cacheData: [],
      baseprop: {
        show: false
      },
      height: 500,
      openNode: {},
      filterBar: {
        sort: 'updateTime',
        cat: this.$route.query.viewState || 'all'
      },
      sortTypeList: [
        {
          lable: this.$t('message.common.projectDetail.sortUpdateTime'),
          value: 'updateTime'
        },
        {
          lable: this.$t('message.common.projectDetail.sortName'),
          value: 'name'
        }
      ],
      showCopyForm: false,
      uploadUrl: `/api/rest_j/v1/dss/framework/orchestrator/importOrchestratorFile?labels=dev`,
      uploadData: null,
      importModal: false,
      workspaceUsers: {
        accessUsers: [],
        releaseUsers: [],
        editUsers: []
      }
    }
  },
  filters,
  watch: {
    tabList(val) {
      this.updateTabList(val)
    },
    "$route.query"(v) {
      this.tabList = [];
      this.getAreaMap();
      this.getProjectData(()=>{
        this.updateBread();
        this.tryOpenWorkFlow();
      });
      if (v.projectID) {
        this.currentTreeId = +v.projectID;
        this.openNode = {
          ...this.openNode,
          [this.currentTreeId]: true
        }
      }
    }
  },
  created() {
    storage.set("currentDssLabels", this.modeOfKey);
    GetWorkspaceUserList({ workspaceId: +this.$route.query.workspaceId }).then(
      (res) => {
        this.workspaceUsers = res.users;
      }
    );
    this.getAreaMap();
    this.getDicSecondList();
  },
  mounted() {
    // 获取所有project展示tree
    this.getAllProjects((res) => {
      if (this.$route.query.projectID) {
        let index
        let it
        res.projects.find((item, idx) => {
          if (item.id == this.$route.query.projectID) {
            index = idx
            it = item
          }
        })
        if (it) {
          res.projects.splice(index, 1)
          res.projects.unshift(it)
        }
      }
      this.rawProjects = res;
      const projs = this.changeCatType(this.$route.query.viewState)
      this.setVirtualRolesForProj({projects: projs})
      this.updateBread();
      this.tryOpenWorkFlow();
    });
    // 获取该用户下可编辑的project展示tree
    this.getAllProjects(res => {
      this.rawUserProjects = res;
    }, { filterProject: true })
    if (this.$route.query.projectID) {
      this.currentTreeId = +this.$route.query.projectID;
      this.openNode = {
        ...this.openNode,
        [this.currentTreeId]: true
      }
    }
    this.height = this.$el.clientHeight
    window.addEventListener('resize', this.resize);
  },
  computed: {
    formatProjectNameList() {
      let res = [];
      if( this.projectsTree.length > 0 ) {
        this.projectsTree.forEach(item => {
          let name = item.name;
          let id = item.id + '';
          res.push({name, id})
        })
      }
      return res;
    }
  },
  methods: {
    createProject() {
      this.actionType = "add";
      this.currentProjectData = {
        name: "",
        description: "",
        business: "",
        applicationArea: "",
        product: "",
        editUsers: [],
        accessUsers: [],
        devProcessList: [],
        releaseUsers: [],
      };
      this.$refs.projectForm.showProject(this.currentProjectData, 'add')
    },
    handleTreeToggle() {
      this.treeFold = !this.treeFold;
      setTimeout(()=>{
        eventbus.emit('workflow.fold.left.tree');
      }, 800)
    },
    // 获取开发流程基本数据
    getDevProcessData(data) {
      this.devProcessBase = data;
      this.getSelectDevProcess();
    },
    // 获取当前选择的开发流程
    getSelectDevProcess() {
      this.selectDevprocess = this.devProcessBase
        ? this.devProcessBase.filter((item) =>
          this.currentProjectData.devProcessList.includes(item.dicValue)
        )
        : [];
      // 切换项目，若项目没有对应模式，自动切换第一个
      const checkHasMode = this.selectDevprocess.some(it => it.dicValue === this.modeOfKey)
      if (this.selectDevprocess.length > 0 && !checkHasMode) {
        this.handleChangeButton(this.selectDevprocess[0])
      }
    },
    // 获取编排列表
    getSelectOrchestratorList() {
      this.selectOrchestratorList = this.orchestratorModeList.list
        ? this.orchestratorModeList.list.filter((item) =>
          this.currentProjectData.orchestratorModeList.includes(item.dicKey)
        )
        : [];
      if (this.selectOrchestratorList.length > 0) {
        this.currentMode = this.selectOrchestratorList[0].dicKey;
      }
    },
    // 获取编排模式的基本信息
    getDicSecondList() {
      GetDicSecondList(this.$route.query.workspaceId).then((res) => {
        this.orchestratorModeList = res.list;
        this.getProjectData();
      });
    },
    // 获取工程的应用领域
    getAreaMap() {
      GetAreaMap().then((res) => {
        this.applicationAreaMap = res.applicationAreas;
      });
    },
    // 获取工程的数据
    async getProjectData(callback) {
      const data = await this.fetchProjectDataById()
      this.currentProjectData = data
      this.getSelectDevProcess();
      this.getSelectOrchestratorList();
      this.loading = false;
      if (callback) {
        callback()
      }
    },
    /**
     * 获取指定项目projectData
     */
    fetchProjectDataById(id) {
      return api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllProjects`,
          {
            workspaceId: +this.$route.query.workspaceId,
            id: id || +this.$route.query.projectID,
          },
          "post"
        )
        .then((res) => {
          const project = res.projects[0];
          setVirtualRoles(project, this.getUserName());
          const data = {
            ...res.projects[0],
            canWrite: project.canWrite(),
          };
          return data;
        })
    },
    // 获取所有project展示tree
    getAllProjects(callback, params) {
      this.loadingTree = true;
      api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllProjects`,
          {
            workspaceId: +this.$route.query.workspaceId,
            ...(params || {})
          },
          "post"
        )
        .then((res) => {
          this.loadingTree = false;
          callback(res);
        });
    },
    // 获取project下工作流
    getFlow(param, resolve) {
      if (this.modeOfKey == 'streamis_prod') {
        return resolve([]);
      }
      api
        .fetch(
          `${this.$API_PATH.ORCHESTRATOR_PATH}getAllOrchestrator`,
          {
            workspaceId: this.$route.query.workspaceId,
            orchestratorMode: this.currentMode,
            projectId: param.id,
          },
          "post"
        )
        .then((res) => {
          const flow = res.page.map((f) => {
            return {
              ...f,
              name: f.orchestratorName,
              projectId: param.id || f.projectId,
              projectName: param.name,
              type: "flow",
            };
          });
          resolve(flow);
        });
    },
    importSended(target, orchestratorId){
      this.getFlow({id: target.id, name: target.name}, (flows) => {
        this.reFreshTreeData({id: target.id, name: target.name}, flows)
        this.tabList = this.tabList.filter(item => {
          return item.query.orchestratorId !== orchestratorId
        }) 
      })
    },
    handleTreeModal(project) {
      this.treeModalShow = true;
      this.currentTreeProject = project;
    },
    handleTreeModalCancel() {
      this.treeModalShow = false;
    },
    handleTreeModalConfirm(param) {
      // tree弹窗添加成功后要更新tree，还要通知右侧workFlow刷新
      this.refreshFlow = false; // 复位
      api
        .fetch(
          `${this.$API_PATH.ORCHESTRATOR_PATH}getAllOrchestrator`,
          {
            workspaceId: this.$route.query.workspaceId,
            orchestratorMode: this.currentMode,
            projectId: param.id,
          },
          "post"
        )
        .then((res) => {
          const p = this.formatProjectNameList.find(item => item.id === param.id)
          const flow = res.page.map((f) => {
            return {
              ...f,
              name: f.orchestratorName,
              projectId: param.id || f.projectId,
              // 补充projectName，点击工作流切换project时使用
              projectName: param.name || p.name,
              type: "flow",
            };
          });
          this.reFreshTreeData(param, flow)
          this.handleTreeModalCancel();
          this.refreshFlow = true;
        });
    },
    openNodeChange(v) {
      this.openNode = {...v}
    },
    async handleTreeClick({item}) {
      const node = item
      // 切换到开发模式
      this.modeOfKey =
        this.selectDevprocess && this.selectDevprocess.length
          ? this.selectDevprocess[0].dicValue
          : DEVPROCESS.DEVELOPMENTCENTER;

      if ( node.type == "streamis_prod" ) {
        this.modeOfKey = "streamis_prod"
        if (node.id != this.$route.query.projectID) {
          // 跨工程，会监听projectID
          const query = {
            workspaceId: this.$route.query.workspaceId,
            projectID: node.id,
            projectName: node.name,
          };
          this.$router.replace({
            name: "Workflow",
            query,
          })
          this.updateBread();
        }
        return;
      }
      if (node && node.children.length < 1 && node.type === 'project') {
        this.currentTreeId = node.id;
        if (node.id != this.$route.query.projectID) {
          // 跨工程，会监听projectID
          const query = {
            workspaceId: this.$route.query.workspaceId,
            projectID: node.id,
            projectName: node.name,
          };
          this.$router.replace({
            name: "Workflow",
            query,
          })
          this.updateBread();
        }
      } else if (node && node.type === 'flow') {
        const { canContinue } = await this.openCheck({id: node.id, version: node.orchestratorVersionId, name: node.name})
        if (!canContinue) {
          return
        }
        this.currentTreeId = node.id;
        if (node.orchestratorId != this.$route.query.flowId) {
          const query = {
            workspaceId: this.$route.query.workspaceId,
            projectID: node.projectId,
            projectName: node.projectName, // getFlow时补充的
            flowId: node.orchestratorId, // 先切换project，然后尝试打开工作流
          };
            // 存储flow
          storage.set("clickFlowInTree", node);
          this.$router.replace({
            name: "Workflow",
            query,
          });
          this.updateBread();
        } else {
          this.openWorkflow({
            id: node.id,
            version: node.orchestratorVersionId + '',
            orchestratorVersionId: node.orchestratorVersionId,
            name: node.name,
            orchestratorMode: node.orchestratorMode,
            editable: node.editable,
            releasable: node.releasable,
            projectID: node.projectId,
            projectName: node.projectName,
            flowId: node.orchestratorId,
            orchestratorId: node.orchestratorId
          })
        }
      }
    },
    onDeleteProject(project) {
      this.deleteProjectShow = true;
      this.ifDelOtherSys = false;
      this.deleteProjectItem = project;
    },
    async onConfigProject(project) {
      if (project.id !== this.currentProjectData.id) {
        const data = await this.fetchProjectDataById(project.id)
        this.$refs.projectForm.showProject(data, 'edit')
      } else {
        this.$refs.projectForm.showProject(this.currentProjectData, 'edit')
      }
      this.actionType = "modify";
    },
    // 刪除單個項目
    deleteProjectConfirm() {
      // 调用删除接口
      const params = {
        id: this.deleteProjectItem.id,
        sure: false,
        ifDelOtherSys: this.ifDelOtherSys,
      };
      api
        .fetch(`${this.$API_PATH.PROJECT_PATH}deleteProject`, params, "post")
        .then((res) => {
          this.loading = false;
          if (res.warmMsg) {
            this.$Modal.confirm({
              title: this.$t("message.common.projectDetail.deleteTitle"),
              content: res.warmMsg,
              onOk: () => {
                params.sure = true;
                this.loading = true;
                api
                  .fetch(
                    `${this.$API_PATH.PROJECT_PATH}deleteProject`,
                    params,
                    "post"
                  )
                  .then(() => {
                    this.$Message.success(
                      `${this.$t(
                        "message.common.projectDetail.deleteProject"
                      )}${this.deleteProjectItem.name}${this.$t(
                        "message.workflow.success"
                      )}`
                    );
                    setTimeout(() => {
                      this.$router.go(0);
                    }, 1000);
                  })
                  .catch(() => {
                    this.loading = false;
                  });
              },
              onCancel: () => {},
            });
          } else {
            this.$Message.success(
              this.$t("message.Project.deleteSuccess", {
                name: this.deleteProjectItem.name,
              })
            );
            setTimeout(() => {
              this.$router.go(0);
            }, 1000);
          }
        })
        .catch(() => {
          this.loading = false;
        });
    },
    onConfigFlow(params) {
      this.$refs.workflow.projectModify(params.id, params);
    },
    onDeleteFlow(params) {
      this.$refs.workflow.deleteWorkflow(params);
    },
    updateTabList(val = []) {
      let workspaceId = this.$route.query.workspaceId
      let workFlowLists = JSON.parse(sessionStorage.getItem(`work_flow_lists_${workspaceId}`)) || [];
      if (workFlowLists.length > 10) {
        workFlowLists = workFlowLists.slice(0,10)
      }
      workFlowLists = workFlowLists.filter(item => !val.some(it => it.id === item.id))
      val.forEach( item => {
        if( workFlowLists.every(i => i.id !== item.id) ) {
          workFlowLists.unshift(item)
        } else {
          let _workFlowLists = workFlowLists.slice()
          let idx = workFlowLists.findIndex(i => i.id === item.id);
          let _item = _workFlowLists.splice(idx, 1)[0];
          _workFlowLists.unshift(_item)
          workFlowLists = _workFlowLists
        }
      })
      sessionStorage.setItem(`work_flow_lists_${workspaceId}`, JSON.stringify(workFlowLists))
    },
    onViewVersion(project) {
      this.$refs.workflow.versionDetail(project.id, project);
    },
    unlockFlow(params, confirmDelete = false) {
      const workspaceData = storage.get("currentWorkspace");
      api
        .fetch(
          `${this.$API_PATH.ORCHESTRATOR_PATH}unlockOrchestrator`,
          {
            id: params.orchestratorId,
            projectId: params.projectId,
            labels: { route: this.modeOfKey },
            workspaceId: workspaceData.id,
            confirmDelete
          },
          "post"
        )
        .then((rst) => {
          if (rst.lockOwner && !confirmDelete) {
            this.$Modal.confirm({
              title: this.$t('message.workflow.unlockflowtitle'),
              content: `
                <p>${this.$t('message.workflow.unlockflow')}: ${params.name}?</p>
                <p  style="margin-top: 10px;color:#ed4014">${this.$t('message.workflow.unlocktip', {name: rst && rst.lockOwner})}</p>
              `,
              okText: this.$t('message.workflow.unlock'),
              cancelText: this.$t('message.workflow.cancel'),
              onOk: () => {
                this.unlockFlow(params, true)
              },
              onCancel: () => {}
            })
          } else {
            this.$t('message.workflow.unlocksuccess')
          }
        })
    },
    exportWorkflow(node) {
      const params = {
        workspaceId: node.workspaceId,
        projectId: node.projectId,
        projectName: node.projectName,
        orchestratorId: node.orchestratorId,
        orcVersionId: node.orchestratorVersionId,
        addOrcVersion: false,
        dssLabels: this.modeOfKey,
        outputFileName: `${node.projectName}_${node.name}`,
        labels: {
          route: this.modeOfKey
        }
      };
      const qstring = qs.stringify(params)
      const url = `http://${window.location.host}/api/rest_j/v1/dss/framework/orchestrator/exportOrchestrator?${qstring}`;
      const a = document.createElement("a");
      a.style.display = "none";
      a.href = url;
      const evObj = document.createEvent("MouseEvents");
      evObj.initMouseEvent(
        "click",
        true,
        true,
        window,
        0,
        0,
        0,
        0,
        0,
        false,
        false,
        true,
        false,
        0,
        null
      );
      a.dispatchEvent(evObj);
      this.$Message.success(this.$t('message.workflow.downloadrequest'))
    },
    onBasepropShow(flow) {
      this.baseprop = {
        ...flow,
        show: true
      }
    },
    updateBread() {
      let workspaceId = this.$route.query.workspaceId
      // 当前项目缓存tab
      let curProjectCachedTab = JSON.parse(sessionStorage.getItem(`work_flow_lists_${workspaceId}`))
      if (this.tabList.length < 1 && curProjectCachedTab) {
        curProjectCachedTab = curProjectCachedTab.filter(item => {
          return item.query && (item.query.projectID || item.query.projectId) === +this.$route.query.projectID
        })
        if (curProjectCachedTab.length) {
          this.tabList = curProjectCachedTab
        }
      }
    },
    tryOpenWorkFlow() {
      // this.modeOfKey不能为空
      const cur = this.projectsTree.filter(item => item.id == this.$route.query.projectID)[0];
      if (this.modeOfKey) {
        if( this.$route.query && this.$route.query.flowId) {
          const flow = storage.get("clickFlowInTree");
          storage.remove("clickFlowInTree");
          if (flow && flow.orchestratorId && flow.orchestratorMode) {
            const param = {
              ...this.$route.query,
              id: flow.id,
              orchestratorId: +this.$route.query.flowId || flow.orchestratorId,
              name: flow.orchestratorName,
              version: flow.version || String(flow.orchestratorVersionId),
              orchestratorMode: flow.orchestratorMode,
              releasable: flow.releasable,
              editable: flow.editable,
            };
            this.openWorkflow(param);
            return
          } else {
            if (cur) {
              this.getFlow(cur, (flows) => {
                if (flows && this.$route.query.flowId) {
                  const flowItem = flows.find(item => item.orchestratorId == this.$route.query.flowId)
                  this.currentTreeId = +this.$route.query.flowId
                  if (flowItem) this.openWorkflow(flowItem)
                }
                this.reFreshTreeData(cur, flows)
              });
            }
          }
        } else {
          if (cur) {
            this.getFlow(cur, (flows) => {
              this.reFreshTreeData(cur, flows)
            });
          }
          if (this.tabList.length) {
            let tab = this.tabList[0];
            if (this.$route.query.flowId) {
              tab = this.tabList.find(item => item.query.flowId == this.$route.query.flowId)
            }
            if (tab) {
              this.onTabClick(tab.tabId);
            }
          }
        }
      }
    },
    reFreshTreeData(project, flows) {
      this.projectsTree = this.projectsTree.map(item => {
        if (item.id == project.id) {
          return {
            ...item,
            loaded: true,
            loading: false,
            opened: true,
            isLeaf: flows.length ? false : true,
            children: flows.length ? flows : []
          };
        } else {
          return item;
        }
      });
    },
    // 确认新增工程 || 确认修改
    ProjectConfirm(projectData, callback) {
      const project = projectData;
      setVirtualRoles(project, this.getUserName());
      this.currentProjectData = {
        ...projectData,
        canWrite: project.canWrite(),
      };
      this.getSelectDevProcess();
      this.getSelectOrchestratorList();
      projectData.workspaceId = +this.$route.query.workspaceId;
      this.loading = true;
      if (this.actionType === "add") {
        const project_params = {
          name: projectData.name,
          applicationArea: projectData.applicationArea,
          business: projectData.business,
          editUsers: projectData.editUsers,
          accessUsers: projectData.accessUsers,
          releaseUsers: projectData.releaseUsers,
          description: projectData.description,
          product: projectData.product,
          workspaceId: projectData.workspaceId,
          devProcessList: projectData.devProcessList,
          orchestratorModeList: projectData.orchestratorModeList,
        };
        api
          .fetch(
            `${this.$API_PATH.PROJECT_PATH}createProject`,
            project_params,
            "post"
          )
          .then(() => {
            typeof callback == "function" && callback(true);
            this.$Message.success(
              `${this.$t(
                "message.common.projectDetail.createProject"
              )}${this.$t("message.workflow.success")}`
            );
            setTimeout(() => {
              this.$router.go(0);
            }, 500);
          })
          .catch(() => {
            typeof callback == "function" && callback();
            this.loading = false;
          });
      } else {
        const projectParams = {
          name: projectData.name,
          id: projectData.id,
          applicationArea: projectData.applicationArea,
          business: projectData.business,
          editUsers: projectData.editUsers,
          accessUsers: projectData.accessUsers,
          releaseUsers: projectData.releaseUsers,
          description: projectData.description,
          product: projectData.product,
          workspaceId: projectData.workspaceId,
          devProcessList: projectData.devProcessList,
          orchestratorModeList: projectData.orchestratorModeList,
        };
        api
          .fetch(
            `${this.$API_PATH.PROJECT_PATH}modifyProject`,
            projectParams,
            "post"
          )
          .then(() => {
            typeof callback == "function" && callback(true);
            this.getProjectData();
            this.$Message.success(
              this.$t("message.common.projectDetail.eidtorProjectSuccess", {
                name: projectData.name,
              })
            );
          })
          .catch(() => {
            typeof callback == "function" && callback();
            this.loading = false;
            this.currentProjectData.business =
              this.$refs.projectForm.originBusiness;
          });
      }
    },
    reopen(it) {
      this.openWorkflow(it.query)
    },
    /**
     * 打开工作流查看并将工作流信息存入tab列表
     * parama 为打开工作流基本信息
     */
    async openWorkflow(params) {

      if (params.lastedNode) {
        const cur = this.projectsTree.filter(item => item.name == params.projectName)[0];
        this.getFlow(cur, (flows) => {
          this.reFreshTreeData(cur, flows)
        });
      }

      if (this.loading) return;
      const {isIn, canContinue } = await this.openCheck(params)
      // 判断当前是否可以打开
      if (!isIn && canContinue) {
        this.currentTreeId = params.id || undefined
        // 历史版本不需要调用接口，直接使用版本里的数据
        if (params.readonly && params.readonly === "true") {
          this.openItemAction(params);
        } else {
          // 打开新的编排先获取接口后在来判断是什么编排
          const workspaceData = storage.get("currentWorkspace");
          this.loading = true;
          api
            .fetch(
              `${this.$API_PATH.ORCHESTRATOR_PATH}openOrchestrator`,
              {
                orchestratorId: params.orchestratorId,
                labels: { route: this.modeOfKey },
                workspaceName: workspaceData.name,
              },
              "post"
            )
            .then((openOrchestrator) => {
              this.loading = false;
              if (openOrchestrator) {
                params.appId =
                  openOrchestrator.OrchestratorVo.dssOrchestratorVersion.appId; // 应用id，例如工作流id
                // 编排的版本id
                params.version = params.orchestratorVersionId =
                  openOrchestrator.OrchestratorVo.dssOrchestratorVersion.id;
                this.openItemAction(params);
              } else {
                return this.$Message.warning(
                  this.$t("message.orchestratorModes.FailedOpenOrchestrator")
                );
              }
            });
        }
      } else if(isIn) {
        const tabId = `${params.id}${params.version}`;
        this.onTabClick(tabId);
      }
    },
    async openCheck(params) {
      // 判断是否为相同编排的不同版本
      // 是否已经打开十个
      const isIn = this.tabList.find(
        (item) => item.tabId === `${params.id}${params.version}`
      );
      const res = { isIn: isIn, canContinue: true}
      if (!isIn) {
        if (this.tabList.length >= 10) {
          this.$Message.warning(
            this.$t("message.orchestratorModes.openTabTen")
          );
          res.canContinue = false
        }
      }
      return res
    },
    /**
     * 删除工作流后，已打开的tab干掉
     */
    deleteWorkflow(params) {
      this.tabList = this.tabList.filter(item => {
        return item.query.orchestratorId != params.orchestratorId
      })
      let workspaceId = this.$route.query.workspaceId
      let workFlowLists = JSON.parse(sessionStorage.getItem(`work_flow_lists_${workspaceId}`)) || [];
      workFlowLists = workFlowLists.filter(it => it.query.orchestratorId != params.orchestratorId)
      sessionStorage.setItem(`work_flow_lists_${workspaceId}`, JSON.stringify(workFlowLists))
      const cur = this.projectsTree.filter(item => item.id == params.projectId)[0];
      this.getFlow(cur, (flows) => {
        this.reFreshTreeData(cur, flows)
        if (this.current.tabId == `${params.id}${params.version||params.orchestratorVersionId}` && this.tabList.length) {
          this.onTabClick(this.tabList[0].tabId)
        }
      });
    },
    onRollBack(orchestratorId) {
      this.tabList = this.tabList.filter(item => {
        return item.query.orchestratorId != orchestratorId
      })
    },
    openItemAction(params) {
      this.current = {
        version: params.version, // 编排版本
        id: params.id, // 编排id
        name: params.name, // 用于tab显示
        query: params,
        tabId: `${params.id}${params.version}`, // 相同编排的不同版本
        isChange: false,
        orchestratorMode: params.orchestratorMode, // 后面根据具体的编排模式确认类型字段
        type: DEVPROCESS.DEVELOPMENTCENTER
      };
      const hasIn = this.tabList.find(it=>it.tabId === this.current.tabId)
      if(!hasIn){
        this.tabList.push(this.current);
      }
    },
    onTabRemove(tabId, del) {
      let index = "";
      const removeData = this.tabList.filter((item, i) => {
        if (item.tabId === tabId) {
          index = i;
          return true;
        }
      })[0];
      // 删除之前得判断改工作流是否改变
      const removeAction = () => {
        // 判断删除的是否是当前打开的
        if (removeData.tabId === this.current.tabId) {
          // 如果是当前打开则判断是否为最后一个，是就显示前一个，不是就显示后一个，如果没有则显示列表
          if (this.tabList.length > 1 && index < this.tabList.length - 1) {
            this.current = this.tabList[index + 1];
            this.currentTreeId = this.current.id;
          } else if ( this.tabList.length > 1 && index === this.tabList.length - 1) {
            this.current = this.tabList[index - 1];
            this.currentTreeId = this.current.id;
          } else {
            this.current = {};
            this.currentTreeId = undefined;
          }
        }
        this.tabList.splice(index, 1);
        if (del !== false) this.deleteEditLock(removeData.query.appId);
        let workspaceId = this.$route.query.workspaceId;
        let workFlowLists = JSON.parse(sessionStorage.getItem(`work_flow_lists_${workspaceId}`)) || [];
        workFlowLists = workFlowLists.filter(it=>it.tabId !== tabId);
        sessionStorage.setItem(`work_flow_lists_${workspaceId}`, JSON.stringify(workFlowLists))
      };
      if (this.tabList[index] && this.tabList[index].isChange) {
        this.$Modal.confirm({
          title: this.$t("message.workflow.process.index.GBTS"),
          content: this.$t("message.workflow.process.index.WBCSFGB"),
          onOk: () => {
            removeAction();
          },
        });
      } else {
        removeAction();
      }
    },
    // 切换开发流程
    handleChangeButton(item) {
      if ( item.dicValue ==  this.modeOfKey ) {
        return
      }
      this.modeOfKey = item.dicValue;
      if (
        item.dicValue === DEVPROCESS.OPERATIONCENTER &&
        this.currentProjectData.id == this.$route.query.projectID &&
        (!this.currentProjectData.releaseUsers ||
          this.currentProjectData.releaseUsers.indexOf(this.getUserName()) ===
            -1)
      ) {
        return this.$Message.warning(this.$t('message.workflow.Nopermission'));
      }
      // 使用的地方很多，存在缓存全局获取
      storage.set("currentDssLabels", this.modeOfKey);
      this.tabList = [];
      this.current = {};
      this.tabId = "";
      const routerMap =  { scheduler: 'Scheduler', dev: 'Workflow', prod: 'ScheduleCenter'}
      if (routerMap[item.dicValue]) {
        this.$router.push({
          name: routerMap[item.dicValue],
          query: this.$route.query,
        });
      }
    },
    deleteEditLock(flowId) {
      const data = storage.get("flowEditLock") || {}
      const key = this.getUserName()
      const item = (data[key] || []).find(it => it.flowId == flowId && it.projectId == this.$route.query.projectID)
      if (item && item.lock) {
        api
          .fetch(
            `${this.$API_PATH.WORKFLOW_PATH}deleteFlowEditLock/${item.lock}`,
            {},
            "post"
          )
      }
    },
    /**
     * 切换tab
     * tabId为tab组件返回的标识
     */
    onTabClick(tabId) {
      // 获取当前切换的工作流信息
      const currenTab = this.tabList.filter((item) => item.tabId === tabId)[0];
      this.currentTreeId = currenTab.id
      this.current = currenTab;
    },
    updateWorkflowList() {
      this.$refs.workflow.fetchFlowData();
    },
    isChange(index, val) {
      if (this.tabList[index]) {
        this.tabList[index].isChange = val[0];
      }
    },
    checkEditable(item, name) {
      // 先判断是否可编辑
      if (item.editUsers && item.editUsers.length > 0) {
        return item.editUsers.some((e) => e === name);
      } else {
        return false;
      }
    },
    getUserName() {
      return storage.get("baseInfo", "local")
        ? storage.get("baseInfo", "local").username
        : null;
    },
    /**
     * 自定义渲染节点
     */
    renderNode(h, params) {
      let item = params.item
      let isOpen = this.openNode[item.id]
      let isActive = item.id == this.currentTreeId
      const projAct = (
        <Dropdown style="margin-right: 10px;float: right" nativeOnClick={(e)=>{
          e.stopPropagation()
        }}>
          <div class="tree-info">
            <SvgIcon style="font-size:16px;margin-top: 6px;display: inline-block;" icon-class="more-more" />
          </div>
          <DropdownMenu slot="list">
            <DropdownItem name="config_project" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'config_project', item)}}>{ this.$t('message.workflow.Configuration') }</DropdownItem>
            <DropdownItem name="delete_project" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'delete_project', item)}}>{ this.$t('message.workflow.Delete') }</DropdownItem>
            <DropdownItem name="import" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'import', item)}}>{ this.$t('message.workflow.importflow') }</DropdownItem>
          </DropdownMenu>
        </Dropdown>
      )
      const projectAdd = (
        <div
          style="margin-right: 5px;float: right"
          class="tree-add"
          onClick={(e)=>{
            e.stopPropagation()
            this.handleTreeModal(item)
          }}
        >
          <SvgIcon icon-class="plus" />
        </div>
      )
      const flowAct = (
        <Dropdown style="margin-right: 5px;float: right" placement="bottom-end" nativeOnClick={(e)=>{
          e.stopPropagation()
        }}>
          <div class="tree-info">
            <SvgIcon style="font-size:16px;margin-top: 6px;display: inline-block;" icon-class="more-more" />
          </div>
          <DropdownMenu slot="list">
            {item.editable && <DropdownItem name="config_flow" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'config_flow', item)}}>{this.$t('message.workflow.Configuration') }</DropdownItem>}
            {item.editable && <DropdownItem name="delete_flow" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'delete_flow', item)}}>{this.$t('message.workflow.Delete') }</DropdownItem>}
            {(item.editable || item.canPublish) && <DropdownItem name="copy_flow" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'copy_flow', item)}}>{this.$t('message.workflow.Copy') }</DropdownItem>}
            {(item.editable || item.canPublish) && <DropdownItem name="unlock" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'unlock', item)}}>{this.$t('message.workflow.unlock') }</DropdownItem>}
            {(item.editable || item.canPublish) && <DropdownItem name="export" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'export', item)}}>{this.$t('message.workflow.Export') }</DropdownItem>}
            <DropdownItem name="viewVersion" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'viewVersion', item)}}>{this.$t('message.workflow.Show') }</DropdownItem>
            <DropdownItem name="baseprop" nativeOnClick={(e)=>{this.handleFlowDropDown(e,'baseprop', item)}}>{this.$t('message.workflow.Essential') }</DropdownItem>
          </DropdownMenu>
        </Dropdown>
      )

      if (item.type === 'project') { // 项目
        return h('div', {
          style: {
            padding: '0 10px',
            color: isActive ? 'rgb(45, 140, 240)' : ''
          }
        }, [
          h('SvgIcon', {
            attrs: {
              'icon-class': isOpen ? "open" : "close"
            }
          }, []),
          h('span', {
            class: 'node-name',
            style: {
              padding: '0 6px',
              whiteSpace: 'nowrap',
              textOverflow: 'ellipsis',
              overflow: 'hidden'
            },
            attrs: {
              title: `名称：${item.name}\n描述：${item.description||''}`
            }
          }, [item.name]),
          (item.canWrite ? projectAdd : ''),
          (item.canWrite ? projAct : '')
        ])
      } else if(item.type === 'flow') { // 工作流
        return h('div', {
          style: {
            padding: '0 10px',
            color: isActive ? 'rgb(45, 140, 240)' : ''
          }
        }, [
          h('SvgIcon', {
            attrs: {
              'icon-class': 'flow'
            }
          }, []),
          h('span', {
            class: 'node-name',
            style: {
              padding: '0 6px',
              whiteSpace: 'nowrap',
              textOverflow: 'ellipsis',
              overflow: 'hidden'
            },
            attrs: {
              title: `名称：${item.name}\n描述：${item.description||''}`
            }
          },
          item.name
          ),
          flowAct
        ])
      } else {
        return h('span', {
          class: 'node-name',
          attrs: {
            'data-open': isOpen ? 'open' : ''
          },
          style: {
            padding: '0 10px',
            color: isActive ? 'rgb(45, 140, 240)' : ''
          }
        }, [item.name])
      }
    },
    handleFlowDropDown(e, name, node) {
      e.stopPropagation();
      switch (name) {
        case "config_flow":
          this.onConfigFlow(node);
          break;
        case "delete_flow":
          this.onDeleteFlow(node);
          break;
        case "config_project":
          this.onConfigProject(node);
          break;
        case "delete_project":
          this.onDeleteProject(node);
          break;
        case "viewVersion":
          this.onViewVersion(node);
          break;
        case "unlock":
          this.unlockFlow(node);
          break;
        case "baseprop":
          this.onBasepropShow(node);
          break;
        case "import":
          this.importModal = true;
          this.$refs.import.init(node)
          break;
        case "export":
          this.exportWorkflow(node);
          break;
        case "copy_flow":
          this.showCopyForm = true
          this.$refs.copyForm.init(node, this.rawUserProjects)
          break;
      }
    },
    resize: debounce(function() {
      this.height = this.$el.clientHeight
    }, 300),
    setVirtualRolesForProj(res) {
      if (this.modeOfKey == "streamis_prod") {
        this.projectsTree = res.projects
          .filter((n) => {
            return (
              n.devProcessList &&
                  n.releaseUsers &&
                  n.releaseUsers.indexOf(this.getUserName()) !== -1
            );
          })
          .map((n) => {
            setVirtualRoles(n, this.getUserName());
            return {
              id: n.id,
              name: n.name,
              type: "streamis_prod",
              canWrite: n.canWrite(),
            };
          });
        if (!this.$route.query.projectID) {
          this.handleTreeClick({item: this.projectsTree[0]});
        }
      } else {
        this.projectsTree = res.projects.map((n) => {
          setVirtualRoles(n, this.getUserName());
          return {
            id: n.id,
            name: n.name,
            description: n.description,
            type: "project",
            canWrite: n.canWrite(),
            canPublish: n.canPublish(),
            children: n.children || []
          };
        });
      }
    },
    /**
     * 排序+筛选
     */
    filerSort(name, type) {
      // 保存已经加载项目下工作流数据
      if (!this.flowData) {
        this.flowData = {}
      }
      this.projectsTree.forEach(it => {
        if (it.children) {
          this.flowData[it.id] = it.children
        }
      })
      const filterFn = () => {
        let sortName = this.filterBar.sort
        let filterCat = this.filterBar.cat
        if (type === 'sort') {
          sortName = name
        } else {
          filterCat = name
        }
        // 先排序，再按分类筛选
        this.sortTypeChange(sortName)
        const projs = this.changeCatType(filterCat)
        this.setVirtualRolesForProj({projects: projs})
      }
      if (this.rawProjects) {
        filterFn()
      } else {
        this.getAllProjects((res) => {
          this.rawProjects = res;
          filterFn()
        })
      }
    },
    sortTypeChange(name = this.filterBar.sort) {
      this.filterBar.sort = name
      if (!this.rawProjects) return []
      this.rawProjects.projects.sort((a, b) => {
        if (name === 'updateTime') {
          return b[name] - a[name]
        } else {
          return a[name].localeCompare(b[name])
        }
      })
    },
    changeCatType(type = this.filterBar.cat) {
      this.filterBar.cat = type
      const curUser = this.getUserName()
      // 筛选
      if (!this.rawProjects) return []
      const projs = this.rawProjects.projects.filter(item => {
        if (this.flowData && this.flowData[item.id]) {
          item.children = this.flowData[item.id]
        }
        if (type === 'owner') {
          return item.createBy === curUser
        } else if(type === 'share') {
          return item.createBy !== curUser
        } else {
          return true
        }
      })
      return projs
    },
    /**
     * 复制请求发送成功后更新左侧项目工作流数据
     * 当前有打开的工作流设置成不可编辑
     * 轮询进度是否复制完成
     */
    copySended({target, source, copyJobId}) {
      this.getFlow({id: target.id, name: target.name}, (flows) => {
        this.reFreshTreeData({id: target.id, name: target.name}, flows)
        const targetFlow = flows.find(it => it.name === target.orchestratorName) || {}
        const sourceTarget = {
          target: {
            ...target,
            orchestratorId: targetFlow.id
          },
          source,
          copyJobId
        }
        eventbus.emit('workflow.copying', sourceTarget)
        this.$Notice.close('copy_workflow_ing')
        this.$Notice.info({
          title: this.$t('message.workflow.Prompt'),
          desc: this.$t('message.workflow.Copying'),
          duration: 0,
          name: 'copy_workflow_ing'
        });
        // 复制状态
        this.checkCopyStatus(sourceTarget)
      });
    },
    checkCopyStatus(sourceTarget) {
      api
        .fetch(
          `${this.$API_PATH.ORCHESTRATOR_PATH}${sourceTarget.copyJobId}/copyInfo`,
          {
          },
          "get"
        )
        .then((res) => {
          if (res.orchestratorCopyInfo)  {
            if (res.orchestratorCopyInfo.isCopying) {
              clearTimeout(this.checkStatusTimer)
              this.checkStatusTimer = setTimeout(()=>{
                this.checkCopyStatus(sourceTarget)
              },3000)
            } else {
              eventbus.emit('workflow.copying', {...sourceTarget, done: true})
              this.$Notice.close('copy_workflow_ing')
              if (res.orchestratorCopyInfo.status) {
                this.$Message.success(this.$t('message.workflow.CopySucceed'))
              } else {
                this.$Message.error(this.$t('message.workflow.CopyFailed'))
              }
            }
          }
        });
    }
  },
  beforeRouteLeave(to, from, next) {
    // 用户退出，后端语言服务子进程无法关闭，要求前端发送关闭
    try {
      if (window.languageClient) {
        window.languageClient.__connected_sql_langserver = false;
        window.languageClient.__connected_py_langserver = false;
        if (window.languageClient.__webSocket_sql_langserver) {
          window.languageClient.sql.sendNotification('textDocument/changePage')

          window.languageClient.__webSocket_sql_langserver.close();
        }
        if (window.languageClient.__webSocket_py_langserver) {
          window.languageClient.python.sendNotification('textDocument/changePage')
          window.languageClient.__webSocket_sql_langserver.close();
        }
      }
    } catch (e) {
      //
    }
    next();
  },
  beforeDestroy() {
    clearTimeout(this.checkStatusTimer)
    window.removeEventListener('resize', this.resize);
  }
};
</script>
<style lang="scss" scoped>
@import "../../assets/styles/workflow.scss";
@import "@dataspherestudio/shared/common/style/variables.scss";

.prop-modal {
  .prop-item {
    padding: 5px;
    .label-prop {
      display: inline-block;
      width: 150px;
      text-align: left;
    }
  }
}
.tree-container {
  font-size: 12px;
  @include font-color($light-text-color, $dark-text-color);
}
</style>
<style lang="scss">
.workflow-wrap {
  .tree-container {
    .tree-info,.tree-add {
      display: none
    }
    .node-item {
      height: 32px;
      line-height: 32px;
      &:hover {
        .tree-info,.tree-add{
          display: block
        }
      }
      .node-name {
        max-width: 72%;
        display: inline-block;
        vertical-align: top;
      }
    }
  }
}
</style>
