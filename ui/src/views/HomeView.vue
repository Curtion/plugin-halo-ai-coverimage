<script lang="ts" setup>
import type { CoverGenerateRecord, CoverGenerateRecordList } from '../types'
import {
  IconRefreshLine,
  VButton,
  VCard,
  VDialog,
  VEmpty,
  VLoading,
  VPageHeader,
  VPagination,
  VSpace,
} from '@halo-dev/components'
import { useQuery } from '@tanstack/vue-query'
import { useRouteQuery } from '@vueuse/router'
import axios from 'axios'
import { ref, watch } from 'vue'
import IconWrench from '~icons/iconoir/wrench'
import RecordListItem from '../components/RecordListItem.vue'

const page = useRouteQuery<number>('page', 1, {
  transform: Number,
})
const size = useRouteQuery<number>('size', 20, {
  transform: Number,
})
const keyword = useRouteQuery<string>('keyword', '')

const http = axios.create({
  baseURL: '/',
})

watch(
  () => [keyword.value],
  () => {
    page.value = 1
  },
)

const {
  data: records,
  isLoading,
  isFetching,
  refetch,
} = useQuery({
  queryKey: ['cover-generate-records', page, size, keyword],
  queryFn: async () => {
    const { data } = await http.get<CoverGenerateRecordList>('/apis/io.github.curtion/v1alpha1/covergeneraterecords', {
      params: {
        page: page.value,
        size: size.value,
        keyword: keyword.value,
        sort: ['metadata.creationTimestamp,desc'],
      },
    })
    return data
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

const dialog = ref({
  visible: false,
  record: null as CoverGenerateRecord | null,
  index: -1,
})

function handleDeleteItem(record: CoverGenerateRecord, index: number) {
  dialog.value.visible = true
  dialog.value.record = record
  dialog.value.index = index
}

function onConfirmDelete() {
  if (dialog.value.index !== -1) {
    records.value?.items.splice(dialog.value.index, 1)
  }
  onCancelDelete()
}

function onCancelDelete() {
  dialog.value.visible = false
  dialog.value.record = null
  dialog.value.index = -1
}
</script>

<template>
  <VPageHeader title="生成记录">
    <template #icon>
      <IconWrench />
    </template>
    <template #actions>
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
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-0']">
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
        <div class="overflow-x-auto">
          <table class="min-w-full table-auto">
            <thead class="bg-gray-50 dark:bg-gray-700">
              <tr>
                <th class="px-6 py-3 text-center text-xs text-gray-500 font-medium tracking-wider uppercase dark:text-gray-300">
                  LLM 提示词
                </th>
                <th class="px-6 py-3 text-center text-xs text-gray-500 font-medium tracking-wider uppercase dark:text-gray-300">
                  T2I 提示词
                </th>
                <th class=":uno: hidden px-6 py-3 text-center text-xs text-gray-500 font-medium tracking-wider uppercase md:table-cell dark:text-gray-300">
                  LLM 提供者
                </th>
                <th class=":uno: hidden px-6 py-3 text-center text-xs text-gray-500 font-medium tracking-wider uppercase md:table-cell dark:text-gray-300">
                  T2I 提供者
                </th>
                <th class="whitespace-nowrap px-6 py-3 text-center text-xs text-gray-500 font-medium tracking-wider uppercase dark:text-gray-300">
                  状态
                </th>
                <th class="whitespace-nowrap px-6 py-3 text-center text-xs text-gray-500 font-medium tracking-wider uppercase dark:text-gray-300">
                  创建时间
                </th>
                <th class="px-6 py-3 text-center text-xs text-gray-500 font-medium tracking-wider uppercase dark:text-gray-300">
                  操作
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200 dark:bg-gray-800 dark:divide-gray-700">
              <RecordListItem
                v-for="(record, index) in records?.items"
                :key="record.metadata.name"
                :record="record"
                @delete="handleDeleteItem(record, index)"
              />
            </tbody>
          </table>
        </div>
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
    :visible="dialog.visible"
    title="确定删除吗？"
    description="你确定要删除这条记录吗？"
    type="warning"
    confirm-type="danger"
    @close="onCancelDelete"
    @cancel="onCancelDelete"
    @confirm="onConfirmDelete"
  />
</template>
