/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// vue.config.js
const path = require('path')
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
// const CspHtmlWebpackPlugin = require('csp-html-webpack-plugin');
const SpeedMeasurePlugin = require("speed-measure-webpack-plugin");
const VirtualModulesPlugin = require('webpack-virtual-modules');
const webpack = require("webpack");
const child_process = require('child_process');

let configFile= `../../config.json`
if (process.env.npm_config_configfile) {
  configFile = `../../${process.env.npm_config_configfile}`
} else if (process.env.npm_config_micro_module) {
  configFile = `../../config.${process.env.npm_config_micro_module}.json`
}
const { apps = {}, exts = {}, conf = {}, version, components = {} } = require(configFile)

if (version) {
  process.env.VUE_APP_VERSION = version
}
// 指定module打包, 不指定则打包全部子应用
// npm run serve --module=scriptis
let modules = process.env.npm_config_module || ''
if (modules) {
  modules = modules.split(',')
  Object.keys(apps).forEach(m => {
    if (!apps[m]) {
      console.error('配置文件无对应module')
    }
    if (modules.indexOf(m) < 0) {
      delete apps[m]
    }
  })
} else {
  modules = Object.keys(apps)
}
let requireComponent = []
let requireComponentVue = []
let appsRoutes = []
let appsI18n = []
let confs = []

Object.entries(apps).forEach(item => {
  if (item[1].module) {
    requireComponent.push(`require.context('@/${item[1].module}',true,/([a-z|A-Z])+\\/index\.js$/)`)
    requireComponentVue.push(`require.context('@/${item[1].module}',true,/([a-z|A-Z])+.vue$/)`)
  }

  // 处理路由
  if (item[1].routes) {
    appsRoutes.push(`${item[0]}: require('@/${item[1].routes}')`)
  }
  // 处理国际化
  if (item[1].i18n) {
    appsI18n.push(`{
      'zh-CN': require('@/${item[1].i18n["zh-CN"]}'),
      'en': require('@/${item[1].i18n['en']}')
    }`)
  }
})

// 扩展模块
let extsMoule = []
Object.keys(exts).forEach((item, index) => {
  extsMoule[index] = `
    '${item}': {
      'module': require('@/${exts[item].module}'),
      'options': ${JSON.stringify(exts[item].options)}
    }
  `
  // 扩展模块国际化文件
  if (exts[item].i18n) {
    appsI18n.push(`{
      'zh-CN': require('@/${exts[item].i18n["zh-CN"]}'),
      'en': require('@/${exts[item].i18n['en']}')
    }`)
  }
})

//vue components
let vuecomps = []
Object.keys(components).forEach(item => {
  vuecomps.push(`${item}: require('@/${components[item]}')`)
})

// config
Object.keys(conf).forEach(item=> {
  if(['app_logo'].includes(item)) {
    confs.push(`${item}: require('@/${conf[item]}')`)
  } else if(typeof conf[item] == 'string') {
    confs.push(`${item}: '${conf[item]}'`)
  } else if(typeof conf[item] == 'object') {
    confs.push(`${item}: ${JSON.stringify(conf[item])}`)
  } else {
    confs.push(`${item}: ${conf[item]}`)
  }
})

// 当前构建分支信息
const branchName = child_process.execSync('git branch --show-current').toString()
const gitUrl = child_process.execSync('git remote -v').toString()
let commitInfo = child_process.execSync('git branch -vv').toString()
commitInfo = commitInfo.split(/[\r\n]/).filter(item => item.indexOf('*') > -1)
const gitInfo = {
  gitUrl,
  branchName,
  commitInfo
}

const virtualModules = new VirtualModulesPlugin({
  'node_modules/dynamic-modules.js': `module.exports = {
    modules: ${JSON.stringify(modules)},
    exts: {${extsMoule.join(',')}},
    appsRoutes: {${appsRoutes.join(',')}},
    vuecomps: {${vuecomps.join(',')}},
    appsI18n: [${appsI18n.join(',')}],
    requireComponent: [${requireComponent.join(',')}],
    requireComponentVue: [${requireComponentVue.join(',')}],
    microModule: ${JSON.stringify(process.env.npm_config_micro_module) || false},
    conf: {${confs.join(',')}},
    gitInfo: ${JSON.stringify(gitInfo)}
  };`
});

const plugins = [
  virtualModules,
  new webpack.ProvidePlugin({
    jQuery: "jquery/dist/jquery.min.js",
    $: "jquery/dist/jquery.min.js"
  })
]

// scriptis 有使用编辑器组件, 需要Monaco Editor
if (modules.indexOf('scriptis') > -1) {
  plugins.push(new MonacoWebpackPlugin())
}

/**
 * resolve
 * @param {*} dir
 */
function resolve(dir) {
  return path.join(__dirname, dir)
}

// if (process.env.NODE_ENV !== 'dev') {
//   plugins.push(new CspHtmlWebpackPlugin(
//     {
//       "base-uri": "'self'",
//       "object-src": "'none'",
//       "child-src": "'none'",
//       "script-src": ["'self'", "'unsafe-eval'"],
//       "style-src": ["'self'", "'unsafe-inline'"],
//       "frame-src": "*",
//       "worker-src": "'self'",
//       "connect-src": [
//         "'self'",
//         "ws:",
//         "https://***REMOVED***",
//         "http://adm.webank.io"
//       ],
//       "img-src": [
//         "data:",
//         "'self'"
//       ]
//     },
//     {
//       enabled: true,
//       nonceEnabled: {
//         'style-src': false
//       }
//     }
//   ))
// }
const smp = new SpeedMeasurePlugin();
const configWrap = config => {
  if (process.env.NODE_ENV === "dev") {
    return config;
  } else {
    return smp.wrap(config);
  }
};
module.exports = {
  publicPath: './',
  outputDir: '../../dist',
  lintOnSave: process.env.NODE_ENV !== "production", // build无需eslint
  productionSourceMap: process.env.NODE_ENV === "dev", // 生产环境无需source map加速构建
  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          javascriptEnabled: true
        }
      },
      scss: {
        implementation: require('sass'),
        sassOptions: {
          fiber: false,
        },
      },
    }
  },
  chainWebpack: (config) => {
    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('../shared/components/svgIcon'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('../shared/components/svgIcon'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
    config.optimization.splitChunks({
      chunks: "all",
      cacheGroups: {
        "monaco-editor": {
          test: /[\\/]monaco-editor[\\/]/,
          name: "monaco-editor",
          enforce: true,
          reuseExistingChunk: true
        },
        iview: {
          test: /[\\/]iview[\\/]/,
          name: "iview",
          enforce: true,
          reuseExistingChunk: true
        },
        "moment-timezone": {
          test: /[\\/]moment-timezone[\\/]/,
          name: "moment-timezone",
          enforce: true,
          reuseExistingChunk: true
        },
        "echarts": {
          test: /[\\/]echarts[\\/]/,
          name: "echarts",
          enforce: true,
          reuseExistingChunk: true
        },
        "butterfly-dag": {
          test: /[\\/]butterfly-dag[\\/]/,
          name: "butterfly-dag",
          enforce: true,
          reuseExistingChunk: true
        },
        "shared": {
          test: /[\\/]shared[\\/]/,
          name: "shared",
          enforce: true,
          reuseExistingChunk: true
        },
        "jquery": {
          test: /[\\/]jquery[\\/]/,
          name: "jquery",
          enforce: true,
          // reuseExistingChunk: true
        }
      }
    })
  },
  configureWebpack: configWrap({
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '../'),
        'vscode': require.resolve('monaco-languageclient/lib/vscode-compatibility')
      }
    },
    plugins
  }),
  // 选项...
  pluginOptions: {
    mock: {
      entry: 'mock.js',
      power: false
    }
  },
  devServer: {
    disableHostCheck: true
  }
}
