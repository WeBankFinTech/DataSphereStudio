<template>
  <div>
    <div class="search-header">
      <!-- <Input v-model="tableOwner" class="searce-item margin-right" placeholder="请输入用户名">
      </Input> -->
      <Input v-model="tableName" class="searce-item margin-right" :placeholder="$t('message.scripts.plstablename')">
      </Input>
      <Select v-model="orderBy" class="searce-item margin-right">
        <Option value="1">{{ $t('message.scripts.defaultsort') }}</Option>
        <Option value="2">{{ $t('message.scripts.ordersize') }}</Option>
        <Option value='3'>{{ $t('message.scripts.ordercreatetime') }}</Option>
        <Option value="4">{{ $t('message.scripts.orderaccesstime') }}</Option>
      </Select>
      <Select v-model="isTableOwner" class="searce-item margin-right">
        <Option value="0">{{ $t('message.scripts.owntable') }}</Option>
        <Option value="1">{{ $t('message.scripts.tablecreateby') }}</Option>
      </Select>
      <Button class="margin-right" type="primary" @click="handleGetTables">{{ $t('message.scripts.Search') }}</Button>
      <Button class="margin-right" type="primary" :title="$t('message.scripts.realsearchTip')" @click="handleGetTables(true)">{{ $t('message.scripts.searchnow') }}</Button>
      <Button class="margin-right" type="success" @click="copyTableName">{{ $t('message.scripts.copytbanme') }}</Button>
      <Button v-if="canTransfer" class="margin-right" type="success" @click="transfer">{{ $t('message.scripts.transfer') }}</Button>
      <Button class="margin-right" type="error" @click="deleteSome">{{ $t('message.scripts.batchdel') }}</Button>
    </div>
    <div class="table-data">
      <div class="field-list-header">
        <div class="field-list-item" style="width:6%;">
          <Checkbox
            v-model="selectAll"
            @on-change="changeCheckAll"
          /></div>
        <div class="field-list-item">{{ $t('message.scripts.Serial') }}</div>
        <div
          class="field-list-item"
          v-for="(item, index) in columns"
          :key="index"
          :class="item.className">{{ item.title }}</div>
      </div>
      <virtual-list
        ref="columnTables"
        :size="46"
        :remain="searchColList.length > maxSize ? maxSize : searchColList.length"
        wtag="ul"
        class="field-list">
        <li
          v-for="(item, index) in searchColList"
          :key="index"
          class="field-list-body">
          <div class="field-list-item" style="width:6%;">
            <Checkbox
              v-model="item.selected"
              @on-change="changeCheckList"
              :disabled="item.tableOwner !== getUserName()"
            /></div>
          <div class="field-list-item">{{ index + 1 }}</div>
          <div
            class="field-list-item"
            :title="formatValue(item, field)"
            v-for="(field, index2) in columns"
            :key="index2"
          >{{ formatValue(item, field) }}</div>
        </li>
      </virtual-list>
      <Page
        class="page-bar"
        :total="pageData.total"
        show-sizer
        :current="pageData.currentPage"
        :page-size="pageData.pageSize"
        :page-size-opts="pageData.opts"
        @on-change="pageChange"
        @on-page-size-change="pageSizeChange"
      ></Page>
      <Modal v-model="showConfirmModal" :title="$t('message.scripts.checkdelconfirm')" width="85%">
        <div class="table-data" style="max-height: 470px">
          <div class="field-list-header">
            <div class="field-list-item" style="width:50px;">
              <Checkbox
                v-model="selectAllConfirm"
                @on-change="changeCheckAllConfirm"
              />
            </div>
            <div class="field-list-item">{{ $t('message.scripts.Serial') }}</div>
            <div
              class="field-list-item"
              v-for="(item, index) in columns"
              :key="index"
              :class="item.className">{{ item.title }}</div>
          </div>
          <virtual-list
            ref="columnTables"
            :size="46"
            :remain="selectedItems.length > 9 ? 9 : selectedItems.length"
            wtag="ul"
            class="field-list">
            <li
              v-for="(item, index) in selectedItems"
              :key="index"
              class="field-list-body">
              <div class="field-list-item" style="width:50px;">
                <Checkbox
                  v-model="item.selected"
                  @on-change="changeCheck"
                /></div>
              <div class="field-list-item">{{ index + 1 }}</div>
              <div
                class="field-list-item"
                :title="formatValue(item, field)"
                v-for="(field, index2) in columns"
                :key="index2"
              >{{ formatValue(item, field) }}</div>
            </li>
          </virtual-list>
        </div>
        <div slot="footer">
          <Button type="text" size="large" @click="Cancel">{{
            $t("message.common.cancel")
          }}</Button>
          <Button type="primary" size="large" @click="confirmSelect">{{
            confirmModalType == 'delete' ? $t("message.common.ok") : $t("message.common.dss.nextstep")
          }}</Button>
        </div>
      </Modal>
      <!-- 表属主转移 -->
      <Modal v-model="showTransferForm" :title="$t('message.scripts.transferTitle')">
        <Form ref="transferForm" :model="formState" :rules="formRule" :label-width="120">
          <FormItem :label="$t('message.scripts.transferForm.title')" prop="title">
            <Input
              v-model="formState.title"
            ></Input>
          </FormItem>
          <FormItem :label="$t('message.scripts.transferForm.owner')" prop="owner">
            <Input
              v-model="formState.owner"
              :placeholder="$t('message.scripts.transferForm.ownerplaceholder')"
            ></Input>
          </FormItem>
          <FormItem :label="$t('message.scripts.transferForm.admin')" prop="admin">
            <Input
              v-model="formState.admin"
              :placeholder="$t('message.scripts.transferForm.adminplaceholder')"
            ></Input>
          </FormItem>
          <FormItem :label="$t('message.scripts.transferForm.desc')" prop="desc">
            <Input
              type="textarea"
              v-model="formState.desc"
            ></Input>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button v-if="!saveTransing" type="text" size="large" @click="showTransferForm = false">{{
            $t("message.workspaceManagement.cancel")
          }}</Button>
          <Button v-if="!saveTransing" type="text" size="large" @click="prevStep">{{
            $t("message.common.dss.prevstep")
          }}</Button>
          <Button type="primary" size="large" @click="saveTransfer" :loading="saveTransing">{{
            $t("message.workspaceManagement.ok")
          }}</Button>
        </div>
      </Modal>
    </div>
    <Spin v-if="loading" fix></Spin>
  </div>
</template>
<script>
import utils from '@dataspherestudio/shared/common/util';
import virtualList from '@dataspherestudio/shared/components/virtualList';
import api from '@dataspherestudio/shared/common/service/api';
export default {
  props: {
    dbName: {
      type: String
    }
  },
  components: {
    virtualList
  },
  computed: {
    maxSize() {
      return Math.floor((window.innerHeight - 255) / 46) - 1;
    },
    selectedItems() {
      const list = []
      this.searchColList.forEach(item=>{
        if (item.selected) {
          list.push({...item, selected: false})
        }
      })
      return list
    },
    canTransfer() {
      return this.$APP_CONF.table_transfer && /(_bak|_work)$/.test(this.dbName)
    }
  },
  watch: {
    loading(val) {
      this.$emit('loadingChange', val)
    }
  },
  data() {
    return {
      columns: [
        { title: this.$t('message.scripts.tablename'), key: 'tableName'},
        { title: this.$t('message.scripts.tbalias'), key: 'tableAlias'},
        { title: this.$t('message.scripts.createtime'), key: 'createTime'},
        { title: this.$t('message.scripts.tablesize'), key: 'tableSize'},
        { title: this.$t('message.scripts.tbowner'), key: 'tableOwner'},
        { title: this.$t('message.scripts.ispartition'), key: 'partitioned', type: 'booleanString'},
        { title: this.$t('message.scripts.iscompress'), key: 'compressed', type: 'boolean'},
        { title: this.$t('message.scripts.compressformat'), key: 'compressedFormat'},
        { title: this.$t('message.scripts.lastaccess'), key: 'viewTime'},
        { title: this.$t('message.scripts.lastupdate'), key: 'modifyTime'},
      ],
      showConfirmModal: false,
      pageData: {
        currentPage: 1,
        pageSize: 20,
        opts: [20, 30, 50],
        total: 0,
      },
      searchColList: [
      ],
      tableOwner: '',
      tableName: '',
      orderBy: '1',
      loading: false,
      tablesName: [],
      isTableOwner: '0',
      selectAllConfirm: false,
      selectAll: false,
      showTransferForm: false,
      formRule: {
        title: [{
          required: true,
          message: this.$t('message.scripts.transferForm.required'),
          trigger: 'change',
        }],
        owner: [{
          required: true,
          message: this.$t('message.scripts.transferForm.required'),
          trigger: 'change',
        }],
        admin: [{
          required: true,
          message: this.$t('message.scripts.transferForm.required'),
          trigger: 'change',
        }],
        desc: [{
          message: this.$t('message.scripts.transferForm.max', {len: 500}),
          trigger: 'change',
          max: 500
        }]
      },
      formState: {
        title: '',
        owner: '',
        admin: '',
        desc: ''
      },
      confirmModalType: 'delete',
      saveTransing: false
    }
  },
  methods: {
    formatValue(item, field) {
      return utils.formatValue(item, field);
    },
    handleGetTables(isreal) {
      this.selectAll = false;
      this.pageData.currentPage = 1;
      this.isRealTime = isreal === true
      this.getDbTables();
    },
    // 获取库表
    getDbTables() {
      const params = {
        dbName: this.dbName,
        // tableOwner: this.tableOwner,
        isTableOwner: this.isTableOwner,
        tableName: this.tableName,
        orderBy: this.orderBy,
        pageSize: this.pageData.pageSize,
        currentPage: this.pageData.currentPage
      }
      if (this.isRealTime) params.isRealTime = true
      this.loading = true;
      api.fetch('/dss/datapipe/datasource/getTableMetaDataInfo', params, 'get').then((rst) => {
        this.searchColList = rst.tableList;
        this.pageData.total = rst.total;
        this.loading = false;
        this.$emit('tableListChange', this.searchColList)
      }).catch(() => {
        this.loading = false;
      });
    },
    pageChange(number) {
      this.selectAll = false;
      this.pageData.currentPage = number;
      this.getDbTables();
    },
    pageSizeChange(size) {
      this.selectAll = false;
      this.pageData.pageSize = size;
      this.pageData.currentPage = 1;
      this.getDbTables();
    },
    // 复制表名
    copyTableName() {
      this.loading = true;
      const params = {
        dbName: this.dbName,
        orderBy: this.orderBy,
        tableName: this.tableName,
        isTableOwner: this.isTableOwner
      }
      if (this.isRealTime) params.isRealTime = true
      api.fetch('/dss/datapipe/datasource/getTablesName', params, 'get').then((rst) => {
        let tablesName = rst.tablesName;
        this.loading = false;
        tablesName = tablesName.slice(0,10000).join('\r\n');
        utils.executeCopy(tablesName);
      }).catch(() => {
        this.loading = false;
      })
    },
    // 批量转移
    transfer() {
      this.confirmModalType = 'transfer'
      if (this.selectedItems.length) {
        this.selectAllConfirm = false
        this.showConfirmModal = true
      } else {
        this.$Message.warning({ content: this.$t('message.scripts.selectfirst') });
      }
    },
    deleteSome() {
      this.confirmModalType = 'delete'
      this.selectedItems.forEach(item => {
        item.selected = false
      });
      if (this.selectedItems.length) {
        this.selectAllConfirm = false
        this.showConfirmModal = true
      } else {
        this.$Message.warning({ content: this.$t('message.scripts.selectfirst') });
      }
    },
    Cancel() {
      this.showConfirmModal = false
    },
    confirmSelect() {
      const toDeleted = this.selectedItems.filter(item => item.selected)
      if (toDeleted.length < 1) {
        this.$Message.warning({ content: this.$t('message.scripts.checkdelconfirm') });
        return
      }
      if (this.confirmModalType == 'transfer') {
        // 批量转移
        this.showTransferForm = true
        this.showConfirmModal = false
      } else {
        // 批量删除
        const code = toDeleted.map(item => `drop ${item.isView ? 'view': 'table'} ${this.dbName}.${item.tableName};`).join('\n')
        const filename = `${this.dbName}_delete_table_${Date.now()}.hql`;
        const md5Path = utils.md5(filename);
        this.dispatch('Workbench:add', {
          id: md5Path,
          filename,
          filepath: '',
          saveAs: true,
          noLoadCache: true,
          code,
        }, (f) => {
          if (!f) {
            return;
          }
          this.$nextTick(() => {
            this.dispatch('Workbench:run', { id: md5Path });
          });
        });
        this.showConfirmModal = false
      }
    },
    saveTransfer() {
      const tbs = this.selectedItems.filter(it => it.selected).map(item => item.tableName)
      const params = {
        approvalTitle: this.formState.title,
        dbName: this.dbName,
        tablesName: tbs,
        newOwner: this.formState.owner,
        dataGovernanceAdmin: this.formState.admin,
        description: this.formState.desc
      }
      if (this.saveTransing) return
      this.$refs.transferForm.validate((valid) => {
        if (valid) {
          this.saveTransing = true
          api.fetch('/dss/datapipe/datasource/transferTablesOwner', params, 'post').then((res) => {
            this.$Message.success(res.message || this.$t("message.common.saveSuccess"));
            this.showTransferForm = false
          }).finally(() => {
            this.saveTransing = false
          });
        }
      })
    },
    prevStep() {
      this.showTransferForm = false
      this.showConfirmModal = true
    },
    changeCheck() {
      this.selectAllConfirm = this.selectedItems.every(it => it.selected)
    },
    changeCheckAllConfirm(v) {
      this.selectedItems.forEach(item => {
        item.selected = v
      })
    },
    changeCheckAll(v) {
      const u = this.getUserName()
      this.searchColList = this.searchColList.map(item => {
        item.selected = v && item.tableOwner === u
        return item
      })
    },
    changeCheckList() {
      const u = this.getUserName()
      this.selectAll = this.searchColList.filter(item => {
        return item.tableOwner === u
      }).every(it => it.selected)
      this.searchColList = [...this.searchColList]
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.search-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  .searce-item {
    width: 250px;
  }
  .margin-right {
    margin-right: 30px;
  }
}
.page-bar {
  text-align: center;
  padding: 16px 0;
}
.table-data, .field-list {
  width: 100%;
  @include font-color($light-text-color, $dark-text-color);
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
      background-color: #5e9de0;
      color: #fff;
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
      width: 11%;
      padding: 0 10px;
      display: inline-block;
      height: 100%;
      text-align: center;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
  }
}
</style>

