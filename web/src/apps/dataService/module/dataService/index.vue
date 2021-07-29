<template>
  <div>
    <navMenu
      :menuFold="navFold"
      @showModal="showModal"
      @handleApiChoosed="handleApiChoosed"
      @on-menu-toggle="handleFold"
      ref="navMenu"
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
        :rules="ruleValidate"
        v-if="modalType === 'group'"
      >
        <FormItem label="业务名称" prop="name">
          <Input
            type="text"
            v-model="groupForm.name"
            placeholder="请输入业务名称"
            style="width: 300px"
          ></Input>
        </FormItem>
        <FormItem label="描述" prop="note">
          <Input
            type="textarea"
            v-model="groupForm.note"
            placeholder="请输入描述"
            style="width: 300px"
          >
          </Input>
        </FormItem>
      </Form>
      <Form
        ref="apiForm"
        :model="apiForm"
        :label-width="100"
        :rules="apiRuleValidate"
        v-if="modalType === 'api'"
      >
        <FormItem label="API模式" required prop="apiType">
          <RadioGroup v-model="apiForm.apiType">
            <Radio label="GUIDE">向导模式</Radio>
            <Radio label="SQL">脚本模式</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="API名称" prop="apiName">
          <Input
            type="text"
            v-model="apiForm.apiName"
            placeholder="请输入名称"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem label="API Path" prop="apiPath">
          <Input
            type="text"
            v-model="apiForm.apiPath"
            placeholder="请输入路径"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem label="API协议" prop="protocol">
          <RadioGroup v-model="apiForm.protocol">
            <Radio label="HTTP">HTTP</Radio>
            <Radio label="HTTPS">HTTPS</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="请求方式" prop="method">
          <Select v-model="apiForm.method" style="width:300px">
            <Option value="GET">GET</Option>
            <Option value="POST">POST</Option>
          </Select>
        </FormItem>
        <FormItem label="返回类型" prop="resType">
          <Select v-model="apiForm.resType" style="width:300px">
            <Option value="JSON">JSON</Option>
          </Select>
        </FormItem>
        <FormItem label="可见范围" prop="previlege">
          <Select v-model="apiForm.previlege" style="width:300px">
            <Option value="WORKSPACE">工作空间</Option>
            <Option value="PRIVATE">私有</Option>
          </Select>
        </FormItem>
        <FormItem label="标签" prop="label">
          <we-tag
            :new-label="$t('message.workspace.addLabel')"
            :tag-list="apiForm.label"
            @add-tag="addTag"
            @delete-tag="deleteTag"
          ></we-tag>
        </FormItem>
        <FormItem label="描述" prop="describe">
          <Input
            type="textarea"
            v-model="apiForm.describe"
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
    <div class="main-wrap" :class="{ 'ds-nav-menu-fold': navFold }">
      <api-congfig :tabDatas="apiTabDatas" @showApiForm="showApiForm" />
    </div>
  </div>
</template>
<script>
import navMenu from "../common/navMenu.vue";
import tag from "@/components/tag/index.vue";
import apiCongfig from "./apiConfig.vue";
import api from "@/common/service/api";
export default {
  components: {
    navMenu,
    apiCongfig,
    "we-tag": tag
  },
  data() {
    return {
      navFold: false,
      confirmLoading: false,
      modalType: "",
      modalVisible: false,
      modalTitle: "",
      groupForm: {
        name: "",
        note: ""
      },
      ruleValidate: {
        name: [
          {
            required: true,
            message: "请输入业务名称",
            trigger: "blur"
          }
        ]
      },
      apiForm: {
        apiType: "GUIDE",
        apiName: "",
        apiPath: "",
        protocol: "HTTP",
        method: "GET",
        resType: "JSON",
        previlege: "WORKSPACE",
        describe: "",
        label: ""
      },
      apiRuleValidate: {
        apiName: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        apiPath: [
          {
            required: true,
            message: "请输入路径",
            trigger: "blur"
          }
        ],
        protocol: [
          {
            required: true,
            message: "请输入协议",
            trigger: "blur"
          }
        ],
        method: [
          {
            required: true,
            message: "请选择请求方式",
            trigger: "blur"
          }
        ],
        resType: [
          {
            required: true,
            message: "请选择返回类型",
            trigger: "blur"
          }
        ],
        previlege: [
          {
            required: true,
            message: "请选择可见范围",
            trigger: "blur"
          }
        ]
      },
      groupData: "",
      apiTabDatas: []
    };
  },
  computed: {},
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // api的基础信息
    //     this.apiData = rst.result;
    //     this.formValidate.approvalNo = this.apiData.approvalVo.approvalNo;
    //     // 更改网页title
    //     document.title = rst.result.aliasName || rst.result.name;
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
    handleFold() {
      this.navFold = !this.navFold;
    },
    showModal(pyload) {
      const { type, data } = pyload;
      this.modalVisible = true;
      this.modalType = type;
      this.modalTitle = type === "group" ? "新增业务流程" : "生成API";
      if (type === "api") {
        this.groupData = data;
      }
    },
    handleModalCancel() {
      this.modalVisible = false;
      this.resetDepartForm();
    },
    handleModalOk() {
      if (this.modalType === "group") {
        this.$refs["groupForm"].validate(valid => {
          console.log(valid);
          this.$refs.navMenu.treeMethod("getApi");
          if (valid) {
            this.confirmLoading = true;
            api
              .fetch(
                `/dss/framework/dbapi/group/create`,
                {
                  workspaceId: this.$route.query.workspaceId,
                  ...this.groupForm
                },
                "post"
              )
              .then(res => {
                console.log(res);
                this.confirmLoading = false;
                this.$refs.navMenu.treeMethod("getApi");
                this.handleModalCancel();
              })
              .catch(() => {
                this.confirmLoading = false;
              });
          }
        });
      } else {
        this.$refs["apiForm"].validate(valid => {
          console.log(valid);
          if (valid) {
            const { id, name } = this.groupData;
            this.$refs.navMenu.treeMethod("addApi", {
              id,
              data: {
                name: this.apiForm.apiName,
                projectId: id,
                projectName: name,
                type: "api"
              }
            });
            this.addTab({ ...this.apiForm, groupId: this.groupData.id });
            this.handleModalCancel();
          }
        });
      }
    },
    addTab(data) {
      const newApis = this.apiTabDatas.map(item => {
        const tmp = { ...item };
        tmp.isActive = false;
        return tmp;
      });
      newApis.push({
        isActive: true,
        name: data.apiName,
        data
      });
      this.apiTabDatas = newApis;
    },
    addTag(label) {
      if (this.apiForm.label) {
        this.apiForm.label += `,${label}`;
      } else {
        this.apiForm.label = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.apiForm.label.split(",");
      const index = tmpArr.findIndex(item => item === label);
      tmpArr.splice(index, 1);
      this.apiForm.label = tmpArr.toString();
    },
    resetDepartForm() {
      if (this.modalType === "group") {
        this.$refs["groupForm"].resetFields();
      } else {
        this.$refs["apiForm"].resetFields();
      }
    },
    showApiForm(apiData) {
      console.log(apiData);
      const { data } = apiData;
      this.apiForm = { ...data };
      this.modalVisible = true;
      this.modalType = "api";
      this.modalTitle = "API属性";
    },
    handleApiChoosed(payload) {
      console.log(payload);
      this.addTab({
        ...payload,
        apiName: payload.name,
        groupId: payload.projectId
      });
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
.main-wrap {
  width: 100%;
  transition: all 0.3s;
  padding-left: 304px;
  &.ds-nav-menu-fold {
    padding-left: 54px;
  }
}
</style>
