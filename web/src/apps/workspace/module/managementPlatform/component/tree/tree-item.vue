<template>
  <div class="tree-item">
    <!-- 父树级别 -->
    <div class="tree-content" v-if="!model._id" :class="{ 'tree-content-active': currentTreeId == model.id }">
      <div class="tree-name" @click="handleItemClick">{{model.name}}</div>
      <div class="tree-add" @click="handleAddClick" v-if="model.type=='component'" :class="{ 'tree-add-active': currentTreeId == model.id }">
        <Icon custom="iconfont icon-plus" size="16"></Icon>
      </div>
      <div class="tree-hold" v-else></div>
    </div>
    <div class="tree-content" v-else :class="{ 'tree-content-active': currentTreeId == model._id }">
        <div class="tree-loading" v-if="model._id > 15">
          <Icon custom="iconfont icon-zujianjieruguanli" size="16"></Icon>
        </div>
        <div class="tree-name" @click="handleItemClick">{{model.name}}</div>
    </div>
    <!-- 子树级别 -->
    <ul class="tree-children" v-if="isFolder" :style="groupStyle">
      <tree-item v-for="item in model.children" :key="item.id" :sonTree="item"
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
    sonTree: {type: Object, required: true},
    onItemClick: {
      type: Function, default: () => false
    },
    onAddClick: {
      type: Function, default: () => false
    },
    currentTreeId: {
      type: Number, default: 0
    }
  },
  data() {
    return {
      model: this.sonTree,
      maxHeight: 0,
    };
  },
  computed: {
    // 默认展开子树
    isFolder () {
      return this.model.children && this.model.children.length
    },
    groupStyle () {
      return {
        'max-height': this.maxHeight + 'px',
      }
    }
  },
  watch: {
    sonTree: {
      handler: function(newValue) {
        console.log('watch son tree')
        this.model = newValue
        this.handleGroupMaxHeight()
      },
      deep: true
    }
  },
  methods: {
    handleAddClick (e) {
      this.onAddClick(this.model)
      this.handleGroupMaxHeight();
    },
    handleItemClick (e) {
      this.onItemClick(this.model)
    },
    handleGroupMaxHeight () {
      if (this.model.children) {
        this.maxHeight = this.model.children.length * 32;
      } else {
        this.maxHeight = 0;
      }
    },
  },
  mounted() {
    this.handleGroupMaxHeight();
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
    padding-left: 12px;
    &:hover {
      background-color: #EDF1F6;
      .tree-add {
        display: block;
      }
    }
  }
  .tree-content-active {
    background-color: #EDF1F6;
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
    font-family: PingFangSC-Regular;
    font-size: 14px;
    // color: rgba(0,0,0,0.65);
  }
  .tree-name-active {
    background-color: #EDF1F6;
    color: rgb(45, 140, 240);
  }
  .tree-children {
    // margin-left: 26px;
    transition: max-height .3s;
    max-height: 0;
    overflow: hidden;
  }
  .tree-icon {
    display: block;
    margin-right: 4px;
  }
  .tree-loading {
    padding-left: 26px;
  }
}
</style>
