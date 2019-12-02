<template>
  <div
    class="workbench-title"
    @click="choose"
    @mouseover="mouseover"
    @mouseout="mouseout">
    <span
      :class="work.logo"
      class="workbench-title-logo">
    </span>
    <span
      :title="work.filepath || work.filename"
      class="workbench-title-text">{{ work.filename }}</span>
    <span class="workbench-title-button">
      <Icon
        v-show="isHover"
        type="md-close"
        @click.stop="remove"/>
      <span
        v-show="!isHover && work.unsave"
        class="fi-radio-on2"/>
      <Spin
        v-show="!isHover && work.data && work.data.running"
        size="small">
        <Icon
          type="ios-loading"
          size="12"
          class="we-icon-loading"/>
      </Spin>
    </span>
  </div>
</template>
<script>
export default {
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      isHover: false,
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
      this.$emit('on-choose', this.work);
    },
    remove() {
      this.$emit('on-remove', this.work);
    },
  },
};
</script>
