<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { PostLoansBorrowRequestDTO, PostLoansReturnRequestDTO } from '../types/library'

const props = defineProps<{
  busy: boolean
  borrowPrefillIsbn: string
  returnPrefillIsbn: string
  activeMode: 'borrow' | 'return'
}>()

const emit = defineEmits<{
  borrow: [payload: PostLoansBorrowRequestDTO]
  returnBook: [payload: PostLoansReturnRequestDTO]
}>()

const activeTab = reactive({
  value: props.activeMode,
})

const borrowForm = reactive<PostLoansBorrowRequestDTO>({
  isbn: props.borrowPrefillIsbn,
  readerId: '',
})

const returnForm = reactive<PostLoansReturnRequestDTO>({
  isbn: props.returnPrefillIsbn,
  readerId: '',
})

watch(
  () => props.borrowPrefillIsbn,
  (isbn) => {
    if (isbn) {
      borrowForm.isbn = isbn
      activeTab.value = 'borrow'
    }
  },
)

watch(
  () => props.returnPrefillIsbn,
  (isbn) => {
    if (isbn) {
      returnForm.isbn = isbn
      activeTab.value = 'return'
    }
  },
)

watch(
  () => props.activeMode,
  (mode) => {
    activeTab.value = mode
  },
)

function submitBorrow() {
  emit('borrow', {
    isbn: borrowForm.isbn.trim(),
    readerId: borrowForm.readerId.trim(),
  })
  borrowForm.readerId = ''
}

function submitReturn() {
  emit('returnBook', {
    isbn: returnForm.isbn.trim(),
    readerId: returnForm.readerId?.trim() || undefined,
  })
}
</script>

<template>
  <section class="transaction-panel">
    <div class="tab-strip">
      <button
        class="tab-button"
        :class="{ active: activeTab.value === 'borrow' }"
        data-testid="borrow-tab-button"
        type="button"
        @click="activeTab.value = 'borrow'"
      >
        借書
      </button>
      <button
        class="tab-button"
        :class="{ active: activeTab.value === 'return' }"
        data-testid="return-tab-button"
        type="button"
        @click="activeTab.value = 'return'"
      >
        還書
      </button>
    </div>

    <form v-if="activeTab.value === 'borrow'" class="book-form" @submit.prevent="submitBorrow">
      <label>
        <span>讀者 ID</span>
        <input
          v-model="borrowForm.readerId"
          data-testid="book-checkout-borrower-input"
          placeholder="samson"
          required
          type="text"
        />
      </label>

      <label>
        <span>ISBN</span>
        <input v-model="borrowForm.isbn" data-testid="borrow-isbn-input" placeholder="978-0-13-235088-4" required type="text" />
      </label>

      <button class="primary-button" data-testid="book-checkout-button" :disabled="props.busy" type="submit">
        確認借出
      </button>
    </form>

    <form v-else class="book-form" @submit.prevent="submitReturn">
      <label>
        <span>ISBN</span>
        <input v-model="returnForm.isbn" data-testid="return-isbn-input" placeholder="978-0-13-235088-4" required type="text" />
      </label>

      <label>
        <span>讀者 ID</span>
        <input v-model="returnForm.readerId" data-testid="return-reader-id-input" placeholder="samson" type="text" />
      </label>

      <button class="secondary-button" data-testid="return-button" :disabled="props.busy" type="submit">
        確認歸還
      </button>
    </form>
  </section>
</template>
