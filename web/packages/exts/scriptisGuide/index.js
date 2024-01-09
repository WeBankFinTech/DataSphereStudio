import { Modal } from 'iview'
import i18n from '@dataspherestudio/shared/common/i18n'
import api from '@dataspherestudio/shared/common/service/api'
import storage from '@dataspherestudio/shared/common/helper/storage'
import imgsvg from './images'
import './index.css'

/**
 * 未初始化用户提示和scriptis使用指引
 */
export default function ({ getUserStage }) {
  let userResult = {}
  // 切换到scriptis 进行未初始化用户提示和scriptis使用指引
  // 涉及行内用户提单只在行内版本有（包括单独scriptis版、dss）
  this.bindHook('app_router_afterchange', async function ({ to }) {
    if (to && to.path === '/home') {
      const userName = storage.get("baseInfo", 'local') ? storage.get("baseInfo", 'local').username : null;
      let res
      if (userResult[userName] && userResult[userName].stage !== 'uninit') {
        res = userResult[userName]
      } else {
        res = await api.fetch(
          getUserStage,
          { userName },
          "get"
        )
        userResult[userName] = res
      }
      const hasShowGuideForNewUser = storage.get("hasShowScriptisGuide");
      let baseInfo = storage.get('baseInfo', 'local') || {}
      const handbook = baseInfo.dss ? baseInfo.dss.handbookUrl || window.$APP_CONF.handbook : window.$APP_CONF.handbook
      if (res && res.stage === 'uninit') {
        Modal.confirm({
          title: i18n.t('message.ext.scriptisGuide.uninitTitle'),
          content: i18n.t('message.ext.scriptisGuide.uninitContent'),
          onOk: () => {
            window.open(handbook + '/km/使用前权限申请.html', '_blank');
          },
          cancelText: i18n.t('message.ext.scriptisGuide.gotit'),
          okText: i18n.t('message.ext.scriptisGuide.viewGuide')
        })
      } else if (res && res.stage === 'new' && !hasShowGuideForNewUser) {
        const content = `
              <div class="scriptis_usage_guide">
                <div class="steps">
                  <div class="step-item">
                    <p class="index">1</p>
                    <img style="padding:20px;width:100%" src=${imgsvg.settings} />
                    <p style="font-weight:600">${i18n.t('message.ext.scriptisGuide.setParams')}</p>
                    <p>${i18n.t('message.ext.scriptisGuide.setinConsole')}</p>
                  </div>
                  <div class="step-item">
                    <p class="index">2</p>
                    <img style="padding:20px;width:100%" src=${imgsvg.script} />
                    <p style="font-weight:600">${i18n.t('message.ext.scriptisGuide.newScript')}</p>
                    <p>${i18n.t('message.ext.scriptisGuide.createContext')}</p>
                  </div>
                  <div class="step-item">
                    <p class="index">3</p>
                    <img style="padding:20px;width:100%" src=${imgsvg.edit} />
                    <p style="font-weight:600">${i18n.t('message.ext.scriptisGuide.editScript')}</p>
                    <p>${i18n.t('message.ext.scriptisGuide.edittip')}</p>
                  </div>
                  <div class="step-item">
                    <p class="index">4</p>
                    <img style="padding:20px;width:100%" src=${imgsvg.run} />
                    <p style="font-weight:600">${i18n.t('message.ext.scriptisGuide.summitRun')}</p>
                    <p>${i18n.t('message.ext.scriptisGuide.selectSqlRun')}</p>
                  </div>
                  <div class="step-item">
                    <p class="index">5</p>
                    <img style="padding:20px;width:100%" src=${imgsvg.query} />
                    <p style="font-weight:600">${i18n.t('message.ext.scriptisGuide.queryReusult')}</p>
                    <p>${i18n.t('message.ext.scriptisGuide.queryTip')}</p>
                  </div>
                </div>
                <p><a href="${handbook}/快速入门/Scriptis快速入门.html">${i18n.t('message.ext.scriptisGuide.quickstart')}</a>, ${i18n.t('message.ext.scriptisGuide.starttext')}</p>
              </div>
            `
        storage.set("hasShowScriptisGuide", true);
        Modal.info({
          title: i18n.t('message.ext.scriptisGuide.scriptisUse'),
          content,
          width: 800,
          okText: i18n.t('message.ext.scriptisGuide.gotit')
        })
      }
    }
    if (to && to.path === '/login') {
      storage.remove("hasShowScriptisGuide")
      userResult = {}
    }
  })
}
