<template>
  <Modal v-model="visible" width="500" class="table-row">
    <div slot="header">{{ $t('message.workflow.Import') }}</div>
    <Form
      ref="formRef"
      :rules="ruleValidate"
      :model="formState"
      label-position="left"
      :label-width="110"
    >
      <FormItem :label="$t('message.workflow.ImportType')" prop="importType">
        <Select v-model="formState.importType">
          <Option value="file">{{ $t('message.workflow.zipfile') }}</Option>
          <Option value="hdfs">{{ $t('message.workflow.hdfs') }}</Option>
        </Select>
      </FormItem>
      <FormItem v-if="formState.importType == 'file'" :label="$t('message.workflow.Upload')" prop="packageFile">
        <Upload
          ref="uploadZip"
          type="drag"
          :before-upload="handleUpload"
          :format="['zip']"
          :max-size="2001000"
          action=""
        >
          <div class="upload-box">
            <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
            <p>{{ $t('message.orchestratorModes.clickOrDragFile') }}</p>
          </div>
        </Upload>
        <div>{{formState.packageFile}}</div>
      </FormItem>
      <FormItem v-if="formState.importType =='hdfs'" :label="$t('message.workflow.Zip')" prop="zipUrl">
        <Input v-model="formState.zipUrl" :placeholder="$t('message.workflow.zippath')" />
      </FormItem>
    </Form>
    <div slot="footer">
      <Button @click="handleCancel">{{ $t('message.common.cancel') }}</Button>
      <Button type="primary" :disabled="importSubmiting" :loading="importSubmiting" @click="handleOk">{{ $t('message.workflow.Confirm') }}</Button>
    </div>
  </Modal>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
import mixin from '@dataspherestudio/shared/common/service/mixin';
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
      importSubmiting: false,
      formState: {
        packageFile: '',
        importType: '',
        zipUrl: '',
      },
      ruleValidate: {
        importType: [{ required: true, message: this.$t('message.workflow.typerequired') }],
        zipUrl: [{ required: true, message: this.$t('message.workflow.Path') }],
        packageFile: [{ required: true, message: this.$t('message.workflow.fileempty') }]
      },
    }
  },
  methods: {
    handleCancel() {
      this.$refs['formRef'].resetFields()
      this.$emit('_changeVisible', false)
    },
    async handleOk() {
      this.$refs['formRef'].validate(async (valid) => {
        if (valid) {
          try {
            await this.importAction()
            // this.$Message.success()
            this.$emit('finish', this.project)
            this.$emit('_changeVisible', false)
            this.$refs['formRef'].resetFields()
          } catch (error) {
            this.importSubmiting = false
            console.log(error)
          }
        }
      })
    },

    handleUpload(file) {
      if (file.name.indexOf('.zip') === -1) {
        this.$Message.warning(this.$t('message.orchestratorModes.selectZip'));
        return false;
      }
      this.$refs.formRef.validateField('packageFile')
      this.formState.packageFile = file.name;
      this.importZip = file
      return false;
    },
    init(node) {
      this.project = node
    },
    importAction() {
      const params = {
        projectName: this.project.name,
        projectID: this.project.id,
        labels: "dev"
      }
      if (this.formState.importType == 'file') {
        // zip 文件导入
        params.packageFile = this.importZip
      } else {
        // hdfs 导入
        params.packageUri = this.formState.zipUrl
      }
      this.importSubmiting = true
      return api.fetch(`${this.$API_PATH.ORCHESTRATOR_PATH}importOrchestratorFile`, params,
        {
          useForm: true,
          headers: {
            "Content-Type": "multipart/form-data"
          }
        })
        .finally(()=>{
          this.importSubmiting = false
        })
    },
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
