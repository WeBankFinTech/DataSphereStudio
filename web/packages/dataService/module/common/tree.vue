<template>
  <div>
    <tree-item v-for="item in data" :key="item.id" :data="item"
      :currentTreeId="currentTreeId"
      :on-item-toggle="handleItemToggle"
      :on-item-click="handleItemClick"
      :on-add-click="handleAddClick"
    />
  </div>
</template>
<script>
import TreeItem from './tree-item.vue'

export default {
  name: "WorkflowTree",
  components: {
    TreeItem
  },
  props: {
    nodes: {
      type: Array,
      default: () => []
    },
    load: {
      type: Function, default: () => false
    },
    currentTreeId: {
      type: Number, default: 0
    }
  },
  data() {
    return {
      data: this.nodes,
    };
  },
  watch: {
    nodes (newValue) {
      this.data = newValue
    },
  },
  mounted() {
    // 默认展开当前project
    this.initCurrentProject();
  },
  methods: {
    initCurrentProject() {
      const node = {
        id: this.$route.query.projectID,
        name: this.$route.query.projectName
      }
      this.handleLoading(node);
      this.handleAsyncLoad(node);
    },
    handleAddClick(project) {
      this.$emit('on-add-click', project)
    },
    handleItemClick(node) {
      this.$emit('on-item-click', node)
    },
    handleItemToggle(node) {
      if (!node.opened && !node.loaded) {
        this.handleLoading(node)
        this.handleAsyncLoad(node)
      } else {
        this.handleToggle(node)
        this.$emit('on-sync-tree', this.data)
      }
    },
    handleToggle(node) {
      this.data = this.data.map(item => {
        if (item.id == node.id) {
          return {
            ...item,
            opened: !node.opened
          }
        } else {
          return item
        }
      });
    },
    handleLoading(node) {
      this.data = this.data.map(item => {
        if (item.id == node.id) {
          return {
            ...item,
            loading: true
          }
        } else {
          return item
        }
      });
    },
    handleAsyncLoad(node) {
      if (this.load) {
        this.load(node, (res) => {
          this.data = this.data.map(item => {
            if (item.id == node.id) {
              return {
                ...item,
                loaded: true,
                loading: false,
                opened: true,
                isLeaf: res.length ? false : true,
                children: res.length ? res : []
              }
            } else {
              return item
            }
          });
          this.$emit('on-sync-tree', this.data)
        })
      }
    },
  }
};
</script>
