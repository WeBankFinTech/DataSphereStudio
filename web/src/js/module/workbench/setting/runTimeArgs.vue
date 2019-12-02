<template>
  <div class="runtime-args">
    <div class="setting-title-wrap">
      <h3 class="setting-title">{{ $t('message.workBench.body.setting.runTimeArgs.settingTitle') }}</h3>
    </div>
    <Form
      ref="paramForm"
      :model="script.params"
      class="custom-variable-content">
      <FormItem>
        <Input
          v-model="script.params.configuration.runtime.args"
          type="text"
          :placeholder="$t('message.workBench.body.setting.runTimeArgs.placeholder')"></Input>
      </FormItem>
    </Form>
  </div>
</template>
<script>
export default {
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
