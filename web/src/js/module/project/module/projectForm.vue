<template>
  <Modal
    v-model="ProjectShow"
    :title="actionType === 'add' ? $t('message.project.createProject') : $t('message.project.editorProject')"
    :closable="false">
    <Form
      :label-width="100"
      label-position="left"
      ref="projectForm"
      :model="projectDataCurrent"
      :rules="formValid"
      v-if="ProjectShow">
      <FormItem
        :label="$t('message.project.projectName')"
        prop="name">
        <Input
          v-model="projectDataCurrent.name"
          :placeholder="$t('message.newConst.enterName')"
          :disabled="actionType === 'modify'"/>
      </FormItem>
      <FormItem
        :label="$t('message.project.product')"
        prop="product">
        <Input
          v-model="projectDataCurrent.product"
          :placeholder="$t('message.newConst.enterName')"/>
      </FormItem>
      <FormItem
        :label="$t('message.project.appArea')"
        prop="applicationArea">
        <Select
          v-model="projectDataCurrent.applicationArea"
          :placeholder="$t('message.project.selectAppArea')">
          <Option
            v-for="(item, index) in applicationAreaMap"
            :label="item"
            :value="index"
            :key="index"/>
        </Select>
      </FormItem>
      <FormItem
        :label="$t('message.project.business')"
        prop="business">
        <we-tag
          :new-label="$t('message.project.addBusiness')"
          :tag-list="projectDataCurrent.business"
          @add-tag="addTag"
          @delete-tag="deleteTag"></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.project.projectDesc')"
        prop="description">
        <Input
          v-model="projectDataCurrent.description"
          type="textarea"
          :placeholder="$t('message.project.pleaseInputProjectDesc')"/>
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
import api from '@/js/service/api';
import tag from '@js/component/tag/index.vue'
export default {
  components: {
    'we-tag': tag,
  },
  props: {
    projectData: {
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
      // formValid: {
      //     name: [
      //         { required: true, message: '请输入名称', trigger: 'blur' },
      //         { message: '名称长度不能大于20', max: 20 },
      //         { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '必须以字母开头，且只支持字母、数字、下划线！', trigger: 'blur' },
      //     ],
      //     description: [
      //         { required: true, message: '请输入工程描述', trigger: 'blur' },
      //     ],
      //     product: [
      //         { required: true, message: '请选择产品', trigger: 'change' },
      //     ],
      //     applicationArea: [
      //         { required: true, message: '请选择应用领域', trigger: 'change' },
      //     ],
      // },
      applicationAreaMap: [],
      originBusiness: '',
    };
  },
  computed: {
    projectDataCurrent() {
      return this.projectData;
    },
    formValid() {
      return {
        name: [
          { required: true, message: this.$t('message.newConst.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.newConst.nameLength')}20`, max: 20 },
          { type: 'string', pattern: /^[\u4e00-\u9fa5a-z][a-zA-z0-9_\u4e00-\u9fa5]*$/, message: this.$t('message.newConst.validNameDesc'), trigger: 'blur' },
        ],
        description: [
          { required: true, message: this.$t('message.project.pleaseInputProjectDesc'), trigger: 'blur' },
        ],
        product: [
          { required: true, message: this.$t('message.project.selectProduct'), trigger: 'change' },
        ],
        applicationArea: [
          { required: true, message: this.$t('message.project.selectAppArea'), trigger: 'change', type: 'number' },
        ],
      }
    }
  },
  mounted() {
    api.fetch('/dss/listApplicationAreas', 'get').then((res) => {
      this.applicationAreaMap = res.applicationAreas
    })
  },
  watch: {
    addProjectShow(val) {
      this.ProjectShow = val;
    },
    ProjectShow(val) {
      if (val) {
        this.originBusiness = this.projectDataCurrent.business;
      }
      this.$emit('show', val);
    },
  },
  methods: {
    Ok() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          this.$emit('confirm', this.projectDataCurrent);
          this.ProjectShow = false;
        } else {
          this.$Message.warning(this.$t('message.newConst.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ProjectShow = false;
      this.projectData.business = this.originBusiness;
    },
    addTag(label) {
      if (this.projectDataCurrent.business) {
        this.projectDataCurrent.business += `,${label}`;
      } else {
        this.projectDataCurrent.business = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.projectDataCurrent.business.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.projectData.business = tmpArr.toString();
    }
  },
};
</script>
