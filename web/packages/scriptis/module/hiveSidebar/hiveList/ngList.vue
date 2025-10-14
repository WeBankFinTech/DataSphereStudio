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
      <span v-else-if="treeData.length < 1 " style="text-align: center;padding-top: 20px;  display: flex;justify-content: center;">暂无数据</span>
    </div>
  </template>
  <script>
  import api from '@dataspherestudio/shared/common/service/api';
  import storage from '@dataspherestudio/shared/common/helper/storage';
  import VirtualTree from '@dataspherestudio/shared/components/virtualTree';
  export default {
    name: 'NGList',
    components: {
      VirtualTree,
    },
    props: {
      list: Array,
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
      list(v) {
        this.treeData = [...v];
        this.rendering = false;
      }
    },
    mounted() {
      this.dbnameDesc = {}
      this.height = this.$el.clientHeight;
      window.addEventListener('resize', this.resize);
    },
    methods: {
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
        if (node.dataType == 'ng_space' || node.dataType == 'ng_tag' || node.dataType == 'ng_edge') { 
          this.$emit('we-contextmenu', {ev, item: node, isOpen: this.openNode[item._id]})
        }
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
        if (item.dataType === 'ng_field') { // 表字段
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
          let title = '';
          if (item.dataType === 'ng_edge_index') {
            title = `边类型索引\n所属边${item.byEdge}`;
          }
          if (item.dataType === 'ng_tag_index') {
            title = `标签类型索引\n所属标签${item.byTag}`;
          }
          let nodeObj = {
            attrs: {
              'data-open': isOpen ? 'open' : '',
            },
            style: {
              'width': '100%',
            }
          }
          return h('span', nodeObj, [
            h('SvgIcon', {
              props: {
                'icon-class': item.typeIcon
              },
                style: {
                  'font-size': '14px',
                  'margin-right': '5px'
                },
                attrs: {
                  type: 'md-arrow-dropright'
                }
              }, []),
              h('span', {
                class: 'node-name v-hivetable-text',
                attrs: {
                  'data-id': item._id,
                  'data-type': item.dataType,
                  title
                }
              }, [item.name])
          ])
        }
      },
      resize() {
        this.height = this.$el.clientHeight
      },
      toggleShow(e) {
        const _id = e.target.dataset.id;
        const dataType = e.target.dataset.type;
        let node = this.nodeItem(_id, this.list)
        if (!this.dbnameDesc[_id] ) {
          // mouseover 在图空间上
          clearTimeout(this.fetchDbInfo)
          if (dataType == 'ng_space') {
            this.fetchDbInfo = setTimeout(() => {
              const params = {
                spaceName: node.spaceName,
                clusterCode: node.clusterCode
              };
              api.fetch('/dss/datapipe/datasource/space',  params, 'get').then((rst) => {
                  const res = rst.dMSpaceInfoBeans ? rst.dMSpaceInfoBeans[0] || {}: {}
                  const title = `${node.spaceName}\n${res.comment||""}`
                  this.dbnameDesc[_id] = title
                  e.target.title = `${node.spaceName}\n${res.comment||""}`
                  e.target.innerText = e.target.innerText
                }).catch(() => {
                });
            }, 800);
          } else if (dataType == 'ng_tag') {
            // this.fetchDbInfo = setTimeout(() => {
            //   const params = {
            //       spaceName: node.spaceName,
            //       clusterCode: node.clusterCode,
            //       tagName
            //   }
            //   api.fetch('/dss/datapipe/datasource/tag-prop', params, 'get').then(res => {
            //       if (res) {
            //         const res = rst.dMSpaceInfoBeans ? rst.dMSpaceInfoBeans[0] || {}: {}
            //         const title = `${node.spaceName}\n${res.comment||""}`
            //         this.dbnameDesc[_id] = title
            //         e.target.title = `${node.spaceName}\n${res.comment||""}`
            //         e.target.innerText = e.target.innerText
            //       }
            //   })
            // }, 800);
          } else if(dataType == 'ng_edge') {
            // this.fetchDbInfo = setTimeout(() => {
            //   const params = {
            //     spaceName: node.spaceName,
            //     clusterCode: node.clusterCode,
            //     edgeTypeName: node.edgeTypeName
            //   }
            //   api.fetch('/dss/datapipe/datasource/edge-prop', params, 'get').then(res => {
            //       if (res) {
            //         const res = rst.dMSpaceInfoBeans ? rst.dMSpaceInfoBeans[0] || {}: {}
            //         const title = `${node.spaceName}\n${res.comment||""}`
            //         this.dbnameDesc[_id] = title
            //         e.target.title = `${node.spaceName}\n${res.comment||""}`
            //         e.target.innerText = e.target.innerText
            //       }
            //   })
            // }, 800);
          }
        } else {
          e.target.title = this.dbnameDesc[_id]
          e.target.innerText =  e.target.innerText
        }
      }
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.resize);
    }
  };
  </script>
<style src="./index.scss" lang="scss">
</style>