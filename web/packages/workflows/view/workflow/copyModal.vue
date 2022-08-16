<template>
  <Modal title="复制工作流" v-model="visible" @on-cancel="cancelCallBack">
    <Form
      ref="formRef"
      :rules="ruleValidate"
      :model="formState"
      :label-width="100"
    >
      <FormItem label="被复制工作流" prop="name">
        <Input v-model="formState.sourceOrchestratorName" disabled />
      </FormItem>
      <FormItem label="复制到项目" prop="targetProjectId">
        <Select v-model="formState.targetProjectId" clearable class="input">
          <Option v-for="item in projects" :key="item.id" :value="item.id">{{item.name}}</Option>
        </Select>
      </FormItem>
      <FormItem label="复制后工作流" prop="targetOrchestratorName">
        <Input v-model="formState.targetOrchestratorName"></Input>
      </FormItem>
      <FormItem label="节点后缀" prop="workflowNodeSuffix">
        <Input style="width:95%;margin-right: 6px;" v-model="formState.workflowNodeSuffix" placeholder="填写后将自动为工作流节点名添加该后缀"></Input>
        <Tooltip max-width="300" content="同一项目中工作流节点重名会导致工作流发布失败，请考虑好节点后缀名再填写，若因重名问题导致发布失败，用户需手动修改对应工作流节点名称" placement="bottom">
          <SvgIcon icon-class="question" />
        </Tooltip>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <template slot="footer">
      <Button @click="handleCancel">取消</Button>
      <Button type="primary" @click="handleOk">确定</Button>
    </template>
  </Modal>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
export default {
  model: {
    prop: '_visible',
    event: '_changeVisible',
  },
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
            message: "请输入名称",
          },
        ],
        targetProjectId: [
          {
            required: true,
            message: "请选择项目",
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
            await this.copyRequest()
            this.$Message.success('复制请求发送成功')
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
              }
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
        return item.name !== data.projectName && (item.canPublish() || item.editable)
      })
    }
  },
}
</script>

<style scoped lang="less"></style>
