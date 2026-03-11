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
