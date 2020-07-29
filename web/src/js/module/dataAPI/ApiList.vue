<template>
  <div class="api-table">
    <Table :columns="columns" :data="tableData" style="margin-bottom: 10px;">
      <template slot-scope="{ row }" slot="name">
        <span>{{ row.name }}</span>
      </template>

      <template slot-scope="{ row }" slot="frequency">
        <span>{{ dateFormat(row.frequency) }}</span>
      </template>

      <template slot-scope="{ row }" slot="createTime">
        <span>{{ dateFormat(row.createTime) }}</span>
      </template>

      <template slot-scope="{ row }" slot="action">
        <Button type="primary" size="small" style="margin-right: 5px" @click="start(row)">执行</Button>
        <Button type="error" size="small" @click="stop(row)">停止</Button>
      </template>
    </Table>
  </div>
</template>

<script>
import moment from 'moment';
import api from '@/js/service/api';

export default {
  props: {
    data: {
      type: Array,
      default: () => []
    },
    columns: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      tableData: []
    }
  },
  watch: {
    data(val) {
      // 保存取到的所有数据
      this.tableData = val;
    }
  },
  methods: {
    dateFormat(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    },
    start(row) {
      this.$emit('start', row);
    },
    stop(row) {
      this.$emit('stop', row);
    }
  }
}
</script>

<style lang="scss" scoped>
.api-table {
  .desc {
    width: 100%;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
