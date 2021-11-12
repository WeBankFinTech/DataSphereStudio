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
        <div class="time-model">
          <template>
            <Date-picker
              style="width: 450px"
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
        <div class="row" >
          <div class="col-md-6">
            <div class="chart-title">
              <span>{{$t('message.scheduler.processStatusStatistics')}}</span>
            </div>
            <div class="row">
              <m-process-state-count :search-params="searchParams" @goToList="goToList">
              </m-process-state-count>
            </div>
          </div>
          <!--<div class="col-md-6">
            <div class="chart-title">
              <span>{{$t('message.scheduler.taskStatusStatistics')}}</span>
            </div>
            <div class="row">
              <m-task-status-count :search-params="searchParams" @goToList="goToList">
              </m-task-status-count>
            </div>
          </div>-->
        </div>
        <div class="row">
          <div class="col-md-12">
            <div class="chart-title" style="margin-bottom: -20px;margin-top: 30px">
              <span>{{$t('message.scheduler.processDefinitionStatistics')}}</span>
            </div>
            <div>
              <m-define-user-count :project-id="searchParams.projectId" @goToList="goToList">
              </m-define-user-count>
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
      projectList: []
    }
  },
  props: {

  },
  methods: {
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
        let searchVal = `${this.workspaceName}-${this.projectName}`
        api.fetch(`dolphinscheduler/projects/list-paging`, {
          pageSize: 100,
          pageNo: 1,
          searchVal: ''
        }, 'get').then(res => {
          this.projectList = res.totalList
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
    getMixedBarLineData(currentDay) {
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.workspaceName}-${this.projectName}/instance/list-paging`, {
          pageSize: 1000,
          pageNo: 1,
          startDate: `${currentDay} 00:00:00`,
          endDate: `${currentDay} 23:59:59`,
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.scheduleTime = formatDate(item.scheduleTime)
            item.startTime = formatDate(item.startTime)
            item.endTime = formatDate(item.endTime)
          })
        })
      })

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
      top: -40px;
    }
    .chart-title {
      text-align: center;
      height: 60px;
      line-height: 60px;
      span {
        font-size: 22px;
        // color: #333;
        @include font-color($workspace-title-color, $dark-workspace-title-color);
        font-weight: bold;
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
</style>
