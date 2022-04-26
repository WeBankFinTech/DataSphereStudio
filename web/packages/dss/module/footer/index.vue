<template>
  <div class="layout-footer">
    <template v-if="!min">
      <div class="footer-btn footer-doc" @click="toggleGuide">
        <SvgIcon icon-class="question" />
      </div>
      <Guide :show="guideShow" @on-toggle="toggleGuide" ref="Guide"/>
      <resource-simple
        ref="resourceSimple"
        :dispatch="dispatch"
        @update-job="updateJob">
      </resource-simple>
      <div class="footer-btn footer-channel"
        :title="msg"
        @click.prevent.stop="toast">
        <SvgIcon class="footer-channel-job" icon-class="job" />
        <span class="footer-channel-job-num">{{ num }}</span>
      </div>
      <Icon type="md-arrow-dropdown" class="min_arrow" @click="min=true" />
    </template>
    <Icon v-else type="md-arrow-dropup"  class="show_arrow" @click="min=false" />
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
      min: false
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
      this.resetChannelPosition()
    }
  },
  methods: {
    getRunningJob() {
      api.fetch('/jobhistory/list', {
        pageSize: 100,
        status: 'Running,Inited,Scheduled',
      }, 'get').then((rst) => {
        // 剔除requestApplicationName为 "nodeexecution" 的task
        let tasks = rst.tasks.filter(item => item.requestApplicationName !== "nodeexecution" && item.requestApplicationName !== "CLI")
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
      this.$refs.resourceSimple.open();
    },
    toggleGuide() {
      this.guideShow = !this.guideShow;
    },
    resetChannelPosition() {
      this.min = false
    }
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
