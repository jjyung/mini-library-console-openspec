import { expect, test } from '@playwright/test'

test('create book, borrow it, search it, and return it via console workflow', async ({ page }) => {
  await page.goto('/')

  await page.getByTestId('create-book-title-input').fill('Clean Code')
  await page.getByTestId('create-book-isbn-input').fill('9780132350884')
  await page.getByTestId('create-book-author-input').fill('Robert C. Martin')
  await page.getByTestId('create-book-category-select').selectOption('technology')
  await page.getByTestId('create-book-copies-input').fill('2')
  await page.getByTestId('create-book-submit-button').click()

  const firstBookRow = page.getByTestId('book-row').first()
  await expect(firstBookRow.getByTestId('book-title')).toHaveText('Clean Code')
  await expect(firstBookRow.getByTestId('book-total-copies')).toHaveText('2')
  await expect(firstBookRow.getByTestId('book-available-copies')).toHaveText('2')

  await firstBookRow.getByTestId('quick-borrow-button').click()
  await expect(page.getByTestId('borrow-isbn-input')).toHaveValue('9780132350884')

  await page.getByTestId('borrow-reader-id-input').fill('Samson')
  await page.getByTestId('borrow-submit-button').click()
  await expect(firstBookRow.getByTestId('book-available-copies')).toHaveText('1')
  await expect(firstBookRow.getByTestId('active-transaction-borrower')).toHaveText('Samson')

  await page.getByTestId('search-books-input').fill('9780132350884')
  await expect(page.getByTestId('book-row')).toHaveCount(1)

  await firstBookRow.getByTestId('quick-return-button').click()
  await expect(page.getByTestId('return-feedback')).toContainText('已完成歸還')
  await expect(firstBookRow.getByTestId('book-available-copies')).toHaveText('2')
  await expect(firstBookRow.getByTestId('empty-transaction-state')).toBeVisible()
})
