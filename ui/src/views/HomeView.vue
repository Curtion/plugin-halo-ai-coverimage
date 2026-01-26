<script lang="ts" setup>
import type { Ref } from 'vue'
import type { CoverGenerateRecord, CoverGenerateRecordList } from '../types'
import {
  IconRefreshLine,
  Toast,
  VButton,
  VCard,
  VDialog,
  VEmpty,
  VEntityContainer,
  VLoading,
  VModal,
  VPageHeader,
  VPagination,
  VSpace,
  VStatusDot,
} from '@halo-dev/components'
import { useQuery } from '@tanstack/vue-query'
import { useRouteQuery } from '@vueuse/router'
import axios from 'axios'
import dayjs from 'dayjs'
import { computed, provide, ref, watch } from 'vue'
import IconWrench from '~icons/iconoir/wrench'
import RecordListItem from '../components/RecordListItem.vue'

const selectedRecordNames = ref<string[]>([])
const checkedAll = ref(false)

provide<Ref<string[]>>('selectedRecordNames', selectedRecordNames)

const page = useRouteQuery<number>('page', 1, {
  transform: Number,
})
const size = useRouteQuery<number>('size', 20, {
  transform: Number,
})
const selectedStatus = useRouteQuery<string | undefined>('status')
const selectedSort = useRouteQuery<string | undefined>('sort')

const http = axios.create({
  baseURL: '/',
})

watch(
  () => [selectedStatus.value, selectedSort.value],
  () => {
    page.value = 1
  },
)

const hasFilters = computed(() => {
  return selectedStatus.value !== undefined || selectedSort.value !== undefined
})

function handleClearFilters() {
  selectedStatus.value = undefined
  selectedSort.value = undefined
}

const {
  data: records,
  isLoading,
  isFetching,
  refetch,
} = useQuery({
  queryKey: ['cover-generate-records', page, size, selectedStatus, selectedSort],
  queryFn: async () => {
    const params: Record<string, any> = {
      page: page.value,
      size: size.value,
    }

    // 构建排序参数
    if (selectedSort.value) {
      params.sort = selectedSort.value
    } else {
      params.sort = 'metadata.creationTimestamp,desc'
    }

    if (selectedStatus.value) {
      params.fieldSelector = `spec.status=${selectedStatus.value}`
    }

    const { data } = await http.get<CoverGenerateRecordList>('/apis/io.github.curtion/v1alpha1/covergeneraterecords', {
      params,
    })
    return data
  },
  refetchInterval: (query) => {
    const data = query.state.data as CoverGenerateRecordList | undefined
    if (data?.items.some(item => item.metadata.deletionTimestamp)) {
      return 1000
    }
    return false
  },
})

watch(
  () => records.value,
  (data) => {
    if (data) {
      page.value = data.page
      size.value = data.size
    }
  },
)

const deleteDialog = ref({
  visible: false,
  record: null as CoverGenerateRecord | null,
  index: -1,
  isBatch: false,
})

const detailDialog = ref({
  visible: false,
  record: null as CoverGenerateRecord | null,
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

function handleDeleteItem(record: CoverGenerateRecord, index: number) {
  deleteDialog.value.visible = true
  deleteDialog.value.record = record
  deleteDialog.value.index = index
  deleteDialog.value.isBatch = false
}

function handleDetailItem(record: CoverGenerateRecord) {
  detailDialog.value.visible = true
  detailDialog.value.record = record
}

async function onConfirmDelete() {
  try {
    const itemsToDelete = deleteDialog.value.isBatch
      ? selectedRecordNames.value
      : [deleteDialog.value.record?.metadata.name].filter(Boolean) as string[]

    const chunks: string[][] = []
    for (let i = 0; i < itemsToDelete.length; i += 5) {
      chunks.push(itemsToDelete.slice(i, i + 5))
    }

    for (const chunk of chunks) {
      await Promise.all(
        chunk.map(name =>
          axios.delete(`/apis/io.github.curtion/v1alpha1/covergeneraterecords/${name}`),
        ),
      )
    }

    selectedRecordNames.value = []
    await refetch()

    Toast.success('删除成功')
  } catch (err) {
    Toast.error(`删除失败: ${String(err)}`)
  } finally {
    onCancelDelete()
  }
}

function onCancelDelete() {
  deleteDialog.value.visible = false
  deleteDialog.value.record = null
  deleteDialog.value.index = -1
  deleteDialog.value.isBatch = false
}

function onCancelDetail() {
  detailDialog.value.visible = false
  detailDialog.value.record = null
}

function checkSelection(record: CoverGenerateRecord) {
  return selectedRecordNames.value.includes(record.metadata.name)
}

function handleCheckAllChange(e: Event) {
  const { checked } = e.target as HTMLInputElement

  if (checked) {
    selectedRecordNames.value
      = records.value?.items.map((item) => {
        return item.metadata.name
      }) || []
  } else {
    selectedRecordNames.value = []
  }
}

function handleDeleteInBatch() {
  if (!selectedRecordNames.value.length) {
    return
  }
  deleteDialog.value.visible = true
  deleteDialog.value.isBatch = true
}

watch(selectedRecordNames, (newValue) => {
  checkedAll.value = newValue.length === records.value?.items.length
})
</script>

<template>
  <VPageHeader title="生成记录">
    <template #icon>
      <IconWrench />
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-0']">
      <template #header>
        <div class="block w-full bg-gray-50 px-4 py-3">
          <div
            class="relative flex flex-col flex-wrap items-start gap-4 sm:flex-row sm:items-center"
          >
            <div class="hidden items-center sm:flex">
              <input
                v-model="checkedAll"
                type="checkbox"
                @change="handleCheckAllChange"
              >
            </div>
            <div class="flex w-full flex-1 items-center sm:w-auto">
              <VSpace v-if="selectedRecordNames.length">
                <VButton type="danger" @click="handleDeleteInBatch">
                  删除
                </VButton>
              </VSpace>
            </div>
            <VSpace spacing="lg" class="flex-wrap">
              <FilterCleanButton
                v-if="hasFilters"
                @click="handleClearFilters"
              />
              <FilterDropdown
                v-model="selectedStatus"
                label="状态"
                :items="[
                  {
                    label: '全部',
                    value: undefined,
                  },
                  {
                    label: '处理中',
                    value: 'PROCESSING',
                  },
                  {
                    label: '成功',
                    value: 'SUCCESS',
                  },
                  {
                    label: '失败',
                    value: 'FAILED',
                  },
                ]"
              />
              <FilterDropdown
                v-model="selectedSort"
                label="排序"
                :items="[
                  {
                    label: '默认',
                    value: undefined,
                  },
                  {
                    label: '创建时间倒序',
                    value: 'metadata.creationTimestamp,desc',
                  },
                  {
                    label: '创建时间正序',
                    value: 'metadata.creationTimestamp,asc',
                  },
                ]"
              />
              <div class="flex flex-row gap-2">
                <div
                  class="group cursor-pointer rounded p-1 hover:bg-gray-200"
                  @click="refetch()"
                >
                  <IconRefreshLine
                    v-tooltip="'刷新'"
                    :class="{ 'animate-spin text-gray-900': isFetching }"
                    class="h-4 w-4 text-gray-600 group-hover:text-gray-900"
                  />
                </div>
              </div>
            </VSpace>
          </div>
        </div>
      </template>
      <VLoading v-if="isLoading" />
      <Transition v-else-if="!records?.items.length" appear name="fade">
        <VEmpty
          message="暂无生成记录"
          title="没有找到生成记录"
        >
          <template #actions>
            <VSpace>
              <VButton @click="refetch">
                刷新
              </VButton>
            </VSpace>
          </template>
        </VEmpty>
      </Transition>
      <Transition v-else appear name="fade">
        <VEntityContainer>
          <RecordListItem
            v-for="(record, index) in records?.items"
            :key="record.metadata.name"
            :record="record"
            :is-selected="checkSelection(record)"
            @delete="handleDeleteItem(record, index)"
            @detail="handleDetailItem"
          />
        </VEntityContainer>
      </Transition>

      <template #footer>
        <VPagination
          v-model:page="page"
          v-model:size="size"
          page-label="页码"
          size-label="每页条数"
          :total-label="`共 ${records?.total || 0} 条`"
          :total="records?.total || 0"
          :size-options="[20, 30, 50, 100]"
        />
      </template>
    </VCard>
  </div>
  <VDialog
    v-model:visible="deleteDialog.visible"
    :title="deleteDialog.isBatch ? '确定要删除选中的记录吗？' : '确定删除吗？'"
    :description="deleteDialog.isBatch ? `你确定要删除选中的 ${selectedRecordNames.length} 条记录吗？` : '你确定要删除这条记录吗？'"
    type="warning"
    confirm-type="danger"
    :on-cancel="onCancelDelete"
    :on-confirm="onConfirmDelete"
  />
  <VModal
    v-model:visible="detailDialog.visible"
    title="记录详情"
    :width="800"
    @close="onCancelDetail"
  >
    <div v-if="detailDialog.record" class="space-y-4">
      <div class="grid grid-cols-3 gap-2">
        <div class="text-gray-500 font-medium dark:text-gray-400">
          文章标题:
        </div>
        <div class="col-span-2 text-gray-900 dark:text-gray-100">
          {{ detailDialog.record.spec.postTitle }}
        </div>
      </div>
      <div class="grid grid-cols-3 gap-2">
        <div class="text-gray-500 font-medium dark:text-gray-400">
          T2I 提示词:
        </div>
        <div class="col-span-2 text-gray-900 dark:text-gray-100">
          {{ detailDialog.record.spec.t2iPrompt }}
        </div>
      </div>
      <div class="grid grid-cols-3 gap-2">
        <div class="text-gray-500 font-medium dark:text-gray-400">
          LLM 提供者:
        </div>
        <div class="col-span-2 text-gray-900 dark:text-gray-100">
          {{ `${detailDialog.record.spec.llmProvider} (${detailDialog.record.spec.llmModelId})` }}
        </div>
      </div>
      <div class="grid grid-cols-3 gap-2">
        <div class="text-gray-500 font-medium dark:text-gray-400">
          T2I 提供者:
        </div>
        <div class="col-span-2 text-gray-900 dark:text-gray-100">
          {{ `${detailDialog.record.spec.t2iProvider} (${detailDialog.record.spec.t2iModelId})` }}
        </div>
      </div>
      <div class="grid grid-cols-3 gap-2">
        <div class="text-gray-500 font-medium dark:text-gray-400">
          状态:
        </div>
        <div class="col-span-2">
          <VStatusDot
            :state="getRecordStatus(detailDialog.record.spec.status).state"
            :label="getRecordStatus(detailDialog.record.spec.status).label"
            :animate="getRecordStatus(detailDialog.record.spec.status).animate"
          />
        </div>
      </div>
      <div class="grid grid-cols-3 gap-2">
        <div class="text-gray-500 font-medium dark:text-gray-400">
          处理结果:
        </div>
        <div class="col-span-2 text-gray-900 dark:text-gray-100">
          {{ detailDialog.record.spec.result }}
        </div>
      </div>
      <div class="grid grid-cols-3 gap-2">
        <div class="text-gray-500 font-medium dark:text-gray-400">
          创建时间:
        </div>
        <div class="col-span-2 text-gray-900 dark:text-gray-100">
          {{ dayjs(detailDialog.record.metadata.creationTimestamp).format('YYYY-MM-DD HH:mm:ss') }}
        </div>
      </div>
    </div>
    <template #footer>
      <VSpace>
        <VButton @click="onCancelDetail">
          关闭
        </VButton>
      </VSpace>
    </template>
  </VModal>
</template>
