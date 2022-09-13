import eventbus from '../helper/eventbus'
let hooks;

/**
 * type component  组件
 *      listener   监听函数
 * mulit 是否绑定多个监听函数
 */

hooks = {
  /**
   * scriptis脚本编辑器顶部扩展按钮
   */
  script_editor_top_tools: {
    type: 'component',
    mulit: true,
    listener: []
  },
  // scriptis 脚本运行底部TAB
  script_console_tabs: {
    type: 'component',
    mulit: true,
    listener: []
  },
  // scriptis 数据库表详情
  script_dbtb_details: {
    type: 'listener',
    mulit: false,
    listener: []
  },
  // workflows: 工作流开发底部TAB面板
  workflow_bottom_panel: {
    type: 'component',
    mulit: true,
    listener: []
  },
  // workflows: 工作流开发底部TAB面板
  workflow_bottom_panel_mounted: {
    type: 'listener',
    mulit: true,
    listener: []
  },
  // scriptis 结果集展示类型
  script_result_type_component: {
    type: 'component',
    mulit: true,
    listener: []
  },
  // scriptis 结果集左侧工具条菜单
  script_result_toolbar: {
    type: 'component',
    mulit: true,
    listener: []
  },
  // scriptis 执行进度左侧工具条菜单
  script_progress_toolbar: {
    type: 'component',
    mulit: true,
    listener: []
  },
  // 路由配置
  app_router_config: {
    type: 'listener',
    mulit: true,
    listener: []
  },
  // 路由变化
  app_router_beforechange: {
    type: 'listener',
    mulit: true,
    listener: []
  },
  app_router_afterchange: {
    type: 'listener',
    mulit: true,
    listener: []
  },
  // 生产中心项目菜单
  scheduler_center_project_menu: {
    type: 'listener',
    mulit: true,
    listener: []
  },
  // 生产中心工作流列表操作按钮
  scheduler_center_workflow_action: {
    type: 'listener',
    mulit: true,
    listener: []
  },
  // 登录成功后
  after_login: {
    type: 'listener',
    mulit: true,
    listener: []
  },
  // /**
  //  * 扩展导航菜单
  //  * @param Array menu lsit
  //  */
  // sub_nav_menu: {
  //   type: 'listener',
  //   mulit: true,
  //   listener: []
  // }
};

const pluginModule = {
  hooks: hooks,
  init: function(pluginModuleList = {}) {
    Object.keys(pluginModuleList).forEach(plugin => {
      if (!pluginModuleList[plugin]) return null;
      if (pluginModuleList[plugin] && typeof pluginModuleList[plugin].module.default === 'function') {
        pluginModuleList[plugin].module.default.call(pluginModule, pluginModuleList[plugin].options);
      }
    });
  },
  storage: {},
  on: eventbus.on,
  off: eventbus.off,
  clear: eventbus.clear,
  emit: eventbus.emit
};

/**
 * 绑定扩展
 * @param {*} name
 * @param {*} listener
 */
function bindHook(name, listener) {
  if (!name) {
    throw new Error('缺少hookname');
  }
  if (name in hooks === false) {
    throw new Error(`不存在的hookname: ${name}`);
  }
  if (hooks[name].mulit === true) {
    hooks[name].listener.push(listener);
  } else {
    hooks[name].listener = listener;
  }
}

/**
 * 触发扩展
 * @param {*} name
 * @param  {...any} args
 */
function emitHook(name, ...args) {
  if (!hooks[name]) {
    throw new Error('不存在的hook name');
  }
  let hook = hooks[name];
  if (hook.mulit === true && hook.type === 'listener') {
    if (Array.isArray(hook.listener)) {
      let promiseAll = [];
      hook.listener.forEach(item => {
        if (typeof item === 'function') {
          promiseAll.push(Promise.resolve(item.call(pluginModule, ...args)));
        }
      });
      return Promise.all(promiseAll);
    }
  } else if (hook.mulit === false && hook.type === 'listener') {
    if (typeof hook.listener === 'function') {
      return Promise.resolve(hook.listener.call(pluginModule, ...args));
    }
  } else if(hook.mulit === true && hook.type === 'component') {
    let comps = [];
    if (Array.isArray(hook.listener)) {
      hook.listener.forEach(item => {
        if (typeof item === 'function') {
          const hookComps = item.call(pluginModule, ...args)
          if(Array.isArray) {
            comps = comps.concat(hookComps)
          } else {
            comps.push(hookComps)
          }
        }
      })
    }
    return comps;
  } else if (hook.type === 'component') {
    return hook.listener;
  }
}

pluginModule.bindHook = bindHook;
pluginModule.emitHook = emitHook;

export default pluginModule
