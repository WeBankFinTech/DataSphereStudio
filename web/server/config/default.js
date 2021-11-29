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
      HOST: '127.0.0.1'
    }
  },
  test: {
    database: {
      DATABASE: 'dss_test',
      USERNAME: 'root',
      PASSWORD: '123456',
      PORT: '3306',
      HOST: '127.0.0.1'
    }
  },
  prd: {
    database: {
      DATABASE: 'dss_test',
      USERNAME: 'root',
      PASSWORD: '123456',
      PORT: '3306',
      HOST: '127.0.0.1'
    }
  }
}

module.exports = config
