<template>
  <div class="layout">
    <router-view/>
    <water-mark
      v-if="watermark.show"
      :text="waterMarkText"
      ref="watermask"></water-mark>
    <Modal v-model="update.show" width="480" :closable="false">
      <template #header style="background:transparent">
        <div class="ivu-modal-confirm-head-icon ivu-modal-confirm-head-icon-info"><i class="ivu-icon ivu-icon-ios-information-circle"></i></div>
        <span style="padding-left:10px">{{ $t('message.common.updatetitle') }}</span>
      </template>
      <p>{{ $t('message.common.updateTip') }}</p>
      <pre style="background-color:transparent;border:none;">{{ update.versiontext }}</pre>
      <template #footer>
        <Button @click="handleCancel">{{ $t('message.common.viewchange') }}</Button>
        <Button type="primary" @click="handleOk">{{ $t('message.common.updatetitle') }}</Button>
      </template>
    </Modal>
  </div>
</template>
<script>
import plugin from '@dataspherestudio/shared/common/util/plugin';
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
      waterMarkText: '',
      update: {
        show: false,
        versiontext: ''
      }
    }
  },
  mounted() {
    this.watermark = this.$APP_CONF.watermark || { template: '', show: false }
    this.getMaskText()
    plugin.on('show_app_update_notice', (version) => {
      this.update = {
        show: true,
        versiontext: version
      }
    })
  },
  methods: {
    getUserName() {
      return storage.get("baseInfo", "local")
        ? storage.get("baseInfo", "local").username
        : null;
    },
    getMaskText() {
      if ( !this.watermark.show ) return
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
    },
    handleOk() {

    },
    handleCancel() {
      this.update.show = false
    }
  },
  beforeDestroy() {
  },
};
</script>
<style src="@/dss/assets/styles/app.scss" lang="scss"></style>
