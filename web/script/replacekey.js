/* eslint-disable require-jsdoc */
var fs = require('fs');
var path = require('path');

var dir = process.argv[2];

var dirPath = path.resolve(dir);

var json = JSON.parse(fs.readFileSync(dirPath, 'utf-8'));

function replaceKey(filePath) {
  fs.readdir(filePath, function (err, files) {
    if (err) {
      console.warn(err)
    } else {
      files.forEach(function (filename) {
        var filedir = path.join(filePath, filename);
        if ( filedir.indexOf('vue-process-demo') < 0 && filedir.indexOf('i18n') < 0 ) {
          fs.stat(filedir, function (eror, stats) {
            if (eror) {
              console.warn('获取文件stats失败');
            } else {
              var isFile = stats.isFile();
              var isDir = stats.isDirectory();
              if (isFile) {
                var content = fs.readFileSync(filedir, 'utf-8');
                var values = Object.values(json.found);
                var keys = Object.keys(json.found)
                values = values.sort((a, b) => b.length - a.length);

                values.forEach((it) => {
                  var key = keys.find(k => json.found[k] == it)
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
                  content = content.replace(attrReg, ` :$1="$t('message.exts.bdp.${key}')"`)
                  content = content.replace(tagReg, `>$1{{ $t('message.exts.bdp.${key}') }}$2<`)
                  content = content.replace(jsReg, `this.$t('message.exts.bdp.${key}')`)
                })

                fs.writeFile(filedir, content, function (err) {
                  if (err) {
                    throw err;
                  }
                  // eslint-disable-next-line no-console
                })
              }
              if (isDir) {
                replaceKey(filedir);
              }
            }
          })
        }
      });
    }
  });
}

replaceKey(json.dirPath);

// 修改翻译json命名key值、修改本文件替换路径message.xxx后执行：
// node ./script/replaceKey.js ./cn.json
