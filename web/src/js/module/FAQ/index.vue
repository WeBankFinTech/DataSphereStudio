<template>
  <div>
    <iframe
      id="iframeName"
      :src="visualSrc"
      frameborder="0"
      width="100%"
      :height="height"
      v-if="isSkip"/>
    <div
      style="display: flex; justify-content: center; align-items: center;"
      :style="{'height': height + 'px'}"
      v-else>
      <span>{{ info }}</span>
    </div>
  </div>
</template>
<script>
import module from './index';
export default {
  mixins: [module.mixin],
  data() {
    return {
      height: 0,
      visualSrc: null,
      isSkip: false,
      info: '请在跳转页面查看……',
    };
  },
  mounted() {
    this.height = this.$route.query.height;
    this.init();
  },
  methods: {
    init() {
      if (this.$route.query.isSkip) {
        const errCode = this.$route.query.errCode;
        const addr = module.data.ENVIR === 'dev' ? 'test.com' : window.location.host;
        this.visualSrc = `https://${addr}/dss/help/errorcode/${errCode}.html`;
        this.isSkip = true;
      } else {
        this.isSkip = false;
        this.info = '请在跳转页面查看……';
        this.linkTo();
      }
      if (!this.height) {
        this.height = window.innerHeight - 230;
      }
    },
    linkTo() {
      const newTab = window.open('about:blank');
      setTimeout(() => {
        newTab.location.href = this.getFAQUrl();
      }, 500);
    },
  },
};
</script>
