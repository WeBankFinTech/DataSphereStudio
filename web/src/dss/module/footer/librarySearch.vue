<template>
  <div class="library-search">
    <div class="library-search-list" v-for="item in doc.list" :key="item.id">
      <div class="search-item-title">{{ item.title }}</div>
      <div class="search-item-desc">{{ item.desc }}</div>
    </div>
  </div>
</template>
<script>
export default {
  name: "librarySearch",
  props: {
    doc: {
      type: Object,
      default: () => {},
    }
  },
  mounted() {
  },
  methods: {
    changeToAnswer(question) {
      this.currentTab = "library";
      this.currentMode = "detail";
      this.currentPaper = question;
    },
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.library-search {
  .library-search-list {
    margin: 12px 15px;
    padding-bottom: 12px;
    font-size: 12px;
    cursor: pointer;
    border-bottom: 1px solid #dee4ec;
    @include border-color(#dee4ec, #404a5d);
    .search-item-title {
      @include font-color(#333, $dark-text-color);
      font-size: 14px;
      margin-bottom: 10px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      &:hover {
        color: #2e92f7;
      }
    }
    .search-item-desc {
      color: #999;
      &:hover {
        color: #666;
      }
    }
  }
}
</style>
