<script setup lang="ts">
import { reactive } from 'vue'
import type { PostBooksRequestDTO } from '../types/library'

const props = defineProps<{
  busy: boolean
}>()

const emit = defineEmits<{
  submit: [payload: PostBooksRequestDTO]
}>()

const form = reactive<PostBooksRequestDTO>({
  title: '',
  isbn: '',
  author: '',
  category: 'technology',
  quantity: 1,
  active: true,
})

function handleSubmit() {
  emit('submit', {
    title: form.title.trim(),
    isbn: form.isbn.trim(),
    author: form.author?.trim() ? form.author.trim() : null,
    category: form.category,
    quantity: Number(form.quantity),
    active: form.active,
  })

  form.title = ''
  form.isbn = ''
  form.author = ''
  form.category = 'technology'
  form.quantity = 1
  form.active = true
}
</script>

<template>
  <section class="form-panel">
    <div class="panel-heading">
      <div>
        <p class="section-label">Create title</p>
        <h2>新增書籍</h2>
      </div>
      <p class="section-copy">輸入書名、ISBN、作者、分類、數量與上架狀態。</p>
    </div>

    <form class="book-form" data-testid="create-book-form" @submit.prevent="handleSubmit">
      <label>
        <span>書名</span>
        <input v-model="form.title" data-testid="create-book-title-input" placeholder="Clean Code" required type="text" />
      </label>

      <label>
        <span>ISBN</span>
        <input v-model="form.isbn" data-testid="create-book-isbn-input" placeholder="978-0-13-235088-4" required type="text" />
      </label>

      <label>
        <span>作者</span>
        <input v-model="form.author" data-testid="create-book-author-input" placeholder="Robert C. Martin" type="text" />
      </label>

      <label>
        <span>分類</span>
        <select v-model="form.category" data-testid="create-book-category-select">
          <option value="literature">文學</option>
          <option value="science">科學</option>
          <option value="technology">科技</option>
          <option value="history">歷史</option>
          <option value="business">商業</option>
        </select>
      </label>

      <label>
        <span>庫存數量</span>
        <input v-model="form.quantity" data-testid="create-book-copies-input" min="1" required type="number" />
      </label>

      <label class="toggle-row">
        <span>上架狀態</span>
        <input v-model="form.active" data-testid="create-book-active-toggle" type="checkbox" />
      </label>

      <button class="primary-button" data-testid="create-book-submit-button" :disabled="props.busy" type="submit">
        新增書籍
      </button>
    </form>
  </section>
</template>
