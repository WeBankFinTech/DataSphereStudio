<template>
  <Modal
    v-model="show"
    width="700"
    :fullscreen="modal.isFullScreen"
    class="table-row">
    <div
      slot="header">
      <div class="table-row-header">
        <span>{{$t('message.common.tableRow.detail')}}</span>
        <Input
          size="small"
          class="table-row-input"
          v-model="searchText"
          :placeholder="$t('message.common.tableRow.search')"></Input>
        <span @click="fullScreenModal" class="full-btn">
          <Icon :type="modal.isFullScreen?'md-contract':'md-expand'" />
          {{modal.isFullScreen?$t('message.scripts.cancelfullscreen'):$t('message.scripts.fullscreen')}}
        </span>
      </div>
    </div>
    <div :class="{'noselectable': baseinfo.resCopyEnable === false}">
      <wb-table
        border
        highlight-row
        outer-sort
        :tableHeight="modal.isFullScreen ? 790:400"
        :columns="columns"
        :tableList="filterRow"
        :isOffset="false"
        class="table-row-table">
      </wb-table>
    </div>
    <div slot="footer">
    </div>
  </Modal>
</template>
<script>
import { debounce } from 'lodash';
import WbTable from '@dataspherestudio/shared/components/table';
export default {
  props: {
    row: {
      type: Array,
      default: () => [],
    },
    baseinfo: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WbTable,
  },
  data() {
    return {
      show: false,
      modal: {
        isFullScreen: false,
      },
      columns: [{
        type: 'index',
        title: '#',
        align: 'center',
      }, {
        title: this.$t('message.common.tableRow.columnName'),
        key: 'columnName',
      }, {
        title: this.$t('message.common.tableRow.value'),
        key: 'value',
      }, {
        title: this.$t('message.common.tableRow.dataType'),
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
    fullScreenModal() {
      this.modal.isFullScreen = !this.modal.isFullScreen
    },
    open() {
      this.show = true;
      this.format();
    },
    format() {
      this.formattedRow = [];
      this.filterRow = this.formattedRow = this.row;
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
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .table-row {
    .table-row-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-right: 26px;
      padding-left: 6px;
      .table-row-input {
        width: 200px;
        float: right;
      }
    .full-btn {
      float: right;
      margin-right: 30px;
      padding-top: 5px;
      cursor: pointer;
      color: #2d8cf0
    };
    }
    .table-row-table {
      .ivu-table th {
        @include bg-color($primary-color,$dark-primary-color);
        @include font-color(#fff, #000);
      }
      .ivu-table .is-null {
        color: $error-color;
        font-style: italic;
      }
    }
    .noselectable {
      user-select: none;
    }
  }
</style>

