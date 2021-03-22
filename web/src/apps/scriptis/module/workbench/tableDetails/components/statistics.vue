<template>
  <div class="statistics">
    <Split v-model="split">
      <Card
        :bordered="false"
        class="statistics-card"
        v-for="(type, index1) in info"
        :key="index1"
        slot="left">
        <p class="title" slot="title">{{ type.title }}</p>
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
        <p slot="title">{{ $t('message.scripts.tableDetails.FQXX') }}</p>
        <span
          v-show="statisticInfo.partitions"
          class="statistics-card-label">{{`* ${$t('message.scripts.tableDetails.DJFQCK')}`}}
          <Icon @click="Partitionsort" type="md-swap" style="transform: rotate(90deg);margin-left: 21px;"/>
        </span>
        <Tree
          ref="partTree"
          :data="formatPartions(statisticInfo.partitions, 0)"
          :empty-text="$t('message.scripts.tableDetails.WFQSJ')"
          class="statistics-card-tree"
          @on-select-change="getCurrentNode"/>
        <div class="statistics-page">
          <Page
            ref="page"
            :total="partitionPage.totalSize"
            :page-size-opts="partitionPage.sizeOpts"
            :page-size="partitionPage.pageSize"
            :current="partitionPage.pageNow"
            class-name="page"
            size="small"
            show-total
            show-sizer
            @on-change="change"
            @on-page-size-change="changeSize" />
        </div>
      </Card>
    </Split>
  </div>
</template>
<script>
import utils from '../utils.js';
import { isEmpty, map } from 'lodash';
import moment from 'moment';
import api from '@/common/service/api';
export default {
  props: {
    statisticInfo: {
      type: Object,
    },
    enEnv: Boolean,
    work: {
      type: Object,
      required: true,
    },
    partitionPage: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      split: 0.5,
      treeArr: [],
      info: [
        {
          title: this.$t('message.scripts.tableDetails.BXX'),
          children: [{
            key: 'fieldsNum',
            title: this.$t('message.scripts.tableDetails.BZD'),
          }, {
            key: 'tableSize',
            title: this.$t('message.scripts.tableDetails.BDX'),
          }, {
            key: 'fileNum',
            title: this.$t('message.scripts.tableDetails.WJM'),
          }, {
            key: 'partitionsNum',
            title: this.$t('message.scripts.tableDetails.FQS'),
          }, {
            key: 'tableLastUpdateTime',
            title: this.$t('message.scripts.tableDetails.ZHFWSJ'),
            type: 'timestramp',
          }],
        },
      ],
    };
  },
  methods: {
    changeSize(val) {
      this.$emit('pageSizeChange', val);
    },
    change(val) {
      this.$emit('pageChange', val)
    },
    async getCurrentNode(node) {
      // 避免分区过大，加载慢，采用动态获取数据
      const partNode = node[0];
      // 如果已经查询过就不用查询
      if (!partNode || partNode.isRender) return;
      const nodePartition = await this.getChildrenPartition(partNode);
      if (nodePartition) {
        const str =!isEmpty(partNode) &&  moment.unix(nodePartition.modificationTime / 1000).format('YYYY-MM-DD HH:mm:ss');
        const isFirstLevel = !isEmpty(partNode);
        const isHasSize = !isEmpty(partNode) && partNode.title.indexOf(this.$t('message.scripts.tableDetails.DX')) === -1;
        if (partNode && isFirstLevel && isHasSize && str) {
          // \xa0代表空格
          partNode.title += `\xa0\xa0\xa0\xa0\xa0\xa0\xa0\xa0（${this.$t('message.scripts.tableDetails.FQDX')}${nodePartition.partitionSize}，${this.$t('message.scripts.tableDetails.WJS')}${nodePartition.fileNum}，创建时间：${str}）`;
          partNode.isRender = true;
        }
      }
    },
    getChildrenPartition(node) {
      if (isEmpty(node)) return;
      const params = {
        database: this.work.data.dbName,
        tableName: this.work.data.name,
        partitionPath: node.partitionPath
      };
      this.$emit('loading', true);
      return api.fetch('datasource/getPartitionStatisticInfo', params, 'get').then((res) => {
        this.$emit('loading', false);
        return res.partitionStatisticInfo;
      }).catch(() => {
        this.$emit('loading', false);
      })
    },
    formatValue(item) {
      const statisticInfo = this.statisticInfo;
      if (!statisticInfo) {
        return null;
      }
      return utils.formatValue(statisticInfo, item);
    },
    // 对partition进行格式化成tree组件需要的格式
    formatPartions(part, level) {
      level = level + 1;
      return this.treeArr = map(part, (o) => {
        return {
          label: o.name,
          title: o.name,
          partitionSize: o.partitionSize,
          expand: false,
          createtime: o.modificationTime,
          children: this.formatPartions(o.childrens, level),
          fileNum: o.fileNum,
          level,
          partitionPath: o.partitionPath,
          isRender: false // 是否查询详情渲染
        };
      });
    },
    Partitionsort(){
      this.$emit('partitionSort');
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
  .title {
    color: #333;
  }
  .statistics-card-item {
    .statistics-card-item-title,
    .statistics-card-item-value {
      font-size: $font-size-small;
      font-weight: 400!important;
    }
  }
</style>
