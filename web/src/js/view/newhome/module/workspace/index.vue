<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1><span>{{$t('message.workspace.infoHeader')}}</span><span class="second-title">{{$t('message.project.infoBodyFirstSubtitle')}}</span></h1>
        <p>{{$t('message.project.infoBodyFirstRow')}}</p>
        <!-- <p>{{$t('message.project.infoBodySecondRow')}}</p> -->
      </div>
    </div>
    <div class="workspace-main">
      <div class="workspace-main-left">
        <Card class="left">
          <div class="workspace-create-search">
            <div class="workspace-create">
              <svg t="1572508997540" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3412" xmlns:xlink="http://www.w3.org/1999/xlink" width="30" height="30">
                <path d="M468.89187555 244.16003161L405.84786173 124.9330568H77.68240987v774.65170172h368.68449976c-9.0617679-19.02971259-16.44063605-38.96560197-21.8776968-59.54876049H133.6064632V303.7087921h782.03056989v197.15817876c7.24941431 6.47269136 15.01664395 12.03920592 21.74824296 19.15916642 12.81592889 13.72210569 23.81950419 28.60929581 34.04635654 44.14375507V244.16003161H468.89187555z m-335.28541235-59.67821433h239.74848791l31.45728 59.5487605H133.6064632v-59.5487605z m614.38786371 536.32720592h111.71865284v59.5487605h-111.71865284v119.22697482h-55.92405333v-119.22697482h-111.71865283v-59.5487605h111.71865283v-119.22697481h55.92405333v119.22697481z m-27.83257283-238.32449579c-138.77450272 0-251.39933235 120.00369778-251.39933235 268.09887606 0 148.09517827 112.49537581 268.22832987 251.26987852 268.22832986 138.90395653 0 251.39933235-120.00369778 251.39933234-268.09887605 0-71.07015111-26.40858075-139.29231803-73.52977383-189.64985678s-111.0713837-78.57847309-177.74010468-78.57847309z m-0.12945383 476.64899161c-107.83503803-0.25890765-195.21637136-93.4656632-195.34582519-208.55011555 0.12945383-115.21390617 87.51078717-208.42066173 195.47527902-208.67956939 52.16989235 0 101.36234667 21.74824297 138.2566874 61.10220642 37.02379457 39.35396347 57.21859161 91.78276347 57.21859161 147.57736297-0.12945383 115.21390617-87.64024098 208.55011555-195.60473284 208.55011555z" fill="#2d8cf0" p-id="3413"></path>
              </svg>
              <p
                class="workspace-create-text"
                style="color:#2d8cf0"
                @click="createWorkspace">
                {{ $t('message.workspace.createWorkspace') }}
              </p>
            </div>
            <div class="header-search">
              <Input
                search
                class="search-input"
                :placeholder="$t('message.workspace.searchWorkspace')"
                @on-search="searchWorkspace"/>
            </div>
            <div class="workspace-header-right">
              <Dropdown @on-click="changeVisual">
                <Icon type="gear-a" />
                <a>
                  {{ $t('message.workspace.display') }}
                </a>
                <Dropdown-menu slot="list">
                  <Dropdown-item v-for="(item, index) in visualCates" :key="index" :name="item.name">{{item.title}}</Dropdown-item>
                </Dropdown-menu>
              </Dropdown>
            </div>
          </div>
          <div class="workspace-content">
            <h3 class="item-header">
              <span>{{ $t('message.workspace.workspaceList') }}</span>
            </h3>
            <Row ref="row" class="content-item" v-show="visual === 'card'">
              <i-col
                ref="col"
                class="workspace-item"
                :xs="12" :sm="8" :md="4" :lg="4"
                v-for="item in originWorkspaceData"
                :key="item.id"
                @click.native="gotoWorkspace(item)"
              >
                <span class="name">{{item.name}}</span>
                <p class="desc">{{item.description}}</p>
                <ul class="lable-list">
                  <template v-for="(tag, tagIndex) in item.label.split(',')">
                    <li v-if="tag && tagIndex <= 2"  class="item" :key="tagIndex">{{tag}}</li>
                  </template>
                </ul>
              </i-col>
            </Row>
            <div v-show="visual === 'table'" class="workspace-table">
              <WorkspaceTable
                :data="originWorkspaceData"
              />
            </div>
            <div class="page">
              <Page
                :total="total"
                size="small"
                :page-size="pageSize"
                :current.sync="pageNum"
                @on-change="changePage"
              />
            </div>
          </div>
        </Card>
        <caseAndIntroduction />
      </div>
      <div class="workspace-main-right">
        <div class="video-header">
          <p class="title">{{$t('message.GLY.KSRM')}}</p>
          <Button type="text" @click="changeVideos">{{$t('message.GLY.HYP')}}</Button>
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
import api from '@/js/service/api';
import caseAndIntroduction from '../caseAndIntroduction';
import WorkspaceForm from './module/workspaceForm.vue';
import WorkspaceTable from './module/workspaceTable.vue';
import storage from "@/js/helper/storage";

export default {
  components: {
    WorkspaceForm,
    WorkspaceTable,
    caseAndIntroduction: caseAndIntroduction.component,
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
      originWorkspaceData: [],
      cacheData: [],
      filteredData: [],
      visual: 'card',
      video: {},
      videos: [],
      videoCache: [],
      visualCates: [
        { name: 'table', title: this.$t('message.workspace.tableDisplay') },
        { name: 'card', title: this.$t('message.workspace.cardDisplay') }
      ],
      total: null,
      pageSize: 4,
      videoSize: 4,
      pageNum: 1,
      videosClick: 1,
      videosMaxClick: null,
      listWrap: 0,//屏幕宽度
      workspaceItemWidth: 252, // workspaceItem 宽度
    }
  },
  created() {
    this.getWorkspaces();
    this.getVideos();
  },
  mounted(){
    this.listWrap = this.$refs.row.$el && this.$refs.row.$el.offsetWidth;
    window.onresize = () => {
      const that = this
      return (() => {
        that.listWrap = this.$refs.row.$el && this.$refs.row.$el.offsetWidth;
      })()
    }
  },
  beforeDestroy(){
    window.onresize = null;
  },
  watch: {
    'listWrap': function(val){ //监听容器宽度变化
      this.pageSize = Math.floor(val / this.workspaceItemWidth)
      this.initWorkspace();
    },
  },
  methods: {
    getWorkspaces() {
      // 获取工作空间数据
      this.loading = true;
      api.fetch('dss/workspaces', {}, 'get').then((res) => {
        this.originWorkspaceData = this.filteredData = this.cacheData = res.workspaces;
        this.initWorkspace();
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    initWorkspace() {
      this.total = this.cacheData.length;
      if (this.total < this.pageSize){
        this.originWorkspaceData = this.cacheData;
      } else {
        this.originWorkspaceData = this.cacheData.slice(0, this.pageSize);
      }
    },
    changePage(currentPage){
      const start = ( currentPage - 1 ) * this.pageSize;
      const end = currentPage * this.pageSize;
      this.originWorkspaceData = this.filteredData.slice(start, end);
    },
    getVideos() {
      this.loading = true;
      api.fetch('dss/workspaces/videos', {}, 'get').then((res) => {
        this.videoCache = res.videos;
        this.initVideos();
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    createWorkspace() {
      const userInfo = storage.get('userInfo');
      if (userInfo.basic && userInfo.basic.status === 0) {
        this.$Modal.confirm({
          title: '开通资源',
          content: '<p>尊敬的用户，使用本功能需要计算和存储资源，您可以去申请开通资源</p>',
          okText: '去开通',
          cancelText: '再看看案例和入门',
          onOk: () => {
            window.open(process.env.VUE_APP_CTYUN_SUBSCRIBE);
          },
          onCancel: () => {
            console.log('Clicked cancel');
          }
        });
        return;
      }
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
      this.total = this.filteredData.length;
      // 搜索后回到第一页
      this.pageNum = 1;
      this.changePage(this.pageNum);
    },
    gotoWorkspace(workspace) {
      const workspaceId = workspace.workspaceId || workspace.id;
      this.$router.push({ path: '/workspace', query: { workspaceId: workspaceId}});
    },
    workspaceShowAction(val) {
      this.workspaceShow = val;
    },
    workspaceConfirm(params) {
      api.fetch('dss/workspaces', params, 'post').then((res) => {
        this.$Message.success(this.$t('message.workspace.createdSuccess'));
        // 创建成功后跳到工作空间首页
        this.gotoWorkspace(res);
      }).catch(() => {
        this.$Message.error(this.$t('message.workspace.createdFailed'));
      })
    },
    changeVisual(type) {
      this.visual = type;
      // 切换视图后回到第一页
      this.pageNum = 1;
      this.changePage(this.pageNum);
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
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
