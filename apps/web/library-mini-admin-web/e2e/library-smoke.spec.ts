import { expect, test } from '@playwright/test'

test('create book, add copy, checkout, and return via test ids', async ({ page }) => {
  await page.goto('/')

  await page.getByTestId('create-book-title-input').fill('Clean Code')
  await page.getByTestId('create-book-author-input').fill('Robert C. Martin')
  await page.getByTestId('create-book-copies-input').fill('2')
  await page.getByTestId('create-book-submit-button').click()

  const firstBookCard = page.getByTestId('book-card').first()
  await expect(firstBookCard.getByTestId('book-title')).toHaveText('Clean Code')
  await expect(firstBookCard.getByTestId('book-total-copies')).toHaveText('2')
  await expect(firstBookCard.getByTestId('book-available-copies')).toHaveText('2')

  await firstBookCard.getByTestId('book-add-copies-input').fill('1')
  await firstBookCard.getByTestId('book-add-copies-button').click()
  await expect(firstBookCard.getByTestId('book-total-copies')).toHaveText('3')
  await expect(firstBookCard.getByTestId('book-available-copies')).toHaveText('3')

  await firstBookCard.getByTestId('book-checkout-borrower-input').fill('Samson')
  await firstBookCard.getByTestId('book-checkout-button').click()
  await expect(firstBookCard.getByTestId('book-available-copies')).toHaveText('2')
  await expect(firstBookCard.getByTestId('book-checked-out-copies')).toHaveText('1')
  await expect(firstBookCard.getByTestId('active-transaction-borrower')).toHaveText('Samson')

  await firstBookCard.getByTestId('return-button').click()
  await expect(firstBookCard.getByTestId('book-available-copies')).toHaveText('3')
  await expect(firstBookCard.getByTestId('book-checked-out-copies')).toHaveText('0')
  await expect(firstBookCard.getByTestId('empty-transaction-state')).toBeVisible()
})

test('show mapped feedback when checkout returns a business error', async ({ page }) => {
  await page.goto('/')

  await page.getByTestId('create-book-title-input').fill('Domain-Driven Design')
  await page.getByTestId('create-book-author-input').fill('Eric Evans')
  await page.getByTestId('create-book-copies-input').fill('1')
  await page.getByTestId('create-book-submit-button').click()

  const firstBookCard = page.getByTestId('book-card').first()
  await page.route('**/api/transactions/checkout', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        code: 'A0000',
        message: 'No available copies for checkout',
      }),
    })
  })

  await firstBookCard.getByTestId('book-checkout-borrower-input').fill('Taylor')
  await firstBookCard.getByTestId('book-checkout-button').click()

  await expect(page.getByTestId('library-feedback')).toHaveText(
    'This title has no available copies to check out.',
  )
  await expect(firstBookCard.getByTestId('book-available-copies')).toHaveText('1')
})
