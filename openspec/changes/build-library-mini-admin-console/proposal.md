# Change Proposal: Build Library Mini Admin Console

## Summary

Refine the existing shared-shelf sample into a product-demo-ready library mini admin console that matches the intended single-page workflow: register books, borrow books, return books, and review live inventory state from one operational view.

## Why

The current project already contains a working Vue + Spring Boot sample, but it does not yet align with the target product story or the referenced UI direction:

- The current UI is an English shared-shelf dashboard, not a Chinese-language library admin console.
- The create-book flow only captures title, author, and initial copies, while the target demo needs ISBN, category, and shelving status.
- Borrow and return flows are embedded in per-book cards, while the target demo uses a dedicated transaction panel plus table-level quick actions.
- The scenario document for `SCN-LIB-001` describes a product-demo narrative that is broader than the current implementation.

Without this change, the repo demonstrates partial inventory operations but does not present a cohesive product walkthrough for the "small library cabinet management" use case.

## Goals

- Deliver a single-page admin console experience that supports the demo flow in `docs/scenarios/SCN-LIB-001.md`.
- Align the frontend layout and terminology with the referenced library admin console UI.
- Expand the backend contract so the frontend can manage book metadata and active borrow state required by the demo.
- Preserve existing `data-testid` integrity while introducing the new UI structure.

## Non-Goals

- Multi-user authentication or role-based access control.
- Persistent database storage beyond the current in-memory teaching setup.
- Reservation, fine settlement, or notification workflows.
- Bulk import/export, pagination, or advanced reporting.

## Scope

### In Scope

- Update the frontend page layout into top bar, transaction panel, create-book panel, and inventory table.
- Extend book creation to include ISBN, category, and active/inactive shelf state.
- Support borrow and return actions through dedicated forms and inventory quick actions.
- Extend API request and response DTOs to carry the fields required by the new UI.
- Update scenario documentation so the product demo script matches the implemented flow.

### Out of Scope

- Replacing the in-memory repositories.
- Introducing a full OpenAPI generation pipeline.
- Dark mode or theming variants not required by the demo.

## Success Criteria

- A presenter can complete the `SCN-LIB-001` flow in one screen: create books, borrow `Clean Code`, and return it.
- Inventory state visibly updates after each operation, including availability and borrowed status.
- API errors continue to map to business-code envelopes and are rendered through centralized frontend feedback handling.
- Existing UI tests can still target stable `data-testid` attributes after the layout change.

## Risks

- The current backend model does not yet track all target fields, so DTO and domain adjustments may ripple through tests.
- UI alignment work can drift away from the Figma-inspired demo if the implementation focuses only on functional parity.
- Quick actions and form-based actions must stay behaviorally consistent to avoid duplicate business logic paths.
