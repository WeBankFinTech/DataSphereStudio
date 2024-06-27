import { resolve } from 'path';
import vue from '@vitejs/plugin-vue';
import { ConfigEnv, defineConfig, loadEnv, UserConfig } from 'vite';
import host from './vite-plugin/vite-host';
import eslintPlugin from 'vite-plugin-eslint';
import virtualModule from './vite-plugin/vite-dynamic-module';

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }: ConfigEnv): UserConfig => {
  // 根据当前工作目录中的 `mode` 加载 .env 文件
  // 设置第三个参数为 '' 来加载所有环境变量，而不管是否有 `VITE_` 前缀。
  const env = loadEnv(mode, process.cwd(), '');
  console.log(env, command, mode);
  return {
    define: {},
    plugins: [host(), vue(), virtualModule(), eslintPlugin()],
    resolve: {
      alias: {
        '@': resolve(__dirname, './'),
        '@dataspherestudio': resolve(__dirname, './packages'),
      },
    },
    css: {
      preprocessorOptions: {
        less: {
          javascriptEnabled: true,
        },
      },
    },
    server: {
      https: false,
      proxy: {
        '/api': {
          target: 'http://***REMOVED***:8088',
          // target: ***REMOVED***
          // target: ***REMOVED***
          secure: false,
          changeOrigin: true,
          followRedirects: true,
          headers: {
            cookie: '',
            // cookie:
            // 'linkis_user_session_ticket_id_v1=FmzyJmP2DGUzEo5Hy/D0OqyinoWZ4+L52vOV55MqIl4=; workspaceId=104; workspaceName=bdapWorkspace_move',
            TokenCode: 'admin-kmsnd',
            TokenUser: 'stacyyan',
            TokenAlive: 'true',
          },
        },
      },
    },
    base: './',
    build: { target: 'chrome66' },
  };
});
