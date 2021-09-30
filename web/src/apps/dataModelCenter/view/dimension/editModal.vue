<template>
  <Modal title="新建/编辑" v-model="visible" @on-cancel="cancelCallBack">
    <Form
      :model="formState"
      ref="formRef"
      :rules="ruleValidate"
      label-position="top"
    >
      <FormItem label="名称" prop="name">
        <Input v-model="formState.name" placeholder="名称"></Input>
      </FormItem>
      <FormItem label="字段标识" prop="formula">
        <Input
          v-model="formState.fieldIdentifier"
          placeholder="字段标识"
        ></Input>
      </FormItem>
      <FormItem label="计算公式" prop="formula">
        <Input
          type="textarea"
          v-model="formState.formula"
          placeholder="计算公式"
        ></Input>
      </FormItem>
      <FormItem label="主题域" prop="warehouseThemeName">
        <Select v-model="formState.warehouseThemeName" placeholder="主题域">
          <Option v-for="item in themesList" :value="item.id" :key="item.id">
            {{ item.name }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="负责人">
        <Input v-model="formState.owner" placeholder="负责人"></Input>
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
        <Input
          type="textarea"
          v-model="formState.comment"
          placeholder="描述"
        ></Input>
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
export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  computed: {
    visible: {
      get() {
        return this._visible;
      },
      set(val) {
        this.$emit("_changeVisible", val);
      },
    },
  },
  props: {
    // 是否可见
    _visible: {
      type: Boolean,
      required: true,
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
      // 验证规则
      ruleValidate: {},
      // 是否加载中
      loading: false,
      // 表单数据
      formState: {
        name: "",
        fieldIdentifier: "",
        owner: "",
        comment: "",
        formula: "",
        warehouseThemeName: "",
        principalName: [],
      },
      themesList: [
        {
          id: "New York",
          name: "New York",
        },
        {
          id: "London",
          name: "London",
        },
      ],
      authorityList: [
        {
          value: "New York",
          label: "New York",
        },
        {
          value: "London",
          label: "London",
        },
      ],
    };
  },
  mounted() {
    // this.handleGetSubjectDomainList();
  },
  methods: {
    async handleGetById(id) {
      this.loading = true;
      let { detail } = await getDimensionsById(id);
      this.loading = false;
      this.formState.name = detail.name;
      this.formState.owner = detail.owner;
      this.formState.principalName = detail.principalName.split(",");
      this.formState.comment = detail.comment;
      this.formState.fieldIdentifier = detail.fieldIdentifier;
      this.formState.warehouseThemeName = detail.warehouseThemeName;
      this.formState.formula = detail.formula;
    },
    cancelCallBack() {
      this.$refs["formRef"].resetFields();
    },
    handleCancel() {
      this.$refs["formRef"].resetFields();
      this.$emit("_changeVisible", false);
    },
    async handleOk() {
      this.$refs["formRef"].validate(async (valid) => {
        if (valid) {
          try {
            if (this.mode === "create") {
              this.loading = true;
              await createDimensions(
                Object.assign({}, this.formState, {
                  principalName: this.formState.principalName.join(","),
                })
              );
              this.loading = false;
            }
            if (this.mode === "edit") {
              this.loading = true;
              await editDimensions(
                this.id,
                Object.assign({}, this.formState, {
                  principalName: this.formState.principalName.join(","),
                })
              );
              this.loading = false;
            }
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
    // async handleGetSubjectDomainList() {
    //   this.loading = true;
    //   let { list } = await getThemesList();
    //   this.loading = false;
    //   this.themesList = list;
    // },
  },
};
</script>

<style scoped lang="less"></style>
