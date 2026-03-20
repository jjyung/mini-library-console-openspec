<script setup lang="ts">
import type { BookSummary } from '../types/library'

const props = defineProps<{
  books: BookSummary[]
  busy: boolean
}>()

const emit = defineEmits<{
  quickBorrow: [isbn: string]
  quickReturn: [isbn: string]
}>()

function statusLabel(status: BookSummary['status']) {
  switch (status) {
    case 'AVAILABLE':
      return '可借閱'
    case 'BORROWED':
      return '已借出'
    case 'INACTIVE':
      return '未上架'
  }
}
</script>

<template>
  <div class="table-shell" data-testid="book-list">
    <table v-if="props.books.length > 0">
      <thead>
        <tr>
          <th>書名</th>
          <th>ISBN</th>
          <th>作者</th>
          <th>分類</th>
          <th>狀態</th>
          <th>可借數 / 總數</th>
          <th>動作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="book in props.books" :key="book.bookId" data-testid="book-card">
          <td data-testid="book-title">{{ book.title }}</td>
          <td class="mono">{{ book.isbn }}</td>
          <td>{{ book.author || '-' }}</td>
          <td>{{ book.category }}</td>
          <td>
            <span class="status-pill" :class="book.status.toLowerCase()" data-testid="book-status">
              {{ statusLabel(book.status) }}
            </span>
          </td>
          <td>
            <span data-testid="book-available-copies">{{ book.availableCount }}</span>
            /
            <span data-testid="book-total-copies">{{ book.totalCount }}</span>
            <span class="visually-hidden" data-testid="book-checked-out-copies">
              {{ book.totalCount - book.availableCount }}
            </span>
          </td>
          <td class="action-cell">
            <button
              class="ghost-button"
              data-testid="book-checkout-button"
              :disabled="props.busy || book.status === 'INACTIVE' || book.availableCount < 1"
              type="button"
              @click="emit('quickBorrow', book.isbn)"
            >
              借出
            </button>
            <button
              class="secondary-button"
              data-testid="return-button"
              :disabled="props.busy || book.availableCount === book.totalCount"
              type="button"
              @click="emit('quickReturn', book.isbn)"
            >
              歸還
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-else class="empty-state" data-testid="empty-transaction-state">
      目前尚無館藏，請先新增書籍。
    </div>
  </div>
</template>
