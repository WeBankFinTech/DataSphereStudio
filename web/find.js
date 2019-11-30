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

/* eslint-disable require-jsdoc */
/* eslint-disable no-console */
var fs = require('fs');
var path = require('path');
var reg = /[\u4e00-\u9fa5]+[：？！:]?/g;
var json = {};

// 要处理的目录参数
var dir = process.argv[2];

var dirPath = path.resolve(dir);

json.dirPath = dirPath;
json.found = {};

function findCNText(filePath) {
  fs.readdir(filePath, function (err, files) {
    if (err) {
      console.warn(err)
    } else {
      files.forEach(function (filename) {
        var filedir = path.join(filePath, filename);
        fs.stat(filedir, function (eror, stats) {
          if (eror) {
            console.warn('获取文件stats失败');
          } else {
            var isFile = stats.isFile();
            var isDir = stats.isDirectory();
            if (isFile) {
              var content = fs.readFileSync(filedir, 'utf-8');
              // 忽略html注释
              content = content.replace(/<!--[\w\W\r\n]*?-->/gmi, '');
              // 忽略js注释
              content = content.replace(/(?:^|\n|\r)\s*\/\*[\s\S]*?\*\/\s*(?:\r|\n|$)/g, '');
              // content = content.replace(/(?:^|\n|\r)\s*\/\/.*(?:\r|\n|$)/g, '');
              content = content.replace(/\s*\/\/.*(?:\r|\n|$)/g, '');
              // 提取中文
              var arrs = content.match(reg) || [];
              arrs.forEach(item => {
                json.found[item] = item;
              });

              fs.writeFile('./cn.json', JSON.stringify(json), function (err) {
                if (err) {
                  throw err;
                }
                console.log('done.');
              })
            }
            if (isDir) {
              findCNText(filedir);
            }
          }
        })
      });
    }
  });
}

findCNText(dirPath);

// 1.执行node find.js ./src/pages/change, 找出目录下所有需要替换的中文形成json
// 2.然后执行node translate.js 调用API批量翻译，翻译结束结果输出cn.json ，对接口翻译结果检查及修改
// 3.生成的json文件每个key值修改完成后，执行node replace.js, 会把所指定目录下的中文全部按第二步的key值替换为$t('key') 或者 this.$t('key'),或者{{ $t('key') }}
// 4.把json文件内容复制到国际配置里
