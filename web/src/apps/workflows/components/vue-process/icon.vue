<template>
  <svg class="icon" aria-hidden="true" @click="click">
    <use :xlink:href="type" />
  </svg>
</template>
<script>
export default {
  props: {
    name: {
      type: String,
      required: true
    }
  },
  computed: {
    type() {
      return this.name ? `#icon-${this.name}` : null
    }
  },
  methods: {
    click(e) {
      this.$emit('click', e)
    }
  }
}
</script>
<style>
.icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
</style>
