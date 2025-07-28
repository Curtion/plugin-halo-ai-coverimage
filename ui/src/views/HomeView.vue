<script setup lang="ts">
import { VPageHeader, VCard, VTabbar } from "@halo-dev/components";
import { ref } from "vue";
import IconWrench from "~icons/iconoir/wrench";

const tabs = [
  { id: 'tab1', label: 'Tab 1' },
  { id: 'tab2', label: 'Tab 2' },
];
const activeTab = ref(tabs[0].id);

function handleTabChange(newTabId: string|number) {
  console.log('Active tab changed to:', newTabId);
  activeTab.value = String(newTabId);
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
        <VTabbar v-model:active-id="activeTab" :items="tabs.map((item) => ({ id: item.id, label: item.label }))"
          class="w-full !rounded-none" type="outline" @change="handleTabChange"></VTabbar>
      </template>
      <div class="rounded-b-base bg-white">
        123
      </div>
    </VCard>
  </div>
</template>

<style lang="scss" scoped></style>
