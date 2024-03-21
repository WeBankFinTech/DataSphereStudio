/**
 * Created by DemonRay on 2019/3/20.
 */
import utils from '../utils'

let defaults = {
  container: false, // can be a selector
  viewLiveFramerate: 0, // set false to update graph pan only on drag end; set 0 to do it instantly; set a number (frames per second) to update not more than N times per second
  dblClickDelay: 200, // milliseconds
  removeCustomContainer: true, // destroy the container specified by user on plugin destroy
  rerenderDelay: 500 // ms to throttle rerender updates to the panzoom for performance
}

class Navigator {
  constructor (cy, options) {
    this.cy = cy
    this._options = utils.extend({}, defaults, options)
    this._init(cy, options)
  }

  _init () {
    this._cyListeners = []
    this._contianer = this.cy.container()

    // Cache bounding box
    this.boundingBox = this.bb()

    let eleRect = this._contianer.getBoundingClientRect()
    // Cache sizes
    this.width = eleRect.width
    this.height = eleRect.height

    // Init components
    this._initPanel()
    this._initThumbnail()
    this._initView()
    this._initOverlay()
  }

  _addCyListener (events, handler) {
    this._cyListeners.push({
      events: events,
      handler: handler
    })

    this.cy.on(events, handler)
  }

  _removeCyListeners () {
    let cy = this.cy

    this._cyListeners.forEach(function (l) {
      cy.off(l.events, l.handler)
    })

    cy.offRender(this._onRenderHandler)
  }

  _initPanel () {
    let options = this._options

    if (options.container) {
      if (typeof options.container === 'string') {
        this.$panel = utils.query(options.container)[ 0 ]
      } else if (utils.isNode(options.container)) {
        this.$panel = options.container
      }
      if (!this.$panel) {
        console.error('There is no any element matching your container')
        return
      }
    } else {
      this.$panel = document.createElement('div')
      document.body.appendChild(this.$panel)
    }

    this.$panel.classList.add('cytoscape-navigator')

    this._setupPanel()
    this._addCyListener('resize', this.resize.bind(this))
  }

  _setupPanel () {
    let panelRect = this.$panel.getBoundingClientRect()
    // Cache sizes
    this.panelWidth = panelRect.width
    this.panelHeight = panelRect.height
  }

  _initThumbnail () {
    // Create thumbnail
    this.$thumbnail = document.createElement('img')

    // Add thumbnail canvas to the DOM
    this.$panel.appendChild(this.$thumbnail)

    // Setup thumbnail
    this._setupThumbnailSizes()
    this._updateThumbnailImage()
  }

  _setupThumbnailSizes () {
    // Update bounding box cache
    this.boundingBox = this.bb()

    this.thumbnailZoom = Math.min(this.panelHeight / this.boundingBox.h, this.panelWidth / this.boundingBox.w)

    // Used on thumbnail generation
    this.thumbnailPan = {
      x: (this.panelWidth - this.thumbnailZoom * (this.boundingBox.x1 + this.boundingBox.x2)) / 2,
      y: (this.panelHeight - this.thumbnailZoom * (this.boundingBox.y1 + this.boundingBox.y2)) / 2
    }
  }

  // If bounding box has changed then update sizes
  // Otherwise just update the thumbnail
  _checkThumbnailSizesAndUpdate () {
    // Cache previous values
    let _zoom = this.thumbnailZoom
    let _panX = this.thumbnailPan.x
    let _panY = this.thumbnailPan.y

    this._setupThumbnailSizes()

    this._updateThumbnailImage()
    if (_zoom !== this.thumbnailZoom || _panX !== this.thumbnailPan.x || _panY !== this.thumbnailPan.y) {
      this._setupView()
    }
  }

  _initView () {
    this.$view = document.createElement('div')
    this.$view.className = 'cytoscape-navigatorView'
    this.$panel.appendChild(this.$view)

    let viewStyle = window.getComputedStyle(this.$view)

    // Compute borders
    this.viewBorderTop = parseInt(viewStyle[ 'border-top-width' ], 10)
    this.viewBorderRight = parseInt(viewStyle[ 'border-right-width' ], 10)
    this.viewBorderBottom = parseInt(viewStyle[ 'border-bottom-width' ], 10)
    this.viewBorderLeft = parseInt(viewStyle[ 'border-left-width' ], 10)

    // Abstract borders
    this.viewBorderHorizontal = this.viewBorderLeft + this.viewBorderRight
    this.viewBorderVertical = this.viewBorderTop + this.viewBorderBottom

    this._setupView()

    // Hook graph zoom and pan
    this._addCyListener('zoom pan', this._setupView.bind(this))
  }

  _setupView () {
    if (this.viewLocked) { return }

    let cyZoom = this.cy.zoom()
    let cyPan = this.cy.pan()

    // Horizontal computation
    this.viewW = this.width / cyZoom * this.thumbnailZoom
    this.viewX = -cyPan.x * this.viewW / this.width + this.thumbnailPan.x - this.viewBorderLeft

    // Vertical computation
    this.viewH = this.height / cyZoom * this.thumbnailZoom
    this.viewY = -cyPan.y * this.viewH / this.height + this.thumbnailPan.y - this.viewBorderTop

    // CSS view
    this.$view.style.width = this.viewW + 'px'
    this.$view.style.height = this.viewH + 'px'
    this.$view.style.position = 'absolute'
    this.$view.style.left = this.viewX + 'px'
    this.$view.style.top = this.viewY + 'px'
  }

  /*
     * Used inner attributes
     *
     * timeout {number} used to keep stable frame rate
     * lastMoveStartTime {number}
     * inMovement {boolean}
     * hookPoint {object} {x: 0, y: 0}
     */
  _initOverlay () {
    // Used to capture mouse events
    this.$overlay = document.createElement('div')
    this.$overlay.className = 'cytoscape-navigatorOverlay'

    // Add overlay to the DOM
    this.$panel.appendChild(this.$overlay)

    // Init some attributes
    this.overlayHookPointX = 0
    this.overlayHookPointY = 0

    // Listen for events
    this._initEventsHandling()
  }

  _initEventsHandling () {
    let eventsLocal = [
      // Mouse events
      'mousedown',
      'mousewheel',
      'DOMMouseScroll', // Mozilla specific event
      // Touch events
      'touchstart'
    ]
    let eventsGlobal = [
      'mouseup',
      'mouseout',
      'mousemove',
      // Touch events
      'touchmove',
      'touchend'
    ]

    // handle events and stop their propagation
    let overlayListener = (env) => {
      // Touch events
      let ev = utils.extend({}, env)
      if (ev.type === 'touchstart') {
        // Will count as middle of View
        ev.offsetX = this.viewX + this.viewW / 2
        ev.offsetY = this.viewY + this.viewH / 2
      }

      // Normalize offset for browsers which do not provide that value
      if (ev.offsetX === undefined || ev.offsetY === undefined) {
        let targetOffset = utils.offset(ev.target)
        ev.offsetX = ev.pageX - targetOffset.left
        ev.offsetY = ev.pageY - targetOffset.top
      }

      if (ev.type === 'mousedown' || ev.type === 'touchstart') {
        this._eventMoveStart(ev)
      } else if (ev.type === 'mousewheel' || ev.type === 'DOMMouseScroll') {
        this._eventZoom(ev)
      }

      env.preventDefault()
      // Prevent default and propagation
      // Don't use peventPropagation as it breaks mouse events
      return false
    }

    // Hook global events
    let globalListener = (env) => {
      let ev = utils.extend({}, env)
      // Do not make any computations if it is has no effect on Navigator
      if (!this.overlayInMovement) return
      // Touch events
      if (ev.type === 'touchend') {
        // Will count as middle of View
        ev.offsetX = this.viewX + this.viewW / 2
        ev.offsetY = this.viewY + this.viewH / 2
      } else if (ev.type === 'touchmove') {
        // Hack - we take in account only first touch
        ev.pageX = ev.touches[ 0 ].pageX
        ev.pageY = ev.touches[ 0 ].pageY
      }

      // Normalize offset for browsers which do not provide that value
      if (ev.target && (ev.offsetX === undefined || ev.offsetY === undefined)) {
        let targetOffset = utils.offset(ev.target)
        ev.offsetX = ev.pageX - targetOffset.left
        ev.offsetY = ev.pageY - targetOffset.top
      }

      // Translate global events into local coordinates
      if (ev.target && ev.target !== this.$overlay) {
        let targetOffset = utils.offset(ev.target)
        let overlayOffset = utils.offset(this.$overlay)

        if (targetOffset && overlayOffset) {
          ev.offsetX = ev.offsetX - overlayOffset.left + targetOffset.left
          ev.offsetY = ev.offsetY - overlayOffset.top + targetOffset.top
        } else {
          return false
        }
      }

      if (ev.type === 'mousemove' || ev.type === 'touchmove') {
        this._eventMove(ev)
      } else if (ev.type === 'mouseup' || ev.type === 'touchend') {
        this._eventMoveEnd(ev)
      }

      env.preventDefault()
      // Prevent default and propagation
      // Don't use peventPropagation as it breaks mouse events
      return false
    }

    eventsLocal.forEach((item) => {
      this.$overlay.addEventListener(item, overlayListener)
    })

    eventsGlobal.forEach((item) => {
      window.addEventListener(item, globalListener)
    })

    this._removeEventsHandling = () => {
      eventsGlobal.forEach(item => {
        window.removeEventListener(item, globalListener)
      })
      eventsLocal.forEach(item => {
        this.$overlay.addEventListener(item, overlayListener)
      })
    }
  }

  _eventMoveStart (ev) {
    let now = new Date().getTime()

    // Check if it was double click
    if (this.overlayLastMoveStartTime &&
            this.overlayLastMoveStartTime + this._options.dblClickDelay > now) {
      // Reset lastMoveStartTime
      this.overlayLastMoveStartTime = 0
      // Enable View in order to move it to the center
      this.overlayInMovement = true

      // Set hook point as View center
      this.overlayHookPointX = this.viewW / 2
      this.overlayHookPointY = this.viewH / 2

      // Move View to start point
      if (this._options.viewLiveFramerate !== false) {
        this._eventMove({
          offsetX: this.panelWidth / 2,
          offsetY: this.panelHeight / 2
        })
      } else {
        this._eventMoveEnd({
          offsetX: this.panelWidth / 2,
          offsetY: this.panelHeight / 2
        })
      }

      this.cy.reset()

      // View should be inactive as we don't want to move it right after double click
      this.overlayInMovement = false
    } else {
      // This is a single click
      // Take care as single click happens before double click 2 times
      this.overlayLastMoveStartTime = now
      this.overlayInMovement = true
      // Lock view moving caused by cy events
      this.viewLocked = true

      // if event started in View
      if (ev.offsetX >= this.viewX && ev.offsetX <= this.viewX + this.viewW &&
                ev.offsetY >= this.viewY && ev.offsetY <= this.viewY + this.viewH
      ) {
        this.overlayHookPointX = ev.offsetX - this.viewX
        this.overlayHookPointY = ev.offsetY - this.viewY
      } else {
        // Set hook point as View center
        this.overlayHookPointX = this.viewW / 2
        this.overlayHookPointY = this.viewH / 2

        // Move View to start point
        this._eventMove(ev)
      }
    }
  }

  _eventMove (ev) {
    this._checkMousePosition(ev)

    // break if it is useless event
    if (!this.overlayInMovement) {
      return
    }

    // Update cache
    this.viewX = ev.offsetX - this.overlayHookPointX
    this.viewY = ev.offsetY - this.overlayHookPointY

    // Update view position
    this.$view.style.left = this.viewX + 'px'
    this.$view.style.top = this.viewY + 'px'

    // Move Cy
    if (this._options.viewLiveFramerate !== false) {
      // trigger instantly
      if (this._options.viewLiveFramerate === 0) {
        this._moveCy()
      } else if (!this.overlayTimeout) {
        // Set a timeout for graph movement
        this.overlayTimeout = setTimeout(() => {
          this._moveCy()
          this.overlayTimeout = false
        }, 1000 / this._options.viewLiveFramerate)
      }
    }
  }

  _checkMousePosition (ev) {
    // If mouse in over View
    if (ev.offsetX > this.viewX && ev.offsetX < this.viewX + this.viewBorderHorizontal + this.viewW &&
            ev.offsetY > this.viewY && ev.offsetY < this.viewY + this.viewBorderVertical + this.viewH) {
      this.$panel.classList.add('mouseover-view')
    } else {
      this.$panel.classList.remove('mouseover-view')
    }
  }

  _eventMoveEnd (ev) {
    // Unlock view changing caused by graph events
    this.viewLocked = false

    // Remove class when mouse is not over Navigator
    this.$panel.classList.remove('mouseover-view')

    if (!this.overlayInMovement) {
      return
    }

    // Trigger one last move
    this._eventMove(ev)

    // If mode is not live then move graph on drag end
    if (this._options.viewLiveFramerate === false) {
      this._moveCy()
    }

    // Stop movement permission
    this.overlayInMovement = false
  }

  _eventZoom (ev) {
    let zoomRate = Math.pow(10, ev.wheelDeltaY / 1000 || ev.wheelDelta / 1000 || ev.detail / -32)
    let mousePosition = {
      left: ev.offsetX,
      top: ev.offsetY
    }
    if (this.cy.zoomingEnabled()) {
      this._zoomCy(zoomRate, mousePosition)
    }
  }

  _updateThumbnailImage () {
    if (this._thumbnailUpdating) {
      return
    }

    this._thumbnailUpdating = true

    let render = () => {
      this._checkThumbnailSizesAndUpdate()
      this._setupView()

      let img = this.$thumbnail
      if (!img) return

      let w = this.panelWidth
      let h = this.panelHeight
      let bb = this.boundingBox
      let zoom = Math.min(w / bb.w, h / bb.h)

      let translate = {
        x: (w - zoom * (bb.w)) / 2,
        y: (h - zoom * (bb.h)) / 2
      }

      let png = this.cy.png({
        full: true,
        scale: zoom
      })

      if (png.indexOf('image/png') < 0) {
        img.removeAttribute('src')
      } else {
        img.setAttribute('src', png)
      }

      img.style.position = 'absolute'
      img.style.left = translate.x + 'px'
      img.style.top = translate.y + 'px'
    }

    this._onRenderHandler = utils.throttle(render, this._options.rerenderDelay)

    this.cy.onRender(this._onRenderHandler)
  }

  _moveCy () {
    this.cy.pan({
      x: -(this.viewX + this.viewBorderLeft - this.thumbnailPan.x) * this.width / this.viewW,
      y: -(this.viewY + this.viewBorderLeft - this.thumbnailPan.y) * this.height / this.viewH
    })
  }

  _zoomCy (zoomRate) {
    let zoomCenter = {
      x: this.width / 2,
      y: this.height / 2
    }

    this.cy.zoom({
      level: this.cy.zoom() * zoomRate,
      renderedPosition: zoomCenter
    })
  }

  destroy () {
    this._onRenderHandler.cancel()
    this._removeCyListeners()
    this._removeEventsHandling()

    // If container is not created by navigator and its removal is prohibited
    if (this._options.container && !this._options.removeCustomContainer) {
      let childs = this.$panel.childNodes
      for (let i = childs.length - 1; i >= 0; i--) {
        this.$panel.removeChild(childs[ i ])
      }
    } else {
      this.$panel.parentNode.removeChild(this.$panel)
    }
  }

  resize () {
    // Cache sizes
    let panelRect = this._contianer.getBoundingClientRect()
    this.width = panelRect.width
    this.height = panelRect.height
    this._setupPanel()
    this._checkThumbnailSizesAndUpdate()
    this._setupView()
  }

  bb () {
    let bb = { w: 0, h: 0 }
    try {
      bb = this.cy.elements().boundingBox()
    } catch (error) {
      //
    }

    if (bb.w === 0 || bb.h === 0) {
      return {
        x1: 0,
        x2: Infinity,
        y1: 0,
        y2: Infinity,
        w: Infinity,
        h: Infinity
      }
    }

    return bb
  }
}

export default (cytoscape) => {
  if (!cytoscape) { return }

  cytoscape('core', 'navigator', function (options) {
    return new Navigator(this, options)
  })
}
