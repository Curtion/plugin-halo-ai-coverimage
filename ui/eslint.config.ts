import antfu from '@antfu/eslint-config'
import unocss from '@unocss/eslint-config/flat'

export default antfu(
  {
    typescript: true,
    vue: true,
    ignores: [],
  },
  unocss,
  {
    rules: {
      'no-console': ['off'], // 允许使用console
      'style/brace-style': ['error', '1tbs'], // 大括号风格
      'curly': ['error', 'all'], // 控制语句后必须有{}
    },
  },
)
