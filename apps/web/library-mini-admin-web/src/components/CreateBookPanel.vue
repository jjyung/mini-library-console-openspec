<script setup lang="ts">
import { reactive } from 'vue'

const props = defineProps<{
  busy: boolean
}>()

const emit = defineEmits<{
  submit: [{ title: string; author: string; initialCopies: number }]
}>()

const form = reactive({
  title: '',
  author: '',
  initialCopies: 1,
})

function handleSubmit() {
  emit('submit', {
    title: form.title,
    author: form.author,
    initialCopies: Number(form.initialCopies),
  })

  form.title = ''
  form.author = ''
  form.initialCopies = 1
}
</script>

<template>
  <section class="content-panel form-panel">
    <header class="section-header stacked">
      <div>
        <p class="section-label">Create title</p>
        <h2>Register a book</h2>
      </div>
      <p class="section-copy">
        Start with the title, author, and the number of physical copies currently on the shelf.
      </p>
    </header>

    <form class="book-form" data-testid="create-book-form" @submit.prevent="handleSubmit">
      <label>
        <span>Title</span>
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
        <span>Author</span>
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
        <span>Initial copies</span>
        <input
          v-model="form.initialCopies"
          data-testid="create-book-copies-input"
          min="1"
          name="initialCopies"
          required
          type="number"
        />
      </label>

      <button
        class="primary-button"
        data-testid="create-book-submit-button"
        :disabled="props.busy"
        type="submit"
      >
        Add book
      </button>
    </form>
  </section>
</template>
