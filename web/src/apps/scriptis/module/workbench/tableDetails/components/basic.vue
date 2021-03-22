<template>
  <div>
    <Card
      :bordered="false"
      class="basic-card"
      v-for="(type, index1) in info"
      :key="index1">
      <p class="title" slot="title">{{ type.title }}</p>
      <div>
        <span
          v-for="(item, index2) in type.children"
          :key="index2"
          class="basic-card-item"
          :class="{'comment': item.key === 'comment'}">
          <span
            class="basic-card-item-title"
            :style="{'width': enEnv?'140px':'100px'}">{{ item.title }}: </span>
          <span
            class="basic-card-item-value"
            :class="{'comment': item.key === 'comment'}"
            :style="{'width': enEnv?'calc(100% - 144px)':'calc(100% - 104px)'}">{{ formatValue(item) }}</span>
        </span>
      </div>
    </Card>
  </div>
</template>
<script>
import utils from '../utils.js';
export default {
  props: {
    tableInfo: {
      type: Object,
    },
    enEnv: Boolean,
  },
  data() {
    return {
      info: [
        {
          title: this.$t('message.scripts.tableDetails.BZBSX'),
          children: [{
            key: 'database',
            title: this.$t('message.scripts.tableDetails.DBN'),
          }, {
            key: 'name',
            title: this.$t('message.scripts.tableDetails.TN'),
          }, {
            key: 'alias',
            title: this.$t('message.scripts.tableDetails.BBM'),
          }, {
            key: 'partitionTable',
            title: this.$t('message.scripts.tableDetails.FQM'),
            type: 'boolean',
          }, {
            key: 'creator',
            title: this.$t('message.scripts.tableDetails.CJYH'),
          }, {
            key: 'createTime',
            title: this.$t('message.scripts.tableDetails.CJSI'),
            type: 'timestramp',
          }, {
            key: 'latestAccessTime',
            title: this.$t('message.scripts.tableDetails.ZHFWSJ'),
            type: 'timestramp',
          }, {
            key: 'comment',
            title: this.$t('message.scripts.tableDetails.BMS'),
          }],
        },
        {
          title: this.$t('message.scripts.tableDetails.BMXSX'),
          children: [{
            key: 'lifecycle',
            title: this.$t('message.scripts.tableDetails.SMZQ'),
            type: 'convert',
          }, {
            key: 'modelLevel',
            title: this.$t('message.scripts.tableDetails.MXCJ'),
            type: 'convert',
          }, {
            key: 'useWay',
            title: this.$t('message.scripts.tableDetails.SYFS'),
            type: 'convert',
          }, {
            key: 'externalUse',
            title: this.$t('message.scripts.tableDetails.WBSFSY'),
            type: 'boolean',
          }],
        },
        {
          title: this.$t('message.scripts.tableDetails.BYYSX'),
          children: [{
            key: 'productName',
            title: this.$t('message.scripts.tableDetails.SXCP'),
          }, {
            key: 'projectName',
            title: this.$t('message.scripts.tableDetails.SSXM'),
          }, {
            key: 'usage',
            title: this.$t('message.scripts.tableDetails.YT'),
          }],
        },
      ],
    };
  },
  computed: {
    tableBaseInfo() {
      const baseInfo = this.tableInfo.baseInfo;
      return Object.assign(baseInfo.application, baseInfo.base, baseInfo.model);
    },
  },
  methods: {
    formatValue(item) {
      return utils.formatValue(this.tableBaseInfo, item);
    },
  },
};
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
  .title {
    color: #333;
  }
  .basic-card-item {
    .basic-card-item-title,
    .basic-card-item-value {
      font-size: $font-size-small;
      font-weight: 400!important;
    }
  }
</style>

