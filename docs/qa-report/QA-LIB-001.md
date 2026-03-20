# QA Report: QA-LIB-001

## Metadata

- QA Report ID: QA-LIB-001
- Related Workflow: WF-LIB-001
- Stage: S6 QA
- Result: pass

## Test Scope

- Covered flows:
  單頁管理控制台載入
  館藏搜尋
  新增書籍
  借出書籍
  歸還書籍
  business error code 映射
  `data-testid` 穩定性
- Environment:
  FE: `apps/web/library-mini-admin-web`
  BE: `apps/api/library-mini-admin-api`
  驗證方式: TypeScript 型別檢查、Spring Boot 自動化測試、Playwright smoke spec 更新
- Test data:
  `Clean Code`
  `Domain-Driven Design`
  `Designing Data-Intensive Applications`
  測試 ISBN:
  `978-0-13-235088-4`
  `978-0-321-12521-7`
  `978-1-449-37332-0`
  `978-9-99-999999-9`

## Execution Summary

- What was tested:
  前端單頁管理台已切換為 TopBar / TransactionCard / AddBookForm / BookTable 結構，並透過 centralized API client 串接新的 ISBN 導向 API。
  後端已驗證館藏查詢、建立、借出、歸還路徑與 business error code。
  自動化驗證包含 `npm run type-check` 與 `./mvnw test`。
- What passed:
  館藏查詢支援關鍵字搜尋書名、ISBN、作者。
  新增書籍支援 ISBN、分類、數量、上架狀態。
  借出與歸還流程以 ISBN 為主要識別，並能正確更新 `availableCount` 與 `status`。
  錯誤情境已驗證 `A0400`、`A0404`、`A0409`、`A0410`、`A0411` 類型映射邏輯。
  `data-testid` 已保留在主要互動元素與清單節點。
- What failed:
  無阻塞缺陷。
- What was not tested:
  未實際執行 `npm run test:e2e`。
  搜尋無結果的最終 UI 呈現僅做設計與手動驗證建議，尚未有獨立自動化斷言。
  行動裝置實機與跨瀏覽器驗證尚未執行。

## Defect List

- DEF-001
  - Severity: low
  - Owner: FE
  - Status: accepted
  - Reproduction steps:
    1. 進入控制台
    2. 輸入不存在的搜尋關鍵字
    3. 觀察空清單呈現
  - Expected result:
    顯示明確的空搜尋結果提示，與初始空館藏提示語意可區分
  - Actual result:
    目前僅有共用空狀態文案，搜尋無結果與尚無館藏未完全分流
  - Fix plan:
    若後續 UX 要求更清楚，可在 FE 增加「無符合搜尋結果」的專用提示與測試

## Re-entry Decision

- If implementation bug:
  若後續出現借還數量或狀態不同步，回流 FE 與 BE 一起檢查 DTO 與狀態映射
- If contract gap:
  若後續需要補充搜尋參數、狀態列舉或借閱資訊欄位，回流 SD 更新 OpenAPI 與 DTO
- If design conflict:
  若 Figma 後續對快速借出 / 快速歸還互動有新定義，回流 Archi 與 FE 更新設計決策
- If requirement ambiguity:
  若搜尋無結果、未上架書籍顯示策略或借閱人資訊曝光範圍需要調整，回流 SA 補充需求

## Final Recommendation

- Can move to S7 Done:
  可以。核心流程已實作完成，主要自動化驗證已通過，無阻塞缺陷。
- Required follow-up actions:
  建議在封存前保留目前驗證結果。
  若要提高 QA 信心，可後續補跑 `npm run test:e2e` 與手動 RWD 驗證。
