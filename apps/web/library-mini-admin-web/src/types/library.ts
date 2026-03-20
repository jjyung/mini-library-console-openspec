export interface ApiEnvelope<TData> {
  code: string
  message: string
  data?: TData
}

export interface BookSummary {
  bookId: string
  title: string
  isbn: string
  author: string | null
  category: string
  status: 'AVAILABLE' | 'BORROWED' | 'INACTIVE'
  availableCount: number
  totalCount: number
  borrowedByReaderId: string | null
}

export interface GetBooksResponseDTO {
  items: BookSummary[]
}

export interface PostBooksRequestDTO {
  title: string
  isbn: string
  author: string | null
  category: string
  quantity: number
  active: boolean
}

export interface PostBooksResponseDTO {
  book: BookSummary
}

export interface PostLoansBorrowRequestDTO {
  isbn: string
  readerId: string
  dueDate?: string
}

export interface PostLoansBorrowResponseDTO {
  book: BookSummary
}

export interface PostLoansReturnRequestDTO {
  isbn: string
  readerId?: string
}

export interface PostLoansReturnResponseDTO {
  book: BookSummary
}

export interface FeedbackState {
  tone: 'neutral' | 'success' | 'error'
  message: string
}
