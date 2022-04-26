<template>
  <div class="tree-item">
    <div
      v-show="!data.hidden"
      class="tree-content"
      :class="{ 'tree-content-active': currentTreeId == data.treeId }"
    >
      <div class="tree-icon" :class="{ leaf: isLeaf }">
        <SvgIcon
          v-if="data.opened"
          icon-class="open"
          @click="handleItemToggle(data)"
        />
        <SvgIcon v-else icon-class="close" @click="handleItemToggle(data)" />
      </div>
      <div class="tree-icon" v-if="data.type == 'step'">
        <SvgIcon icon-class="guide-step" />
      </div>
      <div class="tree-icon" v-if="data.type == 'question'">
        <SvgIcon icon-class="guide-question" />
      </div>
      <div class="tree-name" :title="data.title" @click="handleItemClick(data)">
        {{ data.titleAlias || data.title }}
      </div>
      <div class="tree-op" @click="handleAddClick(data)" v-if="data.canAdd">
        <SvgIcon icon-class="plus" />
      </div>
      <div
        class="tree-op"
        @click="handleDeleteClick(data)"
        v-if="data.canDelete"
      >
        <SvgIcon icon-class="delete" />
      </div>
      <div
        class="tree-op"
        @click="handleUpdateClick(data)"
        v-if="data.canUpdate"
      >
        <SvgIcon icon-class="update" />
      </div>
    </div>
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <tree-item
        v-for="item in data.children"
        :key="item.treeId"
        :data="item"
        :currentTreeId="currentTreeId"
        :on-item-toggle="onItemToggle"
        :on-item-click="onItemClick"
        :on-add-click="onAddClick"
        :on-delete-click="onDeleteClick"
        :on-update-click="onUpdateClick"
      />
    </ul>
  </div>
</template>
<script>
export default {
  name: "TreeItem",
  props: {
    data: { type: Object, required: true },
    onItemClick: {
      type: Function,
      default: () => false,
    },
    onAddClick: {
      type: Function,
      default: () => false,
    },
    onDeleteClick: {
      type: Function,
      default: () => false,
    },
    onUpdateClick: {
      type: Function,
      default: () => false,
    },
    onItemToggle: {
      type: Function,
      default: () => false,
    },
    currentTreeId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      maxHeight: 0,
    };
  },
  computed: {
    isFolder() {
      return this.data.children && this.data.children.length;
    },
    isLeaf() {
      if (this.data.type == "question" || this.data.type == "step") {
        return true;
      } else {
        return this.data.isLeaf;
      }
    },
    groupStyle() {
      return {
        "max-height": this.maxHeight + "px",
      };
    },
  },
  watch: {
    data() {
      this.handleGroupMaxHeight();
    },
  },
  methods: {
    handleAddClick(node) {
      this.onAddClick(node);
    },
    handleDeleteClick(node) {
      this.onDeleteClick(node);
    },
    handleUpdateClick(node) {
      this.onUpdateClick(node);
    },
    handleItemClick(node) {
      this.onItemClick(node);
    },
    handleItemToggle(node) {
      if (!this.isLeaf) {
        this.onItemToggle(node);
      }
    },
    handleGroupMaxHeight() {
      if (this.data.opened && this.data.children) {
        this.maxHeight = this.data.children.length * 32;
      } else {
        this.maxHeight = 0;
      }
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.tree-item {
  white-space: nowrap;
  outline: none;
  .tree-content {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 10px;
    @include font-color($light-text-color, $dark-text-color);
    &:hover {
      @include bg-color(#edf1f6, $dark-active-menu-item);
      .tree-op {
        visibility: visible;
      }
    }
    &-active {
      @include bg-color(#edf1f6, $dark-active-menu-item);
      @include font-color($primary-color, $dark-primary-color);
    }
  }
  .leaf {
    color: transparent;
  }
  .tree-icon {
    display: block;
    margin-right: 4px;
  }
  .tree-op {
    margin-left: 5px;
    display: block;
    visibility: hidden;
    font-size: 16px;
    @include font-color($light-text-color, $dark-text-color);
  }
  .tree-hold {
    display: block;
    width: 20px;
    height: 20px;
  }
  .tree-name {
    display: block;
    flex: 1;
    line-height: 32px;
    padding: 0 6px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .tree-children {
    transition: max-height 0.3s;
    max-height: 0;
    overflow: hidden;
    .leaf {
      display: none;
    }
    .tree-content {
      padding-left: 36px;
    }
  }
}
</style>
