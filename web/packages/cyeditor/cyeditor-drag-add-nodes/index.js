/**
 * Created by DemonRay on 2019/3/22.
 */
import utils from '../utils'

const defaults = {
  container: false,
  addWhenDrop: true,
  nodeTypes: []
}

class DragAddNodes {
  constructor (cy, params) {
    this.cy = cy
    this._options = Object.assign({}, defaults, params)
    this._options.nodeTypes.forEach(item => {
      item.width = item.width || 76
      item.height = item.height || 76
      item.category = item.category || 'other'
    })
    this._options.nodeTypes.forEach(item => {
      item._id = utils.guid()
    })
    if (this._options.nodeTypes.length < 1) return
    this._initShapePanel()
    this._initShapeItems()
    this._initEvents()
  }

  _initShapeItems (searchKey) {
    if (searchKey !== undefined) {
      utils.query('.shape-item').forEach(item => {
        item.style.display = item.dataset.name.indexOf(searchKey) > -1 ? 'flex' : 'none'
      })
    } else {
      const shapes = this._options.nodeTypes.filter(item => item.type && item.image)
      let categorys = {}
      let other = []
      shapes.forEach(item => {
        if (item.category) {
          if (categorys[item.category]) {
            categorys[item.category].push(item)
          } else {
            categorys[item.category] = [item]
          }
        } else {
          other.push(item)
        }
      })
      if (other.length) {
        categorys.other = other
      }
      let categoryDom = Object.keys(categorys).map(item => {
        let shapeItems = categorys[item].map(data => {
          return `<div class="shape-item" draggable="true" data-name="${data.name}" data-id="${data._id}"><img src="${data.image}" draggable="false"/><span>${data.name}</span></div>`
        }).join('')
        return `<div class="category">
                    <div class="panel-title">${item}</div>
                    <div class="shapes">${shapeItems}</div>
                  </div>`
      }).join('')
      this._shapePanel.querySelector('.shape-list').innerHTML = categoryDom
    }
  }
  _initEvents () {
    // 侧边栏收缩
    const svgContainer = this._shapePanel.querySelector('.svg-container');
    let toggleHandler
    if (svgContainer) {
      toggleHandler = () => {
        this.cy.trigger('cyeditor.hide_shapes_panel')
      }
      svgContainer.addEventListener('click', toggleHandler);
    }

    // 搜索过滤
    let searchInput = this._shapePanel.querySelector('input');
    let searchHandler
    if (searchInput) {
      searchHandler = (e) => {
        this._initShapeItems(e.target.value);
      }
      searchInput.addEventListener('input', searchHandler);
    }
    // 拖拽节点至画布
    let rightContainers = this.cy.container()
    this.rightContainers = rightContainers
    let handler = (e) => {
      e.preventDefault()
    }
    let dragStartHandler = (e) => {
      e.dataTransfer.setData('id', e.target.getAttribute('data-id'))
    }
    utils.query('.shape-item').forEach(item => {
      item.addEventListener('dragstart', dragStartHandler)
    })
    let dropHandler = (e) => {
      let shapeId = e.dataTransfer.getData('id')
      let pos = e.target.compareDocumentPosition(rightContainers)
      if (pos === 10) {
        let rect = { x: e.offsetX, y: e.offsetY }
        const shape = this._options.nodeTypes.find(item => item._id === shapeId)
        if (shape) {
          this._addNodeToCy(shape, rect)
        }
      }
    }
    rightContainers.addEventListener('drop', dropHandler)
    rightContainers.addEventListener('dragenter', handler)
    rightContainers.addEventListener('dragover', handler)

    this._removeHandle = () => {
      if (svgContainer) {
        svgContainer.removeEventListener('click', toggleHandler);
      }
      if (searchInput) {
        searchInput.removeEventListener('input', searchHandler);
      }
      rightContainers.removeEventListener('dragenter', handler)
      rightContainers.removeEventListener('dragover', handler)
      rightContainers.removeEventListener('drop', handler)
      utils.query('.shape-item').forEach(item => {
        item.removeEventListener('dragstart', dragStartHandler)
      })
    }
  }

  _addNodeToCy ({ type, width, height, bg, name = '', points, image }, rect) {
    let data = { type, name, bg, width, height }
    data.image = image
    let node = {
      group: 'nodes',
      data,
      position: rect
    }
    if (points) {
      node.data.points = points
    }
    if (this._options.addWhenDrop) {
      this.cy.trigger('cyeditor.addnode', node)
    }
  }

  _initShapePanel () {
    let { _options } = this
    if (_options.container) {
      if (typeof _options.container === 'string') {
        this._shapePanel = utils.query(_options.container)[ 0 ]
      } else if (utils.isNode(_options.container)) {
        this._shapePanel = _options.container
      }
      if (!this._shapePanel) {
        console.error('There is no any element matching your container')
      }
      this._shapePanel.innerHTML = `
      <div class="left-menu">
        <div class="shape-head">
          <div class="head-title">组件</div>
          <div class="head-btn">
            <span class="svg-container">
              <svg class="svg-icon" aria-hidden="true" width="1024px" height="1024px" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
                <path d="M798,708 C815.673112,708 830,722.326888 830,740 C830,757.496381 815.958217,771.713128 798.529179,771.995713 L798,772 L32,772 C14.326888,772 -2.164332e-15,757.673112 0,740 C2.14268868e-15,722.503619 14.0417829,708.286872 31.4708215,708.004287 L32,708 L798,708 Z M282.47518,170.867966 C294.971957,158.371188 315.233237,158.371188 327.730014,170.867966 C340.226791,183.364743 340.226791,203.626022 327.730014,216.1228 L327.730014,216.1228 L160.853521,382.999293 L327.730014,549.8772 C340.226791,562.373978 340.226791,582.635257 327.730014,595.132034 C315.233237,607.628812 294.971957,607.628812 282.47518,595.132034 L282.47518,595.132034 L92.9705628,405.627417 C80.4737854,393.13064 80.4737854,372.86936 92.9705628,360.372583 L92.9705628,360.372583 Z M798,472 C815.673112,472 830,486.326888 830,504 C830,521.496381 815.958217,535.713128 798.529179,535.995713 L798,536 L503,536 C485.326888,536 471,521.673112 471,504 C471,486.503619 485.041783,472.286872 502.470821,472.004287 L503,472 L798,472 Z M798,236 C815.673112,236 830,250.326888 830,268 C830,285.496381 815.958217,299.713128 798.529179,299.995713 L798,300 L503,300 C485.326888,300 471,285.673112 471,268 C471,250.503619 485.041783,236.286872 502.470821,236.004287 L503,236 L798,236 Z M798,0 C815.673112,0 830,14.326888 830,32 C830,49.4963809 815.958217,63.7131276 798.529179,63.9957129 L798,64 L32,64 C14.326888,64 -2.164332e-15,49.673112 0,32 C2.14268868e-15,14.5036191 14.0417829,0.286872383 31.4708215,0.00428708574 L32,0 L798,0 Z" id="path-1"></path>
              </svg>
            </span>
          </div>
        </div>
        <input placeholder="搜索" class="shape-search" autocomplete="off">
        <div class="shape-list"></div>
      </div>`
    } else {
      this._shapePanel = document.createElement('div')
      this._shapePanel.innerHTML = `<input placeholder="搜索" class="shape-search"  autocomplete="off"><div class="shape-list"></div>`
      document.body.appendChild(this._shapePanel)
    }
  }
  destroy() {
    this._removeHandle()
    this._shapePanel.remove()
  }
}

export default (cytoscape) => {
  if (!cytoscape) { return }

  cytoscape('core', 'dragAddNodes', function (params) {
    return new DragAddNodes(this, params)
  })
}
