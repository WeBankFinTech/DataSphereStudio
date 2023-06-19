<template>
  <div>
    <Table :columns="columnData" :data="tableData" :height="200"></Table>
    <div class="version-page-bar">
      <Page
        ref="page"
        :total="page.totalSize"
        :page-size-opts="page.sizeOpts"
        :page-size="page.pageSize"
        :current="page.pageNow"
        class-name="page"
        size="small"
        show-total
        show-sizer
        @on-change="change"
        @on-page-size-change="changeSize" />
    </div>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import mixin from '@dataspherestudio/shared/common/service/mixin';
export default {
  props: {
    orchestratorId: {
      type: [Number, String],
      default: null
    },
    orchestratorVersionId: {
      type: [Number, String],
      default: null
    }
  },
  mixins: [mixin],
  data() {
    return {
      columnData: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t('message.ext.opensource.status'),
          key: 'status'
        },
        {
          title: this.$t('message.ext.opensource.opreator'),
          key: 'userName'
        },
        {
          title: this.$t('message.ext.opensource.copytime'),
          key: 'startTime'
        },
        {
          title: this.$t('message.ext.opensource.errmsg'),
          key: 'exceptionInfo',
          width: '180',
          align: 'center',
          render: (h, scope) => {
            return h(
              "span",
              {
                style: {
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis'
                },
                attrs: {
                  title: scope.row.exceptionInfo
                }
              }, scope.row.exceptionInfo
            );
          }
        }
      ],
      copyHistory: [],
      page: {
        totalSize: 0,
        sizeOpts: [4, 15, 30, 45],
        pageSize: 4,
        pageNow: 1
      }
    }
  },
  computed: {
    tableData() {
      return this.copyHistory.slice((this.page.pageNow -1 ) * this.page.pageSize, this.page.pageNow * this.page.pageSize)
    }
  },
  methods: {
    getHistoryData() {
      return api.fetch(`${this.$API_PATH.ORCHESTRATOR_PATH}listOrchestratorCopyHistory`, {
        orchestratorId: this.orchestratorId,
      },'get').then((res) => {
        (res.copyJobHistory || []).forEach(it => {
          if(it) {
            it.status = it.status ? 'Successed' : 'Failed'
          }
        })
        this.copyHistory = res.copyJobHistory || []
        this.page.totalSize = this.copyHistory.length
      }).catch(() => {
        this.loading = false;
      });
    },
    // 切换分页
    change(val) {
      this.page.pageNow = val;
    },
    // 页容量变化
    changeSize(val) {
      this.page.pageSize = val;
      this.page.pageNow = 1;
    },
  },
  mounted() {
    this.getHistoryData()
  }
}
</script>

<style lang="scss" scoped>
  .version-page-bar {
    text-align: center;
    height: 30px;
    padding-top: 4px;
  }
</style>
