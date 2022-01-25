<template>
  <Form
    :label-width="100"
    label-position="left"
    ref="copyProjectForm"
    :model="copyProjectData"
    :rules="copyFormValid">
    <FormItem
      :label="$t('message.workflow.projectDetail.projectName')"
      prop="name">
      <Input
        v-model="copyProjectData.name"
        :placeholder="$t('message.workflow.projectDetail.inputProjectName')"></Input>
    </FormItem>
  </Form>
</template>
<script>
import api from '@/common/service/api';
export default {
  name: 'Copy',
  props: {
    currentProjectData: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      copyProjectData: {
        id: '',
        name: '',
        version: '',
      },
      // copyFormValid: {
      //     name: [
      //         { required: true, message: '请输入名称', trigger: 'blur' },
      //         { message: '名称长度不能大于64', max: 64 },
      //         { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '必须以字母开头，且只支持字母、数字、下划线！', trigger: 'blur' },
      //     ],
      //     version: [
      //         { required: true, message: '请选择版本号', trigger: 'blur' },
      //     ],
      // },
    };
  },
  created() {
  },
  watch: {
    'currentProjectData.id'(val) {
      if (val) {
        this.copyProjectData.name = '';
      }
    }
  },
  computed: {
    copyFormValid() {
      return {
        name: [
          { required: true, message: this.$t('message.workflow.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}64`, max: 64 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.workflow.validNameDesc'), trigger: 'blur' },
        ],
        version: [
          { required: true, message: this.$t('message.workflow.projectDetail.selectVersion'), trigger: 'blur' },
        ],

      }
    },
  },
  methods: {
    ProjectCopy() {
      this.$refs.copyProjectForm.validate((valid) => {
        if (valid) {
          // 调用复制接口
          this.dispatch('Project:loading', true);
          api.fetch('/dss/copyProjectAndFlow', { projectID: this.currentProjectData.id, projectName: this.copyProjectData.name, workspaceId: +this.$route.query.workspaceId }, 'post').then(() => {
            this.dispatch('Project:loading', false);
            this.dispatch('Project:getData');
          }).catch(() => {
            this.dispatch('Project:loading', false);
          });
        } else {
          this.$Message.warning(this.$t('message.workflow.failedNotice'));
        }
      });
    },
    ProjectCopyReset() {
      this.$refs.copyProjectForm.resetFields();
    },
  },
};
</script>
