<template>
  <div class="create-table-second-step">
    <Spin
      v-if="loading"
      size="large"
      fix/>
    <div class="data-source">
      <div class="data-source-item-wrap">
        <span class="data-source-title">{{$t('message.createTable.SJSXLY')}}</span>
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
        <span class="data-source-title">{{$t('message.createTable.DRFS')}}</span>
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
      @get-tables="getTables"></import-table>
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
        label: this.$t('message.createTable.XJ'),
      }, {
        value: 'import',
        label: this.$t('message.createTable.DR'),
      }],
      importTypeList: [{
        value: 'hive',
        label: 'hive',
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
      this.attrInfo.target.fieldList = [];
    },
  },
};
</script>
