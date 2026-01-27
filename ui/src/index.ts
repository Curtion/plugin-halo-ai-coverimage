import { definePlugin } from '@halo-dev/ui-shared'
import { markRaw } from 'vue'
import IconWrench from '~icons/iconoir/wrench'
import HomeView from './views/HomeView.vue'
import 'virtual:uno.css'

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: 'Root',
      route: {
        path: '/ai-coverimage',
        name: 'ai-coverimage',
        component: HomeView,
        meta: {
          title: 'AI封面图-生成记录',
          searchable: true,
          menu: {
            name: '生成记录',
            group: 'AI封面图',
            icon: markRaw(IconWrench),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
})
