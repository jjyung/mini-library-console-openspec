import { computed, onMounted, ref } from 'vue'
import { borrowBook, createBook, listBooks, returnBook } from '../services/libraryApi'
import type {
  BookSummary,
  FeedbackState,
  PostBooksRequestDTO,
  PostLoansBorrowRequestDTO,
  PostLoansReturnRequestDTO,
} from '../types/library'

const DEFAULT_FEEDBACK: FeedbackState = {
  tone: 'neutral',
  message: '準備開始管理共享書櫃。',
}

export function useLibraryDashboard() {
  const books = ref<BookSummary[]>([])
  const busyState = ref(false)
  const feedback = ref<FeedbackState>(DEFAULT_FEEDBACK)
  const searchKeyword = ref('')

  const isLoading = computed(() => busyState.value)
  const availableCount = computed(() =>
    books.value.reduce((totalCopies, book) => totalCopies + book.availableCount, 0),
  )
  const borrowedCount = computed(() =>
    books.value.reduce((totalCopies, book) => totalCopies + (book.totalCount - book.availableCount), 0),
  )

  onMounted(async () => {
    await loadBooks()
  })

  async function loadBooks(keyword = searchKeyword.value) {
    searchKeyword.value = keyword
    await runAction(async () => {
      const response = await listBooks(searchKeyword.value.trim())
      books.value = response.items
      feedback.value = {
        tone: 'neutral',
        message: response.items.length > 0 ? '館藏已更新。' : '目前尚無館藏資料。',
      }
    })
  }

  async function handleCreateBook(payload: PostBooksRequestDTO) {
    await runAction(async () => {
      await createBook(payload)
      await loadBooks(searchKeyword.value)
      feedback.value = {
        tone: 'success',
        message: `《${payload.title}》已新增到館藏。`,
      }
    })
  }

  async function handleBorrowBook(payload: PostLoansBorrowRequestDTO) {
    await runAction(async () => {
      await borrowBook(payload)
      await loadBooks(searchKeyword.value)
      feedback.value = {
        tone: 'success',
        message: `ISBN ${payload.isbn} 借出成功。`,
      }
    })
  }

  async function handleReturnBook(payload: PostLoansReturnRequestDTO) {
    await runAction(async () => {
      await returnBook(payload)
      await loadBooks(searchKeyword.value)
      feedback.value = {
        tone: 'success',
        message: `ISBN ${payload.isbn} 歸還成功。`,
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
    isLoading,
    feedback,
    searchKeyword,
    availableCount,
    borrowedCount,
    loadBooks,
    createBook: handleCreateBook,
    borrowBook: handleBorrowBook,
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
