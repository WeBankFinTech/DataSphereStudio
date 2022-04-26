<template>
  <div class="task-status-count-model">
    <div v-show="!msg">
      <div class="data-area" style="height: 430px;">
        <div class="col-md-7">
          <div id="task-status-pie" style="height:260px;margin-top: 100px;"></div>
        </div>
        <div class="col-md-5">
          <div class="table-small-model">
            <table>
              <tr>
                <th width="40">{{$t('message.scheduler.header.id')}}</th>
                <th>{{$t('message.scheduler.header.Number')}}</th>
                <th>{{$t('message.scheduler.header.State')}}</th>
              </tr>
              <tr v-for="(item,$index) in taskStatusList" :key="$index">
                <td><span>{{$index+1}}</span></td>
                <td>
                  <span>
                    <a href="javascript:" @click="searchParams.projectId && _goTask(item.key)" :class="searchParams.projectId ?'links':''">{{item.value}}</a>
                  </span>
                </td>
                <td><span class="ellipsis" style="width: 98%;" :title="item.key">{{item.key}}</span></td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>
    <div v-show="msg">
      <m-no-data :msg="msg" v-if="msg" :height="430"></m-no-data>
    </div>
  </div>
</template>
<script>
import _ from 'lodash'
import api from '@dataspherestudio/shared/common/service/api'
import { pie } from './chartConfig'
import Chart from '../../components/ana-charts'
import mNoData from '../../components/noData/noData'
import { tasksStateList } from '../../config'
import util from '@dataspherestudio/shared/common/util'
export default {
  name: 'task-status-count',
  data () {
    return {
      isSpin: true,
      msg: '',
      taskStatusList: []
    }
  },
  props: {
    searchParams: Object
  },
  methods: {
    _goTask (name) {
      this.$emit('goToList', 5, {
        stateType: _.find(tasksStateList, ['desc', name]).code,
        startDate: this.searchParams.startDate,
        endDate: this.searchParams.endDate
      })
    },
    _handleTaskStatus (res) {
      let data = res.taskCountDtos
      this.taskStatusList = _.map(data, v => {
        return {
          // CHECK!!
          key: _.find(tasksStateList, ['code', v.taskStateType]).desc,
          value: v.count,
          type: 'type'
        }
      })
      const myChart = Chart.pie('#task-status-pie', this.taskStatusList, { title: '' })
      myChart.echart.setOption(pie)

      // Jump forbidden in index page
      if (this.searchParams.projectId) {
        myChart.echart.on('click', e => {
          this._goTask(e.data.name)
        })
      }
    }
  },
  watch: {
    searchParams: {
      deep: true,
      immediate: true,
      handler (o) {
        this.isSpin = true
        util.checkToken(() => {
          api.fetch(`dolphinscheduler/projects/analysis/task-state-count`, o, 'get').then(res => {
            this.taskStatusList = []
            this._handleTaskStatus(res)
            this.isSpin = false
          }).catch(() => {
            this.isSpin = false
          })
        })
      }
    }
  },
  beforeCreate () {
  },
  created () {
  },
  beforeMount () {
  },
  mounted () {
  },
  beforeUpdate () {
  },
  updated () {
  },
  beforeDestroy () {
  },
  destroyed () {
  },
  computed: {},
  components: { mNoData }
}
</script>
