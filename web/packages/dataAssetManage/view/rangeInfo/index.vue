<template>
  <div class="range-info-wrap">
    <Table :columns="columns" :data="rangeData"></Table>
  </div>
</template>

<script>
export default {
  name: "rangeInfo",
  props: {
    rangeInfo: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      columns: [
        { title: "分区名", key: "partName" },
        { title: "记录数", key: "reordCnt" },
        { title: "存储量", key: "store" },
        { title: "创建时间", key: "createTime" },
        { title: "最后更新时间", key: "lastAccessTime" }
      ],
      rangeData: this.rangeInfo
    };
  },
  watch: {
    rangeInfo: {
      handler(newVal) {
        this.rangeData = newVal;
      },
      deep: true
    }
  }
};
</script>

<style lang="scss" scoped>
.range-info-wrap {
  padding-left: 24px;
  padding-top: 24px;
}
</style>
