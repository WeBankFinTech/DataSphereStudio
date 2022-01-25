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
var fs = require('fs');
var path = require('path');
var json = JSON.parse(fs.readFileSync('./cn.json', 'utf-8'));

function replacefnT(filePath) {
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
              var values = Object.values(json.translate);
              var keys = Object.keys(json.translate)
              values = values.sort((a, b) => b.length - a.length);

              values.forEach((it) => {
                var key = keys.find(k => json.translate[k] == it)
                // template
                // title="单表校验"
                // name="技术规则"
                // placeholder="dd"
                // label="dd"
                // <span class="colorTag">未通过校验</span>
                // js
                // '技术规则'
                var attrReg = new RegExp(` (title|name|label|placeholder)="${it}"`, 'g')
                var tagReg = new RegExp(`>(\\s*)${it}(\\s*)<`, 'g')
                var jsReg = new RegExp(`(['"])${it}(['"])`, 'g')
                var dir = json.dirPath.split('/').pop()
                content = content.replace(attrReg, ` :$1="t('${dir}.${key}')"`)
                content = content.replace(tagReg, `>$1{{ t('${dir}.${key}') }}$2<`)
                content = content.replace(jsReg, `this.t('${dir}.${key}')`)
              })

              fs.writeFile(filedir, content, function (err) {
                if (err) {
                  throw err;
                }
                // eslint-disable-next-line no-console
              })
            }
            if (isDir) {
              replacefnT(filedir);
            }
          }
        })
      });
    }
  });
}

replacefnT(json.dirPath);
