<script setup lang="ts">
import type { Ref } from 'vue'
import type { CoverGenerateRecord } from '../types'

import { IconMore, VDropdown, VDropdownDivider, VDropdownItem, VEntity, VEntityField, VSpace, VStatusDot } from '@halo-dev/components'
import dayjs from 'dayjs'
import { computed, inject, ref } from 'vue'

const props = defineProps<{
  record: CoverGenerateRecord
  isSelected: boolean
}>()

const emit = defineEmits<{
  (e: 'delete', record: CoverGenerateRecord): void
  (e: 'detail', record: CoverGenerateRecord): void
}>()

const selectedRecordNames = inject<Ref<string[]>>('selectedRecordNames', ref([]))

const checked = computed({
  get() {
    return props.isSelected || selectedRecordNames.value.includes(props.record.metadata.name)
  },
  set(value: boolean) {
    if (value) {
      selectedRecordNames.value = [...selectedRecordNames.value, props.record.metadata.name]
    } else {
      selectedRecordNames.value = selectedRecordNames.value.filter(
        name => name !== props.record.metadata.name,
      )
    }
  },
})

interface StatusProperty {
  label: string
  state: 'success' | 'warning' | 'error'
  animate: boolean
}

const statusMap: Record<'PROCESSING' | 'SUCCESS' | 'FAILED', StatusProperty> = {
  PROCESSING: {
    label: '处理中',
    state: 'warning',
    animate: true,
  },
  SUCCESS: {
    label: '成功',
    state: 'success',
    animate: false,
  },
  FAILED: {
    label: '失败',
    state: 'error',
    animate: false,
  },
}

function getRecordStatus(status: 'PROCESSING' | 'SUCCESS' | 'FAILED'): StatusProperty {
  return statusMap[status]
}
</script>

<template>
  <VEntity :is-selected="isSelected">
    <template #checkbox>
      <input v-model="checked" type="checkbox">
    </template>
    <template #start>
      <VEntityField
        :title="record.spec.postTitle"
        :description="dayjs(record.metadata.creationTimestamp).format('YYYY-MM-DD HH:mm:ss')"
      >
        <template #extra>
          <VSpace class="mt-1 sm:mt-0">
            <VStatusDot
              :state="getRecordStatus(record.spec.status).state"
              :label="getRecordStatus(record.spec.status).label"
              :animate="getRecordStatus(record.spec.status).animate"
            />
          </VSpace>
        </template>
      </VEntityField>
    </template>
    <template #end>
      <VEntityField>
        <template #description>
          <VDropdown>
            <div
              class="h-full flex cursor-pointer items-center px-4 transition-all hover:text-blue-600"
            >
              <IconMore class="h-4 w-4" />
            </div>
            <template #popper>
              <VDropdownItem @click="emit('detail', record)">
                查看详情
              </VDropdownItem>
              <VDropdownDivider />
              <VDropdownItem type="danger" @click="emit('delete', record)">
                删除
              </VDropdownItem>
            </template>
          </VDropdown>
        </template>
      </VEntityField>
    </template>
  </VEntity>
</template>
