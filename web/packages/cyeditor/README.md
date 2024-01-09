# 说明
Cytoscape.js 是一个用于分析和可视化复杂网络数据的 JavaScript 库。它主要用于渲染图形，支持节点和边的自定义样式，并提供了丰富的交互功能。Cytoscape.js 可以用于创建网络图（如社交网络、生物学网络等）、流程图、组织图、文件系统图等。
主要特点：

高度可定制：节点和边的样式可以自定义，支持多种形状、颜色、大小等属性。
强大的布局算法：内置多种布局算法，如环形布局、力导布局、矩阵布局等，也支持自定义布局。
丰富的交互：支持多种交互方式，如拖拽、缩放、选择、悬停等，可以方便地与其他库集成。
插件扩展：通过插件，可以扩展 Cytoscape.js 的功能，如添加新的布局算法、新的交互方式等。
数据驱动：Cytoscape.js 使用 JSON 格式来表示网络数据，方便数据处理和管理。

cyeditor 使用Cytoscape.js配合一些扩展开发实现DSS工作流编辑视图功能。

# 目录及文件说明

```
├─cyeditor-context-menu   # 右键菜单扩展
├─cyeditor-dom-node       # DOM节点
├─cyeditor-drag-add-nodes # 节点拖拽添加
├─cyeditor-edgehandles    # 连线
│  └─edgehandles
├─cyeditor-navigator      # 缩略图导航
├─cyeditor-snap-grid      # 网格画布
├─defaults                # 默认设置
├─utils
├─index.css
└─index.js
```

# 开发调试

  next-web目录下启动

```
# 调试
npm run serve_cyeditor 
# 构建
npm run build_cyeditor 
```

# 配置项

```json
{
  cy: {         // cytoscape.js的配置
    layout: {}, // 布局方式
    styleEnabled: true,
    style: [],  // cytoscape.js的节点、连线样式配置
    minZoom: 0.1,
    maxZoom: 10
  },
  editor: {           // 编辑器的配置
    container: '',    // 组件初始化dom
    zoomRate: 0.2,
    lineType: 'bezier',
    dragAddNodes: true,
    snapGrid: true,
    contextMenu: true,
    navigator: true,
    nodeTypes: [],    // 节点类型
    beforeAdd (el) {
      return true
    },
    afterAdd (el) {

    }
  }
}
```
# API
 - deleteSelected

    设置viewport进行缩放

 - save
 
    设置viewport进行缩放

 - fit

    设置画布viewport缩放显示所有元素
 - zoom

    设置viewport进行缩放

 - toggleGrid
 
    切换网格辅助

 - jpg
 - png
 - json
 
    JSON格式数据设置、获取

 - data

    设置、获取关联的自定义数据

 - removeData

    删除关联的自定义数据
 - destroy

# 使用

```javascript
const cyeditorel = ref<any>(null);
onMounted(() => {
  const container = cyeditorel.value;
  let config = {
    cy: {
      layout: {
        name: 'klay',
        rows: 5,
      },
      styleEnabled: true,
      minZoom: 0.1,
      maxZoom: 10,
    },
    editor: {
      container,
      zoomRate: 0.2,
      lineType: 'bezier',
      dragAddNodes: true,
      snapGrid: true,
      contextMenu: true,
      navigator: true,
      // nodeTypes: [],
      beforeAdd() {
        return true;
      },
      afterAdd() {
        //
      },
    },
  };
  const { editor, cy } = new CyEditor(config);
  editor.json({
    boxSelectionEnabled: true,
    elements: props.data,
    pan: { x: 0, y: 0 },
    panningEnabled: true,
    userPanningEnabled: true,
    userZoomingEnabled: true,
    zoom: 1,
    zoomingEnabled: true,
  });
  cy.layout(config.cy.layout).run();
  editor.on('change', (scope, editor) => {
    let json = editor.json();
    console.log(json);
  });
});
```

