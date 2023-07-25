<template>
  <div
    class="workbench-title"
    @click="choose"
    @mouseover="mouseover"
    @mouseout="mouseout">
    <SvgIcon class='workbench-title-logo' v-if="work.logo" :icon-class="work.logo" :color="work.color"/>
    <span
      v-if="!(node && index === 0)"
      :title="work.filepath || work.filename"
      class="workbench-title-text">{{ work.filename }}</span>
    <span
      v-if="(node && index === 0)"
      :title="work.filepath || work.filename"
      class="workbench-title-text">{{$t('message.scripts.container.title.editorName')}}</span>
    <span
      class="workbench-title-button"
      v-if="!(node && index === 0)">
      <Icon
        v-show="isHover"
        type="md-close"
        style="opacity: 0.65;"
        @click.stop="remove"/>
      <SvgIcon v-show="!isHover && work.unsave && work.type !== 'historyScript'"
        :style="{ color: work.unsave ? '#ed4014' : '' }"
        icon-class="fi-radio-on2"/>
      <Spin
        v-show="!isHover && work.data && work.data.running"
        size="small">
        <Icon
          type="ios-loading"
          size="12"
          style="opacity: 0.85;"
          class="we-icon-loading"/>
      </Spin>
    </span>
  </div>
</template>
<script>
export default {
  name: 'we-title',
  props: {
    work: {
      type: Object,
      required: true,
    },
    node: Object,
    index: Number
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
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .workbench-title {
    position: $relative;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 0 16px;
    padding-right: 30px;
    font-size: $font-size-base;
    height: 24px;
    line-height: 24px;
    .workbench-title-logo {
        font-size: 16px;
        margin-right: 4px;
    }
    .workbench-title-text {
        display: block;
        flex: 1;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    .workbench-title-button {
        position: $absolute;
        right: 10px;
        top: 0;
        font-size: $font-size-base;
        .fi-radio-on2 {
            font-size: $font-size-small;
        }
    }

    .workbench-title-hover {
      @include bg-color(#d1d7dd, $dark-workspace-body-bg-color) ;
    }
  }
</style>

