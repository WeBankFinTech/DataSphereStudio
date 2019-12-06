
<template>
  <div class="designer-control">
    <div class="designer-control-header" />
    <div class="designer-control-buttons">
      <div class="designer-control-button" :title="t('vue-process.amplification')" @click="zoomout">
        <Icon name="fangda" />
      </div>
      <div class="designer-control-button" :title="t('vue-process.narrow')" @click="zoomin">
        <Icon name="suoxiao" />
      </div>
      <div class="designer-control-button" :title="t('vue-process.narrow')" @click="toggleFullScreen">
        <Icon name="quanping" />
      </div>
      <div class="designer-control-button" :title="t('vue-process.format')" @click="formatDialogVisible = !formatDialogVisible; confirmResetDialogVisible = false">
        <Icon name="geshishua" />
      </div>
      <div v-show="state.isFormatted" class="designer-control-button" :title="t('vue-process.return format')" @click="confirmResetDialogVisible = !confirmResetDialogVisible; formatDialogVisible = false">
        <Icon name="ziyuanxhdpi" />
      </div>
    </div>
    <div v-show="formatDialogVisible" class="designer-control-dialog">
      <div class="line-type">
        <input id="straight" type="radio" name="straight" value="straight" :checked="linkType === 'straight'" @click="linkType = 'straight'">
        <label for="straight">
          {{ t('vue-process.straight') }}
        </label>
        <input id="curve" type="radio" name="curve" value="curve" :checked="linkType === 'curve'" @click="linkType = 'curve'">
        <label for="curve">
          {{ t('vue-process.slash') }}
        </label>
      </div>
      <div class="node-width-container">
        <label for="nodeWidth">
          {{ t('vue-process.Width') }}(40-150)：
        </label>
        <input id="nodeWidth" v-model="nodeWidth" type="number" name="nodeWidth" min="40" max="150" @blur="nodeWidthBlur">
      </div>
      <div class="operations">
        <div class="format" @click="format">
          {{ t('vue-process.formatting') }}
        </div>
        <div class="cancel" @click="formatDialogVisible = false">
          {{ t('vue-process.cancel') }}
        </div>
      </div>
    </div>
    <div v-show="confirmResetDialogVisible" class="designer-control-dialog reset">
      <div class="hint">
        {{ t('vue-process.Whether return format') }}
      </div>
      <div class="operations">
        <div class="format" @click="resetToOriginalData">
          {{ t('vue-process.restore') }}
        </div>
        <div class="cancel" @click="confirmResetDialogVisible = false">
          {{ t('vue-process.cancel') }}
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
      nodeWidth: 40
    }
  },
  mounted() {
    this.designer = findComponentUpward(this, 'Designer');
  },
  methods: {
    zoomout() {
      let index = this.pageSizeList.findIndex((page) => page.value === this.state.baseOptions.pageSize);
      if (index !== -1) {
        if (index + 1 > this.pageSizeList.length - 1) {
          return this.designer.$emit('message', {
            type: 'info',
            msg: `${this.t('vue-process.zoom-max')} 300%`
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
            msg: `${this.t('vue-process.zoom-min')} 50%`
          });
        }
        commit(
          this.$store,
          'UPDATE_PAGESIZE',
          this.pageSizeList[index - 1].value
        );
      }
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
    }
  }
}
</script>
