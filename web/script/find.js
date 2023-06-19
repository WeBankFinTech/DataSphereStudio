/* eslint-disable require-jsdoc */
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

//  node ./script/find.js ./packages/exts/bdp
