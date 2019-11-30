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

import i18n from '@/js/i18n'

const CREATE_TABLE_FIELDS_CONFIG = [
  {
    key: 'name',
    name: i18n.t('message.createTable.fieldsName'),
    inputType: 'input',
    // 是否必填
    require: true,
    // 表单项验证规则
    rules: [
      {
        required: true,
        message: i18n.t('message.createTable.fieldsNameRequire'),
      }, {
        pattern: /^[a-zA-Z0-9_]+$/,
        message: i18n.t('message.createTable.fieldsNameRule'),
      },
    ],
    width: '150px',
  },
  {
    key: 'primary',
    name: i18n.t('message.createTable.isPrimary'),
    inputType: 'select',
    require: true,
    opt: [
      { label: 'yes', value: 1 },
      { label: 'no', value: 0 },
    ],
    rules: [],
    width: '80px',
  },
  {
    key: 'type',
    name: i18n.t('message.createTable.fieldsType'),
    inputType: 'select',
    require: true,
    opt: [
      { value: 'string', label: 'string' },
      { value: 'tinyint', label: 'tinyint' },
      { value: 'smallint', label: 'smallint' },
      { value: 'int', label: 'int' },
      { value: 'bigint', label: 'bigint' },
      { value: 'boolean', label: 'boolean' },
      { value: 'float', label: 'float' },
      { value: 'double', label: 'double' },
      { value: 'decimal', label: 'decimal' },
      { value: 'timestamp', label: 'timestamp' },
      { value: 'date', label: 'date' },
      { value: 'char', label: 'char' },
      { value: 'varchar', label: 'varchar' },
    ],
    rules: [],
    width: '100px',
  },
  {
    key: 'length',
    name: i18n.t('message.createTable.length'),
    inputType: 'input',
    rules: [],
    width: '100px',
  },
  {
    key: 'alias',
    name: i18n.t('message.createTable.fieldsAlias'),
    inputType: 'input',
    rules: [],
    width: '150px',
  },
  {
    key: 'rule',
    name: i18n.t('message.createTable.rule'),
    inputType: 'input',
    rules: [],
    width: '150px',
  },
  {
    key: 'comment',
    name: i18n.t('message.createTable.comment'),
    inputType: 'input',
    rules: [],
    width: '210px',
  },
];

const CREATE_TABLE_PARTITIONS_CONFIG = [
  {
    key: 'name',
    name: i18n.t('message.createTable.partitionAttr'),
    inputType: 'string',
    width: '140px',
  },
  {
    key: 'type',
    name: i18n.t('message.createTable.partitionType'),
    inputType: 'string',
    width: '120px',
  },
  {
    key: 'length',
    name: i18n.t('message.createTable.length'),
    inputType: 'input',
    rules: [],
    width: '120px',
  },
  {
    key: 'alias',
    name: i18n.t('message.createTable.partitionAlias'),
    inputType: 'input',
    rules: [],
    width: '200px',
  },
  {
    key: 'comment',
    name: i18n.t('message.createTable.comment'),
    inputType: 'input',
    rules: [],
    width: '440px',
  },
];

const IMPORT_TABLE_FIELDS_CONFIG = [
  {
    title: i18n.t('message.createTable.fieldsName'),
    key: 'name',
    width: '15%',
  }, {
    title: i18n.t('message.createTable.isPrimary'),
    key: 'primary',
    type: 'boolean',
    width: '10%',
    minWidth: '70px',
    inputType: 'select',
    opt: [
      { label: 'yes', value: 1 },
      { label: 'no', value: 0 },
    ],
  }, {
    title: i18n.t('message.createTable.fieldsType'),
    key: 'type',
    width: '10%',
    minWidth: '70px',
    inputType: 'select',
    opt: [
      { value: 'string', label: 'string' },
      { value: 'tinyint', label: 'tinyint' },
      { value: 'smallint', label: 'smallint' },
      { value: 'int', label: 'int' },
      { value: 'bigint', label: 'bigint' },
      { value: 'boolean', label: 'boolean' },
      { value: 'float', label: 'float' },
      { value: 'double', label: 'double' },
      { value: 'decimal', label: 'decimal' },
      { value: 'timestamp', label: 'timestamp' },
      { value: 'date', label: 'date' },
      { value: 'char', label: 'char' },
      { value: 'varchar', label: 'varchar' },
    ],
  }, {
    key: 'length',
    title: i18n.t('message.createTable.length'),
    inputType: 'input',
    width: '10%',
  }, {
    title: i18n.t('message.createTable.alias'),
    key: 'alias',
    width: '10%',
    inputType: 'input',
  }, {
    title: i18n.t('message.createTable.sourceFields'),
    key: 'sourceFields',
    width: '15%',
    minWidth: '166px',
    inputType: 'tag',
    // 是否支持多选
    multiple: 'multiple',
    // 是否多行显示
    wrap: true,
    opt: [],
  }, {
    title: i18n.t('message.createTable.rule'),
    key: 'rule',
    width: '15%',
    minWidth: '70px',
    inputType: 'input',
  }, {
    title: i18n.t('message.createTable.express'),
    key: 'express',
    width: '15%',
    minWidth: '70px',
    inputType: 'input',
  }, {
    title: i18n.t('message.createTable.comment'),
    key: 'comment',
    width: '15%',
    inputType: 'input',
  },
];

const IMPORT_TABLE_FIELDS_CSV_AND_XLSX_CONFIG = [
  {
    title: i18n.t('message.createTable.fieldsName'),
    key: 'name',
    width: '20%',
    inputType: 'input',
  }, {
    title: i18n.t('message.createTable.isPrimary'),
    key: 'primary',
    type: 'boolean',
    width: '10%',
    minWidth: '70px',
    inputType: 'select',
    opt: [
      { label: 'yes', value: 1 },
      { label: 'no', value: 0 },
    ],
  }, {
    title: i18n.t('message.createTable.fieldsType'),
    key: 'type',
    width: '15%',
    minWidth: '70px',
    inputType: 'select',
    opt: [
      { value: 'string', label: 'string' },
      { value: 'tinyint', label: 'tinyint' },
      { value: 'smallint', label: 'smallint' },
      { value: 'int', label: 'int' },
      { value: 'bigint', label: 'bigint' },
      { value: 'boolean', label: 'boolean' },
      { value: 'float', label: 'float' },
      { value: 'double', label: 'double' },
      { value: 'decimal', label: 'decimal' },
      { value: 'timestamp', label: 'timestamp' },
      { value: 'date', label: 'date' },
      { value: 'char', label: 'char' },
      { value: 'varchar', label: 'varchar' },
    ],
  }, {
    key: 'length',
    title: i18n.t('message.createTable.length'),
    inputType: 'input',
    width: '10%',
  }, {
    title: i18n.t('message.createTable.alias'),
    key: 'alias',
    width: '15%',
    inputType: 'input',
  }, {
    title: i18n.t('message.createTable.comment'),
    key: 'comment',
    width: '30%',
    inputType: 'input',
  },
];

const IMPORT_TABLE_PARTITIONS_CONFIG = [
  {
    title: i18n.t('message.createTable.partitionAttr'),
    key: 'name',
    width: '15%',
    minWidth: '70px',
  }, {
    title: i18n.t('message.createTable.partitionValue'),
    key: 'partitionsValue',
    width: '15%',
    minWidth: '70px',
    inputType: 'input',
  }, {
    title: i18n.t('message.createTable.partitionType'),
    key: 'type',
    width: '15%',
    minWidth: '70px',
  }, {
    title: i18n.t('message.createTable.length'),
    key: 'length',
    width: '15%',
    minWidth: '70px',
  }, {
    title: i18n.t('message.createTable.partitionAlias'),
    key: 'alias',
    width: '15%',
    minWidth: '70px',
    inputType: 'input',
  }, {
    title: i18n.t('message.createTable.comment'),
    key: 'comment',
    width: '25%',
    minWidth: '70px',
    inputType: 'input',
  },
];

export default {
  createTableFieldsConfig: CREATE_TABLE_FIELDS_CONFIG,
  createTablePartitionsConfig: CREATE_TABLE_PARTITIONS_CONFIG,
  importTableFieldsConfig: IMPORT_TABLE_FIELDS_CONFIG,
  importTableFieldsCsvAndXlsxConfig: IMPORT_TABLE_FIELDS_CSV_AND_XLSX_CONFIG,
  importTablePartitionsConfig: IMPORT_TABLE_PARTITIONS_CONFIG,
}
;
