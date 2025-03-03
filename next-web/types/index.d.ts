declare type AppModule = 'workspace';

declare type ConfigOfApps = {
  routes: string;
  module: string;
  i18n: {
    en: string;
    'zh-CN': string;
  };
};

declare type AppConf = {
  apps: {
    [key in AppModule]: ConfigOfApps;
  };
  exts: object;
  conf: object;
  version: string;
};

declare interface Window {
  __MICRO_APP_ENVIRONMENT__: any;
}
