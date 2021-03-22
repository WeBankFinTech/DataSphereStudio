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
            <Tabs v-model="showTab">
              <Tab-pane name="dev" :label="$t('message.workspace.DSSDEV')"></Tab-pane>
              <Tab-pane name="shop" :label="$t('message.workspace.DSSSHOP')"></Tab-pane>
            </Tabs>
            <div v-show="showTab === 'dev'">
              <porjectComponent
                ref="project"
                @open-workflow="openWorkflow"
              ></porjectComponent>
              <appProcess></appProcess>
            </div>
            <div v-show="showTab === 'shop'">
              <div class="app-shop-top">
                <h3 class="item-header">
                  <span>{{$t('message.workspace.QuickAccess')}}</span>
                  <!-- 设置按钮 -->
                  <div class="setting-bt-wrap" v-if="!setting">
                    <Button type="text" size="large" icon="md-settings" @click="setting=!setting">{{$t('message.workspace.home.setting')}}</Button>
                  </div>
                  <div class="setting-bt-wrap" v-else>
                    <Button type="text" size="large" icon="md-settings" @click="setting=!setting">{{$t('message.workspace.home.exit')}}</Button>
                  </div>
                </h3>
                <div class="app-list">
                  <div class="nodata-tips" v-if="favoriteApps.length===0">{{$t('message.workspace.home.tips')}}</div>
                  <div v-else v-for="(item, index) in favoriteApps" :key="item.title" class="app-item-wrap" :class="{shadow:setting}" @click="setting?null:linkTo(item, item.url)">
                    <div v-if="setting" class="close-wrap" @click.stop="deleteFavoriteApp(item.id, index)">
                      <Icon type="md-close" size="20"></Icon>
                    </div>
                    <SvgIcon class="app-icon" :icon-class="item.icon ? item.icon : 'shujukaifa-logo'" />
                    <span class="label">{{$t('message.workspace.home.enter', {text: item.title})}}</span>
                  </div>
                  <div class="app-item-add" @click="show = true">
                    <Icon type="md-add" size="25"></Icon>
                  </div>
                </div>
                <!-- <div class="more-util">
                  <Button type="text" size="large" icon="md-more" @click="moreUtil">{{ this.$t('message.workspace.more') }}</Button>
                </div> -->
              </div>
              <applist></applist>
            </div>
          </div>
        </div>
      </div>
      <div v-show="showTab === 'dev'" class="workspace-main-right">
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
    <Modal
      v-model="show"
      :title="$t('message.workspace.home.dlgTitle')">
      <Form
        ref="dynamicForm"
        :model="formDynamic"
        :label-width="100"
        :rules="rules"
      >
        <FormItem :label="$t('message.workspace.home.selectType')"
          prop="selectType">
          <Select v-model="formDynamic.selectType">
            <Option v-for="item in types" :value="`${item.id}`" :key="`${item.id}`">{{ item.title }}</Option>
          </Select>
        </FormItem>

        <FormItem :label="$t('message.workspace.home.selectApp')"
          prop="selectApp">
          <Select v-model="formDynamic.selectApp">
            <Option v-for="item in apps" :value="`${item.id}`" :key="`${item.id}`" :disabled="item.had||!item.active">{{ item.title }}</Option>
          </Select>
        </FormItem>
      </Form>

      <div slot="footer">
        <Button
          @click="show = false"
        >{{$t('message.workspace.home.cancel')}}</Button>
        <Button
          type="primary"
          @click="addFavoriteApp"
        >{{$t('message.workspace.home.save')}}</Button>
      </div>

    </Modal>
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import api from '@/common/service/api';
import util from '@/common/util';
import mixin from '@/common/service/mixin';
import porjectComponent from '../newProject';
import applist from '../appList';
import appProcess from '../appProcess/';
import { GetWorkspaceData, GetWorkspaceApplications } from '@/common/service/apiCommonMethod.js';
export default {
  components: {
    porjectComponent: porjectComponent.component,
    applist: applist.component,
    appProcess: appProcess.component
  },
  mixins: [mixin],
  data() {
    return {
      showTab: "dev",
      loading: false,
      workspaceData: {
        name: "",
        description: ""
      },
      video: {},
      videos: [],
      videoCache: [],
      videoSize: 4,
      videosClick: 1,
      videosMaxClick: null,
      sideDataList: [], // 右侧连接列表
      favoriteApps: [],
      setting: false,
      show: false,
      formDynamic: {
        selectType: "",
        selectApp: "",
      },
      applications: [],
      workspaceId: null,
      rules: {
        selectApp: [
          { required: true, message: this.$t('message.workspace.home.selectApp'), trigger: 'change' },
        ],
        selectType: [
          { required: true, message: this.$t('message.workspace.home.selectType'), trigger: 'change' },
        ],
      },
    }
  },
  created() {
    this.workspaceId = this.$route.query.workspaceId;
    this.init();
    this.getSideDatas();
  },
  watch: {
    $route() {
      this.workspaceId= this.$route.query.workspaceId; //获取传来的参数
      this.init();
    }
  },
  computed: {
    types(){
      return this.applications.map(item=>({title: item.title, id: item.id}))
    },
    apps(){
      if(this.formDynamic.selectType){
        const result = this.applications.find(item=>item.id==this.formDynamic.selectType);
        result.appInstances.map(item=>{
          item.had = false;
          if(this.favoriteApps.find(fItem=>item.id===fItem.menuApplicationId)){
            item.had = true;
          }
        })
        return result.appInstances;
      }
      return [];
    },
  },
  methods: {
    // 进入工作流
    openWorkflow() {

    },
    // 进入知识库
    gotoKnowledge() {
      util.windowOpen(`http://${window.location.host}/wiki/scriptis`);
    },
    // 进入论坛
    gotoFAQ() {
      util.windowOpen(`http://${window.location.host}/kn`);
    },
    linkTo(item){
      this.gotoCommonIframe(item.name, {workspaceId: this.workspaceId})
    },
    moreUtil() {
      this.$router.push({ path: '/workspace', query: {...this.$route.query}});
    },
    init(){
      this.$router.app.$on("getChangeCookies", () => {
        // 因为会重复注册一次，所以要触发后清除事件
        this.$router.app.$off("getChangeCookies");
        GetWorkspaceData(this.workspaceId).then(data=>{
          this.workspaceData = data.workspace;
        })

        api.fetch(`${this.$API_PATH.WORKSPACE_PATH}workspaces/${this.workspaceId}/favorites`, 'get').then(data=>{
          this.favoriteApps = data.favorites;
        })

        GetWorkspaceApplications(this.workspaceId).then(data=>{
          this.applications = data.applications || [];
        })
      });
    },
    deleteFavoriteApp(favoriteId, index){
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}workspaces/${this.workspaceId}/favorites/${favoriteId}`, 'delete').then(()=>{
        this.favoriteApps.splice(index, 1);
      })
    },
    addFavoriteApp(){
      this.$refs.dynamicForm.validate((valid) => {
        if (valid) {
          // this.addAppLoading = true;
          const menuApplicationId = parseInt(this.formDynamic.selectApp, 10);
          const favoriteApp = this.findFavoriteAppsByMenuId(menuApplicationId);
          if(favoriteApp) {
            return this.$Message.error(`${this.$t('message.workspace.home.repeat')}`);
          }
          this.show = false;
          api.fetch(`${this.$API_PATH.WORKSPACE_PATH}workspaces/${this.workspaceId}/favorites`, {menuApplicationId: menuApplicationId},'post').then(data=>{
            const app = this.findAppByApplicationId(this.formDynamic.selectApp)
            this.favoriteApps.push({
              ...app,
              "id": data.favoriteId,
              "menuApplicationId": app.id, //application表里的id
            });

          })
        }
      });
    },
    findAppByApplicationId(id){
      const arr = this.flatApps();
      const result = arr.find(item=>item.id==id);
      return result;
    },

    findFavoriteAppsByMenuId(menuId){
      const arr = this.favoriteApps;
      const result = arr.find(item=>item.menuApplicationId==menuId);
      return result;
    },

    flatApps(){
      const arr = [];
      this.applications.forEach(item=>{
        arr.push(...item.appInstances)
      })
      return arr;
    },
    iconSplit(icon){
      if(icon){
        return icon.split('|')
      }
      return ['','']
    },
    // 打开新页面查看问题
    openBrowser(item) {
      // 有可能是自己系统相对路径和，其他系统绝对路径
      if (item.urlType === 0) {
        this.$router.push({path: item.url, query: this.$route.query})
      } else {
        util.windowOpen(item.url);
      }
    },
    // 获取常见问题
    getSideDatas() {
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getSideInfos`, {
        workspaceID: 106
      }, 'get').then((res) => {
        this.sideDataList = res.presentations
      })
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
