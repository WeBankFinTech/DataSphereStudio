<template>
  <div>
    <h3 class="item-header">
      <span >{{$t('message.workspace.AppStore.title')}}</span>
    </h3>
    <div class="nodata-tips" v-if="tabsApplication.length===0">{{$t('message.workspace.home.tips')}}</div>
    <div v-else class="app-content">
      <MenuSider class="side-bar" v-model="isCollapsed">
        <div class="mune-bar">
          <Menu class="left-mune" width="auto" :active-name="0" theme="light" @on-select="checkout">
            <MenuItem class="left-menuItem" v-for="(item, index) in tabsApplication" :key="index" :name="index">
              <span>{{item.title}}</span>
            </MenuItem>
          </Menu>
        </div>
      </MenuSider>
      <div class="pane-wrap">
        <div v-for="item in tabsApplication[actionIndex].appInstances" :key="item.name" class="pane-item" >
          <div class="item-main">
            <div class="app-title">
              <SvgIcon v-if="!!item.image" class="app-icon" :icon-class="item.image"/>
              <span class="label" :title="item.title">{{item.title}}</span>
              <span class="text-button" @click="navTo(item, item.manualButtonUrl)">{{item.active ? $t('message.workspace.AppStore.demoCase') : $t('message.workspace.AppStore.comingSoon')}}</span>
            </div>
            <div>
              <Button
                v-for="subItem in Object.keys(item.nameAndUrls)"
                :key="subItem"
                size="small"
                type="text"
                class="goto-button"
                @click="linkTo(item, item.nameAndUrls[subItem])">{{subItem}}</Button>
            </div>
          </div>
          <!-- <SvgIcon class="app-bgc" :icon-class="item.image ? item.image : 'bgc-imag'"/> -->
          <SvgIcon v-if="!!item.icon" class="app-bgc" :icon-class="item.icon"/>
          <span v-if="!item.active" class="mask">
            {{$t('message.workspace.AppStore.comingSoon')}}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import MenuSider from '@component/menuSider/index.vue';
import mixin from '@/common/service/mixin';
import util from '@/common/util';
import { GetWorkspaceData, GetWorkspaceApplications } from '@/common/service/apiCommonMethod.js';
export default {
  created(){
    this.workspaceId = this.$route.query.workspaceId;
    this.init();
  },
  components: {
    MenuSider
  },
  watch: {
    $route() {
      this.workspaceId= this.$route.query.workspaceId; //获取传来的参数
      this.init();
    }
  },
  data: function() {
    return {
      isCollapsed: false,
      workspaceId: null,
      workspaceData: {
        name: "",
        description: ""
      },
      show: false,
      applications: [],
      searchResult: [],
      addAppLoading: false,
      rules: {
        selectApp: [
          { required: true, message: this.$t('message.workspace.home.selectApp'), trigger: 'change' },
        ],
        selectType: [
          { required: true, message: this.$t('message.workspace.home.selectType'), trigger: 'change' },
        ],
      },
      search: false,
      actionIndex: 0
    };
  },
  mixins: [mixin],
  methods: {
    checkout(val) {
      this.actionIndex = val;
    },
    iconSplit(icon){
      if(icon){
        return icon.split('|')
      }
      return ['','']
    },
    init(){
      GetWorkspaceData(this.workspaceId).then(data=>{
        this.workspaceData = data.workspace;
      })
      GetWorkspaceApplications(this.workspaceId).then(data=>{
        this.applications = data.applications || [];
      })
    },
    flatApps(){
      const arr = [];
      this.applications.forEach(item=>{
        arr.push(...item.appInstances)
      })
      return arr;
    },

    onSearch(event){
      const value = event.target.value;
      if(value){
        const arr = this.flatApps();
        this.searchResult = arr.filter(item=>{
          if(item.title.indexOf(value)!==-1 || item.labels.indexOf(value)!==-1){
            return true;
          }
          return false;
        })
        this.searchResult = [{title: 'result', appInstances: [...this.searchResult]}];
        this.search = true;
      }else{
        this.search = false;
      }
    },
    linkTo(item){
      if (item.active) {
        this.gotoCommonIframe(item.name, {workspaceId: this.workspaceId})
      } else {
        return this.$Message.warning(`敬请期待！`);
      }
    },
    navTo(item, path){
      if(path){
        if(path.startsWith('http')){
          util.windowOpen(path);
        }else {
          this.$router.push({path: path, query: Object.assign({}, this.$route.query)});
        }
      }else {
        console.warn('path error', path);
      }
    }
  },

  computed: {
    tabsApplication: function(){
      if(this.search){
        return this.searchResult;
      }
      return this.applications;
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
.item-header {
  line-height: 1;
  font-size: $font-size-large;
  margin-bottom: 20px;
  @include font-color($workspace-title-color, $dark-workspace-title-color);
}
.app-content {
  display: flex;
  justify-content: flex-start;
  .side-bar {
    flex-basis: 200px;
    border-right: $border-width-base $border-style-base $border-color-base;
    @include border-color($border-color-base, $dark-border-color-base);
    border-radius: $border-radius-small;
    z-index: 1;
    margin-right: 25px;
    .left-menuItem {
      @include bg-color($light-base-color, $dark-base-color);
    }
    .ivu-menu-item-active {
        @include bg-color($active-menu-item, $dark-active-menu-item);
      }
  }
  .pane-wrap {
    flex: 1;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
    grid-row-gap: 20px;
    grid-column-gap: 20px;
    .pane-item {
      height: 120px;
      border-radius: 2px;
      border: 1px solid #DEE4EC;
      @include border-color($border-color-base, $dark-border-color-base);
      position: relative;
      padding-left: 20px;
      overflow: hidden;
      .item-main {
        position: absolute;
        z-index: 2;
      }
      .app-title {
        display: flex;
        align-items: center;
        margin-top: 25px;
        font-size: 18px;
        line-height: 24px;
        // color: $text-title-color;
        @include font-color($workspace-title-color, $dark-workspace-title-color);
        font-family: PingFangSC-Medium;
        .label {
          width: 120px;
          display: inline-block;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          font-family: PingFangSC-Medium;
          font-size: 18px;
          // color: rgba(0,0,0,0.85);
          @include font-color($workspace-title-color, $dark-workspace-title-color);
          line-height: 24px;
          vertical-align: middle;
        }
        .app-icon {
          font-size: 20px;
          line-height: 24px;
          vertical-align: middle;
          @include font-color($workspace-title-color, $dark-workspace-title-color);
        }
        .text-button {
          font-family: PingFangSC-Regular;
          font-size: 14px;
          // color: #2E92F7;
          @include font-color($primary-color, $dark-primary-color);
          line-height: 100%;
          vertical-align: middle;
          cursor: pointer;
          max-width: 75px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          display: inline-block;
        }
    }
    .goto-button {
      margin-top: 10px;
      font-family: PingFangSC-Regular;
      font-size: 14px;
      // color: rgba(0,0,0,0.65);
      @include font-color($light-text-color, $dark-text-color);
      line-height: 22px;
      text-align: left;
      border: 1px solid #DEE4EC;
      @include border-color($border-color-base, $dark-border-color-base);
      border-radius: 4px;
      cursor: pointer;
    }
    .mask {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 3;
      // background: rgba(255,255,255,0.80);
      @include bg-color(rgba(255,255,255,0.80), rgba(255,255,255,0.1));
      text-align: center;
      line-height: 116px;
      font-size: 21px;
      font-family: PingFangSC-Regular;
      color: rgba(0,0,0,0.85);
      @include font-color($workspace-title-color, $dark-workspace-title-color);
    }
    .app-bgc {
      height: 100%;
      float: right;
      opacity:0.8;
      z-index: 1;
      /deep/.svg-icon {
        width: 140px;
        height: 120px;
      }
    }
  }
}
}
.item-header {
  font-size: $font-size-large;
  font-weight: bold;
  padding-left: 12px;
  border-left: 3px solid $primary-color;
  @include border-color($primary-color, $dark-primary-color);
}

</style>