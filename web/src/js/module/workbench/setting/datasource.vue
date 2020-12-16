<template>
  <div class="dynamic-form">
    <div class="dynamic-form-header-wrap">
      <h4 class="dynamic-form-header-title">{{$t('message.workBench.body.setting.datasource.title')}}</h4>
    </div>
    <Form
      ref="datasource"
      :model="script.params.configuration.runtime.datasource">
      <FormItem
        :label="$t('message.workBench.body.setting.datasource.datasourceSelect')">
        <Select v-model="script.params.configuration.runtime.datasource.datasourceId" style="width:200px" clearable>
          <Option v-for="item in datasourceList" :value="item.value" :key="item.value">{{ item.label }}</Option>
        </Select>
      </FormItem>
    </Form>
  </div>
</template>

<script>
import api from '@/js/service/api';
import { isEqual } from 'lodash';
export default {
  name: "datasource",
  props: {
    script: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      datasourceList: [

      ]
    }
  },
  created() {
    if (this.script.params.configuration.runtime.datasource && this.script.params.configuration.runtime.datasource.datasourceId) {
      this.script.params.configuration.runtime.datasource.datasourceId = parseInt(this.script.params.configuration.runtime.datasource.datasourceId)
    } else {
      this.script.params.configuration['runtime']['datasource'] = {
        datasourceId: null
      }
    }
  },
  mounted() {

    this.init();
  },
  methods: {
    init() {
      let _this = this;
      api.fetch('/dsm/info', {
        "system": "BDP"
      }, 'get').then((rst) => {
        if (rst.query_list) {
          rst.query_list.forEach(function (item) {
            _this.datasourceList.push({
              label: item.dataSourceName,
              value: item.id
            })
          })
        }
      }).catch((err) => {
      });
    }
  }
}
</script>

<style scoped>

</style>
