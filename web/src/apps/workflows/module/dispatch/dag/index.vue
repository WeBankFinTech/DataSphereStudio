<template>
  <div></div>
</template>

<script>
import $ from 'jquery'
import {Canvas} from 'butterfly-dag'
import 'butterfly-dag/dist/index.css'

import NNode from './source/normal-node'

export default {
  name: 'dag',
  data () {
    return {
      canvas: null
    }
  },
  mixins: [],
  props: {
    processId: Number,
    dagData: {
      default: {
        nodes: [],
        edges: []
      },
      required: true
    }
  },
  methods: {
    createOrUpdateCanvas () {
      if (!this.canvas) {
        this.canvas = new Canvas({
          root: $('.dag-page')[0],
          zoomable: false,         //可缩放(可传)
          moveable: true,         //可平移(可传)
          draggable: false,        //节点可拖动(可传)
        })
        this.dagData.nodes.forEach(node=> {
          node.Class = NNode
        })
        this.canvas.draw(this.dagData)
      } else {
        this.dagData.nodes.forEach(node=> {
          node.Class = NNode
        })
        this.canvas.redraw(this.dagData)
      }
    },

  },
  watch: {
    dagData() {
      this.createOrUpdateCanvas()
    }
  },
  beforeCreate () {
  },
  created () {
    this.createOrUpdateCanvas()
  },
  beforeMount () {
  },
  mounted () {
  },
  beforeUpdate () {
  },
  updated () {
  },
  beforeDestroy () {
  },
  destroyed () {
  },
  computed: {
  },
  components: {}
}
</script>

<style lang="scss" scoped>

</style>
