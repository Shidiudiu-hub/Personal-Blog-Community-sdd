import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import process from 'node:process'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Inspect from 'vite-plugin-inspect'
// import vueDevTools from 'vite-plugin-vue-devtools'

export default (mode) => {
  return defineConfig({
    base: '/',
    server: {
      open: true,
      proxy: {
        '/admin-api': {
          target: 'https://bs251102-api.tulan.wang/', // 生产环境
          // target: 'http://192.168.3.69:8888', // 生产环境
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/admin-api/, '')
        }
      }
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    esbuild: {
      drop:
        loadEnv(mode, process.cwd()).VITE_NODE_ENV === 'production'
          ? ['console', 'debugger']
          : []
    },
    plugins: [
      vue(),
      // vueDevTools(),
      Inspect(),
      createSvgIconsPlugin({
        // 指定需要缓存的图标文件夹
        iconDirs: [fileURLToPath(new URL('./src/icons', import.meta.url))],
        // 指定symbolId格式
        symbolId: 'icon-[dir]-[name]'

        /**
         * 自定义插入位置
         * @default: body-last
         */
        // inject?: 'body-last' | 'body-first'

        /**
         * custom dom id
         * @default: __svg__icons__dom__
         */
        // customDomId: '__svg__icons__dom__',
      }),
      AutoImport({
        resolvers: [ElementPlusResolver()]
      }),
      Components({
        resolvers: [ElementPlusResolver()]
      })
    ],
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler' // or 'modern'
        }
      }
    }
  })
}
