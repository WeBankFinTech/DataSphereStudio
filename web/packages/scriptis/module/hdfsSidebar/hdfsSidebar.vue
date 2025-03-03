<template>
  <div class="we-side-bar">
    <we-navbar
      :nav-list="navList"
      @on-add="openFileModal(false)"
      @on-refresh="refresh"
      @on-import="openImportToHiveDialog('out')"
      @text-change="setSearchText"/>
    <we-file-tree
      ref="weFileTree"
      :tree="fileTree"
      :before-remove="beforeRemove"
      :before-change="beforeChange"
      :filter-text="searchText"
      :node-props="nodeProps"
      :load-data-fn="loadDataFn"
      :loading="compLoading"
      :highlight-path="highlightPath"
      :is-root-default-open="isRootDefaultOpen"
      :currentNode="currentNode"
      class="we-side-bar-content"
      @on-refresh="refresh"
      @work-bench-click="benchClick"
      @work-bench-edit="benchEdit"
      @work-bench-dblclick="openToTABAction()"
      @work-bench-contextMenu="benchContextMenu"/>
    <we-menu
      ref="treeContextMenu"
      id="hdfs">
      <we-menu-item
        v-if="currentNode.isLeaf"
        @select="openToTABAction()">
        <span>{{ $t('message.scripts.hdfs.contextMenu.openToTab') }}</span><span>Ctrl+Enter</span>
      </we-menu-item>
      <we-menu-item @select="copyPathAction">
        <span>{{ $t('message.scripts.hdfs.contextMenu.copyPath') }}</span><span>Alt+Shift+C</span>
      </we-menu-item>
      <we-menu-item class="ctx-divider"/>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openFileModal(false)">
        <span>{{ $t('message.scripts.hdfs.contextMenu.addCatalog') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openUploadModal">
        <span>{{ $t('message.scripts.constants.upload') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode._level!=1"
        @select="handleEditBefore">
        <span>{{ $t('message.scripts.constants.rename') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode._level!=1"
        @select="openDeleteModal">
        <span>{{ $t('message.scripts.constants.delete') }}</span>
      </we-menu-item>
      <we-menu-item class="ctx-divider"/>
      <we-menu-item
        v-if="currentNode.isLeaf && isVaildType"
        @select="openImportToHiveDialog">
        <span>{{ $t('message.scripts.hdfs.contextMenu.importToHive') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode.isLeaf"
        @select="openExportDialog">
        <span>{{ $t('message.scripts.hdfs.contextMenu.exportToWorkSpace') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="refresh">
        <span>{{ $t('message.scripts.constants.refresh') }}</span>
      </we-menu-item>
    </we-menu>
    <new-dialog
      v-if="timeoutFlag"
      ref="newFile"
      :type="newDialog.type"
      :is-new="newDialog.isNew"
      :node="newDialog.node"
      :is-leaf="newDialog.isLeaf"
      :default-path="newDialog.defaultPath"
      :is-desc-show="false"
      :is-path-show="true"
      :tree="filterTree"
      :load-data-fn="loadDataFn"
      :filter-node="filterNode"
      @add="handleCreate">
    </new-dialog>
    <upload-dialog
      ref="upload"
      :refresh="refresh"/>
    <show-dialog
      v-if="timeoutFlag"
      ref="exportToFile"
      :filter-node="filterNode"
      :path="sharePath"
      :tree="shareTree"
      :load-data-fn="loadShareDataFn"
      :fs-type="fsType"
      :title="$t('message.scripts.hdfs.dialogTitle.exportToWorkSpace')"
      @import="exportToFile"
      @set-node="setNode"/>
    <we-import-to-hive
      ref="impotToHive"
      :width="560"
      :fs-type="fsType"
      :tree="filterTree"
      :load-data-fn="hiveDataLoadFn"
      :db-list="dbList"
      :filter-node="filterToHiveNode"
      @get-hive="getHiveList"
      @get-tables="getHiveTableList"
      @get-content="getFileContent"
      @get-partitions="getPartitions"
      @on-type-change="changeTreeByType"
      @export="exportToHive"/>
    <delete-dialog
      ref="delete"
      :loading="loading"
      @delete="handleDelete"/>
  </div>
</template>
<script>
import { indexOf, isEmpty, cloneDeep, isError, forEach, findIndex, debounce, find } from 'lodash';
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import storage from '@dataspherestudio/shared/common/helper/storage';
import module from './index';
import weFileTree from '@/scriptis/components/fileTree';
import weNavbar from '@dataspherestudio/shared/components/navbar/navbar.vue';
import newDialog from '@/scriptis/components/newDialog';
import uploadDialog from '@/scriptis/components/uploadDialog';
import showDialog from '@dataspherestudio/shared/components/directoryDialog/show.vue';
import weImportToHive from '@/scriptis/components/importToHive';
import deleteDialog from '@dataspherestudio/shared/components/deleteDialog';
const PREFIX = 'hdfs://';
export default {
  name: 'WorkSidebar',
  components: {
    weFileTree,
    weNavbar,
    newDialog,
    uploadDialog,
    showDialog,
    weImportToHive,
    deleteDialog,
  },
  data() {
    return {
      rootPath: '',
      currentNode: {},
      searchText: '',
      nodeProps: {
        children: 'children',
        label: 'name',
        icon: 'icon',
        isLeaf: 'isLeaf',
      },
      fileTree: [],
      filterTree: [],
      // 用于延迟渲染模块，减少请求
      timeoutFlag: false,
      navList: ['search', 'newFile', 'refresh', 'import'],
      sharePath: '',
      shareTree: [],
      loadShareDataFn: () => {},
      fsType: 'hdfs',
      dbList: [],
      hiveComponent: null,
      loading: false,
      compLoading: false,
      treeLoading: false,
      highlightPath: '',
      isRootDefaultOpen: true,
      newDialog: {
        type: '',
        isNew: true,
        node: {},
        isLeaf: false,
        defaultPath: '',
      },
    };
  },
  computed: {
    isVaildType() {
      let isVaild;
      if (this.currentNode && this.currentNode.data) {
        const name = this.currentNode.data.name;
        const reg = ['.xlsx', '.xls', '.csv', '.txt'];
        const tabSuffix = name.substr(name.lastIndexOf('.'), name.length);
        isVaild = indexOf(reg, tabSuffix) !== -1;
      }
      return isVaild;
    },
  },
  mounted() {
    this.initData();
    this.getAcitveTabAndSetHighlight();
  },
  methods: {
    initData() {
      // 取indexedDB缓存
      this.dispatch('IndexedDB:getTree', {
        name: 'hdfsTree',
        cb: (res) => {
          if (!res || res.value.length <= 0) {
            this.compLoading = true;
            this.treeLoading = true;
            this.getRootPath((status) => {
              if (status) {
                this.getTree((tree) => {
                  this.treeLoading = false;
                  this.compLoading = false;
                  if (!tree) return;
                  this.fileTree.push(tree);
                  // 树结构存储到indexedDB
                  this.dispatch('IndexedDB:appendTree', { treeId: 'hdfsTree', value: this.fileTree });
                });
              } else {
                this.treeLoading = false;
                this.compLoading = false;
              }
            });
          } else {
            this.fileTree = res.value;
            this.timeoutFlag = true;
          }
        }
      })
    },
    changeTreeByType(type) {
      if (type === 'share') {
        this.filterTree = cloneDeep(this.fileTree);
        this.hiveDataLoadFn = this.loadDataFn;
      } else if (type === 'hdfs') {
        this.getHdfsTree().then(({ hdfsTree, loadDataFn }) => {
          this.filterTree = cloneDeep(hdfsTree);
          this.hiveDataLoadFn = loadDataFn;
        });
      }
    },
    getRootPath(cb) {
      this.rootPath = storage.get('hdfsRootPath', 'session');
      if (!this.rootPath) {
        api.fetch(`/filesystem/getUserRootPath`, {
          pathType: 'hdfs',
        }, 'get').then((rst) => {
          if (rst.userHDFSRootPath) {
            storage.set('hdfsRootPath', rst.userHDFSRootPath, 'session');
            this.rootPath = rst.userHDFSRootPath;
            cb(true);
          } else {
            this.$Message.warning(this.$t('message.scripts.hdfs.warning.noRootPath'));
            cb(false);
          }
        }).catch(() => {
          cb(false);
        });
      } else {
        cb(true);
      }
    },
    getTree(cb) {
      const timeout = setTimeout(() => {
        this.compLoading = true;
      }, 2000);
      api.fetch(`/filesystem/getDirFileTrees`, {
        path: this.rootPath,
      }, 'get')
        .then((rst) => {
          clearTimeout(timeout);
          this.compLoading = false;
          if (rst) {
            const tree = rst.dirFileTrees;
            cb(tree);
            // 树结构存储到indexedDB
            this.dispatch('IndexedDB:appendTree', { treeId: 'hdfsTree', value: [tree] });
          }
          this.timeoutFlag = true;
        }).catch(() => {
          this.compLoading = false;
          cb(false);
        });
    },
    setSearchText(value) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.scripts.constants.warning.data'));
      this.searchText = value;
    },
    beforeRemove() {

    },
    benchClick(...args) {
      this.currentNode = args[0].node;
      this.$refs.treeContextMenu.close();
    },
    benchContextMenu({ node, ev }) {
      this.currentNode = node;
      this.$refs.treeContextMenu.open(ev);
    },
    benchdbClick({ node }) {
      if (node.isLeaf) {
        this.openToTABAction(node.data);
      }
    },
    openToTABAction(node) {
      const openNode = node || this.currentNode.data;
      const path = openNode.path;
      this.dispatch('Workbench:openFile', {
        path,
        filename: openNode.name,
        type: 'hdfsScript',
        saveAs: true,
      }, () => {

      });
    },
    copyPathAction() {
      util.executeCopy(this.currentNode.data.path);
    },
    openFileModal(isLeaf) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.scripts.constants.warning.data'));
      let node = this.currentNode.data;
      // 这里是对初始化时，用户去点击navbar时，没有currentNode的情况
      // 此时的currentNode.data是个数组
      if (isEmpty(this.currentNode)) {
        this.currentNode = this.$refs.weFileTree.$refs.tree.root;
        node = this.currentNode.data[0];
      }
      this.filterTree = cloneDeep(this.fileTree);
      this.fsType = 'hdfs';
      this.newDialog = {
        type: this.$t('message.scripts.deleteType.catalog'),
        isNew: true,
        node,
        isLeaf,
        defaultPath: node.path,
      };
      this.$refs.newFile.open();
    },
    openUploadModal() {
      const nameList = [];
      if (this.currentNode.data.children) {
        this.currentNode.data.children.forEach((e) => {
          if (e.isLeaf) {
            nameList.push(e.name);
          }
        });
      }
      const result = /^[a-zA-Z]+:\/\//.exec(this.currentNode.data.path) || [];
      this.$refs.upload.open({
        path: this.currentNode.data.path,
        nameList,
        apiPrefix: module.data.API_PATH,
        type: result[0] || PREFIX,
      });
    },
    handleCreate(node) {
      this.handleCreating(node, (flag) => {
        if (flag) {
          if (node.isLeaf) {
            this.openToTABAction(node);
          }
          this.$Message.success(this.$t('message.scripts.constants.success.add'));
          setTimeout(() => {
            this.refresh('new', node.path);
          }, 500);
        }
      });
    },
    handleCreating(node, cb) {
      api.fetch('/filesystem/createNewDir', {
        path: node.path,
      }).then(() => {
        cb(true);
      }).catch(() => {
        cb(false);
      });
    },
    rename(path, oldPath, cb) {
      api.fetch('/filesystem/rename', {
        oldDest: oldPath,
        newDest: path,
      }).then(() => {
        cb(true);
      }).catch(() => {
        cb(false);
      });
    },
    handleEditBefore() {
      this.currentNode.changeEditState(true);
    },
    beforeChange(args, cb) {
      let path = args.node.path;
      path = path.slice(0, path.lastIndexOf('/') + 1) + args.label;
      try {
        this.dispatch('Workbench:checkExist', {
          path,
        }, (flag) => {
          if (flag) {
            cb(false);
            return this.$Message.error(`${this.$t('message.scripts.constants.error.fileExists')}：${path}`);
          }
          api.fetch('/filesystem/rename', {
            oldDest: args.node.path,
            newDest: path,
          }).then(() => {
            cb(true);
            if (this.currentNode && this.currentNode.data) {
              this.currentNode.data.path = path
            }
          }).catch(() => {
            cb(false);
          });
        });
      } catch (error) {
        let errorMsg = error;
        if (isError(error)) {
          errorMsg = error.message;
        }
        this.$Message.error(errorMsg);
        cb(false);
      }
    },
    benchEdit(...args) {
      const { node, oldLabel } = args[0];
      const path = node.data.path.slice(0, node.data.path.lastIndexOf('/') + 1) + node.label;
      const newNode = {
        name: node.label,
        path,
      };
      // 在树上编辑脚本后，更新打开的tab页名称和路径。
      this.dispatch('Workbench:updateTab', {
        newNode,
        findWork: null,
        oldLabel,
      });
      this.$Message.success(this.$t('message.scripts.constants.success.update'));
      setTimeout(() => {
        this.refresh('edit');
      }, 500);
    },
    openDeleteModal() {
      const leaf = this.currentNode.isLeaf;
      const type = leaf ? this.$t('message.scripts.deleteType.file') : this.$t('message.scripts.deleteType.folder');
      this.$refs.delete.open({
        type,
        name: this.currentNode.label,
      });
    },
    handleDelete() {
      if (this.loading) return this.$Message.warning(this.$t('message.scripts.constants.warning.waiting'));
      const path = this.currentNode.data.path;
      this.loading = true;
      this.dispatch('Workbench:deleteDirOrFile', path, () => {
        api.fetch('/filesystem/deleteDirOrFile', {
          path,
        }).then(() => {
          this.$Message.success(this.$t('message.scripts.constants.success.delete'));
          this.refresh('delete');
          this.currentNode.remove();
          this.loading = false;
          this.currentNode = { ...this.$refs.weFileTree.$refs.tree.root };
          this.currentNode.data = { ...this.currentNode.data[0] };
        }).catch(() => {
          this.loading = false;
        });
      });
    },
    lookForChangeNode(path, node, type) {
      const NODESETTING = {
        tree: {
          name: 'name',
          children: 'children',
        },
        node: {
          name: 'label',
          children: 'computedNodes',
        },
      };
      const set = NODESETTING[type];
      const _tran = (tranData, index, child) => {
        return tranData[child][index];
      };
      let nodePathList = path ? path.split('/') : '';
      let findNode = node;
      forEach(nodePathList, (o) => {
        const i = findIndex(findNode[set.children], (item) => item[set.name] === o);
        if (i >= 0) {
          findNode = _tran(findNode, i, set.children);
          if (type === 'node') {
            // 层级打开expanded
            findNode.expanded = true;
          }
        }
      });
      return findNode;
    },
    hiveDataLoadFn(node, cb) {
      this.loadDataFn(node, cb);
    },
    loadDataFn(node, cb) {
      this.treeLoading = true;
      const timeout = setTimeout(() => {
        this.compLoading = true;
      }, 2000);
      api.fetch(`/filesystem/getDirFileTrees`, {
        path: node.data.path,
      }, 'get')
        .then((rst) => {
          clearTimeout(timeout);
          this.compLoading = false;
          this.treeLoading = false;
          const tree = rst.dirFileTrees.children;
          cb(tree);
          this.$nextTick(() => {
            // 树结构存储到indexedDB
            this.dispatch('IndexedDB:appendTree', { treeId: 'hdfsTree', value: this.fileTree });
          });
        }).catch(() => {
          clearTimeout(timeout);
          this.compLoading = false;
          this.treeLoading = false;
        });
    },
    filterNode(node) {
      return !node.isLeaf;
    },
    filterToHiveNode(node) {
      if (!node.isLeaf) return true;
      const tabSuffix = node.label.substr(node.label.lastIndexOf('.'), node.label.length);
      const reg = ['.xlsx', '.xls', '.csv', '.txt'];
      const isVaild = indexOf(reg, tabSuffix) !== -1;
      return isVaild;
    },
    setNode(node, fsType) {
      if (fsType === 'hdfs') {
        const newFile = this.$refs.newFile;
        if (newFile) {
          newFile.setting.path = node.path;
          newFile.node = node;
        }
      } else {
        this.sharePath = node.path;
      }
    },
    refresh(type, path) {
      // 存储当前修改的树文件夹数据
      const getTreeData = () => {
        if (this.treeLoading) return this.$Message.warning(this.$t('message.scripts.constants.warning.data'));
        this.treeLoading = true;

        // const root = this.rootPath.slice(this.rootPath.indexOf('/') + 2, this.rootPath.length - 1);
        let nodePath = isEmpty(this.currentNode) ? this.rootPath : this.currentNode.data.path;
        if (this.currentNode.isLeaf || type === 'edit' || type === 'delete') {
          nodePath = this.currentNode.data.parentPath;
        } else if (type === 'new' && path) {
          nodePath = path.slice(0, path.lastIndexOf('/'));
        }
        if (isEmpty(this.fileTree)) return this.initData();
        const timeout = setTimeout(() => {
          this.compLoading = true;
        }, 2000);
        api.fetch(`/filesystem/getDirFileTrees`, {
          path: nodePath,
        }, 'get')
          .then((rst) => {
            clearTimeout(timeout);
            this.compLoading = false;
            this.treeLoading = false;
            if (nodePath !== this.fileTree[0].path) {
              let parent = this.fileTree && this.fileTree[0];
              const dropRootPath = nodePath.replace(this.rootPath, '');
              parent = this.lookForChangeNode(dropRootPath, parent, 'tree');
              this.$set(parent, 'children', rst.dirFileTrees.children);
              // 加载完数据后等待重新渲染，拿到渲染后的node
              this.$nextTick(() => {
                let node = this.$refs.weFileTree.$refs.tree.root.computedNodes[0];
                node = this.lookForChangeNode(nodePath, node, 'node');
                node.expanded = true;
              });
            } else {
              this.$set(this.fileTree[0], 'children', rst.dirFileTrees.children);
            }
            // 树结构存储到indexedDB
            this.dispatch('IndexedDB:appendTree', { treeId: 'hdfsTree', value: this.fileTree });
            if (this.searchText) {
              this.$nextTick(() => {
                this.$refs.weFileTree.filter();
              })
            }
          })
          .catch(() => {
            clearTimeout(timeout);
            this.compLoading = false;
            this.treeLoading = false;
          });
      };
      if (this.rootPath) return getTreeData();
      this.getRootPath((status) => {
        if (status) {
          getTreeData();
        } else {
          this.compLoading = false;
          this.treeLoading = false;
        }
      });
    },
    openExportDialog() {
      if (this.shareTree && this.shareTree.length) {
        this.fsType = 'share';
        this.$refs.exportToFile.open();
      } else {
        this.dispatch('WorkSidebar:showTree', (f) => {
          f.getRootPath((status) => {
            if (status) {
              f.getTree((tree) => {
                if (tree) {
                  this.shareTree.push(tree);
                  this.loadShareDataFn = f.loadDataFn;
                  // 未发现使用地方待验证
                  // storage.set('shareTree', this.shareTree, 'session');
                }
              });
            }
          });
          this.fsType = 'share';
          this.$refs.exportToFile.open();
        });
      }
    },
    exportToFile(node) {
      const name = `new_stor_${Date.now()}.out`;
      const code = `from ${this.currentNode.data.path} to ${node.path}/${this.currentNode.data.name}`;
      const md5Path = util.md5(name);
      this.dispatch('Workbench:add', {
        id: md5Path,
        filename: name,
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
    },
    'HdfsSidebar:setHighLight': debounce(function(work) {
      if (!work) return this.highlightPath = '';
      const path = work.filepath;
      const userName = this.getUserName();
      const highLightList = path.split('/');
      const index = highLightList.indexOf(userName);
      highLightList.splice(0, index);
      this.highlightPath = highLightList.join('/');
    }, 500),
    getAcitveTabAndSetHighlight() {
      this.dispatch('IndexedDB:getTabs', (worklist) => {
        if (isEmpty(worklist)) return;
        const activedWork = find(worklist, (work) => work.actived);
        if (activedWork && activedWork.type === 'hdfsScript') {
          const method = 'HdfsSidebar:setHighLight';
          this[method](activedWork);
        }
      });
    },
    openImportToHiveDialog(type) {
      const path = type === 'out' ? '' : this.currentNode.data.path;
      this.fsType = 'hdfs';
      this.filterTree = [];
      this.$nextTick(() => {
        this.filterTree = cloneDeep(this.fileTree);
        this.$refs.impotToHive.open(path);
      });
    },
    getHiveList(cb) {
      this.dispatch('HiveSidebar:showHive', {}, (f) => {
        const methodName = 'HiveSidebar:getDatabase';
        this.hiveComponent = f;
        f[methodName]('', (dbList) => {
          this.dbList = dbList;
          cb(dbList);
        });
      });
    },
    getHiveTableList(db) {
      const methodName = 'HiveSidebar:getTables';
      this.hiveComponent[methodName]({ item: db }, (tables) => {
        const curDb = find(this.dbList, (item) => {
          return item.name === db.name;
        });
        curDb.children = tables;
      });
    },
    getPartitions(tb, cb) {
      const methodName = 'HiveSidebar:getTablePartitions';
      this.hiveComponent[methodName]({ item: tb }, (parts) => {
        cb(parts);
      });
    },
    getFileContent(option, type, cb) {
      let separator = option.separator;
      if (option.separator === '\\t') {
        separator = '%5Ct';
      } else if (option.separator === '%20') {
        separator = ' ';
      } else if (option.separator === '|') {
        separator = '%5C%7C';
      }
      const encoding = type ? '' : option.chartset;
      const fieldDelimiter = type ? '' : separator;
      let escapeQuotes = false;
      let quote = '';
      if (option.quote) {
        escapeQuotes = true;
        quote = option.quote;
      }
      const pathSuffix = option.exportPath.substr(option.exportPath.lastIndexOf('.'), option.exportPath.length);
      const apiName = pathSuffix === '.txt' ? 'formate' : 'getSheetInfo';
      const url = `/filesystem/${apiName}?path=${option.exportPath}&encoding=${encoding}&fieldDelimiter=${fieldDelimiter}&hasHeader=${option.isHasHeader}&escapeQuotes=${escapeQuotes}&quote=${quote}`;
      api.fetch(url, {}, {
        method: 'get',
        timeout: '600000',
      }).then((rst) => {
        if(pathSuffix === '.txt') {
          cb(rst.formate);
        } else {
          cb(rst.sheetInfo);
        }
      }).catch(() => {
        cb(false);
      });
    },
    exportToHive(args) {
      const { firstStep, secondStep, isXls, columns, whetherRepeat } = args;
      const { duplicateName, partTable, duplicateValue } = whetherRepeat;
      let escapeQuotes = false;
      let quote = '';
      let isPartition = false;
      let importData = true;
      /**
             * 场景
             * case1:导入分区表名重复  选择分区也重复  不选复写  importdata true
             *                                      勾选复写  importdata true
             * case2:导入分区表名重复  选择分区不重复           importdata true
             * case3:导入非分区表名重复  不选复写              importdata true
             *                         选复写  不新增分区     imprtdata false
             * case4:导入表名不重复  没勾选新增分区            importData false
             *                      勾选新增分区              importData false
             */
      if (!partTable && secondStep.isOverwrite && duplicateName && !duplicateValue) {
        importData = false;
      } else if (!duplicateName) {
        importData = false;
      }
      if (secondStep.partition && secondStep.partitionValue) {
        isPartition = true;
      }
      let separator = firstStep.separator;
      if (firstStep.separator === '%20') {
        separator = ' ';
      } else if (firstStep.separator === '|') {
        separator = '\\|';
      }
      if (firstStep.quote) {
        escapeQuotes = true;
        quote = firstStep.quote;
      }
      const path = firstStep.exportPath.slice(7, firstStep.exportPath.length);
      const source = {
        path,
        pathType: firstStep.type,
        encoding: isXls ? '' : firstStep.chartset,
        fieldDelimiter: isXls ? '' : separator,
        hasHeader: firstStep.isHasHeader,
        sheet: secondStep.moreSheet.toString(),
        quote,
        escapeQuotes,
      };
      const destination = {
        database: secondStep.dbName,
        tableName: secondStep.tbName,
        importData,
        isPartition,
        partition: secondStep.partition,
        partitionValue: secondStep.partitionValue,
        isOverwrite: secondStep.isOverwrite,
        columns,
      };
      const name = `${secondStep.dbName}.${secondStep.tbName}`;
      const tabName = `import_to_${name}_${Date.now()}`;
      const code = `val destination = """${JSON.stringify(destination)}"""\nval source = """${JSON.stringify(source)}"""\norg.apache.linkis.engineplugin.spark.imexport.LoadData.loadDataToTable(spark,source,destination)`;
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
          return this.$refs.impotToHive.close();
        }
        this.$refs.impotToHive.close();
        this.$nextTick(() => {
          this.dispatch('Workbench:run', {
            id: md5Path,
            type: 'storage',
            executionCode: {
              source,
              destination,
            },
            backgroundType: 'load',
          }, () => {
            this.$refs.impotToHive.loading = false;
          });
        });
      });
    },
  },
};
</script>
<style src="../../assets/styles/sidebar.scss" lang="scss"></style>
