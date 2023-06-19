<template>
  <div class="download-audit">
    <!-- 查询操作 -->
    <Form :model="searchBar" :rules="ruleInline" inline>
      <FormItem prop="userName" :label="$t('message.scripts.Username')">
        <Input :maxlength="50" v-model="searchBar.userName"/>
      </FormItem>
      <FormItem prop="time" :label="$t('message.scripts.downloadtime')">
        <DatePicker type="datetimerange" :transfer="true" :editable="false" placement="bottom-start" v-model="searchBar.time" style="width: 150px"></DatePicker>
      </FormItem>
      <FormItem>
        <Button type="default" @click="reset" style="margin-right: 10px;" >{{ $t('message.scripts.reset') }}</Button>
        <Button type="primary" @click="search(1)">
          {{ $t('message.scripts.search') }}
        </Button>
      </FormItem>
    </Form>

    <Table class="table-content" border :columns="columns" :data="tableData">
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
import api from '@dataspherestudio/shared/common/service/api';
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
        pageSize: 10,
        current: 1
      },
      columns: [
        {
          title: this.$t('message.scripts.Date'),
          key: 'createTime',
          minWidth: 150,
          align: "center",
          sortable: true
        },
        {
          title: this.$t('message.scripts.user'),
          key: 'creator',
          minWidth: 150,
          align: "center"
        },
        {
          title: this.$t('message.scripts.downloadpath'),
          key: 'path',
          minWidth: 200,
          align: "center"
        },
        {
          title: 'Sql',
          key: 'sql',
          minWidth: 200,
          align: "center",
          render: (h, params) => {
            let texts = params.row.sql
            if (texts != null) {
              if (texts.length > 80) {
                texts = texts.substring(0, 80) + '...' // 进行数字截取或slice截取超过长度时以...表示
              }
            }
            return h('Tooltip', {
              props: {
                placement: 'left',
                transfer: true,
                maxWidth: 800
              },
            }, [texts,
              h('span', {
                slot: 'content',
                style: {
                  whiteSpace: 'normal',
                  wordBreak: 'break-all'
                }
              }, params.row.sql)
            ])
          }
        }
      ],
    }
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
      api.fetch('/dss/scriptis/audit/download/query', params,'get').then((res) => {
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

