<template>
  <div class="table-detail">
    <Tabs
      @on-click="getDatalist"
      type="card"
      class="table-detail-tabs"
      :animated="false">
      <TabPane
        :label="item"
        v-for="(item, index) in tabList"
        :key="index"
        style="height:100%;">
        <basic
          v-if="item === $t('message.scripts.tableDetails.BZBSX')"
          :table-info="work.data"
          :en-env="isEnEnv"></basic>
        <field
          v-if="item === $t('message.scripts.tableDetails.BZDXX') && table"
          :table="table"></field>
        <statistics
          v-if="item === $t('message.scripts.tableDetails.BTJXX') && statisticInfo"
          :statisticInfo="statisticInfo"
          :work="work"
          :en-env="isEnEnv"
          :partitionPage="partitionPage"
          @loading="loadingChange"
          @pageSizeChange="pageSizeChange"
          @pageChange="pageChange"
          @partitionSort="partitionSortAction"></statistics>
      </TabPane>
    </Tabs>
    <Spin
      v-if="loading"
      class="current-spin"
      fix>
      <Icon type="ios-loading" size="18" class="spin-icon-load"></Icon>
      <div>Loading</div>
    </Spin>
  </div>
</template>
<script>
import basic from './components/basic.vue';
import field from './components/field.vue';
import statistics from './components/statistics.vue';
import api from '@/common/service/api';
export default {
  components: {
    basic,
    field,
    statistics,
  },
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  computed: {
    isEnEnv() {
      return localStorage.getItem('locale') === 'en';
    }
  },
  methods: {
    // 切换页码
    pageChange(val) {
      this.partitionPage.pageNow = val;
      const params = {
        database: this.work.data.dbName,
        tableName: this.work.data.name,
      };
      this.getTableStatisticInfo(params);
    },
    // 也容量发生变化
    pageSizeChange(val) {
      this.partitionPage.pageSize = val;
      this.partitionPage.pageNow = 1;
      const params = {
        database: this.work.data.dbName,
        tableName: this.work.data.name,
      };
      this.getTableStatisticInfo(params);
    },
    // 分区组件调用单分区查询
    loadingChange(val) {
      this.loading = val;
    },
    getDatalist(id){
      const params = {
        database: this.work.data.dbName,
        tableName: this.work.data.name,
      };
      if(id === 1){
        this.getTableFieldsInfo(params)
      } else if(id === 2){
        if (!this.statisticInfo) {
          this.getTableStatisticInfo(params)
        }
      }
    },
    getTableFieldsInfo(params) {
      this.loading = true;
      api.fetch('/datasource/getTableFieldsInfo', params, 'get').then((rst) => {
        this.table = rst.tableFieldsInfo;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    getTableStatisticInfo(params) {
      params.pageSize = this.partitionPage.pageSize;
      params.pageNow = this.partitionPage.pageNow;
      params.partitionSort = this.partitionSort;
      this.loading = true;
      api.fetch('/datasource/getTableStatisticInfo', params, 'get').then((rst) => {
        this.statisticInfo = rst.tableStatisticInfo;
        this.partitionPage.totalSize = rst.totalSize,
        this.partitionPage.pageSize = rst.pageSize,
        this.partitionPage.pageNow = rst.pageNow
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    partitionSortAction() {
      if(this.partitionSort === 'desc') {
        this.partitionSort = 'asc'
      } else {
        this.partitionSort = 'desc'
      }
      this.pageSizeChange(500);
    }
  },
  data() {
    return {
      tabList: [this.$t('message.scripts.tableDetails.BZBSX'), this.$t('message.scripts.tableDetails.BZDXX'), this.$t('message.scripts.tableDetails.BTJXX')],
      table: null,
      statisticInfo: null,
      loading: false,
      partitionPage: {
        totalSize: 0,
        pageSize: 100,
        pageNow: 1,
        sizeOpts: [100, 250, 500]
      }, // 分区信息的分页
      partitionSort: 'desc' // asc 分区的排序顺序
    };
  },
};
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
.table-detail {
    height: 100%;
    background: $table-thead-bg;
    padding: 16px 10px 10px 16px;
    .table-detail-tabs {
        height: 100%;
        .ivu-tabs-bar {
            margin-bottom: 10px;
        }
        .ivu-tabs-content {
            height: 100%;
        }
    }
}
.current-spin {
  z-index: 10;
}
.spin-icon-load{
  animation: ani-demo-spin 1s linear infinite;
}
.table-detail-tabs {
  /deep/
  .ivu-tabs-bar {
    .ivu-tabs-nav-container {
      font-size: $tag-font-size;
    }
  }
}
@keyframes ani-demo-spin {
  from { transform: rotate(0deg);}
  50%  { transform: rotate(180deg);}
  to   { transform: rotate(360deg);}
}
</style>
