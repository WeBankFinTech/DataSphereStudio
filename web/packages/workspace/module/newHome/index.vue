<template>
  <div class="newHome_page">
    <!-- 首页标题 -->
    <div class="newHome_header">
      <div class="newHome_info">
        <h1>{{$t('message.workspace.infoHeader', {app_name: $APP_CONF.app_name})}}</h1>
        <p>{{$t('message.workspace.infoBodyFirstRow', {app_name: $APP_CONF.app_name})}}</p>
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
              <SvgIcon :iconClass="visualCatesIcon"/>
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

            <li class="newHome_create" @click="createWorkspace" v-if="!noWorkSpace">
              <div class="newHome_create_content">
                <SvgIcon :style="{ 'font-size': '20px' }" icon-class="fi-addproject" color="#2d8cf0"/>
                <p class="newHome_create_text" style="color:#2d8cf0">
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
                :title="$t('message.workspace.Management')"
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
      </div>
      <div class="newHome_main_right">
        <div class="right_header">
          <h3 class="title">{{ $t("message.workspace.KSRM") }}</h3>
        </div>
        <div v-if="isAdmin" class="permissions_wrap">
          <div class="permissions_entry" @click="gotoManagementPlatform()">
            <img src="../../assets/images/u111.svg" />
            <div>{{ $t("message.workspace.managementPlatform") }}</div>
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
      @confirm="workspaceConfirm"
    ></WorkspaceForm>
    <Spin v-if="loading" size="large" fix />
  </div>
</template>
<script>
import WorkspaceForm from "./module/workspaceForm.vue";
import WorkspaceTable from "./module/workspaceTable.vue";
import {
  GetWorkspaceList
} from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
import util from '@dataspherestudio/shared/common/util';
import storage from '@dataspherestudio/shared/common/helper/storage';


export default {
  components: {
    WorkspaceForm,
    WorkspaceTable,
  },
  data() {
    return {
      loading: false,
      actionType: "",
      currentWorkspaceData: {
        name: "",
        description: "",
        label: "",
        department: "",
        workspace_type: ""
      },
      workspaceShow: false,
      cacheData: [],
      filteredData: [],
      visual: false, // false 为默认值显示卡片模式，反之列表模式
      visualCatesTitle: this.$t("message.workspace.tableDisplay"),
      visualCatesIcon: "fi-table",
      visualCates: [
        { icon: "fi-table", title: this.$t("message.workspace.tableDisplay") },
        { icon: "menu", title: this.$t("message.workspace.cardDisplay") }
      ],
      total: null,
      pageSize: 6,
      pageNum: 1,
      isAdminAndSingleServe: false,
      isAdmin: false
    };
  },
  created() {
    this.getWorkspaces();
    const currentModules = util.currentModules();
    // 是管理员且是数据服务的微服务
    this.isAdmin = this.getIsAdmin()
    this.isAdminAndSingleServe = this.isAdmin && currentModules.microModule === "apiServices";
  },
  mounted() {},
  computed: {
    noWorkSpace() {
      return storage.get('noWorkSpace', 'local')
    }
  },
  beforeDestroy() {},
  watch: {},
  methods: {
    getWorkspaces() {
      // 获取工作空间数据
      this.loading = true;
      GetWorkspaceList({}, "get")
        .then(res => {
          this.filteredData = this.cacheData = res.workspaces;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    createWorkspace() {
      this.actionType = "add";
      this.workspaceShow = true;
      this.currentWorkspaceData = {
        name: "",
        description: "",
        label: "",
        department: "",
        workspace_type: ""
      };
    },
    editor(workspace) {
      this.actionType = "editor";
      this.workspaceShow = true;
      this.currentWorkspaceData = workspace;
    },
    searchWorkspace(value) {
      // 通过名称模糊搜索
      value = value.trim();
      if (value) {
        this.filteredData = this.cacheData.filter(
          item => item.name && (item.name.indexOf(value) != -1)
        );
      } else {
        this.filteredData = this.cacheData;
      }
    },
    gotoWorkspace(workspace) {
      const workspaceId = workspace.workspaceId || workspace.id;
      const currentModules = util.currentModules();
      if (currentModules.microModule === "apiServices") {
        this.$router.push({
          path: "/apiservices",
          query: { workspaceId: workspaceId }
        });
      } else {
        this.$router.push({
          path: "/workspaceHome",
          query: { workspaceId: workspaceId }
        });
      }
    },
    workspaceShowAction(val) {
      this.workspaceShow = val;
    },
    workspaceConfirm(params, callback) {
      GetWorkspaceList(params, "post")
        .then(res => {
          typeof callback == 'function' && callback();
          this.$Message.success(this.$t("message.workspace.createdSuccess"));
          // 创建成功后跳到工作空间首页
          this.gotoWorkspace(res);
        })
        .catch(() => {
          typeof callback == 'function' && callback();
          this.$Message.error(this.$t("message.workspace.createdFailed"));
        });
    },
    changeVisual() {
      this.visual = !this.visual;
      if (this.visual) {
        this.visualCatesTitle = this.visualCates[1].title;
        this.visualCatesIcon = this.visualCates[1].icon;
      } else {
        this.visualCatesTitle = this.visualCates[0].title;
        this.visualCatesIcon = this.visualCates[0].icon;
      }
    },
    // 跳转管理页
    gotoManagement(id) {
      this.$router.push({
        path: "/workspaceManagement/productsettings",
        query: { workspaceId: id }
      });
    },
    gotoManagementPlatform() {
      this.$router.push({ path: "/managementPlatform" });
    }
  }
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
