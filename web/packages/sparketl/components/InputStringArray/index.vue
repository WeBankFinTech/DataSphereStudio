<!-- eslint-disable vue/no-v-model-argument -->
<template>
  <Input
    v-model:value="inputValue"
    type="textarea"
    placeholder="value1,value2,value3,..."
    :rows="4"
    @on-blur="onValueBlur"
  ></Input>
</template>

<script>
export default {
  name: "InputStringArray",
  props: {
    value: { type: Array, isRequired: true },
    separator: { type: String, isRequired: false, default: "," },
  },
  data() {
    return {
      inputValue: (this.value || []).join(","),
    };
  },
  methods: {
    onValueBlur(e) {
      const newValue = e.target.value
        .split(this.separator)
        .map((item) => item.trim());
      this.$emit("input", newValue);
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped></style>
