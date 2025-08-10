<script setup lang="ts">
import { VCard, VPageHeader } from '@halo-dev/components'
import IconWrench from '~icons/iconoir/wrench'
</script>

<template>
  <VPageHeader title="生成记录">
    <template #icon>
      <IconWrench />
    </template>
  </VPageHeader>
  <div class="m-0 md:m-4">
    <VCard>
      123
    </VCard>
  </div>
</template>

<style lang="scss" scoped></style>
