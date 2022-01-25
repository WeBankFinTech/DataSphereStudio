var mysql = require("mysql");
var config = require("../config/default.js");

var env = process.env.NODE_ENV;
env = env ? env : "dev";

var pool = mysql.createPool({
  host: config[env].database.HOST,
  user: config[env].database.USERNAME,
  password: config[env].database.PASSWORD,
  database: config[env].database.DATABASE,
  port: config[env].database.PORT
});

class Mysql {
  constructor() {}
  queryMenu() {
    return new Promise((resolve, reject) => {
      pool.query("SELECT * from dss_onestop_menu a ORDER BY a.id;", function(
        error,
        results,
        fields
      ) {
        if (error) {
          throw error;
        }
        resolve(results);
      });
    });
  }
  queryAll() {
    return new Promise((resolve, reject) => {
      pool.query(
        "SELECT * from dss_onestop_menu_application b RIGHT JOIN dss_application a ON a.id = b.application_id ORDER BY a.id;",
        function(error, results, fields) {
          if (error) {
            throw error;
          }
          resolve(results);
        }
      );
    });
  }
  query(id) {
    return new Promise((resolve, reject) => {
      pool.query(
        `SELECT * from dss_onestop_menu_application b RIGHT JOIN dss_application a ON a.id = b.application_id WHERE a.id=${id};`,
        function(error, results, fields) {
          if (error) {
            throw error;
          }
          resolve(results);
        }
      );
    });
  }
  createApplication(data) {
    return new Promise((resolve, reject) => {
      if (
        !data.title_en ||
        !data.title_cn ||
        !data.url ||
        !data.onestop_menu_id ||
        !data.project_url ||
        !data.homepage_url
      ) {
        throw new Error("必填字段缺损");
      }
      if (!data.id) {
        let sql = `INSERT INTO dss_application(id,name,url,project_url,if_iframe,homepage_url,redirect_url) VALUES(0,?,?,?,?,?,?)`;
        let params = [
          data.title_en,
          data.url,
          data.project_url,
          data.if_iframe,
          data.homepage_url,
          data.redirect_url
        ];
        pool.query(sql, params, function(error, result) {
          if (error) {
            throw error;
          }
          if (result && result.insertId) {
            let sql2 = `INSERT INTO dss_onestop_menu_application(id,application_id,onestop_menu_id,title_en,title_cn,desc_en,desc_cn,labels_en,labels_cn,is_active,access_button_en,access_button_cn) VALUES(0,?,?,?,?,?,?,?,?,?,?,?)`;
            let params2 = [
              result.insertId,
              data.onestop_menu_id,
              data.title_en,
              data.title_cn,
              data.desc_en,
              data.desc_cn,
              data.labels_en,
              data.labels_cn,
              data.is_active,
              data.access_button_en || `Enter ${data.title_en}`,
              data.access_button_cn || `进入 ${data.title_cn}`
            ];
            pool.query(sql2, params2, function(error2, result2) {
              if (error2) {
                throw error2;
              }
              resolve(result2);
            });
          }
        });
      } else {
        let sql = `UPDATE dss_application SET name=?,url=?,project_url=?,if_iframe=?,homepage_url=?,redirect_url=? WHERE id=?`;
        let params = [
          data.title_en,
          data.url,
          data.project_url,
          data.if_iframe,
          data.homepage_url,
          data.redirect_url,
          data.id
        ];
        pool.query(sql, params, function(error, result) {
          if (error) {
            throw error;
          }
          let sql2 = `UPDATE dss_onestop_menu_application SET onestop_menu_id=?,title_en=?,title_cn=?,desc_en=?,desc_cn=?,labels_en=?,labels_cn=?,is_active=?,access_button_en=?,access_button_cn=? WHERE application_id=?`;
          let params2 = [
            data.onestop_menu_id,
            data.title_en,
            data.title_cn,
            data.desc_en,
            data.desc_cn,
            data.labels_en,
            data.labels_cn,
            data.is_active,
            data.access_button_en || `Enter ${data.title_en}`,
            data.access_button_cn || `进入 ${data.title_cn}`,
            data.id
          ];
          pool.query(sql2, params2, function(error2, result2) {
            if (error2) {
              throw error2;
            }
            resolve(result2);
          });
        });
      }
    });
  }
}

module.exports = new Mysql();
