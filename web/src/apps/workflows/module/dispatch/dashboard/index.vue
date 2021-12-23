<template>
  <m-list-construction :title="searchParams.projectId ? $t('message.scheduler.projectHome') : $t('message.scheduler.home')">
    <template slot="content">
      <div class="perject-home-content">
        <!--<div class="project-select-model">
          <Select v-model="projectId"
                  @on-change="changeProject"
                  style="width: 300px;float: right;margin-right: 10px;"
          >
            <Option v-for="item in projectList" :value="item.id" :key="item.id">{{item.name}}</Option>
          </Select>
        </div>-->
        <div class="row" >
          <div class="col-md-6 dashboard-module">
            <div class="chart-title">
              <span>{{$t('message.scheduler.processStatusStatistics')}}</span>
              <div class="time-model">
                <template>
                  <Date-picker
                    style="width: 430px"
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
        </div>
        <div class="row">
          <div class="col-md-12 dashboard-module">
            <div class="chart-title">
              <!--<span>{{$t('message.scheduler.taskStatusStatistics')}}</span>-->
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
        <div class="row">
          <div class="col-md-12 dashboard-module">
            <div class="chart-title">
              <span>{{$t('message.scheduler.processDefinitionStatistics')}}</span>
            </div>
            <div class="dashboard-module-content">
              <m-define-user-count :project-id="searchParams.projectId" @goToList="goToList">
              </m-define-user-count>
              <Spin size="large" fix v-if="loading"></Spin>
            </div>
          </div>
        </div>
      </div>
    </template>
  </m-list-construction>
</template>
<script>
import api from '@/common/service/api'
import util from "@/common/util"
//import dayjs from 'dayjs'
import mDefineUserCount from './source/defineUserCount'
import mProcessStateCount from './source/processStateCount'
//import mTaskStatusCount from './source/taskStatusCount'
import mListConstruction from '../components/listConstruction/listConstruction'
import { GetWorkspaceData } from '@/common/service/apiCommonMethod.js'
import {formatDate} from '../convertor'

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
      daySelected: 1,
      loading: false
    }
  },
  props: {

  },
  methods: {
    changeDay(day){
      if (day)
        this.daySelected = day
      let dd = new Date(),
        tYear, tMonth, tDay
      if (this.daySelected === 1) {
        tYear = dd.getFullYear(),
        tMonth = dd.getMonth() + 1 > 9 ? dd.getMonth() + 1 : '0' + (dd.getMonth() + 1),
        tDay = dd.getDate()
      } else if (this.daySelected === 2) {
        dd.setDate(dd.getDate() - 1)
        tYear = dd.getFullYear(),
        tMonth = dd.getMonth() + 1 > 9 ? dd.getMonth() + 1 : '0' + (dd.getMonth() + 1),
        tDay = dd.getDate()
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
    changeProject() {
      util.checkToken(() => {
        this.getProjectId((id) => {
          this.searchParams.projectId = id
          this.searchParams.startDate = this.dataTime[0]
          this.searchParams.endDate = this.dataTime[1]
        })
      })
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
            let curHour = new Date(item.startTime).getHours() + 1
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
              successPercent[index] = (success / objTotal[index]).toFixed(1)
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
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '实例数',
            /*min: 0,
            max: 250,
            interval: 50,*/
            axisLabel: {
              formatter: '{value} 个'
            }
          },
          {
            type: 'value',
            name: '成功率',
            /*min: 0,
            max: 25,
            interval: 5,*/
            axisLabel: {
              formatter: '{value} %'
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
        })
      })
    })

  },
  components: {
    mListConstruction,
    mDefineUserCount,
    mProcessStateCount,
    //mTaskStatusCount
  }
}
</script>

<style lang="scss" rel="stylesheet/scss">
@import '@/common/style/variables.scss';
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
      position: absolute;
      right: 8px;
      top: 0;
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
           border-bottom: 2px solid #2A6F97;
        }
      }
    }
  }
</style>
