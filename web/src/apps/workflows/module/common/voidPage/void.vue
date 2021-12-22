<template>
  <div class="void-page-wrap">
    <div class="void-page-left">
      <div class="void-page-left-title">
        <span>开发中心</span>
      </div>
      <div class="void-page-left-main">
        <div class="void-page-left-main-img">
          <img src="../../../assets/img/void_page.png" alt="空页面" />
        </div>
        <div class="void-page-left-main-tip">
          <span>无打开的工作流，可点击下方按钮添加</span>
        </div>
        <div class="void-page-left-main-button">
          <Button @click.stop="addProject">添加项目</Button>
          <Button
            type="primary"
            style="margin-left: 8px"
            icon="md-add"
            @click.stop="addWrokFlow"
            >添加工作流</Button
          >
        </div>
      </div>
    </div>
    <div class="void-page-right">
      <div class="void-page-right-title">
        <span>最近</span>
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
import eventbus from "@/common/helper/eventbus";
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
    let workspaceId = this.$route.query.workspaceId
    this.lastWorkflowList = JSON.parse(localStorage.getItem(`work_flow_lists_${workspaceId}`)) || [];
    eventbus.on("tabListChange", list => {
      if( list.length > 0 ) {
        this.lastWorkflowList = list.slice();
      } else {
        this.lastWorkflowList = JSON.parse(localStorage.getItem(`work_flow_lists_${workspaceId}`)) || [];
      }
    });
  },
  data() {
    return {
      lastWorkflowList: []
    };
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
  .void-page-left {
    min-width: 240px;
    margin-right: 106px;
    &-title {
      margin-bottom: 40px;
      height: 33px;
      line-height: 33px;
      text-align: left;

      font-family: PingFangSC-Regular;
      font-size: 24px;
      color: rgba(0, 0, 0, 0.65);
      font-weight: 400;
    }
    &-main {
      &-img {
        text-align: left;
        height: 130px;
      }
      &-tip {
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.45);
        letter-spacing: 0;
        text-align: center;
        line-height: 28px;
        height: 28px;
        font-weight: 400;
        margin: 16px 0px;
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

      font-family: PingFangSC-Regular;
      font-size: 18px;
      color: rgba(0, 0, 0, 0.65);
      font-weight: 400;
    }
    .void-page-right-main {
      .void-page-right-list {
        margin-bottom: 16px;
        font-family: PingFangSC-Regular;
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
