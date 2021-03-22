<template>
  <div class="version-content">
    <Table
      :columns="columns"
      :data="versionPageData"
      border>
      <template
        slot-scope="{row, index}"
        slot="action"
      >
        <div class="buttom-box">
          <!-- <Button
            type="error"
            size="small"
            @click="deleteVersion(row)">删除</Button> -->
          <Button
            type="primary"
            size="small"
            @click="viewVersion(row, index)">{{ $t('message.workflow.open') }}</Button>
          <Button
            type="primary"
            size="small"
            @click="checkout(row, index)">{{ index == 0 ? $t('message.workflow.newCreate') : $t('message.workflow.projectDetail.rollBack') }}</Button>
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
    <Modal
      v-model="publishDetaultShow"
      :title="$t('message.workflow.projectDetail.publishDetail')"
      width="554"
      :footer-hide="true">
      <Table
        :columns="detaultColumns"
        :data="publishData"
        border>
      </Table>
    </Modal>
  </div>
</template>
<script>
import moment from 'moment';
import api from '@/common/service/api';
import versionPageMixin from '@/apps/workflows/module/workflow/versionPageMixin.js';

export default {
  props: {
    currentProjectData: {
      type: Object,
      default: null,
    },
  },
  mixins: [versionPageMixin],
  data() {
    return {
      versionData: [],
      publishData: [],
      publishDetaultShow: false,
    };
  },
  created() {
    this.getVersionList();
  },
  watch: {
    'currentProjectData.id'(val) {
      if (val) {
        this.getVersionList();
      }
    }
  },
  computed: {
    columns() {
      return [
        {
          title: this.$t('message.workflow.projectDetail.version'),
          key: 'version',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.workflow.projectDetail.status'),
          key: 'status',
          width: 120,
          align: 'center',
          render: (h, scope) => {
            return h('span', {
              style: {
                color: scope.row.notPublish ? '#515a6e' : '#2d8cf0',
                cursor: 'pointer',
              },
              on: {
                click: () => {
                  this.publishDetault(scope.row);
                },
              },
            }, scope.row.notPublish ? this.$t('message.workflow.projectDetail.unpublish') : this.$t('message.workflow.projectDetail.published'));
          },
        },
        {
          title: this.$t('message.workflow.projectDetail.updator'),
          key: 'updator',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.workflow.projectDetail.comment'),
          key: 'comment',
          width: 186,
          align: 'center',
        },
        {
          title: this.$t('message.workflow.projectDetail.uptateTime'),
          key: 'updateTime',
          width: 160,
          align: 'center',
          render: (h, scope) => {
            return h('span', {}, moment(scope.row.updateTime).format('YYYY-MM-DD HH:mm:ss'));
          },
        },
        {
          title: this.$t('message.workflow.projectDetail.action'),
          slot: 'action',
          align: 'center',
          width: 170,
        },
      ]
    },
    detaultColumns() {
      return [
        {
          title: this.$t('message.workflow.projectDetail.creator'),
          key: 'creator',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.workflow.projectDetail.status'),
          key: 'state',
          width: 120,
          align: 'center',
          render: (h, scope) => {
            return h('span', {}, this.publishResult(scope.row.state));
          },
        },
        {
          title: this.$t('message.workflow.projectDetail.publishData'),
          key: 'comment',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.workflow.projectDetail.publishTime'),
          key: 'updatetime',
          width: 160,
          align: 'center',
          render: (h, scope) => {
            return h('span', {}, moment(scope.row.updateTime).format('YYYY-MM-DD HH:mm:ss'));
          },
        },
      ]
    }
  },
  methods: {
    // 获取工程版本列表
    getVersionList() {
      api.fetch('/dss/listAllProjectVersions', { id: this.currentProjectData.id }, 'get').then((res) => {
        this.versionData = res.versions;
        this.versionPage.totalSize = res.versions.length;
      });
    },
    viewVersion(row, index) {
      this.$emit('modelHidden', false);
      this.$router.push({ name: 'Workflow',
        query: {
          ...this.$route.query,
          projectTaxonomyID: this.currentProjectData.taxonomyID,
          projectID: row.projectID,
          projectVersionID: row.id,
          projectName: this.currentProjectData.name,
          readonly: index == 0 ? 'false' : 'true',
        } });
    },
    deleteVersion(row) {
      this.$Modal.confirm({
        title: this.$t('message.workflow.projectDetail.pubDelete'),
        content: this.$t('message.workflow.projectDetail.confirmDelete', { version: row.version }),
        onOk: () => {
          this.$emit('modelHidden', false);
        },
        onCancel: () => {
        },
      });
    },
    checkout(row, index) {
      this.$Modal.confirm({
        title: index == 0 ? this.$t('message.workflow.projectDetail.addVersion') : this.$t('message.workflow.projectDetail.rollBack'),
        content: this.$t('message.workflow.projectDetail.addVersion', { version: row.version }),
        onOk: () => {
          this.dispatch('Project:loading', true);
          api.fetch('/dss/copyVersionAndFlow', { projectVersionID: row.id }, 'post').then(() => {
            this.dispatch('Project:loading', false);
            this.$emit('modelHidden', false);
            this.getVersionList();
          }).catch(() => {
            this.dispatch('Project:loading', false);
          });
        },
        onCancel: () => {
        },
      });
    },
    publishDetault(row) {
      if (!row.notPublish) {
        this.publishData = [];
        this.publishData.push(row.publishHistory);
        this.publishDetaultShow = true;
      }
    },
    publishResult(state) {
      if (state === 0) {
        return this.$t('message.workflow.projectDetail.init');
      } else if (state === 1) {
        return this.$t('message.workflow.projectDetail.run');
      } else if (state === 2) {
        return this.$t('message.workflow.projectDetail.publishSuccess');
      } else if (state === 3) {
        return this.$t('message.workflow.projectDetail.publishFailed');
      }
    },
  },
};
</script>
<style scoped lang='scss'>
.buttom-box {
    display: flex;
    justify-content: space-around;
    align-items: center;
}
.version-content {
  max-height: 500px;
  overflow-y: auto;
}
.version-page-bar {
  text-align: center;
  height: 30px;
  padding-top: 4px;
}
</style>
