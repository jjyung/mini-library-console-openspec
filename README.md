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

OpenSpec 預設的 `spec-driven` 使用方式，通常會讓使用者先理解為：

- `propose` 會產出 proposal、design、tasks 等可供後續實作的文件
- `apply` 會根據 schema 定義的任務文件逐項執行實作
- skills 的實際行為會參考 change 綁定的 schema，而不是永遠固定使用預設 artifact

## 建議使用流程

建議依序使用：

`explore -> propose -> apply -> archive`

1. `explore`: 先釐清需求、限制、設計參考與實作風險。
2. `propose`: 產出正式提案，固定需求範圍、規格與 tasks。
3. `apply`: 依提案逐步實作並同步更新相關文件。
4. `archive`: 驗收完成後封存變更，保留完整決策與交付紀錄。

## Artifact 位置策略

本專案將 OpenSpec change artifact 與 repo 正式文件分成兩層管理：

- `openspec/changes/<change>/docs/**`
  - 這是 OpenSpec propose / apply / archive 流程追蹤的工作稿位置。
  - `schema.yaml` 中的 `generates: docs/...` 會被 CLI 解析為 change 目錄內的相對路徑，而不是 repo 根目錄。
- `docs/**`
  - 這是 repo 跨 change 統整後的正式文件區。
  - 只適合存放已確認、需要長期維護與跨 change 共用的知識，例如 openapi、api flow、schema，以及經整理後的 requirements / architecture。

換句話說：

- OpenSpec CLI 要能正確辨識 artifact 完成狀態，就必須讓工作稿存在於 `openspec/changes/<change>/docs/**`
- `tasks` 只屬於 change-local 實作追蹤，不同步到 repo 根目錄 `docs/**`
- 若團隊希望保留一份跨 change 的正式文件，應在提案確認、實作穩定或 archive 前，只把共享知識同步整理到 repo 根目錄 `docs/**`

目前這個 custom schema 採用的就是這個策略，而不是直接把 OpenSpec artifact 根目錄改到 repo `docs/**`

## 同步策略

本專案建議將 artifact 分成兩類：

- `change-local only`
  - `tasks`
  - `qaReport`
  - 這些文件服務本次 change 的實作、驗證與封存，不作為 repo 長期知識庫的一部分。
- `shared knowledge`
  - `openapi`
  - `api flow`
  - `schema`
  - 必要時經整理後的 `requirements` / `architecture`
  - 這些文件會影響其他 change、其他開發者或系統整體理解，因此在內容穩定後應同步回 repo-level 文件區。

判斷原則很簡單：

- 如果文件只是在追蹤這次功能怎麼做完，就留在 `openspec/changes/<change>/docs/**`
- 如果文件會成為後續實作、契約對齊或跨功能協作的基準，就整理後同步到 `docs/**` 或對應的共享位置

## Archive Sync Wrapper

本專案保留 OpenSpec 原本的 change-local artifact 行為，並透過 repo-local wrapper 在 archive 時讀取 schema 的自定義設定來同步 repo-level `docs/**`。

- schema 設定位置：
  `openspec/schemas/delivery-responsibility/schema.yaml`
- wrapper script：
  `scripts/archive_change.py`

目前 `delivery-responsibility` schema 會在 archive 時宣告同步以下 artifact：

- `requirements` -> `docs/requirements/`
- `architecture` -> `docs/architecture/`
- `apiContract` -> `docs/openapi/openapi.yaml`
- `qaReport` -> `docs/qa-report/`

`archive_change.py` 會解析：

- `from.base: change`
  代表 `openspec/changes/<change>/...`
- `to.base: repo`
  代表 repo root 下的目標路徑

建議先用 dry-run 驗證：

```sh
python scripts/archive_change.py <change-name> --dry-run
```

確認同步與 archive 目標都正確後，再正式執行：

```sh
python scripts/archive_change.py <change-name>
```

如果只想 archive、不想同步 repo-level docs：

```sh
python scripts/archive_change.py <change-name> --skip-sync
```

## 範例指令

```text
/prompts:opsx-explore UI 參照 https://www.figma.com/make/3ilP5JnPlkgXomqo70MsDW/Library-Mini-Admin-Console?p=f&fullscreen=1 幫我規劃 [SCN-LIB-001.md](docs/scenarios/SCN-LIB-001.md)
```

## 自定義 Workflow

這一節說明本專案如何在保留 OpenSpec 預設使用方式理解的前提下，進一步透過 custom schema 改寫 artifact 結構與 skill 實際行為。

### 本專案的 Schema

本專案目前使用的自定義 schema 是 `delivery-responsibility`，位置如下：

```text
openspec/
├── config.yaml
└── schemas/
    └── delivery-responsibility/
        ├── schema.yaml
        └── templates/
```

這個 schema 以文件驅動交付為主，並保留一份輕量 `tasks` 清單供 `apply` 階段逐步執行：

`scenario -> requirements -> architecture -> apiContract -> tasks -> qaReport`

各 artifact 的角色如下：

- `scenario`: 情境與商業目標澄清
- `requirements`: SA 需求定義
- `architecture`: Archi 架構設計
- `apiContract`: SD API / schema 契約
- `tasks`: 單人實作友善的輕量任務清單，只保留在 change-local 工作稿
- `qaReport`: QA 驗證與缺陷回饋

如果未額外指定 `--schema`，OpenSpec 會直接使用 `openspec/config.yaml` 中設定的 `delivery-responsibility`。

在 OpenSpec 中，`config.yaml` 與 `schema.yaml` 分別代表了不同層次的自定義需求：前者用於調整「現有工作流的行為」，後者則用於定義「全新的工作流結構」。

### Skill 行為如何因 Schema 改變

OpenSpec 的 skill 文案通常會以預設 schema 為例，例如提到 `proposal`、`design`、`tasks`。但實際執行時，CLI 仍然會依據 change 所綁定的 schema 決定：

- 要建立哪些 artifact
- artifact 的依賴順序
- `apply` 階段需要哪些文件才算 ready
- `apply` 要追蹤哪一份任務文件

以本專案為例：

- `propose` 不會固定只產出預設的 `proposal/design/tasks`
- 它會依 `delivery-responsibility` schema 改為產出 `scenario / requirements / architecture / apiContract / tasks / qaReport`
- `apply` 也會依 `schema.yaml` 中的 `apply.requires` 與 `apply.tracks`，以 change-local 的 `tasks` 作為實作清單來源
- repo-level `docs/**` 不會存放 `tasks`，避免把短期實作清單誤當成長期正式文件

因此，使用者可以先理解 OpenSpec 的預設行為，再從本節看到：一旦指定 custom schema，skills 的實際 artifact 與 apply 流程會跟著 schema 改變。

以下是這兩個檔案的詳細說明：

### 1. `config.yaml` (專案配置)

這是最簡單的自定義方式，檔案位於 `openspec/config.yaml`。它主要用於設定專案的全局偏好，而不需要改變工作流的本質。

- **設定預設架構 (Default Schema)**：本專案目前預設為 `delivery-responsibility`，因此在執行指令時可以省略 `--schema`。
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
- **本專案實例**：`openspec/schemas/delivery-responsibility/schema.yaml` 以文件交付為核心，透過 `requires` 描述 SA、Archi、SD、Tasks、QA 對應文件之間的依賴順序。
- **路徑解析提醒**：雖然 `generates` 會寫成 `docs/scenarios/...`、`docs/tasks/...` 這種相對路徑，但 OpenSpec CLI 會把它解析到當前 change 目錄下，例如 `openspec/changes/<change>/docs/...`。
- **靈活性**：您可以從頭開始建立，或是透過 **Fork** 現有的架構（如 `spec-driven`）來快速修改。
- **驗證機制**：在使用自定義架構前，可以透過 `openspec schema validate` 指令檢查語法、模板是否存在、以及是否有循環依賴等問題。

### 兩者的主要差別

| 特性           | `config.yaml`                  | `schema.yaml`                |
| :------------- | :----------------------------- | :--------------------------- |
| **主要目的**   | 調整偏好與全局規則             | 定義工作流結構與產出物       |
| **自定義層次** | 專案層次 (最簡單)              | 自定義架構層次 (進階)        |
| **影響範圍**   | 預設架構、全局上下文、特定規則 | 檔案命名、模板選擇、依賴關係 |
| **適用對象**   | 大多數團隊                     | 有獨特開發流程的團隊         |
