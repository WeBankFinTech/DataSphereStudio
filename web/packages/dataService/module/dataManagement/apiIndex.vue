<template>
  <Tabs value="publish" class="tab-publish">
    <Tab-pane :label='$t("message.dataService.apiIndex.publishApi")' name="publish">
      <div class="filter-box">
        <div class="filter-input">
          <Input v-model="apiName" icon="ios-search" :placeholder='$t("message.dataService.apiIndex.apiName")' clearable @on-clear="handleSearch" @on-click="handleSearch" @on-enter="handleSearch" />
        </div>
      </div>
      <Table :columns="columns" :data="apiList" size="large">
        <template slot-scope="{ row }" slot="status">
          <Tag v-if="row.status == 1" color="green">{{$t("message.dataService.apiIndex.onlined")}}</Tag>
          <Tag v-if="row.status == 0" color="red">{{$t("message.dataService.apiIndex.offlined")}}</Tag>
        </template>
        <template slot-scope="{ row }" slot="operation">
          <div class="operation-wrap">
            <a class="operation" @click="online(row)" v-if="row.status == 0">
              {{ $t("message.dataService.apiIndex.online") }}
            </a>
            <a class="operation" @click="offline(row)" v-if="row.status == 1">
              {{ $t("message.dataService.apiIndex.offline") }}
            </a>
            <a class="operation" @click="test(row)">
              {{ $t("message.dataService.apiIndex.test") }}
            </a>
            <a class="operation" :data-id="row.id" @click="copy(row)">
              {{ $t("message.dataService.apiIndex.copy") }}
            </a>
          </div>
        </template>
      </Table>
      <Spin v-show="loading" size="large" fix/>
      <div class="pagebar" v-if="pageData.total">
        <Page
          :total="pageData.total"
          :current="pageData.pageNow"
          show-elevator
          show-sizer
          show-total
          @on-change="handlePageChange"
          @on-page-size-change="handlePageSizeChange"
        />
      </div>

      <!--确认下线-->
      <Modal v-model="modalConfirm" width="480" :closable="false">
        <div class="modal-confirm-body">
          <div class="confirm-title">
            <SvgIcon class="icon" icon-class="project-workflow" />
            <span class="text">{{ $t("message.dataService.apiIndex.offlineApiTitle") }}</span>
          </div>
          <div class="confirm-desc">{{ $t("message.dataService.apiIndex.offlineApiDesc") }}</div>
        </div>
        <div slot="footer">
          <Button type="default" @click="offlineCancel">{{$t('message.dataService.cancel')}}</Button>
          <Button type="primary" @click="offlineConfirm">{{$t('message.dataService.ok')}}</Button>
        </div>
      </Modal>

    </Tab-pane>
  </Tabs>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
export default {
  data() {
    return {
      columns: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t("message.dataService.apiIndex.col_apiName"),
          key: 'apiName'
        },
        {
          title: 'API Path',
          key: 'apiPath'
        },
        {
          title: this.$t("message.dataService.apiIndex.col_groupName"),
          key: 'groupName'
        },
        {
          title: this.$t("message.dataService.apiIndex.col_datasourceName"),
          key: 'datasourceName'
        },
        {
          title: this.$t("message.dataService.apiIndex.col_apiType"),
          key: 'apiType'
        },
        {
          title: this.$t("message.dataService.apiIndex.col_status"),
          key: "status",
          slot: "status"
        },
        {
          title: this.$t("message.dataService.operation"),
          key: "operation",
          width: '260',
          slot: "operation"
        }
      ],
      apiList: [],
      loading: true,
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10
      },
      apiName: '',
      modalConfirm: false,
      selectedApi: null
    }
  },
  created() {
    this.getApiList();
  },
  methods: {
    getApiList() {
      this.loading = true;
      api.fetch('/dss/data/api/apimanager/list', {
        workspaceId: this.$route.query.workspaceId,
        apiName: this.apiName.trim(),
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        if (res.list) {
          this.loading = false;
          this.apiList = res.list;
          this.pageData.total = res.total;
        }
      }).catch((err) => {
        this.loading = false;
        console.error(err)
      });
    },
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getApiList();
    },
    handlePageChange(page) {
      this.pageData.pageNow = page;
      this.getApiList();
    },
    handleSearch() {
      this.pageData.pageNow = 1;
      this.getApiList();
    },
    test(row) {
      this.$router.push({
        path: `test/${row.id}`,
        query: this.$route.query
      });
    },
    online(row) {
      api.fetch(`/dss/data/api/apimanager/online/${row.id}`, {}, 'post').then((res) => {
        if (res) {
          this.apiList = this.apiList.map(i => {
            if (i.id == row.id) {
              return res.apiInfo;
            } else {
              return i;
            }
          })
        }
      }).catch((err) => {
        console.error(err)
      });
    },
    offline(row) {
      this.selectedApi = row;
      this.modalConfirm = true;
    },
    offlineCancel() {
      this.selectedApi = null;
      this.modalConfirm = false;
    },
    offlineConfirm() {
      this.modalConfirm = false;
      api.fetch(`/dss/data/api/apimanager/offline/${this.selectedApi.id}`, {
      }, 'post').then((res) => {
        if (res) {
          this.apiList = this.apiList.map(i => {
            if (i.id == this.selectedApi.id) {
              return res.apiInfo;
            } else {
              return i;
            }
          })
        }
      }).catch((err) => {
        console.error(err)
      });
    },
    copy(row) {
      api.fetch(`/dss/data/api/apimanager/callPath/${row.id}`, {}, 'get').then((res) => {
        let inputEl = document.createElement('input');
        inputEl.value = `${res.callPathPrefix}`.replace('{protocol}:', location.protocol).replace('{host}', location.host);
        document.body.appendChild(inputEl);
        inputEl.select(); // 选择对象;
        document.execCommand("Copy"); // 执行浏览器复制命令
        this.$Message.success(this.$t('message.dataService.apiIndex.copied'));
        inputEl.remove();
      }).catch((err) => {
        console.error(err)
      });
    }
  },
}
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.tab-publish {
  min-height: calc(100% - 78px);
  padding: 0 24px;
  @include bg-color(#fff, $dark-base-color);
  .filter-box {
    margin-bottom: 20px;
    overflow: hidden;
    .filter-input {
      width: 200px;
      float: right;
    }
  }
  .operation-wrap {
    margin-left: -10px;
  }
  .operation {
    font-size: 14px;
    line-height: 20px;
    color: #2E92F7;
    padding: 0 10px;
    border-right: 1px solid #D8D8D8;
    &:last-child {
      border-right: 0;
    }
  }
}
.pagebar {
  float: right;
  margin: 15px 0;
  padding: 10px 0;
}
.modal-confirm-body {
  .confirm-title {
    margin-top: 10px;
    color: #FF9F3A;
    .text {
      margin-left: 15px;
      font-size: 16px;
      line-height: 24px;
      @include font-color(#333, $dark-text-color);
    }
    .icon {
      font-size: 26px;
    }
  }
  .confirm-desc {
    margin-top: 15px;
    margin-bottom: 10px;
    margin-left: 42px;
    font-size: 14px;
    line-height: 20px;
    @include font-color(#666, $dark-text-color);
  }
}
</style>

