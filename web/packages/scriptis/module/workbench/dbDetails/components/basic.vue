<template>
  <div>
    <Card
      :bordered="false"
      class="basic-card"
      v-for="(type, index1) in info"
      :key="index1">
      <p class="title" slot="title">{{ type.title }} <span class="note">{{ $t('message.scripts.dbinfotips') }}</span></p>
      <div>
        <span
          v-for="(item, index2) in type.children"
          :key="index2"
          class="basic-card-item"
          :class="{'comment': item.key === 'description'}">
          <span
            class="basic-card-item-title"
            :style="{'width': enEnv?'140px':'100px'}">{{ item.title }}: </span>
          <span
            class="basic-card-item-value"
            :class="{'comment': item.key === 'description'}"
            :style="{'width': enEnv?'calc(100% - 144px)':'calc(100% - 104px)'}">{{ formatValue(item) }}</span>
        </span>
      </div>
    </Card>
  </div>
</template>
<script>
import utils from '@dataspherestudio/shared/common/util';
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
          title: this.$t('message.scripts.dbinfs'),
          children: [{
            key: 'dbName',
            title: this.$t('message.scripts.dbname'),
          }, {
            key: 'dbSize',
            title: this.$t('message.scripts.dbsize'),
          }, {
            key: 'dbCapacity',
            title: this.$t('message.scripts.dbquota'),
          }, {
            key: 'tableQuantity',
            title: this.$t('message.scripts.tablenum')
          }, {
            key: 'description',
            title: this.$t('message.scripts.dbdesc')
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
  .note {
    font-weight: normal;
    padding-left: 10px;
    display: inline-block;
    vertical-align: top;
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
              width: 100%;
              height: auto;
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
                  word-break: break-all;
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

