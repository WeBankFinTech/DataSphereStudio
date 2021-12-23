<template>
  <div class="field-list">
    <div style="margin-bottom: 10px;">
      <Input
        v-model="searchText"
        :placeholder="$t('message.scripts.tableDetails.SSZDMC')">
      <Icon
        slot="prefix"
        type="ios-search"/>
      </Input>
    </div>
    <Table border :columns="tableColumns" :data="searchColList"></Table>
  </div>
</template>
<script>
import utils from '../utils.js';
export default {
  props: {
    table: {
      type: Array,
    },
    // tableInfo: {
    //   type: Object,
    // },
  },
  data() {
    return {
      searchText: '',
      searchColList: [],
      tableColumns: [
        { title: this.$t('message.scripts.tableDetails.ZDM'), key: 'name', className: 'field-table-name' },
        { title: this.$t('message.scripts.tableDetails.ZDLX'), key: 'type', className: 'field-table-type' },
        { title: this.$t('message.scripts.tableDetails.BM'), key: 'alias', className: 'field-table-alias' },
        { title: this.$t('message.scripts.tableDetails.SFZJ'), key: 'primary', type: 'boolean', className: 'field-table-primary' },
        { title: this.$t('message.scripts.tableDetails.SFFQ'), key: 'partitionField', type: 'boolean', className: 'field-table-part' },
        { title: this.$t('message.scripts.tableDetails.ZDGZ'), key: 'rule', className: 'field-table-rule' },
        { title: this.$t('message.scripts.tableDetails.MS'), key: 'comment', className: 'field-table-comment' },
      ],
    };
  },
  computed: {
    maxSize() {
      return Math.floor((window.innerHeight - 242) / 46) - 1;
    },
  },
  watch: {
    searchText(val) {
      const reg = /^[\w]*$/;
      if (val && reg.test(val)) {
        this.searchColList = [];
        const regexp = new RegExp(`.*${val}.*`, 'i');
        const tmpList = this.table;
        tmpList.forEach((o) => {
          if (regexp.test(o.name)) {
            this.searchColList.push(o);
          }
        });
      } else {
        this.searchColList = this.table;
      }
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.searchColList = this.table;
    },
    formatValue(item, field) {
      return utils.formatValue(item, field);
    },
  },
};
</script>
