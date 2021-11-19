<template>
  <div class="tab-card-wrap" @click="onChoose">
    <!-- top -->
    <div class="tab-card-t">
      <div class="tab-card-t-l">
        <SvgIcon icon-class="biao" style="fontSize: 16px;" />
        <span style="marginLeft: 8px; fontSize: 16px">{{ model.name }}</span>
      </div>
      <div class="tab-card-t-r">
        <!-- <span>读取次数：{{ model.readCount }}次</span> -->
      </div>
    </div>

    <!-- bottom -->
    <div class="tab-card-b">
      <span style="width: 110px">负责人：{{ model.owner }}</span>
      <span style="width: 210px">创建时间：{{ model.createTime }}</span>
      <span style="width: 110px">数据库：{{ model.dbName }}</span>
      <div>
        <span style="width: 110px">主题域：{{ subject }}</span>
        <span style="width: 110px">分层：{{ layer }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "tabCard",
  props: {
    model: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      modal: []
    };
  },
  methods: {
    onChoose() {
      this.$emit("on-choose", this.model);
    }
  },
  computed: {
    subject: function() {
      let classifications = this.model.classifications;
      let subject = "";
      if (classifications && classifications.length) {
        classifications.forEach(classification => {
          if (
            classification.superTypeNames &&
            classification.superTypeNames.length
          ) {
            if (classification.superTypeNames[0] === "subject") {
              subject = classification.typeName;
            }
          }
        });
      }
      return subject;
    },
    layer: function() {
      let classifications = this.model.classifications;
      let layer = "";
      if (classifications && classifications.length) {
        classifications.forEach(classification => {
          if (
            classification.superTypeNames &&
            classification.superTypeNames.length
          ) {
            if (
              classification.superTypeNames[0] === "layer" ||
              classification.superTypeNames[0] === "layer_system"
            ) {
              layer = classification.typeName;
            }
          }
        });
      }
      return layer;
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.tab-card-wrap {
  min-height: 99px;
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
        content: "";
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
      font-family: PingFangSC-Regular;
      font-size: 14px;
      @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
      text-align: left;
      line-height: 22px;
    }
  }
  .tab-card-b {
    font-size: 14px;
    font-family: PingFangSC-Regular;
    @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
    text-align: left;
    line-height: 22px;
    margin-top: 8px;
    overflow: hidden;
    span {
      margin-right: 80px;
      @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
      display: inline-block;
    }
  }
}
</style>
