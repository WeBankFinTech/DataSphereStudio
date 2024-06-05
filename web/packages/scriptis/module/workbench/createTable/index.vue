<template>
  <div class="create-table-view">
    <first-step
      ref="firstStep"
      :db-list="personalDbList"
      :attr-info="attrInfo"
      @close-tab="closeTab"
      @next-step="nextStep"
      @get-tables="getTables"
      :loading="loading"
      v-show="current === 1"/>
    <second-step
      v-show="current === 2"
      ref="secondStep"
      :db-list="dbList"
      :loading="loading"
      :attr-info="attrInfo"
      @get-fields="getFields"
      @get-tables="getTables"/>
    <div
      class="create-table-view-button-group"
      v-show="current===2">
      <Button
        @click="prev">{{$t('message.scripts.createTable.FHSYB')}}</Button>
      <Button
        type="success"
        @click="generatingStatements"
        :disabled="!canBeCreate">{{$t('message.scripts.createTable.SCDDL')}}</Button>
      <Button
        type="primary"
        @click="createTable"
        :disabled="!canBeCreate">{{$t('message.scripts.createTable.JB')}}</Button>
    </div>
    <Modal
      v-model="modalShow"
      :title="$t('message.scripts.createTable.DDLYJ')"
      :ok-text="$t('message.scripts.createTable.FZZT')"
      @on-ok="executeCopy"
      @on-cancel="cancel">
      <p style="word-break:break-all;max-height:400px;overflow-y:auto;">{{ statements }}</p>
    </Modal>
  </div>
</template>
<script>
import { pick } from 'lodash';
import moment from 'moment';
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import firstStep from './components/firstStep.vue';
import secondStep from './components/secondStep.vue';
export default {
  components: {
    firstStep,
    secondStep,
  },
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  data() {
    const currentDate = this.getCurrentDate();
    return {
      dbList: [],
      personalDbList: [],
      loading: false,
      hiveComponent: null,
      current: 1,
      attrInfo: {
        basic: {
          database: '',
          name: '',
          alias: '',
          usage: '',
          productName: '',
          projectName: '',
          comment: '',
        },
        model: {
          partitionTable: 1,
          lifecycle: 0,
          modelLevel: 0,
          useWay: 0,
          externalUse: 0,
        },
        source: {
          source: 'new',
          importType: 'hive',
          hive: {
            dbName: '',
            tbName: '',
          },
          table: {
            exportPath: '',
            type: 'share',
            separator: ',',
            chartset: 'utf-8',
            quote: '',
            isHasHeader: false,
            sheet: [],
          },
        },
        target: {
          new: {
            newFieldsData: {
              fields: [{
                name: '',
                type: 'string',
                alias: '',
                length: null,
                rule: '',
                primary: 0,
                comment: '',
                partitionField: 0,
              }],
            },
            newPartitionsData: {
              fields: [{
                name: 'ds',
                type: 'string',
                alias: '',
                length: 8,
                comment: '',
                partitionField: 1,
                primary: 0,
              }],
            },
          },
          importFieldsData: {
            fields: [],
          },
          importPartitionsData: {
            fields: [{
              name: 'ds',
              type: 'string',
              alias: '',
              length: 8,
              comment: '',
              partitionsValue: currentDate,
              partitionField: 1,
              primary: 0,
            }],
          },
          sheetName: [],
        },
      },
      statements: null,
      modalShow: false,
    };
  },
  computed: {
    canBeCreate() {
      const info = this.attrInfo;
      return (info.source.source === 'new' && info.target.new.newFieldsData.fields.length) ||
        (info.source.source === 'import' &&
        (info.source.importType === 'hive' && info.basic.name && info.target.importFieldsData.fields.length) ||
        (info.source.importType !== 'hive' && info.source.table.exportPath && info.target.importFieldsData.fields.length));
    },
  },
  mounted() {
    this.getIndDbList();
  },
  beforeDestroy() {
    this.reset();
  },
  methods: {
    getIndDbList() {
      this.dbList = this.work && this.work.data;
      this.dispatch('HiveSidebar:getAllowMap', (map) => {
        this.personalDbList = [];
        if (!map.length) {
          this.personalDbList = this.work.data;
        }
        this.work && this.work.data.forEach((item) => {
          for (let i = 0; i < map.length; i++) {
            if (item.name.match(map[i])) {
              const has = this.personalDbList.find(it => it.name === item.name)
              if (!has) {
                this.personalDbList.push(item);
              }
            }
          }
        });
        if (this.personalDbList.length && !this.personalDbList[0].children.length) {
          this.getTables(this.personalDbList[0].name);
        }
      })
    },
    extendHive() {
      if (!this.hiveComponent) {
        this.dispatch('HiveSidebar:showHive', {}, (f) => {
          this.hiveComponent = f;
        });
      }
    },
    getTables(name) {
      this.loading = true;
      let index = this.dbList.findIndex((item) => item.name === name);
      if( index < 0 ) index = 0
      this.extendHive();
      const methodName = 'HiveSidebar:getTables';
      this.hiveComponent[methodName]({
        item: this.dbList[index],
      }, (tableList) => {
        this.dbList[index].children = tableList;
        this.loading = false;
      });
    },
    getFields(data) {
      this.loading = true;
      this.extendHive();
      const methodName = 'asyncGetTableFieldsInfo';
      this.hiveComponent[methodName]({
        database: data.dbName,
        tableName: data.name,
      }, (fields) => {
        this.loading = false;
        if (!fields) {
          return;
        }
        this.attrInfo.target.importFieldsData.fields = fields.map((item) => {
          return {
            alias: item.alias,
            comment: item.comment,
            express: item.express,
            length: item.length,
            name: item.name,
            partitionField: item.partitionField ? 1 : 0,
            primary: item.primary ? 1 : 0,
            rule: item.rule,
            type: item.type,
            sourceFields: [item.name],
            sourceFieldsOpt: [{
              label: item.name,
              value: item.name,
            }],
          };
        });
      });
    },
    closeTab() {
      this.dispatch('Workbench:removeWork', this.work);
    },
    prev() {
      this.current = 1;
    },
    nextStep() {
      this.current = 2;
    },
    getParams() {
      const basic = this.attrInfo.basic;
      const model = this.attrInfo.model;
      const source = this.attrInfo.source;
      const target = this.attrInfo.target;

      const tableBaseInfo = {
        base: {
          database: basic.database,
          name: basic.name,
          alias: basic.alias,
          partitionTable: model.partitionTable,
          comment: basic.comment,
        },
        model: {
          lifecycle: model.lifecycle,
          modelLevel: model.modelLevel,
          useWay: model.useWay,
          externalUse: model.externalUse,
        },
        application: {
          productName: basic.productName,
          projectName: basic.projectName,
          usage: basic.usage,
        },
      };

      const isXls = source.importType === 'xlsx';
      const isHive = source.importType === 'hive';

      // 这里是因为partitionsValue这个值只有在xlsx和csv的destination和source中用到
      const partitionsValue = target.importPartitionsData.fields[0].partitionsValue;
      // sourceFields是只有展示在前台，后台不需要
      const formattedFieldsList = target.importFieldsData.fields.map((item) => {
        return pick(item, ['alias', 'comment', 'express', 'length', 'name', 'partitionField', 'primary', 'rule', 'type']);
      });

      // 创建一个去掉partitionsValue的数组，这里是因为partitionsValue这个值只有在xlsx和csv的destination和source中用到
      const pickPartitionList = target.importPartitionsData.fields.map((item) => {
        return pick(item, ['name', 'type', 'alias', 'length', 'comment', 'partitionField', 'primary', 'partitionsValue']);
      });
      // 如果是分区表，才需要去合并数组
      const newTableInfo = model.partitionTable ? target.new.newFieldsData.fields.concat(target.new.newPartitionsData.fields) : target.new.newFieldsData.fields;
      const importTableInfo = model.partitionTable ? formattedFieldsList.concat(pickPartitionList) : formattedFieldsList;

      const tableFieldsInfo = source.source === 'new' ? newTableInfo : importTableInfo;

      // 格式化columns，用于在destination中使用
      const columns = target.importFieldsData.fields.map((item) => {
        let scale = '';
        let precision = '';
        let dateFormat = '';
        let length = item.length;
        if (item.type === 'decimal') {
          const arr = item.length.split(',');
          scale = arr[0] || '';
          precision = arr[1] || '';
          length = '';
        }
        return {
          name: item.name.trim(),
          index: item.index,
          comment: item.comment,
          type: item.type,
          length,
          scale,
          precision,
          dateFormat,
        };
      });

      let escapeQuotes = false;
      let quote = '';
      if (source.table.quote) {
        escapeQuotes = true;
        quote = source.table.quote;
      }

      // 对path去去掉前面“file://”的部分
      let path = source.table.exportPath;
      path = path.indexOf('://') === -1 ? path : path.slice(7, path.length);
      const destination = {
        database: basic.database,
        tableName: basic.name,
        importData: false,
        isPartition: model.partitionTable,
        partition: target.importPartitionsData.fields[0].name,
        partitionValue: partitionsValue,
        isOverwrite: false,
        columns: columns,
      };
      if (!isXls) {
        delete source.table.sheet
      }
      let fieldDelimiter = source.table.separator;
      if (source.table.separator === '%20') {
        fieldDelimiter = ' ';
      } else if (source.table.separator === '|') {
        fieldDelimiter = '\\|';
      }
      const sourceP = {
        path,
        pathType: source.table.type,
        hasHeader: source.table.isHasHeader,
        encoding: isXls ? '' : source.table.chartset,
        fieldDelimiter: isXls ? '' : fieldDelimiter,
        sheet: source.table.sheet &&  source.table.sheet.toString(),
        quote,
        escapeQuotes,
      };

      const importInfo = {
        importType: ['hive', 'csv', 'xlsx'].indexOf(source.importType),
        args: isHive ? {
          database: source.hive.dbName,
          table: source.hive.tbName,
        } : (!isXls ? Object.assign({}, source.table, {
          exportPath: path,
        }) : {
          exportPath: path,
          type: source.table.type,
          isHasHeader: source.table.isHasHeader,
        }),
        destination: JSON.stringify(destination),
        source: JSON.stringify(sourceP),
      };
      if (isHive) {
        delete importInfo.destination;
        delete importInfo.source;
      }
      const params = {
        tableBaseInfo,
        tableFieldsInfo,
        importInfo,
      };
      if (source.source === 'new') {
        delete params.importInfo;
      }
      return params;
    },
    generatingStatements() {
      const params = this.getParams();
      api.fetch('/datasource/displaysql', { table: params }).then((rst) => {
        this.statements = rst.sql;
        this.modalShow = true;
      });
    },
    executeCopy() {
      util.executeCopy(this.statements);
      this.$Message.success(this.$t('message.scripts.createTable.DDLYFZ'));
    },
    cancel() {
      this.modalShow = false;
    },
    getValidate() {
      return new Promise((resolve) => {
        if (this.attrInfo.source.source === 'new') {
          const tablerefs = this.$refs.secondStep.$refs.createTable
          tablerefs.$refs.fieldsForm.validate((valid) => {
            if (valid) {
              const dupNames= {}
              tablerefs.target.newFieldsData.fields.forEach(it => {
                if (it.name) {
                  dupNames[it.name] = dupNames[it.name] ? dupNames[it.name] + 1 : 1
                }
              })
              const dupfield = Object.keys(dupNames).filter(it => dupNames[it] > 1)
              if (dupfield.length) {
                this.$Message.warning(this.$t('message.scripts.createTable.dupfields', {fields: dupfield.join('<br>')}));
              } else {
                resolve();
              }
            } else {
              this.$Message.warning(this.$t('message.scripts.failedNotice'));
            }
          });
        } else {
          let fieldError = '';
          this.attrInfo.target.importFieldsData.fields.forEach((it) => {
            if (!it.name) {
              fieldError = this.$t('message.scripts.importToHive.ZDMC');
            } else if (!/^[a-zA-Z0-9_]+$/.test(it.name)) {
              fieldError = this.$t('message.scripts.fieldnamestyle');
            }
          });
          if(fieldError) {
            this.$Message.warning(fieldError);
          } else {
            resolve();
          }
        }
      });
    },
    createTable() {
      this.getValidate().then(() => {
        const tabName = `create_table_${this.attrInfo.basic.name}_${Date.now()}.scala`;
        const md5Path = util.md5(tabName);
        const code = JSON.stringify(this.getParams());
        this.dispatch('Workbench:add', {
          id: md5Path,
          filename: tabName,
          filepath: '',
          saveAs: true,
          noLoadCache: true,
          specialSetting: {
            runType: 'function.mdq',
          },
          code,
        }, () => {
          this.$nextTick(() => {
            this.dispatch('Workbench:run', {
              id: md5Path,
            }, () => {

            });
          });
        });
      });
    },
    getCurrentDate() {
      return moment.unix(moment().unix()).format('YYYYMMDD');
    },
    reset() {
      this.current = 1;
      this.attrInfo = {
        basic: {
          database: '',
          name: '',
          alias: '',
          usage: '',
          productName: '',
          projectName: '',
          comment: '',
        },
        model: {
          partitionTable: 1,
          lifecycle: 0,
          modelLevel: 0,
          useWay: 0,
          externalUse: 0,
        },
        source: {
          source: 'new',
          importType: 'hive',
          hive: {
            dbName: '',
            tbName: '',
          },
          table: {
            exportPath: '',
            type: 'share',
            separator: ',',
            chartset: 'utf-8',
            quote: '',
            isHasHeader: false,
          },
        },
        target: {
          new: {
            newFieldsData: {
              fields: [{
                name: '',
                type: 'string',
                alias: '',
                length: null,
                rule: '',
                primary: 0,
                comment: '',
                partitionField: 0,
              }],
            },
            newPartitionsData: {
              fields: [{
                name: 'ds',
                type: 'string',
                alias: '',
                length: 8,
                comment: '',
                partitionField: 1,
                primary: 0,
              }],
            },
          },
          importFieldsData: {
            fields: [],
          },
          importPartitionsData: {
            fields: [{
              name: 'ds',
              type: 'string',
              alias: '',
              length: 8,
              comment: '',
              partitionsValue: this.currentDate,
              partitionField: 1,
              primary: 0,
            }],
          },
          sheetName: [],
        },
      };
      this.statements = null;
    },
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.create-table-view {
    height: 100%;
    overflow-y: auto;
    .create-table-view-button-group {
        padding: 20px;
        text-align: right;
        button {
            margin-right: 10px;
        }
    }
}
</style>

