<script setup lang="ts">
import type { BookSummary } from '../types/library'

defineProps<{
  books: BookSummary[]
  busy: boolean
}>()

const emit = defineEmits<{
  quickBorrow: [{ isbn: string }]
  quickReturn: [{ isbn: string; readerId?: string }]
}>()

function formatStatus(status: BookSummary['status']) {
  switch (status) {
    case 'AVAILABLE':
      return '可借閱'
    case 'BORROWED':
      return '已借出'
    case 'INACTIVE':
      return '未上架'
  }
}

function getPrimaryBorrowerName(book: BookSummary) {
  return book.activeTransactions[0]?.borrowerName ?? ''
}
</script>

<template>
  <div class="table-shell" data-testid="book-list">
    <table class="inventory-table">
      <thead>
        <tr>
          <th>書名</th>
          <th>ISBN</th>
          <th>作者</th>
          <th>分類</th>
          <th>狀態</th>
          <th>可借數/總數</th>
          <th>借閱人</th>
          <th>動作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="book in books" :key="book.bookId" data-testid="book-row">
          <td data-testid="book-title">{{ book.title }}</td>
          <td data-testid="book-isbn">{{ book.isbn }}</td>
          <td>{{ book.author }}</td>
          <td>{{ book.category }}</td>
          <td>
            <span class="status-pill" :class="book.status.toLowerCase()" data-testid="book-status">
              {{ formatStatus(book.status) }}
            </span>
          </td>
          <td>
            <span data-testid="book-available-copies">{{ book.availableCopies }}</span>
            /
            <span data-testid="book-total-copies">{{ book.totalCopies }}</span>
          </td>
          <td>
            <span v-if="book.activeTransactions.length > 0" data-testid="active-transaction-borrower">
              {{ getPrimaryBorrowerName(book) }}
            </span>
            <span v-else data-testid="empty-transaction-state">-</span>
          </td>
          <td>
            <div class="table-actions">
              <button
                class="table-button primary"
                data-testid="quick-borrow-button"
                :disabled="busy || book.status !== 'AVAILABLE'"
                type="button"
                @click="emit('quickBorrow', { isbn: book.isbn })"
              >
                借出
              </button>
              <button
                class="table-button ghost"
                data-testid="quick-return-button"
                :disabled="busy || book.activeTransactions.length === 0"
                type="button"
                @click="
                  emit('quickReturn', {
                    isbn: book.isbn,
                    readerId: getPrimaryBorrowerName(book) || undefined,
                  })
                "
              >
                歸還
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
