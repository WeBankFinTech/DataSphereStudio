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

import moment from 'moment';

const convertList = {
  lifecycle: [{
    value: 0,
    label: '永久',
  }, {
    value: 1,
    label: '当天有效',
  }, {
    value: 2,
    label: '一周有效',
  }, {
    value: 3,
    label: '一月有效',
  }, {
    value: 4,
    label: '半年有效',
  }],
  modelLevel: [{
    value: 0,
    label: 'ODS(原始数据层)',
  }, {
    value: 1,
    label: 'DWD(明细数据层)',
  }, {
    value: 2,
    label: 'DWS(汇总数据层)',
  }, {
    value: 3,
    label: 'ADS(应用数据层)',
  }],
  useWay: [{
    value: 0,
    label: '一次写多次读',
  }, {
    value: 1,
    label: '增删改查',
  }, {
    value: 2,
    label: '多次覆盖写',
  }, {
    value: 3,
    label: '一次写偶尔读',
  }],
};

/**
 * 格式化值
 * @param {*} item
 * @param {*} field
 * @return {*} return
 */
function formatValue(item, field) {
  const value = item[field.key];
  let formatted = value;
  switch (field.type) {
    case 'boolean':
      formatted = value ? '是' : '否';
      break;
    case 'timestramp':
      formatted = value == '0' || !value ? 0 : moment.unix(value).format('YYYY-MM-DD HH:mm:ss');
      break;
    case 'convert':
      if (!item[field.key] && item[field.key] !== 0) {
        return value;
      }
      formatted = convertList[field.key][item[field.key]].label;
      break;
  }
  return formatted;
}

export default {
  formatValue,
};
