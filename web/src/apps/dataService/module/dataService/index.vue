<template>
  <div>
    <navMenu :menuFold="menuFold" @on-menu-toggle="handleMenuToggle" />
    <div class="ds-main-content" :class="{'ds-main-content-fold': menuFold }">
      服务开发
    </div>
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
      menuFold: false
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
    handleMenuToggle() {
      this.menuFold = !this.menuFold;
    },
  },
}
</script>

<style lang="scss" scoped>
.ds-main-content {
  margin-left: 304px;
  transition: margin-left .3s;
  &.ds-main-content-fold {
    margin-left: 54px;
  }
}
</style>

