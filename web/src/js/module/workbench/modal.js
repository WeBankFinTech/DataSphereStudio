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

import mixin from '@/js/service/mixin';

let logoList = [
  { rule: /\.(bi)$/i, logo: 'fi-bi' },
  { rule: /\.(sql)$/i, logo: 'fi-spark' },
  { rule: /\.(hql)$/i, logo: 'fi-hive' },
  { rule: /\.(out)$/i, logo: 'fi-storage' },
  { rule: /\.scala$/i, logo: 'fi-scala' },
  { rule: /\.jdbc$/i, logo: 'fi-jdbc' },
  { rule: /\.python$/i, logo: 'fi-python' },
  { rule: /\.py$/i, logo: 'fi-spark-python' },
  { rule: /\.r$/i, logo: 'fi-r' },
  { rule: /\.qmlsql$/i, logo: 'fi-spark' },
  { rule: /\.qmlpy$/i, logo: 'fi-python' },
  { rule: /\.txt$/i, logo: 'fi-txt' },
  { rule: /\.log$/i, logo: 'fi-log' },
];

/**
 *  基础
 */
export class Work {
  /**
     * 构造器
     * @param {*} option
     */
  constructor(option) {
    // 唯一标识
    this.id = option.id;
    // 执行id
    this.execID = null;
    // 历史脚本才用得到，只/api/jobhistory/${id}/get请求使用
    this.taskID = option.taskID;
    // 文件名
    this.filename = option.filename;
    // 文件路径
    this.filepath = option.filepath;
    // 数据开发模块会显示文件列表
    this.fileList = option.fileList || [];
    // 是否保存
    this.unsave = option.unsave || false;
    // 是否另存
    this.saveAs = option.saveAs || false;
    // 是否要读取缓存数据，一般用于自动运行的时候
    this.noLoadCache = option.noLoadCache || false;
    // 类型
    this.type = option.type || 'workspaceScript'; // workspaceScript hdfsScript historyScript workflow backgroundScript
    // 数据
    this.data = option.data || null;
    // 脚本内容和参数（test）
    this.code = option.code;
    this.params = option.params;
    this.isShow = true;
    // 用于记录临时脚本或者HDFS脚本另存后保存内容和参数用
    this.ismodifyByOldTab = option.ismodifyByOldTab || false;
    this.owner = mixin.methods.getUserName();
    this.specialSetting = option.specialSetting;
    this.nodeName = option.nodeName || null; // 在工作流操作时记录名称
  }

  /**
     * 根据文件后缀判断logo
     */
  get logo() {
    let logos = logoList.filter((item) => {
      return item.rule.test(this.filename);
    });
    if (logos.length > 0) {
      return logos[0].logo;
    } else {
      return 'javascript: void 0';
    }
  }
}

/**
 * 脚本任务
 */
export class Script {
  /**
     * 构造器
     * @param {*} option
     */
  constructor(option) {
    this.id = option.id;
    this.fileName = option.fileName;
    this.filepath = option.filepath;
    // 脚本内容
    this.data = option.data || option.code || '';
    // 脚本原内容
    this.oldData = option.data || '';
    // 日志
    this.log = option.log || {};
    this.logLine = option.logLine || 1;
    // 历史
    this.history = [];
    // 进度
    this.progress = {
      current: null,
      progressInfo: [],
      waitingSize: null,
      costTime: null,
    };
    // 步骤
    this.steps = [];
    // 运行结果
    this.result = null;
    // 记录结果集的存储路径
    this.resultList = null;
    // 参数
    this.params = option.params || {
      variable: [],
      configuration: {
        special: {},
        runtime: {
          args: '',
          env: [],
        },
        startup: {},
      },
    };

    // editor组件的language
    this.lang = option.lang;
    // 是否可执行
    this.executable = option.executable || false;
    // 是否可配置
    this.configurable = option.configurable || true;
    // 后台使用哪种BDP服务
    this.application = option.application;
    // 后台运行的服务类型
    this.runType = option.runType;
    // 后缀
    this.ext = option.ext;
    // 否为系统支持的脚本类型
    this.scriptType = option.scriptType;
    // ??
    this.abbr = option.abbr;
    // 是否可读
    this.readOnly = option.readOnly || false;
    // 是否正在执行
    this.running = false;
    // 当前的运行状态
    this.status = option.status ? option.status : 'Inited';
    // script视图状态
    this.scriptViewState = {};
  }
}

/**
 *  历史脚本
 */
export class HistoryScript extends Script {
  /**
     *
     * @param {*} option
     */
  constructor(option) {
    super(option);
    this.readOnly = true;
    this.executable = false;
    this.configurable = false;
  }
}
