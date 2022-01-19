<template>
  <div class="library-detail" >
    <div class="library-detail-title">{{ doc.title }}</div>
    <div class="library-detail-time">更新时间：{{ doc.updateTime }}</div>
    <div class="library-detail-content" ref="libraryDetail">
      <p v-html="doc.contentHtml"></p>
    </div>
  </div>
</template>
<script>
import { GetChapterDetail } from "@/common/service/apiGuide";
export default {
  name: "libraryDetail",
  props: {
    doc: {
      type: Object,
      default: () => {},
    },
  },
  mounted() {
    this.init();
  },
  beforeDestroy() {
    this.dispose();
  },
  methods: {
    init() {
      const libraryDetail = this.$refs.libraryDetail;
      libraryDetail.addEventListener("click", this.handleAnchor);
    },
    dispose() {
      const libraryDetail = this.$refs.libraryDetail;
      libraryDetail.removeEventListener("click", this.handleAnchor);
    },
    handleAnchor(e) {
      // 文章内部链接跳转处理
      if (e.target.tagName == "A" && e.target.target != "_blank") {
        const match = e.target.href.match(/\/\d+\.html/g); // 暂定/17.html的格式
        if (match && match[0]) {
          const id = match[0].match(/\d+/g)[0];
          GetChapterDetail(id).then((res) => {
            this.$emit("on-chapter-click", res.result)
          });
        }
        e.preventDefault();
      }
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
  .library-detail-time {
    margin: 15px;
    font-size: 12px;
    @include font-color(#999, $dark-workspace-title-color);
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
