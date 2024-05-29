import child_process from 'child_process';

// 默认打包DSS
let configFile = `dss.js`;
if (process.env.npm_config_configfile) {
  configFile = `.${process.env.npm_config_configfile}`;
}

const config = await import(`./config/${configFile}`);
const { apps = {}, exts = {}, conf = {}, version } = config.default as AppConf;

if (version) {
  process.env.VUE_APP_VERSION = version;
}

const appsRoutes: string[] = [];
const appsI18n: string[] = [];
const confs: string[] = [];

Object.entries(apps).forEach((item) => {
  // 处理路由
  const conf = item[1] as ConfigOfApps;
  if (conf.routes) {
    appsRoutes.push(`${item[0]}: import('@/packages/${conf.routes}')`);
  }
  // 处理国际化
  if (conf.i18n) {
    appsI18n.push(`{
      'zh-CN': import('@/packages/${conf.i18n['zh-CN']}'),
      'en': import('@/packages/${conf.i18n['en']}')
    }`);
  }
});

// 扩展模块
const extsMoule: string[] = [];
Object.keys(exts).forEach((item, index) => {
  extsMoule[index] = `
    '${item}': {
      'module': import('@/exts/${exts[item].module}'),
      'options': ${JSON.stringify(exts[item].options)}
    }
  `;
  // 扩展模块国际化文件
  if (exts[item].i18n) {
    appsI18n.push(`{
      'zh-CN': import('@/exts/${exts[item].i18n['zh-CN']}'),
      'en': import('@/exts/${exts[item].i18n['en']}')
    }`);
  }
});

// config
Object.keys(conf).forEach((item) => {
  if (['app_logo'].includes(item)) {
    confs.push(`${item}: import('@/${conf[item]}')`);
  } else if (typeof conf[item] == 'string') {
    confs.push(`${item}: '${conf[item]}'`);
  } else if (typeof conf[item] == 'object') {
    confs.push(`${item}: ${JSON.stringify(conf[item])}`);
  } else {
    confs.push(`${item}: ${conf[item]}`);
  }
});

const gitInfo: { branchName: string; gitUrl: string; commitInfo: string } = {
  branchName: '',
  gitUrl: '',
  commitInfo: '',
};
try {
  // 当前构建分支信息
  gitInfo.branchName = child_process
    .execSync('git branch --show-current')
    .toString();
  gitInfo.gitUrl = child_process.execSync('git remote -v').toString();
  const commitInfo = child_process.execSync('git branch -vv').toString();
  gitInfo.commitInfo = commitInfo
    .split(/[\r\n]/)
    .filter((item) => item.indexOf('*') > -1)
    .join('');
} catch (e) {
  /* empty */
}

export default function dynamicPlugin() {
  const virtualModuleId = 'virtual-app-module';
  const resolvedVirtualModuleId = '\0' + virtualModuleId;
  return {
    name: 'virtual-module-plugin',
    resolveId(id) {
      if (id === virtualModuleId) {
        return resolvedVirtualModuleId;
      }
    },
    load(id) {
      if (id === resolvedVirtualModuleId) {
        return `export default {
            exts: {${extsMoule.join(',')}},
            appsRoutes: {${appsRoutes.join(',')}},
            appsI18n: [${appsI18n.join(',')}],
            conf: {${confs.join(',')}},
            gitInfo: ${JSON.stringify(gitInfo)}
          };`;
      }
    },
  };
}
