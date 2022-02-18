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
        /**
         * 备注：因为mavonEditor在把md转换为html时会自动给链接加上target="_blank"，但如果链接本身href是以#开头，则忽略
         * 所以，如果需要在文章内部继续做链接跳转其他内置文章，可以把href设置为#id的形式
         */
        const match = e.target.href.match(/#\d+/g); // id的格式为#id
        if (match && match[0]) {
          const id = match[0].replace("#", "");
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
    @include font-color(#333, $dark-text-color);
  }
  .library-detail-time {
    margin: 15px;
    font-size: 12px;
    @include font-color(#999, $dark-text-color);
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
    /deep/h1, /deep/h2, /deep/h3, /deep/h4, /deep/h5, /deep/h6 {
      margin-top: 24px;
      margin-bottom: 16px;
      font-weight: 600;
      line-height: 1.25;
    }
    /deep/blockquote, /deep/dl, /deep/ol, /deep/p, /deep/pre, /deep/table, /deep/ul {
      margin-bottom: 16px;
    }
    /deep/ol {
      padding-left: 2em;
      list-style: decimal;
    }
    /deep/ul {
      padding-left: 2em;
      list-style: disc;
    }
    /deep/table tr {
      background-color: #fff;
      border-top: 1px solid #c6cbd1;
    }
    /deep/table td, /deep/table th {
      padding: 6px 13px;
      border: 1px solid #dfe2e5;
    }
  }
}
</style>
