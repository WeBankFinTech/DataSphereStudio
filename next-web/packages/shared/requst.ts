import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import axios from 'axios';

const pending: { u: string; c: AbortController }[] = [];
const removePending = (config: AxiosRequestConfig) => {
  for (let p = 0; p < pending.length; p++) {
    const key = getReqestKey(config);
    // 如果存在则执行取消操作
    if (pending[p].u === key) {
      pending[p].c.abort(); //执行取消操作
      pending.splice(p, 1); // 移除记录
    }
  }
};

function getReqestKey(config: AxiosRequestConfig) {
  const params = JSON.stringify(config.params);
  return config.url + '&' + config.method + '&' + params;
}

function cutReq(config: AxiosRequestConfig) {
  for (let p = 0; p < pending.length; p++) {
    if (pending[p].u === getReqestKey(config)) {
      return true;
    }
  }
}

const instance = axios.create({
  baseURL:
    import.meta.env.VITE_REQUEST_PREFIX ||
    `${location.protocol}//${window.location.host}`,
  timeout: 600000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
});

// request拦截器
instance.interceptors.request.use(
  (config) => {
    const flag = cutReq(config);
    if (flag === true) {
      // 取消第二次相同请求
      removePending(config);
      return config;
    }
    const abortController = new AbortController();
    const key = getReqestKey(config);
    pending.push({ u: key, c: abortController });
    config.signal = abortController.signal;
    return config;
  },
  (err) => {
    return Promise.reject(err);
  }
);
// response拦截器
instance.interceptors.response.use(
  (response) => {
    removePending(response.config);
    return response;
  },
  (error) => {
    if (axios.isCancel(error)) {
      return {};
    }
    return Promise.reject(error);
  }
);

// 参数处理
const param = function (url: string, data?: object, option?: FetchOptions) {
  if (typeof option === 'string') {
    option = {
      method: option,
    };
  }
  if (option && option.method) {
    if (
      option.method == 'get' ||
      option.method == 'delete' ||
      option.method == 'head' ||
      option.method == 'options'
    ) {
      option.params = data;
    }
    if (
      option.method == 'post' ||
      option.method == 'put' ||
      option.method == 'patch'
    ) {
      option.data = data;
    }
  } else {
    option = {
      method: 'get',
      params: data,
    };
  }

  option.url = url;
  return instance.request(option);
};

// 请求返回数据处理
const success = function (response: AxiosResponse) {
  let data;
  if (response) {
    if (response.status === 401 && api.error[response.status]) {
      api.error[response.status](response);
    }
    if (typeof response.data === 'string' && response.data) {
      try {
        data = JSON.parse(response.data);
      } catch (err) {
        throw new Error('接口返回数据格式错误');
      }
    } else if (response.data) {
      data = response.data;
    }
  } else {
    throw new Error('接口返回数据格式错误');
  }
  if (data && data.status !== 0) {
    if (api.error[data.status]) {
      api.error[data.status](response);
      throw new Error('');
    } else {
      throw new Error(data.message || '');
    }
  }
  return data || {};
};

// 错误处理
const fail = function (error: { response: any; code?: any }) {
  const response = error.response;
  if (error?.code == 'ERR_CANCELED') return;
  if (response && api.error[response.status]) {
    api.error[response.status](response);
  } else {
    if (response && response.data) {
      //
    }
  }
  throw error;
};

/**
 * 请求方法
 * @param {*} url 接口路径
 * @param {*} data 请求参数
 * @param {*} option 配置
 * @returns promise
 * usages：
 * fetch('https://dss-open.wedatasphere.com/api/rest_j/v1/user/publicKey')
 * fetch('https://dss-open.wedatasphere.com/api/rest_j/v1/user/publicKey', {}, 'get')
 * fetch('/user/XX', {}, 'post')
 * fetch('/user/XX', {}, {axios config options})
 */
function fetch(url: string, data?: object, option?: FetchOptions) {
  return param(url, data, option)
    .then(success, fail)
    .then(function (response) {
      return response;
    })
    .catch(function (error) {
      api.showErrorMsg(error);
      throw error;
    });
}

type FetchOptions = string | AxiosRequestConfig;

/**
 * 定义接口返回的固定格式
 */
export interface ResponseResult<T = any> {
  status: number;
  method: string;
  message: string;
  data: T;
}

export type ApiRequest = {
  instance: AxiosInstance;
  setError: (option: any) => void;
  fetch: (
    url: string,
    data?: any,
    option?: FetchOptions
  ) => Promise<ResponseResult<any>>;
  error: Record<string, (res: any) => void>;
  showErrorMsg: (error: Error) => void;
};

const api: ApiRequest = {
  instance: instance,
  error: {
    '-1': function (res) {
      if (res.data && res.data.enableSSO && res.data.SSOURL) {
        return window.location.replace(res.data.SSOURL);
      }
      if (window.location.href.indexOf('/#/login') < 0) {
        if (self != top) {
          window.parent.location.href = '/#/login';
        } else {
          window.location.href = '/#/login';
        }
      }
    },
    '401': function () {
      if (window.location.href.indexOf('/#/login') < 0) {
        if (self != top) {
          window.parent.location.href = '/#/login';
        } else {
          window.location.href = '/#/login';
        }
      }
    },
  },
  setError: function ({ error, showErrorMsg }) {
    if (error && typeof error === 'object') {
      Object.assign(api.error, error);
    }
    if (showErrorMsg) {
      api.showErrorMsg = showErrorMsg;
    }
  },
  showErrorMsg: function (err) {
    // empty
  },
  fetch,
};

export default api;
