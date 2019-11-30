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
import api from '@/js/service/api';
export default {
  name: 'Menu',
  data() {
    return {
      menuList: [
        // {
        //     id: 'user-management',
        //     name: '用户管理',
        //     icon: 'ios-person-outline',
        // },
        {
          id: 'FAQ',
          name: this.$t('message.navMune.FAQ'),
          icon: 'ios-help-circle-outline',
        }, {
          id: 'clearCache',
          name: this.$t('message.navMune.clearCache'),
          icon: 'ios-trash-outline',
        },
        {
          id: 'changeLang',
          name: localStorage.getItem('locale') === 'zh-CN' ? 'English' : '简体中文',
          icon: 'md-repeat',
        }, {
          id: 'logout',
          name: this.$t('message.navMune.logOut'),
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
        case 'FAQ':
          this.openFAQ();
          break;
        case 'clearCache':
          this.clearCache();
          break;
        case 'logout':
          this.getRunningJob();
          break;
        case 'changeLang':
          this.changeLang();
          break;
      }
    },
    openUserManagement() {
      this.$Message.info(this.$t('message.userMenu.comingSoon'));
    },
    openFAQ() {
      const newTab = window.open('about:blank');
      setTimeout(() => {
        newTab.location.href = this.getFAQUrl();
      }, 500);
    },
    clearCache() {
      this.$Modal.confirm({
        title: this.$t('message.userMenu.title'),
        content: this.$t('message.userMenu.content'),
        onOk: () => {
          this.dispatch('IndexedDB:deleteDb');
          this.$Message.success(this.$t('message.userMenu.clearCacheSuccess'));
          setTimeout(() => {
            window.location.reload();
          }, 1000);
        },
        onCancel: () => {
        },
      });
    },
    getRunningJob() {
      // this.dispatch('Footer:getRunningJob', (num) => {
      //     debugger;
      //     if (!num) return this.logout();
      //     this.$Modal.confirm({
      //         title: '警告',
      //         content: `<p>您有${num}个任务正在执行</p><p>是否要继续登出？</p>`,
      //         onOk: () => {
      //             this.logout();
      //         },
      //         onCancel: () => {
      //         },
      //     });
      // });
      this.logout();
    },
    logout() {
      api.fetch('/user/logout', 'get').then(() => {
        this.$emit('clear-session');
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
