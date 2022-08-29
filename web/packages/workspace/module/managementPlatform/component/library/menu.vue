<template>
  <div class="access-component-wrap">
    <div class="head-menu">
      <Button type="primary" @click="handleAddGroup">{{ $t('message.workspace.AddDir') }}</Button>
    </div>
    <library-admin-tree
      class="tree-container"
      :nodes="nodes"
      :currentTreeId="currentTreeId"
      @on-item-click="handleTreeClick"
      @on-add-click="handleAddClick"
      @on-delete-click="handleDeleteClick"
      @on-update-click="handleUpdateClick"
    />

    <Modal
      v-model="modalVisible"
      :title="modalTitle"
      @on-cancel="handleModalCancel"
      footer-hide
    >
      <Form
        ref="catalogForm"
        :model="catalogForm"
        :label-width="100"
        :rules="groupRule"
        v-show="modalType === 'catalog'"
      >
        <FormItem :label="$t('message.workspace.DirectoryName')" prop="title">
          <Input
            type="text"
            v-model="catalogForm.title"
            :placeholder="$t('message.workspace.Please')"
            style="width: 300px"
          ></Input>
        </FormItem>
      </Form>
      <Form
        ref="contentForm"
        :model="contentForm"
        :label-width="100"
        :rules="contentRule"
        v-show="modalType !== 'catalog'"
      >
        <FormItem :label="$t('message.workspace.NodeType')" prop="type">
          <RadioGroup v-model="contentForm.type">
            <Radio label="chapter" :disabled="!!contentForm.id">{{ $t('message.workspace.Documents') }}</Radio>
            <Radio label="catalog" :disabled="!!contentForm.id">{{ $t('message.workspace.Directory') }}</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem :label="$t('message.workspace.NodeTitle')" prop="title">
          <Input
            type="text"
            v-model="contentForm.title"
            :placeholder="$t('message.workspace.inputTitle')"
            style="width: 300px"
          >
          </Input>
        </FormItem>
      </Form>
      <slot name="footer">
        <div class="modalFooter">
          <Button @click="handleModalCancel()" size="large">{{ $t('message.workspace.Cancel') }}</Button>
          <Button
            type="primary"
            size="large"
            :loading="submitLoading"
            @click="handleModalOk()"
            style="margin-left: 10px"
          >{{ $t('message.workspace.Confirm') }}</Button
          >
        </div>
      </slot>
    </Modal>
  </div>
</template>

<script>
import {
  GetLibraryTreeTop,
  GetLibraryTreeById,
  SaveCatalog,
  DeleteCatalog,
  SaveChapter,
  DeleteChapter,
} from '@dataspherestudio/shared/common/service/apiGuide';
import lubanTree from '@dataspherestudio/shared/components/lubanTree';
export default {
  name: "libraryMenu",
  components: {
    "library-admin-tree": lubanTree.libraryAdminTree,
  },
  data() {
    return {
      currentTreeId: "",
      nodes: [],
      // modal and form
      modalType: "",
      modalVisible: false,
      modalTitle: "",
      submitLoading: false,
      catalogForm: {
        parentId: -1, // 第一级目录父id为-1
        title: "",
        description: "",
      },
      groupRule: {
        title: [{ required: true, message: this.$t('message.workspace.inputTitle'), trigger: "blur" }],
      },
      contentForm: {
        catalogId: -1,
        type: "",
        title: "",
      },
      contentRule: {
        type: [
          { required: true, message: this.$t('message.workspace.chooseNodeType'), trigger: "change" },
        ],
        title: [{ required: true, message: this.$t('message.workspace.inputTitle'), trigger: "blur" }],
      },
    };
  },
  mounted() {
    this.initTree();
  },
  methods: {
    initTree() {
      GetLibraryTreeTop().then((data) => {
        this.nodes = this.refreshTreeTop(this.nodes, data.result || []);
      });
    },
    refreshTreeTop(source = [], nodes) {
      // 以新的nodes为主，但要保留source，新的节点取自nodes
      const data = [];
      for (let i = 0, len = nodes.length; i < len; i++) {
        const origin = source.find(item => item.id == nodes[i].id)
        if (origin) {
          data.push({
            ...origin,
            title: nodes[i].title
          })
        } else {
          data.push({
            ...nodes[i],
            type: 'catalog',
            treeId: `catalog_${nodes[i].id}`,
            isLeaf: false,
            canAdd: true,
            canDelete: true,
            canUpdate: true
          });
        }
      }
      return data;
    },
    handleTreeClick(node) {
      if (node.isLeaf) {
        this.currentTreeId = node.treeId;
        this.$router.push({
          path: "/managementPlatform/library",
          query: { id: node.id },
        });
      } else {
        if (!node.opened && !node.loaded) {
          this.nodes = this.handleLoading(this.nodes, node)
          this.refreshTree(node.id);
        } else {
          this.nodes = this.handleToggle(this.nodes, node)
        }
      }
    },
    refreshTree(catalogId) {
      GetLibraryTreeById(catalogId).then((data) => {
        const childrenCatalog = data.result ? (data.result.childrenCatalog || []).map(i => { return {...i, type: "catalog", isLeaf: false, canAdd: true }}) : []
        const childrenChapter = data.result ? (data.result.childrenChapter || []).map(i => { return {...i, type: "chapter", isLeaf: true }}) : []
        const children = childrenCatalog.concat(childrenChapter);
        this.nodes = this.mergeTree(this.nodes, catalogId, children);
      });
    },
    mergeTree(nodes, catalogId, children) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (!nodes[i].isLeaf && nodes[i].id == catalogId) {
          item = {
            ...nodes[i],
            loaded: true,
            loading: false,
            opened: true,
            isLeaf: false,
            children: children.map(c => {
              return {
                ...c,
                treeId: `${c.type}_${c.id}`,
                canDelete: true,
                canUpdate: true,
              }
            })
          }
          matched = true;
        } else if (!matched && !nodes[i].isLeaf && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.mergeTree(nodes[i].children, catalogId, children)
          }
        } else {
          item = nodes[i];
        }
        arr.push(item)
      }
      return arr;
    },
    handleToggle(nodes, node) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (!nodes[i].isLeaf && nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            opened: !nodes[i].opened
          }
          matched = true;
        } else if (!matched && !nodes[i].isLeaf && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.handleToggle(nodes[i].children, node)
          }
        } else {
          item = nodes[i];
        }
        arr.push(item)
      }
      return arr;
    },
    handleLoading(nodes, node) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (!nodes[i].isLeaf && nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            loading: true
          }
          matched = true;
        } else if (!matched && !nodes[i].isLeaf && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.handleLoading(nodes[i].children, node)
          }
        } else {
          item = nodes[i];
        }
        arr.push(item)
      }
      return arr;
    },
    haveChildren(nodes, node) {
      for (let i=0,len=nodes.length; i<len; i++) {
        if (nodes[i].id == node.id && nodes[i].type == node.type ) {
          return nodes[i].children && nodes[i].children.length > 0;
        } else if (nodes[i].children && nodes[i].children.length) {
          return this.haveChildren(nodes[i].children, node);
        }
      }
      return false;
    },
    handleDeleteClick(node) {
      this.$Modal.confirm({
        title: this.$t('message.workspace.ConfirmDel'),
        content: "",
        onOk: () => {
          if (node.type == 'chapter') {
            DeleteChapter(node.id).then(() => {
              this.refreshTree(node.catalogId);
            });
          } else {
            if (this.haveChildren(this.nodes, node)) {
              this.$Message.info(this.$t('message.workspace.Cannot'));
            } else {
              DeleteCatalog(node.id).then(() => {
                if (node.parentId == -1) {
                  this.initTree();
                } else {
                  this.refreshTree(node.parentId);
                }
              });
            }
          }
        },
        onCancel: () => {},
      });
    },
    handleUpdateClick(node) {
      if (node.type == "catalog") {
        // 按当时创建的modal还原
        if (node.parentId == -1) {
          this.showModal({
            type: "catalog",
            op: this.$t('message.workspace.Edit'),
          });
          this.catalogForm = {
            id: node.id,
            title: node.title || "",
            description: node.description || "",
          };
        } else {
          this.showModal({
            type: "chapter",
            op: this.$t('message.workspace.Edit'),
          });
          this.contentForm = {
            catalogId: node.parentId,
            id: node.id,
            type: node.type || 'catalog',
            title: node.title || ""
          };
        }
      } else {
        this.showModal({
          type: "chapter",
          op: this.$t('message.workspace.Edit'),
        });
        this.contentForm = {
          catalogId: node.catalogId,
          id: node.id,
          type: node.type || 'chapter',
          title: node.title || ""
        };
      }
    },
    handleAddClick(node) {
      this.showModal({
        type: "chapter",
        op: this.$t('message.workspace.Add'),
      });
      this.contentForm = {
        catalogId: node.id,
        type: "",
        title: "",
      };
    },
    handleAddGroup() {
      this.showModal({
        type: "catalog",
        op: this.$t('message.workspace.Add'),
      });
    },
    showModal(payload) {
      const { type, op } = payload;
      this.modalVisible = true;
      this.modalType = type;
      this.modalTitle = type === "catalog" ? `${op}目录` : `${op}节点`;
    },
    handleModalCancel() {
      this.modalVisible = false;
      this.submitLoading = false;
      this.resetForm();
    },
    handleModalOk() {
      const modalType = this.modalType;
      if (modalType === "catalog") {
        this.$refs["catalogForm"].validate((valid) => {
          if (valid) {
            this.submitLoading = true;
            SaveCatalog(this.catalogForm).then(() => {
              this.submitLoading = false;
              this.handleModalCancel();
              this.initTree();
            });
          }
        });
      } else {
        this.$refs["contentForm"].validate((valid) => {
          if (valid) {
            this.submitLoading = true;
            if (this.contentForm.type == "catalog") {
              const data = this.contentForm.id ? {
                id: this.contentForm.id,
                parentId: this.contentForm.catalogId,
                title: this.contentForm.title
              } : {
                parentId: this.contentForm.catalogId,
                title: this.contentForm.title
              }
              SaveCatalog(data).then(() => {
                this.refreshTree(this.contentForm.catalogId);
                this.submitLoading = false;
                this.handleModalCancel();
              });
            } else {
              const data = this.contentForm.id ? {
                id: this.contentForm.id,
                catalogId: this.contentForm.catalogId,
                title: this.contentForm.title
              } : {
                catalogId: this.contentForm.catalogId,
                title: this.contentForm.title
              }
              SaveChapter(data).then(() => {
                this.refreshTree(this.contentForm.catalogId);
                this.submitLoading = false;
                this.handleModalCancel();
              });
            }
          }
        });
      }
    },
    resetForm() {
      if (this.modalType === "catalog") {
        this.catalogForm = {
          parentId: -1,
          title: "",
        };
        this.$refs["catalogForm"].resetFields();
      } else {
        this.contentForm = {
          type: "",
          title: "",
        };
        this.$refs["contentForm"].resetFields();
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.head-menu {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px;
  .btn-op {
    width: 32px;
    height: 32px;
    padding: 4px;
    margin: 0 5px;
    text-align: center;
    font-size: 16px;
    cursor: pointer;
    border: 1px solid transparent;
    @include font-color(#333, $dark-workspace-title-color);
    &:hover {
      @include bg-color(#fff, $dark-menu-base-color);
      color: #5cadff;
      border-color: #5cadff;
    }
  }
}
.tree-container {
  padding: 0 10px;
}
.modalFooter {
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 10px;
}
</style>
