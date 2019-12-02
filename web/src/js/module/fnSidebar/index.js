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

import api from '@/js/service/api';
export default {
  name: 'fnSidebar',
  events: ['getAllLoadedFunction'],
  dispatchs: ['WorkSidebar:showTree', 'Workbench:pasteInEditor', 'IndexedDB:updateGlobalCache', 'Workbench:getWorksLangList'],
  data: {},
  methods: {
    getAllLoadedFunction(cb) {
      api.fetch(`/udf/all`).then((rst) => {
        cb({
          list: rst.udfTree.udfInfos,
          isError: false,
        });
      }).catch((err) => {
        cb({
          list: [],
          isError: true,
        });
      });
    },
  },
  component: () => import('./fnSidebar.vue'),
};
