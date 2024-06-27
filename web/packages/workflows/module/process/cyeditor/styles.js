export function getThemeColorConsants(theme) {
  const themeInfo = theme ? theme : window.document.documentElement.getAttribute('data-theme')
  const isDark = themeInfo == 'dark'
  const themeColor = isDark ? {
    nodeBg: '#2a303c',
    nodeColor: 'rgba(255,255,255,0.65)',
    nodeDisabledBg: '#515a6f',
    nodeDisabledColor: '#999'
  } : {
    nodeBg: '#eee',
    nodeColor: '#1E232D',
    nodeDisabledBg: '#fafafa',
    nodeDisabledColor: '#cfd0d3'
  }
  return themeColor
}
/**
 *
 * @param {*} nodeTypes
 * @returns
 */
export default function (nodeTypes) {
  const themeColor = getThemeColorConsants()
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
        'background-color': themeColor.nodeBg,
        'color': themeColor.nodeColor,
        'text-valign': 'center',
        'text-wrap': 'ellipsis',
        'text-max-width': '120px',
        'text-halign': 'center',
        'text-margin-x': '10px',
        'font-size': '12px',
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
