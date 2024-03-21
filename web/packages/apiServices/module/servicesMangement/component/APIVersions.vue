<template>
  <div>
    <div class="versions-header">
      <!-- <DatePicker :transfer="true" class="search-input" type="datetime" :placeholder="$t('message.apiServices.placeholder.dateAndTime')" @on-change="timeChange"></DatePicker> -->
      <DatePicker
        :transfer="true"
        class="search-input"
        :options="shortcutOpt"
        @on-change="timeChange"
        type="daterange"
        placement="bottom-start"
        :placeholder="$t('message.apiServices.placeholder.dateAndTime')"
        :editable="false"/>
      <Button type="primary" @click="search">{{$t('message.apiServices.query.buttonText')}}</Button>
    </div>
    <div class="versions-content">
      <Table :columns="columns" :data="versionPageData" max-height="600" border="">
        <template slot-scope="{row, index}" slot="action">
          <div class="buttom-box">
            <Button
              type="primary"
              size="small"
              @click="viewVersion(row, index)"
            >{{ $t('message.apiServices.apiTable.operation.view') }}</Button>
            <!-- <Button
              type="primary"
              size="small"
              @click="disableVersion(row, index)"
            >{{$t('message.apiServices.apiTable.operation.disable')}}</Button> -->
          </div>
        </template>
      </Table>
      <div class="version-page-bar">
        <Page
          ref="page"
          :total="versionPage.totalSize"
          :page-size-opts="versionPage.sizeOpts"
          :page-size="versionPage.pageSize"
          :current="versionPage.pageNow"
          class-name="page"
          size="small"
          show-total
          show-sizer
          @on-change="change"
          @on-page-size-change="changeSize" />
      </div>
    </div>
    <Modal v-model="viewVersionShow" :footer-hide="true" :title="viewVersionData.currentVersion" class="viewModal">
      <h3>{{ $t('message.apiServices.view.scriptPath')}}</h3>
      <P class="margin-top">{{ viewVersionData.scriptPath }}</P>
      <h3 class="margin-top">{{ $t('message.apiServices.view.scriptContent') }}</h3>
      <pre class="script-content">{{ viewVersionData.content }}</pre>
    </Modal>
  </div>
</template>
<script>
import moment from "moment";
import api from '@dataspherestudio/shared/common/service/api';
export default {
  data() {
    return {
      versionPage: {
        totalSize: 0,
        sizeOpts: [15, 30, 45],
        pageSize: 15,
        pageNow: 1
      },
      originData: [],
      timeValue: '',
      versionData: [],
      viewVersionShow: false,
      viewVersionData: {
        currentVersion: 'ooo1',
        scriptPath: '',
        content: ''
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
  computed: {
    columns() {
      return [
        {
          title: this.$t('message.apiServices.apiTable.version'),
          key: "version",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.status'),
          key: "status",
          align: "center",
          render: (h, scope) => {
            const colors = ['red','rgb(18, 150, 219)','#0F1222','#F29360']
            const list = [this.$t('message.apiServices.disable'),this.$t('message.apiServices.enable'),this.$t('message.apiServices.unsubmit'),this.$t('message.apiServices.submited')]
            return h(
              "span",
              {
                style: { color: colors[scope.row.status]}
              },
              list[scope.row.status]
            );
          }
        },
        {
          title: this.$t('message.apiServices.apiTable.scriptPath'),
          key: "source",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.creator'),
          key: "creator",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.executeUser'),
          key: "executeUser",
          align: "center"
        },
        {
          title: this.$t('message.apiServices.apiTable.publishDateStr'),
          key: "createTime",
          align: "center",
          render: (h, scope) => {
            return h(
              "span",
              {},
              moment(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss")
            );
          }
        },
        // 本期禁用版本的操作
        {
          title: this.$t("message.apiServices.action"),
          slot: "action",
          align: "center",
        }
      ];
    },
    versionPageData() {
      return this.versionData.filter((item, index) => {
        return (this.versionPage.pageNow - 1) * this.versionPage.pageSize <= index && index < this.versionPage.pageNow * this.versionPage.pageSize;
      })
    }
  },
  created() {
    this.getVersionData();
  },
  methods: {
    // 查看事件
    viewVersion(row) {
      api.fetch('/dss/apiservice/apiContentQuery', {
        versionId: row.id
      }, 'get').then((res) => {
        if (res.result) {
          this.viewVersionData.scriptPath = res.result.scriptPath;
          this.viewVersionData.content = res.result.content;
        }
      })
      this.viewVersionData.currentVersion = row.version;
      this.viewVersionShow = true;
    },
    getVersionData() {
      api.fetch(`/dss/apiservice/apiVersionQuery?serviceId=${this.$route.query.id}`, {}, 'get').then((res) => {
        this.originData = this.versionData = res.result || [];
        this.versionPage.totalSize = this.originData && this.originData.length;
      })
    },
    // 切换分页
    change(val) {
      this.versionPage.pageNow = val;
    },
    // 页容量变化
    changeSize(val) {
      this.versionPage.pageSize = val;
      this.versionPage.pageNow = 1;
    },
    search() {
      let startDate = new Date(this.timeValue[0]).getTime();
      let endDate = new Date(this.timeValue[1]).getTime() + 1000*60*60*24;// 由于选择同一天默认是当天0点0时0分所以结束日期加上当天全天时间
      if (this.timeValue === '') {
        this.versionData = this.originData;
      } else {
        this.versionData = this.originData.filter((item) => {
          return item.createTime >= startDate && item.createTime <= endDate;
        })
      }
    },
    timeChange(val) {
      this.timeValue = val;
    },
    disableVersion(row) {
      this.disableApi(row);
    },
    // 禁用api
    disableApi(row) {
      // 发起请求
      let title = '';
      let content = '';
      let path = '';
      if(Number(!row.status) === 0) {
        title = this.$t('message.apiServices.tip.disableConfirmTitle');
        content = this.$t('message.apiServices.tip.disableConfirm', { name: row.version });
        path = 'apiDisable';
      } else {
        title = this.$t('message.apiServices.tip.enableConfirmTitle');
        content = this.$t('message.apiServices.tip.enableConfirm', { name: row.version });
        path = 'apiEnable';
      }
      this.$Modal.confirm({
        title,
        content,
        onOk: () => {
          api.fetch(`/dss/apiservice/${path}`, {
            id: row.apiId
          }, 'get').then(() => {
            this.getVersionData();
            this.$Message.success("Success")
          }).catch(() => {
            this.$Message.error("Failed")
          });
        }
      });
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .versions-header {
    display: flex;
    justify-content: flex-start;
    align-items: $align-center;
    margin-bottom: 20px;
    padding-left: 20px;
  }
  .version-page-bar {
    text-align: $align-center;
    height: 30px;
    padding-top: 4px;
  }
  .margin-top {
    margin-top: 20px;
  }
  .viewModal {
    ::v-deep.ivu-modal {
      width: 80%!important;
      min-width: 700px;
    }
  }
  .script-content {
    font-family: $font-family;
    overflow: auto;
  }
  .search-input {
    margin-right: 15px;
  }
</style>

