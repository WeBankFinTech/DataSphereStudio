
<template>
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
      <div v-show="!state.mapMode" class="designer-control-button" :title="$t('message.workflow.vueProcess.fullscreen')" @click="toggleFullScreen">
        <Icon name="quanping" />
      </div>
      <div v-show="!state.mapMode" class="designer-control-button" :title="$t('message.workflow.vueProcess.format')" @click="clickToolItem('format')">
        <Icon name="geshishua" />
      </div>
      <div v-show="state.isFormatted  && !state.mapMode" class="designer-control-button" :title="$t('message.workflow.vueProcess.returnFormat')" @click="clickToolItem('format')">
        <Icon name="ziyuanxhdpi" />
      </div>
      <div v-show="!state.mapMode" class="designer-control-button" :title="$t('message.workflow.vueProcess.search')" @click="clickToolItem('search')">
        <Icon name="search" />
      </div>
      <div class="designer-control-button" :title="$t('message.workflow.vueProcess.depsearch')" @click="clickNodePath">
        <SvgIcon icon-class="nodepath" />
      </div>
    </div>
    <div v-show="formatDialogVisible" class="designer-control-dialog">
      <div class="line-type">
        <input id="straight" type="radio" name="straight" value="straight" :checked="linkType === 'straight'" @click="linkType = 'straight'">
        <label for="straight">
          {{ $t('message.workflow.vueProcess.straight') }}
        </label>
        <input id="curve" type="radio" name="curve" value="curve" :checked="linkType === 'curve'" @click="linkType = 'curve'">
        <label for="curve">
          {{ $t('message.workflow.vueProcess.slash') }}
        </label>
      </div>
      <div class="node-width-container">
        <label for="nodeWidth">
          {{ $t('message.workflow.vueProcess.width') }}(40-150)：
        </label>
        <input id="nodeWidth" v-model="nodeWidth" type="number" name="nodeWidth" min="40" max="150" @blur="nodeWidthBlur">
      </div>
      <div class="operations">
        <div class="format" @click="format">
          {{ $t('message.workflow.vueProcess.formatting') }}
        </div>
        <div class="cancel" @click="formatDialogVisible = false">
          {{ $t('message.workflow.vueProcess.cancel') }}
        </div>
      </div>
    </div>
    <div v-show="confirmResetDialogVisible" class="designer-control-dialog reset">
      <div class="hint">
        {{ $t('message.workflow.vueProcess.whetherReturnFormat') }}
      </div>
      <div class="operations">
        <div class="format" @click="resetToOriginalData">
          {{ $t('message.workflow.vueProcess.restore') }}
        </div>
        <div class="cancel" @click="confirmResetDialogVisible = false">
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
    <div>
      {{ state.baseOptions.pageSize * 100 + "%" }}
    </div>
  </div>
</template>
<script>
import Icon from './icon.vue'
import { commit, mixin } from './store';
import { findComponentUpward } from './util.js'
export default {
  components: {
    Icon
  },
  mixins: [mixin],
  props: {
    newTipVisible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      pageSizeList: [
        { text: '50%', value: 0.5 },
        { text: '75%', value: 0.75 },
        { text: '100%', value: 1 },
        { text: '125%', value: 1.25 },
        { text: '150%', value: 1.5 },
        { text: '175%', value: 1.75 },
        { text: '200%', value: 2 },
        { text: '300%', value: 3 }
      ],
      // 格式化弹框是否显示
      formatDialogVisible: false,
      // 恢复到格式化之前的状态 的提示弹框
      confirmResetDialogVisible: false,
      // 格式化后的线的类型
      linkType: 'curve',
      // 节点宽度
      nodeWidth: 40,
      // 搜索节点
      searchPanel: false,
      searchText: '',
      mode: 'vueprocess',
      modeDialogVisible: false
    }
  },
  mounted() {
    this.designer = findComponentUpward(this, 'Designer');
  },
  computed: {
    nodes() {
      if (!this.searchPanel) return []
      let {nodes} =  this.$parent.getResult('nodes')
      return nodes.filter(item => item.title.indexOf(this.searchText) > -1)
    }
  },
  methods: {
    // 切换模式
    modeChange() {
      this.modeDialogVisible = false;
      if (this.mode === 'vueprocess') return
      const isChangeCyeditorMode = localStorage.getItem('isChangeCyeditorMode')
      if (isChangeCyeditorMode) {
        this.$emit('modeChange', this.mode)
        return
      }
      this.$Modal.confirm({
        title: '模式切换提示',
        content: '切换模式后，原模式下的流程结构会被初始化，节点位置会发生变化，请确认是否切换',
        okText: '确认切换',
        cancelText: '暂不切换',
        onOk: () => {
          localStorage.setItem('isChangeCyeditorMode', true)
          this.$emit('modeChange', this.mode)
        },
      });
    },
    zoomout() {
      let index = this.pageSizeList.findIndex((page) => page.value === this.state.baseOptions.pageSize);
      if (index !== -1) {
        if (index + 1 > this.pageSizeList.length - 1) {
          return this.designer.$emit('message', {
            type: 'info',
            msg: `${this.$t('message.workflow.vueProcess.zoom-max')} 300%`
          });
        }
        commit(
          this.$store,
          'UPDATE_PAGESIZE',
          this.pageSizeList[index + 1].value
        );
      }
    },
    zoomin() {
      let index = this.pageSizeList.findIndex((page) => page.value === this.state.baseOptions.pageSize);
      if (index !== -1) {
        if (index - 1 < 0) {
          return this.designer.$emit('message', {
            type: 'info',
            msg: `${this.$t('message.workflow.vueProcess.zoom-min')} 50%`
          });
        }
        commit(
          this.$store,
          'UPDATE_PAGESIZE',
          this.pageSizeList[index - 1].value
        );
      }
    },
    clickNodePath() {
      this.designer.$emit('search-node-path')
    },
    toggleFullScreen() {
      commit(this.$store, 'UPDATE_FULL_SCREEN', !this.state.fullScreen);
    },
    // nodeWidth的input失去焦点的时候
    nodeWidthBlur(e) {
      const max = 150
      const min = 40
      const num = parseInt(e.target.value)
      if (isNaN(num)) {
        // 虽然input的type为num
        this.nodeWidth = min
      } else {
        if (num < min) this.nodeWidth = min
        if (num > max) this.nodeWidth = max
      }
    },
    // 进行格式化
    format() {
      const data = {
        linkType: this.linkType,
        nodeWidth: parseInt(this.nodeWidth)
      }
      // 通知designer.vue再传到nodeView.vue
      this.$emit('format', data)
      this.formatDialogVisible = false
    },
    // 恢复到格式化前的状态
    resetToOriginalData() {
      // 通知designer.vue再传到nodeView.vue
      this.$emit('resetToOriginalData')
      this.confirmResetDialogVisible = false
    },
    showModeChangeClick() {
      this.modeDialogVisible = true
      this.formatDialogVisible = false
      this.confirmResetDialogVisible = false
      this.searchPanel = false
    },
    clickToolItem(item) {
      this.modeDialogVisible = false
      if (item === 'search') {
        this.searchText = ''
        this.formatDialogVisible = false
        this.confirmResetDialogVisible = false
        this.searchPanel = !this.searchPanel
      } else if(item === 'format'){
        this.searchPanel = false
        if(this.state.isFormatted) {
          this.confirmResetDialogVisible = !this.confirmResetDialogVisible; this.formatDialogVisible = false
        } else {
          this.formatDialogVisible = !this.formatDialogVisible; this.confirmResetDialogVisible = false
        }
      }
    },
    selectItem(e) {
      const key = e && e.target.dataset.key
      if (key) {
        this.state.nodes.forEach( item => {
          item.selected = false
          if (item.key === key) {
            item.selected = true
            this.$emit('viewNode', item)
          }
        })
      }
      this.searchPanel = false
    }
  }
}
</script>
