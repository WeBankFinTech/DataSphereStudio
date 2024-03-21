function dfs(edges, node, visited, path) {
  visited[node] = true;
  path[node] = true;
  var adjacentEdges = edges.filter(function (edge) {
    return edge.source === node;
  });
  for (var i = 0; i < adjacentEdges.length; i++) {
    var targetNode = adjacentEdges[i].target;
    if (visited[targetNode] && path[targetNode]) {
      return true;
    }
    if (!visited[targetNode] && dfs(edges, targetNode, visited, path)) {
      return true;
    }
  }
  path[node] = false;
  return false;
}

export const hasCycle = (edges) => {
  var visited = {};
  for (var i = 0; i < edges.length; i++) {
    if (!visited[edges[i].source]) {
      var hasCycle = dfs(edges, edges[i].source, visited, {});
      if (hasCycle) {
        return true; // 如果找到闭环，则返回 true
      }
    }
  }
  return false;
}