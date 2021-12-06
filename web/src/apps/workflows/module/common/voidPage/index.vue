<template>
  <div class="void-page-wrap">
    <div class="void-page-main">
      <div class="void-page-main-img">
        <img src="../../../assets/img/void_page.png" alt="空页面" />
      </div>
      <div class="void-page-main-title">
        <span>{{ tipTitle }}</span>
      </div>
      <div class="void-page-main-button">
        <Button
          icon="md-add"
          @click.stop="buttonClick"
          class="void-page-main-button-item"
          >{{ tipTitle.slice(-3) === "工作流" ? "添加工作流" : "添加项目" }}</Button
        >
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VoidPage',
  props: {
    tipTitle: String,
    buttonClick: {
      type: Function,
      default: () => {}
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.void-page-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #fff;
  .void-page-main {
    &-title {
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.45);
      letter-spacing: 0;
      text-align: center;
      line-height: 28px;
      font-weight: 400;
      margin-top: 24px;
      margin-bottom: 16px;
    }
    &-button {
      min-width: 106px;
      height: 32px;
      line-height: 32px;
      text-align: center;
      &-item {
        background: #2e92f7;
        color: #fff;
      }
    }
  }
}
</style>
