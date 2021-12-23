<template>
  <div class="tree-item">
    <div
      class="tree-content"
      :class="{ 'tree-content-active': currentTreeId == model.id }"
    >
      <div class="tree-icon" :class="{ leaf: isLeaf }">
        <SvgIcon
          v-if="model.opened"
          icon-class="open"
          @click="handleItemToggle"
          style="opacity: 0.65;"
        />
        <SvgIcon
          v-else
          icon-class="close"
          style="opacity: 0.65;"
          @click="handleItemToggle"
        />
      </div>
      <div class="tree-icon" v-if="model.type == 'flow'">
        <SvgIcon icon-class="flow" />
      </div>
      <div class="tree-icon" v-if="model.type == 'api'">
        <SvgIcon icon-class="api2" />
      </div>
      <div class="tree-loading" v-if="model.loading">
        <SvgIcon icon-class="loading" />
      </div>
      <div class="tree-name" @click="handleItemClick">
        {{ model.name }}
      </div>
      <Dropdown style="margin-left: 20px" @on-click="handleDropDown">
        <div class="tree-info" v-if="model.type == 'project' && model.canWrite">
          <SvgIcon icon-class="more_more" />
        </div>
        <DropdownMenu slot="list">
          <DropdownItem :name="'config'">配置</DropdownItem>
          <DropdownItem :name="'delete'">删除</DropdownItem>
        </DropdownMenu>
      </Dropdown>
      <div
        class="tree-add"
        @click="handleAddClick"
        v-if="model.type == 'project' && model.canWrite"
      >
        <SvgIcon icon-class="plus" />
      </div>
      <div class="tree-hold" v-else></div>
      <Dropdown
        style="margin-left: 20px"
        @on-click="handleFlowDropDown"
        placement="bottom-end"
      >
        <div class="tree-info" v-if="model.type == 'flow'">
          <SvgIcon icon-class="more_more" />
        </div>
        <DropdownMenu slot="list">
          <DropdownItem :name="'config'" v-if="model.editable">{{
            $t("message.workflow.workflowItem.config")
          }}</DropdownItem>
          <DropdownItem :name="'move'" v-if="model.editable">{{
            $t("message.workflow.workflowItem.move")
          }}</DropdownItem>
          <DropdownItem :name="'delete'" v-if="model.editable">{{
            $t("message.workflow.workflowItem.delete")
          }}</DropdownItem>
          <DropdownItem :name="'viewVersion'">{{
            $t("message.workflow.workflowItem.viewVersion")
          }}</DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </div>
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <tree-item
        v-for="item in model.children"
        :key="item.id"
        :data="item"
        :on-item-click="onItemClick"
        :currentTreeId="currentTreeId"
        :on-config-flow="onConfigFlow"
        :on-delete-flow="onDeleteFlow"
        :on-move-flow="onMoveFlow"
        :on-view-version="onViewVersion"
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
    },
    onConfigProject: {
      type: Function,
      default: () => false
    },
    onDeleteProject: {
      type: Function,
      default: () => false
    },
    onConfigFlow: {
      type: Function,
      default: () => false
    },
    onDeleteFlow: {
      type: Function,
      default: () => false
    },
    onMoveFlow: {
      type: Function,
      default: () => false
    },
    onViewVersion: {
      type: Function,
      default: () => false
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
    },
    handleDropDown(name) {
      if (name === "config") {
        this.onConfigProject(this.model);
      } else {
        this.onDeleteProject(this.model);
      }
    },
    handleFlowDropDown(name) {
      let _this = this;
      switch (name) {
        case "config":
          _this.onConfigFlow(_this.model);
          break;
        case "move":
          _this.onMoveFlow(_this.model);
          break;
        case "delete":
          _this.onDeleteFlow(_this.model);
          break;
        case "viewVersion":
          _this.onViewVersion(_this.model);
          break;
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
  .tree-content {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 10px;
    @include font-color($light-text-color, $dark-text-color);
    &:hover {
      @include bg-color(#edf1f6, $dark-active-menu-item);
      .tree-add {
        visibility: visible;
      }
      .tree-info {
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
    visibility: hidden;
    font-size: 16px;
    @include font-color($light-text-color, $dark-text-color);
  }

  .tree-info {
    display: block;
    visibility: hidden;
    font-size: 16px;
    margin-right: 6px;
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
