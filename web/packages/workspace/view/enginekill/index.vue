<template>
  <div style="height: 100%;  padding: 10px 15px;">
    <div class="title">{{ $t('message.enginelist.ruleset')}}</div>
    <Form
      class="table-searchbar"
      ref="searchBar"
      :model="searchBar"
      inline
      label-position="left"
    >
      <FormItem
        prop="name"
        :label="$t('message.enginelist.rulename')"
        :label-width="80"
      >
        <Input v-model="searchBar.name" @on-enter="doQuery" />
      </FormItem>
      <FormItem
        prop="crossCluster"
        :label="$t('message.enginelist.crossCluster')"
        :label-width="120"
      >
        <Select
          style="width:150px"
          v-model="searchBar.crossCluster"
          :placeholder="$t('message.enginelist.ruleform.plsselect')"
          clearable
          @on-change="handleCrossCluster($event, 'search')"
        >
          <Option :label="$t('message.enginelist.yes')" value="true" />
          <Option :label="$t('message.enginelist.no')" value="false" />
        </Select>
      </FormItem>
      <FormItem
        prop="yarnQueue"
        :label="$t('message.enginelist.queue')"
        :label-width="50"
      >
        <div @click="handleQueueFocus(searchBar.crossCluster)">
          <Select
            style="width:150px"
            v-model="searchBar.yarnQueue"
            filterable
            clearable
            @on-change="doQuery"
          >
            <Option
              v-for="(item) in searchQueues"
              :label="item"
              :title="item"
              :value="item"
              :key="item"
            />
          </Select>
        </div>
      </FormItem>
      <FormItem
        prop="status"
        :label="$t('message.enginelist.status')"
        :label-width="50"
      >
        <Select
          v-model="searchBar.status"
          style="min-width:120px;"
          clearable
          @on-change="doQuery"
        >
          <Option
            v-for="(item) in statusList"
            :label="item.label"
            :value="item.value"
            :key="item.value"
          />
        </Select>
      </FormItem>
      <FormItem class="btn">
        <Button
          type="primary"
          @click="doQuery"
        >{{$t('message.enginelist.find')}}</Button>
        <Button
          type="success"
          @click="add"
        >{{$t('message.enginelist.add')}}</Button>
      </FormItem>
    </Form>
    <div class="card-list-wrapper">
      <div
        class="card-item"
        v-for="item in showList"
        :key="item.name"
      >
        <div class="row-title">
          <div> {{item.name}}
            <span
              class="status"
              :class="{enable:item.status}"
            >
              [{{item.status ? $t('message.enginelist.enable') : $t('message.enginelist.disable')}}]
            </span>
          </div>
          <div>
            <span
              class="btn-text"
              @click="edit(item)"
            >{{$t('message.enginelist.edit')}}</span>
            <span
              class="btn-text"
              @click="enable(item)"
            >{{ item.status ? $t('message.enginelist.disable') : $t('message.enginelist.enable')}}</span>
            <span
              class="btn-text del"
              @click="deleteItem(item)"
            >{{$t('message.enginelist.delete')}}</span>
          </div>
        </div>
        <div class="row-item">
          {{ $t('message.enginelist.ruleform.queue')}}：{{ item.queue }}
        </div>
        <div class="row-item">
          {{ $t('message.enginelist.crossCluster')}}：{{ item.crossCluster ? $t('message.enginelist.yes') : $t('message.enginelist.no') }}
        </div>
        <div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.trigger')}}：{{ formatConditions(item.triggerConditionConf.conditions) }}
          </div>
          <div class="row-item inline-block" v-if="item.triggerConditionConf.conditions.length > 1">
            {{ $t('message.enginelist.ruleform.relation')}}：
            {{ item.triggerConditionConf.relation == 'and' ? $t('message.enginelist.ruleform.and') : $t('message.enginelist.ruleform.or') }}
          </div>
        </div>
        <div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.stop')}}：{{ formatConditions(item.terminateConditionConf.conditions) }}
          </div>
          <div class="row-item inline-block" v-if="item.terminateConditionConf.conditions.length > 1">
            {{ $t('message.enginelist.ruleform.relation')}}：{{ item.terminateConditionConf.relation == 'and' ? $t('message.enginelist.ruleform.and') : $t('message.enginelist.ruleform.or') }}
          </div>
        </div>
        <div class="row-item inline-block">
          {{ $t('message.enginelist.ruleform.alertuser')}}：{{ item.imsConf.enable && item.imsConf.receiver && item.imsConf.receiver.join(';') }}
        </div>
        <div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.modifyUser')}}：{{ item.modifier }}
          </div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.modifyTime')}}：{{ item.modifyTime | formatDate('YYYY-MM-DD HH:mm:ss') }}
          </div>
        </div>
        <div class="row-item inline-block">
          {{ $t('message.enginelist.ruleform.createUser')}}：{{ item.creator }}
        </div>
        <div class="row-item inline-block">
          {{ $t('message.enginelist.ruleform.createTime')}}：{{ item.createTime | formatDate('YYYY-MM-DD HH:mm:ss') }}
        </div>
      </div>
      <div
        v-if="showList.length<1"
        class="no-data"
      >{{ $t('message.common.No') }}</div>
    </div>
    <Page
      v-if="pageData.total > 10"
      class="pagebar"
      :total="pageData.total"
      :current="pageData.pageNow"
      show-total
      @on-change="handlePageChange"
      @on-page-size-change="handlePageSizeChange"
    />
    <Modal
      v-model="showModal"
      :width="640"
      :title="isEdit ? $t('message.enginelist.ruleform.edittitle') : $t('message.enginelist.ruleform.addtitle')"
    >
      <Form
        ref="ruleForm"
        label-position="right"
        class="engine_kill_rule_form"
        :label-width="120"
        :model="formData"
        :rules="formValid"
      >
        <FormItem
          :label="$t('message.enginelist.rulename')"
          prop="name"
        >
          <Input
            v-model="formData.name"
            :placeholder="$t('message.enginelist.ruleform.enterName')"
          />
        </FormItem>
        <FormItem
          :label="$t('message.enginelist.ruleform.description')"
          prop="description"
        >
          <Input
            v-model="formData.description"
            type="textarea"
          />
        </FormItem>
        <FormItem
          :label="$t('message.enginelist.crossCluster')"
          prop="crossCluster"
        >
          <Select
            v-model="formData.crossCluster"
            style="width:150px"
            :disabled="isEdit"
            :placeholder="$t('message.enginelist.ruleform.plsselect')"
            @on-change="handleCrossCluster"
          >
            <Option :label="$t('message.enginelist.yes')" value="true" />
            <Option :label="$t('message.enginelist.no')" value="false" />
          </Select>
        </FormItem>
        <FormItem
          :label="$t('message.enginelist.ruleform.queue')"
          prop="queue"
        >
          <div @click="handleQueueFocus(formData.crossCluster)">
            <Select
              v-model="formData.queue"
              style="width:150px"
              filterable
              :disabled="isEdit"
              :placeholder="$t('message.enginelist.ruleform.selectqueue')"
            >
              <Option
                v-for="(item) in queues"
                :label="item"
                :value="item"
                :title="item"
                :key="item"
              />
            </Select>
          </div>
        </FormItem>
        <!-- 触发条件 -->
        <FormItem
          :label="$t('message.enginelist.ruleform.conditionSet')"
          prop="triggerConditionConf"
        >
          <Checkbox v-model="conditionRule.cpu">{{$t('message.enginelist.cpu')}}</Checkbox>
          <Checkbox v-model="conditionRule.memory">{{$t('message.enginelist.memory')}}</Checkbox>
        </FormItem>
        <Row class="condition-row">
          <Col span="12">
            <div>{{ $t('message.enginelist.ruleform.trigger') }}</div>
            <div
              v-if="conditionRule.cpu"
              class="rule-field"
            >
              <span> {{$t('message.enginelist.cpu')}} >= </span>
              <InputNumber
                style="width:70px; margin-right:5px;margin-left:10px"
                placeholder="1-100"
                type="number"
                @on-blur="validInput"
                :max="100"
                :min="1"
                v-model="formData.triggerConditionConf.conditions[0].value"
              /> %
            </div>
            <div
              v-if="conditionRule.memory"
              class="rule-field"
            >
              <span> {{$t('message.enginelist.memory')}} >= </span>
              <InputNumber
                style="width:70px; margin-right:5px;margin-left:10px"
                placeholder="1-100"
                type="number"
                @on-blur="validInput"
                :max="100"
                :min="1"
                v-model="formData.triggerConditionConf.conditions[1].value"
              /> %
            </div>
            <span
              v-if="conditionRule.cpu && conditionRule.memory"
              style="margin-right:10px"
            >{{$t('message.enginelist.ruleform.relation')}}</span>
            <RadioGroup
              v-if="conditionRule.cpu && conditionRule.memory"
              v-model="formData.triggerConditionConf.relation"
            >
              <Radio label="and">{{$t('message.enginelist.ruleform.and')}}</Radio>
              <Radio label="or">{{$t('message.enginelist.ruleform.or')}}</Radio>
            </RadioGroup>
          </Col>
          <Col span="12">
            <div>{{ $t('message.enginelist.ruleform.stop') }}</div>
            <div
              v-if="conditionRule.cpu"
              class="rule-field"
            >
              <span> {{$t('message.enginelist.cpu')}} <= </span>
              <InputNumber
                style="width:70px; margin-right:5px;margin-left:10px"
                placeholder="1-100"
                type="number"
                @on-blur="validInput"
                :max="100"
                :min="1"
                v-model="formData.terminateConditionConf.conditions[0].value"
              /> %
            </div>
            <div
              v-if="conditionRule.memory"
              class="rule-field"
            >
              <span> {{$t('message.enginelist.memory')}} <= </span>
              <InputNumber
                style="width:70px; margin-right:5px;margin-left:10px"
                @on-blur="validInput"
                placeholder="1-100"
                type="number"
                :max="100"
                :min="1"
                v-model="formData.terminateConditionConf.conditions[1].value"
              /> %
            </div>
            <span
              v-if="conditionRule.cpu && conditionRule.memory"
              style="margin-right:10px"
            >{{$t('message.enginelist.ruleform.relation')}}</span>
            <RadioGroup
              v-if="conditionRule.cpu && conditionRule.memory"
              v-model="formData.terminateConditionConf.relation"
            >
              <Radio label="and">{{$t('message.enginelist.ruleform.and')}}</Radio>
              <Radio label="or">{{$t('message.enginelist.ruleform.or')}}</Radio>
            </RadioGroup>
          </Col>
        </Row>
        <div
          v-if="ruleItemValid"
          class="item-error-tip"
        >{{ruleItemValid}}</div>
        <FormItem
          :label="$t('message.enginelist.ruleform.alertset')"
          prop="imsConf.enable"
        >
          <Checkbox v-model="formData.imsConf.enable">{{$t('message.enginelist.ruleform.alertoption')}}</Checkbox>
        </FormItem>
        <FormItem
          v-if="formData.imsConf.enable"
          :label="$t('message.enginelist.ruleform.level')"
          prop="imsConf.level"
        >
          <Select v-model="formData.imsConf.level">
            <Option
              v-for="(item) in levels"
              :label="item"
              :value="item"
              :key="item"
            />
          </Select>
        </FormItem>
        <FormItem
          v-if="formData.imsConf.enable"
          :label="$t('message.enginelist.ruleform.duration')"
          prop="imsConf.duration"
        >
          <Select v-model="formData.imsConf.duration">
            <Option
              v-for="(item) in durations"
              :value="item.value"
              :key="item.value"
            >
              {{ item.label }}
            </Option>
          </Select>
        </FormItem>
        <FormItem
          v-if="formData.imsConf.enable"
          :label="$t('message.enginelist.ruleform.alertuser')"
          prop="imsConf.receiver"
        >
          <Select
            v-model="formData.imsConf.receiver"
            multiple
            filterable
            :placeholder="$t('message.enginelist.ruleform.selectalertuser')"
          >
            <Option
              v-for="(item) in userlist"
              :value="item.name"
              :key="item.name"
            >
              {{ item.name }}
            </Option>
          </Select>
        </FormItem>
      </Form>
      <template slot="footer">
        <Button @click="handleCancel">{{$t('message.common.cancel')}}</Button>
        <Button
          type="primary"
          @click="saveRule"
        >{{$t('message.common.ok')}}</Button>
      </template>
    </Modal>
  </div>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
import filters from '@dataspherestudio/shared/common/util/filters';
import { GetWorkspaceUserManagement } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
export default {
  components: {
  },
  data() {
    return {
      searchBar: {
        status: '',
        name: '',
        yarnQueue: '',
        crossCluster: ''
      },
      showModal: false,
      list: [],
      formData: {
        strategyId: '',
        name: '',
        description: '',
        crossCluster: '',
        triggerConditionConf: {
          relation: "or",
          conditions: [{
            field: 'cpu',
            operation: '>=',
            value: null
          }, {
            field: 'memory',
            operation: '>=',
            value: null
          }]
        },
        terminateConditionConf: {
          relation: "or",
          conditions: [{
            field: 'cpu',
            operation: '<=',
            value: null
          }, {
            field: 'memory',
            operation: '<=',
            value: null
          }]
        },
        queue: '',
        imsConf: {
          enable: false,
          duration: null,
          level: null,
          receiver: null
        }
      },
      queues: [],
      searchQueues: [],
      levels: ['MINOR', 'WARNING'],
      durations: [
        { value: '1', label: "1min" },
        { value: '5', label: "5min" },
        { value: '10', label: "10min" },
        { value: '30', label: "30min" }
      ],
      userlist: [],
      statusList: [{
        label: this.$t('message.enginelist.enable'),
        value: '1'
      }, {
        label: this.$t('message.enginelist.disable'),
        value: '0'
      }],
      conditionRule: {
        cpu: true,
        memory: false
      },
      isEdit: false,
      ruleItemValid: '',
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10
      },
    }
  },
  computed: {
    formValid() {
      let validateTrigger = (rule, value, callback) => {
        let valid = this.conditionRule.cpu || this.conditionRule.memory;
        if (!valid) {
          this.ruleItemValid = this.$t("message.enginelist.ruleform.selectRuleType")
          callback()
          return
        }
        let triggerConf = this.formData.triggerConditionConf.conditions
        let terminateConf = this.formData.terminateConditionConf.conditions
        if (triggerConf[0] && this.conditionRule.cpu) {
          if (!triggerConf[0].field || triggerConf[0].value > 100 || triggerConf[0].value < 1 || triggerConf[0].value % 1 !== 0) {
            valid = false
          }
        }
        if (triggerConf[1] && this.conditionRule.memory) {
          if (!triggerConf[1].field || triggerConf[1].value > 100 || triggerConf[1].value < 1 || triggerConf[1].value % 1 !== 0) {
            valid = false
          }
        }
        if (terminateConf[0] && this.conditionRule.cpu) {
          if (!terminateConf[0].field || terminateConf[0].value > 100 || terminateConf[0].value < 1 || terminateConf[0].value % 1 !== 0) {
            valid = false
          }
        }
        if (terminateConf[1] && this.conditionRule.memory) {
          if (!terminateConf[1].field || terminateConf[1].value > 100 || terminateConf[1].value < 1 || terminateConf[1].value % 1 !== 0) {
            valid = false
          }
        }
        if (this.conditionRule.cpu && (triggerConf[0].value <= terminateConf[0].value || !terminateConf[0].value)) {
          valid = false
        }
        if (this.conditionRule.memory && (triggerConf[1].value <= terminateConf[1].value || !terminateConf[1].value)) {
          valid = false
        }
        this.ruleItemValid = valid ? '' : this.$t("message.enginelist.ruleform.ruleItvalue")
        callback()
      }

      return {
        name: [
          {
            required: true,
            message: this.$t("message.enginelist.ruleform.enterName"),
            trigger: "blur",
          },
          { message: `${this.$t("message.enginelist.ruleform.rulelength")}64`, max: 64 },
          {
            type: "string",
            pattern: /^[\w\u4e00-\u9fa5]+$/,
            message: this.$t("message.enginelist.ruleform.namerule"),
            trigger: "blur",
          }
        ],
        description: [
          { message: `${this.$t("message.enginelist.ruleform.rulelength")}128`, max: 128 },
        ],
        crossCluster: [
          {
            required: true,
            message: this.$t("message.enginelist.ruleform.plsselectCross"),
            trigger: "change",
          },
        ],
        queue: [
          {
            required: true,
            message: this.$t("message.enginelist.ruleform.selectqueue"),
            trigger: "change",
          },
        ],
        'imsConf.level': [{
          required: true,
          message: this.$t("message.enginelist.ruleform.plsselect"),
          trigger: "change",
        }],
        'imsConf.duration': [
          {
            required: true,
            message: this.$t("message.enginelist.ruleform.plsselect"),
            trigger: "change",
          }
        ],
        'imsConf.receiver': [
          {
            type: 'array',
            required: true,
            max: 15,
            message: this.$t("message.enginelist.ruleform.selectalertuser"),
            trigger: "change",
          }
        ],
        triggerConditionConf: [
          {
            required: true,
            validator: validateTrigger,
            trigger: "blur",
          },
        ]
      }
    },
    showList() {
      return this.list.slice((this.pageData.pageNow - 1) * this.pageData.pageSize, this.pageData.pageSize * this.pageData.pageNow)
    }
  },
  watch: {
    'formData.triggerConditionConf.conditions': {
      handler() {
        this.$refs.ruleForm.validateField('triggerConditionConf')
      },
      deep: true
    },
    'conditionRule.cpu'() {
      this.formData.triggerConditionConf.conditions[0].value = null
      this.formData.terminateConditionConf.conditions[0].value = null
      this.$refs.ruleForm.validateField('triggerConditionConf')
    },
    'conditionRule.memory'() {
      this.formData.triggerConditionConf.conditions[1].value = null
      this.formData.terminateConditionConf.conditions[1].value = null
      this.$refs.ruleForm.validateField('triggerConditionConf')
    }
  },
  filters,
  mounted() {
    this.doQuery()
  },
  methods: {
    doQuery() {
      this.pageData.pageNow = 1
      const params = {
        workspaceId: this.$route.query.workspaceId,
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getEcReleaseStrategyList`, params, 'get').then((res) => {
        this.pageData.totol = res.total
        this.list = (res.strategyList || []).filter(item => {
          let resItem = true
          if (this.searchBar.status) {
            resItem = item.status == this.searchBar.status
          }
          if (this.searchBar.crossCluster) {
            resItem = resItem && this.searchBar.crossCluster === String(!!item.crossCluster);
          }
          if (this.searchBar.yarnQueue && this.searchBar.yarnQueue.trim()) {
            resItem = resItem && this.searchBar.yarnQueue.trim() === item.queue
          }
          if (this.searchBar.name && this.searchBar.name.trim()) {
            resItem = resItem && new RegExp(this.searchBar.name.trim(), 'i').test(item.name)
          }
          return resItem
        })
      })
    },
    handleCrossCluster(crossCluster, type, queue) {
      if (type === 'search') {
        this.searchBar.yarnQueue = '';
        this.searchQueues = [];
        this.doQuery();
      } else {
        this.formData.queue = '';
        this.queues = [];
      }
      if (!crossCluster) return;
      const params = {
        workspaceId: this.$route.query.workspaceId,
        crossCluster
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getQueueList`, params, 'get').then((res) => {
        if(type === 'search') {
          this.searchQueues = res.queueList || [];
        } else {
          this.queues = res.queueList || [];
          if (queue && !this.queues.includes(queue)) {
            this.queues.push(queue)
          }
        }
      })
    },
    handleQueueFocus(val) {
      if (!val) {
        this.$Message.warning(this.$t("message.enginelist.ruleform.plsselectCross"));
      }
    },
    add() {
      this.isEdit = false
      this.getUserList()
      this.queues = []
      this.formData = {
        strategyId: '',
        name: '',
        description: '',
        crossCluster: '',
        triggerConditionConf: {
          relation: "or",
          conditions: [{
            field: 'cpu',
            operation: '>=',
            value: null
          }, {
            field: 'memory',
            operation: '>=',
            value: null
          }]
        },
        terminateConditionConf: {
          relation: "or",
          conditions: [{
            field: 'cpu',
            operation: '<=',
            value: null
          },{
            field: 'memory',
            operation: '<=',
            value: null
          }]
        },
        queue: '',
        imsConf: {
          enable: false,
          duration: null,
          level: null,
          receiver: null
        }
      }
      this.conditionRule = {
        cpu: true,
        memory: false
      }
      this.showModal = true
      this.$refs.ruleForm.resetFields();
      setTimeout(() => {
        this.ruleItemValid = ''
      }, 20);
    },
    saveRule() {
      let url = `${this.$API_PATH.WORKSPACE_PATH}saveEcReleaseStrategy`
      const params = {
        strategyId: this.formData.strategyId,
        name: this.formData.name,
        description: this.formData.description,
        crossCluster: this.formData.crossCluster === 'true' ? true : false,
        triggerConditionConf: {
          relation: this.formData.triggerConditionConf.relation,
          conditions: [...this.formData.triggerConditionConf.conditions]
        },
        terminateConditionConf: {
          relation: this.formData.terminateConditionConf.relation,
          conditions: [...this.formData.terminateConditionConf.conditions]
        },
        queue: this.formData.queue,
        imsConf: {
          ...this.formData.imsConf
        }
      }
      if (!this.conditionRule.cpu) {
        params.triggerConditionConf.conditions.shift()
        params.terminateConditionConf.conditions.shift()
      }
      if (!this.conditionRule.memory) {
        params.triggerConditionConf.conditions.pop()
        params.terminateConditionConf.conditions.pop()
      }
      if (!this.isEdit) {
        delete params.strategyId
      }
      this.$refs.ruleForm.validate(valid => {
        if (valid && !this.ruleItemValid) {
          if (this.loading) return
          this.loading = true
          if (!params.imsConf.enable) {
            params.imsConf.duration = null
            params.imsConf.level = null
            params.imsConf.receiver = null
          }
          api.fetch(url, params, 'put').then(() => {
            this.doQuery();
            this.showModal = false;
            this.$Message.success(this.$t("message.common.saveSuccess"))
          }).finally(() => {
            this.loading = false;
          })
        }
      })
    },
    handleCancel() {
      this.$refs.ruleForm.resetFields();
      this.showModal = false;
    },
    getUserList() {
      if (this.$route.query.workspaceId && this.userlist.length < 1) {
        GetWorkspaceUserManagement({
          workspaceId: this.$route.query.workspaceId
        }).then((res) => {
          this.userlist = res.workspaceUsers;
        })
      }
    },
    deleteItem(item) {
      if (item.status) {
        return this.$Message.warning(this.$t("message.enginelist.disableReq"))
      }
      this.confirmAction({
        title: this.$t("message.enginelist.delRule"),
        content: this.$t("message.enginelist.confirmDel", { data: item.name }),
        ok: () => {
          api.fetch(`${this.$API_PATH.WORKSPACE_PATH}ecReleaseStrategy/${item.strategyId}`, {
          }, 'post').then(() => {
            this.doQuery();
            this.$Message.success(this.$t("message.enginelist.deletesuccess"))
          })
        }
      })
    },
    edit(item) {
      item = JSON.parse(JSON.stringify(item))
      item.crossCluster = String(!!item.crossCluster);
      this.getUserList()
      this.handleCrossCluster(item.crossCluster, 'edit', item.queue);
      if (item.status) {
        return this.$Message.warning(this.$t("message.enginelist.disableReq"))
      }
      this.isEdit = true;
      this.ruleItemValid = '';
      const data = {
        strategyId: item.strategyId,
        name: item.name,
        description: item.description,
        crossCluster: item.crossCluster,
        triggerConditionConf: {
          relation: item.triggerConditionConf.relation,
          conditions: [...item.triggerConditionConf.conditions]
        },
        terminateConditionConf: {
          relation: item.terminateConditionConf.relation,
          conditions: [...item.terminateConditionConf.conditions]
        },
        queue: item.queue,
        imsConf: {
          ...item.imsConf
        }
      }
      const hasCpu = data.terminateConditionConf.conditions.find(it => it.field === 'cpu')
      const hasMem = data.terminateConditionConf.conditions.find(it => it.field === 'memory')
      if (!hasCpu) {
        data.triggerConditionConf.conditions.unshift({
          field: 'cpu',
          operation: '>=',
          value: null
        })
        data.terminateConditionConf.conditions.unshift({
          field: 'cpu',
          operation: '<=',
          value: null
        })
      }
      if (!hasMem) {
        data.triggerConditionConf.conditions.push({
          field: 'memory',
          operation: '>=',
          value: null
        })
        data.terminateConditionConf.conditions.push({
          field: 'memory',
          operation: '<=',
          value: null
        })
      }
      this.conditionRule.cpu = hasCpu != undefined;
      this.conditionRule.memory = hasMem != undefined;
      this.showModal = true;
      if (data.imsConf && data.imsConf.enable) {
        data.imsConf.duration = data.imsConf.duration + ''
      }
      this.$nextTick(() => {
        this.formData = data;
      })
    },
    enable(item) {
      this.confirmAction({
        title: !item.status ? this.$t("message.enginelist.enableRule") : this.$t("message.enginelist.disableRule"),
        content: !item.status ? this.$t("message.enginelist.confirmEnable", { data: item.name }) : this.$t("message.enginelist.confirmDisable", { data: item.name }),
        ok: () => {
          if (this.loading) return
          this.loading = true
          api.fetch(`${this.$API_PATH.WORKSPACE_PATH}changeEcReleaseStrategyStatus`, {
            strategyId: item.strategyId,
            action: item.status ? 'turnDown' : 'turnOn'
          }, 'post').then(() => {
            this.doQuery();
            item.status ? this.$Message.success(this.$t("message.enginelist.disableSucc")) : this.$Message.success(this.$t("message.enginelist.enableSucc"))
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    confirmAction(action) {
      this.$Modal.confirm({
        title: action.title,
        content: action.content,
        onOk: () => {
          action.ok()
        }
      });
    },
    formatConditions(conditions) {
      let string = '';
      conditions.forEach(item => {
        string += `${item.field == 'cpu' ? this.$t('message.enginelist.cpu') : this.$t('message.enginelist.memory')} ${item.operation} ${item.value}% `;
      })
      return string;
    },
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
    },
    handlePageChange(page) {
      this.pageData.pageNow = page;
    },
    validInput() {
      this.$refs.ruleForm.validateField('triggerConditionConf')
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.table-searchbar {
  display: flex;
  .btn {
    flex: 1;
    margin: 0 0 0 20px;
    padding: 0;
    ::v-deep .ivu-form-item-content {
      display: flex;
      justify-content: space-between;
    }
  }
}
.title {
  font-weight: 600;
  padding: 10px 0;
}
.delete-icon {
  margin: 0 20px;
}
.addBtn,
.delete-icon {
  display: inline-block;
  color: $primary-color;
}
.row-title {
  display: flex;
  justify-content: space-between;
  padding-bottom: 8px;
  .btn-text {
    color: $primary-color;
    padding: 0 5px;
    cursor: pointer;
  }
  .status {
    color: $warning-color;
    padding-left: 5px;
    &.enable {
      color: $primary-color;
    }
  }
  .del {
    color: $error-color;
  }
}
.card-item {
  padding: 15px;
  @include border-color(#eee, #37383a);
  @include guide-box-shadow(#eee, #37383a);
  margin-bottom: 20px;
}
.row-item {
  width: 20%;
  padding: 2px 0;
}

.inline-block {
  display: inline-block;
}
.item-error-tip {
  color: $error-color;
  line-height: 16px;
  margin-left: 80px;
}
.engine_kill_rule_form {
  max-height: 475px;
  overflow: auto;
  ::v-deep .ivu-form-item {
    margin-bottom: 18px;
  }
}
.pagebar {
  text-align: center;
  margin-top: 20px;
}
.condition-row {
  margin-left: 110px;
  @include bg-color(#eee, #000a17);
  padding: 20px;
  margin-bottom: 10px;
}
.rule-field {
  display: flex;
  margin: 8px 0;
  align-items: center;
}
.no-data {
  text-align: center;
  padding-top: 50px;
}
</style>
