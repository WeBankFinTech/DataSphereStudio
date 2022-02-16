<template>
  <div class="process-state-count-model">
    <div v-show="!msg">
      <div class="data-area" style="height: 230px;">
        <div class="col-md-8">
          <div id="process-state-pie" style="width:100%;height:250px;"></div>
        </div>
        <div class="col-md-4">
          <div class="table-small-model">
            <table>
              <!--<tr>-->
                <!--<th width="40">{{$t('message.scheduler.header.id')}}</th>-->
                <!--<th>{{$t('message.scheduler.header.State')}}</th>-->
                <!--<th>{{$t('message.scheduler.header.Number')}}</th>-->
              <!--</tr>-->
              <tr v-for="(item,$index) in processStateList" :key="$index">
                <!--<td><span>{{$index+1}}</span></td>-->
                <td><span :style="{'display':'inline','height':'5px','width':'5px','borderRadius':'50%','float':'left','marginTop':'13px','marginRight':'5px','backgroundColor':item.color}"></span><span class="ellipsis" style="width: 70%;" :title="item.key">{{item.key}}</span></td>
                <td>
                  <a v-if="currentName === 'home'" style="cursor: default">{{item.value}}</a>
                  <span v-else><a href="javascript:" @click="searchParams.projectId && _goProcess(item.key)">{{item.value}}</a></span>
                </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>
    <div v-show="msg">
      <m-no-data :msg="msg" v-if="msg" :height="230"></m-no-data>
    </div>
  </div>
</template>
<script>
import _ from 'lodash'
import api from '@/common/service/api'
import { pie } from './chartConfig'
import Chart from '../../components/ana-charts'
import mNoData from '../../components/noData/noData'
import { dashboardStateList } from '../../config'
import echarts from 'echarts'
import util from "@/common/util"
export default {
  name: 'process-state-count',
  data () {
    return {
      isSpin: true,
      msg: '',
      processStateList: [],
      currentName: ''
    }
  },
  props: {
    searchParams: Object,
    workspaceName: String
  },
  methods: {
    _goProcess (name) {
      this.$emit('goToList', 2, {
        stateType: _.find(dashboardStateList, ['desc', name]).code,
        startDate: this.searchParams.startDate,
        endDate: this.searchParams.endDate
      })
    },
    _handleProcessState (res) {
      let data = res.taskCountDtos
      this.processStateList = []
      _.map(data, v => {
        if (_.find(dashboardStateList, ['code', v.taskStateType])) {
          this.processStateList.push({
            key: _.find(dashboardStateList, ['code', v.taskStateType]).desc,
            value: v.count,
            color: _.find(dashboardStateList, ['code', v.taskStateType]).color
          })
        }
      })
      let totalCount = 0
      this.processStateList.forEach(item => {
        totalCount += item.value
      })
      const myChart = Chart.pie('#process-state-pie', this.processStateList, { title: `流程状态数量\n${totalCount}`,
        ring: true})
      myChart.echart.setOption(pie)
      // 首页不允许跳转
      if (this.searchParams.projectId) {
        myChart.echart.on('click', e => {
          this._goProcess(e.data.name)
        })
      }
    }
  },
  watch: {
    searchParams: {
      deep: true,
      immediate: true,
      handler (o) {
        if (!this.workspaceName) return
        this.isSpin = true
        util.checkToken(() => {
          let url, params = {}
          if (o.projectId != 0) {
            url = `dolphinscheduler/projects/analysis/process-state-count`
            params = o
          } else {
            url = `dolphinscheduler/workspaces/${this.workspaceName}/instance/process-state-count`
          }
          api.fetch(url, params, 'get').then(res => {
            this.processStateList = []
            this._handleProcessState(res)
            this.isSpin = false
          }).catch(() => {
            this.isSpin = false
          })
        })
      }
    },
    '$store.state.projects.sideBar': function () {
      echarts.init(document.getElementById('process-state-pie')).resize()
    }
  },
  beforeCreate () {
  },
  created () {
    this.currentName = this.$router.currentRoute.name
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

<style lang="scss" rel="stylesheet/scss" scoped>
  td {
    border-bottom: none !important;
  }
  .table-small-model {
    margin-top: 30px;
  }
</style>
