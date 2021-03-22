<template>
  <div class="console-page">
    <!-- <div class="ad ad-top">广告位</div> -->
    <div class="console-page-content">
      <div class="console-page-content-head">
        <div>
          <span class="console-page-content-title">{{ $t('message.linkis.title') }}</span>
          <!-- <span class="console-page-content-info">{{ $t('message.linkis.info', {num: '13'}) }}</span> -->
        </div>
      </div>
      <div class="console-page-content-body">
        <div class="content-body-side-nav">
          <Card
            :padding="sideNavList.padding"
            :icon="sideNavList.icon"
            shadow
            class="content-body-card">
            <CellGroup
              @on-click="handleCellClick">
              <Cell
                v-for="(item, index2) in sideNavList.children"
                :key="index2"
                :class="{ crrentItem: crrentItem === item.key }"
                :title="item.name"
                :name="item.key"/>
            </CellGroup>
          </Card>
        </div>
        <div
          class="content-body-side-right">
          <div class="content-body-side-right-title">
            <Breadcrumb>
              <BreadcrumbItem :to="skipPath">{{ breadcrumbSecondName }}</BreadcrumbItem>
              <BreadcrumbItem v-if="$route.name === 'viewHistory'">{{ $route.query.taskID }}</BreadcrumbItem>
              <template v-if="$route.name === 'EngineConnList'">
                <BreadcrumbItem>{{ $route.query.instance }}</BreadcrumbItem>
                <BreadcrumbItem>EngineConnList</BreadcrumbItem>
              </template>
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
    <!-- <div class="ad-bottom">
      <div class="ad ad-bottom-div">广告位</div>
      <div class="ad ad-bottom-div">广告位</div>
    </div> -->
  </div>
</template>
<script>
import storage from '@/common/helper/storage';
export default {
  data() {
    return {
      crrentItem: '1-1',
      sideNavList: {
        key: '1',
        name: this.$t('message.linkis.sideNavList.function.name'),
        padding: 0,
        icon: 'ios-options',
        children: [
          { key: '1-1', name: this.$t('message.linkis.sideNavList.function.children.globalHistory'), path: '/console/globalHistory' },
          { key: '1-2', name: this.$t('message.linkis.sideNavList.function.children.resource'), path: '/console/resource' },
          { key: '1-3', name: this.$t('message.linkis.sideNavList.function.children.setting'), path: '/console/setting' },
          { key: '1-4', name: this.$t('message.linkis.sideNavList.function.children.dateReport'), path: '/console/globalValiable' },
          { key: '1-6', name: "ECM管理", path: '/console/ECM' },
          { key: '1-7', name: "微服务管理", path: '/console/microService' },
          { key: '1-5', name: this.$t('message.linkis.sideNavList.function.children.globalValiable'), path: '/console/FAQ' },
        ],
      },
      breadcrumbSecondName: this.$t('message.linkis.sideNavList.function.children.globalHistory'),
      contentHeight: 0,
    };
  },
  computed: {
    skipPath() {
      let path = '';
      if(this.$route.name === 'viewHistory') path = '/console';
      if(this.$route.name === 'EngineConnList') path = '/console/ECM';
      return path;
    }
  },
  created() {
    // 根据路径显示页面的标题
    this.sideNavList.children.forEach(element => {
      if(element.path === this.$route.path) {
        this.breadcrumbSecondName = element.name
      }
    });
  },
  mounted() {
    this.resize(window.innerHeight);
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.getHeight);
  },
  beforeDestroy() {
    // 监听窗口变化，获取浏览器宽高
    window.removeEventListener('resize', this.getHeight);
  },
  methods: {
    getHeight() {
      this.resize(window.innerHeight);
    },
    handleCellClick(index) {
      if(index === '1-6') {
        return this.$Message.success({
          background: true,
          content: '暂未开放！！'
        });
      }
      const activedCellParent = this.sideNavList;
      this.crrentItem = index;
      const activedCell = activedCellParent.children.find((item) => item.key === index);
      this.breadcrumbFirstName = activedCellParent.name;
      this.breadcrumbSecondName = activedCell.name;
      storage.set('lastActiveConsole', activedCell);
      this.$router.push({
        path: activedCell.path,
        query: {
          height: this.contentHeight,
          workspaceId: this.$route.query.workspaceId
        },
      });
    },
    // 设置宽高
    resize(h) {
      this.contentHeight = h - 230;
    },
  },
  beforeRouteEnter(to, from, next) {
    console.log(to, from)
    if (to.name === 'FAQ' && from.name === 'Home') {
      next((vm) => {
        vm.breadcrumbFirstName = this.$t('message.linkis.sideNavList.function.name');
        vm.breadcrumbSecondName = this.$t('message.linkis.sideNavList.function.children.globalValiable');
      });
    } else if ((to.name === 'Console' && from.name === 'Home') || (to.name === 'Console' && from.name === 'Project') || (to.name === 'Console' && from.name === 'Workflow') || !from.name) {
      const lastActiveConsole = storage.get('lastActiveConsole');
      // 如果为历史详情则直接刷新
      if(to.name === 'viewHistory') return next();
      next((vm) => {
        vm.handleCellClick(lastActiveConsole ? lastActiveConsole.key : '1-1');
      });
    }
    next();
  },
};
</script>
<style lang="scss" src="@/apps/linkis/assets/styles/console.scss"></style>
<style lang="scss" scoped>
  .crrentItem {
    color: #338cf0;
  }
</style>

