<template>
  <div class="field-table">
    <Input
      v-model="searchText"
      :placeholder="$t('message.tableDetails.SSZDMC')">
    <Icon
      slot="prefix"
      type="ios-search"/>
    </Input>
    <div class="field-table-header">
      <div class="field-table-item field-table-index">{{ $t('message.tableDetails.XH') }}</div>
      <div
        class="field-table-item"
        v-for="(item, index) in tableColumns"
        :key="index"
        :class="item.className">{{ item.title }}</div>
    </div>
    <virtual-list
      ref="columnTables"
      :size="46"
      :remain="searchColList.length > maxSize ? maxSize : searchColList.length"
      wtag="ul"
      class="field-table">
      <li
        v-for="(item, index) in searchColList"
        :key="index"
        class="field-table-body"
        :style="{'border-bottom': index === searchColList.length - 1 ? '1px solid #dcdee2' : 'none'}">
        <div class="field-table-item field-table-index">{{ index + 1 }}</div>
        <div
          class="field-table-item"
          :title="formatValue(item, field)"
          v-for="(field, index2) in tableColumns"
          :key="index2"
          :class="field.className">{{ formatValue(item, field) }}</div>
      </li>
    </virtual-list>
  </div>
</template>
<script>
import utils from '../utils.js';
import virtualList from '@js/component/virtualList';
export default {
  components: {
    virtualList,
  },
  props: {
    tableInfo: {
      type: Object,
    },
  },
  data() {
    return {
      searchText: '',
      searchColList: [],
      tableColumns: [
        { title: this.$t('message.tableDetails.ZDM'), key: 'name', className: 'field-table-name' },
        { title: this.$t('message.tableDetails.ZDLX'), key: 'type', className: 'field-table-type' },
        { title: this.$t('message.tableDetails.BM'), key: 'alias', className: 'field-table-alias' },
        { title: this.$t('message.tableDetails.SFZJ'), key: 'primary', type: 'boolean', className: 'field-table-primary' },
        { title: this.$t('message.tableDetails.SFFQ'), key: 'partitionField', type: 'boolean', className: 'field-table-part' },
        { title: this.$t('message.tableDetails.ZDGZ'), key: 'rule', className: 'field-table-rule' },
        { title: this.$t('message.tableDetails.MS'), key: 'comment', className: 'field-table-comment' },
      ],
    };
  },
  computed: {
    maxSize() {
      return Math.floor((window.innerHeight - 242) / 46) - 1;
    },
  },
  watch: {
    searchText() {
      const reg = /^[\w]*$/;
      if (this.searchText && reg.test(this.searchText)) {
        this.searchColList = [];
        const regexp = new RegExp(`.*${this.searchText}.*`);
        const tmpList = this.tableInfo.fieldsList;
        tmpList.forEach((o) => {
          if (regexp.test(o.name)) {
            this.searchColList.push(o);
          }
        });
      } else {
        this.searchColList = this.tableInfo.fieldsList;
      }
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.searchColList = this.tableInfo.fieldsList;
    },
    formatValue(item, field) {
      return utils.formatValue(item, field);
    },
  },
};
</script>
