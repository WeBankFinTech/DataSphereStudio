/**
 * Created by DemonRay on 2019/3/24.
 */

import roundRectangle from './vite.svg'


const defaultNodeTypes = [
  {
    type: 'rectangle',
    image: roundRectangle,
    bg: '#999',
    width: 150,
    height: 40,
    name: '矩形',
    category: '基础形状'
  }
]

const defaultNodeStyles = [{
  'selector': 'node[type]',
  'style': {
    'shape': 'data(type)',
    'label': 'data(type)',
    'height': 'data(height)',
    'width': 'data(width)',
    'text-valign': 'center',
    'text-halign': 'center'
  }
}, {
  'selector': 'node[points]',
  'style': {
    'shape-polygon-points': 'data(points)',
    'label': 'polygon\n(custom points)',
    'text-wrap': 'wrap'
  }
}, {
  'selector': 'node[name]',
  'style': {
    // 'content': 'data(name)'
    'content': ''
  }
}, {
  'selector': 'node[image]',
  'style': {
    'background-opacity': 0,
    'background-fit': 'cover',
    'background-image': (e) => { return e.data('image') || { value: '' } }
  }
}, {
  'selector': 'node[bg]',
  'style': {
    'background-opacity': 0.45,
    'background-color': 'data(bg)',
    'border-width': 1,
    'border-opacity': 0.8,
    'border-color': 'data(bg)'
  }
}, {
  selector: 'node:active',
  style: {
    'overlay-color': '#0169D9',
    'overlay-padding': 8,
    'overlay-opacity': 0.25
  }
}, {
  selector: 'node:selected',
  style: {
    'overlay-color': '#0169D9',
    'overlay-padding': 8,
    'overlay-opacity': 0.25
  }
}]

export {
  defaultNodeTypes,
  defaultNodeStyles
}
