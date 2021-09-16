const config = {
  // 启动端口
  port: 3022,

  // 数据库配置
  dev: {
    database: {
      DATABASE: 'dss_test',
      USERNAME: 'root',
      PASSWORD: '123456',
      PORT: '3306',
      HOST: '***REMOVED***'
    }
  },
  test: {
    database: {
      DATABASE: 'dss_test',
      USERNAME: 'root',
      PASSWORD: '123456',
      PORT: '3306',
      HOST: '***REMOVED***'
    }
  },
  prd: {
    database: {
      DATABASE: 'dss_test',
      USERNAME: 'root',
      PASSWORD: '123456',
      PORT: '3306',
      HOST: '***REMOVED***'
    }
  }
}

module.exports = config
