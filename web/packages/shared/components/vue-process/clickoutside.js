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
* 从jquery扣过来的，递归去算
*
* @param {Element} a
* @param {Element} b
* @returns {boolean}
*/
let contains = function (a, b) {
  var adown = a.nodeType === 9 ? a.documentElement : a;
  var bup = b && b.parentNode;
  return a === bup || !!(bup && bup.nodeType === 1 && adown.contains(bup));
}
let cache = {};
let key = 1;
export default {
  inserted(el, binding) {
    el.outsideKey = key++;
    let self = {}
    self.documentHandler = (e) => {
      if (contains(el, e.target) || el === e.target) {
        return false;
      }
      if (binding.value) {
        binding.value(e);
      }
    };
    cache[el.outsideKey] = self;
    document.addEventListener('click', self.documentHandler);
  },
  unbind(el) {
    let self = cache[el.outsideKey];
    document.removeEventListener('click', self.documentHandler);
    delete cache[el.outsideKey];
  }
};
