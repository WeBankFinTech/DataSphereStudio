<template>
  <Modal
    v-model="show"
    width="430"
    class="associate-script">
    <div slot="header">
      <span>{{$t('message.process.associateBar.XZGLJB')}}</span>
    </div>
    <div>
      <Form
        ref="associateScript"
        :model="form"
        :rules="rules"
        :label-width="90">
        <FormItem
          prop="selectedPath"
          :label="$t('message.process.associateBar.XZLJ')">
          <directory-dialog
            :tree="fileTree"
            :load-data-fn="loadDataFn"
            :filter-node="filterNode"
            :path="form.selectedPath"
            class="save-as-modal-path"
            fs-type="script"
            @set-node="setNode"/>
        </FormItem>
      </Form>
    </div>
    <div slot="footer">
      <Button
        :loading="isLoading"
        type="primary"
        @click="submit">{{$t('message.process.associateBar.QD')}}</Button>
    </div>
  </Modal>
</template>
<script>
import { isEmpty, cloneDeep } from 'lodash';
import storage from '@/js/helper/storage';
import directoryDialog from '@js/component/directoryDialog/index.vue';
import { ext } from '@/js/service/nodeType';
export default {
  components: {
    directoryDialog,
  },
  data() {
    return {
      show: false,
      form: {
        selectedPath: '',
      },
      fileTree: [],
      filterNode: () => {},
      loadDataFn: () => {},
      isLoading: false,
      rules: {
        selectedPath: [
          { required: true, message: this.$t('message.process.associateBar.XZYGJB'), trigger: 'blur' },
        ],
      },
      nodeData: null,
      supportModes: null,
    };
  },
  methods: {
    async open(node) {
      this.reset();
      this.show = true;
      this.nodeData = node;
      this.supportModes = this.getSupportModes();
      this.setFilterNode();
    },
    reset() {
      this.form.selectedPath = '';
      this.nodeData = null;
      this.$refs.associateScript.resetFields();
    },
    setFilterNode() {
      // 重新渲染
      this.fileTree = [];
      this.$nextTick(() => {
        this.setFileTree();
      });
      this.filterNode = (node) => {
        const model = ext[this.nodeData.type];
        const match = this.supportModes.find((item) => item.rule.test(node.label) && item.flowType === model);
        return !node.isLeaf || (node.isLeaf && match);
      };
    },
    setNode(node, fsType) {
      this.form.selectedPath = node.path;
    },
    setFileTree() {
      if (isEmpty(this.fileTree)) {
        const tmpTree = storage.get('scriptTree', 'SESSION');
        if (!tmpTree || (tmpTree && isEmpty(tmpTree))) {
          this.dispatch('WorkSidebar:showTree', (f) => {
            this.shareComponent = f;
            f.getRootPath((flag) => {
              f.getTree((tree) => {
                if (tree) {
                  this.fileTree.push(tree);
                  this.loadDataFn = f.loadDataFn;
                }
              });
            });
          });
        } else {
          this.fileTree = cloneDeep(tmpTree);
          if (this.shareComponent) {
            this.loadDataFn = this.shareComponent.loadDataFn;
          } else {
            this.dispatch('WorkSidebar:showTree', (f) => {
              this.shareComponent = f;
              this.loadDataFn = f.loadDataFn;
            });
          }
        }
      }
    },
    submit() {
      if (!this.isLoading) {
        this.$refs.associateScript.validate((valid) => {
          if (valid) {
            this.isLoading = true;
            this.$emit('click', this.nodeData, this.form.selectedPath, () => {
              this.isLoading = false;
              this.show = false;
            });
          } else {
            this.$Message.warning(this.$t('message.process.associateBar.CKYZ'));
          }
        });
      }
    },
  },
};
</script>
