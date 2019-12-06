<template>
  <Form
    :label-width="100"
    label-position="left"
    ref="copyProjectForm"
    :model="copyProjectData"
    :rules="copyFormValid">
    <FormItem
      label="工程名"
      prop="name">
      <Input
        v-model="copyProjectData.name"
        placeholder="请输入工程名"></Input>
    </FormItem>
  </Form>
</template>
<script>
import api from '@/js/service/api';
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
  computed: {
    copyFormValid() {
      return {
        name: [
          { required: true, message: this.$t('message.newConst.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.newConst.nameLength')}64`, max: 64 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.newConst.validNameDesc'), trigger: 'blur' },
        ],
        version: [
          { required: true, message: this.$t('message.project.selectVersion'), trigger: 'blur' },
        ],

      }
    },
  },
  methods: {
    'Project:copy'(cb) {
      this.$refs.copyProjectForm.validate((valid) => {
        if (valid) {
          // 调用复制接口
          this.dispatch('Project:loading', true);
          api.fetch('/dss/copyProjectAndFlow', { projectID: this.currentProjectData.id, projectName: this.copyProjectData.name }, 'post').then((res) => {
            this.dispatch('Project:loading', false);
            this.dispatch('Project:getData');
          }).catch(() => {
            this.dispatch('Project:loading', false);
          });
        } else {
          this.$Message.warning(this.$t('message.newConst.failedNotice'));
        }
      });
    },
    'Project:copyReset'() {
      this.$refs.copyProjectForm.resetFields();
    },
  },
};
</script>
