<template>
  <div class="import-table-csv">
    <Form
      :model="options"
      :label-width="80">
      <FormItem :label="$t('message.scripts.createTable.type')">
        <Select
          v-model="options.type"
          :placeholder="$t('message.scripts.createTable.selectType')"
          @on-change="changeTreeByType">
          <Option
            v-for="(item) in staticData.type"
            :label="item.label"
            :value="item.value"
            :key="item.value"/>
        </Select>
      </FormItem>
      <FormItem
        :label="$t('message.scripts.createTable.path')"
        v-if="fileTree.length">
        <Input
          v-model="options.exportPath"
          @on-focus="onPathInputFocus"></Input>
      </FormItem>
      <template v-if="isPathLeaf">
        <template v-if="importType === 'csv'">
          <FormItem :label="$t('message.scripts.createTable.FGF')">
            <Select
              v-model="options.separator"
              :placeholder="$t('message.scripts.createTable.selectType')">
              <Option
                v-for="(item) in staticData.separator"
                :label="item.label"
                :value="item.value"
                :key="item.value"/>
            </Select>
          </FormItem>
          <FormItem :label="$t('message.scripts.createTable.BMGS')">
            <Select
              v-model="options.chartset"
              :placeholder="$t('message.scripts.createTable.XZBMGS')"
            >
              <Option
                v-for="(item) in staticData.chartset"
                :label="item.label"
                :value="item.value"
                :key="item.value"/>
            </Select>
          </FormItem>
          <FormItem :label="$t('message.scripts.createTable.XDF')">
            <Select
              v-model="options.quote"
              clearable
              :placeholder="$t('message.scripts.createTable.WXDF')">
              <Option
                v-for="(item, index) in staticData.quote"
                :label="item.label"
                :value="item.value"
                :key="index"/>
            </Select>
          </FormItem>
        </template>
        <FormItem :label="$t('message.scripts.createTable.HXWBT')">
          <Checkbox v-model="options.isHasHeader"/>
        </FormItem>
        <FormItem style="text-align:right;padding-right: 20px;">
          <Button
            type="primary"
            :loading="isLoading"
            @click="getFileContent">{{$t('message.scripts.createTable.HQBZDSJ')}}</Button>
        </FormItem>
        <template  v-if="importType === 'xlsx'">
          <FormItem
            :label="$t('message.scripts.importToHive.SHEETB')"
          >
            <Select
              v-model="options.sheet"
              multiple="multiple"
              class="step-from-sheet">
              <Option
                v-for="(item, index) in sheetArray"
                :value="item"
                :key="index"
                :label="item">{{ item }}</Option>
            </Select>
          </FormItem>
        </template>
      </template>
    </Form>
    <Modal
      v-model="isTreeModalShow"
      :title="$t('message.scripts.createTable.XZWJLJ')"
      @on-ok="confirm"
      @on-cancel="cancel">
      <directory-dialog
        :tree="fileTree"
        :load-data-fn="loadDataFn"
        :filter-node="filterNode"
        :path="tmpTreePath"
        :fs-type="options.type"
        @set-node="setNode"
        :height="400"/>
    </Modal>
  </div>
</template>
<script>
import directoryDialog from '@dataspherestudio/shared/components/directoryDialog/index.vue';
import { indexOf } from 'lodash';
export default {
  components: {
    directoryDialog,
  },
  props: {
    importType: {
      type: String,
      default: 'csv',
    },
    options: {
      type: Object,
      required: true,
    },
    sheetArray: {
      type: Array,
      required: false,
    }
  },
  data() {
    return {
      validator: {
        isLeaf: true,
      },
      workspaceComponent: null,
      hdfsComponent: null,
      staticData: {
        type: [
          { label: this.$t('message.scripts.createTable.GXMLDR'), value: 'share' },
          { label: `HDFS${this.$t('message.scripts.createTable.DR')}`, value: 'hdfs' },
        ],
        separator: [
          { label: `${this.$t('message.scripts.createTable.DH')}(,)`, value: ',' },
          { label: `${this.$t('message.scripts.createTable.FH')}(;)`, value: ';' },
          { label: `${this.$t('message.scripts.createTable.ZBF')}(\\t)`, value: '\\t' },
          { label: this.$t('message.scripts.createTable.KG'), value: '%20' },
        ],
        chartset: [
          { label: 'utf-8', value: 'utf-8' },
          { label: 'GBK', value: 'GBK' },
        ],
        quote: [
          { label: `${this.$t('message.scripts.createTable.SYH')}("")`, value: '"' },
          { label: `${this.$t('message.scripts.createTable.DYH')}(\'\')`, value: '\'' },
        ],
        fieldsType: [
          { value: 'string' },
          { value: 'tinyint' },
          { value: 'smallint' },
          { value: 'int' },
          { value: 'bigint' },
          { value: 'boolean' },
          { value: 'float' },
          { value: 'double' },
          { value: 'decimal' },
          { value: 'timestamp' },
          { value: 'date' },
          { value: 'char' },
          { value: 'varchar' },
        ],
        dateType: [
          { label: 'yyyyMMdd', value: 'yyyyMMdd' },
          { label: 'yyyy-MM-dd', value: 'yyyy-MM-dd' },
          { label: 'yyyy.MM.dd', value: 'yyyy.MM.dd' },
          { label: 'yyyy/MM/dd', value: 'yyyy/MM/dd' },
        ],
      },
      fileTree: [],
      loadDataFn: () => {},
      filterNode: () => {},
      isLoading: false,
      isTreeModalShow: false,
      tmpTreePath: '',
    };
  },
  computed: {
    isPathLeaf() {
      return this.options.exportPath && this.options.exportPath.indexOf('.') >= 0;
    },
  },
  watch: {
    importType(val) {
      if (val === 'csv' || val === 'xlsx') {
        this.reset();
        this.getTree('share');
        this.filterNode = () => {};
        this.filterNode = this.getFilterNode(val);
      }
    },
    isPathLeaf(val) {
      this.$emit('isPathLeaf', val);
    }
  },
  mounted() {
    this.getTree('share');
    this.filterNode = this.getFilterNode(this.importType);
  },
  methods: {
    extendWorkSpace(cb) {
      if (!this.workspaceComponent) {
        this.dispatch('WorkSidebar:showTree', (f) => {
          this.workspaceComponent = f;
          cb(f);
        });
      } else {
        cb(this.workspaceComponent);
      }
    },
    extendHdfs(cb) {
      if (!this.hdfsComponent) {
        this.dispatch('HdfsSidebar:showTree', (f) => {
          this.hdfsComponent = f;
          cb(f);
        });
      } else {
        cb(this.hdfsComponent);
      }
    },
    getTree(type) {
      const treeType = type === 'share' ? 'scriptTree' : 'hdfsTree';
      const extend = type === 'share' ? 'extendWorkSpace' : 'extendHdfs';
      this.fileTree = [];
      this.$nextTick(() => {
        // 取indexedDB缓存
        this.dispatch('IndexedDB:getTree', {
          name: treeType,
          cb: (res) => {
            if (!res || res.value.length <= 0) {
              this[extend]((f) => {
                f.getRootPath(() => {
                  f.getTree((tree) => {
                    if (tree) {
                      this.fileTree.push(tree);
                    }
                    this.loadDataFn = f.loadDataFn;
                  });
                });
              });
            } else {
              this.fileTree = res.value;
              this[extend]((f) => {
                this.loadDataFn = f.loadDataFn;
              });
            }
          }
        })
      });
    },
    // 过滤文件夹
    getFilterNode(type) {
      return (node) => {
        const reg = type === 'csv' ? ['.csv', '.txt'] : ['.xlsx', '.xls'];
        if (!node.isLeaf) return true;
        const tabSuffix = node.label.substr(
          node.label.lastIndexOf('.'),
          node.label.length
        );
        const isVaild = indexOf(reg, tabSuffix) !== -1;
        return isVaild;
      };
    },
    setNode(node) {
      this.validator.isLeaf = node.isLeaf;
      const path = node.path;
      this.tmpTreePath = path;
    },
    changeTreeByType() {
      this.getTree(this.options.type);
      this.options.exportPath = '';
    },
    reset() {
      this.tmpTreePath = '';
      this.options.exportPath = '';
      this.options.type = 'share';
      this.options.separator = ',';
      this.options.chartset = 'utf-8';
      this.options.quote = '';
      this.options.isHasHeader = false;
    },
    getFileContent() {
      this.$emit('get-fields', this.options);
    },
    confirm() {
      this.options.exportPath = this.tmpTreePath;
      this.isTreeModalShow = false;
    },
    cancel() {
      this.tmpTreePath = '';
      this.isTreeModalShow = false;
    },
    onPathInputFocus() {
      this.isTreeModalShow = true;
    },
  },
};
</script>
