<template>
  <div class="workflow-arguments">
    <div class="workflow-arguments-setting">
      <div class="workflow-arguments-title-wrap">
        <h4 class="workflow-arguments-title">{{$t('message.workflow.process.arguments.workFlowConfig')}}</h4>
      </div>
      <Form
        ref="scheduleParams"
        :label-width="75"
        :model="scheduleParams"
        :rules="rules">
        <FormItem
          :label="$t('message.workflow.process.arguments.proxyuser')"
          prop="proxyuser"
          :rules="[{ pattern: /^[a-zA-Z0-9_]+$/, message: this.$t('message.workflow.process.arguments.valid'), trigger: 'change' }]">
          <Input
            v-model="scheduleParams.proxyuser"
            type="text"
            :placeholder="$t('message.workflow.process.arguments.inputProxyuser')"
            @on-change="onProxyUserChange"></Input>
        </FormItem>
        <!--
        <h4 class="time-set-title">调度时间</h4>
        <FormItem
          label="执行时间设置: 每"
          :label-width="105">
          <Select v-if="oneSelect === 'MIN'" class="margin-right" style="width:80px" v-model="zeroSelect">
            <Option v-for="item in zeroList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
          <Select
            style="width:80px"
            class="margin-right"
            v-model="oneSelect"
            @on-change="selectOneChange">
            <Option
              v-for="item in oneList"
              :value="item.value"
              :key="item.value">{{ item.label }}</Option>
          </Select>
          <Select class="margin-right" style="width:80px" v-if="towList.length > 0" v-model="towSelect">
            <Option v-for="item in towList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
          <Select class="margin-right" style="width:80px"  v-if="threeList.length > 0" v-model="threeSelect">
            <Option v-for="item in threeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
          <Select class="margin-right" style="width:80px" v-if="selectHourAndMinues" v-model="fourSelect">
            <Option v-for="item in fourList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
          <Select class="margin-right" style="width:80px" v-if="selectHourAndMinues" v-model="fiveSelect">
            <Option v-for="item in fiveList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
        </FormItem>
        <h4 class="time-set-title">失败告警</h4>
        <FormItem
          label="告警用户:"
          :label-width="75"
          prop="alarmUserEmails"
          :rules="[{ pattern: /^[a-zA-Z0-9_.@;]+$/, message: '仅支持字母、数字、下划线、分号、点、@', trigger: 'change' }]"
        >
          <Input
            v-model="scheduleParams.alarmUserEmails"
            placeholder="请输入邮箱或用户名"
            @on-change="alarmUserChange"></Input>
        </FormItem>
        <FormItem
          label="告警级别:"
          :label-width="75"
        >
          <Select
            class="margin-right"
            v-model="scheduleParams.alarmlevel"
            @on-change="selectLevelChange">
            <Option
              v-for="item in levelList"
              :value="item"
              :key="item">{{ item }}</Option>
          </Select>
        </FormItem>

        -->
      </Form>
    </div>
    <dynamic-form
      ref="dynamicForm"
      v-show="!isDispatch"
      :title="$t('message.workflow.process.arguments.globVar')"
      @change="onVariableChange"></dynamic-form>
  </div>
</template>
<script>
import { isEmpty, keys, debounce } from 'lodash';
import util from '@/common/util';
import dynamicForm from '@/components/dynamicForm';
import timeToCronMixin from "@/common/service/timeToCronMixin.js";

export default {
  components: {
    dynamicForm,
  },
  mixins: [timeToCronMixin],
  props: {
    props: {
      type: Array,
      default: () => [],
    },
    isDispatch: {
      type: Boolean,
      default: () => false
    },
    scheduleParamsProp: {}
  },
  data() {
    return {
      scheduleParams: {
        alarmlevel: '',
        alarmUserEmails: '',
        scheduletime: '',
        proxyuser: ''
      },
      rules: {
        proxyuser: [{ pattern: /^[a-zA-Z0-9_@]+$/, message: this.$t('message.workflow.process.arguments.valid'), trigger: 'change' }],
      },
      levelList: [
        'INFO',
        'MINOR',
        'MAJOR',
        'CRITICAL',
      ]
    };
  },
  watch: {
    props: {
      handler() {
        // if (isEmpty(oldVal)) {
        this.setData();
        // }
      },
      deep: true
    },
    scheduleParamsProp() {
      this.setDispatch();
    }
  },
  methods: {
    setDispatch() {
      this.scheduleParams = this.scheduleParamsProp;
      this.scheduleParams.proxyuser = this.props[0] ? this.props[0]['user.to.proxy'] : '';
      this.cronToTime();
    },
    setData() {
      if (!isEmpty(this.props)) {
        this.scheduleParams.proxyuser = this.props[0]['user.to.proxy'];
        const list = this.props.filter((item) => keys(item)[0] !== 'user.to.proxy');
        if (!isEmpty(list)) {
          this.$refs.dynamicForm.formDynamic.list = list.map((item) => {
            const key = Object.keys(item)[0];
            return {
              key,
              value: item[key],
            };
          });
        }
      } else {
        this.scheduleParams.proxyuser = '';
      }
    },
    onProxyUserChange() {
      // 需要校验名字合法才能提交
      if (/^[a-zA-Z0-9_]+$/.test(this.scheduleParams.proxyuser) || this.scheduleParams.proxyuser === '') {
        this.emitChange(this, this.$refs.dynamicForm.formDynamic.list);
      }
    },
    onVariableChange(value) {
      this.emitChange(this, value);
    },
    emitChange: debounce(function(that, value) {
      const variable = util.convertArrayToMap(value);
      const props = [{
        'user.to.proxy': that.scheduleParams.proxyuser,
      }].concat(variable);
      that.$emit('change-props', props);
    }, 300),
  },
};
</script>
<style lang="scss" src="../index.scss">

</style>
