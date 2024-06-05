<template>
  <Alert v-if="showNotice && currentNotice" :title="currentNotice" type="warning" class="top-notice-bar" closable @on-close="close">
    <SvgIcon
      icon-class="horn"
    />
    {{ currentNotice }}
  </Alert>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
export default {
  props: {
    apiPath: {
      type: String,
      required: true
    }
  },
  watch: {
    '$route.path'() {
      this.init()
    }
  },
  data() {
    return {
      showNotice: true,
      currentNotice: ''
    }
  },
  created() {
    this.init();
  },
  methods: {
    fetchNoticeList() {
      return api.fetch(this.apiPath, {}, 'get').then((res) => {
        return res.notices || []
      }).catch(() => {
      });
    },
    async init(show = false) {
      const noticeList = await this.fetchNoticeList() || []
      const dateNow = Date.now()
      const needShow = []
      clearTimeout(this.timer)
      const list = noticeList.filter((item) => {
        return item.endTime > dateNow
      })
      let nextStart = 0;
      let nextEnd = 0;
      list.sort((a,b) => {
        return a.startTime - b.startTime
      }).forEach(item => {
        if (item.startTime <= dateNow) {
          needShow.push(item)
        } else if(nextStart) {
          nextStart = Math.min(item.startTime - dateNow, nextStart)
        } else {
          nextStart = item.startTime - dateNow
        }
      });

      list.sort((a,b) => {
        return a.endTime - b.endTime
      }).forEach(item => {
        if (nextEnd) {
          nextEnd = Math.min(item.endTime - dateNow, nextEnd)
        } else {
          nextEnd = item.endTime - dateNow
        }
      });

      // 快到某条公告生效或者失效时间
      const time = Math.min(nextStart, nextEnd) > 0 ? Math.min(nextStart, nextEnd) : Math.max(nextStart, nextEnd)
      if (time > 0 && time < 10 * 60 * 1000) {
        this.timer = setTimeout(() => {
          this.init(true)
        }, time);
      }


      if (needShow.length && (!this.hasClosed || show)) {
        this.updateShow(needShow);
        this.showNotice = true;
      } else {
        this.showNotice = false;
      }
    },
    // 同时生效的公告一分钟间隔轮播
    updateShow(list = [], index = 0) {
      if (list.length === 1) {
        this.currentNotice = list[0].content;
        return
      }
      if (index > list.length - 1) index = 0
      this.currentNotice = list[index++].content;
      clearTimeout(this.updateShowTimer)
      this.updateShowTimer = setTimeout(() => {
        this.updateShow(list, index )
      }, 1000 * 60);
    },
    close() {
      this.showNotice = false;
      clearTimeout(this.updateShowTimer);
      this.hasClosed = true;
    }
  },
  beforeDestroy() {
    clearTimeout(this.timer);
    clearTimeout(this.updateShowTimer);
  },
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.top-notice-bar {
  font-size: 14px;
  color: $warning-color;
  z-index:2;
  position: absolute;
  top: 55px;
  width: 100%;
  text-overflow: ellipsis;
  overflow: hidden;
  border-radius: unset;
  white-space: nowrap;
}
</style>
