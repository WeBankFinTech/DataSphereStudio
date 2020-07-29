<template>
  <div class="api-container">
    <div class="new-btn">
      <Button type="primary" @click="gotoNewApi">新建API数据源</Button>
    </div>
    <Card>
      <ApiList
        :data="apiData"
        :columns="columns"
        @start="start"
        @stop="stop"
      />
    </Card>
    <Spin
      v-if="loading"
      size="large"
      fix
    />
  </div>
</template>

<script>
import api from '@/js/service/api';
import ApiList from './ApiList';

export default {
  components: {
    ApiList,
  },
  data() {
    return {
      apiData: [],
      columns: [
        {
          title: this.$t('message.api.name'),
          slot: 'name'
        },
        {
          title: this.$t('message.api.frequency'),
          slot: 'frequency'
        },
        {
          title: this.$t('message.api.createTime'),
          slot: 'createTime',
        },
        {
          title: this.$t('message.api.action'),
          slot: 'action',
        }
      ]
    };
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      // 获取api列表
      this.loading = true;
      // jobEngine/listAll
      api.fetch('test/jobEngine/listAll', {}, 'get').then((res) => {
        this.apiData = res.workspaces;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    start(row) {
      this.loading = true;
      api.fetch(`jobEngine/start/${row.id}`, {}, 'get').then((res) => {
        this.loading = false;
        this.$Message.success(this.$t('message.api.startSuccess'));
      }).catch(() => {
        this.loading = false;
        this.$Message.warning(this.$t('message.api.startFail'));
      });
    },
    stop(row) {
      this.loading = true;
      api.fetch(`jobEngine/stop/${row.id}`, {}, 'get').then((res) => {
        this.loading = false;
        this.$Message.success(this.$t('message.api.stopSuccess'));
      }).catch(() => {
        this.loading = false;
        this.$Message.warning(this.$t('message.api.stopFail'));
      });
    },
    gotoNewApi() {
      this.$router.push({ path: '/newApi' });
    }
  },
}
</script>

<style lang="scss" scoped>
.api-container {
  padding: 20px;
  .new-btn {
    margin-bottom: 10px;
  }
}
</style>