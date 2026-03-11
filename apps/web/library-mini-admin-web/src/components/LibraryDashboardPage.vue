<script setup lang="ts">
import { computed, ref } from 'vue'
import BookCollectionPanel from './BookCollectionPanel.vue'
import CreateBookPanel from './CreateBookPanel.vue'
import EmptyStatePanel from './EmptyStatePanel.vue'
import TopBar from './TopBar.vue'
import TransactionPanel from './TransactionPanel.vue'
import { useLibraryDashboard } from '../composables/useLibraryDashboard'

const {
  books,
  filteredBooks,
  isLoading,
  feedback,
  searchKeyword,
  loadBooks,
  createBook,
  checkoutBook,
  returnBook,
} = useLibraryDashboard()

const activeTab = ref<'borrow' | 'return'>('borrow')
const selectedBorrowIsbn = ref('')
const selectedReturnIsbn = ref('')

const borrowStatus = computed(() =>
  feedback.value.mode === 'borrow'
    ? feedback.value
    : {
        mode: 'borrow' as const,
        tone: 'neutral' as const,
        message: '輸入借閱人與 ISBN 後即可完成借出。',
      },
)

const returnStatus = computed(() =>
  feedback.value.mode === 'return'
    ? feedback.value
    : {
        mode: 'return' as const,
        tone: 'neutral' as const,
        message: '輸入 ISBN 後即可歸還書籍。',
      },
)

function focusBorrow(isbn: string) {
  selectedBorrowIsbn.value = isbn
}

function quickReturn(payload: { isbn: string; readerId?: string }) {
  selectedReturnIsbn.value = payload.isbn
  returnBook(payload)
}
</script>

<template>
  <main class="page-shell" data-testid="library-page">
    <TopBar v-model:search-keyword="searchKeyword" />

    <section class="summary-strip">
      <article class="summary-card">
        <span>館藏數量</span>
        <strong data-testid="book-count">{{ books.length }}</strong>
      </article>
      <article class="summary-card">
        <span>可借冊數</span>
        <strong data-testid="available-count">
          {{ books.reduce((totalCopies, book) => totalCopies + book.availableCopies, 0) }}
        </strong>
      </article>
      <article class="summary-card">
        <span>借出冊數</span>
        <strong data-testid="checked-out-count">
          {{ books.reduce((totalCopies, book) => totalCopies + book.checkedOutCopies, 0) }}
        </strong>
      </article>
      <button
        class="ghost-button refresh-button"
        data-testid="refresh-books-button"
        type="button"
        @click="loadBooks"
      >
        重新整理
      </button>
    </section>

    <section class="workspace-grid">
      <TransactionPanel
        v-model:active-tab="activeTab"
        :borrow-status="borrowStatus"
        :busy="isLoading"
        :prefilled-borrow-isbn="selectedBorrowIsbn"
        :prefilled-return-isbn="selectedReturnIsbn"
        :return-status="returnStatus"
        @borrow="checkoutBook"
        @return-book="returnBook"
      />

      <CreateBookPanel :busy="isLoading" @submit="createBook" />
    </section>

    <section class="panel-shell inventory-panel">
      <header class="panel-header">
        <div>
          <p class="panel-kicker">Live Inventory</p>
          <h2>館藏清單</h2>
        </div>
        <span class="feedback-banner" :class="`tone-${feedback.tone}`" data-testid="library-feedback">
          {{ feedback.message }}
        </span>
      </header>

      <EmptyStatePanel v-if="!isLoading && filteredBooks.length === 0 && books.length === 0" />
      <section v-else-if="filteredBooks.length === 0" class="empty-panel" data-testid="empty-search-state">
        <p class="panel-kicker">No Match</p>
        <h3>找不到符合搜尋條件的書籍。</h3>
        <p>試試完整書名、ISBN 或作者關鍵字。</p>
      </section>
      <BookCollectionPanel
        v-else
        :books="filteredBooks"
        :busy="isLoading"
        @quick-borrow="focusBorrow($event.isbn)"
        @quick-return="quickReturn"
      />
    </section>
  </main>
</template>
