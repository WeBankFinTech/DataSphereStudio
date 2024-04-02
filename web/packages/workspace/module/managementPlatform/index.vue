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
          <Tooltip :content="item.title" placement="right">
            <div class="menuTooltip">
              <SvgIcon
                class="icon"
                :icon-class="item.icon"
                verticalAlign="0px"
              />
            </div>
          </Tooltip>
        </div>
      </div>
      <div class="management-platform-sidebar-tree">
        <span class="management-platform-sidebar-tree-header">
          {{ defaultMenu.title }}
        </span>
        <guide-menu v-if="defaultMenu.title == $t('message.workspace.Documents2')" />
        <library-menu v-else-if="defaultMenu.title == $t('message.workspace.Knowledge')" />
        <Tree
          v-else
          class="management-platform-sidebar-tree-container"
          :nodes="defaultMenu.nodes"
          :currentTreeId="currentTreeId"
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
import lubanTree from '@dataspherestudio/shared/components/lubanTree';
import TabList from "./component/tabList/index.vue";
import GuideMenu from "./component/guide/menu.vue";
import LibraryMenu from "./component/library/menu.vue";
import i18n from '@dataspherestudio/shared/common/i18n';
import {
  GetMenu,
  QueryAllData,
  UpdateDataFromId,
  CreateData,
} from '@dataspherestudio/shared/common/service/componentAccess';
import { formatComponentDataForPost } from "./util/fomat";
const menu = [
  {
    title: i18n.t('message.workspace.DepAndUser'),
    icon: "departUser",
    nodes: [
      {
        name: i18n.t('message.workspace.Department'),
        type: "permissions",
        id: 1024,
        pathName: "departManagement",
      },
      {
        name: i18n.t('message.workspace.UserManage'),
        type: "permissions",
        id: 1023,
        pathName: "personManagement",
      },
    ],
  },
  // {
  //   title: i18n.t('message.workspace.ComponentAccess'),
  //   icon: "componentImport",
  //   nodes: [],
  // },
  {
    title: i18n.t('message.workspace.Documents2'),
    icon: "guide",
    nodes: [],
  },
  {
    title: i18n.t('message.workspace.Knowledge'),
    icon: "question",
    nodes: [],
  }
];
const tempComponent = {
  onestopMenuId: 1,
  titleCn: i18n.t('message.workspace.AddComp'),
  titleEn: "",
  url: "",
  homepageUrl: "",
  projectUrl: "",
  redirectUrl: "",
  ifIframe: 1,
  isActive: 1,
  descCn: "",
  descEn: "",
  // access_button_en: 'not null',
  // access_button_cn: this.$t('message.workspace.Cannotempty')
};
export default {
  components: {
    Tree: lubanTree.managementTree,
    "tab-list": TabList,
    "guide-menu": GuideMenu,
    "library-menu": LibraryMenu
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
      currentHeader: this.$t('message.workspace.DepAndUser'),
      breadcrumbName: "",

      tabList: [],
      current: null,

      componentMenu: [],
      componentChildren: [],

      component_id: 16,

      addedFlag: false,
      menuOptions: [],
    };
  },
  watch: {
    defaultMenu: {
      handler: function (newVal) {
        this.defaultMenu = newVal;
      },
      deep: true,
    },
  },
  methods: {
    // 点击 menu
    handleSidebarToggle(title) {
      this.currentHeader = title;
      if (title === this.defaultMenu.title) {
        this.sidebarFold = !this.sidebarFold;
      } else {
        this.defaultMenu = this.menu.find((item) => item.title === title);
        this.sidebarFold = false;
        if (this.defaultMenu.title == this.$t('message.workspace.Documents2')) {
          this.$router.push("guide");
        } else if (this.defaultMenu.title == this.$t('message.workspace.Knowledge')) {
          this.$router.push("library");
        } else if (this.defaultMenu.title == this.$t('message.workspace.ComponentAccess')) {
          this.$router.push("accessComponents");
        }
      }
    },
    // 点击 子树
    handleTreeClick(node) {
      console.log(node)
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
        if (this.tabList.every((item) => item._id !== node._id)) {
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
        componentData.onestopMenuId = node.id;
        this.current = componentData;
        this.tabList.push(componentData);
        this.addedFlag = true;
        this.header = this.$t('message.workspace.ComponentAccess');
        return this.$router.push({ name: "accessComponents" });
      } else {
        const component_data = this.tabList.filter((tab) => tab.isAdded)[0];
        component_data.onestopMenuId = node.id;
        this.current = JSON.parse(JSON.stringify(component_data));
        this.header = this.$t('message.workspace.ComponentAccess');
      }
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
      const currentTab = this.tabList.filter((item) => item._id === _id)[0];
      // tab 页为新增页
      if (currentTab.isAdded) {
        let idx = currentTab.onestopMenuId;
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
        const required_keys = ['id', 'titleEn', 'titleCn', 'url', 'onestopMenuId', 'projectUrl',
          'homepageUrl', 'ifIframe', 'redirectUrl', 'descEn', 'descCn', 'labelsEn', 'labelsCn',
          'isActive', 'accessButtonEn', 'accessButtonCn'
        ]
        for(let key in updateData ) {
          if( required_keys.indexOf(key) < 0) {
            delete updateData[key]
          }
        }
        console.log(updateData)
        UpdateDataFromId(componentItem.id, updateData)
          .then(() => {
            _this.$Message.success(this.$t('message.workspace.UpdateSucc'));
          })
          .catch(() => {
            _this.$Message.fail(this.$t('message.workspace.Update'));
          });
      }
      //新增
      if (componentItem.isAdded) {
        const postData = formatComponentDataForPost(componentItem);
        CreateData(postData)
          .then(() => {
            _this.$Message.success(this.$t('message.workspace.AddSucc'));
          })
          .catch(() => {
            _this.$Message.fail(this.$t('message.workspace.AddFail'));
          });
      }
    },

    // 拉取类别数据
    getMenuForcomponentAccess() {
      let that = this;
      GetMenu()
        .then((data) => {
          const _menuOptions = [];
          let dssOnestopMenuList = data.dssOnestopMenuList
          dssOnestopMenuList.forEach((item) => {
            item.type = "component";
            item.children = [];
            item.opened = true;

            const menu = Object.create(null);
            menu.name = item.name;
            menu.titleCn = item.titleCn;
            menu.titleEn = item.titleEn;

            _menuOptions.push(menu);
          });
          that.menuOptions = _menuOptions;
          console.log("that.menuOptions", that.menuOptions);
          sessionStorage.setItem(
            "menuOptions",
            JSON.stringify(that.menuOptions)
          );
          that.getAllComponentData(dssOnestopMenuList, (nodes) => {
            if (nodes) {
              menu[2].nodes = nodes;
              that.menu = menu;
            }
          });
        })
        .catch((err) => {
          console.log("err", err);
        });
    },

    // 拉取所有子数据
    getAllComponentData(nodes, callback) {
      let that = this;
      let component_id = this.component_id;
      QueryAllData()
        .then((data) => {
          let dssOnestopMenuJoinApplicationList = data.dssOnestopMenuJoinApplicationList
          dssOnestopMenuJoinApplicationList.forEach((item) => {
            nodes.forEach((node) => {
              if (node.id === item.onestopMenuId) {
                item._id = component_id;
                node.children.push(item);
                component_id++;
              }
            });
          });
          that.component_id = component_id;
          callback(nodes);
        })
        .catch((err) => {
          console.log("getAllComponentData error!", err);
          callback(false);
        });
    }
  },
  mounted() {
    // this.getMenuForcomponentAccess();
    if (this.$route.name !== this.lastPathName) {
      this.$router.push("departManagement");
    }
  },
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
$permissions-Backgroud: #f4f7fb;
$per-border-color: #dee4ec;
.management-platform-wrap {
  height: 100%;
  .management-platform-sidebar {
    z-index: 2;
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
        border-left: 3px solid transparent;
        &:hover {
          @include bg-color(#eceff4, $dark-menu-base-color);
        }
      }
      .menu-active {
        border-left: 3px solid rgb(45, 140, 240);
        @include bg-color(#edf1f6, $dark-menu-base-color);
        @include border-color(#2e92f7, #4b8ff3);
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

        @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
        line-height: 34px;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }
  .management-platform-tablist {
    z-index: 1;
    height: 100%;
  }
  .management-platform-sidebar-tree-header {
    padding-left: 12px;
  }
  .menuTooltip {
    width: 50px;
    height: 44px;
  }
  ::v-deep.ivu-tooltip-inner {
    background-color: rgba(70, 76, 91, 1);
  }
}
</style>
