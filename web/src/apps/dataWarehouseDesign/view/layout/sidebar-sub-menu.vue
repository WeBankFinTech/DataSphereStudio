<template>
  <div v-if="menu.meta.publicPage">
    <Menu-item v-if="coverRoute" :name="coverRoute.path">
      <Icon :type="coverRoute.meta.icon" v-if="coverRoute.meta.icon"></Icon>
      {{ coverRoute.meta.title }}
      <router-link :to="`${coverRoute.path}?workspaceId=${$route.query.workspaceId}`" class="menu-item"> </router-link>
    </Menu-item>
    <Submenu
      v-else-if="menu.children && menu.children.length >= 1"
      :name="menu.path"
    >
      <template slot="title">
        <Icon :type="menu.meta.icon"></Icon>
        {{ menu.meta.title }}
      </template>
      <sidebar-sub-menu
        v-for="item in menu.children"
        :key="item.path"
        :menu="item"
      />
    </Submenu>
    <Menu-item v-else :name="menu.path">
      <Icon :type="menu.meta.icon" v-if="menu.meta.icon"></Icon>
      {{ menu.meta.title }}
      <router-link :to="`${menu.path}?workspaceId=${$route.query.workspaceId}`" class="menu-item"> </router-link>
    </Menu-item>
  </div>
</template>
<script>
export default {
  name: 'sidebar-sub-menu',
  props: {
    menu: {
      type: Object,
      required: true,
    },
  },
  computed: {
    coverRoute: function () {
      if (this.menu.meta.cover) {
        if (this.menu.children && this.menu.children.length >= 1) {
          return this.menu.children.find((item) => {
            return item.name === this.menu.meta.cover
          })
        }
      }
      return false
    },
  },
}
</script>
<style lang="scss" scoped>
.menu-item {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  right: 0;
  color: inherit;
}
</style>
