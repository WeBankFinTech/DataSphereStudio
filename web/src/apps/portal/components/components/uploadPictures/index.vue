<template>
  <div>
    <template v-if="uploadList.length > 0">
      <div class="upload-list"  v-for="(item, index) in uploadList" :key="index">
        <img :src="item.url">
        <div class="upload-list-cover">
          <Icon type="ios-eye-outline" @click.native="handleView(index)"></Icon>
          <Icon type="ios-trash-outline" @click.native="handleRemove(index)"></Icon>
        </div>
      </div>
    </template>
    <Upload
      ref="upload"
      :format="['jpg','jpeg', 'png', 'svg']"
      :max-size="100"
      :data="uploadData"
      :before-upload="handleBeforeUpload"
      type="drag"
      action=""
      style="display: inline-block;width:50px;"
    >
      <div style="width: 50px;height:50px;line-height: 50px;">
        <Icon type="ios-cloud-upload" size="20"></Icon>
      </div>
    </Upload>
    <Modal title="View Image" v-model="visible" :footer-hide="true">
      <img
        :src="uploadList[actionIndex].url"
        v-if="visible"
        style="width: 100%"
      >
    </Modal>
  </div>
</template>
<script>
export default {
  props: {
    url: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      visible: false,
      uploadList: [],
      uploadData: {
        portalId: this.$route.query.portalId
      },
      actionIndex: ''
    };
  },
  watch: {
    url(val) {
      if (val) {
        this.uploadList = [{url: this.url}]
      } else {
        this.uploadList = [];
      }
    }
  },
  methods: {
    handleView(index) {
      this.actionIndex = index;
      this.visible = true;
    },
    handleRemove(index) {
      this.uploadList.splice(index, 1);
      this.$emit('uploadlogo', this.uploadList)
    },
    handleBeforeUpload(file) {
      // 读取文件将文件转成base64
      // 文件大小不得超过100k
      if (file.size / 1024 > 100) return this.$Notice.warning({
        title: "Exceeding file size limit",
        desc: "File  " + file.name + " is too large, no more than 100k."
      });
      // 上传的文件只支持特定类型['jpg','jpeg', 'png', 'svg','ico']
      const name = file.name || ''
      const fileType = file.name.substring(name.lastIndexOf('.'), name.length)
      if (!['.jpg','.jpeg', '.png', '.svg', '.ico'].includes(fileType)) return this.$Notice.warning({
        title: "Exceeding file type limit",
        desc: "Only the following image types SVG、PNG、JPG、JPEG、ICO are supported"
      });
      let reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (e) => {
        const obj = {
          url: e.target.result,
        }
        this.uploadList = [obj]
        this.$emit('uploadlogo', this.uploadList)
      }
      return false;
    }
  },
};
</script>
<style lang="scss" scoped>
.upload-list {
  display: inline-block;
  width: 60px;
  height: 60px;
  text-align: center;
  line-height: 60px;
  border: 1px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
  position: relative;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
  margin-right: 4px;
  img {
    width: 100%;
    height: 100%;
  }
  .upload-list-cover {
    display: none;
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.6);
  }
}
.upload-list:hover .upload-list-cover {
  display: block;
}
.upload-list-cover i {
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  margin: 0 2px;
}
</style>