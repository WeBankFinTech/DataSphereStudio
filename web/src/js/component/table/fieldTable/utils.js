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

/**
 * 格式化值
 * @param {*} item
 * @param {*} field
 * @return {*} return
 */
const formatValue = (item, field) => {
  let formatted = '';
  const value = item[field.key];
  if (!field.type) {
    return value;
  }
  if (field.linkfields) {
    if (field.type === 'total') {
      return item && item[field.linkfields] && item[field.linkfields].length || 0;
    }
  }
  switch (field.type) {
    case 'boolean':
      formatted = value ? '是' : '否';
      break;
    case 'timestramp':
      formatted = value == '0' ? 0 : moment.unix(value).format('YYYY-MM-DD HH:mm:ss');
      break;
  }
  return formatted;
}

export default {
  formatValue,
};
