<template>
  <div class="iframeClass">
    <iframe class="iframeClass" :src="url" frameborder="0" width="100%" height="100%" />
  </div>
</template>
<script>

export default {
  props: {
    url: {
      type: String,
      default: ''
    }
  },
  data() {
    return {}
  },
  mounted() {
  },
  methods: {
  },
  beforeDestroy() {
  }
};

</script>
<style lang="scss" scoped>
  .iframeClass {
    position: relative;
    height: 100%;
    width: 100%;
  }

</style>
