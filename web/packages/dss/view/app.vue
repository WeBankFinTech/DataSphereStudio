<template>
  <div class="layout">
    <router-view/>
    <water-mark
      v-if="watermark.show"
      :text="waterMarkText"
      ref="watermask"></water-mark>
  </div>
</template>
<script>
import elementResizeEvent from '@dataspherestudio/shared/common/helper/elementResizeEvent';
import WaterMark from '@dataspherestudio/shared/components/watermark';
import storage from '@dataspherestudio/shared/common/helper/storage';
import moment from 'moment'

export default {
  name: 'App',
  components: {
    WaterMark
  },
  data() {
    return {
      watermark: {},
      waterMarkText: ''
    }
  },
  mounted() {
    this.watermark = this.$APP_CONF.watermark || { template: '', show: false }
    this.getMaskText()
  },
  methods: {
    getUserName() {
      return storage.get("baseInfo", "local")
        ? storage.get("baseInfo", "local").username
        : null;
    },
    getMaskText() {
      const obj = {
        username: this.getUserName(),
        time: moment(new Date()).format("YYYY-MM-DD HH:mm")
      }
      this.waterMarkText = this.watermark.template.replace(/\$\{([^}]*)}/g, function (a, b) {
        return obj[b]
      })
      if (this.watermark.timeupdate) {
        clearTimeout(this.timer)
        this.timer = setTimeout(() => {
          this.getMaskText()
        }, this.watermark.timeupdate)
      }
    }
  },
  beforeDestroy() {
    elementResizeEvent.unbind(this.$el);
  },
};
</script>
<style src="@/dss/assets/styles/app.scss" lang="scss"></style>
