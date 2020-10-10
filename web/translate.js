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
var http = require('http');
var jsonPath = './cn.json';
var jsonStr = fs.readFileSync(jsonPath, 'utf-8');
var jsonValue = JSON.parse(jsonStr);
// http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=%E8%AE%A1%E7%AE%97

// {
//     type: "ZH_CN2EN",
//     errorCode: 0,
//     elapsedTime: 1,
//     translateResult: [
//         [
//             {
//             src: "计算",
//             tgt: "To calculate"
//             }
//         ]
//     ]
// }

var url = 'https://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=';

function translateReq(text, cb) {
  http.get(`${url}${encodeURIComponent(text)}`, {
    headers: {
      'host': 'fanyi.youdao.com',
      'pragma': 'no-cache',
      'proxy-Connection': 'keep-alive',
      'upgrade-insecure-requests': 1,
      'cookie': 'OUTFOX_SEARCH_USER_ID=-1618593213@127.0.0.1; OUTFOX_SEARCH_USER_ID_NCOO=1383392327.7494588; _ga=GA1.2.1485522253.1539140851; P_INFO=15109269782|1545711281|1|youdaonote|00&99|null&null&null#gud&440300#10#0|&0||15109269782; SESSION_FROM_COOKIE=www.baidu.com',
      'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36'
    }
  },
  function (res) {
    var str = '';
    res.on('data', (data) => {
      str += data;
    })
    res.on('end', () => {
      var json = JSON.parse(str)
      cb(json.translateResult[0] || [])
    })
  })
    .on('error', function () {
      cb()
    })
}
var json = {
  ...jsonValue.found
};
var start = 0;
var keys = Object.keys(json);

function translate(start) {
  if (start < keys.length) {
    translateReq(json[keys[start]], function (v) {
      if (v[0]) {
        json[keys[start]] = v[0].tgt;
      }
      console.log(keys[start], 'done');
      start = start + 1;
      translate(start);
    })
  } else {
    var translateJson = {}
    Object.values(json).forEach((v, index) => {
      translateJson[v] = keys[index]
    })

    var jsonResult = {
      ...jsonValue,
      translate: translateJson
    }

    fs.writeFile(jsonPath, JSON.stringify(jsonResult), function (err) {
      if (err) {
        throw err;
      }
      console.log('done.');
    })
  }
}

translate(start);
