import ApiPublish from './scriptis/apiPublish/index.vue'

/**
 * 插件绑定
 */
export default function () {
  // // 插件通过on方法绑定事件，响应应用通过plugin.emit触发的事件
  // this.on('event-app', function(arg) {
  //   console.log('event from app', arg)
  // })
  // 插件钩子type: component, 提供组件

  // 发布数据服务API
  this.bindHook('script_editor_top_tools', function () {
    return {
      name: 'ApiPublish',
      component: ApiPublish
    }
  })
}
