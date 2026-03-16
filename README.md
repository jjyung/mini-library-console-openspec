# Mini Library Console OpenSpec

最小教學專案：**圖書館借書 / 還書**管理台（Vue + Spring Boot），用來示範透過 OpenSpec 留下 Spec 與實作。

## Quick Start

需要 `Node.js 20.19.0` 以上版本。

參考：<https://github.com/Fission-AI/OpenSpec>

全域安裝 OpenSpec：

```sh
npm install -g @fission-ai/openspec@latest
```

切換到專案目錄後初始化：

```sh
cd your-project
openspec init
```

初始化時選擇 AI tool: `Codex`

- OpenSpec 會自動安裝對應 skills
- 安裝完成後請重新啟動 IDE

## OpenSpec 指令介紹

- `openspec-explore`: 用來探索需求、整理問題、釐清範圍與前置假設。
- `openspec-propose`: 用來快速產出 change proposal、spec 與 tasks 等提案文件。
- `openspec-apply-change`: 用來依照既有 OpenSpec change 實作任務內容。
- `openspec-archive-change`: 用來在實作完成後封存 change 與收尾流程。

## 建議使用流程

建議依序使用：

`explore -> propose -> apply -> archive`

1. `explore`: 先釐清需求、限制、設計參考與實作風險。
2. `propose`: 產出正式提案，固定需求範圍、規格與 tasks。
3. `apply`: 依提案逐步實作並同步更新相關文件。
4. `archive`: 驗收完成後封存變更，保留完整決策與交付紀錄。

## 範例指令

```text
/prompts:opsx-explore UI 參照 https://www.figma.com/make/3ilP5JnPlkgXomqo70MsDW/Library-Mini-Admin-Console?p=f&fullscreen=1 幫我規劃 [SCN-LIB-001.md](docs/scenarios/SCN-LIB-001.md)
```

## 自定義 Workflow

在 OpenSpec 中，`config.yaml` 與 `schema.yaml` 分別代表了不同層次的自定義需求：前者用於調整「現有工作流的行為」，後者則用於定義「全新的工作流結構」。

以下是這兩個檔案的詳細說明：

### 1. `config.yaml` (專案配置)

這是最簡單的自定義方式，檔案位於 `openspec/config.yaml`。它主要用於設定專案的全局偏好，而不需要改變工作流的本質。

- **設定預設架構 (Default Schema)**：您可以指定一個預設的架構名稱，這樣在執行指令時就可以省略 `--schema` 參數。
- **注入專案上下文 (Project Context)**：您可以將專案的技術棧 (Tech Stack) 或開發慣例寫在這裡。這些資訊會出現在**所有**產出物的 AI 提示詞中。
- **添加特定產出物規則 (Per-artifact Rules)**：您可以針對特定的產出物 ID 設定規則，這些規則**僅在**生成該特定產出物時才會被注入到 AI 提示詞中。
- **解析順序**：在系統尋找架構時，`config.yaml` 的優先順序排在 CLI 標籤與變動元數據之後，但在系統預設值之前。

### 2. `schema.yaml` (自定義架構)

當專案配置不足以滿足需求時，您需要建立自定義架構，檔案位於 `openspec/schemas/<架構名稱>/schema.yaml`。它負責定義一個完整的工作流及其產出物之間的依賴關係。

- **核心欄位定義**：
  - **id**: 產出物的唯一識別碼，用於指令引用。
  - **generates**: 輸出的檔案路徑或名稱（支援 Glob 模式，如 `specs/**/*.md`）。
  - **template**: 指向 `templates/` 目錄下的模板文件，引導 AI 產出內容。
  - **instruction**: 提供給 AI 的具體指令，說明如何建立該產出物。
  - **requires**: 定義依賴關係，指定哪些檔案必須先生成。
- **靈活性**：您可以從頭開始建立，或是透過 **Fork** 現有的架構（如 `spec-driven`）來快速修改。
- **驗證機制**：在使用自定義架構前，可以透過 `openspec schema validate` 指令檢查語法、模板是否存在、以及是否有循環依賴等問題。

### 兩者的主要差別

| 特性           | `config.yaml`                  | `schema.yaml`                |
| :------------- | :----------------------------- | :--------------------------- |
| **主要目的**   | 調整偏好與全局規則             | 定義工作流結構與產出物       |
| **自定義層次** | 專案層次 (最簡單)              | 自定義架構層次 (進階)        |
| **影響範圍**   | 預設架構、全局上下文、特定規則 | 檔案命名、模板選擇、依賴關係 |
| **適用對象**   | 大多數團隊                     | 有獨特開發流程的團隊         |
