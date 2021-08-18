<template>
  <process :query="$route.query"></process>
</template>
<script>
import process from '@/apps/workflows/module/process';
export default {
  components: {
    process: process.component,
  },
  data() {
    return {
    };
  },
};
</script>