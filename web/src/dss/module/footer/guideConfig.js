// 帮助文档配置
export const guideConfig = {
  "/common": {
    questions: [
      {
        title: '工作空间类型中，项目导向和部门导向的区别？',
        url: ''
      },
      {
        title: '主账号是否可以转移？',
        url: ''
      },
      {
        title: '什么情况下可以删除工作空间？', 
        url: ''
      }
    ]
  },
  "/newhome": {
    title: '了解管理员首页',
    desc: '管理员首页是工作空间管理员才可进入的页面，组织的主账号默认为工作空间管理员，可以在本页规划工作空间架构，如新建、删除工作空间，创建部门和人员等工作。',
    steps: [
      {
        seq: '第一步',
        title: '创建部门和用户',
        content: [
          {
            text: '工作空间管理员可以在首页点击【部门和用户管理】，进行部门和用户创建，管理和删除操作',
          },
          {
            img: 'guide/newhome/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '创建工作空间',
        content: [
          {
            text: '工作空间管理员可以点击【创建工作空间】按钮，进行新建工作空间设置'
          },
          {
            img: 'guide/newhome/step2.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '工作空间类型中，项目导向和部门导向的区别？',
        url: ''
      },
      {
        title: '主账号是否可以转移？',
        url: ''
      },
      {
        title: '什么情况下可以删除工作空间？', 
        url: ''
      }
    ]
  },
  "/permissions": {
    title: '了解部门和用户管理',
    desc: '部门和用户管理是对于整个组织架构的设置和管理页，工作空间管理员可以在本页新增和调整部门层级，以及创建、修改和删除用户',
    steps: [
      {
        seq: '第一步',
        title: '创建部门',
        content: [
          {
            text: '工作空间管理员可以选择部门管理，点击【新增】，进行部门创建，在列表中进行部门【编辑】和【删除】操作，注意：元公司不可删除，仅可编辑修改信息',
          },
          {
            img: 'guide/permissions/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '创建用户',
        content: [
          {
            text: '工作空间管理员可以选择用户管理，点击【新增】，进行用户创建，在列表中进行用户【编辑】和【修改用户密码】操作'
          },
          {
            img: 'guide/permissions/step2.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '用户必须归属于末级子部门吗？',
        url: ''
      },
      {
        title: '什么情况下可以删除部门？',
        url: ''
      },
      {
        title: '如何重置用户密码？',
        url: ''
      }
    ]
  },
  "/workspaceHome": {
    title: '了解工作空间首页',
    desc: '工作空间首页是用户进入鲁班后的操作首页，管理员可以在本页配置工作空间内用户及对应的角色，查看管理台等。其他用户可以在本页创建和管理工程，以及进入工程，进行开发和运维等作业',
    steps: [
      {
        seq: '第一步',
        title: '导入用户及分配角色',
        content: [
          {
            text: '管理员角色由工作空间管理员指定，管理员可以在工作空间首页，点击【工作空间管理】，进入管理页，进行用户导入，角色分配和角色权限配置操作',
          },
          {
            img: 'guide/workspaceHome/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '创建工程',
        content: [
          {
            text: '管理员或开发用户，可点击【创建工程】，配置工程信息，创建新工程'
          },
          {
            img: 'guide/workspaceHome/step2.png'
          }
        ]
      },
      {
        seq: '第三步',
        title: '进入管理台',
        content: [
          {
            text: '管理员，可点击【管理台】，进入全局配置中心，对资源和参数等进行调配'
          },
          {
            img: 'guide/workspaceHome/step3.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '如何查看自己的账户信息？',
        url: ''
      },
      {
        title: '如何修改账户密码？',
        url: ''
      },
      {
        title: '如何快速配置功能菜单？',
        url: ''
      },
      {
        title: '如何快速切换工作空间？',
        url: ''
      },
      {
        title: '个人工作台可以自定义吗？',
        url: ''
      }
    ]
  },
  "/workspaceManagement": {
    title: '了解工作空间管理',
    desc: '工作空间管理页，是工作空间管理员进行工作空间人员管理，角色分配的页面，其可以在本页面查看工作空间信息，导入、删除工作空间成员，以及配置成员角色，配置角色权限策略等',
    steps: [
      {
        seq: '第一步',
        title: '新增用户及配置角色',
        content: [
          {
            text: '管理员可以选择用户信息，点击【创建】，进行用户导入，创建时需对用户的角色进行配置，在列表中进行用户角色【编辑】和用户【删除】操作',
          },
          {
            img: 'guide/workspaceManagement/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '角色权限配置',
        content: [
          {
            text: '管理员可以选择权限信息，点击【创建】，进行角色自定义，创建时需对该角色的菜单权限和组件访问权限进行配置，并支持对已有角色进行权限调整'
          },
          {
            img: 'guide/workspaceManagement/step2.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '同一个用户可以被同时赋予多个角色吗？',
        url: ''
      },
      {
        title: '用户被取消角色后，是否还能编辑之前管理的工程？',
        url: ''
      },
      {
        title: '删除用户后，其管理的工程是否也会被删除？',
        url: ''
      }
    ]
  },
  "/workflow": {
    title: '了解开发中心',
    desc: '开发中心，是进行工作流创建，开发和管理的页面，拥有编辑权限的用户，可以在开发中心创建、编辑和删除工作流，拥有发布权限的用户，可以在开发中心发布已保存的工作流，拥有查看权限的用户，仅可查看对应的工作流信息。',
    steps: [
      {
        seq: '第一步',
        title: '新建/导入工作流',
        content: [
          {
            text: '编辑权限用户或工程拥有者，可以点击【新建/导入编排】或者左侧菜单列表中的【+】，进行工作流创建或导入',
          },
          {
            img: 'guide/workflow/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '创建和编辑工作流节点',
        content: [
          {
            text: '编辑权限用户或工程拥有者，可以拖拽左侧组件栏中的组件进入画布，进行工作流节点的新增，双击节点进行编辑'
          },
          {
            img: 'guide/workflow/step2.png'
          }
        ]
      },
      {
        seq: '第三步',
        title: '调试及发布',
        content: [
          {
            text: '编辑权限用户或工程拥有者，对保存的工作流进行【运行】测试，发布权限用户，可对已保存工作流进行【发布】操作，发布成功后，出现【前往调度中心】按钮，点击进入运维中心'
          },
          {
            img: 'guide/workflow/step3.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '已上线的工作流可以在开发中心修改吗？',
        url: ''
      },
      {
        title: '工程的编辑权限，发布权限，查看权限用户可以变更吗？',
        url: ''
      },
      {
        title: '为何进入不了运维中心？',
        url: ''
      }
    ]
  },
  "/workflow/scheduler": {
    title: '了解运维中心',
    desc: '运维中心，是进行工作流调度管理的页面，拥有发布权限的用户，可以在运维中心查看运维大屏，进行工作流上/下线，工作流运行，工作流定时管理，任务重跑及查看DAG和日志等，可通过【前往调度中心】按钮，或右上方【开发中心】按钮切换【运维中心】进入',
    steps: [
      {
        seq: '第一步',
        title: '运维大屏',
        content: [
          {
            text: '发布权限用户，可以点击左侧菜单点击【运维大屏】切换至运维大屏页，运维大屏展示整个工程下所有工作流的统计图表监控信息',
          },
          {
            img: 'guide/workflow/scheduler/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '工作流上下线',
        content: [
          {
            text: '发布权限用户，可以点击左侧菜单点击【工作流定义】切换至工作流调度页，列表中可点击【上/下线】进行工作流上/下线操作'
          },
          {
            img: 'guide/workflow/scheduler/step2.png'
          }
        ]
      },
      {
        seq: '第三步',
        title: '工作流运行及定时管理',
        content: [
          {
            text: '发布权限用户，对于已上线的工作流，可以在【工作流定义】列表，点击【运行】，设置并运行即时调度，或点击【定时】，进行工作流定时调度设置，设置后可以点击【定时管理】，进入定时列表修改定时设置'
          },
          {
            img: 'guide/workflow/scheduler/step3.png'
          }
        ]
      },
      {
        seq: '第四步',
        title: '任务重跑',
        content: [
          {
            text: '发布权限用户，对于已运行的工作流，可以点击【工作流实例】进入实例列表，点击【重跑】，进行工作流实例的重跑操作'
          },
          {
            img: 'guide/workflow/scheduler/step4.png'
          }
        ]
      },
      {
        seq: '第五步',
        title: '查看DAG及日志',
        content: [
          {
            text: '发布权限用户，可以在【工作流实例】列表，点击实例名称，查看对应DAG图及日志'
          },
          {
            img: 'guide/workflow/scheduler/step5.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '运维大屏展示的信息是整个工程的统计还是单工作流的统计？',
        url: ''
      },
      {
        title: '可以进行节点级的重跑吗？',
        url: ''
      },
      {
        title: '如何修改已上线的定时任务？',
        url: ''
      }
    ]
  },
  "/console": {
    title: '了解管理台',
    desc: '管理台，是工作空间的全局配置中心，管理员可以对于整个工作空间进行资源管控，全局历史查看，组件参数配置，全局变量设置等操作。',
    steps: [
      {
        seq: '第一步',
        title: '查看全局历史',
        content: [
          {
            text: '管理员可以点击【全局历史】查看工作空间的全局历史，并支持搜索',
          },
          {
            img: 'guide/console/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '参数配置',
        content: [
          {
            text: '管理员可以点击【参数配置】，进行组件参数配置，可对组件参数默认值进行修改'
          },
          {
            img: 'guide/console/step2.png'
          }
        ]
      },
      {
        seq: '第三步',
        title: '设置全局变量',
        content: [
          {
            text: '管理员可以点击【全局变量】编辑已有全局变量，和新增全局变量'
          },
          {
            img: 'guide/console/step3.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '普通用户是否可以进入管理台？',
        url: ''
      },
      {
        title: '组件参数配置有默认值吗？',
        url: ''
      },
      {
        title: '全局变量的作用范围是整个工作空间吗？',
        url: ''
      }
    ]
  },
  "/home": {
    title: '了解Scriptis',
    desc: 'Scriptis是一款一站式交互式数据探索分析工具，提供多种计算存储引擎(如Spark、Hive、TiSpark等)、Hive数据库管理功能、资源(如Yarn资源、服务器资源)管理、应用管理和各种用户资源(如UDF、变量等)管理的能力。',
    steps: [
      {
        seq: '第一步',
        title: '建立数据表',
        content: [
          {
            text: '选择【数据库】，点击【+】，进入建表向导，按步骤设置可进行建表操作',
          },
          {
            img: 'guide/home/step1.png'
          }
        ]
      },
      {
        seq: '第二步',
        title: '新建查询脚本',
        content: [
          {
            text: '选择【工作空间】，右击目录，点击【新建脚本】，填写弹出框可进行查询脚本新建操作'
          },
          {
            img: 'guide/home/step2.png'
          }
        ]
      },
      {
        seq: '第三步',
        title: '新增自定义函数',
        content: [
          {
            text: '选择【UDF函数】，右击个人函数，点击【新增函数】，填写弹出框可进行自定义函数新增操作'
          },
          {
            img: 'guide/home/step3.png'
          }
        ]
      }
    ],
    questions: [
      {
        title: '运行结果最多支持展示多少行数据？',
        url: ''
      },
      {
        title: '运行结果集支持导出吗？',
        url: ''
      },
      {
        title: '右下角弹出框的作用是？',
        url: ''
      }
    ]
  },
  "/commonIframe": {
    title: '什么是Exchangis',
    desc: 'Exchangis是一个轻量级的、高扩展性的数据交换平台，支持对结构化及无结构化的异构数据源之间的数据传输，在应用层上具有数据权限管控、节点服务高可用和多租户资源隔离等业务特性，而在数据层上又具有传输架构多样化、模块插件化和组件低耦合等架构特点。'
  },
  "/dataService": {
    title: '什么是数据服务',
    desc: '数据服务为操作者提供快速将数据表生成API的能力，同时支持一键发布现有的API，并进行统一的管理和授权。数据服务包括服务开发和服务管理两块内容，其中服务开发主要是用户对于API的创建和配置，服务管理主要是对于API的管理和授权，可以在左侧菜单切换。',
    steps: [
      {
        seq: '第一步',
        title: '创建业务流程',
        content: [
          {
            text: '（数据服务用户，选择左侧菜单【服务开发列表】，点击【+】可新建业务流程）',
          }
        ]
      },
      {
        seq: '第二步',
        title: '创建API',
        content: [
          {
            text: '（数据服务用户，选择左侧菜单【服务开发列表】，点击创建的业务流程旁的【+】可弹出新增API面板，填写后可新建API）'
          }
        ]
      },
      {
        seq: '第三步',
        title: '配置API',
        content: [
          {
            text: '（数据服务用户，创建API后，在右侧主页面，可配置API信息及参数）'
          }
        ]
      },
      {
        seq: '第四步',
        title: '测试及发布API',
        content: [
          {
            text: '（数据服务用户，配置并保存完API后，可点击【测试】按钮进行单次接口测试，测试后点击【发布】可将API发布上线）'
          }
        ]
      }
    ],
    questions: [
      {
        title: '如何快速查看数据表和表结构？',
        url: ''
      },
      {
        title: '授权到期的API，可以继续进行授权吗？',
        url: ''
      },
      {
        title: 'API发布后是否可以对API进行修改？',
        url: ''
      }
    ]
  },
  "/dataManagement": {
    title: '服务管理',
    desc: '',
    steps: [
      {
        seq: '第一步',
        title: 'API管理',
        content: [
          {
            text: '（数据服务用户，可选择左侧菜单【服务管理】，点击【API管理】，在右侧主页面进行已发布上线的API管理操作，可对API进行【下线】，【测试】和【复制调用地址】操作）',
          }
        ]
      },
      {
        seq: '第二步',
        title: 'API监控',
        content: [
          {
            text: '（数据服务用户，可选择左侧菜单【服务管理】，点击【API监控】，对全局API进行图表监测数据查看，以及可选择【API计量详情】，查看单个API监控信息）'
          }
        ]
      },
      {
        seq: '第三步',
        title: 'API测试',
        content: [
          {
            text: '（数据服务用户，可选择左侧菜单【服务管理】，点击【API测试】，对单个API进行调用测试）'
          }
        ]
      },
      {
        seq: '第四步',
        title: 'API调用',
        content: [
          {
            text: '（数据服务用户，可选择左侧菜单【服务管理】，点击【API调用】，在右侧主页面对已授权调用的API进行管理，并可点击【新增授权】新建一个API调用授权）'
          }
        ]
      }
    ],
    questions: [
      {
        title: '如何快速查看数据表和表结构？',
        url: ''
      },
      {
        title: '授权到期的API，可以继续进行授权吗？',
        url: ''
      },
      {
        title: 'API发布后是否可以对API进行修改？',
        url: ''
      }
    ]
  }
};