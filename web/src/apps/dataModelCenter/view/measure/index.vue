<template>
  <measureView />
</template>
<script>
import measureModel from "@/apps/dataModelCenter/module/measure";
export default {
  components: {
    measureView: measureModel.component,
  },
};
</script>
