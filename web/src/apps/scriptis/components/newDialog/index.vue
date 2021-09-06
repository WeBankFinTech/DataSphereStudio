<template>
  <Modal v-model="show">
    <div
      slot="header"
      width="300">
      <span>{{ modalTitle }}</span>
    </div>
    <div>
      <Form
        ref="newForm"
        :model="newForm"
        :rules="rules"
        :label-width="80"
        size="small">
        <Form-item
          v-if="businessType.length"
          :label="$t('message.scripts.newDialog.nodeType')"
          prop="type">
          <Select v-model="newForm.businessType">
            <Option
              v-for="(item) in businessType"
              :label="item"
              :value="item"
              :key="item"/>
          </Select>
        </Form-item>
        <FormItem
          v-if="isLeaf"
          :label="$t('message.scripts.newDialog.scriptName') + '：'"
          prop="scriptName">
          <Input
            v-model="newForm.scriptName"
            :placeholder="$t('message.scripts.newDialog.placeholder')"
            style="width: 360px;"></Input>
          <span>{{ ext }}</span>
        </FormItem>
        <FormItem
          v-else
          :label="$t('message.scripts.newDialog.catalogName')"
          prop="name">
          <Input
            v-model="newForm.name"
            :placeholder="$t('message.scripts.newDialog.required')"></Input>
        </FormItem>
        <Form-item
          v-if="isLeaf && scriptType.length"
          :label="$t('message.scripts.newDialog.scriptType') + '：'"
          prop="type">
          <Select v-model="newForm.scriptType">
            <Option
              v-for="(item, index) in scriptType"
              :label="item.label"
              :value="item.scriptType"
              :key="index"/>
          </Select>
        </Form-item>
        <FormItem
          v-if="isDescShow"
          :label="$t('message.scripts.constants.description')">
          <Input
            v-model="newForm.description"
            type="textarea"></Input>
        </FormItem>
        <FormItem
          v-if="targetFolder"
          :label="$t('message.scripts.newDialog.targetFolder')"
        >
          <Input
            v-model="targetFolder"
            disabled></Input>
        </FormItem>
        <FormItem
          v-if="isPathShow"
          :label="$t('message.scripts.newDialog.targetScriptPath')"
          prop="targetScriptPath"
        >
          <directory-dialog
            fs-type="script"
            :tree="tree"
            :load-data-fn="loadDataFn"
            :filter-node="filterNode"
            :path="newForm.targetScriptPath"
            @set-node="setNode"/>
        </FormItem>
      </Form>
    </div>
    <div slot="footer">
      <Button @click="show=false">{{ $t('message.scripts.constants.cancel') }}</Button>
      <Button
        :loading="loading"
        type="primary"
        @click="submitForm('newForm')">{{ $t('message.scripts.constants.submit') }}</Button>
    </div>
  </Modal>
</template>
<script>
import directoryDialog from '@component/directoryDialog/index.vue';
export default {
  name: 'NewDialog',
  components: {
    directoryDialog,
  },
  props: {
    isLeaf: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    businessType: {
      type: Array,
      default: () => {
        return [];
      }
    },
    scriptType: {
      type: Array,
      defalut: () => {
        return [];
      }
    },
    targetFolder: {
      type: String,
      defalut: '',
    },
    isPathShow: {
      type: Boolean,
      defalut: false,
    },
    isDescShow: {
      type: Boolean,
      default: true,
    },
    isNew: {
      type: Boolean,
      default: true,
    },
    type: {
      type: String,
      default: '文件夹',
    },
    node: {
      type: Object,
      default: () => {},
    },
    tree: {
      type: Array,
      default: () => [],
    },
    defaultPath: {
      type: String,
      default: '',
    },
    loadDataFn: Function,
    filterNode: Function,
  },
  data() {
    return {
      show: false,
      newForm: {
        name: '',
        scriptName: '',
        description: '',
        businessType: '',
        targetScriptPath: '',
        scriptType: '',
      },
      rules: {
        scriptName: [
          { required: true, message: this.$t('message.scripts.newDialog.rules.scriptName.required'), trigger: 'blur' },
          { min: 1, max: 200, message: this.$t('message.scripts.newDialog.rules.scriptName.lengthLimit'), trigger: 'change' },
          { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.scripts.newDialog.rules.scriptName.letterTypeLimit'), trigger: 'change' }
        ],
        name: [
          { required: true, message: this.$t('message.scripts.newDialog.rules.catalogName.required'), trigger: 'blur' },
          { min: 1, max: 200, message: this.$t('message.scripts.newDialog.rules.catalogName.lengthLimit'), trigger: 'change' },
          { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.scripts.newDialog.rules.catalogName.letterTypeLimit'), trigger: 'change' }
        ],
        targetScriptPath: [
          { required: true, message: this.$t('message.scripts.newDialog.rules.targetScriptPath.required'), trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ext() {
      const item = this.scriptType.find((o) => o.scriptType === this.newForm.scriptType);
      return item ? item.ext : '';
    },
    modalTitle() {
      if (this.isNew) {
        return `${this.$t('message.scripts.constants.add')}${this.type}`;
      }
      return `${this.$t('message.scripts.constants.rename')}${this.type}`;
    }
  },
  methods: {
    open() {
      this.reset();
      this.show = true;
      this.$nextTick(() => {
        if (this.defaultPath) {
          this.newForm.targetScriptPath = this.defaultPath;
        }
        if (this.node && this.node.businessType) {
          this.newForm.businessType = this.node.businessType;
        }
        // isNew为false表示是修改，需填充数据
        if (!this.isNew) {
          let { name, description } = this.node.data;
          this.newForm.name = name;
          this.newForm.description = description;
        }
      });
    },
    close() {
      this.show = false;
      this.reset();
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.update(this.isNew);
        }
      });
    },
    update(isNew) {
      const name = this.isLeaf ? this.newForm.scriptName + this.ext : this.newForm.name;
      const path = isNew ? `${this.newForm.targetScriptPath}/${name}` : this.newForm.targetScriptPath;
      const node = {
        name,
        path,
        businessType: this.newForm.businessType,
        description: this.newForm.description,
        isLeaf: this.isLeaf,
        type: this.type,
      };
      const method = isNew ? 'add' : 'update';
      this.$emit(method, node);
      this.show = false;
    },
    setNode(node) {
      this.newForm.targetScriptPath = node.path;
    },
    reset() {
      this.newForm = {
        name: '',
        scriptName: '',
        description: '',
        businessType: '',
        targetScriptPath: '',
        scriptType: 'hive',
      };
      this.$refs.newForm.resetFields();
    },
  },
};
</script>
