import type { ApiEnvelope, BusinessCode } from '../types/library'

const JSON_HEADERS = {
  'Content-Type': 'application/json',
}

class ApiClientError extends Error {
  readonly code: BusinessCode | string

  constructor(code: BusinessCode | string, message: string) {
    super(message)
    this.name = 'ApiClientError'
    this.code = code
  }
}

export async function requestJson<TResponse>(path: string, init?: RequestInit): Promise<TResponse> {
  let response: Response

  try {
    response = await fetch(path, {
      ...init,
      headers: {
        ...JSON_HEADERS,
        ...init?.headers,
      },
    })
  } catch {
    throw new Error('Unable to reach library service.')
  }

  const envelope = (await response.json()) as ApiEnvelope<TResponse>

  if (envelope.code !== '00000') {
    throw new ApiClientError(envelope.code, mapBusinessCodeMessage(envelope))
  }

  if (!response.ok || !envelope.data) {
    throw new Error('Library service returned an unexpected response.')
  }

  return envelope.data
}

function mapBusinessCodeMessage(envelope: ApiEnvelope<unknown>) {
  switch (envelope.code) {
    case 'A0000':
      return mapClientErrorMessage(envelope.message)
    case 'B0000':
      return 'The library service hit a system issue. Please try again.'
    case 'C0000':
      return 'A dependent service failed. Please try again later.'
    default:
      return 'The library service returned an unexpected response.'
  }
}

function mapClientErrorMessage(message: string) {
  switch (message) {
    case 'title must not be blank':
      return 'Enter a book title before saving.'
    case 'author must not be blank':
      return 'Enter an author before saving.'
    case 'initialCopies must be greater than or equal to 1':
      return 'Initial copies must be at least 1.'
    case 'additionalCopies must be greater than or equal to 1':
      return 'Additional copies must be at least 1.'
    case 'borrowerName must not be blank':
      return 'Enter a borrower name before checking out a copy.'
    case 'bookId must not be blank':
      return 'Select a valid book before submitting this action.'
    case 'Book not found':
      return 'This book could not be found. Refresh the shelf and try again.'
    case 'No available copies for checkout':
      return 'This title has no available copies to check out.'
    case 'Transaction not found':
      return 'This borrowing record could not be found.'
    case 'Transaction is not returnable':
      return 'This borrowing record was already returned.'
    default:
      return 'We could not complete that request. Check the details and try again.'
  }
}
