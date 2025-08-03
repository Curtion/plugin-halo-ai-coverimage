<script setup lang="ts">
import { VAlert, VButton } from '@halo-dev/components'
import { ref } from 'vue'

const schema = [
  {
    $formkit: 'group',
    name: 'llmProvider',
    label: 'LLM提供商设置',
    children: [
      {
        $formkit: 'select',
        name: 'engine',
        label: '服务提供商',
        options: [
          { label: '硅基流动', value: 'siliconflow' },
        ],
      },
      {
        $formkit: 'text',
        name: 'model',
        label: '模型',
        placeholder: '请输入模型ID',
      },
      {
        $formkit: 'text',
        name: 'apiKey',
        label: 'API Key',
        placeholder: '请输入你的 API Key',
      },
      {
        $formkit: 'textarea',
        name: 'prompt',
        label: '提示词',
        placeholder: '请输入提示词',
        rows: 10,
      },
    ],
  },
  {
    $formkit: 'group',
    name: 't2iProvider',
    label: '文生图提供商设置',
    children: [
      {
        $formkit: 'select',
        name: 'engine',
        label: '服务提供商',
        options: [
          { label: '硅基流动', value: 'siliconflow' },
        ],
      },
      {
        $formkit: 'text',
        name: 'model',
        label: '模型',
        placeholder: '请输入模型ID',
      },
      {
        $formkit: 'text',
        name: 'apiKey',
        label: 'API Key',
        placeholder: '请输入你的 API Key',
      },
    ],
  },
]

const data = ref({
  llmProvider: {
    engine: 'siliconflow',
    model: 'Qwen/Qwen3-235B-A22B-Instruct-2507',
    apiKey: '',
    prompt: `# 角色
你是一位经验丰富的创意视觉总监和AI绘画提示词工程师。你的任务是分析给定的文章信息，将其核心思想和情感基调，转化为一个高度具体、富有画面感、且结构优良的AI绘画提示词（Prompt），用于生成文章的封面图。

# 任务
根据下方提供的文章【标题】、【分类】、【标签】和【内容摘要】，遵循【工作流程与规则】，生成一段可以直接用于AI绘画的、高质量的英文或中文提示词。

# 工作流程与规则
深度分析输入信息：

标题 (Title): 提取最核心的【画面主体】。
内容摘要 (Content): 寻找最具画面感的场景、关键物品、动作或比喻，作为【场景/背景】和【附加细节】的灵感。
分类 (Category): 决定画面的整体【氛围/风格】基调。例如，“科技”类对应未来感、赛博朋克；“生活”类对应温馨、自然。
标签 (Tags): 将标签词转化为具体的视觉元素或风格关键词，融入提示词。
结构化构建提示词： 你必须在脑中按照以下六个维度来构思，但最终输出时要将它们融合成一个自然的段落。

1. 画面主体 (Subject): 画面的核心是什么？必须清晰、具体。
2. 场景/背景 (Scene/Background): 主体在什么样的环境中？
3. 氛围/风格 (Atmosphere/Style): 图片的艺术风格和情感基调是什么？（例如：赛博朋克、水彩、吉卜力风格、超现实主义、扁平插画、照片级真实感）。
4. 构图/视角 (Composition/Viewpoint): 画面如何布局？（例如：特写、广角、对称构图、电影感镜头）。
5. 色彩/光线 (Color/Lighting): 主要色调和光照效果是怎样的？（例如：柔和色调、霓虹灯光、黄昏的金色光芒、戏剧性光影）。
6. 附加细节与质量词 (Details & Quality): 加入能提升画面丰富度和质量的词语（例如：细节丰富, 杰作, 最佳质量, 4K, 8K, 电影感）。
输出要求：

最终输出必须是一个连贯的段落，而不是分点的列表。
提示词应尽可能具体，避免模糊不清的描述。
在适当的地方加入从【标签】中提取的关键词。
在结尾处加上通用的画质提升词，如 杰作, 最佳质量, 细节丰富, 4K。`,
  },
  t2iProvider: {
    engine: 'siliconflow',
    model: 'Kwai-Kolors/Kolors',
    apiKey: '',
  },
})

async function handleSubmit() {
  console.log('表单已提交', data.value)
  await new Promise(r => setTimeout(r, 1000))
}
</script>

<template>
  <VAlert title="解释说明" type="info" :closable="false">
    <template #description>
      <ul class="ml-2 list-disc list-inside space-y-1">
        <li>LLM: 分析文章内容, 生成文生图提示词</li>
        <li>文生图: 根据提示词生成文章封面图</li>
      </ul>
    </template>
  </VAlert>
  <div class="mt-4">
    <FormKit v-model="data" type="form" :actions="false">
      <FormKitSchema :schema="schema" />
      <VButton type="secondary" @click="handleSubmit">
        保存
      </VButton>
    </FormKit>
  </div>
</template>

<style lang="scss" scoped>

</style>
