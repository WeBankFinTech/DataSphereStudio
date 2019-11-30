<template>
  <div class="right-draw baseinfo">
    <div class="right-draw-title">
      {{ t('vue-process.Basic') }}
    </div>
    <div class="right-draw-content">
      <div class="row">
        <label class="row-name">
          {{ t('vue-process.node name') }}
        </label>
        <input v-model="title" class="row-input" :placeholder="t('vue-process.enter node name')">
      </div>
      <div class="row">
        <label class="row-name">
          {{ t('vue-process.describe') }}
        </label>
        <textarea v-model="desc" class="row-input" :placeholder="t('vue-process.enter-desc')" />
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
import { findComponentUpward } from './util.js'
import { commit, mixin } from './store';
export default {
  mixins: [mixin],
  props: {
    node: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      title: this.node.title,
      desc: this.node.desc
    }
  },
  mounted() {
    this.designer = findComponentUpward(this, 'Designer')
  },
  methods: {
    save() {
      commit(this.$store, 'UPDATE_NODE', {
        key: this.node.key,
        obj: Object.assign(this.node, {
          lastUpdateTime: Date.now(),
          title: this.title,
          desc: this.desc
        })
      });
      if (this.designer) {
        this.designer.$emit('node-baseInfo', this.node)
        this.designer.closeBaseInfo()
      }
    }
  }
}
</script>
