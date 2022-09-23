import ApiPublish from './scriptis/apiPublish/index.vue'
import CopyHistory from './workflows/bottomTab/copyHistory.vue'
import createProxyModal from './proxyUser/index'
import i18n from '@dataspherestudio/shared/common/i18n'
import storage from '@dataspherestudio/shared/common/helper/storage'
import api from '@dataspherestudio/shared/common/service/api'
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

  // 登录后提示运维用户切换
  this.bindHook('after_login', function () {
    api.fetch('/dss/framework/admin/globalLimits', {}, 'get').then((res) => {
      let baseInfo = storage.get('baseInfo', 'local')
      baseInfo = {
        ...baseInfo,
        dss: {
          ...res.globalLimits
        }
      }
      storage.set('baseInfo', baseInfo, 'local')
      if (baseInfo.dss.proxyEnable) {
        createProxyModal()
      }
    })
  })

  // workflows: 工作流开发底部TAB面板复制历史
  this.bindHook('workflow_bottom_panel', function () {
    return [
      {
        name: i18n.t('message.ext.opensource.copyHistory'),
        icon: 'md-paper-plane',
        component: CopyHistory
      }
    ]
  })

}
