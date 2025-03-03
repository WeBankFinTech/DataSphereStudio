/**
 * 转换原工作流数据到新工作流数据，处理节点和连线
 */
export default function convertFlowJson(flowjson) {
  const elements = {
    nodes: [],
    edges: [],
  }
  if (flowjson && flowjson.nodes) {
    flowjson.nodes.forEach((element) => {
      const item = {
        data: {
          id: element.key,
          type: element.type || element.jobType,
          name: element.title,
          width: 150,
          height: 40,
        },
      }
      if (element.layout) {
        item.position = {
          x: element.layout.x,
          y: element.layout.y,
        }
      }
      item.selected = element.selected;
      item.disabled = element.disabled;
      elements.nodes.push(item)
    })
    const hasNode = (target) => {
      return flowjson.nodes.some(it => (it.id || it.key ) === target)
    }
    (flowjson.edges || []).forEach((element, index) => {
      if (element.source && element.target && hasNode(element.source) && hasNode(element.target)) {
        elements.edges.push({
          data: {
            id: `${index}`,
            source: element.source,
            target: element.target,
          },
        })
      }
    })
  }

  return elements
}
