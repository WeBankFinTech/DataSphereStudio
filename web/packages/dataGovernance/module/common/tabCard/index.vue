<template>
  <div class="tab-card-wrap" @click="onChoose">
    <!-- top -->
    <div class="tab-card-t">
      <div class="tab-card-t-l">
        <SvgIcon icon-class="biao" style="fontsize: 16px" />
        <span
          style="marginleft: 8px; fontsize: 16px"
          v-html="model.name"
        ></span>
      </div>
      <div class="tab-card-t-r">
        <!-- <span>读取次数：{{ model.readCount }}次</span> -->
        <div>
          负责人：<span v-html="model.owner" class="content-html"></span>
        </div>
        <div :title="model.createTime">创建时间：{{ model.createTime }}</div>
      </div>
    </div>

    <!-- bottom -->
    <div class="tab-card-b">
      <div :title="model.dbName">
        数据库：<span v-html="model.dbName" class="content-html"></span>
      </div>
      <div :title="subject">
        主题域：<span v-html="subject" class="content-html"></span>
      </div>
      <div :title="layer">
        分层：<span v-html="layer" class="content-html"></span>
      </div>
    </div>

    <div class="tab-card-b">
      <div v-if="!model.comment">
        <span>描述：-</span>
      </div>
      <div v-else>
        描述：<span v-html="model.comment" class="content-html"></span>
      </div>
    </div>

    <div class="tab-card-b" style="width: 100%">
      <div v-if="model.labels.length > 0">
        <span>标签：</span>
        <span
          class="tab-card-b-tag content-html"
          v-for="(label, idx) in model.labels"
          :key="idx"
          v-html="label"
        ></span>
      </div>
      <div v-else>
        <span>标签：-</span>
      </div>
    </div>

    <div class="tab-card-b" v-if="model.columns && model.columns.length > 0">
      <div class="tab-card-b-field" style="width: 100%">
        <span>相关字段：</span>
        <span
          v-for="(item, idx) in model.columns"
          :key="idx"
          v-html="idx + 1 < model.columns.length ? item + '/' : item"
          class="content-html"
        ></span>
      </div>
    </div>
  </div>
</template>

<script>
import { EventBus } from '../../../module/common/eventBus/event-bus'
export default {
  name: 'tabCard',
  props: {
    model: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      modal: [],
      query: '',
    }
  },
  mounted() {
    EventBus.$on('onQueryForHighLight', (query) => {
      this.query = query
    })
  },
  destroyed() {
    EventBus.$off('onQueryForHighLight')
  },
  methods: {
    onChoose() {
      this.$emit('on-choose', this.model)
    },
  },
  computed: {
    subject: function () {
      let classifications = this.model.classifications
      let subject = ''
      if (classifications && classifications.length) {
        classifications.forEach((classification) => {
          if (
            classification.superTypeNames &&
            classification.superTypeNames.length
          ) {
            if (classification.superTypeNames[0] === 'subject') {
              subject = classification.typeName
            }
          }
        })
      }
      if (this.query) {
        let reg = new RegExp(this.query, 'g')
        let _query = `<span>${this.query}</span>`
        subject = subject.replace(reg, _query)
      }
      return subject
    },
    layer: function () {
      let classifications = this.model.classifications
      let layer = ''
      if (classifications && classifications.length) {
        classifications.forEach((classification) => {
          if (
            classification.superTypeNames &&
            classification.superTypeNames.length
          ) {
            if (
              classification.superTypeNames[0] === 'layer' ||
              classification.superTypeNames[0] === 'layer_system'
            ) {
              layer = classification.typeName
            }
          }
        })
      }
      if (this.query) {
        let reg = new RegExp(this.query, 'g')
        let _query = `<span>${this.query}</span>`
        layer = layer.replace(reg, _query)
      }
      return layer
    },
  },
}
</script>

<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.tab-card-wrap {
  min-height: 10.5vh;
  padding-left: 24px;
  padding-right: 24px;
  border: 1px solid #dee4ec;
  @include border-color(#dee4ec, $dark-border-color-base);
  border-top: none;
  border-left: none;
  display: flex;
  flex-direction: column;
  justify-content: center;
  cursor: pointer;
  &:hover {
    @include bg-color(#f8f9fc, $dark-base-color);
    box-shadow: 0 2px 12px 0 $shadow-color;
  }
  .tab-card-t {
    display: flex;
    &-l {
      @include font-color(#3495f7, $dark-text-color);
      &::after {
        content: '';
        border-left: 1px solid #dee4ec;
        @include border-color(#dee4ec, $dark-border-color-base);
        width: 0;
        right: -15px;
        top: 12px;
        height: 16px;
        margin: 0 12px;
      }
    }
    &-r {
      font-size: 14px;
      @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
      text-align: left;
      line-height: 22px;
      display: flex;
      flex: 1;
      justify-content: space-between;
    }
  }
  .tab-card-b {
    font-size: 14px;

    @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
    text-align: left;
    line-height: 22px;
    margin-top: 8px;
    > div {
      text-overflow: ellipsis;
      white-space: nowrap;
      word-break: break-all;
      overflow: hidden;
      margin-right: 5px;
      width: calc(33% - 5px);
      @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
      display: inline-block;
    }

    &-field {
      height: 40px;
      line-height: 40px;
      @include bg-color(#f4f7fb, $dark-border-color-base);
      padding-left: 12px;

      font-size: 14px;
      text-align: left;
      font-weight: 400;
    }

    &-tag {
      background: rgba(0, 0, 0, 0.04);
      border: 1px solid #dee4ec;
      border-radius: 2px;
      padding: 4px 8px;
    }
  }
}
.content-html {
  ::v-deep span {
    color: #3495f7;
  }
}
</style>
