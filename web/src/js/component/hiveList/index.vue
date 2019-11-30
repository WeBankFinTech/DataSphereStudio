<template>
  <div ref="hiveRoot">
    <we-hive-list
      :data="data"
      :title="title"/>
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import { debounce, find } from 'lodash';
import weHiveList from './list.vue';
export default {
  name: 'WeHive',
  components: {
    weHiveList,
  },
  props: {
    data: Array,
    title: {
      type: String,
      default: '',
    },
    filterText: {
      type: String,
      default: '',
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    filterText(val) {
      this.onDidChange();
    },
  },
  created() {
    this.isRoot = true;
    this.onDidChange = debounce(this.filter, 500);
    this.onDidChange();
  },
  methods: {
    filter() {
      const recursion = function(node, text, isCaseSensitive) {
        node.forEach((child) => {
          child.isVisible = true;
          if (child.children && child.children.length && child.dataType === 'db') {
            recursion(child.children, text, false);
          }
          // 获取数据库中是否有符合搜索条件的表，有的话得显示库名
          const hasVisible = find(child.children, (item) => {
            return item.isVisible;
          });
          let name = child.name;
          let searchText = text;
          // 是否对大小写敏感
          if (!isCaseSensitive) {
            name = child.name.toLowerCase();
            searchText = text.toLowerCase();
          }
          if (name.indexOf(searchText) === -1 && !hasVisible) {
            child.isVisible = false;
          }
        });
      };
      recursion(this.data, this.filterText, false);
    },
  },
};
</script>
