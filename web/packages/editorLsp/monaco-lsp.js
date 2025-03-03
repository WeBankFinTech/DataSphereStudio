import * as monaco from 'monaco-editor';
import storage from '@dataspherestudio/shared/common/helper/storage';
import { sendLLMRequest } from '@dataspherestudio/shared/common/helper/aicompletion';

// 引入主题，语言，关键字
import defaultView from './theme/defaultView';
import logview from './theme/logView';
import sqlLanguage from './languages/sql';
import pyLanguage from './languages/py';
import log from './languages/log';
import sh from './languages/sh';
import out from './languages/out';
import shKeyword from './keyword/sh';

window.setImmediate = setTimeout;

const languagesList = monaco.languages.getLanguages();
const findLang = find(languagesList, (lang) => {
  return lang.id === 'log';
});

const sqlLang = ['sql', 'hql']
window.languageClient = window.languageClient || {}

if (!findLang) {
  // 注册theme
  defaultView.register(monaco);
  logview.register(monaco);

  // 注册languages
  sqlLanguage.register(monaco);
  pyLanguage.register(monaco);
  log.register(monaco);
  sh.register(monaco);
  out.register(monaco);
  // 注册关键字联想
  shKeyword.register(monaco);
  monaco.languages.registerInlineCompletionsProvider("*", {
    provideInlineCompletions: function (model, position, context, token) {
      let language = window.__scirpt_language || "sql";
      delete window.__scirpt_language;
      if (window.$APP_CONF && window.$APP_CONF.aisuggestion !== true ) {
        return {
          items: []
        }
      }
      const value = model.getValue();
      // 使用getOffsetAt()获取光标位置的绝对偏移量
      const cursorOffset = model.getOffsetAt(position);
      // 根据绝对偏移量分割内容
      const prefix = value.substring(0, cursorOffset);
      const suffix = value.substring(cursorOffset);
      return sendLLMRequest({
        language,
        segments: {
          prefix,
          suffix,
        }
      }, position)
    },
    handleItemDidShow() {
      // window.inlineCompletions = []
      // console.log('handleItemDidShow')
    },
    freeInlineCompletions(arg) {
      // console.log(arg, 'freeInlineCompletions')
    }
  });
}

/**
 * 初始化编辑器
 */
export function initClient({ el, value, service }, config, filePath, cb) {
  const options = {
    ...config,
  }
  service.lsp_service = service.lsp_service || {}
  options.wordBasedSuggestions = false;
  options.inlineSuggest =  {
    enabled: true,
    showToolbar: 'never',
    mode: 'prefix',
    suppressSuggestions: false,
  }
  let model
  if (filePath) {
    const uri = monaco.Uri.parse(filePath)
    model = monaco.editor.getModel(uri)
    if (!model) {
      model = monaco.editor.createModel(
        value,
        sqlLang.indexOf(config.language) > -1 ? 'sql' : config.language,
        uri
      )
    } else {
      model.setValue(value)
    }
  }
  if (model) {
    options.model = model
  }
  const editor = monaco.editor.create(el, options);
  const locationObj = {
    protocol: location.protocol == 'https:' ? 'wss:' : 'ws:',
    host: location.host
  }
  /**
   *
   * @param {*} type
   */
  function callBack(type) {
    return ({ client, errMsg }) => {
      if (client) {
        window.languageClient[type] = client
        const closeSuggest = storage.get('close_db_table_suggest');
        if (closeSuggest) {
          client.onReady().then(() => {
            // 关闭库表联想
            const params = {
              command: "changeAssociation",
              arguments: ['close'],
            };
            client.sendRequest("workspace/executeCommand", params);
          })
        }
      }
      if (errMsg) {
        if (cb) {
          cb({ errMsg })
        }
      }
    }
  }
  // sql
  if (sqlLang.indexOf(config.language) > -1 && service.lsp_service.sql) {
    const wsurl = service.lsp_service.sql.replace(/\$\{([^}]*)}/g, function (a, b) {
      return locationObj[b]
    })
    sqlLanguage.connectService(monaco, wsurl, callBack('sql'), editor)
  }
  // python
  if (config.language === 'python' && service.lsp_service.py) {
    const wsurl = service.lsp_service.py.replace(/\$\{([^}]*)}/g, function (a, b) {
      return locationObj[b]
    })
    pyLanguage.connectService(monaco, wsurl, callBack('python'), editor)
  }

  return { monaco, editor }
}

/**
 * get language client
 * @param {*} lang
 */
export function getLanguageClient(lang) {
  if (lang && sqlLang.indexOf(lang) > -1) lang = 'sql'
  return window.languageClient[lang]
}

/**
 * 库表联想
 * @param {*} languageClient
 */
export function changeAssociation(lang, open) {
  const client = getLanguageClient(lang)
  if (client) {
    const params = {
      command: "changeAssociation",
      arguments: open ? ['open'] : ['close'],
    };
    client.sendRequest("workspace/executeCommand", params);
  }
}