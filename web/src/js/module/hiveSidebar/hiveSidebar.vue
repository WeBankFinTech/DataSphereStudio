<template>
  <div class="we-side-bar">
    <we-navbar
      ref="navbar"
      :nav-list="navList"
      :add-title="$t('message.newNavBar.createdTitle')"
      @on-add="openAddTab"
      @on-refresh="reflesh"
      @text-change="setSearchText"/>
    <Spin
      v-if="!tableList.length"
      size="large"
      fix/>
    <we-hive-list
      :data="tableList"
      :filter-text="searchText"
      :loading="isPending"
      class="we-side-bar-content v-hivedb-list"
      @we-click="onClick"
      @we-contextmenu="onContextMenu"
      @we-dblclick="handledbclick"/>
    <we-menu
      ref="contextMenu"
      id="hive">
      <template v-if="currentType === 'db'">
        <we-menu-item @select="copyName">
          {{ $t('message.database.contextMenu.db.copyName') }}
        </we-menu-item>
        <we-menu-item @select="pasteName">
          {{ $t('message.database.contextMenu.db.pasteName') }}
        </we-menu-item>
        <we-menu-item class="ctx-divider"/>
        <we-menu-item @select="reflesh">
          {{ $t('message.constants.refresh') }}
        </we-menu-item>
      </template>
      <template v-if="currentType === 'tb'">
        <we-menu-item
          @select="queryTable"
          v-if="!model">{{ $t('message.database.contextMenu.tb.queryTable') }}</we-menu-item>
        <we-menu-item
          @select="openDeleteDialog"
          v-if="!model">{{ $t('message.database.contextMenu.tb.deleteTable') }}</we-menu-item>
        <we-menu-item @select="describeTable">{{ $t('message.database.contextMenu.tb.viewTable') }}</we-menu-item>
        <!-- <we-menu-item
          v-if="isAllowToExport && !model"
          @select="openExportDialog">{{ $t('message.database.contextMenu.tb.exportTable') }}</we-menu-item> -->
        <we-menu-item class="ctx-divider"/>
        <we-menu-item @select="copyName">{{ $t('message.database.contextMenu.tb.copyName') }}</we-menu-item>
        <we-menu-item @select="pasteName">{{ $t('message.database.contextMenu.tb.pasteName') }}</we-menu-item>
        <we-menu-item @select="copyAllColumns">{{ $t('message.database.contextMenu.tb.copyAllColumns') }}</we-menu-item>
        <we-menu-item class="ctx-divider"/>
        <we-menu-item @select="reflesh">{{ $t('message.constants.refresh') }}</we-menu-item>
      </template>
      <template v-if="currentType === 'field'">
        <we-menu-item @select="copyName">{{ $t('message.database.contextMenu.field.copyColumnsName') }}</we-menu-item>
        <we-menu-item @select="pasteName">{{ $t('message.database.contextMenu.field.pasteName') }}</we-menu-item>
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
import storage from '@/js/helper/storage';
import api from '@/js/service/api';
import util from '@/js/util';
import weHiveList from '@js/component/hiveList';
import WeHiveTableExport from '@js/component/hiveTableExport';
import deleteDialog from '@js/component/deleteDialog';
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
    return {
      navList: ['search', 'newFile', 'refresh'],
      searchText: '',
      currentType: '',
      tableList: [],
      filterDbList: [],
      currentAcitved: null,
      isPending: false,
      fileTree: [],
      isDeleting: false,
      loadDataFn: () => {},
      // 用于避免双击和单击的冲突
      dblClickTimer: null,
    };
  },
  computed: {
    isAllowToExport() {
      return this.currentAcitved && this.currentAcitved.dataType === 'tb';
    },
  },
  mounted() {
    this.initData();
  },
  beforeDestroy() {
    storage.set('hiveTree', this.tableList);
  },
  methods: {
    initData() {
      return new Promise((resolve) => {
        const hiveList = storage.get('hiveTree');
        if (!hiveList || isEmpty(hiveList)) {
          api.fetch(`/datasource/dbs`, 'get').then((rst) => {
            rst.dbs.forEach((db) => {
              this.tableList.push({
                name: db.dbName,
                dataType: 'db',
                isOpen: false,
                isVisible: true,
                children: [],
                iconCls: 'fi-hivedb',
              });
            });
            resolve(this.tableList);
          });
        } else {
          this.tableList = hiveList;
          resolve(this.tableList);
        }
      });
    },
    onClick({ index, item }) {
      this.$refs.contextMenu.close();
      clearTimeout(this.dblClickTimer);
      this.dblClickTimer = setTimeout(() => {
        switch (item.dataType) {
          case 'db':
            this.toggleDB(item, index);
            break;
          case 'tb':
            this.toggleTB(item, index);
            break;
          case 'field':
            break;
        };
      }, 300);
    },
    async toggleDB(item) {
      this.currentAcitved = item;
      this.setHiveCache(this.currentAcitved);
      if (item.children.length) return item.isOpen = !item.isOpen;
      if (this.isPending) return this.$Message.warning(this.$t('message.constants.warning.api'));
      await this.getTables(item, true);
    },
    async toggleTB(item) {
      this.currentAcitved = item;
      this.setHiveCache(this.currentAcitved);
      if (item.children.length) return item.isOpen = !item.isOpen;
      if (this.isPending) return this.$Message.warning(this.$t('message.constants.warning.api'));
      await this.getTableColumns(item, true, true);
    },
    getTables(item, isOpen) {
      return new Promise((resolve, reject) => {
        this.isPending = true;
        const url = `/datasource/tables`;
        api.fetch(url, {
          database: item.name,
        }, 'get').then((rst) => {
          this.isPending = false;
          item.isOpen = isOpen;
          this.$set(item, 'children', rst.tables.map((table) => {
            return {
              dbName: item.name,
              name: table.tableName,
              value: table.tableName,
              dataType: 'tb',
              iconCls: 'fi-table',
              isOpen: false,
              isVisible: true,
              isView: table.isView,
              createdBy: table.createdBy,
              createdAt: table.createdAt,
              lastAccessAt: table.lastAccessAt,
              children: [],
            };
          }));
          storage.set('hiveTree', this.tableList, 'SESSION');
          resolve(item);
        }).catch((err) => {
          reject(err);
          this.isPending = false;
        });
      });
    },
    asyncGetTableColumns({ item, isOpen, async }, cb) {
      this.getTableColumns(item, isOpen, async).then((item) => {
        cb(item);
      });
    },
    getTableColumns(item, isOpen, async) {
      return new Promise((resolve, reject) => {
        try {
          const apiPrefix = module.data.API_PATH;
          const url = `${apiPrefix}datasource/columns?database=${item.dbName}&table=${item.name}`;
          const oReq = new XMLHttpRequest();
          oReq.addEventListener('load', () => {
            item.isOpen = isOpen;
            const rst = JSON.parse(oReq.response);
            const column = rst.data.columns;
            const length = column.length;
            let tmpFullColumnString = '';
            this.$set(item, 'children', map(column, (o, index) => {
              if (index === length - 1) {
                tmpFullColumnString += o.columnName;
              } else if (index < length - 1) {
                tmpFullColumnString += `${o.columnName},`;
              }
              return {
                name: o.columnName,
                dataType: 'field',
                iconCls: 'fi-field',
                partitioned: o.partitioned,
                type: o.columnType,
                comment: o.columnComment,
              };
            }));
            this.$set(item, 'fullColumn', tmpFullColumnString);
            storage.set('hiveTree', this.tableList, 'SESSION');
            resolve(item);
          });
          oReq.open('get', url, async);
          oReq.send(null);
        } catch (e) {
          reject(e);
        }
      });
    },
    onContextMenu({ ev, item }) {
      this.currentType = item.dataType;
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
          copyLable = `${this.currentAcitved.dbName}.${this.currentAcitved.name}`;
          break;
        default:
          copyLable = this.currentAcitved.name;
          break;
      };
      util.executeCopy(copyLable);
    },
    async reflesh() {
      if (this.isPending) return this.$Message.warning(this.$t('message.constants.warning.api'));
      if (this.currentAcitved) {
        this.currentAcitved.children = [];
        if (this.currentAcitved.dataType === 'tb') {
          // 刷新时候清空下size和partitions，否则刷新后再打开表结构数据就会异常
          this.$set(this.currentAcitved, 'size', null);
          this.$set(this.currentAcitved, 'partitions', []);
          await this.getTableColumns(this.currentAcitved, true, true);
        } else {
          await this.getTables(this.currentAcitved, true);
          this.$refs.navbar.searchText = '';
        }
        storage.set('hiveTree', this.tableList, 'SESSION');
      } else {
        const cur = storage.get('activedHiveInfo');
        if (cur && cur.type === 'db') {
          this.currentAcitved = this.tableList.find((item) => item.name === cur.name);
          await this.getTables(this.currentAcitved, true);
        } else {
          this.$Message.warning(this.$t('message.database.notPosition'));
        }
      }
    },
    pasteName() {
      const value = this.currentAcitved.dataType === 'tb' ? `${this.currentAcitved.dbName}.${this.currentAcitved.name}` : this.currentAcitved.name;
      this.dispatch('Workbench:pasteInEditor', value, this.node);
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
    // describeTable() {
    //   const hasChildren = this.currentAcitved.children.length;
    //   const hasSize = this.currentAcitved.size;
    //   const hasPart = this.currentAcitved.partitions && this.currentAcitved.partitions.length;
    //   if (!hasChildren || !hasSize || !hasPart) {
    //     // this.$refs.tableDesc.loading = true;
    //     const waitFor = [];
    //     if (!hasChildren) {
    //       waitFor.push(this.getTableColumns(this.currentAcitved, false, true));
    //     }
    //     if (!hasSize) {
    //       const params = {
    //         database: this.currentAcitved.dbName,
    //         table: this.currentAcitved.name,
    //       };
    //       waitFor.push(this.getTableSize(params).then((size) => {
    //         this.$set(this.currentAcitved, 'size', size);
    //       }));
    //     }
    //     if (!hasPart) {
    //       waitFor.push(this.getPartitions());
    //     }
    //     if (waitFor.length) {
    //       Promise.all(waitFor).then(() => {
    //         const filename = `表详情(${this.currentAcitved.name})`;
    //         const md5 = util.md5(filename);
    //         this.dispatch('Workbench:add', {
    //           id: md5,
    //           filename,
    //           filepath: '',
    //           data: this.currentAcitved,
    //           type: 'tableDetails',
    //         }, (f)=>{
    //         });
    //         // this.$refs.tableDesc.loading = false;
    //       }).catch((err) => {
    //         // this.$refs.tableDesc.loading = false;
    //       });
    //     } else {
    //       // this.$refs.tableDesc.loading = false;
    //     }
    //   }
    // },
    describeTable() {
      const waitFor = [];
      const params = {
        database: this.currentAcitved.dbName,
        tableName: this.currentAcitved.name,
      };
      waitFor.push(this.getTableBaseInfo(params), this.getTableFieldsInfo(params).then((fields) => {
        this.currentAcitved.fieldsList = fields;
      }), this.getTableStatisticInfo(params));
      if (waitFor.length) {
        Promise.all(waitFor).then(() => {
          const filename = `${this.$t('message.hiveTableDesc.tableDetail')}(${this.currentAcitved.name})`;
          const md5 = util.md5(filename);
          this.dispatch('Workbench:add', {
            id: md5,
            filename,
            filepath: '',
            data: this.currentAcitved,
            type: 'tableDetails',
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
        await this.getTableColumns(this.currentAcitved, false, false);
      }
      util.executeCopy(this.currentAcitved.fullColumn);
    },
    openDeleteDialog() {
      const type = this.currentAcitved.isView ? '视图' : '表';
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
            this.currentAcitved = find(this.tableList, (db) => db.name === this.currentAcitved.dbName || this.currentAcitved.name);
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
      if (this.isPending) return this.$Message.warning(this.$t('message.constants.warning.api'));
      this.searchText = value;
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
        storage.set('hiveTree', this.tableList, 'SESSION');
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
    filterDatabase(dbList) {
      return dbList || this.tableList;
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
      const tmpTree = storage.get(treeType, 'SESSION');
      if (!tmpTree || isEmpty(tmpTree)) {
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
        this.fileTree = cloneDeep(tmpTree);
        this.dispatch(method, (f) => {
          this.loadDataFn = f.loadDataFn;
        });
      }
    },
    // 过滤文件夹
    filterNode(node) {
      return !node.isLeaf;
    },
    exportTable(one, two, columns) {
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
        isOverwrite: one.isOverwrite !== '追加',
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
      const tabName = `export__${this.currentAcitved.dbName}.${this.currentAcitved.name}`;
      const code = `val destination = """${JSON.stringify(destination)}"""\nval dataInfo = """${JSON.stringify(dataInfo)}"""\ncom.webank.bdp.dataworkcloud.engine.imexport.ExportData.exportData(spark,dataInfo,destination)`;
      const md5Path = util.md5(tabName);
      this.dispatch('Workbench:add', {
        id: md5Path,
        filename: tabName + '.scala',
        filepath: '',
        // saveAs表示临时脚本，需要关闭或保存时另存
        saveAs: true,
        noLoadCache: true,
        code,
      }, (f) => {
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
      const filename = `${this.$t('message.newNavBar.createdTitle')}_${new Date().getTime()}`;
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
  },
};
</script>
<style src="@assets/styles/sidebar.scss" lang="scss">
</style>
