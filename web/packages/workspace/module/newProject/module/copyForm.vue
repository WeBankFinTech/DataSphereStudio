<template>
  <Form
    :label-width="100"
    label-position="left"
    ref="copyProjectForm"
    :model="copyProjectData"
    :rules="copyFormValid">
    <FormItem
      :label="$t('message.common.projectDetail.projectName')"
      prop="name">
      <Input
        v-model="copyProjectData.name"
        :placeholder="$t('message.common.projectDetail.inputProjectName')"></Input>
    </FormItem>
    <FormItem
        label="是否接入Git"
        prop="associateGit"
      >
        <RadioGroup v-model="copyProjectData.associateGit">
            <Radio label="true" :disabled="!isIncludesDev">是</Radio>
            <Radio label="false">否</Radio>
        </RadioGroup>
        <div v-if="!currentProjectData.associateGit" style="color: red;">
          工作空间管理员未完成Git账号的配置，项目暂无法接入Git
        </div>
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
        associateGit: 'false'
      },
      copyFormValid: {
        name: [
          { required: true, message: this.$t('message.workflow.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}64`, max: 64 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.workflow.validNameDesc'), trigger: 'blur' },
        ],
        associateGit: [
          { required: true, message: '请选择是否接入Git', trigger: 'blur' },
        ],
      },
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
    isIncludesDev() {
      return this.currentProjectData.devProcessList && this.currentProjectData.devProcessList.includes('dev');
    }
  },
  methods: {
    ProjectCopy() {
      this.$refs.copyProjectForm.validate((valid) => {
        if (valid) {
          // 调用复制接口
          api.fetch(`${this.$API_PATH.PROJECT_PATH}copyProject`, {
            projectId: this.currentProjectData.id,
            copyProjectName: this.copyProjectData.name,
            workspaceId: +this.$route.query.workspaceId,
            associateGit: this.copyProjectData.associateGit === 'true',
          }, 'post').then((res) => {
            this.$Message.success(this.$t('message.workspace.CopySucc'))
            this.queryCopyStatus(res.projectId)
            this.copyProjectData.name = '';
            this.copyProjectData.associateGit = 'false'
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
        if (res.status === 2) {
          this.$Notice.info({
            title: this.$t('message.workspace.Prompt'),
            desc: '',
            duration: res.errorMsg ? 0 : 3,
            render: (h) => {
              return h('span', {
                style: {
                  'word-break': 'break-all',
                  'line-height': '20px',
                },
              },
              res.errorMsg ? res.errorMsg : this.$t('message.workspace.CopyDone')
              );
            },
          })
        } else if(res.status === 3) {
          this.$Notice.error({
            title: this.$t('message.workspace.Prompt'),
            desc: '',
            duration: 0,
            render: (h) => {
              return h('span', {
                style: {
                  'word-break': 'break-all',
                  'line-height': '20px',
                },
              },
              res.errorMsg
              );
            },
          })
        } else {
          this.$Notice.info({
            title: this.$t('message.workspace.Prompt'),
            desc: '',
            render: (h) => {
              return h('span', {
                style: {
                  'word-break': 'break-all',
                  'line-height': '20px',
                },
              },
              this.$t('message.workspace.Copying', {data: `(${res.surplusCount}/${res.sumCount})`})
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
        }
      } catch (e) {
        //
      }
    }
  },
};
</script>
