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

import Vue from 'vue';
import contentMenu from './contentMenu.vue';

export default function ContentMenu(option) {
  if (option.autoClose === undefined) {
    option.autoClose = true;
  }
  // 初始化
  let $swap = document.body;
  let $component = new Vue({
    components: {
      'contentMenu': contentMenu
    },
    data() {
      return option
    },
    methods: {
      chooseMenu(...arg) {
        if (option.choose) {
          option.choose(...arg)
        }
      },
      close() {
        if (option.autoClose) {
          this.$destroy();
          this.$el.remove();
        }
      }
    },
    template: '<contentMenu :data="data" :left="left" :top="top" @choose="chooseMenu" @close="close"></contentMenu>'
  });

  $component.$mount();
  $swap.appendChild($component.$el);

  return {
    destroy() {
      $component.$destroy();
      $component.$el.remove();
      $component = null;
    }
  }
}
