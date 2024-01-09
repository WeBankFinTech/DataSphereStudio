function disableGestures () {
  this.saveGestureState()
  this.cy
    .zoomingEnabled(false)
    .panningEnabled(false)
    .boxSelectionEnabled(false)

  if (this.options.disableBrowserGestures) {
    let wlOpts = this.windowListenerOptions

    window.addEventListener('touchstart', this.preventDefault, wlOpts)
    window.addEventListener('touchmove', this.preventDefault, wlOpts)
    window.addEventListener('wheel', this.preventDefault, wlOpts)
  }

  return this
}

function resetGestures () {
  (this.cy
    .zoomingEnabled(this.lastZoomingEnabled)
    .panningEnabled(this.lastPanningEnabled)
    .boxSelectionEnabled(this.lastBoxSelectionEnabled)
  )

  if (this.options.disableBrowserGestures) {
    let wlOpts = this.windowListenerOptions

    window.removeEventListener('touchstart', this.preventDefault, wlOpts)
    window.removeEventListener('touchmove', this.preventDefault, wlOpts)
    window.removeEventListener('wheel', this.preventDefault, wlOpts)
  }

  return this
}

function saveGestureState () {
  let {
    cy
  } = this

  this.lastPanningEnabled = cy.panningEnabled()
  this.lastZoomingEnabled = cy.zoomingEnabled()
  this.lastBoxSelectionEnabled = cy.boxSelectionEnabled()

  return this
}

export default {
  disableGestures,
  resetGestures,
  saveGestureState
}
