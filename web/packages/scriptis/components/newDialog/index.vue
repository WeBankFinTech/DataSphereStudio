<template>
  <Modal v-model="show" width="600" :fullscreen="isFullScreen">
    <div
      slot="header">
      <span>{{ modalTitle }}</span>
      <span @click="fullScreenModal" class="full-btn">
        <Icon :type="isFullScreen?'md-contract':'md-expand'" />
        {{isFullScreen?this.$t('message.scripts.cancelfullscreen'):this.$t('message.scripts.fullscreen')}}
      </span>
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
          <a target="_blank" v-if="scriptHelpLink" style="float:right" :href="scriptHelpLink">{{ $t('message.scripts.scriptUsage') }}</a>
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
            :height="directoryHeight"
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
import directoryDialog from '@dataspherestudio/shared/components/directoryDialog/index.vue';
import storage from '@dataspherestudio/shared/common/helper/storage';
import i18n from '@dataspherestudio/shared/common/i18n';
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
      default: i18n.t('message.scripts.folder'),
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
      isFullScreen: false,
      directoryHeight: 280,
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
    },
    scriptHelpLink() {
      const baseinfo = storage.get("baseInfo", "local") || {}
      const item = this.scriptType.find((o) => o.scriptType === this.newForm.scriptType);
      const scriptGuideMap = {
        ".sql": "SqlUsageGuide",
        ".hql": "HiveUsageGuide",
        ".psql": "PrestoSqlUsageGuide",
        ".tsql": "TrinoSqlUsageGuide",
        ".fql": "FlinkSqlUsageGuide",
        ".out": "StorageUsageGuide",
        ".scala": "ScalaUsageGuide",
        ".jdbc": "JdbcUsageGuide",
        ".python": "PythonUsageGuide",
        ".py": "PythonSparkUsageGuide",
        ".r": "RUsageGuide",
        ".sh": "ShellUsageGuide",
        ".ngql": "NebulaUsageGuide",
      }
      return item ? baseinfo[scriptGuideMap[this.ext]] || '' : ''
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
      this.isFullScreen = false;
      this.directoryHeight = 280;
      this.$refs.newForm.resetFields();
    },
    fullScreenModal() {
      this.isFullScreen = !this.isFullScreen
      if (this.isFullScreen ) {
        this.directoryHeight = window.innerHeight - 260
      } else {
        this.directoryHeight = 280
      }
    },
  },
};
</script>
<style scoped>
.full-btn {
  float: right;
  margin-right: 30px;
  padding-top: 5px;
  cursor: pointer;
  color: #2d8cf0
}
</style>
