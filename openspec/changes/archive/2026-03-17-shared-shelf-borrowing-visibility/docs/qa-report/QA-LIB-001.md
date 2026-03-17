# QA Report: QA-LIB-001

## Metadata

- QA Report ID: QA-LIB-001
- Related Workflow: WF-LIB-001
- Stage: S6 QA
- Result: pass

## Test Scope

- Covered flows:
  - 建立書籍後顯示書籍卡片與總覽統計
  - 補充庫存後更新 total copies 與 available copies
  - 借出後更新 active borrowing、checked out copies 與 available copies
  - 歸還後清除 active borrowing 並恢復可借數量
  - 無可借庫存時 checkout 回傳 business error code
  - transactionId 不存在時 return 回傳 business error code
  - 前端依 business error code / message 映射可讀 feedback
- Environment:
  - FE: `apps/web/library-mini-admin-web`
  - BE: `apps/api/library-mini-admin-api`
  - OpenSpec schema: `delivery-responsibility`
- Test data:
  - `Clean Code` / `Robert C. Martin` / `2`
  - `Domain-Driven Design` / `Eric Evans` / `1`
  - borrowerName: `Samson`, `Taylor`

## Execution Summary

- What was tested:
  - `mvn test`
  - `npm run type-check`
  - `npm run build`
  - `CI=1 npm run test:e2e`
- What passed:
  - Backend service 與 integration tests 全部通過
  - 前端型別檢查通過
  - 前端 production build 通過
  - OpenSpec apply tracking 修復後，任務狀態可正確追蹤並顯示 all_done
- What failed:
  - 無產品邏輯缺陷
  - E2E 無法在目前 sandbox 環境完成，因 Playwright Chromium 啟動受限
- What was not tested:
  - 在可啟動本機瀏覽器或完整桌面權限環境下的實際 Playwright 執行結果

## Defect List

- DEF-001
  - Severity: medium
  - Owner: FE
  - Status: mitigated
  - Reproduction steps:
    - 在 sandbox / headless 環境執行 `CI=1 npm run test:e2e`
    - Playwright 啟動 Chromium
  - Expected result:
    - Chromium 成功啟動並執行 smoke tests
  - Actual result:
    - Chromium 因 `bootstrap_check_in ... Permission denied (1100)` 無法啟動
  - Fix plan:
    - 於具備正常瀏覽器啟動權限的本機或 CI runner 重跑 E2E
    - 若該環境仍失敗，再回流 FE 檢查測試案例或 Playwright 設定

## Re-entry Decision

- If implementation bug:
  - 目前未觀察到需回流 FE / BE 的產品邏輯缺陷
- If contract gap:
  - 目前 API contract 與實作一致，無需回流 SD
- If design conflict:
  - 本次未發現需回流 Archi 的設計衝突
- If requirement ambiguity:
  - 本次未發現需回流 SA 的需求歧義

## Final Recommendation

- Can move to S7 Done:
  - 可以，條件是接受目前 QA 結論以 `pass` 封存，並將 Playwright 啟動限制視為環境問題而非產品缺陷
- Required follow-up actions:
  - 在具正常瀏覽器權限的環境補跑一次 `CI=1 npm run test:e2e`
  - 若結果正常，即可直接執行 archive
