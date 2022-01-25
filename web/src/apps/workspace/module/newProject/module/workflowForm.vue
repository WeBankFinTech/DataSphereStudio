<template>
  <Modal
    v-model="ProjectShow"
    :title="actionType === 'add' ? $t('message.workflow.createWorkflow') : $t('message.workflow.editorWorkflow')"
    :closable="false">
    <Form
      :label-width="100"
      label-position="left"
      ref="projectForm"
      :model="workflowDataCurrent"
      :rules="formValid"
      v-if="workflowDataCurrent">
      <FormItem
        :label="$t('message.workflow.workflowName')"
        prop="name">
        <Input
          v-model="workflowDataCurrent.name"
          :placeholder="$t('message.workflow.inputFlowName')"
        ></Input>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.use')"
        prop="uses">
        <we-tag
          :new-label="$t('message.workflow.addUse')"
          :tag-list="workflowDataCurrent.uses"
          @add-tag="addTag"
          @delete-tag="deleteTag"></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.workflowDesc')"
        prop="description">
        <Input
          v-model="workflowDataCurrent.description"
          type="textarea"
          :maxlength=201
          :placeholder="$t('message.workflow.inputWorkflowDesc')"></Input>
      </FormItem>
    </Form>
    <div slot="footer">
      <Button
        type="text"
        size="large"
        @click="Cancel">{{$t('message.workflow.cancel')}}</Button>
      <Button
        type="primary"
        size="large"
        @click="Ok">{{$t('message.workflow.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import tag from '@component/tag/index.vue'
export default {
  components: {
    'we-tag': tag,
  },
  props: {
    workflowData: {
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
      originBusiness: '',
    };
  },
  computed: {
    workflowDataCurrent() {
      return this.workflowData;
    },
    formValid() {
      return {
        name: [
          { required: true, message: this.$t('message.workflow.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}128`, max: 128 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.workflow.validNameDesc'), trigger: 'blur' },
        ],
        description: [
          { required: true, trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}200`, max: 200 },
        ]
      }
    }
  },
  watch: {
    addProjectShow(val) {
      this.ProjectShow = val;
      // if (!val) {
      //     this.$refs.projectForm.resetFields();
      // }
    },
    ProjectShow(val) {
      if (val) {
        this.originBusiness = this.workflowDataCurrent.uses;
      }
      this.$emit('show', val);
    },
  },
  methods: {
    Ok() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          this.$emit('confirm', this.workflowDataCurrent);
          this.ProjectShow = false;
        } else {
          this.$Message.warning(this.$t('message.workflow.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ProjectShow = false;
      this.workflowDataCurrent.uses = this.originBusiness;
    },
    addTag(label) {
      if (this.workflowDataCurrent.uses) {
        this.workflowDataCurrent.uses += `,${label}`;
      } else {
        this.workflowDataCurrent.uses = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.workflowDataCurrent.uses.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.workflowData.uses = tmpArr.toString();
    }
  },
};
</script>
