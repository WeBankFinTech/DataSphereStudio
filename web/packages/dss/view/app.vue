<template>
  <div class="layout">
    <router-view/>
    <water-mark
      v-if="watermark.show"
      :text="waterMarkText"
      ref="watermask"></water-mark>
    <Modal v-model="update.show" width="480" :closable="false" :mask-closable="false">
      <template #header style="background:transparent">
        <div class="ivu-modal-confirm-head-icon ivu-modal-confirm-head-icon-info"><i class="ivu-icon ivu-icon-ios-information-circle"></i></div>
        <span style="padding-left:10px">{{ $t('message.common.updatetitle') }}</span>
      </template>
      <p class="update-title">{{ $t('message.common.updateTip') }}</p>
      <div v-for="(item,index) in update.releaseNote" :key="index">
        <p class="item-title">{{ item.title }}</p>
        <p class="release-notes" v-for="(noteItem,idx) in item.contents" :key="idx" @click="goItem(noteItem)">
          <a v-if="noteItem.url">
            {{ noteItem.title }}
          </a>
          <span v-else>
            {{ noteItem.title }}
          </span>
        </p>
      </div>
      <template #footer>
        <Button @click="handleCancel">{{ $t('message.common.viewchange') }}</Button>
        <Button type="primary" @click="handleOk">{{ $t('message.common.updatenow') }}</Button>
      </template>
    </Modal>
  </div>
</template>
<script>
import plugin from '@dataspherestudio/shared/common/util/plugin';
import WaterMark from '@dataspherestudio/shared/components/watermark';
import storage from '@dataspherestudio/shared/common/helper/storage';
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';
import moment from 'moment';
import { currentModules } from '@dataspherestudio/shared/common/util/currentModules.js';
import api from '@dataspherestudio/shared/common/service/api';


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
        releaseNote: []
      }
    }
  },
  mounted() {
    this.getMaskText()
    plugin.on('show_app_update_notice', () => {
      let url = '/dss/framework/workspace/getReleaseNote'
      const m = currentModules();
      if (m.microModule === "scriptis") {
        url =  '/dss/scriptis/getReleaseNote'
      }
      api.fetch(url, {
        workspaceId: this.$route.query.workspaceId,
      }, 'get').then((res) => {
        if (res.releaseNote) {
          this.update = {
            show: true,
            releaseNote: res.releaseNote
          }
        }
        this.releaseNoteUrl = res.releaseNoteUrl
      })
    }),
    eventbus.on('watermark.refresh', () => {
      this.waterMarkText = ''
      setTimeout(() => {
        this.getMaskText()
      },400)
    });
    const user_cache = this.getUserName()
    // 切换浏览器tab，激活时判断登录用户和当前页面用户不一致（在其它tab登录了别的用户），需要刷新页面
    window.addEventListener("visibilitychange", () => {
      const user_cache = this.getUserName()
      const user_change = window.username != user_cache && (window.username || user_cache)
      if (document.hidden == false && user_change) {
        location.href = '/'
      }
    });
    if (user_cache && !window.username) {
      window.username = user_cache
    }
  },
  methods: {
    getUserName() {
      return storage.get("baseInfo", "local")
        ? storage.get("baseInfo", "local").username
        : '';
    },
    getMaskText() {
      this.watermark = this.$APP_CONF.watermark || { template: '', show: false }
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
      location.reload()
    },
    handleCancel() {
      if (this.releaseNoteUrl) window.open(this.releaseNoteUrl, '_blank')
    },
    goItem(item) {
      // url类型: 0-内部系统（直接跳转），1-外部系统（新tab打开）
      if(item.urlType) {
        window.open(item.url, '_blank');
      } else {
        location.href = item.url;
      }
    }
  },
  beforeDestroy() {
  },
};
</script>
<style src="@/dss/assets/styles/app.scss" lang="scss"></style>
