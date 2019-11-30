<template>
  <div class="iframeClass">
    <iframe class="iframeClass" id="iframe" :src="visualSrc" frameborder="0" width="100%" height="100%" />
  </div>
</template>
<script>
import api from '@/js/service/api';
import util from '../../../util/index';

export default {
  props: {
    parametes: {
      type: [String, Object],
      default: null
    },
    node: {
      type: [String, Object],
      default: null
    },
  },
  data() {
    return {
      height: 1200,
      visualSrc: '',
    };
  },
  mounted() {
    this.node.jobContent = this.node.jobContent || {}
    this.getUrl();
    window.addEventListener('message', this.msgEvent, false)
  },
  methods: {
    getUrl() {
      // 节点url由后台返回，只用定义变量,和后台同步命名
      const projectId = this.$route.query.projectID;
      const nodeId = this.node.jobContent && this.node.jobContent.id;
      api.fetch('dss/getAppJointProjectID', {
        projectID: projectId,
        nodeType: this.node.nodeType
      }, 'get').then(res => {
        const url = util.replaceHolder(this.node.jumpUrl, {
          ...this.node.jobContent,
          nodeId: nodeId || this.node.key,
          projectId: res.appJointProjectID,
          nodeName: this.node.title
        });
        this.visualSrc = url;
      })

    },
    msgEvent(e) {
      if(e.data) {
        let data = JSON.parse(e.data)
        if (data.type === 'qualitis' && data.nodeId === this.node.key) {
          if(data.data.action === 'add') {
            this.node.jobContent.ruleGroupId = data.data.ruleGroupId
          } else if(data.data.action === 'delete') {
            this.node.jobContent.ruleGroupId = ''
          }
        }
      }
      this.$emit('save', {}, {...this.node})
    },
  },
  beforeDestroy() {
    window.removeEventListener('message', this.msgEvent, false)
  }
};

</script>
<style lang="scss" scoped>
  .iframeClass {
    position: relative;
    height: 100%;
    width: 100%;
  }

</style>
