const apps = require('dynamic-modules')
console.log(apps, 'apps')
window.$APP_CONF =  apps.conf

import { merge } from 'lodash'
import { subAppRoutes } from "./common-router"
// 根据module参数配置要打包的应用，生成的虚拟模块
// 子应用可以有独立的顶层路由配置layout，header，footer，可配置micro_module参数使用子应用layout

let subRoutes = subAppRoutes
const appsRoutes = Object.values(apps.appsRoutes)

/**
 * * 科管版本：scriptis + 管理台（linkis已从dss拆出去）
 * * npm run build --module=scriptis --micro_module=scriptis
 * * 数据服务子应用：apiServices和workspace一起打包
 * * npm run build --module=apiServices,workspace --micro_module=apiServices
 * ! micro_module参数要和module里值一样，否则找不到
 */
if (apps.microModule) {
  if (apps.appsRoutes[apps.microModule].subAppRoutes) {
    subRoutes = apps.appsRoutes[apps.microModule].subAppRoutes
  }
}

if (appsRoutes) {
  appsRoutes.forEach(route => {
    if (apps.microModule === 'apiServices' && route.apiServicesRoutes) {
      subRoutes.children = subRoutes.children.concat(route.apiServicesRoutes)
    } else {
      subRoutes.children = subRoutes.children.concat(route.default)
    }
  });
}

// 合并公共国际化
const i18nConfig = {
  'en': require('@dataspherestudio/shared/common/i18n/en.json'),
  'zh-CN': require('@dataspherestudio/shared/common/i18n/zh.json')
}

// 处理国际化
if (apps.appsI18n) {
  apps.appsI18n.forEach(item => {
    merge(i18nConfig, item)
  });
}
export {
  subRoutes,
  i18nConfig,
  apps
}
