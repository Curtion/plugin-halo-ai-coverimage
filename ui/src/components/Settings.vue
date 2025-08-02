<script setup lang="ts">
import { VButton } from '@halo-dev/components'
import { ref } from 'vue'

const schema = ref([
  {
    $formkit: 'group',
    name: 'provider1',
    label: '提供商设置',
    children: [
      {
        $formkit: 'select',
        name: 'engine',
        label: '生图提供商',
        options: [
          { label: '硅基流动', value: 'siliconflow' },
        ],
      },
      {
        $formkit: 'text',
        name: 'apiKey',
        label: 'API Key',
        placeholder: '请输入你的 API Key',
        help: '请前往 OpenAI 获取你的 API Key。',
      },
    ],
  },
  {
    $formkit: 'group',
    name: 'provider2',
    label: '备用提供商设置',
    children: [
      {
        $formkit: 'select',
        name: 'engine2',
        label: '备用生图提供商',
        options: [
          { label: 'OpenAI', value: 'openai' },
          { label: 'Stable Diffusion', value: 'stablediffusion' },
        ],
      },
      {
        $formkit: 'text',
        name: 'apiKey2',
        label: '备用 API Key',
        placeholder: '请输入你的备用 API Key',
        help: '请输入备用提供商的 API Key。',
      },
    ],
  },
])

const data = ref({
  engine: 'siliconflow',
  engine2: 'openai',
})

async function handleSubmit() {
  console.log('表单已提交', data)
  await new Promise(r => setTimeout(r, 1000))
}
</script>

<template>
  <FormKit v-model="data" type="form" :actions="false">
    <FormKitSchema :schema="schema" />
    <VButton type="secondary" @click="handleSubmit">
      保存
    </VButton>
  </FormKit>
</template>

<style lang="scss" scoped>

</style>
