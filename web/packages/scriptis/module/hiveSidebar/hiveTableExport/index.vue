<template>
  <Modal
    v-model="show"
    :width="width">
    <p slot="header">
      <span>{{ $t('message.scripts.hiveTableExport.DCB') }}</span>
    </p>
    <div class="we-table-export">
      <template>
        <Steps
          :current="stepActive"
          size="small"
          class="we-table-export-step">
          <step
            :title="$t('message.scripts.hiveTableExport.SZDCCS')"/>
          <step
            :title="$t('message.scripts.hiveTableExport.SZDCLJ')"/>
        </Steps>
      </template>
      <div>
        <Spin
          v-if="isLoading || isPartLoading"
          size="large"
          fix/>
        <Form
          v-show="stepActive === 0"
          ref="stepOneForm"
          :model="stepOne"
          :rules="stepOnerules"
          :label-width="80"
          class="we-table-export-form" >
          <FormItem
            :label="$t('message.scripts.hiveTableExport.SJKM')"
            prop="dbName">
            <Select
              v-model="stepOne.dbName"
              :placeholder="$t('message.scripts.hiveTableExport.QSRKM')"
              class="item-width"
              @on-change="handleHiveDbChange">
              <Option
                v-for="(item, index) in dbList"
                :label="item.name"
                :value="item.name"
                :key="index"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.hiveTableExport.SJBM')"
            prop="tbName">
            <Select
              v-model="stepOne.tbName"
              filterable
              clearable
              :remote-method="remoteMethod1"
              @on-open-change="queryWhenOpen"
              class="item-width"
              @on-change="handleTbInput">
              <Option
                v-for="(item, index) in showTbOptions"
                :value="item.value"
                :key="item.name+index">{{ item.name }}</Option>
            </Select>
          </FormItem>
          <FormItem
            v-if="activeTB && !activeTB.isView"
            :label="$t('message.scripts.hiveTableExport.ZDXX')"
            prop="column">
            <Select
              key="column"
              v-model="stepOne.column"
              :disabled="isUnExport"
              multiple
              class="item-width"
              transfer>
              <Option
                v-for="(item, index) in activeTB.children"
                :value="item.name"
                :key="item.name + index"
                :label="item.name">
                {{ item.name }}
              </Option>
            </Select>
          </FormItem>
          <FormItem
            v-if="activeTB && activeTB.isPartition && stepOne.tbName"
            :label="$t('message.scripts.hiveTableExport.FQXX')"
            prop="partitions">
            <Select
              key="part"
              v-model="stepOne.partitions"
              :disabled="isUnExport"
              class="item-width">
              <Option
                v-for="(item, index) in activeTB.partitions"
                :value="item.label"
                :key="index.label"
                :label="item.label">
                {{ item.label }}
              </Option>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.hiveTableExport.DCGS')"
            prop="exportType">
            <Select
              key="type"
              v-model="stepOne.exportType"
              :disabled="isUnExport"
              class="item-width"
              @on-open-change="handleExportTypeOpen">
              <Option
                v-for="(item) in libs.exportTypes"
                :label="item.label"
                :value="item.value"
                :key="item.value"
                :disabled="stepOne.isLargerThenOneGb && item.value==='xlsx'"/>
            </Select>
          </FormItem>
          <FormItem
            v-if="stepOne.exportType === 'csv'"
            :label="$t('message.scripts.hiveTableExport.FGF')"
            prop="separator">
            <Select
              v-model="stepOne.separator"
              class="item-width">
              <Option
                v-for="(item) in libs.separator"
                :label="item.label"
                :value="item.value"
                :key="item.value"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.hiveTableExport.BMGS')"
            v-if="stepOne.exportType === 'csv'"
            prop="chartset">
            <Select
              v-model="stepOne.chartset"
              :placeholder="$t('message.scripts.hiveTableExport.XZBMGS')">
              <Option
                v-for="(item) in libs.chartset"
                :label="item.label"
                :value="item.value"
                :key="item.value"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.hiveTableExport.KZTH')"
            prop="nullValue">
            <Select
              v-model="stepOne.nullValue">
              <Option
                v-for="(item, index) in libs.replacementList"
                :value="item.value"
                :key="index">{{ item.label }}</Option>
            </Select>
          </FormItem>
          <FormItem>
            <Checkbox
              v-model="stepOne.isHasHeader"
              :disabled="isUnExport">{{ $t('message.scripts.hiveTableExport.SXWBT') }}</Checkbox>
          </FormItem>
        </Form>
        <Form
          v-show="stepActive === 1"
          ref="stepTwoForm"
          :model="stepTwo"
          :rules="stepTworules"
          :label-width="80"
          class="we-table-export-form">
          <FormItem
            :label="$t('message.scripts.hiveTableExport.LX')"
            prop="type">
            <Select
              v-model="stepTwo.type"
              :placeholder="$t('message.scripts.hiveTableExport.QXZLX')"
              style="width: 378px;"
              @on-change="handleTypeChange">
              <Option
                v-for="(item) in libs.types"
                :label="item.label"
                :value="item.value"
                :key="item.value"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.hiveTableExport.DCZ')"
            prop="path">
            <directory-dialog
              :tree="tree"
              :load-data-fn="loadDataFn"
              :filter-node="filterNode"
              :path="stepTwo.path"
              :fs-type="stepTwo.type"
              @set-node="setNode"/>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.hiveTableExport.WJM')"
            prop="fileName">
            <Input
              v-model="stepTwo.fileName"
              :placeholder="$t('message.scripts.hiveTableExport.TRDCWJMC')"
              style="width: 350px;">
            </Input>
            <span>.{{ stepOne.exportType }}</span>
          </FormItem>
          <FormItem
            v-if="stepOne.exportType === 'csv'"
            :label="$t('message.scripts.hiveTableExport.FXMS')">
            <RadioGroup v-model="stepOne.isOverwrite">
              <Radio :label="$t('message.scripts.hiveTableExport.ZJ')"/>
              <Radio :label="$t('message.scripts.hiveTableExport.FX')"/>
            </RadioGroup>
          </FormItem>
          <FormItem
            v-if="stepOne.exportType !== 'csv'"
            :label="$t('message.scripts.hiveTableExport.BM')">
            <Input
              v-model="stepTwo.sheetName"
              :placeholder="`${$t('message.scripts.hiveTableExport.QSR')}${$t('message.scripts.hiveTableExport.BM')}`"
              class="item-width">
            </Input>
          </FormItem>
        </Form>
      </div>
    </div>
    <template slot="footer">
      <Button
        v-if="stepActive === 0"
        :disabled="(activeTB && activeTB.isPartMore) || stepOne.isLargerThenFiveGb"
        @click="submitForm('stepOneForm')">{{ $t('message.scripts.hiveTableExport.XYB') }}</Button>
      <Button
        v-if="stepActive === 1"
        :loading="isLoading"
        @click="prev">{{ $t('message.scripts.hiveTableExport.SYB') }}</Button>
      <Button
        v-if="stepActive === 1"
        :loading="isLoading"
        type="primary"
        @click="submitForm('stepTwoForm')">{{ $t('message.scripts.hiveTableExport.TJ') }}</Button>
    </template>
  </Modal>
</template>
<script>
import { find, isEmpty, map } from 'lodash';
import storage from '@dataspherestudio/shared/common/helper/storage';
import directoryDialog from '@dataspherestudio/shared/components/directoryDialog/index.vue';
export default {
  name: 'ExportTable',
  components: {
    directoryDialog,
  },
  props: {
    width: Number,
    tableDetail: {
      type: Object,
      default: () => {},
    },
    dbList: {
      type: Array,
      default: () => [],
    },
    tree: {
      type: Array,
      default: () => [],
    },
    loadDataFn: Function,
    filterNode: Function,
  },
  data() {
    const validateName = (rule, value, callback) => {
      if (this.activeTB.isView) {
        callback(new Error(this.$t('message.scripts.hiveTableExport.WFCZST')));
      } else if (this.activeTB.isPartMore) {
        callback(new Error(this.$t('message.scripts.hiveTableExport.ZBZCDBDC')));
      } else {
        callback();
      }
    };
    return {
      show: false,
      stepActive: 0,
      isLoading: false,
      isPartLoading: false,
      stepOne: {
        dbName: '',
        tbName: '',
        filterTbList: [],
        separator: ',',
        partitions: '',
        column: [],
        exportType: '',
        isHasHeader: false,
        isOverwrite: this.$t('message.scripts.hiveTableExport.ZJ'),
        chartset: 'utf-8',
        isLargerThenOneGb: false,
        isLargerThenFiveGb: false,
        nullValue: 'NULL',
      },
      stepTwo: {
        type: 'share',
        path: null,
        fileName: '',
        sheetName: '',
      },
      libs: {
        types: [
          { label: this.$t('message.scripts.hiveTableExport.GXML'), value: 'share' },
          { label: 'HDFS', value: 'hdfs' },
        ],
        separator: [
          { label: this.$t('message.scripts.hiveTableExport.DH'), value: ',' },
          { label: this.$t('message.scripts.hiveTableExport.FH'), value: ';' },
          { label: this.$t('message.scripts.hiveTableExport.ZBF'), value: '\\t' },
          { label: this.$t('message.scripts.hiveTableExport.KG'), value: '%20' },
        ],
        exportTypes: [
          { label: 'xlsx', value: 'xlsx' },
          { label: 'csv', value: 'csv' },
        ],
        chartset: [
          { label: 'utf-8', value: 'utf-8' },
          { label: 'GBK', value: 'GBK' },
        ],
        replacementList: [
          {
            value: 'NULL',
            label: 'NULL',
          }, {
            value: 'BLANK',
            label: this.$t('message.scripts.hiveTableExport.KZFC'),
          },
        ],
      },
      stepOnerules: {
        dbName: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZSJK'), trigger: 'change' },
        ],
        tbName: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZB'), trigger: 'change' },
          { validator: validateName, trigger: 'change' },
        ],
        separator: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZFGF'), trigger: 'change' },
        ],
        partitions: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZYGFQBDC'), trigger: 'change' },
        ],
        column: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.ZSXZYGZD'), trigger: 'change', type: 'array' },
        ],
        exportType: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZDCGS'), trigger: 'change' },
        ],
        chartset: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZBMGS'), trigger: 'change' },
        ],
        nullValue: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZXYJKZTHDZ'), trigger: 'change' },
        ],
      },
      stepTworules: {
        type: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZDCLJDLX'), trigger: 'change' },
        ],
        path: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.XZDCLJ'), trigger: 'change' },
        ],
        fileName: [
          { required: true, message: this.$t('message.scripts.hiveTableExport.DRDCDWJ'), trigger: 'change' },
          { min: 1, max: 100, message: this.$t('message.scripts.hiveTableExport.CDZ'), trigger: 'change' },
          { type: 'string', pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]*$/, message: this.$t('message.scripts.hiveTableExport.WJMZZCZW'), trigger: 'change' },
        ],
      },
      showTbOptions: []
    };
  },
  computed: {
    activeDB() {
      if (this.dbList && this.stepOne.dbName) {
        const cur = find(this.dbList, (item) => item.name === this.stepOne.dbName);
        return cur;
      }
      return {
        children: [],
      };
    },
    activeTB() {
      if (this.activeDB && this.stepOne.tbName) {
        return find(this.activeDB.children, (item) => item.name === this.stepOne.tbName);
      }
      return null;
    },
    isUnExport() {
      if (this.activeTB) {
        return !(this.stepOne.dbName && this.stepOne.tbName) || this.activeTB.isPartMore || this.activeTB.isView;
      }
      return null;
    },
  },
  methods: {
    async open() {
      if (this.isLoading) {
        this.$Message.warning(this.$t('message.scripts.hiveTableExport.CBDC'));
      } else {
        this.stepActive = 0;
        this.reset();
        if (this.tableDetail) {
          let { dbName, name } = this.tableDetail;
          Object.assign(this.stepOne, {
            exportType: '',
            dbName,
            tbName: name,
          });
          this.$nextTick(() => {
            this.getPartitionInfo();
          });
        }
      }
      this.show = true;
    },

    close() {
      this.reset();
      this.show = false;
    },
    reset() {
      if (!this.isLoading) {
        this.stepOne = {
          dbName: '',
          dbOpt: [],
          tbName: '',
          path: null,
          separator: ',',
          chartset: 'utf-8',
          partitions: '',
          column: [],
          exportType: '',
          isHasHeader: false,
          isOverwrite: this.$t('message.scripts.hiveTableExport.ZJ'),
          isLargerThenOneGb: false,
          isLargerThenFiveGb: false,
          nullValue: 'NULL',
        };
        this.stepTwo = {
          type: 'share',
          path: null,
          fileName: '',
          sheetName: '',
        };
        this.stepActive = 0;
        this.$refs.stepOneForm.resetFields();
        this.$refs.stepTwoForm.resetFields();
      }
    },
    handleHiveDbChange(val) {
      if (val) {
        this.stepOne.tbName = '';
        if (!this.activeDB.children.length) {
          this.$emit('get-tables', this.activeDB);
        }
      }
    },

    handleTbInput(val) {
      if (!val) return;
      this.stepOne.exportType = '';
      if (isEmpty(this.activeTB.children)) {
        this.getPartitionInfo();
      } else {
        this.parseTableColumn();
      }
    },

    handleTypeChange() {
      this.getRootpath();
      this.$emit('get-tree', this.stepTwo.type);
    },

    prev() {
      this.stepActive = 0;
    },

    submitForm(type) {
      this.$refs[type].validate((valid) => {
        if (!valid) return false;
        if (type === 'stepOneForm') {
          this.stepActive = 1;
          if (!this.stepTwo.path) {
            this.getRootpath();
          }
          if (!this.stepTwo.fileName) {
            this.stepTwo.fileName = this.stepOne.tbName;
          }
          return this.$emit('get-tree', this.stepTwo.type);
        }
        this.exportTable();
      });
    },

    handleExportTypeOpen(isOpen) {
      if (!isOpen || !this.activeTB) return;
      let partition = null;
      if (this.activeTB.isPartition) {
        if (!this.stepOne.partitions) {
          this.$Message.warning(this.$t('message.scripts.hiveTableExport.XZFQ'));
          this.$refs.stepOneForm.validateField('partitions');
          return;
        }
        partition = find(this.activeTB.partitions, (o) => {
          return o.title = this.stepOne.partitions;
        });
        this.parseSize(partition).then((size) => {
          const largerThenOneGb = this.determinesSizeOverflow(size, 1);
          const largerThenFiveGb = this.determinesSizeOverflow(size, 5);
          this.$set(this.stepOne, 'isLargerThenOneGb', largerThenOneGb);
          this.$set(this.stepOne, 'isLargerThenFiveGb', largerThenFiveGb);
          if (largerThenFiveGb) {
            return this.$Message.warning(this.$t('message.scripts.hiveTableExport.BDXCG'));
          }
        });
      }
    },

    parseSize(partition) {
      return new Promise((resolve) => {
        if (this.activeTB.isView) return resolve(0);
        if (!partition && this.activeTB.size) return resolve(this.activeTB.size);
        if (partition && partition.size) return resolve(partition.size);
        const params = {
          database: this.stepOne.dbName,
          table: this.stepOne.tbName,
          partition,
        };
        this.$emit('get-size', params, (size) => {
          resolve(size);
        });
      });
    },

    determinesSizeOverflow(size, threshold) {
      if (size === 0) return false;
      const sizeNum = parseInt(size, 10);
      const determinesMb = sizeNum >= (threshold * 1000) && size.match('MB');
      const determinesGb = sizeNum >= threshold && size.match('GB');
      const determinesTb = size.match('TB');
      if (determinesMb || determinesGb || determinesTb) {
        return true;
      }
      return false;
    },

    exportTable() {
      const columnList = this.stepOne.column;
      const columns = columnList.length === this.activeTB.children.length ? '*' : columnList.toString();
      this.stepOne.isPartition = this.activeTB.isPartition;
      this.$emit('export', this.stepOne, this.stepTwo, columns,this.activeTB);
    },
    setNode(node) {
      this.stepTwo.path = node.path;
    },
    parseTableColumn() {
      this.stepOne.column = [];
      const setColumns = () => {
        this.$nextTick(() => {
          this.stepOne.column = map(this.activeTB.children, (o) => o.name);
        });
      };
      if (!isEmpty(this.activeTB.children)) {
        setColumns();
      } else {
        this.isLoading = true;
        this.$emit('get-columns', {
          item: this.activeTB,
          isOpen: false,
          async: true,
        }, () => {
          setColumns();
          this.isLoading = false;
        });
      }
    },
    getPartitionInfo() {
      if (!this.activeTB) return;
      const determinesPartition = (isPart, isMultPart) => {
        this.isPartLoading = false;
        this.$refs.stepOneForm.validateField('tbName');
        if (!isMultPart) {
          this.parseTableColumn();
        }
      };
      let { isPartition, isPartMore } = this.activeTB;
      this.isPartLoading = true;
      if (Object.prototype.hasOwnProperty.call(this.activeTB, 'isPartition')) {
        determinesPartition(isPartition, isPartMore);
      } else {
        this.$emit('get-partitions', this.activeTB, ({ isPartition, isPartMore }) => {
          determinesPartition(isPartition, isPartMore);
        });
      }
    },
    getRootpath() {
      const type = this.stepTwo.type === 'share' ? 'shareRootPath' : 'hdfsRootPath';
      const path = storage.get(type, 'session');
      this.stepTwo.path = path || '';
    },

    queryWhenOpen(isOpen) {
      if (isOpen) {
        this.getShowTbList()
      }
    },
    getShowTbList(query) {
      if (this.activeDB.children && query) {
        this.showTbOptions = this.activeDB.children.filter(it => it.name.indexOf(query.toLowerCase()) > -1).slice(0, 250);
        return
      }
      this.showTbOptions = this.activeDB.children.slice(0, 250);
    },
    remoteMethod1(query) {
      if (query !== "") {
        this.getShowTbList(query)
      } else {
        this.showTbOptions = []
      }
    },
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
