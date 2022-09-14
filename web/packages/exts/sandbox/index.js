import Vue from 'vue'
import i18n from '@dataspherestudio/shared/common/i18n'
import storage from "@dataspherestudio/shared/common/helper/storage"


const register = {
  path: '/register',
  name: 'register',
  meta: {
    title: 'Register',
    publicPage: true,
    keepAlive: true,
  },
  component: () =>
    import ('./register/index.vue'),
}

/**
 * 插件绑定
 */
export default function () {
  // 增加注册页面
  this.bindHook('app_router_config', function (routes) {
    routes.push(register)
  })

  // 登录页面增加注册链接
  // 工作空间首页增加通知
  this.bindHook('app_router_afterchange', function ({ to }) {

    setTimeout(function () {
      if (to && to.path === '/login') {
        let el = document.querySelector('.remember-user-name');
        let bindPhone = document.createElement('div')
        bindPhone.setAttribute('id', 'reg_link_a')
        el.appendChild(bindPhone)
        return new Vue({
          render: (h) => {
            return h(
              'a', {
                style: {
                  color: '#2d8cf0',
                  position: 'absolute',
                  right: '30px'
                },
                on: {
                  click: function () {
                    location.href = "#/register"
                  }
                }
              }, i18n.t('message.common.login.toRegister')
            )
          }
        }).$mount('#reg_link_a')
      }

      if (to && to.path === '/workspaceHome') {
        let headDiv = document.querySelector('.page-bgc-header');
        let bindPhone = document.createElement('div')
        bindPhone.setAttribute('id', 'workspace_home_tip')
        headDiv.appendChild(bindPhone)
        const show = storage.get('workspace_top_notice')
        return new Vue({
          render: (h) => {
            return h(
              'Alert', {
                style: {
                  display: show ? 'none' : 'block'
                },
                props: {
                  type: 'warning',
                  'show-icon': true,
                  closable: true,
                },
                on: {
                  'on-close': function () {
                    storage.set('workspace_top_notice', true)
                  }
                }
              }, [
                i18n.t('message.workspace.topNotice.content')
              ]
            )
          }
        }).$mount('#workspace_home_tip')
      }
    }, 1000)
  })

}
