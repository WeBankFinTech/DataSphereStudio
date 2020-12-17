<template>
  <div class="data-table-content" :class="{'disabele-animate': disabeleAnimate}">
    <div :class="{ 'data-table-main': aiTableScroll }">
      <Table :height="height" class="data-table" :loading="loading" border stripe ref="selection" :row-class-name="rowClassName" :highlight-row="highlightRow" :disabled-hover= "disabledHover" :columns="titles" :data="data"  @on-selection-change="selectionChange" @on-row-click="rowClick" @on-select="onSelect" @on-select-all="onSelectAll" @on-select-cancel="onSelectCancel" :show-header="showHeader" @on-sort-change="onSortChange" @on-expand="onExpand" v-if="titles.length"></Table>
    </div>
    <DataBasePage v-if="!aiTablePageFlag" :totalRecords="totalRecords" :pageSize="pageSize" :pageNo="pageNo" :changePage="changePage" :showTotal="showTotal" :show-elevator="showElevator" :show-sizer="showSizer" :page-size-opts="pageSizeOpts" :on-page-size-change="onPageSizeChange"></DataBasePage>
  </div>
</template>
<script>
import page from '../../page/basePage/'

export default {
  props: {
    loading: {
      type: Boolean,
      default () {
        return false
      }
    },
    aiTableScroll: {
      // 是否自身/100%
      type: Boolean,
      default () {
        return false
      }
    },
    aiTablePageFlag: {
      // 显示分页
      type: Boolean,
      default () {
        return false
      }
    },
    highlightRow: {
      //  是否高亮
      type: Boolean,
      default () {
        return false
      }
    },
    disabledHover: {
      //  禁用鼠标悬停时的高亮
      type: Boolean,
      default () {
        return false
      }
    },
    showHeader: {
      type: Boolean,
      default () {
        return true
      }
    },
    titles: {
      type: Array,
      default () {
        return []
      }
    },
    height: {},
    data: {
      type: Array,
      default () {
        return []
      }
    },
    // 通过属性 row-class-name 可以给某一行指定一个样式名称
    rowClassName: {
      type: Function,
      default: () => () => {}
    },
    selectionChange: {
      type: Function,
      default: () => () => {}
    },
    // 在多选模式下有效，选中某一项时触发
    onSelect: {
      type: Function,
      default: () => () => {}
    },
    // 在多选模式下有效，选中某一项时触发
    onExpand: {
      type: Function,
      default: () => () => {}
    },
    // 在多选模式下有效，点击全选时触发
    onSelectAll: {
      type: Function,
      default: () => () => {}
    },
    // 在多选模式下有效，取消选中某一项时触发
    onSelectCancel: {
      type: Function,
      default: () => () => {}
    },
    rowClick: {
      type: Function,
      default: () => () => {}
    },
    totalRecords: {
      type: Number,
      default () {
        return 0
      }
    },
    pageSize: {
      type: Number,
      default () {
        return 30
      }
    },
    pageNo: {
      type: Number,
      default () {
        return 1
      }
    },
    changePage: {
      type: Function,
      default: () => () => {}
    },
    onPageSizeChange: {
      type: Function,
      default: () => () => {}
    },
    showTotal: {
      type: Boolean,
      default () {
        return true
      }
    },
    disabeleAnimate: {
      type: Boolean,
      default: false
    },
    // 显示电梯
    showElevator: {
      type: Boolean,
      default () {
        return false
      }
    },
    // 显示分页，用来改变page-size
    showSizer: {
      type: Boolean,
      default () {
        return false
      }
    },
    // 每页条数切换的配置
    pageSizeOpts: {
      type: Array,
      default () {
        return [5, 30, 50]
      }
    }
  },
  components: {
    'DataBasePage': page
  },
  methods: {
    onSortChange (val) {
      this.$emit('on-sort-change', val)
    }
  }
}
</script>
<style lang="less">
.data-table{
  table{
    border: 0 none;
  }
  .ivu-table-fixed-body {
    tr {
      button {
        font-size: 13px;
        padding: 0 5px;
      }
    }
  }
  .ivu-table{
    color: #333333 !important;
    font-size: 14px;
    th {
      background-color: #e4e4e4;
    }
    .ivu-table-body {
      font-size: 13px;
    }

    th,  td{
      height:0 !important;
      padding: 10px;
      /*background: #ffffff;*/
      font-size: 13px;
    }
    th {
      background: #e4e4e4;
    }
    td{
      // border: 1px solid #e9eaec;
    }
    .ivu-table-cell-with-expand {
      height: 12px;
      line-height: 12px;
    }
    tr {
      &>td:last-child {
        button {
          font-size: 13px;
          padding: 0 5px;
        }
      }
    }
  }
  .ivu-table-cell{
    padding: 0 !important;
  }

  .ivu-checkbox-wrapper{
    margin-bottom: 0 !important;
    margin-right: 0 !important;
  }
  .ivu-radio-wrapper{
    margin-bottom: 0;
    margin-right: 0;
    text-align: center;
    .ivu-radio {
      margin-right: 0;
    }
  }
  a{
    color: #268066
  }
  .ivu-table-tip {
    overflow-x: hidden !important;
    overflow-y: hidden !important;
  }
}
.disabele-animate {
  .ivu-radio-inner {
    transition: none;
  }
  .ivu-radio-inner:after {
    transition: none;
  }
  .ivu-radio-checked {
    .ivu-radio-inner:after {
      transition: none;
    }
  }
  .ivu-checkbox-inner {
    transition: none;
  }
  .ivu-checkbox-inner:after {
    transition: none;
  }
  .ivu-checkbox-checked {
    .ivu-checkbox-inner:after {
      transition: none;
    }
  }
}
</style>
<style lang="less">
  .data-table-content {
    margin-bottom: 20px;
    .data-table-main{
      width: 100%;
      .data-table{
        .ivu-table-header{
          table {
            min-width: 1200px !important
          }
        }
        .ivu-table-body{
          overflow: auto;
          table {
            min-width: 1200px !important;
          }
        }
      }
    }
  }
</style>
