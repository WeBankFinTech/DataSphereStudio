<template>
  <div>
    <div class="guide-mask" v-if="show" @click="toggleGuide"></div>
    <div
      class="guide-container"
      ref="guideContainer"
      :class="{ 'guide-show': show }"
    >
      <div class="guide-header">
        <span class="header-txt">{{ $t('message.common.dss.dochelp') }}</span>
        <span class="header-close" @click="toggleGuide">
          <SvgIcon icon-class="close2" />
        </span>
      </div>

      <div class="guide-tabs">
        <div class="guide-tab" :class="{ 'guide-tab-active' : currentTab == 'guide'}" @click="changeTab('guide')">{{ $t('message.common.dss.learning') }}</div>
        <div class="guide-tab" :class="{ 'guide-tab-active' : currentTab == 'library'}" @click="changeTab('library')">{{ $t('message.common.dss.knowledge') }}</div>
      </div>

      <div class="guide-navbar" v-show="currentTab == 'library'">
        <div class="navbar-head">
          <span class="navbar-head-btn" :class="{'navbar-head-btn-disabled': isFirst}" @click="changeDocument('prev')">
            <SvgIcon icon-class="putaway" />
          </span>
          <span class="navbar-head-btn" :class="{'navbar-head-btn-disabled': isLast}" @click="changeDocument('next')">
            <SvgIcon icon-class="unfold" />
          </span>
          <div class="navbar-head-search">
            <div class="icon-prefix">
              <SvgIcon icon-class="search" />
            </div>
            <Input v-model="keyword" clearable @on-enter="changeToLibrarySearch" />
          </div>
        </div>
        <div class="navbar-breadcrumb" v-if="currentMode != 'home'">
          <span class="breadcrumb-home" @click="changeToLibraryHome">{{ $t('message.common.dss.homepage') }}</span>
          <span class="breadcrumb-divider"></span>
          <span class="breadcrumb-title">{{ currentMode == 'search' ? this.$t('message.common.dss.Search') : currentDoc.title }}</span>
        </div>
      </div>

      <div class="guide-body" v-show="currentTab == 'guide'">
        <div
          class="guide-box"
          v-if="guide.title || guide.description || (guide.steps && guide.steps.length)"
        >
          <div class="guide-box-title">{{ guide.title }}</div>
          <div class="guide-box-desc">{{ guide.description }}</div>
          <div class="guide-steps">
            <div class="step" :class="{'step-expand': step.expand}" v-for="step in guide.steps" :key="step.seq || step.title">
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
          <div class="guide-box-title">{{ $t('message.common.dss.faq') }}</div>
          <ul class="guide-questions">
            <li v-for="q in guide.questions" :key="q.title">
              <a @click="changeQuestionToLibraryDetail(q)">{{ q.title }}</a>
            </li>
          </ul>
        </div>
      </div>

      <div class="guide-body" :style="libraryStyle" v-show="currentTab == 'library'">
        <library-detail :doc="currentDoc" v-show="currentMode == 'detail'" @on-chapter-click="changeToLibraryDetail" />
        <library-search :doc="currentDoc" v-show="currentMode == 'search'" @on-chapter-click="changeToLibraryDetail" @on-page-change="changeSearchPage" />
        <library-home v-show="currentMode == 'home'" @on-chapter-click="changeToLibraryDetail" />
        <Spin size="large" fix v-if="loading"></Spin>
      </div>

      <div v-if="$APP_CONF.handbook" class="guide-footer">
        <a target="_blank" :href="$APP_CONF.handbook">
          {{ $t('message.common.dss.manual') }}
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
import { GetGuideByPath, QueryChapter } from '@dataspherestudio/shared/common/service/apiGuide';
import libraryHome from "./libraryHome.vue";
import librarySearch from "./librarySearch.vue";
import libraryDetail from "./libraryDetail.vue";

export default {
  name: "Guide",
  components: {
    libraryHome,
    librarySearch,
    libraryDetail
  },
  props: {
    show: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      guide: {},
      currentTab: "guide", // guide or library

      currentMode: "home", // library 展示的内容有3种模式，文档目录首页home，某一具体文档detail，搜索列表search
      history: [ { mode: "home", data: {} } ], // document history for prev or next
      currentIndex: 0, // history当前坐标
      currentDoc: {}, // history当前坐标的数据
      keyword: '', // document搜索
      pageSize: 10,
      loading: false,

      selectedImg: "",
      modalImg: false,
      tabModMap: {}, // 工作流可能同时打开多个，所以缓存起来
    };
  },
  computed: {
    libraryStyle() {
      if (this.currentMode == 'home') {
        // home模式没有面包屑40px
        return {
          height: "calc(100% - 144px)"
        }
      } else {
        return {
          height: "calc(100% - 184px)"
        }
      }
    },
    isFirst() {
      return this.currentIndex == 0
    },
    isLast() {
      return this.currentIndex == this.history.length - 1
    },
    lastHistory() {
      return this.history[this.history.length - 1]
    }
  },
  watch: {
    $route() {
      this.getGuideConfig();
    }
  },
  mounted() {
    this.getGuideConfig();
    this.init();
  },
  beforeDestroy() {
    this.dispose();
  },
  methods: {
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
    changeTab(tab) {
      this.currentTab = tab;
    },
    toggleGuide() {
      this.$emit("on-toggle");
      this.currentTab = "guide";
    },
    changeToLibraryHome() {
      this.currentMode = "home";
      // history队列a b c d e, 如果当前在c，此时有元素进入队列，那么d e会被remove
      this.history = this.history.slice(0, this.currentIndex + 1).concat({ mode: "home", data: {} });
      this.currentIndex = this.currentIndex + 1;
    },
    changeQuestionToLibraryDetail(chapter) {
      this.currentTab = "library";
      this.currentMode = "detail";
      this.currentDoc = chapter;
      // 点击学习tab的问题跳到知识库，然后再次点击同一问题，history不变，currentIndex不变；
      if (this.lastHistory.data.id == chapter.id && this.isLast && !this.isFirst) {
        // 内容相同且currentIndex是最后一个且不是第一个，无须重复展示
      } else {
        // history队列a b c d e, 如果当前在c，此时有元素进入队列，那么d e会被remove
        this.history = this.history.slice(0, this.currentIndex + 1).concat({ mode: "detail", data: chapter });
        this.currentIndex = this.currentIndex + 1;
      }
    },
    changeToLibraryDetail(chapter) {
      this.currentTab = "library";
      this.currentMode = "detail";
      this.currentDoc = chapter;
      // 点击某一文档内置链接，然后点返回，再次点击同一链接，此时currentIndex应该变化，history也最好更新；
      // history队列a b c d e, 如果当前在c，此时有元素进入队列，那么d e会被remove
      this.history = this.history.slice(0, this.currentIndex + 1).concat({ mode: "detail", data: chapter });
      this.currentIndex = this.currentIndex + 1;
    },
    changeToLibrarySearch() {
      if (!this.keyword || !this.keyword.trim()) {
        return;
      }
      if (this.lastHistory.mode == "search" && this.lastHistory.data.keyword == this.keyword.trim()) {
        // 最后一条历史记录是search且keyword没有变化，不处理
      } else {
        this.currentMode = "search";
        // history队列a b c d e, 如果当前在c，此时有元素进入队列，那么d e会被remove
        this.history = this.history.slice(0, this.currentIndex + 1).concat({ mode: "search", data: { keyword: this.keyword } });
        this.currentIndex = this.currentIndex + 1;
        this.loading = true;
        QueryChapter({keyword: this.keyword.trim(), pageNow: 1, pageSize: this.pageSize}).then((res) => {
          const data = this.formatSearchResult(res);
          this.currentDoc = data;
          this.loading = false;
          // 更新history
          this.history = this.history.map((item, index) => {
            if (index == this.currentIndex) {
              return {
                ...item,
                data: data
              }
            } else {
              return item
            }
          })
        });
      }
    },
    changeSearchPage(page) {
      this.loading = true;
      // search分页不更新history，只更新当前doc
      QueryChapter({keyword: this.keyword.trim(), pageNow: page, pageSize: this.pageSize}).then((res) => {
        const data = this.formatSearchResult(res);
        this.currentDoc = data;
        this.loading = false;
      });
    },
    formatSearchResult(res) {
      return {
        list: res.result.map(item => {
          return {
            ...item,
            id: item.id,
            title: item.title,
            desc: (item.contentHtml || "").replace(/<[^>]+>/gim, "").substr(0, 100), // 替换html标签
          }
        }),
        keyword: this.keyword.trim(),
        total: res.total
      };
    },
    changeDocument(type) {
      if (type == "prev" && this.isFirst) {
        return;
      }
      if (type == "next" && this.isLast) {
        return;
      }
      this.loading = false;
      if (type == "prev") {
        this.currentIndex = this.currentIndex - 1;
      } else if (type == "next") {
        this.currentIndex = this.currentIndex + 1;
      }
      const currentHistory = this.history[this.currentIndex];
      this.currentMode = currentHistory.mode;
      this.currentDoc = currentHistory.data;
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
@import "@dataspherestudio/shared/common/style/variables.scss";
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
  width: 650px;
  top: 54px;
  bottom: 0;
  @include bg-color(#fff, $dark-menu-base-color);
  transition: all 0.24s ease-in-out;
  transform: translateX(120%);
  @include guide-box-shadow(#dee4ec, #192235);
  border-radius: 2px;
  text-align:left;
  cursor: auto;
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
  .guide-tabs {
    height: 48px;
    display: flex;
    border-top: 1px solid #dee4ec;
    @include border-color(#dee4ec, #404a5d);
    .guide-tab {
      flex: 1;
      position: relative;
      font-size: 14px;
      line-height: 48px;
      cursor: pointer;
      text-align: center;
      @include font-color(#333, $dark-workspace-title-color);
      &:first-child {
        border-right: 1px solid #dee4ec;
        @include border-color(#dee4ec, #404a5d);
      }
    }
    .guide-tab-active {
      color: #2e92f7;
      &::after {
        content: "";
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 4px;
        background: #2e92f7;
      }
    }
  }
  .guide-navbar {
    .navbar-head {
      display: flex;
      align-items: center;
      height: 48px;
      padding-right: 16px;
      @include bg-color(#f8f9fc, #313847);
      border-top: 1px solid #dee4ec;
      border-bottom: 1px solid #dee4ec;
      @include border-color(#dee4ec, #404a5d);
      .navbar-head-btn {
        padding: 0 12px;
        cursor: pointer;
        font-size: 16px;
        height: 32px;
        line-height: 32px;
        @include font-color(#333, $dark-workspace-title-color);
      }
      .navbar-head-btn-disabled {
        @include font-color(#c0c6cc, #666);
        cursor: default;
      }
      .navbar-head-search {
        position: relative;
        flex: 1;
        height: 32px;
        line-height: 32px;
        .icon-prefix {
          z-index: 2;
          position: absolute;
          left: 10px;
          top: 0;
          font-size: 16px;
          @include font-color(#333, $dark-text-color);
        }
        /deep/.ivu-input {
          text-indent: 24px;
        }
      }
    }
    .navbar-breadcrumb {
      height: 40px;
      padding: 10px 15px;
      overflow: hidden;
      white-space: nowrap;
      .breadcrumb-home {
        display: inline-block;
        cursor: pointer;
        vertical-align: middle;
        @include font-color(#666, $dark-workspace-title-color);
        &:hover {
          color: #2e92f7;
        }
      }
      .breadcrumb-divider {
        display: inline-block;
        margin: 0 8px;
        vertical-align: middle;
        color: #999;
        &::after {
          content: "/";
        }
      }
      .breadcrumb-title {
        display: inline-block;
        vertical-align: middle;
        @include font-color(#999, $dark-text-color);
        cursor: default;
      }
    }
  }
  .guide-body {
    position: relative;
    height: calc(100% - 96px);
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
