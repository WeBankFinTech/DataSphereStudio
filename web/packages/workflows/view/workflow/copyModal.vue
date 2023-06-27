<template>
  <Modal :title="$t('message.workflow.CopyWrokflow')" v-model="visible" @on-cancel="cancelCallBack">
    <Form
      ref="formRef"
      :rules="ruleValidate"
      :model="formState"
      :label-width="110"
    >
      <FormItem :label="$t('message.workflow.Original')" prop="name">
        <Input v-model="formState.sourceOrchestratorName" disabled />
      </FormItem>
      <FormItem :label="$t('message.workflow.CopyProj')" prop="targetProjectId">
        <Select v-model="formState.targetProjectId" clearable class="input">
          <Option v-for="item in projects" :key="item.id" :value="item.id">{{item.name}}</Option>
        </Select>
      </FormItem>
      <FormItem :label="$t('message.workflow.Copied')" prop="targetOrchestratorName">
        <Input v-model="formState.targetOrchestratorName"></Input>
      </FormItem>
      <FormItem :label="$t('message.workflow.Node')" prop="workflowNodeSuffix">
        <Input style="width:95%;margin-right: 6px;" v-model="formState.workflowNodeSuffix" :placeholder="$t('message.workflow.autosuffix')"></Input>
        <Tooltip :content="$t('message.workflow.WorkflowSameName')" class="node-suffix-tips" placement="left">
          <SvgIcon icon-class="question" />
        </Tooltip>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <template slot="footer">
      <Button @click="handleCancel">{{ $t('message.workflow.Cancel') }}</Button>
      <Button type="primary" @click="handleOk">{{ $t('message.workflow.Confirm') }}</Button>
    </template>
  </Modal>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
import mixin from '@dataspherestudio/shared/common/service/mixin';
import { setVirtualRoles } from '@dataspherestudio/shared/common/config/permissions.js';

export default {
  model: {
    prop: '_visible',
    event: '_changeVisible',
  },
  mixins: [mixin],
  computed: {
    visible: {
      get() {
        return this._visible
      },
      set(val) {
        this.$emit('_changeVisible', val)
      },
    },
  },
  props: {
    // 是否可见
    _visible: {
      type: Boolean
    }
  },
  emits: ['finish', '_changeVisible'],
  data() {
    return {
      loading: false,
      projects: [],
      formState: {
        sourceOrchestratorName: '',
        targetOrchestratorName: '',
        targetProjectId: '',
        workflowNodeSuffix: ''
      },
      ruleValidate: {
        targetOrchestratorName: [
          {
            required: true,
            message: this.$t('message.workflow.PleaseInputName'),
          },
          { message: `${this.$t('message.workflow.nameLength')}128`, max: 128 },
          {
            type: 'string',
            pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
            message: this.$t('message.workflow.validNameDesc'),
            trigger: 'blur'
          }
        ],
        targetProjectId: [
          {
            required: true,
            message: this.$t('message.workflow.selcProj'),
          },
        ]
      }
    }
  },
  methods: {
    cancelCallBack() {
      this.$refs['formRef'].resetFields()
    },
    handleCancel() {
      this.$refs['formRef'].resetFields()
      this.$emit('_changeVisible', false)
    },
    async handleOk() {
      this.$refs['formRef'].validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.copyRequest()
            this.$Message.success(this.$t('message.workflow.Copyreq'))
            const targetProj = this.projects.find(it => it.id == this.formState.targetProjectId) || {}
            this.$emit('finish', {
              target: {
                id: this.formState.targetProjectId,
                name: targetProj.name,
                orchestratorId: '',
                orchestratorName: this.formState.targetOrchestratorName
              },
              source: {
                id: this.formState.sourceProjectId,
                name: this.formState.sourceProjectName,
                orchestratorId: this.formState.sourceOrchestratorId,
                orchestratorName: this.formState.sourceOrchestratorName
              },
              copyJobId: res && res.copyJobId
            })
            this.$emit('_changeVisible', false)
            this.$refs['formRef'].resetFields()
          } catch (error) {
            this.loading = false
            console.log(error)
          }
        }
      })
    },
    copyRequest() {
      const params = {
        workspaceId: +this.$route.query.workspaceId,
        ...this.formState,
        sourceProjectId: this.formState.sourceProjectId,
        labels: {route: this.getCurrentDsslabels() },
      }
      return api.fetch(`${this.$API_PATH.ORCHESTRATOR_PATH}copyOrchestrator`, params, "post")
    },
    init(data, {projects}) {
      this.formState = {
        ...this.formState,
        sourceOrchestratorId: data.orchestratorId,
        sourceOrchestratorName: data.orchestratorName,
        sourceProjectId: data.projectId,
        sourceProjectName: data.projectName
      }
      this.projects = projects.filter((item) => {
        setVirtualRoles(item, this.getUserName());
        return item.canPublish() || item.editable
      })
    }
  },
}
</script>

<style lang="scss">
.node-suffix-tips {
  .ivu-tooltip-inner {
    width: 300px;
    white-space: pre-wrap;
  }
}
</style>
