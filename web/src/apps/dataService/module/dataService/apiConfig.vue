<template>
  <div>
    <div class="page-fc-header">
      <p>数据服务</p>
      <div class="divider">/</div>
      <p class="header-subTitle">服务开发</p>
    </div>
    <div class="emptyGuideWrap" v-if="tabDatas.length === 0">
      <empty-guide />
    </div>
    <div class="tabWrap" v-if="tabDatas.length > 0">
      <template v-for="(work, index) in tabDatas">
        <div
          :key="index"
          :class="{ active: work.isActive }"
          class="tab-item"
          ref="work_item"
        >
          <webTab
            :index="index"
            :work="work"
            @on-choose="onChooseWork"
            @on-remove="removeWork"
          />
        </div>
      </template>
    </div>

    <params-config
      v-for="(configData, index) in tabDatas"
      :key="index"
      v-show="configData.isActive"
      :apiData="configData"
      @showApiForm="showApiModel"
      @updateApiData="updateApiData"
    />
  </div>
</template>
<script>
import webTab from "@/apps/workflows/module/common/tabList/tabs.vue";
import paramsConfig from "./paramsConfig.vue";
import emptyGuide from "./emptyGuide.vue";
// import api from "@/common/service/api";
export default {
  components: {
    webTab,
    paramsConfig,
    emptyGuide
  },
  props: {
    tabDatas: {
      type: Array,
      default: () => []
    },
    showApiForm: {
      type: Function
    }
  },
  data() {
    return {
      currentTab: { name: "sdfsaf", id: 1 },
      navFold: false,
      confirmLoading: false,
      modalType: "",
      modalVisible: false
    };
  },
  computed: {
    choosedData() {
      console.log(12333);
      return this.tabDatas.find(item => item.isActive);
    }
  },
  methods: {
    removeWork(tabData) {
      this.$emit("removeTab", tabData.id);
    },
    onChooseWork(tabData) {
      if (tabData.isActive) {
        return;
      } else {
        this.$emit("changeTab", tabData.id);
      }
    },
    showApiModel(apiData) {
      console.log(apiData);
      this.$emit("showApiForm", apiData);
    },
    updateApiData(data) {
      console.log(data);
      this.$emit("updateApiData", data);
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.page-fc-header {
  padding: 25px 0px 25px 10px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  font-size: 21px;
  @include font-color($text-title-color, $dark-text-color);
  font-family: PingFangSC-Regular;
  .divider {
    padding: 0 10px;
  }
  .header-subTitle {
    font-family: PingFangSC-Medium;
  }
} 
.emptyGuideWrap{
  width: 100%;
  min-height: 400px;
  height: calc( 100vh - 140px );
  min-width: 600px;
  border-top: 1px solid #DEE4EC;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
}
.tabWrap {
  padding-left: 10px;
  .tab-item {
    display: inline-block;
    height: 40px;
    line-height: 40px;
    @include font-color($title-color, $dark-text-color);
    cursor: pointer;
    min-width: 100px;
    max-width: 200px;
    overflow: hidden;
    margin-right: 2px;
    &.active {
      height: 40px;
      color: $primary-color;
      border-radius: 4px 4px 0 0;
      border-bottom: 2px solid $primary-color;
      line-height: 38px;
    }
  }
}
.toolBar {
  width: 100%;
  height: 48px;
  margin-top: -5px;
  background: #f8f9fc;
  border: 1px solid rgba($color: #000000, $alpha: 0.2);
  border-left-width: 0;
  border-right-width: 0;
  box-sizing: border-box;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-left: 10px;
  .toolWrap {
    display: flex;
    justify-content: space-around;
    align-items: center;
    width: 90px;
    cursor: pointer;
    font-family: PingFangSC-Regular;
    font-size: 16px;
    color: rgba(0, 0, 0, 0.65);
    & img {
      width: 16px;
    }
    .divider {
      width: 1px;
      height: 16px;
      background: rgba(0, 0, 0, 0.25);
      &.last-divider {
        background: transparent;
      }
    }
  }
}
</style>
