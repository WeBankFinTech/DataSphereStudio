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
            <span class="project-nav-tree-top-t-txt">项目</span>
            <span class="project-nav-tree-top-t-icon">
              <SvgIcon
                icon-class="dev_center_flod"
                style="opacity: 0.65"
                @click="handleTreeToggle"
              />
            </span>
          </div>
        </div>
        <div class="list-container">
          <div class="list-item">
            <div class="list-content" :class="{ 'list-content-active': currentTreeId == 'index' }">
              <div class="list-name" @click="handleTreeClick({id: 'index', type: 'scheduler', name: '项目总览'})">
                {{ `项目总览` }}
              </div>
            </div>
          </div>
          <div
            class="list-item"
            v-for="item in projectsTree"
            :key="item.id"
          >
            <div class="list-content" :class="{ 'list-content-active': currentTreeId == item.id }">
              <div class="list-name" @click="handleTreeClick(item)">
                {{ item.name }}
              </div>
            </div>
          </div>
        </div>
        <Spin v-show="loadingTree" size="large" fix />
      </div>
    </div>

    <WorkflowTabList
      :class="{ 'tree-fold': treeFold }"
      :treeFold="treeFold"
      :loading="loading"
      :modeOfKey="modeOfKey"
      :buttonText="selectDevprocess"
      :currentTab="currentTreeId"
      @handleChangeButton="handleChangeButton"
      @handleChooseScheduler="handleChooseScheduler"
    >
      <DS :activeTab="4" :isindex="currentTreeId == 'index'" class="scheduler-center"></DS>
    </WorkflowTabList>
  </div>
</template>
<script>
import storage from '@dataspherestudio/shared/common/helper/storage';
import WorkflowTabList from '../../components/tabList/index.vue';
import api from '@dataspherestudio/shared/common/service/api';
import { DEVPROCESS, ORCHESTRATORMODES } from '@dataspherestudio/shared/common/config/const.js';
import {
  GetDicSecondList,
  GetDicList
} from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
import { setVirtualRoles } from '@dataspherestudio/shared/common/config/permissions.js';
import eventbus from "@dataspherestudio/shared/common/helper/eventbus";
import DS from "../../module/dispatch";

export default {
  components: {
    WorkflowTabList,
    DS
  },
  data() {
    return {
      tabList: [],
      modeOfKey: DEVPROCESS.OPERATIONCENTER,
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
      devProcessBase: [],
      selectDevprocess: [],
      DEVPROCESS,
      ORCHESTRATORMODES,
      loading: false,
      loadingTree: false,
      projectsTree: [],
      treeFold: false,
      currentTreeId: this.$route.query.projectID ? +this.$route.query.projectID : 'index', // tree中active节点
    };
  },
  watch: {
  },
  async created() {
    const params = {
      parentKey: "p_develop_process",
      workspaceId: this.$route.query.workspaceId,
    };
    const res = await GetDicList(params)
    this.devProcessBase = res.list;
    this.getDicSecondList();
    this.getAllProjects(() => {
      this.getProjectData();
      this.getSelectDevProcess();
      if (this.$route.query.projectID) {
        const item = this.projectsTree.find(item => item.id == this.$route.query.projectID)
        if (item) {
          this.updateCache(item)
        }
      }
    });
  },
  mounted() {
  },
  computed: {
  },
  methods: {
    handleTreeToggle() {
      this.treeFold = !this.treeFold;
    },
    // 获取当前选择的开发流程
    getSelectDevProcess() {
      if (this.currentTreeId === 'index') {
        this.selectDevprocess = this.devProcessBase.filter((item) =>
          DEVPROCESS.OPERATIONCENTER === item.dicValue
        )
      } else {
        this.selectDevprocess = this.devProcessBase
          ? this.devProcessBase.filter((item) =>
            this.currentProjectData.devProcessList.includes(item.dicValue)
          )
          : [];
      }
    },
    // 获取编排模式的基本信息
    getDicSecondList() {
      GetDicSecondList(this.$route.query.workspaceId).then((res) => {
        this.orchestratorModeList = res.list;
      });
    },
    // 获取工程的数据
    getProjectData() {
      if (!this.$route.query.projectID) {
        return
      }
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
          if (res && res.projects[0]) {
            const project = res.projects[0];
            setVirtualRoles(project, this.getUserName());
            this.currentProjectData = {
              ...res.projects[0],
              canWrite: project.canWrite(),
            };
            this.loading = false;
          }
          this.getSelectDevProcess();
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
          callback();
        });
    },
    handleTreeClick(node) {
      if ((node.id == this.$route.query.projectID && this.$route.query.projectID) || (node.id === 'index' && this.currentTreeId ==='index')) return
      this.currentTreeId = node.id;
      if (node.type === "scheduler") {
        this.updateCache(node)
        let query = {}
        if (node.id != this.$route.query.projectID && node.id !== 'index') {
          // 跨工程，会监听projectID
          query = {
            workspaceId: this.$route.query.workspaceId,
            projectID: node.id,
            projectName: node.name,
          };
        } else if(node.id === 'index'){
          query = {
            workspaceId: this.$route.query.workspaceId,
          };
        }
        this.$router.replace({
          name: "Scheduler",
          query,
        })
      }
      this.getSelectDevProcess()
    },
    updateCache(node) {
      const SKEY = this.getTabStorageKey()
      let schedulerTabList = JSON.parse(sessionStorage.getItem(SKEY)) || []
      if( schedulerTabList.findIndex(i => i.id == node.id) < 0 ) {
        schedulerTabList.push(node)
        sessionStorage.setItem(SKEY, JSON.stringify(schedulerTabList))
        eventbus.emit('scheduler_tab_list_change', schedulerTabList)
      }
    },
    getTabStorageKey() {
      return 'scheduler_tab_list_' + this.getUserName()
    },
    // 切换开发流程
    handleChangeButton(item) {
      if ( item.dicValue ==  this.modeOfKey ) {
        return
      }
      // 当前流程的value
      this.modeOfKey = item.dicValue;
      // 使用的地方很多，存在缓存全局获取
      storage.set("currentDssLabels", this.modeOfKey);
      const routerMap =  { scheduler: 'Scheduler', dev: 'Workflow', prod: 'ScheduleCenter'}
      this.$router.push({
        name: routerMap[item.dicValue],
        query: this.$route.query,
      });
    },
    getUserName() {
      return storage.get("baseInfo", "local")
        ? storage.get("baseInfo", "local").username
        : null;
    },
    handleChooseScheduler(tabData) {
      this.handleTreeClick(tabData)
    }
  },
};
</script>
<style lang="scss" scoped>
@import "../../assets/styles/workflow.scss";
@import "@dataspherestudio/shared/common/style/variables.scss";
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
.list-item{
  white-space: nowrap;
  outline: none;
  .list-content {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 10px;
    @include font-color($light-text-color, $dark-text-color);
    &:hover {
      @include bg-color(#edf1f6, $dark-active-menu-item);
    }
    &-active {
      @include bg-color(#edf1f6, $dark-active-menu-item);
      @include font-color($primary-color, $dark-primary-color);
    }
    .list-name {
      display: block;
      flex: 1;
      line-height: 32px;
      padding: 0 6px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
  }
}
</style>
