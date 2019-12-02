<template>
  <Form
    :label-width="100"
    label-position="left">
    <FormItem
      :label="$root.$t('message.publish.desc')">
      <Input
        :rows="4"
        type="textarea"
        v-model="comment"
        :placeholder="$root.$t('message.publish.inputDesc')"></Input>
    </FormItem>
  </Form>
</template>
<script>
import api from '@/js/service/api';
import { setTimeout } from 'timers';
import storage from '@/js/helper/storage';

export default {
  name: 'Publish',
  props: {
    currentProjectData: {
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
  methods: {
    publish() {
      // 发布
      this.dispatch('Project:loading', true);
      api.fetch('/dss/publish', {
        id: this.currentProjectData.id,
        comment: this.comment,
      }).then(() => {
        this.dispatch('Project:loading', false);
        // 轮询结果超过十分钟，提示超时
        let timeoutVlaue = 0;
        this.checkResult(this.currentProjectData.latestVersion.id, timeoutVlaue);
      }).catch(() => {
        this.$Message.warning(this.$root.$t('message.publish.error', { name: this.currentProjectData.name }));
        this.removePercent();
        this.dispatch('Project:getData');
      });
    },
    checkResult(id, timeoutValue) {
      const timer = setTimeout(() => {
        timeoutValue += 8000;
        this.currentProjectData.percent = `${(timeoutValue / 3000).toFixed(0)}%`;
        if ((timeoutValue / 3000).toFixed(0) >= 98) {
          this.currentProjectData.percent = '98%';
        }
        this.dispatch('Project:updataPercent', this.currentProjectData);
        api.fetch('/dss/publishQuery', { projectVersionID: +id }, 'get').then((res) => {
          if (timeoutValue <= (10 * 60 * 1000)) {
            if (res.info.status === 'Inited' || res.info.status === 'Running') {
              clearTimeout(timer);
              this.checkResult(id, timeoutValue);
            } else if (res.info.status === 'Succeed') {
              clearTimeout(timer);
              this.currentProjectData.percent = '100%';
              this.$Message.success(this.$root.$t('message.publish.success', { name: this.currentProjectData.name }));
              this.removePercent();
              this.dispatch('Project:getData');
              this.$emit('updateWorkflow')
            } else if (res.info.status === 'Failed') {
              clearTimeout(timer);
              this.removePercent();
              this.dispatch('Project:getData');
              this.$Modal.error({
                title: this.$root.$t('message.publish.failed'),
                content: `<p style="word-break: break-all;">${res.info.msg}</p>`,
                width: 500,
                okText: this.$root.$t('message.publish.cancel'),
              });
            }
          } else {
            clearTimeout(timer);
            this.$Message.warning(this.$root.$t('message.publish.timeout', { name: this.currentProjectData.name }));
            this.removePercent();
            this.dispatch('Project:getData');
          }
          this.dispatch('Project:updataPercent');
        });
      }, 8000);
    },
    removePercent() {
      let precentList = storage.get('precentList');
      if (precentList) {
        precentList = precentList.filter((item) => {
          return item.id !== this.currentProjectData.id;
        });
        storage.set('precentList', precentList);
      }
    },
  },
};
</script>
