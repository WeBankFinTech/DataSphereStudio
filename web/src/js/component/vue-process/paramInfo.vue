<template>
  <div class="right-draw paraminfo">
    <div class="right-draw-title">
      {{ t('vue-process.parameter') }}
      <svg class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9075" @click="add">
        <path d="M510.352119 912.416237c-220.548741 0-399.339141-178.82643-399.339141-399.403615S289.803378 113.622281 510.352119 113.622281c220.550637 0 399.341037 178.813156 399.341037 399.390341S730.902756 912.416237 510.352119 912.416237zM510.352119 163.544178c-192.982281 0-349.421037 156.461511-349.421037 349.468444S317.369837 862.482963 510.352119 862.482963s349.421037-156.463407 349.421037-349.470341S703.3344 163.544178 510.352119 163.544178zM660.104533 537.979259 535.313067 537.979259l0 124.81043c0 13.778489-11.174874 24.957156-24.959052 24.957156-13.784178 0-24.957156-11.178667-24.957156-24.957156l0-124.81043-124.795259 0c-13.784178 0-24.959052-11.17677-24.959052-24.966637 0-13.780385 11.17677-24.957156 24.959052-24.957156l124.795259 0 0-124.816119c0-13.778489 11.174874-24.951467 24.957156-24.951467 13.786074 0 24.959052 11.171081 24.959052 24.951467l0 124.816119 124.791467 0c13.786074 0 24.959052 11.17677 24.959052 24.957156C685.063585 526.802489 673.890607 537.979259 660.104533 537.979259z" fill="#333333" p-id="9076" />
      </svg>
    </div>
    <div class="right-draw-content">
      <div class="row">
        <label class="half">
          {{ t('vue-process.attribute') }}
        </label>
        <label class="half">
          {{ t('vue-process.value') }}
        </label>
      </div>
      <div v-for="(item, index) in arr" :key="index" class="row">
        <label class="half">
          <Icon name="shanchu" @click="del(index)" />
          <input v-model="item.key" :placeholder="t('vue-process.attribute name')">
        </label>
        <label class="half">
          <input v-model="item.value" :placeholder="t('vue-process.property value')">
        </label>
      </div>
      <div class="row">
        <button class="designer-button row-button" @click="save">
          {{ t('vue-process.save') }}
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
          lastUpdateTime: Date.now(),
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
