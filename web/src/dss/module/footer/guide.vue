<template>
  <div>
    <div class="guide-mask" v-if="show" @click="flag && toggleGuide()"></div>
    <div class="guide-container" :class="{'guide-show': show}">
      <div class="guide-header">
        <span class="header-txt">帮助文档</span>
        <span class="header-close" @click="toggleGuide">
          <SvgIcon icon-class="close2" />
        </span>
      </div>
      <div class="guide-body">
        <div class="guide-box" v-if="guide.title || guide.desc || guide.steps">
          <div class="guide-box-title">{{guide.title}}</div>
          <div class="guide-box-desc">{{guide.desc}}</div>
          <div class="guide-steps">
            <div class="step" :class="{'step-expand': step.expand}" v-for="step in guide.steps" :key="step.seq || step.title">
              <div class="step-head" @click="toggleStep(step)">
                <span class="step-seq">{{step.seq}}</span>
                <span class="step-divider" v-if="step.seq"></span>
                <span class="step-title">{{step.title}}</span>
                <span class="step-arrow">
                  <SvgIcon icon-class="close" v-if="step.expand" />
                  <SvgIcon icon-class="open" v-else />
                </span>
              </div>
              <div class="step-content">
                <p v-for="content in step.content" :key="content.text || content.img">
                  <strong v-if="content.bold && content.text">{{content.text}}</strong>
                  <img v-else-if="content.img" :src="require(`./${content.img}`)" @click="showImgModal(require(`./${content.img}`))" />
                  <span v-else-if="content.text">{{content.text}}</span>
                </p>
              </div>
            </div>
          </div>
        </div>
        <div class="guide-box" v-if="guide.questions && guide.questions.length">
          <div class="guide-box-title">常见问题</div>
          <ul class="guide-questions">
            <li v-for="q in guide.questions" :key="q.title">
              <a :href="q.url" v-if="q.url">{{q.title}}</a>
              <a v-else>{{q.title}}</a>
            </li>
          </ul>
        </div>
      </div>
      <div class="guide-footer">
        <a target="_blank" href="https://saas.ctyun.cn/luban/doc/docs/mindoc">前往用户手册</a>
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
import eventbus from '@/common/helper/eventbus';
import { guideConfig } from './guideConfig.js';

export default {
  name: 'Guide',
  props: {
    show: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      guide: this.getGuideConfig(),
      selectedImg: '',
      modalImg: false,
      flag: true,
      tabModMap: {}
    };
  },
  watch: {
    '$route'(val) {
      this.guide = this.getGuideConfig();
    }
  },
  created() {
    // 开发中心和运维中心使用同一个route，所以使用eventbus来监听变化，从而触发产品即文档的更新
    eventbus.on('workflow.change', this.onWorkflowChange);
    // 工作流编辑-调度中心切换
    eventbus.on('workflow.orchestratorId', this.onWorkflowSchedulerChange);
  },
  beforeDestroy() {
    eventbus.off('workflow.change', this.onWorkflowChange);
    eventbus.off('workflow.orchestratorId', this.onWorkflowSchedulerChange);
  },
  methods: {
    onWorkflowChange(mod) {
      if (mod == 'scheduler') {
        this.guide = this.getGuideConfig('/workflow/scheduler')
      } else if (mod == 'dev') {
        this.guide = this.getGuideConfig();
      }
    },
    onWorkflowSchedulerChange(data) {
      if (data.mod == 'scheduler') {
        this.guide = this.getGuideConfig('/workflow/scheduler')
        this.tabModMap[data.orchestratorId] = data.mod;
      } else if (data.mod == 'dev') {
        this.guide = this.getGuideConfig();
        this.tabModMap[data.orchestratorId] = data.mod;
      } else if (data.mod == 'auto') {
        this.onWorkflowChange(this.tabModMap[data.orchestratorId] || 'dev'); // 默认dev
      }
    },
    getGuideConfig(key) {
      // 指定路径
      if (key && guideConfig[key]) {
        return guideConfig[key];
      }
      // 当前路径
      key = this.$route.path;
      if (guideConfig[key]) {
        return guideConfig[key];
      }
      // 父级以及路径
      key = this.$route.path.split('/').slice(0,2).join('/');
      if (guideConfig[key]) {
        return guideConfig[key];
      }
      // 公共
      return guideConfig['/common'];
    },
    toggleGuide() {
      this.$emit('on-toggle')
    },
    toggleStep(step) {
      this.guide = {
        ...this.guide,
        steps: this.guide.steps.map(i => {
          if (i.title == step.title) {
            return {
              ...i,
              expand: !i.expand
            }
          }
          return i;
        })
      }
    },
    showImgModal(img) {
      this.modalImg = true;
      this.selectedImg = img;
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
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
    transition: all .24s ease-in-out;
    transform: translateX(120%);
    @include guide-box-shadow(#DEE4EC, #192235);
    border-radius: 2px;
    .guide-header {
      position: relative;
      height: 48px;
      padding: 0 15px;
      @include bg-color(#F8F9FC, #313847);
      .header-txt {
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
        border-top: 1px solid #DEE4EC;
        @include border-color(#DEE4EC, #404A5D);
        padding: 20px 15px;
        .guide-box-title {
          padding-left: 10px;
          margin-bottom: 10px;
          border-left: 4px solid #2E92F7;
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
            @include bg-color(#F8F9FC, #383F4E);
            border: 1px solid #DEE4EC;
            @include border-color(#DEE4EC, #404A5D);
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
                @include bg-color(#979797, #4B576E);
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
              transition: opacity,transform .4s ease-out;
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
              border-bottom: 1px solid #DEE4EC;
              @include border-color(#DEE4EC, #404A5D);
            }
            .step-content {
              padding: 10px;
              height: auto;
              opacity: 1;
              transform: translateY(0);
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
                color: #2E92F7;
              }
            }
          }
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
      @include guide-box-shadow(#DEE4EC, #192235);

      border-radius: 2px;
      text-align: center;
      line-height: 48px;
      a {
        font-size: 14px;
        @include font-color(#333, $dark-workspace-title-color);
        text-decoration: none;
        &:hover {
          color: #2E92F7;
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
