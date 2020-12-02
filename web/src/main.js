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
import './js/module'
import App from './js/view/app.vue'
import router from './js/service/router'
import component from './js/component'
import mixin from '../src/js/service/mixin'
import i18n from './js/i18n'
import Bus from '../src/js/service/bus';
import store from '../src/js/store';

import 'iview/dist/styles/iview.css'

Vue.use(VueRouter)
Vue.use(component)
Vue.mixin(mixin);
Vue.config.productionTip = false
Vue.prototype.$bus = Bus;

Vue.use(iView, {
  i18n: (key, value) => i18n.t(key, value)
})
Vue.prototype.$Message.config({
  duration: 3
})

new Vue({
  router,
  store,
  i18n,
  render: (h) => h(App)
}).$mount('#app')
console.log(`当前环境:${process.env.NODE_ENV}`)