<template>
  <div class="editor-setting">
    <div class="editor-setting-header">
      <span>{{ $t('message.workBench.body.script.setting.header') }}</span>
      <Icon
        type="ios-close"
        @click="$emit('setting-close')"/>
    </div>
    <div class="editor-setting-content">
      <custom-variable
        ref="customVariable"
        :script="script"></custom-variable>
      <!-- <runtime-args
        v-if="work.type==='node'"
        ref="runtimeArgs"
        :script="script"></runtime-args>
      <env-variable
        v-if="work.type==='node'"
        ref="envVariable"
        :script="script"></env-variable> -->
    </div>
  </div>
</template>
<script>
import customVariable from '../setting/customVariable.vue';
// import runtimeArgs from '../setting/runTimeArgs.vue';
// import envVariable from '../setting/envVariable.vue';
import { isEqual } from 'lodash';
export default {
  name: 'Setting',
  components: {
    customVariable,
    // runtimeArgs,
    // envVariable,
  },
  props: {
    work: {
      type: Object,
      required: true,
    },
    script: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      origin: null,
    };
  },
  watch: {
    'script.params': {
      handler: function(val) {
        this.work.unsave = !isEqual(JSON.parse(this.origin), val);
      },
      deep: true,
    },
  },
  mounted() {
    this.origin = JSON.stringify(this.script.params);
  },
};
</script>
