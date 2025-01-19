<template>
  <div class="app-wrapper">
    <sidebar class="sidebar-container"/>
    <div class="main-container">
      <div class="header">
        <navbar/>
      </div>
      <div class="app-main">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Header.vue'
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  width: 100%;
  height: 100vh;

  .sidebar-container {
    width: 210px;
    height: 100%;
    background-color: #304156;
  }

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;

    .header {
      height: 50px;
      background-color: #fff;
      box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
    }

    .app-main {
      flex: 1;
      padding: 20px;
      background-color: #f0f2f5;
    }
  }
}
</style>
