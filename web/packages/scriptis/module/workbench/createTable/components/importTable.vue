<template>
  <div class="import-table">
    <Card>
      <p slot="title">
        {{$t('message.scripts.createTable.LYSX')}}
      </p>
      <hive
        v-if="source.importType === 'hive'"
        :hive="source.hive"
        :db-list="dbList"
        @get-fields="getFields"
        @get-tables="getTables"></hive>
      <csv
        v-if="source.importType === 'csv' || source.importType === 'xlsx'"
        :import-type="source.importType"
        :options="source.table"
        :sheetArray="target.sheetName"
        @isPathLeaf="$emit('isPathLeaf', arguments)"
        @get-fields="getFields"
        @clear-fields="handleClearFields"
        @on-change="handleSheetChange"></csv>
    </Card>
    <Card>
      <p slot="title">
        {{$t('message.scripts.createTable.MBZD')}}
      </p>
      <Form
        ref="fieldsForm"
        class="import-table-fields"
        :modal="target.importFieldsData">
        <fields-table
          :key="fieldTableKey"
          :fields="target.importFieldsData.fields"
          :table-columns="source.importType === 'hive' ? hiveFieldsColumns : csvOrXlsxfieldsColumns"
          :can-be-delete="true"
          :special-field-key="specialFieldKey"
          @on-delete="onDelete"
          @on-tag-click="onTagClick"></fields-table>
      </Form>
    </Card>
    <Card v-if="partitionTable">
      <p slot="title">
        {{$t('message.scripts.createTable.partitionAttr')}}
      </p>
      <Form
        class="import-table-fields"
        :model="target.importPartitionsData">
        <fields-table
          :fields="target.importPartitionsData.fields"
          :table-columns="partTableColumns"></fields-table>
      </Form>
    </Card>
    <fieldsModal
      ref="fieldModal"
      :field-list="target.importFieldsData.fields"
      :actived-field-item="activedFieldItem"
    ></fieldsModal>
  </div>
</template>
<script>
import config from '../config.js';
import hive from './importTable_hive.vue';
import csv from './importTable_csv.vue';
import fieldsTable from '@dataspherestudio/shared/components/virtualTable/fieldTable/fieldsTable.vue';
import fieldsModal from './fieldModal.vue';
import api from '@dataspherestudio/shared/common/service/api';
export default {
  components: {
    hive,
    csv,
    fieldsTable,
    fieldsModal,
  },
  props: {
    dbList: {
      type: Array,
      required: true,
    },
    source: {
      type: Object,
      required: true,
    },
    target: {
      type: Object,
      require: true,
    },
    partitionTable: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      hiveFieldsColumns: config.importTableFieldsConfig,
      csvOrXlsxfieldsColumns: config.importTableFieldsCsvAndXlsxConfig,
      partTableColumns: config.importTablePartitionsConfig,
      specialFieldKey: 'sourceFields',
      activedFieldItem: null,
      fieldTableKey: '',
    };
  },
  methods: {
    getFields(data) {
      if (this.source.importType === 'hive') {
        this.$emit('get-fields', data);
      } else {
        this.getFileContent(data);
      }
    },
    getTables(val) {
      this.$emit('get-tables', val);
    },
    getFileContent(option) {
      let separator = option.separator;
      if (option.separator === '\\t') {
        separator = '%5Ct';
      } else if (option.separator === '%20') {
        separator = ' ';
      }
      const encoding = this.source.importType === 'xlsx' ? '' : option.chartset;
      const fieldDelimiter = this.source.importType === 'xlsx' ? '' : separator;
      let escapeQuotes = false;
      let quote = '';
      if (option.quote) {
        escapeQuotes = true;
        quote = option.quote;
      }
      const url = `/filesystem/getSheetInfo?path=${option.exportPath}&encoding=${encoding}&fieldDelimiter=${fieldDelimiter}&hasHeader=${option.isHasHeader}&escapeQuotes=${escapeQuotes}&quote=${quote}`;
      api.fetch(url, {}, {
        method: 'get',
        timeout: '600000',
      }).then((rst) => {
        const sheetList = this.buildList(rst.sheetInfo || {});
        this.target.sheetName = sheetList;
        this.target.importFieldsData.fields = this.buildList(sheetList[0].value || {}).map((item, index) => {
          return {
            name: item.label,
            type: item.value,
            alias: '',
            sourceFields: '',
            rule: '',
            partitionField: 0,
            primary: 0,
            comment: '',
            length: '',
            express: '',
            index
          };
        });
        if(sheetList.length > 0) {
          this.source.table.sheet = sheetList[0].label;
        } else {
          this.source.table.sheet = '';
        }
      }).catch(() => {
      });
    },
    handleSheetChange(val) {
      this.fieldTableKey = val;
      if(val) {
        const item = this.target.sheetName.find(item => item.label === val).value || {};
        const arr = this.buildList(item);
        this.target.importFieldsData.fields  = arr.map((item, index) => {
          return {
            name: item.label,
            type: item.value,
            alias: '',
            sourceFields: '',
            rule: '',
            partitionField: 0,
            primary: 0,
            comment: '',
            length: '',
            express: '',
            index
          };
        });
      } else {
        this.target.importFieldsData.fields = [];
      }
    },
    buildList(obj) {
      let res = [];
      Object.keys(obj).forEach(key => {
        res.push({ value: obj[key], label: key })
      })
      return res;
    },
    handleClearFields() {
      this.target.sheetName = [];
      this.target.importFieldsData.fields = [];
    },
    onDelete(item, index) {
      this.target.importFieldsData.fields.splice(index, 1);
    },
    onTagClick(item) {
      this.activedFieldItem = item;
      this.$refs.fieldModal.open();
    },
  },
};
</script>
