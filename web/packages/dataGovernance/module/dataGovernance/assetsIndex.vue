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
              <we-tab
                :key="work.id"
                :index="index"
                :work="work"
                :isActive="currentTab.guid === work.guid && !textColor"
                @on-choose="onChooseWork"
                @on-remove="onRemoveWork"
                ref="work_item"
              />
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
//import api from '@dataspherestudio/shared/common/service/api';
import weTab from "@dataspherestudio/shared/components/lubanTab/index.vue"
import { EventBus } from "../common/eventBus/event-bus"
export default {
  components: {
    weTab,
  },
  data() {
    return {
      textColor: true,
      index: 0,
      work: {},
      currentTab: {},
      topTapList: [],
    }
  },
  created() {},
  mounted() {
    EventBus.$on("on-choose-card", (model) => {
      let that = this
      that.textColor = false
      const topTapList = that.topTapList.slice(0)
      const { guid } = model
      that.currentTab = model
      if (topTapList.some((model) => model.guid === guid)) {
        return false
      }
      topTapList.push(model)
      that.topTapList = topTapList
    })
  },
  beforeDestroy() {
    // 销毁 eventBus
    EventBus.$off("on-choose-card")
  },
  methods: {
    // 面包屑相关
    selectProject() {
      // 返回到 目录 即搜索页面
      const workspaceId = this.$route.query.workspaceId
      this.$router.push({
        name: "dataGovernance/assets/search",
        query: { workspaceId },
      })
      this.textColor = true
    },
    onChooseWork(modal) {
      const workspaceId = this.$route.query.workspaceId
      const { guid } = modal
      this.$router.push({
        name: "dataGovernance/assets/info",
        params: { guid },
        query: { workspaceId },
      })
      this.currentTab = modal
      this.textColor = false
    },
    onRemoveWork(modal) {
      let that = this
      let topTapList = that.topTapList.slice(0)
      let len = topTapList.length
      let idx = 0

      topTapList.forEach((item, index) => {
        if (item.guid === modal.guid) {
          return (idx = index)
        }
      })

      const removeAction = () => {
        if (that.currentTab.guid === modal.guid) {
          const workspaceId = that.$route.query.workspaceId
          if (len > 1 && idx < len - 1) {
            that.currentTab = topTapList[idx + 1]
            let guid = that.currentTab.guid
            that.$router.push({
              name: "dataGovernance/assets/info",
              params: { guid },
              query: { workspaceId },
            })
          } else if (len > 1 && idx == len - 1) {
            that.currentTab = topTapList[idx - 1]
            let guid = that.currentTab.guid
            that.$router.push({
              name: "dataGovernance/assets/info",
              params: { guid },
              query: { workspaceId },
            })
          } else {
            that.currentTab = {}
            that.textColor = true
            that.$router.push({
              name: "dataGovernance/assets/search",
              query: { workspaceId },
            })
          }
        }
        topTapList.splice(idx, 1)
        that.topTapList = topTapList
      }

      removeAction()
    },
    // 通过 eventBus 获取 面包屑数据
  },
}
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";

.assets-index-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
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
      .top-r-container {
        flex: 1;
        height: 40px;
        display: flex;
        align-items: center;
        padding: 0 15px;
        .tab-item {
          display: inline-block;
          height: 24px;
          line-height: 24px;
          // color: $title-color;
          @include font-color(
            $workspace-title-color,
            $dark-workspace-title-color
          );
          cursor: pointer;
          min-width: 90px;
          max-width: 200px;
          padding: 0 10px;
          overflow: hidden;
          margin-right: 8px;
          @include bg-color(#e1e5ea, $dark-workspace-body-bg-color);
          border-radius: 12px;
          &.active {
            height: 24px;
            @include font-color($primary-color, $dark-primary-color);
            line-height: 24px;
            @include bg-color(#e8eef4, $dark-workspace-body-bg-color);
            border-radius: 12px;
          }
          &:hover {
            height: 24px;
            @include font-color($primary-color, $dark-primary-color);
            line-height: 24px;
            border-radius: 12px;
            @include bg-color(#d1d7dd, $dark-workspace-body-bg-color);
          }
        }
      }
    }
  }

  .assets-index-b {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
}
</style>
