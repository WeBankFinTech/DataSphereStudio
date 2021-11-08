<template>
  <Modal
    title="新建/编辑"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    @on-cancel="cancelCallBack"
  >
    <Form
      :model="formState"
      ref="formRef"
      :rules="ruleValidate"
      label-position="top"
    >
      <FormItem label="名称" prop="name">
        <Input v-model="formState.name" placeholder="建议为中文名"></Input>
      </FormItem>
      <FormItem label="字段标识" prop="fieldIdentifier">
        <Input v-model="formState.fieldIdentifier" placeholder="必须英文字段名">
        </Input>
      </FormItem>
      <FormItem label="计算公式" prop="formula">
        <Input
          type="textarea"
          v-model="formState.formula"
          placeholder="请输入计算公式，如 substring(字段名)"
        >
        </Input>
      </FormItem>
      <FormItem label="主题域" prop="warehouseThemeName">
        <Select
          v-model="formState.warehouseThemeName"
          placeholder="请选择主题域和主题"
        >
          <Option v-for="item in themesList" :value="item.name" :key="item.id">
            {{ item.name }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="负责人" prop="owner">
        <Input v-model="formState.owner" placeholder="默认为创建用户"></Input>
      </FormItem>

      <FormItem label="可用角色" prop="principalName">
        <Select
          v-model="formState.principalName"
          multiple
          placeholder="可用角色"
        >
          <Option
            v-for="item in authorityList"
            :value="item.value"
            :key="item.value"
          >
            {{ item.label }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="描述" prop="comment">
        <Input type="textarea" v-model="formState.comment" placeholder="描述">
        </Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <template slot="footer">
      <Button @click="handleCancel">取消</Button>
      <Button type="primary" @click="handleOk">确定</Button>
    </template>
  </Modal>
</template>

<script>
import {
  createDimensions,
  getDimensionsById,
  editDimensions,
  getThemesList,
} from "../../service/api";
import storage from "@/common/helper/storage";
let userName = storage.get("baseInfo", "local").username;

export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  props: {
    // 是否可见
    _visible: {
      type: Boolean,
    },
    // 模式
    mode: {
      type: String,
      required: true,
    },
    id: {
      type: Number,
    },
  },
  emits: ["finish", "_changeVisible"],
  watch: {
    _visible(val) {
      if (val && this.id) this.handleGetById(this.id);
    },
  },
  data() {
    return {
      // 表单数据
      formState: {
        name: "",
        fieldIdentifier: "",
        owner: userName,
        comment: "",
        formula: "",
        warehouseThemeName: "",
        principalName: ["ALL"],
        isAvailable: 1,
      },
      // 验证规则
      ruleValidate: {
        name: [
          {
            required: true,
            message: "维度名称必填",
            trigger: "submit",
          },
        ],
        fieldIdentifier: [
          {
            required: true,
            message: "标识符必填",
            trigger: "submit",
          },
        ],
      },
      // 是否加载中
      loading: false,
      // 主题列表
      themesList: [],
      // 角色列表
      authorityList: [
        {
          value: "ALL",
          label: "ALL",
        },
        {
          value: "角色1",
          label: "角色1",
        },
        {
          value: "角色2",
          label: "角色2",
        },
      ],
    };
  },
  mounted() {
    this.handleGetSubjectDomainList();
  },
  methods: {
    // 编辑的时候获取现有数据
    async handleGetById(id) {
      this.loading = true;
      let { detail } = await getDimensionsById(id);
      this.loading = false;
      this.formState.name = detail.name;
      this.formState.isAvailable = detail.isAvailable;
      this.formState.owner = detail.owner;
      this.formState.principalName = detail.principalName.split(",");
      this.formState.comment = detail.comment;
      this.formState.fieldIdentifier = detail.fieldIdentifier;
      this.formState.warehouseThemeName = detail.warehouseThemeName;
      this.formState.formula = detail.formula;
    },
    // 取消清空表单
    cancelCallBack() {
      this.$refs["formRef"].resetFields();
    },
    // 点击取消按钮清空表单并关闭
    handleCancel() {
      this.$refs["formRef"].resetFields();
      this.$emit("_changeVisible", false);
    },
    // 点击确定
    async handleOk() {
      // 触发表单验证
      this.$refs["formRef"].validate(async (valid) => {
        if (valid) {
          // 验证完成
          this.loading = true;
          try {
            if (this.mode === "create") {
              await createDimensions(
                Object.assign({}, this.formState, {
                  principalName: this.formState.principalName.join(","),
                })
              );
              this.loading = false;
            }
            if (this.mode === "edit") {
              await editDimensions(
                this.id,
                Object.assign({}, this.formState, {
                  principalName: this.formState.principalName.join(","),
                })
              );
              this.loading = false;
            }
            // 完成之后，清空表单，关闭弹窗，触发回调
            this.$refs["formRef"].resetFields();
            this.$emit("_changeVisible", false);
            this.$emit("finish");
          } catch (error) {
            this.loading = false;
            console.log(error);
          }
        }
      });
    },
    async handleGetSubjectDomainList() {
      this.loading = true;
      let { list } = await getThemesList();
      this.loading = false;
      this.themesList = list;
    },
  },
};
</script>

<style scoped lang="less"></style>
