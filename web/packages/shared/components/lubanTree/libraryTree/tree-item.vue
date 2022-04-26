<template>
  <div class="tree-item">
    <div
      v-show="!data.hidden"
      class="tree-content"
    >
      <div class="tree-icon" :class="{ leaf: data.isLeaf }" v-if="!data.loading">
        <SvgIcon
          v-if="data.opened"
          icon-class="open"
          @click="handleItemClick(data)"
        />
        <SvgIcon v-else icon-class="close" @click="handleItemClick(data)"/>
      </div>
      <div class="tree-loading" v-if="data.loading">
        <SvgIcon icon-class="loading" />
      </div>
      <div class="tree-name" :title="data.title" @click="handleItemClick(data)">
        {{ data.titleAlias || data.title }}
      </div>
    </div>
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <tree-item
        v-for="item in data.children"
        :key="item.treeId || item.id"
        :data="item"
        :on-item-click="onItemClick"
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
    }
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
  .tree-name {
    display: block;
    line-height: 32px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    &:hover {
      @include font-color(rgba(0,0,0,0.85), rgba(255,255,255,0.85));
    }
  }
  .tree-children {
    transition: max-height 0.3s;
    max-height: 0;
    overflow: hidden;
    .leaf {
      visibility: hidden;
      +.tree-name {
        &:hover {
          @include font-color($primary-color, $dark-primary-color);
        }
      }
    }
    .tree-item {
      padding-left: 20px;
    }
  }
}
</style>
