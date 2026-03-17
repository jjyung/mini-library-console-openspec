import { computed, onMounted, ref } from 'vue'
import { addBookCopies, checkoutBookCopy, createBook, listBooks, returnBookCopy } from '../services/libraryApi'
import type { FeedbackState } from '../types/library'

const DEFAULT_FEEDBACK: FeedbackState = {
  tone: 'neutral',
  message: 'Ready to manage the shared shelf.',
}

export function useLibraryDashboard() {
  const books = ref(awaitableEmptyBooks())
  const busyState = ref(false)
  const feedback = ref<FeedbackState>(DEFAULT_FEEDBACK)

  const isLoading = computed(() => busyState.value)

  onMounted(async () => {
    await loadBooks()
  })

  async function loadBooks() {
    await runAction(async () => {
      await refreshBooks()
      feedback.value = {
        tone: 'neutral',
        message: books.value.length > 0 ? 'Inventory refreshed.' : DEFAULT_FEEDBACK.message,
      }
    })
  }

  async function handleCreateBook(payload: { title: string; author: string; initialCopies: number }) {
    await runAction(async () => {
      await createBook(payload)
      await refreshBooks()
      feedback.value = {
        tone: 'success',
        message: 'Book created successfully.',
      }
    })
  }

  async function handleAddCopies(payload: { bookId: string; additionalCopies: number }) {
    await runAction(async () => {
      await addBookCopies(payload)
      await refreshBooks()
      feedback.value = {
        tone: 'success',
        message: 'Copies added successfully.',
      }
    })
  }

  async function handleCheckoutBook(payload: { bookId: string; borrowerName: string }) {
    await runAction(async () => {
      await checkoutBookCopy(payload)
      await refreshBooks()
      feedback.value = {
        tone: 'success',
        message: 'Checkout completed successfully.',
      }
    })
  }

  async function handleReturnBook(payload: { transactionId: string }) {
    await runAction(async () => {
      await returnBookCopy(payload.transactionId)
      await refreshBooks()
      feedback.value = {
        tone: 'success',
        message: 'Return completed successfully.',
      }
    })
  }

  async function runAction(action: () => Promise<void>) {
    try {
      busyState.value = true
      await action()
    } catch (error) {
      feedback.value = mapErrorToFeedback(error)
    } finally {
      busyState.value = false
    }
  }

  async function refreshBooks() {
    const response = await listBooks()
    books.value = response.books
  }

  return {
    books,
    isLoading,
    feedback,
    loadBooks,
    createBook: handleCreateBook,
    addCopies: handleAddCopies,
    checkoutBook: handleCheckoutBook,
    returnBook: handleReturnBook,
  }
}

function mapErrorToFeedback(error: unknown): FeedbackState {
  if (error instanceof Error) {
    return {
      tone: 'error',
      message: error.message,
    }
  }

  return {
    tone: 'error',
    message: 'Unable to reach library service.',
  }
}

function awaitableEmptyBooks() {
  return [] as Awaited<ReturnType<typeof listBooks>>['books']
}
