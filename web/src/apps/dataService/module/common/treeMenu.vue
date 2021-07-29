<template>
  <div>
    <Tree 
      class="tree-container" 
      :nodes="projectsTree" 
      :load="getFlow" 
      :currentTreeId="currentTreeId"
      @on-item-click="handleTreeClick" 
      @on-add-click="handleTreeModal" 
      @on-sync-tree="handleTreeSync"
    />
    <Spin v-show="loadingTree" size="large" fix/>
  </div>
</template>
<script>

import Tree from '@/apps/workflows/module/common/tree/tree.vue';
import api from '@/common/service/api'

export default {
  name: 'TreeMenu',
  components: {
    Tree,
  },
  data() {
    return {
      loadingTree: false,
      projectsTree: [],
      currentTreeId: +this.$route.query.projectID, // tree中active节点
    }
  },
  mounted() {
    this.getAllProjects();
  },
  methods: {
    // 获取所有project展示tree
    getAllProjects() {
      const projects = [{
        "id": 418,
        "applicationArea": 0,
        "business": "",
        "createBy": "hadoop",
        "description": "123",
        "name": "test123",
        "source": null,
        "product": "",
        "editable": true,
        "createTime": 1624933330000,
        "updateTime": 1624933330000,
        "releaseUsers": ["hadoop"],
        "editUsers": ["", "hadoop"],
        "accessUsers": [""],
        "devProcessList": ["dev"],
        "orchestratorModeList": ["pom_work_flow"],
        "archive": false
      }, {
        "id": 406,
        "applicationArea": 0,
        "business": "",
        "createBy": "hadoop",
        "description": "test",
        "name": "yu622",
        "source": null,
        "product": "",
        "editable": true,
        "createTime": 1624343400000,
        "updateTime": 1624867955000,
        "releaseUsers": ["det101"],
        "editUsers": ["hadoop"],
        "accessUsers": [""],
        "devProcessList": ["dev", "scheduler"],
        "orchestratorModeList": ["pom_work_flow"],
        "archive": false
      }, {
        "id": 361,
        "applicationArea": 0,
        "business": "",
        "createBy": "hadoop",
        "description": "1",
        "name": "tet1",
        "source": null,
        "product": "",
        "editable": true,
        "createTime": 1622686085000,
        "updateTime": 1624329358000,
        "releaseUsers": ["qweasd", "det101"],
        "editUsers": ["hadoop"],
        "accessUsers": [""],
        "devProcessList": ["dev"],
        "orchestratorModeList": ["pom_work_flow"],
        "archive": false
      }];
      this.projectsTree = projects.map(n => {
        return {
          id: n.id,
          name: n.name,
          type: 'project',
          canWrite: () => true
        }
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
      api.fetch(`${this.$API_PATH.PROJECT_PATH}getAllOrchestrator`, {
        workspaceId: this.$route.query.workspaceId,
        // orchestratorMode: "pom_work_flow",
        projectId: param.id,
      }, 'post').then((res) => {
        const flow = res.page.map(f => {
          return {
            ...f,
            id: f.orchestratorId, // flow的id是orchestratorId
            name: f.orchestratorName,
            projectId: param.id || f.projectId,
            // 补充projectName，点击工作流切换project时使用
            projectName: param.name,
            type: 'flow'
          }
        });
        resolve(flow);
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
    },
    handleTreeSync(data) {
      // tree中的状态同步到父级，保持状态，handleTreeModalConfirm用到
      this.projectsTree = data;
    },
    handleTreeClick(node) {
    },
  }
};
</script>
<style lang="scss" scoped>
.tree-container {
  padding: 10px;
}
</style>



