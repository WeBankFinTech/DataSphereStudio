<template>
  <div>
    <div class="search-header" v-if="!isAdminMode">
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
      <Button class="margin-right" type="success" @click="copyTableName">{{ $t('message.scripts.copytbanme') }}</Button>
      <Button class="margin-right" type="error" @click="deleteSome">{{ $t('message.scripts.batchdel') }}</Button>
      <Dropdown class="margin-right" @on-click="dropdownClick">
        <Button type="primary">
          {{ $t('message.apiServices.query.more') }}
          <Icon type="ios-arrow-down"></Icon>
        </Button>
        <template #list>
          <DropdownMenu>
            <DropdownItem v-if="canTransfer" name="transfer">{{ $t('message.scripts.transfer') }}</DropdownItem>
            <DropdownItem name="download">{{ $t('message.scripts.download') }}</DropdownItem>
            <DropdownItem name="rename">{{ $t('message.scripts.generate_rename_statement') }}</DropdownItem>
          </DropdownMenu>
        </template>
      </Dropdown>
      <Button v-if="isWorkspaceAdmin && isContainDb" type="text" @click="changeViewMode(true)">切换管理员视图</Button>
    </div>
    <div class="search-header" v-else>
      <Input v-model="tableName" class="searce-item margin-right" :placeholder="$t('message.scripts.plstablename')">
      </Input>
      <Select v-model="orderBy" class="searce-item margin-right">
        <Option value="1">{{ $t('message.scripts.defaultsort') }}</Option>
        <Option value="2">{{ $t('message.scripts.ordersize') }}</Option>
        <Option value='3'>{{ $t('message.scripts.ordercreatetime') }}</Option>
        <Option value="4">{{ $t('message.scripts.orderaccesstime') }}</Option>
      </Select>
      <!-- <Select v-model="isTableOwner" class="searce-item margin-right">
        <Option value="0">{{ $t('message.scripts.owntable') }}</Option>
        <Option value="1">{{ $t('message.scripts.tablecreateby') }}</Option>
      </Select> -->
      <Input v-model="tableOwner" class="searce-item margin-right" placeholder="请输入表属主">
      </Input>
      <Button class="margin-right" type="primary" @click="handleGetTables">{{ $t('message.scripts.Search') }}</Button>
      <Button class="margin-right" type="success" @click="copyTableName">{{ $t('message.scripts.copytbanme') }}</Button>
      <Dropdown class="margin-right" @on-click="dropdownClick">
        <Button type="primary">
          {{ $t('message.apiServices.query.more') }}
          <Icon type="ios-arrow-down"></Icon>
        </Button>
        <template #list>
          <DropdownMenu>
            <DropdownItem v-if="canTransfer" name="transfer">{{ $t('message.scripts.transfer') }}</DropdownItem>
            <DropdownItem name="download">{{ $t('message.scripts.download') }}</DropdownItem>
          </DropdownMenu>
        </template>
      </Dropdown>
      <Button type="text" @click="changeViewMode(false)">切换普通视图</Button>
    </div>
    <div class="table-data" style="position:relative">
      <div class="field-list-header" id="dbtbheader" :class="{'ovy': searchColList.length > maxSize}">
        <div class="field-list-item" style="width: 50px">
          <Checkbox
            v-model="selectAll"
            @on-change="changeCheckAll"
          /></div>
        <div class="field-list-item" style="width: 50px">{{ $t('message.scripts.Serial') }}</div>
        <div
          class="field-list-item"
          v-for="(item, index) in columnCalc"
          :key="index"
          :class="item.className"
          :style="{width: item.width? `${item.width}` : 'auto'}"
          @mousemove.prevent.stop="mousemove"
          @mouseup.prevent.stop="mouseup">{{ item.title }}
          <div
            class="resize-bar"
            :data-col-index="index"
            @mousedown="mousedown"
          ></div>
        </div>
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
          <div class="field-list-item" style="width:50px;">
            <Checkbox
              v-model="item.selected"
              @on-change="changeCheckList"
              :disabled="!isAdminMode && item.tableOwner !== getUserName()"
            /></div>
          <div class="field-list-item" style="width: 50px">{{ pageData.pageSize * (pageData.currentPage - 1) + index + 1 }}</div>
          <div
            class="field-list-item"
            :class="index2 === 0? 'table-name-item': ''"
            :title="formatValue(item, field)"
            v-for="(field, index2) in columnCalc"
            :data-key="field.key"
            :style="{width: columnCalc[index2].width? `${columnCalc[index2].width}` : 'auto'}"
            :key="index2"
          >{{ formatValue(item, field) }}</div>
        </li>
      </virtual-list>
      <div v-if="dragStartX" class="drag-line" :style="{left: dragLine.left, display: dragLine.show}" />
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
            <div class="field-list-item" style="width:50px;">{{ $t('message.scripts.Serial') }}</div>
            <div
              class="field-list-item"
              v-for="(item, index) in columnCalc"
              :key="index"
              :style="{width: item.width? `${item.width}` : 'auto'}"
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
              <div class="field-list-item" style="width:50px;">{{ index + 1 }}</div>
              <div
                class="field-list-item"
                :title="formatValue(item, field)"
                v-for="(field, index2) in columnCalc"
                :style="{width: columnCalc[index2].width? `${columnCalc[index2].width}` : 'auto'}"
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
      <!-- rename -->
      <Modal v-model="showRename" ref="renameModalRef" :title="$t('message.scripts.generate_rename_statement')" width="800px" @on-cancel="renameClose">
        <Form v-show="!renameScript" ref="renameForm" :model="renameForm" :rules="renameRule" :label-width="120">
          <FormItem :label="$t('message.scripts.select_database')" prop="dbSelected">
            <Select v-model="renameForm.dbSelected">
              <Option v-for="item in databaseList" :value="item.name" :key="item.id">{{ item.name }}</Option>
            </Select>
          </FormItem>
        </Form>
        <div v-show="renameScript">
          - - {{ $t("message.scripts.system_generated") }}
          <pre class="rename-code">{{  renameScript }}</pre>
        </div>
        <div slot="footer">
          <Button type="text" size="large" @click="renameClose">{{
            $t("message.workspaceManagement.cancel")
          }}</Button>
          <Button v-if="!renameScript" type="primary" size="large" @click="handleRename('next')">{{
            $t("message.scripts.createTable.next")
          }}</Button>
          <Button v-if="renameScript" type="primary" size="large" @click="handleRename('copy')">
            {{ $t("message.scripts.copy_paste_board") }}
          </Button>
          <Button v-if="renameScript" type="primary" size="large" @click="handleRename('create')">
            {{ $t("message.scripts.generate_script") }}
          </Button>
        </div>
      </Modal>
    </div>
    <Spin v-if="loading" fix></Spin>
  </div>
</template>
<script>
import qs from 'qs';
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
    isContainDb() {
      return /(_bak|_work)$/.test(this.dbName)
    },
    canTransfer() {
      return this.$APP_CONF.table_transfer && this.isContainDb
    },
    columnCalc() {
      return this.columns.map((item, index) => {
        return this.adjustCol[index] ? {
          ...item,
          width: this.adjustCol[index]
        } : {
          ...item
        }
      })
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
        { title: this.$t('message.scripts.tablename'), key: 'tableName', width: '20%'},
        { title: this.$t('message.scripts.tbalias'), key: 'tableAlias', width: '10%'},
        { title: this.$t('message.scripts.createtime'), key: 'createTime', width: '10%'},
        { title: this.$t('message.scripts.tablesize'), key: 'tableSize', width: '8%'},
        { title: this.$t('message.scripts.tbowner'), key: 'tableOwner', width: '8%'},
        { title: this.$t('message.scripts.ispartition'), key: 'partitioned', type: 'booleanString', width: '8%'},
        { title: this.$t('message.scripts.iscompress'), key: 'compressed', type: 'boolean', width: '8%'},
        { title: this.$t('message.scripts.compressformat'), key: 'compressedFormat', width: '8%'},
        { title: this.$t('message.scripts.lastaccess'), key: 'viewTime', width: '10%'},
        { title: this.$t('message.scripts.lastupdate'), key: 'modifyTime', width: '10%'},
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
      saveTransing: false,
      dragStartX: undefined,
      dragEndX: undefined,
      dragLine: {
        show: 'none',
        diff: 0,
        left: 0
      },
      adjustCol: [],
      renameForm: {
        dbSelected: '',
      },
      renameRule: {
        dbSelected: [{
          required: true,
          message: this.$t('message.scripts.transferForm.required'),
          trigger: 'change',
        }]
      },
      showRename: false,
      databaseList: [],
      renameScript: '',
      isAdminMode: false,
      isWorkspaceAdmin: false, // 工作空间管理员
    }
  },
  mounted() {
    this.getDatabases();
  },
  methods: {
    formatValue(item, field) {
      return utils.formatValue(item, field);
    },
    handleGetTables() {
      this.selectAll = false;
      this.pageData.currentPage = 1;
      this.getDbTables();
    },
    // 获取库表
    getDbTables() {
      const params = {
        dbName: this.dbName,
        tableName: this.tableName,
        orderBy: this.orderBy,
        pageSize: this.pageData.pageSize,
        currentPage: this.pageData.currentPage
      }
      if (this.isAdminMode)  {
        params.tableOwner = this.tableOwner
      } else {
        params.isTableOwner = this.isTableOwner
      }
      this.loading = true;
      api.fetch('/dss/datapipe/datasource/getTableMetaDataInfo', params, 'get').then((rst) => {
        this.searchColList = rst.tableList;
        this.pageData.total = rst.total;
        this.isWorkspaceAdmin = rst.isWorkspaceAdmin;
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
      }
      if (this.isAdminMode) {
        params.tableOwner = this.tableOwner
      } else {
        params.isTableOwner = this.isTableOwner
      }
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
    download() {
      const params = {
        dbName: this.dbName,
        tableName: this.tableName,
        orderBy: this.orderBy,
        exactTableName: false
      }
      if (this.isAdminMode) {
        params.tableOwner = this.tableOwner
      } else {
        params.isTableOwner = this.isTableOwner
      }
      const paramsStr = qs.stringify(params)
      window.open("/api/rest_j/v1/dss/datapipe/datasource/downloadTableMetaData?" + paramsStr, '_blank');
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
        const tableOwners = [...new Set(toDeleted.map(item => item.tableOwner))];
        if (this.isAdminMode && tableOwners.length > 1) {
          this.$Message.warning({ content: '每次仅支持转移一位用户的表' });
          return
        }
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
    changeViewMode(val) {
      this.changeCheckAll(false);
      this.isAdminMode = val;
      this.tableOwner = '';
      this.handleGetTables();
    },
    changeCheckAll(v) {
      const u = this.getUserName()
      this.searchColList = this.searchColList.map(item => {
        item.selected = v && (this.isAdminMode || item.tableOwner === u)
        return item
      })
    },
    changeCheckList() {
      const u = this.getUserName()
      this.selectAll = this.searchColList.filter(item => {
        return item.tableOwner === u
      }).every(it => it.selected)
      this.searchColList = [...this.searchColList]
    },
    mousedown(e) {
      if (e && e.target && e.target.dataset.colIndex) {
        this.dragColIndex = e.target.dataset.colIndex - 0
        this.dragStartX = e.clientX
        this.tableLeft = this.$el.getBoundingClientRect().x
      }
    },
    mousemove(e) {
      if (this.dragStartX !== undefined) {
        this.dragEndX = e.clientX
        this.dragLine = {
          left: this.dragEndX - this.tableLeft + 'px',
          diff: this.dragEndX - this.dragStartX,
          show: 'block'
        }
      }
    },
    mouseup(e) {
      if (this.dragStartX !== undefined) {
        this.dragEndX = e.clientX
        this.dragLine = {
          left: this.dragEndX - this.tableLeft + 'px',
          diff: this.dragEndX - this.dragStartX,
          show: 'none'
        }
        this.dragStartX = undefined
        this.adjustColWidth()
      }

    },
    adjustColWidth() {
      let adjustCol = []
      this.$el.querySelectorAll('#dbtbheader .field-list-item').forEach(item => adjustCol.push(item.getBoundingClientRect().width))
      adjustCol = adjustCol.slice(2)
      adjustCol.forEach((item, index) => {
        if (index === this.dragColIndex) {
          adjustCol[index] = adjustCol[index] + this.dragLine.diff + 'px'
        } else {
          adjustCol[index] = adjustCol[index] + this.dragLine.diff / (adjustCol.length - 1) * -1 + 'px'
        }
      })
      this.adjustCol = adjustCol
    },
    // rename code
    renameClose() {
      this.renameScript = "";
      this.renameForm.dbSelected = "";
      this.showRename = false;
      this.changeCheckAll(false);
    },
    beforeRename() {
      if(!this.selectedItems.length) {
        this.$Message.warning({ content: this.$t("message.scripts.select_data_table") });
        return;
      }
      this.showRename = true;
      this.$nextTick(() => {
        this.$refs.renameForm.resetFields();
      });
    },
    openScriptTab() {
      const tables = this.selectedItems.map(item => item.tableName);
      const filename = `${tables[0]}_rename.hql`;
      const md5 = utils.md5(this.renameForm.dbSelected + tables.join(""));
      this.dispatch("Workbench:add", {
        id: md5,
        filename,
        filepath: "",
        code: this.renameScript,
        unsave: true,
      }, () => {});
      this.renameClose();
    },
    async handleRename(type) {
      this.$refs.renameForm.validate(valid => {
        if(!valid) return;
        if (type === "next") {
          this.selectedItems.forEach(item => {
            this.renameScript += `alter table ${this.dbName}.${item.tableName} rename to ${this.renameForm.dbSelected}.${item.tableName};\n`;
          });
        } else if(type === "create") {
          this.openScriptTab();
        } else if(type === "copy") {
          const textArea = document.createElement("textarea");
          textArea.value = this.renameScript;
          document.body.appendChild(textArea);
          textArea.select();
          document.execCommand("copy");
          document.body.removeChild(textArea);
          this.$Message.success(this.$t("message.scripts.paste_successfully"));
        }
      });
    },
    dropdownClick(name) {
      switch(name) {
        case 'transfer':
          this.transfer();
          break;
        case 'download':
          this.download();
          break;
        case 'rename':
          this.beforeRename();
          break;
        default:
          break;
      }
    },
    getDatabases() {
      // 取indexedDB缓存
      this.dispatch('IndexedDB:getTree', {
        name: 'hiveTree',
        cb: (res) => {
          if (!res || res.value.length <= 0) {
            api.fetch(`/datasource/dbs`, 'get').then((rst) => {
              rst.dbs.forEach((db) => {
                this.databaseList.push({
                  id: utils.guid(),
                  name: db.dbName
                });
              });
            });
          } else {
            this.databaseList = res.value;
          }
        }
      });
    },
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
      &:last-child{
          border-bottom: 1px solid $border-color-base;
          @include border-color($border-color-base, $dark-border-color-base);
      }
  }
  .field-list-item {
      padding: 0 10px;
      display: inline-block;
      height: 100%;
      text-align: center;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      position: relative;
      &:not(:first-child){
          border-left: 1px solid $border-color-base;
          @include border-color($border-color-base, $dark-border-color-base);
      }
  }
  .resize-bar {
    position: absolute;
    width: 10px;
    height: 100%;
    bottom: 0;
    right: -5px;
    cursor: col-resize;
    z-index: 1;
  }
  .drag-line {
    position: absolute;
    top: 0;
    width: 0px;
    border-left: .5px dashed #eee;
    height: 100%;
    z-index: 1;
    display: none;
    pointer-events: none;
  }
}
.ovy {
  padding-right: 8px
}
.rename-code {
  overflow: auto;
  max-height: 500px;
}
</style>

