<template>
  <div class="iframeClass">
    <iframe
      ref="ifr"
      class="iframeClass"
      v-if="isRefresh"
      id="iframe"
      :src="visualSrc"
      frameborder="0"
      width="100%"
      :height="height"/>
    <Spin v-if="loading" fix>{{ $t('message.common.Loading') }}</Spin>
  </div>
</template>
<script>
import util from '@dataspherestudio/shared/common/util/index';
export default {
  data() {
    return {
      height: 0,
      visualSrc: '',
      isRefresh: true,
      loading: true
    };
  },
  watch: {
    async '$route.query.projectID'() {
      this.getUrl();
      this.reload()
    },
    async '$route.query.type'() {
      this.getUrl();
      this.reload()
    },
    async '$route.query.url'() {
      this.getUrl();
      this.reload()
    }
  },
  mounted() {
    const ifr = this.$refs.ifr;
    if (ifr) {
      ifr.onload = () => {
        this.loading = false
      }
    }
    this.getUrl();
    // 创建的时候设置宽高
    this.resize(window.innerHeight);
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.resize(window.innerHeight));
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize(window.innerHeight));
  },
  methods: {
    resize(height) {
      this.height = height;
    },
    getUrl() {
      const {url,__noreplace} = this.$route.query;
      if(__noreplace) {
        this.visualSrc = url
      } else {
        this.visualSrc = util.replaceHolder(url, {
          projectId: this.$route.query.projectID,
          projectName: this.$route.query.projectName,
          workspaceId: this.$route.query.workspaceId,
        });
      }
    },
    reload() {
      this.isRefresh = false;
      this.$nextTick(() => this.isRefresh = true);
    }
  },
};
</script>
<style>
.iframeClass{
    height: 100%;
}
</style>
