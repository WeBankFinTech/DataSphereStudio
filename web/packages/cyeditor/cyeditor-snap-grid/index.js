/**
 * Created by DemonRay on 2019/3/21.
 */
import utils from '../utils'

class SnapToGrid {
  constructor (cy, params) {
    this.cy = cy
    let defaults = {
      stackOrder: -1,
      gridSpacing: 35,
      strokeStyle: '#eeeeee',
      lineWidth: 1.0,
      lineDash: [ 5, 8 ],
      zoomDash: true,
      panGrid: true,
      snapToGrid: true,
      drawGrid: true
    }
    this._options = utils.extend(true, {}, defaults, params)
    this._init()
    this._initEvents()
  }

  _init () {
    this._container = this.cy.container()
    this.canvas = document.createElement('canvas')
    this.ctx = this.canvas.getContext('2d')
    this._container.append(this.canvas)
  }

  _initEvents () {
    window.addEventListener('resize', () => this._resizeCanvas())

    this.cy.ready(() => {
      this._resizeCanvas()
      if (this._options.snapToGrid) {
        this.snapAll()
      }
      this.cy.on('zoom', () => this._drawGrid())
      this.cy.on('pan', () => this._drawGrid())
      this.cy.on('free', (e) => this._nodeFreed(e))
      this.cy.on('add', (e) => this._nodeAdded(e))
    })
  }

  _resizeCanvas () {
    let rect = this._container.getBoundingClientRect()
    this.canvas.height = rect.height
    this.canvas.width = rect.width
    this.canvas.style.position = 'absolute'
    this.canvas.style.top = 0
    this.canvas.style.left = 0
    this.canvas.style.zIndex = this._options.stackOrder

    setTimeout(() => {
      let canvasBb = utils.offset(this.canvas)
      let containerBb = utils.offset(this._container)
      this.canvas.style.top = -(canvasBb.top - containerBb.top)
      this.canvas.style.left = -(canvasBb.left - containerBb.left)
      this._drawGrid()
    }, 0)
  }

  _drawGrid () {
    this.clear()

    if (!this._options.drawGrid) {
      return
    }

    let zoom = this.cy.zoom()
    let rect = this._container.getBoundingClientRect()
    let canvasWidth = rect.width
    let canvasHeight = rect.height
    let increment = this._options.gridSpacing * zoom
    let pan = this.cy.pan()
    let initialValueX = pan.x % increment
    let initialValueY = pan.y % increment

    this.ctx.strokeStyle = this._options.strokeStyle
    this.ctx.lineWidth = this._options.lineWidth

    if (this._options.zoomDash) {
      let zoomedDash = this._options.lineDash.slice()

      for (let i = 0; i < zoomedDash.length; i++) {
        zoomedDash[ i ] = this._options.lineDash[ i ] * zoom
      }
      this.ctx.setLineDash(zoomedDash)
    } else {
      this.ctx.setLineDash(this._options.lineDash)
    }

    if (this._options.panGrid) {
      this.ctx.lineDashOffset = -pan.y
    } else {
      this.ctx.lineDashOffset = 0
    }

    for (let i = initialValueX; i < canvasWidth; i += increment) {
      this.ctx.beginPath()
      this.ctx.moveTo(i, 0)
      this.ctx.lineTo(i, canvasHeight)
      this.ctx.stroke()
    }

    if (this._options.panGrid) {
      this.ctx.lineDashOffset = -pan.x
    } else {
      this.ctx.lineDashOffset = 0
    }

    for (let i = initialValueY; i < canvasHeight; i += increment) {
      this.ctx.beginPath()
      this.ctx.moveTo(0, i)
      this.ctx.lineTo(canvasWidth, i)
      this.ctx.stroke()
    }
  }

  _nodeFreed (ev) {
    if (this._options.snapToGrid) {
      this.snapNode(ev.target)
    }
  }

  _nodeAdded (ev) {
    if (this._options.snapToGrid) {
      this.snapNode(ev.target)
    }
  }
  snapNode (node) {
    let pos = node.position()

    let cellX = Math.floor(pos.x / this._options.gridSpacing)
    let cellY = Math.floor(pos.y / this._options.gridSpacing)

    node.position({
      x: (cellX + 0.5) * this._options.gridSpacing,
      y: (cellY + 0.5) * this._options.gridSpacing
    })
  }

  snapAll () {
    this.cy.nodes().each((node) => {
      this.snapNode(node)
    })
  }

  refresh () {
    this._resizeCanvas()
  }

  snapOn () {
    this._options.snapToGrid = true
    this.snapAll()
    this.cy.trigger('cyeditor.snapgridon')
  }

  snapOff () {
    this._options.snapToGrid = false
    this.cy.trigger('cyeditor.snapgridoff')
  }

  gridOn () {
    this._options.drawGrid = true
    this._drawGrid()
    this.cy.trigger('cyeditor.gridon')
  }

  gridOff () {
    this._options.drawGrid = false
    this._drawGrid()
    this.cy.trigger('cyeditor.gridoff')
  }

  clear () {
    let rect = this._container.getBoundingClientRect()
    let width = rect.width
    let height = rect.height

    this.ctx.clearRect(0, 0, width, height)
  }
}

export default (cytoscape) => {
  if (!cytoscape) { return }

  cytoscape('core', 'snapToGrid', function (options) {
    return new SnapToGrid(this, options)
  })
}
