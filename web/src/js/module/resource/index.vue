<template>
  <Modal
    v-model="show"
    :width="780">
    <div slot="header">
      <span>{{ $t('message.resource.header') }}</span>
    </div>
    <slot
      v-if="show"
      ref="resource"/>
    <template slot="footer">
      <Button
        type="primary"
        @click="getExplorerInfo()">{{ $t('message.constants.refresh') }}</Button>
    </template>
  </Modal>
</template>
<script>
export default {
  name: 'ExplorerIndex',
  data() {
    return {
      show: false,
    };
  },
  activated() {
    this.getExplorerInfo();
  },
  methods: {
    // 显示页面
    open() {
      this.show = true;
    },

    // 关闭
    close() {
      this.show = false;
    },

    getExplorerInfo() {
      this.$refs.resource.getExplorerInfo();
    },
  },
};
</script>
