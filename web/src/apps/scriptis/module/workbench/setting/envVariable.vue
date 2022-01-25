<template>
  <dynamic-form
    ref="form"
    :title="$t('message.scripts.setting.envVariable.title')"
    :list="script.params.configuration.runtime.env"></dynamic-form>
</template>
<script>
import dynamicForm from '@/components/dynamicForm';
export default {
  components: {
    dynamicForm,
  },
  props: {
    script: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
    };
  },
  methods: {
  },
};
</script>
