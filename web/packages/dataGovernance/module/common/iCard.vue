<template>
  <div class="i-card-wrap">
    <!-- left -->
    <div class="i-card-wrap-l">
      <SvgIcon :icon-class="model.iconName" style="fontSize: 40px" />
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
  name: "iCard",
  props: {
    model: {
      type: Object,
      default: null
    }
  }
};
</script>

<style scoped lang="scss">
@import "@dataspherestudio/shared/common/style/variables.scss";
.i-card-wrap {
  flex: 1;
  box-sizing: border-box;
  height: 113px;
  display: flex;
  @include bg-color(#fff, $dark-base-color);
  border: 1px solid;
  @include border-color(#dee4ec, $dark-border-color-base);
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

      font-size: 14px;
      @include font-color(#606266, $dark-text-color);
      margin-bottom: 8px;
    }
    &-content {

      font-size: 24px;
      @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
      line-height: 33px;
    }
    &-unit {

      font-size: 16px;
      @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
      line-height: 33px;
      margin-left: 8px;
    }
  }
}
</style>
