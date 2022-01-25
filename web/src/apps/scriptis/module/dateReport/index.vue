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
import mixin from '@/common/service/mixin';
export default {
  data() {
    return {
      visualSrc: this.getProjectJsonResult('dailyReport'),
      loading: true,
      height: 0,
    };
  },
  mixins: [mixin],
  mounted() {
    this.height = this.$route.query.height;
  },
};
</script>
