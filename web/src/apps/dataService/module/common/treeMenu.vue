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
    <div class="dataEmpty" v-if="projectsTree.length === 0">暂无数据</div>
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
      searchValue: "",
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
    handleTreeClick(node) {
      console.log(node);
      this.$emit("handleApiChoosed", node);
    },
    getAllApi(type = "", payload = {}) {
      //获取数据服务所有的api
      api
        .fetch(
          `/dss/framework/dbapi/list?workspaceId=${this.$route.query.workspaceId}`,
          {},
          "get"
        )
        .then(res => {
          console.log(res);
          if (res && res.list) {
            const isUpdate = type === "update";
            const list = res.list.map(n => {
              const childs = n.apis.map(item => {
                return {
                  ...item,
                  projectId: n.groupId,
                  projectName: n.groupName,
                  type: "api"
                };
              });
              let opened = false;
              if (isUpdate) {
                const hit = this.projectsTree.find(p => p.id === n.groupId);
                opened = hit ? !!hit.opened : false;
                if (!payload.id && payload.groupId === n.groupId) {
                  const apiDetail = childs.find(
                    child =>
                      child.name === payload.apiName &&
                      child.path === payload.apiPath
                  );
                  this.$emit("handleApiChoosed", {
                    type: "saveApi",
                    data: { ...apiDetail },
                    apiData: { ...payload }
                  });
                }
              }
              return {
                id: n.groupId,
                name: n.groupName,
                type: "project",
                canWrite: () => true,
                children: childs,
                apis: childs,
                opened
              };
            });
            this.projectsTree = list;
            this.originDatas = _.cloneDeep(this.projectsTree);
          } else {
            this.projectsTree = [];
          }
        });
    },
    addGroup() {
      //添加数据服务api分组
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
      this.executeSearch(value);
    }, 200),
    executeSearch(value) {
      this.searchValue = value;
      const temp = _.cloneDeep(this.originDatas);
      const result = !value ? temp : [];
      if (value) {
        temp.forEach(item => {
          item.opened = true;
          item.children = item.children.filter(
            child => child.name.includes(value)
          );
          if (item.children.length > 0) {
            result.push(item);
          }
        });
      }
      this.projectsTree = result;
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
.dataEmpty {
  display: flex;
  width: 100%;
  height: 40px;
  justify-content: center;
  align-items: center;
  font-family: PingFangSC-Medium;
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
}
</style>
