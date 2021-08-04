<template>
  <div class="management-platform-container" >
    <!-- list header -->
    <div class="management-platform-container-tapbar">
      <div class="management-platform-container-tapbar-header">
        <span>{{ header }}</span>
      </div>
    </div>
    <!-- list container -->
    <!-- 组件接入 -->
    <div class="management-platform-container-list"  v-if="header==='组件接入'">

      <!-- tabs -->

      <!-- appmain  -->
      <div  class="management-platform-container-list-appmain">
        <access-component
        :topTapList="topTapList"
        :currentTab="currentTab"
        @bandleTapTab="tabClick"
        @handleTabRemove="tabRemove"
        @on-save="saveComponent"
        />
      </div>

    </div>
    <div class="management-platform-container-list" :class="{'consoleStyle': header == '控制台'}" v-else>

      <!-- 面包屑  管理台和组件接入模块拥有 -->
      <div class="management-platform-container-list-breadcrumb" v-if="breadcrumbName">
        <Breadcrumb>
          <BreadcrumbItem :to="skipPath">{{ breadcrumbName }}</BreadcrumbItem>
            <BreadcrumbItem v-if="$route.name === 'viewHistory'">{{ $route.query.taskID }}</BreadcrumbItem>
              <template v-if="$route.name === 'EngineConnList'">
                <BreadcrumbItem>{{ $route.query.instance }}</BreadcrumbItem>
              <BreadcrumbItem>EngineConnList</BreadcrumbItem>
          </template>
        </Breadcrumb>
      </div>

      <!-- appmain  -->
      <div  class="management-platform-container-list-appmain" :class="{'addHeight': header == '控制台' }">
        <router-view></router-view>
      </div>

    </div>
  </div>
</template>

<script>
import accessComponent from '../accessComponent/index.vue'
export default {
  name: 'TabList',
  components: {
    'access-component': accessComponent
  },
  props: {
    header: {
      type: String,
      default: ''
    },
    breadcrumbName: {
      type: String,
      default: ''
    },
    contentHeight: {
      type: Number,
      default: 400
    },
    // 组件接入相关
    topTapList: {
      type: Array,
      required: true,
    },
    currentTab: {
      type: null
    },
  },
  computed: {
    skipPath() {
      let path = '';
      if(this.$route.name === 'viewHistory') path = '/managementPlatform/globalHistory';
      if(this.$route.name === 'EngineConnList') path = '/managementPlatform/ECM';
      return path;
    }
  },
  methods: {
    tabClick(id) {
      this.$emit('bandleTapTab', id)
    },
    tabRemove(id) {
      this.$emit('handleTabRemove', id)
    },
    saveComponent(componentItem) {
      this.$emit('on-save', componentItem)
    }
  }
}
</script>

<style lang="scss" scoped>
.management-platform-container {
  margin-left: 304px;
  transition: margin-left .3s;
  position: relative;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  &.sidebar-fold {
    margin-left: 54px;
  }
  &-tapbar {
    background-color: #fff;
    &-header {
      padding: 24px 24px;
      color: rgba(0,0,0,0.65);
      font-family: PingFangSC-Medium;
      font-size: 21px;
      border-bottom: 24px solid #EDF1F6;
    }
  }
  &-list {
    flex: 1;
    display: flex;
    min-height: 685px;
    background: #fff;
    flex-direction: column;
    &-breadcrumb {
      height: 30px;
      border-bottom: 1px solid #DEE4EC;
      margin-bottom: 10px;
      line-height: 30px;
      width: 100%;
      // margin-left: -24px;
      padding-left: 24px;
    }
    &-appmain {
      overflow: hidden;
    }
  }
}
.consoleStyle {
  min-height: 0px;
  margin-left: 24px
}
.addHeight {
  height: 400px;
}
</style>
