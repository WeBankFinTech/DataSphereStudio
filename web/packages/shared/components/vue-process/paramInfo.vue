<template>
  <div class="right-draw paraminfo">
    <div class="right-draw-title">
      {{ $t('message.workflow.vueProcess.parameter') }}
      <SvgIcon class="icon" icon-class="add" color="#333333"/>
    </div>
    <div class="right-draw-content">
      <div class="row">
        <label class="half">
          {{ $t('message.workflow.vueProcess.attribute') }}
        </label>
        <label class="half">
          {{ $t('message.workflow.vueProcess.value') }}
        </label>
      </div>
      <div v-for="(item, index) in arr" :key="index" class="row">
        <label class="half">
          <Icon name="shanchu" @click="del(index)" />
          <input v-model="item.key" :placeholder="$t('message.workflow.vueProcess.attributeName')">
        </label>
        <label class="half">
          <input v-model="item.value" :placeholder="$t('message.workflow.vueProcess.propertyValue')">
        </label>
      </div>
      <div class="row">
        <button class="designer-button row-button" @click="save">
          {{ $t('message.workflow.vueProcess.save') }}
        </button>
      </div>
    </div>
  </div>
</template>
<script>
import Icon from './icon.vue'
import { findComponentUpward } from './util.js'
import { commit, mixin } from './store';
export default {
  components: {
    Icon
  },
  mixins: [mixin],
  props: {
    node: {
      type: Object,
      required: true
    }
  },
  data() {
    let params = this.node.params;
    let arr = [];
    for (let p in params) {
      arr.push({
        key: p,
        value: params[p]
      })
    }
    if (arr.length === 0) {
      arr.push({
        key: '',
        value: ''
      })
    }
    return {
      arr
    }
  },
  mounted() {
    this.designer = findComponentUpward(this, 'Designer')
  },
  methods: {
    save() {
      let params = {};
      this.arr.forEach(item => {
        params[item.key] = item.value
      })
      commit(this.$store, 'UPDATE_NODE', {
        key: this.node.key,
        obj: Object.assign(this.node, {
          modifyTime: Date.now(),
          params
        })
      });
      if (this.designer) {
        this.designer.$emit('node-param', this.node)
        this.designer.closeParam()
      }
    },
    add() {
      this.arr.push({
        key: '',
        value: ''
      })
    },
    del(index) {
      if (this.arr.length <= 1) {
        return;
      }
      this.arr.splice(index, 1)
    }
  }
}
</script>
