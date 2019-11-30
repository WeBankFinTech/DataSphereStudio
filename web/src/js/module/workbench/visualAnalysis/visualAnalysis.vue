<template>
  <div class="visual-analysis">
    <iframe
      id="iframe"
      :src="visualSrc"
      frameborder="0"></iframe>
    <Modal
      v-model="project.show"
      :title="$t('message.workBench.body.visualAnalysis.modalTitle')"
      @on-ok="confirm"
      @on-cancel="cancel">
      <Select
        v-model="project.id">
        <Option
          v-for="item in apps"
          :value="item.id"
          :key="item.id"
          style="width: 200px;">{{ item.name }}</Option>
      </Select>
    </Modal>
  </div>
</template>
<script>
import api from '@/js/service/api';
export default {
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  data() {
    const isWaterMask = this.getWaterMask();
    const username = this.getUserName();
    const dwraisUrl = this.getVsBiUrl();
    let { viewId, projectId, isSaved } = this.work.params;
    const srcPre = `${dwraisUrl}/dss/visualis/#/project/${projectId}/widget`;
    const newSrc = `${srcPre}/add?viewId=${viewId}&isWterMask=${isWaterMask}&username=${username}`;
    const oldSrc = `${srcPre}/${viewId}`;
    let visualSrc = isSaved ? oldSrc : newSrc;
    return {
      srcPre,
      visualSrc,
      project: {
        show: false,
        id: projectId,
        data: {},
      },
      apps: [],
    };
  },
  beforeDestroy() {
    window.removeEventListener('message', this.fn, false);
  },
  mounted() {
    this.hiddenArrow();
    this.fn = (ev) => {
      if (typeof (ev.data) === 'string') {
        let data = JSON.parse(ev.data);
        if (data.type === 'saveWidget') {
          this.queryApplication();
          this.project.show = true;
          delete data.type;
          this.project.data = data;
        }
      }
    };
    window.addEventListener('message', this.fn, false);
  },
  methods: {
    async queryApplication() {
      let data = await api.fetch('/application/list');
      this.apps = data.applications;
    },
    saveWidgetToDws(data) {
      data = Object.assign(data, {
        projectId: this.project.id,
      });
      const vsBiUrl = this.getVsBiUrl();
      api.fetch(`${vsBiUrl}/api/rest_j/v1/visualis/widgets`, data).then(() => {
        // let widgetId = data.widgetId;
        // this.visualSrc = `${this.srcPre}/${viewId}`;
        this.$Message.success(this.$t('message.constants.success.save'));
      });
    },
    confirm() {
      this.saveWidgetToDws(this.project.data);
    },
    cancel() {

    },
    hiddenArrow() {
      let iframe = document.getElementById('iframe');
      let iwindow = iframe.contentWindow;
      iwindow.onload = function() {
        let dom = iwindow.document.querySelector('.anticon.anticon-left');
        dom.style.display = 'none';
      };
    },
  },
};
</script>
<style lang="scss">
.visual-analysis{
    height: 100%;
    iframe {
        width: 100%;
        height: 100%;
    }
}
</style>
