const routes = [
  {
    name: "datamodelcenter",
    path: "/datamodelcenter",
    redirect: "/datamodelcenter/dimension",
    meta: {
      title: "数据模型中心",
      publicPage: true
    },
    component: () => import("./view/layout/index.vue"),
    children: [
      {
        name: "tableManage",
        path: "/datamodelcenter/tableManage",
        component: () => import("./view/layout/emptylayout.vue"),
        redirect: "/datamodelcenter/tableManage/tableSearch",
        meta: {
          title: "数据表管理",
          publicPage: true,
          icon: "ios-paper",
          cover: "tableSearch"
        },
        children: [
          {
            name: "tableSearch",
            path: "/datamodelcenter/tableManage/tableSearch",
            component: () => import("./view/tableManage/tableSearch/index.vue"),
            meta: {
              title: "数据表管理",
              publicPage: true,
              icon: "ios-paper"
            }
          },
          {
            name: "tableInfo",
            path: "/datamodelcenter/tableManage/tableInfo/:id",
            component: () => import("./view/tableManage/tableInfo/index.vue"),
            meta: {
              title: "表详情",
              publicPage: true,
              icon: "ios-paper"
            }
          }
        ]
      },
      {
        name: "dimension",
        path: "/datamodelcenter/dimension",
        component: () => import("./view/dimension/index.vue"),
        meta: {
          title: "维度",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "measure",
        path: "/datamodelcenter/measure",
        component: () => import("./view/measure/index.vue"),
        meta: {
          title: "度量",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "indicators",
        path: "/datamodelcenter/indicators",
        component: () => import("./view/indicators/index.vue"),
        meta: {
          title: "指标",
          publicPage: true,
          icon: "ios-paper"
        }
      }
    ]
  }
];

export default routes;
