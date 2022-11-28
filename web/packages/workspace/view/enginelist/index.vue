<template>
  <div style="height: 100%;  padding: 10px 15px;">
    <Form class="table-searchbar" ref="searchBar" :model="searchBar" inline>
      <FormItem prop="status" :label="$t('message.enginelist.status')" :label-width="50">
        <Select v-model="searchBar.status" multiple style="min-width:120px;">
          <Option
            v-for="(item) in statusList"
            :label="item"
            :value="item"
            :key="item"
          />
        </Select>
      </FormItem>
      <FormItem prop="createUser" :label="$t('message.enginelist.user')" :label-width="50">
        <Select v-model="searchBar.createUser" multiple filterable style="min-width:120px;max-width:355px;">
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
          <Option
            v-for="(item) in engineTypes"
            :label="item"
            :value="item"
            :key="item"
          />
        </Select>
      </FormItem>
      <FormItem prop="yarnQueue" :label="$t('message.enginelist.queue')" :label-width="50">
        <Input v-model="searchBar.yarnQueue" />
      </FormItem>
      <FormItem class="btn">
        <Button
          type="primary"
          @click="getEngineList"
        >{{$t('message.enginelist.find')}}</Button>
        <Button
          type="warning"
          @click="stop"
        >{{$t('message.enginelist.stop')}}</Button>
      </FormItem>
    </Form>
    <Table :columns="columns" :data="list" ref="selectionTable">
      <template slot-scope="{ row }" slot="action">
        <Button
          size="small"
          @click="viewLog(row.name)"
          style="margin-right: 5px"
        >
          {{$t('message.enginelist.viewlog')}}
        </Button>
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
    <Spin v-show="loading" size="large" fix/>
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
        { title: '', type: "selection" },
        { title: this.$t('message.enginelist.cols.engineinst'), key: "id" },
        { title: this.$t('message.enginelist.cols.taskid'), key: "id" },
        { title: this.$t('message.enginelist.cols.enginetype'), key: "id" },
        { title: this.$t('message.enginelist.cols.status'), key: "id" },
        { title: this.$t('message.enginelist.cols.queue'), key: "id" },
        { title: this.$t('message.enginelist.cols.local'), key: "id" },
        { title: this.$t('message.enginelist.cols.yarnmb'), key: "id" },
        { title: this.$t('message.enginelist.cols.yanrcores'), key: "id" },
        { title: this.$t('message.enginelist.cols.creator'), key: "id" },
        { title: this.$t('message.enginelist.cols.start'), key: "id" },
        { title: this.$t('message.enginelist.cols.action'), slot: "action" },
      ],
      list: [],
      loading: false,
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10,
        sizeOpts: [10,20,30,50]
      },
      searchBar: {
        status: '',
        createUser: '',
        engineType: '',
        yarnQueue: ''
      },
      statusList: ['Unlock','Busy'],
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
      //       确认停止当前选中的引擎？
      // 注意：当需要停止的引擎较多时，后台需要时间处理请求，请稍等一段时间后再查询引擎状态。

      // 存在状态为繁忙的引擎被选中，强制停止会导致该引擎上正在运行的任务失败，建议停止前与引擎创建者确认。
      // 注意：当需要停止的引擎较多时，后台需要时间处理请求，请稍等一段时间后再查询引擎状态。

      // 擎停止请求发送成功，可稍后通过搜索查询引擎状态
      const selections = this.$refs.selectionTable.getSelection();
      if (selections.length) {
        //
      } else {
        this.$Message.warning({ content: this.$t('message.enginelist.selectfirst') });
      }
    },
    getEngineList() {
      this.loading = true;
      api.fetch('/dss/data/api/apiauth/list', {
        workspaceId: this.$route.query.workspaceId,
        ...this.searchBar,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        if (res.list) {
          this.loading = false;
        }
      }).catch((err) => {
        console.error(err)
        this.loading = false;
      });
    },
    viewLog() {

    },
    getUserList() {
      if (this.$route.query.workspaceId) {
        GetWorkspaceUserManagement( {
          workspaceId: this.$route.query.workspaceId
        }).then((res) => {
          this.userlist = res.workspaceUsers;
        })
      }
    },
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
</style>
