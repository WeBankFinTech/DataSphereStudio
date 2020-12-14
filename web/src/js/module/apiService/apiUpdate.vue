<template>
  <div class="api-update">
    <Modal
      width="680"
      :title="$t('message.apiService.apiUpdate.title')"
      v-model="updateModalShow"
      @on-cancel="cancel"
      @on-ok="updateApiOk">
      <Form
        ref="copyApi"
        :model="updateApiData"
        :label-width="100" >
        <FormItem
          :label="$t('message.apiService.apiUpdate.apiName')">
          <Input v-model="updateApiData.apiName"></Input>
        </FormItem>
        <FormItem
          :label="$t('message.apiService.apiUpdate.tag')">
          <Input v-model="updateApiData.tag"></Input>
        </FormItem>
        <FormItem
          :label="$t('message.apiService.apiUpdate.describe')">
          <Input v-model="updateApiData.describe"></Input>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import api from '@/js/service/api';
export default {
  name: "apiUpdate",
  props: {
    apiData: {
      type: Object,
      required: true,
    },
    show: {
      type: Boolean,
      required: true,
    }
  },
  mounted() {
    this.updateModalShow = this.show;
  },
  watch: {
    'show': {
      handler(val) {
        this.updateModalShow = val;
      }
    },
    'apiData': {
      handler(value) {
        this.updateApiData = {
          id: value.id,
          apiName: value.apiName,
          tag: value.tag,
          describe: value.describe
        }
      }
    }
  },
  data() {
    return {
      updateModalShow: false,
      updateApiData: {

      },
      apiVersionList: [

      ]
    }
  },
  methods: {
    produceApiInfo() {

    },
    cancel() {
      this.updateModalShow = false;
      this.$emit('on-update-cancel');
    },
    updateApiOk() {
      let _this = this;
      api.fetch('/apiservice/api/' + _this.updateApiData.id, {
        apiName: _this.updateApiData.apiName,
        tag: _this.updateApiData.tag,
        describe: _this.updateApiData.describe
      }, 'put').then((rst) => {
        this.updateModalShow = false;
        this.$emit('on-update');
      }).catch((err) => {

      });
    }
  }
}
</script>

<style scoped>

</style>
