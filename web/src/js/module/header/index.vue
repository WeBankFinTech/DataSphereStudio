<template>
  <div>
    <div class="layout-header">
      <div
        class="layout-header-menu-icon"
        @click="goHome"
      >
        <div style="margin-top:5px; display:inline-block;"
          @mouseleave="mouseleave"
          @mouseover="mouseover">
          <svg t="1572957725118" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1115" width="20" height="20">
            <path d="M896 1024h-213.333333a128 128 0 0 1-128-128v-213.333333a128 128 0 0 1 128-128h213.333333a128 128 0 0 1 128 128v213.333333a128 128 0 0 1-128 128z m-213.333333-384a42.666667 42.666667 0 0 0-42.666667 42.666667v213.333333a42.666667 42.666667 0 0 0 42.666667 42.666667h213.333333a42.666667 42.666667 0 0 0 42.666667-42.666667v-213.333333a42.666667 42.666667 0 0 0-42.666667-42.666667z m-341.333334 384H128a128 128 0 0 1-128-128v-213.333333a128 128 0 0 1 128-128h213.333333a128 128 0 0 1 128 128v213.333333a128 128 0 0 1-128 128z m-213.333333-384a42.666667 42.666667 0 0 0-42.666667 42.666667v213.333333a42.666667 42.666667 0 0 0 42.666667 42.666667h213.333333a42.666667 42.666667 0 0 0 42.666667-42.666667v-213.333333a42.666667 42.666667 0 0 0-42.666667-42.666667z m768-170.666667h-213.333333a128 128 0 0 1-128-128V128a128 128 0 0 1 128-128h213.333333a128 128 0 0 1 128 128v213.333333a128 128 0 0 1-128 128z m-213.333333-384a42.666667 42.666667 0 0 0-42.666667 42.666667v213.333333a42.666667 42.666667 0 0 0 42.666667 42.666667h213.333333a42.666667 42.666667 0 0 0 42.666667-42.666667V128a42.666667 42.666667 0 0 0-42.666667-42.666667z m-341.333334 384H128a128 128 0 0 1-128-128V128a128 128 0 0 1 128-128h213.333333a128 128 0 0 1 128 128v213.333333a128 128 0 0 1-128 128zM128 85.333333a42.666667 42.666667 0 0 0-42.666667 42.666667v213.333333a42.666667 42.666667 0 0 0 42.666667 42.666667h213.333333a42.666667 42.666667 0 0 0 42.666667-42.666667V128a42.666667 42.666667 0 0 0-42.666667-42.666667z" p-id="1116" fill="#00FFFF">
            </path>
          </svg>
        </div>
        <div class="logo">
          <h1 class="logo-title">
            {{$t('message.newConst.logoName')}} <span style="font-size:14px" v-if="moudleName">/ {{moudleName}}</span>
          </h1>
          <img
            class="logo-img"
            src="../../../assets/images/dssLogo6.png" :alt="$t('message.newConst.logoName')">
        </div>
      </div>
      <workspaceMenu v-if="isShowWorkspaceMenu()" :projectList="workspaces" :currentId="parseInt($route.query.workspaceId, 10)" :changeWorkSpace="changeWorkspace"></workspaceMenu>
      <span
        v-if="currentProject.id"
        class="proj-select"
        :title="$t('message.newConst.project')">
        <svg t="1572505474223" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1741" xmlns:xlink="http://www.w3.org/1999/xlink" width="30" height="18">
          <path d="M1019.669291 397.544431c0 0.211533 0.211533 0.211533 0.211534 0.423066v-0.740366c-0.211533-0.105767-0.211533-0.105767-0.211534 0.3173z m0 0" p-id="113308" fill="#1d9cf0"></path><path d="M1201.693609 504.368697H421.559171c-28.133915 0-53.2006 18.297622-61.873461 45.162338L291.466254 759.266201V194.15526H585.285867c0 71.709754 58.065863 129.987151 129.881384 129.987151H985.50668v97.834105c0.211533 17.768789 14.701557 32.04728 32.364579 32.047279 17.980322 0 32.364579-14.701557 32.36458-32.364579 0-0.211533-0.211533-0.211533-0.211534-0.423067h0.211534v-96.987971c0-35.854877-29.085815-65.046459-65.046459-65.046459H715.378785c-35.854877 0-65.046459-29.085815-65.046459-65.046459 0-17.134189-6.980596-33.739545-19.143755-45.902704-12.163159-12.163159-28.768515-19.143755-45.902704-19.143754H291.677787c-35.854877 0-64.834926 29.085815-65.046459 64.834925V959.165074c0.211533 35.854877 29.085815 64.834926 65.046459 64.834926h779.817138c28.345449 0 53.412133-18.086089 61.873461-45.162338l133.160149-409.528273c0-35.749111-28.980048-64.729159-64.834926-64.940692z m0 0M1237.336953 181.992101c-33.528012-107.247332-154.736535-286.098652-553.476615-103.439734 0 0 410.26864-104.4974 462.940406 136.22738l-80.065316 15.759223 178.639787 112.324129 64.517625-196.620108-72.555887 35.74911z m19.989887 3.913364" fill="#1d9cf0" p-id="113309"></path><path d="M1073.927557 1023.894233s-23.691718-11.740093-35.643344-20.412953c-5.394097-3.913364-34.374145-29.085815-35.431811-30.883847l-697.530724-0.6346s-22.845585 3.384531-44.316205-12.903525c-23.797485-18.086089-34.162612-53.412133-34.162612-53.412133l-0.105766 47.806503s-0.423066 12.374692 2.644165 23.585952c2.009565 7.403662 6.028696 14.278491 7.615195 17.345722 15.124624 29.403115 54.892865 29.508881 54.892865 29.508881h782.038237z m-428.143195-5.711396" p-id="113310" fill="#1d9cf0"></path>
        </svg>
      </span>&nbsp;
      <Dropdown
        class="project-dro"
        v-if="currentProject.id">
        <div style="font-size:14px">
          {{ currentProject.name }}
          <Icon type="ios-arrow-down" style="margin-left: 5px"></Icon>
        </div>
        <DropdownMenu slot="list" class="proj-list">
          <div v-for="proj in projectList" :key="proj.id">
            <div class="proj-name">{{ proj.name }}</div>
            <div v-for="p in proj.dssProjectList"
              @click="changeProj(proj,p)"
              :key="proj.id+p.id"
              :class="{'active':p.id == currentProject.id}"
              class="proj-item">
              {{ p.name}}
            </div>
          </div>
        </DropdownMenu>
      </Dropdown>
      <ul class="menu">
        <li v-if="this.$route.query && this.$route.query.workspaceId" class="menu-item" @click="goSpaceHome">{{$t("message.header.home")}}</li>
        <li class="menu-item" @click="goConsole">{{$t("message.header.console")}}</li>
      </ul>
      <div
        v-clickoutside="handleOutsideClick"
        :class="{'selected': isUserMenuShow}"
        class="user"
        @click="handleUserClick">
        <span>{{ userName }}</span>
        <Icon
          v-show="!isUserMenuShow"
          type="ios-arrow-down"
          class="user-icon"/>
        <Icon
          v-show="isUserMenuShow"
          type="ios-arrow-up"
          class="user-icon"/>
        <userMenu
          v-show="isUserMenuShow"
          @clear-session="clearSession"/>
      </div>
      <div
        class="icon-group">
        <Icon
          v-if="isSandbox"
          title="freedback"
          class="book"
          type="ios-chatboxes"
          @click="linkTo('freedback')"></Icon>
        <Icon
          title="about"
          class="book"
          type="ios-book"
          @click="linkTo('book')"></Icon>
        <Icon
          title="github"
          class="git"
          type="logo-github"
          @click="linkTo('github')"></Icon>
      </div>
    </div>
    <nav-menu
      v-show="isNavMenuShow"
      @mouseover.native.capture="mouseover"
      @mouseleave.native="mouseleave"
      @click="handleNavMenuClose"></nav-menu>
  </div>
</template>
<script>
import { isEmpty } from 'lodash';
import api from '@/js/service/api';
import storage from '@/js/helper/storage';
import userMenu from './userMenu.vue';
import workspaceMenu from './workspaceMenu';
import clickoutside from '@js/helper/clickoutside';
import navMenu from '@/js/component/navMenu/index.vue'
import weMenu from '@/js/component/menu/index.vue'
export default {
  directives: {
    clickoutside,
  },
  components: {
    userMenu,
    navMenu,
    workspaceMenu,
  },
  data() {
    return {
      isUserMenuShow: false,
      userName: '',
      isNavMenuShow: false,
      currentProject: {},
      projectList: [],
      workspaces: [],
      isSandbox: process.env.NODE_ENV === 'sandbox'
    };
  },
  created() {
    this.init();
  },
  mounted() {
    this.getCurrentProject();
  },
  computed: {
    moudleName() {
      let moudleNameMaps = {
        '/home': 'Scriptis',
        '/console': 'Console',
        '/kanban': 'Visualis',
        '/process': 'Workflow'
      }
      let moudleName;
      Object.keys(moudleNameMaps).forEach(k => {
        if (this.$route.path.indexOf(k) > -1) {
          moudleName = moudleNameMaps[k]
        }
      })
      return moudleName
    }
  },
  watch: {
    '$route'(newValue) {
      this.projectID = newValue.query.projectID;
      this.getCurrentProject();
      this.getWorkSpace();
    }
  },
  methods: {
    isShowWorkspaceMenu(){
      return (!this.currentProject.id && (this.$route.query && this.$route.query.workspaceId) && this.$route.path.indexOf('workflow')===-1 );
    },
    init() {
      api.fetch('/dss/getBaseInfo', 'get').then((rst) => {
        if (!isEmpty(rst)) {
          this.userName = rst.userInfo.basic.username;
          storage.set('baseInfo', rst);
          storage.set('userInfo', rst.userInfo);
          // window.$Wa.setParam('openId', rst.userInfo.basic.userName);
          this.$router.app.$emit('username', rst.userInfo.basic.username);

          this.$emit('set-init');
        }
        this.getWorkSpace();
      });
    },
    getWorkSpace(){
      api.fetch(`/dss/workspaces`, 'get').then(rst=>{
        if (!isEmpty(rst)) {
          this.workspaces = rst.workspaces;
        }
      })
    },
    goto(name) {
      this.$router.push({
        name: name,
      });
    },
    getClass(path) {
      let arr = [];
      if (this.$route.name === path || this.$route.meta.parent === path) {
        arr.push('selected');
      }
      return arr;
    },
    handleOutsideClick() {
      this.isUserMenuShow = false;
    },
    handleUserClick() {
      this.isUserMenuShow = !this.isUserMenuShow;
    },
    clearSession() {
      this.$emit('clear-session');
    },
    handleNavMenuClose() {
      this.isNavMenuShow = false;
    },
    mouseover() {
      this.isNavMenuShow = this.$route.path != '/project'
    },
    mouseleave() {
      this.isNavMenuShow = false
    },
    getCurrentProject() {
      let projList = storage.get('projectList', 'local');
      this.projectList = projList;
      let projId = this.$route.query.projectID;
      let proj = {};
      if (projId) {
        this.projectList.forEach(item => {
          if (item.dssProjectList) {
            item.dssProjectList.forEach(p => {
              if(p.id == projId) {
                proj = { ...p }
              }
            })
          }
        })
      }
      this.currentProject = proj
    },
    changeProj(proj, p) {
      if(p.id == this.currentProject.id && proj.id == this.$route.query.projectTaxonomyID) return;
      // 得考虑在流程图页面和知画的情况, 在此情况下跳转到工程页
      if (['/process'].includes(this.$route.path)) {
        this.$router.replace({path: '/project'})
      } else {
        this.$router.replace({
          path: this.$route.path,
          query: {
            ...this.$route.query,
            projectTaxonomyID: proj.id,
            projectID: p.id,
            projectVersionID: p.latestVersion.id,
          }
        });
      }
    },
    goHome() {
      this.$router.push('/')
    },
    linkTo(type) {
      let url = '';
      if (type === 'book') {
        url = `https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch3/DSS_User_Manual.md`
      } else if (type === 'github') {
        url = `https://github.com/WeBankFinTech/DataSphereStudio`;
      } else if (type === 'freedback') {
        url = 'https://wj.qq.com/s2/4943071/c037/ ';
        if (localStorage.getItem('locale') === 'en') {
          url = 'https://wj.qq.com/s2/4943706/5a8b';
        }
      }
      const newTab = window.open('about:blank');
      setTimeout(() => {
        newTab.location.href = url;
      }, 500);
    },
    goSpaceHome(){
      this.$router.push({path: '/workspace',query: Object.assign({}, this.$route.query)});
    },
    goConsole(){
      this.$router.push({path: '/console',query: Object.assign({}, this.$route.query)});
    },
    changeWorkspace(data){
      this.$router.push({query: Object.assign({}, this.$route.query, {workspaceId: data.id})});
    }
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
