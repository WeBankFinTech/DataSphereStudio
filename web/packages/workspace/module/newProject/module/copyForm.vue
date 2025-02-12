<template>
  <Form
    :label-width="100"
    label-position="left"
    ref="copyProjectForm"
    :model="copyProjectData"
    :rules="copyFormValid">
    <FormItem
      :label="$t('message.common.projectDetail.workspaceTarget')"
      prop="targetWorkspaceId">
      <Select v-model="copyProjectData.targetWorkspaceId"
        filterable
        clearable @on-change="handleChangeWorkspace">
        <Option v-for="item in workspaceList" :key="item.id" :value="item.id">{{item.name}}</Option>
      </Select>
    </FormItem>
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
        <RadioGroup v-model="copyProjectData.associateGit" @on-change="handleAssociateGit">
            <Radio label="true" :disabled="!isIncludesDev">是</Radio>
            <Radio label="false">否</Radio>
        </RadioGroup>
      </FormItem>
      <FormItem
        v-if="copyProjectData.associateGit === 'true'"
        label="Git读写用户名"
        prop="gitUser"
      >
        <Input
          v-model="copyProjectData.gitUser"
          placeholder="请输入Git读写用户名"
        >
        </Input>
      </FormItem>
      <FormItem
        v-if="copyProjectData.associateGit === 'true'"
        label="Token"
        prop="gitToken"
      >
        <Input
          v-model="copyProjectData.gitToken"
          placeholder="请输入Token"
          type="password"
        >
        </Input>
      </FormItem>
  </Form>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import {
  GetWorkspaceList,
} from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
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
        workspaceId: this.$route.query.workspaceId - 0,
        targetWorkspaceId: this.$route.query.workspaceId - 0,
        id: '',
        name: '',
        associateGit: 'false',
        gitUser: '',
        gitToken: ''
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
        gitUser: [
          { required: true, message: '请输入gitUser', trigger: "blur" },
        ],
        gitToken: [
          { required: true, message: '请输入gitToken', trigger: "blur" },
        ]
      },
      workspaceList: []
    };
  },
  created() {
    GetWorkspaceList({}, "get")
        .then((res) => {
          this.workspaceList = res.workspaces;
        })
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
      return this.currentProjectData.devProcessList && this.currentProjectData.devProcessList.includes('dev') && this.copyProjectData.targetWorkspaceId == this.$route.query.workspaceId;;
    }
  },
  methods: {
    handleAssociateGit() {
      this.copyProjectData.gitUser = '';
      this.copyProjectData.gitToken = '';
    },
    handleChangeWorkspace(v) {
      if (v != this.$route.query.workspaceId) {
        this.copyProjectData.associateGit = 'false'
      }
    },
    ProjectCopy() {
      this.$refs.copyProjectForm.validate((valid) => {
        if (valid) {
          // 调用复制接口
          const param = {
            projectId: this.currentProjectData.id,
            copyProjectName: this.copyProjectData.name,
            workspaceId: +this.$route.query.workspaceId,
            targetWorkspaceId: this.copyProjectData.targetWorkspaceId,
            associateGit: this.copyProjectData.associateGit === 'true',
          }
          if (param.associateGit) {
            param.gitUser = this.copyProjectData.gitUser;
            param.gitToken = this.copyProjectData.gitToken;
          }
          api.fetch(`${this.$API_PATH.PROJECT_PATH}copyProject`, param, 'post').then((res) => {
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
