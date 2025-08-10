<script setup lang="ts">
import type { CoverGenerateRecord } from '../types'
import { VStatusDot } from '@halo-dev/components'

defineProps<{
  record: CoverGenerateRecord
}>()

const statusMap = {
  PROCESSING: {
    label: '处理中',
    color: 'yellow',
    animate: true,
  },
  SUCCESS: {
    label: '成功',
    color: 'green',
    animate: false,
  },
  FAILED: {
    label: '失败',
    color: 'red',
    animate: false,
  },
}

function getRecordStatus(status: 'PROCESSING' | 'SUCCESS' | 'FAILED') {
  return statusMap[status]
}
</script>

<template>
  <tr class="hover:bg-gray-50 dark:hover:bg-gray-600">
    <td v-tooltip="record.spec.llmPrompt" class="max-w-sm truncate px-6 py-4 text-sm text-gray-900 dark:text-white">
      {{ record.spec.llmPrompt }}
    </td>
    <td v-tooltip="record.spec.t2iPrompt" class="max-w-sm truncate px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
      {{ record.spec.t2iPrompt }}
    </td>
    <td class="whitespace-nowrap px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
      {{ `${record.spec.llmProvider} (${record.spec.llmModelId})` }}
    </td>
    <td class="whitespace-nowrap px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
      {{ `${record.spec.t2iProvider} (${record.spec.t2iModelId})` }}
    </td>
    <td class="whitespace-nowrap px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
      <VStatusDot
        :status="getRecordStatus(record.spec.status).color"
        :label="getRecordStatus(record.spec.status).label"
        :animate="getRecordStatus(record.spec.status).animate"
      />
    </td>
    <td class="whitespace-nowrap px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
      {{ record.metadata.creationTimestamp }}
    </td>
  </tr>
</template>
