<template>
  <div>
    <ul class="we-toolbar">
      <li
        @click="openPanel('log')">
        <Icon
          type="md-list-box"
          size="14"/>
        <span class="v-toolbar-icon">{{ $t('message.workBench.body.script.toolbarProgress.log') }}</span>
      </li>
    </ul>
  </div>
</template>
<script>
export default {
  props: {
    status: String,
    application: String,
  },
  data() {
    return {
    };
  },
  methods: {
    openPanel(type) {
      this.$emit('open-panel', type);
    },
  },
};
</script>
