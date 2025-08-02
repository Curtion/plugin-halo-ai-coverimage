<script setup lang="ts">
import { VCard, VPageHeader, VTabbar } from '@halo-dev/components'
import { ref } from 'vue'
import IconWrench from '~icons/iconoir/wrench'
import Quick from '@/components/Quick.vue'
import Settings from '@/components/Settings.vue'

const tabs = [
  { id: 'quick', label: '一键生成' },
  { id: 'setting', label: '设置' },
] as const
const activeTab = ref<'quick' | 'setting'>(tabs[0].id)

function handleTabChange(newTabId: string | number) {
  activeTab.value = String(newTabId) as 'quick' | 'setting'
}
</script>

<template>
  <VPageHeader title="AI封面图">
    <template #icon>
      <IconWrench />
    </template>
  </VPageHeader>
  <div class="m-0 md:m-4">
    <VCard>
      <template #header>
        <VTabbar
          v-model:active-id="activeTab"
          :items="tabs.map((item) => ({ id: item.id, label: item.label }))"
          class="w-full !rounded-none"
          type="outline"
          @change="handleTabChange"
        />
      </template>
      <div class="bg-white">
        <Quick v-if="activeTab === 'quick'" />
        <Settings v-if="activeTab === 'setting'" />
      </div>
    </VCard>
  </div>
</template>

<style lang="scss" scoped></style>
