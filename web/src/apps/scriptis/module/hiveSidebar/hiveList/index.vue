<template>
  <div>
    <virtual-tree
      :list="treeData"
      :render="renderNode"
      :open="openNode"
      :height="height"
      @we-click="onClick"
      @we-contextmenu="onContextMenu"
      @we-open-node="openNodeChange"
      @we-dblclick="handledbclick"/>
    <Spin
      v-if="loading||rendering"
      size="large"
      fix/>
  </div>
</template>
<script>
import storage from '@/common/helper/storage';
import VirtualTree from '@/components/virtualTree';
export default {
  name: 'WeHive',
  components: {
    VirtualTree,
  },
  props: {
    data: Array,
    filterText: {
      type: String,
      default: '',
    },
    loading: {
      type: Boolean,
      default: false,
    },
    node: Object,
    csTableList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    let openNode = storage.get('tree-open-node') || {}
    return {
      treeData: [],
      openNode,
      height: 0,
      rendering: false
    }
  },
  watch: {
    csTableList() {
      this.mergeData()
    },
    data() {
      this.mergeData()
    },
    filterText() {
      this.mergeData()
    }
  },
  mounted() {
    this.height = this.$el.clientHeight
    window.addEventListener('resize', this.resize);
  },
  methods: {
    recursion(node, text, isCaseSensitive = false){
      node.forEach((child) => {
        let name = child.name;
        let searchText = text;
        // 是否对大小写敏感
        if (!isCaseSensitive) {
          name = child.name.toLowerCase();
          searchText = text.toLowerCase();
        }
        child.isVisible = name.indexOf(searchText) > -1
        // 获取数据库中是否有符合搜索条件的表，有的话得显示库名
        if (child.dataType === 'db') {
          if (child.children && child.children.length) {
            this.recursion(child.children, text, isCaseSensitive);
            let hasVisible = child.children.some((item)=>item.isVisible === true)
            if (hasVisible) {
              child.isVisible = true;
            }
          }
        } else if (child.dataType == 'tb') {
          if (child.children && child.children.length) {
            child.children.forEach(it=>it.isVisible = child.isVisible)
          }
        }
      });
    },
    openNodeChange(data) {
      this.openNode = data || {}
      storage.set('tree-open-node', this.openNode)
    },
    onClick({item}) {
      let node = this.nodeItem(item._id, this.treeData)
      if (!node) return
      if (!node.loaded && !item.isLeaf) {
        this.rendering = true
      }
      this.$emit('we-click', {item: node})
    },
    onContextMenu({ev, item}) {
      let node = this.nodeItem(item._id, this.treeData)
      if (!node) return
      if (node.dataType !== 'cat') { // 分类节点没有右键菜单
        this.$emit('we-contextmenu', {ev, item: node, isOpen: this.openNode[item._id]})
      }
    },
    handledbclick(data) {
      this.$emit('we-dblclick', data)
    },
    csClick() {
      this.csshow = !this.csshow
      this.$emit('we-click', {dataType: 'cs'})
    },
    /**
     * 递归查找点击的对应树节点
     */
    nodeItem(_id, data) {
      let node
      let helperFn = (list) => {
        for(let i=0,len=list.length;i<len;i++) {
          if (node) return
          if (list[i]._id === _id) {
            return node = list[i]
          }
          if(list[i].children) {
            helperFn(list[i].children)
          }
        }
      }
      helperFn(data)
      return node
    },
    mergeData() {
      // 工作流那边过来的带有node，合并数据，否则为scriptis数据库数据
      let treeData = []
      if(this.timer) {
        clearTimeout(this.timer)
      }
      this.timer = setTimeout(() => {
        this.recursion(this.data, this.filterText)
        this.recursion(this.csTableList, this.filterText)
        if (this.node) {
          treeData = [{
            _id: 'hive',
            name: 'hive',
            iconCls: 'md-arrow-dropright',
            dataType: 'cat',
            loaded: true,
            children: this.data
          },{
            _id: 'cs',
            name: '上游暂存表',
            iconCls: 'md-arrow-dropright',
            dataType: 'cat',
            loaded: true,
            children: this.csTableList
          }]
          // 工作流界面默认展看hive, cs
          if (!this.openNode['hive'] && !this.openNode['cs']) {
            this.openNode.hive = true;
            this.openNode.cs = true;
          }
        } else {
          treeData = [...this.data]
        }
        this.treeData = treeData
        this.rendering = false
      }, 100);
    },
    /**
     * 自定义渲染节点
     */
    renderNode(h, params) {
      let item = params.item
      let isOpen = this.openNode[item._id]
      if (item.dataType === 'field') { // 表字段
        return h('span', {
          class: 'node-name'
        }, [
          h('span', {
            class: 'v-hivetable-type',
            attrs: {
              title: item.type
            }
          }, [`[${item.type}]`]),
          h('span', {
            class: 'v-hivetable-text',
            attrs: {
              title: item.name
            }
          }, [item.name])
        ])
      } else if(item.dataType === 'cat') { // 工作流那边展示分类，上下游暂存表、hive
        return h('span', {
          class: 'node-name'
        }, [
          h('Icon', {
            class: {
              'icon-transform': isOpen
            },
            style: {
              'font-size': '18px'
            },
            attrs: {
              type: 'md-arrow-dropright'
            }
          }, []),
          h('Icon', {
            style: {
              'margin-right': '5px',
              'font-size': '16px'
            },
            attrs: {
              type: isOpen?'ios-folder-outline':'md-folder'
            }
          }, []),
          item.name
        ])
      } else {
        return h('span', {
          class: 'node-name'
        }, [item.name])
      }
    },
    resize() {
      this.height = this.$el.clientHeight
    },
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize);
  }
};
</script>
<style src="./index.scss" lang="scss">
</style>