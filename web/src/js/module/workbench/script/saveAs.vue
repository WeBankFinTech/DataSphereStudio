<template>
  <Modal
    v-model="show"
    width="600"
    class="save-as-modal">
    <div slot="header">
      <span>{{ $t('message.workBench.body.script.saveAs.header') }}</span>
    </div>
    <div>
      <Form
        ref="saveAsForm"
        :model="form"
        :rules="rules"
        :label-width="90">
        <FormItem
          prop="fileName"
          :label="$t('message.workBench.body.script.saveAs.formItems.fileName.label') + '：'">
          <Input
            v-model="form.fileName"
            class="save-as-modal-name">
          </Input>
          <span>{{ form.ext }}</span>
        </FormItem>
        <FormItem
          prop="selectedPath"
          :label="$t('message.workBench.body.script.saveAs.formItems.selectedPath.label') + '：'">
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
        @click="submit">{{ $t('message.constants.submit')}}</Button>
    </div>
  </Modal>
</template>
<script>
import { isEmpty, cloneDeep } from 'lodash';
import storage from '@/js/helper/storage';
import directoryDialog from '@js/component/directoryDialog/index.vue';

export default {
  components: {
    directoryDialog,
  },
  data() {
    return {
      show: false,
      form: {
        fileName: '',
        ext: '',
        selectedPath: '',
      },
      fileTree: [],
      loadDataFn: () => {},
      isLoading: false,
      oldPath: '',
      rules: {
        fileName: [
          { required: true, message: this.$t('message.workBench.body.script.saveAs.rules.fileName.required'), trigger: 'blur' },
          { min: 1, max: 200, message: this.$t('message.workBench.body.script.saveAs.rules.fileName.lengthLimit'), trigger: 'change' },
          { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.workBench.body.script.saveAs.rules.fileName.letterTypeLimit'), trigger: 'change' },
        ],
        selectedPath: [
          { required: true, message: this.$t('message.workBench.body.script.saveAs.rules.selectedPath.required'), trigger: 'blur' },
        ],
      },
    };
  },
  methods: {
    async open(work) {
      this.show = true;
      this.form.fileName = work.filename.slice(0, work.filename.lastIndexOf('.'));
      this.form.ext = work.data.ext;
      this.oldPath = work.filePath;
      await this.setFileTree();
    },
    filterNode(node) {
      return !node.isLeaf;
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
        this.$refs.saveAsForm.validate((valid) => {
          if (valid) {
            this.isLoading = true;
            const path = this.form.selectedPath + '/' + this.form.fileName + this.form.ext;
            this.dispatch('Workbench:checkExist', {
              path,
            }, (flag) => {
              if (flag) {
                this.isLoading = false;
                this.$Message.warning(`${this.$t('message.constants.error.fileExists')}：${path}`);
              } else {
                // 如果path是不存在的，说明是临时脚本，需要调用新建接口
                if (!this.oldPath) {
                  this.createFile(path);
                  // 这里是实现另存为路径，预留接口
                } else {
                  this.moveTo(path);
                }
              }
            });
          } else {
            this.$Message.warning(this.$t('message.workBench.body.script.saveAs.warning.invalid'));
          }
        });
      }
    },
    createFile(path) {
      this.shareComponent.handleCreating({
        isLeaf: true,
        path,
      }, (flag) => {
        this.show = false;
        this.isLoading = false;
        if (flag) {
          const node = {
            path,
            name: this.form.fileName + this.form.ext,
            // scriptData: this.scriptData,
          };
          this.$emit('save-complete', true, node);
          this.$Message.success(this.$t('message.workBench.body.script.saveAs.success.saveScript'));
        }
      });
    },
    moveTo(path) {
      this.shareComponent.rename(path, this.oldPath, (flag) => {
        this.show = false;
        this.isLoading = false;
        if (flag) {
          const node = {
            path,
            name: this.form.fileName + this.form.ext,
            // scriptData: this.scriptData,
          };
          this.$emit('save-complete', false, node);
          this.$Message.success(this.$t('message.workBench.body.script.saveAs.success.saveScript'));
        }
      });
    },
  },
};
</script>
<style lang="scss" src="../index.scss">
</style>
