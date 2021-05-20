<template>
  <div class="timing-process-model">
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.StartAndStopTime')}}
      </div>
      <div class="cont">
        <template>
          <Date-picker
            style="width: 360px"
            v-model="scheduleTime"
            @change="_datepicker"
            type="datetimerange"
            range-separator="-"
            :start-placeholder="$t('message.scheduler.runTask.startDate')"
            :end-placeholder="$t('message.scheduler.runTask.endDate')"
            value-format="yyyy-MM-dd HH:mm:ss">
          </Date-picker>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <i-button type="primary"  style="margin-left:20px" round :loading="spinnerLoading" @click="preview()">{{$t('message.scheduler.runTask.ExecuteTime')}}</i-button>
      <div class="text">
        {{$t('message.scheduler.runTask.Timing')}}
      </div>
      <div class="cont">
        <template>
          <Poptip
            placement="bottom-start"
            trigger="click">
            <template>
              <Input
                style="width: 360px;"
                type="text"
                readonly
                :value="crontab">
              </Input>
            </template>
            <div class="crontab-box" slot="content">
              <v-crontab v-model="crontab" :locale="i18n"></v-crontab>
            </div>
          </Poptip>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.Timezone')}}
      </div>
      <div class="cont">
        <template>
          <Select v-model=timezoneId filterable placeholder="Timezone">
            <Option
              v-for="item in availableTimezoneIDList"
              :key="item"
              :label="item"
              :value="item">
            </Option>
          </Select>
        </template>
      </div>
    </div>
    <div class="clearfix list">
      <div style = "padding-left: 150px;">{{$t('message.scheduler.runTask.fiveTimes')}}</div>
      <ul style = "padding-left: 150px;">
        <li v-for="(time,i) in previewTimes" :key='i'>{{time}}</li>
      </ul>
    </div>

    <div class="clearfix list">
      <div class="text">
        {{$t('message.scheduler.runTask.failureStrategy')}}
      </div>
      <div class="cont">
        <template>
          <Radio-group v-model="failureStrategy" style="margin-top: 7px;">
            <Radio :label="'CONTINUE'">{{$t('message.scheduler.runTask.continue')}}</Radio>
            <Radio :label="'END'">{{$t('message.scheduler.runTask.end')}}</Radio>
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
          <Select
            style="width: 200px;"
            v-model="warningType">
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
            v-model="warningGroupId"
            :disabled="!notifyGroupList.length">
            <Input slot="trigger" slot-scope="{ selectedModel }" readonly :value="selectedModel ? selectedModel.label : ''" style="width: 200px;" @on-click-icon.stop="warningGroupId = ''">
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
      <template>
        <Button type="text" @click="close()"> {{$t('message.scheduler.runTask.cancel')}} </Button>
        <Button type="primary" round :loading="spinnerLoading" @click="ok()">{{spinnerLoading ? 'Loading...' : (timingData.item.crontab ? $t('message.scheduler.runTask.edit') : $t('message.scheduler.runTask.create'))}} </Button>
      </template>
    </div>
  </div>
</template>
<script>
/*eslint-disable */
import moment from 'moment-timezone'
import api from '@/common/service/api'
import { vCrontab } from './crontab/index'
import dayjs from 'dayjs'

export default {
  name: 'timing-process',
  data () {
    return {
      api,
      processDefinitionId: 0,
      failureStrategy: 'CONTINUE',
      availableTimezoneIDList: moment.tz.names(),
      warningType: 'NONE',
      notifyGroupList: [],
      warningGroupId: '',
      spinnerLoading: false,
      scheduleTime: '',
      crontab: '0 0 * * * ? *',
      timezoneId: moment.tz.guess(),
      cronPopover: false,
      i18n: localStorage.getItem('locale'),
      processInstancePriority: 'MEDIUM',
      workerGroup: '',
      previewTimes: [],
      // Global custom parameters
      receiverCcs: '',
      receivers: '',
      workerGroupsList: [],
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
  props: {
    timingData: Object
  },
  methods: {
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
    _datepicker (val) {
      this.scheduleTime = val
    },
    _verification () {
      if (!this.scheduleTime) {
        this.$message.warning(this.$t('Please select time'))
        return false
      }

      if (this.scheduleTime[0] === this.scheduleTime[1]) {
        this.$message.warning(this.$t('The start time must not be the same as the end'))
        return false
      }

      if (!this.crontab) {
        this.$message.warning(this.$t('Please enter crontab'))
        return false
      }
      return true
    },
    _timing () {
      if (this._verification()) {
        let api = ''
        let searchParams = {
          schedule: JSON.stringify({
            startTime: this.scheduleTime[0],
            endTime: this.scheduleTime[1],
            crontab: this.crontab,
            timezoneId: this.timezoneId
          }),
          failureStrategy: this.failureStrategy,
          warningType: this.warningType,
          processInstancePriority: this.processInstancePriority,
          warningGroupId: this.warningGroupId === '' ? 0 : this.warningGroupId,
          workerGroup: this.workerGroup
        }
        let msg = ''

        // edit
        if (this.timingData.item.crontab) {
          api = 'dag/updateSchedule'
          searchParams.id = this.timingData.item.id
          msg = `${this.$t('Edit')}${this.$t('Success')},${this.$t('Please go online')}`
        } else {
          api = 'dag/createSchedule'
          searchParams.processDefinitionId = this.timingData.item.id
          msg = `${this.$t('Create')}${this.$t('Success')}`
        }

        /*this.api.fetch(api, searchParams, 'post').then(res => {
          this.$message.success(msg)
          this.$emit('onUpdateTiming')
        }).catch(e => {
          this.$message.error(e.msg || '')
        })*/
      }
    },

    _preview () {
      if (this._verification()) {
        let api = 'dag/previewSchedule'
        let searchParams = {
          schedule: JSON.stringify({
            startTime: this.scheduleTime[0],
            endTime: this.scheduleTime[1],
            crontab: this.crontab
          })
        }

        /*this.api.fetch(api, searchParams, 'post').then(res => {
          if (res.length) {
            this.previewTimes = res
          } else {
            this.$message.warning(`${this.$t('There is no data for this period of time')}`)
          }
        })*/
      }
    },

    _getNotifyGroupList () {
      /*return new Promise((resolve, reject) => {
        this.api.fetch('dag/getNotifyGroupList', 'get').then(res => {
          this.notifyGroupList = res
          if (this.notifyGroupList.length) {
            resolve()
          } else {
            reject(new Error(0))
          }
        })
      })*/
    },
    ok () {
      this._timing()
    },
    close () {
      this.$emit('closeTiming')
    },
    preview () {
      this._preview()
    }
  },
  watch: {
  },
  created () {
    if (this.timingData.item.workerGroup === undefined) {
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
    } else {
      this.workerGroup = this.timingData.item.workerGroup
    }
    if (this.timingData.item.crontab) {
      this.crontab = this.timingData.item.crontab
    }
    if (this.timingData.type === 'timing') {
      let date = new Date()
      let year = date.getFullYear()
      let month = date.getMonth() + 1
      let day = date.getDate()
      if (month < 10) {
        month = '0' + month
      }
      if (day < 10) {
        day = '0' + day
      }
      let startDate = year + '-' + month + '-' + day + ' ' + '00:00:00'
      let endDate = (year + 100) + '-' + month + '-' + day + ' ' + '00:00:00'
      let times = []
      times[0] = startDate
      times[1] = endDate
      this.crontab = '0 0 * * * ? *'
      this.scheduleTime = times
    }
  },
  mounted () {
    let item = this.timingData.item
    // Determine whether to echo
    if (this.timingData.item.crontab) {
      this.crontab = item.crontab
      this.scheduleTime = [this.formatDate(item.startTime), this.formatDate(item.endTime)]
      this.timezoneId = item.timezoneId === null ? moment.tz.guess() : item.timezoneId
      this.failureStrategy = item.failureStrategy
      this.warningType = item.warningType
      this.processInstancePriority = item.processInstancePriority
      /*this._getNotifyGroupList().then(() => {
        this.$nextTick(() => {
          this.warningGroupId = item.warningGroupId
        })
      }).catch(() => { this.warningGroupId = '' })*/
    } else {
      /*this._getNotifyGroupList().then(() => {
        this.$nextTick(() => {
          this.warningGroupId = ''
        })
      }).catch(() => { this.warningGroupId = '' })*/
    }
  },
  components: { vCrontab }
}
</script>

<style lang="scss" rel="stylesheet/scss">
  .timing-process-model {
    width: 100%;
    min-height: 300px;
    background: #fff;
    border-radius: 3px;
    margin-top: 0;
    .crontab-box {
      .v-crontab {
      }
    }
    .form-model {
      padding-top: 0;
    }
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
      >.text {
        width: 140px;
        float: left;
        text-align: right;
        line-height: 32px;
        padding-right: 8px;
      }
      .cont {
        width: 350px;
        float: left;
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
  .v-crontab-form-model {
    .list-box {
      padding: 0;
    }
  }
  .x-date-packer-panel .x-date-packer-day .lattice label.bg-hover {
    background: #00BFFF!important;
    margin-top: -4px;
  }
  .x-date-packer-panel .x-date-packer-day .lattice em:hover {
    background: #0098e1!important;
  }
</style>
