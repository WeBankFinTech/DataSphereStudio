<template>
  <div class="tree-item">
    <div
      class="tree-content"
      @click="handleItemToggle"
      :class="{
        'tree-content-active': currentTreeId == model.id,
        'tree-content-children': !model.children
      }"
    >
      <div class="tree-icon" v-if="model.id < 10">
        <SvgIcon v-if="model.opened" icon-class="open" />
        <SvgIcon v-else icon-class="close" />
      </div>
      <div class="tree-icon tree-children-icon" v-if="model.id > 10">
        <!-- <SvgIcon icon-class="flow" :color="'#2e92f7'" /> -->
        <SvgIcon
          icon-class="flow"
          :class="{ 'tree-icon-active': currentTreeId == model.id }"
          :color="currentTreeId == model.id ? '#2e92f7' : ''"
        />
      </div>
      <div
        class="tree-name"
        :class="{ 'tree-name-active': currentTreeId == model.id }"
        @click="handleItemClick"
      >
        {{ model.name }}
      </div>
    </div>
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <tree-item
        v-for="item in model.children"
        :key="item.id"
        :data="item"
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
    data: { type: Object, required: true },
    onItemClick: {
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
      maxHeight: 0,
      itemClickid: 0
    };
  },
  computed: {
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
    data(newValue) {
      this.model = newValue;
      this.handleGroupMaxHeight();
    }
  },
  methods: {
    handleItemClick() {
      const { id } = this.model;
      this.itemClickid = id;
      this.onItemClick(this.model);
    },
    handleItemToggle() {
      if (!this.isLeaf) {
        this.onItemToggle(this.model);
      }
    },
    handleGroupMaxHeight() {
      if (this.model.opened && this.model.children) {
        this.maxHeight = this.model.children.length * 42;
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
  @include font-color($light-text-color, $dark-text-color);
  .tree-content {
    height: 34px;
    display: flex;
    align-items: center;
    cursor: pointer;
    padding-left: 12px;
    &:hover {
      // background-color: #EDF1F6;
      @include bg-color(#edf1f6, $dark-active-menu-item);
    }
  }
  .tree-content-active {
    @include bg-color(#e0edff, $dark-base-color);
    border-right: 3px solid #2e92f7;
    @include border-color(#2e92f7, $dark-border-color-base);
  }
  .tree-content-children {
    margin-bottom: 8px;
  }
  .leaf {
    color: transparent;
  }
  .tree-icon {
    display: block;
    padding-right: 4px;
  }
  .tree-children-icon {
    padding-left: 26px;
  }
  .tree-loading {
    display: block;
    font-size: 16px;
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
    padding: 0 6px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .tree-name-active {
    // background-color: #EDF1F6;
    // @include bg-color(#edf1f6, $dark-active-menu-item);
    // color: rgb(45, 140, 240);
    @include font-color($primary-color, $dark-primary-color);
  }

  .tree-icon-active {
    @include font-color(#2e92f7, $dark-text-color);
  }
  .tree-children {
    transition: max-height 0.3s;
    max-height: 0;
    overflow: hidden;
    .leaf {
      display: none;
    }
  }
}
</style>
