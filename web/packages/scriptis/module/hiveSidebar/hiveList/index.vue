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
      @we-dblclick="handledbclick"
      @mouseover.native="toggleShow"
    />
    <Spin
      v-if="loading||rendering"
      size="large"
      fix/>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import storage from '@dataspherestudio/shared/common/helper/storage';
import VirtualTree from '@dataspherestudio/shared/components/virtualTree';
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
    tableowner: String,
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
    },
    tableowner() {
      this.mergeData()
    }
  },
  mounted() {
    this.dbnameDesc = {}
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
        searchText = searchText.replace(/^[\s]+|[\s]+$/,'')
        const searchDbTb = searchText.split('.')
        const txt = child.dataType === 'db' ? searchDbTb[0] : searchDbTb[1]
        if (searchText.indexOf('.') > -1) { // db.tb
          child.isVisible = txt && name.indexOf(txt) > -1
        } else {
          child.isVisible = name.indexOf(searchText) > -1
        }
        if (this.tableowner && child.dataType == 'tb') {
          child.isVisible = child.isVisible && this.tableowner === child.createdBy
        }
        // 获取数据库中是否有符合搜索条件的表，有的话得显示库名
        if (child.dataType === 'db') {
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
            name: this.$t('message.scripts.upstreamTable'),
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
          class: `node-name ${item.iconCls}`,
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
        let nodeObj = {
          class: `node-name ${item.iconCls}`,
          attrs: {
            'data-open': isOpen ? 'open' : ''
          }
        }
        if (item.dataType === 'tb' && item.createdAt) {
          let createdAt = new Date(item.createdAt*1000)
          createdAt = createdAt.toLocaleDateString().replace(/\//g, "-") + " " + createdAt.toTimeString().substr(0, 8)
          nodeObj.attrs.title = `${item.createdBy}\n${createdAt}`
        }
        return h('span', nodeObj, [item.name])
      }
    },
    resize() {
      this.height = this.$el.clientHeight
    },
    toggleShow(e) {
      const item = e.target.innerText
      const overDb = e.target.className.indexOf('fi-hivedb') > -1
      if ( overDb && !this.dbnameDesc[item] && this.$APP_CONF && !this.$APP_CONF.hide_view_db_detail) {
        // mouseover 在数据库项上且未获取描述信息
        clearTimeout(this.fetchDbInfo)
        this.fetchDbInfo = setTimeout(() => {
          api.fetch('/dss/datapipe/datasource/getSchemaBaseInfo',  {
            dbName: item
          }, 'get').then((rst) => {
            const title = `${item}\n${rst.schemaInfo.description||''}`
            e.target.title = `${item}\n${rst.schemaInfo.description||''}`
            this.dbnameDesc[item] = title
            e.target.innerText = item
          }).catch(() => {
          });
        }, 800);
      } else if (overDb) {
        e.target.title = this.dbnameDesc[item]
        e.target.innerText = item
      }
    }
  },
  beforeDestroy() {
    clearTimeout(this.fetchDbInfo)
    window.removeEventListener('resize', this.resize);
  }
};
</script>
<style src="./index.scss" lang="scss">
</style>
