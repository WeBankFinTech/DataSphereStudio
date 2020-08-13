<template>
  <div class="data-table-page" v-if="totalRecords">
    <Page :total="totalRecords" :page-size="pageSize" :current="pageNo" @on-change="changePage" :show-total="showTotal" :show-elevator="showElevator" :show-sizer="showSizer" :page-size-opts="pageSizeOpts" @on-page-size-change="onPageSizeChange">
      <slot>{{$t('message.page.show')}} {{pageNo == 1 ? 1 : (pageNo - 1)*pageSize + 1}}{{$t('message.page.to')}} {{pageNo*pageSize > totalRecords ? totalRecords: pageNo*pageSize}} ，{{$t('message.page.total')}} {{totalRecords}} {{$t('message.page.record')}}</slot>
    </Page>
  </div>
</template>
<script>
export default {
  props: {
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
        return false
      }
    },
    showElevator: {
      type: Boolean,
      default () {
        return false
      }
    },
    showSizer: {
      type: Boolean,
      default () {
        return false
      }
    },
    pageSizeOpts: {
      type: Array,
      default () {
        return [5, 30, 50]
      }
    }
  }
}
</script>
<style lang="less" scoped>
  .data-table-page {
    padding-top:15px;
    text-align:right;
  }
</style>
