<script setup lang="ts">
import { reactive } from 'vue'
import type { BookSummary } from '../types/library'

const props = defineProps<{
  book: BookSummary
  busy: boolean
}>()

const emit = defineEmits<{
  addCopies: [{ bookId: string; additionalCopies: number }]
  checkout: [{ bookId: string; borrowerName: string }]
  returnBook: [{ transactionId: string }]
}>()

const form = reactive({
  additionalCopies: 1,
  borrowerName: '',
})

function submitAddCopies() {
  emit('addCopies', {
    bookId: props.book.bookId,
    additionalCopies: Number(form.additionalCopies),
  })
  form.additionalCopies = 1
}

function submitCheckout() {
  emit('checkout', {
    bookId: props.book.bookId,
    borrowerName: form.borrowerName,
  })
  form.borrowerName = ''
}
</script>

<template>
  <article class="book-card" data-testid="book-card">
    <header class="book-header">
      <div>
        <p class="book-kicker">{{ props.book.author }}</p>
        <h3 data-testid="book-title">{{ props.book.title }}</h3>
      </div>
      <span class="status-pill" :class="props.book.status.toLowerCase()" data-testid="book-status">
        {{ props.book.status === 'AVAILABLE' ? 'Available' : 'Checked out' }}
      </span>
    </header>

    <dl class="book-metrics">
      <div>
        <dt>Total</dt>
        <dd data-testid="book-total-copies">{{ props.book.totalCopies }}</dd>
      </div>
      <div>
        <dt>Available</dt>
        <dd data-testid="book-available-copies">{{ props.book.availableCopies }}</dd>
      </div>
      <div>
        <dt>Checked out</dt>
        <dd data-testid="book-checked-out-copies">{{ props.book.checkedOutCopies }}</dd>
      </div>
    </dl>

    <div class="action-stack">
      <form class="inline-form" @submit.prevent="submitAddCopies">
        <label>
          <span>Add copies</span>
          <input
            v-model="form.additionalCopies"
            data-testid="book-add-copies-input"
            min="1"
            required
            type="number"
          />
        </label>
        <button
          class="secondary-button"
          data-testid="book-add-copies-button"
          :disabled="busy"
          type="submit"
        >
          Add copies
        </button>
      </form>

      <form class="inline-form" @submit.prevent="submitCheckout">
        <label>
          <span>Borrower</span>
          <input
            v-model="form.borrowerName"
            data-testid="book-checkout-borrower-input"
            placeholder="Samson"
            required
            type="text"
          />
        </label>
        <button
          class="primary-button"
          data-testid="book-checkout-button"
          :disabled="busy || props.book.availableCopies < 1"
          type="submit"
        >
          Checkout
        </button>
      </form>
    </div>

    <div class="loan-panel">
      <p class="loan-label">Active borrowing</p>
      <ul v-if="props.book.activeTransactions.length > 0" class="transaction-list">
        <li
          v-for="transaction in props.book.activeTransactions"
          :key="transaction.transactionId"
          class="transaction-row"
          data-testid="active-transaction"
        >
          <div>
            <strong data-testid="active-transaction-borrower">{{ transaction.borrowerName }}</strong>
            <p>{{ new Date(transaction.checkedOutAt).toLocaleString() }}</p>
          </div>
          <button
            class="ghost-button"
            data-testid="return-button"
            :disabled="busy"
            type="button"
            @click="emit('returnBook', { transactionId: transaction.transactionId })"
          >
            Return
          </button>
        </li>
      </ul>
      <p v-else class="transaction-empty" data-testid="empty-transaction-state">
        No active borrowing record.
      </p>
    </div>
  </article>
</template>
