<template>
  <div class="page-process" ref="page_process">
    <Card
      v-if="!checkEditable(query) && showTip"
      shadow
      class="process-readonly-tip-card"
    >
      <div>
        {{$t("message.workflow.workflowItem.readonlyTip")}}
      </div>
      <Icon type="md-close" class="tipClose" @click.stop="closeTip" />
    </Card>
    <div class="process-tabs">
      <div class="process-tab">
        <div
          v-for="(item, index) in tabs"
          :key="index"
          class="process-tab-item"
          :class="{active: index===active}"
          @click="choose(index)"
          @mouseenter.self="item.isHover = true"
          @mouseleave.self="item.isHover = false"
        >
          <div>
            <img
              class="tab-icon"
              :class="nodeImg[item.node.type].class"
              :src="nodeImg[item.node.type].icon"
              alt
            />
            <div :title="item.title" class="process-tab-name">{{ item.title }}</div>
            <SvgIcon v-show="!item.isHover && item.node && item.node.isChange && checkEditable(query)" class="process-tab-unsave-icon" icon-class="fi-radio-on2"/>
            <Icon
              v-if="item.isHover && (item.close || query.product)"
              type="md-close"
              @click.stop="remove(index)"
            />
          </div>
        </div>
      </div>
      <div class="process-container">
        <template v-for="(item, index) in tabs">
          <Process
            ref="process"
            v-if="item.type === 'Process'"
            v-show="index===active"
            :key="item.key"
            :import-replace="false"
            :flow-id="item.data.appId"
            :version="item.data.version"
            :product="query.product"
            :readonly="!checkEditable(query)"
            :isLatest="query.isLatest === 'true'"
            :tabs="tabs"
            :open-files="openFiles"
            :orchestratorId="item.data.id"
            :orchestratorVersionId="item.data.orchestratorVersionId"
            @changeMap="changeTitle"
            @node-dblclick="dblclickNode(index, arguments)"
            @isChange="isChange(index, arguments)"
            @save-node="saveNode"
            @check-opened="checkOpened"
            @deleteNode="deleteNode"
            @saveBaseInfo="saveBaseInfo"
            @updateWorkflowList="$emit('updateWorkflowList')"
            @showDolphinscheduler="showDS"
          ></Process>
          <Ide
            v-if="item.type === 'IDE'"
            v-show="index===active"
            :key="item.title"
            :parameters="item.data"
            :node="item.node"
            :in-flows-index="index"
            :readonly="!checkEditable(query)"
            @save="saveIDE(index, arguments)"
          ></Ide>
          <commonIframe
            v-if="item.type === 'Iframe'"
            v-show="index===active"
            :key="item.title"
            :parametes="item.data"
            :node="item.node"
            @save="saveNode"
          ></commonIframe>
          <div
            v-if="item.type === 'DS'"
            v-show="index===active"
            :key="item.title"
            style="width:100%; height:100%"
          >
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
                  <SvgIcon @click="changePanel(true)" style="font-size: 40px;left: -12px;top: 45%;position: absolute;cursor: pointer;" v-if="showDag === 1" icon-class="panel-full"/>
                  <SvgIcon @click="changePanel(false)" style="font-size: 40px;left: -12px;top: 37%;position: absolute;cursor: pointer;" v-if="showDag === 1" icon-class="panel-close"/>
                  <SvgIcon @click="changePanel(false)" style="font-size: 80px;left: -32px;top: 35%;position: absolute;cursor: pointer;" v-if="showDag === 2" icon-class="panel-partial"/>
                  <div class="dag-page">
                    <Dag :dagData="dagData" :processId="dagProcessId" v-if="showDag"></Dag>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </template>
      </div>
    </div>
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
import { isEmpty, isArguments } from "lodash";
import api from "@/common/service/api";
import util from "@/common/util";
import Process from "./module.vue";
import Ide from "@/apps/workflows/module/ide";
import commonModule from "@/apps/workflows/module/common";
import { NODETYPE, NODEICON } from "@/apps/workflows/service/nodeType";
import iRun from './run'
import iTiming from './timing'
import dayjs from 'dayjs'
import _ from 'lodash'
import Dag from './dag'
import {ds2butterfly} from './convertor'
import mLog from './log/log'
import { GetWorkspaceData } from '@/common/service/apiCommonMethod.js'

export default {
  components: {
    Process,
    Ide: Ide.component,
    commonIframe: commonModule.component.iframe,
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
      tabs: [
        {
          title: this.$t("message.workflow.process.index.BJMS"),
          type: "Process",
          close: false,
          data: this.query,
          node: {
            isChange: false,
            type: "workflow.subflow"
          },
          key: "工作流",
          isHover: false
        }
      ],
      active: 0,
      setIntervalID: "",
      setTime: 40,
      showTip: true,
      openFiles: {},
      nodeImg: NODEICON,
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
          width: 100,
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
                    params.row.isOnline ? this.offline(params.index) : this.online(params.index)
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
                  type: 'ios-radio-button-on',
                  color: params.row.stateColor,
                  size: 10
                }
              }),
              h('strong', params.row.stateDesc)
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
                  icon: params.row.state === 'PAUSE' ? "md-play" : "md-puase",
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
                  title: this.$t('message.scheduler.delete')
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
          color: '#A9A9A9'
        },
        'RUNNING_EXECUTION': {
          id: 1,
          desc: this.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
          color: '#0097e0'
        },
        'RUNNING_EXEUTION': {
          id: 1,
          desc: this.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
          color: '#0097e0'
        },
        READY_PAUSE: {
          id: 2,
          desc: this.$t('message.scheduler.tasksState.READY_PAUSE'),
          color: '#07b1a3'
        },
        PAUSE: {
          id: 3,
          desc: this.$t('message.scheduler.tasksState.PAUSE'),
          color: '#057c72'
        },
        READY_STOP: {
          id: 4,
          desc: this.$t('message.scheduler.tasksState.READY_STOP'),
          color: '#FE0402'
        },
        STOP: {
          id: 5,
          desc: this.$t('message.scheduler.tasksState.STOP'),
          color: '#e90101'
        },
        FAILURE: {
          id: 6,
          desc: this.$t('message.scheduler.tasksState.FAILURE'),
          color: '#000000'
        },
        SUCCESS: {
          id: 7,
          desc: this.$t('message.scheduler.tasksState.SUCCESS'),
          color: '#33cc00'
        },
        NEED_FAULT_TOLERANCE: {
          id: 8,
          desc: this.$t('message.scheduler.tasksState.NEED_FAULT_TOLERANCE'),
          color: '#FF8C00'
        },
        KILL: {
          id: 9,
          desc: this.$t('message.scheduler.tasksState.KILL'),
          color: '#a70202'
        },
        WAITTING_THREAD: {
          id: 10,
          desc: this.$t('message.scheduler.tasksState.WAITTING_THREAD'),
          color: '#912eed'
        },
        WAITTING_DEPEND: {
          id: 11,
          desc: this.$t('message.scheduler.tasksState.WAITTING_DEPEND'),
          color: '#5101be'
        },
        DELAY_EXECUTION: {
          id: 12,
          desc: this.$t('message.scheduler.tasksState.DELAY_EXECUTION'),
          color: '#5102ce'
        },
        FORCED_SUCCESS: {
          id: 13,
          desc: this.$t('message.scheduler.tasksState.FORCED_SUCCESS'),
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
    this.getCache().then(tabs => {
      if (tabs) {
        this.tabs = tabs;
      }
    });
    this.updateProjectCacheByActive();
    this.changeTitle(false);
    GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
      this.workspaceName = data.workspace.name;
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
    // 判断是否有意编辑权限
    // 没有权限的和历史的都不可编辑
    checkEditable(item) {
      // 编排权限由后台的priv字段判断，1-查看， 2-编辑， 3-发布
      if ([2,3].includes(item.priv) && this.query.readonly !== 'true') {
        return true
      } else {
        return false
      }
    },
    gotoAction(back = -1) {
      if (back) {
        this.$router.go(back);
      }
    },
    // 提示卡片关闭
    closeTip() {
      this.showTip = false;
    },
    choose(index) {
      this.active = index;
      this.updateProjectCacheByActive();
    },
    remove(index) {
      // 如果删掉的是第一个tab，就返回上一页
      if (index === 0) {
        this.$router.go(-1);
      }
      // 删掉子工作流得删掉当前打开的子节点
      const currentTab = this.tabs[index];
      // 找到当前关闭项对应的子类
      const subArray = this.openFiles[currentTab.key] || [];
      const changeList = this.tabs.filter(item => {
        return subArray.includes(item.key) && item.node.isChange;
      });
      // 子工作流关闭时，查询是否有子节点没有保存，是否一起关闭
      if (changeList.length > 0 && currentTab.node.type === NODETYPE.FLOW) {
        let text = `<p>${this.$t("message.workflow.process.index.WBCSFGB")}</p>`;
        if (currentTab.node.isChange) {
          text = `<p>${this.$t("message.workflow.process.index.GGZLWBC")}</p>`;
        }
        this.$Modal.confirm({
          title: this.$t("message.workflow.process.index.GBTS"),
          content: text,
          okText: this.$t("message.workflow.process.index.QRGB"),
          cancelText: this.$t("message.workflow.process.index.QX"),
          onOk: () => {
            // 删除线先判断删除的是否是当前正在打开的tab，如果打开到最后一个tab，如果没有打开还是在当前的tab
            if (this.active === index) {
              // 删除的就是当前打开的
              this.tabs.splice(index, 1);
              this.choose(this.tabs.length - 1);
            } else {
              this.tabs.splice(index, 1);
              // this.choose(this.tabs.length - 1);
            }
            this.updateProjectCacheByTab();
          },
          onCancel: () => {}
        });
      } else {
        // 删除线先判断删除的是否是当前正在打开的tab，如果打开到最后一个tab，如果没有打开还是在当前的tab
        if (this.active === index) {
          // 删除的就是当前打开的
          this.tabs.splice(index, 1);
          this.choose(this.tabs.length - 1);
        } else {
          this.tabs.splice(index, 1);
          // this.choose(this.tabs.length - 1);
        }
        this.updateProjectCacheByTab();
      }
    },
    check(node) {
      if (node) {
        let boolean = true;
        this.tabs.map(item => {
          if (node.key === item.key) {
            boolean = true;
          } else {
            if (this.tabs.length > 10) {
              boolean = false;
              return;
            }
            boolean = true;
          }
        });
        if (!boolean) {
          this.$Message.warning(this.$t("message.workflow.process.index.CCXE"));
        }
        return boolean;
      } else {
        if (this.tabs.length > 10) {
          this.$Message.warning(this.$t("message.workflow.process.index.CCXE"));
          return false;
        }
        return true;
      }
    },
    dblclickNode(index, args) {
      if (!this.check(args[0][0])) {
        return;
      }
      const node = args[0][0];
      // 如果节点已打开，则选择
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].key === node.key) return this.choose(i);
      }
      // 目前的内部节点的supportJump为true，但是没有url,且不需要创建弹窗
      if (node.supportJump && !node.shouldCreationBeforeNode && !node.jumpUrl) {
        const len = node.resources ? node.resources.length : 0;
        if (len && node.jobContent && node.jobContent.script) {
          // 判断是否有保存过脚本
          const resourceId = node.resources[0].resourceId;
          const fileName = node.resources[0].fileName;
          const version = node.resources[0].version;
          let config = {
            method: "get"
          };
          if (this.query.product) {
            config.headers = {
              "Token-User": this.getUserName()
            };
          }
          api.fetch(this.query.product ? "/filesystem/product/openScriptFromBML" : "/filesystem/openScriptFromBML", {
            fileName,
            resourceId,
            version,
            creator: node.creator || "",
            projectName: this.$route.query.projectName || ""
          }, config).then(res => {
            let content = res.scriptContent;
            let params = {};
            params.variable = this.convertSettingParamsVariable(res.metadata);
            params.configuration = !node.params || isEmpty(node.params.configuration) ? {
              special: {},
              runtime: {},
              startup: {}
            } : {
              special: node.params.configuration.special || {},
              runtime: node.params.configuration.runtime || {},
              startup: node.params.configuration.startup || {}
            };
            params.configuration.runtime.contextID = node.contextID;
            params.configuration.runtime.nodeName = node.title;
            this.getTabsAndChoose({
              type: "IDE",
              node,
              data: {
                content,
                params
              }
            });
          });
        } else {
          // 如果节点是导入进来的，可能存在脚本内容
          let content = node.jobContent && node.jobContent.code ? node.jobContent.code : "";
          let params = {};
          params.variable = this.convertSettingParamsVariable({});
          params.configuration = !node.params || isEmpty(node.params.configuration) ? {
            special: {},
            runtime: {},
            startup: {}
          } : {
            special: node.params.configuration.special || {},
            runtime: node.params.configuration.runtime || {},
            startup: node.params.configuration.startup || {}
          };
          params.configuration.runtime.contextID = node.contextID;
          params.configuration.runtime.nodeName = node.title;
          this.getTabsAndChoose({
            type: "IDE",
            node,
            data: {
              content,
              params
            }
          });
        }
        return;
      }
      if (node.type == NODETYPE.FLOW) {
        // 子流程必须已保存, 才可以被打开
        let flowId = node.jobContent ? node.jobContent.embeddedFlowId : "";
        let {orchestratorVersionId, id} = {...this.query}
        this.getTabsAndChoose({
          type: "Process",
          node,
          data: {
            appId: flowId,
            orchestratorVersionId,
            id
          }
        });
        return;
      }
      // iframe打开的节点
      if (node.supportJump && node.jumpUrl) {
        let id = node.jobContent ? node.jobContent.id : "";
        this.getTabsAndChoose({
          type: "Iframe",
          node,
          data: {
            id
          }
        });
      }
    },
    getTabsAndChoose({ type, node, data }) {
      this.$set(node, "isChange", false);
      this.tabs.push({
        type,
        key: node.key,
        title: node.title,
        close: true,
        // 把节点的引用放到这里
        node,
        data,
        isHover: false
      });
      // 记录打开的tab的依赖关系
      this.openFileAction(node);
      this.choose(this.tabs.length - 1);
      this.updateProjectCacheByTab();
    },
    openFileAction(node) {
      // 判断当前打开的节点的父工作过流是否已经有打开的节点s
      const currnentTab = this.tabs[this.active];
      if (Object.keys(this.openFiles).includes(currnentTab.key)) {
        Object.keys(this.openFiles).map(key => {
          // 找到同一父节点下是否曾今已经打开过
          if (key == currnentTab.key) {
            if (!this.openFiles[key].includes(node.key)) {
              this.openFiles[key].push(node.key);
            }
          }
        });
      } else {
        this.openFiles[currnentTab.key] = [node.key];
      }
    },
    saveIDE(index, args) {
      if (!this.check()) {
        return;
      }
      if (args[0].data) {
        this.tabs[index].data = args[0].data;
      }
      // 取到节点
      let node = this.tabs[index].node;
      this.saveNode(args, node, true);
    },
    saveNode(args, node, scriptisSave = true) {
      // scriptisSave用来判断是否是脚本保存的触发和关联触发
      // 这个地方注意：在关联脚本、scriptis保存脚本、qualitis保存都会调用，参数不一样，关联脚本args是对象，scriptis保存是arguments, qualitis保存过来的是空对象，所以要处理下
      let resource = args;
      let currentNode = node;
      if (isArguments(args)) {
        resource = args[0].resource;
        currentNode = args[0].node;
      }
      if (!node.jobContent) {
        node.jobContent = {};
      }
      if (!currentNode || node.key !== currentNode.key) return;
      node.jobContent.script = resource.fileName;
      delete node.jobContent.code; // code提交生成script之后删除code
      if (!node.resources) {
        node.resources = [];
      }
      // qualitis 过来是没有值的, 空对象传给后台会报错
      if (Object.keys(resource).length > 0) {
        if (
          node.resources.length > 0 &&
          node.resources[0].resourceId === resource.resourceId
        ) {
          // 已保存过的直接替换，没有保存的首项追加
          node.resources[0] = resource;
        } else {
          node.resources.unshift(resource);
        }
      }
      this.$refs.process.forEach(item => {
        item.json.nodes.forEach(subitem => {
          if (subitem.key === currentNode.key) {
            // 在这里直接改originalData值，组件里并没有相应，所以改为触发组件事件
            item.updateOriginData(node, scriptisSave);
          }
        });
      });
      // 更新节点的编辑器的内容也更新缓存的tabs
      this.updateProjectCacheByTab();
    },
    convertSettingParamsVariable(params) {
      const variable = isEmpty(params.variable) ? [] : util.convertObjectToArray(params.variable);
      return variable;
    },
    saveTip(cb, cancel) {
      this.$Modal.confirm({
        title: this.$t("message.workflow.process.index.FHTX"),
        content: `<p>${this.$t("message.workflow.process.index.WBCSFBC")}</p>`,
        okText: this.$t("message.workflow.process.index.BC"),
        cancelText: this.$t("message.workflow.process.index.QX"),
        onOk: cb,
        onCancel: cancel
      });
    },
    isChange(index, val) {
      if (this.tabs[index].node) {
        this.tabs[index].node.isChange = val[0];
        this.$emit("isChange", val[0]);
      }
    },
    beforeLeaveHook() {},
    checkOpened(node, cb) {
      const isOpened = this.tabs.find(item => item.key === node.key);
      cb(!!isOpened);
    },
    deleteNode(node) {
      let index = null;
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].key === node.key) {
          index = i;
        }
      }
      if (index) {
        this.remove(index);
      }
    },
    saveBaseInfo(node) {
      this.tabs = this.tabs.map(item => {
        if (item.key === node.key) {
          item.title = node.title;
        }
        return item;
      });
    },
    updateProjectCacheByTab() {
      this.dispatch("workflowIndexedDB:updateProjectCache", {
        projectID: this.$route.query.projectID,
        key: "tabList",
        value: {
          tab: this.tabs,
          ***REMOVED***",
          sKey: "tab",
          sValue: this.query.flowId
        },
        isDeep: true
      });
    },
    updateProjectCacheByActive() {
      this.dispatch("workflowIndexedDB:updateProjectCache", {
        projectID: this.$route.query.projectID,
        key: "tabList",
        value: {
          active: this.active,
          ***REMOVED***",
          sKey: "active",
          sValue: this.query.flowId
        },
        isDeep: true
      });
    },
    getCache() {
      return new Promise(resolve => {
        this.dispatch("workflowIndexedDB:getProjectCache", {
          projectID: this.$route.query.projectID,
          cb: cache => {
            const list = (cache && cache.tabList) || [];
            let tabs = null;
            list.forEach(item => {
              if (+item.flowId === +this.query.flowId) {
                tabs = item.tab;
                this.active = item.active || 0;
              }
            });
            resolve(tabs);
          }
        });
      });
    },
    changeTitle(val) {
      // 地图模式下，名字为地图模式；最新工作流可编辑时，名字为编辑模式；历史版本进去时，为只读模式
      if (val) {
        this.tabs[0].title = this.$t("message.workflow.process.index.DTMS");
      } else {
        if (this.query.readonly === "true") {
          this.tabs[0].title = this.$t("message.workflow.process.index.ZDMS");
        } else {
          this.tabs[0].title = this.$t("message.workflow.process.index.BJMS");
        }
      }
    },
    showDS() {
      util.checkToken(() => {
        let tab = {
          title: this.query.name + '-' + this.$t("message.workflow.process.schedule"),
          type: "DS",
          close: true,
          data: this.query,
          node: {
            isChange: false,
            type: "workflow.subflow"
          },
          key: this.query.appId,
          isHover: false
        }
        for (let i = 0;i < this.tabs.length; i++) {
          let cur = this.tabs[i]
          // 已经打开
          if (cur.key === this.query.appId) {
            return  this.choose(i)
          }
        }
        this.tabs.push(tab)
        this.choose(this.tabs.length - 1)
        this.getListData()
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
    queryTimingList(index) {
      this.schedulerId = this.list[index].id
      this.activeList(3)
    },
    online(index) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/process/release`, {
        processId: this.list[index].id,
        releaseState: 1
      }, {useForm: true}).then(() => {
        this.getListData()
        this.list[index].isOnline = true
      })
    },
    offline(index) {
      api.fetch(`dolphinscheduler/projects/${this.projectName}/process/release`, {
        processId: this.list[index].id,
        releaseState: 0
      }, {useForm: true}).then(() => {
        this.getListData()
        this.list[index].isOnline = false
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
        title: this.$t('message.scheduler.setTime'),
        content: `<p>${this.$t('message.scheduler.delete')}?</p>`,
        okText: this.$t('message.scheduler.ok'),
        cancelText: this.$t('message.scheduler.cancel'),
        onOk: () => {
          api.fetch(`dolphinscheduler/projects/${this.projectName}/schedule/delete`, {
            scheduleId: item.id,
          }, 'get').then(() => {
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
  },
};
</script>
<style lang="scss" src="@/apps/workflows/assets/styles/process.scss"></style>
<style lang="scss" scoped>
.scheduler-wrapper{
  background-color: white;
  min-height: 80vh;
  .scheduler-menu{
    float: left;
    width: 250px;
    font-size: 14px;
    min-height: 80vh;
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
    }
  }
  .left-panel{
    height: 80vh;
    width: calc(100vw - 250px - 65px - 250px);
    background: #FFFFFF;
    position: absolute;
    left: 100vw;
    z-index: 99;
    padding: 23px 26px;
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
