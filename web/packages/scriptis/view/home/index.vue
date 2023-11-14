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
            v-if="props.width"
            @get-dbtable-length="showTip" />
        </template>
      </we-panel-item>
    </we-panel>
    <settingModal ref="settingBtn" />
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import workbenchModule from '@/scriptis/module/workbench';
import workSidebarModule from '@/scriptis/module/workSidebar';
import fnSidebarModule from '@/scriptis/module/fnSidebar';
import hiveSidebarModule from '@/scriptis/module/hiveSidebar';
import hdfsSidebarModule from '@/scriptis/module/hdfsSidebar';
import settingModal from '@/scriptis/module/setting';
import storage from '@dataspherestudio/shared/common/helper/storage';
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';

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
    const close = () => {
      if (window.languageClient) {
        // 用户退出，后端语言服务子进程无法关闭，要求前端发送关闭
        try {
          window.languageClient.__connected_sql_langserver = false;
          window.languageClient.__connected_py_langserver = false;
          if (window.languageClient.__webSocket_sql_langserver) {
            window.languageClient.sql.sendNotification('textDocument/changePage')
            window.languageClient.__webSocket_sql_langserver.close();
          }
          if (window.languageClient.__webSocket_py_langserver) {
            window.languageClient.python.sendNotification('textDocument/changePage')
            window.languageClient.__webSocket_sql_langserver.close();
          }

        } catch (error) {
          //
        }

      }
    }
    if (to.query.notcheck) {
      close();
      next();
      return
    }
    let hasUnsave = this.checkUnsave((save)=>{
      if (!save) {
        close();
        next();
      } else {
        next({...from, query: {...from.query, unsave: Date.now()}})
      }
    })
    if (!hasUnsave) {
      close()
      next();
    }
  },
  computed: {
    maxNavHeight() {
      const navItemHeight = 115;
      return navItemHeight * this.leftSideNavList.length + 100;
    },
  },
  async created() {
    let baseInfo = storage.get('baseInfo', 'local') || {}
    const globalRes = await this.getGlobalLimit()
    baseInfo = {
      ...baseInfo,
      ...globalRes.globalLimits
    }
    storage.set('baseInfo', baseInfo, 'local')
    // languageServerDefaultEnable = true 默认启用language server
    const uselsp = localStorage.getItem('scriptis-edditor-type')
    if (baseInfo.languageServerDefaultEnable && uselsp === null ) {
      localStorage.setItem('scriptis-edditor-type', 'lsp');
      location.reload();
    }
  },
  mounted() {
    this.init();
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.getHeight);
    eventbus.clear('check.scriptis.unsave');
    eventbus.on('check.scriptis.unsave', () => {
      return new Promise((resolve) => {
        const hasUnsave = this.checkUnsave((unsave)=>{
          resolve(unsave)
        })
        if (!hasUnsave) {
          resolve(false)
        }
      })
    });
    setTimeout(() => {
      const baseInfo = storage.get('baseInfo', 'local');
      if (baseInfo.proxyEnable && !baseInfo.proxyUserName) {
        this.showSettingModal()
      } else if(baseInfo.proxyUserName) {
        this.proxyUserName = baseInfo.proxyUserName
      }
      this.showSetting = !!baseInfo.proxyEnable;
    }, 1500)
  },
  beforeDestroy() {
    // 监听窗口变化，获取浏览器宽高
    this.$Notice.close('show-db-table-many-tip')
    window.removeEventListener('resize', this.getHeight);
  },
  methods: {
    getGlobalLimit() {
      return api.fetch(`/dss/scriptis/globalLimits`, {}, {
        method: 'get',
        cacheOptions: {time: 3000}
      })
    },
    getHeight() {
      this.resize(window.innerHeight);
    },
    showTip(length) {
      if (length > 30000) {
        this.$Notice.close('show-db-table-many-tip')
        this.$Notice.warning({
          duration: 0,
          name: 'show-db-table-many-tip',
          title: this.$t('message.scripts.propmpt'),
          desc: this.$t('message.scripts.largedatatip')
        })
      }
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
    },
    checkUnsave(cb) {
      let hasUnsave = false
      if (this.$refs.workbenchContainer) {
        if (this.$refs.workbenchContainer.worklist) {
          // 检查是否有未保存的非临时脚本
          hasUnsave = this.$refs.workbenchContainer.worklist.some(
            (item) => item.unsave && !item.saveAs
          );
        }
        if (hasUnsave) {
          // 提示保存，用户选择不保存则继续跳转
          this.$Modal.confirm({
            title: this.$t('message.scripts.notsaved'),
            content: this.$t('message.scripts.confirmcheck'),
            okText: "",
            cancelText: "",
            onOk: () => {
              if (cb) {
                cb(true)
              }
            },
            onCancel: () => {
              if (cb) {
                cb(false)
              }
            },
          });
        }
      }
      return hasUnsave
    }
  },
};
</script>
<style lang="scss" src="../../assets/styles/home.scss">
</style>
