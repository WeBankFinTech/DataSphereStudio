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

export default {
  name: 'Project',
  events: ['Project:loading', 'Project:getData'],
  dispatchs: {
    Project: ['copy', 'loading', 'copyReset', 'getData', 'updataPercent', 'removePercent'],
  },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
  },
  component: () => import('./index.vue'),
};
