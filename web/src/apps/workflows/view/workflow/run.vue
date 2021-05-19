<template>
  <div class="start-process-model">
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.processName')}}
      </div>
      <div style="line-height: 32px;">{{workflowName}}</div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.failureStrategy')}}
      </div>
      <div class="cont">
        <template>
          <Radio-group v-model="failureStrategy" style="margin-top: 7px;" size="small">
            <Radio :label="'CONTINUE'">{{$t('message.scheduler.runTask.continue')}}</Radio>
            <Radio :label="'END'">{{$t('message.scheduler.runTask.end')}}</Radio>
          </Radio-group>
        </template>

      </div>
    </div>
    <div class="clearfix list" v-if="sourceType === 'contextmenu'" style="margin-top: -8px;">
      <div class="text">
        {{$t('message.scheduler.runTask.nodeExecution')}}
      </div>
      <div class="cont" style="padding-top: 6px;">
        <template>
          <Radio-group v-model="taskDependType" size="small">
            <Radio :label="'TASK_POST'">{{$t('message.scheduler.runTask.backwardExecution')}}</Radio>
            <Radio :label="'TASK_PRE'">{{$t('message.scheduler.runTask.forwardExecution')}}</Radio>
            <Radio :label="'TASK_ONLY'">{{$t('message.scheduler.runTask.executeOnlyTheCurrentNode')}}</Radio>
          </Radio-group>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.notificationStrategy')}}
      </div>
      <div class="cont">
        <template>
          <Select style="width: 200px;" v-model="warningType" size="small">
            <Option
              v-for="city in warningTypeList"
              :key="city.id"
              :value="city.id"
              :label="city.code">
            </Option>
          </Select>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.processPriority')}}
      </div>
      <div class="cont">
        <template>
          <Select v-model="processInstancePriority">
            <Option v-for="item in priorityList" :value="item.code" :key="item.code">{{item.code}}</Option>
          </Select>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.workerGroup')}}
      </div>
      <div class="cont">
        <template>
          <Select v-model="workerGroup">
            <Option v-for="item in workerGroupsList" :value="item.id" :key="item.id">{{item.name}}</Option>
          </Select>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.notificationGroup')}}
      </div>
      <div class="cont">
        <template>
          <Select
            style="width: 200px;"
            clearable
            size="small"
            v-model="warningGroupId"
            :disabled="!notifyGroupList.length">
            <Input slot="trigger" slot-scope="{ selectedModel }" readonly size="small" :value="selectedModel ? selectedModel.label : ''" style="width: 200px;" @on-click-icon.stop="warningGroupId = ''">
              <em slot="suffix" class="el-icon-error" style="font-size: 15px;cursor: pointer;" v-show="warningGroupId"></em>
              <em slot="suffix" class="el-icon-bottom" style="font-size: 12px;" v-show="!warningGroupId"></em>
            </Input>
            <Option
              v-for="city in notifyGroupList"
              :key="city.id"
              :value="city.id"
              :label="city.code">
            </Option>
          </Select>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.complementData')}}
      </div>
      <div class="cont">
        <div style="padding-top: 6px;">
          <template>
            <Checkbox v-model="execType" size="small">{{$t('message.scheduler.runTask.complementProcess?')}}</Checkbox>
          </template>
        </div>
      </div>
    </div>
    <template v-if="execType">
      <div class="clearfix list" style="margin:-6px 0 16px 0">
        <div class="text">
          {{$t('message.scheduler.runTask.modeOfExecution')}}
        </div>
        <div class="cont">
          <template>
            <Radio-group v-model="runMode" style="margin-top: 7px;">
              <Radio :label="'RUN_MODE_SERIAL'">{{$t('message.scheduler.runTask.serialExecution')}}</Radio>
              <Radio :label="'RUN_MODE_PARALLEL'">{{$t('message.scheduler.runTask.parallelExecution')}}</Radio>
            </Radio-group>
          </template>
        </div>
      </div>
      <div class="clearfix list">
        <div class="text">
          {{$t('message.scheduler.runTask.scheduleDate')}}
        </div>
        <div class="cont">
          <template>
            <Date-picker
              style="width: 360px"
              v-model="scheduleTime"
              size="small"
              @change="_datepicker"
              type="datetimerange"
              range-separator="-"
              :start-placeholder="$t('startDate')"
              :end-placeholder="$t('endDate')"
              value-format="yyyy-MM-dd HH:mm:ss">
            </Date-picker>
          </template>
        </div>
      </div>
    </template>
    <div class="clearfix list">
      <div class="text">
        <span>{{$t('message.scheduler.runTask.receivers')}}</span>
      </div>
      <div class="cont" style="width: 688px;">
        <div style="padding-top: 6px;">
          <template>
            <Input v-model="receivers" />
          </template>
        </div>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        <span>{{$t('message.scheduler.runTask.receiverCcs')}}</span>
      </div>
      <div class="cont" style="width: 688px;">
        <div style="padding-top: 6px;">
          <template>
            <Input v-model="receiverCcs" />
          </template>
        </div>
      </div>
    </div>
    <div class="submit">
      <i-button type="text" size="small" @click="close()"> {{$t('message.scheduler.runTask.cancel')}} </i-button>
      <i-button type="primary" size="small" round :loading="spinnerLoading" @click="ok()">{{spinnerLoading ? 'Loading...' : $t('message.scheduler.runTask.start')}} </i-button>
    </div>
  </div>
</template>
<script>
import dayjs from 'dayjs'
import api from '@/common/service/api'

export default {
  name: 'start-process',
  data () {
    return {
      api,
      processDefinitionId: 0,
      failureStrategy: 'CONTINUE',
      warningTypeList: [
        {
          id: 'NONE',
          code: this.$t('message.scheduler.runTask.none_1')
        },
        {
          id: 'SUCCESS',
          code: this.$t('message.scheduler.runTask.success_1')
        },
        {
          id: 'FAILURE',
          code: this.$t('message.scheduler.runTask.failure_1')
        },
        {
          id: 'ALL',
          code: this.$t('message.scheduler.runTask.All_1')
        }
      ],
      workflowName: '',
      warningType: '',
      notifyGroupList: [],
      warningGroupId: '',
      scheduleTime: '',
      spinnerLoading: false,
      execType: false,
      taskDependType: 'TASK_POST',
      runMode: 'RUN_MODE_SERIAL',
      processInstancePriority: 'MEDIUM',
      workerGroup: 'default',
      workerGroupsList: [],
      // Global custom parameters
      receiverCcs: '',
      receivers: '',
      priorityList: [
        {
          code: 'HIGHEST',
          unicode: 'el-icon-top',
          color: '#ff0000'
        },
        {
          code: 'HIGH',
          unicode: 'el-icon-top',
          color: '#ff0000'
        },
        {
          code: 'MEDIUM',
          unicode: 'el-icon-top',
          color: '#EA7D24'
        },
        {
          code: 'LOW',
          unicode: 'el-icon-bottom',
          color: '#2A8734'
        },
        {
          code: 'LOWEST',
          unicode: 'el-icon-bottom',
          color: '#2A8734'
        }
      ]
    }
  },
  mixins: [],
  props: {
    startData: Object,
    startNodeList: {
      type: String,
      default: ''
    },
    sourceType: String
  },
  methods: {
    _datepicker (val) {
      this.scheduleTime = val
    },
    _start () {
      this.spinnerLoading = true
      let param = {
        processDefinitionId: this.startData.id,
        scheduleTime: this.scheduleTime.length && this.scheduleTime.join(',') || '',
        failureStrategy: this.failureStrategy,
        warningType: this.warningType,
        warningGroupId: this.warningGroupId === '' ? 0 : this.warningGroupId,
        execType: this.execType ? 'COMPLEMENT_DATA' : null,
        startNodeList: this.startNodeList,
        taskDependType: this.taskDependType,
        runMode: this.runMode,
        processInstancePriority: this.processInstancePriority,
        workerGroup: this.workerGroup
      }
      // Executed from the specified node
      if (this.sourceType === 'contextmenu') {
        param.taskDependType = this.taskDependType
      }

      /*this.api.fetch(`dag/processStart`, param, 'post').then((res) => {
        this.$message.success(res.msg)
        this.$emit('onUpdateStart')

        setTimeout(() => {
          this.spinnerLoading = false
          this.close()
        }, 500)
      }).catch(e => {
        this.$message.error(e.msg || '')
        this.spinnerLoading = false
      })*/
    },
    _getNotifyGroupList () {
      /*return new Promise((resolve, reject) => {
        this.api.fetch('dag/getNotifyGroupList', 'get').then(res => {
          this.notifyGroupList = res
          resolve()
        })
      })*/
    },
    _getGlobalParams () {
      /*this.api.fetch(`dag/getProcessDetails/${this.startData.id}`, 'get').then(res => {})*/
    },
    ok () {
      this._start()
    },
    close () {
      this.$emit('closeStart')
    }
  },
  watch: {
    execType (a) {
      this.scheduleTime = a ? [dayjs().format('YYYY-MM-DD 00:00:00'), dayjs().format('YYYY-MM-DD 00:00:00')] : ''
    }
  },
  created () {
    this.warningType = this.warningTypeList[0].id
    this.workflowName = this.startData.name
    this._getGlobalParams()
    let stateWorkerGroupsList = []
    if (stateWorkerGroupsList.length) {
      this.workerGroup = stateWorkerGroupsList[0].id
    } else {
      /*this.api.fetch('security/getWorkerGroupsAll', 'get').then(res => {
        this.workerGroupsList = res
        this.$nextTick(() => {
          if (res.length > 0) {
            this.workerGroup = res[0].id
          }
        })
      })*/
    }
  },
  mounted () {
    this._getNotifyGroupList().then(() => {
      this.$nextTick(() => {
        this.warningGroupId = ''
      })
    })
    this.workflowName = this.startData.name
  },
  computed: {},
  components: {}
}
</script>

<style lang="scss" rel="stylesheet/scss">
  .start-process-model {
    width: 100%;
    min-height: 300px;
    background: #fff;
    border-radius: 3px;
  .title-box {
    margin-bottom: 18px;
  span {
    padding-left: 30px;
    font-size: 16px;
    padding-top: 29px;
    display: block;
  }
  }
  .list {
    margin-bottom: 14px;
    height: 32px;
  .text {
    width: 140px;
    float: left;
    text-align: right;
    line-height: 32px;
    padding-right: 8px;
  }
  .cont {
    width: 350px;
    float: left;
  .add-email-model {
    padding: 20px;
  }

  }
  }
  .submit {
    text-align: right;
    padding-right: 30px;
    padding-top: 10px;
    padding-bottom: 30px;
    position: absolute;
    margin-top: 20px;
    right: 0;
  }
  }
</style>
