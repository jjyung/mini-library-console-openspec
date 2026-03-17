# Requirements: REQ-LIB-001

## Metadata

- Requirement ID: REQ-LIB-001
- Related Scenario: SCN-LIB-001
- Related Workflow: WF-LIB-001
- Stage: S1 SA

## Problem And Goal

- Problem: 團隊共享書櫃缺少一致的借閱管理方式，導致館藏是否可借、誰借走了哪一本書，以及何時歸還都缺乏可追蹤資訊。
- Business Goal: 提供一個最小可用的管理主控台，讓使用者能在單一畫面完成建書、補充庫存、借出與歸還，並維持借閱資訊與庫存數量一致。
- User Value: 管理員能快速建立與維護館藏，同事能清楚知道書是否可借，團隊對借閱狀態有共同可信的視圖。

## Scope

- In Scope:
  - 建立書籍資料，包含書名、作者與初始庫存數量。
  - 顯示書籍清單、每本書的 total copies、available copies、checked out copies、status 與 active borrowing。
  - 針對既有書籍補充 copies。
  - 以借閱人姓名執行借出。
  - 從 active borrowing 執行歸還。
  - 顯示首頁總覽統計與操作回饋訊息。
  - 所有 API 回應需包含 business error code。
- Out of Scope:
  - 登入、角色權限與身份驗證整合。
  - 書籍刪除與既有資料編輯。
  - 借閱逾期管理、通知、罰則與預約機制。
  - 與公司成員目錄、外部圖書系統或第三方通知服務整合。

## Actors And Permissions

- Actor: 共享書櫃管理員
  - Responsibilities:
    - 建立書籍與補充庫存。
    - 協助借閱與歸還操作。
    - 觀察館藏總覽與借閱紀錄。
  - Access constraints:
    - 目前無登入流程，預設可操作所有功能。
- Actor: 一般借閱同事
  - Responsibilities:
    - 提供借閱人姓名以完成借出。
    - 配合管理員或直接在共享介面完成歸還。
  - Access constraints:
    - 目前與管理員共用同一介面，不另設權限差異。

## Functional Requirements

1. Requirement: 建立書籍資料
   - Trigger: 使用者在建立書籍表單輸入 title、author、initialCopies 並送出。
   - System behavior: 系統透過 centralized API client 呼叫建立書籍 API，驗證欄位必填與數量大於等於 1，成功後刷新書籍清單與首頁總覽。
   - Success result: 新書出現在書籍清單中，書籍卡片顯示正確的作者、庫存與狀態。
2. Requirement: 補充既有書籍庫存
   - Trigger: 使用者在單一本書的 add copies 表單輸入 additionalCopies 並送出。
   - System behavior: 系統呼叫補充 copies API，驗證 additionalCopies 大於等於 1，成功後更新該書與總覽統計。
   - Success result: total copies 與 available copies 增加，且不新增重複書籍資料。
3. Requirement: 執行借出並記錄借閱人
   - Trigger: 使用者在書籍卡片輸入 borrowerName 並送出 checkout。
   - System behavior: 系統驗證 borrowerName 與 bookId，不可對無可借庫存的書執行借出；借出成功後建立 active borrowing 紀錄，更新可借數量、借出數量與書籍狀態。
   - Success result: active borrowing 顯示借閱人與借出時間，available copies 減少，checked out copies 增加。
4. Requirement: 執行歸還並清除 active borrowing
   - Trigger: 使用者點擊某筆 active borrowing 的 return 操作。
   - System behavior: 系統以 transactionId 呼叫歸還 API；若交易不存在或已歸還，需回傳對應 client error；成功時更新書籍卡片與總覽。
   - Success result: 對應借閱紀錄從 active borrowing 消失，available copies 恢復，checked out copies 減少。
5. Requirement: 顯示統一的操作回饋與錯誤資訊
   - Trigger: 任一建立、補庫存、借出、歸還或刷新操作完成或失敗。
   - System behavior: 前端需依 business error code 將結果映射為可讀訊息，不能只依賴 HTTP status；系統錯誤與 client error 需區分。
   - Success result: 使用者能從 feedback 區塊理解操作結果，且錯誤訊息與業務情境一致。

## Business Rules

- Rule ID: LIB-BR-001
  - Description: 所有書籍建立與補庫存數量都必須大於等於 1。
  - Source: 共享書櫃最小可用資料品質要求與現有 DTO 驗證。
  - Exception handling: 若數量小於 1，API 回傳 `A0000`，前端顯示對應欄位錯誤訊息。
- Rule ID: LIB-BR-002
  - Description: 書名、作者、bookId 與 borrowerName 都視為必填，不可為空白字串。
  - Source: 建立與借閱流程的基本識別需求。
  - Exception handling: 若欄位缺失或空白，API 回傳 `A0000`，訊息需指出失敗欄位。
- Rule ID: LIB-BR-003
  - Description: 當某書 `availableCopies` 為 0 時，不可再執行 checkout。
  - Source: 館藏可借副本數不得為負數。
  - Exception handling: API 回傳 `A0000`，前端需阻擋或明確顯示無法借出。
- Rule ID: LIB-BR-004
  - Description: `status` 由庫存與借出中的 active borrowing 自動計算，不由前端直接提交。
  - Source: 避免狀態與實際庫存脫鉤。
  - Exception handling: 若計算結果異常，視為系統錯誤並回傳 `B0000`。
- Rule ID: LIB-BR-005
  - Description: 所有業務成功回應使用 `00000`，可預期的使用者輸入或業務違規使用 `A0000`，未處理的系統錯誤使用 `B0000`。
  - Source: 專案 business error code 規範。
  - Exception handling: 前端必須依 code 映射訊息，不得僅依 HTTP status 判斷。

## Non-Functional Requirements

- Performance:
  - 在單頁操作情境下，建立、補庫存、借出、歸還後應於一次刷新流程內反映最新清單與總覽。
  - 書籍清單排序需穩定，避免操作後跳動造成辨識困難。
- Security:
  - 目前不導入登入，但仍需維持後端輸入驗證與錯誤封裝，避免未驗證資料直接進入業務流程。
  - 前端不得硬編 API 基底 URL，需透過 centralized client 呼叫相對路徑。
- Auditability:
  - 借閱紀錄至少需保留 transactionId、borrowerName 與 checkedOutAt 供畫面顯示與後續追蹤。
  - API 合約需能追溯對應 API ID。
- Accessibility:
  - 主要操作按鈕、輸入欄位與回饋訊息需維持可辨識語意與可互動狀態。
  - UI 調整不可破壞既有 `data-testid`，以確保自動化驗證持續可用。

## Acceptance Criteria

- Given: 系統目前沒有任何書籍資料
- When: 使用者建立一本 title 為 `Clean Code`、author 為 `Robert C. Martin`、initialCopies 為 `2` 的書籍
- Then: 書籍清單出現對應卡片，首頁總覽顯示 titles 增加，且該書 available copies 為 `2`

- Given: 書籍 `Clean Code` 已存在且目前 total copies 為 `2`
- When: 使用者補充 `1` 本 copies
- Then: 該書 total copies 變為 `3`，available copies 同步變為 `3`

- Given: 書籍 `Clean Code` available copies 為 `3`
- When: 使用者以 borrowerName `Samson` 執行 checkout
- Then: active borrowing 顯示 `Samson`，checked out copies 變為 `1`，available copies 變為 `2`

- Given: `Samson` 的借閱紀錄仍為 active
- When: 使用者執行 return
- Then: active borrowing 清單移除該紀錄，checked out copies 變為 `0`，available copies 恢復為 `3`

- Given: 某書 available copies 已為 `0`
- When: 使用者再次嘗試 checkout
- Then: 系統不得建立新的借閱紀錄，並回傳 `A0000` 對應的可理解錯誤訊息

## Dependencies And Open Questions

- Dependencies:
  - 前端需延續現有 centralized API client 與 feedback 映射邏輯。
  - 後端需延續現有 business error code 與 validation 行為。
  - Figma 視覺對齊需以需求對應的最新設計來源為準，且不破壞 `data-testid`。
- Open Questions:
  - `補充庫存` 是否要作為主要 demo 流程的必要步驟，或保留為次要操作。
  - `status` 是否只保留 `AVAILABLE` / `CHECKED_OUT` 兩種狀態，後續是否需要擴充。
  - 未來若導入登入，是否需要將借閱人從自由輸入改為受控身分來源。
