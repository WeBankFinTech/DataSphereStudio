<template>
  <Modal v-model="show" width="600" :fullscreen="isFullScreen">
    <div
      slot="header">
      <span>{{ this.$t('message.scripts.move.title') }}</span>
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
        <FormItem
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
import i18n from '@dataspherestudio/shared/common/i18n';
export default {
  name: 'MoveDialog',
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
        targetScriptPath: ''
      },
      rules: {
        targetScriptPath: [
          { required: true, message: this.$t('message.scripts.newDialog.rules.targetScriptPath.required'), trigger: 'change' }
        ]
      }
    }
  },
  computed: {
  },
  methods: {
    open() {
      this.reset();
      this.show = true;
      this.$nextTick(() => {
        if (this.defaultPath) {
          this.newForm.targetScriptPath = this.defaultPath;
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
          this.update();
        }
      });
    },
    update() {
      const path = this.newForm.targetScriptPath;
      const hasName = this.selectNode.children && this.selectNode.children.some(it => it.name === this.node.name)
      if (this.defaultPath == path || hasName) {
        return this.$Message.warning(this.$t("message.scripts.move.warn"))
      }
      this.$emit('update', {
        oldDest: this.defaultPath + '/' + this.node.name,
        newDest: path + '/' + this.node.name,
        cb: () => {
          this.show = false;
        }
      });
    },
    setNode(node) {
      this.selectNode = node
      this.newForm.targetScriptPath = node.path;
    },
    reset() {
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
