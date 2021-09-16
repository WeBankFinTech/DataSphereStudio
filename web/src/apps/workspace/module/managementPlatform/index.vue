<template>
  <div class="management-platform-wrap">
    <!-- tree  侧边栏 -->
    <div
      class="management-platform-sidebar"
      :class="{ 'sidebar-fold': sidebarFold }"
    >
      <div class="management-platform-sidebar-menu">
        <div
          class="management-platform-sidebar-menu-item"
          v-for="item in menu"
          :key="item.title"
          :class="{ 'menu-active': item.title == currentHeader }"
          @click="handleSidebarToggle(item.title)"
        >
          <SvgIcon class="icon" :icon-class="item.icon" verticalAlign="0px" />
        </div>
      </div>
      <div class="management-platform-sidebar-tree">
        <span class="management-platform-sidebar-tree-header">
          {{ defaultMenu.title }}
        </span>
        <Tree
          class="management-platform-sidebar-tree-container"
          :nodes="defaultMenu.nodes"
          :currentTreeId="this.currentTreeId"
          @on-item-click="handleTreeClick"
          @on-add-click="handleTreeComponent"
        />
        <Spin v-show="loadingSidebar" size="large" fix />
      </div>
    </div>

    <!-- tablist -->
    <div class="management-platform-tablist">
      <tab-list
        :class="{ 'sidebar-fold': sidebarFold }"
        :header="header"
        :breadcrumbName="breadcrumbName"
        :topTapList="tabList"
        :currentTab="current"
        @handleTabRemove="onTabRemove"
        @bandleTapTab="onTabClick"
        @on-save="saveComponent"
      />
    </div>
  </div>
</template>

<script>
import Tree from "./component/tree/tree.vue";
import TabList from "./component/tabList/index.vue";
import {
  GetMenu,
  QueryAllData,
  UpdateDataFromId,
  CreateData
} from "@/common/service/componentAccess";
import { formatComponentDataForPost } from "./util/fomat";
import storage from "./util/cache";
const menu = [
  {
    title: "部门和用户管理",
    icon: "departUser",
    nodes: [
      {
        name: "部门管理",
        type: "permissions",
        id: 1024,
        pathName: "departManagement"
      },
      {
        name: "用户管理",
        type: "permissions",
        id: 1023,
        pathName: "personManagement"
      }
    ]
  },
  {
    title: "控制台",
    icon: "kongzhitai",
    nodes: [
      {
        name: "全局历史",
        type: "console",
        id: 1022,
        pathName: "globalHistory"
      },
      { name: "资源管理", type: "console", id: 1021, pathName: "resource" },
      { name: "参数配置", type: "console", id: 1020, pathName: "setting" },
      {
        name: "全局变量",
        type: "console",
        id: 1019,
        pathName: "globalValiable"
      },
      { name: "ECM管理", type: "console", id: 1018, pathName: "ECM" },
      // {name: '微服务管理', type: 'console', id: 1017, pathName: 'microService'},
      { name: "常见问题", type: "console", id: 1016, pathName: "FAQ" }
    ]
  },
  {
    title: "组件接入",
    icon: "componentImport",
    nodes: []
  }
];
const tempComponent = {
  onestop_menu_id: 1,
  title_cn: "新增组件",
  title_en: "",
  url: "",
  homepage_url: "",
  project_url: "",
  redirect_url: "",
  if_iframe: 1,
  is_active: 1,
  desc_cn: "",
  desc_en: ""
  // access_button_en: 'not null',
  // access_button_cn: '不能为空'
};
export default {
  components: {
    Tree,
    "tab-list": TabList
  },
  data() {
    return {
      sidebarFold: false,
      loadingSidebar: false,
      currentTreeId: 1024,
      menu,
      lastPathName: "",
      defaultMenu: menu[0],
      header: menu[0].title || "",
      currentHeader: "部门和用户管理",
      breadcrumbName: "",

      tabList: [],
      current: null,

      componentMenu: [],
      componentChildren: [],

      component_id: 16,

      addedFlag: false
    };
  },
  watch: {
    defaultMenu: {
      handler: function(newVal) {
        this.defaultMenu = newVal;
      },
      deep: true
    }
  },
  methods: {
    // 点击 menu
    handleSidebarToggle(title) {
      this.currentHeader = title;
      if (title === this.defaultMenu.title) {
        this.sidebarFold = !this.sidebarFold;
      } else {
        this.defaultMenu = this.menu.find(item => item.title === title);
        this.sidebarFold = false;
      }
    },
    // 点击 子树
    handleTreeClick(node) {
      storage.node = node;
      const { id, pathName, name, type } = node;
      const { title } = this.defaultMenu;

      this.lastPathName = pathName || "accessComponents";
      this.currentTreeId = id;
      this.header = title;

      if (type !== "permissions") {
        this.breadcrumbName = name;
      } else {
        this.breadcrumbName = "";
      }

      if (node._id) {
        this.currentTreeId = node._id;
        this.current = node;
        if (this.tabList.every(item => item._id !== node._id)) {
          this.tabList.push(node);
          return this.$router.push({ name: "accessComponents" });
        }
      }

      this.$router.push({ name: pathName });
    },
    // 新增 子树 适用于组件接入
    handleTreeComponent(node) {
      this.currentTreeId = node.id;
      const flag = this.addedFlag;
      let component_id = this.component_id;
      // 已经新增了，再点击新增会重定向到新增页
      if (!flag) {
        const componentData = Object.assign({ isAdded: true }, tempComponent);
        component_id = component_id + 1;
        this.component_id = component_id;
        componentData._id = component_id;
        componentData.onestop_menu_id = node.id;
        this.current = componentData;
        this.tabList.push(componentData);
        this.addedFlag = true;
        return this.$router.push({ name: "accessComponents" });
      } else {
        const component_data = this.tabList.filter(tab => tab.isAdded)[0];
        component_data.onestop_menu_id = node.id;
        this.current = JSON.parse(JSON.stringify(component_data));
      }
      this.header = "组件接入";
    },

    //关闭tab页
    onTabRemove(_id) {
      let that = this;
      let index = "";
      let len = this.tabList.length;
      const removeData = this.tabList.filter((item, i) => {
        if (item._id == _id) {
          index = i;
          return true;
        }
      })[0];

      const removeAction = () => {
        if (removeData._id === that.current._id) {
          if (len > 1 && index < len - 1) {
            that.current = that.tabList[index + 1];
            that.currentTreeId = that.tabList[index + 1]._id;
          } else if (len > 1 && index == len - 1) {
            that.current = that.tabList[index - 1];
            that.currentTreeId = that.tabList[index - 1]._id;
          } else {
            that.current = {};
            that.currentTreeId = 0;
          }
        }

        // 删除新增页
        if (removeData.isAdded) {
          this.addedFlag = false;
        }

        that.tabList.splice(index, 1);
      };

      removeAction();
    },
    // 点击tab页
    onTabClick(_id) {
      const currentTab = this.tabList.filter(item => item._id === _id)[0];
      // tab 页为新增页
      if (currentTab.isAdded) {
        let idx = currentTab.onestop_menu_id;
        if (typeof idx === "number") {
          this.currentTreeId = idx;
        } else {
          idx = 0;
        }
      } else {
        this.currentTreeId = _id;
      }
      this.current = currentTab;
    },
    // 保存component数据
    saveComponent(componentItem) {
      let _this = this;
      //更新
      if (componentItem.id) {
        const updateData = formatComponentDataForPost(componentItem);
        UpdateDataFromId(componentItem.id, updateData)
          .then(data => {
            let idx = _this.currentTreeId;
            _this.defaultMenu.nodes.forEach(node => {
              if (node.id == updateData.onestop_menu_id) {
                node.children.forEach(child => {
                  if (child._id == idx) {
                    child = updateData;
                  }
                });
              }
            });

            _this.$Message.success("更新成功");
            _this.$;
          })
          .catch(err => {
            _this.$Message.fail("更新失败");
          });
      }
      //新增
      if (componentItem.isAdded) {
        // this.tabList.forEach(tab => {
        //   if( tab._id == componentItem._id ) {
        //     Object.assign(tab, componentItem)
        //   }
        // })
        const postData = formatComponentDataForPost(componentItem);
        CreateData(postData)
          .then(data => {
            // _this.defaultMenu.nodes.forEach( node => {
            //   if( node.id == postData.onestop_menu_id ) {
            //     node.children.push(postData);
            //   }
            // });
            this.getMenuForcomponentAccess();
            _this.$Message.success("新增成功");
          })
          .catch(err => {
            _this.$Message.fail("新增失败");
          });
      }
    },

    ok() {},
    cancel() {},

    // 拉取类别数据
    getMenuForcomponentAccess() {
      let that = this;
      GetMenu()
        .then(data => {
          data.forEach(item => {
            item.type = "component";
            item.children = [];
            item.opened = true;
          });
          that.getAllComponentData(data, nodes => {
            if (nodes) {
              menu[2].nodes = nodes;
              that.menu = menu;
            }
          });
        })
        .catch(err => {
          // that.$Message.fail(err);
          console.log("err", err);
        });
    },

    // 拉取所有子数据
    getAllComponentData(nodes, callback) {
      let that = this;
      let component_id = this.component_id;
      QueryAllData()
        .then(data => {
          data.forEach(item => {
            nodes.forEach(node => {
              if (node.id === item.onestop_menu_id) {
                item._id = component_id;
                node.children.push(item);
                component_id++;
              }
            });
          });
          that.component_id = component_id;
          callback(nodes);
        })
        .catch(err => {
          console.log("getAllComponentData error!", err);
          callback(false);
        });
    }
  },
  mounted() {
    this.getMenuForcomponentAccess();
    if (this.$route.name !== this.lastPathName) {
      // 需要页面刷新 数据持久化
      // const node = menu[0].nodes.slice(1);
      // this.handleTreeClick(node);
      this.$router.push("departManagement");
    }
  },
  created() {
    //拉取后端数据
  }
};
</script>

<style lang="scss" scoped>
@import '@/common/style/variables.scss';
$permissions-Backgroud: #F4F7FB;
$per-border-color:#DEE4EC;
.management-platform-wrap{
  height: 100%;
  .management-platform-sidebar {
    display: flex;
    position: fixed;
    left: 0;
    top: 54px;
    bottom: 0;
    width: 304px;
    @include border-color(#dee4ec, $dark-menu-base-color);
    @include bg-color(#f8f9fc, $dark-menu-base-color);
    transition: all 0.3s;
    &.sidebar-fold {
      width: 54px;
      .management-platform-sidebar-tree {
        transform: translateX(-1304px);
      }
    }
    .management-platform-sidebar-menu {
      z-index: 1;
      width: 54px;
      @include bg-color(#f8f9fc, $dark-base-color);
      border-right: 1px solid #dee4ec;
      @include border-color(#dee4ec, $dark-menu-base-color);
      &-item {
        height: 44px;
        line-height: 44px;
        text-align: center;
        cursor: pointer;
        font-size: 26px;
        @include font-color(#333, $dark-text-color);
        &:hover {
          @include bg-color(#eceff4, $dark-menu-base-color);
        }
      }
    }
    .management-platform-sidebar-tree {
      position: absolute;
      width: 250px;
      left: 54px;
      top: 0px;
      bottom: 0px;
      transition: all 0.3s;
      overflow-y: auto;
      border-right: 1px solid #dee4ec;
      @include border-color(#dee4ec, $dark-menu-base-color);
      &-header {
        font-size: 14px;
        font-weight: bold;
        font-family: PingFangSC-Medium;
        @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
        line-height: 34px;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }
  .management-platform-tablist {
    height: 100%;
  }
  .management-platform-sidebar-tree-header {
    padding-left: 12px;
  }
  .menu-active {
    border-left: 3px solid rgb(45, 140, 240);
    @include bg-color(#edf1f6, $dark-menu-base-color);
    @include border-color(#2e92f7, #4b8ff3);
  }
}
</style>
