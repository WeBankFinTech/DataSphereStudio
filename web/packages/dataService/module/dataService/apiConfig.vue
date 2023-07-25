<template>
  <div class="apiConfigWrap">
    <div class="emptyGuideWrap" v-if="tabDatas.length === 0">
      <empty-guide />
    </div>
    <div class="tabWrap" v-if="tabDatas.length > 0">
      <template v-for="(work, index) in tabDatas">
        <weTab
          :index="index"
          :work="work"
          :isActive="work.isActive"
          :key="index"
          ref="work_item"
          @on-choose="onChooseWork"
          @on-remove="removeWork"
        />
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
import weTab from "@dataspherestudio/shared/components/lubanTab/index.vue"
import paramsConfig from "./paramsConfig.vue"
import emptyGuide from "./emptyGuide.vue"
// import api from '@dataspherestudio/shared/common/service/api';
export default {
  components: {
    weTab,
    paramsConfig,
    emptyGuide,
  },
  props: {
    tabDatas: {
      type: Array,
      default: () => [],
    },
    showApiForm: {
      type: Function,
    },
  },
  data() {
    return {
      currentTab: { name: "sdfsaf", id: 1 },
      navFold: false,
      confirmLoading: false,
      modalType: "",
      modalVisible: false,
    }
  },
  computed: {
    choosedData() {
      return this.tabDatas.find((item) => item.isActive)
    },
  },
  methods: {
    removeWork(tabData) {
      this.$emit("removeTab", tabData.id)
    },
    onChooseWork(tabData) {
      if (tabData.isActive) {
        return
      } else {
        this.$emit("changeTab", tabData.id)
      }
    },
    showApiModel(apiData) {
      console.log(apiData)
      this.$emit("showApiForm", apiData)
    },
    updateApiData(data) {
      console.log(data)
      this.$emit("updateApiData", data)
    },
  },
}
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.emptyGuideWrap {
  width: 100%;
  min-height: 400px;
  height: calc(100vh - 140px);
  min-width: 600px;
  display: flex;
  justify-content: center;
  align-items: center;
  @include bg-color(#fff, $dark-base-color);
}
.tabWrap {
  align-items: center;
  padding: 0 16px;
  height: 40px;
  display: flex;
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
