<template>
  <we-panel diretion="vertical">
    <we-panel-item
      ref="topPanel"
      :index="1"
      :min-size="100"
      class="editor-panel">
      <editor
        v-if="script"
        :script="script"
        :work="work"/>
    </we-panel-item>
    <we-panel-item
      ref="bottomPanel"
      :index="2"
      :height="scriptViewState.bottomPanelHeight"
      :min-size="32"
      class="log-panel"
      @on-change="changeBottomPanel">
      <div class="workbench-tabs">
        <div class="workbench-tab-wrapper">
          <div class="workbench-tab">
            <div
              :class="{active: scriptViewState.showPanel == 'log'}"
              class="workbench-tab-item"
              @click="showLog">
              <span>{{ $t('message.workBench.body.script.historyScript.tabs.log') }}</span>
            </div>
            <div
              v-if="script && script.result"
              :class="{active: scriptViewState.showPanel == 'result'}"
              class="workbench-tab-item"
              @click="showResult">
              <span>{{ $t('message.workBench.body.script.historyScript.tabs.result') }}</span>
            </div>
          </div>
          <div class="workbench-tab-button">
            <Dropdown
              trigger="click"
              placement="bottom-end"
              @on-click="configLogPanel">
              <Icon type="md-list" />
              <DropdownMenu slot="list">
                <DropdownItem name="fullScreen">{{ $t('message.constants.logPanelList.fullScreen') }}</DropdownItem>
                <DropdownItem name="releaseFullScreen">{{ $t('message.constants.logPanelList.releaseFullScreen') }}</DropdownItem>
                <DropdownItem
                  name="min"
                  v-if="!scriptViewState.bottomPanelMin">{{ $t('message.constants.logPanelList.min') }}</DropdownItem>
                <DropdownItem
                  name="releaseMin"
                  v-else>{{ $t('message.constants.logPanelList.releaseMin') }}</DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </div>
        </div>
        <div
          v-show="!scriptViewState.bottomPanelMin"
          class="workbench-container">
          <result
            ref="result"
            v-if="script && script.result"
            v-show="scriptViewState.showPanel == 'result'"
            :script="script"
            :result="script.result"
            :height="scriptViewState.cacheBottomPanelHeight"
            @on-analysis="openAnalysisTab"
            @on-set-change="changeResultSet"/>
          <log
            v-if="scriptViewState.showPanel == 'log' && script"
            :log-line="script.logLine"
            :logs="script.log"
            :height="scriptViewState.cacheBottomPanelHeight"/>
        </div>
      </div>
    </we-panel-item>
  </we-panel>
</template>
<script>
import { isEmpty, isUndefined, find } from 'lodash';
import api from '@/js/service/api';
import util from '@/js/util';
import { HistoryScript } from '../modal.js';
import editor from './editor.vue';
import result from './result.vue';
import log from './log.vue';
export default {
  components: {
    result,
    editor,
    log,
  },
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      script: null,
      scriptViewState: {
        showPanel: 'log',
        height: 0,
        bottomPanelHeight: 0,
        cacheBottomPanelHeight: 0,
        bottomPanelMin: false,
      },
      biLoading: false,
    };
  },
  computed: {
    isPanelMin() {
      if (!this.script) return null;
      const log = this.script.log;
      const emptyLog = isEmpty(log) || !(log.all || log.error || log.info || log.warning);
      const emptyHistory = isEmpty(this.script.history);
      const emptyResult = !this.script.result || isEmpty(this.script.result);
      const emptyProgress = isEmpty(this.script.progress.progressInfo);
      return emptyLog && emptyHistory && emptyResult && emptyProgress;
    },
  },
  watch: {
    'isPanelMin': function(val) {
      if (val) {
        this.configLogPanel('min');
      } else {
        this.configLogPanel('releaseMin');
      }
    },
  },
  created() {
    // 如果当前work存在data，表示工作已经被打开
    if (this.work.data) {
      this.script = this.work.data;
      // 用缓存的scriptViewState替换当前this.scriptViewState
      this.scriptViewState = this.script.scriptViewState;
    } else {
      try {
        api.fetch(`/jobhistory/${this.work.taskID}/get`, 'get').then((rst) => {
          const option = rst.task;
          const supportList = this.getSupportModes();
          const supportedMode = find(supportList, (p) => p.rule.test(this.work.filename));
          this.work.data = this.script = new HistoryScript(Object.assign(supportedMode, option, {
            fileName: this.work.filename,
            filepath: this.work.filepath,
            id: this.work.id,
            data: option.executionCode,
          }));
          this.getLogs(option);
          if (option.resultLocation) {
            this.getResult(option);
          }
        });
      } catch (errorMsg) {
      }
    }
  },
  mounted() {
    this.scriptViewState.height = this.scriptViewState.height || this.$el.clientHeight;
    this.scriptViewState.bottomPanelHeight = this.scriptViewState.bottomPanelHeight || this.$el.clientHeight * 0.4;
  },
  updated() {
  },
  beforeDestroy() {
    this.scriptViewState.bottomPanelHeight = this.scriptViewState.cacheBottomPanelHeight || this.scriptViewState.bottomPanelHeight;
  },
  methods: {
    'Workbench:setEditorPanelSize'(option) {
      if (option.id === this.script.id) {
        this.$refs.topPanel[option.status]({
          isDiy: true,
          diyStyle: {
            top: '33px',
            right: 0,
            bottom: 0,
            left: 0,
          },
        });
      }
    },
    showLog() {
      this.scriptViewState.showPanel = 'log';
    },
    showResult() {
      this.scriptViewState.showPanel = 'result';
    },
    configLogPanel(name) {
      if (name == 'fullScreen' || name == 'releaseFullScreen') {
        this.$refs.bottomPanel[name]({
          isDiy: false,
        });
      }
      if (name == 'min') {
        this.scriptViewState.bottomPanelHeight = 32;
        this.scriptViewState.bottomPanelMin = true;
      }
      if (name == 'releaseMin') {
        this.scriptViewState.bottomPanelHeight = this.scriptViewState.height * 0.4;
        this.scriptViewState.bottomPanelMin = false;
      }
    },
    changeBottomPanel({ height }) {
      this.scriptViewState.cacheBottomPanelHeight = height;
    },
    changeResultSet(data, cb) {
      const resultSet = isUndefined(data.currentSet) ? this.script.resultSet : data.currentSet;
      const findResult = this.script.resultList.find((item) => item.name === `_${resultSet}.dolphin`);
      const resultPath = findResult && findResult.path;
      const hasResult = this.script.resultList[resultSet].hasOwnProperty('result');
      if (!hasResult) {
        const pageSize = 5000;
        const url = '/filesystem/openFile';
        api.fetch(url, {
          path: resultPath,
          pageSize,
        }, 'get')
          .then((ret) => {
            const result = {
              'headRows': ret.metadata,
              'bodyRows': ret.fileContent,
              // 如果totalLine是null，就显示为0
              'total': ret.totalLine ? ret.totalLine : 0,
              // 如果内容为null,就显示暂无数据
              'type': ret.fileContent ? ret.type : 0,
              'cache': {
                offsetX: 0,
                offsetY: 0,
              },
              'path': resultPath,
              'current': 1,
              'size': 20,
            };
            this.$set(this.script.resultList[resultSet], 'result', result);
            this.$set(this.script, 'resultSet', resultSet);
            this.script.result = result;
            this.dispatch('IndexedDB:updateResult', {
              tabId: this.script.id,
              resultSet,
              showPanel: 'result',
              ...this.script.resultList,
            });
            cb();
          }).catch(() => {
            this.$refs.result.resultSet = data.lastSet;
            cb();
          });
      } else {
        this.script.result = this.script.resultList[resultSet].result;
        this.$set(this.script, 'resultSet', resultSet);
        this.dispatch('IndexedDB:updateResult', {
          tabId: this.script.id,
          resultSet: resultSet,
          showPanel: 'result',
          ...this.script.resultList,
        });
        cb();
      }
    },
    async getLogs(option) {
      const url = `/filesystem/openLog`;
      if (!option.logPath) {
        this.script.log = { all: '', error: '', warning: '', info: '' };
        this.script.logLine = 1;
        this.script.scriptViewState = this.scriptViewState;
        return;
      }
      await api.fetch(url, {
        path: option.logPath,
      }, 'get').then((rst) => {
        if (rst) {
          const log = { all: '', error: '', warning: '', info: '' };
          const convertLogs = util.convertLog(rst.log);
          Object.keys(convertLogs).forEach((key) => {
            if (convertLogs[key]) {
              log[key] += convertLogs[key] + '\n';
            }
          });
          this.script.log = log;
          this.script.logLine = rst.fromLine;
          // 把新创建的scriptViewState挂到script对象上
          this.script.scriptViewState = this.scriptViewState;
        }
      });
    },
    async getResult(option) {
      const url1 = `/filesystem/getDirFileTrees`;
      await api.fetch(url1, {
        path: option.resultLocation,
      }, 'get').then((rst) => {
        if (rst.dirFileTrees) {
          // 后台的结果集顺序是根据结果集名称按字符串排序的，展示时会出现结果集对应不上的问题，所以加上排序
          this.script.resultList = rst.dirFileTrees.children.sort((a, b) => parseInt(a.name, 10) - parseInt(b.name, 10));
          if (this.script.resultList.length) {
            const currentResultPath = rst.dirFileTrees.children[0].path;
            const url2 = `/filesystem/openFile`;
            api.fetch(url2, {
              path: currentResultPath,
              page: 1,
              pageSize: 5000,
            }, 'get').then((ret) => {
              this.showResult();
              this.script.result = {
                'headRows': ret.metadata,
                'bodyRows': ret.fileContent,
                'total': ret.totalLine,
                'type': ret.type,
                'cache': { // get from storage or default {}
                  offsetX: 3000 + 1000 * Math.ceil((10 * Math.random())),
                  offsetY: 100,
                },
                'path': currentResultPath,
              };
            });
          }
        }
      });
    },
    async openAnalysisTab() {
      if (this.biLoading) return this.$Message.warning(this.$t('message.constants.warning.biLoading'));
      this.biLoading = true;
      const vsBiUrl = this.getVsBiUrl();
      let result = await api.fetch(`${vsBiUrl}/api/rest_j/v1/visualis/view`, {
        fileName: this.work.filename,
        executionCode: this.script.data,
        resultPath: this.script.result.path,
        runType: this.script.runType,
        resultNumber: this.script.result.current > 0 ? this.script.result.current - 1 : 0,
      }).catch(() => {
        this.biLoading = false;
      });
      if (result) {
        let { id, projectId } = result;
        const filename = `${this.work.filename}_project${projectId}_set${this.script.resultSet}.bi`;
        const md5Path = util.md5(filename);
        this.dispatch('Workbench:add', {
          id: md5Path,
          filename: filename,
          saveAs: true,
          type: 'visualAnalysis',
          params: {
            viewId: id,
            projectId,
          },
        }, () => {
          this.biLoading = false;
        });
      }
    },
  },
};
</script>
