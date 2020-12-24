<template>
  <div class="workflow-arguments">
    <div class="workflow-arguments-setting">
      <div class="workflow-arguments-title-wrap">
        <h4 class="workflow-arguments-title">{{$t('message.process.arguments.workFlowConfig')}}</h4>
      </div>
      <Form
        ref="settingForm"
        :label-width="75"
        :model="settingForm"
        :rules="rules">
        <FormItem
          :label="$t('message.process.arguments.proxyuser')"
          prop="proxyuser">
          <Input
            v-model="settingForm['proxyuser']"
            type="text"
            :placeholder="$t('message.process.arguments.inputProxyuser')"
            @on-change="onProxyUserChange"></Input>
        </FormItem>
      </Form>
    </div>
    <dynamic-form
      ref="dynamicForm"
      :title="$t('message.process.arguments.globVar')"
      @change="onVariableChange"></dynamic-form>
  </div>
</template>
<script>
import { isEmpty, keys, debounce } from 'lodash';
import util from '@/js/util';
import dynamicForm from '@/js/component/dynamicForm';
export default {
  components: {
    dynamicForm,
  },
  props: {
    props: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      settingForm: {
        proxyuser: '',
      },
      rules: {
        proxyuser: [{ pattern: /^[a-zA-Z0-9_]+$/, message: this.$t('message.process.arguments.valid'), trigger: 'change' }],
      },
    };
  },
  watch: {
    props(val, oldVal) {
      if (isEmpty(oldVal)) {
        this.setData();
      }
    },
  },
  mounted() {
    // Airflow DAG pre-set params
    if (this.$use_airflow == 1) {
      this.$refs.dynamicForm.formDynamic.list = [
        {
          key: 'dag.start.time', value: '2020-09-15 00:00:00', 
        },
        {
          key: 'dag.end.time', value: '2120-09-15 00:00:00', 
        },
        {
          key: 'dag.schedule.interval', value: 'hourly',
        }
      ];
    }
  },
  methods: {
    setData() {
      if (!isEmpty(this.props)) {
        this.settingForm.proxyuser = this.props[0]['user.to.proxy'];
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
        this.settingForm.proxyuser = '';
        // Airflow DAG pre-set params
        if (this.$use_airflow == 1) {
          this.$refs.dynamicForm.formDynamic.list = [
            {
              key: 'dag.start.time', value: '2020-09-15 00:00:00', 
            },
            {
              key: 'dag.end.time', value: '2120-09-15 00:00:00', 
            },
            {
              key: 'dag.schedule.interval', value: 'hourly',
            }
          ];
        }
      }
    },
    onProxyUserChange() {
      this.emitChange(this, this.$refs.dynamicForm.formDynamic.list);
    },
    onVariableChange(value) {
      this.emitChange(this, value);
    },
    emitChange: debounce(function(that, value) {
      const variable = util.convertArrayToMap(value);
      const props = [{
        'user.to.proxy': that.settingForm.proxyuser,
      }].concat(variable);
      that.$emit('change-props', props);
    }, 300),
  },
};
</script>
<style lang="scss" src="../index.scss">

</style>
