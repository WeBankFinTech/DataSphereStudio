<template>
  <div class="newHome_page">
    <!-- 首页标题 -->
    <div class="newHome_header">
      <div class="newHome_info">
        <h1>{{$t('message.workspace.infoHeader')}}</h1>
        <p>{{$t('message.workspace.infoBodyFirstRow')}}</p>
      </div>
    </div>
    <!-- 内容主体 -->
    <div class="newHome_main">
      <!-- 左侧内容 -->
      <div class="newHome_main_left">
        <!-- 左侧内容标题和搜索框及显示切换按钮 -->
        <div class="newHome_title_search">
          <!-- 左侧标题 -->
          <h3 class="left_header">
            {{ $t('message.workspace.workspaceList') }}
          </h3>
          <!-- 左侧搜索框内容和切换按钮 -->
          <div class="left_search">
            <!-- 切换显示方式按钮 -->
            <div class="left_visual" @click="changeVisual">
              <SvgIcon color="rgba(0,0,0,0.65)" :iconClass="visualCatesIcon"/>
              <span>
                {{ visualCatesTitle }}
              </span>
            </div>
            <!-- 搜索框 -->
            <div class="header-search">
              <Input
                search
                class="search-input"
                :placeholder="$t('message.workspace.searchWorkspace')"
                @on-search="searchWorkspace"/>
            </div>
          </div>
        </div>
        <!-- 左侧列表内容和卡片切换内容 -->
        <div class="newHome_list">
          <!-- 卡片显示 -->
          <ul class="card-content" ref="row" v-show="!visual">
            <!-- 新增工作空间 -->

            <li class="newHome_create" @click="createWorkspace">
              <div class="newHome_create_content">
                <SvgIcon :style="{ 'font-size': '20px' }" icon-class="fi-addproject"/>
                <p class="newHome_create_text">
                  {{ $t('message.workspace.createWorkspace') }}
                </p>
              </div>
            </li>
            <!-- 卡片显示数据 -->

            <li class="newHome_list_item"
              v-for="item in filteredData"
              :key="item.id"
              @click="gotoWorkspace(item)">
              <div class="item-header">
                <span class="name">{{item.name}}</span>
              </div>
              <SvgIcon
                v-if="isAdminAndSingleServe"
                title="管理"
                class="management-button"
                :style="{ 'font-size': '20px' }"
                icon-class="setting"
                @click.stop="gotoManagement(item.id)"/>

              <p class="desc">{{item.description}}</p>
              <!-- <ul class="lable-list">
                <template v-for="(tag, tagIndex) in item.label.split(',')">
                  <li v-if="tag && tagIndex <= 2"  class="item" :key="tagIndex">{{tag}}</li>
                </template>
              </ul> -->
            </li>
          </ul>
          <!-- 表格显示 -->
          <div v-show="visual" class="newHome_table">
            <WorkspaceTable
              :data="filteredData"
              :isAdminAndSingleServe="isAdminAndSingleServe"
              @createWorkspace="createWorkspace"
              @namagement="gotoManagement"
            />
          </div>
        </div>
      <!-- <caseAndIntroduction /> -->
      </div>
      <div class="newHome_main_right">
        <div class="right_header">
          <h3 class="title">{{$t('message.workspace.KSRM')}}</h3>
          <Button type="text" @click="changeVideos">{{$t('message.workspace.HYP')}}</Button>
        </div>
        <div class="admin-box-video">
          <div v-for="(item, index) in videos" :key="index" class="video-item" @click="play(item)">
            <video width="100%" height="100" controls>
              <source :src="item.url" type="video/mp4" />
            </video>
            <h3 class="video-title">{{item.title}}</h3>
          </div>
        </div>
      </div>
    </div>
    <WorkspaceForm
      ref="workspaceForm"
      :action-type="actionType"
      :project-data="currentWorkspaceData"
      :add-project-show="workspaceShow"
      @show="workspaceShowAction"
      @confirm="workspaceConfirm"></WorkspaceForm>
    <Modal
      v-model="showVideo"
      :title="video.title"
      :footer-hide="true"
      width="800"
    >
      <video v-if="showVideo" width="100%" controls autoplay>
        <source :src="video.url" type="video/mp4" />
      </video>
    </Modal>
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import api from '@/common/service/api';
// import caseAndIntroduction from '../caseAndIntroduction';
import WorkspaceForm from './module/workspaceForm.vue';
import WorkspaceTable from './module/workspaceTable.vue';
import { GetBaseInfo, GetWorkspaceList } from '@/common/service/apiCommonMethod.js';
import util from '@/common/util';

export default {
  components: {
    WorkspaceForm,
    WorkspaceTable,
    // caseAndIntroduction: caseAndIntroduction.component,
  },
  data() {
    return {
      loading: false,
      showVideo: false,
      actionType: '',
      currentWorkspaceData: {
        name: '',
        description: '',
        label: '',
        department: ''
      },
      workspaceShow: false,
      cacheData: [],
      filteredData: [],
      video: {},
      videos: [],
      videoCache: [],
      visual: false,  // false 为默认值显示卡片模式，反之列表模式
      visualCatesTitle: this.$t('message.workspace.tableDisplay'),
      visualCatesIcon: 'fi-table',
      visualCates: [
        { icon: 'fi-table', title: this.$t('message.workspace.tableDisplay') },
        { icon: 'menu', title: this.$t('message.workspace.cardDisplay') },
      ],
      total: null,
      pageSize: 6,
      videoSize: 4,
      pageNum: 1,
      videosClick: 1,
      videosMaxClick: null,
      listWrap: 0,//屏幕宽度
      isAdminAndSingleServe: false
    }
  },
  created() {
    this.getWorkspaces();
    this.getVideos();
    GetBaseInfo().then((res) => {
      const currentModules = util.currentModules();
      // 是管理员且是数据服务的微服务
      this.isAdminAndSingleServe = (res.isAdmin && currentModules.microModule === 'apiServices');
    })
  },
  mounted(){
  },
  computed: {

  },
  beforeDestroy(){
  },
  watch: {

  },
  methods: {
    getWorkspaces() {
      // 获取工作空间数据
      this.loading = true;
      GetWorkspaceList({}, 'get').then((res) => {
        this.filteredData = this.cacheData = res.workspaces;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    getVideos() {
      this.loading = true;
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}workspaces/videos`, {}, 'get').then((res) => {
        this.videoCache = res.videos;
        this.initVideos();
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    createWorkspace() {
      this.actionType = 'add';
      this.workspaceShow = true;
      this.currentWorkspaceData = {
        name: '',
        description: '',
        label: '',
        department: '',
      }
    },
    editor(workspace) {
      this.actionType = 'editor';
      this.workspaceShow = true;
      this.currentWorkspaceData = workspace;
    },
    searchWorkspace(value) {
      // 通过名称模糊搜索
      value = value.trim()
      if (value) {
        this.filteredData = this.cacheData.filter((item) => item.name.indexOf(value) != -1);
      } else {
        this.filteredData = this.cacheData;
      }
    },
    gotoWorkspace(workspace) {
      const workspaceId = workspace.workspaceId || workspace.id;
      const currentModules = util.currentModules();
      if (currentModules.microModule === 'apiServices') {
        this.$router.push({ path: '/apiservices', query: { workspaceId: workspaceId}});
      } else {
        this.$router.push({ path: '/workspaceHome', query: { workspaceId: workspaceId}});
      }
    },
    workspaceShowAction(val) {
      this.workspaceShow = val;
    },
    workspaceConfirm(params) {
      GetWorkspaceList(params, 'post').then((res) => {
        this.$Message.success(this.$t('message.workspace.createdSuccess'));
        // 创建成功后跳到工作空间首页
        this.gotoWorkspace(res);
      }).catch(() => {
        this.$Message.error(this.$t('message.workspace.createdFailed'));
      })
    },
    changeVisual() {
      this.visual = !this.visual;
      if(this.visual) {
        this.visualCatesTitle = this.visualCates[1].title;
        this.visualCatesIcon = this.visualCates[1].icon;
      } else {
        this.visualCatesTitle = this.visualCates[0].title;
        this.visualCatesIcon = this.visualCates[0].icon;
      }
    },
    changeVideos() {
      this.videosClick += 1;
      this.videosClick = this.videosClick > this.videosMaxClick ? 1 : this.videosClick;
      const start = ( this.videosClick - 1 ) * this.videoSize;
      const end = this.videosClick * this.videoSize;
      this.videos = this.videoCache.slice(start, end);
    },
    initVideos() {
      const videosTotal = this.videoCache.length;
      this.videosMaxClick = Math.ceil(videosTotal / this.videoSize);
      if (videosTotal < this.videoSize) {
        this.videos = this.videoCache;
      } else {
        this.videos = this.videoCache.slice(0, this.videoSize);
      }
    },
    play(item) {
      this.showVideo = true;
      this.video = item
    },
    // 跳转管理页
    gotoManagement(id) {
      this.$router.push({ path: '/workspaceManagement/productsettings', query: { workspaceId: id}});
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
