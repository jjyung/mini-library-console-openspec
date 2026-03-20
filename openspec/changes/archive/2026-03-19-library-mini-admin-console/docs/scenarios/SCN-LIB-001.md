# Scenario: SCN-LIB-001

## Metadata

- Scenario ID: SCN-LIB-001
- Title: 小型圖書櫃管理控制台
- Owner: TBD
- Priority: high
- Status: draft
- Related Workflow: WF-LIB-001

## Business Goal

- Problem Statement:
  公司共享書櫃缺乏一致的館藏維護與借還管理方式，導致管理員無法快速確認書籍是否存在、是否可借，以及目前是否已全數借出。
- Expected Outcome:
  建立一個最小可用的管理控制台，讓管理員可在單一頁面完成搜尋、建書、借書與還書，並即時觀察館藏狀態變化。
- Success Metrics:
  管理員能在不切換頁面的情況下完成核心操作，且每次操作後都能從畫面直接確認列表、狀態與可借數量是否正確更新。

## Target Users / Roles

- Primary Users:
  書櫃管理員
- Supporting Roles:
  借閱人，以讀者 ID 表示，不直接操作管理控制台
- Permissions / Constraints:
  本 scenario 僅討論管理員操作；借閱資料只要求輸入讀者 ID，不納入完整會員權限與身份管理。

## User Journey

1. Entry point
   管理員進入 Library Mini Admin 控制台，看到頂部搜尋列、借還交易區、新增書籍表單與館藏列表。
2. Key actions
   管理員新增多本書籍，填寫書名、ISBN、作者、分類、數量與上架狀態；之後可透過搜尋列依書名、ISBN、作者快速定位館藏。當 Samson 想借閱 `Clean Code` 時，管理員輸入讀者 ID 與 ISBN 完成借出；當書籍歸還時，管理員輸入 ISBN 完成歸還。
3. System feedback
   新增成功後，館藏列表立即顯示新資料；搜尋可縮小列表範圍；借出成功後可借數量減少，若剩餘數量為 0，狀態改為 `已借出`；歸還成功後可借數量恢復，若恢復可借，狀態改為 `可借閱`。
4. Completion state
   管理員可在同一控制台中確認館藏資料、搜尋結果與借還結果，並以 ISBN 作為主要識別完成操作。

## Scope

- In Scope:
  單頁管理控制台
  館藏搜尋
  新增書籍
  借出書籍
  歸還書籍
  館藏列表狀態顯示
  書名與 ISBN 同時呈現，並以 ISBN 作為主要識別
  Figma 參照中的 TopBar、TransactionCard、AddBookForm、BookTable 版面結構
- Out of Scope:
  登入與權限驗證
  會員主檔管理
  逾期與罰款流程
  批次匯入匯出
  統計報表
  多據點或多書櫃管理

## Assumptions And Constraints

- Assumptions:
  每本書都有唯一 ISBN。
  借還交易以 ISBN 為主鍵處理。
  讀者資訊在本 scenario 中僅以讀者 ID 表示。
  `已借出` 代表該書可借數量為 0。
- Constraints:
  UI 必須對齊 Figma Make `Library Mini Admin Console` 的單頁管理台布局。
  搜尋至少支援書名、ISBN、作者。
  所有 UI 調整不得破壞既有 `data-testid` 定位。
  不處理逾期、罰款與進階借閱規則。

## Acceptance Direction

- Must have outcomes:
  管理員可在單一頁面完成搜尋、建書、借書與還書。
  館藏列表可同時顯示書名與 ISBN。
  ISBN 為借還操作的主要識別欄位。
- Observable behaviors:
  新增書籍後，列表新增對應資料列。
  搜尋輸入後，列表能依條件縮小結果。
  借出後可借數下降，必要時狀態改為 `已借出`。
  歸還後可借數回升，必要時狀態改為 `可借閱`。
  全數借出時，不可再次借出。
- Risks to validate:
  搜尋條件是否需支援模糊查詢。
  同一本書多冊時，列表與狀態是否足夠清楚。
  快速借出與快速歸還按鈕與表單操作是否會造成認知混淆。

## Open Questions

- Q1: 搜尋結果是否需要即時過濾，還是提交後才更新列表？
- Q2: 快速借出按鈕是否應自動帶入 ISBN 至借書表單？
- Q3: 未上架書籍是否可被搜尋但不可借閱？
