<template>
  <div class="guide-editor">
    <mavon-editor
      v-if="showEditor"
      style="height: 100%; z-index: 1"
      :externalLink="externalLink"
      v-model="source"
      ref="md"
      @save="saveContent"
      @imgAdd="onImgAdd"
    ></mavon-editor>
  </div>
</template>

<script>
import axios from "axios";
import { GetContent, UpdateGuideContent } from '@dataspherestudio/shared/common/service/apiGuide';
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
      externalLink: false, // 禁用自动加载highlight.js，github-markdown-css，katex
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
      }).then(() => {
        this.$Message.success(this.$t('message.workspace.Savesucc'));
      });
    },
    // 绑定@imgAdd event
    onImgAdd(pos, $file) {
      var formdata = new FormData();
      formdata.append("file", $file);
      axios({
        url: `${location.protocol}//${window.location.host}/api/rest_j/v1/dss/guide/admin/guidecontent/uploadImage`,
        method: "post",
        data: formdata,
        headers: { "Content-Type": "multipart/form-data" },
      }).then((response) => {
        if (response.status === 200) {
          if (response.data.data && response.data.data.result) {
            // 成功返回的response.data
            // {
            //   data: {result: "8b50cbf5-e754-4883-97ba-a035f13008a7.jpg"}
            //   result: "8b50cbf5-e754-4883-97ba-a035f13008a7.jpg"
            //   message: "OK"
            //   method: null
            //   status: 0
            // }
            // 将返回的url替换到文本原位置![...](0) -> ![...](url)
            const imgName = response.data.data.result;
            this.$refs.md.$img2Url(pos, `/guideAssets/${imgName}`);
          } else {
            // 失败返回的response.data
            // {
            //   data: {}
            //   message: "没有上传文件"
            //   method: null
            //   status: 1
            // }
            this.$Message.error(response.data.message || this.$t('message.workspace.Uploadfailed'));
          }
        } else {
          this.$Message.error(response.data.message || this.$t('message.workspace.Uploadfailed'));
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.guide-editor {
  height: calc(100vh - 54px);
}
</style>
