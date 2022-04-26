<template>
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
</template>
<script>
export default {
  props: {
    work: {
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
      this.$emit("on-choose", this.work);
    },
    remove() {
      this.$emit("on-remove", this.work);
    }
  }
};
</script>
<style scoped lang="scss">
@import "@dataspherestudio/shared/common/style/variables.scss";
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
