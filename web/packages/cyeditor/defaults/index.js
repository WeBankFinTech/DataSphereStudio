/**
 * Created by DemonRay on 2019/3/24.
 */
import pluginStyles from './plugin-style'
import defaultEditorConfig from './editor-config'
import { defaultNodeTypes, defaultNodeStyles } from './node-types'
import { defaultEdgeTypes, defaultEdgeStyles, getEdgeConf } from './edge-types'

const defaultConfData = {
  node: {
    type: 'rectangle',
    bg: '#999',
    name: '',
    width: 150,
    height: 40,
    image: ''
  },
  edge: {
    lineColor: '#999'
  }
}

export {
  defaultConfData,
  defaultNodeTypes,
  defaultNodeStyles,
  defaultEdgeTypes,
  defaultEdgeStyles,
  getEdgeConf,
  pluginStyles,
  defaultEditorConfig
}
