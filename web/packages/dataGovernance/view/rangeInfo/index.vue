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
  padding-right: 24px;
}
::v-deep .ivu-table-wrapper {
  border: none;
}
::v-deep .ivu-table:after {
  width: 0;
}

::v-deep .ivu-table-large th {
  height: 0px;
}

::v-deep .ivu-table-large td {
  height: 0px;
}

::v-deep .ivu-table th {
  height: 0px;
}

::v-deep .ivu-table td {
  height: 0px;
}

::v-deep .ivu-table-cell {
  padding-left: 24px;
  padding-right: 24px;
  padding-top: 8px;
  padding-bottom: 8px;
}
</style>
