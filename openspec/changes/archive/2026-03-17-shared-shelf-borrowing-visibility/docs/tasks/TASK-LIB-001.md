# Tasks: TASK-LIB-001

## Metadata

- Task ID: TASK-LIB-001
- Related Scenario: SCN-LIB-001
- Related Requirement: REQ-LIB-001
- Related Architecture: ARCH-LIB-001
- Related Contract: docs/openapi/openapi.yaml

## Implementation Goal

- Goal: 讓共享書櫃管理台完整支援建書、補充庫存、借出、歸還、錯誤映射與驗證，並與文件契約保持一致。
- Scope boundary: 僅處理現有最小可用共享書櫃流程，不延伸到登入、預約、逾期通知或持久化資料庫。
- Key risk: 文件中的契約若與現有 FE / BE 命名或 envelope 行為不一致，apply 階段容易出現前後端脫鉤與測試失敗。

## Preconditions

- [x] Scenario is confirmed
- [x] Requirements are baselined
- [x] Architecture is baselined
- [x] API contract is baselined

## Task Checklist

- [x] T1 Confirm implementation entry point and impacted modules
- [x] T2 Implement the main business change
- [x] T3 Handle validation and business error code mapping
- [x] T4 Add or update automated tests
- [x] T5 Verify UI locator integrity and contract alignment if applicable
- [x] T6 Record follow-up notes for QA or handoff

## Verification Notes

- Functional verification:
  - 建立 `Clean Code` 後卡片出現且統計更新
  - 補充 `1` 本後 total / available 同步增加
  - 借出給 `Samson` 後 active borrowing 顯示借閱人與時間
  - 歸還後借閱紀錄消失且數量恢復
- Edge cases:
  - `initialCopies` 或 `additionalCopies` 小於 `1`
  - `borrowerName` 空白
  - `availableCopies` 為 `0` 時嘗試借出
  - `transactionId` 不存在或已歸還
- Rollback or mitigation note:
  - 若 apply 階段發現契約與現有程式差距過大，先以文件為基準修正命名與回應格式，再擴大功能調整。

## Session Notes

- Impacted modules:
  - FE: `src/composables/useLibraryDashboard.ts`, `src/services/httpClient.ts`, `e2e/library-smoke.spec.ts`
  - BE: `src/test/java/com/example/library/controller/LibraryApiIntegrationTest.java`
  - OpenSpec: `openspec/schemas/delivery-responsibility/schema.yaml`, `docs/tasks/tasks.md`
- Implementation summary:
  - 修正 OpenSpec `apply` tracking file 設定，避免 CLI 無法辨識 glob task path。
  - 將 FE business error feedback 改為前端映射文案，不直接顯示後端原始錯誤訊息。
  - 調整 dashboard refresh 流程，避免巢狀 action 吃掉刷新失敗後仍顯示成功訊息。
  - 新增回傳不存在 transactionId 時的 API integration test，並補上 FE E2E mapping 驗證案例。
- QA / handoff:
  - `data-testid` 已維持不變，既有 smoke locator 未改名。
  - `mvn test` 與 `npm run type-check` 通過。
  - `CI=1 npm run test:e2e` 因 sandbox 內 Chromium 啟動權限限制失敗，不是測試案例斷言失敗；若在本機可啟動 Playwright browser 的環境中重跑，應優先驗證新增的 business error feedback case。

## Definition Of Done

- [x] The requested behavior is implemented
- [x] Contract and implementation remain aligned
- [x] Validation and error handling are covered
- [x] Tests or manual verification are recorded
