<template>
  <div class="statistics">
    <Split v-model="split">
      <Card
        :bordered="false"
        class="statistics-card"
        v-for="(type, index1) in info"
        :key="index1"
        slot="left">
        <p slot="title">{{ type.title }}</p>
        <p>
          <span
            v-for="(item, index2) in type.children"
            :key="index2"
            class="statistics-card-item">
            <span
              class="statistics-card-item-title"
              :style="{'width': enEnv?'140px':'100px'}">{{ item.title }}: </span>
            <span
              class="statistics-card-item-value"
              :style="{'width': enEnv?'calc(100% - 144px)':'calc(100% - 104px)'}">{{ formatValue(item) }}</span>
          </span>
        </p>
      </Card>
      <Card
        :bordered="false"
        class="statistics-card"
        slot="right">
        <p slot="title">{{ $t('message.tableDetails.FQXX') }}</p>
        <span
          v-show="tableInfo.isPartition"
          class="statistics-card-label">{{`* ${$t('message.tableDetails.DJFQCK')}`}}</span>
        <Tree
          ref="partTree"
          :data="formatPartions(tableInfo.statisticInfo.partitions, 0)"
          :empty-text="$t('message.tableDetails.WFQSJ')"
          class="statistics-card-tree"
          @on-select-change="getCurrentNode"/>
      </Card>
    </Split>
  </div>
</template>
<script>
import utils from '../utils.js';
import { isEmpty, map } from 'lodash';
export default {
  props: {
    tableInfo: {
      type: Object,
    },
    enEnv: Boolean,
  },
  data() {
    return {
      split: 0.5,
      info: [
        {
          title: this.$t('message.tableDetails.BXX'),
          children: [{
            key: 'fieldsNum',
            title: this.$t('message.tableDetails.BZD'),
          }, {
            key: 'tableSize',
            title: this.$t('message.tableDetails.BDX'),
          }, {
            key: 'fileNum',
            title: this.$t('message.tableDetails.WJM'),
          }, {
            key: 'partitionsNum',
            title: this.$t('message.tableDetails.FQS'),
          }, {
            key: 'tableLastUpdateTime',
            title: this.$t('message.tableDetails.ZHFWSJ'),
            type: 'timestramp',
          }],
        },
      ],
    };
  },
  methods: {
    getCurrentNode(node) {
      const partNode = node[0];
      const isFirstLevel = !isEmpty(partNode) && partNode.level === 1;
      const isHasSize = !isEmpty(partNode) && partNode.title.indexOf(this.$t('message.tableDetails.DX')) === -1;
      if (partNode && isFirstLevel && isHasSize) {
        // \xa0代表空格
        partNode.title += `\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0（${this.$t('message.tableDetails.FQDX')}${partNode.partitionSize}，${this.$t('message.tableDetails.WJS')}${partNode.fileNum}）`;
      }
    },
    formatValue(item) {
      const statisticInfo = this.tableInfo.statisticInfo;
      if (!statisticInfo) {
        return null;
      }
      return utils.formatValue(statisticInfo, item);
    },
    // 对partition进行格式化成tree组件需要的格式
    formatPartions(part, level) {
      level = level + 1;
      return map(part, (o) => {
        return {
          label: o.name,
          title: o.name,
          partitionSize: o.partitionSize,
          expand: false,
          children: this.formatPartions(o.childrens, level),
          fileNum: o.fileNum,
          level,
        };
      });
    },
  },
};
</script>
