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

import{ NODETYPE, NODEICON } from '@/apps/workflows/service/nodeType';
import i18n from '@/common/i18n'
export default [
  {
    title: i18n.t('message.workflow.shape.GNJD'),
    children: [
      {
        type: NODETYPE.FLOW,
        title: 'flow',
        image: NODEICON[NODETYPE.FLOW].icon,
        editParam: false,
        editBaseInfo: false,
      },
      {
        type: NODETYPE.PROJECTNODE,
        title: 'projectNode',
        image: NODEICON[NODETYPE.PROJECTNODE].icon,
        editParam: false,
        editBaseInfo: false,
      },
    ],
  }];
