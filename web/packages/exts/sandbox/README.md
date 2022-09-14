## 说明

此扩展包括以下功能，沙箱版本
- 用户注册
- 服务协议
- 隐私政策
- 工作空间首页顶部展示通知

沙箱版本打包时config.json 里exts需配置上此扩展及open-source

```json

"dss-plugin-sandbox": {
    "module": "exts/sandbox/index.js",
    "i18n": {
      "en": "exts/sandbox/i18n/en.json",
      "zh-CN": "exts/sandbox/i18n/zh.json"
    },
    "options": null
  }
```

## todo
packages里原来所有sandbox的部分移动到插件内