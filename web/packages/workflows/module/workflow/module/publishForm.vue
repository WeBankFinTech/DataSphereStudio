<template>
  <Form
    :label-width="100"
    label-position="left">
    <FormItem
      :label="$root.$t('message.workflow.publish.desc')">
      <Input
        :rows="4"
        type="textarea"
        v-model="comment"
        :placeholder="$root.$t('message.workflow.publish.inputDesc')"></Input>
      <p style="color: red;">{{$root.$t('message.workflow.publish.publishNotice')}}</p>
    </FormItem>
  </Form>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import storage from '@dataspherestudio/shared/common/helper/storage';
export default {
  name: 'OrchestratorPublish',
  props: {
    currentData: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      comment: '',
    };
  },
  created() {
  },
  watch: {
    'currentData.id'(val) {
      if (val) {
        this.comment = '';
      }
    }
  },
  methods: {
    checkResult(releaseTaskId, timeoutValue) {
      const timer = setTimeout(() => {
        timeoutValue += 8000;
        api.fetch(`${this.$API_PATH.PUBLISH_PATH}getPublishStatus`, { releaseTaskId, dssLabel: this.getCurrentDsslabels() }, 'get').then((res) => {
          if (timeoutValue <= (10 * 60 * 1000)) {
            if (res.status === 'init' || res.status === 'running') {
              clearTimeout(timer);
              this.checkResult(releaseTaskId, timeoutValue);
            } else if (res.status === 'success') {
              clearTimeout(timer);
              this.removeRuning(this.currentData.orchestratorId)
              this.$Message.success(this.$root.$t('message.workflow.publish.success', { name: this.currentData.name }));
            } else if (res.status === 'failed') {
              clearTimeout(timer);
              this.removeRuning(this.currentData.orchestratorId)
              this.$Modal.error({
                title: this.$root.$t('message.workflow.publish.failed'),
                content: `<p style="word-break: break-all;">{{ $t('message.workflow.Publishfailed') }}</p>`,
                width: 500,
                okText: this.$root.$t('message.workflow.publish.cancel'),
              });
            }
          } else {
            clearTimeout(timer);
            this.removeRuning(this.currentData.orchestratorId)
            this.$Message.warning(this.$root.$t('message.workflow.publish.timeout', { name: this.currentData.name }));
          }
        });
      }, 8000);
    },
    removeRuning(orchestratorId) {
      let tempArray = storage.get('runningPublish') || [];
      // 防止切换页面，无法传递事件
      storage.set('runningPublish', tempArray.filter((i) => i !== orchestratorId))
      this.$emit('removeRuning', orchestratorId);
    }
  },
};
</script>
