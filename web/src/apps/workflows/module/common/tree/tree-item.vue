<template>
  <div class="tree-item">
    <div class="tree-content">
      <div class="tree-icon" :class="{'leaf': isLeaf}">
        <Icon custom="iconfont icon-open" v-if="model.opened" @click="handleItemToggle"></Icon>
        <Icon custom="iconfont icon-close" v-else @click="handleItemToggle"></Icon>
      </div>
      <div class="tree-icon" v-if="model.type=='flow'">
        <Icon custom="iconfont icon-flow"></Icon>
      </div>
      <div class="tree-loading" v-if="model.loading">
        <Icon custom="iconfont icon-xingzhuang" size="16"></Icon>
      </div>
      <div class="tree-name" :class="{'tree-name-active': currentTreeId == model.id}" @click="handleItemClick">{{model.name}}</div>
      <div class="tree-add" @click="handleAddClick" v-if="model.type=='project' && model.canWrite">
        <Icon custom="iconfont icon-plus" size="20"></Icon>
      </div>
      <div class="tree-hold" v-else></div>
    </div>
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <tree-item v-for="item in model.children" :key="item.id" :data="item"
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
    data: {type: Object, required: true},
    onItemClick: {
      type: Function, default: () => false
    },
    onAddClick: {
      type: Function, default: () => false
    },
    onItemToggle: {
      type: Function, default: () => false
    },
    currentTreeId: {
      type: Number, default: 0
    }
  },
  data() {
    return {
      model: this.data,
      maxHeight: 0,
    };
  },
  computed: {
    isFolder () {
      return this.model.children && this.model.children.length
    },
    isLeaf () {
      if (this.model.type == 'flow') {
        return true;
      } else {
        return this.model.isLeaf;
      }
    },
    groupStyle () {
      return {
        'max-height': this.maxHeight + 'px',
      }
    }
  },
  watch: {
    data (newValue) {
      this.model = newValue
      this.handleGroupMaxHeight()
    },
  },
  methods: {
    handleAddClick (e) {
      this.onAddClick(this.model)
    },
    handleItemClick (e) {
      this.onItemClick(this.model)
    },
    handleItemToggle(e) {
      if (!this.isLeaf) {
        this.onItemToggle(this.model)
      }
    },
    handleGroupMaxHeight () {
      if (this.model.opened && this.model.children) {
        this.maxHeight = this.model.children.length * 32;
      } else {
        this.maxHeight = 0;
      }
    },
  }
};
</script>
<style lang="scss" scoped>
.tree-item {
  white-space: nowrap;
  outline: none;
  .tree-content {
    display: flex;
    align-items: center;
    cursor: pointer;
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
      from { transform: rotate(0deg);}
      50%  { transform: rotate(180deg);}
      to   { transform: rotate(360deg);}
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
    &:hover {
      background-color: #EDF1F6;
    }
  }
  .tree-name-active {
    background-color: #EDF1F6;
    color: rgb(45, 140, 240);
  }
  .tree-children {
    margin-left: 26px;
    transition: max-height .3s;
    max-height: 0;
    overflow: hidden;
    .leaf {
      display: none;
    }
  }
}
</style>
