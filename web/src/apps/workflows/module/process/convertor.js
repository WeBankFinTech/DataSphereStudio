export function ds2butterfly(tasks, connects, locations, taskList, isNode) {
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
      state: '',
      endpoints: isNode ? [] : [
        {
          id: 'top-' + task.id,
          orientation: [0, -1],
          pos: [0.5, 0]
        },
        {
          id: 'bottom-' + task.id,
          orientation: [0, 1],
          pos: [0.5, 0]
        },
        {
          id: 'left-' + task.id,
          orientation: [-1, 0],
          pos: [0, 0.5]
        },
        {
          id: 'right-' + task.id,
          orientation: [1, 0],
          pos: [0, 0.5]
        }
      ]
    })
  })
  connects.forEach(connect => {
    let preId = connect.endPointSourceId,
      curId = connect.endPointTargetId,
      curNode, preNode, prePoint, curPoint
    if (!isNode) {
      nodes.forEach(node => {
        if (node.id === preId) {
          preNode = node
        } else if (node.id === curId) {
          curNode = node
        }
      })
      if (!preNode || !curNode) return
      if (preNode.left < curNode.left) {
        if (preNode.top < curNode.top) {
          if (Math.abs(curNode.top - preNode.top) < Math.abs(curNode.left - preNode.left)) {
            prePoint = 'right-' + preId
            curPoint = 'left-' + curId
          } else {
            prePoint = 'top-' + preId
            curPoint = 'bottom-' + curId
          }
        } else {
          if (Math.abs(curNode.top - preNode.top) < Math.abs(curNode.left - preNode.left)) {
            prePoint = 'right-' + preId
            curPoint = 'left-' + curId
          } else {
            prePoint = 'bottom-' + preId
            curPoint = 'top-' + curId
          }
        }
      } else {
        if (preNode.top < curNode.top) {
          if (Math.abs(curNode.top - preNode.top) < Math.abs(curNode.left - preNode.left)) {
            prePoint = 'left-' + preId
            curPoint = 'right-' + curId
          } else {
            prePoint = 'top-' + preId
            curPoint = 'bottom-' + curId
          }
        } else {
          if (Math.abs(curNode.top - preNode.top) < Math.abs(curNode.left - preNode.left)) {
            prePoint = 'left-' + preId
            curPoint = 'right-' + curId
          } else {
            prePoint = 'bottom-' + preId
            curPoint = 'top-' + curId
          }
        }
      }
    }
    edges.push({
      type: isNode ? 'node' : 'endpoint',//默认
      shapeType: 'AdvancedBezier', //默认
      sourceNode: isNode ? '' : preId, //连接源节点id
      source: isNode ? preId : prePoint,     //连接源锚点id
      targetNode: isNode ? '' : curId, //连接目标节点id
      target: isNode ? curId : curPoint,      //连接目标锚点id
      arrow: true,
      arrowPosition: 1,
      arrowOffset: 0,
      orientationLimit: ['Left', 'Right', 'Top', 'Bottom']
    })
  })
  if (taskList && taskList.length) {
    taskList.forEach(item => {
      nodes.forEach(node => {
        if (node.id === item.taskObj.id && item.stateObj) {
          node.state = item.stateObj
        }
      })
    })
  }
  return {
    nodes,
    edges
  }
}
