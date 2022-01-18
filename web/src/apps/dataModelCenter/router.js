const routes = [
  {
    name: "dataModelCenter",
    path: "/dataModelCenter",
    redirect: "/dataModelCenter/tableManage",
    component: () => import("./view/layout/index.vue"),
    meta: {
      title: "数据模型中心",
      publicPage: true
    },
    children: [
      {
        name: "tableManage",
        path: "/dataModelCenter/tableManage",
        component: () => import("./view/layout/emptylayout.vue"),
        redirect: "/dataModelCenter/tableManage/tableSearch",
        meta: {
          publicPage: true,
          cover: "tableSearch"
        },
        children: [
          {
            name: "tableSearch",
            path: "/dataModelCenter/tableManage/tableSearch",
            component: () => import("./view/tableManage/tableSearch/index.vue"),
            meta: {
              title: "数据表管理",
              publicPage: true,
              icon: "ios-paper"
            }
          },
          {
            name: "tableInfo",
            path: "/dataModelCenter/tableManage/tableInfo",
            component: () => import("./view/tableManage/tableInfo/index.vue"),
            meta: {
              title: "表详情",
              publicPage: true
            }
          },
          {
            name: "tableEditor",
            path: "/dataModelCenter/tableManage/tableEditor",
            component: () => import("./view/tableManage/tableEditor/index.vue"),
            meta: {
              title: "表编辑",
              publicPage: true
            }
          },
          {
            name: "tableVersionInfo",
            path: "/dataModelCenter/tableManage/tableVersionInfo",
            component: () =>
              import("./view/tableManage/tableVersionInfo/index.vue"),
            meta: {
              title: "历史版本信息",
              publicPage: true
            }
          }
        ]
      },
      {
        name: "dimension",
        path: "/dataModelCenter/dimension",
        component: () => import("./view/dimension/index.vue"),
        meta: {
          title: "维度",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "measure",
        path: "/dataModelCenter/measure",
        component: () => import("./view/measure/index.vue"),
        meta: {
          title: "度量",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "indicators",
        path: "/dataModelCenter/indicators",
        component: () => import("./view/indicators/index.vue"),
        meta: {
          title: "指标",
          publicPage: true,
          icon: "ios-paper"
        }
      },
      {
        name: "labels",
        path: "/dataModelCenter/labels",
        component: () => import("./view/labels/index.vue"),
        meta: {
          title: "标签",
          publicPage: true,
          icon: "ios-paper"
        }
      }
    ]
  }
];

export default routes;
