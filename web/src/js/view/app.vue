<template>
  <div class="layout">
    <router-view/>
  </div>
</template>
<script>
import storage from '@/js/helper/storage';
export default {
  name: 'App',
  created() {
    // 用户刷新时有可能已经在另一个tab中登录了另一个用户，这时session会混。
    this.clearSession();
  },
  methods: {
    clearSession() {
      storage.clear();
    },
  },

};
</script>
<style src="@assets/styles/app.scss" lang="scss"></style>
