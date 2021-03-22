<template>
  <div style="height: 100%">
    <iframe v-if="flag" ref="iframe" class="web-iframe" :src="url" />
  </div>
</template>

<script>
export default {
  props: {
    url: {
      type: String,
      default: ''
    },
    cookie: {
      type: String,
      default: ''
    },
    // height: {
    //     type: String,
    //     default: '800px'
    // }
  },
  data() {
    return {
      flag: true,
    }
  },
  watch: {
    url() { // 通过传入的url达到自动刷新的效果
      this.flag = false
      this.$nextTick(() => {
        this.flag = true
        setTimeout(() => {
          if (this.$refs.iframe) {
            this.$refs.iframe.contentWindow.location.reload(true)
          }

        }, 100)
      })
    }
  },
  FesData: {}
}

</script>

<style scoped lang="scss">
.web-iframe {
  border: none;
  width: 100%;
  height: 100%;
}
</style>
