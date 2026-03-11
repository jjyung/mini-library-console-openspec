<script setup lang="ts">
import BookCollectionPanel from './BookCollectionPanel.vue'
import CreateBookPanel from './CreateBookPanel.vue'
import EmptyStatePanel from './EmptyStatePanel.vue'
import { useLibraryDashboard } from '../composables/useLibraryDashboard'

const {
  books,
  isLoading,
  feedback,
  loadBooks,
  createBook,
  addCopies,
  checkoutBook,
  returnBook,
} = useLibraryDashboard()
</script>

<template>
  <main class="page-shell" data-testid="library-page">
    <section class="hero-panel">
      <p class="eyebrow">Shared Shelf Console</p>
      <div class="hero-copy">
        <div>
          <h1>Track every copy before it disappears from the shelf.</h1>
          <p class="hero-description">
            Create books, top up inventory, lend copies to teammates, and return active loans from
            one operational view.
          </p>
        </div>
        <button
          class="ghost-button"
          data-testid="refresh-books-button"
          type="button"
          @click="loadBooks"
        >
          Refresh
        </button>
      </div>
      <dl class="stat-strip">
        <div>
          <dt>Titles</dt>
          <dd data-testid="book-count">{{ books.length }}</dd>
        </div>
        <div>
          <dt>Available</dt>
          <dd data-testid="available-count">
            {{ books.reduce((totalCopies, book) => totalCopies + book.availableCopies, 0) }}
          </dd>
        </div>
        <div>
          <dt>Checked out</dt>
          <dd data-testid="checked-out-count">
            {{ books.reduce((totalCopies, book) => totalCopies + book.checkedOutCopies, 0) }}
          </dd>
        </div>
      </dl>
    </section>

    <section class="workspace-grid">
      <CreateBookPanel :busy="isLoading" @submit="createBook" />

      <section class="content-panel">
        <header class="section-header">
          <div>
            <p class="section-label">Live inventory</p>
            <h2>Bookshelf operations</h2>
          </div>
          <span
            class="feedback-banner"
            :class="`tone-${feedback.tone}`"
            data-testid="library-feedback"
          >
            {{ feedback.message }}
          </span>
        </header>

        <EmptyStatePanel v-if="!isLoading && books.length === 0" />
        <BookCollectionPanel
          v-else
          :books="books"
          :busy="isLoading"
          @add-copies="addCopies"
          @checkout="checkoutBook"
          @return-book="returnBook"
        />
      </section>
    </section>
  </main>
</template>
