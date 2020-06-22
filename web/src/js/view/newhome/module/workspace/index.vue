<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1>{{$t('message.project.infoHeader')}}</h1>
        <p>{{$t('message.project.infoBodyFirstRow')}}</p>
        <p>{{$t('message.project.infoBodySecondRow')}}</p>
      </div>
    </div>
    <div class="workspace-main">
      <Card class="left">
        <div class="workspace-type-header">
          <svg t="1573033007700" viewBox="0 0 1025 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="113086" width="20" height="20"><path d="M451.310305 529.689024L50.690586 339.234666a72.444333 72.444333 0 0 1 0-135.09716L451.374302 13.619151a144.888665 144.888665 0 0 1 122.041821 0l400.491726 190.518355a72.444333 72.444333 0 0 1 0 135.033163L573.416123 529.753021c-38.71804 17.98309-83.387778 17.98309-122.041821 0z" fill="#5580EB" p-id="113087"></path><path d="M512.299218 784.460127c-29.118526 0-57.853071-6.655663-84.027747-19.519012l-392.940107-195.190119a54.333249 54.333249 0 1 1 48.18956-96.891095l392.556127 195.190119c22.91084 11.135436 49.597489 11.135436 72.444333 0l392.940107-195.190119a54.333249 54.333249 0 1 1 48.189561 96.891095l-393.324088 195.190119a189.43041 189.43041 0 0 1-84.027746 19.519012z m0 229.620375a189.43041 189.43041 0 0 1-84.027747-19.519012l-392.940107-195.190118a54.333249 54.333249 0 1 1 48.18956-96.891095l392.556127 195.190118c22.846843 11.26343 49.597489 11.26343 72.444333 0l392.940107-195.190118a54.333249 54.333249 0 1 1 48.189561 96.827098l-393.324088 195.190119a189.43041 189.43041 0 0 1-84.027746 19.583008z" fill="#5580EB" opacity=".5" p-id="113088"></path></svg>
          <span class="workspace-type-header-label">{{$t('message.project.applicationStudio')}}</span>
        </div>
        <div class="workspace-create-search">
          <div class="workspace-create">
            <svg t="1572508997540" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3412" xmlns:xlink="http://www.w3.org/1999/xlink" width="30" height="30">
              <path d="M468.89187555 244.16003161L405.84786173 124.9330568H77.68240987v774.65170172h368.68449976c-9.0617679-19.02971259-16.44063605-38.96560197-21.8776968-59.54876049H133.6064632V303.7087921h782.03056989v197.15817876c7.24941431 6.47269136 15.01664395 12.03920592 21.74824296 19.15916642 12.81592889 13.72210569 23.81950419 28.60929581 34.04635654 44.14375507V244.16003161H468.89187555z m-335.28541235-59.67821433h239.74848791l31.45728 59.5487605H133.6064632v-59.5487605z m614.38786371 536.32720592h111.71865284v59.5487605h-111.71865284v119.22697482h-55.92405333v-119.22697482h-111.71865283v-59.5487605h111.71865283v-119.22697481h55.92405333v119.22697481z m-27.83257283-238.32449579c-138.77450272 0-251.39933235 120.00369778-251.39933235 268.09887606 0 148.09517827 112.49537581 268.22832987 251.26987852 268.22832986 138.90395653 0 251.39933235-120.00369778 251.39933234-268.09887605 0-71.07015111-26.40858075-139.29231803-73.52977383-189.64985678s-111.0713837-78.57847309-177.74010468-78.57847309z m-0.12945383 476.64899161c-107.83503803-0.25890765-195.21637136-93.4656632-195.34582519-208.55011555 0.12945383-115.21390617 87.51078717-208.42066173 195.47527902-208.67956939 52.16989235 0 101.36234667 21.74824297 138.2566874 61.10220642 37.02379457 39.35396347 57.21859161 91.78276347 57.21859161 147.57736297-0.12945383 115.21390617-87.64024098 208.55011555-195.60473284 208.55011555z" fill="#2d8cf0" p-id="3413"></path>
            </svg>
            <p
              class="workspace-create-text"
              style="color:#2d8cf0"
              @click="createWorkspace">
              {{ $t('message.workspace.CREATEDWORKSPACE') }}
            </p>
          </div>
          <div class="header-search">
            <Input
              search
              class="search-input"
              :placeholder="$t('message.workspace.SEARCHWORKSPACE')"
              @on-search="searchWorkspace"/>
          </div>
        </div>
        <div class="workspace-content">
          <h3 class="item-header">
            <span>{{ $t('message.workspace.WORKSPACELIST') }}</span>
          </h3>
          <Row
            class="content-item">
            <i-col
              class="workspace-item"
              :xs="12" :sm="8" :md="6" :lg="5"
              v-for="item in originWorkspaceData"
              :key="item.id"
              @click.native="gotoWorkspace(item)"
            >
              <span class="name">{{item.name}}</span>
              <p class="desc">{{item.description}}</p>
              <ul class="lable-list">
                <template v-for="(tag, tagIndex) in item.tags.split(',')">
                  <li v-if="tag && tagIndex <= 2"  class="item" :key="tagIndex">{{tag}}</li>
                </template>
              </ul>
              <Button size="small" disabled class="editor" @click.stop="editor(item)">{{ $t('message.workspace.Administration') }}</Button>
            </i-col>
          </Row>
        </div>
      </Card>
    </div>
    <WorkspaceForm
      ref="workspaceForm"
      :action-type="actionType"
      :project-data="currentWorkspaceData"
      :add-project-show="workspaceShow"
      @show="workspaceShowAction"
      @confirm="workspaceConfirm"></WorkspaceForm>
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import api from '@/js/service/api';
import WorkspaceForm from './module/workspaceForm.vue';
import storage from "@/js/helper/storage";
export default {
  components: {
    WorkspaceForm
  },
  data() {
    return {
      loading: false,
      actionType: '',
      currentWorkspaceData: {
        workspaceName: '',
        description: '',
        tags: '',
        department: '',
        productName: ''
      },
      workspaceShow: false,
      originWorkspaceData: [],
      cacheData: []
    }
  },
  created() {
    this.getWorkspaces();

  },
  methods: {
    getWorkspaces() {
      // 获取工作空间数据
      this.loading = true;
      api.fetch('dss/getWorkspaces', {}, 'get').then((res) => {
        this.originWorkspaceData = this.cacheData = res.workspaces;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      })
    },
    createWorkspace() {
      this.actionType = 'add';
      this.workspaceShow = true;
      this.currentWorkspaceData = {
        workspaceName: '',
        description: '',
        tags: '',
        department: '',
        productName: ''
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
        this.originWorkspaceData = this.cacheData.filter((item) => item.name.indexOf(value) != -1);
      } else {
        this.originWorkspaceData = this.cacheData;
      }
    },
    gotoWorkspace(workspace) {
      this.$router.push({path: '/workspaceHome/homepage', query: {workspaceId: workspace.id}})
    },
    workspaceShowAction(val) {
      this.workspaceShow = val;
    },
    workspaceConfirm(params) {
      api.fetch('dss/createWorkspace', params, 'post').then(() => {
        this.$Message.success(this.$t('message.workspace.CREATEDSCUCCESS'));
        // 重新获取工作空间列表
        this.getWorkspaces();
      }).catch(() => {
        this.$Message.error(this.$t('message.workspace.CREATEDDFAILD'));
      })
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
