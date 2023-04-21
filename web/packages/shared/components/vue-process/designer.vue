<template>
  <div :style="getStyle()" :class="{'show-toolbar': showActionView}" class="designer" @mousemove="moveShape" @mouseup="stopMoveShape">
    <ShapeView v-show="showViews.shapeView && !shapeFold" ref="shapeView" :shapes="myShapes" :shapeFold="shapeFold" @on-toggle-shape="toggleShape" />
    <div class="designer-expand" v-if="shapeFold" @click="toggleShape">
      <SvgIcon icon-class="unfold" />
    </div>
    <ActionView v-if="showActionView">
      <slot />
    </ActionView>
    <ControlView v-if="showViews.control"
      @format="format"
      @resetToOriginalData="resetToOriginalData"
      @viewNode="nodeScroolIntoView" />
    <NodeView ref="designerView"
      :shapeFold="shapeFold"
      @box-select="boxSelectChange"
      @node-view-scroll="nodeViewScroll" />
    <BaseInfo v-if="state.editBaseInfoNode" v-clickoutside="closeBaseInfo" :node="state.editBaseInfoNode" />
    <ParamInfo v-if="state.editParamNode" v-clickoutside="closeParam" :node="state.editParamNode" />
  </div>
</template>
<style lang="scss" scope src="./style/index.scss"></style>
<script>
import './iconfont.js'
import ControlView from './controlView.vue'
import ActionView from './actionView.vue';
import ShapeView from './shapeView.vue';
import NodeView from './nodeView.vue';
import BaseInfo from './baseInfo.vue'
import ParamInfo from './paramInfo.vue'
import { mapActions, commit, mixin } from './store';
import clickoutside from './clickoutside.js';
import { getKey } from './util.js';

const defaultViewOptions = {
  showBaseInfoOnAdd: true,
  shapeView: true,
  control: true
};

const ctxMenuOptions = {
  defaultMenu: {
    copy: true,
    delete: true,
    config: true,
    param: true
  },
  userMenu: [],
  beforeShowMenu: function (node, arr) {
    return arr
  },
  beforePaste: function (node) { return node }
}

export default {
  name: 'Designer',
  directives: {
    clickoutside
  },
  components: {
    ControlView,
    ActionView,
    ShapeView,
    NodeView,
    BaseInfo,
    ParamInfo
  },
  mixins: [mixin],
  props: {
    value: {
      type: Object,
      default() {
        return {}
      }
    },
    shapes: {
      type: Array,
      default() {
        return []
      }
    },
    disabled: {
      type: Boolean,
      default: false
    },
    mapMode: {
      type: Boolean,
      default: false
    },
    gridOptions: {
      type: Object,
      default() {
        return {}
      }
    },
    nodeOptions: {
      type: Object,
      default() {
        return {}
      }
    },
    viewOptions: {
      type: Object,
      default() {
        return defaultViewOptions
      }
    },
    ctxMenuOptions: {
      type: Object,
      default() {
        return ctxMenuOptions
      }
    }
  },
  data() {
    return {
      showViews: Object.assign({ ...defaultViewOptions }, this.viewOptions),
      nodeViewScrollTop: 0,
      nodeViewScrollLeft: 0,
      showActionView: this.$slots.default !== undefined,
      shapeFold: false,
    }
  },
  computed: {
    myShapes() {
      let myShapes = [];
      if (this.shapes && this.shapes.length > 0) {
        this.shapes.forEach((element) => {
          this.$set(element, 'show', true);
          myShapes.push(element);
        });
      }
      return myShapes;
    },
    myMenuOptions() {
      let defaultMenu = []
      if (this.ctxMenuOptions) {
        let defaultShow = { ...ctxMenuOptions.defaultMenu, ...this.ctxMenuOptions.defaultMenu }
        if (defaultShow.copy) {
          defaultMenu.push({ icon: 'fuzhi', text: this.$t('message.workflow.vueProcess.copy'), value: 'copy' })
        }
        if (defaultShow.delete) {
          defaultMenu.push({ icon: 'shanchu', text: this.$t('message.workflow.vueProcess.delete'), value: 'delete' })
        }
        if (defaultShow.param) {
          defaultMenu.push({ icon: 'canshu', text: this.$t('message.workflow.vueProcess.parameterSet'), value: 'param' })
        }
        if (defaultShow.config) {
          defaultMenu.push({ icon: 'peizhi', text: this.$t('message.workflow.vueProcess.basic'), value: 'config' })
        }
      }
      return Object.assign({ ...ctxMenuOptions }, { ...this.ctxMenuOptions, defaultMenu })
    }
  },
  watch: {
    viewOptions(v) {
      let shapeView = this.showViews.shapeView;
      this.showViews = Object.assign({}, defaultViewOptions, v);
      if (shapeView !== this.showViews.shapeView) {
        commit(this.$store, 'UPDATE_SHAPE_OPTIONS', { viewWidth: this.showViews.shapeView ? 180 : 0 });
        this.$nextTick(() => {
          if (this.$refs.designerView) {
            this.$refs.designerView.initView();
          }
        })
      }
      if (this.showViews.linkType) {
        commit(this.$store, 'UPDATE_LINKTYPE', this.showViews.linkType)
      }
    }
  },
  created() {
    if (this.viewOptions.linkType) {
      commit(this.$store, 'UPDATE_LINKTYPE', this.viewOptions.linkType)
    }
    this.initDeginer({
      value: this.value,
      shapes: this.shapes,
      gridOptions: this.gridOptions,
      nodeOptions: this.nodeOptions,
      mapMode: this.mapMode
    });
    this.setMapMode(this.mapMode);
    this.setDisabled(this.disabled);
    this.$watch('value', function () {
      // 加个异步执行，避免clickOut事件覆盖操作
      setTimeout(() => {
        this.initDeginer({
          value: this.value,
          shapes: this.shapes
        });
        if (!this.$refs.designerView) return;
        this.$refs.designerView._updateCalLinks();
      },0)

    });

    this.$watch('disabled', function () {
      this.setDisabled(this.disabled);
    });
    this.$watch('mapMode', function() {
      this.setMapMode(this.mapMode);
    })
    this.$watch('state.nodes', function () {
      let result = this.getResult('nodes')
      this.$emit('change', result);
    });

    this.$watch('state.links', function () {
      let result = this.getResult('links')
      this.$emit('change', result);
    });
  },
  mounted() {
    this._cacheChange = {};
    commit(this.$store, 'UPDATE_SHAPE_OPTIONS', { viewWidth: this.showViews.shapeView ? 180 : 0 });
    this.$nextTick(() => {
      if (this.$refs.designerView) {
        this.$refs.designerView.initView();
      }
    })
  },
  methods: {
    ...mapActions(['initDeginer', 'setDisabled', 'setDraging', 'clearDraging', 'getNodeByKey', 'setMapMode', 'deleteNode']),
    getStyle() {
      if (this.state.fullScreen) {
        return {
          'position': 'fixed',
          'left': 0,
          'right': 0,
          'top': 0,
          'bottom': 0,
          'z-index': 100
        }
      }
    },
    getResult(type) {
      // 连线关系
      let edges = [];
      // 节点
      let nodes = [];

      if (this._cacheChange.links && type == 'nodes') {
        edges = this._cacheChange.links;
      } else {
        this.state.links.forEach(link => {
          edges.push({
            source: link.beginNode.key,
            target: link.endNode.key,
            label: link.label,
            data: link.data,
            sourceLocation: link.beginNodeArrow,
            targetLocation: link.endNodeArrow
          })
        });
        this._cacheChange.links = edges;
      }
      if (this._cacheChange.nodes && type == 'links') {
        nodes = this._cacheChange.nodes;
      } else {
        this.state.nodes.forEach(node => {
          let innerKeys = ['layout', 'type', 'title', 'desc', 'image', 'ready', 'viewOffsetX', 'viewOffsetY',
            'width', 'height', 'borderWidth',
            'radiusWidth', 'anchorSize', 'borderColor', 'key', 'x', 'y', 'createTime', 'modifyTime'];
          let obj = {
            key: node.key,
            title: node.title,
            desc: node.desc,
            type: node.type,
            layout: {
              height: node.height,
              width: node.width,
              x: node.x,
              y: node.y
            },
            params: node.params,
            resources: node.resources,
            createTime: node.createTime || Date.now(),
            modifyTime: node.modifyTime || Date.now()
          }
          // 把跟流程无关的数据原样返回
          for (let p in node) {
            if (innerKeys.indexOf(p) === -1) {
              obj[p] = node[p]
            }
          }
          nodes.push(obj)
        })
        this._cacheChange.nodes = nodes;
      }

      return {
        edges,
        nodes
      }
    },
    nodeViewScroll({ scrollTop, scrollLeft }) {
      this.nodeViewScrollTop = scrollTop;
      this.nodeViewScrollLeft = scrollLeft;
    },
    addNode(e) {
      let shape = this.state.draging.data;
      let node = Object.assign(
        {
          type: shape.type,
          title: shape.title,
          desc: '',
          image: shape.image,
          template: shape.template,
          render: shape.render,
          ready: false
        },
        this.state.nodeOptions,
        {
          key: getKey(),
          x: (e.pageX - this.state.baseOptions.nodeViewOffsetX - this.state.nodeOptions.width * this.state.baseOptions.pageSize / 2) / this.state.baseOptions.pageSize + this.nodeViewScrollLeft / this.state.baseOptions.pageSize,
          y: (e.pageY - this.state.baseOptions.nodeViewOffsetY - this.state.nodeOptions.height * this.state.baseOptions.pageSize / 2) / this.state.baseOptions.pageSize + this.nodeViewScrollTop / this.state.baseOptions.pageSize
        }
      )
      commit(this.$store, 'ADD_NODE', node);
      this._new_node = node;
      this.setDraging({
        type: 'shape',
        data: node
      });
      setTimeout(()=>{
        if (node.template || node.render) {
          const index = this.state.nodes.findIndex(item => item.key === node.key)
          let nodeComp = this.$refs.designerView.$refs.nodes[index]
          const nodeRect = nodeComp.getHtmlNodeRect()
          if (node.width !== nodeRect.width || node.height !== nodeRect.height) {
            let height = node.height > nodeRect.height ? node.height : nodeRect.height
            const newNode = {...node, width: nodeRect.width, height: height}
            this.$refs.designerView.updateNode(node.key, newNode)
          }
        }
      }, 20)
    },
    toggleShape() {
      this.shapeFold = !this.shapeFold;
      this.$emit('toggle-shape', this.shapeFold);
      commit(this.$store, 'UPDATE_SHAPE_OPTIONS', { viewWidth: !this.shapeFold ? 180 : 0 });
      setTimeout(() => {
        this.layoutView()
      }, 400)
    },
    moveShape(e) {
      if (this.state.disabled) return;
      if (this.state.draging.type == 'shape') {
        this.$refs.shapeView.changeDragShapOffset(e);
        // 超出临界
        if (e.pageX - this.state.baseOptions.shapeViewOffsetX > this.state.shapeOptions.viewWidth - this.state.shapeOptions.width / 2) {
          if (this.state.nodes.length == 0) {
            this.addNode(e);
          } else {
            let lasNode = this.state.nodes[this.state.nodes.length - 1];
            // 拖拽的已经存在画布了，更新位置
            if (!lasNode.ready && lasNode.key == this.state.draging.data.key) {
              commit(this.$store, 'UPDATE_NODE', {
                key: lasNode.key,
                obj: Object.assign(lasNode, {
                  x: (e.pageX - this.state.baseOptions.nodeViewOffsetX - this.state.nodeOptions.width * this.state.baseOptions.pageSize / 2) / this.state.baseOptions.pageSize + this.nodeViewScrollLeft / this.state.baseOptions.pageSize,
                  y: (e.pageY - this.state.baseOptions.nodeViewOffsetY - this.state.nodeOptions.height * this.state.baseOptions.pageSize / 2) / this.state.baseOptions.pageSize + this.nodeViewScrollTop / this.state.baseOptions.pageSize
                })
              });
            } else {
              this.addNode(e);
            }
          }
        }
      }
    },
    stopMoveShape(e) {
      if (this.state.disabled) return;
      if (this.state.draging.type == 'shape' && this.state.nodes.length > 0) {
        let lasNode = this.state.nodes[this.state.nodes.length - 1];
        if (e.pageX - this.state.baseOptions.shapeViewOffsetX > this.state.shapeOptions.viewWidth) {
          // 获取创建用户，适应dss，如果没有给空
          let creator = '';
          if (localStorage.getItem('baseInfo') && localStorage.getItem('baseInfo').username) {
            creator =  localStorage.getItem('baseInfo').username;
          }
          // 超出临界，保存节点
          commit(this.$store, 'UPDATE_NODE', {
            key: lasNode.key,
            obj: Object.assign(lasNode, {
              createTime: Date.now(),
              ready: true,
              creator
            })
          });
          // 默认打开基础信息配置
          if (this.showViews.showBaseInfoOnAdd) {
            commit(this.$store, 'UPDATE_EDIT_BASEINFO_NODE', lasNode);
          }
        } else {
          // 不保存节点
          if (!lasNode.ready) {
            commit(this.$store, 'DELETE_NODE', this.state.nodes.length - 1);
          }
          this._new_node = null;
        }
        if (this._new_node) {
          this.$emit('add', { ...this._new_node });
          this._new_node = null;
        }
        this.clearDraging();
      }
      if (!e.shiftKey && !e.ctrlKey) {
        let hasOuter = false
        const nodes = this.state.nodes.map(item => {
          if (item.runState && item.runState.outerText) {
            hasOuter = true
          }
          return {
            ...item,
            runState: item.runState && item.runState.outerText ? undefined : item.runState
          }
        });
        if (hasOuter) {
          commit(this.$store, 'UPDATE_ALLNODES', nodes)
        }
      }
    },
    closeBaseInfo() {
      // 清除这个修改中的节点
      commit(this.$store, 'UPDATE_EDIT_BASEINFO_NODE', null);
    },
    closeParam() {
      commit(this.$store, 'UPDATE_EDIT_PARAM_NODE', null);
    },

    /**
         * 设置节点的运行状态
         * @param [String] key
         * @param [Object] state
         */
    setNodeRunState(key, state, readonly ) {
      if (readonly && this.state.disabled) return
      let node = this.getNodeByKey(key);
      if (node) {
        this.$set(node, 'runState', state);
        let result = this.getResult('nodes');
        this.$emit('change', result);
      }
    },

    // 在controlView.vue的弹框中点击 格式化 后，调用nodeView.vue中的autoFormat方法进行触发
    format(data) {
      this.$refs.designerView.autoFormat(data)
    },
    // 恢复到格式化前的状态
    resetToOriginalData() {
      this.$refs.designerView.resetFormat()
    },
    // 获得所选择节点
    getSelectedNodes() {
      return this.state.nodes.filter(node => node.selected).map(n => ({ ...n }))
    },
    getState() {
      return this.state;
    },
    boxSelectChange() {
      let result = this.getResult('nodes');
      this.$emit('change', result);
    },
    nodeScroolIntoView(node) {
      this.$refs.designerView.$el.scrollTo(node.x - 100, node.y - 100)
    },
    getHtmlNodeRect(k){
      const index = this.state.nodes.findIndex(item => item.key === k)
      if (index > -1) {
        const nodeComp = this.$refs.designerView.$refs.nodes[index]
        return nodeComp.getHtmlNodeRect()
      }
    },
    layoutView() {
      this.$refs.designerView.initView()
      this.$refs.shapeView.initView()
    }
  }
};
</script>
