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
          v-if="item === '库基本信息'"
          :db-info="dbData.baseInfo"
          :en-env="isEnEnv"></basic>
        <tableList
          v-if="item === '库表信息'"
          ref="tableList"
          :db-name="work.data.name"
          @table-listChange="tableListChange"
          @loading-change="loadingChange"
        ></tableList>
      </TabPane>
    </Tabs>
    <Spin
      v-if="loading"
      class="current-spin"
      fix>
      <Icon type="ios-loading" size="18" class="spin-icon-load"></Icon>
      <div>数据加载中，请耐心等候</div>
    </Spin>
  </div>
</template>
<script>
import basic from './components/basic.vue';
import tableList from './components/tableList.vue';
import api from '@/common/service/api';
export default {
  props: {
    work: {
      type: Object,
      required: true,
    }
  },
  components: {
    basic,
    tableList
  },
  computed: {
    isEnEnv() {
      return localStorage.getItem('locale') === 'en';
    }
  },
  data() {
    return {
      tabList: ['库基本信息', '库表信息'],
      dbData: {
        baseInfo: {},
        tablesList: []
      },
      loading: false
    }
  },
  created() {
    this.getDatalist(0)
  },
  methods: {
    getDatalist(id) {
      console.log(id, this.work)
      // 没有数据才请求
      if (id === 0 && Object.keys(this.dbData.baseInfo).length <= 0) {
        const params = {
          dbName: this.work.data.name
        }
        this.getDbBaseInfo(params);
      } else if (id === 1 && this.dbData.tablesList.length <= 0) {// 没有数据才请求
        if (this.$refs.tableList) {
          this.$refs.tableList[0].getDbTables()
        }
      }
    },
    // 获取库基本信息
    getDbBaseInfo(params = {}) {
      this.loading = true;
      api.fetch('/datasource/getSchemaBaseInfo', params, 'get').then((rst) => {
        console.log(rst, 'rst')
        this.dbData.baseInfo = rst.schemaInfo || {};
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    tableListChange(val) {
      this.dbData.tablesList = val;
    },
    loadingChange(val) {
      this.loading = val;
    }
  }
}
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
</style>

