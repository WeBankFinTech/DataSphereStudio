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

import {filter, isFunction } from 'lodash';

/**
 *
 * @param {*} match 匹配到的文本
 * @param {*} proposals 需要过滤的列表
 * @param {*} fieldString 过滤条件中的字段名
 * @param {*} attachMatch 附加的过滤条件
 * @param {*} needSplit 是否需要对insertText进行截取
 * @return {*}
 */
const getReturnList = ({ match, proposals, fieldString, attachMatch, needSplit, position }, monaco) => {
  if (!match || isFunction(match)) {
    return;
  }
  let replacedStr = '';
  for (let i of match) {
    const reg = /[~'!@#￥$%^&*()-+_=:]/g;
    if (reg.test(i)) {
      replacedStr += `\\${i}`;
    } else {
      replacedStr += i;
    }
  }
  const regexp = new RegExp(`${replacedStr}`, 'i');
  let items = [];
  if (attachMatch && !needSplit) {
    items = filter(proposals, (it) => it[fieldString].startsWith(attachMatch) && regexp.test(it[fieldString]));
  } else if (attachMatch && needSplit) {
    // 这里是对例如create table和drop table的情况进行处理
    proposals.forEach((it) => {
      if (regexp.test(it[fieldString]) && it.label.indexOf(attachMatch[1]) === 0) {
        const text = it.insertText;
        items.push({
          label: it.label,
          documentation: it.documentation,
          insertText: text.slice(text.indexOf(' ') + 1, text.length - 1),
          detail: it.detail,
        });
      }
    });
  } else {
    items = filter(proposals, (it) => regexp.test(it[fieldString]));
  }
  if (position && monaco) {
    items.forEach( it => it.range = new monaco.Range(position.lineNumber, position.column - match.length , position.lineNumber, position.column));
  }
  return {
    isIncomplete: true,
    suggestions: items.slice(0,200), // 太多建议数据无意义
  };
}
export {
  getReturnList,
};
