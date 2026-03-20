import type { ApiEnvelope } from '../types/library'

const JSON_HEADERS = {
  'Content-Type': 'application/json',
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
    throw new Error(mapBusinessCodeMessage(envelope))
  }

  if (!response.ok || !envelope.data) {
    throw new Error('Library service returned an unexpected response.')
  }

  return envelope.data
}

function mapBusinessCodeMessage(envelope: ApiEnvelope<unknown>) {
  switch (envelope.code) {
    case 'A0000':
    case 'A0400':
    case 'A0404':
    case 'A0409':
    case 'A0410':
    case 'A0411':
      return envelope.message || 'Request could not be completed.'
    case 'B0000':
      return 'Library service encountered a system error.'
    case 'C0000':
      return 'A dependent service failed.'
    default:
      return envelope.message || 'Unexpected service response.'
  }
}
