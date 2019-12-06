<template>
  <Modal
    v-model="ClassifyShow"
    :title="actionType === 'add' ? '工作流分类 / 新建' : '工作流分类 / 修改'"
    :closable="false">
    <Form
      :label-width="100"
      ref="classifyForm"
      :model="projectData"
      :rules="rulesValid"
      label-position="left">
      <FormItem
        label="工作流分类名:"
        prop="name">
        <Input
          v-model="projectData.name"
          placeholder="请输入分类名"
          :disabled="actionType === 'modify' && projectData.name === '我的工作流' || projectData.name === '我参与的工作流'"></Input>
      </FormItem>
      <FormItem label="工作流分类描述:">
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
        @click="Cancel">取消</Button>
      <Button
        type="primary"
        size="large"
        @click="Ok">确认</Button>
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
          { required: true, message: '请输入名称', trigger: 'blur' },
          { message: '名称长度不能大于20', max: 20 },
          { type: 'string', pattern: /^[a-zA-Z\u4e00-\u9fa5][a-zA-Z0-9_\u4e00-\u9fa5]*$/, message: '必须以字母或中文开头，且只支持字母、数字、下划线和中文！', trigger: 'blur' },
        ],
      },
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
          this.$Message.warning('验证项未通过，请检查后再试！');
        }
      });
    },
    Cancel() {
      this.ClassifyShow = false;
    },
  },
};
</script>
