const isString = x => typeof x === typeof ''

function getEleJson (overrides, params, addedClasses) {
  let json = {}

  // basic values
  Object.assign(json, params, overrides)

  // make sure params can specify data but that overrides take precedence
  Object.assign(json.data, params.data, overrides.data)

  if (isString(params.classes)) {
    json.classes = params.classes + ' ' + addedClasses
  } else if (Array.isArray(params.classes)) {
    json.classes = params.classes.join(' ') + ' ' + addedClasses
  } else {
    json.classes = addedClasses
  }

  return json
}

function makeEdges (preview = false) {
  let {
    cy,
    options,
    presumptiveTargets,
    previewEles,
    active
  } = this

  let source = this.sourceNode
  let target = this.targetNode
  let classes = preview ? 'eh-preview' : ''
  let added = cy.collection()
  let edgeType = options.edgeType(source, target)

  // can't make edges outside of regular gesture lifecycle
  if (!active) {
    return
  }

  // must have a non-empty edge type
  if (!edgeType) {
    return
  }

  // can't make preview if disabled
  if (preview && !options.preview) {
    return
  }

  // detect cancel
  if (!target || target.size() === 0) {
    previewEles.remove()

    this.emit('cancel', this.mp(), source, presumptiveTargets)

    return
  }

  // just remove preview class if we already have the edges
  if (!preview && options.preview) {
    previewEles.removeClass('eh-preview').style('events', '')

    this.emit('complete', this.mp(), source, target, previewEles)

    return
  }

  let p1 = source.position()
  let p2 = target.position()

  let p
  if (source.same(target)) {
    p = {
      x: p1.x + options.nodeLoopOffset,
      y: p1.y + options.nodeLoopOffset
    }
  } else {
    p = {
      x: (p1.x + p2.x) / 2,
      y: (p1.y + p2.y) / 2
    }
  }

  if (edgeType === 'node') {
    let interNode = cy.add(
      getEleJson({
        group: 'nodes',
        position: p,
        data: { type: 'edgehandle' }
      },
      options.nodeParams(source, target),
      classes
      ))

    let source2inter = cy.add(
      getEleJson({
        group: 'edges',
        data: {
          source: source.id(),
          target: interNode.id()
        }
      },
      options.edgeParams(source, target, 0),
      classes
      )
    )

    let inter2target = cy.add(
      getEleJson({
        group: 'edges',
        data: {
          source: interNode.id(),
          target: target.id()
        }
      },
      options.edgeParams(source, target, 1),
      classes
      )
    )

    added = added.merge(interNode).merge(source2inter).merge(inter2target)
  } else { // flat
    let source2target = cy.add(
      getEleJson({
        group: 'edges',
        data: {
          source: source.id(),
          target: target.id()
        }
      },
      options.edgeParams(source, target, 0),
      classes
      )
    )

    added = added.merge(source2target)
  }

  if (preview) {
    this.previewEles = added

    added.style('events', 'no')
  } else {
    added.style('events', '')

    this.emit('complete', this.mp(), source, target, added)
  }

  return this
}

function makePreview () {
  this.makeEdges(true)

  return this
}

function previewShown () {
  return this.previewEles.nonempty() && this.previewEles.inside()
}

function removePreview () {
  if (this.previewShown()) {
    this.previewEles.remove()
  }

  return this
}

function handleShown () {
  return this.handleNode.nonempty() && this.handleNode.inside()
}

function removeHandle () {
  if (this.handleShown()) {
    this.handleNode.remove()
  }

  return this
}

function setHandleFor (node) {
  let {
    options,
    cy
  } = this

  let handlePosition = typeof options.handlePosition === typeof '' ? () => options.handlePosition : options.handlePosition

  let p = node.position()
  let h = node.outerHeight()
  let w = node.outerWidth()

  // store how much we should move the handle from origin(p.x, p.y)
  let moveX = 0
  let moveY = 0

  // grab axes
  let axes = handlePosition(node).toLowerCase().split(/\s+/)
  let axisX = axes[0]
  let axisY = axes[1]

  // based on handlePosition move left/right/top/bottom. Middle/middle will just be normal
  if (axisX === 'left') {
    moveX = -(w / 2)
  } else if (axisX === 'right') {
    moveX = w / 2
  }
  if (axisY === 'top') {
    moveY = -(h / 2)
  } else if (axisY === 'bottom') {
    moveY = h / 2
  }

  // set handle x and y based on adjusted positions
  let hx = this.hx = p.x + moveX
  let hy = this.hy = p.y + moveY
  let pos = {
    x: hx,
    y: hy
  }

  if (this.handleShown()) {
    this.handleNode.position(pos)
  } else {
    cy.batch(() => {
      this.handleNode = cy.add({
        classes: 'eh-handle',
        data: { type: 'edgehandle' },
        position: pos,
        grabbable: false,
        selectable: false
      })

      this.handleNode.style('z-index', 9007199254740991)
    })
  }

  return this
}

function updateEdge () {
  let {
    sourceNode,
    ghostNode,
    cy,
    mx,
    my,
    options
  } = this
  let x = mx
  let y = my
  let ghostEdge, ghostEles

  // can't draw a line without having the starting node
  if (!sourceNode) {
    return
  }

  if (!ghostNode || ghostNode.length === 0 || ghostNode.removed()) {
    ghostEles = this.ghostEles = cy.collection()

    cy.batch(() => {
      ghostNode = this.ghostNode = cy.add({
        group: 'nodes',
        classes: 'eh-ghost eh-ghost-node',
        data: { type: 'edgehandle' },
        position: {
          x: 0,
          y: 0
        }
      })

      ghostNode.style({
        'background-color': 'blue',
        'width': 0.0001,
        'height': 0.0001,
        'opacity': 0,
        'events': 'no'
      })

      let ghostEdgeParams = options.ghostEdgeParams()

      ghostEdge = cy.add(Object.assign({}, ghostEdgeParams, {
        group: 'edges',
        data: Object.assign({}, ghostEdgeParams.data, {
          source: sourceNode.id(),
          target: ghostNode.id()
        })
      }))

      ghostEdge.addClass('eh-ghost eh-ghost-edge')

      ghostEdge.style({
        'events': 'no'
      })
    })

    ghostEles.merge(ghostNode).merge(ghostEdge)
  }

  ghostNode.position({
    x,
    y
  })

  return this
}

export default {
  makeEdges,
  makePreview,
  removePreview,
  previewShown,
  updateEdge,
  handleShown,
  setHandleFor,
  removeHandle
}
