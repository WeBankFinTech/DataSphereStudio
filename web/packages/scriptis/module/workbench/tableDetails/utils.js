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
import i18n from '@dataspherestudio/shared/common/i18n';
const convertList = {
  lifecycle: [{
    value: 0,
    label: i18n.t('message.scripts.Permanent'),
  }, {
    value: 1,
    label: i18n.t('message.scripts.validday'),
  }, {
    value: 2,
    label: i18n.t('message.scripts.validweek'),
  }, {
    value: 3,
    label: i18n.t('message.scripts.validmonth'),
  }, {
    value: 4,
    label: i18n.t('message.scripts.validhalf'),
  }],
  modelLevel: [{
    value: 0,
    label: `ODS(${i18n.t('message.scripts.datastore')})`,
  }, {
    value: 1,
    label: `DWD(${i18n.t('message.scripts.warehouse')})`,
  }, {
    value: 2,
    label: `DWS(${i18n.t('message.scripts.warehouseservice')})`,
  }, {
    value: 3,
    label: `ADS(${i18n.t('message.scripts.appservice')})`,
  }],
  useWay: [{
    value: 0,
    label: i18n.t('message.scripts.writemany'),
  }, {
    value: 1,
    label: i18n.t('message.scripts.curd'),
  }, {
    value: 2,
    label: i18n.t('message.scripts.multoverride'),
  }, {
    value: 3,
    label: i18n.t('message.scripts.once'),
  }],
};

/**
 * 格式化值
 * @param {*} item
 * @param {*} field
 * @return {*} return
 */
function formatValue(item, field) {
  let value = item[field.key];
  if (typeof item.modeInfo === 'string') {
    item.modeInfo = JSON.parse(item.modeInfo)
  }
  if (field.key === 'modeInfo.type') {
    value = item.modeInfo ? item.modeInfo.type === 'index' ? i18n.t('message.scripts.Metrics'): i18n.t('message.scripts.Dimensions') : ''
  }
  if (field.key === 'modeInfo.name') {
    value = item.modeInfo ? item.modeInfo.name : ''
  }
  let formatted = value;
  switch (field.type) {
    case 'boolean':
      formatted = value ? i18n.t('message.scripts.Yes') : i18n.t('message.scripts.No');
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
