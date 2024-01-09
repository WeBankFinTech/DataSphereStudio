const pluginStyles = [
  {
    selector: '.eh-handle',
    style: {
      'background-color': 'red',
      'width': 12,
      'height': 12,
      'shape': 'ellipse',
      'overlay-opacity': 0,
      'border-width': 12, // makes the handle easier to hit
      'border-opacity': 0,
      'background-opacity': 0.5,
    }
  },
  {
    selector: '.eh-hover',
    style: {
      'background-color': 'red',
      'background-opacity': 0.5
    }
  },
  {
    selector: '.eh-source',
    style: {
      'border-width': 2,
      'border-color': 'red'
    }
  },
  {
    selector: '.eh-target',
    style: {
      'border-width': 2,
      'border-color': 'red'
    }
  },
  {
    selector: '.eh-preview, .eh-ghost-edge',
    style: {
      'background-color': 'red',
      'line-color': 'red',
      'target-arrow-color': 'red',
      'source-arrow-color': 'red'
    }
  },
  {
    selector: '.eh-ghost-edge.eh-preview-active',
    style: {
      'opacity': 0
    }
  }
]

export default pluginStyles
