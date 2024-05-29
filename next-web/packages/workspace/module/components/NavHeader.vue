<template>
  <ul class="nav-container">
    <li class="nav-container__back isOperate" @click="back">
      <LeftOutlined class="font_active" />
    </li>
    <li class="nav-container__title font_active">
      {{ title }}
    </li>
  </ul>
</template>
<script lang="ts" setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { LeftOutlined } from '@fesjs/fes-design/icon';

const props = defineProps({
  title: {
    // 标题
    type: String,
    default: '',
  },
  direcBackPath: {
    type: String,
    default: '',
  },
});

// 菜单标题
const title = computed(() => props.title);
const router = useRouter();

// 返回上一级
const back = () => {
  // 微应用的跳转
  if (window?.__MICRO_APP_ENVIRONMENT__) {
    if (
      window.history.length &&
      window.history.length > 1 &&
      !props.direcBackPath
    ) {
      window.history.go(-1);
    } else {
      const path = `${window.location.href.split('#')[0]}#/`;
      window.location.href = path;
      window.location.reload();
    }
    return;
  }
  // 普通跳转
  if (!props.direcBackPath) {
    // 存在输入路径的情况
    const hasBack = window?.history?.state?.back;
    hasBack ? router.back() : router.replace('/');
  } else {
    // 没有制定路径的情况
    router.replace({ path: props.direcBackPath });
  }
};
</script>
<style lang="less">
@activeColor: #0f1222;
@fontSize: 16px;
.nav-container {
  padding-bottom: 14px;
  font-size: 0;
  display: flex;
  background: #f2f2f2;
  li {
    height: 26px;
    line-height: 26px;
    font-size: @fontSize;
    color: #93949b;
  }
  .isOperate {
    cursor: pointer;
  }
  &__back {
    margin-right: 6px;
    display: inline-flex;
    align-items: center;
  }
  &__title {
    margin-right: 10px;
  }
  .font_active {
    color: @activeColor;
  }
}
</style>
