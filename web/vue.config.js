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
const fs = require('fs')
const FileManagerPlugin = require('filemanager-webpack-plugin');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const CspHtmlWebpackPlugin = require('csp-html-webpack-plugin');
const VirtualModulesPlugin = require('webpack-virtual-modules');
const webpack = require('webpack')
const apps = require('./src/config.json')

const getVersion = () => {
  const pkgPath = path.join(__dirname, './package.json')
  let pkg = fs.readFileSync(pkgPath);
  pkg = JSON.parse(pkg);
  return pkg.version;
}

// 指定module打包, 不指定则打包全部子应用
// npm run serve --module=scriptis
let modules = process.env.npm_config_module || ''
if (modules) {
  modules = modules.split(',')
  Object.keys(apps).forEach(m => {
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
let headers = []

Object.entries(apps).forEach(item => {
  if (item[1].module) {
    requireComponent.push(`require.context('@/${item[1].module}',true,/([a-z|A-Z])+\\/index\.js$/)`)
    requireComponentVue.push(`require.context('@/${item[1].module}',true,/([a-z|A-Z])+.vue$/)`)
  }
  // 获取个模块header
  if (item[1].header) {
    headers.push(`${item[0]}: require('@/${item[1].header}/index.js')`)
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

let buildDynamicModules = Object.values(apps)
buildDynamicModules = JSON.stringify(buildDynamicModules)

const virtualModules = new VirtualModulesPlugin({
  'node_modules/dynamic-modules.js': `module.exports = {
    apps: ${buildDynamicModules},
    modules: ${JSON.stringify(modules)},
    appsRoutes: {${appsRoutes.join(',')}},
    appsI18n: [${appsI18n.join(',')}],
    requireComponent: [${requireComponent.join(',')}],
    requireComponentVue: [${requireComponentVue.join(',')}],
    microModule: ${JSON.stringify(process.env.npm_config_micro_module) || false},
    headers:{${headers.join(',')}}
  };`
});

const plugins = [
  virtualModules,
  new webpack.ProvidePlugin({jQuery:"jquery/dist/jquery.min.js",$:"jquery/dist/jquery.min.js" })
]

// scriptis linkis 有使用编辑器组件, 需要Monaco Editor
if (modules.indexOf('scriptis') > -1 || modules.indexOf('linkis') > -1) {
  plugins.push(new MonacoWebpackPlugin())
}

/**
 * resolve
 * @param {*} dir
 */
function resolve(dir) {
  return path.join(__dirname, dir)
}

if (process.env.NODE_ENV !== 'dev') {
  plugins.push(new CspHtmlWebpackPlugin(
    {
      "base-uri": "'self'",
      "object-src": "'none'",
      "child-src": "'none'",
      "script-src": ["'self'", "'unsafe-eval'"],
      "style-src": ["'self'", "'unsafe-inline'"],
      "frame-src": "*",
      "worker-src": "'self'",
      "connect-src": [
        "'self'",
        "ws:",
        "http://adm.webank.io"
      ],
      "img-src": [
        "data:",
        "'self'"
      ]
    },
    {
      enabled: true,
      nonceEnabled: {
        'style-src': false
      }
    }
  ))
}

module.exports = {
  publicPath: './',
  outputDir: 'dist/dist',
  chainWebpack: (config) => {
    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/components/svgIcon'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/components/svgIcon'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
    if (process.env.NODE_ENV === 'production' || process.env.NODE_ENV === 'sandbox' || process.env.NODE_ENV === 'bdp') {
      config.plugin('compress').use(FileManagerPlugin, [{
        onEnd: {
          copy: [
            { source: './config.sh', destination: `./dist` },
            { source: './install.sh', destination: `./dist` }
          ],
          // 先删除根目录下的zip包
          delete: [`./luban-DataSphereStudio-${getVersion()}-dist.zip`],
          // 将dist文件夹下的文件进行打包
          archive: [
            { source: './dist', destination: `./luban-DataSphereStudio-${getVersion()}-dist.zip` },
          ]
        },
      }])
    }
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
        '@component': path.resolve(__dirname, './src/components')
      }
    },
    plugins
  },
  // 选项...
  pluginOptions: {
    mock: {
      entry: 'mock.js',
      power: false
    }
  },
  devServer: {
    proxy: {
      '/api': {
        target: 'http://192.168.10.201:8088',
        // target: 'http://192.168.10.180:8088',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      },
      '/dolphinscheduler': {
        target: 'http://192.168.10.223:12345',
        changeOrigin: true,
        pathRewrite: {
          '^/dolphinscheduler': '/dolphinscheduler'
        }
      }
    }
  }
}
