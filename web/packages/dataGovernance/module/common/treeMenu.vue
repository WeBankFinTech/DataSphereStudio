<template>
  <div>
    <Tree
      class="tree-container"
      :nodes="projectsTree"
      :load="getFlow"
      :currentTreeId="currentTreeId"
      @on-item-click="handleTreeClick"
      @on-sync-tree="handleTreeSync"
    />
    <div class="dataEmpty" v-if="projectsTree.length === 0">暂无数据</div>
    <Spin v-show="loadingTree" size="large" fix />
  </div>
</template>
<script>
import Tree from "./tree/tree.vue";
import api from '@dataspherestudio/shared/common/service/api';
import _ from "lodash";

export default {
  name: "TreeMenu",
  props: {
    treeNodes: {
      type: Array,
      default: () => []
    },
    treeCurrentId: {
      type: Number,
      default: 1
    }
  },
  components: {
    Tree
  },
  data() {
    return {
      loadingTree: false,
      projectsTree: this.treeNodes,
      currentTreeId: this.treeCurrentId, // tree中active节点
      searchValue: "",
      originDatas: []
    };
  },
  watch: {
    treeCurrentId: {
      handler(newVal) {
        this.currentTreeId = newVal;
      }
    }
  },
  mounted() {
    // this.getAllApi();
  },
  methods: {
    getFlow(param) {
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
      this.$emit("showModal", { type: "api", data: { ...project } });
      this.treeModalShow = true;
      this.currentTreeProject = project;
    },
    handleTreeModalCancel() {
      this.treeModalShow = false;
    },
    handleTreeModalConfirm() {
      // tree弹窗添加成功后要更新tree，还要通知右侧workFlow刷新
    },
    handleTreeSync(data) {
      // tree中的状态同步到父级，保持状态，handleTreeModalConfirm用到
      this.projectsTree = data;
    },
    handleTreeClick(node) {
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
          item.children = item.children.filter(child =>
            child.name.includes(value)
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
@import "@dataspherestudio/shared/common/style/variables.scss";
.panel-title-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  & p {

    font-size: 14px;
    @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
  }
  & div {
    cursor: pointer;
    font-size: 20px;
    @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
  }
}
.dataEmpty {
  margin-top: 30px;
  display: flex;
  width: 100%;
  height: 40px;
  justify-content: center;
  align-items: center;

  font-size: 16px;
  @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
}
</style>
