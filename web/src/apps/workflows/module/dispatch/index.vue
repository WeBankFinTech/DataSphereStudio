<template>
  <div>
    <template>
      <div class="scheduler-wrapper">
        <div class="scheduler-menu">
          <ul>
            <li :class="activeDS == 1 || activeDS == 3? 'active' : ''" @click="activeList(1)">任务列表</li>
            <li :class="activeDS == 2? 'active' : ''" @click="activeList(2)">实例列表</li>
          </ul>
        </div>
        <div class="scheduler-list" v-if="activeDS == 1">
          <template>
            <Table class="scheduler-table" :columns="columns" :data="list"></Table>
            <Page
              size="small"
              v-if="list.length > 0"
              class="page-bar fr"
              :total="pagination.total"
              show-sizer
              show-total
              :current="pagination.current"
              :page-size="pagination.size"
              :page-size-opts="pagination.opts"
              @on-change="pageChange"
              @on-page-size-change="pageSizeChange"
            ></Page>
          </template>
        </div>
        <div class="scheduler-list" v-if="activeDS == 2">
          <template>
            <Table class="scheduler-table" :columns="columns2" :data="list2"></Table>
            <Page
              size="small"
              v-if="list2.length > 0"
              class="page-bar fr"
              :total="pagination2.total"
              show-sizer
              show-total
              :current="pagination2.current"
              :page-size="pagination2.size"
              :page-size-opts="pagination2.opts"
              @on-change="pageChange2"
              @on-page-size-change="pageSizeChange2"
            ></Page>
          </template>
        </div>
        <div class="scheduler-list" v-if="activeDS == 3">
          <template>
            <Table class="scheduler-table" :columns="columns3" :data="list3"></Table>
            <Page
              size="small"
              v-if="list3.length > 0"
              class="page-bar fr"
              :total="pagination3.total"
              show-sizer
              show-total
              :current="pagination3.current"
              :page-size="pagination3.size"
              :page-size-opts="pagination3.opts"
              @on-change="pageChange3"
              @on-page-size-change="pageSizeChange3"
            ></Page>
          </template>
        </div>
        <div :class="getPanelClass()" v-if="activeDS == 2">
          <SvgIcon @click="changePanel(true)" style="font-size: 40px;left: -12px;top: 45%;position: absolute;cursor: pointer;z-index: 5;" v-if="showDag === 1" icon-class="panel-full"/>
          <SvgIcon @click="changePanel(false)" style="font-size: 40px;left: -12px;top: 37%;position: absolute;cursor: pointer;z-index: 5;" v-if="showDag === 1" icon-class="panel-close"/>
          <SvgIcon @click="changePanel(false)" style="font-size: 80px;left: -32px;top: 35%;position: absolute;cursor: pointer;z-index: 5;" v-if="showDag === 2" icon-class="panel-partial"/>
          <div class="dag-page">
            <Dag :dagData="dagData" :processId="dagProcessId" v-if="showDag"></Dag>
          </div>
        </div>
      </div>
    </template>
    <Modal
      :title="$t('message.scheduler.runTask.startTitle')"
      v-model="showRunTaskModal"
      width="550"
      :mask-closable="false">
      <i-run :startData="startData" @onUpdateStart="runTask" @closeStart="closeRun"></i-run>
      <div slot="footer" style="height: 30px;">
      </div>
    </Modal>
    <Modal
      :title="$t('message.scheduler.runTask.timingTitle')"
      v-model="showTimingTaskModal"
      width="550"
      :mask-closable="false">
      <i-timing :timingData="timingData" @onUpdateTiming="setTiming" @closeTiming="closeTiming"></i-timing>
      <div slot="footer" style="height: 30px;">
      </div>
    </Modal>
    <m-log v-if="showLog" :key="logId" :item="logData" :source="source" :logId="logId" @ok="showLog=false" @close="showLog=false"></m-log>
  </div>
</template>
<script>
import api from "@/common/service/api";
import util from "@/common/util";
import iRun from './run'
import iTiming from './timing'
import dayjs from 'dayjs'
import _ from 'lodash'
import Dag from './dag'
import {ds2butterfly} from './convertor'
import mLog from './log/log'
import { GetWorkspaceData } from '@/common/service/apiCommonMethod.js'

export default {
  name: 'dispatch',
  components: {
    iRun,
    iTiming,
    Dag,
    mLog
  },
  props: {
    query: {
      type: Object,
      default: () => {}
    }
  },
  computed: {
    projectName() {
      return `${this.workspaceName}-${this.$route.query.projectName}`
    }
  },
  data() {
    return {
      workspaceName: '',
      activeDS: 1,
      showRunTaskModal: false,
      showTimingTaskModal: false,
      list: [],
      list2: [],
      list3: [],
      columns: [
        {
          title: this.$t('message.scheduler.header.id'),
          width: 100,
          key: 'id'
        },
        {
          title: this.$t('message.scheduler.header.ProcessName'),
          width: 200,
          key: 'name'
        },
        {
          title: this.$t('message.scheduler.header.State'),
          width: 100,
          key: 'releaseStateDesc'
        },
        {
          title: this.$t('message.scheduler.header.CreateTime'),
          width: 100,
          key: 'createTime'
        },
        {
          title: this.$t('message.scheduler.header.UpdateTime'),
          width: 100,
          key: 'updateTime'
        },
        {
          title: this.$t('message.scheduler.header.Description'),
          width: 200,
          key: 'description'
        },
        {
          title: this.$t('message.scheduler.header.ModifyBy'),
          width: 100,
          key: 'modifyBy'
        },
        {
          title: this.$t('message.scheduler.header.TimingState'),
          width: 100,
          key: 'scheduleReleaseStateDesc'
        },
        {
          title: this.$t('message.scheduler.header.Operation'),
          key: 'action',
          fixed: 'right',
          width: 250,
          align: 'center',
          render: (h, params) => {
            return  h('div', [
              h('Button', {
                props: {
                  type: 'success',
                  shape: "circle",
                  icon: "md-arrow-dropright",
                  size: 'small',
                  disabled: !params.row.isOnline
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.run')
                },
                on: {
                  click: () => {
                    this.run(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'info',
                  shape: "circle",
                  icon: "md-alarm",
                  size: 'small',
                  disabled: !params.row.isOnline
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.setTime')
                },
                on: {
                  click: () => {
                    this.openTiming(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: params.row.isOnline ? 'error' : 'warning',
                  shape: "circle",
                  icon: "md-power",
                  size: 'small'
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: params.row.isOnline ? this.$t('message.scheduler.offline') : this.$t('message.scheduler.online'),
                  style: params.row.isOnline ? 'transform: rotate(180deg);line-height: 25px;margin-right: 5px;' : 'margin-right: 5px;'
                },
                on: {
                  click: () => {
                    params.row.isOnline ? this._offline(params.index) : this._online(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'info',
                  shape: "circle",
                  icon: "md-copy",
                  size: 'small',
                  disabled: params.row.isOnline
                },
                style: {
                  marginRight: '5px',
                  display: 'none'
                },
                attrs: {
                  title: this.$t('message.scheduler.copy')
                },
                on: {
                  click: () => {
                    this._copy(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'info',
                  shape: "circle",
                  icon: "md-calendar",
                  size: 'small',
                  disabled: !params.row.isOnline
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.setTime')
                },
                on: {
                  click: () => {
                    this.queryTimingList(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'error',
                  shape: "circle",
                  icon: "md-pint",
                  size: 'small',
                  disabled: params.row.isOnline
                },
                style: {
                  marginRight: '5px',
                  display: 'none'
                },
                attrs: {
                  title: this.$t('message.scheduler.DELETE')
                },
                on: {
                  click: () => {
                    this._deleteWorkflow(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'info',
                  shape: "circle",
                  icon: "md-cloud-download",
                  size: 'small'
                },
                style: {
                  marginRight: '5px',
                  display: 'none'
                },
                attrs: {
                  title: this.$t('message.scheduler.export')
                },
                on: {
                  click: () => {
                    this.exportDefinition(params.index)
                  }
                }
              })
            ]);
          }
        }
      ],
      columns2: [
        {
          title: this.$t('message.scheduler.header.id'),
          width: 100,
          key: 'id'
        },
        {
          title: this.$t('message.scheduler.header.ProcessName'),
          width: 200,
          key: 'name',
          render: (h, params) => {
            return h('div', [
              h('a', {
                props: {
                  href: '#'
                },
                on: {
                  click: () => {
                    this.openDag(params.index)
                  }
                }
              }, params.row.name)
            ])
          }
        },
        {
          title: this.$t('message.scheduler.header.State'),
          key: 'stateDesc',
          width: 100,
          render: (h, params) => {
            return h('div', [
              h('Icon', {
                props: {
                  custom: "iconfont " + params.row.stateIcon,
                  color: params.row.stateColor,
                  size: 15
                },
                attrs: {
                  title: params.row.stateDesc
                }
              })
            ])
          }
        },
        {
          title: this.$t('message.scheduler.header.RunType'),
          width: 100,
          key: 'commandTypeDesc'
        },
        {
          title: this.$t('message.scheduler.header.SchedulingTime'),
          width: 100,
          key: 'scheduleTime'
        },
        {
          title: this.$t('message.scheduler.header.StartTime'),
          width: 100,
          key: 'startTime'
        },
        {
          title: this.$t('message.scheduler.header.EndTime'),
          width: 100,
          key: 'endTime'
        },
        {
          title: this.$t('message.scheduler.header.Duration') + 's',
          width: 100,
          key: 'duration'
        },
        {
          title: this.$t('message.scheduler.header.RunTimes'),
          width: 100,
          key: 'runTimes'
        },
        {
          title: this.$t('message.scheduler.header.FaultTolerantSign'),
          width: 100,
          key: 'recovery'
        },
        {
          title: this.$t('message.scheduler.header.Executor'),
          width: 150,
          key: 'executorName'
        },
        {
          title: 'host',
          width: 150,
          key: 'host'
        },
        {
          title: this.$t('message.scheduler.header.Operation'),
          key: 'action',
          width: 200,
          fixed: 'right',
          align: 'center',
          render: (h, params) => {
            return  h('div', [
              h('Button', {
                props: {
                  type: 'info',
                  shape: "circle",
                  icon: "md-refresh",
                  size: 'small',
                  disabled: params.row.disabled || (params.row.state !== 'SUCCESS' && params.row.state !== 'PAUSE' && params.row.state !== 'FAILURE' && params.row.state !== 'STOP')
                },
                attrs: {
                  title: this.$t('message.scheduler.rerun')
                },
                style: {
                  marginRight: '5px'
                },
                on: {
                  click: () => {
                    this._rerun(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'success',
                  shape: "circle",
                  icon: "md-backspace",
                  size: 'small',
                  disabled: params.row.disabled || params.row.state !== 'FAILURE'
                },
                attrs: {
                  title: this.$t('message.scheduler.recovery')
                },
                style: {
                  marginRight: '5px'
                },
                on: {
                  click: () => {
                    this._recovery(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'error',
                  shape: "circle",
                  icon: params.row.state === 'STOP' ? "md-play" : "md-close",
                  size: 'small',
                  disabled: params.row.disabled || (params.row.state !== 'RUNNING_EXECUTION' && params.row.state !== 'RUNNING_EXEUTION' && params.row.state !== 'STOP')
                },
                attrs: {
                  title: params.row.state === 'STOP' ? this.$t('message.scheduler.recoverySuspend') : this.$t('message.scheduler.STOP')
                },
                style: {
                  marginRight: '5px'
                },
                on: {
                  click: () => {
                    this._stop(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'warning',
                  shape: "circle",
                  icon: params.row.state === 'PAUSE' ? "md-play" : "md-pause",
                  size: 'small',
                  disabled: params.row.disabled || (params.row.state !== 'RUNNING_EXECUTION' && params.row.state !== 'RUNNING_EXEUTION' && params.row.state !== 'PAUSE')
                },
                attrs: {
                  title: params.row.state === 'PAUSE' ? this.$t('message.scheduler.recoverySuspend') : this.$t('message.scheduler.PAUSE')
                },
                style: {
                  marginRight: '5px'
                },
                on: {
                  click: () => {
                    this._suspend(params.index)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'error',
                  shape: "circle",
                  icon: "md-pint",
                  size: 'small',
                  disabled: params.row.state !== 'SUCCESS' && params.row.state !== 'FAILURE' && params.row.state !== 'STOP' && params.row.state !== 'PAUSE'
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.DELETE')
                },
                on: {
                  click: () => {
                    this._deleteInstance(params.index)
                  }
                }
              })
            ]);
          }
        }
      ],
      columns3: [
        {
          title: this.$t('message.scheduler.header.id'),
          width: 100,
          key: 'id'
        },
        {
          title: this.$t('message.scheduler.header.ProcessName'),
          width: 200,
          key: 'processDefinitionName'
        },
        {
          title: this.$t('message.scheduler.header.StartTime'),
          width: 100,
          key: 'startTime'
        },
        {
          title: this.$t('message.scheduler.header.EndTime'),
          width: 100,
          key: 'endTime'
        },
        {
          title: this.$t('message.scheduler.header.crontab'),
          width: 150,
          key: 'crontab'
        },
        {
          title: this.$t('message.scheduler.header.failureStrategy'),
          width: 100,
          key: 'failureStrategy'
        },
        {
          title: this.$t('message.scheduler.header.State'),
          width: 100,
          key: 'releaseStateDesc'
        },
        {
          title: this.$t('message.scheduler.header.CreateTime'),
          width: 100,
          key: 'createTime'
        },
        {
          title: this.$t('message.scheduler.header.UpdateTime'),
          width: 100,
          key: 'updateTime'
        },
        {
          title: this.$t('message.scheduler.header.Operation'),
          key: 'action',
          fixed: 'right',
          width: 250,
          align: 'center',
          render: (h, params) => {
            return  h('div', [
              h('Button', {
                props: {
                  type: 'info',
                  shape: "circle",
                  icon: "md-create",
                  size: 'small',
                  disabled: params.row.isOnline
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.runTask.edit')
                },
                on: {
                  click: () => {
                    this.openTiming(params.row, true)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: params.row.isOnline ? 'error' : 'warning',
                  shape: "circle",
                  icon: "md-power",
                  size: 'small'
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: params.row.isOnline ? this.$t('message.scheduler.offline') : this.$t('message.scheduler.online'),
                  style: params.row.isOnline ? 'transform: rotate(180deg);line-height: 25px;margin-right: 5px;' : 'margin-right: 5px;'
                },
                on: {
                  click: () => {
                    params.row.isOnline ? this.timingOffline(params.row.id) : this.timingOnline(params.row.id)
                  }
                }
              }),
              h('Button', {
                props: {
                  type: 'error',
                  shape: "circle",
                  icon: "md-pint",
                  size: 'small',
                  disabled: params.row.isOnline
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.DELETE')
                },
                on: {
                  click: () => {
                    this.deleteScheduler(params.index)
                  }
                }
              })
            ]);
          }
        }
      ],
      startData: {},
      timingData: {
        item: {},
        type: ''
      },
      pagination: {
        size: 10,
        opts: [5, 10, 30, 45, 60],
        current: 1,
        total: 0
      },
      pagination2: {
        size: 10,
        opts: [5, 10, 30, 45, 60],
        current: 1,
        total: 0
      },
      pagination3: {
        size: 10,
        opts: [5, 10, 30, 45, 60],
        current: 1,
        total: 0
      },
      publishStatus: {
        'NOT_RELEASE': this.$t('message.scheduler.Unpublished'),
        'ONLINE': this.$t('message.scheduler.online'),
        'OFFLINE': this.$t('message.scheduler.offline')
      },
      runningType: [
        {
          desc: this.$t('message.scheduler.START_PROCESS'),
          code: 'START_PROCESS'
        },
        {
          desc: this.$t('message.scheduler.START_CURRENT_TASK_PROCESS'),
          code: 'START_CURRENT_TASK_PROCESS'
        },
        {
          desc: this.$t('message.scheduler.RECOVER_TOLERANCE_FAULT_PROCESS'),
          code: 'RECOVER_TOLERANCE_FAULT_PROCESS'
        },
        {
          desc: this.$t('message.scheduler.RECOVER_SUSPENDED_PROCESS'),
          code: 'RECOVER_SUSPENDED_PROCESS'
        },
        {
          desc: this.$t('message.scheduler.START_FAILURE_TASK_PROCESS'),
          code: 'START_FAILURE_TASK_PROCESS'
        },
        {
          desc: this.$t('message.scheduler.COMPLEMENT_DATA'),
          code: 'COMPLEMENT_DATA'
        },
        {
          desc: this.$t('message.scheduler.SCHEDULER'),
          code: 'SCHEDULER'
        },
        {
          desc: this.$t('message.scheduler.rerun'),
          code: 'REPEAT_RUNNING'
        },
        {
          desc: this.$t('message.scheduler.PAUSE'),
          code: 'PAUSE'
        },
        {
          desc: this.$t('message.scheduler.STOP'),
          code: 'STOP'
        },
        {
          desc: this.$t('message.scheduler.RECOVER_WAITTING_THREAD'),
          code: 'RECOVER_WAITTING_THREAD'
        }
      ],
      tasksState: {
        SUBMITTED_SUCCESS: {
          id: 0,
          desc: this.$t('message.scheduler.tasksState.SUBMITTED_SUCCESS'),
          icon: 'icon-submitted-success',
          color: '#A9A9A9'
        },
        'RUNNING_EXECUTION': {
          id: 1,
          desc: this.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
          icon: 'icon-running-execution',
          color: '#0097e0'
        },
        'RUNNING_EXEUTION': {
          id: 1,
          desc: this.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
          icon: 'icon-running-execution',
          color: '#0097e0'
        },
        READY_PAUSE: {
          id: 2,
          desc: this.$t('message.scheduler.tasksState.READY_PAUSE'),
          icon: 'icon-ready-pause',
          color: '#07b1a3'
        },
        PAUSE: {
          id: 3,
          desc: this.$t('message.scheduler.tasksState.PAUSE'),
          icon: 'icon-pause',
          color: '#057c72'
        },
        READY_STOP: {
          id: 4,
          desc: this.$t('message.scheduler.tasksState.READY_STOP'),
          icon: 'icon-ready-stop',
          color: '#FE0402'
        },
        STOP: {
          id: 5,
          desc: this.$t('message.scheduler.tasksState.STOP'),
          icon: 'icon-stop',
          color: '#e90101'
        },
        FAILURE: {
          id: 6,
          desc: this.$t('message.scheduler.tasksState.FAILURE'),
          icon: 'icon-failure',
          color: '#000000'
        },
        SUCCESS: {
          id: 7,
          desc: this.$t('message.scheduler.tasksState.SUCCESS'),
          icon: 'icon-success',
          color: '#33cc00'
        },
        NEED_FAULT_TOLERANCE: {
          id: 8,
          desc: this.$t('message.scheduler.tasksState.NEED_FAULT_TOLERANCE'),
          icon: 'icon-need-fault-tolerance',
          color: '#FF8C00'
        },
        KILL: {
          id: 9,
          desc: this.$t('message.scheduler.tasksState.KILL'),
          icon: 'icon-kill',
          color: '#a70202'
        },
        WAITTING_THREAD: {
          id: 10,
          desc: this.$t('message.scheduler.tasksState.WAITTING_THREAD'),
          icon: 'icon-waitting-thread',
          color: '#912eed'
        },
        WAITTING_DEPEND: {
          id: 11,
          desc: this.$t('message.scheduler.tasksState.WAITTING_DEPEND'),
          icon: 'icon-watting-depend',
          color: '#5101be'
        },
        DELAY_EXECUTION: {
          id: 12,
          desc: this.$t('message.scheduler.tasksState.DELAY_EXECUTION'),
          icon: 'icon-delay-execution',
          color: '#5102ce'
        },
        FORCED_SUCCESS: {
          id: 13,
          desc: this.$t('message.scheduler.tasksState.FORCED_SUCCESS'),
          icon: 'icon-forced-success',
          color: '#5102ce'
        }
      },
      showDag: 0,
      dagData: {
        nodes: [],
        edges: []
      },
      dagProcessId: '',
      timer: '', //用来轮询工作节点状态的计时器,
      logData: {},
      showLog: false,
      logId: null,
      source: 'list',
      schedulerId: '' //用来查定时管理的id
    }
  },
  mounted() {
    GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
      this.workspaceName = data.workspace.name
      this.getListData()
    })
    util.Hub.$on('dagLog', data => {
      this.getTaskInstanceList(data, (id) => {
        if (!id) {
          this.logId = null
          this.logData = {}
          this.showLog = false
          this.$Message.info(this.$t('message.scheduler.noLog'))
        } else {
          this.logId = id
          this.logData = data
          this.showLog = true
        }
      })
    })
  },
  methods: {
    getTaskInstanceList(data, cb, pageSize=10, pageNo=1) {
      if (!this.dagProcessId) return
      api.fetch(`dolphinscheduler/projects/${this.projectName}/task-instance/list-paging`, {
        processInstanceId: this.dagProcessId,
        pageSize,
        pageNo,
        name: data.label
      }, 'get').then((res) => {
        let list = res.totalList
        let thisTimeList = list.filter(item => item.flag === 'YES')
        for (let i = 0; i < thisTimeList.length; i++) {
          if (thisTimeList[i].name === data.label) {
            return cb && cb(thisTimeList[i].id)
          }
        }
        return cb && cb()
      }).catch(() => {
      })
    },
    getListData(page=1) {
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.projectName}/process/list-paging`, {
          pageSize: this.pagination.size,
          pageNo: page,
          searchVal: this.query.name
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.releaseStateDesc = item.releaseState? this.publishStatus[item.releaseState] : ''
            item.scheduleReleaseStateDesc = item.scheduleReleaseState? this.publishStatus[item.scheduleReleaseState] : '-'
            item.isOnline = item.releaseState === 'ONLINE'
            item.createTime = this.formatDate(item.createTime)
            item.updateTime = this.formatDate(item.updateTime)
            item.disabled = false
          })
          this.list = res.totalList
          this.pagination.total = res.total
        })
      })
    },
    getInstanceListData(page=1) {
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.projectName}/instance/list-paging`, {
          pageSize: this.pagination2.size,
          pageNo: page,
          searchVal: this.query.name
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.scheduleTime = this.formatDate(item.scheduleTime)
            item.startTime = this.formatDate(item.startTime)
            item.endTime = this.formatDate(item.endTime)
            item.duration = this.filterNull(item.duration)
            item.commandTypeDesc = _.filter(this.runningType, v => v.code === item.commandType)[0].desc
            item.stateDesc = this.tasksState[item.state].desc
            item.stateColor = this.tasksState[item.state].color
            item.stateIcon = this.tasksState[item.state].icon
            item.disabled = false
          })
          this.list2 = res.totalList
          this.pagination2.total = res.total
        })
      })
    },
    getSchedulerData(page=1){
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.projectName}/schedule/list-paging`, {
          pageSize: this.pagination3.size,
          pageNo: page,
          searchVal: this.query.name,
          processDefinitionId: this.schedulerId
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.startTime = this.formatDate(item.startTime)
            item.endTime = this.formatDate(item.endTime)
            item.releaseStateDesc = item.releaseState? this.publishStatus[item.releaseState] : ''
            item.isOnline = item.releaseState === 'ONLINE'
            item.createTime = this.formatDate(item.createTime)
            item.updateTime = this.formatDate(item.updateTime)
            item.disabled = false
          })
          this.list3 = res.totalList
          this.pagination3.total = res.total
        })
      })
    },
    checkStart(index, cb){
      api.fetch(`dolphinscheduler/projects/${this.projectName}/executors/start-check`, {
        processDefinitionId: this.list[index].id
      }, {useForm: true}).then(() => {
        cb && cb()
      })
    },
    getReceiver(processDefinitionId, cb){
      api.fetch(`dolphinscheduler/projects/${this.projectName}/executors/get-receiver-cc`, {
        processDefinitionId: processDefinitionId
      }, 'get').then((res) => {
        cb && cb(res)

      })
    },
    run(index) {
      this.checkStart(index, ()=>{
        this.startData = this.list[index]
        this.showRunTaskModal = true
        this.getReceiver(this.list[index].id, (res) => {
          this.startData.receivers = res.receivers
          this.startData.receiversCc = res.receiversCc
        })
      })
    },
    openTiming(index, isEdit) {
      let id
      if (isEdit) {
        this.timingData.item = index
        this.timingData.type = ''
        id = index.processDefinitionId
      } else {
        this.timingData.item = this.list[index]
        this.timingData.type = 'timing'
        id = this.list[index].id
      }
      this.getReceiver(id, (res) => {
        this.timingData.item.receivers = res.receivers
        this.timingData.item.receiversCc = res.receiversCc
      })
      this.showTimingTaskModal = true
    },
    _copy(index) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/process/copy`, {
        processId: this.list[index].id
      }, {useForm: true}).then(() => {
        this.$Message.success(this.$t('message.scheduler.runTask.success'))
        this.getListData()
        util.Hub.$emit('DS-operation', {
          type: 'copy'
        })
      })
    },
    _deleteWorkflow(index) {
      let item = this.list[index]
      this.$Modal.confirm({
        title: this.$t('message.scheduler.DELETE'),
        content: `<p>${this.$t('message.scheduler.delete')}?</p>`,
        okText: this.$t('message.scheduler.ok'),
        cancelText: this.$t('message.scheduler.cancel'),
        onOk: () => {
          api.fetch(`dolphinscheduler/projects/${this.projectName}/process/delete`, {
            processDefinitionId: item.id,
          }, 'get').then(() => {
            this.$Message.success(this.$t('message.scheduler.runTask.success'))
            this.getListData()
          })
        },
        onCancel: () => {}
      })
    },
    _deleteInstance(index) {
      let item = this.list2[index]
      this.$Modal.confirm({
        title: this.$t('message.scheduler.DELETE'),
        content: `<p>${this.$t('message.scheduler.delete')}?</p>`,
        okText: this.$t('message.scheduler.ok'),
        cancelText: this.$t('message.scheduler.cancel'),
        onOk: () => {
          api.fetch(`dolphinscheduler/projects/${this.projectName}/instance/delete`, {
            processInstanceId: item.id,
          }, 'get').then(() => {
            this.$Message.success(this.$t('message.scheduler.runTask.success'))
            this.getInstanceListData()
          })
        },
        onCancel: () => {}
      })
    },
    exportDefinition (index) {
      const downloadBlob = (data, fileNameS = 'json') => {
        if (!data) {
          return
        }
        const blob = new Blob([data])
        const fileName = `${fileNameS}.json`
        if ('download' in document.createElement('a')) { // 不是IE浏览器
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.style.display = 'none'
          link.href = url
          link.setAttribute('download', fileName)
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) // 下载完成移除元素
          window.URL.revokeObjectURL(url) // 释放掉blob对象
        } else { // IE 10+
          window.navigator.msSaveBlob(blob, fileName)
        }
      }
      api.fetch(`dolphinscheduler/projects/${this.projectName}/process/export`, {
        processDefinitionIds: this.list[index].id,
      }, {
        method: 'get',
        responseType: 'blob'
      }).then((res) => {
        downloadBlob(res.data, this.list[index].name)
      })
    },
    queryTimingList(index) {
      this.schedulerId = this.list[index].id
      this.activeList(3)
    },
    _online(index) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/process/release`, {
        processId: this.list[index].id,
        releaseState: 1
      }, {useForm: true}).then(() => {
        this.$Message.success(this.$t('message.scheduler.runTask.success'))
        this.getListData()
        this.list[index].isOnline = true
        util.Hub.$emit('DS-operation', {
          type: 'online'
        })
      })
    },
    _offline(index) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/process/release`, {
        processId: this.list[index].id,
        releaseState: 0
      }, {useForm: true}).then(() => {
        this.$Message.success(this.$t('message.scheduler.runTask.success'))
        this.getListData()
        this.list[index].isOnline = false
        util.Hub.$emit('DS-operation', {
          type: 'offline'
        })
      })
    },
    timingOnline(id) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/schedule/online`, {
        id: id
      }, {useForm: true}).then(() => {
        this.getSchedulerData()
      })
    },
    timingOffline(id) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/schedule/offline`, {
        id: id
      }, {useForm: true}).then(() => {
        this.getSchedulerData()
      })
    },
    runTask() {
      this.showRunTaskModal = false
    },
    closeRun() {
      this.showRunTaskModal = false
    },
    setTiming() {
      this.showTimingTaskModal = false
    },
    closeTiming() {
      this.showTimingTaskModal = false
    },
    activeList(type) {
      this.activeDS = type
      if (this.activeDS === 1) {
        this.getListData()
      } else if (this.activeDS === 2) {
        this.getInstanceListData()
      } else if (this.activeDS === 3) {
        this.getSchedulerData()
      }
    },
    openDag(index) {
      if (this.timer)
        clearInterval(this.timer)
      api.fetch(`dolphinscheduler/projects/${this.projectName}/instance/select-by-id`, {
        processInstanceId: this.list2[index].id,
      }, 'get').then((data) => {
        // process instance
        let processInstanceJson = JSON.parse(data.processInstanceJson)
        let tasks = processInstanceJson.tasks
        let connects = JSON.parse(data.connects)
        let locations = JSON.parse(data.locations)
        this.dagProcessId = this.list2[index].id
        this.pollTask(this.list2[index].id, taskList => {
          this.dagData = ds2butterfly(tasks, connects, locations, taskList)
          this.showDag = 1
        })
        this.timer = setInterval(() => {
          this.pollTask(this.list2[index].id, taskList => {
            this.dagData = ds2butterfly(tasks, connects, locations, taskList)
          })
        }, 1000*30)
      }).catch(() => {
      })
    },
    pollTask(processInstanceId, cb) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/instance/task-list-by-process-id`, {
        processInstanceId: processInstanceId,
      }, 'get').then((data) => {
        // process instance
        let taskList = data.taskList
        taskList.forEach(task => {
          task.stateObj = this.tasksState[task.state]
          task.taskObj = JSON.parse(task.taskJson)
        })
        cb && cb(taskList)
      }).catch(() => {
      })
    },
    getPanelClass() {
      switch (this.showDag) {
        case 0:
          return 'left-panel'
        case 1:
          return 'left-panel partial-panel'
        case 2:
          return 'left-panel full-panel'
      }
    },
    changePanel(expand) {
      this.showDag = expand ? ++this.showDag : --this.showDag
      // 关闭dag图 清除计时器
      if (!this.showDag && this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    },
    _rerun(index) {
      let item = this.list2[index]
      this._countDownFn({
        id: item.id,
        executeType: 'REPEAT_RUNNING',
        index: index,
        buttonType: 'run'
      })
    },
    _recovery(index) {
      let item = this.list2[index]
      this._countDownFn({
        id: item.id,
        executeType: 'START_FAILURE_TASK_PROCESS',
        index: index,
        buttonType: 'store'
      })
    },
    _stop(index) {
      let item = this.list2[index]
      if (item.state === 'STOP') {
        this._countDownFn({
          id: item.id,
          executeType: 'RECOVER_SUSPENDED_PROCESS',
          index: index,
          buttonType: 'suspend'
        })
      } else {
        this._upExecutorsState({
          id: item.id,
          executeType: 'STOP'
        })
      }
    },
    _suspend(index) {
      let item = this.list2[index]
      if (item.state === 'PAUSE') {
        this._countDownFn({
          id: item.id,
          executeType: 'RECOVER_SUSPENDED_PROCESS',
          index: index,
          buttonType: 'suspend'
        })
      } else {
        this._upExecutorsState({
          id: item.id,
          executeType: 'PAUSE'
        })
      }
    },
    deleteScheduler(index) {
      let item = this.list3[index]
      this.$Modal.confirm({
        title: this.$t('message.scheduler.DELETE'),
        content: `<p>${this.$t('message.scheduler.delete')}?</p>`,
        okText: this.$t('message.scheduler.ok'),
        cancelText: this.$t('message.scheduler.cancel'),
        onOk: () => {
          api.fetch(`dolphinscheduler/projects/${this.projectName}/schedule/delete`, {
            scheduleId: item.id,
          }, 'get').then(() => {
            this.$Message.success(this.$t('message.scheduler.runTask.success'))
            this.activeList(1)
          })
        },
        onCancel: () => {}
      })
    },
    _upExecutorsState(param) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/executors/execute`, {
        processInstanceId: param.id,
        executeType: param.executeType
      }, {useFormQuery: true}).then(() => {
        this.$Message.success(this.$t('message.scheduler.runTask.success'))
        this.getInstanceListData()
      }).catch(() => {
        this.getInstanceListData()
      })
    },
    _countDownFn (param) {
      this.buttonType = param.buttonType
      api.fetch(`dolphinscheduler/projects/${this.projectName}/executors/execute`, {
        processInstanceId: param.id,
        executeType: param.executeType
      }, {useFormQuery: true}).then(() => {
        this.list2[param.index].disabled = true
        this.$Message.success(this.$t('message.scheduler.runTask.success'))
        setTimeout(() => {
          this.list2[param.index].disabled = false
          this.getInstanceListData()
        }, 5000)
      }).catch(() => {
        this.getInstanceListData()
      })
    },
    formatISODate (date) {
      let [datetime, timezone] = date.split('+')
      if (!timezone || timezone.indexOf(':') >= 0) return date
      let hourOfTz = timezone.substring(0, 2) || '00'
      let secondOfTz = timezone.substring(2, 4) || '00'
      return `${datetime}+${hourOfTz}:${secondOfTz}`
    },
    formatDate(value, fmt) {
      fmt = fmt || 'YYYY-MM-DD HH:mm:ss'
      if (value === null) {
        return '-'
      } else {
        return dayjs(this.formatISODate(value)).format(fmt)
      }
    },
    filterNull(value) {
      if (value === null || value === '') {
        return '-'
      } else {
        return value
      }
    },
    pageChange(page) {
      this.pagination.current = page
      this.getListData(page)
    },
    pageSizeChange(size) {
      this.pagination.size = size
      this.getListData()
    },
    pageChange2(page) {
      this.pagination2.current = page
      this.getInstanceListData(page)
    },
    pageSizeChange2(size) {
      this.pagination2.size = size
      this.getInstanceListData()
    },
    pageChange3(page) {
      this.pagination3.current = page
      this.getSchedulerData(page)
    },
    pageSizeChange3(size) {
      this.pagination3.size = size
      this.getSchedulerData()
    }
  }
};
</script>
<style lang="scss" scoped>
.scheduler-wrapper{
  background-color: white;
  min-height: 80vh;
  .scheduler-menu{
    float: left;
    width: 250px;
    font-size: 14px;
    min-height: 80vh;
    margin-top: 16px;
    border-right: 1px solid #DEE4EC;
    li {
    padding: 0 40px;
    cursor: pointer;
    line-height: 40px;
    &:hover{
       color: #2E92F7;
     }
    &.active{
       background-color: #ECF4FF;
       color: #2E92F7;
       border-right: 3px solid  #2E92F7
     }
    }
  }
  .scheduler-list{
    float: left;
    padding: 23px 26px;
    .scheduler-table {
      width: calc(100vw - 250px - 70px);
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
    }
  }
  .left-panel{
    height: 80vh;
    width: calc(100vw - 250px - 65px - 250px);
    background: #FFFFFF;
    position: absolute;
    left: 100vw;
    z-index: 99;
    padding: 23px 26px 23px 0;
    transition: all 1s;
    &.partial-panel {
      left: calc(250px + 250px + 60px);
      transition: all 1s;
    }
    .dag-page{
      border: 1px solid #DEE4EC;
      width: 100%;
      height: 100%;
    }
    &.full-panel {
      left: 270px;
      width: calc(100vw - 270px);
      transition: all 1s;
    }
    .close-panel{
      position: absolute;
      right: 26px;
      top: -5px;
      font-size: 16px;
      cursor: pointer;
    }
  }
  .fr{
    float: right;
  }
  .page-bar {
    margin-top: 20px;
  }
}
</style>
