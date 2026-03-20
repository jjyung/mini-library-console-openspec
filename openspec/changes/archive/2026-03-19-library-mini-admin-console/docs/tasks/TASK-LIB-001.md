# Tasks: TASK-LIB-001

> This file is change-local only. Do not promote it to repo-level `docs/**`.
> Use it for apply tracking and short-lived implementation coordination only.

## Metadata

- Task ID: TASK-LIB-001
- Related Scenario: SCN-LIB-001
- Related Requirement: REQ-LIB-001
- Related Architecture: ARCH-LIB-001
- Related Contract: docs/openapi/openapi.yaml

## Implementation Goal

- Goal:
  完成符合 Figma 參照的小型圖書櫃管理控制台，支援搜尋、建書、借書與還書的最小可用流程
- Scope boundary:
  僅實作 SCN-LIB-001 / REQ-LIB-001 / ARCH-LIB-001 / OpenAPI 契約所定義範圍，不包含登入、逾期罰款、報表與批次處理
- Key risk:
  UI 操作與後端狀態映射不一致，或在調整介面時破壞既有 `data-testid`

## Preconditions

- [x] Scenario is confirmed
- [x] Requirements are baselined
- [x] Architecture is baselined
- [x] API contract is baselined

## Task Checklist

- [x] T1 Confirm implementation entry point and impacted modules
  完成定義：確認前端頁面、狀態管理、service layer、centralized API client、後端館藏模組與借還模組的影響範圍
- [x] T2 Implement book list and search flow
  完成定義：實作館藏列表與搜尋條件輸入，支援書名、ISBN、作者查詢，並保留 Figma 版面與 `data-testid`
- [x] T3 Implement add-book flow
  完成定義：串接新增書籍 API、處理 ISBN 重複與必填驗證，成功後刷新或更新館藏列表
- [x] T4 Implement borrow and return flow
  完成定義：串接借出與歸還 API，正確更新 `availableCount`、`status` 與成功/失敗提示；全數借出時不可再借
- [x] T5 Handle validation and business error code mapping
  完成定義：所有前端錯誤提示與後端錯誤回應均依 business error code 映射，不只依賴 HTTP status
- [x] T6 Add or update automated tests
  完成定義：補上至少涵蓋搜尋、新增、借出、歸還與主要錯誤情境的測試或等價驗證
- [x] T7 Verify UI locator integrity and contract alignment
  完成定義：確認 `data-testid` 未被破壞，且 DTO / API path / operation 行為與 OpenAPI 一致
- [x] T8 Record follow-up notes for QA or handoff
  完成定義：整理已知限制、未決議題與建議 QA 驗證重點

## Verification Notes

- Functional verification:
  已完成 `npm run type-check` 與 `./mvnw test`；驗證新增後列表更新、搜尋可定位目標、借出後數量下降、歸還後數量恢復、全數借出時無法再次借出
- Edge cases:
  已覆蓋 ISBN 不存在、ISBN 重複、未上架書籍借出、未借出書籍歸還；搜尋無結果仍建議於 UI 手動驗證
- Rollback or mitigation note:
  若 UI 與 API 契約不同步，先以 OpenAPI 為準修正 DTO 與錯誤碼映射，再回補畫面互動

## Definition Of Done

- [x] The requested behavior is implemented
- [x] Contract and implementation remain aligned
- [x] Validation and error handling are covered
- [x] Tests or manual verification are recorded
