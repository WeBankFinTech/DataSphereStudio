<template>
  <commonIframe
    :url="url"
  ></commonIframe>
</template>
<script>
import commonIframe from '@/dss/view/commonIframe'

export default {
  computed: {
    url() {
      return `/next-web/#/workspace/resourceTmlManage?workspaceId=${this.$route.query.workspaceId}`
    }
  },
  components: {
    commonIframe,
  }
}
</script>
