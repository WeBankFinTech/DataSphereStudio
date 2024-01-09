
import utils from '../../utils'

function canStartOn (node) {
  const { options, previewEles, ghostEles, handleNode } = this
  const isPreview = el => previewEles.anySame(el)
  const isGhost = el => ghostEles.anySame(el)
  const userFilter = el => el.filter(options.handleNodes).length > 0
  const isHandle = el => handleNode.same(el)
  const isTemp = el => isPreview(el) || isHandle(el) || isGhost(el)

  const { enabled, active, grabbingNode } = this

  return (
    enabled && !active && !grabbingNode &&
    (node == null || (!isTemp(node) && userFilter(node)))
  )
}

function canStartDrawModeOn (node) {
  return this.canStartOn(node) && this.drawMode
}

function canStartNonDrawModeOn (node) {
  return this.canStartOn(node) && !this.drawMode
}

function show (node) {
  let { options, drawMode } = this

  if (!this.canStartOn(node) || (drawMode && !options.handleInDrawMode)) { return }

  this.sourceNode = node

  this.setHandleFor(node)

  this.emit('show', this.hp(), this.sourceNode)

  return this
}

function hide () {
  this.removeHandle()

  this.emit('hide', this.hp(), this.sourceNode)

  return this
}

function start (node) {
  if (!this.canStartOn(node)) { return }

  this.active = true

  this.sourceNode = node
  this.sourceNode.addClass('eh-source')

  this.disableGestures()
  this.disableEdgeEvents()

  this.emit('start', this.hp(), node)
}

function update (pos) {
  if (!this.active) { return }

  let p = pos

  this.mx = p.x
  this.my = p.y

  this.updateEdge()
  this.throttledSnap()

  return this
}

function snap () {
  if (!this.active || !this.options.snap) { return false }

  let cy = this.cy
  let tgt = this.targetNode
  let threshold = this.options.snapThreshold
  let sqThreshold = n => { let r = getRadius(n); let t = r + threshold; return t * t }
  let mousePos = this.mp()
  let sqDist = (p1, p2) => (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)
  let getRadius = n => (n.outerWidth() + n.outerHeight()) / 4
  let nodeSqDist = utils.memoize(n => sqDist(n.position(), mousePos), n => n.id())
  let isWithinTheshold = n => nodeSqDist(n) <= sqThreshold(n)
  let cmpSqDist = (n1, n2) => nodeSqDist(n1) - nodeSqDist(n2)
  let allowHoverDelay = false

  let nodesByDist = cy.nodes(isWithinTheshold).sort(cmpSqDist)
  let snapped = false

  if (tgt.nonempty() && !isWithinTheshold(tgt)) {
    this.unpreview(tgt)
  }

  for (let i = 0; i < nodesByDist.length; i++) {
    let n = nodesByDist[i]

    if (n.same(tgt) || this.preview(n, allowHoverDelay)) {
      snapped = true
      break
    }
  }

  return snapped
}

function preview (target, allowHoverDelay = true) {
  let { options, sourceNode, ghostNode, ghostEles, presumptiveTargets, previewEles, active } = this
  let source = sourceNode
  let isLoop = target.same(source)
  let loopAllowed = options.loopAllowed(target)
  let isGhost = target.same(ghostNode)
  let noEdge = !options.edgeType(source, target)
  let isHandle = target.same(this.handleNode)
  let isExistingTgt = target.same(this.targetNode)

  if (!active || isHandle || isGhost || noEdge || isExistingTgt || (isLoop && !loopAllowed)) { return false }

  if (this.targetNode.nonempty()) {
    this.unpreview(this.targetNode)
  }

  clearTimeout(this.previewTimeout)

  let applyPreview = () => {
    this.targetNode = target

    presumptiveTargets.merge(target)

    target.addClass('eh-presumptive-target')
    target.addClass('eh-target')

    this.emit('hoverover', this.mp(), source, target)

    if (options.preview) {
      target.addClass('eh-preview')

      ghostEles.addClass('eh-preview-active')
      sourceNode.addClass('eh-preview-active')
      target.addClass('eh-preview-active')

      this.makePreview()

      this.emit('previewon', this.mp(), source, target, previewEles)
    }
  }

  if (allowHoverDelay && options.hoverDelay > 0) {
    this.previewTimeout = setTimeout(applyPreview, options.hoverDelay)
  } else {
    applyPreview()
  }

  return true
}

function unpreview (target) {
  if (!this.active || target.same(this.handleNode)) { return }

  let { previewTimeout, sourceNode, previewEles, ghostEles, cy } = this
  clearTimeout(previewTimeout)
  this.previewTimeout = null

  let source = sourceNode

  target.removeClass('eh-preview eh-target eh-presumptive-target eh-preview-active')
  ghostEles.removeClass('eh-preview-active')
  sourceNode.removeClass('eh-preview-active')

  this.targetNode = cy.collection()

  this.removePreview(source, target)

  this.emit('hoverout', this.mp(), source, target)
  this.emit('previewoff', this.mp(), source, target, previewEles)

  return this
}

function stop () {
  if (!this.active) { return }

  let { sourceNode, targetNode, ghostEles, presumptiveTargets } = this

  clearTimeout(this.previewTimeout)

  sourceNode.removeClass('eh-source')
  targetNode.removeClass('eh-target eh-preview eh-hover')
  presumptiveTargets.removeClass('eh-presumptive-target')

  this.makeEdges()

  this.removeHandle()

  ghostEles.remove()

  this.clearCollections()

  this.resetGestures()
  this.enableEdgeEvents()

  this.active = false

  this.emit('stop', this.mp(), sourceNode)

  return this
}

export default {
  show,
  hide,
  start,
  update,
  preview,
  unpreview,
  stop,
  snap,
  canStartOn,
  canStartDrawModeOn,
  canStartNonDrawModeOn
}
