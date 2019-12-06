<template>
  <Modal
    v-model="modal.show"
    :width="modal.width"
  >
    <p slot="header">
      {{$t('message.importToHive.DRWJ')}}
    </p>
    <!-- content -->
    <div
      class="we-import-to-hive"
      :class="{'first-step': modal.step === 0}">
      <div class="we-import-to-hive-steps">
        <Steps
          :current="modal.step">
          <step
            :title="`${$t('message.importToHive.BZONE')}：${$t('message.importToHive.WJDR')}`"/>
          <step
            :title="`${$t('message.importToHive.BZTOW')}：${$t('message.importToHive.DRZMRB')}`"/>
        </Steps>

      </div>
      <div class="we-import-to-hive-form">
        <!-- first step-->
        <Form
          v-show="modal.step === 0"
          :model="firstStep"
          :label-width="80">
          <p class="step-form-title">{{ $t('message.importToHive.LY') }}</p>
          <FormItem
            :label="$t('message.importToHive.LX')"
          >
            <Select
              v-model="firstStep.type"
              :placeholder="$t('message.importToHive.QXZLX')"
              @on-change="emitTypeChange">
              <Option
                v-for="(item) in staticData.type"
                :label="item.label"
                :value="item.value"
                :key="item.value"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.importToHive.LJ')"
          >
            <directory-dialog
              :tree="tree"
              :load-data-fn="loadDataFn"
              :filter-node="filterNode"
              :path="firstStep.exportPath"
              :fs-type="firstStep.type"
              @set-node="setNode"/>
          </FormItem>
          <template v-if="firstStep.exportPath && (isTxtType || isXlsType)">
            <p
              class="step-form-title">{{ $t('message.importToHive.GSH') }}</p>
            <template v-if="!isXlsType">
              <FormItem
                :label="$t('message.importToHive.FGF')"
              >
                <Select
                  v-model="firstStep.separator"
                  :placeholder="$t('message.importToHive.QXZLX')">
                  <Option
                    v-for="(item) in staticData.separator"
                    :label="item.label"
                    :value="item.value"
                    :key="item.value"/>
                </Select>
              </FormItem>
              <FormItem
                :label="$t('message.importToHive.BMGS')"
              >
                <Select
                  v-model="firstStep.chartset"
                  :placeholder="$t('message.importToHive.QXZBMGS')"
                >
                  <Option
                    v-for="(item) in staticData.chartset"
                    :label="item.label"
                    :value="item.value"
                    :key="item.value"/>
                </Select>
              </FormItem>
              <FormItem
                :label="$t('message.importToHive.XDF')"
              >
                <Select
                  v-model="firstStep.quote"
                  clearable
                  :placeholder="$t('message.importToHive.WXDF')">
                  <Option
                    v-for="(item, index) in staticData.quote"
                    :label="item.label"
                    :value="item.value"
                    :key="index"/>
                </Select>
              </FormItem>
            </template>
            <FormItem
              :label="$t('message.importToHive.SHWBT')">
              <Checkbox
                v-model="firstStep.isHasHeader"
              />
            </FormItem>
          </template>
        </Form>
        <!-- first step end-->
        <!-- second step-->
        <Form
          v-show="modal.step === 1"
          ref="secondForm"
          :model="secondStep"
          :label-width="80"
          :rules="ruleValidate"
          class="seconed-Form">
          <p class="step-form-title">{{ $t('message.importToHive.MC') }}</p>
          <FormItem
            :label="$t('message.importToHive.SJK')"
            prop="dbName">
            <Select
              v-model="secondStep.dbName"
              :placeholder="$t('message.importToHive.QSRKM')"
              @on-change="handleHiveDbChange"
            >
              <Option
                v-for="(item) in dbList"
                :label="item.name"
                :value="item.name"
                :key="item.name"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.importToHive.SJB')"
            prop="tbName">
            <Input
              v-model="secondStep.tbName"
              :disabled="!secondStep.dbName"
              :placeholder="$t('message.importToHive.SRSJBM')">
            </Input>
          </FormItem>
          <Row>
            <Col span="12">
            <FormItem
              v-if="isShowPartition"
              :label="$t('message.importToHive.FQ')"
              prop="partitionValue"
            >
              <div>
                <span>{{ secondStep.partition }}</span>
                <span> = </span>
                <Input
                  ref="partition"
                  v-model="secondStep.partitionValue"
                  style="width: calc(100% - 30px);"></Input>
              </div>
            </FormItem>
            </Col>
            <Col span="6">
            <FormItem
              v-if="isShowOverwrite"
              :label="$t('message.importToHive.FX')">
              <Checkbox
                v-model="secondStep.isOverwrite"/>
            </FormItem>
            </Col>
            <Col span="6">
            <FormItem
              v-if="isNewPartition && !validator.isView"
              :label="$t('message.importToHive.XZFQ')"
            >
              <Checkbox
                v-model="secondStep.isNewPartition"/>
            </FormItem>
            </Col>
            <Col
              v-if="isNewPartition && secondStep.isNewPartition"
              span="9">
            <FormItem
              :label="$t('message.importToHive.FQ')"
              prop="partition"
            >
              <Input
                v-model="secondStep.partition"></Input>
            </FormItem>
            </Col>
            <Col
              v-if="isNewPartition && secondStep.isNewPartition"
              span="9">
            <FormItem
              :label-width="0"
              prop="partitionValue">
              <span> = </span>
              <Input
                v-model="secondStep.partitionValue"
                style="width: calc(100% - 30px);"></Input>
            </FormItem>
            </Col>
          </Row>
          <FormItem
            v-if="isXlsType"
            :label="$t('message.importToHive.SHEETB')"
          >
            <Select
              v-model="secondStep.moreSheet"
              multiple="multiple"
              class="step-from-sheet">
              <Option
                v-for="(item, index) in secondStep.sheetList"
                :value="item"
                :key="index"
                :label="item">{{ item }}</Option>
            </Select>
          </FormItem>
          <p class="step-form-title">{{ $t('message.importToHive.ZG') }}</p>
          <field-list
            :list="secondStep.fields"
            :static-data="staticData"
            :is-xls="isXlsType"
            @comment-click="handleCommentClick"
            @fields-type-click="handleFieldsTypeChange"/>
        </Form>
        <!-- second step end-->
      </div>
    </div>
    <!-- end content -->
    <template slot="footer">
      <Button
        v-if="modal.step === 0"
        :loading="modal.loading"
        @click="nextStep">{{ $t('message.importToHive.XYB') }}</Button>
      <Button
        v-if="modal.step === 1"
        :loading="modal.loading"
        @click="prevStep">{{ $t('message.importToHive.SYB') }}</Button>
      <Button
        v-if="modal.step === 1"
        :loading="modal.loading"
        type="primary"
        :disabled="validator.isView"
        @click="submit">{{ $t('message.importToHive.TJ') }}</Button>
    </template>
  </Modal>
</template>
<script>
import { indexOf, find, isEmpty, debounce } from 'lodash';
import moment from 'moment';
import fieldList from './fieldList.vue';
import directoryDialog from '@js/component/directoryDialog/index.vue';
export default {
  components: {
    directoryDialog,
    fieldList,
  },
  props: {
    width: {
      type: Number,
      default: 0,
    },
    tree: {
      type: Array,
      default: () => [],
    },
    dbList: {
      type: Array,
      defalut: [],
    },
    fsType: {
      type: String,
      default: 'share',
    },
    loadDataFn: Function,
    filterNode: Function,
  },
  data() {
    let that = this;
    return {
      modal: {
        show: false,
        width: '780px',
        loading: false,
        step: 0,
      },
      validator: {
        isLeaf: true,
        isView: false,
      },
      firstStep: {
        exportPath: '',
        type: 'share',
        separator: ',',
        chartset: 'utf-8',
        quote: '',
        isHasHeader: false,
      },
      secondStep: {
        tbName: '',
        dbName: '',
        moreSheet: [],
        sheetList: [],
        partition: '',
        partitionValue: '',
        isOverwrite: false,
        fields: [],
        isNewPartition: false,
      },
      partitionManager: {
        duplicateName: false,
        partTable: false,
        multiLevel: false,
        duplicateValue: false,
        areas: [],
      },
      poptipValidate: {
        length: [{
          pattern: /^[0-9]+$/,
          message: this.$t('message.importToHive.LXWCHAR'),
        }],
        precision: [{
          pattern: /^([1-9]\d*.?|0.)\d*$/,
          message: this.$t('message.importToHive.ZDJDSRYW'),
        }],
        scale: [{
          pattern: /^[0-9]+$/,
          message: this.$t('message.importToHive.ZDXSWYDY'),
        }],
        comment: [{
          pattern: /^[\u4e00-\u9fa5\w]{1,100}$/,
          message: this.$t('message.importToHive.ZDZSZCZW'),
        }],
      },
      ruleValidate: {
        dbName: [{
          required: true,
          message: this.$t('message.importToHive.SJKMBNWK'),
        }],
        tbName: [
          {
            required: true,
            message: this.$t('message.importToHive.BMBNWK'),
          },
          {
            pattern: /^[a-zA-Z][a-zA-Z0-9_]{1,100}$/,
            message: this.$t('message.importToHive.BMJZCZMKT'),
          },
          {
            validator(rule, value, callback) {
              if (!value) {
                return callback();
              }
              return that.handleTbInput(value, callback);
            },
          },
        ],
        partition: [
          {
            required: true,
            message: this.$t('message.importToHive.FQMBNWK'),
          },
          {
            pattern: /^[A-Za-z0-9-]+$/,
            message: this.$t('message.importToHive.FQMZCSZ'),
          },
        ],
        partitionValue: [
          {
            required: true,
            message: this.$t('message.importToHive.FQZBNWK'),
          },
          {
            pattern: /^[A-Za-z0-9-]+$/,
            message: this.$t('message.importToHive.FQZZZCSZ'),
          },
          {
            validator(rule, value, callback) {
              if (!value) {
                return callback();
              }
              return that.validPartitionValue(value, callback);
            },
          },
        ],
      },
      staticData: {
        type: [
          { label: this.$t('message.importToHive.GXMLDR'), value: 'share' },
          { label: this.$t('message.importToHive.HDFSDR'), value: 'hdfs' },
        ],
        separator: [
          { label: this.$t('message.importToHive.DH'), value: ',' },
          { label: this.$t('message.importToHive.FH'), value: ';' },
          { label: this.$t('message.importToHive.ZBF'), value: '\\t' },
          { label: this.$t('message.importToHive.KG'), value: '%20' },
        ],
        chartset: [
          { label: 'utf-8', value: 'utf-8' },
          { label: 'GBK', value: 'GBK' },
        ],
        quote: [
          { label: `${this.$t('message.importToHive.SYH')}("")`, value: '"' },
          { label: this.$t('message.importToHive.DYH'), value: '\'' },
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
    };
  },
  computed: {
    isXlsType() {
      let exportPath = this.firstStep.exportPath;
      if (!exportPath) return false;
      const reg = ['.xlsx', '.xls'];
      const tabSuffix = exportPath.substr(exportPath.lastIndexOf('.'), exportPath.length);
      const isXlsType = indexOf(reg, tabSuffix) !== -1;
      return isXlsType;
    },
    isTxtType() {
      let exportPath = this.firstStep.exportPath;
      if (!exportPath) return false;
      const reg = ['.txt', '.csv'];
      const tabSuffix = exportPath.substr(exportPath.lastIndexOf('.'), exportPath.length);
      const isTxtType = indexOf(reg, tabSuffix) !== -1;
      return isTxtType;
    },
    isShowPartition() {
      let { dbName, tbName } = this.secondStep;
      let { partTable, multiLevel } = this.partitionManager;
      let isShow = dbName && tbName && partTable && !multiLevel;
      this.initNewPartition(isShow);
      this.$nextTick(() => {
        if (this.$refs.partition) {
          this.validateFieldSync('partitionValue');
        }
      });
      return isShow;
    },
    isShowOverwrite() {
      let { dbName, tbName } = this.secondStep;
      let { duplicateName, partTable, duplicateValue } = this.partitionManager;
      return dbName && tbName && ((duplicateName && !partTable) || (duplicateName && duplicateValue));
    },
    isNewPartition() {
      let { dbName, tbName } = this.secondStep;
      let isShow = !this.partitionManager.duplicateName && dbName && tbName;
      this.initNewPartition(isShow);
      return isShow;
    },
    activeDB() {
      let cur = [];
      if (this.dbList.length && this.secondStep.dbName) {
        cur = find(this.dbList, (item) => item.name === this.secondStep.dbName);
      }
      return cur;
    },
  },
  watch: {
    'secondStep.partitionValue': function(val) {
      const isValid = this.partitionManager.areas.indexOf(val) === -1;
      if (isValid) {
        this.debounceValidateField('tbName', this);
      }
    },
  },
  methods: {
    open(path) {
      if (this.modal.loading) {
        return this.$Message.warning(this.$t('message.importToHive.WJZZZXDR'));
      }
      this.resetSecondStep();
      this.resetFirstStep();
      if (path) {
        this.firstStep.exportPath = path;
      }
      this.firstStep.type = this.fsType;
      this.modal.show = true;
      this.modal.step = 0;
      this.validator.isLeaf = true;
    },

    close() {
      this.modal.show = false;
    },
    validPartitionValue(value, cb) {
      let item = this.partitionManager.areas.indexOf(value);
      if (item !== -1) {
        this.partitionManager.duplicateValue = true;
        return cb(new Error(this.$t('message.importToHive.FQZCF')));
      }
      this.partitionManager.duplicateValue = false;
      cb();
    },
    resetFirstStep() {
      this.firstStep = {
        exportPath: '',
        type: 'share',
        separator: ',',
        chartset: 'utf-8',
        quote: '',
        isHasHeader: false,
      };
    },
    resetSecondStep() {
      this.secondStep = {
        tbName: '',
        dbName: '',
        moreSheet: [],
        sheetList: [],
        partition: '',
        partitionValue: '',
        isOverwrite: false,
        fields: [],
        isNewPartition: false,
      };
    },
    initNewPartition(val) {
      let today = moment.unix(moment().unix()).format('YYYYMMDD');
      const isPart = this.partitionManager.partTable;
      const isNewPart = this.secondStep.isNewPartition;
      this.secondStep.partition = val && (isPart || isNewPart) ? 'ds' : '';
      this.secondStep.partitionValue = val && (isPart || isNewPart) ? today : '';
    },
    handleHiveDbChange(val) {
      const validateTbName = (dbList) => {
        const db = find(dbList, (item) => item.name === val);
        if (db && isEmpty(db.children)) {
          this.$emit('get-tables', db, () => {
            return this.validateFieldSync('tbName');
          });
        }
        this.validateFieldSync('tbName');
      };
      if (val) {
        if (!this.dbList.length) {
          this.$emit('get-hive', (dbList) => {
            return validateTbName(dbList);
          });
        }
        validateTbName(this.dbList);
      }
    },
    async handleTbInput(val, cb) {
      const activeTB = find(this.activeDB.children, (o) => o.value === val);
      if (!activeTB) {
        this.resetPartition();
        return cb();
      }
      this.validator.isView = activeTB.isView;
      if (this.validator.isView) {
        this.resetPartition();
        this.partitionManager.duplicateName = false;
        return cb(new Error((this.$t('message.importToHive.TSWST'))));
      }
      this.partitionManager.duplicateName = true;
      try {
        let tableInfo = await this.judgePartition(activeTB);
        let { isPartition, isPartMore, partitions } = tableInfo;
        let errMsg = this.$t('message.importToHive.BMYCZDQSJK');
        this.partitionManager.partTable = isPartition;
        // 非分区
        if (!this.partitionManager.partTable) {
          return cb(new Error(errMsg + `，${this.$t('message.importToHive.QQRSFCF')}`));
        }
        // 多级分区
        if (isPartMore) {
          this.partitionManager.multiLevel = true;
          errMsg = this.$t('message.importToHive.ZBZCDJFQ');
          return cb(new Error(errMsg));
        }
        // 一级分区
        const firstPartition = partitions[0] || { title: '' };
        this.secondStep.partition = firstPartition.title.slice(0, firstPartition.title.indexOf('='));
        this.partitionManager.areas = [];
        partitions.forEach((part) => {
          let partitionTitle = part.title;
          const area = partitionTitle.slice(partitionTitle.indexOf('=') + 1, partitionTitle.length);
          this.partitionManager.areas.push(area);
        });
        if (!this.secondStep.partitionValue) {
          return cb(new Error(errMsg + `，${this.$t('message.importToHive.QQURFQ')}`));
        }
      } catch (e) {
        return cb(new Error((this.$t('message.importToHive.HQFQSB'))));
      }
    },
    judgePartition(table) {
      return new Promise((resolve) => {
        if (table.hasOwnProperty('isPartition')) {
          resolve(table);
        } else {
          this.$emit('get-partitions', table, (tableInfo) => {
            resolve(tableInfo);
          });
        }
      });
    },
    resetPartition() {
      Object.keys(this.partitionManager).forEach((key) => {
        this.partitionManager[key] = false;
      });
      this.partitionManager.areas = [];
    },
    getTbName() {
      const path = this.firstStep.exportPath;
      return path.slice(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
    },
    nextStep() {
      if (!this.validator.isLeaf) return this.$Message.error(this.$t('message.importToHive.ZQDRWJLJ'));
      this.modal.loading = true;
      this.secondStep.tbName = this.getTbName();
      this.$emit('get-content', this.firstStep, this.isXlsType, (format) => {
        // 默认展示个人库
        const findIndDbName = (list) => {
          const userName = this.getUserName();
          const indDb = find(list, (item) => item.name.indexOf('ind') !== -1 && item.name.indexOf(userName) !== -1);
          return indDb ? indDb.name : '';
        };
        this.modal.loading = false;
        if (!format) return;
        if (!this.dbList.length) {
          this.$emit('get-hive', (dbList) => {
            this.secondStep.dbName = findIndDbName(dbList);
            this.handleHiveDbChange(this.secondStep.dbName);
          });
        } else {
          this.secondStep.dbName = findIndDbName(this.dbList);
          this.handleHiveDbChange(this.secondStep.dbName);
        }
        let { sheetName, columnName, columnType } = format;
        this.modal.step = 1;
        this.secondStep.sheetList = sheetName;
        this.secondStep.moreSheet = sheetName ? sheetName[0] : '';
        this.secondStep.fields = columnName.map((item, index) => {
          return {
            fieldName: item,
            type: columnType[index],
            comment: '',
            commentShow: false,
            dateFormat: '',
          };
        });
      });
    },
    prevStep() {
      this.modal.step = 0;
    },
    async beforeSubmit() {
      let ruleMap = {};
      let validProps = ['dbName', 'tbName'];
      Object.keys(this.ruleValidate).forEach((key) => {
        let rules = this.ruleValidate[key];
        let msgList = [];
        rules.forEach((r) => {
          if (r.message) msgList.push(r.message);
        });
        ruleMap[key] = msgList;
      });
      ruleMap.tbName.push(this.$t('message.importToHive.ZBZCDJFQ'));
      if (this.isShowPartition) {
        validProps.push('partitionValue');
      }
      if (this.secondStep.isNewPartition) {
        validProps.push('partition');
      }
      for (let prop of validProps) {
        let validateMsg = await this.validateFieldSync(prop);
        if (validateMsg && ruleMap[prop].indexOf(validateMsg) !== -1) {
          return false;
        }
      }
      return true;
    },
    validateFieldSync(prop) {
      return new Promise((resolve) => {
        this.$refs.secondForm.validateField(prop, (msg) => {
          resolve(msg);
        });
      });
    },
    fieldsValidate() {
      let poptipValidate = {
        length: {
          pattern: /^[0-9]+$/,
          message: this.$t('message.importToHive.LXWCHAR'),
        },
        precision: {
          pattern: /^([1-9]\d*.?|0.)\d*$/,
          message: this.$t('message.importToHive.ZDJDSRYW'),
        },
        scale: {
          pattern: /^[0-9]+$/,
          message: this.$t('message.importToHive.ZDXSWYDY'),
        },
        comment: [{
          pattern: /^[\u4e00-\u9fa5\w]{1,100}$/,
          message: this.$t('message.importToHive.ZDZSZCZW'),
        }],
      };
      let errMsg = '';
      this.secondStep.fields.forEach((field) => {
        let keys = Object.keys(poptipValidate);
        for (let i = 0; i < keys.length; i++) {
          let key = keys[i];
          let rule = poptipValidate[key];
          let { pattern, message } = rule;
          if (field[key] && pattern && !pattern.test(field[key])) {
            errMsg = message;
            return errMsg;
          }
        }
      });
      return errMsg;
    },
    async submit() {
      let isValid = await this.beforeSubmit();
      let fieldErrMsg = this.fieldsValidate();
      if (fieldErrMsg) return this.$Message.error(fieldErrMsg);
      if (!isValid) return this.$Message.error(this.$t('message.importToHive.QQRSJKMCXX'));
      if (!this.isShowOverwrite) {
        this.secondStep.isOverwrite = false;
      }
      const columns = this.secondStep.fields.map((field) => {
        return {
          name: field.fieldName.trim(),
          comment: field.comment,
          type: field.type,
          length: field.length,
          scale: field.scale,
          precision: field.precision,
          dateFormat: field.dateFormat,
        };
      });
      this.$emit('export', {
        firstStep: this.firstStep,
        secondStep: this.secondStep,
        isXls: this.isXlsType,
        columns,
        whetherRepeat: this.partitionManager,
      });
    },
    setNode(node) {
      this.validator.isLeaf = node.isLeaf;
      this.firstStep.exportPath = node.path;
    },
    emitTypeChange() {
      this.$emit('on-type-change', this.firstStep.type);
    },
    debounceValidateField: debounce((prop, that) => {
      that.$refs.secondForm.validateField(prop, () => {});
    }, 300),
    handleCommentClick(item) {
      item.commentShow = !item.commentShow;
    },
    handleFieldsTypeChange(item) {
      if (item.type === 'date' && !item.dateFormat) {
        item.dateFormat = 'yyyyMMdd';
      }
    },
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
