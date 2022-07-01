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
import api from '@dataspherestudio/shared/common/service/api';
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
          api.fetch(`${this.$API_PATH.PROJECT_PATH}copyProject`, {
            projectId: this.currentProjectData.id, 
            copyProjectName: this.copyProjectData.name, 
            workspaceId: +this.$route.query.workspaceId 
          }, 'post').then((res) => {
            this.$Message.success('复制请求发送成功')
            this.queryCopyStatus(res.projectId)
            this.copyProjectData.name = '';
          }).catch(() => {
          });
        } else {
          this.$Message.warning(this.$t('message.workflow.failedNotice'));
        }
      });
    },
    ProjectCopyReset() {
      this.$refs.copyProjectForm.resetFields();
    },
    async queryCopyStatus(copyProjectId) {
      try {
        const res = await api.fetch(`${this.$API_PATH.PROJECT_PATH}getCopyProjectInfo`, {
          copyProjectId,
          workspaceId: +this.$route.query.workspaceId 
        }, 'post')
        clearTimeout(this.queryTimer)
        const name = copyProjectId + '_copy';
        this.$Notice.close(name);
        this.$Notice.info({
          title: '提示',
          desc: '',
          render: (h) => {
            return h('span', {
              style: {
                'word-break': 'break-all',
                'line-height': '20px',
              },
            },
            `复制中(${res.surplusCount}/${res.sumCount})，请稍候...`
            );
          },
          name
        });
        if (res.surplusCount < 1) {
          this.dispatch('Project:loading', false);
          this.dispatch('Project:getData');
        } else {
          this.queryTimer = setTimeout(() => {
            this.queryCopyStatus(copyProjectId)
          }, 1500);
        }
      } catch (e) {
        //
      }
    }
  },
};
</script>
