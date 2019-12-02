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

import { isEmpty } from 'lodash';
import { getHiveList, getReturnList, getFormatProposalsList } from '../util';
import storage from '@/js/helper/storage';

const kewordInfoProposals = [
  {
    label: 'ADD',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ADD',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ADMIN',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ADMIN',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'AFTER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'AFTER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ANALYZE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ANALYZE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ASC',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ASC',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'BEFORE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'BEFORE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'BUCKET',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'BUCKET',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'BUCKETS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'BUCKETS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'CASCADE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'CASCADE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'CHANGE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'CHANGE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'CLUSTER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'CLUSTER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'CLUSTERED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'CLUSTERED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'CLUSTERSTATUS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'CLUSTERSTATUS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'COLLECTION',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'COLLECTION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'COLUMNS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'COLUMNS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'COMMENT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'COMMENT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'COMPACT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'COMPACT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'COMPACTIONS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'COMPACTIONS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'COMPUTE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'COMPUTE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'CONCATENATE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'CONCATENATE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'CONTINUE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'CONTINUE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DATA',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DATA',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DATABASES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DATABASES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DATETIME',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DATETIME',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DAY',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DAY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DBPROPERTIES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DBPROPERTIES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DEFERRED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DEFERRED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DEFINED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DEFINED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DELIMITED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DELIMITED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DEPENDENCY',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DEPENDENCY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DESC',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DESC',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DIRECTORIES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DIRECTORIES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DIRECTORY',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DIRECTORY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DISABLE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DISABLE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DISTRIBUTE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'DISTRIBUTE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ELEM_TYPE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ELEM_TYPE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ENABLE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ENABLE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ESCAPED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ESCAPED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'EXCLUSIVE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'EXCLUSIVE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'EXPLAIN',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'EXPLAIN',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'EXPORT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'EXPORT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'FIELDS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'FIELDS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'FILE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'FILE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'FILEFORMAT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'FILEFORMAT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'FIRST',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'FIRST',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'FORMAT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'FORMAT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'FORMATTED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'FORMATTED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'FUNCTIONS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'FUNCTIONS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'HOLD_DDLTIME',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'HOLD_DDLTIME',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'HOUR',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'HOUR',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'IDXPROPERTIES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'IDXPROPERTIES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'IGNORE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'IGNORE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'INDEX',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'INDEX',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'INDEXES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'INDEXES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'INPATH',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'INPATH',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'INPUTDRIVER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'INPUTDRIVER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'INPUTFORMAT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'INPUTFORMAT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ITEMS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ITEMS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'JAR',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'JAR',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'KEYS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'KEYS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'KEY_TYPE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'KEY_TYPE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LIMIT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LIMIT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LINES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LINES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LOAD',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LOAD',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LOCATION',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LOCATION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LOCK',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LOCK',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LOCKS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LOCKS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LOGICAL',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LOGICAL',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LONG',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'LONG',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MAPJOIN',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'MAPJOIN',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MATERIALIZED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'MATERIALIZED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'METADATA',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'METADATA',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MINUS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'MINUS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MINUTE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'MINUTE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MONTH',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'MONTH',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MSCK',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'MSCK',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'NOSCAN',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'NOSCAN',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'NO_DROP',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'NO_DROP',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OFFLINE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'OFFLINE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OPTION',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'OPTION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OUTPUTDRIVER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'OUTPUTDRIVER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OUTPUTFORMAT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'OUTPUTFORMAT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OVERWRITE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'OVERWRITE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OWNER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'OWNER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'PARTITIONED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'PARTITIONED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'PARTITIONS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'PARTITIONS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'PLUS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'PLUS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'PRETTY',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'PRETTY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'PRINCIPALS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'PRINCIPALS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'PROTECTION',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'PROTECTION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'PURGE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'PURGE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'READ',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'READ',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'READONLY',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'READONLY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'REBUILD',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'REBUILD',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'RECORDREADER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'RECORDREADER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'RECORDWRITER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'RECORDWRITER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'REGEXP',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'REGEXP',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'RELOAD',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'RELOAD',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'RENAME',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'RENAME',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'REPAIR',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'REPAIR',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'REPLACE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'REPLACE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'REPLICATION',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'REPLICATION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'RESTRICT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'RESTRICT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'REWRITE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'REWRITE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'RLIKE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'RLIKE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ROLE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ROLE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ROLES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'ROLES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SCHEMA',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SCHEMA',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SCHEMAS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SCHEMAS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SECOND',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SECOND',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SEMI',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SEMI',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SERDE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SERDE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SERDEPROPERTIES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SERDEPROPERTIES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SERVER',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SERVER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SETS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SETS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SHARED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SHARED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SHOW',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SHOW',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SHOW_DATABASE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SHOW_DATABASE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SKEWED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SKEWED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SORT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SORT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SORTED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SORTED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SSL',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'SSL',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'STATISTICS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'STATISTICS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'STORED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'STORED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'STREAMTABLE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'STREAMTABLE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'STRING',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'STRING',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'STRUCT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'STRUCT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TABLES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'TABLES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TBLPROPERTIES',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'TBLPROPERTIES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TEMPORARY',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'TEMPORARY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TERMINATED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'TERMINATED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TINYINT',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'TINYINT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TOUCH',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'TOUCH',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TRANSACTIONS',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'TRANSACTIONS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UNARCHIVE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UNARCHIVE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UNDO',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UNDO',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UNIONTYPE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UNIONTYPE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UNLOCK',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UNLOCK',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UNSET',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UNSET',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UNSIGNED',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UNSIGNED',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'URI',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'URI',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'USE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'USE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UTC',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UTC',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'UTCTIMESTAMP',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'UTCTIMESTAMP',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'VALUE_TYPE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'VALUE_TYPE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'VIEW',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'VIEW',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'WHILE',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'WHILE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'YEAR',
    documentation: 'Hive 1.2.0 Non-reserved Keywords',
    insertText: 'YEAR',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'AUTOCOMMIT',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'AUTOCOMMIT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ISOLATION',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'ISOLATION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LEVEL',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'LEVEL',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OFFSET',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'OFFSET',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SNAPSHOT',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'SNAPSHOT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TRANSACTION',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'TRANSACTION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'WORK',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'WORK',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'WRITE',
    documentation: 'Hive 2.0.0 Non-reserved Keywords',
    insertText: 'WRITE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ABORT',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'ABORT',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'KEY',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'KEY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'LAST',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'LAST',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'NORELY',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'NORELY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'NOVALIDATE',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'NOVALIDATE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'NULLS',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'NULLS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'RELY',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'RELY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'VALIDATE',
    documentation: 'Hive 2.1.0 Non-reserved Keywords',
    insertText: 'VALIDATE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DETAIL',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'DETAIL',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DOW',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'DOW',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'EXPRESSION',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'EXPRESSION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'OPERATOR',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'OPERATOR',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'QUARTER',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'QUARTER',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SUMMARY',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'SUMMARY',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'VECTORIZATION',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'VECTORIZATION',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'WEEK',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'WEEK',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'YEARS',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'YEARS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MONTHS',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'MONTHS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'WEEKS',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'WEEKS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'DAYS',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'DAYS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'HOURS',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'HOURS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'MINUTES',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'MINUTES',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'SECONDS',
    documentation: 'Hive 2.2.0 Non-reserved Keywords',
    insertText: 'SECONDS',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'TIMESTAMPTZ',
    documentation: 'Hive 3.0.0 Non-reserved Keywords',
    insertText: 'TIMESTAMPTZ',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ZONE',
    documentation: 'Hive 3.0.0 Non-reserved Keywords',
    insertText: 'ZONE',
    detail: 'Non-reserved Keywords',
  },
  {
    label: 'ALL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ALL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ALTER',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ALTER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'AND',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'AND',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ARRAY',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ARRAY',
    detail: 'Reserved Keywords',
  },
  {
    label: 'AS',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'AS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'AUTHORIZATION',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'AUTHORIZATION',
    detail: 'Reserved Keywords',
  },
  {
    label: 'BETWEEN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'BETWEEN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'BIGINT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'BIGINT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'BINARY',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'BINARY',
    detail: 'Reserved Keywords',
  },
  {
    label: 'BOOLEAN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'BOOLEAN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'BOTH',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'BOTH',
    detail: 'Reserved Keywords',
  },
  {
    label: 'BY',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'BY',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CASE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CASE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CAST',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CAST',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CHAR',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CHAR',
    detail: 'Reserved Keywords',
  },
  {
    label: 'COLUMN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'COLUMN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CONF',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CONF',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CREATE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CREATE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CROSS',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CROSS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CUBE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CUBE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CURRENT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CURRENT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CURRENT_DATE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CURRENT_DATE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CURRENT_TIMESTAMP',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CURRENT_TIMESTAMP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CURSOR',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'CURSOR',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DATABASE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DATABASE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DATE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DATE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DECIMAL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DECIMAL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DELETE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DELETE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DESCRIBE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DESCRIBE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DISTINCT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DISTINCT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DOUBLE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DOUBLE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DROP',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'DROP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ELSE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ELSE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'END',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'END',
    detail: 'Reserved Keywords',
  },
  {
    label: 'EXCHANGE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'EXCHANGE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'EXISTS',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'EXISTS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'EXTENDED',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'EXTENDED',
    detail: 'Reserved Keywords',
  },
  {
    label: 'EXTERNAL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'EXTERNAL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FALSE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'FALSE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'EXTERNAL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'EXTERNAL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FETCH',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'FETCH',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FLOAT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'FLOAT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FOLLOWING',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'EXFOLLOWINGTERNAL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FOR',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'FOR',
    detail: 'Reserved Keywords',
  },
  {
    label: 'from',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'from',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FULL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'FULL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FUNCTION',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'FUNCTION',
    detail: 'Reserved Keywords',
  },
  {
    label: 'GRANT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'GRANT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'GROUP',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'GROUP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'GROUPING',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'GROUPING',
    detail: 'Reserved Keywords',
  },
  {
    label: 'HAVING',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'HAVING',
    detail: 'Reserved Keywords',
  },
  {
    label: 'IF',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'IF',
    detail: 'Reserved Keywords',
  },
  {
    label: 'IMPORT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'IMPORT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'IN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'IN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'INNER',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'INNER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'INSERT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'INSERT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'INT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'INT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'INTERSECT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'INTERSECT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'INTERVAL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'INTERVAL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'INTO',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'INTO',
    detail: 'Reserved Keywords',
  },
  {
    label: 'IS',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'IS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'JOIN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'JOIN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'LATERAL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'LATERAL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'LEFT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'LEFT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'LESS',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'LESS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'LIKE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'LIKE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'LOCAL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'LOCAL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'MACRO',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'MACRO',
    detail: 'Reserved Keywords',
  },
  {
    label: 'MAP',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'MAP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'MORE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'MORE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'NONE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'NONE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'NOT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'NOT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'NULL',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'NULL',
    detail: 'Reserved Keywords',
  },
  {
    label: 'OF',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'OF',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ON',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ON',
    detail: 'Reserved Keywords',
  },
  {
    label: 'OR',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'OR',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ORDER',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ORDER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'OUT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'OUT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'OUTER',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'OUTER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'OVER',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'OVER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PARTIALSCAN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'PARTIALSCAN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PARTITION',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'PARTITION',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PERCENT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'PERCENT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PRECEDING',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'PRECEDING',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PRESERVE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'PRESERVE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PROCEDURE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'PROCEDURE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'RANGE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'RANGE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'READS',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'READS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'REDUCE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'REDUCE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'REVOKE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'REVOKE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'RIGHT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'RIGHT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ROLLUP',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ROLLUP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ROW',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ROW',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ROWS',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'ROWS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'SELECT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'SELECT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'SET',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'SET',
    detail: 'Reserved Keywords',
  },
  {
    label: 'SMALLINT',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'SMALLINT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TABLE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TABLE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TABLESAMPLE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TABLESAMPLE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'THEN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'THEN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TIMESTAMP',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TIMESTAMP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TO',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TO',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TRANSFORM',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TRANSFORM',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TRIGGER',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TRIGGER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TRUE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TRUE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TRUNCATE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'TRUNCATE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'UNBOUNDED',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'UNBOUNDED',
    detail: 'Reserved Keywords',
  },
  {
    label: 'UNION',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'UNION',
    detail: 'Reserved Keywords',
  },
  {
    label: 'UNIQUEJOIN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'UNIQUEJOIN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'UPDATE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'UPDATE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'USER',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'USER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'USING',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'USING',
    detail: 'Reserved Keywords',
  },
  {
    label: 'UTC_TMESTAMP',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'UTC_TMESTAMP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'VALUES',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'VALUES',
    detail: 'Reserved Keywords',
  },
  {
    label: 'VARCHAR',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'VARCHAR',
    detail: 'Reserved Keywords',
  },
  {
    label: 'WHEN',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'WHEN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'WHERE',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'WHERE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'WINDOW',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'WINDOW',
    detail: 'Reserved Keywords',
  },
  {
    label: 'WITH',
    documentation: 'Hive 1.2.0 Reserved Keywords',
    insertText: 'WITH',
    detail: 'Reserved Keywords',
  },
  {
    label: 'COMMIT',
    documentation: 'Hive 2.0.0 Reserved Keywords',
    insertText: 'COMMIT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ONLY',
    documentation: 'Hive 2.0.0 Reserved Keywords',
    insertText: 'ONLY',
    detail: 'Reserved Keywords',
  },
  {
    label: 'REGEXP',
    documentation: 'Hive 2.0.0 Reserved Keywords',
    insertText: 'REGEXP',
    detail: 'Reserved Keywords',
  },
  {
    label: 'RLIKE',
    documentation: 'Hive 2.0.0 Reserved Keywords',
    insertText: 'RLIKE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ROLLBACK',
    documentation: 'Hive 2.0.0 Reserved Keywords',
    insertText: 'ROLLBACK',
    detail: 'Reserved Keywords',
  },
  {
    label: 'START',
    documentation: 'Hive 2.0.0 Reserved Keywords',
    insertText: 'START',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CACHE',
    documentation: 'Hive 2.1.0 Reserved Keywords',
    insertText: 'CACHE',
    detail: 'Reserved Keywords',
  },
  {
    label: 'CONSTRAINT',
    documentation: 'Hive 2.1.0 Reserved Keywords',
    insertText: 'CONSTRAINT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FOREIGN',
    documentation: 'Hive 2.1.0 Reserved Keywords',
    insertText: 'FOREIGN',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PRIMARY',
    documentation: 'Hive 2.1.0 Reserved Keywords',
    insertText: 'PRIMARY',
    detail: 'Reserved Keywords',
  },
  {
    label: 'DAYOFWEEK',
    documentation: 'Hive 2.2.0 Reserved Keywords',
    insertText: 'DAYOFWEEK',
    detail: 'Reserved Keywords',
  },
  {
    label: 'EXTRACT',
    documentation: 'Hive 2.2.0 Reserved Keywords',
    insertText: 'EXTRACT',
    detail: 'Reserved Keywords',
  },
  {
    label: 'FLOOR',
    documentation: 'Hive 2.2.0 Reserved Keywords',
    insertText: 'FLOOR',
    detail: 'Reserved Keywords',
  },
  {
    label: 'INTEGER',
    documentation: 'Hive 2.2.0 Reserved Keywords',
    insertText: 'INTEGER',
    detail: 'Reserved Keywords',
  },
  {
    label: 'PRECISION',
    documentation: 'Hive 2.2.0 Reserved Keywords',
    insertText: 'PRECISION',
    detail: 'Reserved Keywords',
  },
  {
    label: 'VIEWS',
    documentation: 'Hive 2.2.0 Reserved Keywords',
    insertText: 'VIEWS',
    detail: 'Reserved Keywords',
  },
  {
    label: 'TIME',
    documentation: 'Hive 3.0.0 Reserved Keywords',
    insertText: 'TIME',
    detail: 'Reserved Keywords',
  },
  {
    label: 'NUMERIC',
    documentation: 'Hive 3.0.0 Reserved Keywords',
    insertText: 'NUMERIC',
    detail: 'Reserved Keywords',
  },
  {
    label: 'ide.engine.no.limit.allow',
    documentation: '当设置为true时，本次执行的SQL将进行全量导出',
    insertText: 'ide.engine.no.limit.allow',
    detail: 'build-in parameters',
  },
];
// 常用语法结构
const commonGrammaticalStruCture = [
  {
    label: 'drop table',
    documentation: '删除hive表',
    insertText: 'drop table if exists {table_name};',
    detail: '常用语法结构',
  },
  {
    label: 'create table partitioned by',
    documentation: '创建hive分区表',
    insertText: 'create table {table_name} ({columns}) partitioned by ({partition}) row format delimited fields terminated by "," stored as orc;',
    detail: '常用语法结构',
  },
  {
    label: 'create table as select',
    documentation: '通过select创建表',
    insertText: 'create table {table_name} as select;',
    detail: '常用语法结构',
  },
  {
    label: 'insert into table',
    documentation: '添加方式插入数据',
    insertText: 'insert into table {table_name} partition({partition});',
    detail: '常用语法结构',
  },
  {
    label: 'insert overwrite table',
    documentation: '覆盖方式插入数据',
    insertText: 'insert overwrite table {table_name} partition({partition});',
    detail: '常用语法结构',
  },
];
// 系统内嵌变量
const buildInVariableProposals = [
  {
    label: 'run_date',
    documentation: '当前日期的前一天',
    insertText: '${run_date}',
    detail: '系统内嵌变量',
  }, {
    label: 'run_date_std',
    documentation: '当前日期的前一天，年月日之间有横杠分割',
    insertText: '${run_date_std}',
    detail: '系统内嵌变量',
  }, {
    label: 'run_month_begin',
    documentation: '当前月份的第一天',
    insertText: '${run_month_begin}',
    detail: '系统内嵌变量',
  }, {
    label: 'run_month_begin_std',
    documentation: '当前月份的第一天，年月日之间有横杠分割',
    insertText: '${run_month_begin_std}',
    detail: '系统内嵌变量',
  }, {
    label: 'run_month_end',
    documentation: '当前月份的最后一天',
    insertText: '${run_month_end}',
    detail: '系统内嵌变量',
  }, {
    label: 'run_month_end_std',
    documentation: '当前月份的最后一天，年月日之间有横杠分割',
    insertText: '${run_month_end_std}',
    detail: '系统内嵌变量',
  },
];
const columnsProposals = [
  {
    label: 'id',
    documentation: '常用表字段',
    insertText: 'id',
    detail: '常用表字段',
  },
  {
    label: 'id_id',
    documentation: '常用表字段',
    insertText: 'id_id',
    detail: '常用表字段',
  },
  {
    label: 'org',
    documentation: '常用表字段',
    insertText: 'org',
    detail: '常用表字段',
  },
  {
    label: 'product_cd',
    documentation: '常用表字段',
    insertText: 'product_cd',
    detail: '常用表字段',
  },
  {
    label: 'id_no_hash',
    documentation: '常用表字段',
    insertText: 'id_no_hash',
    detail: '常用表字段',
  },
  {
    label: 'id_no_mask',
    documentation: '常用表字段',
    insertText: 'id_no_mask',
    detail: '常用表字段',
  },
  {
    label: 'app_no',
    documentation: '常用表字段',
    insertText: 'app_no',
    detail: '常用表字段',
  },
  {
    label: 'ecif_no',
    documentation: '常用表字段',
    insertText: 'ecif_no',
    detail: '常用表字段',
  },
  {
    label: 'name_hash',
    documentation: '常用表字段',
    insertText: 'name_hash',
    detail: '常用表字段',
  },
  {
    label: 'acct_no',
    documentation: '常用表字段',
    insertText: 'acct_no',
    detail: '常用表字段',
  },
  {
    label: 'card_no',
    documentation: '常用表字段',
    insertText: 'card_no',
    detail: '常用表字段',
  },
  {
    label: 'cust_id',
    documentation: '常用表字段',
    insertText: 'cust_id',
    detail: '常用表字段',
  },
  {
    label: 'case_no',
    documentation: '常用表字段',
    insertText: 'case_no',
    detail: '常用表字段',
  },
  {
    label: 'id_type',
    documentation: '常用表字段',
    insertText: 'id_type',
    detail: '常用表字段',
  },
  {
    label: 'id_no',
    documentation: '常用表字段',
    insertText: 'id_no',
    detail: '常用表字段',
  },
  {
    label: 'cust_type',
    documentation: '常用表字段',
    insertText: 'cust_type',
    detail: '常用表字段',
  },
  {
    label: 'cust_no',
    documentation: '常用表字段',
    insertText: 'cust_no',
    detail: '常用表字段',
  },
  {
    label: 'app_type',
    documentation: '常用表字段',
    insertText: 'app_type',
    detail: '常用表字段',
  },
  {
    label: 'openid',
    documentation: '常用表字段',
    insertText: 'openid',
    detail: '常用表字段',
  },
  {
    label: 'acct_type',
    documentation: '常用表字段',
    insertText: 'acct_type',
    detail: '常用表字段',
  },
  {
    label: 'prod_code',
    documentation: '常用表字段',
    insertText: 'prod_code',
    detail: '常用表字段',
  },
  {
    label: 'prod_id',
    documentation: '常用表字段',
    insertText: 'prod_id',
    detail: '常用表字段',
  },
  {
    label: 'partner_id',
    documentation: '常用表字段',
    insertText: 'partner_id',
    detail: '常用表字段',
  },
  {
    label: 'product_id',
    documentation: '常用表字段',
    insertText: 'product_id',
    detail: '常用表字段',
  },
  {
    label: 'id_acct',
    documentation: '常用表字段',
    insertText: 'id_acct',
    detail: '常用表字段',
  },
  {
    label: 'id_app',
    documentation: '常用表字段',
    insertText: 'id_app',
    detail: '常用表字段',
  },
  {
    label: 'c_prod_cd',
    documentation: '常用表字段',
    insertText: 'c_prod_cd',
    detail: '常用表字段',
  },
  {
    label: 'id_cust',
    documentation: '常用表字段',
    insertText: 'id_cust',
    detail: '常用表字段',
  },
  {
    label: 'id_openid',
    documentation: '常用表字段',
    insertText: 'id_openid',
    detail: '常用表字段',
  },
  {
    label: 'id_wx_openid',
    documentation: '常用表字段',
    insertText: 'id_wx_openid',
    detail: '常用表字段',
  },
  {
    label: 'id_qq_openid',
    documentation: '常用表字段',
    insertText: 'id_qq_openid',
    detail: '常用表字段',
  },
  {
    label: 'id_sg_openid',
    documentation: '常用表字段',
    insertText: 'id_sg_openid',
    detail: '常用表字段',
  },
  {
    label: 'id_loan',
    documentation: '常用表字段',
    insertText: 'id_loan',
    detail: '常用表字段',
  },
  {
    label: 'id_org',
    documentation: '常用表字段',
    insertText: 'id_org',
    detail: '常用表字段',
  },
  {
    label: 'reportsn',
    documentation: '常用表字段',
    insertText: 'reportsn',
    detail: '常用表字段',
  },
  {
    label: 'id_batch',
    documentation: '常用表字段',
    insertText: 'id_batch',
    detail: '常用表字段',
  },
  {
    label: 'id_case',
    documentation: '常用表字段',
    insertText: 'id_case',
    detail: '常用表字段',
  },
  {
    label: 'ds',
    documentation: '常用表字段',
    insertText: 'ds',
    detail: '常用表字段',
  },
];

let dbInfoProposals = [];
let tableInfoProposals = [];
let udfProposals = [];
let variableProposals = [];

export default {
  keyword: kewordInfoProposals,
  register(monaco) {
    const lang = 'hql';
    const hqlProposals = getFormatProposalsList(monaco, kewordInfoProposals, '', 'Keyword');
    const BIVPro = getFormatProposalsList(monaco, buildInVariableProposals, '', 'Variable');
    const CGSPro = getFormatProposalsList(monaco, commonGrammaticalStruCture, '', 'Reference');
    const columnsPro = getFormatProposalsList(monaco, columnsProposals, '', 'Field');
    getHiveList(monaco, lang).then((list) => {
      dbInfoProposals = list.dbInfoProposals;
      tableInfoProposals = list.tableInfoProposals;
      udfProposals = list.udfProposals;
      variableProposals = list.variableProposals;
    });
    monaco.languages.registerCompletionItemProvider('hql', {
      triggerCharacters: 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._'.split(''),
      async provideCompletionItems(model, position) {
        if (isEmpty(dbInfoProposals) || isEmpty(udfProposals) || isEmpty(tableInfoProposals) || isEmpty(variableProposals)) {
          getHiveList(monaco, lang).then((list) => {
            dbInfoProposals = list.dbInfoProposals;
            tableInfoProposals = list.tableInfoProposals;
            udfProposals = list.udfProposals;
            variableProposals = list.variableProposals;
          });
        }
        const textUntilPosition = model.getValueInRange({
          startLineNumber: position.lineNumber,
          startColumn: 1,
          endLineNumber: position.lineNumber,
          endColumn: position.column,
        });

        const tableMatch = textUntilPosition.match(/from\s+(\w+)\.(\w+)?$/);
        const databaseMatch = textUntilPosition.match(/from\s+\w+$/);
        const functionMatch = textUntilPosition.match(/(select|where|on|having|order by|cluster by|sort by)\s+\w+$/i);
        const keywordMatch = textUntilPosition.match(/([^"]*)?$/i);
        const grammarBlockMatch = textUntilPosition.match(/(create table|drop table)\s?$/i);
        if (tableMatch) {
          return getReturnList({
            match: tableMatch[2],
            proposals: tableInfoProposals,
            fieldString: 'caption',
            attachMatch: tableMatch[1],
          });
        } else if (databaseMatch) {
          const match = databaseMatch[0].split(' ')[1];
          return getReturnList({
            match,
            proposals: dbInfoProposals,
            fieldString: 'caption',
          });
        } else if (grammarBlockMatch) {
          const match = grammarBlockMatch[0].split(' ')[1];
          return getReturnList({
            match,
            proposals: CGSPro,
            fieldString: 'insertText',
            attachMatch: grammarBlockMatch,
            needSplit: true,
          });
        } else if (functionMatch) {
          const match = functionMatch[0].split(' ')[1];
          let proposalsList = null;
          // 如果函数发生load状态变化，则重新从indexdb中获取fnlist
          if (storage.get('isFunctionChange_hql')) {
            storage.set('isFunctionChange_hql', false);
            await getHiveList(monaco, lang).then((list) => {
              udfProposals = list.udfProposals;
              proposalsList = udfProposals.concat(columnsPro, variableProposals, BIVPro);
              return getReturnList({
                match,
                proposals: proposalsList,
                fieldString: 'insertText',
              });
            });
          }
          if (storage.get('isGlobalVariableChange')) {
            storage.set('isGlobalVariableChange', false);
            await getHiveList(monaco, lang).then((list) => {
              variableProposals = list.variableProposals;
              proposalsList = udfProposals.concat(columnsPro, variableProposals, BIVPro);
              return getReturnList({
                match,
                proposals: proposalsList,
                fieldString: 'insertText',
              });
            });
          }
          proposalsList = udfProposals.concat(columnsPro, variableProposals, BIVPro);
          return getReturnList({
            match,
            proposals: proposalsList,
            fieldString: 'insertText',
          });
        } else if (keywordMatch) {
          const matchList = keywordMatch[0].split(' ');
          const match = matchList[matchList.length - 1];
          let proposalsList = null;
          // 如果函数发生load状态变化，则重新从indexdb中获取fnlist
          if (storage.get('isFunctionChange_hql')) {
            storage.set('isFunctionChange_hql', false);
            await getHiveList(monaco, lang).then((list) => {
              udfProposals = list.udfProposals;
              proposalsList = hqlProposals.concat(BIVPro, CGSPro, udfProposals, columnsProposals);
              return getReturnList({
                match,
                proposals: proposalsList,
                fieldString: 'insertText',
              });
            });
          }
          proposalsList = hqlProposals.concat(BIVPro, CGSPro, udfProposals, columnsProposals);
          return getReturnList({
            match,
            proposals: proposalsList,
            fieldString: 'insertText',
          });
        }
        return [];
      },
    });
  },
};
