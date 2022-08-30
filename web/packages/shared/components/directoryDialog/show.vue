<template>
  <Modal
    v-model="show"
    :fullscreen="isFullScreen"
    class="we-import-dialog"
    width="600">
    <div slot="header">
      <span>{{ title }}</span>
      <span @click="fullScreenModal" class="full-btn">
        <Icon :type="isFullScreen?'md-contract':'md-expand'" />
        {{isFullScreen?this.$t('message.common.cancelFullScreen'):this.$t('message.common.fullScreen')}}
      </span>
    </div>
    <div class="we-import-dialog-content">
      <span>{{$t('message.common.path')}}</span>
      <directory-dialog
        :tree="tree"
        :load-data-fn="loadDataFn"
        :filter-node="filterNode"
        :path="path"
        :height="directoryHeight"
        @set-node="setNode"/>
    </div>
    <div slot="footer">
      <Button
        type="primary"
        @click="submit">{{$t('message.common.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import directoryDialog from './index.vue';
export default {
  name: 'ShowDialog',
  components: {
    directoryDialog,
  },
  props: {
    filterNode: Function,
    loadDataFn: Function,
    path: {
      type: String,
      require: true,
    },
    fsType: {
      type: String,
      default: 'file',
    },
    tree: {
      type: Array,
      require: true,
      default: () => [],
    },
    title: String,
  },
  data() {
    return {
      show: false,
      node: null,
      isFullScreen: false,
      directoryHeight: 280,
    };
  },
  methods: {
    open() {
      this.show = true;
      this.isFullScreen = false;
      this.directoryHeight = 280;
    },
    setNode(node) {
      this.node = node;
      this.$emit('set-node', node, this.fsType);
    },
    submit() {
      this.show = false;
      this.$emit('import', this.node);
    },
    fullScreenModal() {
      this.isFullScreen = !this.isFullScreen
      if (this.isFullScreen ) {
        this.directoryHeight = window.innerHeight - 200
      } else {
        this.directoryHeight = 280
      }
    },
  },
};
</script>
<style scoped>
.full-btn {
  float: right;
  margin-right: 30px;
  padding-top: 5px;
  cursor: pointer;
  color: #2d8cf0
}
</style>
