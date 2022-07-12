<template>
  <div class="access-component-wrap">
    <!-- headers -->
    <div class="access-component-headers">
      <slot name="header">
        <div class="access-component-headers-container">
          <template v-for="(component, index) in topTapList">
            <we-tab
              ref="component_item"
              :key="component._id"
              :index="index"
              :isActive="currentTab._id == component._id"
              :component="component"
              @on-choose="onChooseComponent"
              @on-remove="removeComponent"
            />
          </template>
        </div>
      </slot>
    </div>
    <!-- appmain -->
    <div class="access-component-appmain" v-if="topTapList.length">
      <tab-item :componentData="currentTab" @on-save="saveComponent" />
    </div>
  </div>
</template>

<script>
import tabItem from "./tab-item.vue"
// import weTab from "./tabs.vue";
import weTab from "@dataspherestudio/shared/components/lubanTab/index.vue"
export default {
  name: "accessComponent",
  components: {
    tabItem,
    weTab,
  },
  props: {
    // 顶部component list数据
    topTapList: {
      type: Array,
      default: () => [],
    },
    currentTab: {
      type: null,
    },
  },
  data() {
    return {}
  },
  watch: {
    currentTab(newValue) {
      this.currentTab = newValue
    },
  },
  methods: {
    removeComponent(tabData) {
      this.$emit("handleTabRemove", tabData._id)
    },
    onChooseComponent(tabData) {
      this.$emit("bandleTapTab", tabData._id)
    },
    saveComponent(componentItem) {
      this.$emit("on-save", componentItem)
    },
  },
}
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.access-component-headers {
  padding: 0px 16px;
  border-bottom: $border-width-base $border-style-base $border-color-base;
  @include border-color($border-color-base, $dark-border-color-base);
  flex: none;
  display: flex;
  align-items: center;
  &-container {
    flex: 1;
    display: flex;
    align-items: center;
    height: 40px;
    overflow-x: auto;
    overflow-y: hidden;
  }
}
.tab-item {
  display: inline-block;
  height: 24px;
  line-height: 24px;
  // color: $title-color;
  @include font-color($workspace-title-color, $dark-workspace-title-color);
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
</style>
