/*eslint-disable */
import qs from 'qs';
import md5 from 'md5';
import * as domUtil from './dom';
import * as objectUtil from './object';
import * as typeUtil from './type';
import * as convertUtil from './convert';
import * as currentModules from './currentModules'
import filters from './filters'

import Vue from 'vue'
const Hub = new Vue()

let util = {
  executeCopy(textValue) {
    const input = document.createElement('textarea');
    document.body.appendChild(input);
    input.value = textValue;
    input.select();
    document.execCommand('Copy');
    input.remove();
  },
  md5,
  /**
   * 替换url中 形如 ${projectId} 格式的参数占位符为真实参数
   * ! url应该是转义过的符合URI规范的链接地址，参数如未在obj定义则最终地址会丢失该参数
   */
  replaceHolder(url, obj = {}) {
    obj = {
      dssurl: location.origin,
      cookies: document.cookie,
      ...obj
    }
    let dist = url.split('?')
    let params = qs.parse(dist[1])
    const holderReg = /\$\{([^}]*)}/g;
    let result = {}
    dist[0] = dist[0].replace(holderReg, function (a, b) {
      return obj[b]
    })
    if (dist[1]) {
      for (let key in params) {
        const resKey = key.replace(holderReg, function (a, b) {
          return obj[b]
        })
        result[resKey] = params[key].replace(holderReg, function (a, b) {
          return obj[b]
        })
      }
    }
    const distUrl = dist.length > 1 ? `${dist[0]}?${qs.stringify(result)}` : dist[0]
    return distUrl
  },
  // 打开新tab浏览器也方法
  windowOpen(url) {
    const newTab = window.open("about:blank");
    setTimeout(() => {
      const reg = /^(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
      if (process.env.NODE_ENV === 'production') {
        if (reg.test(url)) {
          newTab.location.href = url;
        }
      } else {
        newTab.location.href = url;
      }
    }, 500);
  },
  /**
   * 生成guid
   */
  guid() {
    let key;
    if (crypto && crypto.getRandomValues) {
    // eslint-disable-next-line space-infix-ops
      key = ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
      )
    } else {
      key = `${new Date().getTime()}.${Math.ceil(Math.random() * 1000)}`
    }
    return key;
  },
  Hub
};
objectUtil.merge(util, domUtil, objectUtil, typeUtil, convertUtil, currentModules);

export default util;
