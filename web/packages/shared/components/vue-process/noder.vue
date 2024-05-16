<template lang="html">
  <div
    :style="getNodeStyle"
    :title="title"
    class="node-box"
    :class="{'node-disabled': isDisabled, 'node-selected': isChoose}"
    @click="click"
    @dblclick="dblclick"
    @mousedown.stop="mousedown"
    @mouseup="mouseup"
    @contextmenu.prevent.stop>
    <!-- node.template 存在则创建对应组件作为节点 否则为普通节点-->
    <template v-if="selfNode.render || selfNode.template ">
      <component :is="component" v-if="component" :ref="k" @nodeDetail="nodeDetail" :k="k" :data="selfNode.data" />
    </template>
    <template v-else>
      <canvas ref="canvas" class="node-box-bg" />
      <div
        :title="title"
        :style="{'line-height': height * state.baseOptions.pageSize + 'px', left: width * 0.3 > 25 ? '25px' :  width * 0.3 + 'px' }"
        class="node-box-content">
        {{ title }}
      </div>
      <!-- 【0：未执行；1：运行中；2：已成功；3：已失败；4：已跳过】 -->
      <span
        v-if="runState.isShowTime"
        class="executor-pending">
        {{executorTime}}
      </span>
      <SvgIcon v-if="runState.isSvg" class="executor-icon" :style="{'font-size': nodeStatusIconSIze + 'px' }" :title="runState.title" :icon-class="runState.iconType" :color="runState.borderColor"/>
      <Icon
        v-else
        :title="runState.title"
        :type="runState.iconType"
        class="executor-icon" :class="runState.colorClass"
        :size="nodeStatusIconSIze"></Icon>
    </template>
    <template v-if="!state.mapMode">
      <div v-for="(arrow, index) in arrows"
        :key="index"
        :style="getNodeAnchorStyle(arrow)"
        class="node-anchor"
        @mousedown.stop="clickAnchor(arrow, $event)" />
    </template>
    <div v-if="runState.outerText && selected" :style="runState.outerStyle" >{{ runState.outerText }}</div>
  </div>
</template>
<script>
import Vue from 'vue';
import contentMenu from './contentMenu.js';
import { mapActions, commit, mixin } from './store';
import { findComponentUpward, getKey } from './util.js';

export default {
  mixins: [mixin],
  props: {
    k: {
      type: [String, Number],
      required: true
    },
    type: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: true
    },
    x: {
      type: Number,
      required: true
    },
    y: {
      type: Number,
      required: true
    },
    height: {
      type: Number,
      required: true
    },
    width: {
      type: Number,
      required: true
    },
    borderWidth: {
      type: Number,
      required: true
    },
    borderColor: {
      type: String,
      required: true
    },
    radiusWidth: {
      type: Number,
      required: true
    },
    anchorSize: {
      type: Number,
      required: true
    },
    image: {
      type: String,
      required: true
    },
    runState: {
      type: [Object],
      default() {
        return {}
      }
    },
    selected: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    nodeAnchors: {
      type: Array,
      dafault: () => []
    }
  },
  data() {
    return {
      component: null,
      currentBorderColor: this.borderColor,
      isChoose: this.selected,
    };
  },
  computed: {
    isDisabled() {
      return this.disabled;
    },
    getNodeStyle() {
      let styelObj
      if (this.state.mapMode && this.nodeType !== 'canvas') {
        styelObj = {
          'left': this.x * this.state.baseOptions.pageSize+ 'px',
          'top': this.y * this.state.baseOptions.pageSize+ 'px',
          'height': this.height + 'px',
          'width': this.width + 'px',
          'transform': `scale(${this.state.baseOptions.pageSize})`,
          'transform-origin': `top left`
        }
      } else {
        styelObj = {
          'left': this.x * this.state.baseOptions.pageSize + 'px',
          'top': this.y * this.state.baseOptions.pageSize + 'px',
          'height': this.height * this.state.baseOptions.pageSize + 'px',
          'width': this.width * this.state.baseOptions.pageSize + 'px'
        }
      }
      if (this.nodeType == 'canvas') {
        styelObj = {
          ...styelObj,
          'background-image': `url(${this.image})`,
          'background-repeat': 'no-repeat',
          'background-position': '5px center',
          'background-size': '11%'
        }
      }
      return {
        ...styelObj,
        ...this.runState.nodeStyle
      };
    },
    getImageStyle() {
      return {
        transform: `translate(-50%, -50%) scale(${this.state.baseOptions.pageSize})`
      };
    },
    selfNode() {
      return this.getNodeByKey(this.k);
    },
    nodeType() {
      if (this.selfNode && (this.selfNode.render || this.selfNode.template)) {
        return 'html'
      }
      return 'canvas'
    },
    executorTime() {
      return this.runState.time;
    },
    nodeStatusIconSIze() {
      return 14 * this.state.baseOptions.pageSize;
    },
    arrows() {
      if (this.nodeAnchors) {
        return this.nodeAnchors
      }
      return this.state.arrows
    }
  },
  watch: {
    'runState': {
      handler() {
        if (this.runState.borderColor) {
          this.currentBorderColor = this.runState.borderColor;
        }
      },
      deep: true
    },
    width() {
      if (this.nodeType == 'canvas') {
        this.initCanvas();
      }
    },
    selected(v) {
      this.isChoose = v
    }
  },
  created() {
    // 渲染函数 或 vue.extend 组件构造器
    if (this.nodeType == 'html' && typeof this.selfNode.render == 'function') {
      this.component = Vue.component(`html-node-${this.k}`, this.selfNode.render)
    } else if (typeof this.selfNode.template == 'function') { // vue.extend 组件构造器
      this.component = Vue.component(`html-node-${this.k}`, this.selfNode.template)
    }
  },
  mounted() {
    this.designer = findComponentUpward(this, 'Designer');
    this.shape = null;
    this.designer.myShapes.forEach(item => {
      if (item.type === this.type) {
        this.shape = item
      } else {
        if (item.children && item.children.length) {
          item.children.forEach(son => {
            if (son.type === this.type) {
              this.shape = son
            }
          })
        }
      }
    })
    if (this.nodeType == 'html') { return }
    this.initCanvas();
    this.$watch('state.baseOptions.pageSize', function () {
      this.initCanvas();
    });
    this.$watch('currentBorderColor', function () {
      this.initCanvas();
    });
    this.$watch('state.choosing', function (v) {
      if (v && v.type == 'node') {
        this.isChoose = v.key.indexOf(this.k) > -1
      }
    });
  },
  beforeDestroy() {
    if (this.contentMenu) {
      this.contentMenu.destroy();
      this.contentMenu = null;
    }
  },
  methods: {
    nodeDetail(node) {
      this.designer.$emit('node-detail', node)
    },
    ...mapActions(['setDraging', 'clearDraging', 'getNodeByKey']),
    initCanvas() {
      if (!this.canvasCtx) {
        this.canvas = this.$refs.canvas;
        this.canvasCtx = this.canvas.getContext('2d');
      }
      // 设置canvas宽度和高度
      this.canvas.height = this.height * this.state.baseOptions.pageSize;
      this.canvas.width = this.width * this.state.baseOptions.pageSize;
      let ctx = this.canvasCtx;
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
      ctx.lineWidth = this.borderWidth * this.state.baseOptions.pageSize;
      ctx.strokeStyle = this.currentBorderColor;
      ctx.lineCap = 'round'
      ctx.lineJoin = 'round'
      this.roundedRect(
        ctx,
        this.borderWidth / 2 * this.state.baseOptions.pageSize,
        this.borderWidth / 2 * this.state.baseOptions.pageSize,
        (this.width - this.borderWidth) * this.state.baseOptions.pageSize,
        (this.height - this.borderWidth) * this.state.baseOptions.pageSize,
        this.radiusWidth * this.state.baseOptions.pageSize
      );
    },
    roundedRect(ctx, x, y, width, height, radius) {
      ctx.beginPath();
      ctx.moveTo(x, y + radius);
      ctx.lineTo(x, y + height - radius);
      ctx.quadraticCurveTo(x, y + height, x + radius, y + height);
      ctx.lineTo(x + width - radius, y + height);
      ctx.quadraticCurveTo(x + width, y + height, x + width, y + height - radius);
      ctx.lineTo(x + width, y + radius);
      ctx.quadraticCurveTo(x + width, y, x + width - radius, y);
      ctx.lineTo(x + radius, y);
      ctx.quadraticCurveTo(x, y, x, y + radius);
      ctx.stroke();
    },

    // 要考虑到盒子模型
    getNodeAnchorStyle(arrow) {
      let anchorWidth = this.anchorSize * this.state.baseOptions.pageSize;
      let boxWidth = this.width * this.state.baseOptions.pageSize;
      let boxHight = this.height * this.state.baseOptions.pageSize;
      let boxBorderWidth = this.borderWidth * this.state.baseOptions.pageSize;
      let style = {
        'height': anchorWidth + 'px',
        'width': anchorWidth + 'px',
        'border-radius': anchorWidth / 2 + 'px'
      };
      if (arrow == 'top') {
        return Object.assign(style, {
          left: boxWidth / 2 - anchorWidth / 2 + 'px',
          top: -anchorWidth / 2 + boxBorderWidth / 2 + 'px'
        });
      }
      if (arrow == 'bottom') {
        return Object.assign(style, {
          left: boxWidth / 2 - anchorWidth / 2 + 'px',
          top: boxHight - anchorWidth / 2 - boxBorderWidth / 2 + 'px'
        });
      }
      if (arrow == 'left') {
        return Object.assign(style, {
          left: -anchorWidth / 2 + boxBorderWidth / 2 + 'px',
          top: (boxHight - anchorWidth) / 2 + 'px'
        });
      }
      if (arrow == 'right') {
        return Object.assign(style, {
          left: boxWidth - anchorWidth / 2 - boxBorderWidth / 2 + 'px',
          top: (boxHight - anchorWidth) / 2 + 'px'
        });
      }
      // {x,y}
      if (arrow.x !== undefined && arrow.y !== undefined) {
        return Object.assign(style, {
          left: arrow.x - anchorWidth / 2 - boxBorderWidth / 2 + 'px',
          top: arrow.y - anchorWidth / 2 - boxBorderWidth / 2 + 'px'
        });
      }
    },
    showMenu(e) {
      if (this.contentMenu) {
        this.contentMenu.destroy();
        this.contentMenu = null;
      }
      let { defaultMenu, userMenu, beforeShowMenu } = this.designer.myMenuOptions;
      let arr = [
        ...defaultMenu
      ]
      if (userMenu && Array.isArray(userMenu)) {
        arr = arr.concat(userMenu);
      }
      /**
       * 处理图标
       */
      function iconHelper (arr = []) {
        return arr.map((it) => {
          let menuItem = {
            text: it.text,
            value: it.value,
          }
          if (it.img) {
            menuItem.img = it.img;
          } else if (it.icon) {
            menuItem.icon = it.icon;
          }
          if (it.children) {
            menuItem.children = iconHelper(it.children)
          }
          return menuItem
        });
      }
      if (typeof beforeShowMenu === 'function') {
        arr = beforeShowMenu(this.selfNode, arr, 'node');
        if (Array.isArray(arr)) {
          arr = iconHelper(arr)
        } else {
          console.warn('ctxMenuOptions.beforeShowMenu' + this.$t('message.workflow.process.returnRule'))
        }
      }
      if (arr.length < 1) { return }
      this.contentMenu = contentMenu({
        data: arr,
        left: e.clientX,
        top: e.clientY,
        choose: (data) => {
          // if (this.state.disabled) return;
          this.designer.$emit(`on-ctx-menu`, data.value, this.selfNode, 'node')
          this.$emit('operat-node', data.value, this.k);
        }
      })
    },
    mousedown(e) {
      this._is_drag = false;
      // 点击鼠标左键
      if (e.which == 1) {
        if (this.state.disabled) return;
        let keys = [];
        if (this.state.choosing.type == 'node') {
          keys = e.ctrlKey || this.state.choosing.key.length > 1 ? [...this.state.choosing.key] : []
        }
        if (keys.indexOf(this.k) < 0) {
          keys.push(this.k);
        }
        let selectedNodesData = {};
        let nodes = [];
        this.state.nodes.forEach((node) => {
          if (keys.indexOf(node.key) > -1) {
            selectedNodesData[node.key] = {
              beginX: parseInt(node.x),
              beginY: parseInt(node.y)
            }
            nodes.push(node)
          }
        })
        // if (this.designer) {
        //   commit(this.$store, 'UPDATE_CHOOSING', {
        //     type: 'node',
        //     key: keys
        //   });
        // }
        this.setDraging({
          type: 'node',
          data: {
            key: this.k,
            beginX: parseInt(this.x),
            beginY: parseInt(this.y),
            beginPageX: e.pageX,
            beginPageY: e.pageY,
            selectedNodesBegin: selectedNodesData,
            nodes: nodes
          }
        });
      }

      this._mouseBeginX = e.clientX;
      this._mouseBeginY = e.clientY;
    },
    mouseup(e) {
      // 点击鼠标右键
      if (e.which == 3) {
        this.showMenu(e);
      }
      if (this.state.disabled) return;
      this._mouseEndX = e.clientX;
      this._mouseEndY = e.clientY;

      if (this.isdrag(this._mouseBeginX, this._mouseBeginY, this._mouseEndX, this._mouseEndY)) {
        this._is_drag = true;
        e.preventDefault();
        return false;
      } else {
        let keys = [];
        if (e.ctrlKey && this.state.choosing.type == 'node') {
          keys = this.state.choosing.key || []
        }
        if (keys.indexOf(this.k) < 0) {
          keys.push(this.k);
        } else {
          keys = keys.filter(it => it != this.k)
        }
        if (this.designer) {
          commit(this.$store, 'UPDATE_CHOOSING', {
            type: 'node',
            key: keys
          });
        }
      }
    },
    click(e) {
      // if (this.state.disabled) return; // 由外面控制
      if (this._is_drag || e.ctrlKey) {
        e.preventDefault();
        e.stopPropagation();
        return false;
      }
      this.designer.$emit('node-click', this.selfNode)
    },
    isdrag(x1, y1, x2, y2) {
      if (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) > 4) {
        return true;
      }
      return false;
    },
    clickAnchor(arrow, e) {
      if (this.state.disabled) return;
      let beginNode = this.selfNode;
      let endNodeArrow = arrow;
      let x = this.x;
      let y = this.y;
      let endNodeArrows = {
        top: 'bottom',
        bottom: 'top',
        left: 'right',
        right: 'left'
      };
      if (endNodeArrows[arrow]) {
        endNodeArrow = endNodeArrows[arrow];
        if (endNodeArrow == 'top') {
          y = y + this.height;
        }
        if (endNodeArrow == 'bottom') {
          y = y - this.height;
        }
        if (endNodeArrow == 'left') {
          x = x + this.width;
        }
        if (endNodeArrow == 'right') {
          x = x - this.width;
        }
      } else {
        //
      }
      let endNode = Object.assign({}, beginNode, {
        key: getKey(),
        x: x,
        y: y
      })
      this.setDraging({
        type: 'link',
        data: {
          key: getKey(),
          beginNode: beginNode,
          beginNodeArrow: arrow,
          endNode: endNode,
          endNodeArrow: endNodeArrow,
          beginX: e.pageX - this.state.baseOptions.nodeViewOffsetX,
          beginY: e.pageY - this.state.baseOptions.nodeViewOffsetY,
          endX: 0,
          endY: 0,
          ...this.state.linkerOptions
        }
      });
    },
    dblclick() {
      if (this.designer) {
        this.designer.$emit('node-dblclick', this.selfNode)
      }
    },

    /**
     * template创建的vue compnent节点盒子layout属性
     */
    getHtmlNodeRect() {
      if (this.$refs[this.k]) {
        let nodeComp = this.$refs[this.k]
        return nodeComp.$el.getBoundingClientRect()
      }
    }
  }
};
</script>
