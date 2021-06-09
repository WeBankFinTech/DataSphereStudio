<template>
  <div class="input-number-model">
    <template>
      <Button type="primary" size="small" @click="onReduce()" :disabled="(value < (min + 1))"><span>-</span></Button>
      <Input v-model="value" placeholder=" " @blur="onBlur" />
      <Button type="primary" size="small" @click="onIncrease()" :disabled="(value > (max - 1))"><span>+</span></Button>
    </template>
  </div>
</template>
<script>
export default {
  name: 'input-number',
  data () {
    return {
      value: 1,
      isIncrease: false,
      isReduce: false
    }
  },
  props: {
    min: {
      type: Number,
      default: 0
    },
    max: {
      type: Number,
      default: 10
    },
    propsValue: Number
  },
  methods: {
    onBlur () {
      let $reg = /^\+?[1-9][0-9]*$/　　// eslint-disable-line
      let $val = this.value
      // Verify integer
      if (!$reg.test($val)) {
        this.value = this.min
      }
      // Maximum value
      if (this.value > this.max) {
        this.value = this.max
      }
      // minimum value
      if (this.min > this.value) {
        this.value = this.min
      }
      this.$emit('on-number', this.value)
    },
    onIncrease () {
      this.value = parseInt(this.value) + 1
      this.$emit('on-number', this.value)
    },
    onReduce () {
      this.value = parseInt(this.value) - 1
      this.$emit('on-number', this.value)
    }
  },
  watch: {
  },
  beforeCreate () {
  },
  created () {
    this.value = this.propsValue ? this.propsValue : this.min
  },
  beforeMount () {
  },
  mounted () {
  },
  beforeUpdate () {
  },
  updated () {
  },
  beforeDestroy () {
  },
  destroyed () {
  },
  computed: {},
  components: {}
}
</script>

<style lang="scss" rel="stylesheet/scss">
  .input-number-model {
    display: inline-block;
    button{
      padding: 6px 10px;
      position: relative;
      .bt-text {
        font-size: 18px;
        color: #888;
      }
    }
    .ans-input {
      width: 80px;
      margin:0 -2px 0 -1px;
      input {
        text-align: center;
      }
    }
    button,input{
      vertical-align: middle;
    }
  }
</style>
