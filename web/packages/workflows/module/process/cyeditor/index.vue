<template>
  <div :class="[{ 'fullScreenCyeditor': fullScreen }, 'cyeditor']">
    <div ref="cyeditorel" class="cy-editor-container"></div>
    <div v-show="hoverTitle" class="hover-title" :style="titlePos.pos">
      {{ hoverTitle }}
    </div>
    <!-- 【0：未执行；1：运行中；2：已成功；3：已失败；4：已跳过】 -->
    <div ref="nodestaus" style="position:absolute;top: 0">
      <template v-for="(node) in value.nodes">
        <div class="run-status" :key="node.key" :data-key="node.key" >
          <div v-if="node.runState" class="run-status-content">
            <span
              v-if="node.runState.isShowTime"
              class="executor-pending">
              {{node.runState ? node.runState.time : ''}}
            </span>
            <SvgIcon
              v-if="node.runState.isSvg"
              class="executor-icon"
              :style="{'font-size': '14px' }"
              :title="node.runState.title"
              :icon-class="node.runState.iconType"
              :color="node.runState.borderColor"
            />
            <Icon
              v-else
              :title="node.runState.title"
              :type="node.runState.iconType"
              class="executor-icon"
              :class="node.runState.colorClass"
              :size="14" />
          </div>
        </div>
      </template>
    </div>
    <ControlView
      :zoomSize="zoomSize"
      :minZoom="minZoom"
      :maxZoom="maxZoom"
      :layoutType="layoutType"
      :data="value"
      :default-viewMode="viewMode"
      :newTipVisible="newTipVisible"
      @format="format"
      @zoomChange="zoomChange"
      @screenSizeChange="screenSizeChange"
      @viewNode="nodeScroolIntoView"
      @modeChange="modeChange"
      @clickNodePath="$emit('search-node-path')"
    />
    <div class="designer-expand" v-show="leftMenuFold" @click="toggleMenu" style="top: 4px">
      <SvgIcon icon-class="dev_center_open" />
    </div>
  </div>
</template>
<script>
import { debounce } from 'lodash'
import CyEditor from '@dataspherestudio/cyeditor/index'
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';
import getStyles, { getThemeColorConsants } from './styles'
import convert from './convert'
import ControlView from './controlView.vue'
// import testdata from '../workflow01.json'

export default {
  name: 'CyeditorDesigner',
  components: {
    ControlView
  },
  props: {
    value: {
      type: Object,
      default: () => {
        return {
          nodes: [],
          edges: []
        }
      },
    },
    shapes: {
      type: Array,
      default: () => [],
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    readable: {
      type: Boolean,
      default: false,
    },
    ctxMenuOptions: {
      type: Object,
      default() {
        return {}
      },
    },
    viewMode: {
      type: String,
      default: 'cyeditor'
    },
    newTipVisible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      leftMenuFold: false,
      fullScreen: false,
      hoverTitle: '',
      titlePos: {
        pos: {
          left: '0px',
          top: '0px',
        },
      },
      state: {
        shapeOptions: {
          viewWidth: 200,
        },
      },
      pan: { x: 50, y: 0 },
      minZoom: 0.5,
      maxZoom: 3,
      zoomSize: 0,
      layoutType: null
    }
  },
  watch: {
    value(v) {
      this.initEditor(v)
    },
    shapes() {
      this.initEditor()
    },
    disabled: {
      immediate: true,
      handler(newVal) {
        if (this.instance) {
          this.instance.editor.setOption('disabled', newVal)
        }
      }
    }
  },
  methods: {
    toggleMenu() {
      this.leftMenuFold = false;
      if (this.instance) {
        this.instance.editor.toggleShapesPanel()
      }
    },
    initEditor(v) {
      const nodeTypes = this.getNodeTypes()
      if (nodeTypes.length < 1) {
        return
      }
      v = v || this.value || {nodes: [], edges: []};
      const data = convert(v);
      let cy, editor
      if (this.instance) {
        editor = this.instance.editor
        cy = this.instance.cy
      } else {
        const container = this.$refs.cyeditorel
        const contextMenu = {
          menus: [],
          beforeShow: (e, menus) => {
            if (this.ctxMenuOptions.beforeShowMenu) {
              let type = 'view'
              menus = this.disabled ? [] : [
                {
                  id: 'delete',
                  content: '删除',
                  value: 'delete',
                  text: '删除',
                  icon: 'shanchu'
                }
              ]
              if (e.target.isNode && e.target.isNode()) {
                type = 'node'
              } else if (e.target.isEdge && e.target.isEdge()) {
                type = 'link'
              } else {
                menus = []
              }
              const node = this.findNodeById(e.target.data().id)

              menus = this.ctxMenuOptions.beforeShowMenu(node, menus, type)
              menus = menus.map((it) => {
                return {
                  ...it,
                  id: it.value,
                  content: it.text,
                }
              })
              return menus
            }
            // if (e.target.isNode() || e.target.isEdge()) {
            //   return true
            // }
            return false
          },
          beforeClose: () => {
            return true
          },
        }
        let config = {
          disabled: this.disabled,
          cy: {
            layout: { name: 'preset' },
            styleEnabled: true,
            style: getStyles(nodeTypes),
            minZoom: this.minZoom,
            maxZoom: this.maxZoom,
            boxSelectionEnabled: true,
            pan: { x: 50, y: 0 },
            panningEnabled: true,
            userPanningEnabled: true,
            userZoomingEnabled: true,
            zoom: v.config && v.config.zoom || 1,
            zoomingEnabled: true,
          },
          editor: {
            container,
            zoomRate: 0.2,
            lineType: 'straight',
            dragAddNodes: !this.readable,
            snapGrid: false,
            contextMenu,
            navigator: true,
            nodeTypes: nodeTypes,
            beforeAdd() {
              return true
            },
            afterAdd() {
              //
            },
            // getDomNode(data: { image: string; name: string }) {
            //   let div = document.createElement('div');
            //   div.className = 'cyeditor-dom-node';
            //   div.title = data.name;
            //   const nodeItem = nodeTypes.find((it) => it.type === data.type);
            //   const time = nodeItem.time
            //     ? `<div class="executor-pending">${nodeItem.executorTime}dffd</div>`
            //     : '';
            //   div.innerHTML = `${time}<image src="${nodeItem.image}"><span>${data.name}<span>`;
            //   return div;
            // },
          },
        }
        this.instance = new CyEditor(config)
        editor = this.instance.editor
        cy = this.instance.cy
        let timer
        // 初始化
        this.updataNodeStatuStyle()
        cy.on('mouseover', 'node', (e) => {
          clearTimeout(timer)
          // hover title
          timer = setTimeout(() => {
            const pan = cy.pan()
            const zoom = cy.zoom()
            const data = e.target.data()
            const pos = e.target.position()
            this.hoverTitle = data.name
            this.titlePos.pos = {
              left: pos.x * zoom + (data.width / 2) * zoom + pan.x + 200 + 'px',
              top: pos.y * zoom + pan.y + 'px',
            }
          }, 300)
        })
        cy.on('mouseout', 'node', () => {
          this.hoverTitle = ''
          clearTimeout(timer)
        })
        cy.on('cyeditor.ctxmenu', (scope, { menuItem, target, event }) => {
          let type = 'view'
          let el
          if (target.isNode && target.isNode()) {
            type = 'node'
            el = this.findNodeById(target.data().id)
          } else if (target.isEdge && target.isEdge()) {
            type = 'link'
            el = this.findLinkById(target.data().id)
            if (menuItem.id === 'delete') {
              target.remove()
            }
          }
          this.$emit('on-ctx-menu', menuItem.id, el || event, type)
        })
        cy.on('drag', 'node', debounce(() => {
          const data = this.getJsonData()
          this.$emit('change', data)
        }, 300))
        cy.on(
          'remove',
          (arg) => {
            const { type } = arg.target.data()
            if (type !== 'edgehandle') {
              this.hasChange()
            }
          }
        )
        cy.on('cxttap', 'node', (event) => {
          const node = event.target;
          cy.batch(function() {
            cy.$(':selected').unselect();
            node.select();
          });
        });
        cy.on("pan zoom", () => {
          this.zoomSize = +Number(cy.zoom()).toFixed(2);
          this.hasChange()
          const data = this.getJsonData()
          this.$emit('change', data)
          this.updataNodeStatuStyle()
        });

        cy.on('position bounds', 'node', (ev) => {
          if(!this.$refs.nodestaus) return
          let cy_node = ev.target;
          let id      = cy_node.id();
          let dom = this.$refs.nodestaus.querySelector(`[data-key='${id}']`)
          if (!dom)
            return;
          let style_transform = `translate(${cy_node.position('x').toFixed(2)}px, ${cy_node.position('y').toFixed(2)}px)`;
          dom.style.webkitTransform = style_transform;
          dom.style.msTransform     = style_transform;
          dom.style.transform       = style_transform;

          dom.style.display = 'inline';
          dom.style.position = 'absolute';
          dom.style['z-index'] = 10;
        });

        editor.on('hide_shapes_panel', () => {
          this.leftMenuFold = true;
        })
        editor.on('addnode', ({ target }) => {
          this.hasChange()
          this.$emit('node-add', {
            ...target,
            desc: '',
            key: target.id,
          })
        })
        editor.on('addlink', ({ target }) => {
          // 环形
          let parents =[]
          let getParentNodes = (node, parents) => {
            this.value.edges.forEach(link => {
              if (link.target == node) {
                if (parents.indexOf(link.source) == -1) {
                  parents.push(link.source);
                  getParentNodes(link.source, parents);
                }
              }
            });
          };
          getParentNodes(target.source, parents)
          if (parents.indexOf(target.target) != -1) {
            cy.getElementById(target.id).remove()
            this.$emit('message', {
              type: 'error',
              msg: this.$t('message.workflow.vueProcess.closed-loop')
            });
          }
          // 重复连线
          const hasEdge = this.value.edges.find(it => {
            return it.source === target.source && it.target === target.target
          })
          if (hasEdge) {
            cy.getElementById(target.id).remove()
            this.$emit('message',{
              type: 'error',
              msg: this.$t('message.workflow.vueProcess.repeatNodes')
            })
            return
          }
          this.hasChange();
          this.$emit('link-add', {
            ...target,
          })
        })
        editor.on('click', (e) => {
          if (e.target.isNode && e.target.isNode()) {
            const node = this.findNodeById(e.target.data().id)
            if (node) {
              this.$emit('node-click', node)
            }
          }
        })
        editor.on('dblclick', (e) => {
          if (e.target.isNode && e.target.isNode()) {
            const node = this.findNodeById(e.target.data().id)
            if (node) this.$emit('node-dblclick', node)
          }
        })
      }
      let pan = { ...this.pan };
      if (v.config) {
        v.config.pan && (pan = v.config.pan)
        v.config.layoutType && !this.layoutType && (this.layoutType = v.config.layoutType)
      }
      if (!this.zoomSize) {
        this.zoomSize = (v.config && v.config.zoom) ? v.config.zoom : 1
      }
      editor.json({
        elements: data,
        pan,
        zoom: this.zoomSize
      })
    },
    updataNodeStatuStyle() {
      if (!this.$refs.nodestaus || !this.instance) return
      const cy = this.instance.cy;
      let pan  = cy.pan();
      let zoom = cy.zoom();
      let transform = "translate(" + (pan.x + 200) + "px," + pan.y  + "px) scale(" + zoom + ")";
      this.$refs.nodestaus.style.msTransform = transform;
      this.$refs.nodestaus.style.transform = transform;
    },
    getNodeTypes() {
      const list = []
      ;(this.shapes || []).forEach((it) => {
        it.children.forEach((item) => {
          list.push({
            category: it.title,
            type: item.type,
            name: item.title,
            image: item.image,
            bg: '#fff',
            width: 150,
            height: 40,
            padding: 10
          })
        })
      })
      return list
    },
    findNodeById(id) {
      return this.value.nodes.find((it) => (it.id || it.key) == id)
    },
    findLinkById(id) {
      return this.value.edges.find((it) => (it.id || it.key) == id)
    },
    getSelectedNodes() {
      if (this.instance) {
        return this.instance.cy
          .$('node:selected')
          .map((it) => {
            return this.findNodeById(it.data().id)
          })
          .filter((it) => it)
      }
    },
    deleteNode(id) {
      if (this.instance) {
        this.instance.cy.getElementById(id).remove()
      }
    },
    getJsonData() {
      if (this.instance) {
        const data = this.instance.editor.json()
        const nodes = (data.elements.nodes || []).map((it) => {
          let node = this.findNodeById(it.data.id)
          if (!node) {
            node = {
              createTime: Date.now(),
              desc: '',
              id: it.data.id,
              key: it.data.id,
              title: '',
              type: it.data.type,
            }
          }
          node.layout = {
            ...node.layout,
            ...it.position,
          }
          return node
        })
        const edges = (data.elements.edges || []).map((it) => {
          return it.data
        })
        return {
          nodes,
          edges,
          config: {
            type: 'cyeditor',
            pan: data.pan,
            zoom: data.zoom,
            layoutType: this.layoutType
          }
        }
      }
    },
    hasChange() {
      const data = this.getJsonData()
      this.$emit('change', data)
    },
    setNodeRunState(key, state, readonly) {
      if (readonly && this.state.disabled) return
      let node = this.findNodeById(key);
      if (node) {
        const cy = this.instance.cy
        const currentNode = cy.$(`#${key}`)
        const runState = {
          ...state,
        }
        if ([3, 2].includes(state.status)) {
          currentNode.style({
            'border-color': state.status === 2 ? 'green' : 'red',
            'border-width': '1px',
            'border-style': 'solid',
          })
        } else {
          currentNode.style({
            'border-width': '0px',
          })
        }
        this.$set(node, 'runState', runState);
      }
    },
    getState() {
      return {
        baseOptions: {
          pageSize: this.zoomSize
        }
      }
    },
    format(config) {
      this.instance.editor.layout(config)
      this.layoutType = config
      this.hasChange()
    },
    zoomChange({ type, size }) {
      if (size < 0.2) {
        this.instance.cy.zoom(type === 'zoomout' ? this.maxZoom : this.minZoom)
        return
      }
      this.instance.editor.zoom(type === 'zoomout' ? 1 : -1, size)
    },
    screenSizeChange() {
      this.fullScreen = !this.fullScreen
      this.$emit('screenSizeChange', this.fullScreen)
    },
    // 切换模式
    modeChange(mode) {
      this.$emit('changeViewMode', 'changeViewMode', mode)
    },
    // 主题切换
    changeTheme(theme) {
      const themeColor = getThemeColorConsants(theme)
      this.instance.cy.style().selector('node[name]').style({
        'background-color': themeColor.nodeBg,
        'color': themeColor.nodeColor
      }).update()
    },
    nodeScroolIntoView(id) {
      const cy = this.instance.cy;
      const new_node = cy.getElementById(id); // 通过nodeId获取
      cy.animate({
        center: {
          eles: new_node,
        }
      });
      cy.batch(function() {
        cy.$(':selected').unselect();
        new_node.select();
      });
    },
  },
  mounted() {
    this.initEditor();
    eventbus.on('theme.change', this.changeTheme);
  },
  beforeDestroy() {
    eventbus.off('theme.change', this.changeTheme);
    if (this.instance) {
      this.instance.editor.destroy()
      this.instance = null
    }
  }
}
</script>
<style lang="scss">
@import '@dataspherestudio/cyeditor/index.scss';
.cyeditor {
  position: relative;
  height: calc(100% - 36px);
  @include bg-color(#fff, $dark-base-color);
}
.fullScreenCyeditor {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
.cy-editor-container {
  height: 100%;
  .cyeditor-dom-node {
    padding: 5px;
    display: flex !important;
    align-items: center;
    border: 1px solid #eee;
    overflow: visible;
    img {
      width: 28px;
      height: 28px;
    }
    span {
      display: inline-block;
      width: 120px;
      height: 28px;
      line-height: 28px;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  .tool-overlay {
    position: absolute;
    left: 205px;
    @include bg-color(#f7f9fb, $dark-workflow-bg-color);
    // background: #f7f9fb;
    top: calc(100% - 168px);
  }
  &.hide_shapes_panel {
    .tool-overlay {
      left: 5px;
    }
  }
}
.run-status-content {
    position: relative;
    top: -18px;
    right: -75px;
    .executor-pending {
      position: absolute;
      @include bg-color(#fff, $dark-workflow-bg-color);
      border: 2px solid #666;
      border-radius: 4px;
      padding: 3px;
      height: 28px;
      line-height: 28px;
      display: flex;
      align-items: center;
    }
    .executor-pending:after,
    .executor-pending:before {
      top: 100%;
      left: 50%;
      border: solid transparent;
      content: ' ';
      height: 0;
      width: 0;
      position: absolute;
      pointer-events: none;
    }

    .executor-pending:after {
      border-top-color: #fff;
      border-width: 7px;
      margin-left: -23px;
    }
    .executor-pending:before {
      border-top-color: #666;
      border-width: 10px;
      margin-left: -26px;
    }
  }
.hover-title {
  left: 400px;
  top: 200px;
  position: absolute;
  padding: 4px;
  @include bg-color(#fff, $dark-workflow-bg-color);
  @include font-color($text-color, $dark-workflow-font-color);
  // background-color: #fff;
  // border: 1px solid #999;
}
.run-status {
  position: absolute;
  @include bg-color(#fff, $dark-workflow-bg-color);
  @include font-color($text-color, $dark-workflow-font-color);
}
</style>
