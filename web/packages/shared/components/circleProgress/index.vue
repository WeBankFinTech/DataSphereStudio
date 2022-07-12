<template>
  <div
    class="circle-progress"
    :style="{'width': width, 'height': height}">
    <svg viewBox="0 0 104 104">
      <path
        d="M 52 52 m 0 -47 a 47 47 0 1 1 0 94 a 47 47 0 1 1 0 -94"
        class="circle-progress__track">
      </path>
      <path
        d="M 52 52 m 0 -47 a 47 47 0 1 1 0 94 a 47 47 0 1 1 0 -94"
        class="circle-progress__path"
        :style="{
          'stroke-dashoffset': ((1-percent) < 0 ? 0 : (1-percent))*299.08+'px',
          'stroke': color
        }">
      </path>
    </svg>
    <span class="circle-progress__perent">{{ formattedPercent }}%</span>
    <span class="circle-progress__text" v-if="max">{{ used }}{{ suffixe }}/{{ max }}{{ suffixe }}</span>
    <span class="circle-progress__text" v-else-if="title && titlein">{{ title }}</span>
    <span class="circle-progress__title" v-if="!titlein">{{ title }}</span>
  </div>
</template>
<script>
export default {
  props: {
    percent: Number,
    used: String,
    max: String,
    suffixe: String,
    title: String,
    titlein: {
      type: Boolean,
      default: false
    },
    color: {
      type: String,
      default: 'orange'
    },
    width: String,
    height: String,
    fixed: {
      type: Number,
      default: 0
    }
  },
  computed: {
    formattedPercent() {
      return (this.percent * 100).toFixed(this.fixed);
    },
  },
};
</script>
<style lang="scss" src="./index.scss">

</style>
