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

// 特别注意，使用require.context会导致模块打包到bundle，失去按需加载的优势。
import moduleMixin from './moduleMixin';
let modules = {};
const requireComponent = require.context(
  // 其组件目录的相对路径
  './',
  // 是否查询其子目录
  true,
  /([a-z|A-Z])+\/index\.js$/
);

requireComponent.keys().forEach((fileName) => {
  // 获取组件配置
  let moduleConfig = requireComponent(fileName);
  moduleConfig = moduleConfig.default || moduleConfig;
  moduleConfig.mixin = moduleMixin(moduleConfig);
  modules[fileName] = moduleConfig;
});

const requireComponentVue = require.context(
  // 其组件目录的相对路径
  './',
  // 是否查询其子目录
  true,
  /([a-z|A-Z])+.vue$/
);

requireComponentVue.keys().forEach((fileName) => {
  // 获取组件配置
  let vueConfig = requireComponentVue(fileName);
  vueConfig = vueConfig.default || vueConfig;
  let indexPath = './' + fileName.split('/')[1] + '/index.js';
  let moduleConfig = modules[indexPath];
  let mixin = moduleConfig && moduleConfig.mixin;
  if (!vueConfig.mixins) {
    vueConfig.mixins = [mixin];
  } else {
    if (vueConfig.mixins.indexOf(mixin) == -1) {
      vueConfig.mixins.push(mixin);
    }
  }
});

modules = null;
