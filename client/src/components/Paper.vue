<template>
  <div class="paper">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "Paper"
}
</script>

<style scoped>
.paper {
  padding: 1rem;
  background-color: #eeeeee;
  border-radius: 8px;
  box-shadow: 0 5px 10px rgba(154, 160, 185, 0.1), 0 15px 40px rgba(166, 173, 201, 0.2);
}
</style>