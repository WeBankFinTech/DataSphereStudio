<template>
  <Row style="height:100%">
    <Col span="3" style="height:100%"> <sidebar /></Col>
    <Col span="21" style="height:100%"> <router-view></router-view></Col>
  </Row>
</template>

<script>
import sidebar from "./sidebar.vue";
export default {
  components: {
    sidebar,
  },
};
</script>
