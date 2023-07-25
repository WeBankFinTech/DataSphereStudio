<template>
  <div class="create-table-second-step">
    <Spin
      v-if="loading"
      size="large"
      fix/>
    <div class="data-source">
      <div class="data-source-item-wrap">
        <span class="data-source-title">{{$t('message.scripts.createTable.SJSXLY')}}</span>
        <Select
          v-model="attrInfo.source.source"
          class="data-source-type">
          <Option
            v-for="(item) in sourceTypeList"
            :key="item.value"
            :value="item.value">{{ item.label }}</Option>
        </Select>
      </div>
      <div
        style="margin-left:20px;"
        class="data-source-item-wrap"
        v-show="attrInfo.source.source === 'import'">
        <span class="data-source-title">{{$t('message.scripts.createTable.DRFS')}}</span>
        <Select
          v-model="attrInfo.source.importType"
          class="data-source-type"
          @on-change="onImportTypeChange">
          <Option
            v-for="(item) in importTypeList"
            :key="item.value"
            :value="item.value">{{ item.label }}</Option>
        </Select>
      </div>
    </div>
    <create-table
      ref="createTable"
      v-if="attrInfo.source.source === 'new'"
      :target="attrInfo.target.new"
      :partition-table="attrInfo.model.partitionTable"></create-table>
    <import-table
      ref="importTable"
      v-if="attrInfo.source.source === 'import' && dbList.length"
      :source="attrInfo.source"
      :target="attrInfo.target"
      :db-list="dbList"
      :partition-table="attrInfo.model.partitionTable"
      @get-fields="getFields"
      @get-tables="getTables"
      @isPathLeaf="csvOrXlsxPathChange"></import-table>
  </div>
</template>
<script>
import createTable from './createTable.vue';
import importTable from './importTable.vue';
export default {
  components: {
    createTable,
    importTable,
  },
  props: {
    dbList: {
      type: Array,
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    attrInfo: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      sourceTypeList: [{
        value: 'new',
        label: this.$t('message.scripts.createTable.XJ'),
      }, {
        value: 'import',
        label: this.$t('message.scripts.createTable.DR'),
      }],
      importTypeList: [{
        value: 'hive',
        label: 'hive',
      },
      {
        value: 'csv',
        label: 'csv',
      }, {
        value: 'xlsx',
        label: 'xlsx',
      }],
    };
  },
  methods: {
    getTables(val) {
      this.$emit('get-tables', val);
    },
    getFields(data) {
      this.$emit('get-fields', data);
    },
    onImportTypeChange() {
      // 导入方式改变时清空字段列表
      this.attrInfo.target.importFieldsData.fields = [];
      this.attrInfo.target.fieldList = [];
      this.attrInfo.source.hive.tbName = '';
    },
    csvOrXlsxPathChange(arg) {
      // 当选择的路径不是csv和xlsx时，清空字段
      if (!arg[0]) {
        this.attrInfo.target.importFieldsData.fields = [];
        this.attrInfo.target.fieldList = [];
      }
    }
  },
};
</script>
<style lang="scss" scoped>
.create-table-second-step {
    .data-source {
        display: block;
        border-radius: 4px;
        font-size: 14px;
        position: relative;
        -webkit-transition: all .2s ease-in-out;
        transition: all .2s ease-in-out;
        padding: 14px 16px;
        line-height: 1;
        .data-source-item-wrap {
            display: inline-block;
            .data-source-title {
                display: inline-block;
                height: 20px;
                line-height: 20px;
                font-size: 14px;
                font-weight: 700;
            }
            .data-source-type {
                margin-left: 20px;
                width: 200px;
            }
        }
    }
}
</style>


