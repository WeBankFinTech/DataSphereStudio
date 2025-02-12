<template>
  <div class="we-file-navbar">
    <Input
      class="searchbox"
      v-model="searchText"
      clearable
      @on-enter="onSearch"
      @on-clear="onSearch"
      :placeholder="placeholder"
      :style="{'width': getWidth()}"
    />
    <div
      class="we-file-navbar-nav">
      <Icon
        v-if="nav.isShowNav('export')"
        :size="16"
        type="ios-share-outline"
        :title="$t('message.common.navBar.dataStudio.outTable')"
        class="navbar-cursor"
        @click="exportFrom"/>
      <Icon
        v-if="nav.isShowNav('mytable')"
        :type="all ? 'ios-person-outline' : 'ios-people-outline'"
        class="navbar-cursor"
        :size="18"
        :title="all ? $t('message.common.navBar.dataStudio.mytable') : $t('message.common.navBar.dataStudio.alltable')"
        @click="filterMyTable"/>
      <Icon
        v-if="nav.isShowNav('import')"
        :size="20"
        type="ios-archive-outline"
        :title="$t('message.common.navBar.dataStudio.importHive')"
        class="navbar-cursor"
        @click="importTo"/>
      <Icon
        v-if="nav.isShowNav('newFile')"
        :size="16"
        type="ios-add-circle-outline"
        :title="addTitle"
        class="navbar-cursor"
        @click="addFile"/>
      <Icon
        v-if="nav.isShowNav('refresh')"
        :size="20"
        type="ios-refresh"
        :title="$t('message.common.refresh')"
        class="navbar-cursor"
        @click="refresh"/>
    </div>
  </div>
</template>
<script>
import { debounce } from 'lodash';
import Nav from './nav.js';
export default {
  props: {
    navList: {
      type: Array,
      require: true,
    },
    placeholder: {
      type: String,
      default () {
        return this.$t('message.common.navBar.dataStudio.searchPlaceholder')
      }
    },
    addTitle: {
      type: String,
      default () {
        return this.$t('message.common.navBar.dataStudio.addTitle')
      }
    },
  },
  data() {
    return {
      searchText: '',
      all: true
    };
  },
  watch: {
    searchText: debounce(function(value) {
      this.$emit('text-change', value);
    }, 350)
  },
  computed:{
    nav(){
      return new Nav({
        navList: this.navList,
      })
    }
  },
  methods: {
    showSearch() {
      this.$nextTick(() => {
        this.$refs.searchbox.onfocus();
      });
    },
    onSearch() {
      this.$emit('text-change', this.searchText.trim());
    },
    refresh() {
      this.$emit('on-refresh');
    },
    addFile() {
      this.$emit('on-add');
    },
    importTo() {
      this.$emit('on-import');
    },
    exportFrom() {
      this.$emit('on-export');
    },
    filterMyTable() {
      this.all = !this.all
      this.$emit('on-change-table-owner', this.all);
    },
    getWidth() {
      const len = this.navList.length;
      return `calc(100% - ${(len -1)*24}px)`;
    },
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
<style lang="scss" scoped>
  .searchbox{
   flex: 1;
   border-bottom: 1px solid #eee;
   ::v-deep i.ivu-input-icon{
     color: #808695;
     font-weight: normal;
   }
   ::v-deep .ivu-input{
     border: none;
   }
   ::v-deep .ivu-input:focus{
     box-shadow: none;
   }
  }
</style>
