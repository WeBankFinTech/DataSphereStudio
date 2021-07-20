<template>
  <div class="manage-wrap">
    <div class="manage-head">API测试</div>
  </div>
</template>
<script>
// import api from "@/common/service/api";
export default {
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
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.pageNum = page;
    },
  },
}
</script>

<style lang="scss" scoped>
.manage-wrap {
  padding: 0 24px;
  background: #fff;
  .manage-head {
    margin-bottom: 15px;
    padding-bottom: 10px;
    font-size: 16px;
    color: #333;
    font-weight: bold;
    border-bottom: 1px solid #DEE4EC;
  }
}
</style>