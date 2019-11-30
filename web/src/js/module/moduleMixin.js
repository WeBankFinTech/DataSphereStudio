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

import eventbus from '../helper/eventbus';
import Fesx from '../helper/fesx';
import util from '../util';

let module = function(config) {
  if (!config.name) {
    return console.error('请配置独一无二的ModuleName');
  }
  // 控制对外抛出的事件
  let dispatchs = config.dispatchs || [];
  if (util.isPlainObject(dispatchs)) {
    let arr = [];
    for (let p in dispatchs) {
      if (util.isArray(dispatchs[p])) {
        dispatchs[p].forEach((item) => {
          arr.push(`${p}:${item}`);
        });
      }
    }
    dispatchs = arr;
  }
  // 全局状态
  let fesx = new Fesx(config.name, config.data);
  // 处理模块 methods
  if (config.methods) {
    let methods = Object.keys(config.methods);
    if (methods.length > 0) {
      methods.forEach((name) => {
        let method = config.methods[name];
        if (method) {
          eventbus.on(`${config.name}:${name}`, method.bind(config));
        }
      });
    }
  }
  /**
     * 挂载dispatch到空模块
     * @param {*} name
     * @param {*} param
     * @param {*} [cb=new Function()]
     * @return {Promise}
     */
  config.dispatch = function(name, param, cb = new Function()) {
    let result;
    if (util.isArray(dispatchs) && dispatchs.indexOf(name) != -1) {
      result = eventbus.emit(name, param, cb);
    }
    return Promise.resolve(result);
  };

  return {
    data: function() {
      // 模块共同的fesx
      let data = {
        fesx: fesx,
      };
      return data;
    },
    created() {
      // 处理模块下的组件监听
      if (config.events) {
        let events = config.events;
        if (util.isArray(events) && events.length > 0) {
          events.forEach((name) => {
            let method = this[name];
            if (method) {
              eventbus.on(`${name}`, method);
            }
          });
        }
      }
    },
    beforeDestroy: function() {
      if (config.events) {
        let events = config.events;
        if (util.isArray(events) && events.length > 0) {
          events.forEach((name) => {
            let method = this[name];
            if (method) {
              eventbus.off(name, method);
            }
          });
        }
      }
    },
    methods: {
      // 触发事件
      dispatch: config.dispatch,
    },
  };
};
export default module;
