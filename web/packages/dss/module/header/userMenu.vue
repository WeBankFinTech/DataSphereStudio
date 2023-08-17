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
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';
export default {
  name: 'dssMenu',
  mixins: [mixin],
  data() {
    const theme = localStorage.getItem('theme')==='dark' ? 'light' : 'dark'
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
        },
        {
          id: 'changeTheme',
          name: this.$t(`message.common.theme.${theme}`),
          icon: 'md-repeat',
        },
        {
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
        case 'changeTheme':
          this.changeTheme();
          break;
      }
    },
    openUserManagement() {
      this.$Message.info(this.$t('message.common.userMenu.comingSoon'));
    },
    clearCache() {
      localStorage.setItem('cacheGuide', null)
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
    async logout() {
      const unsave = await eventbus.emit('check.scriptis.unsave');
      if (unsave) return
      api.fetch('/user/logout', {}).then(() => {
        this.$emit('clear-session');
        storage.set('need-refresh-proposals-hql', true);
        storage.set('need-refresh-proposals-python', true);
        // 手动退出清掉baseInfo
        storage.remove('baseInfo', 'local');
        this.$router.push({ path: '/login',  query: { 'notcheck': true } });
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
    },
    changeTheme(){
      document.querySelector('body').classList.add('notransition');
      if(localStorage.getItem('theme')==='dark'){
        window.document.documentElement.setAttribute('data-theme', '')
        localStorage.setItem('theme', '');
        eventbus.emit('theme.change', 'light');
        eventbus.emit('watermark.refresh');
      }else {
        window.document.documentElement.setAttribute('data-theme', 'dark')
        localStorage.setItem('theme', 'dark');
        eventbus.emit('theme.change', 'dark');
        eventbus.emit('watermark.refresh');
      }
      setTimeout(() => {
        document.querySelector('body').classList.remove('notransition');
      }, 1000);

      this.menuList = this.menuList.map(i => {
        if (i.id == 'changeTheme') {
          return {
            id: 'changeTheme',
            name: localStorage.getItem('theme')==='dark' ? this.$t(`message.common.theme.light`) : this.$t(`message.common.theme.dark`),
            icon: 'md-repeat',
          }
        } else {
          return i;
        }
      })
    }
  },
};
</script>
<style scoped lang="scss">
@import '@dataspherestudio/shared/common/style/headerUserMenu.scss';
</style>
