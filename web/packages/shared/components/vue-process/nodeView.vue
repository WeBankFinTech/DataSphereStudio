<template lang="html">
  <!-- 摆放节点的大工作区 -->
  <div ref="viewport" :style="getBaseStyle"
    class="designer-viewport"
    :class="{'designer-viewport-fold': shapeFold}"
    @mousemove="mousemove"
    @mousedown="mousedown"
    @mouseup="mouseup"
    @contextmenu.prevent="showMenu">
    <!-- 大工作区的背景 -->
    <div :style="getGridStyle" />

    <!-- 节点 -->
    <div v-clickoutside="clickoutsideNode">
      <Noder v-for="(node) in state.nodes"
        ref="nodes"
        :key="node.key"
        :k="node.key"
        :type="node.type"
        :title="node.title"
        :x="node.x"
        :y="node.y"
        :height="node.height"
        :width="node.width"
        :radius-width="node.radiusWidth"
        :border-width="node.borderWidth"
        :border-color="node.borderColor"
        :anchor-size="node.anchorSize"
        :image="node.image"
        :selected="node.selected"
        :run-state="node.runState"
        :node-anchors="node.nodeAnchors"
        @operat-node="operatNode" />
    </div>

    <!-- 连线 -->
    <div v-clickoutside="clickoutsideLink">
      <Linker v-for="(link) in state.links"
        :key="link.key"
        :k="link.key"
        :begin-node="link.beginNode"
        :begin-node-arrow="link.beginNodeArrow"
        :end-node="link.endNode"
        :end-node-arrow="link.endNodeArrow"
        :line-width="link.lineWidth"
        :link-type="link.linkType || state.linkType"
        :border-width="link.borderWidth"
        :ext-width="link.extWidth"
        :label="link.label"
        :border-color="link.borderColor"
        @after-calculate="afterCalculate" />
      <canvas ref="canvas" width="10000" height="10000" class="canvas" />
    </div>

    <!--是两条相互垂直的线，在拖拽时用来作为对齐的基线 -->
    <div v-show="state.snapLineX.show" :style="getSnapLineXStyle()" class="snap-line" />
    <div v-show="state.snapLineY.show" :style="getSnapLineYStyle()" class="snap-line" />

    <Linker v-if="state.draging.type=='link'"
      :k="state.draging.data.key"
      :begin-node="state.draging.data.beginNode"
      :begin-node-arrow="state.draging.data.beginNodeArrow"
      :end-node="state.draging.data.endNode"
      :end-node-arrow="state.draging.data.endNodeArrow"
      :line-width="state.draging.data.lineWidth"
      :link-type="state.linkType"
      :border-width="state.draging.data.borderWidth"
      :ext-width="state.draging.data.extWidth"
      :border-color="state.draging.data.borderColor"
      class="drag-linker-box"
      @after-calculate="afterCalculate" />
    <div class="box-select-layer" :style="boxSelectLayerStyle" />
  </div>
</template>

<script>
// import grid from './grid.vue';
import Noder from './noder.vue';
import Linker from './linker.vue';
import clickoutside from './clickoutside.js';
import drawLinker from './drawLinkers.js';
import contentMenu from './contentMenu.js';
import { mapActions, commit, mixin } from './store';
import { findComponentUpward, getKey } from './util.js'
export default {
  directives: {
    clickoutside
  },
  components: {
    // grid,
    Noder,
    Linker
  },
  props: {
    shapeFold: {
      type: Boolean,
      default: false
    }
  },
  // 通过这个mixin引入store和state
  mixins: [mixin],
  data() {
    return {
      scrollTop: 0,
      scrollLeft: 0,
      // 每一次格式化前，保存当下的数据，用于 恢复到格式化之前的状态，这是一个数组，说明可以一直 恢复格式化之前的状态 到第一次格式化前
      initDatas: [],
      // 因为树是先横向摆，等到x值到一定的时候统一换行，所以也可以看成是某几棵树摆在一行，然后再换行，currentMaxRowY就是记录这一行最大的Y值
      currentMaxRowY: 0,
      boxSelectLayerStyle: {}
    };
  },
  computed: {
    // 获取大工作区的样式
    getBaseStyle() {
      return {
        left: this.state.shapeOptions.viewWidth + 'px'
      };
    },
    // 获取大工作区的背景
    getGridStyle() {
      return {
        height: this.state.gridOptions.height * this.state.baseOptions.pageSize + 'px',
        width: this.state.gridOptions.width * this.state.baseOptions.pageSize + 'px'
      }
    }
  },
  mounted() {
    this.designer = findComponentUpward(this, 'Designer');

    this.$refs.viewport.addEventListener('scroll', this.scrollAction);

    this.$watch('state.fullScreen', function () {
      this.initView();
    });

    this.$nextTick(this.initView)
    // this.autoFormat({
    //   nodeWidth: 200,
    //   linkType: 'straight'
    // })
  },
  updated() {
    // 在mounted里this.state.nodes.length为0，所以要在updated里写，注意要判断 别死循环
    if (this.state && this.state.nodes && this.state.nodes.length) {
      if (typeof this.state.nodes[0].width === 'number') {
        // 将全局的nodeWidth改为新的格式化后的nodeWidth，这样保持新拖进工作板的节点宽度也是和当前已有节点宽度一样
        this.state.nodeOptions.width = this.state.nodes[0].width
        commit(this.$store, 'UPDATE_NODE_OPTION', this.state.nodeOptions);
      }

      // 如果node有linkType属性，说明已经经过格式化了
      // node的linkType可能和当前的this.state.linkType不同，如果不同，就要进行更新
      if (this.state.nodes[0].linkType && this.state.nodes[0].linkType !== this.state.linkType) {
        // 将全局的连线方式改为格式化后的连线方式
        commit(this.$store, 'UPDATE_LINKTYPE', this.state.nodes[0].linkType);
      }
    }
  },
  beforeDestroy() {
    if (this.contentMenu) {
      this.contentMenu.destroy();
      this.contentMenu = null;
    }
    this.$refs.viewport.removeEventListener('scroll', this.scrollAction);
  },
  methods: {
    ...mapActions(['setDraging', 'clearDraging', 'deleteNode', 'getNodeByKey']),
    scrollAction(e) {
      this.scrollTop = e.srcElement.scrollTop;
      this.scrollLeft = e.srcElement.scrollLeft;
      this.$emit('node-view-scroll', {
        scrollTop: this.scrollTop,
        scrollLeft: this.scrollLeft
      })
    },
    initView() {
      let rect = this.$el.getBoundingClientRect();
      commit(this.$store, 'UPDATE_NODEVIEW_OFFSET', {
        x: rect.left,
        y: rect.top
      });
      // html 节点自动调整节点宽高并更新连线
      let chgNodes = []
      this.state.nodes.forEach((node, index)=>{
        if (node.template || node.render) {
          let nodeComp = this.$refs.nodes[index]
          const nodeRect = nodeComp.getHtmlNodeRect()
          if (node.width !== nodeRect.width || node.height !== nodeRect.height) {
            // 如果真是动态设置的高度，此时nodeRect.height会很小
            let height = node.height > nodeRect.height ? node.height : nodeRect.height
            const newNode = {...node, width: nodeRect.width, height: height}
            chgNodes.push(newNode)
            this.updateNode(node.key, newNode)
          }
        }
      })
      if (chgNodes.length) {
        this.state.links.forEach((link, index) => {
          const beginNode = chgNodes.find(n => {
            return n.key === link.beginNode.key
          })
          const endNode = chgNodes.find(n => {
            return n.key === link.endNode.key
          })
          let newLink = { ...link }
          if (beginNode) {
            newLink.beginNode = beginNode
          }
          if (endNode) {
            newLink.endNode = endNode
          }
          if (beginNode || endNode) {
            this.updateLink(index, newLink)
          }
        })
      }
    },

    // 一键格式化
    autoFormat(data) {
      const { nodes, links, nodeOptions, linkType } = this.state
      nodes.forEach((node) => {
        // 初始化childNodes和parentNodes
        node.childNodes = []
        node.parentNodes = []
        // 这两个属性必须要在这里初始化，不然可能会有无限引用的情况，无法JSON.stringify
      })
      // 保留一个原始数据，用于后面的 恢复到格式化前的状态 操作
      const initData = {
        initNodes: JSON.parse(JSON.stringify(nodes)),
        initLinks: JSON.parse(JSON.stringify(links)),
        // 初始节点宽度
        initNodeWidth: nodeOptions.width,
        // 初始连线类型 直线/斜线 等
        initLinkType: linkType
      }
      this.initDatas.push(initData)
      // x、y以及宽度高度的一些常量
      const cons = {
        // 第一棵树的起始x值
        initX: 100,
        // 第一棵树的起始y值
        initY: 100,
        // 虽然现在一个节点的宽高是定死的，但还是写成变量好一点，不然万一以后节点的宽高变了，很难改动
        // 一个节点的宽度
        nodeWidth: data.nodeWidth,
        // 一个节点的高度
        nodeHeight: 40,
        // 相邻两个节点的宽度间隙
        nodeWidthGap: 40,
        // 相邻两个节点的高度间隙
        nodeHeightGap: 100,
        // 相邻两棵树的宽度间隙
        treesWidthGap: 100,
        // 相邻两棵树的高度间隙
        treesHeightGap: 100,
        // 一行最多达到多少的x值之后就换行
        maxReturnX: 3000
      }
      // 无节点时不需要格式化
      // 直接emit message在外部使用时就能监测到了
      if (!(nodes && nodes.length > 0)) {
        return this.designer.$emit('message', {
          type: 'error',
          msg: this.$t('message.workflow.vueProcess.cannotBeFormatted')
        });
      }
      // 无连线时，将所有节点排列整齐
      if (!(links && links.length > 0)) {
        // 一排摆放多少个节点
        const accountInOneRow = 6
        nodes.forEach((node, index) => {
          node.x = cons.initX + index % accountInOneRow * (cons.nodeWidth + cons.nodeWidthGap)
          node.y = cons.initY + Math.floor(index / accountInOneRow) * (cons.nodeHeight + cons.nodeHeightGap)
          node.width = data.nodeWidth
          this.updateNode(node.key, node)
        })
        // 格式化之后，更新全局的nodeWidth, 全局的连线方式, 全局的isFormatted
        this.updateAfterFormat(data)
        return
      }
      // 按照层级存放节点
      let nodesWithLevel = {
        // 当前是否有跨级连接的情况
        crossLevelConnection: false,
        // 根节点们
        1: []
      }
      // 遍历nodes和links数组，把每个节点的子节点和父节点都先存下来，后面有用，这样拿到所有节点的父节点和子节点只需要遍历一次links数组
      nodes.forEach((node) => {
        // 该节点是否已经设置好了层级
        node.alreadySetLevel = false
        // 该节点是否已经匹配到某棵树里了，在这里初始化
        node.alreadyFindPair = false
        // 初始化 node.level
        node.level = 0
        // 初始化 node.allChildrenKeys 该节点的所有子孙节点的keys集合
        node.allChildrenKeys = []
        // 该节点是否已经设置好x和y值了，在跨级连接的格式化方法中用到，在这里初始化
        node.alreadySetXY = false
        // 在跨级连接的格式化中，该相关的所有节点都已经遍历过了
        node.alreadyFindAllConnectedNodes = false

        // 获取该节点的所有父节点和子节点
        links.forEach((link) => {
          // 说明此时node是link.endNode的父节点
          if (link.beginNode.key === node.key) node.childNodes.push(link.endNode)
          // 说明此时node是link.endNode的子节点
          if (link.endNode.key === node.key) node.parentNodes.push(link.beginNode)
        })
        // 拿到所有的根节点
        if (!node.parentNodes.length) {
          // 说明该node没有parentNode 是一个根节点
          node.level = 1
          nodesWithLevel['1'].push(node)
        }
      })
      // console.log('获取完根节点之后的：nodesWithLevel: ', nodesWithLevel);

      // 将所有节点按照层级进行存放 如果有跨级连接的现象this.getNodesWithLevel返回false，如果是正常的一层一层连接下来的，this.getNodesWithLevel返回nodesWithLevel对象
      this.setNodesWithLevel(nodesWithLevel, 1)
      // 这一步是分界点，会知道该树状图有无跨级连接的现象，在下面用两种不同的格式化方式对有、无跨级连接的树状图进行格式化
      // 因为树是先横向摆，等到x值到一定的时候统一换行，所以也可以看成是某几棵树摆在一行，然后再换行，currentMaxRowY就是记录这一行最大的Y值
      this.currentMaxRowY = 0
      // 说明当前没有跨级连接的现象，是一层一层连接下来的
      if (!nodesWithLevel.crossLevelConnection) {
        // 把所有节点拆分成n棵独立的数
        // 获取到根节点的所有子节点的key值数组
        nodesWithLevel['1'].forEach((node) => {
          // 第一层的节点的level值已设
          node.alreadySetLevel = true
          // 将该节点的所有子孙节点的key值放进node的allChildrenKeys属性中
          this.setAllChildrenKeys(node, node)
        })
        // 进行配对，区分出哪些根节点是在一棵树里的
        const treePairs = []
        nodesWithLevel['1'].forEach((node1, index) => {
          // 如果node1还没进行配对 再执行逻辑遍历其他节点
          if (!node1.alreadyFindPair) {
            const pair = [node1]
            nodesWithLevel['1'].forEach((node2, i) => {
              // 同一个节点不进行比较 且node2也还没配对
              if (index !== i && !node2.alreadyFindPair) {
                // 如果两个节点至少有一个相同的子节点，就返回true，说明是同一棵树
                if (this.hasSameChild(node1, node2)) {
                  pair.push(node2)
                  node2.alreadyFindPair = true
                }
              }
            })
            node1.alreadyFindPair = true
            treePairs.push(pair)
          }
        })
        // console.log('存放每一棵树根节点集合的 treePairs: ', treePairs);
        // 现在的treePairs是一个数组，每一项也还是一个数组，存放的是一棵树的所有根节点，现在要将treePairs的每一项，换成这棵树所有节点的key值数组
        treePairs.forEach((treePair, index) => {
          let newTreePair = []
          treePair.forEach((item) => {
            newTreePair.push(item.key)
            newTreePair = newTreePair.concat(item.allChildrenKeys)
          })
          // 对newTreePair去重
          // console.log('newTreePair: ', newTreePair);
          // 直接用 treePair = ... 不会生效，这里treePair是形参，要改treePairs还是要用treePairs[index]
          treePairs[index] = Array.from(new Set(newTreePair))
        })
        // console.log('将treePairs的每一项换成一棵树的所有节点之后的 treePairs: ', treePairs);
        // 根据根节点的配对和子节点的key值，从nodesWithLevel（按照层级存放的所有节点）中一层一层地取出每棵树所有的节点
        // 存放所有的树
        const trees = []
        treePairs.forEach((treePair) => {
          // 存放当前这棵树的所有节点
          const tree = []
          // treePair也还是一个数组，存放的是当前这棵树的所有根节点
          treePair.forEach((key) => {
            // 根据这个key，从nodes中取出这个node
            // 因为必须按照顺序取node，所以不能把所有key丢进去取，只能按照顺序一个一个依次来
            tree.push(this.findNodeByKey(key, nodes))
          })
          trees.push(tree)
        })
        // console.log('所有树，每棵树里存放的都是按照顺序摆放的所有节点 trees: ', trees);
        // 每一棵树的起始x值以及每一排的起始x值，顺序和tree在trees里面的顺序对应
        const treeXYs = [
          {
            initX: cons.initX,
            initY: cons.initY
          }
        ]
        trees.forEach((tree, index) => {
          // 用来存放每一排有多少个数量的节点
          const nodesPerRow = {}
          tree.forEach((item) => {
            if (!nodesPerRow[item.level]) {
              nodesPerRow[item.level] = 1
            } else {
              nodesPerRow[item.level] += 1
            }
          })
          // console.log('每一排有多少个数量的节点 nodesPerRow: ', nodesPerRow);
          // 获取：
          // 1、这一棵树最多节点的一排有多少个节点
          // 2、这一棵树最多节点的一排的width为多少
          // 3、这棵树的中线的x值
          // 4、算出这棵树每一排的起始x值并存进treeXs中
          // 5、这棵树的总高度
          // 6、下一棵树的起始x值 = 上一棵树的起始x值 + 上一棵树的最大宽度 + 两棵树的宽度间隙
          this.getRealXY(nodesPerRow, treeXYs, trees, index, cons)
        })
        // console.log('每一棵树的起始x值以及每一排的起始x值 treeXYs: ', treeXYs);
        // 根据每一棵树的key顺序以及其对应的level值，摆放每一个节点
        trees.forEach((tree, index) => {
          // 这棵树的各排的起始x值和y值
          const treeXY = treeXYs[index]
          tree.forEach((node) => {
            // locatedNodes指这一排已经摆放好的nodes的数量
            if (!treeXY[node.level].locatedNodes) {
              treeXY[node.level].locatedNodes = 1
            } else {
              treeXY[node.level].locatedNodes += 1
            }
            node.x = treeXY[node.level].x + (treeXY[node.level].locatedNodes - 1) * (cons.nodeWidth + cons.nodeWidthGap)
            node.y = treeXY[node.level].y
            // 把节点的宽度改为用户设置的宽度
            node.width = data.nodeWidth
            this.updateNode(node.key, node)
          })
        })
        // 把所有节点都改成由 父节点的下方 到 子节点的上方
        for (let i = 0; i < links.length; i++) {
          links[i].beginNodeArrow = 'bottom'
          links[i].endNodeArrow = 'top'
          this.updateLink(i, links[i])
        }
      } else {
        // 说明现在有跨级连接的现象
        // 存放所有的树
        const trees = []
        // 从一个节点开始，通过这个节点的所有关联节点，再找所有关联节点的关联节点，找到node1这棵树里的所有节点，并设置好XY值
        nodes.forEach((node1) => {
          // 还没设置好x和y值的节点才需要执行下面的逻辑
          if (!node1.alreadySetXY) {
            // 因为下面会迭代完一棵树的所有节点，所以在这里进来时一定是一棵树的第一个节点
            node1.x = 0
            node1.y = 0
            // 将node1放进trees数组中
            const tree = {
              nodes: [node1],
              currentMaxX: {
                // 设置 y===0 这一层当前的最大x值
                0: 0
              },
              // 当前tree对象是trees数组里的第几项
              index: trees.length
            }
            trees.push(tree)
            // 已经设置好XY的就不需要再重复设置了
            node1.alreadySetXY = true
            this.updateNode(node1.key, node1)
            if (!(node1.childNodes.length === 0 && node1.parentNodes.length === 0)) {
              // 说明node1肯定不是独立的节点，至少有一个子节点或一个父节点。如果是独立的节点，也就是一棵独立的树，按照上面的逻辑，设置成(0,0)并push进trees里就行了

              // 通过这个节点的所有关联节点，再找所有关联节点的关联节点，找到node1这棵树里的所有节点，并设置好XY值
              this.setAllConnectedNodesXY(trees, tree.index, links)
            }
          }
        })
        // console.log('从一个节点开始，通过这个节点的所有关联节点，再找所有关联节点的关联节点，找到node1这棵树里的所有节点，并设置好XY值 trees: ', trees);
        // 遍历trees数组，对于某一棵树，获取最多节点的那个层级（相同的Y值）有多少节点，算出最长那一层有多长，然后根据起始x值和总长度，算出（这一步和自顶向下的构建方式相同，只是自顶向下的构建方式中是通过每个节点唯一的level值来区分每一排的，这里是通过每个节点的y值来区分的）
        // 每一棵树的起始x值以及每一排的起始x值，顺序和tree在trees里面的顺序对应
        const treeXYs = [
          {
            initX: cons.initX,
            initY: cons.initY
          }
        ]
        trees.forEach((tree, index) => {
          // 在自顶向下的格式化方法中，level是1、2、3等自然数，从1开始加，但现在这个方法中，y值可能为0也可能为负数，所以要先将最小的y值加到1，并记录下这个差值
          let distance = 0
          Object.keys(tree.currentMaxX).forEach((key) => { if ((1 - key) > distance) distance = 1 - key })
          // console.log('distance: ', distance);
          treeXYs[index].distance = distance
          // 用来存放每一排有多少个数量的节点
          const nodesPerRow = {}
          tree.nodes.forEach((item) => {
            if (!nodesPerRow[item.y + distance]) {
              nodesPerRow[item.y + distance] = 1
            } else {
              nodesPerRow[item.y + distance] += 1
            }
          })
          // console.log('每一排有多少个数量的节点 nodesPerRow: ', nodesPerRow);
          // 获取：
          // 1、这一棵树最多节点的一排有多少个节点
          // 2、这一棵树最多节点的一排的width为多少
          // 3、这棵树的中线的x值
          // 4、算出这棵树每一排的起始x值并存进treeXs中
          // 5、这棵树的总高度
          // 6、下一棵树的起始x值 = 上一棵树的起始x值 + 上一棵树的最大宽度 + 两棵树的宽度间隙
          this.getRealXY(nodesPerRow, treeXYs, trees, index, cons)
        })
        // console.log('每一棵树的起始x值以及每一排的起始x值 treeXYs: ', treeXYs);
        // 根据每一棵树的key顺序以及其对应的level值，摆放每一个节点
        trees.forEach((tree, index) => {
          // 这棵树的各排的起始x值和y值
          const treeXY = treeXYs[index]
          const distance = treeXY.distance
          tree.nodes.forEach((node) => {
            // locatedNodes指这一排已经摆放好的nodes的数量
            if (!treeXY[node.y + distance].locatedNodes) {
              treeXY[node.y + distance].locatedNodes = 1
            } else {
              treeXY[node.y + distance].locatedNodes += 1
            }
            node.x = treeXY[node.y + distance].x + (treeXY[node.y + distance].locatedNodes - 1) * (cons.nodeWidth + cons.nodeWidthGap)
            node.y = treeXY[node.y + distance].y
            // 把节点的宽度改为用户设置的宽度
            node.width = data.nodeWidth
            this.updateNode(node.key, node)
          })
        })

        // 把所有节点都改成由 父节点的下方 到 子节点的上方
        for (let i = 0; i < links.length; i++) {
          if (Math.abs(links[i].beginNode.y - links[i].endNode.y) > cons.nodeHeight + cons.nodeHeightGap) {
            // 当该连线是跨级连线时
            links[i].beginNodeArrow = 'left'
            links[i].endNodeArrow = 'left'
          } else if (links[i].beginNode.y - links[i].endNode.y === 0) {
            // 相同的y值的两个节点有连线时，也就是在树状图上是左右摆放的两个节点
            if (links[i].beginNode.x > links[i].endNode.x) {
              links[i].beginNodeArrow = 'left'
              links[i].endNodeArrow = 'right'
            } else {
              links[i].beginNodeArrow = 'right'
              links[i].endNodeArrow = 'left'
            }
          } else {
            // 当该连线是两个相邻层级连线时
            if (links[i].endNode.y > links[i].beginNode.y) {
              // beginNode是endNode的父节点
              links[i].beginNodeArrow = 'bottom'
              links[i].endNodeArrow = 'top'
            } else {
              links[i].beginNodeArrow = 'bottom'
              links[i].endNodeArrow = 'bottom'
            }
          }
          this.updateLink(i, links[i])
        }
      }
      // 格式化之后，更新全局的nodeWidth, 全局的连线方式, 全局的isFormatted
      this.updateAfterFormat(data)
    },
    // 更新节点
    updateNode(key, node) {
      commit(this.$store, 'UPDATE_NODE', {
        key,
        obj: node
      });
    },
    // 更新所有节点
    updateAllNodes(nodes) {
      commit(this.$store, 'UPDATE_ALLNODES', nodes);
    },
    // 更新连线
    updateLink(index, link) {
      commit(this.$store, 'UPDATE_LINKER', {
        index,
        obj: link
      });
    },
    // 更新所有连线
    updateAllLinks(links) {
      commit(this.$store, 'UPDATE_ALLLINKERS', links);
    },
    // 格式化之后，更新全局的nodeWidth, 全局的连线方式, 全局的isFormatted
    updateAfterFormat(data) {
      // 将全局的nodeWidth改为新的格式化后的nodeWidth
      this.state.nodeOptions.width = data.nodeWidth
      commit(this.$store, 'UPDATE_NODE_OPTION', this.state.nodeOptions);
      // 将全局的连线方式改为格式化后的连线方式
      commit(this.$store, 'UPDATE_LINKTYPE', data.linkType);
      // 设置isFormatted值为true，表明已经进行过格式化
      commit(this.$store, 'UPDATE_ISFORMATTED', true);
      this.state.nodes.forEach((node) => {
        // 清空childNodes和parentNodes数组，因为该数组中可能是无限引用，会导致JSON.parse(JSON.stringify)时出错，所以在格式化后要清空
        node.childNodes = []
        node.parentNodes = []
        // 给每个节点加上linkType属性，用于在最初进行判断，如果更改了连线类型，那新增节点也应该用新类型进行连接，虽然现在每个节点的连接方式都是一样的，但以后可能会不一样，所以就设置成每个节点的属性了
        node.linkType = this.state.linkType
      })
      this.updateAllNodes(this.state.nodes)
    },
    // 从node1向direction找，如果能找到node2，则返回true
    findNode(node1, node2, direction) {
      // node1必须要有子节点才可能向下找到node2
      if (direction === 'down' && node1.childNodes && node1.childNodes.length) {
        // 一直向下（子节点）找
        for (let i = 0; i < node1.childNodes.length; i++) {
          if (node1.childNodes[i].key === node2.key) return true
          // 如果没有找到就继续迭代，一直迭代到没有没有子节点的节点为止
          this.findNode(node1.childNodes[i], node2, 'down')
        }
      }
      // node1必须要有父节点才可能向上找到node2
      if (direction === 'up' && node1.parentNodes && node1.parentNodes.length) {
        // 一直向上（父节点）找
        for (let i = 0; i < node1.parentNodes.length; i++) {
          if (node1.parentNodes[i].key === node2.key) return true
          // 如果没有找到就继续迭代，一直迭代到没有没有子节点的节点为止
          this.findNode(node1.parentNodes[i], node2, 'up')
        }
      }
      return false
    },
    // 将所有节点按照层级进行存放 如果有跨级连接的现象this.getNodesWithLevel返回false，如果是正常的一层一层连接下来的，this.getNodesWithLevel返回nodesWithLevel对象
    setNodesWithLevel(nodesWithLevel, level) {
      let hasDeepLevel = false
      // 执行到这里，说明肯定有节点，所以肯定会有根节点，这里无须再多判断
      nodesWithLevel[level].forEach((node) => {
        if (node.childNodes.length) {
          // 后面有直接return的情况，所以不能用forEach
          for (let i = 0; i < node.childNodes.length; i++) {
            if (node.childNodes[i].level && node.childNodes[i].level !== level + 1) {
              // 说明当前节点属于多个层级了，说明肯定有跨级连接的情况了
              // 不用再执行完剩下的set逻辑了，因为nodesWithLevel的其他值也不重要了，有跨级连线的格式化方法和无跨级连线的格式化方法不同
              return nodesWithLevel.crossLevelConnection = true
            } else {
              if (!node.childNodes[i].alreadySetLevel) node.childNodes[i].level = level + 1
            }
            if (!node.childNodes[i].alreadySetLevel) {
              if (!nodesWithLevel[level + 1 + '']) {
                // 如果nodesWithLevel[level + 1 + '']还是undefined
                nodesWithLevel[level + 1 + ''] = [node.childNodes[i]]
              } else {
                // 如果nodesWithLevel[level + 1 + '']已经是数组了
                nodesWithLevel[level + 1 + ''].push(node.childNodes[i])
              }
              node.childNodes[i].alreadySetLevel = true
            }
            // 还有子节点，说明还有下一层
            if (node.childNodes[i].childNodes.length) hasDeepLevel = true
          }
        }
      })
      // 如果这一层的某个节点还有子节点，说明还有下一层
      if (hasDeepLevel) this.setNodesWithLevel(nodesWithLevel, level + 1)
    },
    // 将该节点的所有子孙节点的key值放进其allChildrenKeys属性中
    setAllChildrenKeys(currentNode, rootNode) {
      if (currentNode.childNodes.length) {
        currentNode.childNodes.forEach((item) => {
          rootNode.allChildrenKeys.push(item.key)
          this.setAllChildrenKeys(item, rootNode)
        })
      }
    },
    // 如果两个节点至少有一个相同的子节点，就返回true，说明是同一棵树
    hasSameChild(node1, node2) {
      for (let i = 0; i < node1.allChildrenKeys.length; i++) {
        for (let j = 0; j < node2.allChildrenKeys.length; j++) {
          if (node1.allChildrenKeys[i] === node2.allChildrenKeys[j]) return true
        }
      }
      return false
    },
    // 根据key在nodes中找到对应的node
    findNodeByKey(key, nodes) {
      for (let i = 0; i < nodes.length; i++) {
        if (key === nodes[i].key) return nodes[i]
      }
    },
    // 通过先找一个节点的所有关联节点，再找所有关联节点的关联节点，找到node1这棵树里的所有节点，并设置好XY值
    setAllConnectedNodesXY(trees, index, links) {
      const tree = trees[index]
      // 第一次进入这个函数时，因为tree.nodes就是一个只有node1一项的数组，所以不用单独传node1了
      tree.nodes.forEach((node1) => {
        if (!node1.alreadyFindAllConnectedNodes) {
          // 遍历links数组，找到和node1有关系的节点
          links.forEach((link1, index1) => {
            // 如果这条link至少还有一个节点没有设置x和y值才执行下面的逻辑，两个节点都已经设置了x和y值就跳过了
            if (!(link1.beginNode.alreadySetXY && link1.endNode.alreadySetXY)) {
              // 如果node1是这条link1的父节点
              if (node1.key === link1.beginNode.key) {
                // 要找另外还有没有从node1到link1.endNode的更长的路径，如果没有，就把link1.endNode设置成node1的子节点(y值+1)
                // 因为不可能有环，所以只需要找node1还有没有从其他子节点出去到link1.endNode的路径
                // 因为上面判断了node1是link1的父节点，所以node1至少有一个子节点
                if (node1.childNodes.length > 1) {
                  // node1有多个子节点时才进行遍历，找从其他子节点有没有能到link1.endNode的路径
                  let isAnotherPath = false
                  for (let i = 0; i < node1.childNodes.length; i++) {
                    // 当前连线的endNode这个node1的子节点不需要往下找
                    if (node1.childNodes[i].key !== link1.endNode.key) {
                      // 找是否有其他更长的路径，能从node1到link1.endNode
                      isAnotherPath = this.findNode(node1.childNodes[i], link1.endNode, 'down')
                      // 如果有一条更长的路径，就不用再找了，这个节点肯定在这里不需要设置x和y值了
                      if (isAnotherPath) break
                    }
                  }
                  // 如果没有其他的路径能从node1到link1.endNode并且link1.endNode还没设置x和y值
                  if (!isAnotherPath && !link1.endNode.alreadySetXY) {
                    // 说明没有其他从node1到link1.endNode的路径了，node1和link1.endNode的直连路径就是最短的，这个时候就给link1.endNode定x和y值了
                    this.setNodeXY(node1, link1.endNode, tree, 'down')
                  }
                } else {
                  // node1只有一个子节点，所以直接把子节点的x和y设置出来
                  if (!link1.endNode.alreadySetXY) {
                    this.setNodeXY(node1, link1.endNode, tree, 'down')
                  }
                }
              }
              // 如果node1是这条link1的子节点
              if (node1.key === link1.endNode.key) {
                // 注释参考上面node1.key === link1.beginNode.key时，这里是换了个方向
                if (node1.parentNodes.length > 1) {
                  let isAnotherPath = false
                  for (let i = 0; i < node1.parentNodes.length; i++) {
                    if (node1.parentNodes[i].key !== link1.beginNode.key) {
                      isAnotherPath = this.findNode(node1.parentNodes[i], link1.beginNode, 'up')
                      if (isAnotherPath) break
                    }
                  }
                  if (!isAnotherPath && !link1.beginNode.alreadySetXY) {
                    // 注意这里，link1.beginNode是node1的父节点，所以y是-1，和上面+1不同
                    this.setNodeXY(node1, link1.beginNode, tree, 'up')
                  }
                } else {
                  if (!link1.beginNode.alreadySetXY) {
                    // 注意这里，link1.beginNode是node1的父节点，所以y是-1，和上面+1不同
                    this.setNodeXY(node1, link1.beginNode, tree, 'up')
                  }
                }
              }
            }
            // 说明links遍历结束了，node1相关的所有节点都已经遍历过了 将node1的alreadyFindAllConnectedNodes值设为true
            if (index1 === links.length - 1) {
              node1.alreadyFindAllConnectedNodes = true
              // 继续迭代，找所有node1关联节点的关联节点
              this.setAllConnectedNodesXY(trees, index, links)
            }
          })
        }
      })
    },
    // 没有其他从node1到node2的路径了，node1和node2的直连路径就是最短的，这个时候就给node2定x和y值了
    setNodeXY(node1, node2, tree, direction) {
      node2.y = direction === 'down' ? node1.y + 1 : node1.y - 1
      node2.x = tree.currentMaxX[node2.y] !== undefined ? tree.currentMaxX[node2.y] + 1 : 0
      tree.currentMaxX[node2.y] = node2.x
      node2.alreadySetXY = true
      tree.nodes.push(node2)
    },
    // 获取：
    // 1、这一棵树最多节点的一排有多少个节点
    // 2、这一棵树最多节点的一排的width为多少
    // 3、这棵树的中线的x值
    // 4、算出这棵树每一排的起始x值并存进treeXs中
    // 5、这棵树的总高度
    // 6、下一棵树的起始x值 = 上一棵树的起始x值 + 上一棵树的最大宽度 + 两棵树的宽度间隙
    getRealXY(nodesPerRow, treeXYs, trees, index, cons) {
      // 这一棵树最多节点的一排有多少个节点
      let maxNodeCount = 1
      Object.values(nodesPerRow).forEach((value) => {
        if (value > maxNodeCount) maxNodeCount = value
      })
      // console.log('这一棵树最多节点的一排有多少个节点 maxNodeCount: ', maxNodeCount);
      // 这一棵树最多节点的一排的width为多少
      const maxRowWidth = maxNodeCount * cons.nodeWidth + (maxNodeCount - 1) * cons.nodeWidthGap
      // console.log('这一棵树最多节点的一排的width为多少 maxRowWidth: ', maxRowWidth);
      // 这棵树的中线的x值
      const middleLineX = maxRowWidth / 2 + treeXYs[index].initX
      // console.log('这棵树的中线的x值 middleLineX: ', middleLineX);
      // 算出这棵树每一排的起始x值并存进treeXs中
      Object.keys(nodesPerRow).forEach((key) => {
        treeXYs[index][key] = {}
        // 每一排的起始x值 = 中线的x值 - 这一排的总width值 / 2
        treeXYs[index][key].x = middleLineX - (nodesPerRow[key] * cons.nodeWidth + (nodesPerRow[key] - 1) * cons.nodeWidthGap) / 2
        // 每一排的y值 = 每棵树的起始y值 + （排数 - 1） * （节点高 + 节点的高度间隔）
        treeXYs[index][key].y = treeXYs[index].initY + (key - 1) * (cons.nodeHeight + cons.nodeHeightGap)
      })
      // 这棵树的总高度
      const treeTotalHeight = Object.keys(nodesPerRow).length * cons.nodeHeight + (Object.keys(nodesPerRow).length - 1) * cons.nodeHeightGap

      // 计算下一棵树的属性
      // 下一棵树的起始x值 = 上一棵树的起始x值 + 上一棵树的最大宽度 + 两棵树的宽度间隙
      if (treeXYs.length < trees.length) {
        const tempInitX = treeXYs[index].initX + maxRowWidth + cons.treesWidthGap
        // 如果下一棵树的起始x都小于等于cons.maxReturnX，就继续在同一个水平线上往右边摆树
        treeXYs[index + 1] = {}
        // 如果这棵树的总高度大于currentMaxRowY（可能是其他同一排的树的总高度，也可能是0），就更新currentMaxRowY值，保持currentMaxRowY值始终是这一排最高的树的总高度
        if (treeTotalHeight > this.currentMaxRowY) this.currentMaxRowY = treeTotalHeight
        if (tempInitX <= cons.maxReturnX) {
          treeXYs[index + 1].initX = tempInitX
          treeXYs[index + 1].initY = treeXYs[index].initY
        } else {
          // 如果下一棵树的起始x大于cons.maxReturnX了，则换行到之前树的下面去放这棵树
          // 起始x为所有行第一棵树的起始x值
          treeXYs[index + 1].initX = cons.initX
          // 起始y为 上一棵树的起始y + 上一排最高的树的总高度 + 两棵树的高度间隙
          treeXYs[index + 1].initY = treeXYs[index].initY + this.currentMaxRowY + cons.treesHeightGap
          // 换行了，新的一行只有这一棵树，新一行还没摆树，所以新的一行的currentMaxRowY暂时为0
          this.currentMaxRowY = 0
        }
      }
    },

    // 恢复到格式化前的状态
    resetFormat() {
      // 减少后面写this.initDatas的次数
      const datas = this.initDatas
      const { initNodes, initLinks, initNodeWidth, initLinkType } = datas[datas.length - 1]
      // 进行恢复
      this.updateAllNodes(initNodes)
      this.updateAllLinks(initLinks)

      // 因为最开始state.links里面的某个link里面的beginNode和endNode其实都只是存的指向state.nodes里的某个node，但如果是撤销格式化之后，因为state.links是JSON.parse(JSON.stringify())之后的，所以link里的beginNode和endNode是新的对象了，但后面的逻辑需要link里的beginNode和endNode就是state.nodes里面的对应node，所以进行一个处理
      this.state.links.forEach((link) => {
        link.beginNode = this.findNodeByKey(link.beginNode.key, this.state.nodes)
        link.endNode = this.findNodeByKey(link.endNode.key, this.state.nodes)
      })
      this.updateAllLinks(this.state.links)

      // 恢复全局的nodeWidth
      this.state.nodeOptions.width = initNodeWidth
      commit(this.$store, 'UPDATE_NODE_OPTION', this.state.nodeOptions);
      // 恢复全局的连线方式
      commit(this.$store, 'UPDATE_LINKTYPE', initLinkType);

      datas.splice(datas.length - 1, 1)
      // 设置isFormatted值为false，表明已经恢复到第一次格式化之前的状态，将不再显示恢复按钮
      if (datas.length === 0) commit(this.$store, 'UPDATE_ISFORMATTED', false);
    },

    // 在连线时判断是否能将两个节点连起来
    canAddLink(startNode, endNode) {
      // 获取这个node的所有parent节点
      let getParentNodes = (node, parents) => {
        this.state.links.forEach(link => {
          if (link.endNode == node) {
            if (parents.indexOf(link.beginNode) == -1) {
              parents.push(link.beginNode);
              getParentNodes(link.beginNode, parents);
            }
          }
        });
      };
      if (startNode && endNode) {
        // 不能自己连自己
        if (startNode == endNode) {
          console.error(this.$t('message.workflow.vueProcess.unableToConnect'));
          this.designer.$emit('message', {
            type: 'error',
            msg: this.$t('message.workflow.vueProcess.unableToConnect')
          });
          return false;
        }
        // 如果两者已经连过，则不能继续再连
        if (
          this.state.links.filter(link => {
            if (link.beginNode == startNode && link.endNode == endNode) {
              return true;
            }
            if (link.endNode == startNode && link.beginNode == endNode) {
              return true;
            }
            return false;
          }).length > 0
        ) {
          console.error(this.$t('message.workflow.vueProcess.repeatNodes'));
          this.designer.$emit('message', {
            type: 'error',
            msg: this.$t('message.workflow.vueProcess.repeatNodes')
          });
          return false;
        }
        // 不能形成闭环
        let parents = [];
        getParentNodes(startNode, parents);

        if (parents.indexOf(endNode) != -1) {
          console.error(this.$t('message.workflow.vueProcess.closed-loop'));
          this.designer.$emit('message', {
            type: 'error',
            msg: this.$t('message.workflow.vueProcess.closed-loop')
          });
          return false;
        }
        return true;
      }
      return false;
    },
    // 两个节点连接时进行一些计算 参数：beginNodeKey 起始节点；trendArrow 趋势
    calculateLinkToNode(x, y, beginNodeKey, trendArrow) {
      let result = {
        x: x,
        y: y,
        node: null,
        arrow: ''
      };
      for (let i = 0; i < this.state.nodes.length; i++) {
        let node = this.state.nodes[i];
        if (beginNodeKey !== node.key) {
          let minx = node.x * this.state.baseOptions.pageSize;
          let middlex = (node.x + node.width / 2) * this.state.baseOptions.pageSize;
          let maxx = (node.x + node.width) * this.state.baseOptions.pageSize;
          let miny = node.y * this.state.baseOptions.pageSize;
          let middley = (node.y + node.height / 2) * this.state.baseOptions.pageSize;
          let maxy = (node.y + node.height) * this.state.baseOptions.pageSize;
          if (trendArrow == 'left' || trendArrow == 'right') {
            // 左边
            if (x >= minx - 20 && x < middlex && y <= maxy && y >= miny) {
              result.x = minx;
              result.arrow = 'left';
              result.node = node;
              break;
            }
            // 右侧
            if (x <= maxx + 20 && x >= middlex && y <= maxy && y >= miny) {
              result.x = maxx;
              result.arrow = 'right';
              result.node = node;
              break;
            }

            if (x <= maxx && x >= minx) {
              // 顶部
              if (y < miny && y > miny - 20) {
                result.y = miny;
                result.arrow = 'top';
                result.node = node;
                break;
              }
              // 底部
              if (y > maxy && y < maxy + 20) {
                result.y = maxy;
                result.arrow = 'bottom';
                result.node = node;
                break;
              }
            }
          }
          if (trendArrow == 'bottom' || trendArrow == 'top') {
            // 顶部
            if (y >= miny - 20 && y < middley && x <= maxx && x >= minx) {
              result.y = miny;
              result.arrow = 'top';
              result.node = node;
              break;
            }
            // 右侧
            if (y <= maxy + 20 && y >= middley && x <= maxx && x >= minx) {
              result.y = maxy;
              result.arrow = 'bottom';
              result.node = node;
              break;
            }

            if (y <= maxy && y >= miny) {
              // 顶部
              if (x < minx && x > minx - 20) {
                result.x = minx;
                result.arrow = 'left';
                result.node = node;
                break;
              }
              // 底部
              if (x > maxx && x < maxx + 20) {
                result.x = maxx;
                result.arrow = 'right';
                result.node = node;
                break;
              }
            }
          }
        }
      }
      return result;
    },
    // 框选
    mousedown(e) {
      let viewport = this.$el.getBoundingClientRect();
      let beginX = e.pageX - viewport.left + this.$el.scrollLeft;
      let beginY = e.pageY - viewport.top + this.$el.scrollTop;
      if (e.shiftKey) {
        this.setDraging({
          type: 'box-select',
          data: {
            beginX,
            beginY
          }
        });
      } else {
        if (this.state.disabled) return;
        if (!this.calcLinks) return;
        let key = this.findLinkByXY(beginX, beginY);
        let links = { };
        Object.keys(this.calcLinks).forEach(item => {
          links[item] = { ...this.calcLinks[item] }
        });
        if (key) {
          let isDbclickLink = false
          if (this.clickState && this.clickState[key]) {
            isDbclickLink = Date.now() - this.clickState[key] < 300
          }
          this.clickState = {
            [key]: Date.now()
          }
          this._xyInLinkKey = key;
          commit(this.$store, 'UPDATE_CHOOSING', {
            type: 'link',
            key
          });
          links[key] = { ...this.calcLinks[key], stroke: '#3399ff', fill: '#3399ff' }
          if (isDbclickLink) {
            const linkItem = this.state.links.find(l=>l.key === key)
            this.designer.$emit('link-dblclick', key, linkItem)
          }
        } else {
          if (this._xyInLinkKey) {
            this._xyInLinkKey = false;
          }
        }
        this.drawLinkerRAF(links);
      }
    },
    // 鼠标移动时触发
    mousemove(e) {
      if (this.state.disabled) return;
      if (!this.state.draging.type) return;
      if (this.state.draging.type == 'node') {
        // 拖动节点
        let dragData = this.state.draging.data;
        if (dragData.beginPageX != null && dragData.beginPageY != null) {
          let deltaX = (e.pageX - dragData.beginPageX) / this.state.baseOptions.pageSize;
          let deltaY = (e.pageY - dragData.beginPageY) / this.state.baseOptions.pageSize;
          let i = 0;
          let len = dragData.nodes.length;
          for (; i < len; i++) {
            let node = dragData.nodes[i];
            let x = dragData.selectedNodesBegin[node.key].beginX + deltaX;
            let y = dragData.selectedNodesBegin[node.key].beginY + deltaY;

            if (this._cacheMaxXY) {
              if (x + node.width > this._cacheMaxXY.maxX) {
                this._cacheMaxXY.maxX = x + node.width;
                this._need_resize = this._cacheMaxXY;
              }
              if (y + node.height > this._cacheMaxXY.maxY) {
                this._need_resize = this._cacheMaxXY;
                this._cacheMaxXY.maxY = y + node.height;
              }
            }

            if (x < 0 || y < 0) {
              break;
            }
            if (this.state.mapMode && node.projectData) {
              // 如果是工程节点禁止移动
              if (node.type === 'projectNode') {
                break;
              }
              // x值得取值范围
              const projectLayout = node.projectData.layout;
              const minX = projectLayout.x;
              const maxX = projectLayout.x + projectLayout.width - node.width;
              // y得取值范围
              const minY = projectLayout.y + 100; // 减去头部的高度
              const maxY = projectLayout.y + projectLayout.height - node.height;
              // 节点x值小于最小值时
              if (x < minX) {
                x = minX;
              } else if (x > maxX) {
                x = maxX;
              }
              if (y < minY) {
                y = minY;
              } else if (y > maxY) {
                y = maxY;
              }
            }
            let newNode = Object.assign(node, {
              x,
              y
            });

            if (dragData.nodes.length < 2) {
              this.calculateSnap({
                compareNode: newNode,
                x: newNode.x + newNode.width / 2,
                y: newNode.y + newNode.height / 2
              });
            }

            commit(this.$store, 'UPDATE_NODE', {
              key: node.key,
              obj: newNode
            })
          }
        }
      }
      if (this.state.draging.type == 'link') {
        // 地图模式禁止连线
        if (this.state.mapMode) return;
        let link = this.state.draging.data;
        let endX = e.pageX - this.state.baseOptions.nodeViewOffsetX + this.scrollLeft;
        let endY = e.pageY - this.state.baseOptions.nodeViewOffsetY + this.scrollTop;
        let x = 0;
        let y = 0;
        let endNodeArrow;
        // 先根据拖拽的趋势判断方向,endNodeArrow为link到另一个节点的趋势方向
        endNodeArrow = link.endNodeArrow;
        if (Math.abs(endY - (link.beginY + this.scrollTop)) > Math.abs(endX - (link.beginX + this.scrollLeft))) {
          if (endY - link.beginY > 0) {
            endNodeArrow = 'top';
          } else {
            endNodeArrow = 'bottom';
          }
        } else {
          if (endX - link.beginX > 0) {
            endNodeArrow = 'left';
          } else {
            endNodeArrow = 'right';
          }
        }

        // 如果拖拽的点接近某个节点的边，这里endX和endY就要处理下了
        let result = this.calculateLinkToNode(endX, endY, link.beginNode.key, endNodeArrow);
        if (!result.arrow) {
          this.setDraging({
            type: 'link',
            data: Object.assign(link, {
              preEndNode: null
            })
          });
        } else {
          // 如果拖拽的点判定到靠近某个节点，这时候松手才保存（未来考虑更优方案）
          endNodeArrow = result.arrow;
          endX = result.x;
          endY = result.y;
          this.setDraging({
            type: 'link',
            data: Object.assign(link, {
              preEndNode: result.node
            })
          });
        }

        if (endNodeArrow == 'top') {
          x = endX - link.endNode.width / 2 * this.state.baseOptions.pageSize;
          y = endY;
        }
        if (endNodeArrow == 'bottom') {
          x = endX - link.endNode.width / 2 * this.state.baseOptions.pageSize;
          y = endY - link.endNode.height * this.state.baseOptions.pageSize;
        }
        if (endNodeArrow == 'left') {
          x = endX;
          y = endY - link.endNode.height / 2 * this.state.baseOptions.pageSize;
        }
        if (endNodeArrow == 'right') {
          x = endX - link.endNode.width * this.state.baseOptions.pageSize;
          y = endY - link.endNode.height / 2 * this.state.baseOptions.pageSize;
        }
        // 重新获取最新的值
        link = this.state.draging.data;
        this.setDraging({
          type: 'link',
          data: Object.assign(link, {
            endNode: Object.assign(link.endNode, {
              x: x / this.state.baseOptions.pageSize,
              y: y / this.state.baseOptions.pageSize
            }),
            endNodeArrow: endNodeArrow,
            endX: endX,
            endY: endY
          })
        });
      }
      if (this.state.draging.type == 'box-select') {
        let viewport = this.$el.getBoundingClientRect();
        let dragBox = this.state.draging.data;
        let left = dragBox.beginX;
        let top = dragBox.beginY;
        let width = e.pageX - viewport.left + this.$el.scrollLeft - dragBox.beginX;
        let height = e.pageY - viewport.top + this.$el.scrollTop - dragBox.beginY;
        if (width < 0) {
          left = left + width;
          width = -1 * width;
        }
        if (height < 0) {
          top = top + height;
          height = -1 * height;
        }
        this.boxSelectLayerStyle = {
          left: `${left}px`,
          top: `${top}px`,
          width: `${width}px`,
          height: `${height}px`,
          display: 'block'
        }
      }
    },
    // 点击鼠标松开时触发
    mouseup() {
      if (this.state.disabled) return;
      if (!this.state.draging.type) return;
      if (this.state.draging.type == 'node') {
        // 如果在修正线以内，则自动修正

        let dragNode = this.state.draging.data;
        if (this.state.snapLineX.show) {
          this.state.nodes.forEach((node) => {
            if (node.key === dragNode.key) {
              commit(this.$store, 'UPDATE_NODE', {
                key: dragNode.key,
                obj: Object.assign(node, {
                  modifyTime: Date.now(),
                  y: this.state.snapLineX.top - node.height / 2
                })
              });
            }
          });
          commit(this.$store, 'UPDATE_SNAPLINE_X', {
            show: false,
            left: 0,
            top: 0
          });
        }

        if (this.state.snapLineY.show) {
          this.state.nodes.forEach((node) => {
            if (node.key === dragNode.key) {
              commit(this.$store, 'UPDATE_NODE', {
                key: dragNode.key,
                obj: Object.assign(node, {
                  modifyTime: Date.now(),
                  x: this.state.snapLineY.left - node.width / 2
                })
              });
            }
          });
          commit(this.$store, 'UPDATE_SNAPLINE_Y', {
            show: false,
            left: 0,
            top: 0
          });
        }
        this.clearDraging();
        this.$emit('moveNode');
        this.drawLinkerRAF();
      }
      let { canAddLink } = this.designer.viewOptions;
      if (this.state.draging.type == 'link') {
        let link = this.state.draging.data;
        if (link.preEndNode) {
          if (typeof canAddLink === 'function') {
            canAddLink = canAddLink(link.beginNode, link.preEndNode)
          } else if(canAddLink === undefined) {
            canAddLink = this.canAddLink(link.beginNode, link.preEndNode)
          }
          if (canAddLink) {
            let endPos
            let minDis
            if (link.preEndNode.nodeAnchors) {
              link.preEndNode.nodeAnchors.forEach(it => {
                let dist = Math.sqrt((link.preEndNode.x + it.x - link.endX) * (link.preEndNode.x + it.x - link.endX) + (link.preEndNode.y + it.y - link.endY) * (link.preEndNode.y + it.y - link.endY));
                if(!minDis || minDis> dist) {
                  minDis = dist
                  endPos = {...it, arrow: link.endNodeArrow}
                }
              })
              if (minDis < 30) {
                // 增加个节点
                commit(this.$store, 'ADD_LINKER', {
                  key: link.key,
                  beginNode: link.beginNode,
                  beginNodeArrow: link.beginNodeArrow,
                  endNode: link.preEndNode,
                  linkType: this.state.linkType,
                  endNodeArrow: endPos,
                  ...this.state.linkerOptions
                });
              } else {
                console.log('无可用锚点')
              }
            } else {
              // 增加个节点
              commit(this.$store, 'ADD_LINKER', {
                key: link.key,
                beginNode: link.beginNode,
                beginNodeArrow: link.beginNodeArrow,
                endNode: link.preEndNode,
                linkType: this.state.linkType,
                endNodeArrow: link.endNodeArrow,
                ...this.state.linkerOptions
              });
            }
          }
          // 新增连线，通知组件外部
          this.designer.$emit('link-add');
        }
        delete this.calcLinks[link.key];
        this.drawLinkerRAF();
        this.clearDraging();
      }

      if (this.state.draging.type == 'box-select') {
        this.boxSelected();
        this.clearDraging();
      }
    },
    // 对节点进行操作时触发
    operatNode(type, nodeKey) {
      if (this.state.disabled) return;

      let node = this.getNodeByKey(nodeKey);
      if (type == 'config') {
        commit(this.$store, 'UPDATE_EDIT_BASEINFO_NODE', node);
      } else if (type == 'param') {
        // 参数配置
        commit(this.$store, 'UPDATE_EDIT_PARAM_NODE', node);
      } else if (type == 'delete') {
        let needRedrawLink = false;
        this.state.links.forEach(link => {
          if (link.beginNode.key === nodeKey || link.endNode.key === nodeKey) {
            delete this.calcLinks[link.key];
            needRedrawLink = true;
          }
        })
        if (this.state.links.length < 1) {
          this.calcLinks = {};
          needRedrawLink = true;
        }
        if (needRedrawLink) this.drawLinkerRAF();
        this.designer.$emit('node-delete', node);
      } else if (type == 'copy') {
        commit(this.$store, 'UPDATE_COPYING', {
          type: 'node',
          key: nodeKey
        });
      }
    },
    // 对连线进行操作时触发
    operatLiner(type, linkKey) {
      if (this.state.disabled) return;
      if (type == 'delete') {
        commit(this.$store, 'DELETE_LINKER_BY_KEY', linkKey);
        delete this.calcLinks[linkKey];
        this.drawLinkerRAF();
        this.designer.$emit('link-delete');
      }
    },
    // 在外面的大工作区点击右键时触发
    showMenu(e) {
      if (this.state.disabled) return;
      if (this.contentMenu) {
        this.contentMenu.destroy();
        this.contentMenu = null;
      }
      if (this._xyInLinkKey) {
        return this.showLinkerMenu(e, this._xyInLinkKey)
      }
      this._left = e.offsetX;
      this._top = e.offsetY;
      let arr = [];
      let copyingNode;
      if (this.state.copying.type == 'node' && this.state.copying.key != '') {
        copyingNode = this.getNodeByKey(this.state.copying.key);
        if (copyingNode) {
          arr.push({
            icon: 'fuzhi',
            text: this.$t('message.workflow.vueProcess.paste'),
            value: 'paste'
          })
        }
      }
      let { beforeShowMenu, beforePaste } = this.designer.myMenuOptions;
      const iconHelper = function (arr) {
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
        arr = beforeShowMenu(null, arr, 'view');
        if (Array.isArray(arr)) {
          arr = iconHelper(arr)
        } else {
          console.warn('ctxMenuOptions.beforeShowMenu返回值必须是一个数组')
        }
      }
      if (arr.length < 1) return;
      this.contentMenu = contentMenu({
        data: arr,
        left: e.clientX,
        top: e.clientY,
        choose: (data) => {
          if (this.state.disabled) return;
          if (data && data.value == 'paste') {
            let newNode = Object.assign({}, copyingNode, {
              key: getKey(),
              createTime: Date.now(),
              x: this._left / this.state.baseOptions.pageSize,
              y: this._top / this.state.baseOptions.pageSize
            });

            if (typeof beforePaste === 'function') {
              newNode = beforePaste(newNode);
            }
            if (newNode) {
              commit(this.$store, 'ADD_NODE', newNode);
              commit(this.$store, 'UPDATE_COPYING', {
                type: '',
                key: ''
              });
            }
          }
          this.designer.$emit(`on-ctx-menu`, data.value, e, 'view')
        }
      })
    },
    showLinkerMenu(e, k) {
      let arr = [{ icon: 'shanchu', text: this.$t('message.workflow.vueProcess.delete'), value: 'delete' }]
      let { beforeShowMenu } = this.designer.myMenuOptions;
      if (typeof beforeShowMenu === 'function') {
        arr = beforeShowMenu(this.k, arr, 'link');
        if (Array.isArray(arr)) {
          arr = arr.map((it) => {
            let menuItem = {
              text: it.text,
              value: it.value
            }
            if (it.img) {
              menuItem.img = it.img;
            } else if (it.icon) {
              menuItem.icon = it.icon;
            }
            return menuItem
          });
        } else {
          console.warn('ctxMenuOptions.beforeShowMenu返回值必须是一个数组')
        }
      }
      if (arr.length < 1) { return }
      this.contentMenu = contentMenu({
        data: arr,
        left: e.clientX,
        top: e.clientY,
        choose: (data) => {
          if (this.state.disabled) return;
          this.operatLiner(data.value, k);
          this.designer.$emit('on-ctx-menu', data.value, k, 'link')
        }
      })
    },

    // 拖拽时的对齐的线
    getSnapLineXStyle() {
      return {
        width: this.state.gridOptions.width * this.state.baseOptions.pageSize + 'px',
        height: '1px',
        left: this.state.snapLineX.left * this.state.baseOptions.pageSize + 'px',
        top: this.state.snapLineX.top * this.state.baseOptions.pageSize + 'px'
      };
    },
    getSnapLineYStyle() {
      return {
        width: '1px',
        height: this.state.gridOptions.height * this.state.baseOptions.pageSize + 'px',
        left: this.state.snapLineY.left * this.state.baseOptions.pageSize + 'px',
        top: this.state.snapLineY.top * this.state.baseOptions.pageSize + 'px'
      };
    },
    // 计算展示对齐线，当前node展示位置x,y轴与其他node相距不到6px展示
    calculateSnap({ x, y, compareNode }) {
      for (let i = 0; i < this.state.nodes.length; i++) {
        let node = this.state.nodes[i];
        if (compareNode.key !== node.key) {
          let _x = node.x + node.width / 2;
          if (Math.abs(x - _x) < 6) {
            commit(this.$store, 'UPDATE_SNAPLINE_Y', {
              show: true,
              left: _x,
              top: 0
            });
            break;
          } else {
            commit(this.$store, 'UPDATE_SNAPLINE_Y', {
              show: false,
              left: _x,
              top: 0
            });
          }
        }
      }
      for (let i = 0; i < this.state.nodes.length; i++) {
        let node = this.state.nodes[i];
        if (compareNode.key !== node.key) {
          let _y = node.y + node.height / 2;
          if (Math.abs(y - _y) < 6) {
            commit(this.$store, 'UPDATE_SNAPLINE_X', {
              show: true,
              left: 0,
              top: _y
            });
            break;
          } else {
            commit(this.$store, 'UPDATE_SNAPLINE_X', {
              show: false,
              left: 0,
              top: _y
            });
          }
        }
      }
    },
    // 框选选中节点加selected
    boxSelected() {
      if (this.boxSelectLayerStyle.width) {
        // 找出框选范围内的节点并修改selected
        let chooseNodes = []
        this.state.nodes.forEach((node, index) => {
          let nodeRef = this.$refs.nodes[index]
          let {
            left,
            top,
            width,
            height } = getComputedStyle(nodeRef.$el);
          let nodePos = { left, top, width, height };
          if (node.selected) {
            chooseNodes.push(node.key)
          } else if (this.isInBoxSelect(nodePos, this.boxSelectLayerStyle)) {
            chooseNodes.push(node.key);
          }
        })
        setTimeout(() => {
          commit(this.$store, 'UPDATE_CHOOSING', {
            type: 'node',
            key: chooseNodes
          });
        }, 0)
        if (chooseNodes.length) {
          this.$emit('box-select');
        }
        this.boxSelectLayerStyle = {}
      }
    },
    isInBoxSelect({ top, left, width, height }, box) {
      top = top.replace('px', '') - 0;
      left = left.replace('px', '') - 0;
      width = width.replace('px', '') - 0;
      height = height.replace('px', '') - 0;
      let bTop = box.top.replace('px', '') - 0;
      let bLeft = box.left.replace('px', '') - 0;
      let bWidth = box.width.replace('px', '') - 0;
      let bHeight = box.height.replace('px', '') - 0;
      let isIn = top > bTop && left > bLeft && top + height < bTop + bHeight && left + width < bLeft + bWidth
      return isIn
    },
    clickoutsideNode(e) {
      if (this.state.disabled) return;
      // 这里改成当只有点击大背景板时才取消选择
      if (!e.ctrlKey && !e.shiftKey && this.state.choosing.type == 'node' && e.target === this.$refs.canvas) {
        commit(this.$store, 'UPDATE_CHOOSING', {
          type: 'node',
          key: []
        });
      }
    },
    clickoutsideLink() {
      if (this.state.disabled) return;
      if (this._xyInLinkKey) {
        this._xyInLinkKey = false;
        this.drawLinkerRAF();
      }
      if (this.state.choosing.type == 'link') {
        commit(this.$store, 'UPDATE_CHOOSING', {
          type: 'link',
          key: []
        });
      }
    },
    drawLinkers(links) {
      if (!this.canvasCtx) {
        this.canvas = this.$refs.canvas;
        this.canvasCtx = this.canvas.getContext('2d');
        this._need_resize = true;
      }
      if (this._need_resize) {
        if (this._need_resize === true) {
          let xy = this.getMaxXY();
          this._cacheMaxXY = xy;
        }
        let width = this._cacheMaxXY.maxX > 10000 ? 10000 : this._cacheMaxXY.maxX;
        let height = this._cacheMaxXY.maxY > 10000 ? 10000 : this._cacheMaxXY.maxY;
        // 拖动缓冲距离
        this.canvas.width = width + 800;
        this.canvas.height = height + 800;
        this._need_resize = false
      }

      let ctx = this.canvasCtx;
      if (!ctx) {
        return
      }
      let calcLinks = {};
      this.state.links.forEach(l => {
        calcLinks[l.key] = this.calcLinks[l.key];
      });
      this.calcLinks = calcLinks;
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
      const lines = this.adjustLines(links);
      drawLinker(ctx, lines, this.state);
    },
    adjustLines(links) {
      links = JSON.parse(JSON.stringify(links));
      this.pointsMap = {}
      return Object.keys(links).map(key => {
        if (this.state.mapMode) { // 解决工作流地图模式同一个节点多条连线重叠覆盖
          this.adjustLinePoints(links, key);
        }
        let { nodes, stroke, fill, lineWidth, linkType } = links[key];
        return { key, nodes, lineWidth, stroke, fill, linkType };
      })
    },
    adjustLinePoints(links, key) {
      const len = links[key].nodes.length
      const linkItem = this.state.links.find(link => link.key === key)
      if (len && linkItem) {
        // 记录所有连线起始点重复点位
        let benginPointKey = `${links[key].nodes[0].x}:${links[key].nodes[0].y}`
        let endPointKey = `${links[key].nodes[len-1].x}:${links[key].nodes[len-1].y}`
        if (this.pointsMap[benginPointKey]) { // 同一个点多条连线调整连线起始点坐标
          if(links[key].nodes[0].x === links[key].nodes[1].x ) {
            // 控制调整宽度，最多处理同一点10条连线
            let adjust = this.pointsMap[benginPointKey] * linkItem.beginNode.width / 20
            adjust = adjust < linkItem.beginNode.width / 2 ? adjust : 0;
            links[key].nodes[0].x = links[key].nodes[0].x + adjust
            links[key].nodes[1].x = links[key].nodes[1].x + adjust
          }
          if(links[key].nodes[0].y === links[key].nodes[1].y ) {
            let adjust = this.pointsMap[benginPointKey] * linkItem.beginNode.height / 20
            adjust = adjust < linkItem.beginNode.height / 2 ? adjust : 0;
            links[key].nodes[0].y = links[key].nodes[0].y+ adjust
            links[key].nodes[1].y = links[key].nodes[0].y + adjust
          }
          this.pointsMap[benginPointKey] = this.pointsMap[benginPointKey] + 1
        } else {
          this.pointsMap[benginPointKey] = 1
        }
        if (this.pointsMap[endPointKey]) {
          if(links[key].nodes[len-1].x === links[key].nodes[len-2].x ) {
            let adjust = this.pointsMap[endPointKey] * linkItem.endNode.width / 20
            adjust = adjust < linkItem.endNode.width / 2 ? adjust : 0;
            links[key].nodes[len-1].x = links[key].nodes[len-1].x + adjust
            links[key].nodes[len-2].x = links[key].nodes[len-2].x + adjust
          }
          if(links[key].nodes[len-1].y === links[key].nodes[len-2].y ) {
            let adjust = this.pointsMap[endPointKey] * linkItem.endNode.height / 20
            adjust = adjust < linkItem.endNode.height / 2 ? adjust : 0;
            links[key].nodes[len-1].y = links[key].nodes[len-1].y  + adjust
            links[key].nodes[len-2].y = links[key].nodes[len-2].y + adjust
          }
          this.pointsMap[endPointKey] = this.pointsMap[endPointKey] + 1
        } else {
          this.pointsMap[endPointKey] = 1
        }
      }
    },
    afterCalculate(link) {
      this.calcLinks = this.calcLinks || {};
      this.calcLinks[link.key] = link;
      let len = Object.keys(this.calcLinks).length;
      if (len >= this.state.links.length) {
        this.drawLinkerRAF();
      }
    },
    drawLinkerRAF(links) {
      links = links || this.calcLinks || {};
      if (this._scheduledAnimationFrame) { return }
      this._scheduledAnimationFrame = true;
      window.requestAnimationFrame(() => {
        this._scheduledAnimationFrame = false;
        this.drawLinkers(links)
      });
    },
    findLinkByXY(x, y) {
      let dis = 10;
      let linkKey;
      Object.keys(this.calcLinks).forEach(key => {
        let { nodes, lineWidth } = this.calcLinks[key];
        for (let i = 0; i < nodes.length - 1; i++) {
          let p0 = nodes[i];
          let p1 = nodes[i + 1];
          let p = { x, y };
          let inX = (x <= p1.x && x >= p0.x) || (x >= p1.x && x <= p0.x)
          let inY = (y <= p1.y && y >= p0.y) || (y >= p1.y && y <= p0.y)
          if (inX || inY) {
            let pdis = this.disToLine(p0, p1, p)
            if (pdis < lineWidth * 10) {
              if (pdis < dis) {
                dis = pdis;
                linkKey = key;
              }
            }
          }
        }
      })
      return linkKey;
    },
    disToLine(p0, p1, p) {
      let dis;
      if (p1.x === p0.x) {
        dis = Math.abs(p1.x - p.x)
      } else if (p1.y === p0.y) {
        dis = Math.abs(p1.y - p.y)
      } else {
        let k = (p0.y - p1.y) / (p0.x - p1.x);
        let b = (p0.y * p1.x - p1.y * p0.x) / (p1.x - p0.x);
        dis = Math.abs(k * p.x + b - p.y);
      }
      return dis;
    },
    getMaxXY() {
      let maxX = 0;
      let maxY = 0;
      this.state.nodes.forEach((element, index) => {
        if (index === 0) {
          maxX = element.x + element.width;
          maxY = element.y + element.height;
        } else {
          if (element.y > maxY) {
            maxY = element.y + element.height;
          }
          if (element.x > maxX) {
            maxX = element.x + element.width;
          }
        }
      });
      return { maxX, maxY }
    },
    _updateCalLinks() {
      this.calcLinks = {};
      this.drawLinkerRAF();
    }
  }
};
</script>
<style scoped>
.canvas {
    position: absolute;
    left:0;
    top:0;
}
</style>
