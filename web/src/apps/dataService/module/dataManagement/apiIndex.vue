<template>
  <Tabs value="publish" class="tab-publish">
    <Tab-pane :label='$t("message.dataService.apiIndex.publishApi")' name="publish">
      <div class="filter-box">
        <div class="filter-input">
          <Input v-model="apiName" icon="ios-clock-outline" :placeholder='$t("message.dataService.apiIndex.apiName")' @on-click="handleSearch" @on-enter="handleSearch" />
        </div>
      </div>
      <Table :columns="columns" :data="apiList" size="large">
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
            <Poptip v-model="row.visibleCopy">
              <a class="operation" :data-id="row.id" @click="copy(row)">
                {{ $t("message.dataService.apiIndex.copy") }}
              </a>
              <div slot="content">
                {{ $t("message.dataService.apiIndex.copyTips") }}
                <Button type="primary" size="small" class="operation-copy">{{ $t("message.dataService.apiIndex.copyBtn") }}</Button>
              </div>
            </Poptip>
          </div>
        </template>
      </Table>
      <div class="pagebar" v-if="pageData.total">
        <Page
          :total="pageData.total"
          :current="pageData.pageNow"
          show-elevator
          show-sizer
          @on-change="handlePageChange"
          @on-page-size-change="handlePageSizeChange"
        />
      </div>
    </Tab-pane>
  </Tabs>
</template>
<script>
import api from "@/common/service/api";
import ClipboardJS from 'clipboard';
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
          title: this.$t("message.dataService.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      apiList: [],
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10
      },
      apiName: '',
    }
  },
  created() {
    this.getApiList();
  },
  methods: {
    getApiList() {
      // 获取api相关数据
      api.fetch('/dss/framework/dbapi/apimanager/list', {
        workspaceId: this.$route.query.workspaceId,
        apiName: this.apiName,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        if (res.list) {
          this.apiList = res.list;
          this.pageData.total = res.total;
        }
      }).catch((err) => {
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
      this.getApiList();
    },
    test(row) {
      this.$router.push({
        path: `test/${row.id}`,
        query: this.$route.query
      });
    },
    online(row) {
      api.fetch(`/dss/framework/dbapi/apimanager/online/${row.id}`, {}, 'post').then((res) => {
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
      api.fetch(`/dss/framework/dbapi/apimanager/offline/${row.id}`, {
      }, 'post').then((res) => {
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
    copy(row) {
      api.fetch(`/dss/framework/dbapi/apimanager/callPath/${row.id}`, {}, 'get').then((res) => {
        this.apiList = this.apiList.map(i => {
          if (i.id == row.id) {
            return {
              ...i,
              visibleCopy: true,
              contentCopy: res.callPathPrefix
            };
          } else {
            return {
              ...i,
              visibleCopy: false,
              contentCopy: ''
            };
          }
        })
        const clipboard = new ClipboardJS('.operation-copy', {
          text: function (trigger) {
            return res.callPathPrefix;
          },
        });
        clipboard.on('success', e => {
          this.$Message.success(this.$t('message.dataService.apiIndex.copied')); 
          this.apiList = this.apiList.map(i => {
            if (i.id == row.id) {
              return {
                ...i,
                visibleCopy: false,
                contentCopy: ''
              };
            } else {
              return i;
            }
          })
          clipboard.destroy();
        });
      }).catch((err) => {
        console.error(err)
      });
    }
  },
}
</script>
<style lang="scss" scoped>
.tab-publish {
  padding: 0 24px;
  background: #fff;
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
  margin-top: 15px;
  padding: 10px 0;
}
</style>

