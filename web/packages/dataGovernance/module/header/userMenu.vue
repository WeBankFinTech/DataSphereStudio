<template>
  <ul
    class="user-menu">
    <li class="user-menu-arrow"/>
    <li
      class="user-menu-item"
      v-for="(menu) in menuList"
      :key="menu.id"
      @click="handleClick(menu.id)">
      <Icon
        class="user-menu-item-icon"
        :type="menu.icon">
      </Icon>
      <span>{{ menu.name }}</span>
    </li>
  </ul>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import storage from '@dataspherestudio/shared/common/helper/storage';
import mixin from '@dataspherestudio/shared/common/service/mixin';
export default {
  name: 'Menu',
  mixins: [mixin],
  data() {
    return {
      menuList: [
        // {
        //     id: 'user-management',
        //     name: '用户管理',
        //     icon: 'ios-person-outline',
        // },
        {
          id: 'clearCache',
          name: this.$t('message.common.clearCache'),
          icon: 'ios-trash-outline',
        },
        {
          id: 'changeLang',
          name: localStorage.getItem('locale') === 'zh-CN' ? 'English' : '简体中文',
          icon: 'md-repeat',
        }, {
          id: 'logout',
          name: this.$t('message.common.logOut'),
          icon: 'ios-log-out',
        }],
    };
  },
  methods: {
    handleClick(type) {
      switch (type) {
        case 'user-management':
          this.openUserManagement();
          break;
        case 'clearCache':
          this.clearCache();
          break;
        case 'logout':
          this.logout();
          break;
        case 'changeLang':
          this.changeLang();
          break;
      }
    },
    openUserManagement() {
      this.$Message.info(this.$t('message.common.userMenu.comingSoon'));
    },
    clearCache() {
      this.$Modal.confirm({
        title: this.$t('message.common.userMenu.title'),
        content: this.$t('message.common.userMenu.content'),
        onOk: () => {
          this.dispatch('dssIndexedDB:deleteDb');
          this.$Message.success(this.$t('message.common.userMenu.clearCacheSuccess'));
          setTimeout(() => {
            window.location.reload();
          }, 1000);
        },
        onCancel: () => {
        },
      });
    },
    logout() {
      api.fetch('/user/logout', {}).then(() => {
        this.$emit('clear-session');
        storage.set('need-refresh-proposals-hql', true);
        storage.set('need-refresh-proposals-python', true);
        this.$router.push({ path: '/login' });
      });
    },
    changeLang() {
      // 中文切换英文
      if (localStorage.getItem('locale') === 'zh-CN') {
        localStorage.setItem('locale', 'en');
      } else {
        localStorage.setItem('locale', 'zh-CN');
      }
      window.location.reload();
    }
  },
};
</script>
<style scoped lang="scss">
@import '@dataspherestudio/shared/common/style/headerUserMenu.scss';
</style>
