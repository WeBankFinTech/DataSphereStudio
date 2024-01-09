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

/**
 *判断类型
 * @param {*} obj 需要判断的对象
 */
export function typeOf(obj) {
  const toString = Object.prototype.toString;
  const map = {
    '[object Boolean]': 'boolean',
    '[object Number]': 'number',
    '[object String]': 'string',
    '[object Function]': 'function',
    '[object Array]': 'array',
    '[object Date]': 'date',
    '[object RegExp]': 'regExp',
    '[object Undefined]': 'undefined',
    '[object Null]': 'null',
    '[object Object]': 'object'
  };
  return map[toString.call(obj)];
}

/**
 *  Find components upward
 * @param {*} context
 * @param {*} componentName
 */
export function findComponentUpward(context, componentName) {
  let componentNames;
  if (typeOf(componentName) === 'string') {
    componentNames = [componentName];
  } else if (typeOf(componentName) === 'array') {
    componentNames = componentName;
  } else {
    return null;
  }

  let parent = context.$parent;
  let name = parent.$options.name;
  while (parent && (!name || componentNames.indexOf(name) < 0)) {
    parent = parent.$parent;
    if (parent) name = parent.$options.name;
  }
  return parent;
}

/**
 *  Find component downward
 * @param {*} context
 * @param {*} componentName
 */
export function findComponentDownward(context, componentName) {
  let componentNames;
  if (typeOf(componentName) === 'string') {
    componentNames = [componentName];
  } else if (typeOf(componentName) === 'array') {
    componentNames = componentName;
  } else {
    return null;
  }

  const childrens = context.$children;
  let children = null;

  if (childrens.length) {
    childrens.forEach(child => {
      const name = child.$options.name;
      if (componentNames.indexOf(name) != -1) {
        children = child;
      }
    });

    for (let i = 0; i < childrens.length; i++) {
      const child = childrens[i];
      const name = child.$options.name;
      if (componentNames.indexOf(name) != -1) {
        children = child;
        break;
      } else {
        children = findComponentDownward(child, componentNames);
        if (children) break;
      }
    }
  }
  return children;
}

/**
 * 生成guid
 */
export function getKey() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0,
      v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}
