<template>
  <div class="layout-footer">
    <div class="footer-btn footer-doc" @click="toggleGuide">
      <SvgIcon icon-class="question" />
    </div>
    <Guide :show="guideShow" @on-toggle="toggleGuide" />

    <resource-simple
      ref="resourceSimple"
      @update-job="updateJob">
    </resource-simple>
    <div class="footer-btn footer-channel" :title="msg" @click.prevent.stop="toast">
      <SvgIcon class="footer-channel-job" icon-class="job" />
      <span class="footer-channel-job-num">{{ num }}</span>
    </div>
  </div>
</template>
<script>
import resourceSimpleModule from '@/dss/module/resourceSimple';
import Guide from './guide.vue'
import api from '@/common/service/api';
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
    };
  },
  created() {
    // 让其它接口请求保持在getBasicInfo接口后面请求
    setTimeout(() => {
      this.getRunningJob();
    }, 500);
  },
  watch: {
    '$route'() {
      // this.resetChannelPosition();
    }
  },
  methods: {
    getRunningJob() {
      api.fetch('/jobhistory/list', {
        pageSize: 100,
        status: 'Running,Inited,Scheduled',
      }, 'get').then((rst) => {
        // 剔除requestApplicationName为 "nodeexecution" 的task
        let tasks = rst.tasks.filter(item => item.requestApplicationName !== "nodeexecution")
        this.num = tasks.length;
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
      // 取消拖动后自动点击事件
      if (this.isMouseMove) {
        return;
      }
      this.$refs.resourceSimple.open();
    },
    toggleGuide() {
      this.guideShow = !this.guideShow;
    }
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
