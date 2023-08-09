<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <div class="info-name">
          <span>{{$t('message.workspace.workspaceTitle')}}</span>
          <span class="workspace-name">{{ workspaceData.name }}</span>
        </div>
        <!-- <h1>{{$t('message.workspace.workspaceHeader', {name: workspaceData.name})}}</h1> -->
        <p>{{$t('message.workspace.workspaceHeaderDesc')}}</p>
      </div>
    </div>
    <div class="workspace-main">
      <div class="workspace-main-left">
        <div class="app-list-main">
          <div class="app-list-tabs">
            <porjectComponent
            ></porjectComponent>
            <appProcess></appProcess>
          </div>
        </div>
      </div>
      <div class="workspace-main-right">
        <div class="right-box" v-for="(item, i) in sideDataList" :key="item.key">
          <div class="knowledge-header">
            <span class="title">{{ item.name }}</span>
          </div>
          <ul class="knowledge-list">
            <li v-for="(subItem, index) in item.contents" :key="index" class="knowledge-item" @click="openBrowser(subItem)">
              <SvgIcon class="side-icon" :icon-class="subItem.icon"/>
              {{subItem.title}}
            </li>
          </ul>
          <div v-if="i !== sideDataList.length - 1" class="line"></div>
        </div>
      </div>
    </div>
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import mixin from '@dataspherestudio/shared/common/service/mixin';
import porjectComponent from '../newProject';
import appProcess from '../appProcess/';
import util from '@dataspherestudio/shared/common/util';
import { GetWorkspaceData } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
export default {
  components: {
    porjectComponent: porjectComponent.component,
    appProcess: appProcess.component
  },
  mixins: [mixin],
  data() {
    return {
      loading: false,
      workspaceData: {
        name: "",
        description: ""
      },
      sideDataList: [], // 右侧连接列表
      workspaceId: null,
    }
  },
  created() {
    this.$router.app.$off("getChangeCookies");
    this.init();
    this.getSideDatas();
  },
  watch: {
    $route() {
      this.init();
      this.getSideDatas();
    }
  },
  computed: {
  },
  methods: {
    init() {
      this.workspaceId= this.$route.query.workspaceId; //获取传来的参数
      sessionStorage.removeItem(`work_flow_lists_${this.workspaceId}`)
      this.$router.app.$on("getChangeCookies", () => {
        // 因为会重复注册一次，所以要触发后清除事件
        this.$router.app.$off("getChangeCookies");
        GetWorkspaceData(this.workspaceId).then(data=>{
          this.workspaceData = data.workspace;
        })
      });
    },
    // 打开新页面查看问题
    openBrowser(item) {
      // 有可能是自己系统相对路径和，其他系统绝对路径
      if (item.urlType === 0) {
        this.$router.push({path: item.url, query: this.$route.query})
      } else {
        util.windowOpen(item.url);
        // this.$router.push({
        //   path: `/commonIframe/${item.title}`,
        //   query: {
        //     ...this.$route.query,
        //     __noreplace: 1,
        //     url: item.url
        //   }
        // })
      }
    },
    // 获取常见问题
    getSideDatas() {
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getSideInfos`, {
        workspaceID: this.workspaceId
      }, 'get').then((res) => {
        this.sideDataList = res.presentations
      })
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
