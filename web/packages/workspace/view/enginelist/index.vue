<template>
  <div style="height: 100%;  padding: 10px 15px;">
    <Form class="table-searchbar" ref="searchBar" :model="searchBar" inline>
      <FormItem prop="status" :label="$t('message.enginelist.status')" :label-width="50">
        <Select v-model="searchBar.status" multiple style="min-width:120px;">
          <Option v-for="(item) in statusList" :label="item" :value="item" :key="item" />
        </Select>
      </FormItem>
      <FormItem prop="createUser" :label="$t('message.enginelist.user')" :label-width="50">
        <Select
          v-model="searchBar.createUser"
          multiple
          filterable
          style="min-width:120px;max-width:355px;"
        >
          <Option
            v-for="(item) in userlist"
            :label="item.name"
            :value="item.name"
            :key="item.name"
          />
        </Select>
      </FormItem>
      <FormItem prop="engineType" :label="$t('message.enginelist.engineType')" :label-width="80">
        <Select v-model="searchBar.engineType" multiple style="min-width:120px;">
          <Option v-for="(item) in engineTypes" :label="item" :value="item" :key="item" />
        </Select>
      </FormItem>
      <FormItem prop="yarnQueue" :label="$t('message.enginelist.queue')" :label-width="50">
        <Input v-model="searchBar.yarnQueue" />
      </FormItem>
      <FormItem class="btn">
        <Button type="primary" @click="doQuery">{{$t('message.enginelist.find')}}</Button>
        <Button type="warning" @click="stop">{{$t('message.enginelist.stop')}}</Button>
      </FormItem>
    </Form>
    <Table :columns="columns" :data="list" ref="selectionTable" @on-sort-change="sortFn">
      <template slot-scope="{ row }" slot="yarnmem">
        <span>{{ row.yarn && row.yarn.queueMemory }}</span>
      </template>
      <template slot-scope="{ row }" slot="yarncpu">
        <span>{{ row.yarn && row.yarn.queueCpu }}</span>
      </template>
      <template slot-scope="{ row }" slot="queue">
        <span>{{ row.yarn && row.yarn.queueName }}</span>
      </template>
      <template slot-scope="{ row }" slot="local">
        <span>{{ row.driver.cpu }} cores, {{ row.driver.memory }}, {{ row.driver.instance }} apps</span>
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
    <Spin v-show="loading" size="large" fix />
  </div>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
import { GetWorkspaceUserManagement } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
export default {
  components: {
  },
  data() {
    return {
      columns: [
        { title: '', type: "selection", width: '50'},
        { title: this.$t('message.enginelist.cols.engineinst'), key: "instance" },
        { title: this.$t('message.enginelist.cols.enginetype'), key: "engineType", width: '90'},
        { title: this.$t('message.enginelist.cols.status'), key: "instanceStatus", width: '80'},
        { title: this.$t('message.enginelist.cols.queue'), slot: "queue", width: '110'},
        { title: this.$t('message.enginelist.cols.local'), slot: "local" },
        { title: this.$t('message.enginelist.cols.yarnmb'), slot: "yarnmem", sortable: 'custom' },
        { title: this.$t('message.enginelist.cols.yanrcores'), slot: "yarncpu", sortable: 'custom'},
        { title: this.$t('message.enginelist.cols.creator'), key: "creator", width: '100'},
        { title: this.$t('message.enginelist.cols.start'), key: "createTime" }
      ],
      list: [],
      loading: false,
      stoping: false,
      showviewlog: false,
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10,
        sizeOpts: [10, 20, 30, 50]
      },
      searchBar: {
        status: '',
        createUser: '',
        engineType: '',
        yarnQueue: ''
      },
      statusList: ['Starting', 'Idle', 'Busy'],
      userlist: [],
      engineTypes: ['hive', 'spark', 'flink']
    }
  },
  mounted() {
    this.getUserList()
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
    stop() {
      // 确认停止当前选中的引擎？
      // 注意：当需要停止的引擎较多时，后台需要时间处理请求，请稍等一段时间后再查询引擎状态。
      // 存在状态为繁忙的引擎被选中，强制停止会导致该引擎上正在运行的任务失败，建议停止前与引擎创建者确认。
      // 注意：当需要停止的引擎较多时，后台需要时间处理请求，请稍等一段时间后再查询引擎状态。
      const selections = this.$refs.selectionTable.getSelection();
      if (selections.length) {
        const hasBusy = selections.some(it => it.instanceStatus === 'Busy')
        let content = ''
        content = hasBusy ? `
            <p style="margin-top: 10px;color:#ed4014">${this.$t('message.enginelist.hasBusy')}</p>
            <p>${this.$t('message.enginelist.stopTip')}</p>
          ` : `
            <p>${this.$t('message.enginelist.stopConfirm')}</p>
            <p>${this.$t('message.enginelist.stopTip')}</p>
          `
        this.$Modal.confirm({
          title: this.$t('message.enginelist.stoptitle'),
          content,
          onOk: () => {
            this.stopAction(selections)
          },
          onCancel: () => { }
        })
      } else {
        this.$Message.warning({ content: this.$t('message.enginelist.selectfirst') });
      }
    },
    doQuery() {
      this.pageData.pageNow = 1
      this.getEngineList()
    },
    getEngineList() {
      this.loading = true;
      const params = {
        workspaceId: this.$route.query.workspaceId,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }
      if (this.searchBar.status) {
        params.status = this.searchBar.status
      }
      if (this.searchBar.createUser) {
        params.createUser = this.searchBar.createUser
      }
      if (this.searchBar.engineType) {
        params.engineType = this.searchBar.engineType
      }
      if (this.searchBar.yarnQueue) {
        params.yarnQueue = this.searchBar.yarnQueue
      }
      if (this.sort) {
        params.orderBy = this.sort.orderBy
        params.sortBy = this.sort.sortBy
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}listEngineConnInstances`, params, 'post').then((res) => {
        this.list = res.engineList || [];
        this.pageData.total = res.total
        this.loading = false;
      }).catch((err) => {
        console.error(err)
        this.loading = false;
      });
    },
    getUserList() {
      if (this.$route.query.workspaceId) {
        GetWorkspaceUserManagement({
          workspaceId: this.$route.query.workspaceId
        }).then((res) => {
          this.userlist = res.workspaceUsers;
        })
      }
    },
    stopAction(selections) {
      const list = selections.map(it => {
        return { engineInstance: it.instance }
      })
      this.stoping = true
      api.fetch('/dss/framework/workspace/killEngineConnInstances', {
        instances: list
      }, 'post').then(() => {
        this.$Message.success(this.$t('message.enginelist.stopReq'))
      }).finally(() => {
        this.stoping = false
      })
    },
    sortFn({ column, order}) {
      let key = ''
      if (column.slot === 'yarncpu') {
        key = 'queueCpu'
      }
      if (column.slot === 'yarnmem') {
        key = 'queueMemory'
      }
      if(order === 'normal') {
        this.sort = null
      } else {
        this.sort = {
          sortBy: key,
          orderBy: order
        }
      }
      this.pageData.pageNow = 1
      this.getEngineList()
    }
  }
}
</script>
<style lang="scss" scoped>
.table-searchbar {
  display: flex;
  .btn {
    flex: 1;
    margin: 0 0 0 20px;
    padding: 0;
    ::v-deep .ivu-form-item-content {
      display: flex;
      justify-content: space-between;
    }
  }
}
.pagebar {
  text-align: center;
  margin-top: 20px
}
</style>
