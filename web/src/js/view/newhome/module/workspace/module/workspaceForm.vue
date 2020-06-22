<template>
  <Modal
    v-model="ProjectShow"
    :title="actionType === 'add' ? $t('message.workspace.NEWWORKSPACE') : $t('message.workspace.EIDITOR')"
    :closable="false">
    <Form
      :label-width="100"
      label-position="left"
      ref="projectForm"
      :model="projectDataCurrent"
      :rules="formValid"
      v-if="ProjectShow">
      <FormItem
        :label="$t('message.workspace.WORKNAME')"
        prop="workspaceName">
        <Input
          v-model="projectDataCurrent.workspaceName"
          :placeholder="$t('message.newConst.enterName')"
          :disabled="actionType === 'modify'"></Input>
      </FormItem>
      <FormItem
        :label="$t('message.workspace.DEPARTMNET')"
        prop="department">
        <Select
          v-model="projectDataCurrent.department"
          :placeholder="$t('message.workspace.SELECTDEPARTMNET')">
          <Option
            v-for="(item, index) in departments"
            :label="item.frontName"
            :value="item.frontName"
            :key="index"/>
        </Select>
      </FormItem>
      <FormItem
        :label="$t('message.workspace.PRODUCT')"
        prop="productName">
        <Input
          v-model="projectDataCurrent.productName"
          :placeholder="$t('message.workspace.ENTERPRODUCTNAME')"></Input>
      </FormItem>
      <FormItem
        :label="$t('message.workspace.LABEL')"
        prop="business">
        <we-tag
          :new-label="$t('message.project.addBusiness')"
          :tag-list="projectDataCurrent.tags"
          @add-tag="addTag"
          @delete-tag="deleteTag"></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.workspace.DESCRIPTION')"
        prop="description">
        <Input
          v-model="projectDataCurrent.description"
          type="textarea"
          :placeholder="$t('message.project.pleaseInputProjectDesc')"></Input>
      </FormItem>
    </Form>
    <div slot="footer">
      <Button
        type="text"
        size="large"
        @click="Cancel">{{$t('message.newConst.cancel')}}</Button>
      <Button
        type="primary"
        size="large"
        @click="Ok">{{$t('message.newConst.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import api from '@/js/service/api';
import tag from '@js/component/tag/index.vue'
export default {
  components: {
    'we-tag': tag,
  },
  props: {
    projectData: {
      type: Object,
      default: null,
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
      ProjectShow: false,
      departments: [],
      originBusiness: '',
    };
  },
  computed: {
    projectDataCurrent() {
      return this.projectData;
    },
    formValid() {
      return {
        workspaceName: [
          { required: true, message: this.$t('message.newConst.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.newConst.nameLength')}128`, max: 128 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.newConst.validNameDesc'), trigger: 'blur' },
        ],
        description: [
          { required: true, message: this.$t('message.project.pleaseInputProjectDesc'), trigger: 'blur' },
        ],
        productName: [
          { required: true, message: this.$t('message.project.selectProduct'), trigger: 'change' },
        ],
        department: [
          { required: true, message: this.$t('message.project.selectAppArea'), trigger: 'change', type: 'string' },
        ],
      }
    }
  },
  mounted() {
    api.fetch('/dss/listDepartments', 'get').then((res) => {
      this.departments = res.departments
    })
  },
  watch: {
    addProjectShow(val) {
      this.ProjectShow = val;
    },
    ProjectShow(val) {
      if (val) {
        this.originBusiness = this.projectDataCurrent.tags;
      }
      this.$emit('show', val);
    },
  },
  methods: {
    Ok() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          this.$emit('confirm', this.projectDataCurrent);
          this.ProjectShow = false;
        } else {
          this.$Message.warning(this.$t('message.newConst.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ProjectShow = false;
      this.projectData.tags = this.originBusiness;
    },
    addTag(label) {
      if (this.projectDataCurrent.tags) {
        this.projectDataCurrent.tags += `,${label}`;
      } else {
        this.projectDataCurrent.tags = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.projectDataCurrent.tags.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.projectData.tags = tmpArr.toString();
    }
  },
};
</script>
