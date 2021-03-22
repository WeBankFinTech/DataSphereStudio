<template>
  <div
    class="workbench-title"
    @click="choose"
    @mouseover="mouseover"
    @mouseout="mouseout">
    <!-- <span
      :class="work.logo"
      class="workbench-title-logo">
    </span> -->
    <SvgIcon class='workbench-title-logo' :icon-class="work.logo" :color="iconColor[work.logo]"/>
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
        @click.stop="remove"/>
      <SvgIcon v-show="!isHover && work.unsave && work.type !== 'historyScript'" icon-class="fi-radio-on2"/>
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
    node: Object,
    index: Number
  },
  data() {
    return {
      isHover: false,
      iconColor: {
        'fi-bi': '#9771E7',
        'fi-spark': '#FF9900',
        'fi-hive': '#F4CF38',
        'fi-storage': '#4DB091',
        'fi-scala': '#ED4014',
        'fi-jdbc': '#444444',
        'fi-python': '#3573A6',
        'fi-spark-python': '#3573A6',
        'fi-r': '#2D8CF0',
        'fi-txt': '#444444',
        'fi-log': '#444444',
      },
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
@import '@/common/style/variables.scss';
  .workbench-title {
    position: $relative;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 0 16px;
    padding-right: 30px;
    font-size: $font-size-base;
    height: 30px;
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
  }
</style>

