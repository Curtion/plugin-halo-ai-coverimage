<script setup lang="ts">
import { VAlert, VButton } from '@halo-dev/components'
import { ref } from 'vue'

const scopeOptions = [
  { label: '全部文章', value: 'all' },
  { label: '无封面文章', value: 'no-cover' },
]

const scope = ref('all')

function handleSubmit() {
  console.log('Form submitted with scope:', scope.value)
}
</script>

<template>
  <VAlert title="注意事项" type="info" :closable="false">
    <template #description>
      <ul class="ml-2 list-disc list-inside space-y-1">
        <li>操作前请先配置API相关参数。</li>
        <li>该功能会覆盖文章原始封面, 操作前建议备份。</li>
        <li>一键生成会为所选的文章范围自动生成封面图。</li>
      </ul>
    </template>
  </VAlert>
  <div class="mt-4">
    <FormKit type="form">
      <FormKit
        v-model="scope"
        type="select"
        label="文章范围"
        name="scope"
        :options="scopeOptions"
      />
      <div class="mt-4">
        <VButton type="secondary" @click="handleSubmit">
          一键生成
        </VButton>
      </div>
    </FormKit>
  </div>
</template>

<style lang="scss" scoped>

</style>
