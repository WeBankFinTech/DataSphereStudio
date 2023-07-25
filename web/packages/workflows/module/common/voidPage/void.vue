<template>
  <div class="void-page-wrap">
    <div class="void-page-left">
      <div class="void-page-left-title">
        <span>{{ $t('message.workflow.Development') }}</span>
      </div>
      <div class="void-page-left-main">
        <div class="void-page-left-main-img">
          <SvgIcon icon-class="empty" width="200px" height="200px" />
        </div>
        <div class="void-page-left-main-tip">
          <span>{{ $t('message.workflow.Noopen') }}</span>
        </div>
        <div class="void-page-left-main-button">
          <Button @click.stop="addProject">{{ $t('message.workflow.Add') }}</Button>
          <Button
            type="primary"
            style="margin-left: 8px"
            icon="md-add"
            @click.stop="addWrokFlow"
          >{{ $t('message.workflow.Addworkflow') }}</Button
          >
        </div>
      </div>
    </div>
    <div class="void-page-right">
      <div class="void-page-right-title" v-if="lastWorkflowList.length">
        <span>{{ $t('message.workflow.Recent') }}</span>
      </div>
      <div class="void-page-right-main">
        <div
          v-for="(item, idx) in lastWorkflowList"
          :key="idx"
          class="void-page-right-list"
        >
          <a class="void-page-right-list-link" @click="goto(item)">{{ item.name }}</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Void",
  props: {
    addProject: {
      type: Function,
      default: () => {}
    },
    addWrokFlow: {
      type: Function,
      default: () => {}
    },
    goto: {
      type: Function,
      default: () => {}
    }
  },
  mounted() {
  },
  data() {
    return {
      lastWorkflowList: []
    };
  }
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";

.void-page-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  @include bg-color(#fff, $dark-workspace-body-bg-color);
  .void-page-left {
    min-width: 240px;
    margin-right: 106px;
    &-title {
      height: 33px;
      line-height: 33px;
      text-align: left;
      @include font-color($light-text-color, $dark-text-color);


      font-size: 24px;
      @include border-color(rgba(0, 0, 0, 0.65), $dark-border-color-base);
      font-weight: 400;
    }
    &-main {
      &-img {
        text-align: left;
        @include font-color(#ebebeb, #3f434c);
      }
      &-tip {

        font-size: 14px;
        @include border-color(rgba(0, 0, 0, 0.65), $dark-border-color-base);
        letter-spacing: 0;
        text-align: center;
        line-height: 28px;
        height: 28px;
        font-weight: 400;
        margin-bottom: 16px;
        @include font-color($light-text-color, $dark-text-color);
      }
      &-button {
        text-align: left;
        height: 32px;
      }
    }
  }

  .void-page-right {
    margin-left: 106px;
    &-title {
      box-sizing: content-box;
      height: 25px;
      line-height: 25px;
      padding: 16px 0px;
      @include font-color($light-text-color, $dark-text-color);


      font-size: 18px;
      @include border-color(rgba(0, 0, 0, 0.65), $dark-border-color-base);
      font-weight: 400;
    }
    .void-page-right-main {
      .void-page-right-list {
        margin-bottom: 16px;

        font-size: 14px;
        color: #2e92f7;
        font-weight: 400;

        &-link {
          text-decoration: none;
        }
      }
    }
  }
}
</style>
