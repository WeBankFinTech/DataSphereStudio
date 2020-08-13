<template>
  <div>
    <div>
      <Card>
        <h2>{{ $t("message.oneService.apiTest.tabs.apiTestInfo.params")}}</h2>
        <!--遮罩层-->
        <Modal
          width="680"
          v-model="showParams">
          <div
            class="api-module-title"
            slot="header">
            {{$t('message.oneService.apiTest.tabs.apiTestInfo.selectParams')}}
          </div>
          <div name="disableCheckbox">
            <Checkbox :disabled="true" :value="true" :checked="true" :key="item" v-for="item in selectParams">{{item}}</Checkbox>
          </div>
          <div class="table-title"><span class="midText">{{$t('message.oneService.apiTest.tabs.apiTestInfo.allSelectParams')}}</span></div>

          <div >
            <Checkbox-group v-model="selectParams" v-show="apiTestInfo.paramList!=null" @on-change="selectApiParams">
              <Checkbox :label="item.displayName" :value="item.displayName" :key="item.displayName" v-for="item in apiTestInfo.paramList">
                <span>{{item.displayName}}</span>
              </Checkbox>
            </Checkbox-group>
            <Checkbox
              :indeterminate="indeterminate"
              :checked="checkAll"
              @on-change="handleCheckAll">{{$t('message.oneService.apiTest.tabs.apiTestInfo.allSelect')}}</Checkbox>

          </div>

          <div slot="footer">
            <Button
              @click="showParamsCancel">{{$t('message.oneService.apiPublish.addApiModal.cancel')}}</Button>
            <Button
              type="primary"
              :disabled="false"
              @click="showParamsOk" >{{$t('message.oneService.apiPublish.addApiModal.confirm')}}</Button>
          </div>
        </Modal>

        <div >
          <div>


            <div class="params-show" :label="item" style="width:500px;display:inline;margin-right:15px" :value="item" :key="item" v-for="item in selectParams">
              <span>{{item+":"}}</span>
              <i-input style="width: 100px" :label="item" :value.sync="requestParams.item" :name="item"  @on-blur="requestParamsChange" required inline/>
            </div>


            <div  class="more-params" style="width:150px;margin-right: 8px" @click="onClick">{{$t('message.oneService.apiTest.tabs.apiTestInfo.moreParams')}}</div>
            <!-- <FormItem :label="$t('message.oneService.apiTest.tabs.apiTestInfo.apiVersion')"> -->
            <Select v-model="apiTestInfo.apiVersion" @on-change="apiVersionChange" style="width:100px;margin-left: 8px">
              <Option v-for="item in apiVersionList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
            <!-- </FormItem> -->
            <Button
              type="primary"
              style="margin-left: 8px;display: inline;width:100px"
              @click="testApiClick">{{$t('message.oneService.apiTest.tabs.apiTestInfo.testApi')}}</Button>
          </div>

        </div>
        <!-- <Table :columns="paramInfoColumns" :data="apiTestInfo.paramList" @change="onChange" ></Table> -->
      </Card>
    </div>
    <br>
    <Card  v-show="scriptViewState.showPanel == 'result'">
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
    </Card>
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
    id: {
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
  data() {
    return {
      showParams: false,
      selectParams: [],
      requestParams: {},
      indeterminate: false,
      checkAll: false,
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
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.description'),
          key: 'description',
          align: 'center'

        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.displayName'),
          key: 'displayName',
          align: 'center'

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
        apiMethod: null,
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
    this.scriptViewState.height = this.scriptViewState.height || this.$el.clientHeight;
    this.scriptViewState.bottomPanelHeight = this.scriptViewState.bottomPanelHeight || this.$el.clientHeight * 0.4;
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
        id: _this.id,
        version: _this.apiTestInfo.apiVersion
      }, 'get').then((rst) => {
        if (rst.result) {

          rst.result.forEach((item, index) => {
            _this.apiTestInfo.paramList.push({
              paramName: item.name,
              paramType: item.type,
              require: item.requireStr,
              defaultValue: item.testValue,
              description: item.description==null?'-':item.description,
              displayName: item.displayName
            })
          });

        }
      }).catch((err) => {
        console.log(err)
      });

      api.fetch('/oneservice/queryById', {
        id: _this.id
      }, 'get').then((rst) => {
        if (rst.result) {

          _this.apiTestInfo.path = rst.result.path;
          _this.apiTestInfo.apiMethod= rst.result.method.toLowerCase()

        }
      }).catch((err) => {
        console.log(err)
      });
    },
    apiVersionChange() {
      this.initApiParamInfo();
    },
    apiVersionQueryPageList() {
      let _this = this;
      api.fetch('/oneservice/apiVersionQuery', {
        id: _this.id
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
        if(_this.requestParams[item.paramName] != null){
          params[item.paramName] = _this.requestParams[item.paramName]
        }else{
          params[item.paramName] = item.defaultValue;
        }
      })
      let url = this.apiTestInfo.path.startsWith("/")?'/oneservice/execute' + this.apiTestInfo.path:'/oneservice/execute/' + this.apiTestInfo.path

      api.fetch(url, {
        moduleName: 'dss-web',
        params: params
      }, this.apiTestInfo.apiMethod).then((rst) => {

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

    },
    onClick(){
      this.showParams=true
    },
    showParamsCancel(){
      this.showParams=false
    },
    showParamsOk(){
      this.showParams=false
    },
    selectApiParams(){
      if (this.selectParams.length === this.apiTestInfo.paramList.length) {
        this.indeterminate = false;
        this.checkAll = true;
      } else if (this.selectParams.length > 0) {
        this.indeterminate = true;
        this.checkAll = false;
      } else {
        this.indeterminate = false;
        this.checkAll = false;
      }
    },
    handleCheckAll(){
      if (this.indeterminate) {
        this.checkAll = false;
      } else {
        this.checkAll = !this.checkAll;
      }
      this.indeterminate = false;

      if (this.checkAll) {
        this.apiTestInfo.paramList.forEach(item=>{
          this.selectParams.push(item.displayName)
        })
      } else {
        this.selectParams = [];
      }
    },
    requestParamsChange(data){
      let value =data.target.value
      let displayName=data.target.name
      this.apiTestInfo.paramList.forEach(item=>{
        if(item.displayName===displayName){
          this.requestParams[item.paramName]=value
        }
      })
      // console.log('---',this.requestParams)
    }
  }
}
</script>

<style scoped>
.more-params{
  /* width: 100px; */
  /* word-break: keep-all; */
  /* margin: 10px 10px; */
  font: outline;
  cursor: pointer;
  font-size: 14px;
  font-family: 'Times New Roman', Times, serif;
  color: rgb(62, 62, 248);
  text-rendering: optimizeLegibility;
  font-feature-settings: "kern" 1;
  -webkit-font-feature-settings: "kern";
  -moz-font-feature-settings: "kern";
  -moz-font-feature-settings: "kern=1";
  font-kerning: normal;
  display: inline;
}
.table-title {
    position: relative;
    margin: 0 auto;
    width: 650px;
    height: 1px;
    background-color: #d4d4d4;
    text-align: center;
    font-size: 16px;
    color: rgba(101, 101, 101, 1);
  }
 .midText {
    position: absolute;
    left: 50%;
    background-color: #ffffff;
    padding: 0 15px;
    transform: translateX(-50%) translateY(-50%);
  }
</style>
