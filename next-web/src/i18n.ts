import { createI18n } from 'vue-i18n';
import Apps from 'virtual-app-module';
import { merge } from 'lodash';

const messages = {
  en: {},
  'zh-CN': {},
};

Apps.appsI18n.forEach((appI18n: any) => {
  Promise.all([appI18n['zh-CN'], appI18n['en']]).then((item) => {
    const msgconf = {
      'zh-CN': item[0].default,
      en: item[1].default,
    };
    merge(messages, msgconf);
  });
});

const i18n = createI18n({
  legacy: false,
  locale: 'zh-CN',
  messages,
});

export default i18n;
