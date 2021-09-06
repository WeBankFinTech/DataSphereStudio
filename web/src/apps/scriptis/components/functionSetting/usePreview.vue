<template>
  <div
    :title="title"
    class="preview">
    <span class="preview-type">{{ paraL }} </span>
    <span class="preview-name">{{ name }}</span>
    <span>(</span>
    <span class="preview-type">{{ paraR }}</span>
    <span>)</span>
  </div>
</template>
<script>
export default {
  props: {
    name: String,
    paraL: String,
    paraR: String,
    title: String,
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
