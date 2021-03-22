<template>
  <div class="field-list">
    <Input
      v-model="searchText"
      :placeholder="$t('message.scripts.tableDetails.SSZDMC')">
    <Icon
      slot="prefix"
      type="ios-search"/>
    </Input>
    <div class="field-list-header">
      <div class="field-list-item field-list-index">序号</div>
      <div
        class="field-list-item"
        v-for="(item, index) in tableColumns"
        :key="index"
        :class="item.className">{{ item.title }}</div>
    </div>
    <virtual-list
      ref="columnTables"
      :size="46"
      :remain="searchColList.length > maxSize ? maxSize : searchColList.length"
      wtag="ul"
      class="field-list">
      <li
        v-for="(item, index) in searchColList"
        :key="index"
        class="field-list-body"
        :style="{'border-bottom': index === searchColList.length - 1 ? '1px solid #dcdee2' : 'none'}">
        <div class="field-list-item field-list-index">{{ index + 1 }}</div>
        <div
          class="field-list-item"
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
import virtualList from '@/components/virtualList';
export default {
  components: {
    virtualList,
  },
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
