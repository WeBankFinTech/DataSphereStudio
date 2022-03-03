<template>
  <layeredView />
</template>
<script>
import layeredModel from "@/apps/dataWarehouseDesign/module/layered";
export default {
  components: {
    layeredView: layeredModel.component,
  },
};
</script>
