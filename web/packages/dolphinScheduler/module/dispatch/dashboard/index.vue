<template>
  <m-list-construction :title="searchParams.projectId ? $t('message.scheduler.projectHome') : $t('message.scheduler.home')">
    <template slot="content">
      <div class="perject-home-content">
        <div class="row" >
          <div class="col-md-6 dashboard-module" style="margin-right: 10px">
            <div class="chart-title">
              <span>{{$t('message.scheduler.processStatusStatistics')}}</span>
              <div class="time-model">
                <template>
                  <Date-picker
                    style="width: 250px"
                    v-model="dataTime"
                    type="datetimerange"
                    @on-change="_datepicker"
                    range-separator="-"
                    :start-placeholder="$t('message.scheduler.runTask.startDate')"
                    :end-placeholder="$t('message.scheduler.runTask.endDate')"
                    format="yyyy-MM-dd HH:mm:ss">
                  </Date-picker>
                </template>
              </div>
            </div>
            <div class="row dashboard-module-content">
              <m-process-state-count :search-params="searchParams" @goToList="goToList">
              </m-process-state-count>
              <Spin size="large" fix v-if="loading"></Spin>
            </div>
          </div>

          <div class="col-md-6 dashboard-module">
            <div class="chart-title">
              <span>周期实例完成情况</span>
              <div class="day-change">
                <div :class="stateSelected === 1?'selected': ''" @click="changeState(1)">运行成功</div>
                <div :class="stateSelected === 2?'selected': ''" @click="changeState(2)">运行失败</div>
              </div>
            </div>
            <div class="row dashboard-module-content">
              <div id="areaChart" style="height: 330px"></div>
              <Spin size="large" fix v-if="loading"></Spin>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-md-6 dashboard-module" style="margin-right: 10px">
            <div class="chart-title">
              <span>{{$t('message.scheduler.processDefinitionStatistics')}}</span>
            </div>
            <div class="dashboard-module-content">
              <m-define-user-count :project-id="searchParams.projectId" @goToList="goToList">
              </m-define-user-count>
              <Spin size="large" fix v-if="loading"></Spin>
            </div>
          </div>

          <div class="col-md-6 dashboard-module">
            <div class="chart-title">
              <span>流程耗时排名</span>
              <div class="time-model">
                <template>
                  <Date-picker
                    style="width: 250px"
                    v-model="consumptionTime"
                    type="datetimerange"
                    @on-change="changeTime"
                    range-separator="-"
                    :options="options1"
                    format="yyyy-MM-dd HH:mm:ss">
                  </Date-picker>
                </template>
              </div>
              <!-- <div class="day-change"> -->
              <!--<div :class="stateSelected === 1?'selected': ''" @click="changeState(1)">运行成功</div>-->
              <!--<div :class="stateSelected === 2?'selected': ''" @click="changeState(2)">运行失败</div>-->
              <!--</div>-->
            </div>
            <div class="row dashboard-module-content">
              <Table :columns="columns" :data="consumptionList" style="height: 430px;" class="consumption-table">
                <!--<template slot-scope="{ row }" slot="operation">-->
                <!--</template>-->
              </Table>
              <Spin size="large" fix v-if="loading"></Spin>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12 dashboard-module">
            <div class="chart-title">
              <span>工作流实例与成功率统计</span>
              <div class="day-change">
                <div :class="daySelected === 1?'selected': ''" @click="changeDay(1)">今日</div>
                <div :class="daySelected === 2?'selected': ''" @click="changeDay(2)">昨日</div>
              </div>
            </div>
            <div class="dashboard-module-content">
              <div id="mixedBarLine" style="height: 500px"></div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </m-list-construction>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api'
import util from '@dataspherestudio/shared/common/util'
//import dayjs from 'dayjs'
import mDefineUserCount from './source/defineUserCount'
import mProcessStateCount from './source/processStateCount'
//import mTaskStatusCount from './source/taskStatusCount'
import mListConstruction from '../components/listConstruction/listConstruction'
import { GetWorkspaceData } from '@dataspherestudio/shared/common/service/apiCommonMethod.js'
import {formatDate} from '../convertor'
import { tasksState } from '../config'

import echarts from 'echarts'

export default {
  name: 'projects-index-index',
  data () {
    return {
      searchParams: {
        projectId: null,
        startDate: '',
        endDate: ''
      },
      projectName: this.$route.query.projectName,
      workspaceName: '',
      projectId: '',
      dataTime: [],
      projectList: [],

      mixedBarLineChart: null,
      areaChart: null,
      daySelected: 1,
      stateSelected: 1,
      loading: false,

      consumptionList: [],
      consumptionTime: [],
      consumptionParams: {},
      options1: {
        shortcuts: [
          {
            text: '今天',
            value () {
              const start = new Date();
              start.setHours(0)
              start.setMinutes(0)
              start.setSeconds(0)
              const end = new Date();
              return [start, end];
            }
          },
          {
            text: '昨天',
            value () {
              const end = new Date();
              end.setHours(0)
              end.setMinutes(0)
              end.setSeconds(0)
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24);
              start.setHours(0)
              start.setMinutes(0)
              start.setSeconds(0)
              return [start, end];
            }
          },
          {
            text: '最近一周',
            value () {
              const end = new Date();
              const start = new Date();
              start.setHours(0)
              start.setMinutes(0)
              start.setSeconds(0)
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              return [start, end];
            }
          }
        ]
      }
    }
  },
  props: {

  },
  methods: {
    getConsumptionData() {
      if (!this.projectName) return
      api.fetch(`dolphinscheduler/projects/${this.workspaceName}-${this.projectName}/instance/list-order-by-duration`, {
        pageSize: 10,
        pageNo: 1,
        startDate: this.consumptionParams.startDate  || '',
        endDate: this.consumptionParams.endDate || '',
      }, 'get').then((res) => {
        this.consumptionList = res.totalList
      })
    },
    changeTime(val) {
      this.consumptionParams.startDate = val[0]
      this.consumptionParams.endDate = val[1]
      this.getConsumptionData()
    },
    changeState(state) {
      if (state) {
        this.stateSelected = state
      }
      let curState
      if (state == 1) {
        curState = 'SUCCESS'
      } else if (state == 2) {
        curState = 'FAILURE'
      }
      this.getAreaData((areaData) => {
        this.buildAreaChart(areaData)
      }, curState)
    },
    changeDay(day){
      if (day)
        this.daySelected = day
      let dd = new Date(),
        tYear, tMonth, tDay
      if (this.daySelected === 1) {
        tYear = dd.getFullYear(),
        tMonth = dd.getMonth() + 1 > 9 ? dd.getMonth() + 1 : '0' + (dd.getMonth() + 1),
        tDay = dd.getDate() > 9 ? dd.getDate() : '0' + dd.getDate()
      } else if (this.daySelected === 2) {
        dd.setDate(dd.getDate() - 1)
        tYear = dd.getFullYear(),
        tMonth = dd.getMonth() + 1 > 9 ? dd.getMonth() + 1 : '0' + (dd.getMonth() + 1),
        tDay = dd.getDate() > 9 ? dd.getDate() : '0' + dd.getDate()
      }
      if (this.daySelected) {
        this.getMixedBarLineData(`${tYear}-${tMonth}-${tDay}`, (dd) => {
          this.buildMixedBarLineChart(dd)
        })
      }
    },
    getFullName() {
      for (let i = 0; i < this.projectList.length; i++) {
        if (this.projectList[i].id === this.projectId) {
          return this.projectList[i].name
        }
      }
    },

    goToList() {
      if (this.getFullName() === `${this.workspaceName}-${this.projectName}`) {
        this.$emit('goToList', ...arguments)
      }
    },
    _datepicker (val) {
      this.searchParams.startDate = val[0]
      this.searchParams.endDate = val[1]
    },
    getProjectId(cb) {
      if (this.projectId) {
        return cb(this.projectId)
      } else {
        this.loading = true
        let searchVal = `${this.workspaceName}-${this.projectName}`
        api.fetch(`dolphinscheduler/projects/list-paging`, {
          pageSize: 100,
          pageNo: 1,
          searchVal: ''
        }, 'get').then(res => {
          this.projectList = res.totalList
          this.loading = false
          for (let i = 0; i < res.totalList.length; i++) {
            if (res.totalList[i].name === searchVal) {
              this.projectId = res.totalList[i].id
              return cb(this.projectId)
            }
          }
          this.projectId = res.totalList.length ? res.totalList[0].id : ''
          return cb(this.projectId)
        })
      }
    },
    getMixedBarLineData(currentDay, cb) {
      if (!this.projectName) return
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.workspaceName}-${this.projectName}/instance/list-paging`, {
          pageSize: 1000,
          pageNo: 1,
          startDate: `${currentDay} 00:00:00`,
          endDate: `${currentDay} 23:59:59`,
        }, 'get').then((res) => {
          let objTotal = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            successTotal = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            failureTotal = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            successPercent = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
          res.totalList.forEach(item => {
            item.startTime = formatDate(item.startTime)
            item.endTime = formatDate(item.endTime)
            let curHour = new Date(item.startTime).getHours()
            objTotal[curHour] = objTotal[curHour] + 1
            let curState = item.state
            if (curState === 'SUCCESS') {
              successTotal[curHour] = successTotal[curHour] + 1
            }
            if (curState === 'FAILURE') {
              failureTotal[curHour] = failureTotal[curHour] + 1
            }
          })
          successTotal.forEach((success, index) => {
            if (objTotal[index]) {
              successPercent[index] =  parseInt((success / objTotal[index]) * 100)
            } else {
              successPercent[index] = 0
            }
          })
          cb && cb({
            objTotal,
            successTotal,
            failureTotal,
            successPercent
          })
        })
      })
    },
    getAreaData(cb, stateType) {
      // eslint-disable-next-line require-jsdoc
      function getFormatDateString(dd) {
        let tYear = dd.getFullYear(),
          tMonth = dd.getMonth() + 1 > 9 ? dd.getMonth() + 1 : '0' + (dd.getMonth() + 1),
          tDay = dd.getDate() > 9 ? dd.getDate() : '0' + dd.getDate()
        return `${tYear}-${tMonth}-${tDay}`
      }
      if (!this.projectName) return

      let dd = new Date(),
        dd1 = new Date()
      dd1.setDate(dd.getDate() - 1)
      let dateString1 = getFormatDateString(dd),
        dateString2 = getFormatDateString(dd1)
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.workspaceName}-${this.projectName}/instance/statistics`, {
          dates: dateString1 + ',' + dateString2,
          step: 2,
          stateType: stateType || 'SUCCESS'
        }, 'get').then((data) => {
          cb && cb([data[dateString2], data[dateString1]])
        })
      })
    },
    buildAreaChart(data) {
      this.areaChart = echarts.init(document.getElementById('areaChart'))
      let option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        legend: {
          data: ['昨日', '今日']
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['2时','4时', '6时', '8时', '10时','12时','14时','16时','18时','20时','22时','24时'],
          nameTextStyle: {
            color: 'rgb(150, 150, 150)'
          },
          axisLine: {
            lineStyle: {
              color: 'rgb(237, 237, 237)'
            }
          },
          axisLabel: {
            color: 'rgb(150, 150, 150)'
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          name: '实例数(个)',
          nameTextStyle: {
            color: 'rgb(150, 150, 150)'
          },
          axisLine: {
            lineStyle: {
              color: 'rgb(237, 237, 237)'
            }
          },
          axisLabel: {
            color: 'rgb(150, 150, 150)'
          }
        },
        series: [
          {
            name: '昨日',
            data: data[0],
            type: 'line',
            areaStyle: {},
            color: '#87AEFA'
          },
          {
            name: '今日',
            data: data[1],
            type: 'line',
            areaStyle: {},
            color: '#5AD8A6'
          }
        ]
      };
      this.areaChart.setOption(option)
    },
    buildMixedBarLineChart(data) {
      this.mixedBarLineChart = echarts.init(document.getElementById('mixedBarLine'))

      let option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        /*toolbox: {
          feature: {
            dataView: {show: true, readOnly: false},
            magicType: {show: true, type: ['line', 'bar']},
            restore: {show: true},
            saveAsImage: {show: true}
          }
        },*/
        legend: {
          data: ['实例数', '成功率']
        },
        xAxis: [
          {
            type: 'category',
            data: ['1时', '2时', '3时', '4时', '5时', '6时', '7时', '8时', '9时', '10时', '11时', '12时',
              '13时', '14时', '15时', '16时', '17时', '18时', '19时', '20时', '21时', '22时', '23时', '24时'],
            axisPointer: {
              type: 'shadow'
            },
            nameTextStyle: {
              color: 'rgb(150, 150, 150)'
            },
            axisLine: {
              lineStyle: {
                color: 'rgb(237, 237, 237)'
              }
            },
            axisLabel: {
              color: 'rgb(150, 150, 150)'
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '实例数',
            minInterval: 1,
            /*min: 0,
            max: 250,
            interval: 50,*/
            axisLabel: {
              formatter: '{value} 个',
              color: 'rgb(150, 150, 150)'
            },
            nameTextStyle: {
              color: 'rgb(150, 150, 150)'
            },
            axisLine: {
              lineStyle: {
                color: 'rgb(237, 237, 237)'
              }
            }
          },
          {
            type: 'value',
            name: '成功率',
            //min: 0,
            max: 100,
            interval: 20,
            axisLabel: {
              formatter: '{value} %',
              color: 'rgb(150, 150, 150)'
            },
            nameTextStyle: {
              color: 'rgb(150, 150, 150)'
            },
            axisLine: {
              lineStyle: {
                color: 'rgb(237, 237, 237)'
              }
            }
          }
        ],
        series: [
          {
            name: '实例数',
            type: 'bar',
            data: data.objTotal,
            color: '#87AEFA'
          },
          {
            name: '成功率',
            type: 'line',
            yAxisIndex: 1,
            data: data.successPercent,
            color: '#5AD8A6'
          },
          {
            name: '失败数',
            type: 'line',
            yAxisIndex: 1,
            data: data.failureTotal,
            color: 'rgba(0,0,0,0)'
          },
        ]
      };
      this.mixedBarLineChart.setOption(option)
    },
    showDuration(seconds) {
      let remain = seconds,
        dayUnit = 60*60*24,
        hourUnit = 60*60,
        minuteUnit = 60,
        str = ''
      if (remain / dayUnit >= 1) {
        str += `${parseInt(remain/dayUnit)}天`
        remain = remain % dayUnit
      }
      if (remain / hourUnit >= 1) {
        str += `${parseInt(remain/hourUnit)}小时`
        remain = remain % hourUnit
      }
      if (remain / minuteUnit >= 1) {
        str += `${parseInt(remain/minuteUnit)}分`
        remain = remain % minuteUnit
      }
      if (remain >= 1) {
        str += `${parseInt(remain)}秒`
      }
      return str
    }
  },
  created () {
    //this.dataTime[0] = dayjs().format('YYYY-MM-DD 00:00:00')
    //this.dataTime[1] = dayjs().format('YYYY-MM-DD HH:mm:ss')
    GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
      this.workspaceName = data.workspace.name
      util.checkToken(() => {
        this.getProjectId((id) => {
          this.searchParams.projectId = id
          this.searchParams.startDate = this.dataTime[0]
          this.searchParams.endDate = this.dataTime[1]
        })
        this.$nextTick(() => {
          this.changeDay()
          this.changeState()
          this.getConsumptionData()
        })
      })
    })

  },
  components: {
    mListConstruction,
    mDefineUserCount,
    mProcessStateCount,
    //mTaskStatusCount
  },
  computed: {
    columns() {
      return [
        {
          type: 'index',
          width: 60,
          align: 'center'
        },
        {
          title: '实例名',
          key: 'name',
          //width: 240,
          align: 'center',
        },
        {
          title: this.$t('message.common.projectDetail.status'),
          key: 'state',
          align: 'center',
          width: 80,
          render: (h, scope) => {
            return h('span', {}, tasksState[scope.row.state].desc);
          },
        },
        {
          title: '耗时',
          key: 'duration',
          width: 210,
          align: 'center',
          render: (h, scope) => {
            return h('span', {}, this.showDuration(scope.row.duration))
          },
        }
      ]
    }
  }
}
</script>

<style lang="scss" rel="stylesheet/scss">
@import '@dataspherestudio/shared/common/style/variables.scss';
  .perject-home-content {
    padding: 10px 20px;
    position: relative;
    width: 100%;
    .project-select-model {
      position: absolute;
      left: 150px;
      top: -40px;
    }
    .time-model {
      position: relative;
      top: -60px;
      left: calc(100% - 230px);
    }
    .chart-title {
      padding: 0 30px;
      text-align: left;
      height: 60px;
      line-height: 60px;
      border-bottom: 1px solid #DEE4EC;
      span {
        font-size: 16px;
        @include font-color($workspace-title-color, $dark-workspace-title-color);
      }
    }
    .ivu-picker-panel-body {
      span {
        font-size: 12px;
      }
    }
    .ivu-btn-primary {
      >span {
          color: #fff;
       }
    }
  }
  .table-small-model {
    padding: 0 10px;
    table {
      width: 100%;
      @include font-color($light-text-color, $dark-text-color);
      tr {
        td {
          height: 32px;
          line-height: 32px;
          border-bottom: 1px solid #ecedec;
        }
      }
    }
    .ellipsis {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space:  nowrap;
      display: block;
    }
  }

  .dashboard-module{
    &.col-md-6 {
      width: calc(50% - 10px);
    }
    background: #FFFFFF;
    border: 1px solid #DEE4EC;
    border-radius: 2px;
    padding: 0;
    position: relative;
    margin-bottom: 24px;
    min-width: 570px;
    .dashboard-module-content {
      padding: 30px;
      position: relative;
      margin:0 0 24px;
    }
    .day-change {
      float: right;
      >div {
        height: 60px;
        margin: 0 10px;
        padding: 0 3px;
        display: inline-block;
        cursor: pointer;
        &.selected {
           border-bottom: 2px solid #2E92F7;
        }
      }
    }
  }
  .consumption-table {
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
    .ivu-table {
      overflow-y: auto;
      overflow-x: hidden;
    }
  }
</style>
