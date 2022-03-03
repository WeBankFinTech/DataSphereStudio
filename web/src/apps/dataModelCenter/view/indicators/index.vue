<template>
  <indicatorsView />
</template>
<script>
import indicatorsModel from "@/apps/dataModelCenter/module/indicators";
export default {
  components: {
    indicatorsView: indicatorsModel.component,
  },
};
</script>
