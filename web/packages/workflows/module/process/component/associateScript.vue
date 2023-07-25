<template>
  <Modal
    v-model="show"
    width="600"
    :fullscreen="isFullScreen"
    class="associate-script">
    <div slot="header" class="header-bar">
      <span>{{$t('message.workflow.process.associateBar.XZGLJB')}}</span>
      <span @click="fullScreenModal" class="full-btn">
        <Icon :type="isFullScreen?'md-contract':'md-expand'" />
        {{isFullScreen?this.$t('message.workflow.CancelFullScreen'):this.$t('message.workflow.Full')}}
      </span>
    </div>
    <div>
      <Form
        ref="associateScript"
        :model="form"
        :rules="rules"
        :label-width="90">
        <FormItem
          prop="selectedPath"
          :label="$t('message.workflow.process.associateBar.XZLJ')">
          <directory-dialog
            :tree="fileTree"
            :load-data-fn="loadDataFn"
            :filter-node="filterNode"
            :height="directoryHeight"
            :path="form.selectedPath"
            :is-root-default-open="true"
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
        @click="submit">{{$t('message.workflow.process.associateBar.QD')}}</Button>
    </div>
  </Modal>
</template>
<script>
import { isEmpty, cloneDeep } from 'lodash';
import directoryDialog from '@dataspherestudio/shared/components/directoryDialog/index.vue';
import { ext } from '@/workflows/service/nodeType'
import mixin from '@dataspherestudio/shared/common/service/mixin';
export default {
  components: {
    directoryDialog,
  },
  mixins: [mixin],
  data() {
    return {
      show: false,
      form: {
        selectedPath: '',
      },
      fileTree: [],
      filterNode: () => {},
      loadDataFn: () => {},
      directoryHeight: 280,
      isLoading: false,
      rules: {
        selectedPath: [
          { required: true, message: this.$t('message.workflow.process.associateBar.XZYGJB'), trigger: 'blur' },
        ],
      },
      nodeData: null,
      supportModes: null,
      isFullScreen: false
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
    fullScreenModal() {
      this.isFullScreen = !this.isFullScreen
      if (this.isFullScreen ) {
        this.directoryHeight = window.innerHeight - 220
      } else {
        this.directoryHeight = 280
      }
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
    setNode(node) {
      this.form.selectedPath = node.path;
    },
    setFileTree() {
      if (isEmpty(this.fileTree)) {
        // 取indexedDB缓存
        this.dispatch('IndexedDB:getTree', {
          name: 'scriptTree',
          cb: (res) => {
            if (!res || (res && res.value.length <= 0)) {
              this.dispatch('WorkSidebar:showTree', (f) => {
                this.shareComponent = f;
                f.getRootPath(() => {
                  f.getTree((tree) => {
                    if (tree) {
                      this.fileTree.push(tree);
                      this.loadDataFn = f.loadDataFn;
                    }
                  });
                });
              });
            } else {
              this.fileTree = cloneDeep(res.value);
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
        })
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
            this.$Message.warning(this.$t('message.workflow.process.associateBar.CKYZ'));
          }
        });
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.save-as-modal-path {
    width: 100%;
    display: inline-block;
}
.header-bar {
  font-size: 14px;
}
.full-btn {
  float: right;
  margin-right: 30px;
  padding-top: 5px;
  cursor: pointer;
  color: #2d8cf0
}
</style>

