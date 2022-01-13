<template>
  <div class="access-component-wrap">
    <div class="head-menu">
      <Button type="primary" @click="handleAddGroup">添加目录</Button>
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
        ref="groupForm"
        :model="groupForm"
        :label-width="100"
        :rules="groupRule"
        v-show="modalType === 'group'"
      >
        <FormItem label="目录名称" prop="title">
          <Input
            type="text"
            v-model="groupForm.title"
            placeholder="请输入目录名称"
            style="width: 300px"
          ></Input>
        </FormItem>
        <FormItem label="目录描述" prop="description">
          <Input
            type="textarea"
            v-model="groupForm.description"
            placeholder="请输入描述"
            style="width: 300px"
          >
          </Input>
        </FormItem>
      </Form>
      <Form
        ref="contentForm"
        :model="contentForm"
        :label-width="100"
        :rules="contentRule"
        v-show="modalType !== 'group'"
      >
        <FormItem label="所属目录" prop="catalog">
          <Input
            type="text"
            v-model="contentForm.catalog"
            style="width: 300px"
            disabled
          >
          </Input>
        </FormItem>
        <FormItem label="节点类型" prop="type">
          <RadioGroup v-model="contentForm.type">
            <Radio label="1" :disabled="!!contentForm.id">文档</Radio>
            <Radio label="2" :disabled="!!contentForm.id">目录</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="节点标题" prop="title">
          <Input
            type="text"
            v-model="contentForm.title"
            placeholder="请输入标题"
            style="width: 300px"
          >
          </Input>
        </FormItem>
      </Form>
      <slot name="footer">
        <div class="modalFooter">
          <Button @click="handleModalCancel()" size="large">取消</Button>
          <Button
            type="primary"
            size="large"
            :loading="submitLoading"
            @click="handleModalOk()"
            style="margin-left: 10px"
            >确认</Button
          >
        </div>
      </slot>
    </Modal>
  </div>
</template>

<script>
import {
  GetGuideTree,
  SaveGuideGroup,
  SaveGuideContent,
  DeleteGuideContent,
  DeleteGuideGroup,
} from "@/common/service/apiGuide";
import util from "@/common/util";
import lubanTree from "@/components/lubanTree";
export default {
  name: "guideMenu",
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
      groupForm: {
        title: "",
        description: "",
      },
      groupRule: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
      },
      contentForm: {
        type: "", // 类型: 1文档，2目录
        title: "",
      },
      contentRule: {
        type: [
          { required: true, message: "请选择节点类型", trigger: "change" },
        ],
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
      },
    };
  },
  mounted() {
    this.refreshTree();
  },
  methods: {
    refreshTree() {
      GetGuideTree().then((data) => {
        this.nodes = this.mergeTree(this.nodes, data.result || []);
        this.nodes = [
          {
            id: 1,
            title: '工作空间首页',
            type: 'category',
            isLeaf: false,
            treeId: `category_1`,
            canAdd: true,
            canDelete: true,
            canUpdate: true,
            children: []
          },
          {
            id: 2,
            title: '工作空间首页2',
            type: 'category',
            isLeaf: false,
            treeId: `category_2`,
            canAdd: true,
            canDelete: true,
            canUpdate: true,
            children: []
          }
        ]
      });
    },
    mergeTree(source = [], nodes) {
      // 以新的nodes为主，但要保留source中的opened等属性，path，title属性及新的节点取自nodes
      const data = [];
      for (let i = 0, len = nodes.length; i < len; i++) {
        const origin = source.find(item => item.id == nodes[i].id)
        data.push({
          ...nodes[i],
          opened: origin ? origin.opened : false,
          treeId: `group_${nodes[i].id}`,
          path: nodes[i].path,
          title: nodes[i].title || nodes[i].path,
          canAdd: true,
          canDelete: true,
          canUpdate: true,
          children: nodes[i].children.map((c) => {
            return {
              ...c,
              treeId: `content_${c.id}`,
              type: c.type == 1 ? "step" : "question", // 类型: 1文档，2目录
              canDelete: true,
              canUpdate: true,
            };
          }),
        });
      }
      return data;
    },
    handleTreeClick(node) {
      if (node.isLeaf) {
        console.log('isleaf', node)
        this.currentTreeId = node.treeId;
        this.$router.push({
          path: "/managementPlatform/library",
          query: { id: node.id },
        });
      } else {
        if (!node.opened && !node.loaded) {
          this.nodes = this.handleLoading(this.nodes, node)
          setTimeout(() => {
            this.nodes = this.mockTree(this.nodes, node);
            console.log(this.nodes, node)
          }, 200)
        } else {
          console.log('toggle')
          this.nodes = this.handleToggle(this.nodes, node)
        }
      }
    },
    mockTree(nodes, node) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            loaded: true,
            loading: false,
            opened: true,
            isLeaf: false,
            children: [
              {
                id: util.guid(),
                title: '章节1',
                type: 'chapter',
                isLeaf: true,
                treeId: util.guid(),
                canDelete: true,
                canUpdate: true,
              },
              {
                id: util.guid(),
                title: '章节2',
                type: 'chapter',
                isLeaf: true,
                treeId: util.guid(),
                canDelete: true,
                canUpdate: true,
              },
              {
                id: util.guid(),
                title: '子目录',
                type: 'category',
                isLeaf: false,
                treeId: util.guid(),
                canAdd: true,
                canDelete: true,
                canUpdate: true,
              }
            ]
          }
          matched = true;
        } else if (!matched && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.mockTree(nodes[i].children, node)
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
        if (nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            opened: !nodes[i].opened
          }
          matched = true;
        } else if (!matched && nodes[i].children && nodes[i].children.length) {
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
        if (nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            loading: true
          }
          matched = true;
        } else if (!matched && nodes[i].children && nodes[i].children.length) {
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
    handleDeleteClick(node) {
      this.$Modal.confirm({
        title: "确认删除吗",
        content: "",
        onOk: () => {
          if (node.type) {
            DeleteGuideContent(node.id).then((res) => {
              this.refreshTree();
            });
          } else {
            const group = this.nodes.find((i) => i.id == node.id);
            if (group && group.children && group.children.length) {
              this.$Message.info("该页面group还有所属内容，不能删除");
            } else {
              DeleteGuideGroup(node.id).then((res) => {
                this.refreshTree();
              });
            }
          }
        },
        onCancel: () => {},
      });
    },
    handleUpdateClick(node) {
      const type = node.type ? "content" : "group";
      this.showModal({
        type: type,
        op: "修改",
      });
      // 修改操作时，不要直接把node赋值，会同时影响tree的数据
      if (type == "group") {
        this.groupForm = {
          id: node.id,
          title: node.title || "",
          description: node.description || "",
        };
      } else {
        this.contentForm = {
          id: node.id,
          type: node.type == "step" ? "1" : "2", // 类型: 1文档，2目录
          title: node.title || ""
        };
      }
    },
    handleAddClick(node) {
      this.showModal({
        type: "content",
        op: "新增",
      });
      this.contentForm = {
        groupId: node.id,
        type: "",
        title: "",
      };
    },
    handleAddGroup() {
      this.showModal({
        type: "group",
        op: "新增",
      });
    },
    showModal(payload) {
      const { type, op } = payload;
      this.modalVisible = true;
      this.modalType = type;
      this.modalTitle = type === "group" ? `${op}目录` : `${op}文档`;
    },
    handleModalCancel() {
      this.modalVisible = false;
      this.submitLoading = false;
      this.resetForm();
    },
    handleModalOk() {
      const modalType = this.modalType;
      if (modalType === "group") {
        this.$refs["groupForm"].validate((valid) => {
          if (valid) {
            this.submitLoading = true;
            SaveGuideGroup(this.groupForm).then((res) => {
              this.submitLoading = false;
              this.handleModalCancel();
              this.refreshTree();
            });
          }
        });
      } else {
        this.$refs["contentForm"].validate((valid) => {
          if (valid) {
            this.submitLoading = true;
            // 类型: 1-步骤step，2-问题question
            SaveGuideContent({
              ...this.contentForm,
              type: +this.contentForm.type,
            }).then((res) => {
              this.submitLoading = false;
              this.handleModalCancel();
              this.refreshTree();
            });
          }
        });
      }
    },
    resetForm() {
      if (this.modalType === "group") {
        this.groupForm = {
          title: "",
          description: "",
        };
        this.$refs["groupForm"].resetFields();
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
@import "@/common/style/variables.scss";
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
