<template>
  <div class="tree-item">
    <!-- 父树级别 -->
    <div
      class="tree-content"
      v-if="!model._id"
      :class="{ 'tree-content-active': currentTreeId == model.id }"
    >
      <div class="tree-open" v-if="model.type == 'component'">
        <SvgIcon
          icon-class="open"
          v-if="model.opened"
          @click="handleItemToggle(model.opened)"
        />
        <SvgIcon
          icon-class="close"
          v-else
          @click="handleItemToggle(model.opened)"
        />
      </div>
      <div class="tree-name" @click="handleItemClick">{{ model.name }}</div>
      <div
        class="tree-add"
        @click="handleAddClick"
        v-if="model.type == 'component'"
        :class="{ 'tree-add-active': currentTreeId == model.id }"
      >
        <SvgIcon icon-class="plus" />
      </div>
      <div class="tree-hold" v-else></div>
    </div>
    <div
      class="tree-content"
      v-else
      :class="{ 'tree-content-active': currentTreeId == model._id }"
    >
      <div class="tree-loading" v-if="model._id > 15">
        <SvgIcon icon-class="componentImport" />
      </div>
      <div class="tree-name" @click="handleItemClick">{{ model.name }}</div>
    </div>
    <!-- 子树级别 -->
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <tree-item
        v-for="item in model.children"
        :key="item.id"
        :sonTree="item"
        :on-item-click="onItemClick"
        :currentTreeId="currentTreeId"
      />
    </ul>
  </div>
</template>
<script>
export default {
  name: "TreeItem",
  props: {
    sonTree: { type: Object, required: true },
    onItemClick: {
      type: Function,
      default: () => false
    },
    onAddClick: {
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
      model: this.sonTree,
      maxHeight: 0
    };
  },
  computed: {
    // 默认展开子树
    isFolder() {
      return this.model.children && this.model.children.length;
    },
    groupStyle() {
      return {
        "max-height": this.maxHeight + "px"
      };
    }
  },
  watch: {
    sonTree: {
      handler: function(newValue) {
        this.model = newValue;
        this.handleGroupMaxHeight();
      },
      deep: true
    }
  },
  methods: {
    handleAddClick() {
      this.onAddClick(this.model);
      this.handleGroupMaxHeight();
    },
    handleItemClick() {
      this.onItemClick(this.model);
    },
    handleGroupMaxHeight() {
      if (this.model.children && this.model.opened) {
        this.maxHeight = this.model.children.length * 32;
      } else {
        this.maxHeight = 0;
      }
    },
    handleItemToggle(flag) {
      if (flag) {
        this.maxHeight = 0;
      } else {
        this.handleGroupMaxHeight();
      }
      this.model.opened = !this.model.opened;
    }
  },
  mounted() {
    this.handleGroupMaxHeight();
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.tree-item {
  white-space: nowrap;
  outline: none;
  @include font-color($light-text-color, $dark-text-color);
  .tree-content {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding-left: 12px;
    &:hover {
      @include bg-color(#edf1f6, $dark-active-menu-item);
      .tree-add {
        display: block;
      }
    }
  }
  .tree-content-active {
    @include bg-color(#edf1f6, $dark-active-menu-item);
    color: rgb(45, 140, 240);
  }
  .tree-icon {
    display: block;
    margin-right: 4px;
  }
  .tree-add-active {
    display: block !important;
    margin-right: 12px;
  }
  .tree-add {
    display: none;
    margin-right: 12px;
    font-size: 20px;
  }
  .tree-hold {
    display: block;
    width: 20px;
    height: 20px;
  }
  .tree-name {
    display: block;
    flex: 1;
    line-height: 34px;
    // padding: 0 6px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;

    font-size: 14px;
    // color: rgba(0,0,0,0.65);
  }
  .tree-name-active {
    @include bg-color(#edf1f6, $dark-active-menu-item);
    color: rgb(45, 140, 240);
  }
  .tree-children {
    // margin-left: 26px;
    transition: max-height 0.3s;
    max-height: 0;
    overflow: hidden;
  }
  .tree-icon {
    display: block;
    margin-right: 4px;
  }
  .tree-loading {
    padding-left: 26px;
    padding-right: 5px;
    font-size: 16px;
  }
  .tree-open {
    padding-right: 8px;
  }
}
</style>
