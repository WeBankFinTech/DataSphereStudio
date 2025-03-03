import devip from 'dev-ip';
import { Plugin } from 'vite';

export default function vitePluginHost(): Plugin {
  return {
    name: 'vite-plugin-host',
    apply: 'serve',
    config: () => ({
      server: {
        host: '0.0.0.0',
        hmr: {
          host: devip()[0],
          port: 5173,
        },
      },
    }),
    configureServer(server) {
      server.middlewares.use((req, _, next) => {
        // @ts-ignore
        req.url = req._parsedUrl.path;
        next();
      });
    },
  };
}
