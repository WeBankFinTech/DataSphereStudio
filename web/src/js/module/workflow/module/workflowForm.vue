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
        prop="projectDescipte">
        <Input
          v-model="workflowDataCurrent.description"
          type="textarea"
          :placeholder="$t('message.workflow.inputWorkflowDesc')"></Input>
      </FormItem>
    </Form>
    <div slot="footer">
      <Button
        type="text"
        size="large"
        @click="Cancel">{{$t('message.newConst.cancel')}}</Button>
      <Button
        type="primary"
        size="large"
        @click="Ok">{{$t('message.newConst.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import tag from '@js/component/tag/index.vue'
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
          { required: true, message: this.$t('message.newConst.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.newConst.nameLength')}20`, max: 20 },
          { type: 'string', pattern: /^[\u4e00-\u9fa5a-z][a-zA-z0-9_\u4e00-\u9fa5]*$/, message: this.$t('message.newConst.validNameDesc'), trigger: 'blur' },
        ],
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
          this.$Message.warning(this.$t('message.newConst.failedNotice'));
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
