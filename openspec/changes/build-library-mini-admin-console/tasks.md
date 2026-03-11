# Tasks: Build Library Mini Admin Console

## 1. Prepare design reference and demo narrative

- [x] Capture the chosen Figma reference under `docs/figma/` or document an equivalent local reference path for implementation.
- [x] Update `docs/scenarios/SCN-LIB-001.md` into a product-demo script aligned with the target console flow.
- [x] Record any terminology decisions for Chinese UI copy so frontend and docs stay consistent.

## 2. Expand backend domain and API contracts

- [x] Extend the book domain model with ISBN, category, and active/inactive shelf state.
- [x] Update request and response DTOs so book creation, listing, checkout, and return expose the fields required by the new UI.
- [x] Adjust service logic to support ISBN-driven borrow and return flows while preserving business-code error handling.
- [x] Update controller and service tests for the revised contracts and validation paths.

## 3. Update frontend types and data access

- [x] Update `src/types/library.ts` to match the revised backend DTOs without manual field renaming outside an adapter layer.
- [x] Extend `src/services/libraryApi.ts` to use the centralized API client for the new create, borrow, and return payloads.
- [x] Refactor `useLibraryDashboard.ts` so it can manage transaction-form submissions, refreshed inventory state, and mapped error feedback.

## 4. Rebuild the dashboard UI around the target console layout

- [x] Replace the current hero-plus-card-grid layout with a top bar, transaction panel, create-book panel, and inventory table.
- [x] Upgrade the create-book form to collect title, ISBN, author, category, quantity, and shelf status.
- [x] Add borrow and return forms with stable `data-testid` coverage and explicit success/error feedback areas.
- [x] Replace book cards with a table-oriented inventory component that supports quick borrow and quick return actions.

## 5. Verify demo readiness

- [x] Update frontend smoke or e2e coverage to exercise the `SCN-LIB-001` walkthrough.
- [x] Verify all new and preserved `data-testid` locators remain stable for QA.
- [x] Confirm the final UI and interaction flow align with the selected Figma reference and demo script.
