<template>
  <div class="menuSiderWrap">
    <span @click="toggleCollapse" class="triggerIcon" :style="triggerStyle">
      <i v-if="value" class="ivu-icon ivu-icon-ios-arrow-forward"></i>
      <i v-else class="ivu-icon ivu-icon-ios-arrow-back"></i>
    </span>
    <div :style="siderStyle">
      <slot></slot>
    </div>
  </div>
</template>
<script>

export default {
  name: 'MenuSider',
  props: {
    value: {  // if it's collpased now
      type: Boolean,
      default: false
    },
    width: {
      type: [Number, String],
      default: 200
    },
    collapsedWidth: {
      type: [Number, String],
      default: 0
    }
  },
  data () {
    return {
      
    };
  },
  computed: {
    siderStyle(){
      return {
        transition: 'all .2s ease-in-out',
        overflow: 'hidden',
        width: (this.value ? this.collapsedWidth : this.width) + 'px'
      } 
    },
    triggerStyle() {
      return {
        right: (this.value ? -18: 0) + 'px'
      }
    }
  },
  methods: {
    toggleCollapse(){
      let value = !this.value;
      this.$emit('input', value);
    }
  },
  watch: {
    value (stat) {
      this.$emit('on-collapse', stat);
    }
  },
  mounted () {
  },
  beforeDestroy () {
  }
};
</script>
<style scoped lang="scss">
    .menuSiderWrap {
        position: relative;
    }
    .triggerIcon {
        position: absolute;
        top: 20px;
        font-size: 14px;
        background: #333c;
        color: #fff;
        line-height: 16px;
        padding: 2px;
        border-radius: 2px;
        cursor: pointer
    }
</style>