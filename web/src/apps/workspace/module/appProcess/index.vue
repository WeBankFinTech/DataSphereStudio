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
          <Button class="link" @click="openTab(item.url)">{{item.title}}</Button>
        </div>
        <SvgIcon :key="item.id" v-if="index !== appDevProcess.length - 1" class="icon-item" icon-class="right-ar"/>
      </div>
    </div>
  </div>
</template>
<script>
import util from '@/common/util';
import { GetDicList } from '@/common/service/apiCommonMethod.js';
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
@import '@/common/style/variables.scss';
.item-header {
  margin-bottom: 20px;
  font-size: $font-size-large;
  font-weight: bold;
  &::before {
    content: '';
    padding-right: 12px;
    border-left: 3px solid $primary-color;
  }
}
.list-content {
  .list-item-bar {
    position: relative;
    float: left;
    margin-right: 25px;
    margin-bottom: 25px;
    .list-item {
      padding: 10px;
      border: 1px solid #ccc;
      border-radius: $border-radius-small;
      width: 186px;
      height: 386px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      align-items: center;
      .status-icon {
        font-size: 115px;
      }
      .title {
        font-size: $font-size-large;
        font-weight: bold;
      }
      .desc {
        font-size: $font-size-small;
        display: none;
        width: 100px;
      }
      .link {
        font-size: $font-size-base;
      }
      &:hover {
        transform: translateY(-5px);
        transition: transform .2s linear;
        box-shadow: 0 2px 12px 0 $shadow-color;
        .status-icon {
          margin-top: 0;
        }
        .desc {
          display: block;
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
      right: -35px;
      font-size: 30px;
    }
  }
}
</style>