import { createApp } from 'vue'
import 'vue-cropper/dist/index.css'
import 'normalize.css' // a modern alternative to CSS resets

import App from '@/App'
import { createPinia } from 'pinia' // pinia
import router from '@/router/index'
import 'element-plus/dist/index.css'
import 'virtual:svg-icons-register'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import ElementPlus from 'element-plus'
import VueCropper from 'vue-cropper'
import '@/permission' // permission control

import directives from '@/directives/index'
import autoUpdate from '@/utils/auto-update'
const app = createApp(App)
app
  .use(createPinia())
  .use(router)
  .use(directives)
  .use(autoUpdate)
  .component('Plus', ElementPlusIconsVue.Plus)
  .use(ElementPlus, {
    locale: zhCn
  })
  .use(VueCropper)
  .mount('#app')
