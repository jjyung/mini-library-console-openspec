import { requestJson } from './httpClient'
import type {
  GetBooksResponseDTO,
  PostBooksRequestDTO,
  PostBooksResponseDTO,
  PostLoansBorrowRequestDTO,
  PostLoansBorrowResponseDTO,
  PostLoansReturnRequestDTO,
  PostLoansReturnResponseDTO,
} from '../types/library'

export function listBooks(keyword?: string) {
  const searchParams = new URLSearchParams()
  if (keyword) {
    searchParams.set('keyword', keyword)
  }

  const path = searchParams.size > 0 ? `/api/books?${searchParams.toString()}` : '/api/books'
  return requestJson<GetBooksResponseDTO>(path)
}

export function createBook(payload: PostBooksRequestDTO) {
  return requestJson<PostBooksResponseDTO>('/api/books', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function borrowBook(payload: PostLoansBorrowRequestDTO) {
  return requestJson<PostLoansBorrowResponseDTO>('/api/loans/borrow', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function returnBook(payload: PostLoansReturnRequestDTO) {
  return requestJson<PostLoansReturnResponseDTO>('/api/loans/return', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
