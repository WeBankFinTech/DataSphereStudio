<template>
  <div>
    <div class="title">数据服务操作步骤</div>
    <div class="stepWrap">
      <div>
        <div v-for="(value, index) in steps" :key="index" class="leftWrap">
          <div class="stepNumber">{{ index + 1 }}</div>
          <div :class="{'line': true, 'lineNone': index === 4}"></div>
        </div>
      </div>
      <div>
        <div v-for="(value, index) in steps" :key="index" class="rightWrap">
          {{ value }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      steps: [
        "点击左侧菜单-服务开发课表-加号按钮-创建业务流程",
        "点击业务流程-加号按钮-创建API",
        "配置API-添加数据源及设置参数，并保存",
        "测试",
        "发布-左侧菜单-服务管理，管理已发布API"
      ]
    };
  },
  methods: {}
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.title {

  font-size: 16px;
  @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
  line-height: 24px;
}
.stepWrap {
  display: flex;
  margin-top: 25px;
}
.leftWrap {
  width: 30px;
  height: 64px;
  overflow: hidden;
}
.stepNumber {
  width: 28px;
  height: 28px;
  box-sizing: border-box;
  border: 2px solid #dee4ec;
  @include border-color(#dee4ec, $dark-text-color);
  border-radius: 14px;
  display: flex;
  justify-content: center;
  align-items: center;

  font-size: 14px;
  @include font-color(rgba(0, 0, 0, 0.45), $dark-text-color);
}
.line {
  width: 2px;
  height: 41px;
  @include bg-color(#dee4ec, $dark-text-color);
  margin-left: 12px;
}
.lineNone{
  display: none;
}
.rightWrap {
  width: 380px;
  height: 64px;
  margin-left: 10px;
  padding-top: 2px;

  font-size: 14px;
  @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
  line-height: 22px;
}
</style>
