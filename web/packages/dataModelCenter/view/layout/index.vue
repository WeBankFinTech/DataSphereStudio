<template>
  <Row style="height: 100%">
    <Col span="3" style="height: 100%">
      <sidebar/>
    </Col>
    <Col span="21" class="content-view">
      <div class="page-content">
        <router-view/>
      </div>
    </Col>
  </Row>
</template>

<script>
import sidebar from "./sidebar.vue";

export default {
  components: {sidebar},
};
</script>
<style scoped lang='scss'>
@import "../../assets/styles/common.scss";

.content-view {
  height: 100%;
  overflow: auto;
}
</style>
