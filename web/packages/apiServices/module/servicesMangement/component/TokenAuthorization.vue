<template>
  <div>
    <Form :model="formItem" inline>
      <FormItem>
        <DatePicker
          :transfer="true"
          class="datepicker"
          :options="shortcutOpt"
          v-model="formItem.applyTime"
          type="daterange"
          placement="bottom-start"
          :placeholder="$t('message.apiServices.date.placeholder')"
          style="width: 200px"
          :editable="false"/>
      </FormItem>
      <FormItem :label-width="80" :label="$t('message.apiServices.label.applicant')" style="width: 300px">
        <Input v-model="formItem.user" :placeholder="$t('message.apiServices.placeholder.emter')"></Input>
      </FormItem>
      <FormItem :label-width="80" :label="$t('message.apiServices.apiTable.version')" style="width: 300px">
        <Input v-model="formItem.version" :placeholder="$t('message.apiServices.placeholder.emter')"></Input>
      </FormItem>
      <FormItem :label-width="80" :label="$t('message.apiServices.label.status')"  style="width: 300px">
        <Select v-model="formItem.status">
          <Option value="">{{$t('message.apiServices.all')}}</Option>
          <Option value="1">{{$t('message.apiServices.normal')}}</Option>
          <Option value="0">{{$t('message.apiServices.expired')}}</Option>
        </Select>
      </FormItem>
      <FormItem >
        <Button @click="hanlderSearch" type="primary">{{$t('message.apiServices.query.buttonText')}}</Button>
      </FormItem>
    </Form>
    <div class="versions-content">
      <Table :columns="columns" :data="tokenData" max-height="600" border="">
        <template slot-scope="{row}" slot="status">
          <span v-if="row.status == 0" style="color:red;">{{ $t('message.apiServices.expired') }}</span>
          <span style="color:rgb(18, 150, 219);" v-else>{{ $t('message.apiServices.normal') }}</span>
        </template>
        <template slot-scope="{row, index}" slot="action">
          <div class="buttom-box">
            <Button
              type="primary"
              size="small"
              @click="viewVersion(row, index)"
            >{{$t('message.apiServices.open')}}</Button>
          </div>
        </template>
      </Table>
      <div class="version-page-bar">
        <Page
          ref="page"
          :total="page.totalSize"
          :page-size-opts="page.sizeOpts"
          :page-size="page.pageSize"
          :current="page.pageNow"
          class-name="page"
          size="small"
          show-total
          show-sizer
          @on-change="change"
          @on-page-size-change="changeSize" />
      </div>
    </div>
  </div>
</template>
<script>
import moment from "moment";
import api from '@dataspherestudio/shared/common/service/api';
export default {
  data() {
    return {
      tokenData: [],
      formItem: {
        applyTime: "",
        user: "",
        version: "",
        status: ""
      },
      page: {
        totalSize: 0,
        sizeOpts: [15, 30, 45],
        pageSize: 15,
        pageNow: 1
      },
      shortcutOpt: {
        shortcuts: [
          {
            text: this.$t('message.apiServices.shortcuts.week'),
            value() {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              return [start, end];
            },
          },
          {
            text: this.$t('message.apiServices.shortcuts.month'),
            value() {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              return [start, end];
            },
          },
          {
            text: this.$t('message.apiServices.shortcuts.threeMonths'),
            value() {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              return [start, end];
            },
          },
        ],
      },
    }
  },
  created() {
    this.getTokenData()
  },
  computed: {
    columns() {
      return [
        {
          title: this.$t('message.apiServices.apiTable.user'),
          key: "user",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.version'),
          key: "version",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.status'),
          slot: "status",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.applySource'),
          key: "applySource",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.duration'),
          key: "duration",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.reason'),
          key: "reason",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.ip_whitelist'),
          key: "ipWhitelist",
          align: "center",
        },
        {
          title: this.$t('message.apiServices.apiTable.apply_time'),
          key: "applyTime",
          align: "center",
          render: (h, scope) => {
            return h(
              "span",
              {},
              moment(scope.row.applyTime).format("YYYY-MM-DD HH:mm:ss")
            );
          }
        },
        {
          title: this.$t('message.apiServices.apiTable.caller'),
          key: "caller",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.access'),
          key: "access",
          align: "center"
        },
      ];
    }
  },
  methods: {
    // 获取token管理数据
    getTokenData() {
      // todo token列表
      api.fetch('/dss/apiservice/tokenQuery', {
        apiId: this.$route.query.id,
        user: this.formItem.user || undefined,
        status: this.formItem.status || undefined,
        version: this.formItem.version || undefined,
        startDate: this.formItem.applyTime[0] ? this.formItem.applyTime[0].getTime() : undefined,
        endDate: this.formItem.applyTime[1] ? this.formItem.applyTime[1].getTime() : undefined,
        currentPage: this.page.pageNow,
        pageSize: this.page.pageSize,
      }, 'get').then((res) => {
        this.tokenData = res.queryList;
        console.log('this.tokenData: ', this.tokenData);
        this.page.totalSize = res.total;
        // 每次数据查询之后需比较当前页，是否超出可选范围
        const num = Math.ceil(this.page.totalSize / this.page.pageSize);
        if (this.page.pageNow > num) {
          this.page.pageNow = 1;
        }
      })
    },
    // 查询
    hanlderSearch() {
      this.getTokenData();
    },
    // 切换分页
    change(val) {
      this.page.pageNow = val;
      this.getTokenData();
    },
    // 页容量变化
    changeSize(val) {
      this.page.pageSize = val;
      this.page.pageNow = 1;
      this.getTokenData();
    },
  }
}
</script>
<style lang="scss" scoped>
  .version-page-bar {
    text-align: center;
    height: 30px;
    padding-top: 4px;
  }
</style>

