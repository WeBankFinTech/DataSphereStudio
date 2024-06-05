import { defaultNodeTypes, defaultNodeStyles } from './node-types'
import { defaultEdgeStyles } from './edge-types'
import pluginStyles from './plugin-style'

export default {
  cy: {
    layout: {
      name: 'concentric',
      fit: false,
      concentric: function (n) { return 0 },
      minNodeSpacing: 100
    },
    styleEnabled: true,
    style: [
      ...defaultEdgeStyles,
      ...defaultNodeStyles,
      ...pluginStyles
    ],
    minZoom: 0.1,
    maxZoom: 10
  },
  editor: {
    container: '',
    zoomRate: 0.2,
    lineType: 'bezier',
    dragAddNodes: true,
    snapGrid: true,
    contextMenu: true,
    navigator: true,
    nodeTypes: defaultNodeTypes,
    beforeAdd (el) {
      return true
    },
    afterAdd (el) {
      //
    }
  }
}
