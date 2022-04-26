<template>
  <m-list-construction :title="$t('message.scheduler.Gantt')">
    <template slot="content">
      <div class="gantt-model">
        <div class="gantt-state">
          <div class="state-tasks-color-sp">
            <a href="javascript:">
              <span>{{$t('message.scheduler.taskStatus')}}</span>
            </a>
            <a href="javascript:" v-for="(item) in tasksState" :key="item.id">
              <em class="ivu-icon ivu-icon-md-cube" :style="{color:item.color}"></em>
              <span>{{item.desc}}</span>
            </a>
          </div>
        </div>
        <template v-if="!isNodata">
          <div class="gantt"></div>
        </template>
        <template v-else>
          <m-no-data></m-no-data>
        </template>
        <m-spin :is-spin="isLoading">
        </m-spin>
      </div>
    </template>
  </m-list-construction>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api'
import { GetWorkspaceData } from '@dataspherestudio/shared/common/service/apiCommonMethod.js'
import Gantt from './source/gantt'
import mSpin from '../components/spin/spin'
import mNoData from '../components/noData/noData'
import { tasksStateList } from '../config'
import mListConstruction from '../components/listConstruction/listConstruction'

export default {
  name: 'instance-gantt-index',
  data () {
    return {
      // Node state
      tasksState: tasksStateList,
      // loading
      isLoading: true,
      // gantt data
      ganttData: {
        taskNames: []
      },
      // Data available
      isNodata: false,
      workspaceName: ''
    }
  },
  props: {
    instanceId: Number
  },
  methods: {
    /**
     * get data
     */
    _getViewGantt () {
      this.isLoading = true
      api.fetch(`dolphinscheduler/projects/${this.projectName}/instance/view-gantt`, {
        processInstanceId: this.instanceId
      }, 'get').then((res) => {
        this.ganttData = res
        if (!res.taskNames.length || !res) {
          this.isLoading = false
          this.isNodata = true
          return
        }
        // Gantt
        Gantt.init({
          el: '.gantt',
          tasks: res.tasks
        })
        setTimeout(() => {
          this.isLoading = false
        }, 200)
      }).catch(() => {
        this.isLoading = false
      })
    }
  },
  watch: {},
  created () {},
  mounted () {
    GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
      this.workspaceName = data.workspace.name
      this._getViewGantt()
    })
  },
  updated () {
  },
  beforeDestroy () {
  },
  destroyed () {
  },
  computed: {
    projectName() {
      return `${this.workspaceName}-${this.$route.query.projectName}`
    }
  },
  components: { mListConstruction, mSpin, mNoData }
}
</script>

<style lang="scss" rel="stylesheet/scss">
.d3-toottip {
  text-align: left;
  ul {
    li {
      overflow: hidden;
      span {
        &.sp1 {
          width: 70px;
          text-align: right;
          display: inline-block;
          padding-right: 6px;
        }
      }
    }
  }
}
.gantt-model {
  background: url('img/dag_bg.png');
  height: 100%;
  width: 100%;
  .gantt-state {
    background: #fff;
    height: 37px;
    line-height: 18px;
    padding-left: 20px;
  }
  .gantt {
    height: 64vh;
    overflow-y: scroll;
  }
  rect {
    cursor: pointer;
  }
  path {
    &.link{
      fill: none;
      stroke: #666;
      stroke-width: 2px;
    }
  }
  g.tick line{
    shape-rendering: crispEdges;
  }
  .axis {
    path,line {
      fill: none;
      stroke: #000;
      shape-rendering: crispEdges;
    }
    text {
      font: 11px sans-serif;
    }
  }
  circle {
    stroke: #666;
    fill: #0097e0;
    stroke-width: 1.5px;
  }
  g.axis path {
    shape-rendering: crispEdges;
  }
}

.state-tasks-color-sp {
  >a {
    display: inline-block;
    margin-right: 10px;
    cursor:default;
    &:hover {
      span {
        color: #333;
      }
    }
    >i {
      border-radius: 10px;
      display: inline-block;
      vertical-align: middle;
      font-size: 14px;
    }
    span {
      vertical-align: middle;
      font-size: 12px;
    }
  }
}

</style>
