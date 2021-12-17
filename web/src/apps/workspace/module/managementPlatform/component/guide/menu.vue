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
      <Tooltip content="添加页面" placement="top">
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
            placeholder="请输入文档path"
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
        <FormItem label="文档描述" prop="desc">
          <Input
            type="textarea"
            v-model="groupForm.desc"
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
            <Radio label="step">步骤</Radio>
            <Radio label="question">问题</Radio>
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
        <FormItem label="标题前缀" prop="seq" v-if="contentForm.type == 'step'">
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
        if (this.nodes.some((item) => item.path.trim() === result)) {
          callback(new Error("该path已经存在"));
          return;
        } else {
          callback();
        }
      }
    };
    return {
      mode: "all",
      currentTreeId: 1,
      nodes: [
        {
          id: 1,
          path: "/",
          title: "工作空间",
          canAdd: true,
          canDelete: true,
          canUpdate: true,
          children: [
            {
              id: 11,
              type: "question",
              title: "工作空间",
              titleAlias: "工作空间",
              canDelete: true,
              canUpdate: true,
            },
            {
              id: 12,
              type: "step",
              title: "工作空间2",
              titleAlias: "工作空间2",
              canDelete: true,
              canUpdate: true,
            },
          ],
        },
        {
          id: 2,
          path: "/ab",
          title: "控制台",
          canAdd: true,
          canDelete: true,
          canUpdate: true,
          children: [
            {
              id: 21,
              type: "question",
              title: "观礼台",
              titleAlias: "观礼台",
              canDelete: true,
              canUpdate: true,
            },
            {
              id: 22,
              type: "step",
              title: "观礼台2",
              titleAlias: "观礼台2",
              canDelete: true,
              canUpdate: true,
            },
          ],
        },
      ],
      // modal and form
      modalType: "",
      modalVisible: false,
      modalTitle: "",
      submitLoading: false,
      groupForm: {
        path: "",
        title: "",
        desc: "",
      },
      groupRule: {
        path: [
          { required: true, validator: validateGroupPath, trigger: "blur" },
        ],
      },
      contentForm: {
        type: "",
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
  watch: {
    currentTab(newValue) {
      this.currentTab = newValue;
    },
  },
  methods: {
    handleTreeClick(node) {
      console.log("node", node);
      this.currentTreeId = node.id;
      if (node.type) {
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
      console.log("delete", node.type ? "content" : "group");
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
          desc: node.desc || "",
        };
      } else {
        this.contentForm = {
          id: node.id,
          path: node.path,
          type: node.type || "",
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
      this.resetForm();
    },
    handleModalOk() {
      const modalType = this.modalType;
      if (modalType === "group") {
        this.$refs["groupForm"].validate((valid) => {
          this.$refs.navMenu.treeMethod("getApi");
          if (valid) {
            this.submitLoading = true;
          }
        });
      } else {
        this.$refs["contentForm"].validate((valid) => {
          if (valid) {
            this.handleModalCancel();
          }
        });
      }
    },
    resetForm() {
      if (this.modalType === "group") {
        this.groupForm = {
          path: "",
          title: "",
          desc: "",
        };
      } else {
        this.contentForm = {
          type: "",
          title: "",
          titleAlias: "",
          seq: "",
        };
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
  justify-content: flex-end;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 10px;
}
.access-component-headers {
  padding: 0px $padding-25;
  border-bottom: $border-width-base $border-style-base $border-color-base;
  @include border-color($border-color-base, $dark-border-color-base);
  margin-top: 12px;
  flex: none;
  display: flex;
  align-items: center;
  font-size: $font-size-large;
  .active {
    border-bottom: 2px solid $primary-color;
  }
  &-container {
    flex: 1;
    height: 40px;
  }
}
.tab-item {
  display: inline-block;
  height: 40px;
  line-height: 40px;
  @include font-color($title-color, $dark-text-color);
  cursor: pointer;
  min-width: 100px;
  max-width: 200px;
  overflow: hidden;
  margin-right: 2px;
  &.active {
    height: 40px;
    color: $primary-color;
    border-radius: 4px 4px 0 0;
    border-bottom: 2px solid $primary-color;
    line-height: 38px;
  }
}
</style>
