<template>
  <div
    :class="editorName"
    class="we-editor"/>
</template>
<script>
import monaco from './monaco-loader';
import { merge, debounce } from 'lodash';
import storage from '@dataspherestudio/shared/common/helper/storage';
import highRiskGrammar from './highRiskGrammar';
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';

const types = {
  code: {
    theme: 'defaultView',
    wordWrap: 'on',
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
    readOnly: {
      type: Boolean,
      default: false
    },
    options: Object,
    executable: {
      type: Boolean,
      default: true,
    },
    scriptType: String,
    application: String,
  },
  data() {
    const closeSuggest = storage.get('close_db_table_suggest', 'local')
    const autobreak = storage.get('editor_auto_breakline', 'local')
    return {
      editor: null,
      editorModel: null,
      decorations: null,
      isParserClose: true, // 默认关闭语法验证
      dbtbsuggest: !closeSuggest, // 默认打开库表联想
      autobreak: autobreak, // 默认关闭自动换行
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
          automaticLayout: false,
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
          fontFamily: 'Consolas,Menlo,Courier,monospace,"JinbiaoSong", "JinbiaoSongExt"'
        },
      );
      return config;
    },
  },
  watch: {
    'currentConfig.readOnly' (val) {
      this.editor.updateOptions({readOnly: val});
    },
    'value': function(newValue) {
      if (this.editor) {
        this.$emit('on-operator');
        this.deltaDecorations(newValue);
        if (newValue == this.getValue()) {
          return;
        }
        let readOnly = this.currentConfig.readOnly;
        if (readOnly) {
          // editor.setValue 和 model.setValue 都会丢失撤销栈
          // this.editorModel.setValue(newValue);
          let range = this.editor.getModel().getFullModelRange();
          const text = newValue;
          const op = {
            range,
            text,
            forceMoveMarkers: true,
          };
          this.editorModel.pushEditOperations('insertValue', [op]);

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
    language() {
      this.initParser();
    }
  },
  mounted() {
    this.initMonaco();
    this.changeTheme(localStorage.getItem('theme'));
    eventbus.on('theme.change', this.changeTheme);
  },
  beforeDestroy: function() {
    // 销毁 editor，进行gc
    if(this.actions) {
      this.actions.forEach(it => {
        it.dispose()
      })
    }
    this.editor && this.editor.dispose();
    this.editorModel && this.editorModel.dispose();
    eventbus.off('theme.change', this.changeTheme);
    this.editor = null
    this.monaco = null
    this.editorModel = null
  },
  methods: {
    // 初始化
    initMonaco() {
      this.editor = monaco.editor.create(this.$el, this.currentConfig);
      this.monaco = monaco;
      this.editorModel = this.editor.getModel();
      if (this.type !== 'log') {
        if (this.scriptType !== 'hdfsScript' && !this.readOnly) {
          this.addCommands();
          this.addActions();
        }
        if (this.language === 'hql') {
          this.initParser();
        } else {
          this.deltaDecorations(this.value);
        }
      }
      this.$emit('onload');
      this.editor.onDidChangeModelContent(debounce(() => {
        this.$emit('input', this.getValue());
      }), 100);
      this.editor.onContextMenu(debounce(() => {
        // 需要调换文字的右键菜单功能
        const selectList = [{label: 'Change All Occurrences', text: '改变所有出现'}, {label: 'Format Document', text: '格式化'}, {label: 'Command Palette', text: '命令面板'}, {label: 'Cut', text: this.$t('message.common.Cut')}, {label: 'Copy', text: this.$t('message.common.copy')}];
        if (localStorage.getItem('locale') === 'zh-CN') {
          selectList.forEach((item) => {
            let elmentList = document.querySelectorAll(`.actions-container .action-label[aria-label="${item.label}"]`);
            this.changeInnerText(elmentList, item.text);
          })
        }
      }), 100)
    },
    changeTheme(theme) {
      if (theme == 'dark') {
        monaco.editor.setTheme('vs-dark'); // dark模式使用自带的vs-dark theme
      }
      if (theme == 'light') {
        monaco.editor.setTheme('logview');
      }
    },
    changeInnerText(elList, text) {
      elList.forEach((el) => {
        el.innerText = text;
      })
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
      this.actions = [];
      const action_0 = this.editor.addAction({
        id: 'editor.action.execute',
        label: this.$t('message.common.monacoMenu.YXJB'),
        keybindings: [monaco.KeyCode.F3],
        keybindingContext: null,
        contextMenuGroupId: 'navigation',
        contextMenuOrder: 1.5,
        run() {
          vm.$emit('on-run');
        },
      });

      // this.editor.addAction({
      //   id: 'format',
      //   label: this.$t('message.common.monacoMenu.GSH'),
      //   keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KEY_L],
      //   keybindingContext: null,
      //   contextMenuGroupId: 'control',
      //   contextMenuOrder: 1.5,
      //   run(editor) {
      //     editor.trigger('anyString', 'editor.action.formatDocument');
      //   },
      // });

      const action_1 = this.editor.addAction({
        id: 'find',
        label: this.$t('message.common.monacoMenu.CZ'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_F],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.6,
        run(editor) {
          editor.trigger('find', 'actions.find');
        },
      });

      const action_2 =this.editor.addAction({
        id: 'replace',
        label: this.$t('message.common.monacoMenu.TH'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_H],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.7,
        run(editor) {
          editor.trigger('findReplace', 'editor.action.startFindReplaceAction');
        },
      });

      const action_3 =this.editor.addAction({
        id: 'commentLine',
        label: this.$t('message.common.monacoMenu.HZS'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.US_SLASH],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.8,
        run(editor) {
          editor.trigger('commentLine', 'editor.action.commentLine');
        },
      });

      const action_4 =this.editor.addAction({
        id: 'paste',
        label: this.$t('message.common.monacoMenu.ZT'),
        keybindings: [],
        keybindingContext: null,
        contextMenuGroupId: '9_cutcopypaste',
        contextMenuOrder: 2,
        run() {
          const copyString = storage.get('copyString');
          if (!copyString || copyString.length < 0) {
            vm.$Message.warning(this.$t('message.common.monacoMenu.HBQWJCFZWB'));
          } else {
            vm.insertValueIntoEditor(copyString);
          }
          return null;
        },
      });

      const action_5 =this.editor.addAction({
        id: 'gotoLine',
        label: this.$t('message.common.monacoMenu.TDZDH'),
        keybindings: [monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_G],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 1.9,
        run(editor) {
          editor.trigger('gotoLine', 'editor.action.gotoLine');
        },
      });

      // 打开、关闭库表联想
      this.closeDbTbSuggest = this.editor.createContextKey('closeDbTbSuggest', this.dbtbsuggest);
      this.openDbTbSuggest = this.editor.createContextKey('openDbTbSuggest', !this.dbtbsuggest);
      const action_6 =this.editor.addAction({
        id: 'closeDbTbSuggest',
        label: this.$t('message.common.monacoMenu.GBKBTS'),
        keybindings: [],
        keybindingContext: null,
        // 用于控制右键菜单的显示
        precondition: 'closeDbTbSuggest',
        contextMenuGroupId: 'control',
        contextMenuOrder: 2.2,
        run() {
          vm.dbtbsuggest = false;
          // 控制右键菜单的显示
          vm.openDbTbSuggest.set(true);
          vm.closeDbTbSuggest.set(false);
          storage.set('close_db_table_suggest', true, 'local')
        },
      });

      const action_7 = this.editor.addAction({
        id: 'openDbTbSuggest',
        label: this.$t('message.common.monacoMenu.DKKBTS'),
        keybindings: [],
        keybindingContext: null,
        precondition: 'openDbTbSuggest',
        contextMenuGroupId: 'control',
        contextMenuOrder: 2.3,
        run() {
          vm.dbtbsuggest = true;
          vm.openDbTbSuggest.set(false);
          vm.closeDbTbSuggest.set(true);
          storage.set('close_db_table_suggest', false, 'local')
        },
      });

      // 打开、关闭自动换行
      this.closeAutoBreak = this.editor.createContextKey('closeAutoBreak', this.autobreak);
      this.openAutoBreak = this.editor.createContextKey('openAutoBreak', !this.autobreak);
      const action_8 = this.editor.addAction({
        id: 'closeAutoBreak',
        label: this.$t('message.common.monacoMenu.GBZDHH'),
        keybindings: [],
        keybindingContext: null,
        // 用于控制右键菜单的显示
        precondition: 'closeAutoBreak',
        contextMenuGroupId: 'control',
        contextMenuOrder: 2.4,
        run() {
          vm.autobreak = false;
          // 控制右键菜单的显示
          vm.openAutoBreak.set(true);
          vm.closeAutoBreak.set(false);
          storage.set('editor_auto_breakline', true, 'local');
          vm.editor.updateOptions({wordWrap: 'off'});
        },
      });

      const action_9 =this.editor.addAction({
        id: 'openAutoBreak',
        label: this.$t('message.common.monacoMenu.DKZDHH'),
        keybindings: [],
        keybindingContext: null,
        precondition: 'openAutoBreak',
        contextMenuGroupId: 'control',
        contextMenuOrder: 2.5,
        run() {
          vm.autobreak = true;
          vm.openAutoBreak.set(false);
          vm.closeAutoBreak.set(true);
          storage.set('editor_auto_breakline', false, 'local');
          vm.editor.updateOptions({wordWrap: 'on'});
        },
      });

      const action_10 =this.editor.addAction({
        id: 'newdbsuggest',
        label: this.$t('message.common.monacoMenu.newdbcomplition'),
        keybindings: [],
        keybindingContext: null,
        contextMenuGroupId: 'control',
        contextMenuOrder: 2.5,
        run() {
          localStorage.setItem('scriptis-edditor-type', 'lsp');
          location.reload();
        },
      });
      this.actions = [ action_0, action_1, action_2, action_3, action_4, action_5, action_6, action_7, action_8, action_9, action_10]
      if (this.language === 'hql') {
        // 控制语法检查
        this.closeParser = this.editor.createContextKey('closeParser', !this.isParserClose);
        this.openParser = this.editor.createContextKey('openParser', this.isParserClose);
        const action_11 = this.editor.addAction({
          id: 'closeParser',
          label: this.$t('message.common.monacoMenu.GBYFJC'),
          keybindings: [],
          keybindingContext: null,
          // 用于控制右键菜单的显示
          precondition: 'closeParser',
          contextMenuGroupId: 'control',
          contextMenuOrder: 2.0,
          run() {
            vm.isParserClose = true;
            // 控制右键菜单的显示
            vm.openParser.set(true);
            vm.closeParser.set(false);
            vm.deltaDecorations();
          },
        });

        const action_12 = this.editor.addAction({
          id: 'openParser',
          label: this.$t('message.common.monacoMenu.DKYFJC'),
          keybindings: [],
          keybindingContext: null,
          precondition: 'openParser',
          contextMenuGroupId: 'control',
          contextMenuOrder: 2.1,
          run() {
            vm.isParserClose = false;
            vm.openParser.set(false);
            vm.closeParser.set(true);
            vm.deltaDecorations();
          },
        });
        this.actions.push(action_11);
        this.actions.push(action_12);
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
            const validParser = !vm.sqlParser ? null : vm.sqlParser.parser.parseSyntax(val, 'hive');
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
        vm.decorations = vm.editor && vm.editor.deltaDecorations(decora, []);
        if (cb) {
          cb(true);
        }
      }
    }, 500),
    setHighRiskGrammar() {
      let highRiskList = [];
      const HOVER_MESSAGE = this.$t('message.common.monacoMenu.GYFSYGWYF');
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
    async initParser() {
      this.monaco.editor.setModelLanguage(this.editorModel, this.language);
      if (this.language === 'hql' && !this.sqlParser) {
        this.sqlParser = await import('dt-sql-parser')
      }
      this.deltaDecorations(this.value);
    }
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
