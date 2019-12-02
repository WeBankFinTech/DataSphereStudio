<template>
  <div class="we-side-bar">
    <we-navbar
      :nav-list="navList"
      @on-add="openFileModal(false)"
      @on-refresh="refresh"
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
      class="we-side-bar-content"
      @on-refresh="refresh"
      @work-bench-click="benchClick"
      @work-bench-edit="benchEdit"
      @work-bench-dblclick="benchdbClick"
      @work-bench-contextMenu="benchContextMenu"/>
    <we-menu
      ref="treeContextMenu"
      id="file">
      <we-menu-item
        v-if="currentNode.isLeaf"
        @select="openToTABAction()">
        <span>{{ $t('message.workSpace.contextMenu.openToTab') }}</span><span>Ctrl+Enter</span>
      </we-menu-item>
      <we-menu-item @select="copyPathAction">
        <span>{{ $t('message.workSpace.contextMenu.copyPath') }}</span><span>Alt+Shift+C</span>
      </we-menu-item>
      <we-menu-item class="ctx-divider"/>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openFileModal(false)">
        <span>{{ $t('message.workSpace.contextMenu.addCatalog') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openFileModal(true)">
        <span>{{ $t('message.workSpace.contextMenu.addScript') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openUploadModal">
        <span>{{ $t('message.constants.upload') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode._level!=1"
        @select="handleEditBefore">
        <span>{{ $t('message.constants.rename') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode._level!=1"
        @select="openDeleteModal">
        <span>{{ $t('message.constants.delete') }}</span>
      </we-menu-item>
      <we-menu-item class="ctx-divider"/>
      <!-- <we-menu-item
        v-if="currentNode.isLeaf && isVaildType"
        @select="openImportToHiveDialog">
        <span>{{ $t('message.workSpace.contextMenu.importToHive') }}</span>
      </we-menu-item> -->
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="refresh">
        <span>{{ $t('message.constants.refresh') }}</span>
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
      :script-type="newDialog.scriptType"
      :tree="filterTree"
      :load-data-fn="loadDataFn"
      :filter-node="filterNode"
      @add="handleCreate">
    </new-dialog>
    <upload-dialog
      ref="upload"
      :refresh="refresh"/>
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
import api from '@/js/service/api';
import util from '@/js/util';
import storage from '@/js/helper/storage';
import weFileTree from '@js/component/fileTree';
import weNavbar from '@js/component/navbar/navbar.vue';
import newDialog from '@js/component/newDialog';
import uploadDialog from '@/js/component/uploadDialog';
import weImportToHive from '@js/component/importToHive';
import deleteDialog from '@js/component/deleteDialog';
const PREFIX = 'file://';
export default {
  name: 'WorkSidebar',
  components: {
    weFileTree,
    weNavbar,
    newDialog,
    uploadDialog,
    weImportToHive,
    deleteDialog,
  },
  props: {
    type: {
      type: String,
      default: 'file',
    },
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
      navList: ['search', 'newFile', 'refresh'],
      hdfsTree: [],
      hdfsPath: '',
      loadHdfsDataFn: () => {},
      fsType: 'share',
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
        scriptType: [],
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
        const tabSuffix = name.substr(
          name.lastIndexOf('.'),
          name.length
        );
        isVaild = indexOf(reg, tabSuffix) !== -1;
      }
      return isVaild;
    },
  },
  mounted() {
    this.initData();
  },
  methods: {
    initData() {
      const tmpTree = storage.get('scriptTree', 'SESSION');
      if (!tmpTree || isEmpty(tmpTree)) {
        this.treeLoading = true;
        this.compLoading = true;
        this.getRootPath((status) => {
          if (status) {
            this.getTree((tree) => {
              this.treeLoading = false;
              if (!tree) return;
              this.fileTree.push(tree);
              storage.set('scriptTree', this.fileTree, 'SESSION');
              this.getAcitveTabAndSetHighlight();
            });
          } else {
            this.treeLoading = false;
            this.compLoading = false;
          }
        });
      } else {
        this.fileTree = tmpTree;
        this.timeoutFlag = true;
        this.getAcitveTabAndSetHighlight();
      }
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
      this.rootPath = storage.get('shareRootPath', 'SESSION');
      if (!this.rootPath) {
        api.fetch(`/filesystem/getUserRootPath`, {
          pathType: 'file',
        }, 'get').then((rst) => {
          if (rst.userLocalRootPath) {
            storage.set('shareRootPath', rst.userLocalRootPath, 'SESSION');
            this.rootPath = rst.userLocalRootPath;
            cb(true);
          } else {
            this.$Message.warning(this.$t('message.workSpace.warning.getRootPath'));
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
            storage.set('scriptTree', [tree], 'SESSION');
          }
          this.timeoutFlag = true;
        }).catch(() => {
          clearTimeout(timeout);
          this.compLoading = false;
          cb(false);
        });
    },
    setSearchText(value) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.constants.warning.data'));
      this.searchText = value;
    },
    beforeRemove() {},
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
      }, () => {});
    },
    copyPathAction() {
      util.executeCopy(this.currentNode.data.path);
    },
    openFileModal(isLeaf) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.constants.warning.data'));
      let node = this.currentNode.data;
      // 这里是对初始化时，用户去点击navbar时，没有currentNode的情况
      // 此时的currentNode.data是个数组
      if (isEmpty(this.currentNode)) {
        this.currentNode = this.$refs.weFileTree.$refs.tree.root;
        node = this.currentNode.data[0];
      }
      this.filterTree = cloneDeep(this.fileTree);
      this.fsType = 'share';
      this.newDialog = {
        type: isLeaf ? this.$t('message.deleteType.script') : this.$t('message.deleteType.catalog'),
        isNew: true,
        node,
        isLeaf,
        scriptType: this.getSupportModes().filter((item) => item.isCanBeNew),
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
      this.$refs.upload.open({
        path: this.currentNode.data.path,
        nameList,
        apiPrefix: this.fesx.API_PATH,
        type: PREFIX,
      });
    },
    handleCreate(node) {
      this.handleCreating(node, (flag) => {
        if (flag) {
          if (node.isLeaf) {
            this.openToTABAction(node);
          }
          this.$Message.success(this.$t('message.constants.success.add'));
          setTimeout(() => {
            this.refresh('new', node.path);
          }, 500);
        }
      });
    },
    handleCreating(node, cb) {
      const url = node.isLeaf
        ? '/filesystem/createNewFile'
        : '/filesystem/createNewDir';
      api.fetch(url, {
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
            return this.$Message.error(`${this.$t('message.constants.error.fileExists')}：${path}`);
          }
          api.fetch('/filesystem/rename', {
            oldDest: args.node.path,
            newDest: path,
          }).then(() => {
            cb(true);
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
      this.$Message.success(this.$t('message.constants.success.update'));
      setTimeout(() => {
        this.refresh('edit');
      }, 500);
    },
    openDeleteModal() {
      const leaf = this.currentNode.isLeaf;
      const type = leaf ? this.$t('message.deleteType.file') : this.$t('message.deleteType.folder');
      this.$refs.delete.open({
        type,
        name: this.currentNode.label,
      });
    },
    handleDelete() {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      const path = this.currentNode.data.path;
      this.dispatch('Workbench:deleteDirOrFile', path, (flag) => {
        // 如果是没保存的tab页，要用户保存之后再删除，
        // 否则调用接口，文件在后台删除，前台保存会报错！
        if (flag === 'save' || flag === 'none') {
          this.loading = true;
          api.fetch('/filesystem/deleteDirOrFile', {
            path,
          }).then(() => {
            this.loading = false;
            this.$Message.success(this.$t('message.constants.success.delete'));
            this.refresh('delete');
            this.currentNode.remove();
          }).catch(() => {
            this.loading = false;
          });
        } else {
          this.$refs.delete.close();
          this.$Message.warning(this.$t('message.workSpace.warning.scriptChanged'));
        }
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
        const i = findIndex(findNode[set.children], (item) => {
          return item[set.name] === o;
        });
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
      api.fetch(
        `/filesystem/getDirFileTrees`, {
          path: node.data.path,
        },
        'get'
      ).then((rst) => {
        clearTimeout(timeout);
        this.compLoading = false;
        this.treeLoading = false;
        const tree = rst.dirFileTrees.children;
        cb(tree);
        this.$nextTick(() => {
          storage.set('scriptTree', this.fileTree, 'SESSION');
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
      const tabSuffix = node.label.substr(
        node.label.lastIndexOf('.'),
        node.label.length
      );
      const reg = ['.xlsx', '.xls', '.csv', '.txt'];
      const isVaild = indexOf(reg, tabSuffix) !== -1;
      return isVaild;
    },
    setNode(node, fsType) {
      if (fsType === 'share') {
        const newFile = this.$refs.newFile;
        if (newFile) {
          newFile.setting.path = node.path;
          newFile.node = node;
        }
      } else {
        this.hdfsPath = node.path;
      }
    },
    refresh(type, path) {
      // 存储当前修改的树文件夹数据
      const getTreeData = () => {
        if (this.treeLoading) return this.$Message.warning(this.$t('message.constants.warning.data'));
        if (isEmpty(this.fileTree)) return this.initData();
        this.treeLoading = true;
        this.compLoading = true;
        // const root = this.rootPath.slice(this.rootPath.indexOf('/') + 2, this.rootPath.length - 1);
        // 这里不能传''，后台没有传空的逻辑，如果为空时传根目录
        let nodePath = isEmpty(this.currentNode) ? this.rootPath : this.currentNode.data.path;
        if (this.currentNode.isLeaf || type === 'edit' || type === 'delete') {
          // 如果是文件、编辑或删除的时候，要请求上一级文件夹的数据
          nodePath = this.currentNode.data.parentPath;
        } else if (type === 'new' && path) {
          nodePath = path.slice(0, path.lastIndexOf('/'));
        }
        const timeout = setTimeout(() => {
          this.compLoading = true;
        }, 2000);
        api.fetch(`/filesystem/getDirFileTrees`, {
          path: nodePath,
        }, 'get').then((rst) => {
          clearTimeout(timeout);
          this.compLoading = false;
          this.treeLoading = false;
          // 非根目录的逻辑
          if (nodePath !== this.fileTree[0].path) {
            let parent = this.fileTree && this.fileTree[0];
            const dropRootPath = nodePath.replace(this.rootPath, '');
            parent = this.lookForChangeNode(dropRootPath, parent, 'tree');
            this.$set(parent, 'children', rst.dirFileTrees.children);
            // 加载完数据后等待重新渲染，拿到渲染后的node
            this.$nextTick(() => {
              let node = this.$refs.weFileTree.$refs.tree.root
                .computedNodes[0];
              node = this.lookForChangeNode(dropRootPath, node, 'node');
              node.expanded = true;
            });
          } else {
            this.$set(
              this.fileTree[0],
              'children',
              rst.dirFileTrees.children
            );
          }
          storage.set('scriptTree', this.fileTree, 'SESSION');
        }).catch(() => {
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
    async getHdfsTree() {
      if (this.hdfsTree.length) {
        return {
          hdfsTree: this.hdfsTree,
          loadDataFn: this.loadHdfsDataFn,
        };
      }
      return new Promise((resolve) => {
        this.dispatch('HdfsSidebar:showTree', (f) => {
          f.getRootPath((status) => {
            if (status) {
              f.getTree((tree) => {
                if (tree) {
                  this.hdfsTree = [tree];
                  this.loadHdfsDataFn = f.loadDataFn;
                  storage.set(
                    'hdfsTree',
                    this.hdfsTree,
                    'SESSION'
                  );
                  resolve({
                    hdfsTree: this.hdfsTree,
                    loadDataFn: this.loadHdfsDataFn,
                  });
                }
              });
            }
          });
        });
      });
    },
    revealInSideBar(work) {
      if (!work || !work.filepath) {
        return;
      }
      // 获得父级文件路径数组
      let fileDirs = work.filepath.replace(this.rootPath, '').split('/');
      fileDirs.pop();
      fileDirs = fileDirs.filter((item) => item)
        .map((item, index) => {
          return item ? this.rootPath + fileDirs.slice(0, index + 1).join('/') : false;
        });
      // 遍历文件树查找需要加载数据的路径
      let needLoadDirs = [];
      fileDirs.forEach((item, index) => {
        const travers = (treeNode) => {
          treeNode.forEach((node) => {
            if (node.path === item && !node.children) {
              needLoadDirs = fileDirs.slice(index, fileDirs.length);
              return;
            }
            if (node.children) {
              travers(node.children);
            }
          });
        };
        travers(this.fileTree);
      });

      // 依次向下展开work.filepath相应节点, 需要展开的路径：needLoadDirs
      const expandFileDir = (dirs) => {
        let index = 0;
        const expand = (index) => {
          if (index < dirs.length) {
            const dirPath = dirs[index++];
            const travers = (treeNode) => {
              treeNode.forEach((node) => {
                if (node.path === dirPath) {
                  node.expanded = true;
                  return;
                }
                if (node.children) {
                  travers(node.children);
                }
              });
            };
            travers(this.fileTree);
            this.$nextTick(() => {
              expand(index);
            });
          }
        };
        expand(index);
      };

      // 没有children数据，请求加载目录数据之后展开目录，已加载数据则直接展开
      if (needLoadDirs.length > 0) {
        const loadDirPromises = needLoadDirs.map((path) => {
          return api.fetch(`/filesystem/getDirFileTrees`, {
            path: path,
          }, 'get').then((res) => res.dirFileTrees);
        });

        Promise.all(loadDirPromises).then((res) => {
          res.reverse();
          const reducer = (accumulator, currentValue) => {
            currentValue.children.forEach((item) => {
              if (item.path === accumulator.path) {
                item.children = accumulator.children;
              }
              item.expanded = false;
            });
            currentValue.expanded = false;
            return currentValue;
          };
          let data = res.reduce(reducer);
          // 更新树数据源
          const travers = (treeNode) => {
            treeNode.forEach((node) => {
              if (node.path === data.path) {
                node.children = data.children;
                return;
              }
              if (node.children) {
                travers(node.children);
              }
            });
          };
          travers(this.fileTree);
          expandFileDir(needLoadDirs);
        });
      } else {
        // 展开work.filepath相应节点,路径上节点统一expanded初始化false
        const travers = (treeNode) => {
          treeNode.forEach((node) => {
            if (fileDirs.indexOf(node.path) > -1) {
              node.expanded = false;
            }
            if (node.children) {
              travers(node.children);
            }
          });
        };
        travers(this.fileTree);
        this.$nextTick(() => {
          expandFileDir(fileDirs);
        });
      }
    },
    'WorkSidebar:revealInSideBar'(work) {
      this.getRootPath((status) => {
        if (status) {
          this.revealInSideBar(work);
        }
      });
    },
    'WorkSidebar:setHighLight': debounce(function(work) {
      if (!work) return this.highlightPath = '';
      const path = work.filepath;
      const userName = this.getUserName();
      const highLightList = path.split('/');
      const index = highLightList.indexOf(userName);
      highLightList.splice(0, index);
      // 树组件根据highlightPath区高亮文件树
      this.highlightPath = highLightList.join('/');
    }, 500),
    // 在切换回工作空间的时候，去判断当前acived的tab是否需要高亮
    getAcitveTabAndSetHighlight() {
      this.dispatch('IndexedDB:getTabs', (worklist) => {
        if (isEmpty(worklist)) return;
        const activedWork = find(worklist, (work) => work.actived);
        if (activedWork && activedWork.type === 'workspaceScript') {
          let method = 'WorkSidebar:setHighLight';
          this[method](activedWork);
          method = 'WorkSidebar:revealInSideBar';
          this[method](activedWork);
        }
      });
    },
    openImportToHiveDialog(type) {
      const path = type === 'out' ? '' : this.currentNode.data.path;
      this.fsType = 'share';
      // 用于刷新过滤规则
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
    getHiveTableList(db, cb) {
      const methodName = 'HiveSidebar:getTables';
      this.hiveComponent[methodName]({ item: db }, (tables) => {
        const curDb = find(this.dbList, (item) => {
          return item.name === db.name;
        });
        curDb.children = tables;
        cb && cb();
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
      }
      const encoding = type ? '' : option.chartset;
      const fieldDelimiter = type ? '' : separator;
      let escapeQuotes = false;
      let quote = '';
      if (option.quote) {
        escapeQuotes = true;
        quote = option.quote;
      }
      const url = `/filesystem/formate?path=${option.exportPath}&encoding=${encoding}&fieldDelimiter=${fieldDelimiter}&hasHeader=${option.isHasHeader}&escapeQuotes=${escapeQuotes}&quote=${quote}`;
      api.fetch(url, {}, {
        method: 'get',
        timeout: '600000',
      }).then((rst) => {
        cb(rst.formate);
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
      const separator = firstStep.separator === '%20' ? ' ' : firstStep.separator;
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
      const tabName = `import_${secondStep.tbName}_to_${name}`;
      const code = `val destination = """${JSON.stringify(destination)}"""\nval source = """${JSON.stringify(source)}"""\ncom.webank.bdp.dataworkcloud.engine.imexport.LoadData.loadDataToTable(spark,source,destination)`;
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
  }
};
</script>
<style src="@assets/styles/sidebar.scss" lang="scss"></style>
