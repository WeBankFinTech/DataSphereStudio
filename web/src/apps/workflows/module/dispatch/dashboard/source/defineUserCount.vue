<template>
  <div class="define-user-count-model">
    <div v-if="msg">
      <div class="data-area">
        <div id="process-definition-bar" style="height:330px"></div>
      </div>
    </div>
    <div v-else>
      <m-no-data :height="330"></m-no-data>
    </div>
  </div>
</template>
<script>
import _ from 'lodash'
import api from '@/common/service/api'
import { bar } from './chartConfig'
import Chart from '../../components/ana-charts'
import mNoData from '../../components/noData/noData'
import util from "@/common/util"
export default {
  name: 'define-user-count',
  data () {
    return {
      isSpin: true,
      msg: true,
      parameter: {}
    }
  },
  props: {
    projectId: Number,
    workspaceName: String
  },
  methods: {
    _handleDefineUser (res) {
      let data = res.userList || []
      this.defineUserList = _.map(data, v => {
        return {
          key: v.userName + ',' + v.userId + ',' + v.count,
          value: v.count
        }
      })
      const myChart = Chart.bar('#process-definition-bar', this.defineUserList, {barColor: '#89C2D9', yAxis: {
        minInterval: 1,
        axisLabel: {
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
      }})
      myChart.echart.setOption(bar)
      // Jump not allowed on home page
      if (this.projectId) {
        myChart.echart.on('click', e => {
          this.$emit('goToList', 1, {
            userId: e.name.split(',')[1]
          })
        })
      }
    }
  },
  watch: {
    projectId: {
      deep: true,
      immediate: true,
      handler () {
        if (!this.workspaceName) return
        this.isSpin = true
        this.parameter.projectId = this.projectId
        let url, params = {}
        if (this.projectId != 0) {
          url = `dolphinscheduler/projects/analysis/define-user-count`
          params = this.parameter
        } else {
          url = `dolphinscheduler/workspaces/${this.workspaceName}/instance/define-user-count`
        }
        util.checkToken(() => {
          api.fetch(url, params, 'get').then(res => {
            this.msg = res.count > 0
            this.processStateList = []
            this.defineUserList = []
            this._handleDefineUser(res)
          }).catch(() => {
            this.isSpin = false
          })
        })
      }
    }
  },
  created () {

  },
  mounted () {
  },
  components: { mNoData }
}
</script>
