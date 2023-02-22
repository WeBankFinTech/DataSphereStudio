<template>
  <div class="layout-footer" @mousemove="onMouseMove" :style="{'pointer-events': `${(isMouseDown || guideShow) ? 'initial' : 'none'}`}">
    <div ref="footerChannel" class="tool-btns" :class="{min:min}" @mousedown.prevent.stop="onMouseDown">
      <template v-if="!min">
        <div class="footer-btn footer-doc" @click="toggleGuide">
          <SvgIcon icon-class="question" />
        </div>
        <resource-simple
          ref="resourceSimple"
          :dispatch="dispatch"
          @update-job="updateJob">
        </resource-simple>
        <div class="footer-btn footer-channel"
          :title="msg"
          @click="toast">
          <SvgIcon class="footer-channel-job" icon-class="job" />
          <span class="footer-channel-job-num">{{ num }}</span>
        </div>
        <Icon type="md-arrow-dropdown" class="min_arrow" @click="min=true" />
      </template>
      <Icon v-else type="md-arrow-dropup"  class="show_arrow" @click="min=false" />
    </div>
    <Guide :show="guideShow" @on-toggle="toggleGuide" ref="Guide"/>
  </div>
</template>
<script>
import resourceSimpleModule from '@dataspherestudio/shared/components/resourceSimple';
import Guide from './guide.vue'
import api from '@dataspherestudio/shared/common/service/api';
export default {
  components: {
    resourceSimple: resourceSimpleModule.component,
    Guide,
  },
  data() {
    return {
      num: 0,
      msg: '',
      guideShow: false,
      min: false,
      isMouseDown: false,
      isMouseMove: false
    };
  },
  created() {
    // 让其它接口请求保持在getBasicInfo接口后面请求
    setTimeout(() => {
      this.getRunningJob();
    }, 500);
  },
  mounted() {
    this.positionInfo = { x: 0, y: 0}
    document.onmouseup = () => {
      this.isMouseDown = false;
    }
    window.addEventListener('resize', this.resetChannelPosition)
  },
  watch: {
    '$route'() {
      this.resetChannelPosition()
    }
  },
  methods: {
    getRunningJob() {
      // 换成展示引擎数
      api.fetch('/linkisManager/rm/engines').then((rst) => {
        if (rst.engines) {
          this.num = rst.engines.length;
        }
      });
    },
    'Footer:updateRunningJob'(num) {
      this.num = num;
    },
    'Footer:getRunningJob'(cb) {
      cb(this.num);
    },
    updateJob(num) {
      const method = 'Footer:updateRunningJob';
      this[method](num);
    },
    toast() {
      if (!this.isMouseMove) {
        this.$refs.resourceSimple.open();
      }
    },
    toggleGuide() {
      if (!this.isMouseMove) {
        if (this.$APP_CONF.faq_link) {
          window.open("/_book/", '_blank');
          return
        }
        this.guideShow = !this.guideShow;
      }
    },
    onMouseDown(e) {
      const footerChannel = this.$refs.footerChannel;
      this.positionInfo = {
        x: e.clientX - footerChannel.offsetLeft,
        y: e.clientY - footerChannel.offsetTop
      }
      this.isMouseMove = false;
      this.isMouseDown = true;
    },
    onMouseMove(e) {
      const footerChannel = this.$refs.footerChannel;
      if (!this.isMouseDown) return
      let x = e.clientX - this.positionInfo.x
      let y = e.clientY - this.positionInfo.y
      if (x > document.documentElement.clientWidth - 40) {
        x =  document.documentElement.clientWidth - 40
      }
      if (y > document.documentElement.clientHeight - 160) {
        y =  document.documentElement.clientHeight - 160
      }
      if (x < 20) {
        x = 20
      }
      if (y < 20) {
        y = 20
      }
      footerChannel.style.left = x + 'px';
      footerChannel.style.top = y + 'px';
      if (Math.abs(e.movementX) > 10 || Math.abs(e.movementY) > 10) {
        this.isMouseMove = true;
      }
    },
    resetChannelPosition() {
      this.min = false
      const footerChannel = this.$refs.footerChannel;
      if (footerChannel) {
        footerChannel.style.left = document.documentElement.clientWidth - 80 + 'px';
        footerChannel.style.top = document.documentElement.clientHeight - 180 + 'px';
      }
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resetChannelPosition)
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
