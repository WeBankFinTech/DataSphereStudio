<template>
  <div class="content-editor">
    <codemirror ref="mycode" v-model="code" :options="options"></codemirror>
  </div>
</template>

<script>
// 文件内引入
import { codemirror } from "vue-codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/theme/ambiance.css"; // 这里引入的是主题样式，根据设置的theme的主题引入，一定要引入！！
require("codemirror/mode/javascript/javascript"); // 这里引入的模式的js，根据设置的mode引入，一定要引入！！
require("codemirror/mode/sql/sql"); // 这里引入的模式的js，根据设置的mode引入，一定要引入！！
require("codemirror/mode/clike/clike"); // 这里引入的模式的js，根据设置的mode引入，一定要引入！！

export default {
  name: "CodemirrorName",
  // 注册使用
  components: {
    codemirror,
  },
  props: {
    value: { type: String, isRequired: true },
    height: { type: String, default: "300" },
    cmMode: { type: String, default: "text/javascript" },
    readOnly: { type: Boolean, default: false },
  },
  data() {
    return {
      editor: null,
      options: {
        mode: this.cmMode,
        line: true,
        tabSize: 4, // 制表符的宽度
        indentUnit: 2, // 一个块应该缩进多少个空格（无论这在编辑语言中意味着什么）。默认值为 2。
        firstLineNumber: 1, // 从哪个数字开始计算行数。默认值为 1。
        readOnly: this.readOnly, // 只读
        autorefresh: true,
        smartIndent: true, // 上下文缩进
        lineNumbers: true, // 是否显示行号
        styleActiveLine: true, // 高亮选中行
        viewportMargin: Infinity, //处理高度自适应时搭配使用
        showCursorWhenSelecting: true, // 当选择处于活动状态时是否应绘制游标
      },
    };
  },
  computed: {
    code: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit("change", val);
      },
    },
  },
  //   watch: {
  //     value(val) {
  //       this.code = val;
  //     },
  //   },
  mounted() {
    // this.editor = this.$ref.mycode.codemirror;
    // this.editor.setSize("auto", height);
  },
  methods: {
    onCmCodeChange(newCode) {
      console.log("this is new code", newCode);
      this.code = newCode;
      this.$emit("editorChange", newCode);
    },
  },
};
</script>

<style scoped>
.content {
  width: 100%;
  height: 100%;
  border: 1px solid #dcdee2;
}
</style>
