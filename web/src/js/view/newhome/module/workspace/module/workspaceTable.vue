<template>
  <div class="workspace-table">
    <Table :columns="columns" :data="tableData" style="margin-bottom: 10px;">
      <template slot-scope="{ row }" slot="name">
        <span>{{ row.name }}</span>
      </template>

      <template slot-scope="{ row }" slot="createTime">
        <span>{{ dateFormat(row.createTime) }}</span>
      </template>

      <template slot-scope="{ row }" slot="label" >
        <Tag color="blue" v-for="(label, index) in row.label.split(',')" :key="index">{{label}}</Tag>
      </template>

      <template slot-scope="{ row }" slot="description">
        <span>{{ row.description }}</span>
      </template>
    </Table>
  </div>
</template>

<script>
import moment from 'moment';

export default {
  props: {
    data: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      tableData: [],
      columns: [
        {
          title: this.$t('message.workspace.workName'),
          slot: 'name'
        },
        {
          title: this.$t('message.workspace.createTime'),
          slot: 'createTime',
          sortable: true
        },
        {
          title: this.$t('message.workspace.label'),
          slot: 'label'
        },
        {
          title: this.$t('message.workspace.description'),
          slot: 'description',
          ellipsis: true
        }
      ]
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
  }
}
</script>
