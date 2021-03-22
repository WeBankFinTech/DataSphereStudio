<template>
  <div class="home-page">
    <div
      class="nav-list"
      :class="{'narrow': !(navHeight >= maxNavHeight &&　navHeight　> 790)}">
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
        <workSidebar v-if="leftModule.key === 2"/>
        <hiveSidebar v-if="leftModule.key === 3"/>
        <fnSidebar
          v-if="leftModule.key === 4"
          type="udf"
          key="udf"/>
        <fnSidebar
          v-if="leftModule.key === 5"
          type="method"
          key="method"/>
        <hdfsSidebar v-if="leftModule.key === 6"/>
      </we-panel-item>
      <we-panel-item
        :index="2"
        class="center-panel">
        <template slot-scope="props">
          <workbench
            :width="props.width"
            v-if="props.width"/>
        </template>
      </we-panel-item>
    </we-panel>
  </div>
</template>
<script>
import workbenchModule from '@/apps/scriptis/module/workbench';
import workSidebarModule from '@/apps/scriptis/module/workSidebar';
import fnSidebarModule from '@/apps/scriptis/module/fnSidebar';
import hiveSidebarModule from '@/apps/scriptis/module/hiveSidebar';
import hdfsSidebarModule from '@/apps/scriptis/module/hdfsSidebar';
export default {
  components: {
    workbench: workbenchModule.component,
    workSidebar: workSidebarModule.component,
    fnSidebar: fnSidebarModule.component,
    hiveSidebar: hiveSidebarModule.component,
    hdfsSidebar: hdfsSidebarModule.component,
  },
  data() {
    return {
      leftSideNavList: [
        { key: 2, name: this.$t('message.scripts.navList.workSpace'), isActive: false, icon: 'fi-folder' },
        { key: 3, name: this.$t('message.scripts.navList.database'), isActive: false, icon: 'fi-hivedb' },
        { key: 4, name: this.$t('message.scripts.navList.udfFunction'), isActive: false, icon: 'fi-fx-udf' },
        { key: 5, name: this.$t('message.scripts.navList.methodFunction'), isActive: false, icon: 'fi-fx-method' },
        { key: 6, name: this.$t('message.scripts.navList.hdfs'), isActive: false, icon: 'fi-disk' },
      ],
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
  },
  mounted() {
    this.init();
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.getHeight);
  },
  beforeDestroy() {
    // 监听窗口变化，获取浏览器宽高
    window.removeEventListener('resize', this.getHeight);
  },
  methods: {
    getHeight() {
      this.resize(window.innerHeight);
    },
    init() {
      this.chooseLeftModule(this.leftSideNavList[0]);
      this.resize(window.innerHeight);
    },
    chooseLeftModule(item) {
      if (this.leftModule && this.leftModule.key !== item.key) {
        this.leftModule.isActive = false;
      }
      item.isActive = !item.isActive;
      this.leftModule = item.isActive ? item : null;
    },
    resize(height) {
      this.navHeight = height;
    },
  },
};
</script>
<style lang="scss" src="@/apps/scriptis/assets/styles/home.scss">
</style>
