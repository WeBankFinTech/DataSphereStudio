<template>
  <div class="main-sidebar">
    <Menu class="menu-wrap" :active-name="activeMenu" :open-names="openMenu">
      <sidebar-sub-menu
        v-for="menu in menuList"
        :key="menu.path"
        :menu="menu"
      />
    </Menu>
  </div>
</template>
<script>
import SidebarSubMenu from "./sidebar-sub-menu.vue";
import routers from "@/apps/dataModelCenter/router.js";
export default {
  name: "sidebar",
  components: {
    SidebarSubMenu,
  },
  data() {
    return {
      menuActiveName: "hive",
      menuList: routers[0].children,
    };
  },
  computed: {
    activeMenu() {
      return this.$route.path;
    },
    openMenu() {
      return [this.$route.path.split("/").slice(0, 2).join("/")];
    },
  },
};
</script>
<style lang="scss" scoped>
.main-sidebar,
.menu-wrap {
  width: 100% !important;
  height: 100%;
}
</style>
