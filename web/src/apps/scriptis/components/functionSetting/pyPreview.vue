<template>
  <div class="preview">
    <span>udf.register("</span>
    <span class="preview-name">{{ name }}</span>
    <span>", </span>
    <span class="preview-params">{{ pyPara }}</span>
    <span>)</span>
  </div>
</template>
<script>
export default {
  props: {
    name: String,
    pyPara: String,
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
