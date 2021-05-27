export function ds2butterfly(tasks, connects, locations) {
  let nodes = [],
    edges = []
  tasks.forEach(task => {
    nodes.push({
      id: task.id,
      left: locations[task.id].x,
      top: locations[task.id].y,
      label: task.name,
      node_type: task.type,
      node_params: task.params,
      node_preTasks: task.preTasks,
      endpoints: [
        {
          id: 'point1-' + task.id,
          orientation: [0, -1],
          pos: [0.5, 0]
        },
        {
          id: 'point2-' + task.id,
          orientation: [0, 1],
          pos: [0.5, 0]
        }
      ]
    })
  })
  connects.forEach(connect => {
    edges.push({
      type: 'endpoint',//默认
      shapeType: 'AdvancedBezier', //默认
      sourceNode: connect.endPointSourceId, //连接源节点id
      source: 'point2-' + connect.endPointSourceId,     //连接源锚点id
      targetNode: connect.endPointTargetId, //连接目标节点id
      target: 'point1-' + connect.endPointTargetId,      //连接目标锚点id
      arrow: true,
      arrowPosition: 1,
      arrowOffset: 0
    })
  })
  return {
    nodes,
    edges
  }
}
