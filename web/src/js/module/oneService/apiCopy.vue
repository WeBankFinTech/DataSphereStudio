<template>
  <div class="api-copy">
    <Modal
      width="680"
      v-model="copyModalShow">
      <Form
        ref="copyApi"
        :model="copyApiData"
        :label-width="100" >
        <Row>
          <Col span="18">
          <FormItem
            :label="$t('message.oneService.apiCopy.apiVersion')">
            <Select v-model="copyApiData.apiVersion">
              <Option v-for="item in apiVersionList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
          </Col>
          <Col span="6" style="text-align: center">
          <Button type="primary" @click="produceApiInfo">{{$t('message.oneService.apiCopy.apiInfo')}}</Button>
          </Col>
        </Row>
        <Row>
          <Col colspan="2">
          <Input v-model="copyApiData.apiInfo" type="textarea" disabled :rows="8"></Input>
          </Col>
        </Row>
      </Form>
      <div slot="footer">
        <Button
          @click="cancel">{{$t('message.oneService.apiCopy.cancel')}}</Button>
        <Button
          type="primary"
          @click="copyApiOk">{{$t('message.oneService.apiCopy.copy')}}</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import api from '@/js/service/api';
export default {
  name: "apiCopy",
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
    this.copyModalShow = this.show;
    this.init();
  },
  watch: {
    'show': {
      handler(val) {
        this.copyModalShow = val;
      }
    }
  },
  data() {
    return {
      copyModalShow: false,
      copyApiData: {
        apiInfo: ""
      },
      apiVersionList: [

      ]
    }
  },
  methods: {
    init() {
      let _this = this;
      api.fetch('/oneservice/apiVersionQuery', {
        scriptPath: this.apiData.scriptPath
      }, 'get').then((rst) => {
        if (rst.result) {
          rst.result.forEach((item, index) => {
            _this.apiVersionList.push({
              label: item.version,
              value: item.version
            })
          })

          _this.copyApiData.apiVersion = rst.result[0].version
        }
      }).catch((err) => {

      });
    },
    produceApiInfo() {
      let _this = this;
      api.fetch('/oneservice/apiParamQuery', {
        scriptPath: this.apiData.scriptPath,
        version: this.apiData.apiVersion
      }, 'get').then((rst) => {
        if (rst.result) {

          var resultStr = {}
          rst.result.forEach((item, index) => {
            resultStr[item.name] = item.testValue;
          });

          _this.copyApiData.apiInfo = JSON.stringify(resultStr, null, 4);
        } else {
          _this.copyApiData.apiInfo = JSON.stringify(JSON.parse("{}"), null, 4);
        }
      }).catch((err) => {
        _this.copyApiData.apiInfo = JSON.stringify(JSON.parse("{}"), null, 4);
      });
    },
    cancel() {
      this.copyModalShow = false;
      this.$emit('on-copy-cancel');
    },
    copyApiOk() {
      let _this = this;
      if (_this.copyApiData.apiInfo && '' != _this.copyApiData.apiInfo) {
        _this.$copyText(_this.copyApiData.apiInfo).then(function (e) {
          _this.$emit('on-copy');

          _this.$Message.success(_this.$t('message.oneService.apiCopy.copySuccess'));
        }, function (e) {
          console.log("copy error")
        })
      } else {

      }
    }
  }
}
</script>

<style scoped>

</style>
