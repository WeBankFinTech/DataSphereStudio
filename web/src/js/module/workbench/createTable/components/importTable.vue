<template>
  <div class="import-table">
    <Card>
      <p slot="title">
        {{$t('message.createTable.LYSX')}}
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
        @get-fields="getFields"></csv>
    </Card>
    <Card>
      <p slot="title">
        {{$t('message.createTable.MBZD')}}
      </p>
      <div
        class="import-table-fields">
        <fields-table
          :fields="target.fieldList"
          :table-columns="source.importType === 'hive' ? hiveFieldsColumns : csvOrXlsxfieldsColumns"
          :max-size="5"
          :can-be-delete="source.importType === 'hive'"
          :special-field-key="specialFieldKey"
          @on-delete="onDelete"
          @on-tag-click="onTagClick"></fields-table>
      </div>
    </Card>
    <Card v-if="partitionTable">
      <p slot="title">
        {{$t('message.createTable.partitionAttr')}}
      </p>
      <div
        class="import-table-fields">
        <fields-table
          :fields="target.partitionList"
          :table-columns="partTableColumns"
          :max-size="1"></fields-table>
      </div>
    </Card>
    <fieldsModal
      ref="fieldModal"
      :field-list="target.fieldList"
      :actived-field-item="activedFieldItem"
    ></fieldsModal>
  </div>
</template>
<script>
import config from '../config.js';
import hive from './importTable_hive.vue';
import csv from './importTable_csv.vue';
import fieldsTable from '@js/component/table/fieldTable/fieldsTable.vue';
import fieldsModal from './fieldModal.vue';
import api from '@/js/service/api';
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
      const url = `/filesystem/formate?path=${option.exportPath}&encoding=${encoding}&fieldDelimiter=${fieldDelimiter}&hasHeader=${option.isHasHeader}&escapeQuotes=${escapeQuotes}&quote=${quote}`;
      api.fetch(url, {}, {
        method: 'get',
        timeout: '600000',
      }).then((rst) => {
        if (!rst.formate.columnName.length) {
          return this.target.fieldList = [];
        }
        this.target.fieldList = rst.formate.columnName.map((field) => {
          return {
            name: field,
            type: '',
            alias: '',
            sourceFields: '',
            rule: '',
            partitionField: 0,
            primary: 0,
            comment: '',
            length: '',
            express: '',
          };
        });
        rst.formate.columnType.forEach((type, index) => {
          this.target.fieldList[index].type = type;
        });
        this.target.fieldList = this.target.fieldList.slice();
        this.target.sheetName = rst.formate.sheetName;
      }).catch(() => {
      });
    },
    onDelete(item, index) {
      this.target.fieldList.splice(index, 1);
    },
    onTagClick(item) {
      this.activedFieldItem = item;
      this.$refs.fieldModal.open();
    },
  },
};
</script>
