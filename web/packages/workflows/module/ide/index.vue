<template>
  <div class="ide">
    <div
      class="nav-list"
      :class="{'narrow': !(navHeight >= maxNavHeight &&　navHeight　> 790)}"
      v-if="leftSideNavList.length">
      <div class="nav-warp">
        <div
          v-for="(item, index) in leftSideNavList"
          :class="{ actived: item.isActive }"
          :key="index"
          class="nav-item"
          @click="chooseLeftModule(item)"
          :title="item.name">
          <SvgIcon class="nav-icon" :icon-class="item.icon"/>
          <span
            v-if="navHeight >= maxNavHeight &&　navHeight　> 790"
            class="nav-name"
          >
            {{ item.name }}
          </span>
        </div>
      </div>
    </div>
    <we-panel
      class="container"
      diretion="horizontal">
      <we-panel-item
        v-if="leftModule"
        :index="1"
        :width="200"
        :min-size="200"
        :max-size="400"
        class="left-panel">
        <workSidebar v-if="leftModule.key === 'workSpace'"/>
        <hiveSidebar
          v-if="leftModule.key === 'database'"
          :node="node"
          :model="model"/>
        <fnSidebar
          v-if="leftModule.key === 'udfFunction'"
          type="udf"
          key="udf"
          :node="node"
          :model="model"/>
        <fnSidebar
          v-if="leftModule.key === 'methodFunction'"
          type="method"
          :node="node"
          key="method"
          :model="model"/>
        <hdfsSidebar v-if="leftModule.key === 'hdfs'"/>
      </we-panel-item>
      <we-panel-item
        :index="2"
        class="center-panel">
        <template slot-scope="props">
          <workbench
            :width="props.width"
            :parameters="parameters"
            :node="node"
            :readonly="readonly"
            v-if="props.width"/>
        </template>
      </we-panel-item>
    </we-panel>
  </div>
</template>
<script>
import workbenchModule from '@dataspherestudio/scriptis/module/workbench';
import workSidebarModule from '@dataspherestudio/scriptis/module/workSidebar';
import fnSidebarModule from '@dataspherestudio/scriptis/module/fnSidebar';
import hiveSidebarModule from '@dataspherestudio/scriptis/module/hiveSidebar';
import hdfsSidebarModule from '@dataspherestudio/scriptis/module/hdfsSidebar';
import { NODETYPE } from '@/workflows/service/nodeType'
export default {
  components: {
    workbench: workbenchModule.component,
    workSidebar: workSidebarModule.component,
    fnSidebar: fnSidebarModule.component,
    hiveSidebar: hiveSidebarModule.component,
    hdfsSidebar: hdfsSidebarModule.component,
  },
  props: {
    parameters: {
      type: Object,
      default: () => {
        return {
          content: '',
          params: {},
        };
      },
    },
    node: Object,
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      leftSideNavList: [],
      leftModule: null,
      level: 0,
      navHeight: 0,
    };
  },
  computed: {
    maxNavHeight() {
      const navItemHeight = 115;
      return navItemHeight * this.leftSideNavList.length + 100;
    },
    model() {
      return this.node && (this.node.type.split('.')[1] || '');
    },
  },
  mounted() {
    this.init();
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.resize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize);
  },
  methods: {
    init() {
      this.leftSideNavList = this.setLeftSideNavList();
      if (!this.node || this.node.type !== NODETYPE.SHELL) {
        this.chooseLeftModule(this.leftSideNavList[0]);
      }
      this.resize();
    },
    setLeftSideNavList() {
      const workSpace = { key: 'workSpace', name: this.$t('message.workflow.navList.workSpace'), isActive: false, icon: 'fi-folder' };
      const database = { key: 'database', name: this.$t('message.workflow.navList.database'), isActive: false, icon: 'fi-hivedb' };
      const udfFunction = { key: 'udfFunction', name: this.$t('message.workflow.navList.udfFunction'), isActive: false, icon: 'fi-fx-udf' };
      const methodFunction = { key: 'methodFunction', name: this.$t('message.workflow.navList.methodFunction'), isActive: false, icon: 'fi-fx-method' };
      const hdfs = { key: 'hdfs', name: this.$t('message.workflow.navList.hdfs'), isActive: false, icon: 'fi-disk' };
      let menuList = [workSpace, database, udfFunction, methodFunction, hdfs];
      if (this.node) {
        if (this.node.type === NODETYPE.SHELL) {
          menuList = [];
        } else {
          menuList = [database, udfFunction, methodFunction];
        }
      }
      return menuList;
    },
    chooseLeftModule(item) {
      if (this.leftModule && this.leftModule.key !== item.key) {
        this.leftModule.isActive = false;
      }
      item.isActive = !item.isActive;
      this.leftModule = item.isActive ? item : null;
    },
    resize() {
      this.navHeight = window.innerHeight;
    },
    'IDE:saveNode'(args, showTips = true) {
      if (args.node && this.node.key === args.node.key) {
        args.showTips = showTips;
        this.$emit('save', args);
      }
    },
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
