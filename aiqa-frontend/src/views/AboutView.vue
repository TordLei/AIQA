<template>
  <div class="about">
    <h1>This is an about page</h1>
  </div>
  <div>{{ JSON.stringify(loginUserStore.loginUser) }}</div>
</template>
<script setup lang="ts">
import useRangeHeaderValue from "@arco-design/web-vue/es/date-picker/hooks/use-range-header-value";
import { useLoginUserStore } from "@/store/userStore";

const loginUserStore = useLoginUserStore();
</script>
