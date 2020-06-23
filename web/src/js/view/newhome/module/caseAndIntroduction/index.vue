<template>
  <div class="admin-box">
    <div class="admin-box-top">
      <div class="admin-box-left">
        <p class="title">{{$t('message.GLY.ALTY')}}</p>
        <div class="admin-left-content">
          <div class="content-box" v-for="(item, index) in demos" :key="index">
            <div style="width: 30%;margin: auto;">
              <img style="width: 100%;margin-top:10px" :src="getsrc(item,index)" alt />
            </div>

            <div style="margin-top: 15px;">
              <h3 style="font-size: 12px;text-align: center;margin-bottom: 25px;">{{index}}</h3>
              <div class="demo-item" v-for="(text,index) in item" :key="index">
                <a class="demo-title" :href="filterValid(text.url)">{{text.title}}</a>

              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="admin-box-right">
        <p class="title">{{$t('message.GLY.KSRM')}}</p>
        <div class="admin-box-video">
          <div v-for="(item,index) in data.videos" :key="index" class="video-item" @click="play(item)">
            <video width="100%" height="100" controls>
              <source :src="item.url" type="video/mp4" />
            </video>
            <h3 class="video-title">{{item.title}}</h3>
          </div>
        </div>
      </div>
    </div>
    <Modal
      v-model="showVideo"
      :title="video.title"
      :footer-hide="true"
      width="800"
    >
      <video v-if="showVideo" width="100%" controls autoplay>
        <source :src="video.url" type="video/mp4" />
      </video>
    </Modal>
  </div>
</template>
<script>
export default {
  props: {
    applist: Array,
    data: Object
  },
  data() {
    return {
      menuurl: [
        {title: 'Schedulis',url: '/workspaceHome/scheduleCenter',icon: 'fi-schedule'},
        {title: 'Scriptis',url: '/home',icon: 'fi-scriptis'},
        {title: 'Workflow',url: '/project',icon: 'fi-workflow1'},
      ],
      showVideo: false,
      video: {},
      demos: {

      }
    }
  },
  watch: {
    data(val) {
      let temDemo = {workflow: '',
        application: '',
        visualization: '',
        工作流: '',
        应用场景: '',
        可视化: '',}
      Object.keys(temDemo).map((key) => {
        if (this.data.demos[key]) {
          this.demos[key] = this.data.demos[key]
        }
      })
    }
  },
  methods: {
    // 过滤a标签url，防止XSS
    filterValid(url) {
      const reg = /^(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
      if (reg.test(url)) {
        return url;
      } else {
        return 'javascript:;';
      }
    },
    getsrc(v, index) {
      let src = {
        workflow: require("../images/edit1.png"),
        application: require("../images/333.png"),
        visualization: require("../images/111.png"),
        工作流: require("../images/edit1.png"),
        应用场景: require("../images/333.png"),
        可视化: require("../images/111.png"),

      };
      return src[index];
    },
    play(item) {
      this.showVideo = true;
      this.video = item
    }
  }
};
</script>

<style lang="scss" scoped>
.admin-box {
  padding-top: 20px;
  .title {
    font-size: 14px;
    border-left: 3px solid #2d8cf0;
    padding-left: 5px;
    font-weight: 900;
    height: 25px;
    color: #333;
  }
  .admin-box-top {
    display: flex;
    justify-content: space-between;
    .admin-box-left {
      padding: 10px;
      // width: 70%;
      flex: 1;
      .content-box {
        padding: 5px;
        width: 24%;
      }
      .admin-left-content {
        justify-content: space-around;
        display: flex;
        width: 100%;
      }
      .demo-item {
        text-align: center;
      }
      .demo-title {
        font-size: 12px;
        padding: 5px 0;
        cursor: pointer;
        display: inline-block;
      }
      .demo-desc {
        margin: 5px;
        font-size: 12px;
        height: 24px;
        line-height: 24px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
    .admin-box-right {
      flex-basis: 200px;
      padding: 0 20px 0 20px;
      .admin-box-video {
        padding: 15px 15px 15px 0;
      }
      .video-title {
        padding: 5px 0;
        text-align: center;
        font-size: 12px;
      }
      .video-item {
        background: #f2f2f2;
        margin-bottom: 20px;
        cursor: pointer;
      }
      video {
        pointer-events: none
      }
    }
  }
  .admin-box-role {
    width: 30%;
    height: 100%;
    box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.349019607843137);
    background-color: rgba(255, 255, 255, 1);
    padding: 10px;
  }
  .admin-left-bottom {
    margin-top: 60px;
    .admin-box-set {
      padding: 10px;
      height: 100%;
      display: inline-block;
      border: 1px solid transparent;
      &:hover {
        border: 1px solid #eee;
        border-radius: 5px;
        color: #2d8cf0;
        cursor: pointer;
      }
    }
  }
  .admin-box-content {
    .admin-box-a {
      width: 108px;
      height: 91px;
      display: block;
      border: none;
      border-radius: 5px;
      text-align: center;
      padding: 13px;
      .admin-icon {
        font-size: 45px;
      }
      .fi-schedule {
        color: rgb(102, 102, 255);
      }
      .fi-scriptis {
        color: rgb(0, 153, 255);
      }
      .admin-box-text {
        margin-top: 5px;
        font-weight: 700;
        font-style: normal;
        padding-top: 5px;
      }
    }
  }
}
</style>
