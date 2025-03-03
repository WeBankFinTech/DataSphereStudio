<template>
    <div>
      <virtual-tree
        :list="treeData"
        :render="renderNode"
        :open="openNode"
        :height="height - 33"
        @we-click="onClick"
        @we-contextmenu="onContextMenu"
        @we-open-node="openNodeChange"
        @we-dblclick="handledbclick"
      />
      <Spin
        v-if="loading||rendering"
        size="large"
        fix/>
      <span v-else-if="treeData.length < 1 " style="text-align: center;padding-top: 20px;  display: flex;justify-content: center;">暂无数据</span>
    </div>
  </template>
  <script>
  import storage from '@dataspherestudio/shared/common/helper/storage';
  import VirtualTree from '@dataspherestudio/shared/components/virtualTree';
  export default {
    name: 'NGList',
    components: {
      VirtualTree,
    },
    props: {
      list: Array,
      filterText: {
        type: String,
        default: '',
      },
      loading: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      let openNode = storage.get('tree-open-ngnode') || {}
      return {
        treeData: [],
        openNode,
        height: 0,
        rendering: false
      }
    },
    watch: {
      filterText() {
        this.mergeData()
      },
      list(v) {
        this.mergeData()
      }
    },
    mounted() {
      this.height = this.$el.clientHeight;
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
          searchText = searchText.replace(/^[\s]+|[\s]+$/,'')
          const searchDbTb = searchText.split('.')
          const txt = child.dataType === 'starrocks_db' ? searchDbTb[0] : searchDbTb[1]
          if (searchText.indexOf('.') > -1) { // db.tb
            child.isVisible = txt && name.indexOf(txt) > -1
          } else {
            child.isVisible = name.indexOf(searchText) > -1
          }
          // 获取数据库中是否有符合搜索条件的表，有的话得显示库名
          if (child.dataType === 'starrocks_db') {
            if (child.children && child.children.length) {
              this.recursion(child.children, text, isCaseSensitive);
              let hasVisible = child.children.some((item)=>item.isVisible === true)
              if (searchText.indexOf('.') > -1) {
                child.isVisible = searchDbTb[1] ? !!(child.isVisible && hasVisible) : child.isVisible
              } else if(hasVisible) {
                child.isVisible = hasVisible
              }
            } else if(searchDbTb[1]) {
              child.isVisible = false;
            }
          } else if (child.dataType == 'starrocks_tb') {
            if (child.children && child.children.length) {
              child.children.forEach(it=>it.isVisible = child.isVisible)
            }
          }
        });
      },
      mergeData() {
        if (this.timer) {
          clearTimeout(this.timer)
        }
        this.timer = setTimeout(() => {
          this.recursion(this.list, this.filterText)
          this.treeData = [...this.list]
          this.rendering = false
        }, 300);
      },
      openNodeChange(data) {
        this.openNode = data || {}
        storage.set('tree-open-ngnode', this.openNode)
      },
      onClick({item}) {
        let node = this.nodeItem(item._id, this.list)
        if (!node) return
        if (!node.loaded && !item.isLeaf) {
          this.rendering = true
        }
        this.$emit('we-click', {item: node})
      },
      onContextMenu({ev, item}) {
        let node = this.nodeItem(item._id, this.list)
        if (!node) return
        this.$emit('we-contextmenu', {ev, item: node, isOpen: this.openNode[item._id]})
      },
      handledbclick(data) {
        this.$emit('we-dblclick', data)
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

      /**
       * 自定义渲染节点
       */
      renderNode(h, params) {
        let item = params.item
        let isOpen = this.openNode[item._id]
        if (item.dataType === 'starrocks_field') { // 表字段
          return h('span', {
            class: `${item.typeIcon}`,
          }, [
            h('span', {
              class: 'v-hivetable-type',
              attrs: {
                title: item.type
              }
            }, item.type ? [`[${item.type}]`] : ''),
            h('span', {
              class: 'node-name v-hivetable-text',
              attrs: {
                title: item.name
              }
            }, [item.name])
          ])
        } else {
          let nodeObj = {
            class: `${item.typeIcon}`,
            attrs: {
              'data-open': isOpen ? 'open' : '',
            },
            style: {
              'width': '100%',
            }
          }
          return h('span', nodeObj, [

              h('span', {
                class: 'node-name v-hivetable-text',
                attrs: {
                  'data-id': item._id,
                  'data-type': item.dataType,
                  title: item.name
                }
              }, [item.name])
          ])
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