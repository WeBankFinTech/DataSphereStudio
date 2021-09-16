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
        "点击业务流程-加号按钮-常见API",
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
@import "@/common/style/variables.scss";
.title {
  font-family: PingFangSC-Medium;
  font-size: 16px;
  color: rgba(0, 0, 0, 0.65);
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
  border-radius: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: PingFangSC-Regular;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
}
.line {
  width: 2px;
  height: 41px;
  background: #dee4ec;
  margin-left: 12px;
}
.lineNone{
  background: #fff;
}
.rightWrap {
  width: 380px;
  height: 64px;
  margin-left: 10px;
  padding-top: 2px;
  font-family: PingFangSC-Regular;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  line-height: 22px;
}
</style>
