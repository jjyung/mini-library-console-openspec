import { computed, onMounted, ref } from 'vue'
import { checkoutBookCopy, createBook, listBooks, returnBookCopy } from '../services/libraryApi'
import type { FeedbackState } from '../types/library'

const DEFAULT_FEEDBACK: FeedbackState = {
  mode: 'general',
  tone: 'neutral',
  message: '準備好管理共享書櫃。',
}

export function useLibraryDashboard() {
  const books = ref(awaitableEmptyBooks())
  const busyState = ref(false)
  const feedback = ref<FeedbackState>(DEFAULT_FEEDBACK)
  const searchKeyword = ref('')

  const isLoading = computed(() => busyState.value)
  const filteredBooks = computed(() => {
    const keyword = searchKeyword.value.trim().toLowerCase()
    if (!keyword) {
      return books.value
    }

    return books.value.filter((book) =>
      [book.title, book.isbn, book.author].some((field) => field.toLowerCase().includes(keyword)),
    )
  })

  onMounted(async () => {
    await loadBooks()
  })

  async function loadBooks() {
    await runAction(async () => {
      const response = await listBooks()
      books.value = response.books
      feedback.value = {
        mode: 'general',
        tone: 'neutral',
        message: response.books.length > 0 ? '館藏資料已更新。' : DEFAULT_FEEDBACK.message,
      }
    })
  }

  async function handleCreateBook(payload: {
    title: string
    isbn: string
    author: string
    category: string
    active: boolean
    initialCopies: number
  }) {
    await runAction(async () => {
      await createBook(payload)
      await loadBooks()
      feedback.value = {
        mode: 'general',
        tone: 'success',
        message: `《${payload.title}》已新增到館藏。`,
      }
    })
  }

  async function handleCheckoutBook(payload: { isbn: string; readerId: string; dueDate?: string }) {
    await runAction(async () => {
      await checkoutBookCopy(payload)
      await loadBooks()
      feedback.value = {
        mode: 'borrow',
        tone: 'success',
        message: `借書成功，${payload.readerId} 已借出 ${payload.isbn}。`,
      }
    })
  }

  async function handleReturnBook(payload: { isbn: string; readerId?: string }) {
    await runAction(async () => {
      await returnBookCopy(payload)
      await loadBooks()
      feedback.value = {
        mode: 'return',
        tone: 'success',
        message: `書籍 ${payload.isbn} 已完成歸還。`,
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

  return {
    books,
    filteredBooks,
    isLoading,
    feedback,
    searchKeyword,
    loadBooks,
    createBook: handleCreateBook,
    checkoutBook: handleCheckoutBook,
    returnBook: handleReturnBook,
  }
}

function mapErrorToFeedback(error: unknown): FeedbackState {
  if (error instanceof Error) {
    return {
      mode: 'general',
      tone: 'error',
      message: error.message,
    }
  }

  return {
    mode: 'general',
    tone: 'error',
    message: '目前無法連線到圖書櫃服務。',
  }
}

function awaitableEmptyBooks() {
  return [] as Awaited<ReturnType<typeof listBooks>>['books']
}
