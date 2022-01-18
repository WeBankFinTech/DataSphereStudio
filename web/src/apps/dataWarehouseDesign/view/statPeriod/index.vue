<template>
  <statPeriodView />
</template>
<script>
import statPeriodModel from "@/apps/dataWarehouseDesign/module/statPeriod";
export default {
  components: {
    statPeriodView: statPeriodModel.component,
  },
};
</script>
