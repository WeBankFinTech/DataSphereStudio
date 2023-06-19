<template>
  <div style="height: 100%;  padding: 10px 15px;">
    <Table
      :columns="columns"
      :data="list"
      ref="selectionTable"
    >
      <template
        slot-scope="{ row }"
        slot="yarnmem"
      >
        <span>{{ row.yarn && row.yarn.queueMemory }}</span>
      </template>
      <template
        slot-scope="{ row }"
        slot="yarncpu"
      >
        <span>{{ row.yarn && row.yarn.queueCpu }}</span>
      </template>
      <template
        slot-scope="{ row }"
        slot="queue"
      >
        <span>{{ row.yarn && row.yarn.queueName }}</span>
      </template>
      <template
        slot-scope="{ row }"
        slot="local"
      >
        <span>{{ row.driver.cpu }} cores, {{ row.driver.memory }}, {{ row.driver.instance }} apps</span>
      </template>
      <template
        slot-scope="{ row }"
        slot="killTime"
      >
        <span>{{ row.killTime | formatDate('YYYY-MM-DD HH:mm:ss') }}</span>
      </template>
    </Table>
    <Page
      v-if="pageData.total"
      class="pagebar"
      :total="pageData.total"
      :current="pageData.pageNow"
      :page-size-opts="pageData.sizeOpts"
      show-elevator
      show-sizer
      show-total
      @on-change="handlePageChange"
      @on-page-size-change="handlePageSizeChange"
    />
    <Spin
      v-show="loading"
      size="large"
      fix
    />
  </div>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
import filters from '@dataspherestudio/shared/common/util/filters';

export default {
  components: {
  },
  data() {
    return {
      columns: [
        { title: this.$t('message.enginelist.cols.engineinst'), key: "instance" },
        { title: this.$t('message.enginelist.cols.enginetype'), key: "engineType", width: '90' },
        { title: this.$t('message.enginelist.cols.queue'), slot: "queue", width: '110' },
        { title: this.$t('message.enginelist.cols.local'), slot: "local", width: '120' },
        { title: this.$t('message.enginelist.cols.yarnmb'), slot: "yarnmem" },
        { title: this.$t('message.enginelist.cols.yanrcores'), slot: "yarncpu" },
        { title: this.$t('message.enginelist.cols.freeTime'), key: "unlockDuration", width: '90' },
        { title: this.$t('message.enginelist.cols.creator'), key: "owner" , width: '100' },
        { title: this.$t('message.enginelist.cols.start'), key: "createTime" },
        { title: this.$t('message.enginelist.cols.killer'), key: "killer" , width: '100' },
        { title: this.$t('message.enginelist.cols.requestTime'), key: "killTime",  slot: "killTime" },

      ],
      list: [],
      loading: false,
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10,
        sizeOpts: [10, 20, 30, 50]
      },
    }
  },
  filters,
  mounted() {
    this.getEngineList()
  },
  methods: {
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getEngineList();
    },
    handlePageChange(page) {
      this.pageData.pageNow = page;
      this.getEngineList();
    },
    getEngineList() {
      this.loading = true;
      const params = {
        workspaceId: this.$route.query.workspaceId,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}listEcKillHistory`, params, 'get').then((res) => {
        this.list = res.engineList || []
        this.pageData.total = res.total
        this.loading = false;
      }).catch((err) => {
        console.error(err)
        this.loading = false;
      });
    },

  }
}
</script>
<style lang="scss" scoped>
.pagebar {
  text-align: center;
  margin-top: 20px;
}
</style>
