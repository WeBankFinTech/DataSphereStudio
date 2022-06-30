<template>
  <div>
    <template>
      <div class="scheduler-wrapper">
        <div class="scheduler-menu">
          <ul>
            <li :class="activeDS == 4? 'active' : ''" @click="activeList(4)">{{$t('message.scheduler.dashboard')}}</li>
            <li v-if="!isindex" :class="activeDS == 1 || activeDS == 3? 'active' : ''" @click="activeList(1)">{{$t('message.scheduler.processDefinition')}}</li>
            <li v-if="!isindex" :class="activeDS == 2? 'active' : ''" @click="activeList(2)">{{$t('message.scheduler.processInstance')}}</li>
            <li v-if="!isindex" :class="activeDS == 5? 'active' : ''" @click="activeList(5)">{{$t('message.scheduler.taskInstance')}}</li>
          </ul>
        </div>
        <div class="scheduler-list" v-if="activeDS == 4">
          <template>
            <dashboard @goToList="goToList"></dashboard>
          </template>
        </div>
        <div class="scheduler-list" v-if="activeDS == 1">
          <template>
            <div class="scheduler-list-title">
              <span>{{$t('message.scheduler.processDefinition')}}</span>
              <Input v-model="searchVal" style="width: auto;float: right" @on-enter="activeList(1)">
                <Icon type="ios-search" slot="suffix" @click="activeList(1)" style="cursor: pointer;"/>
              </Input>
            </div>
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
          <template v-if="!showGantt">
            <div class="scheduler-list-title">
              <span>{{$t('message.scheduler.processInstance')}}</span>
              <div class="fr">
                <Input v-model="searchVal" style="width: auto;float: right" @on-enter="activeList(2)">
                  <Icon type="ios-search" slot="suffix" @click="activeList(2)" style="cursor: pointer;"/>
                </Input>
                <template>
                  <Date-picker
                    style="width: 350px;float: right;margin-right: 10px;"
                    v-model="dateTime"
                    type="datetimerange"
                    @on-ok="_datepicker"
                    range-separator="-"
                    :start-placeholder="$t('message.scheduler.runTask.startDate')"
                    :end-placeholder="$t('message.scheduler.runTask.endDate')"
                    format="yyyy-MM-dd HH:mm:ss">
                  </Date-picker>
                  <Select v-model="instanceStateType"
                    @on-change="_changeInstanceState"
                    :placeholder="$t('message.scheduler.selectState')"
                    style="width: 150px;float: right;margin-right: 10px;"
                  >
                    <Option value="" key="-">-</Option>
                    <Option v-for="item in tasksStateList" :value="item.code" :key="item.id">{{item.desc}}</Option>
                  </Select>
                </template>
              </div>
            </div>
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
          <gantt :instanceId="instanceId" v-else></gantt>
        </div>
        <div class="scheduler-list" v-if="activeDS == 3">
          <template>
            <div class="scheduler-list-title">{{$t('message.scheduler.setTimeManage')}}</div>
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
        <div class="scheduler-list" v-if="activeDS == 5">
          <template>
            <div class="scheduler-list-title">
              <span>{{$t('message.scheduler.taskInstance')}}</span>
              <Input v-model="taskName" style="width: auto;float: right" @on-enter="activeList(5)">
                <Icon type="ios-search" slot="suffix" @click="activeList(5)" style="cursor: pointer;"/>
              </Input>
              <template>
                <Date-picker
                  style="width: 350px;float: right;margin-right: 10px;"
                  v-model="dateTime2"
                  type="datetimerange"
                  @on-ok="_datepicker2"
                  @on-clear="_datepicker2"
                  range-separator="-"
                  :start-placeholder="$t('message.scheduler.runTask.startDate')"
                  :end-placeholder="$t('message.scheduler.runTask.endDate')"
                  format="yyyy-MM-dd HH:mm:ss">
                </Date-picker>
                <Select v-model="instanceStateType2"
                  @on-change="_changeInstanceState2"
                  :placeholder="$t('message.scheduler.selectState')"
                  style="width: 150px;float: right;margin-right: 10px;"
                >
                  <Option value="" key="-">-</Option>
                  <Option v-for="item in tasksStateList" :value="item.code" :key="item.id">{{item.desc}}</Option>
                </Select>
              </template>
            </div>
            <Table class="scheduler-table" :columns="columns5" :data="list5"></Table>
            <Page
              size="small"
              v-if="list5.length > 0"
              class="page-bar fr"
              :total="pagination5.total"
              show-sizer
              show-total
              :current="pagination5.current"
              :page-size="pagination5.size"
              :page-size-opts="pagination5.opts"
              @on-change="pageChange5"
              @on-page-size-change="pageSizeChange5"
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
    <div id="rightMenu" class="right-menu"></div>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api'
import util from '@dataspherestudio/shared/common/util'
import iRun from './run'
import iTiming from './timing'
import _ from 'lodash'
import Dag from './dag'
import {ds2butterfly, formatDate, filterNull} from './convertor'
import mLog from './log/log'
import gantt from './gantt'
import { GetWorkspaceData } from '@dataspherestudio/shared/common/service/apiCommonMethod.js'
import { tasksState, publishStatus, runningType, tasksStateList } from './config'
import dashboard from './dashboard/index'
import moment from 'moment'

export default {
  name: 'dispatch',
  components: {
    iRun,
    iTiming,
    Dag,
    mLog,
    gantt,
    dashboard
  },
  props: {
    query: {
      type: Object,
      default: () => {}
    },
    tabName: {
      type: String,
      default: ''
    },
    activeTab: {
      default: 1
    },
    isindex: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    tabName() {
      this.searchVal = this.tabName
    },
    activeTab() {
      this.activeDS = this.activeTab
    },
    '$route.query.projectID': {
      handler: function() {
        if (!this.workspaceName) {
          GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
            this.workspaceName = data.workspace.name
            this.activeList(this.activeDS)
          })
        } else {
          this.activeList(this.activeDS)
        }
      },
      deep: true,
      immediate: true

    },
  },
  computed: {
    projectName() {
      return `${this.workspaceName}-${this.$route.query.projectName}`
    }
  },
  data() {
    return {
      userId: '', //列表入参,用户id
      searchVal: this.tabName, //搜索名字
      taskName: '', //任务实例名称
      dateTime: [], //实例列表搜索时间
      instanceStateType: '',//实例列表搜索状态
      dateTime2: [], //任务列表搜索时间
      instanceStateType2: '',//任务列表搜索状态
      workspaceName: '',
      activeDS: this.activeTab,
      showRunTaskModal: false,
      showTimingTaskModal: false,
      list: [],
      list2: [],
      list3: [],
      list5: [],
      columns: [
        {
          title: this.$t('message.scheduler.header.id'),
          width: 100,
          key: 'id'
        },
        {
          title: this.$t('message.scheduler.header.ProcessName'),
          minWidth: 200,
          key: 'name'
        },
        {
          title: this.$t('message.scheduler.header.State'),
          width: 100,
          key: 'releaseStateDesc'
        },
        {
          title: this.$t('message.scheduler.header.CreateTime'),
          width: 200,
          key: 'createTime'
        },
        {
          title: this.$t('message.scheduler.header.UpdateTime'),
          width: 200,
          key: 'updateTime'
        },
        {
          title: this.$t('message.scheduler.header.Description'),
          width: 300,
          key: 'description',
          render: (h, params) => {
            return h('div', [
              h('span', {
                style: {
                  overflow: 'hidden',
                  wordBreak: 'break-all',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  display: 'block'
                },
                attrs: {
                  title: params.row.description
                },
              }, params.row.description)
            ])
          }
        },
        {
          title: this.$t('message.scheduler.header.ModifyBy'),
          width: 120,
          key: 'modifyBy'
        },
        {
          title: this.$t('message.scheduler.header.TimingState'),
          width: 150,
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
                  title: this.$t('message.scheduler.setTimeManage')
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
          minWidth: 200,
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
          width: 200,
          key: 'scheduleTime'
        },
        {
          title: this.$t('message.scheduler.header.StartTime'),
          width: 200,
          key: 'startTime'
        },
        {
          title: this.$t('message.scheduler.header.EndTime'),
          width: 200,
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
          width: 250,
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
              }),
              h('Button', {
                props: {
                  type: 'info',
                  shape: "circle",
                  icon: "md-list-box",
                  size: 'small'
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.Gantt')
                },
                on: {
                  click: () => {
                    this._gantt(params.row)
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
          minWidth: 200,
          key: 'processDefinitionName'
        },
        {
          title: this.$t('message.scheduler.header.StartTime'),
          width: 200,
          key: 'startTime'
        },
        {
          title: this.$t('message.scheduler.header.EndTime'),
          width: 200,
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
          width: 200,
          key: 'createTime'
        },
        {
          title: this.$t('message.scheduler.header.UpdateTime'),
          width: 200,
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
      columns5: [
        {
          title: this.$t('message.scheduler.header.id'),
          width: 100,
          key: 'id'
        },
        {
          title: this.$t('message.scheduler.header.Name'),
          minWidth: 200,
          key: 'name'
        },
        {
          title: this.$t('message.scheduler.header.ProcessInstance'),
          width: 150,
          key: 'processInstanceName'
        },
        {
          title: this.$t('message.scheduler.header.Executor'),
          width: 100,
          key: 'executorName'
        },
        {
          title: this.$t('message.scheduler.header.nodeType'),
          width: 100,
          key: 'taskType'
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
          title: this.$t('message.scheduler.header.SubmitTime'),
          width: 200,
          key: 'submitTime'
        },
        {
          title: this.$t('message.scheduler.header.StartTime'),
          width: 200,
          key: 'startTime'
        },
        {
          title: this.$t('message.scheduler.header.EndTime'),
          width: 200,
          key: 'endTime'
        },
        {
          title: 'host',
          width: 150,
          key: 'host'
        },
        {
          title: this.$t('message.scheduler.header.Duration') + 's',
          width: 100,
          key: 'duration'
        },
        {
          title: this.$t('message.scheduler.header.retryTimes'),
          width: 120,
          key: 'retryTimes'
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
                  icon: "md-paper",
                  size: 'small'
                },
                style: {
                  marginRight: '5px'
                },
                attrs: {
                  title: this.$t('message.scheduler.viewLog')
                },
                on: {
                  click: () => {
                    this._viewLog(params.index)
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
        type: '',
        id: ''
      },
      pagination: {
        size: 10,
        opts: [10, 20, 30, 40],
        current: 1,
        total: 0
      },
      pagination2: {
        size: 10,
        opts: [10, 20, 30, 40],
        current: 1,
        total: 0
      },
      pagination3: {
        size: 10,
        opts: [10, 20, 30, 40],
        current: 1,
        total: 0
      },
      pagination5: {
        size: 10,
        opts: [10, 20, 30, 40],
        current: 1,
        total: 0
      },
      publishStatus,
      runningType,
      tasksState,
      tasksStateList,
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
      schedulerId: '', //用来查定时管理的id
      instanceId: '', //甘特图的实例id
      showGantt: false
    }
  },
  mounted() {
    GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
      this.workspaceName = data.workspace.name
      if (this.activeDS === 1) {
        this.getListData()
      }
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
  beforeDestroy() {
    // 清除计时器
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    fetchTaskInstanceList(page=1, data) {
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.projectName}/task-instance/list-paging`, {
          pageSize: this.pagination5.size,
          pageNo: page,
          searchVal: this.taskName,
          ...data
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.submitTime = formatDate(item.submitTime)
            item.startTime = formatDate(item.startTime)
            item.endTime = formatDate(item.endTime)
            item.duration = filterNull(item.duration)
            item.stateDesc = this.tasksState[item.state].desc
            item.stateColor = this.tasksState[item.state].color
            item.stateIcon = this.tasksState[item.state].icon
            item.disabled = false
          })
          this.list5 = res.totalList
          this.pagination5.total = res.total
        })
      })
    },
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
    getListData(page=1, data) {
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.projectName}/process/list-paging`, {
          pageSize: this.pagination.size,
          pageNo: page,
          searchVal: this.searchVal,
          ...data
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.releaseStateDesc = item.releaseState? this.publishStatus[item.releaseState] : ''
            item.scheduleReleaseStateDesc = item.scheduleReleaseState? this.publishStatus[item.scheduleReleaseState] : '-'
            item.isOnline = item.releaseState === 'ONLINE'
            item.createTime = formatDate(item.createTime)
            item.updateTime = formatDate(item.updateTime)
            item.disabled = false
          })
          this.list = res.totalList
          this.pagination.total = res.total
        })
      })
    },
    getInstanceListData(page=1, data) {
      util.checkToken(() => {
        api.fetch(`dolphinscheduler/projects/${this.projectName}/instance/list-paging`, {
          pageSize: this.pagination2.size,
          pageNo: page,
          searchVal: this.searchVal,
          ...data
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.scheduleTime = formatDate(item.scheduleTime)
            item.startTime = formatDate(item.startTime)
            item.endTime = formatDate(item.endTime)
            item.duration = filterNull(item.duration)
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
          processDefinitionId: this.schedulerId
        }, 'get').then((res) => {
          res.totalList.forEach(item => {
            item.startTime = formatDate(item.startTime)
            item.endTime = formatDate(item.endTime)
            item.releaseStateDesc = item.releaseState? this.publishStatus[item.releaseState] : ''
            item.isOnline = item.releaseState === 'ONLINE'
            item.createTime = formatDate(item.createTime)
            item.updateTime = formatDate(item.updateTime)
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
      let id, tempData = {}
      if (isEdit) {
        tempData.item = index
        tempData.type = ''
        id = index.processDefinitionId
      } else {
        tempData.item = this.list[index]
        tempData.type = 'timing'
        id = this.list[index].id
      }
      this.timingData.id = id
      this.getReceiver(id, (res) => {
        this.timingData.item = {
          ...tempData.item,
          receivers: res.receivers,
          receiversCc: res.receiversCc
        }
        this.showTimingTaskModal = true
      })
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
    setTiming(id) {
      this.schedulerId = id
      this.showTimingTaskModal = false
      this.activeList(3)
    },
    closeTiming() {
      this.showTimingTaskModal = false
    },
    activeList(type, data) {
      // 清除计时器
      if (this.timer) {
        clearInterval(this.timer)
      }
      this.activeDS = type
      if (this.activeDS === 1) {
        this.getListData(1, data)
      } else if (this.activeDS === 2) {
        this.showGantt = false
        if (data) {
          if (data.startDate && data.endDate) {
            data.startDate = moment(data.startDate).format('YYYY-MM-DD HH:mm:ss')
            data.endDate = moment(data.endDate).format('YYYY-MM-DD HH:mm:ss')
            this.dateTime = [data.startDate, data.endDate]
          } else {
            data.startDate = this.dateTime.length > 1 ? this.dateTime[0] : ''
            data.endDate = this.dateTime.length > 1 ? this.dateTime[1] : ''
          }
          if (data.stateType) {
            this.instanceStateType = data.stateType
          } else {
            data.stateType = this.instanceStateType
          }
        }
        this.getInstanceListData(1, data)
      } else if (this.activeDS === 3) {
        this.getSchedulerData()
      } else if (this.activeDS === 4) {
        this.activeDS = 0
        this.$nextTick(() => {
          this.activeDS = 4
        })

      } else if (this.activeDS === 5) {
        if (data) {
          if (data.startDate && data.endDate) {
            data.startDate = moment(data.startDate).format('YYYY-MM-DD HH:mm:ss')
            data.endDate = moment(data.endDate).format('YYYY-MM-DD HH:mm:ss')
            this.dateTime2 = [data.startDate, data.endDate]
          } else {
            data.startDate = this.dateTime2.length > 1 ? this.dateTime2[0] : ''
            data.endDate = this.dateTime2.length > 1 ? this.dateTime2[1] : ''
          }
          if (data.stateType) {
            this.instanceStateType2 = data.stateType
          } else {
            data.stateType = this.instanceStateType2
          }
        }
        this.fetchTaskInstanceList(1, data)
      }
    },
    openDag(index) {
      // 清除计时器
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
        const contextMenu = {
          start: this.$t('message.scheduler.run'),
          log: this.$t('message.scheduler.viewLog')
        }
        this.dagProcessId = this.list2[index].id
        this.pollTask(this.list2[index].id, taskList => {
          this.dagData = ds2butterfly(tasks, connects, locations, taskList, false, contextMenu)
          this.showDag = 1
        })
        this.timer = setInterval(() => {
          this.pollTask(this.list2[index].id, taskList => {
            this.dagData = ds2butterfly(tasks, connects, locations, taskList, false, contextMenu)
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
    _viewLog(index) {
      let item = this.list5[index]
      this.logId = item.id
      this.logData = {}
      this.showLog = true
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
        this.$Message.success(this.$t('message.scheduler.runTask.operationSuccess'))
        setTimeout(() => {
          this.list2[param.index].disabled = false
          this.getInstanceListData()
        }, 5000)
      }).catch(() => {
        this.getInstanceListData()
      })
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
    },
    pageChange5(page) {
      this.pagination5.current = page
      this.fetchTaskInstanceList(page)
    },
    pageSizeChange5(size) {
      this.pagination5.size = size
      this.fetchTaskInstanceList()
    },
    _gantt (item) {
      this.instanceId = item.id
      this.showGantt = true
    },
    goToList(active, data){
      this.searchVal = ''
      this.activeList(active, data)
    },
    // 实例列表
    _datepicker() {
      let searchDate = this.dateTime.length > 1 ? {
        startDate: this.dateTime[0],
        endDate: this.dateTime[1]
      } : {}
      this.activeList(2, searchDate)
    },
    _changeInstanceState() {
      this.activeList(2, {
        stateType: this.instanceStateType
      })
    },
    // 任务列表
    _datepicker2() {
      let searchDate = this.dateTime2.length > 1 ? {
        startDate: this.dateTime2[0],
        endDate: this.dateTime2[1]
      } : {}
      this.activeList(5, searchDate)
    },
    _changeInstanceState2() {
      this.activeList(5, {
        stateType: this.instanceStateType2
      })
    }
  }
};
</script>
<style lang="scss">
  a {
    color: #2d8cf0;
  &:hover {
     color: #57a3f3;
   }
  }
</style>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.scheduler-center{
  position: relative;
  overflow: hidden;
  margin: 20px 20px 0;
  border: 1px solid rgb(237,237,237);
  border-left: none;
  .scheduler-wrapper{
    padding-top: 0;
  .scheduler-menu{
    min-height: 80vh;
  }
  .scheduler-list{
    min-height: 80vh;
  }
  }
}
.scheduler-wrapper{
  @include bg-color($workflow-body-bg-color, $dark-workflow-body-bg-color);
  min-height: 80vh;
  float: left;
  width: 100%;
  overflow-y: hidden;

  .scheduler-menu{
    float: left;
    width: 200px;
    padding-top: 16px;
    font-size: 14px;
    min-height: calc(80vh - 16px);
    border-right: 1px solid #DEE4EC;
    border-left: 1px solid #DEE4EC;
    padding-bottom: 3000px;
    margin-bottom: -3000px;
    @include border-color($border-color-base, $dark-workspace-background);
    @include bg-color($light-base-color, $dark-base-color);
    @include font-color($light-text-color, $dark-text-color);
    li {
    padding: 0 40px;
    cursor: pointer;
    line-height: 40px;
    &:hover{
       @include font-color($primary-color, $dark-primary-color);
     }
    &.active{
       @include bg-color($active-menu-item, $dark-active-menu-item);
       border-right: 3px solid  #2E92F7;
       @include font-color($primary-color, $dark-primary-color);
       @include border-color($primary-color, $dark-primary-color);
     }
    }
  }
  .scheduler-list{
    float: left;
    padding: 10px 25px;
    min-height: calc(80vh - 16px);
    width: calc(100% - 250px);
    .scheduler-list-title {
      padding-bottom: 17px;
      font-size: 16px;
      @include font-color($light-text-color, $dark-text-color);
      line-height: 22px;
      font-weight: bolder;
    }
    .scheduler-table {
      width: 100%;
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
    }
  }
  .left-panel{
    height: 80vh;
    width: calc(100vw - 250px - 250px - 60px - 300px);
    @include bg-color($workflow-body-bg-color, $dark-workflow-body-bg-color);
    position: fixed;
    bottom: 0;
    left: 100vw;
    z-index: 99;
    padding: 23px 26px 23px 0;
    transition: all 1s;
    &.partial-panel {
      left: calc(250px + 250px + 60px + 300px);
      transition: all 1s;
    }
    .dag-page{
      border: 1px solid #DEE4EC;
      @include border-color($border-color-base, $dark-border-color-base);
      width: 100%;
      height: 100%;
    }
    &.full-panel {
      left: 310px;
      width: calc(100vw - 310px);
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
<style lang="scss">
  .right-menu {
    position: fixed;
    width: 90px;
    background: #fff;
    border-radius: 3px;
    box-shadow: 0 2px 4px 1px rgba(0, 0, 0, 0.1);
    padding: 4px 0;
    visibility: hidden;
    z-index: 99
  }
</style>
<style lang="scss">
  .right-menu {
    position: fixed;
    width: 90px;
    background: #fff;
    border-radius: 3px;
    box-shadow: 0 2px 4px 1px rgba(0, 0, 0, 0.1);
    padding: 4px 0;
    visibility: hidden;
    z-index: 99
  }
</style>
