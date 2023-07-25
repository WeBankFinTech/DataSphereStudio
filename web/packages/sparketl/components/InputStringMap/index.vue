<template>
  <Input
    v-model="inputValue"
    type="textarea"
    :placeholder="'key1=value1\nkey2=value2\n...'"
    :rows="4"
    @on-blur="onValueBlur"
  ></Input>
</template>

<script>
import { toJson } from '../../utils/index'
export default {
  name: 'InputStringMap',
  props: {
    value: { type: Object, isRequired: true, default: () => ({}) },
    separator: { type: String, isRequired: false, default: '\n' },
  },
  data() {
    const arr = []
    let val = this.value || {}
    for (let key of Object.keys(val)) {
      arr.push(key + '=' + val[key])
    }
    const inputValue = arr.join(this.separator)
    return {
      inputValue,
    }
  },
  methods: {
    onValueBlur(e) {
      const newValue = toJson(e.target.value, this.separator)
      this.$emit('input', newValue)
    },
  },
}
</script>

<style scoped></style>
