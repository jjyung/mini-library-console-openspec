# Architecture: ARCH-LIB-001

## Metadata

- Architecture ID: ARCH-LIB-001
- Related Requirement: REQ-LIB-001
- Related Workflow: WF-LIB-001
- Stage: S2 Archi

## Architectural Goal

- Why this design exists: 以現有 Vue + Spring Boot 最小專案為基礎，建立一條清楚的共享書櫃管理主流程，讓 FE、BE 與 API 合約對同一組館藏與借閱規則保持一致。
- Target quality attributes:
  - 一致性：館藏數量、借閱紀錄與 summary 指標在操作後同步更新。
  - 可維護性：UI、API、業務邏輯與 DTO 契約各自分層，降低後續擴充成本。
  - 可驗證性：保留 `data-testid`、business error code 與明確 DTO schema，支援 E2E 與契約驗證。

## Context And Boundaries

- Upstream systems:
  - 目前無外部 upstream 系統，所有操作皆由管理台直接發起。
- Downstream systems:
  - 目前無資料庫或第三方整合，資料存於後端 in-memory repository。
- Internal modules:
  - FE: `LibraryDashboardPage`、`CreateBookPanel`、`BookCollectionPanel`、`BookCard`、`useLibraryDashboard`、`libraryApi`、`httpClient`
  - BE: `BookController`、`TransactionController`、`LibraryService` / `LibraryServiceImpl`、`BookRepository`、`BorrowTransactionRepository`
  - Shared contract: OpenAPI、DTO、business error code、`data-testid` 驗證約束
- Ownership boundaries:
  - FE 負責畫面組裝、操作流程、錯誤映射與 locator 穩定性。
  - BE 負責驗證、借閱規則、狀態計算與 business error code 封裝。
  - API 合約負責明確定義 request / response schema 與錯誤情境。

## Solution Outline

- Frontend responsibilities:
  - 以單一 dashboard 畫面承接建立書籍、補充庫存、借出、歸還與刷新操作。
  - 透過 composable 管理 loading、feedback 與清單刷新，不在 UI component 內直接呼叫 fetch。
  - 依 API 回應更新 summary、書籍卡片、active borrowing 與錯誤訊息。
- Backend responsibilities:
  - 提供 books 與 transactions 相關 API。
  - 驗證 title、author、bookId、borrowerName 與數量欄位。
  - 計算 available copies、checked out copies 與 `BookStatusEnum`。
  - 將可預期業務錯誤封裝為 `A0000`，未處理錯誤封裝為 `B0000`。
- Shared contracts:
  - API 以 `/api` 為 base path，實際資源路徑為 `/books` 與 `/transactions/*`。
  - DTO 命名遵循 Request / Response 規範，回應 envelope 包含 `code`、`message`、`data`。
  - FE 不得依賴 HTTP 200/500 之外的狀態碼來做主要業務判斷。

## Data And Control Flow

1. Request entry
   使用者在 FE dashboard 觸發建立、補庫存、借出、歸還或刷新；composable 透過 `libraryApi` 呼叫 centralized client。
2. Validation
   FE 先依 UI 狀態做基礎阻擋，例如無 available copies 時禁用 checkout；BE 再以 DTO annotation 與 service validation 做最終驗證。
3. Business processing
   `LibraryServiceImpl` 建立書籍、補充 copies、產生 borrow transaction、執行 return，並依 active transaction 計算狀態與數量。
4. Persistence or integration
   後端將 `Book` 與 `BorrowTransaction` 寫入 in-memory repositories；目前不串接外部資料來源。
5. Response and error mapping
   Controller 以 `ApiResponse` 回傳成功資料；`GlobalExceptionHandler` 將 client error 轉為 `A0000`、system error 轉為 `B0000`；FE `httpClient` 依 `code` 映射訊息並決定 feedback。

## Key Decisions

- Decision: 採用單頁 dashboard 串接完整借還書流程
  - Reason: 目前需求聚焦最小可用操作台，單頁能降低導覽成本並讓 summary、表單、書籍卡與借閱紀錄維持同一工作上下文。
  - Alternatives considered:
    - 分成「館藏管理」與「借閱管理」兩頁
    - 以 modal 分拆借閱與歸還流程
  - Trade-offs:
    - 單頁更直覺，但當資料量增加時畫面密度會上升。
- Decision: `status` 由後端依 available copies 自動計算
  - Reason: 狀態屬於衍生資料，交由後端集中計算可避免前後端對同一規則產生分歧。
  - Alternatives considered:
    - 前端自行推導狀態
    - 將狀態獨立儲存在資料模型中
  - Trade-offs:
    - 後端責任較重，但可換取更穩定的一致性。
- Decision: 保留 business error code 封裝並由 FE 做 code-based 映射
  - Reason: 專案規範要求 UI 不得只依賴 HTTP status，且這能為後續擴充更細緻錯誤映射留下空間。
  - Alternatives considered:
    - 直接將錯誤文案硬顯示在元件內
    - 單純以 HTTP status 驅動錯誤 UI
  - Trade-offs:
    - 需要維護額外 envelope schema，但契約清晰度更高。

## Risks And Mitigations

- Risk: 目前使用 in-memory repository，重啟後資料遺失
  - Impact: 只能支援示範與教學情境，不適合作為正式共享書櫃記錄來源。
  - Mitigation: 在文件中明確標註為 MVP 限制，後續若要持久化再擴充 repository 實作。
- Risk: 單一書籍卡片承載多個操作，資料量增加時可讀性下降
  - Impact: 使用者可能難以快速辨識 active borrowing 與庫存變化。
  - Mitigation: 先維持 MVP 卡片式設計，後續若資料量成長再引入篩選、排序或明細頁。
- Risk: Figma Make 來源未指定精確 frame，視覺驗收基準可能漂移
  - Impact: FE 實作可能與需求方預期的視覺層級不一致。
  - Mitigation: 在 propose 階段先以現有 UI 能力對齊，待取得明確 frame / node 後再補充設計基準。

## Readiness For SD

- Inputs ready for OpenAPI:
  - 已明確定義五個核心操作：list books、create book、add copies、checkout、return。
  - 已明確定義主要錯誤碼策略：`00000`、`A0000`、`B0000`。
  - 已明確定義主要 DTO 與衍生欄位，例如 `BookSummaryResponseDTO` 與 `ActiveTransactionResponseDTO`。
- Inputs ready for FE and BE planning:
  - FE 受影響模組、BE 受影響 controller / service / repository 邊界皆已清楚。
  - 驗證、錯誤處理與 `data-testid` 穩定性已納入設計限制。
- Outstanding questions:
  - 是否要在 SD 或 apply 前決定補庫存是否屬於主流程必要驗收。
  - 是否要保留現有英文 UI 文案，或後續一併做語系整理。
