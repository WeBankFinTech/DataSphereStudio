<template>
  <div class="tree-item">
    <div
      v-show="!data.hidden"
      class="tree-content"
      :class="{ 'tree-content-active': currentTreeId == data.treeId }"
    >
      <div class="tree-icon" :class="{ leaf: isLeaf }" v-if="!data.loading">
        <SvgIcon
          v-if="data.opened"
          icon-class="open"
          @click="handleItemClick(data)"
        />
        <SvgIcon v-else icon-class="close" @click="handleItemClick(data)" />
      </div>
      <div class="tree-loading" v-if="data.loading">
        <SvgIcon icon-class="loading" />
      </div>
      <div class="tree-name" :title="data.title" @click="handleItemClick(data)">
        {{ data.titleAlias || data.title }}
      </div>
      <div class="tree-op" @click="handleAddClick(data)" v-if="data.canAdd && !isLeaf">
        <SvgIcon icon-class="plus" />
      </div>
      <div class="tree-op" @click="handleDeleteClick(data)" v-if="data.canDelete">
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
    currentTreeId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      maxHeight: "max-content",
    };
  },
  computed: {
    isFolder() {
      return this.data.children && this.data.children.length;
    },
    isLeaf() {
      if (this.data.type == "chapter") {
        return true;
      } else {
        return this.data.isLeaf;
      }
    },
    groupStyle() {
      return {
        "max-height": this.maxHeight,
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
    handleGroupMaxHeight() {
      if (this.data.opened && this.data.children) {
        this.maxHeight = "max-content";
      } else {
        this.maxHeight = "0px";
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
    @include font-color($light-text-color, $dark-text-color);
    &:hover {
      @include font-color($primary-color, $dark-primary-color);
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
  .tree-loading {
    display: block;
    margin-right: 4px;
    animation: ani-demo-spin 1s linear infinite;
  }
  @keyframes ani-demo-spin {
    from {
      transform: rotate(0deg);
    }
    50% {
      transform: rotate(180deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
  .tree-op {
    margin-right: 5px;
    display: block;
    visibility: hidden;
    font-size: 16px;
    @include font-color($light-text-color, $dark-text-color);
  }
  .tree-name {
    display: block;
    flex: 1;
    line-height: 32px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .tree-children {
    transition: max-height 0.3s;
    max-height: 0;
    overflow: hidden;
    .leaf {
      visibility: hidden;
    }
    .tree-item {
      padding-left: 20px;
    }
  }
}
</style>
