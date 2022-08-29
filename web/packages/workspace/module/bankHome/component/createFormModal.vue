<template>
  <Modal
    v-model="show"
    :title="$t('message.workspace.newWorkspace')"
    :closable="false">
    <Form
      :label-width="100"
      label-position="left"
      ref="projectForm"
      :model="formData"
      :rules="formValid">
      <FormItem
        :label="$t('message.workspace.workName')"
        prop="name">
        <Input
          v-model="formData.name"
          :placeholder="$t('message.workspace.enterName')"
          :disabled="actionType === 'modify'" />
      </FormItem>
      <FormItem
        :label="$t('message.workspace.department')"
        prop="department">
        <Select
          v-model="formData.department"
          :placeholder="$t('message.workspace.selectDepartment')">
          <Option
            v-for="(item, index) in departments"
            :label="item.deptName"
            :value="String(item.id)"
            :key="index"/>
        </Select>
      </FormItem>
      <FormItem
        :label="$t('message.workspace.label')"
        prop="label">
        <we-tag
          :new-label="$t('message.workspace.addLabel')"
          :tag-list="formData.label"
          @add-tag="addTag"
          @delete-tag="deleteTag"></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.workspace.description')"
        prop="description">
        <Input
          v-model="formData.description"
          type="textarea"
          :placeholder="$t('message.workspace.pleaseInputWorkspaceDesc')" />
      </FormItem>
    </Form>
    <div slot="footer">
      <Button
        type="text"
        size="large"
        @click="Cancel">{{$t('message.workspace.cancel')}}</Button>
      <Button
        type="primary"
        size="large"
        @click="Ok">{{$t('message.workspace.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import tag from '@dataspherestudio/shared/components/tag/index.vue';
import { GetDepartments, CheckWorkspaceNameExist } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
export default {
  components: {
    'we-tag': tag,
  },
  props: {
    projectData: {
      type: Object,
      default: () => {},
    },
    addProjectShow: {
      type: Boolean,
      default: false,
    },
    actionType: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      show: true,
      departments: [],
    };
  },
  computed: {
    formData() {
      return this.projectData;
    },
    formValid() {
      return {
        name: [
          { required: true, message: this.$t('message.workspace.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.workspace.nameLength')}128`, max: 128 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.workspace.validNameDesc'), trigger: 'blur' },
          { validator: this.checkNameExist, message: this.$t('message.workspace.validNameExist'), trigger: 'blur' },
        ],
        description: [
          { required: true, message: this.$t('message.workspace.pleaseInputWorkspaceDesc'), trigger: 'blur' },
        ],
        department: [
          { required: true, message: this.$t('message.workspace.selectDepartment'), trigger: 'change' },
        ],
      }
    }
  },
  mounted() {
    GetDepartments().then((res) => {
      this.departments = res.deptList;
    });
  },
  watch: {
    addProjectShow(val) {
      this.ProjectShow = val;
    },
    ProjectShow(val) {
      this.$emit('show', val);
    },
  },
  methods: {
    checkNameExist(rule, value, callback) {
      CheckWorkspaceNameExist({ name: value }).then((res) => {
        if (res.workspaceNameExists) {
          callback(new Error(this.$t('message.workspace.Not')));
        } else {
          callback();
        }
      });
    },
    Ok() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          this.$emit('confirm', this.formData);
          this.ProjectShow = false;
        } else {
          this.$Message.warning(this.$t('message.workspace.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ProjectShow = false;
    },
    addTag(label) {
      if (this.formData.label) {
        this.formData.label += `,${label}`;
      } else {
        this.formData.label = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.formData.label.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.projectData.label = tmpArr.toString();
    }
  },
};
</script>
