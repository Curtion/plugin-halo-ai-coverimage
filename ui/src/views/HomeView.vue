<script setup lang="ts">
import { VCard, VPageHeader } from '@halo-dev/components'
import IconWrench from '~icons/iconoir/wrench'
import Quick from '@/components/Quick.vue'
</script>

<template>
  <VPageHeader title="AI封面图">
    <template #icon>
      <IconWrench />
    </template>
  </VPageHeader>
  <div class="m-0 md:m-4">
    <VCard>
      <Quick />
    </VCard>
  </div>
</template>

<style lang="scss" scoped></style>
