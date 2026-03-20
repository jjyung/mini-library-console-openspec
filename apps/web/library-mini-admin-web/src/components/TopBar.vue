<script setup lang="ts">
const props = defineProps<{
  searchKeyword: string
  busy: boolean
}>()

const emit = defineEmits<{
  search: [keyword: string]
  refresh: []
}>()
</script>

<template>
  <header class="top-bar">
    <div class="brand-block">
      <div class="brand-icon">LB</div>
      <div>
        <h1>Library Mini Admin</h1>
        <p>管理控制台</p>
      </div>
    </div>

    <div class="search-block">
      <input
        :value="props.searchKeyword"
        data-testid="library-search-input"
        placeholder="搜尋書名、ISBN、作者..."
        type="text"
        @input="emit('search', ($event.target as HTMLInputElement).value)"
      />
    </div>

    <button class="ghost-button" data-testid="topbar-refresh-button" :disabled="props.busy" type="button" @click="emit('refresh')">
      重新整理
    </button>
  </header>
</template>
