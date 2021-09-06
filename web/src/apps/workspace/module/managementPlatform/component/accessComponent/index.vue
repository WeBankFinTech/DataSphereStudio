<template>
  <div class="access-component-wrap">
    <!-- headers -->
    <div class="access-component-headers">
      <slot name="header">
        <div class="access-component-headers-container">
          <template v-for="(component, index) in topTapList">
            <div
              :key="component._id"
              :class="{ active: currentTab._id === component._id }"
              class="tab-item"
              ref="component_item"
            >
              <we-tab
                :index="index"
                :component="component"
                @on-choose="onChooseComponent"
                @on-remove="removeComponent"
              />
            </div>
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
import tabItem from "./tab-item.vue";
import weTab from "./tabs.vue";
export default {
  name: "accessComponent",
  components: {
    tabItem,
    weTab
  },
  props: {
    // 顶部component list数据
    topTapList: {
      type: Array,
      default: () => []
    },
    currentTab: {
      type: null
    }
  },
  data() {
    return {};
  },
  watch: {
    currentTab(newValue) {
      this.currentTab = newValue;
    }
  },
  methods: {
    removeComponent(tabData) {
      this.$emit("handleTabRemove", tabData._id);
    },
    onChooseComponent(tabData) {
      this.$emit("bandleTapTab", tabData._id);
    },
    saveComponent(componentItem) {
      this.$emit("on-save", componentItem);
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.access-component-headers {
  padding: 0px $padding-25;
  border-bottom: $border-width-base $border-style-base $border-color-base;
  @include border-color($border-color-base, $dark-border-color-base);
  margin-top: 12px;
  flex: none;
  display: flex;
  align-items: center;
  font-size: $font-size-large;
  .active {
    border-bottom: 2px solid $primary-color;
  }
  &-container {
    flex: 1;
    height: 40px;
  }
}
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
</style>
