<template>
  <div class="api-form">
    <Form
      :label-width="100"
      label-position="left"
      ref="apiForm"
      :model="apiData"
      :rules="formValid"
    >
      <FormItem
        :label="$t('message.api.name')"
        prop="apiName">
        <Input
          v-model="apiData.apiName"
          :placeholder="$t('message.api.enterName')"
        />
      </FormItem>
      <FormItem
        :label="$t('message.api.url')"
        prop="url">
        <Input
          v-model="apiData.url"
          :placeholder="$t('message.api.enterUrl')"
        />
      </FormItem>
      <!-- api参数 -->
      <FormItem>
        <Row>
          <Col>
          <Button type="dashed" long @click="handleAddGetParams" icon="md-add">{{$t('message.api.addParams')}}</Button>
          </Col>
        </Row>
      </FormItem>
      <FormItem
        v-for="(item, index) in getParams"
        :key="index"
        :label="'参数 ' + Number(index + 1)"
      >
        <Row>
          <Col span="10">
          <Input type="text" v-model="item.key" placeholder="key"/>
          </Col>
          <Col span="10" offset="1">
          <Input type="text" v-model="item.value" placeholder="value"/>
          </Col>
          <Col span="2" offset="1">
          <Button type="error" size="small" @click="removeGetParams(index)">{{$t('message.api.delete')}}</Button>
          </Col>
        </Row>
      </FormItem>
      <FormItem
        :label="$t('message.api.dataJsonPath')"
        prop="dataJsonPath">
        <Input
          v-model="apiData.dataJsonPath"
          :placeholder="$t('message.api.enterDataPath')"
        />
      </FormItem>
      <FormItem
        :label="$t('message.api.successCode')"
        prop="successCode">
        <Input
          v-model="apiData.successCode"
          :placeholder="$t('message.api.enterCode')"
        />
      </FormItem>
      <FormItem
        :label="$t('message.api.successCodeJsonPath')"
        prop="successCodeJsonPath">
        <Input
          v-model="apiData.successCodeJsonPath"
          :placeholder="$t('message.api.enterCodePath')"
        />
      </FormItem>
      <!-- 定时 -->
      <FormItem :label="$t('message.api.frequency')"
        prop="frequency">
        <TimePicker
          :open="open"
          v-model="apiData.frequency"
          confirm
          @on-change="handleChange"
          @on-clear="handleClear"
          @on-ok="handleOk">
          <a href="javascript:void(0)" @click="handleClick">
            <Icon type="ios-clock-outline"></Icon>
            <template v-if="apiData.frequency === ''">{{$t('message.api.selectFrequency')}}</template>
            <template v-else>{{ apiData.frequency }}</template>
          </a>
        </TimePicker>
      </FormItem>
      <FormItem
        :label="$t('message.api.defaultFS')"
        prop="defaultFS">
        <Input
          v-model="apiData.defaultFS"
          :placeholder="$t('message.api.enterHive')"
        />
      </FormItem>
      <FormItem
        :label="$t('message.api.fileName')"
        prop="fileName">
        <Input
          v-model="apiData.fileName"
          :placeholder="$t('message.api.enterFileName')"
        />
      </FormItem>
      <FormItem
        :label="$t('message.api.path')"
        prop="path">
        <Input
          v-model="apiData.path"
          :placeholder="$t('message.api.enterPath')"
        />
      </FormItem>
      <!-- 字段映射 -->
      <FormItem>
        <Row>
          <Col>
          <Button type="dashed" long @click="handleAddRelation" icon="md-add">{{$t('message.api.addRelation')}}</Button>
          </Col>
        </Row>
      </FormItem>
      <FormItem
        :label="$t('message.api.relation')"
        required
      >
        <Row>
          <Col :span="12">
          <Mapping
            :options="readerOptions"
            name="source"
            ref="readerCol"
          />
          </Col>
          <Col :span="12">
          <Mapping
            :options="writerOptions"
            ref="writerCol"
            name="target"
            isDelete
            @remove="handleRemoveReaderCol"
          />
          </Col>
        </Row>
      </FormItem>
    </Form>
    <div class="form-btn">
      <Button
        size="large"
        @click="Cancel">{{$t('message.api.cancel')}}</Button>
      <Button
        type="primary"
        size="large"
        :loading="loading"
        @click="Ok">{{$t('message.api.ok')}}</Button>
    </div>
  </div>
</template>

<script>
import api from '@/js/service/api';
import Mapping from './Mapping';

export default {
  components: {
    Mapping,
  },
  data() {
    return {
      index: 1,
      frequency: 0,
      apiData: {
        apiName: 'httpreader',
        url: 'http://9p8yse.natappfree.cc/api',
        successCode: '200',
        successCodeJsonPath: '$.code',
        dataJsonPath: '$.data',
        defaultFS: 'hdfs://192.168.9.180:9000',
        fileName: 'orc_table',
        path: '/user/hive/warehouse/orc_table',
        frequency: ''
      },
      getParams: [
        { key: 'sex', value: '1', index: 1 },
      ],
      open: false,
      loading: false
    }
  },
  computed: {
    formValid() {
      return {
        name: [{ required: true, message: this.$t('message.api.enterName'), trigger: 'blur' }],
        url: [{ required: true, message: this.$t('message.api.enterUrl'), trigger: 'blur' }],
        successCode: [{ required: true, message: this.$t('message.api.enterCode'), trigger: 'blur' }],
        successCodeJsonPath: [{ required: true, message: this.$t('message.api.enterCodePath'), trigger: 'blur' }],
        dataJsonPath: [{ required: true, message: this.$t('message.api.enterDataPath'), trigger: 'blur' }],
        defaultFS: [{ required: true, message: this.$t('message.api.enterHive'), trigger: 'blur' }],
        path: [{ required: true, message: this.$t('message.api.enterPath'), trigger: 'blur' }],
        fileName: [{ required: true, message: this.$t('message.api.enterFileName'), trigger: 'blur' }],
        readerCol: [{ required: true, message: this.$t('message.api.enterReaderCol'), trigger: 'blur' }],
        writerCol: [{ required: true, message: this.$t('message.api.enterWriterCol'), trigger: 'blur' }],
        frequency: [{ required: true, message: this.$t('message.api.selectFrequency'), trigger: 'blur' }]
      };
    },
    readerOptions() {
      return [
        { value: 'LONG', label: 'LONG' },
        { value: 'DOUBLE', label: 'DOUBLE' },
        { value: 'STRING', label: 'STRING' },
        { value: 'BOOLEAN', label: 'BOOLEAN' },
        { value: 'DATE', label: 'DATE' },
        { value: 'INT', label: 'INT' }
      ]
    },
    writerOptions() {
      return [
        { value: 'TINYINT', label: 'TINYINT' },
        { value: 'SMALLINT', label: 'SMALLINT' },
        { value: 'INT', label: 'INT' },
        { value: 'BIGINT', label: 'BIGINT' },
        { value: 'FLOAT', label: 'FLOAT' },
        { value: 'DOUBLE', label: 'DOUBLE' },
        { value: 'STRING', label: 'STRING' },
        { value: 'VARCHAR', label: 'VARCHAR' },
        { value: 'CHAR', label: 'CHAR' },
        { value: 'BOOLEAN', label: 'BOOLEAN' },
        { value: 'TIMESTAMP', label: 'TIMESTAMP' },
        { value: 'DATE', label: 'DATE' }
      ]
    },
  },
  methods: {
    Cancel() {
      this.gotoApiList();
    },
    Ok() {
      if (!this.handleValidateMapping()) {
        this.$Message.warning(this.$t('message.api.enterMapping'));
        return;
      }
      this.$refs.apiForm.validate((valid) => {
        if (valid) {
          // 构建url参数
          const url = this.formatUrl();
          const { apiName, dataJsonPath, successCode, successCodeJsonPath, defaultFS, path, fileName } = this.apiData;
          const data = {
            frequency: this.frequency,
            // apiName,
            reader: {
              dataJsonPath, successCode: Number(successCode), successCodeJsonPath, url: [url],
              httpMethod: 'get', delayTime: 300,
              column: this.$refs.readerCol.formData
            },
            writer: {
              defaultFS, path, fileName, column: this.$refs.writerCol.formData
            }
          };
          console.log(data)
          this.loading = true;
          api.fetch('/api/rest_j/v1/test/jobEngine/addConfig', data, 'post').then(() => {
            this.loading = false;
            // 跳转到api列表
            // this.gotoApiList();
            this.$Message.success(this.$t('message.api.successNotice'));
          }).catch(() => {
            this.loading = false;
            this.$Message.warning(this.$t('message.api.failedNotice'));
          });
        }
      });
    },
    formatUrl() {
      let params = [];
      for (const item of this.getParams) {
        params.push(`${item.key}=${item.value}`);
      }
      return this.apiData.url+'?'+params.join('&');
    },
    handleAddRelation() {
      this.$refs.readerCol.add();
      this.$refs.writerCol.add();
    },
    handleValidateMapping() {
      const readerValid = this.$refs.readerCol.validate();
      const writerValid = this.$refs.writerCol.validate();
      return readerValid && writerValid;
    },
    handleClick () {
      this.open = !this.open;
    },
    handleChange (time) {
      if (time) {
        const timeArr = time.split(':').map(item => Number(item));
        this.frequency = timeArr[0] * 60 * 60 + timeArr[1] * 60 + timeArr[2];
      }
    },
    handleClear () {
      this.open = false;
      this.frequency = 0;
    },
    handleOk () {
      this.open = false;
    },
    gotoApiList() {
      this.$router.push({ path: '/api' });
    },
    handleAddGetParams() {
      this.index++;
      this.getParams.push({
        value: '',
        key: '',
        index: this.index,
      });
    },
    removeGetParams(index) {
      this.getParams.splice(index, 1);
    },
    handleRemoveReaderCol(index) {
      this.$refs.readerCol.remove(index);
    }
  },
}
</script>

<style lang="scss" scoped>
.api-form {
  padding: 20px 40px;
  width: 80%;
  .form-btn {
    display: flex;
    justify-content: center;
    .ivu-btn {
      width: 100px;
      margin-right: 10px;
    }
  }
}
</style>