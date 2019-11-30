<template>
  <div>
    <Card
      :bordered="false"
      class="basic-card"
      v-for="(type, index1) in info"
      :key="index1">
      <p slot="title">{{ type.title }}</p>
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
          title: this.$t('message.tableDetails.BZBSX'),
          children: [{
            key: 'database',
            title: this.$t('message.tableDetails.DBN'),
          }, {
            key: 'name',
            title: this.$t('message.tableDetails.TN'),
          }, {
            key: 'alias',
            title: this.$t('message.tableDetails.BBM'),
          }, {
            key: 'partitionTable',
            title: this.$t('message.tableDetails.FQM'),
            type: 'boolean',
          }, {
            key: 'creator',
            title: this.$t('message.tableDetails.CJYH'),
          }, {
            key: 'createTime',
            title: this.$t('message.tableDetails.CJSI'),
            type: 'timestramp',
          }, {
            key: 'latestAccessTime',
            title: this.$t('message.tableDetails.ZHFWSJ'),
            type: 'timestramp',
          }, {
            key: 'comment',
            title: this.$t('message.tableDetails.BMS'),
          }],
        },
        {
          title: this.$t('message.tableDetails.BMXSX'),
          children: [{
            key: 'lifecycle',
            title: this.$t('message.tableDetails.SMZQ'),
          }, {
            key: 'modelLevel',
            title: this.$t('message.tableDetails.MXCJ'),
          }, {
            key: 'useWay',
            title: this.$t('message.tableDetails.SYFS'),
          }, {
            key: 'externalUse',
            title: this.$t('message.tableDetails.WBSFSY'),
            type: 'boolean',
          }],
        },
        {
          title: this.$t('message.tableDetails.BYYSX'),
          children: [{
            key: 'productName',
            title: this.$t('message.tableDetails.SXCP'),
          }, {
            key: 'projectName',
            title: this.$t('message.tableDetails.SSXM'),
          }, {
            key: 'usage',
            title: this.$t('message.tableDetails.YT'),
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
