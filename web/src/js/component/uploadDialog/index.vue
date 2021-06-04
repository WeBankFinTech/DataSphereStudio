<template>
  <Modal
    v-model="show"
    class="we-upload"
    width="340">
    <div slot="header">
      <span>{{$t('message.uploadDialog.SCJBWJ')}}</span>
    </div>
    <div class="we-upload-content">
      <Upload
        ref="upload"
        :action="updateUrl"
        :headers="headers"
        :data="uploadData"
        :max-size="maxSize"
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-progress="handleProgress"
        :on-exceeded-size="exceededSize"
        multiple
        type="drag">
        <div style="padding: 20px 0">
          <Icon
            type="ios-cloud-upload"
            size="52"
            style="color: #3399ff"/>
          <p class="el-upload__text">{{$t('message.uploadDialog.TWJDSQ')}}</p>
          <p class="el-upload__tip">{{ msg }}</p>
          <p class="we-upload_path">{{$t('message.uploadDialog.PATH')}}{{ path }}</p>
        </div>
      </Upload>
      <Spin
        v-if="isUploading"
        size="large"
        fix/>
    </div>
    <template slot="footer">
      <Button
        :disabled="isUploading"
        size="small"
        type="warning"
        @click="resetUpload">{{$t('message.uploadDialog.QKLB')}}</Button>
    </template>
  </Modal>
</template>
<script>
export default {
  props: {
    refresh: Function,
  },
  data() {
    return {
      show: false,
      uploadFiles: [],
      updateUrl: '',
      maxSize: 0,
      msg: null,
      path: null,
      listName: null,
      uploadData: null,
      isBtnShow: true,
      isUploading: false,
    };
  },
  computed: {
    headers() {
      return {
        // 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
      };
    },
  },
  watch: {
    show(val) {
      if (!val) {
        this.uploadFiles = [];
        this.path = null;
        this.uploadData = null;
        this.listName = null;
        this.isBtnShow = true;
        this.isUploading = false;
        this.$refs.upload.clearFiles();
      }
    },
  },
  methods: {
    // 打开的时候赋值
    async open(data) {
      this.path = data.path;
      if (data.nameList) {
        this.listName = data.nameList;
      }
      this.uploadData = { path: this.path };
      this.show = true;
      if (data.type === 'file://') {
        this.msg = `(${this.$t('message.uploadDialog.LIMIT2M')})`;
        this.maxSize = 2048;
      } else {
        this.msg = `(${this.$t('message.uploadDialog.LIMIT100M')})`;
        this.maxSize = 102400;
      }
      this.updateUrl = `${data.apiPrefix}filesystem/upload`;
    },

    // 关闭的时候清空数据
    close() {
      if (this.isUploading) {
        this.$confirm(this.$t('message.uploadDialog.ZDSC'), this.$t('message.uploadDialog.TS'), {
          confirmButtonText: this.$t('message.uploadDialog.ZJGB'),
          cancelButtonText: this.$t('message.uploadDialog.QX'),
          type: 'warning',
        }).then(() => {
          this.path = null;
          this.listName = null;
          this.uploadData = null;
          this.isUploading = false;
          this.show = false;
        }).catch(() => {
          this.isUploading = true;
          this.$Message.warning(this.$t('message.uploadDialog.QXBBSCCK'));
        });
      } else {
        this.show = false;
        this.path = null;
        this.listName = null;
        this.uploadData = null;
        this.isUploading = false;
      }
    },

    // 错误时的回调
    handleError(err, file, fileList) {
      this.$Message.error(err.message);
      this.isUploading = false;
      this.refresh();
      this.close();
    },

    // 成功时的回调
    handleSuccess(response, file, fileList) {
      if (response.status === 0) {
        this.$Message.success(this.$t('message.uploadDialog.SCCG', { name: file.name }));
        this.isUploading = false;
        this.close();
        setTimeout(() => {
          this.refresh();
        }, 800);
      } else {
        this.$Message.warning(response.message);
      }
    },

    exceededSize(file, fileList) {
      this.$Message.warning(this.$t('message.uploadDialog.WJDXCCXE'));
    },

    // 发生改变时的回调
    beforeUpload(file) {
      const isInFlag = this.listName.find((item) => item === file.name);
      const regLeaf = /^[.\w\u4e00-\u9fa5]{1,200}\.[A-Za-z]+$/;
      if (isInFlag) {
        this.$Message.warning(this.$t('message.uploadDialog.WJYCZ'));
        return false;
      } else if (!regLeaf.test(file.name)) {
        // 18.2.8 新增正则判断，如果验证不通过则提示警告，不让上传
        this.$Message.warning(this.$t('message.uploadDialog.WJMBHF'));
        return false;
      }
      this.uploadFiles.push(file);
      // return false;
    },

    // 上传文件过程中的回调
    handleProgress(event, file, fileList) {
      if (file.status === 'uploading') {
        this.isUploading = true;
      } else {
        this.isUploading = false;
      }
    },

    // 提交表单
    upload() {
      this.isUploading = true;
    },

    // 清除文件列表
    resetUpload() {
      if (this.isUploading) {
        this.$Message.warning(this.$t('message.uploadDialog.SCZQK'));
      } else {
        this.$refs.upload.clearFiles();
      }
    },
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
