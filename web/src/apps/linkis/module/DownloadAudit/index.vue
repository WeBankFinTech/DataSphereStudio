<template>
  <div>
    <!-- 查询操作 -->
    <Form :model="searchBar" :rules="ruleInline" inline>
      <FormItem prop="userName" :label="$t('message.linkis.downloadAudit.userName.label')">
        <Input :maxlength="50" v-model="searchBar.userName" :placeholder="$t('message.linkis.searchUserName')"/>
      </FormItem>
      <FormItem prop="time" :label="$t('message.linkis.downloadAudit.time.label')">
        <DatePicker type="datetimerange" :transfer="true" :editable="false" placement="bottom-start" v-model="searchBar.time" placeholder="Select date and time" style="width: 150px"></DatePicker>
      </FormItem>
      <FormItem>
        <Button type="default" @click="reset" style="margin-right: 10px;" >{{ $t('message.linkis.reset') }}</Button>
        <Button type="primary" @click="search(1)">
          {{ $t('message.linkis.search') }}
        </Button>
      </FormItem>
    </Form>

    <Table class="table-content" border :columns="columns" :data="tableData">
        <template slot-scope="{ row }" slot="sql">
            <strong>{{ JSON.parse(row.sql) }}</strong>
        </template>
    </Table>
    <div class="page-bar">
      <Page
        ref="page"
        :total="page.totalSize"
        :page-size="page.pageSize"
        :current="page.current"
        class-name="page"
        size="small"
        show-total
        show-elevator
        @on-change="change"/>
    </div>
  </div>
</template>
<script>
import api from '@/common/service/api';
import moment from "moment";
export default {
  data() {
    return {
      ruleInline: {},
      searchBar: {
        userName: "",
        time: [],
      },
      tableData: [],
      page: {
        totalSize: 0,
        // sizeOpts: [15, 30, 45],
        pageSize: 10,
        current: 1
      },
      columns: [
        {
          title: this.$t('message.linkis.tableColumns.date'),
          key: 'createTime',
          minWidth: 100,
          align: "center"
        },
        {
          title: this.$t('message.linkis.tableColumns.user'),
          key: 'creator',
          minWidth: 100,
          align: "center"
        },
        {
          title: this.$t('message.linkis.tableColumns.scriptFilePath'),
          key: 'path',
          minWidth: 100,
          align: "center"
        },
        {
          title: this.$t('message.linkis.tableColumns.sqlContent'),
          key: 'sql',
          slot: 'sql',
          minWidth: 300,
          align: "center"
        }
      ],
    }
  },
  computed: {
  },
  created() {
    this.search()
  },
  methods: {
    search(current = 1){
      let {userName, time: [startTime, endTime]} = this.searchBar
      let params = {
        userName,
        startTime: startTime ? this.timeFormat(startTime) : '',
        endTime: endTime ? this.timeFormat(endTime) : '',
        pn: current,
      }
      api.fetch('/dss/framework/audit/script/download/query', params,'get').then((res) => {
        this.tableData = res.data.list;
        this.page.current = res.data.pageNum == 0 ? 1 : res.data.pageNum;
        this.page.totalSize = res.data.total;
      })
    },
    reset() {
      this.searchBar = {
        userName: "",
        time: [],
      };
      this.search()
    },
    // 时间格式转换
    timeFormat(time) {
      return moment(new Date(time)).format('YYYY-MM-DD HH:mm:ss')
    },
    // 切换分页,传入改变后的页码
    change(val) {
      this.search(val)
    }
  }
}
</script>

<style src="./index.scss" lang="scss" scoped></style>

