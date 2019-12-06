<template>
  <Modal
    v-model="show"
    width="700"
    class="table-row">
    <div
      slot="header">
      <div class="table-row-header">
        <span>{{$t('message.container.tableRow.detail')}}</span>
        <Input
          size="small"
          class="table-row-input"
          v-model="searchText"
          :placeholder="$t('message.container.tableRow.search')"></Input>
      </div>
    </div>
    <div>
      <Table
        class="table-row-table"
        stripe
        :columns="columns"
        :data="filterRow"
        :max-height="500"></Table>
    </div>
    <div slot="footer">
    </div>
  </Modal>
</template>
<script>
import { debounce } from 'lodash';
export default {
  props: {
    row: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      show: false,
      columns: [{
        type: 'index',
        width: 60,
        align: 'center',
      }, {
        title: this.$t('message.container.tableRow.columnName'),
        key: 'columnName',
      }, {
        title: this.$t('message.container.tableRow.value'),
        key: 'value',
      }, {
        title: this.$t('message.container.tableRow.dataType'),
        key: 'dataType',
      }],
      formattedRow: [],
      filterRow: [],
      searchText: '',
    };
  },
  watch: {
    searchText(val) {
      if (val) {
        this.filter(this);
      } else {
        this.filterRow = this.formattedRow;
      }
    },
  },
  methods: {
    open() {
      this.show = true;
      this.format();
    },
    format() {
      this.formattedRow = [];
      const keys = Object.keys(this.row);
      keys.forEach((key) => {
        if (key !== 'cellClassName') {
          const columnName = key.slice(key.indexOf(':') + 1, key.indexOf(','));
          const dataType = key.slice(key.match('dataType').index + 9, key.lastIndexOf(','));
          const value = this.row[key];
          const newItem = {
            cellClassName: {},
            columnName,
            value,
            dataType,
          };
          if (columnName === 'NULL') {
            newItem.cellClassName.columnName = 'is-null';
          } else if (dataType === 'NULL') {
            newItem.cellClassName.dataType = 'is-null';
          } else if (value === 'NULL') {
            newItem.cellClassName.value = 'is-null';
          }
          this.formattedRow.push(newItem);
        }
      });
      this.filterRow = this.formattedRow;
    },
    filter: debounce((that) => {
      const regexp = new RegExp(`.*${that.searchText}.*`);
      that.filterRow = that.formattedRow.filter((item) => {
        return regexp.test(item.columnName) || regexp.test(item.value) || regexp.test(item.dataType);
      });
    }, 500),
  },
};
</script>
