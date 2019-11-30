<template>
  <div class="version-content">
    <Table
      :columns="columns"
      :data="versionData"
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
            @click="viewVersion(row, index)">打开</Button>
          <Button
            type="primary"
            size="small"
            @click="checkout(row, index)">{{ index == 0 ? $t('message.newConst.newCreate') : $t('message.project.rollBack') }}</Button>
        </div>
      </template>
    </Table>
    <Modal
      v-model="publishDetaultShow"
      :title="$t('message.project.publishDetail')"
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
import api from '@/js/service/api';

export default {
  props: {
    currentProjectData: {
      type: Object,
      default: null,
    },
  },
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
  computed: {
    columns() {
      return [
        {
          title: this.$t('message.project.version'),
          key: 'version',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.project.status'),
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
            }, scope.row.notPublish ? this.$t('message.project.unpublish') : this.$t('message.project.published'));
          },
        },
        {
          title: this.$t('message.project.updator'),
          key: 'updator',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.project.comment'),
          key: 'comment',
          width: 186,
          align: 'center',
        },
        {
          title: this.$t('message.project.uptateTime'),
          key: 'updateTime',
          width: 160,
          align: 'center',
          render: (h, scope) => {
            return h('span', {}, moment(scope.row.updateTime).format('YYYY-MM-DD HH:mm:ss'));
          },
        },
        {
          title: this.$t('message.project.action'),
          slot: 'action',
          align: 'center',
          width: 170,
        },
      ]
    },
    detaultColumns() {
      return [
        {
          title: this.$t('message.project.creator'),
          key: 'creator',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.project.status'),
          key: 'state',
          width: 120,
          align: 'center',
          render: (h, scope) => {
            return h('span', {}, this.publishResult(scope.row.state));
          },
        },
        {
          title: this.$t('message.project.publishData'),
          key: 'comment',
          width: 120,
          align: 'center',
        },
        {
          title: this.$t('message.project.publishTime'),
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
      });
    },
    viewVersion(row, index) {
      this.$emit('modelHidden', false);
      this.$router.push({ name: 'Workflow',
        query: {
          projectTaxonomyID: this.currentProjectData.taxonomyID,
          projectID: row.projectID,
          projectVersionID: row.id,
          readonly: index == 0 ? 'false' : 'true',
        } });
    },
    deleteVersion(row) {
      this.$Modal.confirm({
        title: this.$t('message.project.pubDelete'),
        content: this.$t('message.project.confirmDelete', { version: row.version }),
        onOk: () => {
          this.$emit('modelHidden', false);
        },
        onCancel: () => {
        },
      });
    },
    checkout(row, index) {
      this.$Modal.confirm({
        title: index == 0 ? this.$t('message.project.addVersion') : this.$t('message.project.rollBack'),
        content: this.$t('message.project.addVersion', { version: row.version }),
        onOk: () => {
          this.dispatch('Project:loading', true);
          api.fetch('/dss/copyVersionAndFlow', { projectVersionID: row.id }, 'post').then((res) => {
            this.dispatch('Project:loading', false);
            this.$emit('modelHidden', false);
            // this.getVersionList();
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
        return this.$t('message.project.init');
      } else if (state === 1) {
        return this.$t('message.project.run');
      } else if (state === 2) {
        return this.$t('message.project.publishSuccess');
      } else if (state === 3) {
        return this.$t('message.project.publishFailed');
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
</style>
