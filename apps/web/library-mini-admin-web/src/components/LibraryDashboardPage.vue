<script setup lang="ts">
import { ref } from 'vue'
import AddBookForm from './AddBookForm.vue'
import BookTable from './BookTable.vue'
import TopBar from './TopBar.vue'
import TransactionCard from './TransactionCard.vue'
import { useLibraryDashboard } from '../composables/useLibraryDashboard'
import type { PostBooksRequestDTO, PostLoansBorrowRequestDTO, PostLoansReturnRequestDTO } from '../types/library'

const {
  books,
  isLoading,
  feedback,
  searchKeyword,
  availableCount,
  borrowedCount,
  loadBooks,
  createBook,
  borrowBook,
  returnBook,
} = useLibraryDashboard()

const borrowPrefillIsbn = ref('')
const returnPrefillIsbn = ref('')
const transactionMode = ref<'borrow' | 'return'>('borrow')

async function handleSearch(keyword: string) {
  await loadBooks(keyword)
}

async function handleCreateBook(payload: PostBooksRequestDTO) {
  await createBook(payload)
}

async function handleBorrow(payload: PostLoansBorrowRequestDTO) {
  await borrowBook(payload)
}

async function handleReturn(payload: PostLoansReturnRequestDTO) {
  await returnBook(payload)
  returnPrefillIsbn.value = payload.isbn
  transactionMode.value = 'return'
}

function handleQuickBorrow(isbn: string) {
  borrowPrefillIsbn.value = isbn
  transactionMode.value = 'borrow'
}

async function handleQuickReturn(isbn: string) {
  returnPrefillIsbn.value = isbn
  transactionMode.value = 'return'
  await returnBook({ isbn })
}
</script>

<template>
  <div data-testid="library-page">
    <TopBar :busy="isLoading" :search-keyword="searchKeyword" @refresh="loadBooks()" @search="handleSearch" />

    <main class="dashboard-shell">
      <section class="hero-panel">
        <p class="eyebrow">Library Mini Admin</p>
        <div class="hero-copy">
          <div>
            <h1>在單一控制台掌握館藏與借還。</h1>
            <p class="hero-description">
              以 ISBN 為主識別，快速搜尋、建書、借出與歸還，讓共享書櫃的狀態一眼可見。
            </p>
          </div>
          <span class="feedback-banner" :class="`tone-${feedback.tone}`" data-testid="library-feedback">
            {{ feedback.message }}
          </span>
        </div>

        <dl class="stat-strip">
          <div>
            <dt>館藏數</dt>
            <dd data-testid="book-count">{{ books.length }}</dd>
          </div>
          <div>
            <dt>可借數</dt>
            <dd data-testid="available-count">{{ availableCount }}</dd>
          </div>
          <div>
            <dt>借出數</dt>
            <dd data-testid="checked-out-count">{{ borrowedCount }}</dd>
          </div>
        </dl>
      </section>

      <section class="workspace-grid">
        <TransactionCard
          :active-mode="transactionMode"
          :borrow-prefill-isbn="borrowPrefillIsbn"
          :busy="isLoading"
          :return-prefill-isbn="returnPrefillIsbn"
          @borrow="handleBorrow"
          @return-book="handleReturn"
        />
        <AddBookForm :busy="isLoading" @submit="handleCreateBook" />
      </section>

      <section class="content-panel">
        <div class="section-heading">
          <div>
            <p class="section-label">Live inventory</p>
            <h2>館藏列表</h2>
          </div>
          <button class="ghost-button" data-testid="refresh-books-button" :disabled="isLoading" type="button" @click="loadBooks()">
            Refresh
          </button>
        </div>

        <BookTable
          :books="books"
          :busy="isLoading"
          @quick-borrow="handleQuickBorrow"
          @quick-return="handleQuickReturn"
        />
      </section>
    </main>
  </div>
</template>
