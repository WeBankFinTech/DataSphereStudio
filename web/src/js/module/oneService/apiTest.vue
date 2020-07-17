<template>
  <div>
    <Tabs value="api-test">
      <!--
      <TabPane :label="$t('message.oneService.apiTest.tabs.apiInfo.title')" name="api-info">
        <ApiCallSatistics/>
      </TabPane>
      -->
      <TabPane :label="$t('message.oneService.apiTest.tabs.apiTestInfo.title')" name="api-test">
        <ApiTestRun :scriptPath="scriptPath"/>
      </TabPane>
      <TabPane :label="$t('message.oneService.apiTest.tabs.apiVersionInfo.title')" name="api-version">
        <ApiVersionManager :scriptPath="scriptPath"/>
      </TabPane>
    </Tabs>
  </div>
</template>

<script>
import api from '@/js/service/api';
import ApiCallSatistics from '@/js/module/oneService/apiCallSatistics'
import ApiTestRun from '@/js/module/oneService/apiTestRun'
import ApiVersionManager from '@/js/module/oneService/apiVersionManager'
import Base64 from 'js-base64';
export default {
  name: "apiTest",
  props: {

  },
  components: {
    // 'ApiCallSatistics': ApiCallSatistics,
    'ApiTestRun': ApiTestRun,
    'ApiVersionManager': ApiVersionManager
  },
  data() {
    return {
      scriptPath: ''
    }
  },
  created: function () {
    let path = window.location.href.split("?")
    let href = path[0]+"?"+path[1]
    // let query = Base64.Base64.decode(path[1])
    // href = path[0]+"?"+ query

    this.scriptPath  = this.getUrlKey("scriptPath", href)
  },
  computed: {

  },
  mounted() {
    this.initApiVersionInfo();
  },
  methods: {
    initApiVersionInfo() {

    },
    getUrlKey(name,url){
      return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(url) || [, ""])[1].replace(/\+/g, '%20')) || null
    }
  }
}
</script>

<style scoped>

</style>
