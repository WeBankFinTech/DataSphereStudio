<template>
  <div>
    <div class="layout-header">
      <div
        class="layout-header-menu-icon"
      >
        <div class="logo">
          <img
            class="logo-img"
            src="../../assets/images/dssLogo.png"
            :alt="$APP_CONF.app_name"
          >
          <span class="version">{{sysVersion}}</span>
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
      <ul class="menu">
        <li class="menu-item"  @click="goRolesPath">{{moduleString}}</li>
      </ul>
      <div class="icon-group">
        <Icon
          v-if="isSandbox"
          :title="$t('message.common.home')"
          class="book"
          type="ios-chatboxes"
          @click="linkTo('freedback')"
        ></Icon>
      </div>
    </div>
  </div>
</template>
<script>
import userMenu from "./userMenu.vue";
import clickoutside from '@dataspherestudio/shared/common/helper/clickoutside';
import mixin from '@dataspherestudio/shared/common/service/mixin';
import util from '@dataspherestudio/shared/common/util';

export default {
  directives: {
    clickoutside
  },
  components: {
    userMenu,
  },
  data() {
    return {
      // 根据接口返回渲染快捷入口
      homeRoles: null,
      sysVersion: process.env.VUE_APP_VERSION,
      isUserMenuShow: false,
      userName: "",
      isSandbox: this.$APP_CONF.isSandbox,
    };
  },
  mixins: [mixin],
  created() {
  },
  mounted() {
    this.userName = this.getUserName();
  },
  computed: {
    moduleString() {
      const currentModules = util.currentModules();
      return currentModules.microModule ? 'process' : 'workflow'
    }
  },
  methods: {
    handleOutsideClick() {
      this.isUserMenuShow = false;
    },
    handleUserClick() {
      this.isUserMenuShow = !this.isUserMenuShow;
    },
    clearSession() {
      this.$emit("clear-session");
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
    goRolesPath() {
      this.$router.push({path: `/${this.moduleString}`, query: Object.assign({}, this.$route.query)});
    },
  }
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
