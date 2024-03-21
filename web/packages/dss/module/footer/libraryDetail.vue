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
import { GetChapterDetail } from '@dataspherestudio/shared/common/service/apiGuide';
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
@import "@dataspherestudio/shared/common/style/variables.scss";
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
    ::v-deepimg {
      display: block;
      border: 0;
      max-width: 100%;
      cursor: pointer;
    }
    ::v-deeph1 {
      font-size: 18px;
      margin: 6px 0;
    }
    ::v-deeph2 {
      font-size: 16px;
      margin: 6px 0;
    }
    ::v-deeph3 {
      font-size: 14px;
      margin: 6px 0;
    }
    ::v-deeph4 {
      font-size: 12px;
      margin: 6px 0;
    }
    ::v-deeph5 {
      font-size: 12px;
      margin: 6px 0;
    }
    p ::v-deepp {
      margin: 5px;
    }
  }
}
</style>
