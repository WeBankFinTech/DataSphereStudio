<template>
  <Modal
    v-model="ClassifyShow"
    :title="ftitle"
    :closable="false">
    <Form
      :label-width="100"
      ref="classifyForm"
      :model="projectData"
      :rules="rulesValid"
      label-position="left">
      <FormItem
        :label="$t('message.workflow.WorkflowCategory')"
        prop="name">
        <Input
          v-model="projectData.name"
          :placeholder="$t('message.workflow.inputCategory')"
          :disabled="actionType === 'modify' && projectData.name === this.$t('message.workflow.Myworkflow') || projectData.name === this.$t('message.workflow.WorkflowMy')"></Input>
      </FormItem>
      <FormItem :label="$t('message.workflow.workflowdesc')">
        <Input
          v-model="projectData.description"
          type="textarea"
          :placeholder="$t('message.workflow.inputCategory')"></Input>
      </FormItem>
    </Form>
    <div slot="footer">
      <Button
        type="text"
        size="large"
        @click="Cancel">{{ $t('message.workflow.Cancel') }}</Button>
      <Button
        type="primary"
        size="large"
        @click="Ok">{{ $t('message.workflow.Confirm') }}</Button>
    </div>
  </Modal>
</template>
<script>
export default {
  props: {
    projectClassify: {
      type: Object,
      default: null,
    },
    addClassifyShow: {
      type: Boolean,
      defalut: null,
    },
    actionType: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      ClassifyShow: this.addClassifyShow,
      rulesValid: {
        name: [
          { required: true, message: this.$t('message.workflow.PleaseInputName'), trigger: 'blur' },
          { message: '名称长度不能大于20', max: 20 },
          { type: 'string', pattern: /^[a-zA-Z\u4e00-\u9fa5][a-zA-Z0-9_\u4e00-\u9fa5]*$/, message: this.$t('message.workflow.inputStyle'), trigger: 'blur' },
        ],
      },
    };
  },
  computed: {
    projectData() {
      return this.projectClassify;
    },
    ftitle() {
      return this.actionType === 'add' ? `${this.$t('message.workflow.Create')} / ${this.$t('message.workflow.inputCategory')}` : `${this.$t('message.workflow.Edit')} / ${this.$t('message.workflow.inputCategory')}`
    }
  },
  watch: {
    ClassifyShow(val) {
      this.$emit('showModal', val);
      if (!val) {
        this.$refs.classifyForm.resetFields();
      }
    },
    addClassifyShow(val) {
      this.ClassifyShow = val;
    },
  },
  methods: {
    Ok() {
      this.$refs.classifyForm.validate((valid) => {
        if (valid) {
          this.$emit('addProjectClassifyConfirm', this.projectData);
          this.ClassifyShow = false;
        } else {
          this.$Message.warning(this.$t('message.workflow.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ClassifyShow = false;
    },
  },
};
</script>
