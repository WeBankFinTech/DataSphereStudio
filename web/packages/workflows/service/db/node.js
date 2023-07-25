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
import { Basic } from '@dataspherestudio/shared/common/service/db/index.js';
import { isArray } from 'util';
/**
 * @class Globalcache
 * @extends {Basic}
 */
class Node extends Basic {
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
     * @param {*} nodeId
     * @param {*} key
     * @return {*}
     */
  async getNodeCache({nodeId, key}) {
    let caches = await this.get(nodeId);
    if (key && caches[0]) {
      return caches[0][key];
    }
    return caches && caches[0];
  }

  /**
     * @param {*} nodeId
     * @param {*} value
     * @return {*}
     */
  async addNodeCache({nodeId, value}) {
    let cache = await this.getNodeCache({nodeId});
    if (!isNil(cache)) {
      return this.update(nodeId, value)
    }
    return this.add(Object.assign({nodeId}, value));
  }


  /**
     * @param {*} nodeId
     * @param {*} key
     * @param {*} value
     * @return {*}
     */
  async updateNodeCache({nodeId, key, value}) {
    let cache = {};
    if (key) {
      if (isArray(key)) {
        key.forEach((k) => {
          cache[k] = value[k];
        })
      } else {
        cache[key] = value[key];
      }
    } else {
      cache = value;
    }
    return nodeId ? this.update(nodeId, cache) : null;
  }


  /**
     * @param {*} args
     * @return {*}
     */
  async removeNodeCache({nodeId, key}) {
    let cache = await this.getNodeCache({nodeId});
    if (isNil(cache)) {
      if (key) {
        cache[key] = null;
        return this.update(nodeId, cache);
      }
      return this.remove(nodeId);
    }
  }
}
const node = new Node('node');

export default node;
