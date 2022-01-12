<template>
  <div class="library-detail" >
    <div class="library-detail-title">{{ doc.title }}</div>
    <div class="library-detail-content">
      <p v-html="doc.contentHtml"></p>
    </div>
  </div>
</template>
<script>
export default {
  name: "libraryDetail",
  props: {
    doc: {
      type: Object,
      default: () => {},
    },
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.library-detail {
  .library-detail-title {
    margin: 20px 15px;
    font-size: 20px;
    @include font-color(#333, $dark-workspace-title-color);
  }
  .library-detail-content {
    padding: 0 15px 15px;
    @include font-color(#666, $dark-text-color);
    /deep/img {
      display: block;
      border: 0;
      max-width: 100%;
      cursor: pointer;
    }
  }
}
</style>
