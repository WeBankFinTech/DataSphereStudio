<template>
  <div class="library-home">
    <h2 class="library-home-title">文档目录</h2>
    <library-tree
      :nodes="nodes"
      @on-item-click="handleTreeClick"
    />
  </div>
</template>
<script>
import lubanTree from "@/components/lubanTree";
import util from "@/common/util";

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
    this.nodes = [
      {
        id: 1,
        title: '工作空间首页',
        isLeaf: false,
        children: []
      },
      {
        id: 2,
        title: '应用商店',
        isLeaf: false,
        children: []
      }
    ]
  },
  methods: {
    handleTreeClick(node) {
      if (node.isLeaf) {
        console.log('isleaf', node)
      } else {
        if (!node.opened && !node.loaded) {
          this.nodes = this.handleLoading(this.nodes, node)
          setTimeout(() => {
            this.nodes = this.mockTree(this.nodes, node);
            console.log(this.nodes, node)
          }, 200)
        } else {
          console.log('toggle')
          this.nodes = this.handleToggle(this.nodes, node)
        }
      }
    },
    mockTree(nodes, node) {
      const arr = [];
      let matched = false;
      for (let i=0,len=nodes.length; i<len; i++) {
        let item = {};
        if (nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            loaded: true,
            loading: false,
            opened: true,
            isLeaf: false,
            children: [
              {
                id: util.guid(),
                title: '子节点',
                isLeaf: true,
              },
              {
                id: util.guid(),
                title: '子节点2',
                isLeaf: true,
              },
              {
                id: util.guid(),
                title: '子节点3',
                isLeaf: false,
              }
            ]
          }
          matched = true;
        } else if (!matched && nodes[i].children && nodes[i].children.length) {
          item = {
            ...nodes[i],
            children: this.mockTree(nodes[i].children, node)
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
        if (nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            opened: !nodes[i].opened
          }
          matched = true;
        } else if (!matched && nodes[i].children && nodes[i].children.length) {
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
        if (nodes[i].id == node.id) {
          item = {
            ...nodes[i],
            loading: true
          }
          matched = true;
        } else if (!matched && nodes[i].children && nodes[i].children.length) {
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
@import "@/common/style/variables.scss";
.library-home {
  margin: 12px 15px;
  .library-home-title {
    font-size: 16px;
    margin-bottom: 10px;
    @include font-color(#333, $dark-text-color);
  }
}
</style>
