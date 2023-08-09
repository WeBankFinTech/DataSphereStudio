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
          v-if="item === $t('message.scripts.dbinfs')"
          :db-info="dbData.baseInfo"
          :en-env="isEnEnv"></basic>
        <tableList
          v-if="item === $t('message.scripts.tableifs')"
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
      <div>{{ $t('message.scripts.loadingwait') }}</div>
    </Spin>
  </div>
</template>
<script>
import basic from './components/basic.vue';
import tableList from './components/tableList.vue';
import api from '@dataspherestudio/shared/common/service/api';
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
      tabList: [this.$t('message.scripts.dbinfs'), this.$t('message.scripts.tableifs')],
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
      api.fetch('/dss/datapipe/datasource/getSchemaBaseInfo', params, 'get').then((rst) => {
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
@import '@dataspherestudio/shared/common/style/variables.scss';
.table-detail {
    height: 100%;
    @include bg-color($table-thead-bg, $dark-base-color);
    padding: 16px 10px 10px 16px;
    .table-detail-tabs {
        .ivu-tabs-bar {
            margin-bottom: 10px;
        }
        .ivu-tabs-content {
            height: 100%;
        }
    }
}
</style>

