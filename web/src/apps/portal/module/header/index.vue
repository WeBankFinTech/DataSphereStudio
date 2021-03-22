<template>
  <div>
    <div class="layout-header">
      <div
        class="layout-header-menu-icon"
      >
        <div class="logo-bar">
          <img v-if="logoUrl" :src="logoUrl">
          <!-- <SvgIcon style="font-size: 2rem;" icon-class="menu" color="#00FFFF"/> -->
        </div>
        <div class="logo">
          <h1 class="portal-title">
            {{moudleName}}
          </h1>
        </div>
      </div>
      <div
        v-clickoutside="handleOutsideClick"
        :class="{'selected': isUserMenuShow}"
        class="user"
        @click="handleUserClick"
      >
        <span>{{ userName }}</span>
        <Icon v-show="!isUserMenuShow" type="ios-arrow-down" class="user-icon"/>
        <Icon v-show="isUserMenuShow" type="ios-arrow-up" class="user-icon"/>
        <userMenu v-show="isUserMenuShow" @clear-session="clearSession"/>
      </div>
      <!-- <ul class="menu">
        <li v-if="$route.path !== '/newhome' && $route.path !== '/bankhome' && $route.query.workspaceId" class="menu-item" @click="goSpaceHome">{{$t("message.home.navBar.home")}}</li>
      </ul> -->
      <div class="icon-group">
        <Icon
          v-if="isSandbox"
          :title="$t('message.home.navBar.home')"
          class="book"
          type="ios-chatboxes"
          @click="linkTo('freedback')"
        ></Icon>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from "lodash";
import api from "@/common/service/api";
import storage from "@/common/helper/storage";
import userMenu from "./userMenu.vue";
import clickoutside from "@/common/helper/clickoutside";
import mixin from '@/common/service/mixin';
import util from '@/common/util';
import { GetBaseInfo } from '@/common/service/apiCommonMethod.js';
export default {
  directives: {
    clickoutside
  },
  components: {
    userMenu
  },
  data() {
    return {
      // 是否开启logo跳转，默认不开启(管理员才开启)
      isAdmin: false,
      isUserMenuShow: false,
      userName: "",
      isSandbox: process.env.NODE_ENV === "sandbox",
      portalName: '',
      logoUrl: ''
    };
  },
  mixins: [mixin],
  created() {
    this.init();
    this.getPortalSet();
  },
  computed: {
    moudleName() {

      return this.portalName || '欢迎来到数据门户开发中心';
    },
  },
  methods: {
    init() {
      GetBaseInfo().then(rst => {
        if (!isEmpty(rst)) {
          this.userName = rst.username;
          // 根据权限来判断是否开启logo跳转(管理员才会开启)
          this.isAdmin = rst.isAdmin;
          storage.set("baseInfo", rst, 'local');
          storage.set("userInfo", rst.username);
          this.$router.app.$emit("username", rst.username);
          this.$emit("set-init");
        }
      });
    },
    goto(name) {
      this.$router.push({
        name: name,
      });
    },
    handleOutsideClick() {
      this.isUserMenuShow = false;
    },
    handleUserClick() {
      this.isUserMenuShow = !this.isUserMenuShow;
    },
    clearSession() {
      this.$emit("clear-session");
    },
    goHome() {
      if(this.isAdmin) this.$router.push("/newhome");
    },
    linkTo(type) {
      let url = "";
      if (type === "book") {
        url = `https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch3/DSS_User_Manual.md`;
      } else if (type === "github") {
        url = `https://github.com/WeBankFinTech/DataSphereStudio`;
      } else if (type === "freedback") {
        url = "https://wj.qq.com/s2/4943071/c037/ ";
        if (localStorage.getItem("locale") === "en") {
          url = "https://wj.qq.com/s2/4943706/5a8b";
        }
      }
      util.windowOpen(url);
    },
    goSpaceHome(){
      let workspaceId = this.$route.query.workspaceId;
      if(!workspaceId) return this.goHome();
      this.$router.push({path: '/workspaceHome',query: { workspaceId }});
      this.currentProject = {};
    },
    getPortalSet() {
      if (this.$route.query.portalId) {
        let url = '/dataportal/configuration';
        api.fetch(url, { dataPortalId: this.$route.query.portalId }, 'get').then(res => {
          if (res.Configuration) {
            this.portalName = res.Configuration.name;
            this.logoUrl = res.Configuration.logo;
          }
        })
      }
    }
  }
};
</script>
<style lang="scss" src="./index.scss" scoped></style>
