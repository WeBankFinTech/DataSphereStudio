<template>
  <Drawer
    title="新建/编辑维度"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    @on-close="cancelCallBack"
    width="920"
    :styles="styles"
    :inner="true"
    :transfer="false"
    :mask-closable="false"
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
      <FormItem label="主题域" prop="_warehouseTheme">
        <Select
          v-model="formState._warehouseTheme"
          placeholder="请选择主题域和主题"
        >
          <Option
            v-for="item in themesList"
            :value="`${item.name}|${item.enName}`"
            :key="item.id"
          >
            {{ item.name }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="负责人" prop="owner">
        <Select
          v-model="formState.owner"
          placeholder="默认为创建用户"
        >
          <Option
            v-for="item in usersList"
            :value="item.name"
            :key="item.name"
          >
            {{ item.name }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="可用角色" prop="principalName">
        <Select
          multiple
          :value="(formState.principalName || '').split(',')"
          @input="formState.principalName = $event.join()"
          placeholder="可用角色"
        >
          <Option
            v-for="item in rolesList"
            :value="item.roleFrontName"
            :key="item.roleId"
          >
            {{ item.roleFrontName }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="描述" prop="comment">
        <Input type="textarea" v-model="formState.comment" placeholder="描述">
        </Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <div class="drawer-footer">
      <Button
        style="margin-right: 8px"
        type="primary"
        @click="handleOk"
        :disabled="refCount !== 0 && mode === 'edit'"
      >
        确定
      </Button>
      <Button @click="handleCancel">取消</Button>
    </div>
  </Drawer>
</template>

<script>
import {getThemesList, getUsersList, getRolesList} from "@/apps/dataModelCenter/service/api/common";
import {
  createDimensions,
  getDimensionsById,
  editDimensions,
} from "@/apps/dataModelCenter/service/api/dimensions";
import mixin from "@/common/service/mixin";

export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  mixins: [mixin],
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
      if (val) this.handlePreFetchData()
      if (val && this.id) this.handleGetById(this.id);
    },
  },
  data() {
    return {
      // 表单数据
      formState: {
        name: "",
        fieldIdentifier: "",
        owner: this.getUserName(),
        comment: "",
        formula: "",
        _warehouseTheme: "",
        principalName: "ALL",
        isAvailable: 1,
      },
      // 验证规则
      ruleValidate: {
        name: [
          {
            required: true,
            message: "名称必填",
            trigger: "submit",
          },
          {
            message: "仅支持中文，下划线，数字",
            pattern: /^[0-9_\u4e00-\u9fa5]+$/g,
            trigger: "submit",
          },
        ],
        fieldIdentifier: [
          {
            required: true,
            message: "标识符必填",
            trigger: "submit",
          },
          {
            message: "仅支持英文，下划线，数字",
            pattern: /^[a-zA-Z0-9_]+$/g,
            trigger: "submit",
          },
        ],
      },
      // 是否加载中
      loading: false,
      // 主题列表
      themesList: [],
      // 用户列表
      usersList: [],
      // 角色列表
      rolesList: [],
      // 引用次数
      refCount: 0,
      // 底部样式
      styles: {
        height: "calc(100% - 55px)",
        overflow: "auto",
        paddingBottom: "53px",
        position: "static",
      },
    };
  },
  methods: {
    /**
     * 获取预置数据
     */
    handlePreFetchData() {
      this.loading = true
      let id = this.getCurrentWorkspaceId()
      Promise.all([getUsersList(id), getRolesList(id), getThemesList()]).then(([userRes, roleRes, themeRes]) => {
        this.loading = false
        this.usersList = userRes.users;
        this.rolesList = roleRes.users;
        this.themesList = themeRes.list;
      }).catch(() => {
        this.loading = false
      })
    },
    // 编辑的时候获取现有数据
    async handleGetById(id) {
      this.loading = true;
      let {detail} = await getDimensionsById(id);
      this.loading = false;
      this.formState.name = detail.name;
      this.formState.isAvailable = detail.isAvailable;
      this.formState.owner = detail.owner;
      this.formState.principalName = detail.principalName;
      this.formState.comment = detail.comment;
      this.formState.fieldIdentifier = detail.fieldIdentifier;
      this.formState._warehouseTheme = `${detail.warehouseThemeName}|${detail.warehouseThemeNameEn}`;
      this.formState.formula = detail.formula;
      this.refCount = detail.refCount;
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
    /**
     * @description 获取格式化数据
     */
    handleGetFormatData() {
      let [warehouseThemeName, warehouseThemeNameEn] =
        this.formState._warehouseTheme.split("|");
      return Object.assign({}, this.formState, {
        _warehouseTheme: undefined,
        warehouseThemeName,
        warehouseThemeNameEn,
      });
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
              await createDimensions(this.handleGetFormatData());
              this.loading = false;
            }
            if (this.mode === "edit") {
              await editDimensions(this.id, this.handleGetFormatData());
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
    }
  },
};
</script>

<style scoped lang="scss">
.drawer-footer {
  width: 100%;
  position: absolute;
  bottom: 0;
  left: 0;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: left;
  background: #fff;
}
</style>
