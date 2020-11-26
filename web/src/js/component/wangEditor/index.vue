<template>
  <div ref="editor"></div>
</template>
<script>
import E from 'wangeditor'

export default {
  data() {
    return {
      editor: null,
    };
  },
  mounted() {
    this.createEditor();
    // api.fetch('dss/workspaces/departments', 'get').then((res) => {
    //   this.departments = res.departments;
    // });
  },
  methods: {
    createEditor() {
      this.editor = new E(this.$refs.editor);
      this.editor.config.height = 200;
      this.editor.config.zIndex = 100;
      // this.editor.config.uploadImgShowBase64 = false // base 64 存储图片
      this.editor.config.uploadImgServer = 'http://otp.cdinfotech.top/file/upload_images'; // 配置服务器端地址
      this.editor.config.uploadImgHeaders = {}; // 自定义 header
      this.editor.config.uploadFileName = 'file'; // 后端接受上传文件的参数名
      // this.editor.config.uploadImgMaxSize = 2 * 1024 * 1024; // 将图片大小限制为 2M
      // this.editor.config.uploadImgMaxLength = 6; // 限制一次最多上传 3 张图片
      // this.editor.config.uploadImgTimeout = 3 * 60 * 1000; // 设置超时时间

      // 配置菜单
      this.editor.config.menus = [
        'head', // 标题
        'bold', // 粗体
        'fontSize', // 字号
        'fontName', // 字体
        'italic', // 斜体
        'underline', // 下划线
        'strikeThrough', // 删除线
        'foreColor', // 文字颜色
        'backColor', // 背景颜色
        'link', // 插入链接
        'list', // 列表
        'justify', // 对齐方式
        'quote', // 引用
        'emoticon', // 表情
        'image', // 插入图片
        'table', // 表格
        'video', // 插入视频
        'code', // 插入代码
        'undo', // 撤销
        'redo', // 重复
        'fullscreen' // 全屏
      ]

      this.editor.config.uploadImgHooks = {
        fail: (xhr, editor, result) => {
          // 插入图片失败回调
        },
        success: (xhr, editor, result) => {
          // 图片上传成功回调
        },
        timeout: (xhr, editor) => {
          // 网络超时的回调
        },
        error: (xhr, editor) => {
          // 图片上传错误的回调
        },
        customInsert: (insertImg, result, editor) => {
          // 图片上传成功，插入图片的回调
          // result为上传图片成功的时候返回的数据，这里我打印了一下发现后台返回的是data：[{url:"路径的形式"},...]
          // console.log(result.data[0].url)
          // insertImg()为插入图片的函数
          //循环插入图片
          // for (let i = 0; i < 1; i++) {
          // console.log(result)
          let url = "http://otp.cdinfotech.top"+result.url;
          insertImg(url);
          // }
        }
      }
      this.editor.config.onchange = (html) => {
        this.info_ = html // 绑定当前逐渐地值
        this.$emit('change', this.info_) // 将内容同步到父组件中
      }
      this.editor.create();
    }
  },
  beforeDestroy() {
    // 销毁编辑器
    this.editor.destroy();
    this.editor = null;
  }
};
</script>