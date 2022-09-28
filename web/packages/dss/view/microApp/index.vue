<template>
  <div class="iframeClass">
    <iframe
      class="iframeClass"
      v-if="visualSrc"
      id="mircoApp"
      :src="visualSrc"
      frameborder="0"
      width="100%"
      sandbox="allow-same-origin allow-scripts allow-top-navigation"
      :height="height">
    </iframe>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util/index';

export default {
  data() {
    return {
      height: 0,
      visualSrc: '',
      isRefresh: true
    };
  },
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
  methods: {
    resize(height) {
      this.height = height;
    },
    generateUrlTail(){
      const tail = [];
      tail.push('app_id=facade-framework');
      tail.push('timestamp=1659924920342');
      tail.push('nonce=12345');
      tail.push('user=lucaszhu');
      tail.push('signature=eca1a93c2c2bb8fc55972d76d0c1267c7782f51552a5d4562a2d871a63f64168');
      return tail.join('&');
    },
    async initMicroApp() {
      try {
        // 注册微应用信息
        // await api.fetch(`/mfgov/fesdk/register/v1?${this.generateUrlTail()}`, {
        //   // 微前端（唯一）名称
        //   mf_name: 'data_lineage',
        //   mf_func_desc: 'DATA LINEAGE',
        //   // 微前端静态资源访问相对路径
        //   mf_res_entrance_location: '/data_lineage',
        //   // 微前端静态资源版本
        //   mf_latest_ver: '1.0.0',
        //   mf_ref_backend_apis: '/data_lineage',
        // }, {
        //   transformRequest: [
        //     function (data) {
        //       let ret = ''
        //       for (let it in data) {
        //         ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
        //       }
        //       ret = ret.substring(0, ret.lastIndexOf('&'));
        //       return ret
        //     }
        //   ],
        //   headers: {
        //     'Content-Type': 'application/x-www-form-urlencoded',
        //   },
        //   baseURL: 'http://127.0.0.1:9090',
        // });

        // 查询绑定信息
        const data = await api.fetch(`/mfgov/fesdk/bindQuery/v1?${this.generateUrlTail()}`, {
          main_mf_name: 'dss',
          sub_mf_name: 'data_lineage',
        }, {
          method: 'get',
          baseURL: 'http://127.0.0.1:9090',
        });
        this.visualSrc = util.replaceHolder(`${data.accessLocation}?baseUrl=${data.accessLocation.split('#')[0]}` || '');
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
