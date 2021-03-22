### 1.0.0版本重构说明

由于DSS里存在相对独立的一些功能模块（如scriptis、workflow、管理控制台等），之前是一个整体大的单页应用开发模式，虽然在具体业务逻辑上也是模块化开发，但这些模块都混在一起，在迭代开发过程中暴露一些问题。比如社区需要贡献代码，对这些修改的代码影响范围比较难以评估，同时存在跨模块的组件引用，对于新增需求开发时需要对整体应用比较熟悉，对于刚加入的开发同事要熟悉整个应用也有很大挑战。同时DSS有接入第三方产品系统的需求，这个比较符合微前端的应用场景。所以有必要拆分应用。

### 重构目的

- 前端工程管理方面达到目录结构清晰，方便维护及迭代开发
- 后续微前端的实施
- 插件开发支持

### 重构目标

鉴于目前DSS现状及后续规划要求，决定采用 一个主应用+多个子应用 的方式来重构前端工程
主应用部分负责提供应用入口，组织整个应用。子应用必须是可以看作是一个单独的应用，并且各子应用之间应当明确边界，
在设计开发层面应没有相互耦合依赖关系。
主应用需要提供应用入口，通过一个配置文件管理子应用的加载。各子应用提供路由、国际化等配置在主应用启动时会被合并
提供可扩展的插件化开发支持

### 项目结构

```
├─dist              # 构建后静态资源
├─docs              # 文档
├─node_modules
├─patches           # 修复第三方依赖
├─public            # 公共index.html
├─scripts           # 工具脚本
└─src
    ├─apps               # dss各应用存放目录，每个应用独立
    │  ├─apiServices     # 数据服务相关功能
    │  ├─linkis          # linkis相关功能
    │  │  ├─assets       # 应用所需的图片、css等资源
    │  │  ├─components   # 应用所需的组件
    │  │  ├─i18n         # 国际化的中英文json
    │  │  ├─module       # 当前应用所需的模块，每个模块相对独立，模块私有资源内置
    │  │  └─view         # 当前应用的页面，路由在同级目录下router.js下配置
    │  ├─scriptis        # scriptis相关功能
    │  │  ├─assets       # 应用所需的图片、css等资源
    │  │  ├─config       #
    │  │  ├─i18n         # 国际化的中英文json
    │  │  ├─module       # 当前应用所需的模块，每个模块相对独立，模块私有资源内置
    │  │  ├─service
    │  │  └─view         # 当前应用的页面，路由在同级目录下router.js下配置
    │  ├─workspace              # 工作空间应用
    │  │  ├─assets              # 静态资源
    │  │  ├─i18n                # 当前应用所需的国际化翻译json
    │  │  ├─module              # 当前应用所需的模块
    │  │  └─view
    │  └─workflows
    ├─common                    # 全局使用的公共服务和方法
    ├─components                # 全局使用的公共组件部分
    ├─dss                       # 项目的顶级根应用，apps里的应用都是其子路由
    ├─config.json               # 子应用配置
    ├─dynamic-apps.js           # 动态应用模块合并
    ├─main.js                   # 主应用启动入口
    └─router.js                 # 合并后的应用路由
```

### 建议/约束

新增功能模块先确定涉及应用，按照上面目录结构维护代码同时建议遵守以下约束：

- 子应用可以配置自己的layout需要在应用router模块导出配置subAppRoutes，参考scriptis
- 子应用支持使用自己的header，需要在config.json里配置模块路径
- 各应用需要使用iView作为UI库，并提供路由，国际化等配置写入config.json
- 各应用间不可以相互依赖
- 可复用组件，资源需要合理放置
- 各应用路由应以应用名做统一前缀
- 各应用之间需要事件通信，应当在config.json 里声明对应module文件路径
- 新增功能模块需要按照现有目录约束建立文件，已有功能修改应在有限模块内进行，控制影响范围
- 全局共用组件、公共基础样式、工具方法修改需评估后才能修改，并且重点review

### 待优化项

1. src/components 公共组件需要review，这部分公共组件应对大量数据的情况做性能方面的优化

### 如何新增一个子应用

1. config.json 新增应用配置
2. src/apps 下新建应用目录进行应用开发


### 前端开发、构建打包

```
# 开发启动DSS
npm run serve
# 子应用开发
npm run serve --module=scriptis,workflows --micro_module=workflows
# 打包DSS应用
npm run build
# 打包子应用，支持通过module组合
npm run build --module=scriptis,workflows --micro_module=workflows
npm run build --module=apiServices,workspace --micro_module=apiServices
```