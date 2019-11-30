<template>
  <dynamic-form
    ref="form"
    :title="$t('message.workBench.body.setting.customVariable.title')"
    :list="script.params.variable"></dynamic-form>
</template>
<script>
import dynamicForm from '@/js/component/dynamicForm';
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
