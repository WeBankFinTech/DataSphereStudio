<template>
  <div v-if="!product && ( !readonly || readonly ==='false')" class="bottom-tab-container" :class="{min: minSize}">
    <div class="tab-content" :class="{show: showContent}">
      <div class="panel-content" :style="panelStyle">
        <div 
          class="drag-handle"
          @mousedown="startDrag"
          @mouseover="handleMouseOver"
          @mouseout="handleMouseOut">
          <div class="drag-line" :class="{ 'hover': isHandleHover }"></div>
        </div>
        <div class="content-wrapper">
          <div class="content-head" @click="showLogPanel('', false, 'out')">
            {{curTab.name}} <span v-show="showLog">/ 日志</span>
            <Icon class="close-panel" size="18" type="md-close" @click="closePanel"></Icon>
          </div>
          <div class="content-body">
            <component 
              ref="bottomTabPage"
              v-show="!showLog" 
              class="component" 
              :is="curTab.component"
              :orchestratorId="orchestratorId"
              :orchestratorVersionId="orchestratorVersionId"
              :appId="flowId"
              @event-from-ext="eventFromExt"
              @release="release"/>
            <Log
              v-show="showLog"
              class="process-console"
              :logs="historyLogs"
              :height="currentPanelHeight"
              @close="showLogPanel('', false)"
            />
          </div>
        </div>
      </div>
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
import api from '@dataspherestudio/shared/common/service/api';

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
      minSize: true,
      currentPanelHeight: 294,
      panelHeight: 294,
      isDragging: false,
      startY: 0,
      startHeight: 0,
      isHandleHover: false,
    }
  },
  computed: {
    panelStyle() {
      return {
        height: this.showContent ? `${this.currentPanelHeight}px` : '32px',
        transform: this.showContent ? `translateY(-${this.currentPanelHeight}px)` : 'none'
      }
    }
  },
  methods: {
    handleWindowResize() {
      this.panelHeight = this.showContent ? 294 : 32;
      this.currentPanelHeight = this.panelHeight;
    },
    release (obj) {
      this.$emit('release', obj)
    },
    clickItem(item) {
      if (this.curTab.name !== item.name) {
        if (this.showContent === false) {
            this.panelHeight = 294;
        }
        this.showContent = true;
        this.showLog = false;
        this.curTab = item;
      } else {
        if (this.showContent) {
          this.closePanel();
        } else {
          if(this.showContent === false) {
            this.panelHeight = 294;
          }
          this.showContent = true;
          this.curTab = item;
        }
      }
      this.currentPanelHeight = this.panelHeight;
      setTimeout(() => {
        if(this.$refs.bottomTabPage && this.$refs.bottomTabPage.calcTableHeight) {
          this.$refs.bottomTabPage.calcTableHeight(this.currentPanelHeight - 32);
        }
      }, 100)
    },
    toggleSize() {
      this.minSize = !this.minSize
      this.showLog = false
      this.closePanel()
    },
    showLogPanel(logPath, show = true, type = '') {
      if (logPath) {
        api.fetch('/filesystem/openLog', {
          path: logPath //'hdfs:///appcom/logs/linkis/log/2021-08-26/nodeexecution/stacyyan/753499.log'
        }, 'get').then((rst) => {
          if (rst) {
            this.historyLogs = rst.log
          }
        }).catch(() => {
        });
      } else {
        this.historyLogs = ''
      }
      // 点击最外部关闭按钮也会调该函数，需要过滤
      if (type !== 'out') {
        this.showContent = true
      }
      // 没打开tab页时，默认展开日志面板
      if(this.panelHeight === 32) {
        this.handleWindowResize()
      }
      this.showLog = show
    },
    closePanel() {
      this.showContent = false;
      this.curTab = {};
      this.currentPanelHeight = 32;
      this.panelHeight = 32;
    },
    showPanel(key, logPath) {
      const item = this.tabs.find(it => it.key === key)
      if (this.curTab.key !== item.key) {
        this.minSize =false
        this.showContent = true
        this.showLog = false
        this.curTab = item
      }
      if (key == 'execHistory' && logPath) {
        this.showLogPanel(logPath)
      }
    },
    eventFromExt(evt) {
      if (evt && evt.callFn && typeof this[evt.callFn] === 'function') {
        this[evt.callFn](...evt.params)
      }
    },
    resizePanel() {
      if (this.$refs.wePanelRef && this.$refs.wePanelItemRef) {
        const height = this.$refs.wePanelItemRef.$el.clientHeight;
        this.currentPanelHeight = height;
        this.panelHeight = height;
      }
    },
    startDrag(e) {
      if (!this.showContent) return;
      
      this.isDragging = true;
      this.startY = e.clientY;
      this.startHeight = this.currentPanelHeight;
      
      // 添加类以禁用文本选择
      document.body.style.userSelect = 'none';
      document.addEventListener('mousemove', this.doDrag);
      document.addEventListener('mouseup', this.stopDrag);
      
      e.preventDefault();
    },
    
    doDrag(e) {
      if (!this.isDragging) return;
      
      // 修改计算方式：向上拖动增加高度
      const deltaY = this.startY - e.clientY;
      let newHeight = this.startHeight + deltaY;
      
      const maxHeight = window.innerHeight * 0.826
      const minHeight = window.innerHeight * 0.11
      // 限制最小和最大高度
      newHeight = Math.max(minHeight, Math.min(maxHeight, newHeight));
      
      this.currentPanelHeight = newHeight;
      this.panelHeight = newHeight;

      // 触发内容区域重新计算
      this.$nextTick(() => {
        this.$refs.bottomTabPage.calcTableHeight(this.currentPanelHeight - 32);
      });
    },
    
    stopDrag() {
      this.isDragging = false;
      // 恢复文本选择
      document.body.style.userSelect = '';
      document.removeEventListener('mousemove', this.doDrag);
      document.removeEventListener('mouseup', this.stopDrag);
    },

    handleMouseOver() {
      this.isHandleHover = true;
    },

    handleMouseOut() {
      this.isHandleHover = false;
    },
  },
  watch: {
    showContent: {
      immediate: true,
      handler(val) {
        this.panelHeight = val ? 294 : 32;
        this.$nextTick(() => {
          this.resizePanel();
        });
      }
    }
  },
  mounted() {
    window.addEventListener('resize', this.handleWindowResize);
    plugin.emitHook('workflow_bottom_panel_mounted', this)
  },
  beforeDestroy() {
    document.removeEventListener('mousemove', this.doDrag);
    document.removeEventListener('mouseup', this.stopDrag);
    window.removeEventListener('resize', this.handleWindowResize);
  }
}
</script>
<style scoped lang="scss">
@import '@dataspherestudio/shared/common/style/variables.scss';
.bottom-tab-container {
  height: 32px;
  position: absolute;
  bottom: 0px;
  width: 100%;
  z-index: 9999;
  
  &.min {
    width: 40px;
    overflow: hidden;
  }
  
  .tab-content {
    position: absolute;
    top: 32px;
    width: 100%;
    @include bg-color($light-base-color, $dark-base-color);
    @include border-color(#dee4ec, $dark-menu-base-color);
    z-index: 10000;
    
    .panel-wrapper {
      height: 100%;
      position: relative;

      ::v-deep {
        .ivu-split-wrapper {
          height: 100%;
          border: none;
          background: $light-base-color;
        }

        .ivu-split-trigger {
          height: 6px;
          background: #f8f8f9;
          border-top: 1px solid #dcdee2;
          border-bottom: 1px solid #dcdee2;
          cursor: ns-resize;
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          z-index: 10001;

          &:hover {
            background: #e8eaec;
          }
        }

        .ivu-split-horizontal {
          height: 100%;
        }
      }
    }
  }

  .panel-content {
    position: relative;
    display: flex;
    flex-direction: column;
    background: $light-base-color;
    z-index: 10000;
    transition: transform 0.3s, height 0.3s;

    .drag-handle {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 6px;
      cursor: ns-resize;
      z-index: 10001;
      background: transparent;
      
      .drag-line {
        width: 100px;
        height: 2px;
        margin: 2px auto;
        background: #dcdee2;
        transition: all 0.2s;
        
        &.hover {
          background: #2d8cf0;
          width: 150px;
        }
      }

      &:hover {
        background: rgba(45, 140, 240, 0.1);
      }
    }

    .content-wrapper {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-height: 0;
      height: 100%;
      overflow: hidden;

      .content-head {
        @include bg-color(#F8F9FC, $dark-background-color-header);
        @include font-color(#333, $dark-text-color);
        border-top: $border-width-base $border-style-base #dcdcdc;
        border-bottom: $border-width-base $border-style-base #dcdcdc;
        @include border-color($border-color-base, $dark-menu-base-color);
        padding: 5px 10px;
        height: 32px;
        flex: 0 0 32px;
      }

      .content-body {
        flex: 1;
        position: relative;
        min-height: 0;
        height: calc(100% - 32px);

        .component {
          @include bg-color(#fff, $dark-base-color);
          height: 100%;
          overflow-y: auto;
          background: $light-base-color;
        }

        .process-console {
          position: absolute;
          left: 0;
          right: 0;
          top: 0;
          bottom: 0;
          background: $light-base-color;
          overflow-y: auto;
        }
      }
    }
  }

  .close-panel {
    float: right;
  }
  .tab-menu {
    display: flex;
    @include bg-color(#F8F9FC, $dark-background-color-header);
    z-index: 10000;
    position: relative;
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
