<template>
  <div class="we-side-bar">
    <we-navbar
      :nav-list="navList"
      @on-add="openFileModal(false)"
      @on-refresh="refresh"
      @text-change="setSearchText"
      @on-import="openImportToHiveDialog('out')" />
    <we-file-tree
      ref="weFileTree"
      :tree="fileTree"
      :before-remove="beforeRemove"
      :before-change="beforeChange"
      :filter-text="searchText"
      :node-props="nodeProps"
      :load-data-fn="loadDataFn"
      :loading="treeLoading"
      :highlight-path="highlightPath"
      :is-root-default-open="isRootDefaultOpen"
      :currentNode="currentNode"
      class="we-side-bar-content"
      @on-refresh="refresh"
      @work-bench-click="benchClick"
      @work-bench-edit="benchEdit"
      @work-bench-dblclick="benchdbClick"
      @work-bench-contextMenu="benchContextMenu"/>
    <div class="we-file-tree is-empty" :style="{height:'100%',padding:'10px'}"
      v-if="!(fileTree && fileTree.length) && treeLoading === 0">
      <div>{{ $t('message.scripts.errorclick') }}<a @click="refresh">{{ $t('message.common.refresh') }}</a>{{ $t('message.scripts.retry') }}</div>
    </div>
    <we-menu
      ref="treeContextMenu"
      id="file">
      <we-menu-item
        v-if="currentNode.isLeaf"
        @select="openToTABAction()">
        <span>{{ $t('message.scripts.contextMenu.openToTab') }}</span><span>Ctrl+Enter</span>
      </we-menu-item>
      <we-menu-item @select="copyPathAction">
        <span>{{ $t('message.scripts.contextMenu.copyPath') }}</span><span>Alt+Shift+C</span>
      </we-menu-item>
      <we-menu-item class="ctx-divider"/>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openFileModal(false)">
        <span>{{ $t('message.scripts.contextMenu.addCatalog') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openFileModal(true)">
        <span>{{ $t('message.scripts.contextMenu.addScript') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="!currentNode.isLeaf"
        @select="openUploadModal">
        <span>{{ $t('message.scripts.constants.upload') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode.isLeaf"
        @select="handleMove">
        <span>{{ $t('message.scripts.contextMenu.move') }}</span>
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
        <span>{{ $t('message.scripts.contextMenu.importToHive') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode.isLeaf"
        @select="openImportDialog">
        <span>{{ $t('message.scripts.contextMenu.importToHdfs') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="currentNode.isLeaf"
        @select="copyName">
        <span>{{ $t('message.scripts.contextMenu.copyscript') }}</span>
      </we-menu-item>
      <we-menu-item
        v-if="!currentNode.isLeaf && show"
        @select="stickScript">
        <span>{{ $t('message.scripts.contextMenu.stickscript') }}</span>
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
      :script-type="newDialog.scriptType"
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
      ref="importToHdfs"
      :filter-node="filterNode"
      :path="hdfsPath"
      :tree="hdfsTree"
      :load-data-fn="loadHdfsDataFn"
      :fs-type="fsType"
      :title="$t('message.scripts.contextMenu.importToHdfs')"
      @import="importToHdfs"
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
    <move
      ref="moveFile"
      :node="newDialog.node"
      :is-leaf="newDialog.isLeaf"
      :default-path="newDialog.defaultPath"
      :tree="filterTree"
      :load-data-fn="loadDataFn"
      :filter-node="filterNode"
      @update="handleMoveUpdate" />
  </div>
</template>
<script>
import { indexOf, isEmpty, cloneDeep, isError, forEach, findIndex, debounce, find, isArray } from 'lodash';
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import storage from '@dataspherestudio/shared/common/helper/storage';
import weFileTree from '@/scriptis/components/fileTree';
import weNavbar from '@dataspherestudio/shared/components/navbar/navbar.vue';
import newDialog from '@/scriptis/components/newDialog';
import uploadDialog from '@/scriptis/components/uploadDialog';
import showDialog from '@dataspherestudio/shared/components/directoryDialog/show.vue';
import weImportToHive from '@/scriptis/components/importToHive';
import deleteDialog from '@dataspherestudio/shared/components/deleteDialog';
import mixin from '@dataspherestudio/shared/common/service/mixin';
import module from './index';
import move from './move';
const PREFIX = 'file://';
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
    move
  },
  mixins: [mixin],
  props: {
    type: {
      type: String,
      default: 'file',
    },
  },
  data() {
    return {
      path: '',
      newName: '',
      show: false,
      rootPath: '',
      currentNode: {
        // isEditState: false
      },
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
      hdfsTree: [],
      hdfsPath: '',
      loadHdfsDataFn: () => {},
      fsType: 'share',
      dbList: [],
      hiveComponent: null,
      loading: false, // delete loading
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
      // 取indexedDB缓存
      this.dispatch('IndexedDB:getTree', {
        name: 'scriptTree',
        cb: (res) => {
          if (!res || res.value.length <= 0) {
            this.treeLoading = true;
            this.getRootPath((status) => {
              if (status) {
                this.getTree((tree) => {
                  this.treeLoading = false;
                  if (!tree) return;
                  this.fileTree.push(tree);
                  // 树结构存储到indexedDB
                  this.dispatch('IndexedDB:appendTree', { treeId: 'scriptTree', value: this.fileTree });
                  this.getAcitveTabAndSetHighlight();
                });
              } else {
                this.treeLoading = false;
              }
            });
          } else {
            this.fileTree = res.value;
            this.timeoutFlag = true;
            this.getAcitveTabAndSetHighlight();
          }
        }
      })
    },
    changeTreeByType(type) {
      if (type === 'share') {
        this.filterTree = cloneDeep(this.fileTree);
        this.filterTree[0].children = this.filterTree[0].children.filter( function (node) {
          if (!node.isLeaf) return true;
          const tabSuffix = node.name.substr(
            node.name.lastIndexOf('.'),
            node.name.length
          );
          const reg = ['.xlsx', '.xls', '.csv', '.txt'];
          const isVaild = indexOf(reg, tabSuffix) !== -1;
          return isVaild;
        })
        this.hiveDataLoadFn = this.loadDataFn;
      } else if (type === 'hdfs') {
        this.getHdfsTree().then(({ hdfsTree, loadDataFn }) => {
          this.filterTree = cloneDeep(hdfsTree);
          this.hiveDataLoadFn = loadDataFn;
        });
      }
    },
    getRootPath(cb) {
      this.rootPath = storage.get('shareRootPath', 'session');
      if (!this.rootPath) {
        api.fetch(`/filesystem/getUserRootPath`, {
          pathType: 'file',
        }, 'get').then((rst) => {
          if (rst.userLocalRootPath) {
            storage.set('shareRootPath', rst.userLocalRootPath, 'session');
            this.rootPath = rst.userLocalRootPath;
            cb(true);
          } else {
            this.$Message.warning(this.$t('message.scripts.warning.getRootPath'));
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
      api.fetch(`/filesystem/getDirFileTrees`, {
        path: this.rootPath,
      }, 'get')
        .then((rst) => {
          if (rst) {
            const tree = rst.dirFileTrees;
            cb(tree);
            this.dispatch('IndexedDB:appendTree', { treeId: 'scriptTree', value: [tree] });
          }
          this.timeoutFlag = true;
        }).catch(() => {
          this.treeLoading = 0
          cb(false);
        });
    },
    setSearchText(value) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.scripts.constants.warning.data'));
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
      const source = openNode.copy ? this.path : '';
      this.dispatch('Workbench:openFile', {
        source,
        path,
        filename: openNode.name,
      }, () => {});
    },
    copyPathAction() {
      util.executeCopy(this.currentNode.data.path);
    },
    copyName() {
      let nodeName = this.currentNode.data.name;
      let index = nodeName.lastIndexOf(".")
      let typeSuffix = nodeName.substring(index+1,nodeName.length);
      let typeArr = ['sql','hql','py','scala','python']
      if(typeArr.indexOf(typeSuffix) === -1){
        this.$Message.warning(this.$t('message.scripts.constants.success.prohibit'));
        return;
      }
      let insertStr = "_cp.";
      this.path = this.currentNode.data.path;
      this.newName = nodeName.replace(/\./g, insertStr);
      this.show = true;
    },
    stickScript() {
      let arr = [];
      let name = "";
      for (var i = 0; i < this.currentNode.data.children.length; i++) {
        arr.push(this.currentNode.data.children[i].name);
      }

      if (arr.indexOf(this.newName) > -1) {
        for (let i = 1; ; i++) {
          name = this.newName.replace(/\./g, `_${i}.`);
          if (arr.indexOf(name) === -1) {
            break;
          }
        }
      } else {
        name = this.newName;
      }
      const path = `${this.currentNode.data.path}/${name}`;
      const node = {
        name,
        path: path,
        businessType: "",
        description: "",
        isLeaf: true,
        copy: true,
        type: "脚本文件"
      };
      this.handleCreate(node);
    },
    openFileModal(isLeaf) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.scripts.constants.warning.data'));
      let node = isArray(this.currentNode.data) ? this.currentNode.data[0] : this.currentNode.data;
      // 这里是对初始化时，用户去点击navbar时，没有currentNode的情况
      // 此时的currentNode.data是个数组
      if (isEmpty(this.currentNode)) {
        this.currentNode = { ...this.$refs.weFileTree.$refs.tree.root };
        this.currentNode.data = { ...this.currentNode.data[0] };
        node = this.currentNode.data;
      }
      // 如果是选中的脚本，就不让新建
      // if (node.isLeaf) return;
      this.filterTree = cloneDeep(this.fileTree);
      this.fsType = 'share';
      this.newDialog = {
        type: isLeaf ? this.$t('message.scripts.deleteType.script') : this.$t('message.scripts.deleteType.catalog'),
        isNew: true,
        node,
        isLeaf,
        scriptType: this.getSupportModes().filter((item) => item.isCanBeNew),
        defaultPath: node.isLeaf ? this.rootPath : node.path,
      };
      if (this.$refs.newFile) {
        this.$refs.newFile.open();
      }
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
          let text = node.copy ? this.$t('message.scripts.constants.success.stick') : this.$t('message.scripts.constants.success.add')
          this.$Message.success(text);
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
      this.currentNode.isEditState = true;
    },
    handleMove() {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.scripts.constants.warning.data'));
      let node = isArray(this.currentNode.data) ? this.currentNode.data[0] : this.currentNode.data;
      this.dispatch('Workbench:isOpenTab', {
        oldDest: node.path,
      }, (isOpenTab) => {
        if (isOpenTab) {
          return this.$Message.error(this.$t('message.scripts.hasopen'));
        } else {
          this.filterTree = cloneDeep(this.fileTree);
          this.fsType = 'share';
          this.newDialog = {
            type: this.$t('message.scripts.deleteType.script'),
            isNew: true,
            node,
            isLeaf: true,
            scriptType: this.getSupportModes().filter((item) => item.isCanBeNew),
            defaultPath: node.parentPath,
          };
          if (this.$refs.moveFile) {
            this.$refs.moveFile.open();
          }
        }
      })
    },
    handleMoveUpdate(params) {
      api.fetch('/filesystem/rename', params).then(() => {
        if (params.cb) {
          params.cb()
          this.refresh('move', params.newDest)
          if (this.currentNode.parent.data.path !== this.rootPath) {
            this.currentNode.parent.data.expanded = false
          }
          this.currentNode.parent.data.children = this.currentNode.parent.data.children.filter(it => it.path !== this.currentNode.data.path)
          // 删除本地缓存
          this.dispatch('Workbench:deleteDirOrFile', this.currentNode.data.path)
        }
        this.$Message.success(this.$t("message.scripts.move.success"))
      }).catch(() => {
      });
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
          // 判断是否在tab中打开，如果打开阻止修改
          this.dispatch('Workbench:isOpenTab', {
            newLabel: args.label,
            oldLabel: args.node.name,
            oldDest: args.node.path,
          }, (isOpenTab) => {
            if (isOpenTab) {
              return this.$Message.error(this.$t('message.scripts.hasopen'));
            } else {
              api.fetch('/filesystem/rename', {
                oldDest: args.node.path,
                newDest: path,
              }).then(() => {
                if (this.currentNode && this.currentNode.data) {
                  this.currentNode.data.path = path
                }
                cb(true);
              }).catch(() => {
                cb(false);
              });
            }
          })

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
      this.dispatch('Workbench:deleteDirOrFile', path, (flag) => {
        // 如果是没保存的tab页，要用户保存之后再删除，
        // 否则调用接口，文件在后台删除，前台保存会报错！
        if (flag === 'save' || flag === 'none') {
          this.loading = true;
          api.fetch('/filesystem/deleteDirOrFile', {
            path,
          }).then(() => {
            this.loading = false;
            this.$Message.success(this.$t('message.scripts.constants.success.delete'));
            const parent = { ...this.currentNode.parent, data: {...this.currentNode.parent.data } }
            this.refresh('delete');
            this.currentNode.remove();
            this.currentNode = parent
          }).catch(() => {
            this.loading = false;
          });
        } else {
          this.$refs.delete.close();
          this.$Message.warning(this.$t('message.scripts.warning.scriptChanged'));
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
      api.fetch(
        `/filesystem/getDirFileTrees`, {
          path: node.data.path,
        },
        'get'
      ).then((rst) => {
        this.treeLoading = false;
        const tree = rst.dirFileTrees.children;
        cb(tree);
      }).catch(() => {
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
        if (this.treeLoading) return this.$Message.warning(this.$t('message.scripts.constants.warning.data'));
        if (isEmpty(this.fileTree)) return this.initData();
        this.treeLoading = true;

        let nodePath = isEmpty(this.currentNode) ? this.rootPath : this.currentNode.data.path;
        if (type === 'move') {
          nodePath = path.slice(0, path.lastIndexOf('/'));
        } else if (type === 'delete') {
          nodePath = this.currentNode.data.parentPath || nodePath;
        } else if (this.currentNode.isLeaf || type === 'edit') {
          // 如果是文件、编辑的时候，要请求上一级文件夹的数据
          nodePath = this.currentNode.data.parentPath;
        } else if (type === 'new' && path) {
          nodePath = path.slice(0, path.lastIndexOf('/'));
        }
        api.fetch(`/filesystem/getDirFileTrees`, {
          path: nodePath,
        }, 'get').then((rst) => {
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
          this.dispatch('IndexedDB:appendTree', { treeId: 'scriptTree', value: this.fileTree });
          // 在渲染完毕后进行过滤
          if (this.searchText) {
            this.$nextTick(() => {
              this.$refs.weFileTree.filter();
            })
          }
        }).catch(() => {
          this.treeLoading = false;
        });
      };
      if (this.rootPath) return getTreeData();
      this.getRootPath((status) => {
        if (status) {
          getTreeData();
        } else {
          this.treeLoading = false;
        }
      });
    },
    openImportDialog() {
      this.fsType = 'hdfs';
      this.getHdfsTree().then(() => {
        this.$refs.importToHdfs.open();
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
                  // 树结构存储到indexedDB
                  this.dispatch('IndexedDB:appendTree', { treeId: 'hdfsTree', value: this.hdfsTree });
                  this.filterHdfsTreeData();
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
    filterHdfsTreeData() {
      this.hdfsTree[0].children = this.hdfsTree[0].children.filter( function (node) {
        if (!node.isLeaf) return true;
        const tabSuffix = node.name.substr(
          node.name.lastIndexOf('.'),
          node.name.length
        );
        const reg = ['.xlsx', '.xls', '.csv', '.txt'];
        const isVaild = indexOf(reg, tabSuffix) !== -1;
        return isVaild;
      })
    },
    importToHdfs(node) {
      const name = `new_stor_${Date.now()}.out`;
      const code = `from ${this.currentNode.data.path} to ${node.path}/${this.currentNode.data.name}`;
      const md5Path = util.md5(name);
      this.dispatch(
        'Workbench:add',
        {
          id: md5Path,
          filename: name,
          // saveAs表示临时脚本，需要关闭或保存时另存
          saveAs: true,
          filepath: '',
          code,
        },
        () => {
          this.$nextTick(() => {
            this.dispatch('Workbench:run', { id: md5Path });
          });
        }
      );
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
  }
};
</script>
<style src="../../assets/styles/sidebar.scss" lang="scss"></style>
