/**
 * Created by DemonRay on 2019/3/25.
 */

import cytoscape from 'cytoscape'
import Klay from 'cytoscape-klay'
import utils from './utils'
import EventBus from './utils/eventbus'
import SnapGrid from './cyeditor-snap-grid'
import Cynavigator from './cyeditor-navigator'
import Edgehandles from './cyeditor-edgehandles'
import DragAddNodes from './cyeditor-drag-add-nodes'
import ContextMenu from './cyeditor-context-menu'
import DomNode from './cyeditor-dom-node'
import {
  defaultConfData,
  defaultEditorConfig,
  defaultNodeTypes,
} from './defaults/index'

class CyEditor extends EventBus {
  constructor(params = defaultEditorConfig) {
    super()
    this._plugins = {}
    this._listeners = {}
    this._initOptions(params)
    this._initDom()
    this._initCy()
    this._initPlugin()
    this._initEvents()
    if (params.disabled) {
      this.setOption('disabled', params.disabled)
    }
    return {
      editor: this,
      cy: this.cy,
    }
  }

  _initOptions(params = {}) {
    this.editorOptions = Object.assign(
      {},
      defaultEditorConfig.editor,
      params.editor
    )
    const { zoomRate } = this.editorOptions
    this._handleOptonsChange = {
      snapGrid: this._snapGridChange,
      lineType: this._lineTypeChange,
    }
    if (params.editor.nodeTypes) {
      this.setOption('nodeTypes', params.editor.nodeTypes)
    }
    if (zoomRate <= 0 || zoomRate >= 1) {
      console.error(
        'zoomRate must be float number, greater than 0 and less than 1'
      )
    }
    this.cyOptions = Object.assign({}, defaultEditorConfig.cy, params.cy)
    const { elements } = this.cyOptions
    if (elements) {
      if (Array.isArray(elements.nodes)) {
        elements.nodes.forEach((node) => {
          node.data = Object.assign({}, defaultConfData.node, node.data)
        })
      }
      if (Array.isArray(elements.edges)) {
        elements.edges.forEach((edge) => {
          edge.data = Object.assign({}, defaultConfData.edge, edge.data)
        })
      }
    }
  }

  _initCy() {
    this.cyOptions.container = this.editorContianer.querySelector('#cy')
    this.cy = cytoscape(this.cyOptions)
    if (this.editorOptions.getDomNode) {
      this.cy.domNode()
    }
  }

  _initDom() {
    let { dragAddNodes, navigator, container } = this.editorOptions
    let left = dragAddNodes ? `<div class="left"></div>` : ''
    let navigatorDom = navigator ? `<div class="thumb"></div>` : ''
    let right = ''
    if (navigator) {
      right = `<div class="tool-overlay">${navigatorDom}</div>`
    }
    let domHtml = `${left}<div id="cy" class="cy"></div> ${right}`
    let editorContianer
    if (container) {
      if (typeof container === 'string') {
        editorContianer = utils.query(container)[0]
      } else if (utils.isNode(container)) {
        editorContianer = container
      }
      editorContianer.className = 'cy-editor-container'
      if (!editorContianer.classList.contains('cy-editor-container')) {
        editorContianer.className =
          editorContianer.className + ' cy-editor-container'
      }
      if (!editorContianer) {
        console.error('There is no any element matching your container')
        return
      }
    } else {
      editorContianer = document.createElement('div')
      editorContianer.className = 'cy-editor-container'
      document.body.appendChild(editorContianer)
    }
    editorContianer.innerHTML = domHtml
    this.editorContianer = editorContianer
  }

  _initEvents() {
    const {edgehandles } = this._plugins;
    this._clicked = this._clicked || null
    this._listeners.handleClick = (e) => {
      if (edgehandles) {
        edgehandles.active = true;
        edgehandles.stop(e);
      }
      if (e.target.data().type === 'edgehandle') return

      if (this._clicked && this._clicked === e.target) {
        this._clicked = null
        e.preventDefault()
        e.stopPropagation()
        this.emit('dblclick', e.target)
      } else {
        this._clicked = e.target
        this.emit('click', e.target)
        setTimeout(() => {
          if (this._clicked && this._clicked === e.target) {
            this._clicked = null
          }
        }, 300)
      }
    }

    this._listeners.select = (e) => {
      if (this._doAction === 'select') return
    }

    this._listeners.addEles = (evt, el) => {
      if (el.position) {
        let panZoom = { pan: this.cy.pan(), zoom: this.cy.zoom() }
        let x = (el.position.x - panZoom.pan.x) / panZoom.zoom
        let y = (el.position.y - panZoom.pan.y) / panZoom.zoom
        el.position = { x, y }
      }
      if (!this._hook('beforeAdd', el, true)) return
      let dom
      if (this.editorOptions.getDomNode) {
        dom = this.editorOptions.getDomNode(el.data)
      }
      const nodeData = Object.assign({}, defaultConfData.node, {
        data: dom ? { ...el.data, dom } : { ...el.data },
        position: el.position,
      })
      const elObj = this.cy.add(nodeData)
      this._hook('afterAdd', el)
      this.emit('addnode', elObj.data(), this)
    }

    this._listeners.hideShapesPanel = () => {
      this.toggleShapesPanel()
      this.emit('hide_shapes_panel')
    }

    this.cy
      .on('click', this._listeners.handleClick)
      .on('select', this._listeners.select)
      .on('cyeditor.addnode', this._listeners.addEles)
      .on('cyeditor.hide_shapes_panel', this._listeners.hideShapesPanel)
    this.emit('ready')
  }

  _initPlugin() {
    const { dragAddNodes, contextMenu, snapGrid, navigator, getDomNode } =
      this.editorOptions
    if (getDomNode && !this.cy.domNode) {
      cytoscape.use(DomNode)
    }
    if (navigator && !this.cy.navigator) {
      cytoscape.use(Cynavigator)
    }
    if (snapGrid && !this.cy.snapGrid) {
      cytoscape.use(SnapGrid)
    }
    if (dragAddNodes && !this.cy.dragAddNodes) {
      cytoscape.use(DragAddNodes)
    }
    if (contextMenu && !this.cy.contextMenu) {
      cytoscape.use(ContextMenu)
    }
    if (!this.cy.edgehandles) {
      cytoscape.use(Edgehandles)
    }
    cytoscape.use(Klay)
    // edge
    this._plugins.edgehandles = this.cy.edgehandles({
      snap: false,
      handlePosition() {
        return 'middle middle'
      },
      complete: (sourceNode, targetNode, addedEles) => {
        this.emit('addlink', addedEles.data())
      },
      edgeParams: this._edgeParams.bind(this),
    })

    // drag node add to cy
    if (dragAddNodes) {
      this._plugins.dragAddNodes = this.cy.dragAddNodes({
        container: this.editorContianer.querySelector('.cy-editor-container .left'),
        nodeTypes: this.editorOptions.nodeTypes,
      })
    } else {
      this.editorContianer.className += ' hide_shapes_panel'
    }

    // snap-grid
    if (snapGrid) {
      this._plugins.cySnapToGrid = this.cy.snapToGrid()
    }

    // navigator
    if (navigator) {
      this._plugins.navigator = this.cy.navigator({
        container: this.editorContianer.querySelector('.cy-editor-container .thumb'),
        rerenderDelay: 1000
      })
    }

    // context-menu
    if (contextMenu) {
      this._plugins.contextMenu = this.cy.contextMenu({editorOptions: this.editorOptions, ...contextMenu})
    }
  }

  _snapGridChange() {
    if (!this._plugins.cySnapToGrid) return
    if (this.editorOptions.snapGrid) {
      this._plugins.cySnapToGrid.gridOn()
      this._plugins.cySnapToGrid.snapOn()
    } else {
      this._plugins.cySnapToGrid.gridOff()
      this._plugins.cySnapToGrid.snapOff()
    }
  }

  _edgeParams() {
    return {
      data: { lineType: this.editorOptions.lineType },
    }
  }

  _lineTypeChange(value) {
    let selected = this.cy.$('edge:selected')
    if (selected.length < 1) {
      selected = this.cy.$('edge')
    }
    selected.forEach((item) => {
      item.data({
        lineType: value,
      })
    })
  }

  _hook(hook, params, result = false) {
    if (typeof this.editorOptions[hook] === 'function') {
      const res = this.editorOptions[hook](params)
      return result ? res : true
    }
  }

  doCommand(evt, item) {
    switch (item.command) {
      case 'gridon':
        this.toggleGrid()
        break
      case 'zoomin':
        this.zoom(1)
        break
      case 'zoomout':
        this.zoom(-1)
        break
      case 'fit':
        this.fit()
        break
      case 'delete':
        this.deleteSelected()
        break
      case 'boxselect':
        this.cy.userPanningEnabled(!item.selected)
        this.cy.boxSelectionEnabled(item.selected)
        break
      default:
        break
    }
  }

  /**
   * change editor option, support snapGrid, lineType
   * @param {string|object} key
   * @param {*} value
   */
  setOption(key, value) {
    if (key === 'disabled' && this.cy) {
      this.editorOptions.disabled = value;
      if (this.editorOptions.disabled) {
        // 设置为只读模式
        this.cy.boxSelectionEnabled(false); // 禁用框选功能
        this.cy.autoungrabify(true); // 禁止节点拖动，但画布可拖动
        if (this._plugins.edgehandles) { // 禁止节点连线
          this._plugins.edgehandles.disable();
        }
      } else {
        // 取消只读模式
        this.cy.boxSelectionEnabled(true);
        this.cy.autoungrabify(false);
        if (this._plugins.edgehandles) {
          this._plugins.edgehandles.enable();
        }
      }
    } else if (typeof key === 'string') {
      this.editorOptions[key] = value
      if (typeof this._handleOptonsChange[key] === 'function') {
        this._handleOptonsChange[key].call(this, value)
      }
    } else if (typeof key === 'object') {
      Object.assign(this.editorOptions, key)
    }
  }

  deleteSelected() {
    let selected = this.cy.$(':selected')
    if (selected.length) {
      this.cy.remove(selected)
    }
  }

  async save() {
    try {
      let blob = await this.cy.png({ output: 'blob-promise' })
      if (window.navigator.msSaveBlob) {
        window.navigator.msSaveBlob(blob, `chart-${Date.now()}.png`)
      } else {
        let a = document.createElement('a')
        a.download = `chart-${Date.now()}.png`
        a.href = window.URL.createObjectURL(blob)
        a.click()
      }
    } catch (e) {
      console.log(e)
    }
  }

  fit() {
    if (!this._fit_status) {
      this._fit_status = { pan: this.cy.pan(), zoom: this.cy.zoom() }
      this.cy.fit()
    } else {
      this.cy.viewport({
        zoom: this._fit_status.zoom,
        pan: this._fit_status.pan,
      })
      this._fit_status = null
    }
  }

  zoom(type = 1, level) {
    level = level || this.editorOptions.zoomRate
    let w = this.cy.width()
    let h = this.cy.height()
    let zoom = this.cy.zoom() + level * type
    let pan = this.cy.pan()
    pan.x = pan.x + (-1 * w * level * type) / 2
    pan.y = pan.y + (-1 * h * level * type) / 2
    this.cy.viewport({
      zoom,
      pan,
    })
  }

  toggleGrid() {
    if (this._plugins.cySnapToGrid) {
      this.setOption('snapGrid', !this.editorOptions.snapGrid)
    } else {
      console.warn('Can not `toggleGrid`, please check the initialize option')
    }
  }

  jpg(opt = {}) {
    return this.cy.png(opt)
  }

  png(opt) {
    return this.cy.png(opt)
  }
  /**
   * Export the graph as JSON or Import the graph as JSON
   * @param {*} opt params for cy.json(opt)
   * @param {*} keys JSON Object keys
   */
  json(opt = false, keys) {
    keys = keys || [
      'boxSelectionEnabled',
      'elements',
      'pan',
      'panningEnabled',
      'userPanningEnabled',
      'userZoomingEnabled',
      'zoom',
      'zoomingEnabled',
    ]
    // export
    let json = {}
    if (typeof opt === 'boolean') {
      let cyjson = this.cy.json(opt)
      keys.forEach((key) => {
        json[key] = cyjson[key]
        if (key === 'elements') {
          // 连线辅助点过滤
          cyjson[key].nodes = (cyjson[key].nodes || []).filter(
            (it) => it.data.type !== 'edgehandle'
          )
          // 连线一定要有对应的source、target
          cyjson[key].edges = (cyjson[key].edges || []).filter(
            (it) =>
              cyjson[key].nodes.some(
                (item) => item.data.id === it.data.source
              ) &&
              cyjson[key].nodes.some((item) => item.data.id === it.data.target)
          )
        }
      })
      return json
    }
    // import
    if (typeof opt === 'object') {
      json = {}
      if (opt.elements) {
        if (Array.isArray(opt.elements.nodes)) {
          opt.elements.nodes.forEach((node) => {
            let dom
            if (this.editorOptions.getDomNode) {
              dom = this.editorOptions.getDomNode(node.data)
            }
            node.data = Object.assign({}, defaultConfData.node, {
              ...node.data,
              dom,
            })
          })
        }
        if (Array.isArray(opt.elements.edges)) {
          opt.elements.edges.forEach((edge) => {
            edge.data = Object.assign({}, defaultConfData.edge, edge.data)
          })
        }
      }
      keys.forEach((key) => {
        json[key] = opt[key]
      })
    }
    return this.cy.json(json)
  }

  /**
   * get or set data
   * @param {string|object} name
   * @param {*} value
   */
  data(name, value) {
    return this.cy.data(name, value)
  }

  /**
   *  remove data
   * @param {string} names  split by space
   */
  removeData(names) {
    this.cy.removeData(names)
  }

  /**
   * 格式化
   * @param config
   */
  layout(
    config = {
      name: 'klay',
      fit: false,
    }
  ) {
    this.cy.layout(config).run()
  }

  toggleShapesPanel() {
    if ( this._plugins.dragAddNodes ) {
      let className = this.editorContianer.className.trim()
      if (className.indexOf('hide_shapes_panel') > -1) {
        this.editorContianer.className = className.replace('hide_shapes_panel', '')
      } else {
        this.editorContianer.className = className + ' hide_shapes_panel'
      }
    }
  }

  destroy() {
    this.cy.removeAllListeners()
    this.cy.destroy()
    this.clear()
    if (this._plugins.navigator) {
      this._plugins.navigator.destroy()
    }
    if (this._plugins.edgehandles) {
      this._plugins.edgehandles.destroy()
    }
    if (this._plugins.dragAddNodes) {
      this._plugins.dragAddNodes.destroy()
    }
    if (this._plugins.ContextMenu) {
      this._plugins.ContextMenu.destroy()
    }
    this._listeners = {}
  }
}

export default CyEditor
