<template>
  <div class="editor-setting">
    <div class="editor-setting-header">
      <span>{{ $t('message.scripts.setting.header') }}</span>
      <Icon
        type="ios-close"
        @click="$emit('setting-close')"/>
    </div>
    <div class="editor-setting-content">
      <custom-variable
        ref="customVariable"
        :script="script"></custom-variable>
    </div>
  </div>
</template>
<script>
import customVariable from '../setting/customVariable.vue';
import { isEqual } from 'lodash';
export default {
  name: 'Setting',
  components: {
    customVariable,
  },
  props: {
    work: {
      type: Object,
      required: true,
    },
    script: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      origin: null,
    };
  },
  watch: {
    'script.params': {
      handler: function(val) {
        this.work.unsave = !this.script.readOnly && !isEqual(JSON.parse(this.origin), val);
      },
      deep: true,
    },
  },
  mounted() {
    this.origin = JSON.stringify(this.script.params);
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.editor-setting {
  width: 100%;
  min-width: 200px;
  height: 100%;
  // min-width: 50px;
  @include bg-color($light-base-color, $dark-base-color);
  border-left: $border-width-base $border-style-base $border-color-base;
  @include border-color($border-color-base, $dark-workspace-background);
  overflow: auto;
  // position: $absolute;
  // top: 32px;
  // right: 0;
  // bottom: 0;
  .editor-setting-header {
    border-bottom: $border-width-base $border-style-base #e8eaec;
    @include border-color($border-color-base, $dark-border-color-base);
    padding: 10px 16px;
    line-height: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    span {
      // color: #17233d;
      @include font-color($workspace-title-color, $dark-workspace-title-color);
      font-size: $font-size-base;
    }
    i {
      font-size: 26px;
      // color: #999;
      @include font-color(#999, $dark-text-color);
      cursor: pointer;
    }
  }
  .setting-title-wrap {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 30px;
    padding-right: 7px;
    .setting-title {
      display: inline-block;
    }
    .setting-icon-add {
      cursor: pointer;
    }
  }
  .runtime-args {
    padding: 10px 10px 2px;
    margin: 10px;
    @include bg-color($menu-dark-subsidiary-color, $dark-base-color);
    border-radius: $border-radius-small;
  }
}
</style>

