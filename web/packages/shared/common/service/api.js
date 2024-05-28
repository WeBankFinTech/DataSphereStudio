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
import util from '@dataspherestudio/shared/common/util';
import axios from 'axios';
import {
  Message,
  Notice
} from 'iview';
import cache from './apiCache';
import qs from './querystring'
import storage from "./storage"
import i18n from '../i18n'

// 什么一个数组用于存储每个请求的取消函数和标识
let pending = [];
let cancelConfig = null;
const abortController = new AbortController();
let removePending = (config) => {
  for (let p = 0; p < pending.length; p++) {
    const params = JSON.stringify(config.params);
    // 如果存在则执行取消操作
    if (pending[p].u === config.url + '&' + config.method + '&' + params) {
      // pending[p]['controller'].abort();// 执行取消操作
      pending.splice(p, 1); // 移除记录
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

let getErrorMsg = (message) => {
  let msg;
  if (message === 'Request failed with status code 414') {
    msg = 'URL长度超出最大限制';
  } else if (message === 'Request failed with status code 413') {
    msg = '请求体内容超出最大限制';
  }
  return msg;
}

const instance = axios.create({
  baseURL: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
  timeout: 600000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  },
});

instance.interceptors.request.use((config) => {
  // 增加国际化参数
  config.headers['Content-language'] = localStorage.getItem('locale') || 'zh-CN';
  config.metadata = {
    startTime: Date.now()
  }
  if (/\/application\//.test(config.url)) {
    config.url = `http://${window.location.host}` + config.url
  }

  // 增加token
  if (/dolphinscheduler/.test(config.url)) {
    config.headers['token'] = api.getToken()
    config.url = `http://${window.location.host}/` + config.url
  }

  if (config.useForm) {
    let formData = new FormData()
    Object.keys(config.data).forEach(key => {
      formData.append(key, config.data[key])
    })
    config.data = formData
  }
  // fallback application/json to application/x-www-form-urlencoded
  if (config.useFormQuery) {
    config.headers['Content-Type'] = 'application/x-www-form-urlencoded'
    config.data = qs(config.data)
  }

  let flag = cutReq(config);
  // 当上一次相同请求未完成时，无法进行第二次相同请求
  if (flag === true) {
    removePending(config);
    return config;
  } else {
    const params = JSON.stringify(config.params);
    pending.push({
      u: config.url + '&' + config.method + '&' + params,
      controller: abortController,
    });
    // 用于正常请求出现错误时移除
    cancelConfig = config;
    config.signal = abortController.signal;
    return config;
  }
}, (error) => {
  Promise.reject(error);
});

instance.interceptors.response.use((response) => {
  response.config.metadata.endTime = Date.now()
  const duration = response.config.metadata.endTime - response.config.metadata.startTime
  if (window.$Wa && duration > 2000) window.$Wa.log(`接口耗时 ${duration}: ${response.config.url}`);
  // 在一个ajax响应成功后再执行取消操作，把已完成的请求从pending中移除
  removePending(response.config);
  return response;
}, (error) => {
  if (error.config) {
    error.config.metadata.endTime = Date.now()
    const duration = error.config.metadata.endTime - error.config.metadata.sartTime
    if (window.$Wa && duration > 2000) window.$Wa.log(`接口耗时 ${duration}: ${error.config.url}`);
  }
  // 出现接口异常或者超时时的判断
  if (error.code === 'ERR_BAD_REQUEST' && getErrorMsg(error.message)) {
    if (!error.response) error.response = {};
    error.response.data = {
      message: getErrorMsg(error.message),
      method: '',
      status: 1,
      data: {solution: null}
    }
    return error.response;
  } else if ((error.message && error.message.indexOf('timeout') >= 0) || (error.request && error.request.status !== 200)) {
    for (let p in pending) {
      if (pending[p].u === cancelConfig.url + '&' + cancelConfig.method + '&' + JSON.stringify(cancelConfig.params)) {
        pending.splice(p, 1); // 移除记录
      }
    }
    // 优先返回后台返回的错误信息，其次是接口返回
    return error.response || error;
  } else if (axios.isCancel(error)) {
    // 如果是pengding状态，弹出提示！
    return {
      // data: { message: '接口请求中！请稍后……' },
    };
  } else {
    return error;
  }
});
let showLoginTips = false
const api = {
  instance: instance,
  error: {
    '-1': function (res) {
      if (res.data && res.data.enableSSO && res.data.SSOURL) {
        return window.location.replace(res.data.SSOURL);
      }
      if (window.location.href.indexOf('/#/login') < 0) {
        window.location.href = '/#/login'
      }
      if (!showLoginTips && res.data.method.indexOf('jobhistory/governanceStationAdmin') < 0) {
        showLoginTips = true
        setTimeout(() => {
          showLoginTips = false
        }, 5000)
        throw new Error('您尚未登录，请先登录!');
      }
    },
  },
  constructionOfResponse: {
    codePath: 'status',
    successCode: '0',
    messagePath: 'message',
    resultPath: 'data',
  },
};

const getData = function (data) {
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
const API_ERR_MSG = 'API_ERR_MSG'
const success = function (response) {
  if (util.isNull(api.constructionOfResponse.codePath) || util.isNull(api.constructionOfResponse.successCode) ||
    util.isNull(api.constructionOfResponse.messagePath) || util.isNull(api.constructionOfResponse.resultPath)) {
    console.error('【FEX】Api配置错误: 请调用setConstructionOfResponse来设置API的响应结构');
    return;
  }
  let data;
  if (response) {
    if (response.status === 401 && !(response.data && response.data.status === -1)) {
      window.location.href = '/newhome'
      throw new Error('token失效，请重新进入之前页面!');
    }
    if (util.isString(response.data) && response.data) {
      try {
        data = JSON.parse(response.data);
      } catch (e) {
        console.log(e, response.data)
        throw new Error(API_ERR_MSG);
      }
    } else if (util.isObject(response.data)) {
      // 兼容ds blob流下载
      if (response.status === 200 && !response.data.data && !response.data.msg && response.data.code != api.constructionOfResponse.successCode) {
        data = {}
        data.data = response
        data.msg = 'success'
        data.code = api.constructionOfResponse.successCode
      } else {
        data = response.data
      }
    } else {
      throw new Error(API_ERR_MSG);
    }
    let res = getData(data);
    let code = res.codePath;
    let message = res.messagePath;
    let result = res.resultPath;
    const throwErr = function (msg) {
      const err = new Error(msg)
      if (result && result.solution) {
        err.solution = result.solution
      }
      err.response = response
      throw err;
    }
    // 兼容 dolphin 返回数据结构
    if (data.msg) {
      if (data.code != api.constructionOfResponse.successCode) {
        if (api.error[data.code]) {
          api.error[data.code](response);
          throwErr('');
        } else {
          throwErr(data.msg || API_ERR_MSG);
        }
      }
    } else {
      if (code != api.constructionOfResponse.successCode) {
        if (api.error[code]) {
          api.error[code](response);
          throwErr('');
        } else {
          throwErr(message || API_ERR_MSG);
        }
      }
    }
    if (result) {
      let len = 0
      let hasBigData = Object.values(result).some(item => {
        if (Array.isArray(item)) {
          len = item.length > len ? item.length : len
          return len > 200
        }
      })
      if (hasBigData) {
        console.log(response.data, '潜在性能问题大数据量', len)
        if (window.$Wa) window.$Wa.log(`大数据量接口(${len}):${response.config.url}`);
      }
    }

    return result || {};
  }
};

const fail = function (error) {
  let _message = '';
  let response = error.response;
  console.log('error', error);
  if (response && api.error[response.status]) {
    api.error[response.status].forEach((fn) => fn(response));
  } else {
    _message = API_ERR_MSG;
    if (response && response.data) {
      let data;
      if (util.isString(response.data) && response.data) {
        try {
          data = JSON.parse(response.data);
        } catch (e) {
          //
        }

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

const param = function (url, data, option) {
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
  // cacheOptions接口数据缓存 {time} time为0则请求之后缓存在内存里的数据不清理
  if (option.cacheOptions) {
    option.adapter = cache(option.cacheOptions)
  }
  option.url = url;

  return instance.request(option);
};

let showApiErrorTips = true
let lastMsg = ''
const showErrMsg = function (error) {
  let msg = error.message || error.msg
  if (lastMsg !== msg && msg) {
    lastMsg = msg
  } else {
    return
  }
  if (msg === API_ERR_MSG) {
    msg = i18n.t('message.common.apierrmsg')
  }
  let isHoverNotice = {}
  const checkPath = !error.response || error.response.config.url.indexOf('dss/guide/solution/reportProblem') < 0
  if (window.$APP_CONF && window.$APP_CONF.error_report && checkPath) {
    const noticeName = 'err_' + Date.now()
    isHoverNotice[noticeName] = false
    Notice.error({
      name: noticeName,
      duration: 6,
      closable: true,
      title: i18n.t('message.common.errTitle'), //isEn ? 'Error' : '错误提示',
      render: (h) => {
        return h('div', {
          class: 'g-err-msg-div',
          attrs: {
            'data-erritem': noticeName
          }
        }, [
          h('div', {
            style: {
              'word-break': 'break-all',
              'margin-bottom': '10px',
              'text-align': 'left',
              'line-height': '18px'
            }
          }, msg),
          h('button', {
            class: 'ivu-btn ivu-btn-default ivu-btn-small',
            style: {
              background: '#ec6565',
              color: '#fff',
              display: error.solution !== undefined ? 'inline-block' : 'none'
            },
            on: {
              click: () => {
                if (error.solution && error.solution.solutionUrl) {
                  window.open(error.solution.solutionUrl, '_blank')
                } else if (error.response) {
                  // 上报
                  let requestBody = error.response.config.data
                  try {
                    requestBody = typeof error.response.config.data === 'string' ? JSON.parse(error.response.config.data) : error.response.config.data
                  } catch (e) {
                    //
                  }
                  action('/dss/guide/solution/reportProblem', {
                    requestUrl: error.response.config.url,
                    queryParams: error.response.config.params,
                    requestBody,
                    requestHeaders: {
                      Cookie: document.cookie,
                      ...error.response.config.headers
                    },
                    responseBody: error.response.data
                  }).then(() => {
                    Message.success(i18n.t('message.common.errsubmited'))
                  })
                }
                Notice.close(noticeName)
              }
            }
          }, error.solution && error.solution.solutionUrl ? i18n.t('message.common.errsolution') : i18n.t('message.common.errsubmit'))
        ])
      },
      onClose: () => {
        return !isHoverNotice[noticeName]
      }
    })
    setTimeout(() => {
      document.querySelectorAll('.g-err-msg-div').forEach(ele => {
        const erritem =ele.dataset.erritem
        ele.parentElement.parentElement.style.textAlign = 'left'
        ele.parentElement.style.display = 'block'
        ele.parentElement.style.padding = 0
        if (ele.parentElement.parentElement.className.indexOf('ivu-notice-notice-err') < 0) {
          ele.parentElement.parentElement.className = ele.parentElement.parentElement.className + ' ivu-notice-notice-err'
        }
        ele.parentElement.parentElement.addEventListener('mouseover', () => {
          isHoverNotice[erritem] = true
        }, false)
      })
    }, 150)
  } else {
    Notice.error({
      desc: msg,
      title: i18n.t('message.common.errTitle') ,
      duration: 4
    });
  }
}
// 对于某些特殊接口，接口报错时不做展示
const isShowErr = (url) => {
  const noShowUrlList = ['/validator/code-precheck'];
  if (noShowUrlList.includes(url)) {
    return false;
  }
  return true;
}
const action = function (url, data, option) {
  return param(url, data, option)
    .then(success, fail)
    .then(function (response) {
      return response;
    })
    .catch(function (error) {
      if (error && error.response && error.response.data && error.response.data.data) {
        error.solution = error.response.data.data.solution
      }
      if (error.message === API_ERR_MSG || error.msg === API_ERR_MSG) {
        if (showApiErrorTips) {
          showApiErrorTips = false
          setTimeout(() => {
            showApiErrorTips = true
          }, 3000)
          if (isShowErr(url)) {
            showErrMsg(error)
          }
        }
      } else {
        if (isShowErr(url)) {
          showErrMsg(error)
        }
      }
      setTimeout(() => {
        lastMsg = ''
      }, 4000)
      throw error;
    });
};

api.fetch = action;

api.option = function (option) {
  if (option.root) {
    instance.defaults.baseURL = option.root;
  }
  if (option.timeout && util.isNumber(option.timeout)) {
    instance.defaults.timeout = option.timeout;
  }
  if (option.config && util.isObject(option.config)) {
    Object.keys(option.config).forEach(function (key) {
      instance.defaults[key] = option.config[key];
    });
  }
};

api.setError = function (option) {
  if (option && util.isObject(option)) {
    util.merge(api.error, option);
  }
};

api.setResponse = function (constructionOfResponse) {
  this.constructionOfResponse = constructionOfResponse;
};

api.getToken = function () {
  return storage.get("token", true);
}
export default api;
