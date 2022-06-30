<template>
  <div class="title-wrap">
    <span class="title-line"></span>
    <span class="title-name">{{ title }}</span>
  </div>
</template>

<script>
export default {
  name: "title",
  props: {
    title: {
      type: String,
      default: ""
    }
  }
};
</script>

<style scoped lang="scss">
@import "@dataspherestudio/shared/common/style/variables.scss";
.title-wrap {
  margin-bottom: 24px;
}
.title-line {
  display: inline-block;
  width: 4px;
  height: 16px;
  @include bg-color(#2e92f7, $dark-base-color);
  margin-right: 8px;
  vertical-align: middle;
}
.title-name {
  display: inline-block;
  font-size: 16px;
  line-height: 22px;
  @include font-color(rgba($color: #000000, $alpha: 0.85), $dark-text-color);

  font-weight: bold;
  vertical-align: middle;
}
</style>
