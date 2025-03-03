# DSS Template

vue3、vite、fes-design，是否使用 TS

新版 DSS 初始化项目，原 DSS 项目已有功能逐步迁移

## 本地开发及接口调试：

```
lerna bootstrap
wnpm run prepare
wnpm run dev
```

### 方式一

本地开发请求后端接口通过配置 vite proxy 来进行，cookie 可以从 dss 登录接口后 set-cookie 里取

### 方式二

浏览器插件 SwitchyOmega 配置规则，非接口的资源转发至 http://localhost:5173，开发时访问目标域名即可
网址正则示例：
^http://sit.dss.bdap.weoa.com(?!/api)._
^http://10.107.97.166:8088(?!/api)._

## 代码提交规范

请执行 wnpm run prepare 配置好提交前 eslint 检测及 commit 信息规范校验的 git hook

## 项目结构

```
├─dist              # 构建后静态资源
├─node_modules
├─exts              # 扩展插件
└─packages          # 各应用模块
    ├─dss
    ├─shared
    └─workspace      # 工作空间管理

```

### 其它
