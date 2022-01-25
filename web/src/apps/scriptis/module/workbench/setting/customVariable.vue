<template>
  <dynamic-form
    ref="form"
    :title="$t('message.scripts.setting.customVariable.title')"
    :list="script.params.variable"></dynamic-form>
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
};
</script>
