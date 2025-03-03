declare module 'virtual-app-module' {
  import type { RouteRecordRaw } from 'vue-router';
  type AppModuleType = 'dss' | 'workspace' | 'scriptis';

  let exts: object;
  let appsRoutes: {
    [key in AppModuleType]: RouteRecordRaw[];
  };
  let appsI18n: [];
  let conf: {
    [key: string]: any;
  };
  let gitInfo: {
    [key: string]: any;
  };
}
