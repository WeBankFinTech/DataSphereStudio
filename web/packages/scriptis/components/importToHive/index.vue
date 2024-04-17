<template>
  <Modal
    v-model="modal.show"
    :width="modal.width"
    :fullscreen="isFullScreen"
  >
    <div slot="header">
      <span>{{$t('message.scripts.importToHive.DRWJ')}}</span>
      <span @click="fullScreenModal" class="full-btn">
        <Icon :type="isFullScreen?'md-contract':'md-expand'" />
        {{isFullScreen?$t('message.scripts.cancelfullscreen'):$t('message.scripts.fullscreen')}}
      </span>
    </div>
    <!-- content -->
    <div
      class="we-import-to-hive we-import-fix-style"
      :class="{'first-step': modal.step === 0}">
      <div class="we-import-to-hive-steps">
        <Steps
          :current="modal.step">
          <step
            :title="`${$t('message.scripts.importToHive.BZONE')}：${$t('message.scripts.importToHive.WJDR')}`"/>
          <step
            :title="`${$t('message.scripts.importToHive.BZTOW')}：${$t('message.scripts.importToHive.DRZMRB')}`"/>
        </Steps>

      </div>
      <div class="we-import-to-hive-form">
        <!-- first step-->
        <Form
          v-show="modal.step === 0"
          :model="firstStep"
          :label-width="80">
          <!-- <p v-if="!isXlsType" class="step-form-title">{{ $t('message.scripts.importToHive.LY') }}</p> -->
          <FormItem
            :label="$t('message.scripts.importToHive.LX')"
            v-show="!hideDir"
          >
            <Select
              v-model="firstStep.type"
              :placeholder="$t('message.scripts.importToHive.QXZLX')"
              @on-change="emitTypeChange">
              <Option
                v-for="(item) in staticData.type"
                :label="item.label"
                :value="item.value"
                :key="item.value"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.importToHive.LJ')"
          >
            <Input
              v-if="!hideDir"
              v-model="filterText"
              :placeholder="$t('message.common.navBar.dataStudio.searchPlaceholder')"
              class="we-directory-input"></Input>
            <directory-dialog
              :height="226"
              :tree="tree"
              :is-hide="hideDir"
              :load-data-fn="loadDataFn"
              :filter-node="handelFilterNode"
              :filter-text="filterText"
              :path="firstStep.exportPath"
              :fs-type="firstStep.type"
              :is-root-default-open="true"
              @set-node="setNode"/>
          </FormItem>
          <template v-if="firstStep.exportPath && (isTxtType || isXlsType)">
            <!-- <p
              v-if="!isXlsType"
              class="step-form-title">{{ $t('message.scripts.importToHive.GSH') }}</p> -->
            <template>
              <FormItem
                :label="$t('message.scripts.importToHive.SHWBT')">
                <Checkbox
                  v-model="firstStep.isHasHeader"
                />
              </FormItem>
              <FormItem
                v-if="!isXlsType"
                :label="$t('message.scripts.importToHive.FGF')"
              >
                <Select
                  v-model="firstStep.separator"
                  :placeholder="$t('message.scripts.importToHive.QXZLX')"
                  transfer>
                  <Option
                    v-for="(item) in staticData.separator"
                    :label="item.label"
                    :value="item.value"
                    :key="item.value"/>
                </Select>
              </FormItem>
              <FormItem
                v-if="!isXlsType"
                :label="$t('message.scripts.importToHive.BMGS')"
              >
                <Select
                  v-model="firstStep.chartset"
                  :placeholder="$t('message.scripts.importToHive.QXZBMGS')"
                  transfer
                >
                  <Option
                    v-for="(item) in staticData.chartset"
                    :label="item.label"
                    :value="item.value"
                    :key="item.value"/>
                </Select>
              </FormItem>
              <FormItem
                v-if="!isXlsType"
                :label="$t('message.scripts.importToHive.XDF')"
              >
                <Select
                  v-model="firstStep.quote"
                  clearable
                  :placeholder="$t('message.scripts.importToHive.WXDF')"
                  transfer>
                  <Option
                    v-for="(item, index) in staticData.quote"
                    :label="item.label"
                    :value="item.value"
                    :key="index"/>
                </Select>
              </FormItem>
            </template>

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
          <!-- <p class="step-form-title">{{ $t('message.scripts.importToHive.MC') }}</p> -->
          <FormItem
            :label="$t('message.scripts.importToHive.SJK')"
            prop="dbName">
            <Select
              v-model="secondStep.dbName"
              :placeholder="$t('message.scripts.importToHive.QSRKM')"
              @on-change="handleHiveDbChange"
            >
              <Option
                v-for="(item) in dbFilterList"
                :label="item.name"
                :value="item.name"
                :key="item.name"/>
            </Select>
          </FormItem>
          <FormItem
            :label="$t('message.scripts.importToHive.SJB')"
            prop="tbName">
            <Input
              v-model="secondStep.tbName"
              :disabled="!secondStep.dbName"
              :placeholder="$t('message.scripts.importToHive.SRSJBM')" />
          </FormItem>
          <Row>
            <Col span="12">
              <FormItem
                v-if="isShowPartition"
                :label="$t('message.scripts.importToHive.FQ')"
                prop="partitionValue"
              >
                <div style="white-space: nowrap;">
                  <span style="max-width: 40%;overflow: hidden;text-overflow: ellipsis;display: inline-block;vertical-align: top;" :title="secondStep.partition">{{ secondStep.partition }}</span>
                  <span> = </span>
                  <Input
                    ref="partition"
                    v-model="secondStep.partitionValue"
                    style="width: 50%" />
                </div>
              </FormItem>
            </Col>
            <Col span="6">
              <FormItem
                v-if="isShowOverwrite"
                :label="$t('message.scripts.importToHive.FX')">
                <Checkbox
                  v-model="secondStep.isOverwrite"/>
              </FormItem>
            </Col>
            <Col span="6">
              <FormItem
                v-if="isNewPartition && !validator.isView"
                :label="$t('message.scripts.importToHive.XZFQ')"
              >
                <Checkbox
                  v-model="secondStep.isNewPartition"/>
              </FormItem>
            </Col>
            <Col
              v-if="isNewPartition && secondStep.isNewPartition"
              span="9">
              <FormItem
                :label="$t('message.scripts.importToHive.FQ')"
                prop="partition"
              >
                <Input
                  v-model="secondStep.partition" />
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
                  style="width: calc(100% - 30px);" />
              </FormItem>
            </Col>
          </Row>
          <FormItem
            v-if="isXlsType"
            :label="$t('message.scripts.importToHive.SHEETB')"
            prop="moreSheet"
          >
            <Select
              v-model="secondStep.moreSheet"
              class="step-from-sheet"
              @on-change="handleSheetChange"
            >
              <Option
                v-for="(item, index) in secondStep.sheetList"
                :value="item.label"
                :key="index"
                :label="item.label">{{ item.label }}</Option>
            </Select>
          </FormItem>
          <!-- <p class="step-form-title">{{ $t('message.scripts.importToHive.ZG') }}</p> -->
          <field-list
            :key="secondStep.moreSheet"
            :list="secondStep.fields"
            :static-data="staticData"
            :is-xls="isXlsType"
            @comment-click="handleCommentClick"
            @delete-click="handleItemDelete"
            @fields-type-click="handleFieldsTypeChange"
            @on-scroll="handleScroll"/>
        </Form>
        <!-- second step end-->
      </div>
    </div>
    <!-- end content -->
    <template slot="footer">
      <Button
        v-if="modal.step === 0"
        :loading="modal.loading"
        @click="nextStep">{{ $t('message.scripts.importToHive.XYB') }}</Button>
      <Button
        v-if="modal.step === 1"
        :loading="modal.loading"
        @click="prevStep">{{ $t('message.scripts.importToHive.SYB') }}</Button>
      <Button
        v-if="modal.step === 1 && isXlsType"
        :loading="modal.loading"
        type="primary"
        :disabled="validator.isView"
        @click="submit('continue')">{{ $t('message.scripts.importToHive.TJHJXDRSJ') }}</Button>
      <Button
        v-if="modal.step === 1"
        :loading="modal.loading"
        type="primary"
        :disabled="validator.isView"
        @click="submit('close')">{{ $t('message.scripts.importToHive.TJBJS') }}</Button>
    </template>
  </Modal>
</template>
<script>
import { indexOf, find, debounce } from 'lodash';
import moment from 'moment';
import fieldList from './fieldList.vue';
import directoryDialog from '@dataspherestudio/shared/components/directoryDialog/index.vue';
import mixin from '@dataspherestudio/shared/common/service/mixin';
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
      dbFilterList: [],
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
        moreSheet: '',
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
          message: this.$t('message.scripts.importToHive.LXWCHAR'),
        }],
        precision: [{
          pattern: /^([1-9]\d*.?|0.)\d*$/,
          message: this.$t('message.scripts.importToHive.ZDJDSRYW'),
        }],
        scale: [{
          pattern: /^[0-9]+$/,
          message: this.$t('message.scripts.importToHive.ZDXSWYDY'),
        }],
        comment: [{
          pattern: /^[\u4e00-\u9fa5\w]{1,100}$/,
          message: this.$t('message.scripts.importToHive.ZDZSZCZW'),
        }],
      },
      ruleValidate: {
        dbName: [{
          required: true,
          message: this.$t('message.scripts.importToHive.SJKMBNWK'),
        }],
        tbName: [
          {
            required: true,
            message: this.$t('message.scripts.importToHive.BMBNWK'),
          },
          {
            pattern: /^[a-zA-Z][a-zA-Z0-9_]{0,100}$/,
            message: this.$t('message.scripts.importToHive.BMJZCZMKT'),
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
            message: this.$t('message.scripts.importToHive.FQMBNWK'),
          },
          {
            pattern: /^[A-Za-z0-9-]+$/,
            message: this.$t('message.scripts.importToHive.FQMZCSZ'),
          },
        ],
        partitionValue: [
          {
            required: true,
            message: this.$t('message.scripts.importToHive.FQZBNWK'),
          },
          {
            pattern: /^[A-Za-z0-9-]+$/,
            message: this.$t('message.scripts.importToHive.FQZZZCSZ'),
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
        moreSheet: [{
          required: true,
          message: 'sheet表不能为空',
        }],
      },
      staticData: {
        type: [
          { label: this.$t('message.scripts.importToHive.GXMLDR'), value: 'share' },
          { label: this.$t('message.scripts.importToHive.HDFSDR'), value: 'hdfs' },
        ],
        separator: [
          { label: this.$t('message.scripts.importToHive.DH'), value: ',' },
          { label: this.$t('message.scripts.importToHive.FH'), value: ';' },
          { label: this.$t('message.scripts.importToHive.ZBF'), value: '\\t' },
          { label: this.$t('message.scripts.importToHive.KG'), value: '%20' },
          { label: this.$t('message.scripts.hiveTableExport.SX'), value: '|' },
        ],
        chartset: [
          { label: 'utf-8', value: 'utf-8' },
          { label: 'GBK', value: 'GBK' },
        ],
        quote: [
          { label: `${this.$t('message.scripts.importToHive.SYH')}("")`, value: '"' },
          { label: `${this.$t('message.scripts.importToHive.DYH')}('')`, value: '\'' },
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
      lastScrollStart: 0,
      lastScrollEnd: 0,
      isFullScreen: false,
      directoryHeight: 280,
      hideDir: false,
      operStatus: 'close',
      filterText: ''
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
  mixins: [mixin],
  watch: {
    'secondStep.partitionValue': function(val) {
      const isValid = this.partitionManager.areas.indexOf(val) === -1;
      if (isValid) {
        this.debounceValidateField('tbName', this);
      }
    },
    dbList(newDbList) {
      const reg = ['_qml', '_ind', '_work', '_tmp', '_bak'];
      this.dbFilterList = newDbList.filter(item => {
        const tabSuffix = item.name.substr(
          item.name.lastIndexOf('_'),
          item.name.length
        );
        return indexOf(reg, tabSuffix) !== -1;
      });
    }
  },
  methods: {
    handelFilterNode(node) {
      let label = node.label;
      let textValid = true;
      if (this.filterText) {
        let searchText = this.filterText;
        label = label.toLowerCase();
        searchText = searchText.toLowerCase();
        textValid = label.indexOf(searchText) !== -1;
      }
      return textValid && this.filterNode(node)
    },
    open(path) {
      if (this.modal.loading) {
        return this.$Message.warning(this.$t('message.scripts.importToHive.WJZZZXDR'));
      }
      this.resetSecondStep();
      this.resetFirstStep();
      this.hideDir = false
      if (path) {
        this.firstStep.exportPath = path;
        this.hideDir = true
      }
      this.firstStep.type = this.fsType;
      this.modal.show = true;
      this.operStatus = 'close'
      this.modal.step = 0;
      this.validator.isLeaf = true;
      this.isFullScreen = false;
      this.directoryHeight = 280;
      this.filterText = '';
    },

    close() {
      if (this.operStatus === 'close') {
        this.modal.show = false;
      } else {
        this.secondStep.tbName = '';
        this.secondStep.partition = '';
        this.secondStep.partitionValue = '';
        this.secondStep.moreSheet = '';
        this.secondStep.fields = [];
      }
    },
    validPartitionValue(value, cb) {
      let item = this.partitionManager.areas.indexOf(value);
      if (item !== -1) {
        this.partitionManager.duplicateValue = true;
        return cb(new Error(this.$t('message.scripts.importToHive.FQZCF')));
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
        moreSheet: '',
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
        if (db) {
          this.$emit('get-tables', db, () => {
            return this.validateFieldSync('tbName');
          });
        }
        this.validateFieldSync('tbName');
      };
      if (val) {
        this.$emit('get-hive', (dbList) => {
          return validateTbName(dbList);
        });
      }
    },
    async handleTbInput(val, cb) {
      const activeTB = find(this.activeDB.children, (o) => o.value.toLowerCase() === val.toLowerCase());
      if (!activeTB) {
        this.resetPartition();
        return cb();
      }
      this.validator.isView = activeTB.isView;
      if (this.validator.isView) {
        this.resetPartition();
        this.partitionManager.duplicateName = false;
        return cb(new Error((this.$t('message.scripts.importToHive.TSWST'))));
      }
      this.partitionManager.duplicateName = true;
      try {
        let tableInfo = await this.judgePartition(activeTB);
        let { isPartition, isPartMore, partitions } = tableInfo;
        let errMsg = this.$t('message.scripts.importToHive.BMYCZDQSJK');
        this.partitionManager.partTable = isPartition;
        // 非分区
        if (!this.partitionManager.partTable) {
          return cb(new Error(errMsg + `，${this.$t('message.scripts.importToHive.QQRSFCF')}`));
        }
        // 多级分区
        if (isPartMore) {
          this.partitionManager.multiLevel = true;
          errMsg = this.$t('message.scripts.importToHive.ZBZCDJFQ');
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
          return cb(new Error(errMsg + `，${this.$t('message.scripts.importToHive.QQURFQ')}`));
        }
      } catch (e) {
        return cb(new Error((this.$t('message.scripts.importToHive.HQFQSB'))));
      }
    },
    judgePartition(table) {
      return new Promise((resolve) => {
        if (Object.prototype.hasOwnProperty.call(table, 'isPartition')) {
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
    buildList(obj) {
      let res = [];
      Object.keys(obj).forEach(key => {
        const items = (obj[key] || []).map(item => {
          const [label, value] = Object.entries(item || {})[0];
          return { value, label };
        })
        res.push({ value: items, label: key })
      })
      return res;
    },
    nextStep() {
      if (!this.validator.isLeaf) return this.$Message.error(this.$t('message.scripts.importToHive.ZQDRWJLJ'));
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
        this.$emit('get-hive', (dbList) => {
          this.secondStep.dbName = findIndDbName(dbList);
          this.handleHiveDbChange(this.secondStep.dbName);
        });
        this.modal.step = 1;
        let exportPath = this.firstStep.exportPath;
        const pathSuffix = exportPath.substr(exportPath.lastIndexOf('.'), exportPath.length);
        if(pathSuffix !== '.txt') {
          const sheetList = this.buildList(format);
          this.secondStep.sheetList = sheetList;
          this.secondStep.moreSheet = sheetList ? sheetList[0].label : '';
          this.handleSheetChange(this.secondStep.moreSheet);
        } else {
          let { sheetName, columnName, columnType } = format;
          this.secondStep.sheetList = sheetName;
          this.secondStep.moreSheet = sheetName ? sheetName[0] : '';
          this.secondStep.fields = columnName.map((item, index) => {
            return {
              fieldName: item,
              type: columnType[index],
              comment: '',
              commentShow: false,
              dateFormat: '',
              index
            };
          });
        }
      });
    },
    handleSheetChange(val) {
      if(val) {
        const arr = this.secondStep.sheetList.find(item => item.label === val).value || [];
        this.secondStep.fields = arr.map((item, index) => {
          return {
            fieldName: item.label,
            type: item.value,
            comment: '',
            commentShow: false,
            dateFormat: '',
            index
          };
        });
      }
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
      ruleMap.tbName.push(this.$t('message.scripts.importToHive.ZBZCDJFQ'));
      if (this.isShowPartition) {
        validProps.push('partitionValue');
      }
      if (this.secondStep.isNewPartition) {
        validProps.push('partition');
      }
      if(this.isXlsType) {
        validProps.push('moreSheet');
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
        fieldName: {
          pattern: /^[a-zA-Z0-9_]+$/,
          message: this.$t('message.scripts.fieldnamestyle'),
        },
        length: {
          pattern: /^[0-9]+$/,
          message: this.$t('message.scripts.importToHive.LXWCHAR'),
        },
        precision: {
          pattern: /^([1-9]\d*.?|0.)\d*$/,
          message: this.$t('message.scripts.importToHive.ZDJDSRYW'),
        },
        scale: {
          pattern: /^[0-9]+$/,
          message: this.$t('message.scripts.importToHive.ZDXSWYDY'),
        },
        comment: [{
          pattern: /^[\u4e00-\u9fa5\w]{1,100}$/,
          message: this.$t('message.scripts.importToHive.ZDZSZCZW'),
        }],
      };
      let errMsg = '';
      this.secondStep.fields.forEach((field) => {
        let keys = Object.keys(poptipValidate);
        for (let i = 0; i < keys.length; i++) {
          let key = keys[i];
          let rule = poptipValidate[key];
          let { pattern, message } = rule;
          if (key === 'fieldName' && !field[key]) {
            errMsg = this.$t('message.scripts.importToHive.ZDMC');
            return errMsg;
          } else if (field[key] && pattern && !pattern.test(field[key])) {
            errMsg = message;
            return errMsg;
          }
        }
      });
      return errMsg;
    },
    async submit(status) {
      this.operStatus = status;
      let isValid = await this.beforeSubmit();
      let fieldErrMsg = this.fieldsValidate();
      if (fieldErrMsg) return this.$Message.error(fieldErrMsg);
      if (!isValid) return this.$Message.error(this.$t('message.scripts.importToHive.QQRSJKMCXX'));
      if (!this.isShowOverwrite) {
        this.secondStep.isOverwrite = false;
      }
      const columns = this.secondStep.fields.map((field) => {
        return {
          name: field.fieldName.trim(),
          index: field.index,
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
      this.firstStep.exportPath = '';
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
    handleItemDelete(item) {
      const index = this.secondStep.fields.findIndex((i) => i.fieldName === item.fieldName);
      if (this.secondStep.fields.length > 1) {
        this.secondStep.fields.splice(index, 1);
      } else if (this.secondStep.fields.length === 1) {
        this.$Message.warning(this.$t('message.scripts.unableDel'));
      }
    },
    // 在滚动字段列表时，需要对列表名称是否符合规范进行验证
    handleScroll(data) {
      if (this.lastScrollEnd !== data.end) {
        this.lastScrollStart = data.start;
        this.lastScrollEnd = data.end;
        // 只有滚动停止后再去验证
        const toGetCheck = debounce((that, data) => {
          for (let i = data.start; i <= data.end; i++) {
            // 表单的fields里面保存有表单需要进行验证的项，而且只有可视区域的
            const item = this.$refs.secondForm.fields.find((item) => item.prop === `fields.${i}.fieldName`)
            if (item) {
              item.validate('');
            }
          }
        }, 300)
        // 由于滚动条滚动结束后得到的数据不是当前可视区域，所以需要在异步300秒后去验证
        // 只有这样才能拿到可视区域列表的index
        setTimeout(() => {
          toGetCheck(this, data);
        }, 300)
      }
    },
    fullScreenModal() {
      this.isFullScreen = !this.isFullScreen
      if (this.isFullScreen ) {
        this.directoryHeight = window.innerHeight - 200
      } else {
        this.directoryHeight = 280
      }
    },
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
<style scoped>
.full-btn {
  float: right;
  margin-right: 30px;
  padding-top: 5px;
  cursor: pointer;
  color: #2d8cf0
}
.we-import-fix-style {
  overflow: initial;
}
</style>
