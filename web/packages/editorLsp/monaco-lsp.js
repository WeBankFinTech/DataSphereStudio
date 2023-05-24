import * as monaco from 'monaco-editor';

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
}

/**
 * 初始化编辑器
 */
export function initClient({ el, value, service }, config, filePath, cb) {
  const options = { ...config }
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
  function callBack (type) {
    return ({client, errMsg})=> {
      if (client) {
        window.languageClient[type] = client
      }
      if (errMsg) {
        if (cb) {
          cb({errMsg})
        }
      }
    }
  }
  // sql
  if (sqlLang.indexOf(config.language) > -1 && service.sql) {
    const wsurl = service.sql.replace(/\$\{([^}]*)}/g, function (a, b) {
      return locationObj[b]
    })
    sqlLanguage.connectService(editor, wsurl, callBack('sql'))
  }
  // python
  if (config.language === 'python' && service.py) {
    const wsurl = service.py.replace(/\$\{([^}]*)}/g, function (a, b) {
      return locationObj[b]
    })
    pyLanguage.connectService(editor, wsurl, callBack('python'))
  }

  return { monaco, editor }
}

/**
 * get language client
 * @param {*} lang
 */
export function getLanguageClient(lang) {
  if (lang && sqlLang.indexOf(lang) > -1 ) lang = 'sql'
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
