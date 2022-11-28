<template>
  <div style="height: 100%;  padding: 10px 15px;">
    <Form class="table-searchbar" ref="searchBar" :model="searchBar" :rules="ruleInline" inline>
      <FormItem prop="status" :label="$t('message.enginelist.status')" :label-width="50">
        <Select v-model="searchBar.status" style="min-width:120px;">
          <Option
            v-for="(item) in statusList"
            :label="item.label"
            :value="item.value"
            :key="item.value"
          />
        </Select>
      </FormItem>
      <FormItem prop="user" :label="$t('message.enginelist.user')" :label-width="50">
        <Select v-model="searchBar.user" style="min-width:120px;">
          <Option
            v-for="(item) in userlist"
            :label="item.label"
            :value="item.value"
            :key="item.value"
          />
        </Select>
      </FormItem>
      <FormItem prop="engineType" :label="$t('message.enginelist.engineType')" :label-width="80">
        <Select v-model="searchBar.engineType" style="min-width:120px;">
          <Option
            v-for="(item) in engineTypes"
            :label="item.label"
            :value="item.value"
            :key="item.value"
          />
        </Select>
      </FormItem>
      <FormItem prop="queue" :label="$t('message.enginelist.queue')" :label-width="50">
        <Select v-model="searchBar.queue" style="min-width:120px;">
          <Option
            v-for="(item) in queueList"
            :label="item.label"
            :value="item.value"
            :key="item.value"
          />
        </Select>
      </FormItem>
      <FormItem class="btn">
        <Button
          type="primary"
          @click="enginelist"
        >{{$t('message.enginelist.find')}}</Button>
        <Button
          type="warning"
          @click="stop"
        >{{$t('message.enginelist.stop')}}</Button>
      </FormItem>
    </Form>
    <Table :columns="columns" :data="list">
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

      },
      statusList: [],
      userlist: [],
      engineTypes: [],
      queueList: [],
      ruleInline: []
    }
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

    },
    getEngineList() {
      this.loading = true;
      api.fetch('/dss/data/api/apiauth/list', {
        workspaceId: this.$route.query.workspaceId,
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
</style>
