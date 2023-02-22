<template>
  <div class="we-side-bar">
    <we-navbar
      ref="navbar"
      placeholder="db.table"
      :nav-list="navList"
      :add-title="$t('message.scripts.createdTitle')"
      @on-add="openAddTab"
      @on-refresh="reflesh"
      @on-export="openExportDialog('out')"
      @on-change-table-owner="toggleTableOwner"
      @text-change="setSearchText"/>
    <Spin
      v-if="!tableList.length"
      size="large"
      fix/>
    <we-hive-list
      class="we-side-bar-content v-hivedb-list"
      :data="tableList"
      :node="node"
      :filter-text="searchText"
      :tableowner="tableowner"
      :loading="isPending"
      :csTableList="csTableList"
      @we-click="onClick"
      @we-contextmenu="onContextMenu"
      @we-dblclick="handledbclick"/>
    <we-menu
      ref="contextMenu"
      id="hive">
      <template v-if="currentType === 'db'">
        <we-menu-item @select="copyName">
          {{ $t('message.scripts.database.contextMenu.db.copyName') }}
        </we-menu-item>
        <we-menu-item @select="pasteName">
          {{ $t('message.scripts.database.contextMenu.db.pasteName') }}
        </we-menu-item>
        <we-menu-item v-if="!$APP_CONF.hide_view_db_detail" @select="describeDb">
          {{ $t('message.scripts.database.contextMenu.db.dbinfo') }}
        </we-menu-item>
        <we-menu-item class="ctx-divider"/>
        <we-menu-item @select="reflesh">
          {{ $t('message.scripts.constants.refresh') }}
        </we-menu-item>
        <we-menu-item v-if="sortshow" @select="timeSort">
          {{sortText}}
        </we-menu-item>
      </template>
      <template v-if="currentType === 'tb'">
        <we-menu-item
          @select="queryTable"
          v-if="!model">{{ $t('message.scripts.database.contextMenu.tb.queryTable') }}</we-menu-item>
        <we-menu-item
          @select="openDeleteDialog"
          v-if="!model">{{ $t('message.scripts.database.contextMenu.tb.deleteTable') }}</we-menu-item>
        <we-menu-item v-show="nodekeyshow && !$APP_CONF.hide_view_tb_detail" @select="describeTable">{{ $t('message.scripts.database.contextMenu.tb.viewTable') }}</we-menu-item>
        <we-menu-item
          v-if="isAllowToExport && !model"
          @select="openExportDialog">{{ $t('message.scripts.database.contextMenu.tb.exportTable') }}</we-menu-item>
        <we-menu-item class="ctx-divider"/>
        <we-menu-item @select="copyName">{{ $t('message.scripts.database.contextMenu.tb.copyName') }}</we-menu-item>
        <we-menu-item @select="pasteName">{{ $t('message.scripts.database.contextMenu.tb.pasteName') }}</we-menu-item>
        <we-menu-item @select="copyAllColumns">{{ $t('message.scripts.database.contextMenu.tb.copyAllColumns') }}</we-menu-item>
        <we-menu-item class="ctx-divider"/>
        <we-menu-item @select="reflesh">{{ $t('message.scripts.constants.refresh') }}</we-menu-item>
      </template>
      <template v-if="currentType === 'field'">
        <we-menu-item @select="copyName">{{ $t('message.scripts.database.contextMenu.field.copyColumnsName') }}</we-menu-item>
        <we-menu-item @select="pasteName">{{ $t('message.scripts.database.contextMenu.field.pasteName') }}</we-menu-item>
      </template>
    </we-menu>
    <we-hive-table-export
      ref="exportDialog"
      :width="490"
      :table-detail="currentAcitved"
      :db-list="filterDbList"
      :tree="fileTree"
      :load-data-fn="loadDataFn"
      :filter-node="filterNode"
      @get-columns="asyncGetTableColumns"
      @get-tables="getTables"
      @get-tree="getFileTree"
      @get-partitions="getPartitions"
      @get-size="getPartitionsSize"
      @export="exportTable"/>
    <delete-dialog
      ref="deleteDialog"
      :loading="isDeleting"
      @delete="deleteTable"/>
  </div>
</template>
<script>
import { isEmpty, map, find, cloneDeep } from 'lodash';
import module from './index';
import storage from '@dataspherestudio/shared/common/helper/storage';
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import deleteDialog from '@dataspherestudio/shared/components/deleteDialog';
import weHiveList from './hiveList';
import WeHiveTableExport from './hiveTableExport';
import plugin from '@dataspherestudio/shared/common/util/plugin'
export default {
  name: 'WorkSidebar',
  components: {
    weHiveList,
    WeHiveTableExport,
    deleteDialog,
  },
  props: {
    model: String,
    node: Object,
  },
  data() {
    const baseinfo = storage.get("baseInfo", "local") || {}
    return {
      tableowner: baseinfo.tableowner || '',
      searchText: '',
      currentType: '',
      tableList: [],
      initial: [],
      filterDbList: [],
      currentAcitved: null,
      isPending: false,
      fileTree: [],
      isDeleting: false,
      sortshow: false,
      loadDataFn: () => {},
      // 用于避免双击和单击的冲突
      dblClickTimer: null,
      oReq: null,
      csTableList: []
    };
  },
  computed: {
    sortText(){
      if(this.currentAcitved.sort){
        return this.$t('message.scripts.database.contextMenu.db.liststringSort')
      }else{
        return this.$t('message.scripts.database.contextMenu.db.listSort')
      }
    },
    isAllowToExport() {
      if (!this.currentAcitved) {
        return false;
      }
      if (this.currentAcitved.dataType !== 'tb') {
        return false;
      }
      const baseinfo = storage.get("baseInfo", "local") || {}
      if (baseinfo.exportTableEnable === false) {
        return false
      }
      return this.checkAllow(this.currentAcitved.dbName);
    },
    navList() {
      if (this.node && Object.keys(this.node)) {
        return ['search', 'refresh']
      } else {
        return ['search', 'mytable', 'newFile', 'refresh', 'export']
      }
    }
  },
  mounted() {
    this.initData();
    if (this.node && Object.keys(this.node)) {
      this.getTables({name: 'default', csssign: true }, true);
    }
  },
  beforeDestroy() {
    this.cacheHiveTree()
  },
  methods: {
    initData() {
      return new Promise((resolve) => {
        this.dispatch('IndexedDB:getTree', {
          name: 'hiveTree',
          cb: (res) => {
            if (!res || res.value.length <= 0) {
              api.fetch(`/datasource/dbs`, 'get').then((rst) => {
                rst.dbs.forEach((db) => {
                  this.tableList.push({
                    _id: util.guid(),
                    name: db.dbName,
                    dataType: 'db',
                    sort: false,
                    iconCls: 'fi-hivedb',
                    children: [],
                  });
                });
                resolve(this.tableList);
              });
            } else {
              this.tableList = res.value || [];
              resolve(this.tableList);
            }
          }
        })
      });
    },
    onClick({ item }) {
      this.$refs.contextMenu.close();
      switch (item.dataType) {
        case 'db':
          this.loadDBTable(item);
          break;
        case 'tb':
          this.toggleTB(item);
          break;
        case 'cs':
          this.csTitleClick(item);
          break;
        case 'field':
          break;
      }
    },
    csTitleClick(item) {
      this.currentAcitved = item;
      this.setHiveCache(this.currentAcitved);
    },
    async loadDBTable(node) {
      this.currentAcitved = node;
      this.setHiveCache(this.currentAcitved);
      if (node.children && node.children.length) return
      if (this.isPending) return this.$Message.warning(this.$t('message.scripts.constants.warning.api'));
      await this.getTables(node);
    },
    async toggleTB(node) {
      this.currentAcitved = node;
      this.setHiveCache(this.currentAcitved);
      if (node.children && node.children.length) return
      if (this.isPending) return this.$Message.warning(this.$t('message.scripts.constants.warning.api'));
      await this.getTableColumns(node);
    },
    getTables(item, cssInit = false) {// 是否cs库的初始化
      return new Promise((resolve, reject) => {
        this.isPending = true;
        if(item && item.csssign){
          const params = {
            contextID: this.node.contextID,
            nodeName: this.node.title,
            labels: {
              route: this.getCurrentDsslabels()
            }
          }
          api.fetch('/dss/workflow/tables', params).then((res) => {
            this.isPending = false;
            const temArray = res.tables.map((table) => {
              return {
                _id: util.guid(),
                dbName: item.name,
                name: table.tableName,
                value: table.tableName,
                dataType: 'tb',
                iconCls: 'fi-table',
                isView: table.isView,
                createdBy: table.createdBy,
                createdAt: table.createdAt,
                time: table.createdAt,
                title: table.createdBy,
                lastAccessAt: table.lastAccessAt,
                children: [],
                contextKey: table.contextKey,
                contextID: this.node.contextID,
              };
            })
            if (cssInit) {
              this.csTableList = temArray
            } else {
              this.$set(item, 'children', temArray);
            }
          }).catch((err) => {
            reject(err);
            this.isPending = false;
          });
        } else if(item) {
          const url = `/datasource/tables`
          api.fetch(url, {
            database: item.name,
          }, 'get').then((rst) => {
            this.isPending = false;
            item.loaded = true;
            this.$set(item, 'children', rst.tables.map((table) => {
              return {
                _id: util.guid(),
                dbName: item.name,
                name: table.tableName,
                value: table.tableName,
                dataType: 'tb',
                iconCls: 'fi-table',
                isView: table.isView,
                createdBy: table.createdBy,
                createdAt: table.createdAt,
                time: table.createdAt,
                title: table.createdBy,
                lastAccessAt: table.lastAccessAt,
                children: [],
              };
            }));
            this.tableList = [...this.tableList]
            this.cacheHiveTree()
            resolve(item);
          }).catch((err) => {
            reject(err);
            this.isPending = false;
          });
        } else {
          console.warn('item not defined');
        }
      });
    },
    asyncGetTableColumns({ item }, cb) {
      this.currentAcitved = item;
      this.getTableColumns(item).then((item) => {
        cb(item);
      });
    },
    getTableColumns(item) {
      if(item.contextKey){
        const params={
          contextID: item.contextID,
          nodeName: item.name,
          contextKey: item.contextKey,
          labels: {
            route: this.getCurrentDsslabels()
          }
        }
        api.fetch(`/dss/workflow/columns`, params).then((res)=>{
          const column = res.columns;
          const length = column.length;
          let tmpString = '';
          item.loaded = true
          this.$set(item,'children',map(column,(o,index)=>{
            if(index === length - 1){
              tmpString+=o.columnName;
            }else if (index < length - 1) {
              tmpString += `${o.columnName},`;
            }
            return{
              name: o.columnName,
              dataType: 'field',
              iconCls: 'fi-field',
              partitioned: o.partitioned,
              type: o.columnType,
              comment: o.columnComment,
            }
          })
          )
          this.$set(item, 'fullColumn', tmpString);
          this.tableList = [...this.tableList]
          this.cacheHiveTree();
        })
      }else{
        return new Promise((resolve, reject) => {
          try {
            api.fetch(`/datasource/columns`, {database: item.dbName, table: item.name}, 'get').then((rst)=>{
              const column = rst.columns;
              const length = column.length;
              let tmpFullColumnString = '';
              this.$set(item, 'children', map(column, (o, index) => {
                if (index === length - 1) {
                  tmpFullColumnString += o.columnName;
                } else if (index < length - 1) {
                  tmpFullColumnString += `${o.columnName},`;
                }
                return {
                  _id: util.guid(),
                  name: o.columnName,
                  dataType: 'field',
                  iconCls: 'fi-field',
                  partitioned: o.partitioned,
                  type: o.columnType,
                  comment: o.columnComment,
                };
              }));
              item.loaded = true;
              this.$set(item, 'fullColumn', tmpFullColumnString);
              this.tableList = [...this.tableList]
              this.cacheHiveTree();
              resolve(item);
            })

          } catch (e) {
            reject(e);
          }
        });
      }
    },
    onContextMenu({ ev, item, isOpen }) {
      this.nodekeyshow = item.contextKey ? false : true;
      this.currentType = item.dataType;
      this.sortshow = isOpen;
      this.$refs.contextMenu.open(ev);
      this.currentAcitved = item;
      this.setHiveCache(this.currentAcitved);
    },
    handledbclick({ item }) {
      this.currentAcitved = item;
      this.setHiveCache(this.currentAcitved);
      clearTimeout(this.dblClickTimer);
      this.pasteName();
    },
    copyName() {
      let copyLable = '';
      switch (this.currentAcitved.dataType) {
        case 'tb':
          if(this.currentAcitved.contextKey){
            copyLable = this.currentAcitved.name
          }else{
            copyLable = `${this.currentAcitved.dbName}.${this.currentAcitved.name}`;
          }
          break;
        default:
          copyLable = this.currentAcitved.name;
          break;
      }
      util.executeCopy(copyLable);
    },
    async reflesh() {
      if (this.isPending) return this.$Message.warning(this.$t('message.scripts.constants.warning.api'));
      if (this.currentAcitved) {
        this.currentAcitved.children = [];
        if (this.currentAcitved.dataType === 'tb') {
          // 刷新时候清空下size和partitions，否则刷新后再打开表结构数据就会异常
          this.$set(this.currentAcitved, 'size', null);
          this.$set(this.currentAcitved, 'partitions', []);
          await this.getTableColumns(this.currentAcitved);
        } else if (this.currentAcitved.dataType === 'cs') { // 点击上游暂存表后刷新
          this.getTables({name: 'default', csssign: true }, true);
        } else {
          await this.getTables(this.currentAcitved);
          this.$refs.navbar ? this.$refs.navbar.searchText = '' : '';
        }
        this.cacheHiveTree();
      } else {
        const cur = storage.get('activedHiveInfo');
        if (cur && cur.type === 'db') {
          this.currentAcitved = this.tableList.find((item) => item.name === cur.name);
          await this.getTables(this.currentAcitved);
        } else {
          this.$Message.warning(this.$t('message.scripts.database.notPosition'));
        }
      }
    },
    pasteName() {
      let value = this.currentAcitved.dataType === 'tb' ? `${this.currentAcitved.dbName}.${this.currentAcitved.name}` : this.currentAcitved.name;
      if(this.currentAcitved.contextKey){
        value = this.currentAcitved.name
      }
      this.dispatch('Workbench:pasteInEditor', value, this.node);
    },
    timeSort(){
      let item = this.currentAcitved.children.slice(0);
      this.currentAcitved.sort = !this.currentAcitved.sort;
      if(item.length && this.currentAcitved.sort){
        item.sort((a,b)=>{
          return b.createdAt - a.createdAt
        })
        this.currentAcitved.children = item
        this.tableList = [...this.tableList]
      }else{
        this.reflesh()
      }
    },
    queryTable() {
      const tabName = `${this.currentAcitved.dbName}.${this.currentAcitved.name}`;
      const code = `select * from ${tabName} limit 100`;
      const filename = `${tabName}_select.hql`;
      const md5Path = util.md5(filename);
      this.dispatch('Workbench:add', {
        id: md5Path,
        filename,
        filepath: '',
        // saveAs表示临时脚本，需要关闭或保存时另存
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
    },
    describeTable() {
      const filename = `${this.$t('message.scripts.hiveTableDesc.tableDetail')}(${this.currentAcitved.name})`;
      const md5 = util.md5(filename);
      const ext = plugin.emitHook('script_dbtb_details', {
        context: this,
        params: {
          type: 'tableDetails',
          filename,
          md5
        }
      })
      if (ext) {
        return
      }
      const waitFor = [];
      const params = {
        database: this.currentAcitved.dbName,
        tableName: this.currentAcitved.name,
      };
      waitFor.push(this.getTableBaseInfo(params));
      if (waitFor.length) {
        Promise.all(waitFor).then(() => {
          this.dispatch('Workbench:add', {
            id: md5,
            filename,
            filepath: '',
            data: this.currentAcitved,
            type: 'tableDetails',
            currentNodeKey: this.node ? this.node.key : ''
          }, () => {
          });
        }).catch(() => {
        });
      }
    },
    getTableBaseInfo(params) {
      return new Promise((resolve) => {
        api.fetch('/datasource/getTableBaseInfo', params, 'get').then((rst) => {
          this.currentAcitved.baseInfo = rst.tableBaseInfo;
          resolve();
        });
      });
    },
    asyncGetTableFieldsInfo(params, cb) {
      this.getTableFieldsInfo(params).then((fields) => {
        cb(fields);
      }).catch(() => {
        cb(false);
      });
    },
    getTableFieldsInfo(params) {
      return new Promise((resolve, reject) => {
        api.fetch('/datasource/getTableFieldsInfo', params, 'get').then((rst) => {
          resolve(rst.tableFieldsInfo);
        }).catch((err) => {
          reject(err);
        });
      });
    },
    getTableStatisticInfo(params) {
      return new Promise((resolve, reject) => {
        api.fetch('/datasource/getTableStatisticInfo', params, 'get').then((rst) => {
          this.currentAcitved.statisticInfo = rst.tableStatisticInfo;
          resolve();
        }).catch((err) => {
          reject(err);
        });
      });
    },
    getTableSize(params) {
      return new Promise((resolve, reject) => {
        api.fetch('/datasource/size', params, 'get').then((rst) => {
          resolve(rst.sizeInfo.size);
        }).catch((err) => {
          reject(err);
        }).catch((err) => {
          reject(err);
        });
      });
    },
    async copyAllColumns() {
      if (!this.currentAcitved.fullColumn) {
        await this.getTableColumns(this.currentAcitved);
      }
      util.executeCopy(this.currentAcitved.fullColumn);
    },
    openDeleteDialog() {
      const type = this.currentAcitved.isView ? this.$t('message.scripts.view') : this.$t('message.scripts.table');
      this.$refs.deleteDialog.open({
        type,
        name: this.currentAcitved.name,
      });
    },
    deleteTable() {
      this.isDeleting = true;
      const tableName = `${this.currentAcitved.dbName}.${this.currentAcitved.name}`;
      const code = this.currentAcitved.isView ? `drop view ${tableName};` : `drop table ${tableName};`;
      const filename = `${tableName}_drop.hql`;
      const md5Path = util.md5(filename);
      this.dispatch('Workbench:add', {
        id: md5Path,
        filename,
        filepath: '',
        saveAs: true,
        noLoadCache: true,
        // type: 'backgroundScript',
        code,
      }, (f) => {
        if (!f) {
          this.isDeleting = false;
          return this.$refs.deleteDialog.close();
        }
        this.isDeleting = false;
        this.$refs.deleteDialog.close();
        this.$nextTick(() => {
          this.dispatch('Workbench:run', {
            id: md5Path,
          }, () => {
            // 由于删除表成功之后，currentAcitved指向为当前已被删除的表，所以要改为刷新库
            this.currentAcitved = find(this.tableList, (db) => db.name === (this.currentAcitved.dbName || this.currentAcitved.name));
            this.setHiveCache(this.currentAcitved);
            this.reflesh();
          });
        });
      });
    },
    openExportDialog(type) {
      if (type === 'out') {
        this.currentAcitved = null;
        this.setHiveCache(this.currentAcitved);
      }
      this.filterDbList = this.filterDatabase();
      this.$refs.exportDialog.open();
    },
    setSearchText(value) {
      if (this.isPending) return this.$Message.warning(this.$t('message.scripts.constants.warning.api'));
      this.searchText = value;
    },
    toggleTableOwner(all) {
      // 切换个人表，全部表
      if (all) {
        this.tableowner = ''
      } else {
        this.tableowner = this.getUserName()
      }
      const baseInfo = storage.get("baseInfo", "local")
      storage.set('baseInfo', {
        ...baseInfo,
        tableowner: this.tableowner
      }, 'local')
    },
    getPartitions(tableInfo, cb) {
      const Acitvedtable = tableInfo || this.currentAcitved;
      const url = `/datasource/partitions`;
      api.fetch(url, {
        database: Acitvedtable.dbName,
        table: Acitvedtable.name,
      }, 'get').then((rst) => {
        const isPartMore = rst.partitionInfo.partitions && !isEmpty(rst.partitionInfo.partitions[0].children);
        const partitions = this.formatPartions(rst.partitionInfo.partitions);
        this.$set(Acitvedtable, 'isPartition', rst.partitionInfo.isPartition);
        this.$set(Acitvedtable, 'isPartMore', isPartMore);
        this.$set(Acitvedtable, 'partitions', partitions);
        this.cacheHiveTree();
        if (cb) {
          cb({
            isPartition: rst.partitionInfo.isPartition,
            isPartMore,
            partitions,
          });
        }
      });
    },
    getPartitionsSize(params, cb) {
      // cloneDeep是因为值会发生改变。
      const originPart = cloneDeep(params.partition);
      const currentdb = find(this.tableList, (db) => db.name === params.database);
      this.currentAcitved = find(currentdb.children, (tb) => tb.name === params.table);
      this.setHiveCache(this.currentAcitved);
      if (params.partition) {
        this.$set(params, 'partition', this.getDeepPath(params.partition));
      }
      api.fetch('/datasource/size', params, 'get').then((rst) => {
        const size = rst.sizeInfo.size;
        if (!params.partition) {
          this.currentAcitved.size = size;
        } else {
          const findPart = find(this.currentAcitved.partitions, (item) => item.path === originPart.path);
          findPart.size = size;
        }
        cb(rst.sizeInfo.size);
      });
    },
    getPartitionsSizeByOuter(tbInfo, cb) {
      const params = {
        database: tbInfo.dbName,
        table: tbInfo.name,
        partition: tbInfo.partNode ? this.getDeepPath(tbInfo.partNode) : tbInfo.partNode,
      };
      api.fetch('/datasource/size', params, 'get').then((rst) => {
        cb(rst.sizeInfo.size);
      });
    },
    setPartitionsTitle(node, size) {
      node.title += `(分区大小：${size})`;
    },
    // 分区名称在后台存储是以name1/name2/name3的方式
    // 返回前台是{path:name1, children:[{path:name1/name2}]}，所以通过这个函数拿到最底层的path
    getDeepPath(node) {
      if (isEmpty(node.children)) return node.path;
      for (let i = 0; i < node.children.length; i++) {
        return this.getDeepPath(node.children[i]);
      }
    },
    // 对partition进行格式化成tree组件需要的格式
    formatPartions(part) {
      return map(part, (o) => {
        return {
          label: o.label,
          title: o.label,
          expand: false,
          children: this.formatPartions(o.children),
          path: o.path,
        };
      });
    },
    // 过滤数据库列表，由于将整个库列表暴露给其他模块可能有安全问题，所以只对外抛出符合要求的数据库列表，操作子表或者子字段也能通过地址映射填充。
    filterDatabase(dbList) {
      const list = dbList || this.tableList;
      return list.filter((db) => {
        return this.checkAllow(db.name);
      });
    },
    'HiveSidebar:getDatabase'(option, cb) {
      this.initData().then((dbList) => {
        cb(this.filterDatabase(dbList));
      });
    },
    'HiveSidebar:getTables'(option, cb) {
      this.getTables(option.item, false).then((item) => {
        cb(item.children);
      });
    },
    'HiveSidebar:getTablePartitions'(option, cb) {
      this.getPartitions(option.item, (item) => {
        cb(item);
      });
    },
    'HiveSidebar:deletedAndRefresh'() {
      // 由于删除表成功之后，currentAcitved指向为当前已被删除的表，所以要改为刷新库
      this.currentAcitved = find(this.tableList, (db) => db.name === this.currentAcitved.dbName || this.currentAcitved.name);
      this.setHiveCache(this.currentAcitved);
      this.reflesh();
    },
    getFileTree(type) {
      const method = type === 'share' ? 'WorkSidebar:showTree' : 'HdfsSidebar:showTree';
      const treeType = type === 'share' ? 'scriptTree' : 'hdfsTree';
      // 取indexedDB缓存
      this.dispatch('IndexedDB:getTree', {
        name: treeType,
        cb: (res) => {
          if (!res || res.value.length <= 0) {
            this.fileTree = [];
            this.dispatch(method, (f) => {
              f.getRootPath(() => {
                f.getTree((tree) => {
                  if (tree) {
                    this.fileTree.push(tree);
                  }
                  this.loadDataFn = f.loadDataFn;
                });
              });
            });
          } else {
            this.fileTree = cloneDeep(res.value);
            this.dispatch(method, (f) => {
              this.loadDataFn = f.loadDataFn;
            });
          }
        }
      })
    },
    // 过滤文件夹
    filterNode(node) {
      return !node.isLeaf;
    },
    exportTable(one, two, columns, tb) {
      if (!this.currentAcitved) {
        this.currentAcitved = tb
      }
      const part = one.partitions.split('=');
      let separator = one.separator;
      if (one.separator === '%20') {
        separator = ' ';
      } else if (one.separator === '\\t') {
        separator = '\t';
      }
      // 路径最后一个字符可能是/,会导致拼接错误
      const lastParam = two.path.lastIndexOf('/') + 1 === two.path.length ? two.path.length - 1 : two.path.length;
      const path = two.path.slice(7, lastParam);
      const dataInfo = {
        database: one.dbName,
        tableName: one.tbName,
        isPartition: one.isPartition,
        partition: part[0],
        partitionValue: part[1],
        columns,
      };
      const destination = {
        path: `${path}/${two.fileName}.${one.exportType}`,
        pathType: two.type,
        hasHeader: one.isHasHeader,
        isCsv: one.exportType === 'csv',
        isOverwrite: one.isOverwrite !== this.$t('message.scripts.append'),
        fieldDelimiter: separator,
        sheetName: two.sheetName || one.tbName,
        encoding: one.exportType === 'csv' ? one.chartset : '',
        nullValue: one.nullValue,
      };
      if (destination.isCsv) {
        delete destination.sheetName;
      } else {
        delete destination.isOverwrite;
        delete destination.fieldDelimiter;
      }
      const tabName = `export__${this.currentAcitved.dbName}.${this.currentAcitved.name}_${Date.now()}`;
      const code = `val destination = """${JSON.stringify(destination)}"""\nval dataInfo = """${JSON.stringify(dataInfo)}"""\norg.apache.linkis.engineplugin.spark.imexport.ExportData.exportData(spark,dataInfo,destination)`;
      const md5Path = util.md5(tabName);
      this.dispatch('Workbench:add', {
        id: md5Path,
        filename: tabName + '.scala',
        filepath: '',
        // saveAs表示临时脚本，需要关闭或保存时另存
        saveAs: true,
        noLoadCache: true,
        action: 'export_table',
        code,
      }, (f) => {
        this.currentAcitved = null
        if (!f) {
          return this.$refs.exportDialog.close();
        }
        this.$refs.exportDialog.close();
        this.$nextTick(() => {
          this.dispatch('Workbench:run', {
            id: md5Path,
            type: 'storage',
            executionCode: {
              dataInfo,
              destination,
            },
            backgroundType: 'export',
          }, () => {
            this.$refs.exportDialog.loading = false;
          });
        });
      });
    },
    openAddTab() {
      const filename = `${this.$t('message.scripts.createdTitle')}_${new Date().getTime()}`;
      const md5 = util.md5(filename);
      this.dispatch('Workbench:add', {
        id: md5,
        filename,
        filepath: '',
        data: this.tableList,
        type: 'createTable',
      }, () => {
      });
    },
    setHiveCache(cur) {
      let item = null;
      if (cur) {
        item = {
          name: cur.name,
          type: cur.dataType,
        };
      }
      storage.set('activedHiveInfo', item);
    },
    checkAllow(name) {
      const allowMap = module.data.ALLOW_MAP;
      if (!allowMap.length) {
        return true;
      }
      for (let i = 0; i < allowMap.length; i++) {
        if (name.match(allowMap[i])) {
          return true;
        }
      }
    },
    cacheHiveTree() {
      this.dispatch('IndexedDB:appendTree', { treeId: 'hiveTree', value: this.tableList })
    },
    describeDb() {
      const filename = `库详情(${this.currentAcitved.name})`;
      const md5 = util.md5(filename);
      const ext = plugin.emitHook('script_dbtb_details', {
        context: this,
        params: {
          type: 'dbDetails',
          filename,
          md5
        }
      })
      if (ext) {
        return
      }
      this.dispatch('Workbench:add', {
        id: md5,
        filename,
        filepath: '',
        data: this.currentAcitved,
        type: 'dbDetails',
        currentNodeKey: this.node ? this.node.key : ''
      }, () => {
      });
    }
  },

};
</script>
<style src="../../assets/styles/sidebar.scss" lang="scss">
</style>
