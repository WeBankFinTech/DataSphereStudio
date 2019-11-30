<template>
  <Modal
    v-model="versionDetail"
    :title="$t('message.workflow.versionDetail')"
    :footer-hide="true"
    width="810">
    <div class="version-content">
      <Table
        :columns="columns"
        :data="versionData"
        max-height="600"
        border>
        <template
          slot-scope="{row}"
          slot="action">
          <Button
            type="primary"
            size="small"
            @click="viewVersion(row)">{{$t('message.workflow.open')}}</Button>
        </template>
      </Table>
    </div>
  </Modal>
</template>
<script>
import moment from 'moment';

export default {
  props: {
    versionDetailShow: {
      type: Boolean,
      default: false,
    },
    versionData: {
      type: Array,
      defatult: [],
    },
  },
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
      this.$emit('versionDetailShow', val);
    },
  },
  computed: {
    columns() {
      return [
        {
          title: this.$t('message.project.version'),
          key: 'version',
          width: 130,
          align: 'center',
        },
        {
          title: this.$t('message.project.updator'),
          key: 'updator',
          width: 130,
          align: 'center',
        },
        {
          title: this.$t('message.project.comment'),
          key: 'comment',
          width: 200,
          align: 'center',
        },
        {
          title: this.$t('message.project.uptateTime'),
          key: 'updateTime',
          width: 186,
          align: 'center',
          render: (h, scope) => {
            return h('span', {}, moment(scope.row.updateTime).format('YYYY-MM-DD HH:mm:ss'));
          },
        },
        {
          title: this.$t('message.project.action'),
          slot: 'action',
          align: 'center',
          width: 130,
        },
      ]
    }
  },
  created() {

  },
  methods: {
    viewVersion(row) {
      this.versionDetail = false;
      this.$emit('goto', row);
    },
  },
};
</script>
<style scoped lang='scss'>
.version-content {
  max-height: 500px;
  overflow-y: auto;
}
</style>
