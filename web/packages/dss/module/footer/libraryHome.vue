<template>
  <div class="library-home">
    <h2 class="library-home-title">{{ $t('message.common.dss.dir') }}</h2>
    <library-tree
      :nodes="nodes"
      @on-item-click="handleTreeClick"
    />
  </div>
</template>
<script>
import {
  GetCatalogTop,
  GetCatalogById,
  GetChapterDetail,
} from '@dataspherestudio/shared/common/service/apiGuide';
import lubanTree from '@dataspherestudio/shared/components/lubanTree';

export default {
  name: "libraryHome",
  components: {
    libraryTree: lubanTree.libraryTree,
  },
  props: {
    show: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      nodes: []
    };
  },
  mounted() {
    this.initTree();
  },
  methods: {
    initTree() {
      GetCatalogTop().then((data) => {
        this.nodes = (data.result || []).map(item => {
          return {
            ...item,
            id: item.id,
            title: item.title,
            isLeaf: false,
            // children: []
          }
        });
      });
    },
    handleTreeClick(node) {
      if (node.isLeaf) {
        GetChapterDetail(node.id).then((res) => {
          this.$emit("on-chapter-click", res.result)
        });
      } else {
        if (!node.opened && !node.loaded) {
          this.nodes = this.handleLoading(this.nodes, node)
          this.refreshTree(node.id);
        } else {
          this.nodes = this.handleToggle(this.nodes, node)
        }
      }
    },
    refreshTree(catalogId) {
      GetCatalogById(catalogId).then((data) => {
        const childrenCatalog = data.result ? (data.result.childrenCatalog || []).map(i => { return {...i, type: "catalog", isLeaf: false }}) : []
        const childrenChapter = data.result ? (data.result.childrenChapter || []).map(i => { return {...i, type: "chapter", isLeaf: true }}) : []
        const children = childrenCatalog.concat(childrenChapter);
        this.nodes = this.mergeTree(this.nodes, catalogId, children);
      });
    },
    mergeTree(nodes, catalogId, children) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (!nodes[i].isLeaf && nodes[i].id == catalogId) {
          item = {
            ...nodes[i],
            loaded: true,
            loading: false,
            opened: true,
            isLeaf: false,
            children: children
          }
          matched = true;
        } else if (!matched && !nodes[i].isLeaf && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.mergeTree(nodes[i].children, catalogId, children)
          }
        } else {
          item = nodes[i];
        }
        arr.push(item)
      }
      return arr;
    },
    handleToggle(nodes, node) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (!nodes[i].isLeaf && nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            opened: !nodes[i].opened
          }
          matched = true;
        } else if (!matched && !nodes[i].isLeaf && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.handleToggle(nodes[i].children, node)
          }
        } else {
          item = nodes[i];
        }
        arr.push(item)
      }
      return arr;
    },
    handleLoading(nodes, node) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (!nodes[i].isLeaf && nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            loading: true
          }
          matched = true;
        } else if (!matched && !nodes[i].isLeaf && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.handleLoading(nodes[i].children, node)
          }
        } else {
          item = nodes[i];
        }
        arr.push(item)
      }
      return arr;
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.library-home {
  margin: 12px 15px;
  .library-home-title {
    font-size: 16px;
    margin-bottom: 10px;
    @include font-color(#333, $dark-text-color);
  }
}
</style>
