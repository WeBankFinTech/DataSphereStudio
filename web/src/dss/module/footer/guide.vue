<template>
  <div>
    <div class="guide-mask" v-if="show" @click="toggleGuide"></div>
    <div
      class="guide-container"
      ref="guideContainer"
      :class="{ 'guide-show': show }"
    >
      <div class="guide-header">
        <span
          class="header-back"
          @click="changeToGuide"
          v-show="currentTab == 'library'"
        >
          <SvgIcon icon-class="putaway" />
        </span>
        <span class="header-txt">帮助文档</span>
        <span class="header-close" @click="toggleGuide">
          <SvgIcon icon-class="close2" />
        </span>
      </div>
      <div class="guide-body" v-show="currentTab == 'guide'">
        <div
          class="guide-box"
          v-if="guide.title || guide.description || guide.steps"
        >
          <div class="guide-box-title">{{ guide.title }}</div>
          <div class="guide-box-desc">{{ guide.description }}</div>
          <div class="guide-steps">
            <div
              class="step"
              :class="{ 'step-expand': step.expand }"
              v-for="step in guide.steps"
              :key="step.title"
            >
              <div class="step-head" @click="toggleStep(step)">
                <span class="step-seq">{{ step.seq }}</span>
                <span class="step-divider" v-if="step.seq"></span>
                <span class="step-title">{{ step.title }}</span>
                <span class="step-arrow">
                  <SvgIcon icon-class="close" v-if="step.expand" />
                  <SvgIcon icon-class="open" v-else />
                </span>
              </div>
              <div class="step-content">
                <p v-html="step.contentHtml"></p>
              </div>
            </div>
          </div>
        </div>
        <div class="guide-box" v-if="guide.questions && guide.questions.length">
          <div class="guide-box-title">常见问题</div>
          <ul class="guide-questions">
            <li v-for="q in guide.questions" :key="q.title">
              <a @click="changeToAnswer(q)">{{ q.title }}</a>
            </li>
          </ul>
        </div>
      </div>

      <div class="guide-body" v-show="currentTab == 'library'">
        <div class="paper-title">{{ currentPaper.title }}</div>
        <div class="paper-content">
          <p v-html="currentPaper.contentHtml"></p>
        </div>
      </div>

      <div class="guide-footer">
        <a target="_blank" href="https://saas.ctyun.cn/luban/doc/docs/mindoc">
          前往用户手册
        </a>
      </div>
    </div>

    <Modal v-model="modalImg" :closable="false" :footer-hide="true" width="60%">
      <div class="modal-img-container">
        <img :src="selectedImg" />
      </div>
      <div slot="footer"></div>
    </Modal>
  </div>
</template>
<script>
import eventbus from "@/common/helper/eventbus";
import { GetGuideByPath } from "@/common/service/apiGuide";

export default {
  name: "Guide",
  props: {
    show: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      guide: {},
      currentPaper: {},
      currentTab: "guide", // guide or library
      selectedImg: "",
      modalImg: false,
      tabModMap: {}, // 工作流可能同时打开多个，所以缓存起来
    };
  },
  watch: {
    $route(val) {
      this.getGuideConfig();
    },
  },
  mounted() {
    this.getGuideConfig();
    this.init();
    // 工作流编辑-调度中心切换
    eventbus.on("workflow.orchestratorId", this.onWorkflowSchedulerChange);
  },
  beforeDestroy() {
    this.dispose();
    eventbus.off("workflow.orchestratorId", this.onWorkflowSchedulerChange);
  },
  methods: {
    onWorkflowSchedulerChange(data) {
      if (data.mod == "scheduler") {
        this.getGuideConfig("/workflow/scheduler");
        this.tabModMap[data.orchestratorId] = data.mod;
      } else if (data.mod == "dev") {
        this.getGuideConfig();
        this.tabModMap[data.orchestratorId] = data.mod;
      } else if (data.mod == "auto") {
        if (this.tabModMap[data.orchestratorId] == "scheduler") {
          this.getGuideConfig("/workflow/scheduler");
        } else {
          this.getGuideConfig(); // 默认dev
        }
      }
    },
    getGuideConfig(key) {
      // reset guide
      this.guide = {};
      // path传参优先级
      // 1. 指定路径
      // 2. 当前路径
      // 3. 父级以及路径，适用于某些二级子页面共享一个父级文档
      // 4. 降级公共 /common
      const pathPriority = [];
      const parentPath = this.$route.path.split("/").slice(0, 2).join("/");
      key && pathPriority.push(key); // 指定路径
      pathPriority.push(this.$route.path); // 当前路径
      parentPath != this.$route.path && pathPriority.push(parentPath); // 父级以及路径
      pathPriority.push("/common"); // 公共

      this.loadGuide(pathPriority);
    },
    loadGuide(pathPriority) {
      if (pathPriority && pathPriority.length) {
        const path = pathPriority.shift(); // 从第一个开始
        GetGuideByPath({ path: path }).then((data) => {
          if (data.result) {
            this.guide = {
              title: data.result.title,
              description: data.result.description,
              steps: data.result.children.filter((i) => i.type == 1),
              questions: data.result.children.filter((i) => i.type == 2),
            };
          } else {
            // 第一优先级path没有找到，继续按第二优先级查找
            this.loadGuide(pathPriority);
          }
        });
      }
    },
    changeToAnswer(question) {
      this.currentTab = "library";
      this.currentPaper = question;
    },
    changeToGuide() {
      this.currentTab = "guide";
      this.currentPaper = {};
    },
    toggleGuide() {
      this.$emit("on-toggle");
      this.currentTab = "guide";
      this.currentPaper = {};
    },
    toggleStep(step) {
      this.guide = {
        ...this.guide,
        steps: this.guide.steps.map((i) => {
          if (i.title == step.title) {
            return {
              ...i,
              expand: !i.expand,
            };
          }
          return i;
        }),
      };
    },
    init() {
      const guideContainer = this.$refs.guideContainer;
      guideContainer.addEventListener("click", this.showImgModal);
    },
    dispose() {
      const guideContainer = this.$refs.guideContainer;
      guideContainer.removeEventListener("click", this.showImgModal);
    },
    showImgModal(e) {
      if (e.target.tagName == "IMG" && e.target.src) {
        this.modalImg = true;
        this.selectedImg = e.target.src;
      }
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.guide-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
.guide-container {
  position: fixed;
  right: 0;
  width: 400px;
  top: 54px;
  bottom: 0;
  @include bg-color(#fff, $dark-menu-base-color);
  transition: all 0.24s ease-in-out;
  transform: translateX(120%);
  @include guide-box-shadow(#dee4ec, #192235);
  border-radius: 2px;
  .guide-header {
    position: relative;
    height: 48px;
    padding: 0 15px;
    @include bg-color(#f8f9fc, #313847);
    .header-txt {
      font-size: 16px;
      line-height: 48px;
      @include font-color(#333, $dark-workspace-title-color);
    }
    .header-back {
      margin-right: 10px;
      cursor: pointer;
      font-size: 16px;
      line-height: 48px;
      @include font-color(#333, $dark-workspace-title-color);
    }
    .header-close {
      position: absolute;
      right: 15px;
      top: 0;
      bottom: 0;
      width: 48px;
      line-height: 48px;
      cursor: pointer;
      text-align: center;
      @include font-color(#333, $dark-workspace-title-color);
    }
  }
  .guide-body {
    height: calc(100% - 48px);
    padding-bottom: 48px;
    overflow-x: hidden;
    overflow-y: scroll;
    overflow-y: overlay;
    @include bg-color(#fff, $dark-menu-base-color);
    .guide-box {
      border-top: 1px solid #dee4ec;
      @include border-color(#dee4ec, #404a5d);
      padding: 20px 15px;
      .guide-box-title {
        padding-left: 10px;
        margin-bottom: 10px;
        border-left: 4px solid #2e92f7;
        font-size: 14px;
        line-height: 20px;
        @include font-color(#333, $dark-workspace-title-color);
        font-weight: bold;
      }
      .guide-box-desc {
        margin: 10px 0;
        font-size: 14px;
        @include font-color(#666, $dark-text-color);
        line-height: 20px;
      }
      .guide-steps {
        .step {
          margin-top: 10px;
          @include bg-color(#f8f9fc, #383f4e);
          border: 1px solid #dee4ec;
          @include border-color(#dee4ec, #404a5d);
          border-radius: 4px;
          .step-head {
            display: flex;
            align-items: center;
            @include font-color(#333, $dark-workspace-title-color);
            cursor: pointer;
            &:hover {
              color: #2d8cf0;
            }
            .step-seq {
              display: block;
              padding-left: 10px;
              font-size: 14px;
              font-weight: 500;
            }
            .step-divider {
              display: block;
              margin: 0 20px;
              width: 1px;
              height: 16px;
              @include bg-color(#979797, #4b576e);
            }
            .step-title {
              display: block;
              flex: 1;
              font-size: 14px;
              font-weight: 500;
            }
            .step-arrow {
              display: block;
              padding: 10px;
            }
          }
          .step-content {
            overflow: hidden;
            height: 0;
            opacity: 0;
            transform: translateY(-10px);
            transition: opacity, transform 0.4s ease-out;
            p {
              margin: 6px 0;
              font-size: 14px;
              line-height: 20px;
              @include font-color(#333, $dark-text-color);
            }
            img {
              max-width: 100%;
              cursor: zoom-in;
            }
          }
        }
        .step-expand {
          .step-head {
            border-bottom: 1px solid #dee4ec;
            @include border-color(#dee4ec, #404a5d);
          }
          .step-content {
            padding: 10px;
            height: auto;
            opacity: 1;
            transform: translateY(0);
            /deep/img {
              display: block;
              border: 0;
              max-width: 100%;
              cursor: pointer;
            }
          }
        }
      }
      .guide-questions {
        list-style: none;
        li {
          position: relative;
          margin: 10px 0;
          padding-left: 15px;
          a {
            font-size: 14px;
            line-height: 20px;
            @include font-color(#333, $dark-text-color);
            text-decoration: none;
            &:hover {
              color: #2e92f7;
            }
          }
        }
      }
    }
    .paper-title {
      margin: 20px 15px;
      font-size: 20px;
      @include font-color(#333, $dark-workspace-title-color);
    }
    .paper-content {
      padding: 0 15px 15px;
      @include font-color(#666, $dark-text-color);
      /deep/img {
        display: block;
        border: 0;
        max-width: 100%;
        cursor: pointer;
      }
    }
  }
  .guide-footer {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 48px;
    @include bg-color(#fff, #313847);
    @include guide-box-shadow(#dee4ec, #192235);

    border-radius: 2px;
    text-align: center;
    line-height: 48px;
    a {
      font-size: 14px;
      @include font-color(#333, $dark-workspace-title-color);
      text-decoration: none;
      &:hover {
        color: #2e92f7;
      }
    }
  }
}
.guide-show {
  transform: translateX(0);
}
.modal-img-container {
  text-align: center;
  img {
    max-width: 100%;
  }
}
</style>
