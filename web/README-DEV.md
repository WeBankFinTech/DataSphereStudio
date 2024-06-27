### 1.1.0版本说明

- 前端工程管理方面达到目录结构清晰，方便维护及迭代开发
- 可选模块构建，产出不同用户的应用
- 插件开发支持

### 项目结构

```
├─dist              # 构建后静态资源
├─docs              # 文档
├─node_modules
└─packages          # 各应用模块
    ├─apiServices
    ├─dataGovernance
    ├─dataService
    ├─dolphinScheduler
    ├─dss
    ├─scheduleCenter
    ├─scriptis
    ├─shared
    │  ├─common
    │  └─components
    ├─workflows
    └─workspace

```

### 建议/约束

新增功能模块先确定涉及应用，按照上面目录结构维护代码同时建议遵守以下约束：

- 子应用可以配置自己的layout需要在应用router模块导出配置subAppRoutes
- 各应用需要使用iView作为UI库，并提供路由，国际化等配置写入config.json
- 可复用组件，资源需要合理放置，packages/shared 共享组件方法，修改需要注意影响
- 各应用路由应以应用名做统一前缀
- 各应用之间需要事件通信，应当在config.json 里声明对应module文件路径
- 新增功能模块需要按照现有目录约束建立文件，已有功能修改应在有限模块内进行，控制影响范围
- 全局共用组件、公共基础样式、工具方法修改需评估后才能修改，并且重点review
- 插件扩展开发，扩展点增加，修改需要讨论，注意兼容

### 如何新增一个子应用，如何扩展

1. config.json apps里新增应用配置（apps里配置的字应用模块会一起合并打包）
2. packages 下新建应用目录或者插件目录进行应用开发

参考packages/demo：
npm run serve --configfile=config.demo.json
http://localhost:8080/#/demoHome

### 前端开发、构建打包

```
# 安装依赖
npm i
# 开发启动DSS
npm run serve
# 运行部分模块子应用，支持通过module组合。如科管版本：
npm run serve --module=scriptis
# 打包DSS应用
npm run build
# 打包子应用，支持通过module组合
npm run build --module=scriptis
npm run build --module=apiServices,workspace --micro_module=apiServices
npm run build --module=scheduleCenter,workflows,workspace,scriptis --micro_module=scheduleCenter
```
