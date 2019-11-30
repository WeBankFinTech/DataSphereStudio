<template>
  <div
    class="date-report">
    <iframe
      id="iframeName"
      :src="visualSrc"
      frameborder="0"
      width="100%"
      :height="height"/>
  </div>
</template>
<script>
export default {
  data() {
    return {
      visualSrc: this.getDataReportUrl(),
      loading: true,
      height: 0,
    };
  },
  mounted() {
    this.height = this.$route.query.height;
  },
};
</script>
