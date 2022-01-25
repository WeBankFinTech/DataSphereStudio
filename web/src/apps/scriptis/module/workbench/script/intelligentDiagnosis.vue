<template>
  <div class="we-intelligent-diagnosis">
    <Table
      :columns="columns"
      :data="diagnosis"></Table>
  </div>
</template>
<script>
export default {
  props: {
    diagnosis: {
      type: Array,
      defalut: () => [],
    },
  },
  data() {
    return {
      columns: [{
        title: '诊断状态',
        key: 'isFault',
        align: 'center',
        render: (h, scope) => {
          let type = 'green';
          let label = '正常';
          if (scope.row.isFault) {
            type = 'orange';
            label = '警告';
          }
          return h('Tag', {
            props: {
              color: type,
            },
          }, label);
        },
      }, {
        title: '诊断码',
        key: 'tuningCode',
        align: 'center',
      }, {
        title: '诊断描述',
        key: 'resultDesc',
        align: 'center',
      }],
    };
  },
};
</script>
<style lang="scss" scoped>
.we-intelligent-diagnosis {
    /deep/.ivu-table-wrapper {
        border: none;
    }
}
</style>

