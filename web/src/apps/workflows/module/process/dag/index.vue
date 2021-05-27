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

    }
  },
  mixins: [],
  props: {
    processId: Number
  },
  methods: {
    onTab () {
    },

  },
  watch: {
  },
  beforeCreate () {
  },
  created () {
    let canvas = new Canvas({
      root: $('.dag-page')[0],
      zoomable: false,         //可缩放(可传)
      moveable: false,         //可平移(可传)
      draggable: false,        //节点可拖动(可传)
    });
    canvas.draw({
      groups: [],  //分组信息
      nodes: [{
        id: 'test1',
        left: 10,
        top: 10,
        label: '1',
        Class: NNode
      },{
        id: 'test2',
        left: 100,
        top: 100,
        label: '2',
        Class: NNode
      }],  //节点信息
      edges: [{
        type: 'node',
        source: 'test1',
        target: 'test2',
        arrow: true,
        arrowPosition: 1,
        arrowOffset: -10,  // 箭头的最终位置：线条长度 * arrowPosition + arrowOffset
        label: 'execute'
      }]  // 连线信息
    })
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
