<template>
  <div class="designer-toolbar">
    <slot />
  </div>
</template>

<script>
import { mixin } from './store';
export default {
  mixins: [mixin],
  data() {
    return {
    };
  },
  mounted() {
  },
  methods: {
  }
};
</script>
