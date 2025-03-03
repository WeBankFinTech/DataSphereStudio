import { createApp } from 'vue';
import router from './router';
import i18n from './i18n';
import App from './App.vue';
import {
  FSpace,
  FMessage,
  FInput,
  FButton,
  FTable,
  FPagination,
  FSelect,
  FTableColumn,
  FDropdown,
  FTooltip,
  FCard,
  FEllipsis,
  FModal,
  FDrawer,
  FForm,
  FDatePicker,
  FSelectCascader,
} from '@fesjs/fes-design';
import {
  BTablePage,
  BSearch,
  BTableHeaderConfig,
} from '@fesjs/traction-widget';
import { request } from '@dataspherestudio/shared';
import './base.less';
// 设置请求错误提示
request.setError({
  showErrorMsg: function (error: any) {
    FMessage.error(error.message || '这是一条消息');
  },
});

router().then((router) => {
  const app = createApp(App);
  app.use(BTablePage);
  app.use(BSearch);
  app.use(BTableHeaderConfig);
  app.use(FDrawer);
  app.use(FSpace);
  // @ts-ignore
  app.use(FMessage);
  app.use(FInput);
  app.use(FButton);
  app.use(FTable);
  app.use(FPagination);
  app.use(FSelect);
  app.use(FTableColumn);
  app.use(FDropdown);
  app.use(FTooltip);
  app.use(FCard);
  app.use(FEllipsis);
  app.use(FModal);
  app.use(FForm);
  app.use(FSpace);
  app.use(FDatePicker);
  app.use(FSelectCascader);
  app.use(i18n);
  app.use(router);
  app.mount('#app');
});
