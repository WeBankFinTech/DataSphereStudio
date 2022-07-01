<template>
  <div class="preview">
    <span>create temporary function </span>
    <span class="preview-name">{{ name }}</span>
    <span> as "</span>
    <span class="preview-params">{{ jarPara }}</span>
    <span>"</span>
  </div>
</template>
<script>
export default {
  props: {
    name: String,
    jarPara: String,
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
