<template>
  <div class="layout">
    <router-view/>
    <div v-if="watermark.show" id="watermark"></div>
  </div>
</template>
<script>
import elementResizeEvent from '@dataspherestudio/shared/common/helper/elementResizeEvent';
import WaterMark from '@dataspherestudio/shared/components/watermark/watermark';
import storage from '@dataspherestudio/shared/common/helper/storage';
import moment from 'moment'

export default {
  name: 'App',
  data() {
    return {
      watermark: {}
    }
  },
  mounted() {
    this.watermark = this.$APP_CONF.watermark || { template: '', show: false }
    if (this.watermark.show) {
      this.resizeWaterMark();
      elementResizeEvent.bind(this.$el, this.resizeWaterMark);
    }
  },
  methods: {
    resizeWaterMark() {
      new WaterMark(this.$el.querySelector('#watermark'), {
        text: this.getMaskText,
        fontSize: 14,
        color: localStorage.getItem('theme')==='dark' ? '#eee' : '#000',
        timeupdate: this.watermark.timeupdate
      })
    },
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
      return this.watermark.template.replace(/\$\{([^}]*)}/g, function (a, b) {
        return obj[b]
      })
    },
  },
  beforeDestroy() {
    elementResizeEvent.unbind(this.$el);
  },
};
</script>
<style src="@/dss/assets/styles/app.scss" lang="scss"></style>
