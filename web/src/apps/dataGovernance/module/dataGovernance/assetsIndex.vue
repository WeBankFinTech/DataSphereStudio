<template>
  <div class="assets-index-wrap">
    <!-- top 面包屑  -->
    <div class="assets-index-t">
      <!-- top -->
      <div class="assets-index-t-t1">
        <slot name="top">
          <div
            class="top-l-text"
            :class="{ active: textColor }"
            :style="{ color: textColor }"
            @click="selectProject"
          >
            <span>{{ $t("message.dataGovernance.menu") }}</span>
          </div>
          <div class="top-r-container">
            <template v-for="(work, index) in topTapList">
              <div
                :key="work.tabId"
                :class="{
                  active: currentTab.tabId === work.tabId && !textColor
                }"
                class="tab-item"
                ref="work_item"
              >
                <we-tab
                  :index="index"
                  :work="work"
                  @on-choose="onChooseWork"
                  @on-remove="onRemoveWork"
                />
              </div>
            </template>
          </div>
        </slot>
      </div>
    </div>

    <!-- bottom  -->
    <div class="assets-index-b">
      <router-view></router-view>
    </div>
  </div>
</template>
<script>
//import api from "@/common/service/api";
import weTab from "../../../workflows/module/common/tabList/tabs.vue";
export default {
  components: {
    weTab
  },
  data() {
    return {
      textColor: true,
      index: 0,
      work: {},
      topTapList: []
    };
  },
  created() {},
  methods: {
    // 面包屑相关
    selectProject() {},
    onChooseWork() {},
    onRemoveWork() {}
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.assets-index-wrap {
  .assets-index-t {
    .assets-index-t-t1 {
      padding: 0px $padding-25;
      border-bottom: $border-width-base $border-style-base $border-color-base;
      @include border-color(
        $background-color-base,
        $dark-workspace-body-bg-color
      );
      @include font-color($workspace-title-color, $dark-workspace-title-color);
      flex: none;
      display: flex;
      align-items: center;
      font-size: $font-size-large;
      .top-l-text {
        cursor: pointer;
        flex: none;
        font-size: $font-size-large;
        padding: 0 15px;
        margin-bottom: -1px;
        line-height: 40px;
        position: relative;
        &::after {
          content: "";
          border-left: 1px solid #dee4ec;
          @include border-color($border-color-base, $dark-border-color-base);
          width: 0;
          position: absolute;
          right: -15px;
          top: 12px;
          height: 16px;
          margin: 0 15px;
        }
      }
      .active {
        border-bottom: 2px solid $primary-color;
        @include border-color($primary-color, $dark-primary-color);
      }
      .top-r-container {
        flex: 1;
        height: 40px;
      }
    }
  }

  .assets-index-b {
  }
}
</style>
