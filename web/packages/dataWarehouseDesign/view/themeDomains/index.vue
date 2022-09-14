<template>
  <themeDomainsView />
</template>
<script>
import themeDomainsModel from "../../module/themeDomains";
export default {
  components: {
    themeDomainsView: themeDomainsModel.component,
  },
};
</script>
