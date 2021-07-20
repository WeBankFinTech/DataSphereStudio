<template>
  <div class="ds-nav-menu-wrap" :class="{ 'ds-nav-menu-fold': treeFold }">
    <div class="ds-nav-menu">
      <div
        class="ds-nav-menu-item"
        :class="{ active: currentTab == '/dataService' }"
        @click="handleTabClick('dataService')"
      >
        <img src="../../assets/images/develop_nav.svg" class="develop_nav" />
      </div>
      <div
        class="ds-nav-menu-item"
        :class="{ active: currentTab == '/dataManagement' }"
        @click="handleTabClick('dataManagement')"
      >
        <Icon custom="iconfont icon-project" size="26"></Icon>
      </div>
    </div>
    <div class="ds-nav-panel" v-if="currentTab == '/dataService'">
      <div class="panel-title-wrap">
        <p>服务开发</p>
        <div @click="addGroup()">
          <Icon custom="iconfont icon-plus" size="20"></Icon>
        </div>
      </div>
      <Input prefix="ios-search" placeholder="请输入" style="width: auto" />
      <Tree
        class="tree-container"
        :nodes="projectsTree"
        :load="getFlow"
        :currentTreeId="currentTreeId"
        @on-item-click="handleTreeClick"
        @on-add-click="handleTreeModal"
        @on-sync-tree="handleTreeSync"
      />
      <Spin v-show="loadingTree" size="large" fix />
    </div>
    <div class="ds-nav-panel" v-if="currentTab == '/dataManagement'">
      服务管理菜单
    </div>
  </div>
</template>
<script>
import Tree from "@/apps/workflows/module/common/tree/tree.vue";
import api from "@/common/service/api";

export default {
  name: "navMenu",
  components: {
    Tree
  },
  data() {
    return {
      currentTab: this.$route.path,
      loadingTree: false,
      projectsTree: [],
      treeFold: false,
      currentTreeId: +this.$route.query.projectID // tree中active节点
    };
  },
  mounted() {
    this.getAllProjects();
    this.getAllApi();
  },
  methods: {
    handleTabClick(tab) {
      if (this.$route.path == `/${tab}`) {
        this.handleTreeToggle();
      } else {
        this.$router.push({
          name: tab,
          query: this.$route.query
        });
      }
    },
    handleTreeToggle() {
      this.treeFold = !this.treeFold;
      this.$emit("handleFold", this.treeFold);
    },
    // 获取所有project展示tree
    getAllProjects() {
      const projects = [
        {
          id: 418,
          applicationArea: 0,
          business: "",
          createBy: "hadoop",
          description: "123",
          name: "test123",
          source: null,
          product: "",
          editable: true,
          createTime: 1624933330000,
          updateTime: 1624933330000,
          releaseUsers: ["hadoop"],
          editUsers: ["", "hadoop"],
          accessUsers: [""],
          devProcessList: ["dev"],
          orchestratorModeList: ["pom_work_flow"],
          archive: false
        },
        {
          id: 406,
          applicationArea: 0,
          business: "",
          createBy: "hadoop",
          description: "test",
          name: "yu622",
          source: null,
          product: "",
          editable: true,
          createTime: 1624343400000,
          updateTime: 1624867955000,
          releaseUsers: ["det101"],
          editUsers: ["hadoop"],
          accessUsers: [""],
          devProcessList: ["dev", "scheduler"],
          orchestratorModeList: ["pom_work_flow"],
          archive: false
        },
        {
          id: 361,
          applicationArea: 0,
          business: "",
          createBy: "hadoop",
          description: "1",
          name: "tet1",
          source: null,
          product: "",
          editable: true,
          createTime: 1622686085000,
          updateTime: 1624329358000,
          releaseUsers: ["qweasd", "det101"],
          editUsers: ["hadoop"],
          accessUsers: [""],
          devProcessList: ["dev"],
          orchestratorModeList: ["pom_work_flow"],
          archive: false
        }
      ];
      this.projectsTree = projects.map(n => {
        return {
          id: n.id,
          name: n.name,
          type: "project",
          canWrite: () => true
        };
      });
      // this.loadingTree = true;
      // api.fetch(`${this.$API_PATH.PROJECT_PATH}getAllProjects`, {
      //   workspaceId: +this.$route.query.workspaceId
      // }, 'post').then((res) => {
      //   this.loadingTree = false;
      //   this.projectsTree = res.projects.map(n => {
      //     return {
      //       id: n.id,
      //       name: n.name,
      //       type: 'project',
      //       canWrite: n.canWrite()
      //     }
      //   });
      // })
    },
    // 获取project下工作流
    getFlow(param, resolve) {
      api
        .fetch(
          `${this.$API_PATH.PROJECT_PATH}getAllOrchestrator`,
          {
            workspaceId: this.$route.query.workspaceId,
            // orchestratorMode: "pom_work_flow",
            projectId: param.id
          },
          "post"
        )
        .then(res => {
          const flow = res.page.map(f => {
            return {
              ...f,
              id: f.orchestratorId, // flow的id是orchestratorId
              name: f.orchestratorName,
              projectId: param.id || f.projectId,
              // 补充projectName，点击工作流切换project时使用
              projectName: param.name,
              type: "flow"
            };
          });
          resolve(flow);
        });
    },
    handleTreeModal(project) {
      console.log(project);
      console.log("addApi");
      this.$emit("showModal", { type: "api", data: { ...project } });
      this.treeModalShow = true;
      this.currentTreeProject = project;
    },
    handleTreeModalCancel() {
      this.treeModalShow = false;
    },
    handleTreeModalConfirm(param) {
      // tree弹窗添加成功后要更新tree，还要通知右侧workFlow刷新
    },
    handleTreeSync(data) {
      // tree中的状态同步到父级，保持状态，handleTreeModalConfirm用到
      this.projectsTree = data;
    },
    handleTreeClick(node) {},
    getAllApi() {
      api
        .fetch(
          `/dss/framework/dbapi/list`,
          {
            workspaceId: this.$route.query.workspaceId
          },
          "post"
        )
        .then(res => {
          console.log(res);
        });
    },
    addGroup() {
      console.log("addGroup");
      this.$emit("showModal", { type: "group" });
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.ds-nav-menu-wrap {
  display: flex;
  position: fixed;
  left: 0;
  top: 54px;
  bottom: 0;
  width: 304px;
  background: #f8f9fc;
  transition: all 0.3s;
  &.ds-nav-menu-fold {
    width: 54px;
    .ds-nav-panel {
      transform: translateX(-304px);
    }
  }
  .ds-nav-menu {
    z-index: 1;
    width: 54px;
    background: #f8f9fc;
    border-right: 1px solid #dee4ec;
    &-item {
      height: 44px;
      line-height: 44px;
      text-align: center;
      cursor: pointer;
      &:hover {
        background: #eceff4;
      }
    }
    .active {
      background: #eceff4;
      border-left: 3px solid #2e92f7;
    }
  }
  .ds-nav-panel {
    position: absolute;
    width: 250px;
    left: 54px;
    top: 0;
    bottom: 0;
    transition: all 0.3s;
    padding: 10px;
    overflow-y: auto;
    border-right: 1px solid #dee4ec;
  }
}
.develop_nav {
  width: 24px;
}
.panel-title-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  & p {
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.65);
  }
  & div {
    cursor: pointer;
  }
}
</style>
