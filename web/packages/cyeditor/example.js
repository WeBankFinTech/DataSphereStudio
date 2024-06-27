import CyEditor from './index'
import './index.scss'
import test from './defaults/vite.svg'
const container = '#app'
// 右键菜单配置示例
const contextMenu = {
  menus: [],
  beforeShow: (e, menus) => {
    let type = 'view'
    menus = [
      {
        id: 'delete',
        content: '删除',
      },
    ]
    if (e.target.isNode && e.target.isNode()) {
      type = 'node'
    } else if (e.target.isEdge && e.target.isEdge()) {
      type = 'link'
    } else {
      menus = []
    }
    return menus
  },
  beforeClose: () => {
    return true
  },
}
// 节点类型
const nodeTypes =  [{
  category: '基础',
  type: 'rect',
  name: '矩形1',
  image: test,
  bg: '#fff',
  width: 150,
  height: 40,
},{
  type: '高级',
  image: test,
  bg: '#999',
  width: 150,
  height: 40,
  name: '矩形2',
  category: '基础形状'
}]
/**
 * 节点\连线样式
 * @param {*} nodeTypes
 * @returns
 */
function getStyles (nodeTypes) {
  return [
    {
      selector: 'node',
      style: {
        shape: 'rectangle',
        'background-position-x': '2px',
        'background-position-y': '8px',
        'background-width': '24px',
        'background-height': '24px',
        'background-image': (e) => {
          const nodeItem = nodeTypes.find(
            (it) => it.type === e.data('type')
          );
          return (nodeItem && nodeItem.image) || { value: '' };
        },
      },
    },
    {
      selector: 'node[height]',
      style: {
        height: 'data(height)',
      },
    },
    {
      selector: 'node[width]',
      style: {
        width: 'data(width)',
      },
    },
    {
      selector: 'node[name]',
      style: {
        label: 'data(name)',
        'background-color': '#eee',
        'text-valign': 'center',
        'text-wrap': 'ellipsis',
        'text-max-width': '120px',
        'text-halign': 'center',
        'text-margin-x': '10px',
        'font-size': '13px',
      },
    },
    {
      selector: 'node:active',
      style: {
        'overlay-color': '#0169D9',
        'overlay-padding': 8,
        'overlay-opacity': 0.25,
      },
    },
    {
      selector: 'node:selected',
      style: {
        'overlay-color': '#0169D9',
        'overlay-padding': 8,
        'overlay-opacity': 0.25,
      },
    },
    {
      selector: 'edge',
      style: {
        'curve-style': 'bezier',
        'target-arrow-shape': 'triangle',
        width: 1,
      },
    },
    {
      selector: 'edge[lineType]',
      style: {
        'curve-style': 'data(lineType)',
      },
    },
    {
      selector: 'edge[lineColor]',
      style: {
        'target-arrow-shape': 'triangle',
        width: 1,
        'line-color': 'data(lineColor)',
        'source-arrow-color': 'data(lineColor)',
        'target-arrow-color': 'data(lineColor)',
        'mid-source-arrow-color': 'data(lineColor)',
        'mid-target-arrow-color': 'data(lineColor)',
      },
    },
    {
      selector: 'edge[lineColor]:active',
      style: {
        'overlay-color': '#0169D9',
        'overlay-padding': 3,
        'overlay-opacity': 0.25,
        'line-color': 'data(lineColor)',
        'source-arrow-color': 'data(lineColor)',
        'target-arrow-color': 'data(lineColor)',
        'mid-source-arrow-color': 'data(lineColor)',
        'mid-target-arrow-color': 'data(lineColor)',
      },
    },
    {
      selector: 'edge[lineColor]:selected',
      style: {
        'overlay-color': '#0169D9',
        'overlay-padding': 3,
        'overlay-opacity': 0.25,
        'line-color': 'data(lineColor)',
        'source-arrow-color': 'data(lineColor)',
        'target-arrow-color': 'data(lineColor)',
        'mid-source-arrow-color': 'data(lineColor)',
        'mid-target-arrow-color': 'data(lineColor)',
      },
    },
    {
      selector: '.eh-handle',
      style: {
        'background-color': 'red',
        width: 12,
        height: 12,
        shape: 'ellipse',
        'overlay-opacity': 0,
        'border-width': 12, // makes the handle easier to hit
        'border-opacity': 0,
        'background-opacity': 0.5,
      },
    },
    {
      selector: '.eh-hover',
      style: {
        'background-color': 'red',
        'background-opacity': 0.5,
      },
    },
    {
      selector: '.eh-source',
      style: {
        'border-width': 1,
        'border-color': 'red',
      },
    },
    {
      selector: '.eh-target',
      style: {
        'border-width': 1,
        'border-color': 'red',
      },
    },
    {
      selector: '.eh-preview, .eh-ghost-edge',
      style: {
        'background-color': 'red',
        'line-color': 'red',
        'target-arrow-color': 'red',
        'source-arrow-color': 'red',
      },
    },
    {
      selector: '.eh-ghost-edge.eh-preview-active',
      style: {
        opacity: 0,
      },
    },
  ];
}

// 初始化
let config = {
  cy: {
    layout: 'preset',
    styleEnabled: true,
    style: getStyles(nodeTypes),
    minZoom: 0.1,
    maxZoom: 3,
    boxSelectionEnabled: true,
    pan: { x: 50, y: 0 },
    panningEnabled: true,
    userPanningEnabled: true,
    userZoomingEnabled: true,
    zoom: 1,
    zoomingEnabled: true,
  },
  editor: {
    container,
    zoomRate: 0.1,
    lineType: 'bezier',
    dragAddNodes: true,
    snapGrid: true,
    contextMenu,
    navigator: true,
    nodeTypes,
    beforeAdd() {
      return true
    },
    afterAdd() {
      //
    },
    // getDomNode(data) {
    //   let div = document.createElement('div');
    //   div.className = 'cyeditor-dom-node';
    //   div.title = data.name;
    //   // const nodeItem = nodeTypes.find((it) => it.type === data.type);
    //   div.innerHTML = `<image src="${data.image}"><span>${data.name}<span>`;
    //   return div;
    // },
  },
}
const { editor, cy } = new CyEditor(config)

// 右键菜单点击事件
cy.on('cyeditor.ctxmenu', (scope, { menuItem, target, event }) => {
  let type = 'view'
  let el
  if (target.isNode && target.isNode()) {
    type = 'node'
    el = target.data()
  } else if (target.isEdge && target.isEdge()) {
    type = 'link'
    el = target.data()
    // if (menuItem.id === 'delete') {
    //   target.remove()
    // }
  }
  console.log('on-ctx-menu', menuItem.id, el || event, type)
})
// 监听事件
cy.on('remove', () => {})

editor.on('addnode', ({ target }) => {
  console.log('node-add', {
    ...target,
  })

  const data = editor.json()
  console.log(data)
})

editor.on('addlink', ({ target }) => {
  console.log('link-add', {
    ...target,
  })
})

editor.on('click', (e) => {
  if (e.target.isNode && e.target.isNode()) {
    console.log('node-click', e.target.data().id)
  }
})
editor.on('dblclick', (e) => {
  if (e.target.isNode && e.target.isNode()) {
    console.log('dblclick', e.target.data().id)
  }
})
// 方法API
editor.json({
  boxSelectionEnabled: true,
  elements: {
    "nodes": [
        {
            "data": {
                "type": "rect",
                "name": "矩形1",
                "bg": "#fff",
                "width": 150,
                "height": 40,
                "image": "/img/vite.8e3a10e1.svg",
                "id": "9158b849-d468-48ef-b59c-440ccd0c1009"
            },
            "position": {
                "x": 262.5,
                "y": 122.5
            },
            "group": "nodes",
            "removed": false,
            "selected": false,
            "selectable": true,
            "locked": false,
            "grabbable": true,
            "pannable": false,
            "classes": "eh-preview-active"
        },
        {
            "data": {
                "type": "rect",
                "name": "矩形1",
                "bg": "#fff",
                "width": 150,
                "height": 40,
                "image": "/img/vite.8e3a10e1.svg",
                "id": "18be7b5d-4925-408f-b99b-6829038489ae"
            },
            "position": {
                "x": 192.5,
                "y": 262.5
            },
            "group": "nodes",
            "removed": false,
            "selected": false,
            "selectable": true,
            "locked": false,
            "grabbable": true,
            "pannable": false,
            "classes": "eh-preview-active"
        },
        {
            "data": {
                "type": "rect",
                "name": "矩形1",
                "bg": "#fff",
                "width": 150,
                "height": 40,
                "image": "/img/vite.8e3a10e1.svg",
                "id": "86b161a5-2cac-4047-a1bd-f02514a13032"
            },
            "position": {
                "x": 157.5,
                "y": 332.5
            },
            "group": "nodes",
            "removed": false,
            "selected": false,
            "selectable": true,
            "locked": false,
            "grabbable": true,
            "pannable": false,
            "classes": ""
        }
    ],
    "edges": [
        {
            "data": {
                "source": "9158b849-d468-48ef-b59c-440ccd0c1009",
                "target": "18be7b5d-4925-408f-b99b-6829038489ae",
                "lineType": "bezier",
                "id": "85b93f50-0469-4c79-bb9d-e5e3c99c25cf"
            },
            "position": {
                "x": 17.5,
                "y": 17.5
            },
            "group": "edges",
            "removed": false,
            "selected": false,
            "selectable": true,
            "locked": false,
            "grabbable": true,
            "pannable": true,
            "classes": ""
        }
    ]
  },
  pan: { x: 0, y: 0 },
  panningEnabled: true,
  userPanningEnabled: true,
  userZoomingEnabled: true,
  zoom: 1,
  zoomingEnabled: true,
})

// editor.fit

// editor.zoom

// editor.json

// editor.png

// editor.toggleGrid

// editor.deleteSelected

// editor.destroy

// editor.layout
