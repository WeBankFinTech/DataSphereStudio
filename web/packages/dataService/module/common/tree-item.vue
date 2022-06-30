<template>
  <div class="tree-item">
    <div class="tree-content" :class="{ 'tree-content-active': currentTreeId == model.id }">
      <div class="tree-icon" :class="{ leaf: isLeaf }">
        <SvgIcon v-if="model.opened" icon-class="open" @click="handleItemToggle"/>
        <SvgIcon v-else icon-class="close" @click="handleItemToggle"/>
      </div>
      <div class="tree-icon" v-if="model.type == 'api'">
        <SvgIcon icon-class="api2" />
      </div>
      <div class="tree-loading" v-if="model.loading">
        <SvgIcon icon-class="loading" />
      </div>
      <div
        class="tree-name"
        @click="handleItemClick"
      >
        {{ model.name }}
      </div>
      <div
        class="tree-add"
        @click="handleAddClick"
        v-if="model.type == 'project' && model.canWrite"
      >
        <SvgIcon icon-class="plus" />
      </div>
      <div class="tree-hold" v-else></div>
    </div>
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <div class="tree-children-wrap">
        <tree-item
          v-for="item in model.children"
          :key="item.id"
          :data="item"
          :on-item-click="onItemClick"
          :currentTreeId="currentTreeId"
        />
      </div>
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
      default: () => false
    },
    onAddClick: {
      type: Function,
      default: () => false
    },
    onItemToggle: {
      type: Function,
      default: () => false
    },
    currentTreeId: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      model: this.data,
      maxHeight: 0
    };
  },
  computed: {
    isFolder() {
      return this.model.children && this.model.children.length;
    },
    isLeaf() {
      if (this.model.type == "flow" || this.model.type == "api") {
        return true;
      } else {
        return this.model.isLeaf;
      }
    },
    groupStyle() {
      return {
        "max-height": this.maxHeight + "px"
      };
    }
  },
  watch: {
    data(newValue) {
      this.model = newValue;
      this.handleGroupMaxHeight();
    }
  },
  methods: {
    handleAddClick() {
      this.onAddClick(this.model);
    },
    handleItemClick() {
      this.onItemClick(this.model);
    },
    handleItemToggle() {
      if (!this.isLeaf) {
        this.onItemToggle(this.model);
      }
    },
    handleGroupMaxHeight() {
      if (this.model.opened && this.model.children) {
        this.maxHeight = this.model.children.length * 32;
      } else {
        this.maxHeight = 0;
      }
    }
  }
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
      @include bg-color(#EDF1F6, $dark-active-menu-item);
      .tree-add {
        visibility: visible;
      }
    }
    &-active {
      @include bg-color(#EDF1F6, $dark-active-menu-item);
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
  .tree-add {
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
