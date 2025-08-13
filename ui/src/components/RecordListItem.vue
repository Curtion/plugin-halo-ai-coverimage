<script setup lang="ts">
import type { CoverGenerateRecord } from '../types'
import { VButton, VStatusDot } from '@halo-dev/components'
import dayjs from 'dayjs'

defineProps<{
  record: CoverGenerateRecord
}>()

const emit = defineEmits<{
  (e: 'delete', record: CoverGenerateRecord): void
}>()

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
  <tr class="hover:bg-gray-50 dark:hover:bg-gray-600">
    <td v-tooltip="record.spec.t2iPrompt" class="max-w-sm truncate px-6 py-4 text-center text-sm text-gray-500 dark:text-gray-300">
      {{ record.spec.t2iPrompt }}
    </td>
    <td class="text-center text-sm text-gray-500 dark:text-gray-300">
      {{ `${record.spec.llmProvider} (${record.spec.llmModelId})` }}
    </td>
    <td class="text-center text-sm text-gray-500 dark:text-gray-300">
      {{ `${record.spec.t2iProvider} (${record.spec.t2iModelId})` }}
    </td>
    <td class="text-center text-sm text-gray-500 dark:text-gray-300">
      <div class="flex justify-center">
        <VStatusDot
          :state="getRecordStatus(record.spec.status).state"
          :label="getRecordStatus(record.spec.status).label"
          :animate="getRecordStatus(record.spec.status).animate"
        />
      </div>
    </td>
    <td class="text-center text-sm text-gray-500 dark:text-gray-300">
      {{ record.spec.result }}
    </td>
    <td class="text-center text-sm text-gray-500 dark:text-gray-300">
      {{ dayjs(record.metadata.creationTimestamp).format('YYYY-MM-DD HH:mm:ss') }}
    </td>
    <td class="whitespace-nowrap px-6 py-4 text-center text-sm">
      <VButton type="danger" size="sm" @click="emit('delete', record)">
        删除
      </VButton>
    </td>
  </tr>
</template>
