
<template>
  <div class="cyeditor-control">
    <div class="designer-control">
      <div class="designer-control-header" />
      <div class="designer-control-buttons">
        <div class="designer-control-button" :title="$t('message.workflow.vueProcess.modeChange')" @click="showModeChangeClick">
          <Poptip :value="newTipVisible" placement="left-start" :disabled="true">
            <SvgIcon icon-class="mode-change" />
            <template #content>
              <span>这里有新功能了哦！快来试试吧！！！</span>
            </template>
          </Poptip>
        </div>
        <div class="designer-control-button" :title="$t('message.workflow.vueProcess.amplification')" @click="zoomout">
          <Icon name="fangda" />
        </div>
        <div class="designer-control-button" :title="$t('message.workflow.vueProcess.narrow')" @click="zoomin">
          <Icon name="suoxiao" />
        </div>
        <div class="designer-control-button" :title="$t('message.workflow.vueProcess.fullscreen')" @click="toggleFullScreen">
          <Icon name="quanping" />
        </div>
        <div class="designer-control-button" :title="$t('message.workflow.vueProcess.format')" @click="clickToolItem('format')">
          <Icon name="geshishua" />
        </div>
        <div class="designer-control-button" :title="$t('message.workflow.vueProcess.search')" @click="clickToolItem('search')">
          <Icon name="search" />
        </div>
        <div class="designer-control-button" :title="$t('message.workflow.vueProcess.depsearch')" @click="clickNodePath">
          <SvgIcon icon-class="nodepath" />
        </div>
      </div>
      <div v-show="formatDialogVisible" class="designer-control-dialog">
        <div class="line-type">
          <label>{{ $t('message.workflow.vueProcess.formattingLable') }}</label>
          <input id="klay" type="radio" name="klay" value="klay" :checked="linkType === 'klay'" @click="linkType = 'klay'">
          <label for="klay">
            {{ $t('message.workflow.vueProcess.klayLayout') }}
          </label>
          <input id="breadthfirst" type="radio" name="breadthfirst" value="breadthfirst" :checked="linkType === 'breadthfirst'" @click="linkType = 'breadthfirst'">
          <label for="breadthfirst">
            {{ $t('message.workflow.vueProcess.breadthfirstLayout') }}
          </label>
        </div>
        <div class="operations">
          <div class="dialog-btn comfirm-btn" @click="format">
            {{ $t('message.workflow.Confirm') }}
          </div>
          <div class="dialog-btn" @click="formatDialogVisible = false">
            {{ $t('message.workflow.vueProcess.cancel') }}
          </div>
        </div>
      </div>
      <div v-show="modeDialogVisible" class="designer-control-dialog">
        <div class="line-type">
          <label>{{ $t('message.workflow.vueProcess.modeChange') }}</label>
          <input id="vueprocess" type="radio" name="vueprocess" value="vueprocess" :checked="mode === 'vueprocess'" @click="mode = 'vueprocess'">
          <label for="vueprocess">
            {{ $t('message.workflow.vueProcess.modeVueProcess') }}
          </label>
          <input id="cyeditor" type="radio" name="cyeditor" value="cyeditor" :checked="mode === 'cyeditor'" @click="mode = 'cyeditor'">
          <label for="cyeditor">
            {{ $t('message.workflow.vueProcess.modeCyeditor') }}
          </label>
        </div>
        <div class="operations">
          <div class="dialog-btn comfirm-btn" @click="modeChange">
            {{ $t('message.workflow.Confirm') }}
          </div>
          <div class="dialog-btn" @click="modeDialogVisible = false">
            {{ $t('message.workflow.vueProcess.cancel') }}
          </div>
        </div>
      </div>
      <div v-show="searchPanel" class="designer-control-dialog search">
        <div class="search-head"> {{ $t('message.common.Search') }}
          <input id="searchText" v-model="searchText" type="text" name="searchText">
          <span class="close" @click="searchPanel = false">{{ $t('message.common.Close') }}</span>
        </div>
        <div class="node-list-container" @click="selectItem">
          <div class="node-item" v-for="node in nodes" :data-key="node.key" :key="node.key" :title="node.title">{{node.title}}</div>
          <div v-if="nodes.length < 1 " class="no-data">{{ $t('message.common.nodeNotFound') }}</div>
        </div>
      </div>
      <div>
        {{ zoomStr }}
      </div>
    </div>
  </div>

</template>
<script>
import Icon from './icon.vue'
const EVENT = {
  ZOOM_CHANGE: 'zoomChange',
  SCREEN_SIZE_CHANGE: 'screenSizeChange',
  FORMAT: 'format',
  REST_FORMAT: 'restFormat',
  VIEW_NODE: 'viewNode',
  CLICK_NODE_PATH: 'clickNodePath',
  MODE_CHANGE: 'modeChange'
}
export default {
  props: {
    data: {
      type: Object,
      default: () => {
        return {}
      },
    },
    defaultViewMode: {
      type: String,
      default: 'cyeditor'
    },
    newTipVisible: {
      type: Boolean,
      default: false
    },
    zoomSize: {
      type: Number,
      default: 1
    },
    minZoom: {
      type: Number,
      default: 0.5
    },
    maxZoom: {
      type: Number,
      default: 3
    },
    layoutType: {
      type: Object,
      default: () => ({
        name: 'klay',
        fit: false,
      })
    }
  },
  components: {
    Icon
  },
  data() {
    return {
      // 格式化弹框是否显示
      formatDialogVisible: false,
      // 格式化类型
      linkType: 'klay',
      isFormatted: false,
      // 搜索节点
      searchPanel: false,
      searchText: '',
      mode: this.defaultViewMode,
      modeDialogVisible: false
    }
  },
  watch: {
    defaultViewMode(newVal) {
      if (newVal !== this.mode) {
        this.mode = newVal
      }
    },
    layoutType(newVal) {
      if (newVal && newVal.name !== this.linkType) {
        this.linkType = newVal.name
      }
    }
  },
  computed: {
    nodes() {
      if (!this.searchPanel) return []
      let nodes =  this.data.nodes || []
      return nodes.filter(item => item.title.indexOf(this.searchText) > -1)
    },
    zoomStr() {
      return Math.round(this.zoomSize * 100) + '%'
    }
  },
  methods: {
    zoomout() {
      if (this.zoomSize >= this.maxZoom) {
        return this.$Notice.info({
          desc: `${this.$t('message.workflow.vueProcess.zoom-max')} 300%`
        });
      }
      let size = 0.2
      if (this.zoomSize + size > this.maxZoom) {
        size = this.maxZoom - this.zoomSize
      }
      this.$emit(EVENT.ZOOM_CHANGE, { type: 'zoomout', size: Math.abs(size).toFixed(2)})
    },
    zoomin() {
      if (this.zoomSize <= this.minZoom) {
        return this.$Notice.info({
          desc: `${this.$t('message.workflow.vueProcess.zoom-min')} 50%`
        });
      }
      let size = 0.2
      if (this.zoomSize - size < this.minZoom) {
        size = this.zoomSize - this.minZoom
      }
      this.$emit(EVENT.ZOOM_CHANGE, { type: 'zoomin', size: Math.abs(size).toFixed(2)})
    },
    toggleFullScreen() {
      this.$emit(EVENT.SCREEN_SIZE_CHANGE)
    },
    // 切换模式
    modeChange() {
      this.modeDialogVisible = false;
      if (this.mode === this.defaultViewMode) return
      this.$emit(EVENT.MODE_CHANGE, this.mode)
    },
    // 节点查询
    clickNodePath() {
      this.$emit(EVENT.CLICK_NODE_PATH)
    },
    // 进行格式化
    format() {
      const data = {
        klay: {
          name: 'klay',
          fit: false,
          klay: {
            spacing: 50,
          }
        },
        breadthfirst: {
          name: 'breadthfirst',
          directed: true,
          padding: 10,
        }
      }
      this.$emit(EVENT.FORMAT, data[this.linkType])
      this.formatDialogVisible = false
    },
    showModeChangeClick() {
      this.modeDialogVisible = true
      this.formatDialogVisible = false
      this.searchPanel = false
    },
    clickToolItem(item) {
      this.modeDialogVisible = false
      if (item === 'search') {
        this.searchText = ''
        this.formatDialogVisible = false
        this.searchPanel = !this.searchPanel
      } else if(item === 'format'){
        this.searchPanel = false
        if(this.isFormatted) {
          this.formatDialogVisible = false
        } else {
          this.formatDialogVisible = !this.formatDialogVisible;
        }
      }
    },
    selectItem(e) {
      const key = e && e.target.dataset.key
      if (key) {
        this.$emit(EVENT.VIEW_NODE, key)
      }
      this.searchPanel = false
    }
  }
}
</script>
<style lang="scss" scoped>
 @import "@dataspherestudio/shared/common/style/variables.scss";
.cyeditor-control {
  .designer-control {
    position: absolute;
    top: 0;
    right: 14px;
    width: 36px;
    z-index: 500;

    .designer-control-header {
      @include bg-color(#5c5c5c, #474E5D);
      height: 8px;
      border-left: 1px solid #5c5c5c;
      border-right: 1px solid #5c5c5c;
      border-top: 1px solid #646464;
      border-bottom: 1px solid #000;
      @include border-color(#5c5c5c, #474E5D);
    }

    .designer-control-buttons {
      @include bg-color(#fff, #2A303C);
      padding: 10px 0px 10px;
      border-left: 1px solid #d7dde4;
      border-right: 1px solid #d7dde4;
      border-bottom: 1px solid #d7dde4;
      @include border-color(#d7dde4, #2A303C);
      overflow: visible !important;
      .ivu-poptip-inner {
        @include bg-color(#fff, #2A303C);
      }
    }

    .designer-control-button {
      position: relative;
      margin: 5px 5px 5px 5px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      font-size: 18px;
      cursor: pointer;
    }

    .designer-control-dialog {
      position: absolute;
      top: 50px;
      right: 40px;
      min-width: 220px;
      width: max-content;
      height: auto;
      border-radius: 5px;
      @include bg-color(#fff, $dark-base-color);
      @include border-color(#eee, #37383a);
      border: 1px solid;
      @include guide-box-shadow(#eee, #37383a);

      .line-type {
        width: 100%;
        height: 40px;
        display: flex;
        justify-content: center;

        input,
        label {
          margin: 0 3px;
          align-self: center;
          cursor: pointer;
        }
      }
      .node-width-container {
        width: 100%;
        height: 40px;
        text-align: center;
      }
      .operations {
        width: 100%;
        height: 40px;
        text-align: center;

        .dialog-btn {
          margin-left: 5px;
          display: inline-block;
          width: 50px;
          height: 24px;
          line-height: 24px;
          border: 1px solid #d7dde4;
          cursor: pointer;
          border-radius: 4px;
          &:hover {
            border: 1px solid #37383a;
          }
        }
        .comfirm-btn {
          background-color: $primary-color;
          color: #fff;
        }
      }
    }
  }
 }
</style>
