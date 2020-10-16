/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import qs from 'qs';
import md5 from 'md5';
import * as domUtil from './dom';
import * as objectUtil from './object';
import * as typeUtil from './type';
import * as convertUtil from './convert';


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
  }
};
objectUtil.merge(util, domUtil, objectUtil, typeUtil, convertUtil);

export default util;
