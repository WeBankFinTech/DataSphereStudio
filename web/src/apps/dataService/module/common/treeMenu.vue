<template>
  <div>
    <div class="panel-title-wrap">
      <p>服务开发</p>
      <div @click="addGroup()">
        <Icon custom="iconfont icon-plus" size="20"></Icon>
      </div>
    </div>
    <Input
      size="small"
      :value="searchValue"
      prefix="ios-search"
      placeholder="请输入"
      style="width: 230px;border:0;margin-top: 10px;"
      @on-change="handleSearch"
    />
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
</template>
<script>
import Tree from "@/apps/workflows/module/common/tree/tree.vue";
import api from "@/common/service/api";
import _ from "lodash";

export default {
  name: "TreeMenu",
  components: {
    Tree
  },
  data() {
    return {
      loadingTree: false,
      projectsTree: [],
      currentTreeId: +this.$route.query.projectID, // tree中active节点
      searchValue: 123,
      originDatas: []
    };
  },
  mounted() {
    this.getAllApi();
  },
  methods: {
    getFlow(param, resolve) {
      this.projectsTree = this.projectsTree.map(item => {
        if (item.id == param.id) {
          return {
            ...item,
            loaded: true,
            loading: false,
            opened: true,
            isLeaf: false
          };
        } else {
          return item;
        }
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
      //获取数据服务所有的api
      this.searchValue = "";
      api
        .fetch(
          `/dss/framework/dbapi/list?workspaceId=${this.$route.query.workspaceId}`,
          {},
          "get"
        )
        .then(res => {
          console.log(res);
          if (res && res.list) {
            this.projectsTree = res.list.map(n => {
              return {
                id: n.groupId,
                name: n.groupName,
                type: "project",
                canWrite: () => true,
                children: n.apis,
                apis: n.apis
              };
            });
            this.originDatas = _.cloneDeep(this.projectsTree);
          } else {
            this.projectsTree = [];
          }
        });
    },
    addGroup() {
      //添加数据服务api分组
      console.log("addGroup");
      this.$emit("showModal", { type: "group" });
    },
    addApi(groupId, apiData) {
      //添加数据服务api
      this.searchValue = "";
      this.projectsTree = this.originDatas.map(item => {
        if (item.id == groupId) {
          return {
            ...item,
            loaded: true,
            loading: false,
            opened: true,
            isLeaf: false,
            children: [...item.children, apiData]
          };
        } else {
          return item;
        }
      });
      this.originDatas = _.cloneDeep(this.projectsTree);
    },
    handleSearch: _.debounce(function(e) {
      const value = e.target.value;
      console.log(value);
      console.log(2333);
      this.executeSearch(value);
    }, 500),
    executeSearch(value) {
      this.searchValue = value;
      if (value) {
        const temp = _.cloneDeep(this.originDatas);
        this.projectsTree = temp.filter(item => {
          return !!item.children.find(child => child.name.include(value));
        });
      } else {
        this.projectsTree = _.cloneDeep(this.originDatas);
      }
    }
  }
};
</script>
<style lang="scss" scoped>
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
