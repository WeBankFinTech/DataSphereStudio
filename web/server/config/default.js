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
      HOST: '172.24.2.61'
    }
  },
  test: {
    database: {
      DATABASE: 'dss_test',
      USERNAME: 'root',
      PASSWORD: '123456',
      PORT: '3306',
      HOST: '172.24.2.61'
    }
  },
  prd: {
    database: {
      DATABASE: 'dss_test',
      USERNAME: 'root',
      PASSWORD: '123456',
      PORT: '3306',
      HOST: '172.24.2.61'
    }
  }
}

module.exports = config
