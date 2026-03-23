<template>
  <router-view />
</template>

<script setup lang="ts">
</script>

<style>
/* 全局样式优化 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #ffffff;
  color: #333;
  line-height: 1.6;
}

/* 滚动条样式优化 - 细滚动条 */
::-webkit-scrollbar {
  width: 4px;
  height: 4px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

::-webkit-scrollbar-thumb {
  background: #d1d1d1;
  border-radius: 2px;
}

::-webkit-scrollbar-thumb:hover {
  background: #b1b1b1;
}
</style>