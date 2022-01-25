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
import utils from '@/common/util';
export default {
  props: {
    dbInfo: {
      type: Object,
    },
    enEnv: Boolean,
  },
  data() {
    return {
      info: [
        {
          title: '库基本信息',
          children: [{
            key: 'dbName',
            title: '库名',
          }, {
            key: 'dbSize',
            title: '库大小',
          }, {
            key: 'dbCapacity',
            title: '库配额',
          }, {
            key: 'tableQuantity',
            title: '表数量'
          }],
        }
      ],
    };
  },
  computed: {
    tableBaseInfo() {
      return this.dbInfo;
    },
  },
  methods: {
    formatValue(item) {
      return utils.formatValue(this.dbInfo, item);
    },
  },
};
</script>
<style lang="scss" scoped>
  .title {
    color: #333;
  }
  .basic-card {
      margin-bottom: 10px;
      height: calc(100% - 52px);
      overflow: hidden;
      .basic-card-item {
          display: inline-flex;
          width: 50%;
          height: 36px;
          padding-left: 10px;
          align-items: center;
          &.comment {
              height: 42px;
              align-items: start;
          }
          .basic-card-item-title {
              display: inline-block;
              width: 100px;
              font-weight: bold;
          }
          .basic-card-item-value {
              display: inline-block;
              width: calc(100% - 104px);
              overflow: hidden;
              &.comment {
                  overflow-y: auto;
                  height: 100%;
              }
          }
      }
  }
  .basic-card-item {
    .basic-card-item-title,
    .basic-card-item-value {
      font-size: 12px;
      font-weight: 400!important;
    }
  }
</style>

