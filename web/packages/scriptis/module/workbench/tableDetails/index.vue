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
          :meta-data="metadata"
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
import api from '@dataspherestudio/shared/common/service/api';
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
      switch (id) {
        case 1:
          this.getTableFieldsInfo(params)
          break;
        case 2:
          if (!this.statisticInfo) {
            this.getTableStatisticInfo(params)
          }
          break;
        case 0:
          this.getTableComperssInfo(params)
          break;
        default:
          break;
      }
    },
    getTableComperssInfo(data) {
      const params = {
        dbName: data.database,
        isTableOwner: '0',
        orderBy: '1',
        tableName: data.tableName,
        isRealTime: true,
        exactTableName: true,
        pageSize: 1,
        currentPage: 1
      }
      api.fetch('/dss/datapipe/datasource/getTableMetaDataInfo', params, 'get').then((rst) => {
        this.metadata = rst.tableList[0] || {}
      }).catch(() => {
      });
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
      metadata: {},
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
  mounted() {
    if (!this.metadata.tableName) {
      this.getDatalist(0)
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.table-detail {
    height: 100%;
    @include bg-color($table-thead-bg, $dark-base-color);
    padding: 16px 10px 10px 16px;
    .table-detail-tabs {
        height: 100%;
        @include font-color($light-text-color, $dark-text-color);
        .ivu-tabs-bar {
            margin-bottom: 10px;
        }
        .ivu-tabs-content {
            height: 100%;
        }
        .basic-card {
            margin-bottom: 10px;
            height: calc(100% - 52px);
            overflow: hidden;
            .basic-card-item {
                display: inline-flex;
                width: 50%;
                height: 36px;
                padding-left: 10px;
                align-items: center;
                &.comment {
                    height: 42px;
                    align-items: start;
                }
                .basic-card-item-title {
                    display: inline-block;
                    width: 100px;
                    font-weight: bold;
                }
                .basic-card-item-value {
                    display: inline-block;
                    width: calc(100% - 104px);
                    overflow: hidden;
                    &.comment {
                        overflow-y: auto;
                        height: 100%;
                    }
                }
            }
        }
        .field-list {
            height: calc(100% - 52px);
            overflow: hidden;
            width: 100%;
            .field-list-header,
            .field-list-body {
                width: 100%;
                display: flex;
                border: 1px solid #dcdee2;
                @include border-color($border-color-base, $dark-border-color-base);
                height: 46px;
                line-height: 46px;
            }
            .field-list-header {
                @include bg-color(#5e9de0, $dark-menu-base-color);
                @include font-color(#fff, $dark-workspace-title-color);
                font-weight: bold;
                margin-top: 10px;
                border: none;
            }
            .field-list-body {
                border-bottom: none;
                @include bg-color($light-base-color, $dark-base-color);
                &:not(:first-child){
                    border-bottom: 1px solid $border-color-base;
                    @include border-color($border-color-base, $dark-border-color-base);
                }
            }
            .field-list-item {
                width: 200px;
                padding: 0 10px;
                display: inline-block;
                height: 100%;
                text-align: center;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            .field-list-index {
                width: 5%;
                min-width: 50px;
            }
            .field-list-name {
                width: 15%;
            }
            .field-list-type {
                width: 10%;
                min-width: 70px;
            }
            .field-list-alias {
                width: 15%;
            }
            .field-list-primary {
                width: 10%;
                min-width: 70px;
            }
            .field-list-part {
                width: 10%;
                min-width: 70px;
            }
            .field-list-rule {
                width: 10%;
                min-width: 70px;
            }
            .field-list-comment {
                width: 25%;
            }
        }
        .statistics {
            height: calc(100% - 52px);
            overflow: hidden;
            .statistics-card {
                height: 100%;
                .ivu-card-body {
                    height: calc(100% - 52px);
                }
                .statistics-card-item {
                    display: inline-flex;
                    width: 100%;
                    height: 36px;
                    line-height: 36px;
                    padding-left: 10px;
                    align-items: center;
                    .statistics-card-item-title {
                        display: inline-block;
                        width: 100px;
                        font-weight: bold;
                    }
                    .statistics-card-item-value {
                        display: inline-block;
                        width: calc(100% - 104px);
                        overflow: hidden;
                    }
                }
                .statistics-card-label {
                    margin-left: 4px;
                }
                .statistics-card-tree {
                    overflow-y: auto;
                    height: calc(100% - 50px);
                    margin-left: 20px;
                }
                .statistics-page {
                    height: 42px;
                    margin-top: 8px;
                    text-align: center;
                }
            }
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
