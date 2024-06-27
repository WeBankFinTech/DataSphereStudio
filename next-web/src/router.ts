import type { RouteRecordRaw } from 'vue-router';
import { createRouter, createWebHashHistory } from 'vue-router';

import Apps from 'virtual-app-module';

console.log(Apps.appsRoutes);
function initRoutes() {
  return Promise.all(Object.values(Apps.appsRoutes)).then((route) => {
    let routes: RouteRecordRaw[] = [];
    //@ts-ignore
    route.forEach((it) => (routes = routes.concat(it.default)));
    return routes;
  });
}

export default async function init() {
  const routes: RouteRecordRaw[] = await initRoutes();
  const router = createRouter({
    history: createWebHashHistory(),
    routes: routes,
  });

  // 全局前置守卫
  router.beforeEach((to, from, next) => {
    next();
  });

  router.afterEach((to) => {
    const _title = to.meta.title as string;
    if (_title) {
      window.document.title = _title;
    }
  });
  return router;
}
