<template>
  <div class="iframeClass">
    <iframe class="iframeClass" id="iframe" :src="visualSrc" frameborder="0" width="100%" height="100%" />
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util/index';

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
    query: {
      type: Object
    },
    url: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      height: 1200,
      visualSrc: '',
    };
  },
  mounted() {
    if (this.node) {
      this.node.jobContent = this.node.jobContent || {}
      this.getUrl();
    }
    if (this.url) {
      this.visualSrc = this.url
    }
    window.addEventListener('message', this.msgEvent, false)
  },
  methods: {
    getUrl() {
      const params = {
        nodeType: this.node.type,
        projectID: +this.$route.query.projectID,
        flowId: this.query.appId,
        params: {
          ...this.node.jobContent,
          title: this.node.title,
          desc: this.node.desc
        },
        labels: {
          route: this.getCurrentDsslabels()
        }
      }
      api.fetch('dss/workflow/getAppConnNodeUrl', params, 'post').then((res) => {
        console.log(res, 'url')
        const url = util.replaceHolder(res.jumpUrl, {
          ...this.node.jobContent,
          nodeId: this.node.key,
          nodeName: this.node.title,
          contextID: this.node.contextID
        });
        this.visualSrc = url;
      })
    },
    msgEvent(e) {
      console.log('message: ', e)
      if(e.data) {
        try {
          let data = typeof e.data === 'string' ? JSON.parse(e.data) : e.data || {}
          if (data.type === 'qualitis' && data.nodeId === this.node.key) {
            if(data.data.action === 'add') {
              this.node.jobContent.ruleGroupId = data.data.ruleGroupId
            } else if(data.data.action === 'delete') {
              this.node.jobContent.ruleGroupId = ''
            }
          }
          this.$emit('save', {}, {...this.node}, false)
        } catch(err) {
          console.error(err)
        }
      }
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
