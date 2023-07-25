<template>
  <div>
    <div class="title">{{ $t('message.workflow.Streaming') }}</div>
    <div class="stepWrap">
      <div>
        <div v-for="(value, index) in steps" :key="index" class="leftWrap">
          <div class="stepNumber">{{ index + 1 }}</div>
          <div :class="{'line': true, 'lineNone': index === 4}"></div>
        </div>
      </div>
      <div>
        <div v-for="(value, index) in steps" :key="index" class="rightWrap">
          <span v-html="value"></span>
          <!-- {{ value }} -->
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
        `Flink SQL 脚本开发调试&nbsp;&nbsp;&nbsp;&nbsp;前往<a href="#/home?workspaceId=${this.$route.query.workspaceId}">即席查询</a>`,
        `离线创建jar包并配置&nbsp;&nbsp;&nbsp;&nbsp;样例<a href='./streamis/flinkJar.zip' download="flinkJar.zip">下载</a>&nbsp;&nbsp;配置说明下载<a href='./streamis/Streamis流式应用ZIP包上传格式.txt' download="Streamis流式应用ZIP包上传格式.txt">下载</a>`,
        "导入jar包至实时计算中心",
        this.$t('message.workflow.confitEnv'),
        this.$t('message.workflow.Configure')
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
