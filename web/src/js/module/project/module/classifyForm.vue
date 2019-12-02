<template>
  <Modal
    v-model="ClassifyShow"
    :title="actionType === 'add' ? `${$t('message.project.projectClassify')} / ${$t('message.project.newCreate')}` : `${$t('message.project.projectClassify')} / ${$t('message.project.editor')}`"
    :closable="false">
    <Form
      :label-width="100"
      label-position="left"
      ref="classifyForm"
      :model="projectData"
      :rules="rulesValid"
      v-if="ClassifyShow">
      <FormItem
        label="工程分类名:"
        prop="name">
        <Input
          v-model="projectData.name"
          placeholder="请输入分类名"
          :disabled="actionType === 'modify' && projectData.name === '我的工程' || projectData.name === '我参与的工程'"></Input>
      </FormItem>
      <FormItem
        label="工程分类描述:"
        prop="projectDescription">
        <Input
          v-model="projectData.description"
          type="textarea"
          placeholder="请输入分类名"></Input>
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
      laoding: true,
      // rulesValid: {
      //     name: [
      //         { required: true, message: '请输入名称', trigger: 'blur' },
      //         { message: '名称长度不能大于20', max: 20 },
      //         { type: 'string', pattern: /^[a-zA-Z\u4e00-\u9fa5][a-zA-Z0-9_\u4e00-\u9fa5]*$/, message: '必须以字母或汉字开头，且只支持字母、数字、下划线和中文！', trigger: 'blur' },
      //     ],
      // },
    };
  },
  computed: {
    projectData() {
      return this.projectClassify;
    },
  },
  watch: {
    ClassifyShow(val) {
      this.$emit('showModal', val);
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
          this.$Message.warning(this.$t('message.newConst.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ClassifyShow = false;
    },
  },
};
</script>
