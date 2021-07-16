<template>
  <div>
    <navMenu />
    服务管理
  </div>
</template>
<script>
import navMenu from '../common/navMenu.vue';
// import api from "@/common/service/api";
export default {
  components: {
    navMenu,
  },
  data() {
    return {
    }
  },
  computed: {

  },
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // api的基础信息
    //     this.apiData = rst.result;
    //     this.formValidate.approvalNo = this.apiData.approvalVo.approvalNo;
    //     // 更改网页title
    //     document.title = rst.result.aliasName || rst.result.name;
    //     // 加工api信息tab的数据
    //     this.apiInfoData = [
    //       { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
    //       { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
    //       { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
    //     ]
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  methods: {
  },
}
</script>

<style lang="scss" scoped>
@import '@/common/style/variables.scss';
</style>

