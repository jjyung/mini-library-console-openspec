# Library Mini Admin Console Figma Reference

## Reference

- Figma Make URL: `https://www.figma.com/make/3ilP5JnPlkgXomqo70MsDW/Library-Mini-Admin-Console?p=f&fullscreen=1`
- Scope for this change: single-page admin console with top bar, transaction panel, create-book panel, and inventory table

## Implementation Notes

- Frontend implementation should follow this reference as the latest requirement-scoped UI source for the mini library admin console.
- If visual details conflict with older local examples, this Figma reference takes precedence for layout and interaction flow.
- `data-testid` locators must remain stable during alignment work.

## Chinese UI Terminology

- `Library Mini Admin` → `小型圖書櫃管理控制台`
- `Borrow` → `借書`
- `Return` → `還書`
- `Add Book` → `新增書籍`
- `Inventory` → `館藏列表`
- `Available` → `可借閱`
- `Borrowed` → `已借出`
- `Inactive` → `未上架`
- `Reader ID` → `讀者 ID`
- `Shelf Status` → `上架狀態`

## Demo Alignment

- The product demo script is documented in `docs/scenarios/SCN-LIB-001.md`.
- The implementation should support the demo path: 建立館藏 → 借出 `Clean Code` → 歸還 `Clean Code` → 檢視館藏狀態。
