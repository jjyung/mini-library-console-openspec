import { requestJson } from './httpClient'
import type {
  GetBooksResponseDTO,
  PostBooksRequestDTO,
  PostBooksResponseDTO,
  PostTransactionsCheckoutRequestDTO,
} from '../types/library'

export function listBooks() {
  return requestJson<GetBooksResponseDTO>('/api/books')
}

export function createBook(payload: PostBooksRequestDTO) {
  return requestJson<PostBooksResponseDTO>('/api/books', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function addBookCopies(payload: { bookId: string; additionalCopies: number }) {
  return requestJson<PostBooksResponseDTO>(`/api/books/${payload.bookId}/copies`, {
    method: 'POST',
    body: JSON.stringify({
      additionalCopies: payload.additionalCopies,
    }),
  })
}

export function checkoutBookCopy(payload: PostTransactionsCheckoutRequestDTO) {
  return requestJson('/api/transactions/checkout', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function returnBookCopy(transactionId: string) {
  return requestJson(`/api/transactions/${transactionId}/return`, {
    method: 'POST',
  })
}
