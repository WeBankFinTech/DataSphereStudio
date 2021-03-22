<template>
  <Modal
    v-model="versionDetail"
    :title="$t('message.workflow.versionDetail')"
    :footer-hide="true"
    width="810"
  >
    <div class="version-content">
      <Table :columns="columns" :data="versionPageData" max-height="600" border="">
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
import api from "@/common/service/api";
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
      this.versionPage.totalSize = val.length;
    }
  },
  computed: {
    columns() {
      return [
        {
          title: this.$t("message.workflow.projectDetail.version"),
          key: "version",
          width: 130,
          align: "center"
        },
        {
          title: this.$t("message.workflow.projectDetail.updator"),
          key: "updater",
          width: 130,
          align: "center"
        },
        {
          title: this.$t("message.workflow.projectDetail.comment"),
          key: "comment",
          width: 200,
          align: "center"
        },
        {
          title: this.$t("message.workflow.projectDetail.uptateTime"),
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
          title: this.$t("message.workflow.projectDetail.action"),
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
    checkout(row, index) {
      this.$Modal.confirm({
        title:
          index == 0
            ? this.$t("message.workflow.projectDetail.addVersion")
            : this.$t("message.workflow.projectDetail.rollBack"),
        content: this.$t("message.workflow.projectDetail.addVersion", {
          version: row.version
        }),
        onOk: () => {
          api
            .fetch(
              "/dss/copyVersionAndFlow",
              { workflowVersionID: row.id },
              "post"
            )
            .then(() => {
              this.versionDetail = false;
            })
            .catch(() => {
            });
        },
        onCancel: () => {}
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
