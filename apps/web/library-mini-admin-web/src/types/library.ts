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
  author: string
  totalCopies: number
  availableCopies: number
  checkedOutCopies: number
  status: 'AVAILABLE' | 'CHECKED_OUT'
  activeTransactions: ActiveTransaction[]
}

export interface GetBooksResponseDTO {
  books: BookSummary[]
}

export interface PostBooksRequestDTO {
  title: string
  author: string
  initialCopies: number
}

export interface PostBooksResponseDTO {
  book: BookSummary
}

export interface PostTransactionsCheckoutRequestDTO {
  bookId: string
  borrowerName: string
}

export interface FeedbackState {
  tone: 'neutral' | 'success' | 'error'
  message: string
}
