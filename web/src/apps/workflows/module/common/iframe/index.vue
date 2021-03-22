<template>
  <div class="iframeClass">
    <iframe class="iframeClass" id="iframe" :src="visualSrc" frameborder="0" width="100%" height="100%" />
  </div>
</template>
<script>
import api from '@/common/service/api';
import util from '@/common/util/index';

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
    // 获取需要在创建的时候填写的参数
    getCreatePrams(node) {
      const createParams = {}
      node.nodeUiVOS.filter((item) => !item.nodeMenuType)
        .map(item => item.key).map(item => {
          createParams[item] = node[item];
        })
      return createParams;
    },
    getUrl() {
      const createParams = this.getCreatePrams(this.node);
      const params = {
        nodeType: this.node.type,
        projectID: +this.$route.query.projectID,
        params: {
          ...this.node.jobContent,
          ...createParams
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
          // projectId: res.appJointProjectID,
          nodeName: this.node.title,
          contextID: this.node.contextID
        });
        this.visualSrc = url;
      })
    },
    msgEvent(e) {
      if(e.data) {
        try {
          let data = JSON.parse(e.data)
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
