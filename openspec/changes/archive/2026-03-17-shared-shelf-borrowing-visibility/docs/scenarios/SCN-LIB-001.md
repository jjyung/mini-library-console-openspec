# Scenario: SCN-LIB-001

## Metadata

- Scenario ID: SCN-LIB-001
- Title: 共享書櫃館藏與借閱可視化
- Owner: Product / Mini Library Console
- Priority: high
- Status: draft
- Related Workflow: WF-LIB-001

## Business Goal

- Problem Statement: 團隊共享書櫃目前缺少一致的借閱管理方式，常出現書被拿走後無法確認借閱人、書是否仍可借、以及哪些書長時間未歸還的情況。
- Expected Outcome: 使用者可以在單一管理主控台完成建書、補充庫存、借出書籍與歸還書籍，並即時看到館藏與借閱狀態的變化。
- Success Metrics:
  - 管理員可在空狀態下建立至少一本書並看到書籍卡片出現在清單中。
  - 使用者可完成一次借閱流程並在畫面中辨識借閱人與庫存變化。
  - 使用者可完成一次歸還流程並確認可借數量恢復、借閱紀錄清空。
  - 首頁總覽資訊可反映書籍總數、可借數量與借出數量。

## Target Users / Roles

- Primary Users: 共享書櫃管理員、一般借閱同事。
- Supporting Roles: 團隊成員、後續驗證流程中的 QA 與需求整理人員。
- Permissions / Constraints:
  - 目前假設為單一共享管理介面，無登入與角色權限差異。
  - 借閱人以文字名稱輸入，不串接員工名單或身份驗證系統。
  - UI 調整不得破壞既有 `data-testid` 定位能力。

## User Journey

1. Entry point
   使用者開啟 Shared Shelf Console，先看到書櫃管理總覽、書籍建立表單，以及目前館藏清單或空狀態提示。
2. Key actions
   管理員先建立書籍資料，輸入書名、作者與初始庫存數量；若後續需要擴充館藏，可針對單一本書補充庫存；當同事要借書時，使用者在書籍卡片輸入借閱人姓名並執行借出；當書籍歸還時，使用者從 active borrowing 區塊完成歸還。
3. System feedback
   畫面需提供成功或錯誤訊息，並即時更新書籍卡片中的 total copies、available copies、checked out copies、status 與 active borrowing 清單；頁首 summary 也需同步更新。
4. Completion state
   完成一輪建立、借出、歸還後，館藏狀態仍保持可追蹤，借閱記錄可被辨識，且系統能正確反映書籍是否可借。

## Scope

- In Scope:
  - 建立書籍資料。
  - 顯示書籍清單與每本書的庫存摘要。
  - 補充既有書籍的 copies。
  - 以借閱人姓名執行借出。
  - 顯示 active borrowing 與歸還操作。
  - 顯示首頁總覽資訊與操作回饋。
- Out of Scope:
  - 登入、身份驗證、角色權限控管。
  - 書籍刪除與編輯既有書籍基本資料。
  - 借閱逾期規則、罰則與提醒通知。
  - 書籍預約、候補、批次匯入匯出。
  - 員工通訊錄或第三方目錄整合。

## Assumptions And Constraints

- Assumptions:
  - 同一本書可以有多個 copy，系統以單一書籍卡片聚合顯示。
  - 借閱人資訊先以自由輸入文字處理，不要求唯一識別。
  - 使用者操作時期待畫面能立即反映最新狀態。
- Constraints:
  - 此情境需對齊最小可用產品定位，不擴張到完整圖書館系統。
  - 前端 API 呼叫需延續 centralized API client 的做法。
  - 錯誤處理需對齊 business error code，而非僅依賴 HTTP status。
  - 視覺與互動應參照需求對應的 Figma 來源，但不得破壞現有測試定位。

## Acceptance Direction

- Must have outcomes:
  - 使用者能從空狀態完成建立書籍、借出、歸還的完整流程。
  - 畫面中的庫存摘要與借閱資訊在每次操作後保持一致。
  - 借閱紀錄可以被辨識並支持歸還操作。
- Observable behaviors:
  - 建立書籍後，書籍卡片與總覽統計出現對應資料。
  - 補充庫存後，total copies 與 available copies 會增加。
  - 借出後，borrower 會出現在 active borrowing 區塊。
  - 歸還後，若無借閱紀錄，畫面顯示空的 borrowing state。
- Risks to validate:
  - 無可借庫存時，借出入口是否足夠清楚地阻擋錯誤操作。
  - 多筆 active borrowing 同時存在時，卡片資訊是否仍容易閱讀。
  - Figma 視覺層級若與現有實作不同，需確認 summary、feedback 與卡片資訊的優先級。

## Open Questions

- Q1: `補充庫存` 是否應被視為主 scenario 的必要步驟，還是保留為延伸場景？
- Q2: 是否需要在 scenario 後續 requirements 中明確定義 `status` 的轉換規則？
- Q3: 借閱人名稱未來是否要收斂為公司成員名單選擇，而不是自由輸入？
- Q4: Figma Make 連結後續是否會補充更明確的 frame / node，讓 UI 驗收基準更具體？
