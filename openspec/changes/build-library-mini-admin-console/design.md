# Design: Build Library Mini Admin Console

## Overview

This change reshapes the current teaching sample into a product-demo-oriented admin console while keeping the architecture intentionally simple:

- Vue frontend remains a single dashboard page backed by composables and a centralized API client.
- Spring Boot backend remains an in-memory service with business-code API envelopes.
- The UI shifts from card-oriented shelf management to a console layout optimized for demonstration.

## Current State

### Frontend

- `LibraryDashboardPage.vue` renders a hero banner, one create-book panel, and a grid of per-book cards.
- Book creation only supports title, author, and initial copies.
- Checkout and return are initiated from each individual book card.
- Feedback is a single banner message managed by `useLibraryDashboard`.

### Backend

- `Book` and its DTOs only support title, author, and copy counts.
- Checkout uses `bookId` plus `borrowerName`.
- Return uses `transactionId`.
- List responses expose active transactions, which is useful but does not map directly to the intended ISBN-driven transaction panel.

## Target Experience

The page should support this operational flow:

```text
┌─────────────────────────────────────────────────────────────┐
│ Top bar: title, search, admin identity                     │
├─────────────────────────────┬───────────────────────────────┤
│ Transaction panel           │ Create book panel            │
│ - Borrow tab                │ - title                      │
│ - Return tab                │ - ISBN                       │
│ - status feedback           │ - author                     │
│                             │ - category                   │
│                             │ - quantity                   │
│                             │ - active state               │
├─────────────────────────────────────────────────────────────┤
│ Inventory table                                                │
│ - title / ISBN / author / category / status / counts         │
│ - quick borrow / quick return                                 │
└─────────────────────────────────────────────────────────────┘
```

This layout allows a presenter to narrate the full book lifecycle without leaving the page.

## Design Decisions

### 1. Keep a single dashboard route

The demo benefits from having the entire flow on one page. Introducing routing would add implementation overhead without improving the learning goal.

### 2. Promote ISBN to a first-class identifier in the UI

The target flow references books by title in narration but operationally works best when the transaction panel can accept ISBN. The backend should therefore store and expose ISBN for each book.

Implications:

- `PostBooksRequestDTO` and response DTOs need ISBN support.
- The domain model needs a unique ISBN field.
- Borrow and return operations need an ISBN-based path or a reliable frontend adapter from ISBN to domain identifiers.

### 3. Add category and active shelf state to the book model

The target create-book form contains category and active/inactive state. These should be represented explicitly instead of being inferred in the view.

Implications:

- Add category and status fields to the backend domain model.
- Distinguish between availability and shelf activity:
  - `ACTIVE` or `INACTIVE` shelf state
  - availability counts derived from active borrow transactions

### 4. Preserve centralized API access and feedback mapping

Frontend components must continue to avoid direct fetch logic. The existing `libraryApi.ts` and `httpClient.ts` pattern already fits the repo rules and should be extended rather than bypassed.

### 5. Preserve and expand `data-testid`

Existing locators should not be removed casually. New layouts must carry stable test IDs for:

- search field
- transaction tabs
- borrow form
- return form
- create-book fields
- inventory table rows and action buttons
- feedback banners

## Data Model Changes

### Book domain

Add or expose the following fields:

- `isbn`
- `category`
- `shelfStatus`
- `availableCopies`
- `totalCopies`
- active borrower context needed by the UI

Possible shelf status values:

- `ACTIVE`
- `INACTIVE`

Availability presentation can still derive a separate UI status:

- `AVAILABLE`
- `BORROWED`
- `INACTIVE`

### Transaction flow

Borrow input:

- `readerId`
- `isbn`
- optional `dueDate`

Return input:

- `isbn`
- optional `readerId`

The implementation can resolve ISBN to the current active transaction server-side. That keeps the frontend simpler and matches the Figma-style transaction panel.

## API Adjustments

Expected API evolution:

- `POST /api/books`
  - request adds ISBN, category, initial copies, and active shelf state
- `GET /api/books`
  - response expands each book summary to include ISBN, category, shelf status, availability status, and any active borrower snapshot needed for the table
- `POST /api/transactions/checkout`
  - request shifts from `bookId` / `borrowerName` to `isbn` / `readerId` / optional `dueDate`
- `POST /api/transactions/return`
  - add a body-based return endpoint or adjust the existing contract so the UI can return by ISBN instead of transaction ID

The exact endpoint shape should favor OpenAPI-style DTO naming already present in the codebase and should continue returning business error codes through `ApiResponse`.

## Frontend Structure Changes

Suggested component reshaping:

- `LibraryDashboardPage.vue`
  - orchestrates top bar, transaction panel, create-book panel, and inventory table
- `TopBar` or equivalent
  - presents product title, search input, and admin identity
- `TransactionPanel`
  - owns borrow/return tabs and emits typed actions
- `CreateBookPanel`
  - upgraded to the richer form
- `BookCollectionPanel`
  - likely replaced by a table-oriented component better aligned with the target design

The composable should remain the place where API calls, optimistic refresh decisions, and feedback state are coordinated.

## Migration Strategy

1. Extend backend domain and DTOs first so the frontend has a stable contract to target.
2. Update frontend types and centralized API client.
3. Replace the current card-grid dashboard with the new console layout.
4. Update tests and scenario documentation last so they reflect the final flow instead of an intermediate state.

## Testing Strategy

- Backend:
  - service tests for create, borrow, return, and inactive/unavailable error paths
  - integration tests for expanded DTO contracts and business-code responses
- Frontend:
  - component or e2e smoke coverage for create, borrow, return, and inventory refresh flow
  - locator checks for preserved `data-testid` attributes

## Open Questions

- Whether search is implemented fully in this change or remains presentation-only for the first demo pass.
- Whether due date and overdue messaging are in scope for the backend or only represented visually later.
- Whether the Figma Make reference should be copied into `docs/figma/` to satisfy the repo’s design-reference convention before implementation starts.
