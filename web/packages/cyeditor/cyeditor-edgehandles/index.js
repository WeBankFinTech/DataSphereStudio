import Edgehandles from './edgehandles'

export default (cytoscape) => {
  if (!cytoscape) { return }

  cytoscape('core', 'edgehandles', function (options) {
    return new Edgehandles(this, options)
  })
}
