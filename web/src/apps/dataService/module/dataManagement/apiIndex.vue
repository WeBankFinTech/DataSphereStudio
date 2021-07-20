<template>
  <Tabs value="publish" class="tab-publish">
    <Tab-pane label="发布的API" name="publish">
      <div class="filter-box">
        <div class="filter-input">
          <Input v-model="apiName" icon="ios-clock-outline" placeholder="API名称" @on-click="handleSearch" @on-enter="handleSearch" />
        </div>
      </div>
      <Table :columns="columns" :data="data" size="large">
        <template slot-scope="{ row, index }" slot="operation">
          <div class="operation-wrap">
            <a class="operation" @click="edit(row, index)">
              {{ $t("message.dataService.online") }}
            </a>
            <a class="operation" @click="edit(row, index)">
              {{ $t("message.dataService.offline") }}
            </a>
            <a class="operation">
              {{ $t("message.dataService.test") }}
            </a>
            <a class="operation">
              {{ $t("message.dataService.copy") }}
            </a>
          </div>
        </template>
      </Table>
      <div class="pagebar">
        <Page
          :total="pageData.total"
          :current="pageData.pageNum"
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
// import api from "@/common/service/api";
export default {
  data() {
    return {
      columns: [
        {
          title: '姓名',
          key: 'name'
        },
        {
          title: '年龄',
          key: 'age'
        },
        {
          title: '地址',
          key: 'address'
        },
        {
          title: this.$t("message.permissions.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      data: [
        {
          name: '李小红',
          age: 30,
          address: '上海市浦东新区世纪大道'
        },
        {
          name: '周小伟',
          age: 26,
          address: '深圳市南山区深南大道'
        }
      ],
      pageData: {
        total: 20,
        pageNum: 1,
        pageSize: 10
      },
      apiName: ''
    }
  },
  computed: {

  },
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // api的基础信息
    //     this.apiData = rst.result;
    //     this.formValidate.approvalNo = this.apiData.approvalVo.approvalNo;
    //     // 更改网页title
    //     document.title = rst.result.aliasName || rst.result.name;
    //     // 加工api信息tab的数据
    //     this.apiInfoData = [
    //       { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
    //       { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
    //       { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
    //     ]
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  methods: {
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.pageNum = page;
    },
    handleSearch() {
      console.log('search', this.apiName)
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

