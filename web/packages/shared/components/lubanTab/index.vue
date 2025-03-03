<template>
  <div class="tabs-wrap" :class="{ active: isActive }">
    <div
      class="tabs-title"
      @click="choose"
      @mouseover="mouseover"
      @mouseout="mouseout"
    >
      <span :title="work.name" class="tabs-title-text">{{ work.name }}</span>
      <span class="tabs-title-button">
        <Icon
          v-show="isHover"
          class="close-icon"
          size="12"
          type="md-close"
          @click.stop="remove"
        />
      </span>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    work: {
      type: Object,
      required: true,
    },
    isActive: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isHover: false,
    }
  },
  methods: {
    mouseover() {
      this.isHover = true
    },
    mouseout() {
      this.isHover = false
    },
    choose() {
      this.$emit("on-choose", this.work)
    },
    remove() {
      this.$emit("on-remove", this.work)
    },
  },
}
</script>
<style scoped lang="scss">
@import "@dataspherestudio/shared/common/style/variables.scss";

.tabs-wrap {
  display: inline-block;
  height: 24px;
  line-height: 24px;
  // color: $title-color;
  @include font-color($workspace-title-color, $dark-workspace-title-color);
  cursor: pointer;
  min-width: 90px;
  max-width: 135px;
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
    max-width: none;
    border:1px solid #2E92F7
  }
  &:hover {
    height: 24px;
    @include font-color($primary-color, $dark-primary-color);
    line-height: 24px;
    border-radius: 12px;
    @include bg-color(#d1d7dd, $dark-workspace-body-bg-color);
  }
}
.tabs-title {
  position: $relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 12px;
  font-size: 14px;
  height: 24px;
  .tabs-title-text {
    display: block;
    flex: 1;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .tabs-title-button {
    position: $absolute;
    right: -5px;
    top: 0;
    font-size: $font-size-large;
    margin-top: -1px;
    .close-icon {
      @include font-color(rgba(0, 0, 0, 0.45), #f4f7fb);
    }
  }
}
</style>
