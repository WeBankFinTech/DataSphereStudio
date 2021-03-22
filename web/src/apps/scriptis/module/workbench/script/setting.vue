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
        this.work.unsave = !isEqual(JSON.parse(this.origin), val);
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
@import '@/common/style/variables.scss';
.editor-setting {
  width: 20%;
  min-width: 200px;
  background: $background-color-base;
  border-left: $border-width-base $border-style-base $border-color-base;
  overflow-y: auto;
  position: $absolute;
  top: 32px;
  right: 0;
  bottom: 0;
  .editor-setting-header {
    border-bottom: $border-width-base $border-style-base #e8eaec;
    padding: 10px 16px;
    line-height: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    span {
      color: #17233d;
      font-size: $font-size-base;
    }
    i {
      font-size: 26px;
      color: #999;
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
    background: $menu-dark-subsidiary-color;
    border-radius: $border-radius-small;
  }
}
</style>

