<template>
  <div class="console-page">
    <div class="console-page-content">
      <div class="console-page-content-head">
        <div>
          <span class="console-page-content-title">{{ $t('message.apiService.title') }}</span>
        </div>
      </div>
      <div class="console-page-content-body">
        <div class="content-body-side-nav">
          <Card
            v-for="(card, index) in sideNavList"
            :key="index"
            :padding="card.padding"
            :title="card.name"
            :icon="card.icon"
            shadow
            class="content-body-card">
            <CellGroup
              @on-click="handleCellClick">
              <Cell
                v-for="(item, index2) in card.children"
                :key="index2"
                :title="item.name"
                :name="item.key"/>
            </CellGroup>
          </Card>
        </div>
        <div
          class="content-body-side-right">
          <div class="content-body-side-right-title">
            <Breadcrumb>
              <BreadcrumbItem to="/apiService">{{ breadcrumbFirstName }}</BreadcrumbItem>
              <BreadcrumbItem>{{ breadcrumbSecondName }}</BreadcrumbItem>
            </Breadcrumb>
          </div>
          <div
            class="content-body-side-right-content"
            :style="{'height': contentHeight + 'px'}">
            <router-view></router-view>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import storage from '@/js/helper/storage';
export default {
  data() {
    return {
      sideNavList: [{
        key: '1',
        name: this.$t('message.apiService.sideNavList.function.name'),
        padding: 0,
        icon: 'ios-options',
        children: [
          { key: '1-1', name: this.$t('message.apiService.sideNavList.function.children.apiMarket'), path: '/apiService/apiMarket' },
          // { key: '1-2', name: '', path: '/apiService/apiTest' },
        ],
      }],
      breadcrumbFirstName: this.$t('message.apiService.sideNavList.function.name'),
      breadcrumbSecondName: this.$t('message.apiService.sideNavList.function.children.apiMarket'),
      contentHeight: 0,
    };
  },
  created() {
  },
  mounted() {
    this.resize(window.innerHeight);
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', () => {
      this.resize(window.innerHeight);
    });
  },
  beforeDestroy() {
    // 监听窗口变化，获取浏览器宽高
    window.removeEventListener('resize', () => {
      this.resize(window.innerHeight);
    });
  },
  methods: {
    handleCellClick(index) {
      const activedCellParent = this.sideNavList[index.slice(0, 1) - 1];
      const activedCell = activedCellParent.children.find((item) => item.key === index);
      this.breadcrumbFirstName = activedCellParent.name;
      this.breadcrumbSecondName = activedCell.name;
      storage.set('lastActiveConsole', activedCell);
      this.$router.push({
        path: activedCell.path,
        query: {
          height: this.contentHeight,
        },
      });
    },
    // 设置宽高
    resize(h) {
      this.contentHeight = h - 230;
    },
  },
  beforeRouteEnter(to, from, next) {
    if (to.name === 'FAQ' && from.name === 'Home') {
      next((vm) => {
        vm.breadcrumbFirstName = this.$t('message.apiService.sideNavList.function.name');
        vm.breadcrumbSecondName = this.$t('message.apiService.sideNavList.function.children.apiMarket');
      });
    } else if (to.name === 'Console' && from.name === 'Home') {
      const lastActiveConsole = storage.get('lastActiveConsole');
      next((vm) => {
        vm.handleCellClick(lastActiveConsole ? lastActiveConsole.key : '1-1');
      });
    }
    next();
  },
};
</script>
<style lang="scss" src="@assets/styles/console.scss"></style>
