<template>
  <div class="wrap" :class="{'full': isFullscreen}">
    <div class="flex toolbar">
      <div
        class="workbench-body-navbar-item"
        @click="fullAction">
        <Icon :type="isFullscreen?'md-contract':'md-expand'" />
        <span class="navbar-item-name">{{ isFullscreen ? $t('message.scripts.constants.logPanelList.releaseFullScreen') : $t('message.scripts.constants.logPanelList.fullScreen') }}</span>
      </div>
    </div>
    <div ref="editor" class="el-editor" />
  </div>

</template>
<script>
import monaco from "./monaco-loader";
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';

const defaultToolbar = {
  mergeDefaults: true
}
export default {
  name: 'WeEditorCompare',
  props: {
    original: String,
    value: {
      type: String,
      required: true
    },
    theme: {
      type: String,
      default: 'vs'
    },
    language: String,
    options: Object,
    diffEditor: {
      type: Boolean,
      default: false
    },
    toolbar: {
      type: [Boolean, Object],
      default: () => defaultToolbar
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  model: {
    event: 'change'
  },

  data(){
    return {
      isFullscreen: false
    }
  },

  watch: {
    options: {
      deep: true,
      handler(options) {
        if (this.editor) {
          const editor = this.getModifiedEditor()
          editor.updateOptions(options)
        }
      }
    },

    value(newValue) {
      if (this.editor) {
        const editor = this.getModifiedEditor()
        if (editor && editor.getValue && newValue !== editor.getValue()) {
          editor.setValue(newValue);
        }
      }
    },

    original(newValue) {
      if(this.editor) {
        this.editor.originalEditor.setValue(newValue)
      }
    },

    language(newVal) {
      if (this.editor) {
        const editor = this.getModifiedEditor()
        this.monaco.editor.setModelLanguage(editor.getModel(), newVal)
      }
    },

    theme(newVal) {
      if (this.editor) {
        this.monaco.editor.setTheme(newVal)
      }
    },

    diffEditor() {
      this.changeModel()
    }
  },

  computed: {

  },

  mounted() {
    document.addEventListener('keyup', this.esc, false);
    this.initMonaco(monaco)
    this.monaco = monaco;
    this.changeTheme(localStorage.getItem('theme'));
    eventbus.on('theme.change', this.changeTheme);

  },

  beforeDestroy() {
    document.removeEventListener('keyup', this.esc, false);
    eventbus.off('theme.change', this.changeTheme);
    this.editor && this.editor.dispose()
  },

  methods: {
    changeModel() {
      this.editor && this.editor.dispose();
      if(this.monaco) {
        this.initMonaco(this.monaco)
      }
    },
    initMonaco(monaco) {
      const options = Object.assign({
        value: this.value,
        theme: this.theme,
        language: this.language,
        automaticLayout: true,
        scrollBeyondLastLine: false,
        minimap: {
          enabled: false,
        },
        readOnly: this.readOnly
      },
      this.options
      )
      if (this.diffEditor) {
        this.editor = monaco.editor.createDiffEditor(this.$refs.editor, options)
        const originalModel = monaco.editor.createModel(
          this.original,
          this.language
        )
        const modifiedModel = monaco.editor.createModel(
          this.value,
          this.language
        )
        this.editor.setModel({
          original: originalModel,
          modified: modifiedModel
        })
      } else {
        this.editor = monaco.editor.create(this.$refs.editor, options)
      }

      // @event `change`
      const editor = this.getModifiedEditor()
      editor.onDidChangeModelContent(event => {
        const value = editor.getValue();
        this.$emit('change', value, event);
      })
      this.layout();
      this.$emit('editorDidMount', this.editor)
    },

    getEditor() {
      return this.editor
    },

    getModifiedEditor() {
      return this.diffEditor && this.editor.getModifiedEditor ? this.editor.getModifiedEditor() : this.editor
    },

    focus() {
      this.editor.focus()
    },

    fullAction() {
      if (this.isFullscreen) {
        this.exitFullScreen()
      } else  {
        this.fullScreen()
      }
    },

    layout() {
      this.$nextTick(()=>{
        this.editor.layout();
      })
    },

    esc(event) {
      var which = event.which || event.keyCode;
      if (this.isFullscreen && which == 27) {
        this.exitFullScreen();
      }
    },

    exitFullScreen() {
      this.isFullscreen = false;
      this.layout();
      this.$emit('full-screen-change', false)
    },

    fullScreen() {
      this.isFullscreen = true;
      this.layout();
      this.$emit('full-screen-change', true)
    },

    changeTheme(theme) {
      if (theme == 'dark') {
        this.monaco.editor.setTheme('logview-dark'); // dark模式使用自带的vs-dark theme
      } else {
        this.monaco.editor.setTheme('logview');
      }
    },
  }
}

</script>
<style scoped lang="scss">
    .wrap {
        position: relative;
        border: 1px solid #eee;
        height: 100%;
    }
    .toolbar {
        height: 30px;
        line-height: 30px;
        margin-bottom: 1px;
        border-bottom: 1px solid #dcdee2;
    }
    img {
        display: inline-block;
        margin-right: 5px;
    }
    .el-editor {
        width: 100%;
        height: calc(100% - 32px);
        min-height: 380px;
    }
    .workbench-body-navbar-item {
        margin: 0 16px;
        cursor: pointer;
        color: #666;
    }
    .full {
        position: fixed;
        top: 0;
        left: 0;
        border: 0;
        right: 0;
        width: 100%;
        height: 100%;
        background: #fff;
        z-index: 9999;
    }
    .flex {
      display: flex;
      align-items: center;
      display: flex;
    }
</style>
