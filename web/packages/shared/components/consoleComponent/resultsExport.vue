<template>
  <Modal
    v-model="show"
    :fullscreen="isFullScreen"
    class="results-export"
    width="600">
    <div slot="header">
      <span>{{ $t('message.common.resultsExport.header') }}</span>
      <span @click="fullScreenModal" class="full-btn">
        <Icon :type="isFullScreen?'md-contract':'md-expand'" />
        {{isFullScreen?this.$t('message.common.cancelFullScreen'):this.$t('message.common.fullScreen')}}
      </span>
    </div>
    <div class="results-export-content">
      <Form
        ref="resultsExport"
        :model="exportOption"
        :rules="rules"
        :label-width="110">
        <FormItem
          :label="$t('message.common.resultsExport.formItems.name.label')"
          prop="name">
          <Input
            v-model="exportOption.name"
            :placeholder="$t('message.common.resultsExport.formItems.name.placeholder')"
            style="width: 320px;"></Input>
          <Icon
            :title="$t('message.common.resultsExport.formItems.name.title')"
            type="md-help-circle"
            class="results-export-content-help"
          ></Icon>
        </FormItem>
        <FormItem
          :label="$t('message.common.toolbar.exportFormat')"
          prop="format">
          <RadioGroup v-model="exportOption.format">
            <Radio label="1">CSV</Radio>
            <Radio label="2">Excel</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem
          :label="$t('message.common.toolbar.coding')"
          prop="coding">
          <RadioGroup v-model="exportOption.coding">
            <Radio label="1">UTF-8</Radio>
            <Radio label="2">GBK</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem
          :label="$t('message.common.toolbar.replace')"
          prop="nullValue">
          <RadioGroup v-model="exportOption.nullValue">
            <Radio label="1">NULL</Radio>
            <Radio label="2">{{
              $t('message.common.toolbar.emptyString')
            }}</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem
          v-if="exportOption.format == 1"
          :label="$t('message.common.toolbar.selectsplit')"
          prop="splitChar">
          <RadioGroup v-model="exportOption.splitChar">
            <Radio
              v-for="item in splitList"
              :label="item.value"
              :name="item.value"
              :key="item.value"
            >
              {{ item.label }}
            </Radio>
          </RadioGroup>
        </FormItem>
        <FormItem
          v-if="exportOption.format == 2"
          :label="$t('message.common.toolbar.autoformat')"
        >
          <Checkbox v-model="exportOption.autoFormat"></Checkbox>
        </FormItem>
        <FormItem
          :label="$t('message.common.resultsExport.formItems.path.label')"
          prop="path">
          <directory-dialog
            :filter-node="filterNode"
            :path="exportOption.path"
            :tree="fileTree"
            :load-data-fn="loadDataFn"
            :is-root-default-open="true"
            :height="directoryHeight"
            :title="$t('message.common.resultsExport.formItems.path.title')"
            fs-type="script"
            class="result-modal-path"
            @set-node="setNode"/>
        </FormItem>
        <FormItem v-if="script.resultList && script.resultList.length>1 && exportOption.format === '2'">
          <Checkbox v-model="isAll" :disabled="!allDisbled">{{ $t('message.common.resultsExport.formItems.path.isSheet') }}</Checkbox>
        </FormItem>
      </Form>
    </div>
    <div slot="footer">
      <Button
        v-show="show"
        type="primary"
        @click.stop="submit">{{ $t('message.common.ok') }}</Button>
    </div>
  </Modal>
</template>
<script>
import { isEmpty, cloneDeep, throttle} from 'lodash';
import util from '@dataspherestudio/shared/common/util';
import directoryDialog from '@dataspherestudio/shared/components/directoryDialog/index.vue';
export default {
  name: 'ResultsExport',
  components: {
    directoryDialog,
  },
  props: {
    currentPath: {
      type: String,
      default: '',
    },
    script: [Object],
    dispatch: {
      type: Function,
      required: true
    }
  },
  data() {
    return {
      isAll: false,
      show: false,
      isFullScreen: false,
      directoryHeight: 280,
      exportOption: {
        name: '',
        path: '',
        format: '1',
        coding: '1',
        nullValue: '1',
        splitChar: '1',
        keepNewline: '0',
        autoFormat: false
      },
      fileTree: [],
      hdfsComponent: null,
      loadDataFn: () => {},
      rules: {
        name: [
          { required: true, message: this.$t('message.common.resultsExport.rules.name.required'), trigger: 'blur' },
          { min: 1, max: 200, message: this.$t('message.common.resultsExport.rules.name.lengthLimit'), trigger: 'change' },
          { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.common.resultsExport.rules.name.letterTypeLimit'), trigger: 'change' },
        ],
        path: [
          { required: true, message: this.$t('message.common.resultsExport.rules.path.required'), trigger: 'change' },
        ],
      },
      splitList: [
        { label: this.$t('message.scripts.hiveTableExport.DH'), value: '1', data: ',' },
        { label: this.$t('message.scripts.hiveTableExport.FH'), value: '2', data: ';' },
        { label: this.$t('message.scripts.hiveTableExport.ZBF'), value: '3', data: '\t' },
        // { label: this.$t('message.scripts.hiveTableExport.KG'), value: '4', data: ' ' },
        { label: this.$t('message.scripts.hiveTableExport.SX'), value: '5', data: '|' },
      ],
    };
  },
  computed: {
    allDisbled() {
      return ['hql', 'sql', 'py', 'tsql'].includes(this.script.runType);
    }
  },
  methods: {
    open() {
      this.show = true;
      this.reset();
      let fileName = '';
      if (this.script.fileName && this.script.fileName !== 'undefined') {
        fileName = this.script.fileName.replace(/\./g, '_');
      } else if(this.script.title && this.script.title !== 'undefined') {
        fileName = this.script.title
      }
      this.exportOption.name = `${fileName}__${Date.now()}`;
      this.setFileTree();
    },
    setFileTree() {
      if (isEmpty(this.fileTree)) {
        // 取indexedDB缓存
        this.dispatch('IndexedDB:getTree', {
          name: 'scriptTree',
          cb: (res) => {
            if (!res || (res && res.value.length <= 0)) {
              this.dispatch('WorkSidebar:showTree', (f) => {
                this.hdfsComponent = f;
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
              if (this.hdfsComponent) {
                this.loadDataFn = this.hdfsComponent.loadDataFn;
              } else {
                this.dispatch('WorkSidebar:showTree', (f) => {
                  this.hdfsComponent = f;
                  this.loadDataFn = f.loadDataFn;
                });
              }
            }
          }
        })
      }
    },
    filterNode(node) {
      return !node.isLeaf;
    },
    setNode(node) {
      this.exportOption.path = node.path;
    },
    submit() {
      this.$refs.resultsExport.validate((valid) => {
        if (!valid) return false;
        this.show = false;
        throttle(this.exportConfirm(), 500);
      })
    },
    exportConfirm() {
      const tabName = `new_stor_${Date.now()}.out`;
      // 导出结果集增加是否导出全部，当选择导出全部时，来源路径不带后缀文件
      let temPath = this.currentPath;
      if (this.isAll) {
        temPath = temPath.substring(0, temPath.lastIndexOf('/'));
      }
      const exportOptionName = this.exportOption.format === '2' ?  `${this.exportOption.name}.xlsx`: `${this.exportOption.name}.csv`
      const code = `from ${temPath} to ${this.exportOption.path}/${exportOptionName}`;
      const md5Path = util.md5(tabName);
      const params =  {
        id: md5Path,
        filename: tabName,
        filepath: '',
        // saveAs表示临时脚本，需要关闭或保存时另存
        saveAs: true,
        noLoadCache: true,
        code
      }
      const nullValue = this.exportOption.nullValue === '1' ? 'NULL' : 'BLANK'
      const charset = this.exportOption.coding === '1' ? 'utf-8' : 'gbk'
      const splitChar = (this.splitList.find(it => it.value == this.exportOption.splitChar) || {} ).data
      if (this.exportOption.format === '2') {
        params.params =  {
          configuration: {
            runtime: {
              'wds.linkis.pipeline.export.excel.auto_format.enable': this.exportOption.autoFormat,
              "pipeline.output.charset": charset, //结果集导出字符集
              "pipeline.output.shuffle.null.type": nullValue, //空值替换
            }
          }
        }
      } else if(this.exportOption.format === '1') {
        params.params =  {
          configuration: {
            runtime: {
              "pipeline.field.split": splitChar, //csv分隔符
              "pipeline.output.charset": charset, //结果集导出字符集
              "pipeline.output.shuffle.null.type": nullValue, //空值替换
            }
          }
        }
      }
      if (this.$route.name === 'Home') {
        this.dispatch('Workbench:add', params , (f) => {
          if (!f) {
            return;
          }
          this.$nextTick(() => {
            this.dispatch('Workbench:run', { id: md5Path });
          });
        });
      } else {
        this.$router.push({
          path: '/home',
          query: { workspaceId: this.$route.query.workspaceId }
        });
        setTimeout(() => {
          this.dispatch('Workbench:add', params , (f) => {
            if (!f) {
              return;
            }
            this.$nextTick(() => {
              this.dispatch('Workbench:run', { id: md5Path });
            });
          });
        }, 3000);
      }
    },
    reset() {
      this.exportOption = {
        name: '',
        path: '',
        format: '1',
        coding: '1',
        nullValue: '1',
        splitChar: '1',
        keepNewline: '0',
      };
      this.fileTree = [];
    },
    fullScreenModal() {
      this.isFullScreen = !this.isFullScreen
      if (this.isFullScreen ) {
        this.directoryHeight = window.innerHeight - 330
      } else {
        this.directoryHeight = 280
      }
    },
  },
};
</script>
<style lang="scss" scoped>
  .results-export {
    .results-export-content-help {
        cursor: pointer;
        font-size: 14px;
    }
    .result-modal-path {
        width: 100%;
        display: inline-block;
    }
    .ivu-form-item {
        margin-bottom: 8px;
    }
}
.full-btn {
  float: right;
  margin-right: 30px;
  padding-top: 5px;
  cursor: pointer;
  color: #2d8cf0
}
</style>
