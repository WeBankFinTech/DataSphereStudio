<template>
  <div class="tab-card-wrap">
    <!-- top -->
    <div class="tab-card-t">
      <div class="tab-card-t-l">
        <SvgIcon icon-class="biaozongshu" style="fontSize: 16px" />
        <span style="marginLeft: 8px">{{ model.tableName }}</span>
      </div>
      <div class="tab-card-t-r">
        <span>读取次数：{{ model.readCount }}次</span>
      </div>
    </div>

    <!-- bottom -->
    <div class="tab-card-b">
      <span>负责人：{{ model.userName }}</span>
      <span>创建时间：{{ model.ctime }}</span>
      <span>更新时间：{{ model.utime }}</span>
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
  :hover {
    background: #f8f9fc;
    box-shadow: 0 2px 12px 0 $shadow-color;
  }
  .tab-card-t {
    &-l {
      color: #3495f7;
      &::after {
        content: "";
        border-left: 1px solid #dee4ec;
        width: 0;
        position: absolute;
        right: -15px;
        top: 12px;
        height: 16px;
        margin: 0 12px;
      }
    }
    &-r {
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.85);
      text-align: left;
      line-height: 22px;
    }
  }
  .tab-card-b {
    font-size: 14px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    text-align: left;
    line-height: 22px;
    span {
      margin-right: 80px;
      color: rgba(0, 0, 0, 0.65);
    }
  }
}
</style>
