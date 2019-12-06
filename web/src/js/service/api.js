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
 * 操作Api
 */
import util from '../util';
import axios from 'axios';
import router from './router';
import { Message } from 'iview';

// 什么一个数组用于存储每个请求的取消函数和标识
let pending = [];
let cancelConfig = null;
let CancelToken = axios.CancelToken;
let removePending = (config) => {
  for (let p = 0; p < pending.length; p++) {
    const params = JSON.stringify(config.params);
    // 如果存在则执行取消操作
    if (pending[p].u === config.url + '&' + config.method + '&' + params) {
      pending[p].f();// 执行取消操作
      pending.splice(p, 1);// 移除记录
    }
  }
};

let cutReq = (config) => {
  for (let p = 0; p < pending.length; p++) {
    const params = JSON.stringify(config.params);
    if (pending[p].u === config.url + '&' + config.method + '&' + params) {
      return true;
    }
  }
};

const instance = axios.create({
  baseURL: process.env.VUE_APP_MN_CONFIG_PREFIX || `http://${window.location.host}/api/rest_j/v1/`,
  timeout: 30000,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json;charset=UTF-8' },
});

instance.interceptors.request.use((config) => {
  // 增加国际化参数
  config.headers['Content-language'] = localStorage.getItem('locale') || 'zh-CN';
  let flag = cutReq(config);
  // 当上一次相同请求未完成时，无法进行第二次相同请求
  if (flag === true) {
    return config;
  } else {
    const params = JSON.stringify(config.params);
    // 用于正常请求出现错误时移除
    cancelConfig = config;
    config.cancelToken = new CancelToken((c) => {
      // 添加标识和取消函数
      pending.push({
        u: config.url + '&' + config.method + '&' + params,
        f: c,
      });
    });
    return config;
  }
}, (error) => {
  Promise.reject(error);
});

instance.interceptors.response.use((response) => {
  // 在一个ajax响应成功后再执行取消操作，把已完成的请求从pending中移除
  removePending(response.config);
  return response;
}, (error) => {
  // 出现接口异常或者超时时的判断
  if ((error.message && error.message.indexOf('timeout') >= 0) || (error.request && error.request.status !== 200)) {
    for (let p in pending) {
      if (pending[p].u === cancelConfig.url + '&' + cancelConfig.method + '&' + JSON.stringify(cancelConfig.params)) {
        pending.splice(p, 1);// 移除记录
      }
    }
    // 优先返回后台返回的错误信息，其次是接口返回
    return error.response || error;
  } else if (axios.Cancel) {
    // 如果是pengding状态，弹出提示！
    return {
      data: { message: '接口请求中！请稍后……' },
    };
  } else {
    return error;
  }
});

const api = {
  instance: instance,
  error: {
    '-1': function(res) {
      const data = res.data.data;
      if (data.enableSSO && data.SSOURL) {
        return window.location.replace(data.SSOURL);
      }
      router.push('/login');
      throw new Error('您尚未登录，请先登录!');
    },
  },
  constructionOfResponse: {
    codePath: 'status',
    successCode: '0',
    messagePath: 'message',
    resultPath: 'data',
  },
};

const getData = function(data) {
  let _arr = ['codePath', 'messagePath', 'resultPath'];
  let res = {};
  _arr.forEach((item) => {
    let pathArray = api.constructionOfResponse[item].split('.');
    let result = pathArray.length === 1 && pathArray[0] === '*' ? data : data[pathArray[0]];
    for (let j = 1; j < pathArray.length; j++) {
      result = result[pathArray[j]];
      if (!result) {
        if (j < pathArray.length - 1) {
          console.error(`【FEX】ConstructionOfResponse配置错误：${item}拿到的值是undefined，请检查配置`);
        }
        break;
      }
    }
    res[item] = result;
  });
  return res;
};

const success = function(response) {
  if (util.isNull(api.constructionOfResponse.codePath) || util.isNull(api.constructionOfResponse.successCode) ||
        util.isNull(api.constructionOfResponse.messagePath) || util.isNull(api.constructionOfResponse.resultPath)) {
    console.error('【FEX】Api配置错误: 请调用setConstructionOfResponse来设置API的响应结构');
    return;
  }
  let data;
  if (response) {
    if (util.isString(response.data)) {
      data = JSON.parse(response.data);
    } else if (util.isObject(response.data)) {
      data = response.data;
    } else {
      throw new Error('后台接口异常，请联系开发处理！');
    }
    let res = getData(data);
    let code = res.codePath;
    let message = res.messagePath;
    let result = res.resultPath;
    if (code != api.constructionOfResponse.successCode) {
      if (api.error[code]) {
        api.error[code](response);
        throw new Error('');
      } else {
        throw new Error(message || '后台接口异常，请联系开发处理！');
      }
    }
    return result || {};
  }
};

const fail = function(error) {
  let _message = '';
  let response = error.response;
  if (response && api.error[response.status]) {
    api.error[response.status].forEach((fn) => fn(response));
  } else {
    _message = '后台接口异常，请联系开发处理！';
    if (response && response.data) {
      let data;
      if (util.isString(response.data)) {
        data = JSON.parse(response.data);
      } else if (util.isObject(response.data)) {
        data = response.data;
      }
      if (data) {
        let res = getData(data);
        _message = res.messagePath;
      }
    }
  }
  error.message = _message;
  throw error;
};

const param = function(url, data, option) {
  let method = 'post';
  if (util.isNull(url)) {
    return console.error('请传入URL');
  } else if (!util.isNull(url) && util.isNull(data) && util.isNull(option)) {
    option = {
      method: method,
    };
  } else if (!util.isNull(url) && !util.isNull(data) && util.isNull(option)) {
    option = {
      method: method,
    };
    if (util.isString(data)) {
      option.method = data;
    } else if (util.isObject(data)) {
      option.data = data;
    }
  } else if (!util.isNull(url) && !util.isNull(data) && !util.isNull(option)) {
    if (!util.isObject(data)) {
      data = {};
    }
    if (util.isString(option)) {
      option = {
        method: option,
      };
    } else if (util.isObject(option)) {
      option.method = option.method || method;
    } else {
      option = {
        method: method,
      };
    }
    if (option.method == 'get' || option.method == 'delete' || option.method == 'head' || option.method == 'options') {
      option.params = data;
    }
    if (option.method == 'post' || option.method == 'put' || option.method == 'patch') {
      option.data = data;
    }
  }
  option.url = url;

  return instance.request(option);
};

const action = function(url, data, option) {
  return param(url, data, option)
    .then(success, fail)
    .then(function(response) {
      return response;
    })
    .catch(function(error) {
      error.message && Message.error(error.message);
      throw error;
    });
};

api.fetch = action;

api.option = function(option) {
  if (option.root) {
    instance.defaults.baseURL = option.root;
  }
  if (option.timeout && util.isNumber(option.timeout)) {
    instance.defaults.timeout = option.timeout;
  }
  if (option.config && util.isObject(option.config)) {
    Object.keys(option.config).forEach(function(key) {
      instance.defaults[key] = option.config[key];
    });
  }
};

api.setError = function(option) {
  if (option && util.isObject(option)) {
    util.merge(api.error, option);
  }
};

api.setResponse = function(constructionOfResponse) {
  this.constructionOfResponse = constructionOfResponse;
};

export default api;
