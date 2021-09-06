<template>
  <div class="tree-item">
    <div
      :class="{
        'tree-content': true,
        'tree-content-leaf': isLeaf,
        'tree-content-node': !isLeaf
      }"
    >
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
        :class="{ 'tree-name-active': currentTreeId == model.id }"
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
    handleAddClick(e) {
      this.onAddClick(this.model);
    },
    handleItemClick(e) {
      this.onItemClick(this.model);
    },
    handleItemToggle(e) {
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
@import "@/common/style/variables.scss";
.tree-item {
  white-space: nowrap;
  outline: none;
  @include font-color($light-text-color, $dark-text-color);
  overflow: hidden;
  .tree-content {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 10px;
    &:hover {
      // background-color: #EDF1F6;
      @include bg-color(#edf1f6, $dark-active-menu-item);
    }
  }
  .tree-content-leaf {
    padding-left: 26px;
  }
  .tree-content-node {
    width: 280px;
    transition: width 0.1s;
    &:hover {
      width: 249px;
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
  .tree-name-active {
    // background-color: #EDF1F6;
    @include bg-color(#edf1f6, $dark-active-menu-item);
    // color: rgb(45, 140, 240);
    @include font-color($primary-color, $dark-primary-color);
  }
  .tree-children {
    margin-left: 0px;
    transition: max-height 0.3s;
    max-height: 0;
    overflow: hidden;
    .leaf {
      display: none;
    }
  }
}
</style>
