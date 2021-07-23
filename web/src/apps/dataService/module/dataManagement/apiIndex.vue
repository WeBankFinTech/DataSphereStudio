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
            <a class="operation" @click="copy(row)">
              {{ $t("message.dataService.apiIndex.copy") }}
            </a>
          </div>
        </template>
      </Table>
      <div class="pagebar">
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
export default {
  data() {
    return {
      columns: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: 'API名称',
          key: 'apiName'
        },
        {
          title: '业务流程',
          key: 'groupName'
        },
        {
          title: '数据源名称',
          key: 'datasourceName'
        },
        {
          title: 'API类型',
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
      apiName: ''
    }
  },
  computed: {

  },
  created() {
    this.getApiList();
  },
  methods: {
    getApiList() {
      // 获取api相关数据
      api.fetch('/dss/framework/dbapi/apimanager/list', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
        apiName: this.apiName,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        console.log(res)
        if (res.list) {
          this.apiList = res.list;
          this.pageData.total = res.total;
        }
      }).catch((err) => {
        console.error(err)
      });
    },
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
      this.getApiList();
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.pageNow = page;
      this.getApiList();
    },
    handleSearch() {
      console.log('search', this.apiName)
      this.getApiList();
    },
    test(row) {
      this.$router.push({
        name: 'dataManagement/test',
        query: {...this.$route.query, apiId: row.id || 1},
      });
    },
    online(row) {
      api.fetch(`/dss/framework/dbapi/apimanager/online/${row.id}`, {
      }, 'post').then((res) => {
        console.log(res)
        if (res) {
          // this.getApiList();
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
        console.log(res)
        if (res) {
          // this.getApiList();
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
      api.fetch(`/dss/framework/dbapi/apimanager/callPath/${row.id}`, {
      }, 'get').then((res) => {
        console.log(res.callPathPrefix)
      }).catch((err) => {
        console.error(err)
      });
    },
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

