<template>
  <div class="workflow-resource">
    <div class="workflow-resource-upload">
      <Upload
        ref="upload"
        :action="updateUrl"
        :headers="headers"
        :data="uploadData"
        :default-file-list="uploadFiles"
        :max-size="maxSize"
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-progress="handleProgress"
        :on-exceeded-size="exceededSize"
        :on-remove="handleRemove"
        :on-preview="handlePreview"
        type="drag"
        :disabled="readonly">
        <div style="padding: 20px 0">
          <Icon
            type="ios-cloud-upload"
            size="52"
            style="color: #3399ff"/>
          <p class="el-upload__text">{{$t('message.workflow.process.resourceBar.TZDLSC')}}</p>
        </div>
      </Upload>
    </div>
    <Spin
      v-if="isUploading"
      size="large"
      fix/>
  </div>
</template>
<script>
import { remove } from 'lodash';
import api from '@/common/service/api';
import module from '../index';
import { NODETYPE } from '@/apps/workflows/service/nodeType'
export default {
  props: {
    resources: {
      type: Array,
      default: () => [],
    },
    flowName: String,
    isRipetition: {
      type: Boolean,
      default: false,
    },
    nodeType: {
      type: String,
      default: '',
    },
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      uploadFiles: [],
      updateUrl: '',
      uploadData: null,
      msg: null,
      path: null,
      maxSize: 0,
      isUploading: false,
    };
  },
  computed: {
    headers() {
      return {
        // 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',/
      };
    },
  },
  watch: {
    resources(val) {
      // if (isEmpty(oldVal)) {
      this.setData();
      // }
      if (!val.length) {
        this.reset();
      }
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.updateUrl = `${module.data.API_PATH}bml/uploadShareResource`;
      this.uploadData = { system: 'WTSS', isExpire: true, projectName: this.$route.query.projectName || '' };
      this.maxSize = 1024000;
      this.setData();
    },
    setData() {
      if (this.resources.length) {
        this.uploadFiles = this.resources.map((item) => {
          item.name = item.fileName;
          return item;
        });
      }
    },
    reset() {
      this.uploadFiles = [];
      this.msg = null;
      this.path = null;
    },
    // 发生改变时的回调
    beforeUpload(file) {
      const isInFlag = this.uploadFiles.find((item) => item.name === file.name);
      const regLeaf = /^[-.\w\u4e00-\u9fa5]{1,200}\.[A-Za-z]+$/;
      const sizeResult = file.size >= 100 * 1024 * 1024;
      if (this.isRipetition) {
        if (this.nodeType === NODETYPE.SPARKPY) {
          if (!(/\.zip$/i.test(file.name))) {
            this.$Message.warning(this.$t('message.workflow.process.resourceBar.PYSPARKNODE'));
            return false;
          }
        } else if (this.nodeType === NODETYPE.SPARKSQL) {
          if (!(/\.jar$/i.test(file.name))) {
            this.$Message.warning(this.$t('message.workflow.process.resourceBar.SPARKNOSW'));
            return false;
          }
        }
        if (isInFlag) { // 有相同传resourceId升版本
          this.uploadData.resourceId = isInFlag.resourceId;
        }
      }
      if (isInFlag && !this.isRipetition) {
        this.$Message.warning(this.$t('message.workflow.process.resourceBar.WJYCZ'));
        return false;
      } else if (!regLeaf.test(file.name)) {
        this.$Message.warning(this.$t('message.workflow.process.resourceBar.WJMCBHF'));
        return false;
      } else if (sizeResult) {
        this.$Message.warning(this.$t('message.workflow.process.resourceBar.SCBCG100'));
        return false;
      }
    },
    // 成功时的回调
    handleSuccess(response, file, fileList) {
      if (response.status === 0) {
        this.$Message.success(this.$t('message.workflow.process.resourceBar.SCCG', {name: file.name}));
        if (this.isRipetition) { // 支持同名上传，后者覆盖前者
          remove(fileList, (item) => item.name === file.name && item.resourceId !== file.resourceId);
          remove(this.uploadFiles, (item) => item.name === file.name);
        }
        this.uploadFiles.push(Object.assign(file, {
          fileName: file.name,
          resourceId: response.data.resourceId,
          version: response.data.version,
        }));
        this.$emit('update-resources', this.uploadFiles);
      } else {
        remove(fileList, (item) => item.name === file.name);
        this.$Message.warning(response.message);
      }
      this.isUploading = false;
    },
    // 错误时的回调
    handleError(err, file) {
      this.$Message.error(file.message);
      this.isUploading = false;
    },
    // 上传文件过程中的回调
    handleProgress(event, file) {
      if (file.status === 'uploading') {
        this.isUploading = true;
      } else {
        this.isUploading = false;
      }
    },
    exceededSize() {
      this.$Message.warning(this.$t('message.workflow.process.resourceBar.WJCCXE'));
    },
    handleRemove(file) {
      api.fetch('/bml/deleteResource', {
        resourceId: file.resourceId,
      }, 'post').then(() => {
        remove(this.uploadFiles, (item) => item.resourceId === file.resourceId);
        this.$emit('update-resources', this.uploadFiles);
        this.$Message.success(this.$t('message.workflow.process.resourceBar.WJBSC', {name: file.name}));
      });
    },
    handlePreview() {
    },
  },
};
</script>
<style lang="scss" src="../index.scss">

</style>
