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
let CopyWebpackPlugin = require('copy-webpack-plugin')
let FileManagerPlugin = require('filemanager-webpack-plugin');
let MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
let path = require('path')
let fs = require('fs');

const getVersion = () => {
  const pkgPath = path.join(__dirname, './package.json');
  let pkg = fs.readFileSync(pkgPath);
  pkg = JSON.parse(pkg);
  return pkg.version;
}

// const host = "ai.ctyun.cn";
// const port = "8088";

const host = "saas.ctyun.cn";
const port = "443";

module.exports = {
  publicPath: process.env.VUE_APP_PREFIX,
  outputDir: 'dist/dist',
  devServer: {
    port: 8080,
    https: true,
    open: true,
    disableHostCheck: true,
    overlay: {
      warnings: false,
      errors: true
    },
    proxy: {    //代理转发
      '^/luban/api/rest_j/v1': {
        target: `https://${host}:${port}`,  //后端服务地址
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/luban/api/rest_j/v1': '/luban/api/rest_j/v1'
        }
      },
      '^/luban/ws/api': {    //websocket
        target: `wss://${host}:${port}`,
        ws: true,
        secure: false,
      },
      '^/luban/operationApi': {
        target: 'http://10.30.90.89:30011/delta-yunying',
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/luban/operationApi': ''
        },
      },
      '^/luban/test': {
        target: 'http://192.168.10.83:9030/api/rest_j/v1',
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/luban/test': ''
        },
      },
    }
    // after: require('./mock/mock-server.js')
  },
  chainWebpack: (config) => {
    if (process.env.NODE_ENV === 'production' || process.env.NODE_ENV === 'sandbox') {
      config.plugin('compress').use(FileManagerPlugin, [{
        onEnd: {
          copy: [
            { source: 'node_modules/monaco-editor/dev/vs', destination: `./dist/dist/static/vs` },
            { source: './config.sh', destination: `./dist/conf` },
            { source: './install.sh', destination: `./dist/bin` }
          ],
          // 先删除根目录下的zip包
          delete: [`./Luban-DataSphereStudio-${getVersion()}-dist.zip`],
          // 将dist文件夹下的文件进行打包
          archive: [
            { source: './dist', destination: `./Luban-DataSphereStudio-${getVersion()}-dist.zip` },
          ]
        },
      }])
    }
  },
  configureWebpack: {
    resolve: {
      alias: {
        'vue$': 'vue/dist/vue.esm.js',
        '@': path.resolve(__dirname, './src'),
        '@js': path.resolve(__dirname, './src/js'),
        '@assets': path.resolve(__dirname, './src/assets')
      }
    },
    plugins: [
      new CopyWebpackPlugin([{
        from: 'node_modules/monaco-editor/dev/vs',
        to: 'static/vs',
      },{
        from: 'src/luban-doc',
        to: 'luban-doc',
      }]),
      new MonacoWebpackPlugin({}),
    ]
  },
  // 选项...
  pluginOptions: {
    mock: {
      entry: 'mock.js',
      power: false
    }
  }
}
