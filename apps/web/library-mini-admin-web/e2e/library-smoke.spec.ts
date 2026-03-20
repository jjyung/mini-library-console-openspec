import { expect, test } from '@playwright/test'

test('create book, search it, borrow it, and return it via test ids', async ({ page }) => {
  await page.goto('/')

  await page.getByTestId('create-book-title-input').fill('Clean Code')
  await page.getByTestId('create-book-isbn-input').fill('978-0-13-235088-4')
  await page.getByTestId('create-book-author-input').fill('Robert C. Martin')
  await page.getByTestId('create-book-copies-input').fill('2')
  await page.getByTestId('create-book-submit-button').click()

  const firstBookRow = page.getByTestId('book-card').first()
  await expect(firstBookRow.getByTestId('book-title')).toHaveText('Clean Code')
  await expect(firstBookRow.getByTestId('book-total-copies')).toHaveText('2')
  await expect(firstBookRow.getByTestId('book-available-copies')).toHaveText('2')

  await page.getByTestId('library-search-input').fill('235088')
  await expect(page.getByTestId('book-card')).toHaveCount(1)

  await firstBookRow.getByTestId('book-checkout-button').click()
  await page.getByTestId('book-checkout-borrower-input').fill('samson')
  await page.locator('.transaction-panel').getByTestId('book-checkout-button').click()
  await expect(firstBookRow.getByTestId('book-available-copies')).toHaveText('1')
  await expect(firstBookRow.getByTestId('book-checked-out-copies')).toHaveText('1')

  await firstBookRow.getByTestId('return-button').click()
  await expect(firstBookRow.getByTestId('book-available-copies')).toHaveText('2')
  await expect(firstBookRow.getByTestId('book-checked-out-copies')).toHaveText('0')
})
