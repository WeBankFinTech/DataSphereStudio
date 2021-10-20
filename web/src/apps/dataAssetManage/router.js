const routes = [
  {
    name: "dataAssetManage",
    path: "/dataAssetManage",
    redirect: "/dataAssetManage/overview",
    component: () => import("./view/layout/index.vue"),
    meta: {
      title: "数据资产",
      publicPage: true,
      icon: "ios-paper"

    },
    children: [
      {
        name: "overview",
        path: "/dataAssetManage/overview",
        component: () => import("./module/dataAssetManage/overview.vue"),
        meta: {
          title: "数据总览",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "assets",
        path: "/dataAssetManage/assets",
        redirect: "/dataAssetManage/assets/search",
        component: () => import("./module/dataAssetManage/assetsIndex.vue"),
        meta: {
          title: "数据资产目录",
          publicPage: true,
          cover: "assetsSearch"
        },
        children: [
          {
            name: "assetsSearch",
            path: "/dataAssetManage/assets/search",
            component: () => import("./view/assetsSearch/index.vue"),
            meta: {
              title: "数据资产目录",
              publicPage: true,
              icon: "ios-paper"
            }
          },
          {
            name: "assetsInfo",
            path: "/dataAssetManage/assets/info",
            component: () => import("./view/assetsInfo/index.vue"),
            meta: {
              title: "数据资产详情",
              publicPage: true,
              cover: "assetsInfo"
            }
          }
        ]
      }
    ]
  }
];

export default routes;
