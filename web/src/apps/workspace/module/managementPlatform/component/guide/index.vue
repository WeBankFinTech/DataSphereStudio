<template>
  <div class="guide-editor">
    <mavon-editor
      v-if="showEditor"
      style="height: 100%; z-index: 1"
      v-model="source"
    ></mavon-editor>
    <div v-else>dashboard</div>
  </div>
</template>

<script>
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
  // mounted() {
  //   console.log("mout", this.$route.query.id);
  // },
  methods: {
    getChapter() {
      // this.$route.query.id
      setTimeout(() => {
        this.source = "### title" + this.$route.query.id;
      }, 100);
    },
    onChooseComponent(tabData) {
      this.$emit("bandleTapTab", tabData._id);
    },
    saveComponent(componentItem) {
      this.$emit("on-save", componentItem);
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
