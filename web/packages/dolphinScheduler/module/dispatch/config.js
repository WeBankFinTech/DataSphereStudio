import i18n from '@dataspherestudio/shared/common/i18n'
i18n.$t = i18n.t
const tasksState = {
  SUBMITTED_SUCCESS: {
    id: 0,
    desc: i18n.$t('message.scheduler.tasksState.SUBMITTED_SUCCESS'),
    icon: 'icon-submitted-success',
    color: '#A9A9A9'
  },
  'RUNNING_EXECUTION': {
    id: 1,
    desc: i18n.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
    icon: 'icon-running-execution',
    color: '#0097e0'
  },
  'RUNNING_EXEUTION': {
    id: 100,
    desc: i18n.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
    icon: 'icon-running-execution',
    color: '#0097e0'
  },
  READY_PAUSE: {
    id: 2,
    desc: i18n.$t('message.scheduler.tasksState.READY_PAUSE'),
    icon: 'icon-ready-pause',
    color: '#07b1a3'
  },
  PAUSE: {
    id: 3,
    desc: i18n.$t('message.scheduler.tasksState.PAUSE'),
    icon: 'icon-pause',
    color: '#057c72'
  },
  READY_STOP: {
    id: 4,
    desc: i18n.$t('message.scheduler.tasksState.READY_STOP'),
    icon: 'icon-ready-stop',
    color: '#FE0402'
  },
  STOP: {
    id: 5,
    desc: i18n.$t('message.scheduler.tasksState.STOP'),
    icon: 'icon-stop',
    color: '#e90101'
  },
  FAILURE: {
    id: 6,
    desc: i18n.$t('message.scheduler.tasksState.FAILURE'),
    icon: 'icon-failure',
    color: '#000000'
  },
  SUCCESS: {
    id: 7,
    desc: i18n.$t('message.scheduler.tasksState.SUCCESS'),
    icon: 'icon-success',
    color: '#33cc00'
  },
  NEED_FAULT_TOLERANCE: {
    id: 8,
    desc: i18n.$t('message.scheduler.tasksState.NEED_FAULT_TOLERANCE'),
    icon: 'icon-need-fault-tolerance',
    color: '#FF8C00'
  },
  KILL: {
    id: 9,
    desc: i18n.$t('message.scheduler.tasksState.KILL'),
    icon: 'icon-kill',
    color: '#a70202'
  },
  WAITTING_THREAD: {
    id: 10,
    desc: i18n.$t('message.scheduler.tasksState.WAITTING_THREAD'),
    icon: 'icon-waitting-thread',
    color: '#912eed'
  },
  WAITTING_DEPEND: {
    id: 11,
    desc: i18n.$t('message.scheduler.tasksState.WAITTING_DEPEND'),
    icon: 'icon-watting-depend',
    color: '#5101be'
  },
  DELAY_EXECUTION: {
    id: 12,
    desc: i18n.$t('message.scheduler.tasksState.DELAY_EXECUTION'),
    icon: 'icon-delay-execution',
    color: '#5102ce'
  },
  FORCED_SUCCESS: {
    id: 13,
    desc: i18n.$t('message.scheduler.tasksState.FORCED_SUCCESS'),
    icon: 'icon-forced-success',
    color: '#5102ce'
  }
}

const tasksStateList = [
  {
    id: 0,
    desc: i18n.$t('message.scheduler.tasksState.SUBMITTED_SUCCESS'),
    icon: 'icon-submitted-success',
    code: 'SUBMITTED_SUCCESS',
    color: '#A9A9A9'
  },
  {
    id: 1,
    desc: i18n.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
    icon: 'icon-running-execution',
    code: 'RUNNING_EXEUTION',
    color: '#0097e0'
  },
  {
    id: 2,
    desc: i18n.$t('message.scheduler.tasksState.READY_PAUSE'),
    icon: 'icon-ready-pause',
    code: 'READY_PAUSE',
    color: '#07b1a3'
  },
  {
    id: 3,
    desc: i18n.$t('message.scheduler.tasksState.PAUSE'),
    icon: 'icon-pause',
    code: 'PAUSE',
    color: '#057c72'
  },
  {
    id: 4,
    desc: i18n.$t('message.scheduler.tasksState.READY_STOP'),
    icon: 'icon-ready-stop',
    code: 'READY_STOP',
    color: '#FE0402'
  },
  {
    id: 5,
    desc: i18n.$t('message.scheduler.tasksState.STOP'),
    icon: 'icon-stop',
    code: 'STOP',
    color: '#e90101'
  },
  {
    id: 6,
    desc: i18n.$t('message.scheduler.tasksState.FAILURE'),
    icon: 'icon-failure',
    code: 'FAILURE',
    color: '#000000'
  },
  {
    id: 7,
    desc: i18n.$t('message.scheduler.tasksState.SUCCESS'),
    icon: 'icon-success',
    code: 'SUCCESS',
    color: '#33cc00'
  },
  {
    id: 8,
    desc: i18n.$t('message.scheduler.tasksState.NEED_FAULT_TOLERANCE'),
    icon: 'icon-need-fault-tolerance',
    code: 'NEED_FAULT_TOLERANCE',
    color: '#FF8C00'
  },
  {
    id: 9,
    desc: i18n.$t('message.scheduler.tasksState.KILL'),
    icon: 'icon-kill',
    code: 'KILL',
    color: '#a70202'
  },
  {
    id: 10,
    desc: i18n.$t('message.scheduler.tasksState.WAITTING_THREAD'),
    icon: 'icon-waitting-thread',
    code: 'WAITTING_THREAD',
    color: '#912eed'
  },
  {
    id: 11,
    desc: i18n.$t('message.scheduler.tasksState.WAITTING_DEPEND'),
    icon: 'icon-watting-depend',
    code: 'WAITTING_DEPEND',
    color: '#5101be'
  }
]

const dashboardStateList = [
  {
    id: 1,
    desc: i18n.$t('message.scheduler.tasksState.RUNNING_EXECUTION'),
    icon: 'icon-running-execution',
    code: 'RUNNING_EXEUTION',
    color: '#73A0FA'
  },
  {
    id: 3,
    desc: i18n.$t('message.scheduler.tasksState.PAUSE'),
    icon: 'icon-pause',
    code: 'PAUSE',
    color: '#F9D66F'
  },
  {
    id: 5,
    desc: i18n.$t('message.scheduler.tasksState.STOP'),
    icon: 'icon-stop',
    code: 'STOP',
    color: '#EB7E65'
  },
  {
    id: 6,
    desc: i18n.$t('message.scheduler.tasksState.FAILURE'),
    icon: 'icon-failure',
    code: 'FAILURE',
    color: '#838383'
  },
  {
    id: 7,
    desc: i18n.$t('message.scheduler.tasksState.SUCCESS'),
    icon: 'icon-success',
    code: 'SUCCESS',
    color: '#73DEB3'
  }
]

const publishStatus = {
  'NOT_RELEASE': i18n.$t('message.scheduler.Unpublished'),
  'ONLINE': i18n.$t('message.scheduler.online'),
  'OFFLINE': i18n.$t('message.scheduler.offline')
}

const runningType = [
  {
    desc: i18n.$t('message.scheduler.START_PROCESS'),
    code: 'START_PROCESS'
  },
  {
    desc: i18n.$t('message.scheduler.START_CURRENT_TASK_PROCESS'),
    code: 'START_CURRENT_TASK_PROCESS'
  },
  {
    desc: i18n.$t('message.scheduler.RECOVER_TOLERANCE_FAULT_PROCESS'),
    code: 'RECOVER_TOLERANCE_FAULT_PROCESS'
  },
  {
    desc: i18n.$t('message.scheduler.RECOVER_SUSPENDED_PROCESS'),
    code: 'RECOVER_SUSPENDED_PROCESS'
  },
  {
    desc: i18n.$t('message.scheduler.START_FAILURE_TASK_PROCESS'),
    code: 'START_FAILURE_TASK_PROCESS'
  },
  {
    desc: i18n.$t('message.scheduler.COMPLEMENT_DATA'),
    code: 'COMPLEMENT_DATA'
  },
  {
    desc: i18n.$t('message.scheduler.SCHEDULER'),
    code: 'SCHEDULER'
  },
  {
    desc: i18n.$t('message.scheduler.rerun'),
    code: 'REPEAT_RUNNING'
  },
  {
    desc: i18n.$t('message.scheduler.PAUSE'),
    code: 'PAUSE'
  },
  {
    desc: i18n.$t('message.scheduler.STOP'),
    code: 'STOP'
  },
  {
    desc: i18n.$t('message.scheduler.RECOVER_WAITTING_THREAD'),
    code: 'RECOVER_WAITTING_THREAD'
  }
]

const tasksType = {
  SHELL: {
    desc: 'SHELL',
    color: '#646464',
    icon: 'icon-shell'
  },
  WATERDROP: {
    desc: 'WATERDROP',
    color: '#646465',
    icon: 'icon-shell'
  },
  SUB_PROCESS: {
    desc: 'SUB_PROCESS',
    color: '#0097e0',
    icon: 'icon-shell'
  },
  PROCEDURE: {
    desc: 'PROCEDURE',
    color: '#525CCD',
    icon: 'icon-shell'
  },
  SQL: {
    desc: 'SQL',
    color: '#7A98A1',
    icon: 'icon-shell'
  },
  SPARK: {
    desc: 'SPARK',
    color: '#E46F13',
    icon: 'icon-shell'
  },
  FLINK: {
    desc: 'FLINK',
    color: '#E46F13',
    icon: 'icon-shell'
  },
  MR: {
    desc: 'MapReduce',
    color: '#A0A5CC',
    icon: 'icon-shell'
  },
  PYTHON: {
    desc: 'PYTHON',
    color: '#FED52D',
    icon: 'icon-shell'
  },
  DEPENDENT: {
    desc: 'DEPENDENT',
    color: '#2FBFD8',
    icon: 'icon-shell'
  },
  HTTP: {
    desc: 'HTTP',
    color: '#E46F13',
    icon: 'icon-shell'
  },
  DATAX: {
    desc: 'DataX',
    color: '#1fc747',
    icon: 'icon-shell'
  },
  SQOOP: {
    desc: 'SQOOP',
    color: '#E46F13',
    icon: 'icon-shell'
  },
  CONDITIONS: {
    desc: 'CONDITIONS',
    color: '#E46F13',
    icon: 'icon-shell'
  }
}

export {
  tasksState,
  tasksStateList,
  publishStatus,
  runningType,
  tasksType,
  dashboardStateList
}
