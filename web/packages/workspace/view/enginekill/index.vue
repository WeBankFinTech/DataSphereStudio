<template>
  <div style="height: 100%;  padding: 10px 15px;">
    <div class="title">{{ $t('message.enginelist.ruleset')}}</div>
    <Form class="table-searchbar" ref="searchBar" :model="searchBar" inline label-position="left">
      <FormItem prop="name" :label="$t('message.enginelist.rulename')" :label-width="80">
        <Input v-model="searchBar.name" />
      </FormItem>
      <FormItem prop="yarnQueue" :label="$t('message.enginelist.queue')" :label-width="50">
        <Input v-model="searchBar.yarnQueue" />
      </FormItem>
      <FormItem prop="status" :label="$t('message.enginelist.status')" :label-width="50">
        <Select v-model="searchBar.status" multiple style="min-width:120px;">
          <Option v-for="(item) in statusList" :label="item.label" :value="item.value" :key="item.value" />
        </Select>
      </FormItem>
      <FormItem class="btn">
        <Button type="primary" @click="doQuery">{{$t('message.enginelist.find')}}</Button>
        <Button type="success" @click="add">{{$t('message.enginelist.add')}}</Button>
      </FormItem>
    </Form>
    <div class="card-list-wrapper">
      <div class="card-item" v-for="item in showList" :key="item.name">
        <div class="row-title">
          <div> {{item.name}}
            <span class="status" :class="{enable:item.status}" >
              [{{item.status ? $t('message.enginelist.enable') : $t('message.enginelist.disable')}}]
            </span></div>
          <div>
            <span class="btn-text" @click="edit(item)">{{$t('message.enginelist.edit')}}</span>
            <span class="btn-text" @click="enable(item)">{{ item.enable ? $t('message.enginelist.disable') : $t('message.enginelist.enable')}}</span>
            <span class="btn-text del" @click="deleteItem(item)">{{$t('message.enginelist.delete')}}</span>
          </div>
        </div>
        <div class="row-item">
          {{ $t('message.enginelist.ruleform.queue')}}：{{ item.queue }}
        </div>
        <div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.trigger')}}：{{ formatConditions(item.triggerConditionConf.conditions) }}
          </div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.relation')}}：
            {{ item.triggerConditionConf.relation == 'and' ? $t('message.enginelist.ruleform.and') : $t('message.enginelist.ruleform.or') }}
          </div>
        </div>
        <div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.stop')}}：{{ formatConditions(item.terminateConditionConf.conditions) }}
          </div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.relation')}}：{{ item.triggerConditionConf.relation == 'and' ? $t('message.enginelist.ruleform.and') : $t('message.enginelist.ruleform.or') }}
          </div>
        </div>
        <div class="row-item inline-block">
          {{ $t('message.enginelist.ruleform.alertuser')}}：{{ item.imsConf.receiver && item.imsConf.receiver.join('') }}
        </div>
        <div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.modifyUser')}}：{{ item.modifier }}
          </div>
          <div class="row-item inline-block">
            {{ $t('message.enginelist.ruleform.modifyTime')}}：{{ item.modifiyTime | formatDate('YYYY-MM-DD HH:mm:ss') }}
          </div>
        </div>
        <div class="row-item inline-block">
          {{ $t('message.enginelist.ruleform.createUser')}}：{{ item.ceator }}
        </div>
        <div class="row-item inline-block">
          {{ $t('message.enginelist.ruleform.createTime')}}：{{ item.createTime | formatDate('YYYY-MM-DD HH:mm:ss') }}
        </div>
      </div>
      <div v-if="showList.length<1" class="no-data">{{ $t('message.common.No') }}</div>
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
      :title="isEdit ? $t('message.enginelist.ruleform.edittitle') : $t('message.enginelist.ruleform.addtitle')"
    >
      <Form
        ref="ruleForm"
        label-position="left"
        class="engine_kill_rule_form"
        :label-width="80"
        :model="formData"
        :rules="formValid">
        <FormItem
          :label="$t('message.enginelist.rulename')"
          prop="name">
          <Input
            v-model="formData.name"
            :placeholder="$t('message.enginelist.ruleform.enterName')"
          />
        </FormItem>
        <FormItem
          :label="$t('message.enginelist.ruleform.description')"
          prop="description">
          <Input
            v-model="formData.description"
            type="textarea"
          />
        </FormItem>
        <FormItem
          :label="$t('message.enginelist.ruleform.queue')"
          prop="queue">
          <Select
            v-model="formData.queue"
            :placeholder="$t('message.enginelist.ruleform.selectqueue')">
            <Option
              v-for="(item) in queues"
              :label="item"
              :value="item"
              :key="item"/>
          </Select>
        </FormItem>
        <!-- 触发条件 -->
        <FormItem
          :label="$t('message.enginelist.ruleform.trigger')"
          prop="triggerConditionConf">
          <span style="margin-right:10px">{{$t('message.enginelist.ruleform.relation')}}</span>
          <RadioGroup v-model="formData.triggerConditionConf.relation">
            <Radio label="and">{{$t('message.enginelist.ruleform.and')}}</Radio>
            <Radio label="or">{{$t('message.enginelist.ruleform.or')}}</Radio>
          </RadioGroup>
          <div v-for="(ruleIt,idx) in formData.triggerConditionConf.conditions" :key="idx" style="display:flex;margin:5px 0;">
            <Select
              v-model="formData.triggerConditionConf.conditions[idx].field"
              :placeholder="$t('message.enginelist.ruleform.selectqueue')">
              <Option
                v-for="(item, index) in types"
                :label="item.label"
                :value="item.value"
                :key="index"/>
            </Select>
            <span style="margin:0 20px">>=</span>
            <InputNumber style="width:120px; margin-right:5px" placeholder="1-100" type="number"
              :max="100" :min="1" v-model="formData.triggerConditionConf.conditions[idx].value" /> %
            <div class="delete-icon" @click="delRuleItem('triggerConditionConf', idx)"><SvgIcon icon-class="delete"/></div>
          </div>
          <div v-if="ruleItemValid.triggerConditionConf" class="item-error-tip">{{ruleItemValid.triggerConditionConf}}</div>
          <div v-if="formData.triggerConditionConf.conditions.length < 2" class="addBtn" @click="addTrigger">
            {{$t('message.enginelist.ruleform.addruleit')}}
          </div>
        </FormItem>
        <!-- 终止条件 -->
        <FormItem
          :label="$t('message.enginelist.ruleform.stop')"
          prop="terminateConditionConf">
          <span style="margin-right:10px">{{$t('message.enginelist.ruleform.relation')}}</span>
          <RadioGroup v-model="formData.terminateConditionConf.relation">
            <Radio label="and">{{$t('message.enginelist.ruleform.and')}}</Radio>
            <Radio label="or">{{$t('message.enginelist.ruleform.or')}}</Radio>
          </RadioGroup>
          <div v-for="(ruleIt,idx) in formData.terminateConditionConf.conditions" :key="idx" style="display:flex;margin:5px 0;">
            <Select
              v-model="formData.terminateConditionConf.conditions[idx].field"
              :placeholder="$t('message.enginelist.ruleform.selectqueue')">
              <Option
                v-for="(item, index) in types"
                :label="item.label"
                :value="item.value"
                :key="index"/>
            </Select>
            <span style="margin:0 20px"><=</span>
            <InputNumber style="width:120px; margin-right:5px" placeholder="1-100" type="number"
              :max="100" :min="1" v-model="formData.terminateConditionConf.conditions[idx].value" /> %
            <div class="delete-icon" @click="delRuleItem('terminateConditionConf', idx)"><SvgIcon icon-class="delete"/></div>
          </div>
          <div v-if="ruleItemValid.terminateConditionConf" class="item-error-tip">{{ruleItemValid.terminateConditionConf}}</div>
          <div v-if="formData.terminateConditionConf.conditions.length < 2" class="addBtn" @click="addStop">
            {{$t('message.enginelist.ruleform.addruleit')}}
          </div>
        </FormItem>
        <FormItem
          :label="$t('message.enginelist.ruleform.alertset')"
          prop="imsConf.enable">
          <Checkbox v-model="formData.imsConf.enable">{{$t('message.enginelist.ruleform.alertoption')}}</Checkbox>
        </FormItem>
        <FormItem
          v-show="formData.imsConf.enable"
          :label="$t('message.enginelist.ruleform.level')"
          prop="imsConf.level">
          <Select
            v-model="formData.imsConf.level">
            <Option
              v-for="(item) in levels"
              :label="item"
              :value="item"
              :key="item"/>
          </Select>
        </FormItem>
        <FormItem
          v-show="formData.imsConf.enable"
          :label="$t('message.enginelist.ruleform.duration')"
          prop="imsConf.duration">
          <Select
            v-model="formData.imsConf.duration">
            <Option
              v-for="(item) in durations"
              :value="item.value"
              :key="item.value">
              {{ item.label }}
            </Option>
          </Select>
        </FormItem>
        <FormItem
          v-show="formData.imsConf.enable"
          :label="$t('message.enginelist.ruleform.alertuser')"
          prop="imsConf.receiver">
          <Select
            v-model="formData.imsConf.receiver"
            multiple
            :placeholder="$t('message.enginelist.ruleform.selectalertuser')">
            <Option
              v-for="(item) in userlist"
              :value="item.name"
              :key="item.name">
              {{ item.name }}
            </Option>
          </Select>
        </FormItem>
      </Form>
      <template slot="footer">
        <Button @click="handleCancel">{{$t('message.common.cancel')}}</Button>
        <Button type="primary" @click="saveRule">{{$t('message.common.ok')}}</Button>
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
        yarnQueue: ''
      },
      showModal: false,
      list: [],
      formData: {
        strategyId: '',
        name: '',
        description: '',
        triggerConditionConf: {
          relation: "or",
          conditions: [{
            field: 'cpu',
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
      levels: ['MINOR','WARNING'],
      durations: [
        {value: '1', label: "1min"},
        {value: '5', label: "5min"},
        {value: '10', label: "10min"},
        {value: '30', label: "30min"}
      ],
      userlist: [],
      statusList: [{
        label: this.$t('message.enginelist.enable'),
        value: '1'
      }, {
        label: this.$t('message.enginelist.disable'),
        value: '0'
      }],
      types: [{
        label: this.$t('message.enginelist.cpu'),
        value: 'cpu'
      }, {
        label: this.$t('message.enginelist.memory'),
        value: 'memory'
      }],
      isEdit: false,
      ruleItemValid: {
        triggerConditionConf: '',
        terminateConditionConf: ''
      },
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
        let valid = true;
        let repeat = false;
        if (value.conditions) {
          if (value.conditions[0]) {
            if (!value.conditions[0].field || value.conditions[0].value > 100 || value.conditions[0].value < 1 || value.conditions[0].value % 1 !== 0) {
              valid = false
            }
          }
          if (value.conditions[1]) {
            if(!value.conditions[1].field || value.conditions[1].value > 100 || value.conditions[1].value < 1 || value.conditions[1].value % 1 !== 0){
              valid = false
            }
          }
          if (value.conditions.length > 1 && value.conditions[0].field == value.conditions[1].field) {
            repeat = true
          }
        } else {
          valid = false
        }
        let triggerStop = this.formData.terminateConditionConf.conditions.length === this.formData.triggerConditionConf.conditions.length
        this.formData.triggerConditionConf.conditions.forEach(item => {
          const sItm = this.formData.terminateConditionConf.conditions.find(it => it.field === item.field)
          if (!sItm || sItm.value >= item.value) {
            triggerStop = false
          }
        });
        if (repeat) {
          this.ruleItemValid[rule.field] = this.$t("message.enginelist.ruleform.repeat")
        } else {
          this.ruleItemValid[rule.field] = valid ? triggerStop ? '' : this.$t("message.enginelist.ruleform.ruleIttype") : this.$t("message.enginelist.ruleform.ruleItvalue")
        }
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
            validator: validateTrigger,
            trigger: "blur",
          },
        ],
        terminateConditionConf: [
          {
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
        this.$refs.ruleForm.validateField('terminateConditionConf')
      },
      deep: true
    },
    'formData.terminateConditionConf.conditions': {
      handler() {
        this.$refs.ruleForm.validateField('triggerConditionConf')
        this.$refs.ruleForm.validateField('terminateConditionConf')
      },
      deep: true
    }
  },
  filters,
  mounted() {
    // this.doQuery()
    // this.getQueueList()
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
          if (this.searchBar.yarnQueue.trim()) {
            resItem = resItem && this.searchBar.yarnQueue.trim() === item.queue
          }
          if (this.searchBar.name.trim()) {
            resItem = resItem && item.name.indexOf(this.searchBar.name.trim()) > -1
          }
          return resItem
        })
      })
    },
    getQueueList() {
      const params = {
        workspaceId: this.$route.query.workspaceId,
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getQueueList`, params, 'get').then((res) => {
        this.queues = res.queueList || []
      })
    },
    add() {
      // this.getUserList()
      // this.showModal = true
    },
    saveRule() {
      let url = `${this.$API_PATH.WORKSPACE_PATH}saveEcReleaseStrategy`
      const params = { ...this.formData }
      if (!this.isEdit) {
        delete params.strategyId
      }
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          if (this.loading) return
          this.loading = true
          api.fetch(url, params, 'put').then(() => {
            this.doQuery();
            this.$Message.success(this.$t("message.common.Success"))
          }).finally(()=>{
            this.loading = false
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
    addTrigger() {
      this.formData.triggerConditionConf.conditions.push({
        field: '',
        operation: '>=',
        value: null
      })
    },
    addStop() {
      this.formData.terminateConditionConf.conditions.push({
        field: '',
        operation: '<=',
        value: null
      })
    },
    delRuleItem(type, index) {
      this.formData[type].conditions.splice(index,1);
    },
    deleteItem(item) {
      if (item.status) {
        return this.$t("message.enginelist.disableReq")
      }
      this.confirmAction({
        title: this.$t("message.enginelist.delRule"),
        content: this.$t("message.enginelist.confirmDel", {data: item.name}),
        ok: () => {
          api.fetch(`${this.$API_PATH.WORKSPACE_PATH}ecReleaseStrategy/${item.strategyId}`, {
          }, 'delete').then(() => {
            this.doQuery();
            this.$Message.success(this.$t("message.enginelist.deletesuccess"))
          })
        }
      })
    },
    edit(item) {
      this.getUserList()
      if (item.status) {
        return this.$t("message.enginelist.disableReq")
      }
      this.isEdit = true;
      this.showModal = true;
      this.formData = {...item}
    },
    enable(item) {
      this.confirmAction({
        title: !item.status ? this.$t("message.enginelist.enableRule") : this.$t("message.enginelist.disableRule"),
        content: !item.status ? this.$t("message.enginelist.confirmEnable" , {data: item.name}) : this.$t("message.enginelist.confirmDisable", {data: item.name}),
        ok: () => {
          if (this.loading) return
          this.loading = true
          api.fetch(`${this.$API_PATH.WORKSPACE_PATH}changeEcReleaseStrategyStatus`, {
            strategyId: item.strategyId,
            action: item.status ? 'turnDown' : 'turnOn '
          }, 'post').then(() => {
            this.doQuery();
            this.$Message.success(this.$t("message.common.Success"))
          }).finally(()=>{
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
        string += `${item.field == 'cpu' ? this.$t('message.enginelist.cpu') : this.$t('message.enginelist.memory') } ${item.operation} ${item.value}`;
      })
      return string;
    },
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
    },
    handlePageChange(page) {
      this.pageData.pageNow = page;
    },
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
.delete-icon{
  margin: 0 20px
}
.addBtn,.delete-icon {
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
  box-shadow: 0 0 3px 2px #eee;
  border: 1px solid #eee;
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
  margin-top: 20px
}
.no-data {
  text-align: center;
  padding-top: 50px;
}
</style>
