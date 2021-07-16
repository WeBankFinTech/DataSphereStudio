<template>
  <div>
    <navMenu @showModal="showModal" />
    <Modal
      v-model="modalVisible"
      :title="modalTitle"
      @on-cancel="handleModalCancel"
      footer-hide
    >
      <Form
        ref="groupForm"
        :label-width="100"
        :rules="ruleValidate"
        v-if="modalType === 'group'"
      >
        <FormItem label="业务名称" required prop="groupName">
          <Input
            type="text"
            v-model="groupForm.groupName"
            placeholder="请输入业务名称"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem label="描述" prop="description">
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
        ref="apiForm"
        :label-width="100"
        :rules="ruleValidate"
        v-if="modalType !== 'api'"
      >
        <FormItem label="API模式" required prop="apiType">
          <RadioGroup v-model="apiForm.apiType">
            <Radio label="GUIDE">向导模式</Radio>
            <Radio label="SQL">脚本模式</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="SQL模式" required prop="sqlType">
          <RadioGroup v-model="apiForm.sqlType">
            <Radio label="BASE">基础SQL</Radio>
            <Radio label="ADCANCED">高级SQL</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="API名称" required prop="apiName">
          <Input
            type="text"
            v-model="apiForm.apiName"
            placeholder="请输入名称"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem label="API Path" required prop="apiPath">
          <Input
            type="text"
            v-model="apiForm.apiPath"
            placeholder="请输入路径"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem label="API协议" required prop="protocol">
          <CheckboxGroup v-model="apiForm.protocol">
            <Checkbox label="HTTP"></Checkbox>
            <Checkbox label="HTTPS"></Checkbox>
          </CheckboxGroup>
        </FormItem>
        <FormItem label="请求方式" required prop="method">
          <Select v-model="apiForm.method" style="width:300px">
            <Option value="GET">GET</Option>
            <Option value="POST">POST</Option>
          </Select>
        </FormItem>
        <FormItem label="返回类型" required prop="returnType">
          <Select v-model="apiForm.returnType" style="width:300px">
            <Option value="GET">GET</Option>
            <Option value="POST">POST</Option>
          </Select>
        </FormItem>
        <FormItem label="可见范围" required prop="previlege">
          <Select v-model="apiForm.previlege" style="width:300px">
            <Option value="WORKSPACE">工作空间</Option>
            <Option value="PRIVATE">私有</Option>
          </Select>
        </FormItem>
        <FormItem label="标签" prop="label">
          <Input
            type="textarea"
            v-model="apiForm.description"
            placeholder="请输入描述"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem label="描述" prop="description">
          <Input
            type="textarea"
            v-model="apiForm.description"
            placeholder="请输入描述"
            style="width: 300px"
          >
          </Input>
        </FormItem>
      </Form>
      <slot name="footer">
        <div class="modalFooter">
          <Button @click="handleModalCancel()" size="large">{{
            $t("message.workspace.cancel")
          }}</Button>
          <Button
            type="primary"
            size="large"
            :loading="confirmLoading"
            @click="handleModalOk()"
            style="margin-left: 10px"
            >{{ $t("message.workspace.ok") }}</Button
          >
        </div></slot
      >
    </Modal>
  </div>
</template>
<script>
import navMenu from "../common/navMenu.vue";
// import api from "@/common/service/api";
export default {
  components: {
    navMenu
  },
  data() {
    return {
      confirmLoading: false,
      modalType: "",
      modalVisible: false,
      modalTitle: "",
      groupForm: {
        groupName: "",
        description: ""
      },
      ruleValidate: {
        groupName: [
          {
            required: true,
            message: "业务名称不能为空",
            trigger: "blur"
          }
        ]
      },
      apiForm: {
        apiType: "",
        sqlType: "",
        apiName: "",
        apiPath: "",
        protocol: [],
        method: "",
        returnType: "",
        previlege: "",
        description: ""
      }
    };
  },
  computed: {},
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // 加工api信息tab的数据
    //     this.apiInfoData = [
    //       { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
    //       { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
    //       { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
    //     ]
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  methods: {
    showModal(type) {
      this.modalVisible = true;
      this.modalType = type;
      this.modalTitle = type === "group" ? "新增业务流程" : "api";
    },
    handleModalCancel() {
      this.modalVisible = false;
      this.resetDepartForm();
    },
    handleModalOk() {
      this.$refs["groupForm"].validate(valid => {
        console.log(valid);
      });
    },
    resetDepartForm() {
      this.$refs["apiForm"].resetFields();
      this.parentErrorTip = "";
      this.editingData = "";
      this.apiForm = {
        parentId: "",
        deptName: "",
        leader: "",
        phone: "",
        email: ""
      };
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.modalFooter {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 10px;
}
</style>
