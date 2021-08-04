<template>
  <div class="management-platform-wrap">

    <!-- tree  侧边栏 -->
    <div class="management-platform-sidebar" :class="{'sidebar-fold': sidebarFold}" >
      <div class="management-platform-sidebar-menu">
        <div class="management-platform-sidebar-menu-item active" v-for="item in menu" :key="item.title" @click="handleSidebarToggle(item.title)">
          <Icon :custom="`iconfont icon-${item.icon}`" size="26"></Icon>
        </div>
      </div>
      <div class="management-platform-sidebar-tree">
        <span class="management-platform-sidebar-tree-header">
          {{ defaultMenu.title }}
        </span>
        <Tree
          class="management-platform-sidebar-tree-container"
          :nodes='defaultMenu.nodes'
          :currentTreeId = this.currentTreeId
          @on-item-click="handleTreeClick"
          @on-add-click="handleTreeComponent"
        />
        <Spin v-show="loadingSidebar" size="large" fix/>
      </div>
    </div>

    <!-- tablist -->
    <div class="management-platform-tablist" >
      <tab-list
      :class="{'sidebar-fold': sidebarFold }"
      :header = header
      :breadcrumbName = breadcrumbName
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
import Tree from './component/tree/tree.vue';
import TabList from './component/tabList/index.vue';
const menu = [
  {
    title: '部门和用户管理',
    icon: 'bumenheyonghuguanli',
    nodes: [
      {name: '部门管理', type: 'permissions', id: 1024, pathName: 'departManagement'},
      {name: '用户管理', type: 'permissions', id: 1023, pathName: 'personManagement'}
    ]
  },
  {
    title: '控制台',
    icon: 'kongzhitai',
    nodes: [
      {name: '全局历史', type: 'console', id: 1022, pathName: 'globalHistory'},
      {name: '资源管理', type: 'console', id: 1021, pathName: 'resource'},
      {name: '参数配置', type: 'console', id: 1020, pathName: 'setting'},
      {name: '全局变量', type: 'console', id: 1019, pathName: 'globalValiable'},
      {name: 'ECM管理', type: 'console', id: 1018, pathName: 'ECM'},
      // {name: '微服务管理', type: 'console', id: 1017, pathName: 'microService'},
      {name: '常见问题', type: 'console', id: 1016, pathName: 'FAQ'},
    ]
  },
  {
    title: '组件接入',
    icon: 'zujianjieruguanli',
    nodes: [
      {name: '数据接入', type: 'component', id: 1015, children: [
        {name: 'xxx', id: 1012, catagory: 'dataAccess', engName: 'xxx', baseurl: 'https://www.baidu.com', homePage: 'https://www.baidu.com', projectPage: 'https://www.baidu.com', spInterface: 'xxx', iframe: false, activate: false, description: 'xxx', engDescription: 'xxx'},
        {name: 'zzz', id: 1010, catagory: 'dataAccess', engName: 'xxx', baseurl: 'https://www.baidu.com', homePage: 'https://www.baidu.com', projectPage: 'https://www.baidu.com', spInterface: 'xxx', iframe: false, activate: false, description: 'xxx', engDescription: 'xxx'},
        {name: 'aaa', id: 1009, catagory: 'dataAccess', engName: 'xxx', baseurl: 'https://www.baidu.com', homePage: 'https://www.baidu.com', projectPage: 'https://www.baidu.com', spInterface: 'xxx', iframe: false, activate: false, description: 'xxx', engDescription: 'xxx'}
      ], pathName: 'accessComponents'},
      {name: '数据开发', type: 'component', id: 1014, children: [
        {name: 'ccc', id: 1011, catagory: 'dataAccess', engName: 'xxx', baseurl: 'https://www.baidu.com', homePage: 'https://www.baidu.com', projectPage: 'https://www.baidu.com', spInterface: 'xxx', iframe: false, activate: false, description: 'xxx', engDescription: 'xxx'}
      ], pathName: 'accessComponents'},
      {name: '数据治理', type: 'component', id: 1013, children: [], pathName: 'accessComponents'},
    ]
  }
];
const tempComponent = {
  catagory: 'dataAccess',
  name: '新增组件',
  engName: '',
  baseurl: '',
  homePage: '',
  projectPage: '',
  spInterface: '',
  iframe: true,
  activate: true,
  description: '',
  engDescription: '',
  isAdded: true,
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

      defaultMenu: menu[0],
      header: menu[0].title || '',
      breadcrumbName: '',

      tabList: [],
      current: null,

      addedFlag: false,
    }
  },
  methods: {
    // 点击 menu
    handleSidebarToggle(title) {
      if( title === this.defaultMenu.title ) {
        this.sidebarFold = !this.sidebarFold
      } else {
        this.defaultMenu = this.menu.find( item => item.title === title )
        this.sidebarFold = false
      }
    },
    // 点击 子树
    handleTreeClick(node) {
      console.log('node', node)
      const { id, pathName, name, type } = node;
      const { title } = this.defaultMenu;

      this.currentTreeId = id;
      this.header = title;

      if( type !== 'permissions' )  {
        this.breadcrumbName = name;
      } else {
        this.breadcrumbName = ''
      }

      if ( node.id < 1013 ) {
        this.current = node;
        if( this.tabList.every(item => item.id !== node.id) ) {
          this.tabList.push(node);
          return this.$router.push({ name: 'accessComponents' })
        }
      }
      this.$router.push({ name: pathName })
    },
    // 新增 子树 适用于组件接入
    handleTreeComponent(node) {
      this.currentTreeId = node.id;
      console.log(node, 'f-node')
      const flag = this.addedFlag;
      let len = 0;
      // 计算 id
      this.defaultMenu.nodes.forEach(node => {
        len = len + node.children.length;
      })
      const componentData = Object.assign({id: 1012 - len, isAdded: true}, tempComponent);
      // 已经新增了，再点击新增会重定向到新增页
      if( !flag ) {
        this.current = componentData;
        this.tabList.push(componentData);
        this.addedFlag = true;
        return this.$router.push({ name: 'accessComponents' })
      } else {
        console.log('重定向')
        this.current = this.tabList.filter(tab => tab.isAdded)[0];
      }
    },

    //关闭tab页
    onTabRemove(id) {
      let that = this;
      let index = '';
      let len = this.tabList.length;
      const removeData = this.tabList.filter((item, i) => {
        if ( item.id == id ) {
          index = i;
          return true
        }
      })[0];

      const removeAction = () => {
        if (removeData.id === that.current.id) {
          if (len > 1 && index < len - 1) {
            that.current = that.tabList[index + 1];
          } else if(len > 1 && index == len - 1) {
            that.current = that.tabList[index - 1];
          } else {
            that.current = {};
          }
        }

        // 删除新增页
        if ( removeData.isAdded ) {
          console.log( 'delete added tab!' )
          this.addedFlag = false;
        }


        that.tabList.splice(index, 1);
      }

      removeAction();
    },
    // 点击tab页
    onTabClick(id) {
      const currentTab = this.tabList.filter( item => item.id === id )[0];
      this.current = currentTab;
    },
    // 保存component数据
    saveComponent(componentItem) {
      //更新
      console.log('save component', componentItem)
      // if(!componentItem.isAdded) {

      // }

      //新增

    }
  },
  mounted() {

  },
  created() {
    //拉取后端数据
  }
}
</script>

<style lang="scss" scoped>
@import '@/common/style/variables.scss';
$permissions-Backgroud: #F4F7FB;
$per-border-color:#DEE4EC;
.management-platform-wrap{
  min-height: 100%;
  .management-platform-sidebar {
  display: flex;
  position: fixed;
  left: 0;
  top: 54px;
  bottom: 0;
  width: 304px;
  background: #F8F9FC;
  transition: all .3s;
  &.sidebar-fold {
    width: 54px;
    .management-platform-sidebar-tree {
       transform: translateX(-1304px);
    }
  }
  .management-platform-sidebar-menu {
    z-index: 1;
    width: 54px;
    background: #F8F9FC;
    border-right: 1px solid #DEE4EC;
    &-item {
      height: 44px;
      line-height: 44px;
      text-align: center;
      cursor: pointer;
      &:hover {
        background: #ECEFF4;
      }
    }
  }
  .management-platform-sidebar-tree {
    position: absolute;
    width: 250px;
    left: 54px;
    top: 0px;
    bottom: 0px;
    transition: all .3s;
    padding: 8px 12px;
    overflow-y: auto;
    border-right: 1px solid #DEE4EC;
    &-header {
      font-size: 14px;
      font-weight: bold;
      font-family: PingFangSC-Medium;
      color: rgba(0,0,0,0.65);
      line-height: 34px;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
  }
  .management-platform-tablist {
    background-color: #fff;
  }
}
</style>
