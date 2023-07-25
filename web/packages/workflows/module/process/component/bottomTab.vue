<template>
  <div v-if="!product && ( !readonly || readonly ==='false')" class="bottom-tab-container" :class="{min: minSize}">
    <div class="tab-content" :class="{show: showContent}">
      <div class="content-head" @click="showLogPanel('', false)">
        {{curTab.name}} <span v-show="showLog">/ 日志</span>
        <Icon class="close-panel" size="18" type="md-close" @click="closePanel"></Icon>
      </div>
      <component v-show="!showLog" class="component" :is="curTab.component"
        :orchestratorId="orchestratorId"
        :orchestratorVersionId="orchestratorVersionId"
        :appId="flowId"
        @event-from-ext="eventFromExt"
        @release="release"/>
      <Log
        v-show="showLog"
        class="process-console"
        :logs="historyLogs"
        :height="264"
        @close="showLogPanel('', false)"
      />
    </div>
    <div class="tab-menu">
      <template v-for="item in tabs">
        <div v-show="!minSize" :key="item.name" class="tab-menu-item" :class="{active: item.name === curTab.name}" @click="clickItem(item)">
          <Icon v-if="item.icon" :type="item.icon"></Icon>
          {{item.name}}
          <div class="devider"></div>
        </div>
      </template>
      <span  @click="toggleSize" class="tab-menu-item" :class="{close: minSize}">
        <Icon :type="minSize?'ios-arrow-forward':'ios-arrow-back'" size="16"/>
        <Icon type="ios-options" size="20"/>
        {{ $t('message.workflow.Fold') }}
      </span>
    </div>
  </div>
</template>
<script>

import Log from './log.vue';

import plugin from '@dataspherestudio/shared/common/util/plugin';
const extComponents = plugin.emitHook('workflow_bottom_panel') || {}

export default {
  props: {
    flowId: {
      type: Number
    },
    orchestratorId: {
      type: [Number, String],
      default: null
    },
    orchestratorVersionId: {
      type: [Number, String],
      default: null
    },
    readonly: {
      type: [String, Boolean],
      default: false,
    },
    product: {
      type: [Boolean],
      default: false,
    },
  },
  components: {
    Log,
  },
  data() {
    return {
      tabs: extComponents,
      curTab: {},
      showLog: false,
      historyLogs: '',
      showContent: false,
      minSize: true
    }
  },
  methods: {
    release (obj) {
      this.$emit('release', obj)
    },
    clickItem(item) {
      if (this.curTab.name !== item.name) {
        this.showContent = true
        this.showLog = false
        this.curTab = item
      } else {
        if (this.showContent) {
          this.closePanel()
        } else {
          this.showContent = true
          this.curTab = item
        }
      }
    },
    toggleSize() {
      this.minSize = !this.minSize
      this.showLog = false
      this.closePanel()
    },
    showLogPanel(logs, show = true) {
      this.showLog = show
      this.historyLogs = logs
    },
    closePanel() {
      this.showContent = false
      this.curTab = {}
    },
    showPanel(key) {
      const item = this.tabs.find(it => it.key === key)
      if (this.curTab.key !== item.key) {
        this.minSize =false
        this.showContent = true
        this.showLog = false
        this.curTab = item
      }
    },
    eventFromExt(evt) {
      if (evt && evt.callFn && typeof this[evt.callFn] === 'function') {
        this[evt.callFn](...evt.params)
      }
    },
  },
  mounted() {
    plugin.emitHook('workflow_bottom_panel_mounted', this)
  }
}
</script>
<style scoped lang="scss">
@import '@dataspherestudio/shared/common/style/variables.scss';
.bottom-tab-container {
  height: 32px;
  z-index: 1002;
  position: absolute;
  bottom: 0px;
  width: 100%;
  &.min {
    width: 40px;
    overflow: hidden;
  }
  .content-head {
    @include bg-color(#F8F9FC, $dark-background-color-header);
    @include font-color(#333, $dark-text-color);
    border-top: $border-width-base $border-style-base #dcdcdc;
    border-bottom: $border-width-base $border-style-base #dcdcdc;
    @include border-color($border-color-base, $dark-menu-base-color);
    padding: 5px 10px
  }
  .tab-content {
    position: absolute;
    top: 32px;
    @include bg-color($light-base-color, $dark-base-color);
    width: 100%;
    @include border-color(#dee4ec, $dark-menu-base-color);
    z-index: 1;
    transition: transform .5s;
    &.show{
      transform: translate3d(0, -292px, 0)
    }
    .component {
      height: 230px;
      overflow-x: auto;
      overflow-y: hidden
    }
  }
  .process-console {
    left: 0;
    top: 28px;
    min-height: 230px;
  }
  .close-panel {
    float: right;
  }
  .tab-menu {
    display: flex;
    @include bg-color(#F8F9FC, $dark-background-color-header);
    z-index: 3;
  }
  .tab-menu-item {
    height: 32px;
    line-height: 32px;
    margin-left: 10px;
    cursor: pointer;
    @include font-color(#333, $dark-text-color);
    &.active {
      @include font-color($primary-color, $dark-primary-color);
    }
    &.close {
      background: #001C40;
      @include font-color($primary-color, $dark-primary-color);
      margin-left: 0;
    }
  }
  .devider {
    border-left: 1px solid $border-color-base;
    @include border-color(#DEE4EC, $dark-text-color);
    margin: 8px 15px;
    display: inline;
  }
}

</style>
