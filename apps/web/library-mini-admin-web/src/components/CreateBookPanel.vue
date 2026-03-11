<script setup lang="ts">
import { reactive } from 'vue'

const props = defineProps<{
  busy: boolean
}>()

const emit = defineEmits<{
  submit: [
    {
      title: string
      isbn: string
      author: string
      category: string
      active: boolean
      initialCopies: number
    },
  ]
}>()

const form = reactive({
  title: '',
  isbn: '',
  author: '',
  category: 'technology',
  active: true,
  initialCopies: 1,
})

function handleSubmit() {
  emit('submit', {
    title: form.title,
    isbn: form.isbn,
    author: form.author,
    category: form.category,
    active: form.active,
    initialCopies: Number(form.initialCopies),
  })

  form.title = ''
  form.isbn = ''
  form.author = ''
  form.category = 'technology'
  form.active = true
  form.initialCopies = 1
}
</script>

<template>
  <section class="panel-shell form-panel">
    <header class="panel-header stacked">
      <div>
        <p class="panel-kicker">Catalog Entry</p>
        <h2>新增書籍</h2>
      </div>
      <p class="panel-copy">
        建立書籍基本資料，讓館藏、借閱、歸還都能在同一個控制台追蹤。
      </p>
    </header>

    <form class="book-form" data-testid="create-book-form" @submit.prevent="handleSubmit">
      <label>
        <span>書名</span>
        <input
          v-model="form.title"
          data-testid="create-book-title-input"
          name="title"
          placeholder="Clean Code"
          required
          type="text"
        />
      </label>

      <label>
        <span>ISBN</span>
        <input
          v-model="form.isbn"
          data-testid="create-book-isbn-input"
          name="isbn"
          placeholder="9780132350884"
          required
          type="text"
        />
      </label>

      <label>
        <span>作者</span>
        <input
          v-model="form.author"
          data-testid="create-book-author-input"
          name="author"
          placeholder="Robert C. Martin"
          required
          type="text"
        />
      </label>

      <label>
        <span>分類</span>
        <select v-model="form.category" data-testid="create-book-category-select" name="category">
          <option value="technology">科技</option>
          <option value="architecture">架構</option>
          <option value="productivity">生產力</option>
          <option value="design">設計</option>
        </select>
      </label>

      <label>
        <span>庫存數量</span>
        <input
          v-model="form.initialCopies"
          data-testid="create-book-copies-input"
          min="1"
          name="initialCopies"
          required
          type="number"
        />
      </label>

      <label class="switch-row">
        <span>上架狀態</span>
        <button
          class="toggle-button"
          :class="{ active: form.active }"
          data-testid="create-book-active-toggle"
          type="button"
          @click="form.active = !form.active"
        >
          {{ form.active ? '可借閱' : '未上架' }}
        </button>
      </label>

      <button
        class="primary-button"
        data-testid="create-book-submit-button"
        :disabled="props.busy"
        type="submit"
      >
        新增書籍
      </button>
    </form>
  </section>
</template>
