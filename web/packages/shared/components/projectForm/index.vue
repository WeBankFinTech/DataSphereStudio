<template>
  <Modal
    v-model="ProjectShow"
    :title="
      actionType === 'add'
        ? $t('message.workflow.projectDetail.createProject')
        : $t('message.workflow.projectDetail.editorProject')
    "
    :closable="false"
  >
    <Form
      :label-width="100"
      label-position="left"
      ref="projectForm"
      :model="projectDataCurrent"
      :rules="formValid"
      class="project_form"
    >
      <FormItem
        :label="$t('message.workflow.projectDetail.projectName')"
        prop="name"
      >
        <Input
          v-model="projectDataCurrent.name"
          :placeholder="$t('message.workflow.enterName')"
          :disabled="actionType === 'modify'"
        ></Input>
      </FormItem>
      <FormItem
        v-if="!framework"
        :label="$t('message.workflow.projectDetail.product')"
        prop="product"
      >
        <Input
          v-model="projectDataCurrent.product"
          :placeholder="$t('message.workflow.enterName')"
        >
        </Input>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.projectDetail.appArea')"
        prop="applicationArea"
      >
        <Select
          v-model="projectDataCurrent.applicationArea"
          :placeholder="$t('message.workflow.projectDetail.selectAppArea')"
        >
          <Option
            v-for="(item, index) in applicationAreaMap"
            :label="item"
            :value="index"
            :key="index"
          />
        </Select>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.projectDetail.publishPermissions')"
        prop="releaseUsers"
      >
        <luban-select
          v-model="projectDataCurrent.releaseUsers"
          multiple
          filterable
          :placeholder="$t('message.workflow.projectDetail.userAllowedPublish')"
        >
          <Option
            v-for="(item, index) in releaseUsers"
            :label="item"
            :value="item"
            :key="index"
          />
        </luban-select>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.projectDetail.editPermissions')"
        prop="editUsers"
      >
        <luban-select
          v-model="projectDataCurrent.editUsers"
          :disabled-tags="[projectDataCurrent.createBy]"
          multiple
          filterable
          :placeholder="$t('message.workflow.projectDetail.usersAllowedToEdit')"
        >
          <Option
            v-for="(item, index) in editUsersMap"
            :disabled="item == projectDataCurrent.createBy"
            :label="item"
            :value="item"
            :key="index"
          />
        </luban-select>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.projectDetail.viewPermissions')"
        prop="accessUsers"
      >
        <luban-select
          v-model="projectDataCurrent.accessUsers"
          :disabled-tags="[projectDataCurrent.createBy]"
          multiple
          filterable
          :placeholder="$t('message.workflow.projectDetail.usersAllowedToView')"
        >
          <Option
            v-for="(item, index) in accessUsersMap"
            :disabled="item == projectDataCurrent.createBy"
            :label="item"
            :value="item"
            :key="index"
          />
        </luban-select>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.projectDetail.devProcess')"
        prop="devProcessList"
      >
        <CheckboxGroup v-model="projectDataCurrent.devProcessList">
          <Checkbox
            v-for="item in devProcess"
            :label="item.dicValue"
            :key="item.dicKey"
          >
            <SvgIcon class="icon-style" :icon-class="item.icon" />
            <span>{{ item.dicName }}</span>
          </Checkbox>
        </CheckboxGroup>
      </FormItem>
      <FormItem
        v-if="framework"
        :label="$t('message.workflow.projectDetail.orchestratorMode')"
        prop="orchestratorModeList"
      >
        <CheckboxGroup v-model="projectDataCurrent.orchestratorModeList">
          <Checkbox
            v-for="item in orchestratorModeList.list"
            :label="item.dicKey"
            :key="item.dicKey"
          >
            <span class="icon-bar">
              <SvgIcon class="icon-style" :icon-class="item.icon" />
              <span style="margin-left: 10px">{{ item.dicName }}</span>
            </span>
          </Checkbox>
        </CheckboxGroup>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.projectDetail.business')"
        prop="business"
      >
        <we-tag
          :new-label="$t('message.workflow.projectDetail.addBusiness')"
          :tag-list="projectDataCurrent.business"
          @add-tag="addTag"
          @delete-tag="deleteTag"
        ></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.projectDetail.projectDesc')"
        prop="description"
      >
        <Input
          v-model="projectDataCurrent.description"
          type="textarea"
          :placeholder="
            $t('message.workflow.projectDetail.pleaseInputProjectDesc')
          "
        ></Input>
      </FormItem>
    </Form>
    <div slot="footer">
      <Button type="text" size="large" @click="Cancel">{{
        $t("message.workflow.cancel")
      }}</Button>
      <Button
        type="primary"
        size="large"
        :disabled="submiting"
        :loading="submiting"
        @click="Ok"
      >{{ $t("message.workflow.ok") }}</Button
      >
    </div>
  </Modal>
</template>
<script>
import storage from '@dataspherestudio/shared/common/helper/storage';
import tag from "@dataspherestudio/shared/components/tag/index.vue";
import lubanSelect from "@dataspherestudio/shared/components/select/index.vue";
import _ from "lodash";
import {
  GetWorkspaceUserList,
  GetDicList,
  CheckProjectNameRepeat
} from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
export default {
  components: {
    "we-tag": tag,
    "luban-select": lubanSelect,
  },
  props: {
    projectData: {
      type: Object,
      default: null,
    },
    actionType: {
      type: String,
      default: "",
    },
    applicationAreaMap: {
      type: Array,
      default: () => [],
    },
    framework: {
      type: Boolean,
      default: false,
    },
    orchestratorModeList: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      test: [],
      ProjectShow: false,
      originBusiness: "",
      editUsersMap: [],
      accessUsersMap: [],
      releaseUsers: [],
      devProcess: [
        {
          id: 1,
          icon: "",
          title: "开发中心",
          checked: false,
        },
        {
          id: 2,
          icon: "",
          title: "生产中心",
          checked: false,
        },
      ],
      devProcessList: [],
      selectCompiling: [],
      projectDataCurrent: {},
      submiting: false,
    };
  },
  computed: {
    formValid() {
      let validateName = async (rule, value, callback) => {
        let currentWorkspaceName = storage.get("currentWorkspace")
          ? storage.get("currentWorkspace").name
          : null;
        let username = storage.get("baseInfo", "local")
          ? storage.get("baseInfo", "local").username
          : null;
        // 校验是否重名
        let repeat
        try {
          const res = await CheckProjectNameRepeat(value)
          repeat = res.repeat
        } catch (error) {
          //
        }
        if ((currentWorkspaceName && username && value.match(currentWorkspaceName)) || value.match(username)) {
          callback(
            new Error(this.$t("message.workflow.projectDetail.validateName"))
          );
        } else if (repeat && this.actionType === 'add') {
          callback(
            new Error(this.$t("message.workflow.projectDetail.nameUnrepeatable"))
          );
        } else {
          callback();
        }
      };
      return {
        name: [
          {
            required: true,
            message: this.$t("message.workflow.enterName"),
            trigger: "blur",
          },
          { message: `${this.$t("message.workflow.nameLength")}150`, max: 150 },
          {
            type: "string",
            pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
            message: this.$t("message.workflow.validNameDesc"),
            trigger: "blur",
          },
          { validator: validateName, trigger: "blur" },
        ],
        description: [
          {
            required: true,
            message: this.$t(
              "message.workflow.projectDetail.pleaseInputProjectDesc"
            ),
            trigger: "blur",
          },
        ],
        product: [
          {
            required: true,
            message: this.$t("message.workflow.projectDetail.selectProduct"),
            trigger: "change",
          },
        ],
        applicationArea: [
          {
            required: true,
            message: this.$t("message.workflow.projectDetail.selectAppArea"),
            trigger: "change",
            type: "number",
          },
        ],
        devProcessList: [
          {
            required: true,
            message: this.$t("message.workflow.projectDetail.pleaseSelect"),
            trigger: "blur",
            type: "array",
          },
        ],
        orchestratorModeList: [
          {
            required: true,
            message: this.$t("message.workflow.projectDetail.pleaseSelect"),
            trigger: "blur",
            type: "array",
          },
        ],
        // releaseUsers: [
        //   {
        //     required: true,
        //     message: this.$t(
        //       "message.workflow.projectDetail.userAllowedPublish"
        //     ),
        //     trigger: "change",
        //     type: "array",
        //   },
        // ],
      };
    },
  },
  mounted() {
    GetWorkspaceUserList({ workspaceId: +this.$route.query.workspaceId }).then(
      (res) => {
        this.accessUsersMap = res.users.accessUsers;
        this.editUsersMap = res.users.editUsers;
        this.releaseUsers = res.users.releaseUsers;
      }
    );
    this.getData();
  },
  watch: {
    ProjectShow(val) {
      if (val) {
        this.originBusiness = this.projectDataCurrent.business;
      }
    },
    projectData(value) {
      const cloneObj = _.cloneDeep(value);
      this.projectDataCurrent = cloneObj;
    },
  },
  methods: {
    getData() {
      const params = {
        parentKey: "p_develop_process",
        workspaceId: this.$route.query.workspaceId,
      };
      GetDicList(params).then((res) => {
        this.devProcess = res.list;
        this.$emit("getDevProcessData", this.devProcess);
      });
    },
    Ok() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          this.submiting = true;
          this.$emit("confirm", this.projectDataCurrent, (success) => {
            if (success) this.ProjectShow = false;
            this.submiting = false;
          });
          this.$refs.projectForm.resetFields();
        } else {
          this.submiting = false;
          this.$Message.warning(this.$t("message.workflow.failedNotice"));
        }
      });
    },
    Cancel() {
      this.ProjectShow = false;
      this.$refs.projectForm.resetFields();
      this.projectData.business = this.originBusiness;
    },
    addTag(label) {
      if (this.projectDataCurrent.business) {
        this.projectDataCurrent.business += `,${label}`;
      } else {
        this.projectDataCurrent.business = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.projectDataCurrent.business.split(",");
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.projectData.business = tmpArr.toString();
    },
    showProject(params) {
      this.ProjectShow = true
      // 新增只有一项自动勾选
      if (this.orchestratorModeList && this.orchestratorModeList.list.length === 1 && params.name) {
        params.orchestratorModeList = [this.orchestratorModeList.list[0].dicKey]
      }
      this.projectDataCurrent = {...params}
    }
  },
};
</script>
<style lang="scss" scoped>
.icon-style {
  vertical-align: middle;
  margin-left: 10px;
  font-size: 16px;
  color: black;
}
.project_form {
  height: 60vh;
  overflow-y: auto;
  padding: 5px;
  max-height: 500px;
}
</style>
