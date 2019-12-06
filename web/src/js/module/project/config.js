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

import i18n from '@/js/i18n';
import sender from './images/systemIcon/sender.svg';

const SYSTEM_MAP = [
  {
    name: 'Exchangis',
    font: 'fi-exchange',
    subName: i18n.t('message.project.exchangisSubTitle'),
    color: '#66CC99',
    path: 'exchangis'
  },
  {
    name: 'Scriptis',
    font: 'fi-scriptis',
    subName: i18n.t('message.project.scriptisSubTitle'),
    color: '#6666FF',
    iconSize: '28px',
    path: 'linkis'
  },
  {
    name: 'Visualis',
    font: 'fi-visualis',
    subName: i18n.t('message.project.visualisSubTitle'),
    color: '#0099FF',
    iconSize: '34px',
    path: 'visualis'
  },
  {
    name: 'Qualitis',
    font: 'fi-qualitis',
    subName: i18n.t('message.project.qualitisSubTitle'),
    color: '#339999',
    path: 'qualitis'
  },
  {
    name: 'Schedulis',
    font: 'fi-schedule',
    subName: i18n.t('message.project.schedulisSubTitle'),
    color: '#6666CC',
    path: 'schedulis'
  },
];

const SETTING = {
  size: 3,
}

export default {
  systemMap: SYSTEM_MAP,
  setting: SETTING,
};
