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

import {isNil} from 'lodash';
import { Basic } from '@/common/service/db/index.js';
import { isArray } from 'util';
/**
 * @class Globalcache
 * @extends {Basic}
 */
class Project extends Basic {
  /**
     *Creates an instance of Globalcache.
     * @param {*} table
     * @param {*} db
     * @memberof Globalcache
     */
  constructor(table) {
    super(table);
  }

  /**
     * @param {*} projectID
     * @param {*} key
     * @return {*}
     */
  async getProjectCache({projectID, key}) {
    projectID = Number(projectID);
    let caches = await this.get(projectID);
    if (key) {
      return caches[0][key];
    }
    return caches[0];
  }

  /**
     * @param {*} projectID
     * @param {*} value
     * @return {*}
     */
  async addProjectCache({projectID, value}) {
    projectID = Number(projectID);
    let cache = await this.getProjectCache({projectID});
    if (!isNil(cache)) {
      return this.update(projectID, value)
    }
    return this.add(Object.assign({projectID}, value));
  }


  /**
     * @param {*} projectID
     * @param {*} key
     * @param {*} value token是用于第二层比对的字段，sKey是实际写入的key，sValue是用于比对的值
     * @param {*} isDeep 是否要对第二层数据进行修改，如果需要的话，就需要在value中携带
     * @return {*}
     */
  async updateProjectCache({projectID, key, value, isDeep}) {
    projectID = Number(projectID);
    let cache = {};
    if (key) {
      if (isDeep) {
        cache = await this.getProjectCache({projectID}) || {};
        let arr = cache && cache[key] || [];
        arr.forEach((item) => {
          if (item[value.token] === Number(value.sValue)) {
            item[value.sKey] = value[value.sKey];
          }
        })
      } else {
        if (isArray(key)) {
          key.forEach((k) => {
            cache[k] = value[k];
          })
        } else {
          cache[key] = value[key];
        }
      }
    } else {
      cache = value;
    }
    return this.update(projectID, cache);
  }


  /**
     * @param {*} args
     * @return {*}
     */
  async removeProjectCache({projectID, key}) {
    projectID = Number(projectID);
    let cache = await this.getProjectCache({projectID});
    if (isNil(cache)) {
      if (key) {
        cache[key] = null;
        return this.update(projectID, cache);
      }
      return this.remove(projectID);
    }
  }
}
const project = new Project('project');

export default project;
