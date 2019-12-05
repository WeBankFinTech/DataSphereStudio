<template>
  <div
    :class="editorName"
    class="we-editor"/>
</template>
<script>
import monaco from './monaco-loader';
import { merge, debounce } from 'lodash';
import storage from '@/js/helper/storage';
import highRiskGrammar from './highRiskGrammar';

const types = {
  code: {
    theme: 'defaultView',
  },
  log: {
    language: 'log',
    theme: 'logview',
    readOnly: true,
    glyphMargin: false,
    selectOnLineNumbers: false,
    wordWrap: 'on',
  },
};
export default {
  name: 'WeEditor',
  props: {
    id: {
      type: String,
      required: false,
    },
    type: {
      type: String,
      default: 'code',
    },
    theme: String,
    language: String,
    value: String,
    readOnly: Boolean,
    options: Object,
    executable: {
      type: Boolean,
      default: true,
    },
    scriptType: String,
    application: String,
  },
  data() {
    return {
      editor: null,
      editorModel: null,
      decorations: null,
      isParserClose: false,
      closeParser: null,
      openParser: null,
      sqlParser: null,
    };
  },
  computed: {
    editorName() {
      return `we-editor-${this.type}`;
    },
    currentConfig() {
      let typeConfig = types[this.type];
      let config = merge(
        {
          automaticLayout: true,
          scrollBeyondLastLine: false,
          minimap: {
            enabled: false,
          },
          readOnly: this.readOnly,
          glyphMargin: true,
        },
        typeConfig,
        this.options,
        {
          value: this.value,
          theme: this.theme,
          language: this.language,
        },
      );
      return config;
    },
  },
  watch: {
    'value': function(newValue, oldValue) {
      if (this.editor) {
        this.$emit('on-operator');
        this.deltaDecorations(newValue);
        if (newValue == this.getValue()) {
          return;
        }
        let readOnly = this.editor.getConfiguration().readOnly;
        if (readOnly) {
          // editor.setValue 和 model.setValue 都会丢失撤销栈
          this.editor.setValue(newValue);
        } else {
          // 有撤销栈
          let range = this.editor.getModel().getFullModelRange();
          const text = newValue;
          const op = {
            identifier: {
              major: 1,
              minor: 1,
            },
            range,
            text,
            forceMoveMarkers: true,
          };
          this.editor.executeEdits('insertValue', [op]);
        }
      }
    },
  },
  mounted() {
    this.initMonaco();
  },
  beforeDestroy: function() {
    // 销毁 editor，进行gc
    this.editor && this.editor.dispose();
  },
  methods: {
    // 初始化
    initMonaco() {
      this.editor = monaco.editor.create(this.$el, this.currentConfig);
      this.monaco = monaco;
      this.editorModel = this.editor.getModel();
      this.isParserClose = !!storage.get('isParserClose', 'local'); if (this.type !== 'log') {
        if (this.scriptType !== 'hdfsScript' && !this.readOnly) {
          this.addCommands();
          this.addActions();
        }
        if (this.language === 'hql') {
          import('dt-sql-parser').then((parser) => {
            this.sqlParser = parser;
            this.deltaDecorations(this.value);
          });
        } else {
          this.deltaDecorations(this.value);
        }
      }
      this.$emit('onload');
      this.editor.onDidScrollChange(debounce(() => {
        this.$emit('scrollChange');
      }), 300);
      this.editor.onDidChangeModelContent(debounce(() => {
        this.$emit('input', this.getValue());
      }), 100);
    },
    undo() {
      this.editor.trigger('anyString', 'undo');
    },
    redo() {
      this.editor.trigger('anyString', 'redo');
    },
    // 保存当前的值
    save() {
      if (this.editorModel) {
        // Replace the entire text buffer value contained in this model.
        // 执行完之后，会清除undo栈
        // const content = this.getValue();
        // this.editorModel.setValue(content);
        this.deltaDecorations();
      }
    },
    // 保存的编辑状态 ViewState
    /**
         *  Yes, editor.saveViewState stores:
            cursor position
            scroll location
            folded sections
            for a certain model when it is connected to an editor instance.
            Once the same model is connected to the same or a different editor instance, editor.restoreViewState can be used to restore the above listed state.

            There are very many things that influence how rendering occurs:
            the current theme
            the current wrapping settings set on the editor
            the enablement of a minimap, etc.
            the current language configured for a model
            etc.
         */
    saveViewState() {
      if (this.editorModel) {
        this.editorModel.viewState = this.editor.saveViewState();
      }
    },
    // 重置之前保存的编辑状态 ViewState
    restoreViewState() {
      if (this.editorModel && this.editorModel.viewState) {
        this.editor.restoreViewState(this.editorModel.viewState);
      }
    },
    // 获取编辑器内容
    getValue() {
      return this.editor.getValue({
        lineEnding: '\n',
        preserveBOM: false,
      });
    },
    // 获取选择的内容
    getValueInRange() {
      const selection = this.editor.getSelection();
      return selection.isEmpty() ? null : this.editor.getModel().getValueInRange(selection);
    },
    // 在编辑器选中的范围插入值
    insertValueIntoEditor(value) {
      if (this.editor) {
        const SelectedRange = this.editor.getSelection();
        let range = null;
        if (SelectedRange) {
          range = new monaco.Range(
            SelectedRange.startLineNumber,
            SelectedRange.startColumn,
            SelectedRange.endLineNumber,
            SelectedRange.endColumn
          );
          const text = value;
          const op = {
            identifier: {
              major: 1,
              minor: 1,
            },
            range,
            text,
            forceMoveMarkers: true,
          };
          this.editor.executeEdits('insertValue', [op]);
        }
      }
    },
    addCommands() {
      // 保存当前脚本
      this.editor.addCommand(monaco.KeyMod.CtrlCmd + monaco.KeyCode.KEY_S, () => {
        this.$emit('on-save');
      });
      // 运行当前脚本
      if (this.executable) {
        this.editor.addCommand(monaco.KeyCode.F3, () => {
          this.$emit('on-run');
        });
      }
      // 调用浏览器本身的转换小写动作
      this.editor.addCommand(monaco.KeyMod.CtrlCmd + monaco.KeyMod.Shift
                    + monaco.KeyCode.KEY_U, () => {
        this.editor.trigger('toLowerCase', 'editor.action.transformToLowercase');
      });
      // 调用浏览器本身的转换大写动作
      this.editor.addCommand(monaco.KeyMod.CtrlCmd + monaco.KeyCode.KEY_U, () => {
        this.editor.trigger('toUpperCase', 'editor.action.transformToUppercase');
      });
    },
    addActions() {
      const vm = this;

      this.editor.addAction({
        id: 'editor.action.execute',
        label: this.$t('message.monacoMenu.YXJB'),
        keybindings: [monaco.KeyCode.F3],
        keybindingContext: null,
        contextMenuGroupId: 'navigation',
        contextMenuOrder: 1.5,
        run(ed) {
          vm.$emit('on-run');
        },
      });

      this.editor.addAction({
        id: 'format',
        label: this.$t('message.monacoMenu.GSH'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KEY_L],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.5,
        run(editor) {
          editor.trigger('anyString', 'editor.action.formatDocument');
        },
      });

      this.editor.addAction({
        id: 'find',
        label: this.$t('message.monacoMenu.CZ'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_F],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.6,
        run(editor) {
          editor.trigger('find', 'actions.find');
        },
      });

      this.editor.addAction({
        id: 'replace',
        label: this.$t('message.monacoMenu.TH'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_H],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.7,
        run(editor) {
          editor.trigger('findReplace', 'editor.action.startFindReplaceAction');
        },
      });

      this.editor.addAction({
        id: 'commentLine',
        label: this.$t('message.monacoMenu.HZS'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.US_SLASH],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.8,
        run(editor) {
          editor.trigger('commentLine', 'editor.action.commentLine');
        },
      });

      this.editor.addAction({
        id: 'paste',
        label: this.$t('message.monacoMenu.ZT'),
        keybindings: [],
        keybindingContext: null,
        contextMenuGroupId: '9_cutcopypaste',
        contextMenuOrder: 2,
        run(ed) {
          const copyString = storage.get('copyString');
          if (!copyString || copyString.length < 0) {
            vm.$Message.warning(this.$t('message.monacoMenu.HBQWJCFZWB'));
          } else {
            vm.insertValueIntoEditor(copyString);
          }
          return null;
        },
      });

      this.editor.addAction({
        id: 'gotoLine',
        label: this.$t('message.monacoMenu.TDZDH'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_G],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.9,
        run(editor) {
          editor.trigger('gotoLine', 'editor.action.gotoLine');
        },
      });

      if (this.language === 'hql') {
        // 控制语法检查
        this.closeParser = this.editor.createContextKey('closeParser', !this.isParserClose);
        this.openParser = this.editor.createContextKey('openParser', this.isParserClose);
        this.editor.addAction({
          id: 'closeParser',
          label: this.$t('message.monacoMenu.GBYFJC'),
          keybindings: [],
          keybindingContext: null,
          // 用于控制右键菜单的显示
          precondition: 'closeParser',
          contextMenuGroupId: 'control',
          contextMenuOrder: 2.0,
          run(editor) {
            storage.set('isParserClose', true, 'local'); vm.isParserClose = true;
            // 控制右键菜单的显示
            vm.openParser.set(true);
            vm.closeParser.set(false);
            vm.deltaDecorations();
          },
        });

        this.editor.addAction({
          id: 'openParser',
          label: this.$t('message.monacoMenu.DKYFJC'),
          keybindings: [],
          keybindingContext: null,
          precondition: 'openParser',
          contextMenuGroupId: 'control',
          contextMenuOrder: 2.1,
          run(editor) {
            storage.set('isParserClose', false, 'local'); vm.isParserClose = false;
            vm.openParser.set(false);
            vm.closeParser.set(true);
            vm.deltaDecorations();
          },
        });
      }
    },
    deltaDecorations: debounce(function(value, cb) {
      const vm = this;
      if (!vm.isParserClose) {
        let highRiskList = [];
        const lang = vm.language;
        const app = vm.application;
        if (lang === 'python' || (app === 'spark' && ['java', 'hql'].indexOf(lang) !== -1) || app === 'hive') {
          // 高危语法的高亮
          highRiskList = vm.setHighRiskGrammar();
          const decora = vm.decorations || [];
          let isParseSuccess = true;
          if (lang === 'hql') {
            const val = value || vm.value;
            const validParser = vm.isParserClose ? null : vm.sqlParser.parser.parseSyntax(val, 'hive');
            let newDecora = [];
            if (validParser) {
              isParseSuccess = false;
              const warningLalbel = `编译语句时异常：在行${validParser.loc.first_line}:${validParser.loc.first_column}，输入词'${validParser.text}'附近可能存在sql语法错误`;
              const range = new monaco.Range(
                validParser.loc.first_line,
                validParser.loc.first_column,
                validParser.loc.last_line,
                validParser.loc.last_column + 1,
              );
              // 第一个元素是对错误的关键词做高亮的，第二个元素是对行和margin块做高亮的
              newDecora = [{
                range,
                options: {
                  inlineClassName: 'inlineDecoration',
                },
              }, {
                range,
                options: {
                  isWholeLine: true,
                  className: 'contentClass',
                  // 要在margin中使用色块，编辑器必须将glyphMargin设置为true
                  glyphMarginClassName: 'glyphMarginClass',
                  // hover提示，文档中使用glyphHoverMessage是错的
                  hoverMessage: {
                    value: warningLalbel,
                  },
                },
              }];
            }
            // 第一个参数是旧的，用于清空decorations
            vm.decorations = vm.editor.deltaDecorations(decora, newDecora.concat(highRiskList));
            vm.$emit('is-parse-success', isParseSuccess);
          } else {
            vm.decorations = vm.editor.deltaDecorations(decora, highRiskList);
          }
          if (cb) {
            cb(isParseSuccess);
          }
        } else {
          if (cb) {
            cb(true);
          }
        }
      } else {
        // 关闭语法检查时，如果编辑器上有错误色块，先清除
        const decora = vm.decorations || [];
        vm.decorations = vm.editor.deltaDecorations(decora, []);
        if (cb) {
          cb(true);
        }
      }
    }, 500),
    setHighRiskGrammar() {
      let highRiskList = [];
      const HOVER_MESSAGE = this.$t('message.monacoMenu.GYFSYGWYF');
      let highRiskGrammarToken = highRiskGrammar[this.language];
      if (this.language === 'java') {
        highRiskGrammarToken = highRiskGrammar.scala;
      }
      if (highRiskGrammarToken && highRiskGrammarToken.length) {
        // 因为正则的token有多个，所以要用所有的规则去遍历
        highRiskGrammarToken.forEach((key) => {
          // 使用正则去抓取编辑器中的匹配文本，是一个数组
          const errorLabel = this.editorModel.findMatches(key, false, true, false, null, true);
          if (errorLabel.length) {
            let formatedRiskGrammer = [];
            errorLabel.forEach((item) => {
              // 拿到当前整行的内容
              const lineContent = this.editorModel.getLineContent(item.range.startLineNumber);
              let reg = /^[^\-\-]\s*/;
              if (this.language === 'python') {
                reg = /^[^\#]\s*/;
              } else if (this.language === 'java') {
                reg = /^[^\/\/]\s*/;
              }
              const isHasComment = reg.test(lineContent);
              // 判断是否有注释
              if (isHasComment) {
                formatedRiskGrammer.push({
                  range: item.range,
                  options: {
                    inlineClassName: 'highRiskGrammar',
                    hoverMessage: {
                      value: HOVER_MESSAGE,
                    },
                  },
                });
              }
            });
            highRiskList = highRiskList.concat(formatedRiskGrammer);
          }
        });
      }
      return highRiskList;
    },
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
