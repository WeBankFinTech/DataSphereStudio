<template>
  <div class="guide-editor">
    <mavon-editor
      v-if="showEditor"
      style="height: 100%; z-index: 1"
      v-model="source"
      @save="saveContent"
    ></mavon-editor>
    <div v-else>dashboard</div>
  </div>
</template>

<script>
import { GetContent, UpdateGuideContent } from "@/common/service/apiGuide";
import { mavonEditor } from "mavon-editor";
import "mavon-editor/dist/css/index.css";
import "mavon-editor/dist/markdown/github-markdown.min.css";
export default {
  name: "guide",
  components: {
    mavonEditor,
  },
  data() {
    return {
      source: "",
    };
  },
  computed: {
    showEditor() {
      return !!this.$route.query.id;
    },
  },
  watch: {
    "$route.query.id": {
      handler: function (value) {
        if (value) {
          this.getChapter();
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getChapter() {
      GetContent(this.$route.query.id).then((data) => {
        this.source = data.result.content || "";
      });
    },
    saveContent(value, render) {
      UpdateGuideContent({
        id: this.$route.query.id,
        content: value,
        contentHtml: render,
      }).then((res) => {
        this.$Message.success("保存成功");
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.guide-editor {
  height: calc(100vh - 54px);
}
</style>
