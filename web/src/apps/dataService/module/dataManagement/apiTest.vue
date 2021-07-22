<template>
  <Test />
</template>
<script>
// import api from "@/common/service/api";
import Test from '../common/test.vue';
export default {
  components: {
    Test,
  },
  data() {
    return {
    }
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

</style>