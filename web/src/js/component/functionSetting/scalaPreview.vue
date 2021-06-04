<template>
  <div class="preview">
    <span>sqlContext.udf.register[</span>
    <span class="preview-type">{{ typeL }}</span>
    <template v-if="typeR">
      <span>, </span>
      <span class="preview-type">{{ typeR }}</span>
    </template>
    <span>]("</span>
    <span class="preview-name">{{ name }}</span>
    <span>", </span>
    <span class="preview-params">{{ para }}</span>
    <span>)</span>
  </div>
</template>
<script>
export default {
  props: {
    name: String,
    typeL: String,
    typeR: String,
    para: String,
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
