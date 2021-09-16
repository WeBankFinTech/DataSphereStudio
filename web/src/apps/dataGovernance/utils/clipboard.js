import Vue from 'vue';
import Clipboard from 'clipboard';

/**
 *@returns
 */
function clipboardSuccess() {
  Vue.prototype.$Message.success({
    content: '复制成功',
    duration: 1.5
  });
}


/**
 *@returns
 */
function clipboardError() {
  Vue.prototype.$Message.success({
    message: '复制失败',
    duration: 1.5
  });
}

/**
 * 把字符串中的内容放到剪切板上
 * @param {String, Object}
 * @param {void}
 */
export default function handleClipboard(text, event) {
  const clipboard = new Clipboard(event.target, {
    text: () => text
  });
  clipboard.on('success', () => {
    clipboardSuccess();
    clipboard.destroy();
  });
  clipboard.on('error', () => {
    clipboardError();
    clipboard.destroy();
  });
  clipboard.onClick(event);
}
