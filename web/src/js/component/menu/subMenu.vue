<template>
  <ul 
    v-show="isShow" 
    class="second-menu">
    <slot/>
  </ul>
</template>
<script>
export default {
  name: 'WeSubMenu',
  data() {
    return {
      isShow: false,
    };
  },
  mounted() {
    this.initEvent();
  },
  methods: {
    initEvent() {
      this.$on('we-menu-over', this.handleOver);
      this.$on('we-menu-leave', this.handleOut);
    },
    handleOver(e) {
      this.isShow = true;
    },
    handleOut(e) {
      this.isShow = false;
    },
  },
};
</script>
