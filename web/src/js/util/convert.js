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

/*eslint-disable */
import i18n from '@/js/i18n';
/**
 * 转换日志
 *
 * @return {Object}
 */
export function convertLog(logs) {
    let logMap = {
        all: '',
        error: '',
        warning: '',
        info: '',
    };
    let newMap = {};
    if (typeof logs === 'string') {
        newMap = {
            all: logs,
        };
    } else if (Array.isArray(logs)) {
        let keysArr = ['error', 'warning', 'info', 'all'];
        logs.forEach((log, index) => {
            newMap[keysArr[index]] = log;
        });
    } else if (_.isPlainObject(logs)) {
        newMap = logs;
    }

    return Object.assign(logMap, newMap);
}

/**
 * 转换时间戳差值
 * @param {*} runningTime
 * @return {*}
 */
export function convertTimestamp(runningTime) {
    // const time = Math.floor(runningTime / 1000);
    const time = (runningTime / 1000).toFixed(1);
    if (time <= 0) {
        return `0${i18n.t('message.constants.time.second')}`;
    } else if (time < 60) {
        return `${time}${i18n.t('message.constants.time.second')}`;
    } else if (time < 3600) {
        return `${(time / 60).toPrecision(2)}${i18n.t('message.constants.time.minute')}`;
    } else if (time < 86400) {
        return `${(time / 3600).toPrecision(2)}${i18n.t('message.constants.time.hour')}`;
    }
    return `${(time / 86400).toPrecision(2)}${i18n.t('message.constants.time.day')}`;
}

/**
 * 排序
 * @param {*} a 第一个参数
 * @param {*} b 第两个参数
 * @param {*} type 类型，可能是desc和asc
 */
export function sort(a, b, type) {
    const sortString = (a, b, type) => {
        for (let i = 0; i < a.length; i++) {
            if (a[i] !== b[i]) {
                const aAcsii = a.charCodeAt(i);
                const bAcsii = b.charCodeAt(i);
                const returnS = type === 'desc' ? bAcsii - aAcsii : aAcsii - bAcsii;
                return returnS;
            }
        }
    };
    const fa = parseInt(a, 10);
    const fb = parseInt(b, 10);
    if (!isNaN(fa) && !isNaN(fb)) {
        if (fa.toString().length === a.toString().length && fb.toString().length === b.toString().length) {
            return type === 'desc' ? b - a : a - b;
        } else if (!isNaN(Number(a)) && !isNaN(Number(b))) {
            return type === 'desc' ? b - a : a - b;
        } else {
            return sortString(a, b, type);
        }
    } else {
        return sortString(a, b, type);
    }
}

/**
 * 转换数组为对象key:value形式
 * @param {*} arr
 * @return {*}
 */
export function convertArrayToObject(arr) {
    const obj = {};
    _.forEach(arr, (item) => {
        obj[item.key] = item.value;
    });
    return obj;
}

/**
 * 转换对象为数组形式
 * @param {*} obj
 */
export function convertObjectToArray(obj) {
    const arr = [];
    _.forIn(obj, (value, key) => {
        arr.push({
            key,
            value,
        });
    });
    return arr;
}

/**
 * 转换数组为数组[{key1:value1},{key2,value2}]形式
 * @param {*} arr
 * @return {*}
 */
export function convertArrayToMap(arr) {
    const tmp = [];
    _.forEach(arr, (item) => {
        const obj = {};
        obj[item.key] = item.value;
        tmp.push(obj);
    });
    return tmp;
}
