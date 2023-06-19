<template>
  <div>
    <h3 class="item-header">
      <span >{{$t('message.Project.AppDevProcess')}}</span>
    </h3>
    <div class="list-content">
      <div class="list-item-bar" v-for="(item, index) in appDevProcess" :key="item.id">
        <div  class="list-item">
          <SvgIcon class="status-icon" :icon-class="item.icon"/>
          <div class="title">{{item.dicName}}</div>
          <p class="desc">{{item.dicValue}}</p>
          <Button v-if="item.url" class="link" @click="openTab(item.url)">{{item.title}}</Button>
          <Button v-else class="link" disabled>{{ $t('message.workspace.Staytuned') }}</Button>
        </div>
        <SvgIcon :key="item.id" v-if="index !== appDevProcess.length - 1" class="icon-item" icon-class="right-ar"/>
      </div>
    </div>
  </div>
</template>
<script>
import util from '@dataspherestudio/shared/common/util';
import { GetDicList } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
export default {
  data(){
    return {
      appDevProcess: []
    }
  },
  created() {
    this.getData();
  },
  methods: {
    // 获取应用流程数据
    getData() {
      const params = {
        parentKey: "w_develop_process",
        workspaceId: this.$route.query.workspaceId
      }
      GetDicList(params).then((res) => {
        this.appDevProcess = res.list;
      })
    },
    openTab(url) {
      util.windowOpen(url);
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.item-header {
  margin-bottom: 20px;
  font-size: $font-size-large;
  font-weight: bold;
  @include font-color($workspace-title-color,$dark-workspace-title-color);
  &::before {
    content: '';
    padding-right: 12px;
    border-left: 3px solid $primary-color;
    @include border-color($primary-color, $dark-primary-color);
  }
}
.list-content {
  display: flex;
  flex-direction: row;
  .list-item-bar {
    position: relative;
    flex: 1;
    margin-left: 20px;
    &:first-child {
      margin-left: 0;
    }
    .list-item {
      padding: 35px 15px 15px 15px;
      @include bg-color($app-process-bg-color, $dark-app-process-bg-color);
      border: 1px solid #DEE4EC;
      @include border-color($border-color-base, $dark-app-process-bg-color);
      box-shadow: 1px 1px 2px 1px rgba(29, 121, 214, 0.1);
      border-radius: $border-radius-small;
      min-width: 140px;
      height: 360px;
      display: flex;
      flex-direction: column;
      align-items: center;
      .status-icon {
        font-size: 115px;
      }
      .title {
        font-size: $font-size-large;
        font-weight: bold;
        margin-top: 5px;
        @include font-color($workspace-title-color, $dark-workspace-title-color);
      }
      .desc {
        font-size: $font-size-small;
        display: none;
        @include font-color($light-text-desc-color, $dark-text-desc-color);
      }
      .link {
        height: 30px;
        width: 95px;
        margin-top: 35px;
        font-size: $font-size-base;
        display: flex;
        flex-direction: column;
        justify-content: space-evenly;
        @include bg-color($app-process-bg-color, $dark-app-process-bg-color);
        @include border-color($border-color-base, $dark-border-color-base);
        @include font-color($light-text-color, $dark-text-color);
      }
      &:hover {
        transform: translateY(-1px);
        transition: transform .2s linear;
        box-shadow: 2px 2px 2px 1px rgba(29, 121, 214, 0.1);
        .status-icon {
          margin-top: 0;
        }
        .desc {
          display: block;
          margin-top: 15px;
        }
        .link {
          margin-top: 15px;
        }
        .title {
          color:$primary-color;
        }
      }
    }
    .icon-item {
      margin: 0 20px;
      position: absolute;
      top: 50%;
      right: -40px;
      font-size: 30px;
      /deep/.svg-icon {
        height: 20px;
      }
    }
  }
  @media only screen and (min-width: 1600px){
    .list-item-bar {
      margin-left: 40px;
      &:first-child {
        margin-left: 0;
      }
    }
  }
}
</style>
