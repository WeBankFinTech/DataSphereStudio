<template>
  <div>
    <tree-item
      v-for="item in data"
      :key="item.id"
      :data="item"
      :on-item-click="handleItemClick"
    />
  </div>
</template>
<script>
import TreeItem from "./tree-item.vue";

export default {
  name: "libraryTree",
  components: {
    TreeItem,
  },
  props: {
    nodes: {
      type: Array,
      default: () => [],
    }
  },
  data() {
    return {
      data: this.nodes,
    };
  },
  watch: {
    nodes(newValue) {
      this.data = newValue;
    },
  },
  methods: {
    handleItemClick(node) {
      this.$emit("on-item-click", node);
    }
  },
};
</script>
