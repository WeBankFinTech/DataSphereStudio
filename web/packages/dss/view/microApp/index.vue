<template>
  <div class="iframeClass">111
    <iframe
      class="iframeClass"
      v-if="visualSrc"
      id="mircoApp"
      :src="visualSrc"
      frameborder="0"
      width="100%"
      :height="height">
    </iframe>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';

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
    async initMicroApp() {
      try {
        // 注册微应用信息
        await api.fetch(`/mfgov/fesdk/register/v1`, {
          // 微前端（唯一）名称
          mf_name: 'lineage',
          mf_func_desc: 'LINEAGE',
          // 微前端静态资源访问相对路径
          mf_res_entrance_location: '/lineage',
          // 微前端静态资源版本
          mf_latest_ver: '1.0.0',
          mf_ref_backend_apis: '/lineage',
        }, {
          transformRequest: [
            function (data) {
              let ret = ''
              for (let it in data) {
                ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
              }
              ret = ret.substring(0, ret.lastIndexOf('&'));
              return ret
            }
          ],
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          baseURL: 'http://127.0.0.1:9090',
        });

        // 查询绑定信息
        const data = await api.fetch(`/mfgov/fesdk/bindQuery/v1`, {
          main_mf_name: 'dss',
          sub_mf_name: 'lineage',
        }, {
          method: 'get',
          baseURL: 'http://127.0.0.1:9090',
        });
        this.visualSrc = data.accessLocation || '';
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
