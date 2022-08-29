<template>
  <div class="library-search">
    <template v-if="doc.list && doc.list.length">
      <div class="library-search-list" v-for="item in doc.list" :key="item.id" @click="showChapter(item)">
        <div class="search-item-title">{{ item.title }}</div>
        <div class="search-item-desc">{{ item.desc }}</div>
      </div>
      <div class="library-search-page">
        <Page :total="doc.total" show-total @on-change="changePage"></Page>
      </div>
    </template>
    <div class="library-search-empty" v-else>
      <SvgIcon icon-class="empty" width="160px" height="160px" />
      <div class="empty-tips">{{ $t('message.common.dss.notfound') }}</div>
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
  methods: {
    showChapter(item) {
      this.$emit("on-chapter-click", item)
    },
    changePage(page){
      this.$emit("on-page-change", page);
    },
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
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
  .library-search-page {
    margin: 12px 15px;
  }
  .library-search-empty {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 100px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    @include font-color(#ebebeb, #3f434c);
    .empty-tips {
      @include font-color(#333, $dark-text-color);
    }
  }
}
</style>
