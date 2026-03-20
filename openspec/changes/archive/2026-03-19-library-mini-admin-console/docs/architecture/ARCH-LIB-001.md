# Architecture: ARCH-LIB-001

## Metadata

- Architecture ID: ARCH-LIB-001
- Related Requirement: REQ-LIB-001
- Related Workflow: WF-LIB-001
- Stage: S2 Archi

## Architectural Goal

- Why this design exists:
  以最小可用方式建立一套前後端可對齊的館藏管理架構，支援單頁管理台中的新增、搜尋、借出與歸還流程。
- Target quality attributes:
  一致的狀態更新、清晰的 UI/Domain 邊界、可擴充的 API 契約、穩定的測試定位與需求可追溯性。

## Context And Boundaries

- Upstream systems:
  無外部上游系統；第一版由管理台直接操作本服務 API
- Downstream systems:
  書籍館藏資料儲存層與借還交易紀錄儲存層
- Internal modules:
  前端管理控制台、前端 API client、後端館藏模組、後端借還模組、共享 DTO 與錯誤碼定義
- Ownership boundaries:
  FE 負責畫面呈現、輸入驗證與狀態回饋；BE 負責館藏與借還規則、資料一致性與 business error code；共享契約負責 DTO、API ID 與狀態列舉對齊

## Solution Outline

- Frontend responsibilities:
  實作符合 Figma 的單頁控制台，包含 TopBar 搜尋列、TransactionCard 借還區、AddBookForm 新增區、BookTable 館藏列表。所有 API 呼叫需透過 centralized API client，不得於 component 內直接使用 fetch/axios。UI 必須保留既有 `data-testid`。
- Backend responsibilities:
  提供館藏查詢、建立、借出與歸還 API；以 ISBN 為主要識別執行驗證與狀態更新；統一回傳 business error code；確保全數借出與未上架等規則由後端主導。
- Shared contracts:
  定義 `BookStatus`、書籍欄位 DTO、借還 request/response DTO 與錯誤 response DTO；OpenAPI 文件作為 FE/BE 對齊基準。

## Data And Control Flow

1. Request entry
   管理員在單頁控制台輸入搜尋條件或提交新增、借出、歸還表單；前端透過 centralized API client 發出請求。
2. Validation
   前端先做必填與基本格式檢查；後端再驗證 ISBN 是否存在、數量與狀態是否合法、讀者 ID 是否提供。
3. Business processing
   後端根據 API 類型建立館藏、篩選館藏，或執行借還操作；借出時扣減 `availableCount`，歸還時增加 `availableCount`，並依結果調整 `status`。
4. Persistence or integration
   後端更新書籍主檔與必要的借還紀錄資料；第一版不依賴外部第三方系統。
5. Response and error mapping
   後端以 `00000` 或對應錯誤碼回傳結果；前端依 business error code 顯示成功或錯誤訊息並更新列表，不可只依賴 HTTP status。

## Key Decisions

- Decision:
  採用單頁管理台作為唯一操作入口
  - Reason:
    與 Figma 參照一致，並符合最小可用管理流程
  - Alternatives considered:
    將搜尋、館藏、借還分成多頁
  - Trade-offs:
    單頁操作效率高，但需要更清楚的局部狀態管理與元件責任切分

- Decision:
  以 ISBN 作為書籍主識別欄位
  - Reason:
    可避免同名書籍造成歧義，並符合 scenario 已確認決策
  - Alternatives considered:
    以書名或內部流水號作為主要識別
  - Trade-offs:
    ISBN 較穩定，但 UI 仍需同時呈現書名以維持可讀性

- Decision:
  將 `已借出` 定義為可借數量為 0 的狀態
  - Reason:
    可讓狀態與庫存數量有單一一致語意
  - Alternatives considered:
    只要發生借閱就標示為已借出
  - Trade-offs:
    多冊書籍仍可能處於 `可借閱`，需搭配可借數顯示避免誤解

- Decision:
  第一版排除逾期與罰款流程
  - Reason:
    使用者已明確要求忽略該訊號，避免最小可用範圍被擴大
  - Alternatives considered:
    保留逾期欄位與罰款顯示
  - Trade-offs:
    範圍更聚焦，但未來若要擴充需調整借還紀錄模型

## Risks And Mitigations

- Risk:
  搜尋若僅由前端過濾，資料量成長後可能失去一致性與效能
  - Impact:
    列表結果與後端真實資料可能不同步
  - Mitigation:
    OpenAPI 預留查詢參數，第一版可先支援簡單查詢，後續再切換為後端查詢

- Risk:
  快速借出與表單借出共存可能造成操作認知混淆
  - Impact:
    使用者不確定按鈕是否直接送出或僅帶入資料
  - Mitigation:
    在需求與 UI 文案中明確定義快速操作行為，並補上對應測試

- Risk:
  UI 狀態與後端狀態映射不一致
  - Impact:
    列表顯示與真實庫存不一致，造成館藏判斷錯誤
  - Mitigation:
    由後端回傳標準化狀態列舉與數量欄位，前端只做展示與行為禁用

## Readiness For SD

- Inputs ready for OpenAPI:
  已確認核心資源為 `books` 與 `loans`，並明確定義搜尋、建立、借出、歸還四類操作與錯誤情境
- Inputs ready for FE and BE planning:
  已定義單頁 UI 模組邊界、ISBN 主識別、狀態轉換規則與 centralized API client 原則
- Outstanding questions:
  搜尋最終採即時前端過濾、後端查詢，或混合模式
  快速借出是否應直接帶入借書表單而非立即送出
