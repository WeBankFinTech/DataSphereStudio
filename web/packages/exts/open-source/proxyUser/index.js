import Vue from 'vue'
import ProxyUserModal from './modal.vue'
import i18n from '@dataspherestudio/shared/common/i18n'

const createProxyModal = () => {
  let body = document.body;
  let bindPhone = document.createElement('div')
  bindPhone.setAttribute('id', 'proxy_modal_')
  body.appendChild(bindPhone)

  return new Vue({
    i18n,
    render: (h) => {
      return h(
        ProxyUserModal,
        {
          props: {
            show: true
          }
        }
      )
    }
  }).$mount('#proxy_modal_')
}

export default createProxyModal
