<template>
  <div>
    <div>
    </div>
  </div>
</template>
<script>
import node from './base_node';
import RelationEdge from './edge';
import DagreCanvas from './dagre-canvas';
import './index.scss';
import 'butterfly-dag/dist/index.css';

export default {
  name: 'lineage',
  data() {
    return {
      canvas: null
    }
  },
  props: {
    processId: String,
    lineageData: {
      required: true
    }
  },
  methods: {
    convertData() {
      let data = {
        nodes: [],
        edges: []
      }
      let keys = Object.keys(this.lineageData.guidEntityMap)
      keys.forEach(item => {
        const cur = this.lineageData.guidEntityMap[item]
        let icon
        if (cur.status !== 'ACTIVE') {
          icon = '已失效表'
        } else if (cur.typeName === 'hive_table') {
          icon = '生效表'
        } else if (cur.typeName === 'hive_process') {
          icon = 'hp'
        } else if (cur.typeName === 'spark_process') {
          icon = 'sp'
        }
        data.nodes.push({
          id: item,
          name: cur.displayText,
          icon: icon,
          Class: node,
          className: 'nodeBackground-color'
        })
      })
      this.lineageData.relations.forEach(relation => {
        data.edges.push({
          source: relation.fromEntityId,
          target: relation.toEntityId
        })
      })
      return data
    },
    createOrUpdateCanvas (data) {
      let root = document.getElementById('dag-canvas');
      if (!this.canvas) {
        this.canvas = new DagreCanvas({
          root: root,
          disLinkable: true, // 可删除连线
          linkable: true,    // 可连线
          //draggable: true,   // 可拖动
          zoomable: true,    // 可放大
          moveable: true,    // 可平移
          layout: {
            type: 'dagreLayout',
            options: {
              rankdir: 'LR',
              nodesep: 40,
              ranksep: 40,
              controlPoints: false,
            },
          },
          theme: {
            edge: {
              shapeType: 'AdvancedBezier',
              arrow: true,
              arrowPosition: 0.5,
              Class: RelationEdge
            }
          }
        })
        this.canvas.draw(data);
      } else {
        this.canvas.drageReDraw(data);
      }
    }
  },
  mounted () {
    this.createOrUpdateCanvas(this.convertData())
  }
}
</script>
