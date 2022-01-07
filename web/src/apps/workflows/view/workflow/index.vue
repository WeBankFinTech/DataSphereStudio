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
      <!-- <div class="project-nav-menu">
        <div class="project-nav-menu-item active" @click="handleTreeToggle">
          <SvgIcon class="nav-icon" icon-class="project-workflow" />
        </div>
      </div> -->
      <div class="project-nav-tree">
        <div class="project-nav-tree-top">
          <div class="project-nav-tree-top-t">
            <span class="project-nav-tree-top-t-txt">项目</span>
            <span class="project-nav-tree-top-t-icon">
              <SvgIcon
                icon-class="dev_center_flod"
                style="opacity: 0.65"
                @click="handleTreeToggle"
              />
            </span>
          </div>
          <!-- <div class="project-nav-tree-top-b">
            <SvgIcon icon-class="search-icon" style="opacity: 0.25" />
            <input
              type="text"
              class="project-nav-tree-top-b-input"
              placeholder="请输入"
            />
          </div> -->
        </div>
        <Tree
          class="tree-container"
          ref="projectTree"
          :nodes="projectsTree"
          :load="getFlow"
          :currentTreeId="currentTreeId"
          @on-item-click="handleTreeClick"
          @on-add-click="handleTreeModal"
          @on-sync-tree="handleTreeSync"
          @on-config-project="onConfigProject"
          @on-delete-project="onDeleteProject"
          @on-config-flow="onConfigFlow"
          @on-delete-flow="onDeleteFlow"
          @on-move-flow="onMoveFlow"
          @on-view-version="onViewVersion"
        />
        <!-- <div class="project-nav-tree-bottom">
          <div class="project-nav-add" @click.stop="createProject">
            <Icon type="md-add" />
            <span style="margin-left: 8px">添加项目</span>
          </div>
        </div> -->
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
      :textColor="textColor"
      :tabName="tabName"
      :topTabList="topTabList"
      :modeOfKey="modeOfKey"
      :buttonText="selectDevprocess"
      :bottomTapList="tabList"
      :modeName="modeName"
      :currentTab="current"
      @bandleTapTab="onTabClick"
      @handleTabRemove="onTabRemove"
      @handleChangeButton="handleChangeButton"
      @selectProject="selectProject"
      @menuHandleChangeButton="menuHandleChangeButton"
    >
      <template v-if="modeOfKey === DEVPROCESS.DEVELOPMENTCENTER">
        <div
          class="workflowListContainer"
          v-show="textColor || (tabList.length < 1 && projectsTree.length > 0)"
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
            @publishSuccess="publishSuccess"
            @on-tree-modal-confirm="handleTreeModalConfirm"
          >
            <Tabs class="tabs-content" slot="tagList" v-model="currentMode">
              <TabPane
                v-for="item in selectOrchestratorList"
                :label="item.dicName"
                :key="item.dicKey"
                :name="item.dicKey"
              >
              </TabPane>
            </Tabs>
          </Workflow>
        </div>

        <template
          v-for="(item, index) in tabList.filter(
            (i) => i.type === DEVPROCESS.DEVELOPMENTCENTER
          )"
        >
          <commonIframe
            v-if="item.url && item.isIframe"
            v-show="
              (item.version
                ? currentVal.name === item.name &&
                  currentVal.version === item.version
                : currentVal.name === item.name) && !textColor
            "
            :key="item.name"
            :url="current.url"
          ></commonIframe>
          <process
            v-else-if="item.orchestratorMode === ORCHESTRATORMODES.WORKFLOW"
            :key="item.tabId"
            v-show="
              (item.version
                ? currentVal.name === item.name &&
                  currentVal.version === item.version
                : currentVal.name === item.name) && !textColor
            "
            :query="item.query"
            @updateWorkflowList="updateWorkflowList"
            @isChange="isChange(index, arguments)"
          ></process>
          <makeUp
            v-else
            v-show="
              (item.version
                ? currentVal.name === item.name &&
                  currentVal.version === item.version
                : currentVal.name === item.name) && !textColor
            "
            :key="item.name"
            :currentVal="currentVal"
          ></makeUp>
        </template>
      </template>
      <template v-if="modeOfKey === DEVPROCESS.OPERATIONCENTER">
        <DS :activeTab="4" class="scheduler-center"></DS>
      </template>
      <template v-else>
        <!-- 其他应用流程 -->
      </template>
    </WorkflowTabList>
    <ProjectForm
      ref="projectForm"
      :action-type="actionType"
      :project-data="currentProjectData"
      :add-project-show="ProjectShow"
      :applicationAreaMap="applicationAreaMap"
      :classify-list="cacheData"
      :framework="true"
      :orchestratorModeList="orchestratorModeList"
      @getDevProcessData="getDevProcessData"
      @show="ProjectShowAction"
      @confirm="ProjectConfirm"
    ></ProjectForm>

    <!-- 删除项目弹窗 -->
    <Modal
      v-model="deleteProjectShow"
      :title="$t('message.workflow.projectDetail.deleteProject')"
      @on-ok="deleteProjectConfirm"
    >
      {{ $t("message.workflow.projectDetail.confirmDeleteProject")
      }}{{ deleteProjectItem.name }}?
      <br />
      <br />
    </Modal>
    <!-- <Spin
      v-if="loading"
      size="large"
      fix/> -->
  </div>
</template>
<script>
import projectDb from "@/apps/workflows/service/db/project.js";
import Workflow from "@/apps/workflows/module/workflow";
import WorkflowModal from "@/apps/workflows/module/workflow/module/workflowModal.vue";
import Process from "@/apps/workflows/module/process";
import storage from "@/common/helper/storage";
// import Tree from "@/apps/workflows/module/common/tree/tree.vue";
import Tree from "@/apps/workflows/module/common/workflowTree/tree.vue";
import WorkflowTabList from "@/apps/workflows/module/common/tabList/index.vue";
import MakeUp from "@/apps/workflows/module/makeUp";
import ProjectForm from "@/components/projectForm/index.js";
import api from "@/common/service/api";
import { DEVPROCESS, ORCHESTRATORMODES } from "@/common/config/const.js";
import {
  GetDicSecondList,
  GetAreaMap,
} from "@/common/service/apiCommonMethod.js";
import { setVirtualRoles } from "@/common/config/permissions.js";
import DS from "@/apps/workflows/module/dispatch";
import eventbus from "@/common/helper/eventbus";

export default {
  components: {
    Tree,
    WorkflowModal,
    Workflow: Workflow.component,
    process: Process.component,
    WorkflowTabList,
    makeUp: MakeUp.component,
    ProjectForm,
    DS,
  },
  data() {
    return {
      textColor: "#2D8CF0",
      indexLabel: (h) => {
        return h("span", {}, "概览");
      },
      tabList: [],
      currentVal: { name: "index", version: "index" },
      lastVal: null,
      current: null,
      modeOfKey: "",
      modeName: "工作流开发",
      tabName: "",
      topTabList: undefined,
      currentMode: null,
      ProjectShow: false,
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
      deleteProjectItem: "", // 删除的工程项
      actionType: "", // add || modify
      dataList: [
        {
          id: 1,
          name: this.$t("message.workflow.projectDetail.WCYDXM"),
          dwsProjectList: [],
        },
      ],
      cacheData: [],
    };
  },
  watch: {
    tabList(val, oldVal) {
      let workspaceId = this.$route.query.workspaceId
      let workFlowLists = JSON.parse(localStorage.getItem(`work_flow_lists_${workspaceId}`)) || [];
      if (workFlowLists.length > 5) {
        workFlowLists = workFlowLists.slice(0,5)
      }
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
      localStorage.setItem(`work_flow_lists_${workspaceId}`, JSON.stringify(workFlowLists))
      eventbus.emit('tabListChange', workFlowLists)
    },
    currentVal(val, oldVal) {
      this.lastVal = oldVal;
      this.currentVal = val;
      if (this.modeOfKey === "dev" || this.modeOfKey === "scheduler")
        this.updateProjectCache();
    },
    "$route.query.projectID"() {
      this.tabList = [];
      this.lastVal = null;
      this.getAreaMap();
      this.getProjectData();
      this.tryOpenWorkFlow();
      this.updateBread();
    },
    selectOrchestratorList(val) {
      if (val.length > 0) {
        this.currentMode = val[0].dicKey;
      }
    },
    currentWorkdapceData(val) {
      console.log(val, "工作空间数据");
    },
  },
  created() {
    this.getAreaMap();
    this.getDicSecondList();
    if (this.isScheduler) {
      this.modeOfKey = DEVPROCESS.OPERATIONCENTER;
    }
    // 获取所有project展示tree
    this.getAllProjects(() => {});
  },
  mounted() {
    // this.getCache();
    this.updateBread();
    this.tryOpenWorkFlow();
  },
  computed: {
    currentWorkdapceData() {
      return storage.get("currentWorkspace");
    },
    isScheduler() {
      return this.$route.name === "Scheduler";
    },
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
    // initClick() {
    //   if (this.projectsTree.length > 0) {
    //     const cur = this.projectsTree[0];
    //     this.getFlow(cur, (flow) => {
    //       if (flow.length > 0) {
    //         const cur_node = flow[0];
    //         this.handleTreeClick(cur_node);
    //         this.$refs.projectTree.handleItemToggle(cur);
    //       } else {
    //         this.handleTreeClick(cur);
    //       }
    //     });
    //   }
    // },
    createProject() {
      this.actionType = "add";
      this.ProjectShow = true;
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
      this.orchestratorModeList = {
        list: this.orchestratorModeList.list.map((i) => {
          if (i.dicKey == "pom_work_flow") {
            // 编排模式暂时只支持工作流
            return {
              ...i,
              enabled: true,
            };
          } else {
            return i;
          }
        }),
      };
    },
    handleTreeToggle() {
      this.treeFold = !this.treeFold;
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
    },
    // 获取编排列表
    getSelectOrchestratorList() {
      this.selectOrchestratorList = this.orchestratorModeList.list
        ? this.orchestratorModeList.list.filter((item) =>
          this.currentProjectData.orchestratorModeList.includes(item.dicKey)
        )
        : [];
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
    getProjectData() {
      api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllProjects`,
          {
            workspaceId: +this.$route.query.workspaceId,
            id: +this.$route.query.projectID,
          },
          "post"
        )
        .then((res) => {
          //运维中心路由且未选中任何项目
          if (!this.$route.query.projectID && this.isScheduler) {
            this.modeOfKey = DEVPROCESS.OPERATIONCENTER;
            return this.getAllProjects();
          }
          const project = res.projects[0];
          setVirtualRoles(project, this.getUserName());
          this.currentProjectData = {
            ...res.projects[0],
            canWrite: project.canWrite(),
          };
          this.getSelectDevProcess();
          this.getSelectOrchestratorList();
          this.loading = false;
        });
    },
    // 获取所有project展示tree
    getAllProjects(callback) {
      this.loadingTree = true;
      api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllProjects`,
          {
            workspaceId: +this.$route.query.workspaceId,
          },
          "post"
        )
        .then((res) => {
          this.loadingTree = false;
          if (this.isScheduler) {
            this.projectsTree = res.projects
              .filter((n) => {
                return (
                  n.devProcessList &&
                  n.devProcessList.includes("scheduler") &&
                  n.releaseUsers &&
                  n.releaseUsers.indexOf(this.getUserName()) !== -1
                );
              })
              .map((n) => {
                setVirtualRoles(n, this.getUserName());
                return {
                  id: n.id,
                  name: n.name,
                  type: "scheduler",
                  canWrite: n.canWrite(),
                };
              });
            if (!this.$route.query.projectID)
              this.handleTreeClick(this.projectsTree[0]);
          } else {
            this.projectsTree = res.projects.map((n) => {
              setVirtualRoles(n, this.getUserName());
              return {
                id: n.id,
                name: n.name,
                type: "project",
                canWrite: n.canWrite(),
              };
            });
          }
          callback();
        });
    },
    // 获取project下工作流
    getFlow(param, resolve) {
      if (this.isScheduler) {
        return resolve([]);
      }
      api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllOrchestrator`,
          {
            workspaceId: this.$route.query.workspaceId,
            // orchestratorMode: "pom_work_flow",
            projectId: param.id,
          },
          "post"
        )
        .then((res) => {
          const flow = res.page.map((f) => {
            return {
              ...f,
              id: f.orchestratorId, // flow的id是orchestratorId
              name: f.orchestratorName,
              projectId: param.id || f.projectId,
              // 补充projectName，点击工作流切换project时使用
              projectName: param.name,
              type: "flow",
            };
          });
          resolve(flow);
        });
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
          `${this.$API_PATH.PROJECT_PATH}getAllOrchestrator`,
          {
            workspaceId: this.$route.query.workspaceId,
            // orchestratorMode: "pom_work_flow",
            projectId: param.id,
          },
          "post"
        )
        .then((res) => {
          const flow = res.page.map((f) => {
            return {
              ...f,
              id: f.orchestratorId, // flow的id是orchestratorId
              name: f.orchestratorName,
              projectId: param.id || f.projectId,
              // 补充projectName，点击工作流切换project时使用
              projectName: param.name,
              type: "flow",
            };
          });
          this.projectsTree = this.projectsTree.map((item) => {
            if (item.id == param.id) {
              return {
                ...item,
                loaded: true,
                loading: false,
                opened: true, // 展开
                isLeaf: flow.length ? false : true,
                children: flow.length ? flow : item.children,
              };
            } else {
              return item;
            }
          });
          this.handleTreeModalCancel();
          this.refreshFlow = true;
        });
    },
    handleTreeSync(data) {
      // tree中的状态同步到父级，保持状态，handleTreeModalConfirm用到
      this.projectsTree = data;
    },
    handleTreeClick(node) {
      // 切换到开发模式
      this.modeOfKey =
        this.selectDevprocess && this.selectDevprocess.length
          ? this.selectDevprocess[0].dicValue
          : DEVPROCESS.DEVELOPMENTCENTER;
      if (this.isScheduler) {
        this.modeOfKey = DEVPROCESS.OPERATIONCENTER;
      }
      if (node.type == "flow") {
        if (this.isScheduler) {
          return this.$Message.warning("运维中心暂只支持项目级别");
        }
        this.currentTreeId = node.orchestratorId;
        // 如果点击其它project的flow，应该切换project
        if (node.projectId != this.$route.query.projectID) {
          // 跨工程，会监听projectID
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
          const param = {
            ...this.$route.query,
            id: node.orchestratorId, // flow的id是orchestratorId
            name: node.orchestratorName,
            version: String(node.orchestratorVersionId),
            orchestratorMode: node.orchestratorMode,
            releasable: node.releasable,
            editable: node.editable,
          };
          // 同工程下切换flow还要兼顾右侧tab的状态
          const isIn = this.tabList.find(
            (item) => item.id === param.id && item.version === param.version
          );
          if (isIn) {
            const tabId = param.id + param.version;
            this.onTabClick(tabId);
          } else {
            this.openWorkflow(param);
          }
          // 同project下切换flow，应该切换产品文档到开发模式，清除前面的flow进入editor编辑模式而更新的guide，其他情况因为route change可以监测到
          eventbus.emit("workflow.orchestratorId", {
            orchestratorId: node.orchestratorId,
            mod: "auto",
          });
        }
      } else if (node.type === "project" || node.type === "scheduler") {
        this.currentTreeId = node.id;
        if (node.id == this.$route.query.projectID) {
          this.selectProject();
        } else {
          // 跨工程，会监听projectID
          const query = {
            workspaceId: this.$route.query.workspaceId,
            projectID: node.id,
            projectName: node.name,
          };
          node.type === "scheduler"
            ? this.$router.replace({
              name: "Scheduler",
              query,
            })
            : this.$router.replace({
              name: "Workflow",
              query,
            });
          this.updateBread();
        }
      }
    },
    onDeleteProject(project) {
      this.deleteProjectShow = true;
      this.deleteProjectItem = project;
    },
    onConfigProject(project) {
      this.actionType = "modify";
      this.menuHandleChangeButton();
    },
    // 刪除單個項目
    deleteProjectConfirm() {
      // 调用删除接口
      const params = {
        id: this.deleteProjectItem.id,
        sure: false,
        ifDelOtherSys: false,
      };
      api
        .fetch(`${this.$API_PATH.PROJECT_PATH}deleteProject`, params, "post")
        .then((res) => {
          this.loading = false;
          if (res.warmMsg) {
            this.$Modal.confirm({
              title: this.$t("message.workflow.projectDetail.deleteTitle"),
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
                        "message.workflow.projectDetail.deleteProject"
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
      // this.$refs.workflow.workflowModify(params);
    },
    onDeleteFlow(params) {
      this.$refs.workflow.deleteWorkflow(params);
    },
    getFlowData(params = {}) {
      if (!params.projectId) return;
      this.loading = true;
      api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllOrchestrator`,
          params,
          "post"
        )
        .then((res) => {
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    onMoveFlow() {},
    onViewVersion(project) {
      this.$refs.workflow.versionDetail(project.id, project);
    },
    // 切换project，更新面包屑
    updateBread() {
      let workspaceId = this.$route.query.workspaceId;
      let currentWorkspaceName = storage.get("currentWorkspace")
        ? storage.get("currentWorkspace").name
        : "首页";
      let projectName = this.$route.query.projectName;
      this.topTabList = [
        {
          name: currentWorkspaceName,
          url: `/workspaceHome?workspaceId=${workspaceId}`,
        },
        { name: projectName, url: `` },
      ];
      if (this.$route.query.flowId) {
        this.currentTreeId = +this.$route.query.flowId;
      } else {
        this.currentTreeId = +this.$route.query.projectID;
      }
    },
    tryOpenWorkFlow() {
      // this.modeOfKey不能为空
      if (this.modeOfKey && this.$route.query && this.$route.query.flowId) {
        const flow = storage.get("clickFlowInTree");
        if (flow && flow.orchestratorId && flow.orchestratorMode) {
          const param = {
            ...this.$route.query,
            id: +this.$route.query.flowId || flow.orchestratorId, // flow的id是orchestratorId
            name: flow.orchestratorName,
            version: String(flow.orchestratorVersionId),
            orchestratorMode: flow.orchestratorMode,
            releasable: flow.releasable,
            editable: flow.editable,
          };
          this.openWorkflow(param);
        }
        storage.remove("clickFlowInTree");
      }
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
            typeof callback == "function" && callback();
            this.$Message.success(
              `${this.$t(
                "message.workflow.projectDetail.createProject"
              )}${this.$t("message.workflow.success")}`
            );
            setTimeout(() => {
              this.$router.go(0);
            }, 500);
            // this.getclassListData().then(data => {
            //   // 新建完工程进到工作流页
            //   const currentProject = data[0].dwsProjectList.filter(
            //     project => project.name === projectData.name
            //   )[0];
            //   this.$router.push({
            //     name: "Workflow",
            //     query: {
            //       ...this.$route.query,
            //       projectID: currentProject.id,
            //       projectName: currentProject.name,
            //       notPublish: currentProject.notPublish
            //     }
            //   });
            // });
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
            typeof callback == "function" && callback();
            this.getProjectData();
            this.$Message.success(
              this.$t("message.workflow.projectDetail.eidtorProjectSuccess", {
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
    ProjectShowAction(val) {
      this.ProjectShow = val;
    },
    // 公共的菜单点击事件
    menuHandleChangeButton() {
      // 目前只有一个，后面插件化再去判断
      // 只读用户不可操作
      if (!this.checkEditable(this.currentProjectData, this.getUserName()))
        return this.$Message.warning(
          this.$t("message.orchestratorModes.noEditPermission")
        );
      this.ProjectShow = true;
    },
    /**
     * 打开工作流查看并将工作流信息存入tab列表
     * parama 为打开工作流基本信息
     */
    openWorkflow(params) {
      if (params.lastedNode) {
        const cur = this.projectsTree.filter(item => item.name == params.projectName)[0];
        this.getFlow(cur, (flow) => {
          this.$refs.projectTree.handleItemToggle(cur);
        });
      }
      this.currentTreeId = params.id || undefined
      if (this.loading) return;
      // 判断是否为相同编排的不同版本，不是则将信息新增tab列表
      const isIn = this.tabList.find(
        (item) => item.id === params.id && item.version === params.version
      );
      if (!isIn || isIn === -1) {
        if (this.tabList.length >= 10)
          return this.$Message.warning(
            this.$t("message.orchestratorModes.openTabTen")
          );

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
                orchestratorId: params.id,
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
                params.orchestratorVersionId =
                  openOrchestrator.OrchestratorVo.dssOrchestratorVersion.id;
                this.openItemAction(params);
              } else {
                return this.$Message.warning(
                  this.$t("message.orchestratorModes.FailedOpenOrchestrator")
                );
              }
            });
        }
      } else {
        // 有的话就打开
        this.lastVal = this.currentVal;
        this.currentVal = isIn;
        this.tabName = params.id + params.version;
        // 打开工作流将textColor清空，使tab为选中状态
        this.textColor = "";
      }
    },
    openItemAction(params) {
      this.current = {
        version: params.version, // 编排版本
        id: params.id, // 编排id
        name: params.name, // 用于tab显示
        query: params,
        tabId: params.id + params.version, // 相同编排的不同版本
        isChange: false,
        orchestratorMode: params.orchestratorMode, // 后面根据具体的编排模式确认类型字段
        type: DEVPROCESS.DEVELOPMENTCENTER,
        //isIframe: openOrchestrator.isIframe,
        //url: openOrchestrator.OrchestratorOpenUrl + `?${qs.stringify(params)}`
      };
      this.tabList.push(this.current);
      this.lastVal = this.currentVal;
      this.currentVal = this.current;
      this.updateProjectCache(params.projectID, true);
      this.tabName = params.id + params.version;
      // 打开工作流将textColor清空，使tab为选中状态
      this.textColor = "";
    },
    onTabRemove(tabId) {
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
        if (removeData.tabId === this.currentVal.tabId) {
          // 如果是当前打开则判断是否为最后一个，是就显示前一个，不是就显示后一个，如果没有则显示列表
          if (this.tabList.length > 1 && index < this.tabList.length - 1) {
            this.current =
              this.currentVal =
              this.lastVal =
                this.tabList[index + 1];
            this.currentTreeId = this.current.id;
          } else if (
            this.tabList.length > 1 &&
            index === this.tabList.length - 1
          ) {
            this.current =
              this.currentVal =
              this.lastVal =
                this.tabList[index - 1];
            this.currentTreeId = this.current.id;
          } else {
            this.current = this.currentVal = this.lastVal = {};
            // 没有那就是选中开发中心或者编排中心
            this.textColor = "#2D8CF0";
            this.currentTreeId = undefined;
          }
        }
        this.tabList.splice(index, 1);
        this.tabName = this.currentVal.id + this.currentVal.version || "";
        this.updateProjectCache(null, true);
      };
      if (this.tabList[index].isChange) {
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
      if ( item.dicValue !=  this.modeOfKey ) {
        this.getAllProjects(()=>{});
      }
      if (
        item.dicValue === DEVPROCESS.OPERATIONCENTER &&
        this.currentProjectData.id == this.$route.query.projectID &&
        (!this.currentProjectData.releaseUsers ||
          this.currentProjectData.releaseUsers.indexOf(this.getUserName()) ===
            -1)
      ) {
        return this.$Message.warning("无运维权限");
      }
      // 当前流程的value
      this.modeOfKey = item.dicValue;
      // 使用的地方很多，存在缓存全局获取
      storage.set("currentDssLabels", this.modeOfKey);
      this.tabList = [];
      this.textColor = "#2D8CF0";
      this.lastVal = null;
      this.currentVal = {};
      this.current = null;
      this.tabId = "";
      if (item.dicValue === "scheduler" && !this.isScheduler) {
        this.$router.replace({
          name: "Scheduler",
          query: this.$route.query,
        });
      } else if (item.dicValue === "dev" && this.isScheduler) {
        this.$router.replace({
          name: "Workflow",
          query: this.$route.query,
        });
      }
    },
    // 选择列表
    selectProject() {
      this.textColor = "#2D8CF0";
    },
    /**
     * 切换tab
     * tabId为tab组件返回的标识
     */
    onTabClick(tabId) {
      // 获取当前切换的工作流信息
      const currenTab = this.tabList.filter((item) => item.tabId === tabId)[0];
      this.lastVal = this.currentVal;
      this.currentVal = {
        ...currenTab,
        tabId: currenTab.tabId,
      };
      this.currentTreeId = currenTab.id
      this.current = currenTab;
      this.tabName = currenTab.id + currenTab.version;
      // 切换工作流将textColor清空，使tab为选中状态
      this.textColor = "";
    },
    async getCache() {
      const query = this.$route.query;
      if (query && query.projectID) {
        const cache = await projectDb.getProjectCache({
          projectID: query.projectID,
        });
        if (cache && Number(query.projectID) === Number(cache.projectID)) {
          this.lastVal = cache.lastVal;
          if (cache.tabList) {
            this.currentVal = cache.currentVal || {
              name: "index",
              version: "index",
            };
            this.tabList = cache.tabList;
            this.current = cache.tabList.find((item) => {
              if (this.currentVal.version && item.version) {
                return (
                  item.name === this.currentVal.name &&
                  item.version == this.currentVal.version
                );
              } else {
                return item.name === this.currentVal.name;
              }
            });
          } else {
            this.current = [];
          }
        }
      }
    },
    updateProjectCache(projectID, wantUpadateList) {
      const value = {
        currentVal: this.currentVal,
        lastVal: this.lastVal,
      };
      if (wantUpadateList) {
        value.tabList = this.tabList;
      }
      projectID = projectID || this.$route.query.projectID;
      projectDb.updateProjectCache({
        projectID,
        value,
      });
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
    publishSuccess(params) {
      this.tabList = this.tabList.filter(
        (i) =>
          i.tabId !==
          String(params.orchestratorId) + params.orchestratorVersionId
      );
    },
    getclassListData() {
      this.loading = true;
      return api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllProjects`,
          { workspaceId: +this.$route.query.workspaceId },
          "post"
        )
        .then((res) => {
          res.projects.map((item, index) => {
            setVirtualRoles(item, this.getUserName());
          });
          this.dataList[0].dwsProjectList = res.projects;
          this.cacheData = this.dataList;
          this.dataList.forEach((item) => {
            this.sortType[item.id] = this.$t(
              "message.workflow.projectDetail.updteTime"
            );
          });
          this.loading = false;

          storage.set("projectList", this.cacheData, "local");
          return this.cacheData;
        })
        .catch(() => {
          this.loading = false;
        });
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/apps/workflows/assets/styles/workflow.scss";
.item-header {
  font-size: $font-size-base;
  margin: 10px 25px;
  font-weight: bold;
  padding-left: 5px;
  border-left: 3px solid $primary-color;
  @include border-color($primary-color, $dark-primary-color);
}
.rightCardContainer {
  will-change: auto;
  padding: 20px;
  .cardItem {
    box-shadow: 0 0 6px $shadow-color;
    position: relative;
    padding: 10px;
    line-height: 1;
    display: flex;
    align-items: center;
    &:not(:last-child) {
      margin-bottom: 50px;
    }
    .ios-arrow-round-down {
      position: absolute;
      bottom: -45px;
      left: 50%;
      // color: $primary-color;
      @include font-color($primary-color, $dark-primary-color);
      font-size: 40px;
      transform: translateX(-50%);
    }
    .cardItemText {
      margin-left: 10px;
      font-size: $font-size-base;
      font-weight: 700;
    }
  }
}
</style>
