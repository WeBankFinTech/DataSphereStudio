<template>
  <themeDomainsView />
</template>
<script>
import themeDomainsModel from "@/apps/dataWarehouseDesign/module/themeDomains";
export default {
  components: {
    themeDomainsView: themeDomainsModel.component,
  },
};
</script>
