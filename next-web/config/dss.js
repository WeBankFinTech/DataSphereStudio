export default {
  apps: {
    workspace: {
      routes: 'workspace/router',
      module: 'workspace/module',
      i18n: {
        en: 'workspace/i18n/en.json',
        'zh-CN': 'workspace/i18n/zh.json',
      },
    },
  },
  exts: {},
  conf: {
    app_name: 'DataSphere Studio',
    // app_logo: 'src/assets/images/dssLogo.png',
    user_guide: '',
    // hide_view_tb_detail: true,
    // hide_view_db_detail: true,
    watermark: {
      show: false,
      template: '${username} ${time}',
      timeupdate: 60000,
    },
    // lsp_service: {
    //   sql: "${protocol}//${host}/server",
    //   py: "${protocol}//${host}/python",
    // },
  },
  version: '1.1.12',
};
