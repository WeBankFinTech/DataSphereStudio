<template>
  <div>
    <div>
      <Form v-model="apiTestInfo"
        :label-width="90"
        inline>
        <FormItem :label="$t('message.oneService.apiTest.tabs.apiTestInfo.apiVersion')">
          <Select v-model="apiTestInfo.apiVersion" @on-change="apiVersionChange">
            <Option v-for="item in apiVersionList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
        </FormItem>
        <FormItem>
          <Button
            type="primary"
            style="margin-left: 8px"
            @click="testApiClick">{{$t('message.oneService.apiTest.tabs.apiTestInfo.testApi')}}</Button>
        </FormItem>
      </Form>
      <Table :columns="paramInfoColumns" :data="apiTestInfo.paramList"></Table>
    </div>
    <br>
    <div
      v-if="scriptViewState.showPanel == 'result'"
      v-show="!scriptViewState.bottomPanelMin"
      class="workbench-container">
      <h1>{{$t('message.oneService.apiTest.tabs.apiTestInfo.testResult')}}</h1>
      <!--
      <we-progress
        v-if="scriptViewState.showPanel == 'progress'"
        :current="script.progress.current"
        :waiting-size="script.progress.waitingSize"
        :steps="script.steps"
        :status="script.status"
        :application="script.application"
        :progress-info="script.progress.progressInfo"
        :cost-time="script.progress.costTime"
        @open-panel="openPanel"/>
      -->
      <result
        v-if="scriptViewState.showPanel == 'result'"
        ref="result"
        :result="script.result"
        :script="script"
        :height="scriptViewState.cacheBottomPanelHeight"
        @on-analysis="openAnalysisTab"
        @on-set-change="changeResultSet"/>
      <!--
      <log
        v-if="scriptViewState.showPanel == 'log'"
        :logs="script.log"
        :log-line="script.logLine"
        :script-view-state="scriptViewState"
        :height="scriptViewState.cacheBottomPanelHeight"/>
      -->
    </div>
  </div>
</template>

<script>
import { isEmpty, find, dropRight, toArray, values, isString, findIndex, last, isUndefined, debounce } from 'lodash';
import api from '@/js/service/api';
import DataTable from '@/js/component/table/dataTable'
import result from '@/js/module/oneService/result.vue';
// import log from '@/js/module/workbench/script/log.vue';
// import weProgress from '@/js/module/workbench/script/progress.vue';
export default {
  name: "apiTestRun",
  props: {
    scriptPath: {
      type: String,
      required: true,
    }
  },
  components: {
    // 'DataTable': DataTable,
    result,
    // log,
    // weProgress
  },
  mounted() {
    this.scriptViewState.height = this.scriptViewState.height || this.$el.clientHeight;
    this.scriptViewState.bottomPanelHeight = this.scriptViewState.bottomPanelHeight || this.$el.clientHeight * 0.4;
  },
  data() {
    return {
      paramInfoColumns: [
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.paramName'),
          key: 'paramName'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.paramType'),
          key: 'paramType',
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.require.title'),
          align: 'center',
          key: 'require'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.defaultValue'),
          key: 'defaultValue',
          width: '200',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  value: params.row.defaultValue
                },
                on: {

                }
              })
            ]);
          }
        }
      ],
      apiTestInfo: {
        apiVersion: null,
        paramList: [

        ]
      },
      apiVersionList: [

      ],
      apiVersionInfo: {

      },
      scriptViewState: {
        showPanel: 'progress',
        height: 0,
        bottomPanelHeight: 0,
        cacheBottomPanelHeight: 0,
        bottomPanelMin: false,
        cacheLogScroll: 0,
      },
      script: {
        log: {
          all: "",
          error: "",
          info: "",
          warning: ""
        },
        logLine: 1,
        result: {
          cache: {
            offsetX: "",
            offsetY: ""
          },
          current: 1,
          size: 10,
        },
        scriptViewState: {
          bottomPanelHeight: 342.8,
          bottomPanelMin: false,
          cacheBottomPanelHeight: 342.8,
          cacheLogScroll: 0,
          height: 857,
          showPanel: "progress"
        }
      },
      excuteLoading: false
    }
  },
  computed: {

  },
  mounted() {
    this.initApiVersionInfo();
  },
  methods: {
    initApiVersionInfo() {
      let _this = this;
      _this.apiVersionQueryPageList();
    },
    initApiParamInfo() {
      let _this = this;
      _this.apiTestInfo.paramList = []

      api.fetch('/oneservice/apiParamQuery', {
        scriptPath: _this.scriptPath,
        version: _this.apiTestInfo.apiVersion
      }, 'get').then((rst) => {
        if (rst.result) {

          rst.result.forEach((item, index) => {
            _this.apiTestInfo.paramList.push({
              paramName: item.name,
              paramType: item.type,
              require: item.requireStr,
              defaultValue: item.testValue
            })
          });

        }
      }).catch((err) => {

      });

      api.fetch('/oneservice/query', {
        scriptPath: _this.scriptPath
      }, 'get').then((rst) => {
        if (rst.result) {

          _this.apiTestInfo.path = rst.result.path;

        }
      }).catch((err) => {

      });
    },
    apiVersionChange() {
      this.initApiParamInfo();
    },
    apiVersionQueryPageList() {
      let _this = this;
      api.fetch('/oneservice/apiVersionQuery', {
        scriptPath: _this.scriptPath
      }, 'get').then((rst) => {
        if (rst.result) {

          _this.apiVersionList = []

          rst.result.forEach((item, index) => {
            _this.apiVersionList.push({
              label: item.version,
              value: item.version
            })
          })

          _this.apiTestInfo.apiVersion = rst.result[0].version

          _this.initApiParamInfo();
        }
      }).catch((err) => {

      });
    },
    testApiClick() {
      let  _this = this;

      if (_this.excuteLoading) {
        return;
      }

      _this.excuteLoading = true;

      var params = {}
      this.apiTestInfo.paramList.forEach((item, index) => {
        params[item.paramName] = item.defaultValue;
      })

      api.fetch('/oneservice/execute/' + this.apiTestInfo.path, {
        moduleName: 'test',
        params: params
      }, 'get').then((rst) => {

        if (rst) {

          var bodyRows = []
          var metadata = []
          rst.forEach((item, index) => {

            if (0 == metadata.length) {
              for (var key in item) {
                metadata.push("columnName:" + key + ",dataType:string,comment:")
              }
            }

            var bodyRowsItem = []

            for (var key in item) {
              bodyRowsItem.push(item[key])
            }

            bodyRows.push(bodyRowsItem)
          })

          _this.showPanelTab('result');
          const storeResult = {
            'headRows': metadata,
            'bodyRows': bodyRows,
            'total': rst.length,
            'type': '2',
            cache: {
              offsetX: 0,
              offsetY: 0,
            },
            // 'path': this.execute.currentResultPath,
            current: 1,
            size: 10,
          };

          // _this.$set(this.execute.resultList[0], 'result', storeResult);
          _this.$set(this.script, 'resultSet', 0);
          _this.script.result = storeResult;
          _this.script.resultList = rst;

        }

        _this.excuteLoading = false;

      }).catch((err) => {
        _this.excuteLoading = false;
      });
    },
    showPanelTab(type) {
      this.scriptViewState.showPanel = type;
      if (type === 'log') {
        this.localLogShow();
      }
    },
    localLogShow() {
      if (!this.debounceLocalLogShow) {
        this.debounceLocalLogShow = debounce(() => {
          if (this.localLog) {
            this.script.log = Object.freeze({ ...this.localLog.log });
            this.script.logLine = this.localLog.logLine;
          }
        }, 1500);
      }
      this.debounceLocalLogShow();
    },
    openAnalysisTab() {

    },
    changeResultSet() {

    }
  }
}
</script>

<style scoped>

</style>
