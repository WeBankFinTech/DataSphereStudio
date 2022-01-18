const routes = [
  {
    name: "dataWarehouseDesign",
    path: "/dataWarehouseDesign",
    redirect: "/dataWarehouseDesign/themeDomains",
    component: () => import("./view/layout/index.vue"),
    meta: {
      title: "数仓规划",
      publicPage: true
    },
    children: [
      {
        name: "themeDomains",
        path: "/dataWarehouseDesign/themeDomains",
        component: () => import("./view/themeDomains/index.vue"),
        meta: {
          title: "主题域配置",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "layered",
        path: "/dataWarehouseDesign/layered",
        component: () => import("./view/layered/index.vue"),
        meta: {
          title: "分层配置",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "modifier",
        path: "/dataWarehouseDesign/modifier",
        component: () => import("./view/modifier/index.vue"),
        meta: {
          title: "修饰词管理",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "statPeriod",
        path: "/dataWarehouseDesign/statPeriod",
        component: () => import("./view/statPeriod/index.vue"),
        meta: {
          title: "统计周期管理",
          publicPage: true,
          icon: "ios-paper"
        }
      }
    ]
  }
];

export default routes;
