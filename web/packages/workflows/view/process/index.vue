<template>
  <process :query="$route.query"></process>
</template>
<script>
import process from '@/workflows/module/process';
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