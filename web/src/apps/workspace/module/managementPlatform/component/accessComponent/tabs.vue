<template>
  <div
    class="tabs-title"
    @click="choose"
    @mouseover="mouseover"
    @mouseout="mouseout"
  >
    <span :title="component.title_cn" class="tabs-title-text">{{
      component.title_en
    }}</span>
    <span class="tabs-title-button">
      <Icon
        v-show="isHover"
        class="close-icon"
        size="18"
        type="md-close"
        @click.stop="remove"
      />
    </span>
  </div>
</template>
<script>
export default {
  props: {
    component: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isHover: false
    };
  },
  methods: {
    mouseover() {
      this.isHover = true;
    },
    mouseout() {
      this.isHover = false;
    },
    choose() {
      this.$emit("on-choose", this.component);
    },
    remove() {
      this.$emit("on-remove", this.component);
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.tabs-title {
  position: $relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 16px;
  padding-right: 35px;
  font-size: $font-size-large;
  height: 38px;
  .tabs-title-text {
    display: block;
    flex: 1;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .tabs-title-button {
    position: $absolute;
    right: 10px;
    top: 0;
    font-size: $font-size-large;
    margin-top: -1px;
    .close-icon {
      @include font-color(rgba(0, 0, 0, 0.45), $dark-text-color);
    }
  }
}
</style>
