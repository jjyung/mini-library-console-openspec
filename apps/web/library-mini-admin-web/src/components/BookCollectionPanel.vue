<script setup lang="ts">
import BookCard from './BookCard.vue'
import type { BookSummary } from '../types/library'

defineProps<{
  books: BookSummary[]
  busy: boolean
}>()

const emit = defineEmits<{
  addCopies: [{ bookId: string; additionalCopies: number }]
  checkout: [{ bookId: string; borrowerName: string }]
  returnBook: [{ transactionId: string }]
}>()
</script>

<template>
  <div class="book-grid" data-testid="book-list">
    <BookCard
      v-for="book in books"
      :key="book.bookId"
      :book="book"
      :busy="busy"
      @add-copies="emit('addCopies', $event)"
      @checkout="emit('checkout', $event)"
      @return-book="emit('returnBook', $event)"
    />
  </div>
</template>
