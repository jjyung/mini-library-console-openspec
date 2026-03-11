<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { FeedbackState } from '../types/library'

const props = defineProps<{
  busy: boolean
  borrowStatus: FeedbackState
  returnStatus: FeedbackState
  prefilledBorrowIsbn: string
  prefilledReturnIsbn: string
}>()

const emit = defineEmits<{
  borrow: [{ isbn: string; readerId: string; dueDate?: string }]
  returnBook: [{ isbn: string; readerId?: string }]
}>()

const activeTab = defineModel<'borrow' | 'return'>('activeTab', { required: true })

const borrowForm = reactive({
  readerId: '',
  isbn: '',
  dueDate: '',
})

const returnForm = reactive({
  isbn: '',
  readerId: '',
})

watch(
  () => props.prefilledBorrowIsbn,
  (isbn) => {
    if (isbn) {
      borrowForm.isbn = isbn
      activeTab.value = 'borrow'
    }
  },
)

watch(
  () => props.prefilledReturnIsbn,
  (isbn) => {
    if (isbn) {
      returnForm.isbn = isbn
      activeTab.value = 'return'
    }
  },
)

function submitBorrow() {
  emit('borrow', {
    isbn: borrowForm.isbn,
    readerId: borrowForm.readerId,
    dueDate: borrowForm.dueDate || undefined,
  })
}

function submitReturn() {
  emit('returnBook', {
    isbn: returnForm.isbn,
    readerId: returnForm.readerId || undefined,
  })
}
</script>

<template>
  <section class="panel-shell" data-testid="transaction-panel">
    <header class="panel-header">
      <div>
        <p class="panel-kicker">Transaction Desk</p>
        <h2>借書與還書</h2>
      </div>
    </header>

    <div class="tab-strip">
      <button
        class="tab-button"
        :class="{ active: activeTab === 'borrow' }"
        data-testid="borrow-tab-button"
        type="button"
        @click="activeTab = 'borrow'"
      >
        借書
      </button>
      <button
        class="tab-button"
        :class="{ active: activeTab === 'return' }"
        data-testid="return-tab-button"
        type="button"
        @click="activeTab = 'return'"
      >
        還書
      </button>
    </div>

    <form
      v-if="activeTab === 'borrow'"
      class="form-grid"
      data-testid="borrow-form"
      @submit.prevent="submitBorrow"
    >
      <label>
        <span>借閱人</span>
        <input
          v-model="borrowForm.readerId"
          data-testid="borrow-reader-id-input"
          name="readerId"
          placeholder="Samson"
          required
          type="text"
        />
      </label>

      <label>
        <span>ISBN</span>
        <input
          v-model="borrowForm.isbn"
          data-testid="borrow-isbn-input"
          name="borrowIsbn"
          placeholder="9780132350884"
          required
          type="text"
        />
      </label>

      <label>
        <span>到期日（可選）</span>
        <input
          v-model="borrowForm.dueDate"
          data-testid="borrow-due-date-input"
          name="dueDate"
          type="date"
        />
      </label>

      <p class="feedback-banner" :class="`tone-${props.borrowStatus.tone}`" data-testid="borrow-feedback">
        {{ props.borrowStatus.message }}
      </p>

      <button
        class="primary-button"
        data-testid="borrow-submit-button"
        :disabled="props.busy"
        type="submit"
      >
        確認借出
      </button>
    </form>

    <form
      v-else
      class="form-grid"
      data-testid="return-form"
      @submit.prevent="submitReturn"
    >
      <label>
        <span>ISBN</span>
        <input
          v-model="returnForm.isbn"
          data-testid="return-isbn-input"
          name="returnIsbn"
          placeholder="9780132350884"
          required
          type="text"
        />
      </label>

      <label>
        <span>借閱人（可選）</span>
        <input
          v-model="returnForm.readerId"
          data-testid="return-reader-id-input"
          name="returnReaderId"
          placeholder="Samson"
          type="text"
        />
      </label>

      <p class="feedback-banner" :class="`tone-${props.returnStatus.tone}`" data-testid="return-feedback">
        {{ props.returnStatus.message }}
      </p>

      <button
        class="secondary-button"
        data-testid="return-submit-button"
        :disabled="props.busy"
        type="submit"
      >
        確認歸還
      </button>
    </form>
  </section>
</template>
