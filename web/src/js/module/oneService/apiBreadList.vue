<template>
  <div class="example-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item
        v-for="(item,index) in breadList"
        :key="index"
        :to="{ path: item.path }"
      >{{item.name}}</el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>
<script>
export default {
  name: "apiBreadList",
  data() {
    return {
      breadList: [{
        name: "API市场",
        path: "/home"
      }, {
        name: "数据服务",
        path: "/setting"
      }]
    };
  }
};
</script>
