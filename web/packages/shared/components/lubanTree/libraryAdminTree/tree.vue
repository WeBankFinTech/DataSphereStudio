<template>
  <div>
    <tree-item
      v-for="item in data"
      :key="item.id"
      :data="item"
      :currentTreeId="currentTreeId"
      :on-item-click="handleItemClick"
      :on-add-click="handleAddClick"
      :on-delete-click="handleDeleteClick"
      :on-update-click="handleUpdateClick"
    />
  </div>
</template>
<script>
import TreeItem from "./tree-item.vue";

export default {
  name: "libraryAdminTree",
  components: {
    TreeItem,
  },
  props: {
    nodes: {
      type: Array,
      default: () => [],
    },
    currentTreeId: {
      type: String,
      default: "",
    },
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
    handleAddClick(node) {
      this.$emit("on-add-click", node);
    },
    handleDeleteClick(node) {
      this.$emit("on-delete-click", node);
    },
    handleUpdateClick(node) {
      this.$emit("on-update-click", node);
    },
    handleItemClick(node) {
      this.$emit("on-item-click", node);
    }
  },
};
</script>
