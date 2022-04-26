<template>
  <div>
    <div class="search-header">
      <!-- <Input v-model="tableOwner" class="searce-item margin-right" placeholder="请输入用户名">
      </Input> -->
      <Input v-model="tableName" class="searce-item margin-right" placeholder="请输入表名">
      </Input>
      <Select v-model="orderBy" class="searce-item margin-right">
        <Option value="1">默认排序</Option>
        <Option value="2">表大小排序</Option>
        <Option value='3'>创建时间排序</Option>
        <Option value="4">访问时间排序</Option>
      </Select>
      <Select v-model="isTableOwner" class="searce-item margin-right">
        <Option value="0">我有权限的表</Option>
        <Option value="1">我创建的表</Option>
      </Select>
      <Button class="margin-right" type="primary" @click="handleGetTables">查询</Button>
      <Button class="margin-right" type="success" @click="copyTableName">复制表名</Button>
      <Button class="margin-right" type="error" @click="deleteSome">批量删除</Button>
    </div>
    <div class="table-data">
      <div class="field-list-header">
        <div class="field-list-item" style="width:6%;"></div>
        <div class="field-list-item">序号</div>
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
          class="field-list-body"
          :style="{'border-bottom': index === searchColList.length - 1 ? '1px solid #dcdee2' : 'none'}">
          <div class="field-list-item" style="width:6%;">
            <Checkbox
              v-model="item.selected"
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
      <Modal v-model="showConfirmModal" title="请再次勾选确认删除项" width="85%">
        <div class="table-data" style="max-height: 470px">
          <div class="field-list-header">
            <div class="field-list-item" style="width:50px;"></div>
            <div class="field-list-item">序号</div>
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
              class="field-list-body"
              :style="{'border-bottom': index === selectedItems.length - 1 ? '1px solid #dcdee2' : 'none'}">
              <div class="field-list-item" style="width:50px;">
                <Checkbox
                  v-model="item.selected"
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
            $t("message.workspaceManagement.cancel")
          }}</Button>
          <Button type="primary" size="large" @click="confirmSelect">{{
            $t("message.workspaceManagement.ok")
          }}</Button>
        </div>
      </Modal>
    </div>
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
      return Math.floor((window.innerHeight - 242) / 46) - 1;
    },
    selectedItems() {
      const list = []
      this.searchColList.forEach(item=>{
        if (item.selected) {
          list.push({...item, selected: false})
        }
      })
      return list
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
        { title: '表名', key: 'tableName'},
        { title: '表别名', key: 'tableAlias'},
        { title: '创建时间', key: 'createTime'},
        { title: '表大小', key: 'tableSize'},
        { title: '表属主', key: 'tableOwner'},
        { title: '是否分区', key: 'partitioned', type: 'booleanString'},
        { title: '压缩格式', key: 'compressedFormat'},
        { title: '最近访问时间', key: 'viewTime'},
        { title: '最近修改时间', key: 'modifyTime'},
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
      isTableOwner: '0'
    }
  },
  methods: {
    formatValue(item, field) {
      return utils.formatValue(item, field);
    },
    handleGetTables() {
      this.pageData.currentPage = 1;
      this.getDbTables();
    },
    // 获取库表
    getDbTables() {
      const params = {
        dbName: this.dbName,
        tableOwner: this.tableOwner,
        isTableOwner: this.isTableOwner,
        tableName: this.tableName,
        orderBy: this.orderBy,
        pageSize: this.pageData.pageSize,
        currentPage: this.pageData.currentPage
      }
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
      this.pageData.currentPage = number;
      this.getDbTables();
    },
    pageSizeChange(size) {
      this.pageData.pageSize = size;
      this.pageData.currentPage = 1;
      this.getDbTables();
    },
    // 复制表名
    copyTableName() {
      this.loading = true;
      api.fetch('/dss/datapipe/datasource/getTablesName', {
        dbName: this.dbName,
        orderBy: this.orderBy,
        tableName: this.tableName,
        isTableOwner: this.isTableOwner
      }, 'get').then((rst) => {
        let tablesName = rst.tablesName;
        this.loading = false;
        tablesName = tablesName.slice(0,10000).join('\r\n');
        utils.executeCopy(tablesName);
      }).catch(() => {
        this.loading = false;
      })
    },
    deleteSome() {
      this.selectedItems.forEach(item => {
        item.selected = false
      });
      if (this.selectedItems.length) {
        this.showConfirmModal = true
      } else {
        this.$Message.warning({ content: '请先选择删除项' });
      }
    },
    Cancel() {
      this.showConfirmModal = false
    },
    confirmSelect() {
      const toDeleted = this.selectedItems.filter(item => item.selected)
      if (toDeleted.length) {
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
      } else {
        this.$Message.warning({ content: '请先选择删除项' });
      }
    }
  }
}
</script>
<style lang="scss" scoped>
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
  padding: 20px 0;
}
.table-data, .field-list {
  height: calc(100% - 52px);
  overflow: hidden;
  width: 100%;
  .field-list-header,
  .field-list-body {
      width: 100%;
      display: flex;
      border: 1px solid #dcdee2;
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
      background: #fff;
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

