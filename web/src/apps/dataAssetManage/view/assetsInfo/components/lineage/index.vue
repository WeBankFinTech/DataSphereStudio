<template>
  <div ref="domRef" style="width: 100%;height: 1200px"></div>
</template>
<script>
import G6 from '@antv/g6';
import formatDate from '@/apps/dataModelCenter/utils/formatDate'

/**
 * @description 格式化文本溢出隐藏
 * @param text {String} 要截取的文本
 * @param length {Number} 指定长度
 * @param elipsis {String} 填充文本
 * @returns {string|*}
 */
function formatText(text, length = 5, elipsis = '...') {
  if (!text) return '';
  if (text.length > length) return `${text.substr(0, length)}${elipsis}`;
  return text;
}

const tooltip = new G6.Tooltip({
  offsetX: 10,
  offsetY: 20,
  getContent(e) {
    let data = e.item.getModel();
    let box = document.createElement('span');
    box.innerHTML = `
      <div style="width: 200px">
        <h4>名称: ${data.label || data.id}</h4>
        <ul>
          <li>创建时间：${formatDate(data.data.attributes.createTime)}</li>
          <li>创建人：${data.data.attributes.owner}</li>
          <li>状态：${data.data.status}</li>
        </ul>
      </div>
    `;
    return box;
  },
  itemTypes: ['node'],
});

// 注册节点
G6.registerNode('entitie_node', {
  draw(cfg, group) {
    const {size, img, style, isCurrent} = cfg;
    // 如果 cfg 中定义了 style 需要同这里的属性进行融合
    const keyShape = group.addShape('circle', {
      attrs: {
        stroke: isCurrent ? '#1890ff' : '',
        r: size[0] / 2,
        lineWidth: 3,
        fill: isCurrent ? '#ffffff' : '',
        x: 0,
        y: 0,
      },
      name: 'circle-shape',
    });
    group.addShape('image', {
      attrs: {
        img: img,
        x: -20,
        y: -20,
        width: size[0] * 0.8,
        height: size[1] * 0.8,
        cursor: 'pointer',
      },
      name: 'image-shape',
      draggable: true,
    });
    group.addShape('text', {
      attrs: {
        x: 0, // 居中
        y: size[1] / 2 + (isCurrent ? 14 : 6),
        textAlign: 'center',
        textBaseline: 'middle',
        text: formatText( cfg.label),
        fill: '#666',
      },
      name: 'text-shape',
    });
    return keyShape;
  },
});

/**
 * @description 工具函数
 * @param {Object} e
 */
function refreshDragedNodePosition(e) {
  const model = e.item.get('model');
  model.fx = e.x;
  model.fy = e.y;
}

export default {
  props: {
    processId: String,
    lineageData: {
      required: true
    }
  },
  methods: {
    // 根据类型获取图标
    getTypeIcon(type) {
      try {
        return require(`./icon/${type.toLowerCase()}.png`);
      } catch (error) {
        let typeArr = type.split('_');
        if (typeArr[0] === type) return '';
        return this.getTypeIcon(type.replace(`_${typeArr[typeArr.length - 1]}`, ''));
      }
    }
  },
  computed: {
    data: {
      get: function () {
        let data = this.lineageData
        if (data.guidEntityMap) {
          let nodes = Object.values(data.guidEntityMap).map((node) => {
            console.log(node)
            return {
              id: node.guid,
              label: node.displayText,
              img: this.getTypeIcon(node.typeName),
              data: node,
              isCurrent: data.baseEntityGuid === node.guid,
            };
          });
          let edges = data.relations.map((edge) => {
            return {
              source: edge.fromEntityId,
              target: edge.toEntityId,
              id: edge.relationshipId,
              direction: edge.fromEntityId === data.baseEntityGuid ? 'out' : edge.toEntityId === data.baseEntityGuid ? 'enter' : null,
              style: {
                stroke: edge.fromEntityId === data.baseEntityGuid ? '#ff4d4f' : edge.toEntityId === data.baseEntityGuid ? '#36cfc9' : '#ffc53d',
              },
            };
          });
          return {nodes, edges};
        }
        return {nodes: [], edges: []};
      },
    }
  },
  mounted() {
    let domRef = this.$refs['domRef']
    let {width, height} = domRef.getBoundingClientRect()
    const graph = new G6.Graph({
      container: domRef,
      width: width,
      height: height,
      animate: true,
      modes: {
        default: ['drag-canvas', 'zoom-canvas'],
      },
      linkCenter: false,
      layout: {
        type: 'force',
        nodeStrength: -3000,
        preventOverlap: true,
        nodeSize: 60, // 布局参数，节点大小，用于判断节点是否重叠
        linkDistance: 100, // 布局参数，边长
      },
      defaultNode: {
        type: 'entitie_node',
        size: [50, 50],
      },
      defaultEdge: {
        style: {
          cursor: 'pointer',
          lineWidth: 3,
          endArrow: true,
        },
      },
      plugins: [toolbar, tooltip],
    });
    graph.data({
      nodes: this.data.nodes,
      edges: this.data.edges,
    });
    graph.render();
    graph.on('node:dragstart', function (e) {
      graph.layout();
      refreshDragedNodePosition(e);
    });
    graph.on('node:drag', function (e) {
      const forceLayout = graph.get('layoutController').layoutMethods[0];
      forceLayout.execute();
      refreshDragedNodePosition(e);
    });
    graph.on('node:dragend', function (e) {
      e.item.get('model').fx = null;
      e.item.get('model').fy = null;
    });
  }
}
</script>
<style scoped lang="scss">

</style>
