# Requirements: REQ-LIB-001

## Metadata

- Requirement ID: REQ-LIB-001
- Related Scenario: SCN-LIB-001
- Related Workflow: WF-LIB-001
- Stage: S1 SA

## Problem And Goal

- Problem:
  管理員目前無法以單一流程管理共享書櫃的館藏、借出與歸還，導致資訊分散且書籍狀態不可見。
- Business Goal:
  建立一個最小可用的 Library Mini Admin Console，支援館藏建立、搜尋、借出與歸還，並以 ISBN 作為主要識別。
- User Value:
  管理員能快速找到書籍並正確更新館藏狀態，降低共享書櫃管理混亂與人工確認成本。

## Scope

- In Scope:
  單頁管理台布局與互動
  書籍新增表單
  館藏搜尋
  書籍列表狀態顯示
  借出與歸還流程
  基本欄位驗證與 business error code 映射
- Out of Scope:
  身份驗證與授權
  讀者資料維護
  逾期與罰款規則
  報表分析與通知機制
  批次匯入匯出

## Actors And Permissions

- Actor: 書櫃管理員
  - Responsibilities:
    維護館藏、查詢館藏、執行借出與歸還、觀察狀態與庫存變化
  - Access constraints:
    僅能透過管理控制台進行操作，所有交易均以 ISBN 為主要識別欄位
- Actor: 借閱人
  - Responsibilities:
    提供讀者 ID 供管理員輸入借閱資訊
  - Access constraints:
    不直接操作系統

## Functional Requirements

1. Requirement: 系統必須提供單頁管理控制台
   - Trigger:
     管理員開啟 Library Mini Admin Console
   - System behavior:
     系統顯示頂部搜尋列、借還交易區、新增書籍表單與館藏列表
   - Success result:
     管理員可於同一頁面看見所有核心操作入口

2. Requirement: 系統必須支援新增書籍
   - Trigger:
     管理員提交新增書籍表單
   - System behavior:
     系統驗證書名、ISBN、分類與數量為必填，並建立新館藏資料；若 ISBN 已存在則回傳錯誤
   - Success result:
     新增成功後館藏列表立即顯示新書，包含書名、ISBN、作者、分類、狀態與可借數量

3. Requirement: 系統必須支援館藏搜尋
   - Trigger:
     管理員在搜尋列輸入書名、ISBN 或作者相關文字
   - System behavior:
     系統依輸入條件篩選館藏列表
   - Success result:
     管理員可快速定位目標書籍

4. Requirement: 系統必須支援借出書籍
   - Trigger:
     管理員提交借書表單或從館藏列表觸發借出操作
   - System behavior:
     系統以 ISBN 找到館藏，驗證讀者 ID 與借閱狀態，將可借數量減一；若剩餘數量為 0，狀態更新為 `已借出`
   - Success result:
     借出成功後畫面顯示成功訊息，列表同步更新可借數與狀態

5. Requirement: 系統必須支援歸還書籍
   - Trigger:
     管理員提交還書表單或從館藏列表觸發歸還操作
   - System behavior:
     系統以 ISBN 找到館藏並恢復可借數量；若書籍恢復為可借，狀態更新為 `可借閱`
   - Success result:
     歸還成功後畫面顯示成功訊息，列表同步更新可借數與狀態

6. Requirement: 系統必須處理不可借與不存在的錯誤情境
   - Trigger:
     管理員嘗試借出不存在、未上架或已全數借出的書籍，或嘗試歸還不存在或未借出的書籍
   - System behavior:
     系統回傳對應 business error code 與可理解訊息，不得僅依 HTTP status 呈現
   - Success result:
     管理員可明確理解失敗原因，且列表資料不被錯誤修改

## Business Rules

- Rule ID: BR-LIB-001
  - Description:
    ISBN 為書籍主識別欄位，借還交易與重複判斷均以 ISBN 為準
  - Source:
    Scenario 決策
  - Exception handling:
    若 ISBN 不存在，回傳 client error code

- Rule ID: BR-LIB-002
  - Description:
    `已借出` 表示該書可借數量等於 0，而非僅代表曾被借閱
  - Source:
    Scenario 決策
  - Exception handling:
    若歸還後可借數量大於 0，狀態需恢復為 `可借閱`

- Rule ID: BR-LIB-003
  - Description:
    未上架書籍可顯示於館藏與搜尋結果，但不得借出
  - Source:
    Figma 與 scenario 假設
  - Exception handling:
    借出時回傳 client error code 與對應錯誤訊息

- Rule ID: BR-LIB-004
  - Description:
    同一 ISBN 不可重複建立第二筆館藏主資料
  - Source:
    最小可用館藏設計
  - Exception handling:
    新增時回傳 client error code，提示 ISBN 已存在

## Non-Functional Requirements

- Performance:
  管理台常用操作需在單次互動中回應，館藏搜尋在一般小型書櫃資料量下應提供即時或近即時回饋
- Security:
  本階段不納入登入流程，但 API 與 UI 錯誤處理仍需遵循 business error code 規範，不暴露不必要內部訊息
- Auditability:
  至少需保留欄位與流程設計可支援後續擴充借還紀錄
- Accessibility:
  表單欄位、狀態標示與按鈕需具可辨識文字，並保留穩定 `data-testid`

## Acceptance Criteria

- Given:
  管理員已進入控制台，且館藏列表中尚未存在輸入的 ISBN
- When:
  管理員新增一本有效書籍
- Then:
  系統建立館藏資料，並在列表中顯示書名、ISBN、狀態與可借數量

- Given:
  館藏列表中存在可借的 `Clean Code`，其 ISBN 為有效資料，且可借數大於 0
- When:
  管理員輸入讀者 ID 與 ISBN 執行借出
- Then:
  系統顯示借出成功，可借數減一；若剩餘可借數為 0，狀態更新為 `已借出`

- Given:
  館藏列表中存在已借出的書籍
- When:
  管理員以 ISBN 執行歸還
- Then:
  系統顯示歸還成功，可借數增加，且若可借數大於 0，狀態更新為 `可借閱`

- Given:
  管理員輸入不存在或不可借的 ISBN
- When:
  管理員執行借出或歸還
- Then:
  系統回傳對應 business error code 與錯誤訊息，且不應修改既有館藏資料

## Dependencies And Open Questions

- Dependencies:
  前端需依循 Figma Make `Library Mini Admin Console` 畫面結構
  後端需提供館藏查詢、建立、借出、歸還 API
  前後端需共用一致的 DTO 與 business error code 契約
- Open Questions:
  搜尋要採 client-side filter 還是 server-side query
  快速借出與快速歸還的欄位帶入策略是否需要固定
  是否需要在第一版即保留借閱人資訊於列表顯示
