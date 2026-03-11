export interface ApiEnvelope<TData> {
  code: string
  message: string
  data?: TData
}

export interface ActiveTransaction {
  transactionId: string
  borrowerName: string
  checkedOutAt: string
}

export interface BookSummary {
  bookId: string
  title: string
  isbn: string
  author: string
  category: string
  totalCopies: number
  availableCopies: number
  checkedOutCopies: number
  status: 'AVAILABLE' | 'BORROWED' | 'INACTIVE'
  activeTransactions: ActiveTransaction[]
}

export interface GetBooksResponseDTO {
  books: BookSummary[]
}

export interface PostBooksRequestDTO {
  title: string
  isbn: string
  author: string
  category: string
  active: boolean
  initialCopies: number
}

export interface PostBooksResponseDTO {
  book: BookSummary
}

export interface PostTransactionsCheckoutRequestDTO {
  isbn: string
  readerId: string
  dueDate?: string
}

export interface FeedbackState {
  mode: 'general' | 'borrow' | 'return'
  tone: 'neutral' | 'success' | 'error'
  message: string
}

export interface PostTransactionsReturnRequestDTO {
  isbn: string
  readerId?: string
}
