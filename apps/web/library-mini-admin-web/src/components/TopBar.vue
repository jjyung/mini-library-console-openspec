<script setup lang="ts">
const searchKeyword = defineModel<string>('searchKeyword', { required: true })
</script>

<template>
  <header class="top-bar">
    <div class="brand-block">
      <div class="brand-mark" aria-hidden="true">LB</div>
      <div>
        <p class="brand-kicker">Library Mini Admin</p>
        <h1>小型圖書櫃管理控制台</h1>
      </div>
    </div>

    <label class="search-shell" for="library-search-input">
      <span class="search-label">搜尋館藏</span>
      <input
        id="library-search-input"
        v-model="searchKeyword"
        data-testid="search-books-input"
        name="searchKeyword"
        placeholder="搜尋書名、ISBN、作者"
        type="text"
      />
    </label>

    <div class="admin-card">
      <p class="admin-name">Admin</p>
      <p class="admin-role">共享書櫃管理員</p>
    </div>
  </header>
</template>
