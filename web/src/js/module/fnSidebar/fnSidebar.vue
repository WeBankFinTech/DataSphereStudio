<template>
  <div class="we-side-bar">
    <we-navbar
      :nav-list="navList"
      @on-refresh="refresh"
      @text-change="setSearchText"/>
    <we-file-tree
      ref="weFileTree"
      :tree="fnTree"
      :load-data-fn="loadDataFn"
      :filter-text="searchText"
      :node-props="nodeProps"
      :loading="compLoading"
      class="we-side-bar-content"
      @on-refresh="refresh"
      @work-bench-contextMenu="benchContextMenu"
      @work-bench-check="benchCheck"
      @work-bench-click="benchClick"/>
    <we-menu
      ref="treeContextMenu"
      :id="type">
      <template v-if="isOwn">
        <we-menu-item
          v-if="!isLeaf && !model"
          @select="openModal(true, 'fn')">
          <span>{{ $t('message.fnSideBar.contextMenu.isOwn.addFunction') }}</span>
        </we-menu-item>
        <we-menu-item
          v-if="!isLeaf && !model"
          @select="openModal(true, 'folder')">
          <span>{{ $t('message.fnSideBar.contextMenu.isOwn.addFolder') }}</span>
        </we-menu-item>
        <we-menu-item
          v-if="!isLeaf"
          class="ctx-divider"/>
        <we-menu-item
          v-if="!isRoot && !model"
          @select="openDeleteModal">
          <span>{{ $t('message.constants.delete') }}</span>
        </we-menu-item>
        <we-menu-item
          v-if="isLeaf && !model"
          @select="openModal(false, 'fn')">
          <span>{{ $t('message.constants.update') }}</span>
        </we-menu-item>
        <we-menu-item
          v-if="!isLeaf && !isRoot && !model"
          @select="openModal(false, 'folder')">
          <span>{{ $t('message.constants.rename') }}</span>
        </we-menu-item>
        <we-menu-item
          v-if="isUdfManager && isUnShare"
          @select="openShareModal(true)">
          <span>{{ $t('message.fnSideBar.contextMenu.isOwn.share') }}</span>
        </we-menu-item>
      </template>
      <template v-if="isLeaf">
        <we-menu-item
          v-if="isOwn"
          class="ctx-divider"/>
        <we-menu-item @select="copyName">
          <span>{{ $t('message.fnSideBar.contextMenu.isLeaf.copyName') }}</span>
        </we-menu-item>
        <we-menu-item @select="pasteName">
          <span>{{ $t('message.fnSideBar.contextMenu.isLeaf.pasteName') }}</span>
        </we-menu-item>
      </template>
      <template v-if="isLeaf && isOwn && currentNode.data.shared">
        <we-menu-item @select="handleExpired">
          <span>{{ $t('message.fnSideBar.contextMenu.isOwnAndLeaf.updateToExpired') }}</span>
        </we-menu-item>
        <we-menu-item @select="openShareModal(false)">
          <span>{{ $t('message.fnSideBar.contextMenu.isOwnAndLeaf.viewSharedUsers') }}</span>
        </we-menu-item>
      </template>
      <template v-if="!isLeaf">
        <we-menu-item @select="refresh">
          <span>{{ $t('message.constants.refresh') }}</span>
        </we-menu-item>
      </template>
    </we-menu>
    <new-dialog
      ref="folder"
      :loading="loading"
      :type="newDialog.type"
      :is-new="newDialog.isNew"
      :node="newDialog.node"
      @add="newNode"
      @update="updateNode"/>
    <setting-modal
      ref="fn"
      :loading="loading"
      :tree="shareTree"
      :load-data-fn="loadShareTreeFn"
      :filter-node="filterNode"
      :is-udf="FNTYPE==='udf'"
      @add="newNode"
      @update="updateNode"
      @type-change="changeFnType"/>
    <delete-dialog
      ref="delete"
      :loading="loading"
      @delete="deleteNode"/>
    <share-modal
      ref="share"
      :loading="loading"
      @add-share="shareNode"
      @update-share="updateShareNode"/>
  </div>
</template>
<script>
import { isEmpty, map, find, get, cloneDeep } from 'lodash';
import module from './index';
import api from '@/js/service/api';
import util from '@/js/util';
import storage from '@/js/helper/storage';
import weFileTree from '@js/component/fileTree';
import deleteDialog from '@js/component/deleteDialog';
import newDialog from '@js/component/newDialog';
import settingModal from '@js/component/functionSetting';
import shareModal from '@js/component/functionShare';
export default {
  name: 'FnSidebar',
  components: {
    weFileTree,
    newDialog,
    settingModal,
    deleteDialog,
    shareModal,
  },
  props: {
    type: {
      type: String,
      default: 'udf',
    },
    model: String,
    node: Object,
  },
  data() {
    return {
      FNTYPE: this.type === 'udf' ? 'udf' : 'function',
      loading: false,
      navList: ['search', 'refresh'],
      currentNode: {},
      searchText: '',
      nodeProps: {
        children: 'childrens',
        label: 'name',
        icon: 'icon',
        isLeaf: 'isLeaf',
      },
      fnTree: [],
      shareTree: [],
      loadShareTreeFn: Function,
      workSpaceComponent: null,
      isUdfManager: false,
      compLoading: false,
      treeLoading: false,
      filterNode: () => {},
      newDialog: {
        type: '文件夹',
        isNew: true,
        node: {},
      },
    };
  },
  computed: {
    isLeaf() {
      return this.currentNode && this.currentNode.isLeaf;
    },
    isOwn() {
      return this.currentNode && this.currentNode.data && this.currentNode.data.type === 'self';
    },
    isRoot() {
      return this.currentNode && this.currentNode._level === 1;
    },
    isUnShare() {
      return this.currentNode && this.currentNode.data && !this.currentNode.data.shared && this.currentNode.data.udfType !== 0;
    },
  },
  created() {
    this.initTreeData();
    this.$nextTick(() => {
      api.fetch('/udf/authenticate').then((rst) => {
        this.isUdfManager = rst.isUDFManager;
      });
    });
  },
  methods: {
    initTreeData() {
      const tmp = this.getName();
      const fnTree = storage.get(tmp, 'SESSION');
      if (!fnTree || isEmpty(fnTree)) {
        this.treeLoading = true;
        const timeout = setTimeout(() => {
          this.compLoading = true;
        }, 2000);
        api.fetch('/udf/list', {
          type: 'self',
          treeId: -1,
          category: this.FNTYPE,
        }).then((rst) => {
          clearTimeout(timeout);
          this.treeLoading = false;
          this.compLoading = false;
          this.fnTree = map(rst.udfTree.childrens, (o) => {
            return this.handleTree(o);
          });
          storage.set(tmp, this.fnTree, 'SESSION');
        }).catch(() => {
          clearTimeout(timeout);
          this.treeLoading = false;
          this.compLoading = false;
        });
      } else {
        this.fnTree = fnTree;
      }
    },
    setSearchText(value) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.constants.warning.data'));
      this.searchText = value;
    },
    benchContextMenu({ node, ev }) {
      this.currentNode = node;
      this.$refs.treeContextMenu.open(ev);
    },
    benchClick(...args) {
      this.currentNode = args[0].node;
    },
    benchCheck(...args) {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      const node = args[0].node.data;
      const url = `/udf/isload`;
      this.loading = true;
      api.fetch(url, {
        udfId: node.id,
        isLoad: node.load,
      }, 'get').then(() => {
        this.loading = false;
        if (node.load) {
          this.$Message.success(this.$t('message.fnSideBar.success.load'));
        } else {
          this.$Message.success(this.$t('message.fnSideBar.success.cancelLoading'));
        }
        storage.set(this.getName(), this.fnTree);
        this.getLoadingFnList();
      }).catch(() => {
        node.load = !node.load;
        this.loading = false;
      });
    },
    newNode(data) {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      if (this.checkExist({
        isNew: true,
        data,
        node: this.currentNode,
      })) return;
      this.loading = true;
      let params = null;
      let url = null;
      if (!data.isLeaf) {
        url = '/udf/tree/add';
        params = {
          parent: this.currentNode.data.id,
          name: data.name,
          description: data.description,
          category: this.FNTYPE,
        };
        this.calling(url, params, 'new', data.isLeaf);
      } else {
        url = '/udf/add';
        params = {
          isShared: data.shared,
          udfInfo: {
            udfName: data.name,
            description: data.description,
            path: data.path,
            shared: false,
            useFormat: data.useFormat,
            expire: false,
            load: data.defaultLoad,
            registerFormat: data.registerFormat,
            treeId: this.currentNode.data.id,
            udfType: data.udfType,
          },
        };
        this.calling(url, params, 'new', data.isLeaf);
      }
    },
    updateNode(data) {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      if (data.name !== this.currentNode.label) {
        if (this.checkExist({
          isNew: false,
          data,
          node: this.currentNode,
        })) return;
      }
      this.loading = true;
      let params = null;
      let url = null;
      if (!data.isLeaf) {
        url = '/udf/tree/update';
        params = {
          id: this.currentNode.data.id,
          parent: this.currentNode.parent.data.id,
          name: data.name,
          description: data.description,
          category: this.FNTYPE,
        };
      } else {
        url = '/udf/update';
        params = {
          isShared: data.shared,
          udfInfo: {
            id: this.currentNode.data.id,
            udfName: data.name,
            description: data.description,
            path: data.path,
            shared: data.shared,
            useFormat: data.useFormat,
            load: true,
            expire: false,
            registerFormat: data.registerFormat,
            treeId: this.currentNode.parent.data.id,
            udfType: data.udfType,
          },
        };
      }
      this.calling(url, params, 'edit', data.isLeaf);
    },
    /**
         * 新增和修改文件夹、函数时调用接口
         * @param {string} url 请求的Url
         * @param {object} params 是请求参数
         * @param {string} mode 是new或者edit
         * @param {boolean} isLeaf 是是否是函数或者文件夹
         */
    calling(url, params, mode, isLeaf) {
      let msg = this.$t('message.constants.success.update');
      if (mode === 'new') {
        msg = isLeaf ? this.$t('message.fnSideBar.success.addUdf') : this.$t('message.fnSideBar.success.addFolder');
      }
      api.fetch(url, params).then(() => {
        this.$Message.success(msg);
        this.loading = false;
        this.refresh(mode);
        if (isLeaf) {
          this.getLoadingFnList();
        }
      }).catch(() => {
        this.loading = false;
      });
    },
    checkExist({ isNew, data, node }) {
      const list = isNew ? node.data.childrens : node.parent.data.childrens;
      const isIn = find(list, (o) => {
        return o.name === data.name && o.isLeaf === data.isLeaf;
      });
      const msg = !data.isLeaf ? `${this.$t('message.constants.error.folderExists')}${data.name}` : `${this.$t('message.constants.error.functionExists')}${data.name}`;
      if (isIn) return this.$Message.warning(msg);
    },
    openModal(isNew, type) {
      if (type === 'fn') {
        let type = this.$t('message.deleteType.common');
        if (!isNew && this.currentNode.data.udfType > 0) {
          type = '';
        }
        const defaultVaule = this.FNTYPE === 'udf' ? type : '';
        this.changeFnType(defaultVaule);
      }
      if (type === 'fn') {
        const data = {
          node: this.currentNode,
          type: this.$t('message.deleteType.folder'),
          isNew,
        };
        this.$refs[type].open(data);
      } else {
        this.newDialog = {
          type: this.$t('message.deleteType.folder'),
          isNew,
          node: this.currentNode,
        };
        this.$refs[type].open();
      }
    },
    openDeleteModal() {
      if (this.currentNode.data.shared) {
        this.$Message.warning(this.$t('message.fnSideBar.warning.functionShared'));
      } else {
        const leaf = this.currentNode.isLeaf;
        const opt = {
          type: leaf ? this.$t('message.deleteType.function') : this.$t('message.deleteType.folder'),
          name: this.currentNode.label,
        };
        const noEmptyMsg = this.$t('message.fnSideBar.warning.noEmpty');
        if (!leaf) {
          if (this.currentNode.childrens && this.currentNode.childrens.length) {
            this.$Message.warning(noEmptyMsg);
          } else if (!this.currentNode.childrens || isEmpty(this.currentNode.childrens)) {
            this.refresh().then((childrens) => {
              if (isEmpty(childrens)) {
                this.$refs.delete.open(opt);
              } else {
                this.$Message.warning(noEmptyMsg);
              }
            });
          } else {
            this.$refs.delete.open(opt);
          }
        } else {
          this.$refs.delete.open(opt);
        }
      }
    },
    deleteNode() {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      this.loading = true;
      let url;
      let param;
      if (this.currentNode.isLeaf) {
        url = `/udf/delete/${this.currentNode.data.id}`;
        param = {
          isShared: this.currentNode.data.shared,
        };
      } else {
        url = `/udf/tree/delete/${this.currentNode.data.id}`;
        param = {};
      }
      api.fetch(url, param, 'get').then((rst) => {
        this.loading = false;
        this.$Message.success(this.$t('message.constants.success.delete'));
        this.refresh('delete');
        if (this.currentNode.isLeaf) {
          this.getLoadingFnList();
        }
      }).catch((err) => {
        this.loading = false;
      });
    },
    openShareModal(flag) {
      if (this.treeLoading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      this.treeLoading = true;
      this.compLoading = true;
      const node = this.fnTree.find((item) => item.type === 'share');
      api.fetch('/udf/list', {
        type: node.type,
        treeId: node.id,
        category: this.FNTYPE,
      }).then((rst) => {
        this.treeLoading = false;
        this.compLoading = false;
        const list = rst.udfTree.childrens;
        if (this.currentNode.data.shared) {
          const params = {
            udfName: this.currentNode.data.name,
          };
          api.fetch('/udf/getSharedUsers', params).then((rst) => {
            this.$refs.share.open({ tree: list, node: this.currentNode, isView: !flag, shareUser: rst.shareUsers.toString() });
          });
        }
        this.$refs.share.open({ tree: list, node: this.currentNode, isView: !flag });
      }).catch((err) => {
        this.treeLoading = false;
        this.compLoading = false;
      });
    },
    shareNode(option) {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      this.loading = true;
      const node = this.currentNode.data;
      const path = node.path;
      const fileName = path.slice(path.lastIndexOf('/') + 1, path.length);
      const params = {
        fileName,
        shareParentId: option.shareParentId,
        sharedUsers: option.sharedUsers,
        udfInfo: {
          createUser: node.createUser,
          description: node.description,
          id: node.id,
          expire: node.expire,
          load: node.load,
          path: node.path,
          registerFormat: node.registerFormat,
          shared: node.shared,
          treeId: node.treeId,
          udfName: node.udfName,
          udfType: node.udfType,
          useFormat: node.useFormat,
        },
      };
      api.fetch('/udf/shareUDF', params).then(() => {
        this.loading = false;
        this.$Message.success(`${this.$t('message.fnSideBar.success.shareFunction')}：${this.currentNode.data.name}`);
        this.refresh('edit');
      }).catch((err) => {
        this.loading = false;
      });
    },
    updateShareNode(option) {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.waiting'));
      const params = {
        udfName: this.currentNode.data.name,
        sharedUsers: option.sharedUsers,
      };
      api.fetch('/udf/updateSharedUsers', params).then(() => {
        this.loading = false;
        this.$Message.success(this.$t('message.fnSideBar.success.updateShareUser'));
      }).catch((err) => {
        this.loading = false;
      });
    },
    handleExpired() {
      this.$Modal.confirm({
        title: this.$t('message.fnSideBar.confirm.title'),
        content: this.$t('message.fnSideBar.confirm.content'),
        onOk: () => {
          const params = {
            udfId: this.currentNode.data.id,
            udfName: this.currentNode.data.name,
          };
          api.fetch('/udf/setExpire', params).then((rst) => {
            this.currentNode.data.shared = false;
            this.$Message.success(this.$t('message.fnSideBar.success.functionExpired'));
          });
        },
        onCancel: () => {
        },
      });
    },
    // 对初始化的树数据进行格式化
    handleTree(tree) {
      const fnType = ['sys', 'bdp', 'share'].includes(tree.userName) ? tree.userName : 'self';
      const isSys = fnType === 'sys';
      // 对文件夹进行格式化
      if (tree.hasOwnProperty('childrens')) {
        let tmp = tree.childrens || tree.udfInfos;
        if (tree.childrens && tree.udfInfos) {
          tmp = [...tree.childrens, ...tree.udfInfos];
        }
        const childrenFormat = map(tmp, (item) => {
          // 子UDF中未携带userName，无法判断其type
          item.userName = fnType;
          return this.handleTree(item);
        });
        return {
          id: tree.id,
          name: tree.name,
          description: tree.description,
          parent: tree.parent,
          userName: tree.userName,
          childrens: childrenFormat,
          expanded: false,
          isLeaf: false,
          hasFileOpen: false,
          isShow: true,
          hasModify: false,
          type: fnType,
        };
      }
      // 对函数进行格式化
      return Object.assign({
        name: tree.udfName,
        isLeaf: true,
        isFn: true,
        isShow: true,
        disabled: isSys,
        type: fnType,
      }, tree);
    },
    // 请求文件夹数据
    loadDataFn(node, cb) {
      this.treeLoading = true;
      this.compLoading = true;
      api.fetch('/udf/list', {
        type: node.data.type,
        treeId: node.data.id,
        category: this.FNTYPE,
      }).then((rst) => {
        this.treeLoading = false;
        this.compLoading = false;
        if (!node.isLeaf) {
          const tree = this.handleTree(rst.udfTree).childrens;
          cb(tree);
          this.$nextTick(() => {
            storage.set(this.getName(), this.fnTree, 'SESSION');
          });
        }
        cb();
      }).catch((err) => {
        this.treeLoading = false;
        this.compLoading = false;
      });
    },
    lookForChangeNode(id, node, type) {
      const NODESETTING = {
        tree: {
          id: 'id',
          children: 'childrens',
        },
        node: {
          id: 'data.id',
          children: 'computedNodes',
        },
      };
      const set = NODESETTING[type];
      for (let i = 0; i < node.length; i++) {
        if (get(node[i], set.id) === id) {
          // 打开最底层的文件夹
          node[i].expanded = true;
          return node[i];
        } else {
          if (!isEmpty(node[i][set.children])) {
            const findNode = this.lookForChangeNode(id, node[i][set.children], type);
            if (findNode) {
              // 打开父级目录的文件夹
              node[i].expanded = true;
              return findNode;
            }
          }
        }
      }
    },
    refresh(type) {
      return new Promise((resolve, reject) => {
        if (this.treeLoading) return this.$Message.warning(this.$t('message.constants.warning.data'));
        let id = this.currentNode.data.id;
        // 编辑或删除时要去刷新上一级目录
        if (this.currentNode.isLeaf) {
          id = this.currentNode.parent.data.id;
        } else if (type === 'edit' || type === 'delete') {
          id = this.currentNode.parent.data.id;
        }
        const parent = this.lookForChangeNode(id, this.fnTree, 'tree');
        if (parent) {
          this.treeLoading = true;
          this.compLoading = true;
          api.fetch('/udf/list', {
            type: parent.type,
            treeId: parent.id,
            category: this.FNTYPE,
          }).then((rst) => {
            this.treeLoading = false;
            this.compLoading = false;
            const children = this.handleTree(rst.udfTree).childrens;
            this.$set(parent, 'childrens', children);
            // 加载完数据后等待重新渲染，拿到渲染后的node
            this.$nextTick(() => {
              let node = this.$refs.weFileTree.$refs.tree.root._childNodes;
              node = this.lookForChangeNode(id, node, 'node');
              node.expanded = true;
              resolve(children);
              storage.set(this.getName(), this.fnTree, 'SESSION');
            });
          }).catch((err) => {
            this.treeLoading = false;
            this.compLoading = false;
          });
        } else {
          this.$Message.error(this.$t('message.constants.error.refresh'));
        }
      });
    },
    // 设置文件树
    setFileTree() {
      if (isEmpty(this.shareTree)) {
        const tmpTree = storage.get('scriptTree', 'SESSION');
        if (!tmpTree || (tmpTree && isEmpty(tmpTree))) {
          this.dispatch('WorkSidebar:showTree', (f) => {
            this.workSpaceComponent = f;
            f.getRootPath((flag) => {
              f.getTree((tree) => {
                if (tree) {
                  this.shareTree.push(tree);
                  this.loadShareTreeFn = f.loadDataFn;
                }
              });
            });
          });
        } else {
          this.shareTree = cloneDeep(tmpTree);
          if (this.workSpaceComponent) {
            this.loadShareTreeFn = this.workSpaceComponent.loadDataFn;
          } else {
            this.dispatch('WorkSidebar:showTree', (f) => {
              this.workSpaceComponent = f;
              this.loadShareTreeFn = f.loadDataFn;
            });
          }
        }
      }
    },

    copyName() {
      let copyLable = this.currentNode.data.name;
      util.executeCopy(copyLable);
    },

    pasteName() {
      const value = this.currentNode.data.name;
      this.dispatch('Workbench:pasteInEditor', value, this.node);
    },

    changeFnType(type) {
      // 重新渲染
      this.shareTree = [];
      this.$nextTick(() => {
        this.setFileTree();
      });
      if (type === this.$t('message.deleteType.common')) {
        this.filterNode = (node) => {
          const name = node.label;
          const tabSuffix = name.substr(name.lastIndexOf('.'), name.length);
          return !node.isLeaf || (node.isLeaf && tabSuffix === '.jar');
        };
      } else {
        this.filterNode = (node) => {
          const name = node.label;
          const tabSuffix = name.substr(name.lastIndexOf('.'), name.length);
          return !node.isLeaf || (node.isLeaf && (tabSuffix === '.py' || tabSuffix === '.scala'));
        };
      }
    },

    getName() {
      return `${this.FNTYPE}Tree`;
    },

    getLoadingFnList() {
      // 加载和取消加载函数时，从后台重新获取已加载的函数列表
      module.methods.getAllLoadedFunction((fnList) => {
        // 部分更新GlobalCache这张表的fnList
        this.dispatch('IndexedDB:updateGlobalCache', {
          id: this.getUserName(),
          fnList: fnList.list,
        });
        // isFunctionChange用于控制编辑器的完成状态是否要和indexDb中的部分更新GlobalCache这张表的fnList进行比对
        // 由于可能存在打开多种脚本语言的问题，所以需要有几个变量去控制脚本联想词的比对
        this.dispatch('Workbench:getWorksLangList', (list) => {
          list.forEach((item) => {
            if (item === 'hql') {
              storage.set('isFunctionChange_hql', true);
            } else if (item === 'python') {
              storage.set('isFunctionChange_python', true);
            }
          });
        });
      });
    },
  },
};
</script>
<style src="@assets/styles/sidebar.scss" lang="scss"></style>
