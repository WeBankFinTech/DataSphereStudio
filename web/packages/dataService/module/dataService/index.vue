<template>
  <div style="height:100%">
    <navMenu
      :menuFold="navFold"
      :currentTreeId="apiTabDatas.find(i=>i.isActive) ? apiTabDatas.find(i=>i.isActive).id : null"
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
        v-show="modalType === 'group'"
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
        v-show="modalType !== 'group'"
      >
        <FormItem label="API模式" required prop="apiType">
          <RadioGroup v-model="apiForm.apiType">
            <Radio label="GUIDE" :disabled="modalType === 'updateApi'"
            >向导模式</Radio
            >
            <Radio label="SQL" :disabled="modalType === 'updateApi'"
            >脚本模式</Radio
            >
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
            :disabled="modalType === 'updateApi'"
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
      <api-congfig
        :tabDatas="apiTabDatas"
        @showApiForm="showApiForm"
        @removeTab="removeTab"
        @changeTab="changeTab"
        @updateApiData="updateApiData"
      />
      <Spin v-show="loadingData" size="large" fix />
    </div>
  </div>
</template>
<script>
import navMenu from "../common/navMenu.vue";
import tag from '@dataspherestudio/shared/components/tag/index.vue';
import apiCongfig from "./apiConfig.vue";
import api from '@dataspherestudio/shared/common/service/api';
import _ from 'lodash';

export default {
  components: {
    navMenu,
    apiCongfig,
    "we-tag": tag
  },
  data() {
    const validateGroupName = (rule, value, callback) => {
      const result = value && value.trim();
      const reg = /^[\u4e00-\u9fa5a-zA-Z][\w_\u4e00-\u9fa5]{3,19}$/g;
      if (!reg.test(result)) {
        callback(
          new Error(
            "支持汉字、英文、数字、下划线（_），以英文或汉字开头，4~20个字符"
          )
        );
      } else {
        if (this.groupDatas.some(item => item.name.trim() === result)) {
          callback(new Error("该名称已经存在"));
          return;
        } else {
          callback();
        }
      }
    };
    const validateAPIName = (rule, value, callback) => {
      const result = value && value.trim();
      const reg = /^[\u4e00-\u9fa5a-zA-Z][\w_\u4e00-\u9fa5]{3,19}$/g;
      if (!reg.test(result)) {
        callback(
          new Error(
            "支持汉字、英文、数字、下划线（_），以英文或汉字开头，4~20个字符"
          )
        );
      } else {
        const apis = (this.groupData && this.groupData.apis) || [];
        const isUpdate = this.modalType === "updateApi";
        let isHad = apis.some(item => item.name.trim() === result);
        if (isUpdate) {
          isHad = false;
          const groupData = this.allProjectTree.find(item => {
            return item.groupId + "" === this.apiForm.groupId + "";
          });
          if (groupData) {
            const apis2 = groupData.apis || [];
            apis2.forEach(item => {
              if (
                item.name.trim() === result &&
                item.id + "" !== this.apiForm.id + ""
              ) {
                isHad = true;
              }
            });
          }
        }
        if (isHad) {
          callback(new Error("该名称已经存在"));
          return;
        } else {
          callback();
        }
      }
    };
    const validateAPIPath = (rule, value, callback) => {
      const result = value && value.trim();
      const reg = /^[\w_-]{4,199}$/g;
      if (!reg.test(result)) {
        callback(
          new Error(
            "支持英文、数字、下划线（_）、连字符（-)，且不能以正斜线（/）开头，4到200个字符"
          )
        );
      } else {
        const isUpdate = this.modalType === "updateApi";
        const isHad = this.allProjectTree.some(item => {
          const { apis } = item;
          return apis.some(api => {
            return isUpdate
              ? api.path.trim() === result && api.id !== this.apiForm.id
              : api.path.trim() === result;
          });
        });
        if (isHad) {
          callback(new Error("该路径已经存在，请更换"));
          return;
        } else {
          callback();
        }
      }
    };
    return {
      loadingData: false,
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
            validator: validateGroupName,
            trigger: "blur"
          }
        ]
      },
      apiForm: {
        apiType: "GUIDE",
        apiName: "",
        apiPath: "",
        protocol: "HTTP",
        method: "POST",
        resType: "JSON",
        previlege: "WORKSPACE",
        describe: "",
        label: ""
      },
      apiRuleValidate: {
        apiName: [
          {
            required: true,
            validator: validateAPIName,
            trigger: "blur"
          }
        ],
        apiPath: [
          {
            required: true,
            validator: validateAPIPath,
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
      apiTabDatas: [],
      groupDatas: [],
      allProjectTree: []
    };
  },
  computed: {},
  methods: {
    handleFold() {
      this.navFold = !this.navFold;
    },
    showModal(pyload) {
      const { type, data, groupDatas } = pyload;
      this.modalVisible = true;
      this.modalType = type;
      this.modalTitle = type === "group" ? "新增业务流程" : "生成API";
      console.log(pyload);
      if (type === "api") {
        this.groupData = data;
        this.allProjectTree = groupDatas;
      } else {
        this.groupDatas = groupDatas;
      }
    },
    handleModalCancel() {
      this.modalVisible = false;
      this.resetForm();
    },
    handleModalOk() {
      const modalType = this.modalType;
      if (modalType === "group") {
        this.$refs["groupForm"].validate(valid => {
          this.$refs.navMenu.treeMethod("getApi");
          if (valid) {
            this.confirmLoading = true;
            const params = {
              workspaceId: this.$route.query.workspaceId,
              ...this.groupForm
            };
            params.name = params.name.trim();
            api
              .fetch(`/dss/data/api/group/create`, params, "post")
              .then(() => {
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
          if (valid) {
            if (modalType === "api") {
              const { groupId, name } = this.groupData;
              const tempId = Date.now();
              const params = { ...this.apiForm };
              params.apiName = params.apiName.trim();
              params.apiPath = params.apiPath.trim();
              this.$refs.navMenu.treeMethod("addApi", {
                id: groupId,
                data: {
                  name: params.apiName,
                  projectId: groupId,
                  projectName: name,
                  tempId,
                  type: "api"
                }
              });
              this.addTab({
                ...params,
                groupId: this.groupData.groupId,
                tempId
              });
            } else {
              this.updateApi();
            }

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
        id: data.id || data.tempId,
        data,
        originData: _.cloneDeep(data)
      });
      this.apiTabDatas = newApis;
    },
    updateApi() {
      this.apiTabDatas = this.apiTabDatas.map(item => {
        const temp = { ...item };
        if (temp.isActive) {
          temp.data = { ...temp.data, ...this.apiForm };
        }
        return temp;
      });
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
    resetForm() {
      if (this.modalType === "group") {
        this.$refs["groupForm"].resetFields();
        this.groupForm = {
          name: "",
          note: ""
        };
      } else {
        this.$refs["apiForm"].resetFields();
        this.apiForm = {
          apiType: "GUIDE",
          apiName: "",
          apiPath: "",
          protocol: "HTTP",
          method: "POST",
          resType: "JSON",
          previlege: "WORKSPACE",
          describe: "",
          label: ""
        };
      }
    },
    showApiForm(apiData) {
      const { data } = apiData;
      this.apiForm = { ...data };
      this.modalVisible = true;
      this.modalType = "updateApi";
      this.modalTitle = "API属性";
    },
    handleApiChoosed(payload) {
      if (payload.type === "api") {
        const { allProjectTree, ...rest } = payload;
        const { id, tempId } = rest;
        const newApis = [...this.apiTabDatas];
        const hitIndex = newApis.findIndex(
          item => item.id === id || item.id === tempId
        );
        this.allProjectTree = allProjectTree;
        if (hitIndex !== -1) {
          this.apiTabDatas = newApis.map((item, index) => {
            const tmp = { ...item };
            tmp.isActive = hitIndex === index;
            return tmp;
          });
          return;
        }
        this.loadingData = true;
        api
          .fetch(`/dss/data/api/detail?apiId=${id}`, {}, "get")
          .then(res => {
            this.loadingData = false;
            const data = res && res.detail;
            if (data) {
              this.addTab({
                ...rest,
                apiName: payload.name,
                groupId: payload.projectId,
                ...data,
                resType: data.resType || "JSON"
              });
            }
          })
          .catch(() => {
            this.loadingData = false;
          });
      } else if (payload.type === "saveApi") {
        const newApis = this.apiTabDatas.map(item => {
          let tmp = { ...item };
          const { data, apiData } = payload;
          if (tmp.isActive) {
            const newData = { ...apiData, id: data.id, path: apiData.apiPath };
            tmp = {
              ...item,
              apiName: data.name,
              id: data.id,
              data: newData,
              originData: {...newData}
            };
          }
          return tmp;
        });
        this.apiTabDatas = newApis;
      }
    },
    removeTab(id) {
      const newApis = [...this.apiTabDatas];
      const hitIndex = newApis.findIndex(item => item.id === id);
      newApis.splice(hitIndex, 1);
      if (this.apiTabDatas[hitIndex].isActive && newApis.length > 0) {
        newApis[0].isActive = true;
      }
      this.apiTabDatas = newApis;
    },
    changeTab(id) {
      this.apiTabDatas = this.apiTabDatas.map(item => {
        return { ...item, isActive: item.id === id };
      });
    },
    updateApiData(data) {
      if (data.id) {
        this.updateTab(data);
      }
      this.$refs.navMenu.treeMethod("getApi", data);
    },
    updateTab(data) {
      const newApis = this.apiTabDatas.map(item => {
        let tmp = { ...item };
        if (tmp.id === data.id) {
          tmp = { ...item, name: data.apiName, data, originData: _.cloneDeep(data) };
        }
        return tmp;
      });
      this.apiTabDatas = newApis;
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.modalFooter {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 10px;
}
.main-wrap {
  width: 100%;
  height: 100%;
  transition: all 0.3s;
  padding-left: 304px;
  @include bg-color(#fff, $dark-base-color);
  &.ds-nav-menu-fold {
    padding-left: 54px;
  }
}
</style>
