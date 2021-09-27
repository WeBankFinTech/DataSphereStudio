<template>
  <div class="page-content">
    <GeneralInfo />
    <Tabs value="name1">
      <TabPane label="基本信息" name="name1">基本信息</TabPane>
      <TabPane label="字段信息" name="name2">字段信息</TabPane>
      <TabPane label="分区信息" name="name3">分区信息</TabPane>
      <TabPane label="数据预览" name="name4">数据预览</TabPane>
    </Tabs>
  </div>
</template>

<script>
import GeneralInfo from "./generalInfo.vue";
export default {
  components: {
    GeneralInfo,
  },
  mounted(){
    console.log(this.$route.params.id)
  }
};
</script>

<style>
@import "../../../assets/styles/common.scss";

</style>
