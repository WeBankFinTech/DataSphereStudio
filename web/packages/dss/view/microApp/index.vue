<template>
  <div class="iframeClass">
    <iframe
      class="iframeClass"
      v-if="isRefresh"
      id="mircoApp"
      :src="visualSrc"
      frameborder="0"
      width="100%"
      sandbox="allow-forms allow-modals allow-popups allow-same-origin allow-scripts allow-top-navigation"
      :height="height">
    </iframe>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util/index';
import mixin from '@dataspherestudio/shared/common/service/mixin';

export default {
  data() {
    return {
      height: 0,
      visualSrc: '',
      isRefresh: true
    };
  },
  mixins: [mixin],
  mounted() {
    // 创建的时候设置宽高
    this.resize(window.innerHeight);
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.resize(window.innerHeight));

    this.initMicroApp();
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize(window.innerHeight));
  },
  watch: {
    '$route.params.appName'() {
      // 切换应用, 重新获取数据
      this.initMicroApp();
      this.reload();
    },
  },
  methods: {
    resize(height) {
      this.height = height;
    },
    reload() {
      this.isRefresh = false;
      this.$nextTick(() => this.isRefresh = true);
    },
    generateUrlTail(){
      const tail = [];
      tail.push('app_id=facade-framework');
      tail.push('timestamp=1659924920342');
      tail.push('nonce=12345');
      tail.push('user=dss');
      tail.push('signature=eca1a93c2c2bb8fc55972d76d0c1267c7782f51552a5d4562a2d871a63f64168');
      return tail.join('&');
    },
    humpToLine(str){
      return str.replace(/([A-Z])/g, '_$1').toLowerCase().slice(1);
    },
    async initMicroApp() {
      console.log(process.env.VUE_APP_MICRO_PREFIX)
      try {
        // 查询绑定信息
        const data = await api.fetch(`/mfgov/fesdk/bindQuery/v1?${this.generateUrlTail()}`, {
          main_mf_name: 'dss',
          sub_mf_name: this.humpToLine(this.$route.params.appName),
        }, {
          method: 'get',
          baseURL: process.env.VUE_APP_MICRO_PREFIX,
          headers: {
            proxyUser: this.getUserName(),
          }
        });
        this.visualSrc = util.replaceHolder(`${data.accessLocation}?baseUrl=${data.accessLocation.split('#')[0].replace(/\/$/, '')}` || '');
      } catch (err) {
        console.warn('-------', err);
      }
    }
  },
};
</script>
<style>
  .iframeClass {
    height: 100%;
  }
</style>
