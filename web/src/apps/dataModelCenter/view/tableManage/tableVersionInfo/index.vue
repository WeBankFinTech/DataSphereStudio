<template>
  <tableVersionInfoView />
</template>
<script>
import tableVersionInfoModel from "@/apps/dataModelCenter/module/tableVersionInfo";
export default {
  components: {
    tableVersionInfoView: tableVersionInfoModel.component,
  },
};
</script>
