<template>
  <Modal
    v-model="versionDetail"
    :title="$t('message.workflow.versionDetail')"
    :footer-hide="true"
    width="810"
  >
    <div class="version-content">
      <Table :columns="columns" :data="versionPageData" max-height="600" border="" :loading="loading">
        <template slot-scope="{row, index}" slot="action">
          <div class="buttom-box">
            <Button
              type="primary"
              size="small"
              :disabled="row.flowEditLockExist"
              @click="viewVersion(row, index)">
              <Icon v-if="row.flowEditLockExist" type="ios-lock" />
              {{ row.flowEditLockExist ? $t("message.workflow.workflowItem.lock") : $t('message.workflow.open')}}
            </Button>
            <Button
              v-if="(index !== 0 || versionPage.pageNow !== 1) && $route.name === 'Workflow'"
              type="primary"
              size="small"
              :disabled="row.flowEditLockExist"
              @click="rollback(row, index)">
              {{ $t("message.workflow.workflowItem.rollback") }}
            </Button>
          </div>
        </template>
      </Table>
      <div class="version-page-bar">
        <Page
          ref="page"
          :total="versionPage.totalSize"
          :page-size-opts="versionPage.sizeOpts"
          :page-size="versionPage.pageSize"
          :current="versionPage.pageNow"
          class-name="page"
          size="small"
          show-total
          show-sizer
          @on-change="change"
          @on-page-size-change="changeSize" />
      </div>
    </div>
  </Modal>
</template>
<script>
import moment from "moment";
import versionPageMixin from '../versionPageMixin.js';

export default {
  props: {
    versionDetailShow: {
      type: Boolean,
      default: false
    },
    versionData: {
      type: Array,
      default: () => []
    }
  },
  mixins: [versionPageMixin],
  data() {
    return {
      versionDetail: false,
      loading: false
    };
  },
  watch: {
    versionDetailShow(val) {
      this.versionDetail = val;
    },
    versionDetail(val) {
      this.$emit("versionDetailShow", val);
    },
    versionData(val) {
      this.loading = false
      this.versionPage.totalSize = val ? val.length : 0;
    }
  },
  computed: {
    columns() {
      return [
        {
          title: this.$t("message.common.projectDetail.version"),
          key: "version",
          width: 130,
          align: "center",
          render: (h, scope) => {
            return h(
              "span",
              {},
              scope.index === 0 && this.versionPage.pageNow === 1 ? this.$t("message.common.projectDetail.editing") : scope.row.version
            );
          }
        },
        {
          title: this.$t("message.common.projectDetail.updator"),
          key: "updater",
          width: 130,
          align: "center"
        },
        {
          title: this.$t("message.common.projectDetail.comment"),
          key: "comment",
          width: 200,
          align: "center"
        },
        {
          title: this.$t("message.common.projectDetail.uptateTime"),
          key: "updateTime",
          width: 186,
          align: "center",
          render: (h, scope) => {
            return h(
              "span",
              {},
              moment(scope.row.updateTime).format("YYYY-MM-DD HH:mm:ss")
            );
          }
        },
        {
          title: this.$t("message.common.projectDetail.action"),
          slot: "action",
          align: "center",
          width: 130
        }
      ];
    }
  },
  created() {},
  methods: {
    viewVersion(row, index) {
      this.versionDetail = false;
      this.$emit("goto", row, index);
    },
    rollback(row) {
      this.$Modal.confirm({
        title: this.$t('message.common.projectDetail.rollBack'),
        content: this.$t('message.common.projectDetail.newVersion', { version: row.version }),
        onOk: () => {
          this.loading = true;
          this.$emit('rollback', row);
        },
        onCancel: () => {
        },
      });
    }
  }
};
</script>
<style scoped lang='scss'>
.buttom-box {
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.version-page-bar {
  text-align: center;
  height: 30px;
  padding-top: 4px;
}
</style>
