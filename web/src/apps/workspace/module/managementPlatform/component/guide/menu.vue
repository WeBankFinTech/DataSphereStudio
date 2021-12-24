<template>
  <div class="access-component-wrap">
    <div class="head-menu">
      <Tooltip content="查看全部节点" placement="top">
        <div class="btn-op" @click="handleChangeMode('all')">
          <SvgIcon icon-class="guide-all" />
        </div>
      </Tooltip>
      <Tooltip content="只看question节点" placement="top">
        <div class="btn-op" @click="handleChangeMode('question')">
          <SvgIcon icon-class="guide-question" />
        </div>
      </Tooltip>
      <Tooltip content="只看step节点" placement="top">
        <div class="btn-op" @click="handleChangeMode('step')">
          <SvgIcon icon-class="guide-step" />
        </div>
      </Tooltip>
      <Tooltip content="添加页面(group)" placement="top">
        <div class="btn-op" @click="handleAddGroup">
          <SvgIcon icon-class="guide-add" />
        </div>
      </Tooltip>
    </div>
    <Tree
      class="management-platform-sidebar-tree-container"
      :nodes="nodes"
      :currentTreeId="currentTreeId"
      @on-item-click="handleTreeClick"
      @on-item-toggle="handleItemToggle"
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
        <FormItem label="文档path" prop="path">
          <Input
            type="text"
            v-model="groupForm.path"
            placeholder="请输入文档path，path以/开头"
            style="width: 300px"
          ></Input>
        </FormItem>
        <FormItem label="文档名称" prop="title">
          <Input
            type="text"
            v-model="groupForm.title"
            placeholder="请输入文档名称"
            style="width: 300px"
          ></Input>
        </FormItem>
        <FormItem label="文档描述" prop="description">
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
        <FormItem label="所属路径" prop="path">
          <Input
            type="text"
            v-model="contentForm.path"
            style="width: 300px"
            disabled
          >
          </Input>
        </FormItem>
        <FormItem label="节点类型" prop="type">
          <RadioGroup v-model="contentForm.type">
            <Radio label="1" :disabled="!!contentForm.id">步骤</Radio>
            <Radio label="2" :disabled="!!contentForm.id">问题</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="内容标题" prop="title">
          <Input
            type="text"
            v-model="contentForm.title"
            placeholder="请输入标题"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem label="标题别名" prop="titleAlias">
          <Input
            type="text"
            v-model="contentForm.titleAlias"
            placeholder="请输入titleAlias"
            style="width: 300px"
            :disabled="modalType === 'updateApi'"
          >
          </Input>
        </FormItem>
        <FormItem label="标题前缀" prop="seq" v-if="contentForm.type == 1">
          <Input
            type="text"
            v-model="contentForm.seq"
            placeholder="请输入seq"
            style="width: 300px"
            :disabled="modalType === 'updateApi'"
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

import Tree from "./tree.vue";
export default {
  name: "guideMenu",
  components: {
    Tree,
  },
  data() {
    const validateGroupPath = (rule, value, callback) => {
      const result = value && value.trim();
      const reg = /^\/[\w_]*$/;
      if (!reg.test(result)) {
        callback(new Error("path以/开头，支持英文、数字、下划线（_）"));
      } else {
        const path_not_change = this.nodes.find(
          (item) => item.path.trim() === result && item.id == this.groupForm.id
        );
        if (this.groupForm.id && path_not_change) {
          // 如果是修改，且修改的group的path没有变化，说明只修改了title等属性
          callback();
        } else {
          // 修改path，检查不能和其他group重复
          if (this.nodes.some((item) => item.path.trim() === result)) {
            callback(new Error("该path已经存在"));
            return;
          } else {
            callback();
          }
        }
      }
    };
    return {
      mode: "all",
      currentTreeId: "",
      nodes: [],
      // modal and form
      modalType: "",
      modalVisible: false,
      modalTitle: "",
      submitLoading: false,
      groupForm: {
        path: "",
        title: "",
        description: "",
      },
      groupRule: {
        path: [
          { required: true, validator: validateGroupPath, trigger: "blur" },
        ],
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
      },
      contentForm: {
        type: "", // 类型: 1-步骤step，2-问题question
        title: "",
        titleAlias: "",
        seq: "",
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
      });
    },
    mergeTree(source = [], nodes) {
      // 新的nodes合并到source，保留source中的canAdd，opened等属性，path，title属性及新的节点取自nodes
      const data = [];
      for (let i = 0, len = nodes.length; i < len; i++) {
        let merge = source[i] || nodes[i] || {};
        data.push({
          ...merge,
          treeId: `group_${nodes[i].id}`,
          path: nodes[i].path,
          title: nodes[i].title,
          canAdd: true,
          canDelete: true,
          canUpdate: true,
          children: nodes[i].children.map((c) => {
            return {
              ...c,
              treeId: `content_${c.id}`,
              type: c.type == 1 ? "step" : "question", // 类型: 1-步骤step，2-问题question
              canDelete: true,
              canUpdate: true,
            };
          }),
        });
      }
      return data;
    },
    handleTreeClick(node) {
      // group节点点击不处理
      if (node.type) {
        this.currentTreeId = node.treeId;
        this.$router.push({
          path: "/managementPlatform/guide",
          query: { id: node.id },
        });
      }
    },
    handleChangeMode(mode) {
      this.mode = mode;
      // hidden nodes
      this.nodes = this.nodes.map((i) => {
        return {
          ...i,
          children: i.children.map((c) => {
            return {
              ...c,
              hidden: mode == "all" ? false : c.type != mode,
            };
          }),
        };
      });
    },
    handleItemToggle(node) {
      this.nodes = this.nodes.map((item) => {
        if (item.id == node.id) {
          return {
            ...item,
            opened: !node.opened,
          };
        } else {
          return item;
        }
      });
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
          path: node.path || "",
          title: node.title || "",
          description: node.description || "",
        };
      } else {
        this.contentForm = {
          id: node.id,
          path: node.path,
          type: node.type == "step" ? "1" : "2", // 类型: 1-步骤step，2-问题question
          title: node.title || "",
          titleAlias: node.titleAlias || "",
          seq: node.seq || "",
        };
      }
    },
    handleAddClick(node) {
      this.showModal({
        type: "content",
        op: "新增",
      });
      this.contentForm = {
        path: node.path,
        groupId: node.id,
        type: "",
        title: "",
        titleAlias: "",
        seq: "",
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
      this.modalTitle = type === "group" ? `${op}文档` : `${op}节点`;
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
          path: "",
          title: "",
          description: "",
        };
        this.$refs["groupForm"].resetFields();
      } else {
        this.contentForm = {
          type: "",
          title: "",
          titleAlias: "",
          seq: "",
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
    color: #657180;
    &:hover {
      color: #5cadff;
      background-color: #fff;
      border-color: #5cadff;
    }
  }
}
.modalFooter {
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 10px;
}
</style>
