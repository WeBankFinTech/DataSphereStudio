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
        <div class="settings nav-item" v-if="showSetting" @click="showSettingModal">
          <SvgIcon class="nav-icon" icon-class="icon-shezhi" style="fontSize: 18px;" />
        </div>
      </div>
    </div>
    <div class="cur-proxy-user">
      <Tooltip v-if="proxyUserName" :content="`当前代理用户：${proxyUserName}`" placement="right">
        <SvgIcon class="nav-icon" icon-class="user" style="fontSize: 18px;" />
      </Tooltip>
    </div>
    <we-panel
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
            ref="workbenchContainer"
            :width="props.width"
            v-if="props.width"/>
        </template>
      </we-panel-item>
    </we-panel>
    <settingModal ref="settingBtn" />
  </div>
</template>
<script>
import workbenchModule from '@/scriptis/module/workbench';
import workSidebarModule from '@/scriptis/module/workSidebar';
import fnSidebarModule from '@/scriptis/module/fnSidebar';
import hiveSidebarModule from '@/scriptis/module/hiveSidebar';
import hdfsSidebarModule from '@/scriptis/module/hdfsSidebar';
import settingModal from '@/scriptis/module/setting';
import storage from '@dataspherestudio/shared/common/helper/storage';

export default {
  components: {
    workbench: workbenchModule.component,
    workSidebar: workSidebarModule.component,
    fnSidebar: fnSidebarModule.component,
    hiveSidebar: hiveSidebarModule.component,
    hdfsSidebar: hdfsSidebarModule.component,
    settingModal
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
      showSetting: false,
      proxyUserName: ''
    };
  },
  //组建内的守卫
  beforeRouteLeave(to, from, next) {
    if (this.$refs.workbenchContainer) {
      let hasUnsave = false;
      if (this.$refs.workbenchContainer.worklist) {
        // 检查是否有未保存的非临时脚本
        hasUnsave = this.$refs.workbenchContainer.worklist.some(
          (item) => item.unsave && !item.saveAs
        );
      }

      if (hasUnsave) {
        // 提示保存，用户选择不保存则继续跳转
        this.$Modal.confirm({
          title: "正在编辑的代码未保存，请先检查",
          content: "点击确定去检查，点击取消不保存，继续跳转",
          okText: "",
          cancelText: "",
          onOk: () => {},
          onCancel: () => {
            next(); //如果用户点击取消 则直接跳转到用户点击的路由页面
          },
        });
      } else {
        next();
      }
    }
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
    const baseInfo = storage.get("baseInfo", "local") || {}
    if (baseInfo.proxyEnable && !baseInfo.proxyUserName) {
      this.showSettingModal()
    } else if(baseInfo.proxyUserName) {
      this.proxyUserName = baseInfo.proxyUserName
    }
    this.showSetting = !!baseInfo.proxyEnable
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
    showSettingModal() {
      this.$refs.settingBtn.toggle()
    }
  },
};
</script>
<style lang="scss" src="../../assets/styles/home.scss">
</style>
