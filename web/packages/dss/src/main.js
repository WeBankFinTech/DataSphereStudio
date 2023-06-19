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

import Vue from 'vue'
import iView from 'iview'
import VueRouter from 'vue-router'

import { apps, i18nConfig } from './dynamic-apps'
import i18n from '@dataspherestudio/shared/common/i18n'

i18n.mergeLocaleMessage(Vue.config.lang, i18nConfig[Vue.config.lang])

import component from '@dataspherestudio/shared/components'
import App from '../view/app.vue'
import router from './router'

import mixinDispatch from '@dataspherestudio/shared/common/service/moduleMixin'
import plugin from '@dataspherestudio/shared/common/util/plugin'

// 导入各模块的公共路径常量
import API_PATH from '@dataspherestudio/shared/common/config/apiPath.js'
import 'iview/dist/styles/iview.css'
import '@dataspherestudio/shared/common/style/theme/default.less'

// Icon
import SvgIcon from '@dataspherestudio/shared/components/svgIcon/index.vue'// svg component


// register globally
Vue.component('SvgIcon', SvgIcon)

if (apps.vuecomps) {
  Object.keys(apps.vuecomps).forEach(it => {
    if (apps.vuecomps[it].default && apps.vuecomps[it].default.install) {
      Vue.use(apps.vuecomps[it].default)
    } else {
      Vue.component(it, apps.vuecomps[it].default)
    }
  })
}

import('@dataspherestudio/shared/components/svgIcon/index.js')

import '../module/index.js'

// 扩展模块
if (apps.exts) {
  plugin.init(apps.exts)
}

// moduleMixin
if (apps.requireComponent) {
  apps.requireComponent.forEach(item=>{
    mixinDispatch(item)
  })
}
if (apps.requireComponentVue) {
  apps.requireComponentVue.forEach(item=>{
    mixinDispatch(undefined, item)
  })
}

Vue.use(VueRouter)
Vue.use(component)

Vue.use(iView, {
  i18n: (key, value) => i18n.t(key, value)
})

Vue.config.productionTip = false
Vue.prototype.$Message.config({
  duration: 3
})
Vue.prototype.$Notice.config({
  duration: 4
})

// 全局变量
Vue.prototype.$API_PATH = API_PATH;
Vue.prototype.$APP_CONF = apps.conf || {};

new Vue({
  router: router(),
  i18n,
  render: (h) => h(App)
}).$mount('#app')

console.log(`当前环境:${process.env.NODE_ENV}`)

if (localStorage.getItem('theme') === 'dark') {
  window.document.documentElement.setAttribute('data-theme', 'dark');
} else {
  window.document.documentElement.setAttribute('data-theme', '');
}

