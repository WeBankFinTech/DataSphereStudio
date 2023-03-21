import {
  MonacoLanguageClient,
  MonacoServices,
  createConnection,
  ErrorAction,
  CloseAction
} from "monaco-languageclient";
import { listen } from "vscode-ws-jsonrpc";
import ReconnectingWebSocket from "reconnecting-websocket";

let installed = false
let languageClient
let maxRetries = 10
window.languageClient = window.languageClient || {}
/**
 * 创建websocket
 * @param {*} url
 */
function createWebSocket(url) {
  const socketOptions = {
    maxReconnectionDelay: 10000,
    minReconnectionDelay: 1000,
    reconnectionDelayGrowFactor: 1.3,
    connectionTimeout: 10000,
    maxRetries,
    debug: false,
  };
  return new ReconnectingWebSocket(url, [], socketOptions);
}

/**
 * 创建client
 * @param {*} connection
 */
function createLanguageClient(connection, documentSelector = ["python"]) {
  return new MonacoLanguageClient({
    name: "Python Language Server MonacoClient",
    clientOptions: {
      documentSelector,
      errorHandler: {
        error: () => ErrorAction.Continue,
        closed: () => CloseAction.DoNotRestart
      },
    },
    connectionProvider: {
      get: (errorHandler, closeHandler) => {
        return Promise.resolve(
          createConnection(connection, errorHandler, closeHandler)
        );
      },
    },
  });
}

/**
 * register language
 * @param {*} monaco
 */
export function register(monaco) {
  monaco.languages.register({
    id: 'python',
    extensions: ['.py', '.python'],
    aliases: ['py', 'python'],
    mimetypes: ["application/json"],
  });
}

/**
 * connect language server
 * @param {*} editor
 */
export function connectService(editor, url, cb) {
  if (url) {
    if (!installed) {
      installed = true;
      MonacoServices.install(editor);
    }
    if (window.languageClient.__connected_py_langserver !== true) {
      window.languageClient.__connected_py_langserver = true;
      const webSocket = createWebSocket(url);
      webSocket.addEventListener('error', () => {
        if (webSocket._retryCount >= maxRetries) {
          cb({
            errMsg: 'connect-failded'
          })
        }
      });
      window.languageClient.__webSocket_py_langserver = webSocket;
      listen({
        webSocket,
        onConnection: (connection) => {
          if (languageClient) { languageClient.cleanUp() }
          // if (!languageClient) {
          languageClient = createLanguageClient(connection);
          const disposable = languageClient.start();
          connection.onClose(() => disposable.dispose());
          // }
          if (cb) {
            cb({
              client: languageClient
            })
          }
        },
      });
    } else {
      if (cb) {
        cb({
          client: languageClient
        })
      }
    }
  }
}

export default {
  register,
  connectService
}
