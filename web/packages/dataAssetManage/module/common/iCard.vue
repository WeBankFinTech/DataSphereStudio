<template>
  <div class="i-card-wrap">
    <!-- left -->
    <div class="i-card-wrap-l">
      <SvgIcon :icon-class="model.iconName" style="fontSize: 40px"/>
    </div>

    <!-- right -->
    <div class="i-card-wrap-r">
      <div class="i-card-wrap-r-title">
        <span>{{ model.title }}</span>
      </div>
      <div>
        <span class="i-card-wrap-r-content">{{ model.content }}</span>
        <span class="i-card-wrap-r-unit">{{ model.unit }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'iCard',
  props: {
    model: {
      type: Object,
      default: null
    }
  }
}
</script>

<style scoped lang="scss">
.i-card-wrap {
    box-sizing: border-box;
    width: 364px;
    height: 113px;
    display: flex;
    background: #FFFFFF;
    border: 1px solid #DEE4EC;
    border-radius: 4px;
    margin-right: 24px;
    &-l {
        width: 85px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    &-r {
        display: flex;
        flex-direction: column;
        justify-content: center;
        &-title {
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: #606266;
          margin-bottom: 8px;
        }
        &-content {
          font-family: PingFangSC-Medium;
          font-size: 24px;
          color: rgba(0,0,0,0.85);
          line-height: 33px;
        }
        &-unit {
          font-family: PingFangSC-Medium;
          font-size: 16px;
          color: rgba(0,0,0,0.45);
          line-height: 33px;
          margin-left: 8px;
        }
    }
}
</style>
